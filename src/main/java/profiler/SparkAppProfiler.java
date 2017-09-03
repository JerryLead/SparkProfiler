package profiler;

import analyzer.SparkAppsAnalyzer;
import appinfo.Application;
import parser.*;
import util.JsonFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SparkAppProfiler {

    private boolean useAppList;
    private String appJsonDir;
    private Set<String> appIdSet;

    // e.g.,
    // app-20170622150508-0330
    // app-20170622143730-0331


    public SparkAppProfiler(boolean useAppList, String appJsonDir) {
        this.useAppList = useAppList;
        this.appJsonDir = appJsonDir;
    }

    public void setAppSet(Set<String> appIdSet) {
        this.appIdSet = appIdSet;
    }

    public Set<String> parseAppIdList(String appIdsFile) {
        BufferedReader br;
        Set<String> appIdSet = new TreeSet<String>();

        try {
            br = new BufferedReader(new FileReader(appIdsFile));

            String appId;

            while ((appId = br.readLine()) != null) {
                if (appId.startsWith("app"))
                    appIdSet.add(appId.trim());
            }

            if(appIdSet.size() == 0) {
                System.err.println("None apps to be profile, exit!");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return appIdSet;
    }

    public List<Application> profileApps() {

        List<Application> applications = new ArrayList<Application>();

        // appDir = profiles/appName_appId/
        File appDir = new File(appJsonDir);

        for (File appJsonFile : appDir.listFiles()) {
            if (appJsonFile.isDirectory() && appJsonFile.getName().contains("app")) {

                // RDDJoin-CMS-4-28G-0.5_app-20170623114155-0011
                String fileName = appJsonFile.getName();
                String appId = fileName.substring(fileName.lastIndexOf("app"));

                if (useAppList) {
                    if (appIdSet.contains(appId)) {
                        applications.add(profileApp(appJsonFile));
                        System.out.println("[Done] " + fileName + " has been profiled!");
                    }
                } else {
                    System.out.println("[Parsing] " + fileName);
                    applications.add(profileApp(appJsonFile));
                    System.out.println("[Done] " + fileName + " has been profiled!");
                }
            }
        }

        return applications;
    }

    // appJsonFile = profiles/appName_appId/
    private Application profileApp(File appJsonFile) {

        String dir = appJsonFile.getAbsolutePath();

        // parse application.json
        String applicationJson = dir + File.separatorChar + "application.json";
        AppJsonParser appJsonParser = new AppJsonParser();
        Application app = appJsonParser.parseApplication(applicationJson);

        // parse jobs.json
        String jobsJsonFile = dir + File.separatorChar + "jobs.json";
        JobsJsonParser jobsJsonParser = new JobsJsonParser();
        String jobsJson = JsonFileReader.readFile(jobsJsonFile);
        // add jobs into the app
        jobsJsonParser.parseJobsJson(jobsJson, app);

        // parse stages.json
        String stagesJsonFile = dir + File.separatorChar + "stages.json";
        StagesJsonParser stagesJsonParser = new StagesJsonParser();
        String stagesJson = JsonFileReader.readFile(stagesJsonFile);
        // add stages into the app
        stagesJsonParser.parseStagesJson(stagesJson, app);

        // parse tasks info in jobId/stageId/attemptId.json and taskSummary.json
        for (File jobDir : appJsonFile.listFiles()) {

            String jobName = jobDir.getName();
            if (jobDir.isDirectory() && jobName.startsWith("job")) {
                int jobId = Integer.parseInt(jobName.substring(jobName.lastIndexOf('-') + 1));

                for (File stageDir : jobDir.listFiles()) {
                    String stageName = stageDir.getName();

                    if (stageDir.isDirectory() && stageName.startsWith("stage")) {
                        int stageId = Integer.parseInt(stageName.substring(stageName.lastIndexOf('-') + 1));

                        StageTasksJsonParser stageTasksJsonParser = new StageTasksJsonParser();
                        stageTasksJsonParser.parseStageTasksJson(stageDir, jobId, stageId, app);
                    }
                }
            }
        }

        // parse allexecutors info in jobId/stageId/allexecutors.json
        String allexecutorsFile = dir + File.separatorChar + "allexecutors.json";
        String allexecutorsJson = JsonFileReader.readFile(allexecutorsFile);
        // add allexecutors info into the app
        ExecutorsJsonParser executorsJsonParser = new ExecutorsJsonParser();
        executorsJsonParser.parseExecutorsJson(allexecutorsJson, app);

        // parse Executor GC logs
        String executorsDir = dir + File.separatorChar + "executors";
        executorsJsonParser.parseExecutorGCSummary(executorsDir, app);

        return app;
    }


    public static void main(String args[]) {

        // 1. Users can specify the appIds to be profiled using "useAppList = true" and "appList.txt".
        // 2. If useAppList, all the applications in the appJsonDir will be profiled.
        boolean useAppList = false;
        // Users need to specify the appIds to be profiled
        String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments/applists/appList.txt";
        String appJsonDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/SVM-1.0";

        SparkAppProfiler profiler = new SparkAppProfiler(useAppList, appJsonDir);

        if (useAppList) {
            // Obtain the appIds from the file (a list of appIds)
            Set<String> appIdSet = profiler.parseAppIdList(appIdsFile);
            profiler.setAppSet(appIdSet);
        }
        
        // Profile the app based on the saved json and output the profiles
        List<Application> apps = profiler.profileApps();

        SparkAppsAnalyzer analyzer = new SparkAppsAnalyzer(apps);
        analyzer.analyzeAppStatistics();
        analyzer.outputStatistics(appJsonDir + File.separatorChar + "Statistics");

    }
}
