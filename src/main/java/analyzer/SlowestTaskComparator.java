package analyzer;

import appinfo.Application;
import appinfo.Executor;
import appinfo.Task;
import appinfo.TaskAttempt;;
import profiler.SparkAppProfiler;


import util.JsonFileReader;
import util.RelativeDifference;

import java.io.File;
import java.util.*;

/**
 * Created by xulijie on 17-11-9.
 */
public class SlowestTaskComparator {

    // key = E1-Parallel-0.5
    private Map<String, Application> appMap = new HashMap<String, Application>();
    private int[] selectedStageIds;
    private String appJsonDir0;
    private String appJsonDir1;

    private String[] metrics;
    private String applicationName;

    public SlowestTaskComparator(String applicationName, int[] selectedStageIds, String appJsonDir0, String appJsonDir1, String[] metrics) {
        this.applicationName = applicationName;
        this.selectedStageIds = selectedStageIds;
        this.appJsonDir0 = appJsonDir0;
        this.appJsonDir1 = appJsonDir1;
        this.metrics = metrics;
        List<Application> medianAppsList0 = SparkAppProfiler.profileMedianApps(appJsonDir0);
        List<Application> medianAppsList1 = SparkAppProfiler.profileMedianApps(appJsonDir1);

        for (Application app : medianAppsList0) {
            String appName = app.getName();
            String mode = "";
            String collector = "";

            if (appName.contains("-1-"))
                mode = "E1";
            else if (app.getName().contains("-2-"))
                mode = "E2";
            else if (app.getName().contains("-4-"))
                mode = "E4";

            if (appName.contains("Parallel"))
                collector = "Parallel";
            else if (app.getName().contains("CMS"))
                collector = "CMS";
            else if (app.getName().contains("G1"))
                collector = "G1";

            appMap.put(mode + "-" + collector + "-0.5", app);
        }

        for (Application app : medianAppsList1) {
            String appName = app.getName();
            String mode = "";
            String collector = "";

            if (appName.contains("-1-"))
                mode = "E1";
            else if (app.getName().contains("-2-"))
                mode = "E2";
            else if (app.getName().contains("-4-"))
                mode = "E4";

            if (appName.contains("Parallel"))
                collector = "Parallel";
            else if (app.getName().contains("CMS"))
                collector = "CMS";
            else if (app.getName().contains("G1"))
                collector = "G1";

            appMap.put(mode + "-" + collector + "-1.0", app);
        }
    }

    private void computeRelativeDifference() {
        String[] dataModes = {"0.5", "1.0"};
        String[] modes = {"E1", "E2", "E4"};
        String[] collectors = {"Parallel", "CMS", "G1"};

        for (String dataMode : dataModes) {
            for (String mode : modes) {

                // <E1-Parallel-0.5, E1-CMS-0.5, E1-G1-0.5>
                List<Application> appList = new ArrayList<Application>();
                for (String collector : collectors) {
                    String key = mode + "-" + collector + "-" + dataMode;

                    Application app = appMap.get(key);
                    appList.add(app);
                }

                compareAppDuration(dataMode, mode, appList);

                List<Application> successfulAppList = new ArrayList<Application>();

                for (Application app : appList) {
                    if (app.getStatus().equalsIgnoreCase("SUCCEEDED"))
                        successfulAppList.add(app);
                }

                // System.out.println("\n\n=============[" + mode + "-" + dataMode + "]============");
                if (dataMode.equalsIgnoreCase("0.5") && !successfulAppList.isEmpty())
                    compareSlowestTask(dataMode, mode, successfulAppList, appJsonDir0);
                else if (dataMode.equalsIgnoreCase("1.0") && !successfulAppList.isEmpty())
                    compareSlowestTask(dataMode, mode, successfulAppList, appJsonDir1);
            }
        }
    }

