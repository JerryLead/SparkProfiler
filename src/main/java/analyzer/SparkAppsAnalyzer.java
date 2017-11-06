package analyzer;

import appinfo.Application;
import appinfo.Executor;
import appinfo.TaskAttempt;
import statstics.ApplicationStatistics;
import util.FileTextWriter;
import util.JsonFileReader;

import java.io.File;
import java.util.*;


public class SparkAppsAnalyzer {

    // Key: AppName, Value: the same app that runs multiple times
    // Key = RDDJoin-CMS-1-7G-0.5, Value = [app-20170630121954-0025, app-20170630122434-0026, ...]
    private Map<String, List<Application>> appNameToIdsMap = new HashMap<String, List<Application>>();
    private Map<String, ApplicationStatistics> appStatisticsMap = new HashMap<String, ApplicationStatistics>();

    // profiledApps = [apps with different names in the appList]
    public SparkAppsAnalyzer(List<Application> profiledApps) {
        for (Application app : profiledApps) {
            String appName = app.getName();
            if (appName.contains("-n")) {
                appName = appName.substring(0, appName.indexOf("-n"));
            }

            if (!appNameToIdsMap.containsKey(appName)) {
                List<Application> appList = new ArrayList<Application>();
                appList.add(app);
                appNameToIdsMap.put(appName, appList);
            } else {
                List<Application> appList = appNameToIdsMap.get(appName);
                appList.add(app);
            }
        }
    }

    public void analyzeAppStatistics(Integer[] stageIdsToMerge) {
        for (Map.Entry<String, List<Application>> app : appNameToIdsMap.entrySet()) {
            ApplicationStatistics appStatistics = new ApplicationStatistics(app.getValue(), stageIdsToMerge);
            appStatisticsMap.put(app.getKey(), appStatistics);
        }
    }

    public void outputStatistics(String statisticsDir) {
        for (Map.Entry<String, ApplicationStatistics> appEntry : appStatisticsMap.entrySet()) {
            String appName = appEntry.getKey();
            ApplicationStatistics appStatistics = appEntry.getValue();
            appStatistics.setAppName(appName);

            String appStatisticsFile = statisticsDir + File.separatorChar + appName + "-stat.txt";
            FileTextWriter.write(appStatisticsFile, appStatistics.toString());

            System.out.println("[Done] The statistics of " + appName + " has been computed!");
        }
    }

    public void outputTaskInStage(String appDir, String dirName, int[] selectedStageIds) {
        Set<Integer> stageIds = new HashSet<Integer>();
        for (int id : selectedStageIds)
            stageIds.add(id);

        for (Map.Entry<String, List<Application>> appEntry : appNameToIdsMap.entrySet()) {
            // appName = RDDJoin-CMS-1-7G-0.5
            String appName = appEntry.getKey();
            String outputTaskInfoFile = appDir + File.separatorChar + dirName + File.separatorChar + appName + "-tasks.txt";

            StringBuilder sb = new StringBuilder();
            sb.append("[appName = " + appName + "]\n");

            for (Application app : appEntry.getValue()) {
                if (app.getStatus().equals("SUCCEEDED")) {
                    sb.append("[appId = " + app.getAppId() + "]\n");
                    String taskInfo = app.getTaskInfosInStage(stageIds);
                    sb.append(taskInfo);
                }
            }

            FileTextWriter.write(outputTaskInfoFile, sb.toString());
        }
    }

    public void outputSlowestTask(String appDir, String dirName, int[] selectedStageIds) {

        Set<Integer> stageIds = new HashSet<Integer>();
        for (int id : selectedStageIds)
            stageIds.add(id);

        for (Map.Entry<String, ApplicationStatistics> appEntry : appStatisticsMap.entrySet()) {
            String appName = appEntry.getKey();
            ApplicationStatistics appStatistics = appEntry.getValue();
            appStatistics.setAppName(appName);

            String appStatisticsFile = appDir + File.separatorChar + dirName + File.separatorChar + appName + "-slowestTask.txt";
            TaskAttempt slowestTask = appStatistics.getSlowestTask(stageIds);

            if (slowestTask != null) {

                String appId = slowestTask.getAppId();
                int executorId = slowestTask.getExecutorId();

                Application appWithSlowestTask = null;

                for (Application app : appNameToIdsMap.get(appName)) {
                    if (app.getAppId().equalsIgnoreCase(appId))
                        appWithSlowestTask = app;
                }
                Executor executor = appWithSlowestTask.getExecutor(executorId + "");

                String stderr = appDir + File.separatorChar + appName + "_" + appId + File.separatorChar +
                        "executors" + File.separatorChar + executorId + File.separatorChar + "stderr";
                String excutorLog = JsonFileReader.readFile(stderr);

                StringBuilder sb = new StringBuilder();
                sb.append("======================= Slowest Task in " + appName + "_" + appId + " stage" + stageIds + " =======================\n");

                sb.append(slowestTask + "\n\n");
                // sb.append(slowestTask.jsonString() + "\n\n");
                sb.append("======================= Slowest Task Executor GC Metrics " + " =======================\n");
                sb.append(executor + "\n\n");
                sb.append("======================= Slowest Task Execution Log " + " =======================\n");
                sb.append(excutorLog);



                FileTextWriter.write(appStatisticsFile, sb.toString());
            }

            System.out.println("[Done] The slowestTask of " + appName + " has been computed!");
        }
    }

}
