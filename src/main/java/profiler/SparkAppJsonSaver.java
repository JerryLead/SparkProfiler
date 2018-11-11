package profiler;

import appinfo.ResourceMetrics;
import appinfo.TopMetrics;
import gc.ExecutorGCLogParser;
import gc.ExecutorGCLogParserWithGCeasy;
import parser.AppJsonParser;
import parser.ExecutorsJsonParser;
import util.CommandRunner;
import util.FileChecker;
import util.FileTextWriter;
import util.JsonFileReader;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SparkAppJsonSaver {
    private String masterIP = "";

    // e.g.,
    // app-20170622150508-0330
    // app-20170622143730-0331
    private List<String> appIdList = new ArrayList<String>();

    private String prefix;

    Map<String, List<TopMetrics>> topMetricsMap = new HashMap<String, List<TopMetrics>>();


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
    public void saveExecutorGCInfo(String userName, String[] slavesIP,
                                    String executorLogFile,
                                    String outputDir) {

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
        String rsync = "rsync -av --exclude *.jar --exclude *.hprof " + userName + "@";

        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (String slaveIP : slavesIP) {
            cachedThreadPool.execute(new Runnable() {
                public void run() {
                    for (String appId : appIdList) {
                        // /dataDisk/spark-2.1.4.19-bin-2.7.1/worker/appId/executorID/{stdout, stderr}
                        String logFile = executorLogFile + "/" + appId + "/*";
                        String outputFile = outputDir + File.separatorChar + appIdtoName.get(appId) + File.separatorChar + "executors";
                        String cmd = rsync + slaveIP + ":" + logFile + " " + outputFile;
                        CommandRunner.exec(cmd);
                    }
                }
            });
        }

        /*
        for (String slaveIP : slavesIP) {
            for (String appId : appIdList) {
                // /dataDisk/spark-2.1.4.19-bin-2.7.1/worker/appId/executorID/{stdout, stderr}
                String logFile = executorLogFile + "/" + appId + "/*";
                String outputFile = outputDir + File.separatorChar + appIdtoName.get(appId) + File.separatorChar + "executors";
                String cmd = rsync + slaveIP + ":" + logFile + " " + outputFile;
                CommandRunner.exec(cmd);
            }
        }
        */
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

                            String gcEventFile = executorDir.getAbsolutePath() + File.separatorChar + "gcEvent-" + executorId + ".txt";
                            String gcPlainEventFile = executorDir.getAbsolutePath() + File.separatorChar + "gcPlainEvent-" + executorId + ".txt";

                            String gcMetricsFile = executorDir.getAbsolutePath() + File.separatorChar + "gcMetrics-" + executorId + ".json";
                            if (FileChecker.isGCFile(gcLogFile)) {
                                // ExecutorGCLogParser.parseExecutorGCLog(gcLogFile, exportCVSFile, chartPNGFile);
                                // ExecutorGCLogParser.parseExecutorGCLogToSummary(gcLogFile, gcEventFile, "CSV_TS");
                                // ExecutorGCLogParser.parseExecutorGCLogToSummary(gcLogFile, exportCVSFile, "SUMMARY");
                                ExecutorGCLogParser.parseExecutorGCLogToSummary(gcLogFile, gcPlainEventFile, "PLAIN");

                                // ExecutorGCLogParserWithGCeasy.parseExecutorGCLog(gcLogFile, gcMetricsFile);
                            }

                        }
                    }

                }
            }
        }
    }

    private void saveTopMetrics(String userName, String[] slavesIP, String sparkTopLogDir,
                                 String outputDir) {

        // rsync -av root@aliSlave2:/dataDisk/GCTest/SparkTopLogs/RDDJoinTest-0.5-6.5G/*.top
        // /Users/xulijie/Documents/GCResearch/NewExperiments/profiles/RDDJoin-0.5/topMetrics/slavex/*.top
        String rsync = "rsync -av " + userName + "@";


        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (String slaveIP : slavesIP) {
            cachedThreadPool.execute(new Runnable() {
                public void run() {
                    String logFile = sparkTopLogDir + "/*.txt";
                    String outputFile = outputDir + "topMetrics/" + slaveIP;
                    String cmd = rsync + slaveIP + ":" + logFile + " " + outputFile;

                    File file = new File(outputFile);
                    if (!file.exists())
                        file.mkdirs();
                    CommandRunner.exec(cmd);
                }
            });
        }


        /*
        for (String slaveIP : slavesIP) {
            String logFile = sparkTopLogDir + "/*.txt";
            String outputFile = outputDir + "topMetrics/" + slaveIP;
            String cmd = rsync + slaveIP + ":" + logFile + " " + outputFile;

            File file = new File(outputFile);
            if (!file.exists())
                file.mkdirs();
            CommandRunner.exec(cmd);
        }
        */
    }

    // outputDir = /Users/xulijie/Documents/GCResearch/NewExperiments/profiles/RDDJoin-0.5
    private void parseTopMetrics(String outputDir) {
        String topMetricsDir = outputDir + "/topMetrics";

        // slaveDir = /Users/xulijie/Documents/GCResearch/NewExperiments/profiles/RDDJoin-0.5/aliSlave1
        for (File slaveDir : new File(topMetricsDir).listFiles()) {
            if (slaveDir.isDirectory()) {
                String slaveName = slaveDir.getName();

                // topFile = /Users/xulijie/Documents/GCResearch/NewExperiments/profiles/RDDJoin-0.5/aliSlave1/rjoin-CMS-1-6656m-0.5-n1.top
                for (File topFile : slaveDir.listFiles()) {
                    analyzeTopMetricFile(slaveName, topFile);
                }
            }
        }

        for (File appDir : new File(outputDir).listFiles()) {
            if (appDir.isDirectory()) {
                // RDDJoin-CMS-1-6656m-0.5-n1
                String appName = appDir.getName();
                if (appName.indexOf('_') != -1) {
                    appName = appName.substring(appName.indexOf('-') + 1, appName.indexOf('_'));
                    File executorsFile = new File(appDir, "executors");

                    Set<String> aliveExecutorIds = ExecutorsJsonParser.getAliveExecutors(appDir.getAbsolutePath()
                            + File.separatorChar + "allexecutors.json");

                    for(File executorDir : executorsFile.listFiles()) {
                        // executors/0
                        if (executorDir.isDirectory()) {
                            String executorId = executorDir.getName();

                            if (aliveExecutorIds.contains(executorId)) {

                                String stderrFile = executorDir.getAbsolutePath() + File.separatorChar + "stderr";

                                List<String> lines = JsonFileReader.readFileLines(stderrFile);
                                if (!lines.isEmpty()) {
                                    // 17/11/02 11:17:44 INFO CoarseGrainedExecutorBackend: Started daemon with process name: 14388@slave7
                                    String line = lines.get(0);
                                    String pid = line.substring(line.lastIndexOf(':') + 2, line.lastIndexOf('@'));
                                    String slave = line.substring(line.lastIndexOf('@') + 1);
                                    String key = appName + "_" + slave + "_" + pid;

                                    if (topMetricsMap.containsKey(key)) {
                                        List<TopMetrics> list = topMetricsMap.get(key);
                                        String topMetricFile = executorDir.getAbsolutePath() + File.separatorChar + "topMetrics.txt";
                                        StringBuilder sb = new StringBuilder();

                                        for (TopMetrics tm : list) {
                                            sb.append(tm + "\n");
                                        }

                                        FileTextWriter.write(topMetricFile, sb.toString());
                                        System.out.println("[Done] Writing topMetrics into " + topMetricFile);
                                    }


                                }


                            }
                        }

                    }
                }
            }

        }
    }

    // rjoin-CMS-1-6656m-0.5-n1.top
    private void analyzeTopMetricFile(String slaveName, File topFile) {
        // key = appName_slaveName_PID

        List<String> topMetricsLines = JsonFileReader.readFileLines(topFile.getAbsolutePath());
        String time = "";

        String name = topFile.getName();
        // CMS-1-6656m-0.5-n1
        name = name.substring(name.indexOf("-") + 1, name.lastIndexOf("."));
        if(slaveName.startsWith("ali"))
            slaveName = slaveName.toLowerCase().substring(3);

        for (String line : topMetricsLines) {
            if (line.startsWith("top")) {
                time = line.substring(line.indexOf("-") + 2, line.indexOf("up") - 1);

            } else if (line.trim().endsWith("java")) {
                String[] metrics = line.trim().split("\\s+");
                String PID = metrics[0];
                double CPU = Double.parseDouble(metrics[8]);
                String memoryStr = metrics[5];
                double memory;
                if (memoryStr.endsWith("g")) {
                    memory = Double.parseDouble(memoryStr.substring(0, memoryStr.indexOf("g")));
                } else if (memoryStr.endsWith("t")) {
                    memory = Double.parseDouble(memoryStr.substring(0, memoryStr.indexOf("t")));
                    memory = memory * 1024;
                } else if (memoryStr.endsWith("m")) {
                    memory = Double.parseDouble(memoryStr.substring(0, memoryStr.indexOf("m")));
                    memory = memory / 1024;
                } else {
                    memory = Double.parseDouble(memoryStr);
                    memory = memory / 1024 / 1024;

                }

                TopMetrics topMetrics = new TopMetrics(time, CPU, memory);
                // RDDJoin-CMS-1-6656m-0.5-1_app-20171102111743-0003
                // rjoin-CMS-1-6656m-0.5-n1.top

                String key = name + "_" + slaveName + "_" + PID;

                if (topMetricsMap.containsKey(key)) {
                    topMetricsMap.get(key).add(topMetrics);
                } else {
                    List<TopMetrics> list = new ArrayList<TopMetrics>();
                    list.add(topMetrics);
                    topMetricsMap.put(key, list);
                }

                // System.out.println("[" + time + "] PID = " + PID + ", CPU = "
                //        + CPU + ", Memory = " + memory);
            }
        }

    }

    public static void main(String args[]) {


        String masterIP = "master";

        // Users need to specify the appIds to be profiled
        // e.g., app-20170623113634-0010
        //       app-20170623113111-0009
        //       app-20170623112547-0008
        //String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments-2018/applists/SQLGroupBy-1.0-200G-2/SQLGroupByAppList-200G.txt";
        //String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments-2018/applists/AggregateByKey-0.5-5/aggregateByKeyAppList-100G.txt";
        //String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments-2018/applists/SQLJoin-1.0-200G/SQLJoinAppList-200G.txt";
        //String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments-2018/applists/Join-1.0-Memory/JoinAppList-200G-Memory.txt";
        //String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments-2018/applists/Join-1.0-CPU/JoinAppList-200G-CPU.txt";
        String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments-2018/applists/Join-1.0-memory-5.5G/JoinAppList-200G-Memory.txt";




        // String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments-2018/applists/AggregateByKey-1.0/aggregateByKeyAppList-200G.txt";

        //String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments-2018/applists/Join-0.5-100G/JoinAppList-100G.txt";
        // String outputDir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/profiles/RDDJoin-1.0";
        //String outputDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/Join-0.5-100G/";
        // outputDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/AggregateByKey-1.0-CPU/";
        //String outputDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/Join-1.0-Memory/";
        //String outputDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/Join-1.0-CPU/";
        String outputDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/Join-1.0-memory-5.5G/";

        //String outputDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/AggregateByKey-1.0/";
        //String outputDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/SQLJoin-1.0-200G/";

        // String outputDir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/profiles/PageRank-0.5";


        //String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/SQLJoin-1.0-6.5G-200G";
        //String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/AggregateByKey-1.0-6.5G-200G-CPU2";
        //String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/Join-1.0-6.5G-200G-CPU2";
        //String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/Join-1.0-6.5G-200G-memory5G";
        String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/Join-1.0-6.5G-200G-memory";


        //String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/AggregateByKey-0.5-6.5G-100G-5";
        //String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/Join-1.0-6.5G-200G";
        //String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/AggregateByKey-1.0-6.5G-200G";
        //String sparkTopLogDir = "/dataDisk/GCTest/SparkTopLogs/Join-1.0-6.5G-100G";
        // The executor log files are stored on each slave node
        String executorLogFile = "/dataDisk/spark-2.3.0/worker/";
        String[] slavesIP = new String[]{"slave1", "slave2", "slave3", "slave4", "slave5", "slave6", "slave7", "slave8"};
        String userName = "root";

        SparkAppJsonSaver saver = new SparkAppJsonSaver(masterIP);

        // Obtain the appIds from the file (a list of appIds)app-20171117095258-0045
        //saver.parseAppIdList(appIdsFile);

        // Save the app's jsons info into the outputDir
        //saver.saveAppJsonInfo(outputDir);

        //saver.saveExecutorGCInfo(userName, slavesIP, executorLogFile, outputDir);

        //saver.saveTopMetrics(userName, slavesIP, sparkTopLogDir, outputDir);

        // saver.parseExecutorGCInfo(outputDir, false);

        saver.parseTopMetrics(outputDir);
    }


}