    // /Users/xulijie/Documents/GCResearch/NewExperiments/medianProfiles/GroupByRDD-0.5
    private void compareSlowestTask(String dataMode, String mode, List<Application> appList, String appJsonDir) {
        Application slowestApp = appList.get(appList.size() - 1);

        List<Task> tasksInSelectedStages = new ArrayList<Task>();

        for (int id : selectedStageIds) {
            tasksInSelectedStages.addAll(slowestApp.getStage(id).getFirstStage().getTaskMap().values());
        }


        tasksInSelectedStages.sort(new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return (int) (task2.getFirstTaskAttempt().getDuration() - task1.getFirstCompletedTask().getDuration());
            }
        });


        Task slowestTask = tasksInSelectedStages.get(0);
        int slowestStageId = slowestTask.getStageId();
        System.out.println("SlowestStageID = " + slowestStageId);

        String collector = getGCName(slowestApp);


        System.out.println("\n\t" + getTaskInfo(dataMode, mode, collector, slowestTask.getFirstTaskAttempt(), appJsonDir));

        for (int i = appList.size() - 2; i >= 0; i--) {
            Application app = appList.get(i);
            Task task = app.getStage(slowestStageId).getFirstStage().getTaskMap()
                    .get(slowestTask.getTaskId());

            if (slowestTask.getFirstTaskAttempt().getIndex() != task.getFirstTaskAttempt().getIndex()) {
                for (Task t : app.getStage(slowestStageId).getFirstStage().getTaskMap().values()) {
                    TaskAttempt ta = t.getFirstCompletedTask();
                    TaskAttempt slowestTa = slowestTask.getFirstCompletedTask();

                    if (ta.getShuffleWriteMetrics_recordsWritten() == slowestTa.getShuffleWriteMetrics_recordsWritten() &&
                        ta.getOutputMetrics_recordsWritten() == slowestTa.getOutputMetrics_recordsWritten()) {
                        task = t;
                    }
                }
            }
            // System.out.println("------------[" + getGCName(app) + "]------------");
            collector = getGCName(app);
            System.out.println("\n\t" + getTaskInfo(dataMode, mode, collector, task.getFirstTaskAttempt(), appJsonDir));
        }
    }

    private int getSpillTime(Application app, TaskAttempt task, String appJsonDir) {

        int executorId = task.getExecutorId();

        Executor executor = app.getExecutor(executorId + "");

        String stderr = appJsonDir + File.separatorChar + app.getName() + "_" + app.getAppId() + File.separatorChar +
                "executors" + File.separatorChar + executorId + File.separatorChar + "stderr";
        List<String> excutorLogLines = JsonFileReader.readFileLines(stderr);

        int spillTime = 0;
        int taskId = task.getTaskId();
        for (String line : excutorLogLines) {
            if (line.contains("[Task " + taskId + " SpillMetrics]")) {
                int writeTime = Integer.parseInt(line.substring(line.indexOf("writeTime") + 12, line.indexOf(" s,")));
                long recordsWritten = Integer.parseInt(line.substring(line.indexOf("recordsWritten") + 17, line.indexOf(", bytesWritten")));
                spillTime += writeTime;
            }
        }

        return spillTime;
    }

    private Executor getExecutor(Application app, TaskAttempt task) {
        int executorId = task.getExecutorId();
        return app.getExecutor(executorId + "");
    }

    private String getTaskInfo(String dataMode, String mode, String collector, TaskAttempt task, String appJsonDir) {
        StringBuilder sb = new StringBuilder();
        sb.append(applicationName + "-" + dataMode + "-" + mode + " & ");
        Application app = appMap.get(mode + "-" + collector + "-" + dataMode);
        Executor executor = getExecutor(app, task);

        for (String metric: metrics) {
            if (metric.equalsIgnoreCase("Mode"))
                sb.append(collector + " & ");
            else if (metric.equalsIgnoreCase("ID"))
                sb.append(task.getIndex() + " & ");
            else if (metric.equalsIgnoreCase("Duration"))
                sb.append(task.getDuration() / 1000 + " s & ");
            else if (metric.equalsIgnoreCase("CPU Time"))
                sb.append(String.format("%.0f", (double) task.getExecutorCpuTime() / 1000 / 1000 / 1000) + " s & ");
            else if (metric.equalsIgnoreCase("GC Time"))
                sb.append(String.format("%.0f", (double) task.getJvmGcTime() / 1000) + " s & ");
            else if (metric.equalsIgnoreCase("Shuffled Size/Records"))
                sb.append(task.getShuffleReadMetrics_recordsRead() + " / " + task.getShuffleReadMetrics_bytesRead() / 1024 / 1024 + " MB & ");
            else if (metric.equalsIgnoreCase("Memory Spill"))
                sb.append(String.format("%.1f", (double)  task.getMemoryBytesSpilled() / 1024 / 1024 / 1024) + " GB & ");
            else if (metric.equalsIgnoreCase("Output Size/Records"))
                sb.append(task.getOutputMetrics_recordsWritten() + " / " + task.getOutputMetrics_bytesWritten() / 1024 / 1024 + " MB & ");
            else if (metric.equalsIgnoreCase("Spill Time"))
                sb.append(getSpillTime(app, task, appJsonDir) + " s & "); // E1-Parallel-0.5
            else if (metric.equalsIgnoreCase("Executor CPU"))
                sb.append(executor.getMaxCPUusage() + " \\% & ");
            else if (metric.equalsIgnoreCase("Executor Memory"))
                sb.append(String.format("%.1f", executor.getMaxMemoryUsage()) + " GB & ");
            else if (metric.equalsIgnoreCase("Executor YoungGC"))
                sb.append((long) executor.getgCeasyMetrics().getGcStatistics_minorGCTotalTime() + " s & ");
            else if (metric.equalsIgnoreCase("Executor FullGC"))
                sb.append((long) executor.getgCeasyMetrics().getGcStatistics_fullGCTotalTime() + " s & ");
            else if (metric.equalsIgnoreCase("Executor GCCause"))
                sb.append(executor.getgCeasyMetrics().getGcCauses() + " & ");
            else if (metric.equalsIgnoreCase("Executor GCTips"))
                sb.append(executor.getgCeasyMetrics().getTipsToReduceGCTime() + " & ");
            else if (metric.equalsIgnoreCase("Executor GCpause"))
                sb.append((long) executor.getGcMetrics().getAccumPause() + " s & ");
            else if (metric.equalsIgnoreCase("Executor FullGCPause"))
                sb.append((long) executor.getGcMetrics().getFullGCPause() + " s & ");
            else if (metric.equalsIgnoreCase("Executor ID & "))
                sb.append(executor.getId());
            else if (metric.equalsIgnoreCase("Input Size/Records"))
                sb.append(task.getInputMetrics_recordsRead() + " / " + task.getInputMetrics_bytesRead() / 1024 / 1024 + " MB & ");
            else if (metric.equalsIgnoreCase("Shuffle Write Size / Records"))
                sb.append(task.getShuffleWriteMetrics_recordsWritten() + " / " + task.getShuffleWriteMetrics_bytesWritten() / 1024 / 1024 + " MB & ");



        }

        return sb.toString() + " \\\\ \\hline";
    }

    // <E1-Parallel-0.5, E1-CMS-0.5, E1-G1-0.5>
    private void compareAppDuration(String dataMode, String mode, List<Application> appList) {
        appList.sort(new Comparator<Application>() {
            @Override
            public int compare(Application app1, Application app2) {
                return (int) (app1.getDuration() - app2.getDuration());
            }
        });

        long initDuration = 0;
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        System.out.println("[" + mode + "-" + dataMode + "]");
        for (Application app : appList) {
            long duration = app.getDuration();

            if (!app.getStatus().equalsIgnoreCase("SUCCEEDED")) {
                duration = -1;
            }

            double relativeDiff = RelativeDifference.getRelativeDifference(initDuration, duration) * 100;
            String label = "";
            if (relativeDiff > 20)
                label = "<<";
            else if (relativeDiff > 10)
                label = "<";
            else if (relativeDiff >= 0)
                label = "~";
            else
                label = "!";

            System.out.println("\t" + getGCName(app) + " = " + duration / 1000);
            initDuration = duration;
            if (first) {
                sb.append(getGCName(app));
                first = false;
            } else {
                sb.append(label + getGCName(app) + "(" + (int) relativeDiff + ")");
            }
        }

        System.out.println("\t" + sb.toString());

    }

    private String getGCName(Application app) {
        String appName = app.getName();
        String collector = "";

        if (appName.contains("Parallel"))
            collector = "Parallel";
        else if (app.getName().contains("CMS"))
            collector = "CMS";
        else if (app.getName().contains("G1"))
            collector = "G1";

        return collector;
    }


    public static void main(String args[]) {

        String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/NewExperiments/medianProfiles/";

        String[] metrics = {
                "Mode",
                "ID",
                "Duration",
                "GC Time",
                "Spill Time",
                "Memory Spill",
                "Executor CPU",
                "Executor Memory",
                "Executor YoungGC",
                "Executor FullGC",
                "Executor GCCause",
//                "Executor GCTips",
//                "Executor GCpause",
//                "Executor FullGCPause",
                "Executor ID",
                "Shuffled Size/Records",
                "Output Size/Records",
                "Input Size/Records",
                "Shuffle Write Size / Records"
        };



        /*
        String applicationName = "GroupBy";
        int[] selectedStageIds = new int[]{1};

        String appJsonDir0 = appJsonRootDir + "GroupByRDD-0.5";
        String appJsonDir1 = appJsonRootDir + "GroupByRDD-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics);
        comparator.computeRelativeDifference();
        */


        /*
        String applicationName = "Join";
        int[] selectedStageIds = new int[]{2};
        String appJsonDir0 = appJsonRootDir + "RDDJoin-0.5";
        String appJsonDir1 = appJsonRootDir + "RDDJoin-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics);
        comparator.computeRelativeDifference();
        */



        String applicationName = "SVM";
        int[] selectedStageIds = new int[]{4, 6, 8, 10, 12, 14, 16, 18, 20, 22};
        String appJsonDir0 = appJsonRootDir + "SVM-0.5";
        String appJsonDir1 = appJsonRootDir + "SVM-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics);
        comparator.computeRelativeDifference();

        /*
        String applicationName = "PageRank";
        int[] selectedStageIds = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String appJsonDir0 = appJsonRootDir + "PageRank-0.5";
        String appJsonDir1 = appJsonRootDir + "PageRank-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics);
        comparator.computeRelativeDifference();
        */
    }
}
