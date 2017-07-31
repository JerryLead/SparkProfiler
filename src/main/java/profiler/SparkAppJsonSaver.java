package profiler;

import gc.ExecutorGCLogParser;
import parser.AppJsonParser;
import parser.ExecutorsJsonParser;
import util.CommandRunner;
import util.FileChecker;

import java.io.*;
import java.util.*;


public class SparkAppJsonSaver {
    private String masterIP = "";

    // e.g.,
    // app-20170622150508-0330
    // app-20170622143730-0331
    private List<String> appIdList = new ArrayList<String>();

    private String prefix;


    public SparkAppJsonSaver(String masterIP) {
        this.masterIP = masterIP;
        prefix = "http://" + masterIP + ":18080/api/v1";
    }

    public void parseAppIdList(String appIdsFile) {
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(appIdsFile));

            String appId;

            while ((appId = br.readLine()) != null) {
                if (appId.startsWith("app"))
                    appIdList.add(appId.trim());
            }

            if(appIdList.size() == 0) {
                System.err.println("None apps to be profile, exit!");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * The following app pages will be saved:
     * /applications/[app-id], including the application information.
     * /applications/[app-id]/jobs, including the information of each job.
     * /applications/[app-id]/stages, including the information of each stage.
     * /applications/[app-id]/stages/[stage-id]/[stage-attempt-id], including the tasks and executorSummary.
     * /applications/[app-id]/stages/[stage-id]/[stage-attempt-id]/taskSummary?quantiles=0,0.25,0.5,0.75,1, including the taskSummary.
     *
     * /applications/[app-id]/executors, A list of all active executors for the given application.
     * /applications/[app-id]/allexecutors, A list of all(active and dead) executors for the given application.
     * /applications/[app-id]/storage/rdd, A list of stored RDDs for the given application.
     **/

    public void saveAppJsonInfo(String outputDir) {

        for (String appId : appIdList) {
            AppJsonParser appJsonParser = new AppJsonParser(masterIP, appId);
            appJsonParser.saveAppJson(outputDir);
            System.out.println("[Done] The json information of " + appId + " has been saved into " + outputDir);
        }
    }

    /**
     *
     * @param slavesIP e.g., slave1
     * @param executorLogFile /dataDisk/spark-2.1.4.19-bin-2.7.1/worker/
     * @param outputDir /Users/xulijie/Documents/GCResearch/Experiments/profiles/
     */
    public void saveExecutorGCInfo(String userName, String[] slavesIP, String executorLogFile, String outputDir) {

        // create executors directory in outputDir/appName_appId/
        Map<String, String> appIdtoName = new HashMap<String, String>();
        for (String appId : appIdList)
            appIdtoName.put(appId, "");

        for (File appDir : new File(outputDir).listFiles()) {
            String appId = appDir.getName().substring(appDir.getName().indexOf('_') + 1);
            if (appIdtoName.containsKey(appId)) {
                File executorFile = new File(appDir, "executors");
                executorFile.mkdir();
                appIdtoName.put(appId, appDir.getName());
            }
        }

        // rsync -av --exclude *.jar root@aliSlave2:/dataDisk/spark-2.1.4.19-bin-2.7.1/worker/app-20170616152828-0285/*
        // /Users/xulijie/Documents/GCResearch/Experiments/profiles/*_app-20170623114155-0011/executors/
        String rsync = "rsync -av --exclude *.jar " + userName + "@";

        for (String slaveIP : slavesIP) {
            for (String appId : appIdList) {
                // /dataDisk/spark-2.1.4.19-bin-2.7.1/worker/appId/executorID/{stdout, stderr}
                String logFile = executorLogFile + "/" + appId + "/*";
                String outputFile = outputDir + File.separatorChar + appIdtoName.get(appId) + File.separatorChar + "executors";
                String cmd = rsync + slaveIP + ":" + logFile + " " + outputFile;
                CommandRunner.exec(cmd);
            }
        }
    }

    private void parseExecutorGCInfo(String outputDir, boolean useAppList) {

        Map<String, String> appIdtoName = new HashMap<String, String>();
        for (String appId : appIdList)
            appIdtoName.put(appId, "");

        for (File appDir : new File(outputDir).listFiles()) {
            String appId = appDir.getName().substring(appDir.getName().indexOf('_') + 1);
            if (useAppList == false && appId.startsWith("app") || appIdtoName.containsKey(appId)) {
                File executorsFile = new File(appDir, "executors");

                Set<String> aliveExecutorIds = ExecutorsJsonParser.getAliveExecutors(appDir.getAbsolutePath()
                        + File.separatorChar + "allexecutors.json");

                for(File executorDir : executorsFile.listFiles()) {
                    // executors/0
                    if (executorDir.isDirectory()) {
                        String executorId = executorDir.getName();

                        if (aliveExecutorIds.contains(executorId)) {

                            String gcLogFile = executorDir.getAbsolutePath() + File.separatorChar + "stdout";
                            String exportCVSFile = executorDir.getAbsolutePath() + File.separatorChar + "gcMetrics-" + executorId + ".csv";
                            String chartPNGFile = executorDir.getAbsolutePath() + File.separatorChar + "gcChart-" + executorId + ".png";

                            if (FileChecker.isGCFile(gcLogFile))
                                ExecutorGCLogParser.parseExecutorGCLog(gcLogFile, exportCVSFile, chartPNGFile);

                        }
                    }

                }
            }
        }
    }

    public static void main(String args[]) {


        String masterIP = "aliMaster";

        // Users need to specify the appIds to be profiled
        // e.g., app-20170623113634-0010
        //       app-20170623113111-0009
        //       app-20170623112547-0008
        String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments/applists/appList.txt";
        String outputDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/SVM-1.0/";

        // The executor log files are stored on each slave node
        String executorLogFile = "/dataDisk/spark-2.1.4.19-bin-2.7.1/worker";
        String[] slavesIP = new String[]{"aliSlave1", "aliSlave2", "aliSlave3", "aliSlave4", "aliSlave5", "aliSlave6", "aliSlave7", "aliSlave8"};
        String userName = "root";

        SparkAppJsonSaver saver = new SparkAppJsonSaver(masterIP);

        // Obtain the appIds from the file (a list of appIds)
        saver.parseAppIdList(appIdsFile);

        // Save the app's jsons info into the outputDir
        saver.saveAppJsonInfo(outputDir);

        saver.saveExecutorGCInfo(userName, slavesIP, executorLogFile, outputDir);

        saver.parseExecutorGCInfo(outputDir, false);
    }

}
