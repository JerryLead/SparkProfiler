package profiler;

import analyzer.SparkAppsAnalyzer;
import appinfo.*;
import parser.*;
import util.JsonFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static sun.misc.Version.println;

public class SparkAppProfiler {

    private boolean useAppList;
    private String appJsonDir;
    private Set<String> appIdSet;

    // e.g.,
    // app-20170622150508-0330
    // app-20170622143730-0331


    private SparkAppProfiler(boolean useAppList, String appJsonDir) {
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

        // Set the spill duration for each task (parsed from stderr)
        transferSpillMetricsToTasks(app);


        return app;
    }

    private void transferSpillMetricsToTasks(Application app) {
        // Key: taskId
        Map<Integer, List<SpillMetrics>> spilledMetricsMap
                = new HashMap<Integer, List<SpillMetrics>>();

        for (Executor executor : app.getExecutors()) {
            for (SpillMetrics spillMetrics : executor.getSpillMetricsList()) {
                int taskId = spillMetrics.getTaskId();
                double spillDuraiton = spillMetrics.getSpillDuration();

                if (spilledMetricsMap.containsKey(taskId))
                    spilledMetricsMap.get(taskId).add(spillMetrics);
                else {
                    List<SpillMetrics> list = new ArrayList<SpillMetrics>();
                    list.add(spillMetrics);
                    spilledMetricsMap.put(taskId, list);
                }
            }
        }

        for (Stage stage : app.getStageMap().values()) {
            if (stage.getCompletedStage() != null) {
                for (Task task : stage.getCompletedStage().getTaskMap().values()) {
                    if(task.getCompletedTask() != null) {
                        int taskId = task.getTaskId();
                        List<SpillMetrics> spillMetricsList = spilledMetricsMap.get(taskId);
                        if (spillMetricsList != null)
                            task.getCompletedTask().setSpillMetricsList(spillMetricsList);
                    }
                }
            }
        }
    }

    public static void profile(String app, String appJsonDir, int[] selectedStageIds) {

        Integer[] stageIdsToMerge = {};
        if (app.equalsIgnoreCase("SVM"))
            stageIdsToMerge = new Integer[]{4, 6, 8, 10, 12, 14, 16, 18, 20, 22};

        SparkAppProfiler profiler = new SparkAppProfiler(false, appJsonDir);

        // Profile the app based on the saved json and output the profiles
        List<Application> apps = profiler.profileApps();

        SparkAppsAnalyzer analyzer = new SparkAppsAnalyzer(apps);
        analyzer.analyzeAppStatistics(stageIdsToMerge);
        analyzer.outputStatistics(appJsonDir + File.separatorChar + "Statistics");
        analyzer.outputTaskInStage(appJsonDir, "TaskInfo", selectedStageIds);
        // analyzer.outputSlowestTask(appJsonDir, "Abnormal", selectedStageIds);
        // analyzer.outputSlowestTask(appJsonDir, "Abnormal-json", selectedStageIds);

        analyzer.outputMedianApp(appJsonDir + File.separatorChar + "MedianApp");
        /*
        apps.sort(new Comparator<Application>() {
            @Override
            public int compare(Application app1, Application app2) {
                return (int) (app1.getDuration() - app2.getDuration());
            }
        });

        Application medianApp = apps.get(apps.size() / 2);
        return medianApp;
        */
        // System.out.println("[Median] appName = " + medianApp.getName() + ", appId = " + medianApp.getAppId() + ", duration = " + medianApp.getDuration());
        // System.out.println("[Min] appName = " + apps.get(0).getName() + ", appId = " + apps.get(0).getAppId() + ", duration = " + apps.get(0).getDuration());
        for (Application a : apps) {
            a.countGCTimeInShuffle();
        }

    }

    public static List<Application> profileMedianApps(String appJsonDir) {

        SparkAppProfiler profiler = new SparkAppProfiler(false, appJsonDir);

        // Profile the app based on the saved json and output the profiles
        List<Application> apps = profiler.profileApps();

        return apps;
    }

    public static void main(String args[]) {

        // 1. Users can specify the appIds to be profiled using "useAppList = true" and "appList.txt".
        // 2. If useAppList, all the applications in the appJsonDir will be profiled.
        // Users need to specify the appIds to be profiled

        // String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/medianProfiles/";
        String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/medianProfiles/";

        String app = "AggregateByKey";
        int[] selectedStageIds = new int[]{1};
        //String appJsonDir = appJsonRootDir + "AggregateByKey-0.5";
        //profile(app, appJsonDir, selectedStageIds);
        String appJsonDir = appJsonRootDir + "AggregateByKey-1.0";
        profile(app, appJsonDir, selectedStageIds);

/*
        String app = "GroupBy";
        int[] selectedStageIds = new int[]{1};
        String appJsonDir = appJsonRootDir + "GroupByRDD-0.5";
        profile(app, appJsonDir, selectedStageIds);
        appJsonDir = appJsonRootDir + "GroupByRDD-1.0";
        profile(app, appJsonDir, selectedStageIds);
*/

/*
        String app = "Join";
        int[] selectedStageIds = new int[]{2};
        //String appJsonDir = appJsonRootDir + "RDDJoin-0.5";
        //profile(app, appJsonDir, selectedStageIds);
        String appJsonDir = appJsonRootDir + "Join-1.0";
        profile(app, appJsonDir, selectedStageIds);
*/
        /*
        String  app = "SVM";
        int[] selectedStageIds = new int[]{4, 6, 8, 10, 12, 14, 16, 18, 20, 22};
        String appJsonDir = appJsonRootDir + "SVM-0.5";
        profile(app, appJsonDir, selectedStageIds);
        appJsonDir = appJsonRootDir + "SVM-1.0";
        profile(app, appJsonDir, selectedStageIds);
        */

        /*
        String app = "PageRank";
        int[] selectedStageIds = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String appJsonDir = appJsonRootDir + "PageRank-0.5";
        profile(app, appJsonDir, selectedStageIds);
        // appJsonDir = appJsonRootDir + "PageRank-1.0";
        // profile(app, appJsonDir, selectedStageIds);
        */


    }

}
