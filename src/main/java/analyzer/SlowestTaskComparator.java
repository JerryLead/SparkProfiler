package analyzer;

import appinfo.*;
;
import com.google.gson.*;
import jdk.nashorn.internal.objects.DataPropertyDescriptor;
import profiler.SparkAppProfiler;


import util.DateParser;
import util.FileTextWriter;
import util.JsonFileReader;
import util.RelativeDifference;

import java.io.File;
import java.io.FileWriter;
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

    // taskRange: only plot the resource usage while the task is running
    private boolean taskRange;

    // if true, output the slowest task in each application (not the tasks with the same ids)
    private boolean slowestTaskMode;

    public SlowestTaskComparator(String applicationName, int[] selectedStageIds,
                                 String appJsonDir0, String appJsonDir1,
                                 String[] metrics, boolean taskRange,
                                 boolean slowestTaskMode) {
        this.applicationName = applicationName;
        this.selectedStageIds = selectedStageIds;
        this.appJsonDir0 = appJsonDir0;
        this.appJsonDir1 = appJsonDir1;
        this.metrics = metrics;
        this.slowestTaskMode = slowestTaskMode;
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
                collector = "P";
            else if (app.getName().contains("CMS"))
                collector = "C";
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
                collector = "P";
            else if (app.getName().contains("CMS"))
                collector = "C";
            else if (app.getName().contains("G1"))
                collector = "G1";

            appMap.put(mode + "-" + collector + "-1.0", app);
        }
    }

    private void computeRelativeDifference() {
        String[] dataModes = {"0.5", "1.0"};
        String[] modes = {"E1", "E2", "E4"};
        String[] collectors = {"P", "C", "G1"};

        Map<String, String> latexTable = new HashMap<String, String>();

        for (String dataMode : dataModes) {
            for (String mode : modes) {

                // <E1-Parallel-0.5, E1-CMS-0.5, E1-G1-0.5>
                List<Application> appList = new ArrayList<Application>();
                for (String collector : collectors) {
                    String key = mode + "-" + collector + "-" + dataMode;

                    Application app = appMap.get(key);
                    appList.add(app);
                }

                String latex = compareAppDuration(dataMode, mode, appList);

                if (latexTable.get(mode) == null) {
                    latexTable.put(mode, latex);
                } else {
                    latexTable.put(mode, latexTable.get(mode) + latex);
                }

                List<Application> successfulAppList = new ArrayList<Application>();
                List<Application> failedAppList = new ArrayList<Application>();

                for (Application app : appList) {
                    if (app.getStatus().equalsIgnoreCase("SUCCEEDED"))
                        successfulAppList.add(app);
                    else
                        failedAppList.add(app);
                }

                // System.out.println("\n\n=============[" + mode + "-" + dataMode + "]============");
                if (dataMode.equalsIgnoreCase("0.5") && !successfulAppList.isEmpty())
                    compareSlowestTask(dataMode, mode, successfulAppList, appJsonDir0);
                else if (dataMode.equalsIgnoreCase("1.0") && !successfulAppList.isEmpty())
                    compareSlowestTask(dataMode, mode, successfulAppList, appJsonDir1);

                if (dataMode.equalsIgnoreCase("0.5") && !failedAppList.isEmpty())
                    outputFailedTaskExecutorResourceUsage(dataMode, mode, failedAppList, appJsonDir0);
                else if (dataMode.equalsIgnoreCase("1.0") && !failedAppList.isEmpty())
                    outputFailedTaskExecutorResourceUsage(dataMode, mode, failedAppList, appJsonDir1);

            }
        }

        System.out.println("\n===========================================================================\n");

        System.out.println(latexTable.get("E1"));
        System.out.println(latexTable.get("E2"));
        System.out.println(latexTable.get("E4"));


    }

    private void outputFailedTaskExecutorResourceUsage(String dataMode, String mode, List<Application> failedAppList, String appJsonDir) {

        for (Application failedApp : failedAppList) {

            List<TaskAttempt> tasksInSelectedStages = new ArrayList<TaskAttempt>();

            for (int id : selectedStageIds) {
                if (failedApp.getStage(id) != null && failedApp.getStage(id).getFailedStage() != null) {
                    for (Task task : failedApp.getStage(id).getFailedStage().getTaskMap().values()) {
                        if (task.getFailedTask() != null)
                            tasksInSelectedStages.add(task.getFailedTask());
                    }
                }
            }

            for (TaskAttempt task : tasksInSelectedStages) {
                StringBuilder sb = new StringBuilder();

                // "launchTime" : "2017-11-24T19:31:22.897GMT",
                String startTime = task.getLaunchTime();
                long endMS = DateParser.parseDate(startTime) + task.getDuration();

                // HH:mm:ss
                startTime = DateParser.getDate(DateParser.parseDate(startTime));
                String endTime = DateParser.getDate(endMS);

                sb.append("[Task][Id = " + task.getTaskId() + "][Index = " + task.getIndex() + "]\n");
                sb.append(task + "\n");

                int startTimeValue = DateParser.getTimeValue(startTime);
                int endTimeValue = DateParser.getTimeValue(endTime);


                String collector = getGCName(failedApp);
                Application app = appMap.get(mode + "-" + collector + "-" + dataMode);
                String appName = app.getName().substring(0, app.getName().indexOf("-"));

                Executor executor = getExecutor(app, task);

                sb.append("[Executor][Id = " + executor.getId() + "]\n");
                sb.append(executor + "\n\n");

                sb.append("[TopMetrics][start = " + startTime + ", end = " + endTime + "]\n");

                String filePath = appJsonDir + File.separatorChar + "failedTasks";
                filePath += File.separatorChar + appName + "-" + dataMode + "-" + mode + "-" + collector;

                String filename = appName + "-" + dataMode + "-" + mode + "-" + collector + "-" + task.getTaskId()
                        + "-" + task.getIndex() + "-e" + executor.getId() + ".txt";

                List<TopMetrics> list = executor.getTopMetricsList();

                List<TopMetrics> filteredExecutorMetrics = new ArrayList<TopMetrics>();

                if (taskRange) {
                    int initValue = 0;
                    boolean update24 = false;

                    for (TopMetrics topMetrics : list) {
                        String time = topMetrics.getTime();
                        int timeValue = DateParser.getTimeValue(time);

                        if (timeValue < initValue) {
                            timeValue += 24 * 60 * 60;
                            if (update24 == false) {
                                if (startTime.startsWith("0"))
                                    startTimeValue += 24 * 60 * 60;
                                if (endTime.startsWith("0"))
                                    endTimeValue += 24 * 60 * 60;
                                update24 = true;
                            }
                        }

                        if (filteredExecutorMetrics.isEmpty()) {
                            if (timeValue > startTimeValue - 5) {
                                filteredExecutorMetrics.add(topMetrics);
                            }
                        } else {
                            if (timeValue < endTimeValue + 5)
                                filteredExecutorMetrics.add(topMetrics);
                        }

                        initValue = timeValue;
                    }
                } else {
                    filteredExecutorMetrics.addAll(list);
                }


                sb.append("\n[Top Metrics][Executor " + executor.getId() + "]\n");
                for (TopMetrics topMetrics : filteredExecutorMetrics)
                    sb.append(topMetrics + "\n");

                String stderrFile = appJsonDir + File.separatorChar + app.getName() + "_" + app.getAppId() + File.separatorChar +
                        "executors" + File.separatorChar + executor.getId() + File.separatorChar + "stderr";

                List<String> lines = JsonFileReader.readFileLines(stderrFile);

                String slave = "";
                if (!lines.isEmpty()) {
                    // 17/11/02 11:17:44 INFO CoarseGrainedExecutorBackend: Started daemon with process name: 14388@slave7
                    String line = lines.get(0);
                    String pid = line.substring(line.lastIndexOf(':') + 2, line.lastIndexOf('@'));
                    slave = line.substring(line.lastIndexOf('@') + 1);
                }

                slave = slave.replace("s", "aliS");

                String slaveTopMetricsPath = appJsonDir.replace("medianProfiles", "profiles");
                slaveTopMetricsPath += File.separatorChar + "topMetrics" + File.separatorChar + slave
                        + File.separatorChar
                        + app.getName().replace("GroupByRDD", "rGroupBy").replace("RDDJoin", "rjoin")
                        + ".txt";

                // System.out.println(slaveTopMetricsPath);

                List<TopMetrics> slaveMetricsList = analyzeSlaveTopMetrics(slaveTopMetricsPath);
                List<TopMetrics> filteredSlaveMetricsList = new ArrayList<TopMetrics>();

                if (taskRange) {
                    int initValue = 0;
                    for (TopMetrics topMetrics : slaveMetricsList) {
                        String time = topMetrics.getTime();
                        int timeValue = DateParser.getTimeValue(time);

                        if (timeValue < initValue) {
                            timeValue += 24 * 60 * 60;
                        }

                        if (filteredSlaveMetricsList.isEmpty()) {
                            if (timeValue > startTimeValue - 5) {
                                filteredSlaveMetricsList.add(topMetrics);
                            }
                        } else {
                            if (timeValue < endTimeValue + 5)
                                filteredSlaveMetricsList.add(topMetrics);
                        }

                        initValue = timeValue;
                    }
                } else {
                    filteredSlaveMetricsList.addAll(slaveMetricsList);
                }


                sb.append("\n[Top Metrics][" + slave + "]\n");
                for (TopMetrics topMetrics : filteredSlaveMetricsList)
                    sb.append(topMetrics + "\n");

                FileTextWriter.write(filePath + File.separatorChar + filename, sb.toString());
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
        outputTaskAndExecutorResourceUsage(dataMode, mode, collector, slowestTask.getFirstTaskAttempt(), appJsonDir);

        for (int i = appList.size() - 2; i >= 0; i--) {
            Application app = appList.get(i);

            Task task = null;

            if (slowestTaskMode) {
                tasksInSelectedStages = new ArrayList<Task>();

                for (int id : selectedStageIds) {
                    tasksInSelectedStages.addAll(app.getStage(id).getFirstStage().getTaskMap().values());
                }

                tasksInSelectedStages.sort(new Comparator<Task>() {
                    @Override
                    public int compare(Task task1, Task task2) {
                        return (int) (task2.getFirstTaskAttempt().getDuration() - task1.getFirstCompletedTask().getDuration());
                    }
                });

                task = tasksInSelectedStages.get(0);
            } else {
                task = app.getStage(slowestStageId).getFirstStage().getTaskMap()
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
            }

            // System.out.println("------------[" + getGCName(app) + "]------------");
            collector = getGCName(app);
            System.out.println("\n\t" + getTaskInfo(dataMode, mode, collector, task.getFirstTaskAttempt(), appJsonDir));
            outputTaskAndExecutorResourceUsage(dataMode, mode, collector, task.getFirstTaskAttempt(), appJsonDir);
        }
    }


    private void outputTaskAndExecutorResourceUsage(String dataMode, String mode, String collector, TaskAttempt task, String appJsonDir) {
        String filePath = appJsonDir + File.separatorChar + "slowestTasks";
        StringBuilder sb = new StringBuilder();

        // "launchTime" : "2017-11-24T19:31:22.897GMT",
        String startTime = task.getLaunchTime();
        long endMS = DateParser.parseDate(startTime) + task.getDuration();

        // HH:mm:ss
        startTime = DateParser.getDate(DateParser.parseDate(startTime));
        String endTime = DateParser.getDate(endMS);

        sb.append("[Task][Id = " + task.getTaskId() + "][Index = " + task.getIndex() + "]\n");
        sb.append(task + "\n");

        int startTimeValue = DateParser.getTimeValue(startTime);
        int endTimeValue = DateParser.getTimeValue(endTime);

        Application app = appMap.get(mode + "-" + collector + "-" + dataMode);
        String appName = app.getName().substring(0, app.getName().indexOf("-"));

        Executor executor = getExecutor(app, task);

        sb.append("[Executor][Id = " + executor.getId() + "]\n");
        sb.append(executor + "\n\n");

        sb.append("[TopMetrics][start = " + startTime + ", end = " + endTime + "]\n");

        filePath += File.separatorChar + appName + "-" + dataMode + "-" + mode + "-" + collector;

        String filename = appName + "-" + dataMode + "-" + mode + "-" + collector + "-" + task.getTaskId()
                + "-" + task.getIndex() + "-e" + executor.getId() + ".txt";

        List<TopMetrics> list = executor.getTopMetricsList();

        List<TopMetrics> filteredExecutorMetrics = new ArrayList<TopMetrics>();

        if (taskRange) {
            int initValue = 0;
            boolean update24 = false;

            for (TopMetrics topMetrics : list) {
                String time = topMetrics.getTime();
                int timeValue = DateParser.getTimeValue(time);

                if (timeValue < initValue) {
                    timeValue += 24 * 60 * 60;
                    if (update24 == false) {
                        if (startTime.startsWith("0"))
                            startTimeValue += 24 * 60 * 60;
                        if (endTime.startsWith("0"))
                            endTimeValue += 24 * 60 * 60;
                        update24 = true;
                    }
                }

                if (filteredExecutorMetrics.isEmpty()) {
                    if (timeValue > startTimeValue - 5) {
                        filteredExecutorMetrics.add(topMetrics);
                    }
                } else {
                    if (timeValue < endTimeValue + 5)
                        filteredExecutorMetrics.add(topMetrics);
                }

                initValue = timeValue;
            }
        } else {
            filteredExecutorMetrics.addAll(list);
        }


        sb.append("\n[Top Metrics][Executor " + executor.getId() + "]\n");
        for (TopMetrics topMetrics : filteredExecutorMetrics)
            sb.append(topMetrics + "\n");

        String stderrFile = appJsonDir + File.separatorChar + app.getName() + "_" + app.getAppId() + File.separatorChar +
                "executors" + File.separatorChar + executor.getId() + File.separatorChar + "stderr";

        List<String> lines = JsonFileReader.readFileLines(stderrFile);

        String slave = "";
        if (!lines.isEmpty()) {
            // 17/11/02 11:17:44 INFO CoarseGrainedExecutorBackend: Started daemon with process name: 14388@slave7
            String line = lines.get(0);
            String pid = line.substring(line.lastIndexOf(':') + 2, line.lastIndexOf('@'));
            slave = line.substring(line.lastIndexOf('@') + 1);
        }

        slave = slave.replace("s", "aliS");

        String slaveTopMetricsPath = appJsonDir.replace("medianProfiles", "profiles");
        slaveTopMetricsPath += File.separatorChar + "topMetrics" + File.separatorChar + slave
                + File.separatorChar
                + app.getName().replace("GroupByRDD", "rGroupBy").replace("RDDJoin", "rjoin")
                + ".txt";

        // System.out.println(slaveTopMetricsPath);

        List<TopMetrics> slaveMetricsList = analyzeSlaveTopMetrics(slaveTopMetricsPath);
        List<TopMetrics> filteredSlaveMetricsList = new ArrayList<TopMetrics>();

        if (taskRange) {
            int initValue = 0;
            for (TopMetrics topMetrics : slaveMetricsList) {
                String time = topMetrics.getTime();
                int timeValue = DateParser.getTimeValue(time);

                if (timeValue < initValue) {
                    timeValue += 24 * 60 * 60;
                }

                if (filteredSlaveMetricsList.isEmpty()) {
                    if (timeValue > startTimeValue - 5) {
                        filteredSlaveMetricsList.add(topMetrics);
                    }
                } else {
                    if (timeValue < endTimeValue + 5)
                        filteredSlaveMetricsList.add(topMetrics);
                }

                initValue = timeValue;
            }
        } else {
            filteredSlaveMetricsList.addAll(slaveMetricsList);
        }


        sb.append("\n[Top Metrics][" + slave + "]\n");
        for (TopMetrics topMetrics : filteredSlaveMetricsList)
            sb.append(topMetrics + "\n");

        FileTextWriter.write(filePath + File.separatorChar + filename, sb.toString());

    }

    private List<TopMetrics> analyzeSlaveTopMetrics(String executorTopMetricsPath) {
        // key = appName_slaveName_PID
        List<TopMetrics> slaveTopMetrics = new ArrayList<TopMetrics>();

        List<String> topMetricsLines = JsonFileReader.readFileLines(executorTopMetricsPath);
        String time = "";
        double cpu = 0;
        double memory = 0;

        for (String line : topMetricsLines) {
            if (line.startsWith("top"))
                time = line.substring(line.indexOf("-") + 2, line.indexOf("up") - 1);
            // %Cpu(s): 54.2 us,  2.0 sy,  0.0 ni, 34.6 id,  8.7 wa,  0.0 hi,  0.6 si,  0.0 st
            // KiB Mem : 32947020 total, 28891956 free,  2518352 used,  1536712 buff/cache
            if (line.startsWith("%Cpu"))
                cpu = 100 - Double.parseDouble(line.substring(line.indexOf("ni") + 4, line.indexOf("id") - 1));
            if (line.startsWith("KiB Mem")) {
                memory = Double.parseDouble(line.substring(line.indexOf("free") + 5, line.indexOf("used") - 1).trim());
                slaveTopMetrics.add(new TopMetrics(time, cpu, memory / 1024 / 1024));
            }
        }

        return slaveTopMetrics;
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
                long recordsWritten = Long.parseLong(line.substring(line.indexOf("recordsWritten") + 17, line.indexOf(", bytesWritten")));
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
                // sb.append(task.getIndex() + "-" + task.getTaskId() + " & ");
                sb.append(task.getTaskId() + " & ");
            else if (metric.equalsIgnoreCase("Duration"))
                sb.append(task.getDuration() / 1000 + " s & ");
            else if (metric.equalsIgnoreCase("CPU Time"))
                sb.append(String.format("%.0f", (double) task.getExecutorCpuTime() / 1000 / 1000 / 1000) + " s & ");
            else if (metric.equalsIgnoreCase("GC Time"))
                sb.append(String.format("%.0f", (double) task.getJvmGcTime() / 1000) + " s & ");
            else if (metric.equalsIgnoreCase("Shuffled Size/Records"))
                sb.append(task.getShuffleReadMetrics_recordsRead() + " / " + task.getShuffleReadMetrics_bytesRead() / 1024 / 1024 + " MB & ");
            else if (metric.equalsIgnoreCase("Memory Spill")) {
                double spill = (double) task.getMemoryBytesSpilled() / 1024 / 1024 / 1024;
                if (spill == 0)
                    sb.append("0 G & ");
                else
                    sb.append(String.format("%.1f", spill) + " G & ");
            }
            else if (metric.equalsIgnoreCase("Output Size/Records"))
                sb.append(task.getOutputMetrics_recordsWritten() + " / " + task.getOutputMetrics_bytesWritten() / 1024 / 1024 + " MB & ");
            else if (metric.equalsIgnoreCase("Spill Time"))
                sb.append(getSpillTime(app, task, appJsonDir) + " s & "); // E1-Parallel-0.5
            else if (metric.equalsIgnoreCase("executorDeserializeTime"))
                sb.append(task.getExecutorDeserializeTime() + " ms & ");
            else if (metric.equalsIgnoreCase("resultSerializationTime"))
                sb.append(task.getResultSerializationTime() + " ms & "); // E1-Parallel-0.5
            else if (metric.equalsIgnoreCase("CPU Time"))
                sb.append(String.format("%.0f", (double) task.getExecutorCpuTime() / 1000 / 1000 / 1000) + " s & ");
            else if (metric.equalsIgnoreCase("Executor CPU"))
                sb.append(executor.getMaxCPUusage() + " \\% & ");
            else if (metric.equalsIgnoreCase("Executor Memory"))
                sb.append(String.format("%.1f", executor.getMaxMemoryUsage()) + " GB & ");
            else if (metric.equalsIgnoreCase("Executor Allocated"))
                sb.append(String.format("%.1f", executor.getgCeasyMetrics().getJvmHeapSize_total_allocatedSize() / 1024) + " G & ");
            else if (metric.equalsIgnoreCase("Executor Peak"))
                sb.append(String.format("%.1f", executor.getgCeasyMetrics().getJvmHeapSize_total_peakSize() / 1024) + " G & ");
            else if (metric.equalsIgnoreCase("Executor Old Peak"))
                sb.append(String.format("%.1f", executor.getgCeasyMetrics().getJvmHeapSize_oldGen_peakSize() / 1024) + " G & ");
            else if (metric.equalsIgnoreCase("Executor YoungGC"))
                sb.append((long) executor.getgCeasyMetrics().getGcStatistics_minorGCTotalTime() + " s (" +
                        executor.getgCeasyMetrics().getGcStatistics_minorGCCount() + ") & ");
            else if (metric.equalsIgnoreCase("Executor FullGC"))
                sb.append(executor.getgCeasyMetrics().getGcStatistics_fullGCTotalTime() + " s (" +
                        executor.getgCeasyMetrics().getGcStatistics_fullGCCount() + ", " +
                        (long) executor.getgCeasyMetrics().getGcStatistics_fullGCMaxTime() + " s)" +
                        " & ");
            else if (metric.equalsIgnoreCase("Executor GCCause"))
                sb.append(simpleGCCause(executor.getgCeasyMetrics().getGcCauses()) + " & ");
            else if (metric.equalsIgnoreCase("Executor GCTips"))
                sb.append(executor.getgCeasyMetrics().getTipsToReduceGCTime() + " & ");
            else if (metric.equalsIgnoreCase("Executor GCpause"))
                sb.append((long) executor.getGcMetrics().getAccumPause() + " s & ");
            else if (metric.equalsIgnoreCase("Executor FullGCPause"))
                sb.append((long) executor.getGcMetrics().getFullGCPause() + " s & ");
            else if (metric.equalsIgnoreCase("Executor ID"))
                sb.append(executor.getId() + " & ");
            else if (metric.equalsIgnoreCase("Input Size/Records"))
                sb.append(task.getInputMetrics_recordsRead() + " / " + task.getInputMetrics_bytesRead() / 1024 / 1024 + " M & ");
            else if (metric.equalsIgnoreCase("Shuffle Write Size / Records"))
                sb.append(task.getShuffleWriteMetrics_recordsWritten() + " / " + task.getShuffleWriteMetrics_bytesWritten() / 1024 / 1024 + " M & ");



        }

        return sb.toString() + " \\\\ \\hline";
    }

    // <E1-Parallel-0.5, E1-CMS-0.5, E1-G1-0.5>
    private String compareAppDuration(String dataMode, String mode, List<Application> appList) {
        appList.sort(new Comparator<Application>() {
            @Override
            public int compare(Application app1, Application app2) {
                return (int) (app1.getDuration() - app2.getDuration());
            }
        });

        long initDuration = 0;
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        String parallel = "";
        String cms = "";
        String g1 = "";

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

            String gcName = getGCName(app);

            double durationMin = (double) duration / 1000 / 60;
            double slowestStageDuration = 0;

            if (!app.getStatus().equalsIgnoreCase("SUCCEEDED")) {
                slowestStageDuration = -1;
            } else {
                for (int id : selectedStageIds)
                    slowestStageDuration += (double) app.getStage(id).getFirstStage().getDuration();
                slowestStageDuration = slowestStageDuration / 1000 / 60;
            }

            System.out.println("\t" + gcName + " = " + durationMin);

            String durationMetrics = "{" + String.format("%.1f", durationMin) + "}_{(" + String.format("%.1f", slowestStageDuration) + ")}";

            if (gcName.equals("P"))
                parallel = durationMetrics; // min
            else if (gcName.equals("C"))
                cms = durationMetrics;
            else if (gcName.equals("G1"))
                g1 = durationMetrics;

            initDuration = duration;
            if (first) {
                sb.append(getGCName(app));
                first = false;
            } else {
                sb.append(label + getGCName(app));
                // sb.append(label + getGCName(app) + "(" + (int) relativeDiff + ")");
            }
        }

        System.out.println("\t" + sb.toString());

        // & E1 & ${3.4}$  &  ${2.5}$ & ${2.6}$  & $ [C, G1] \ll P$
        String latex = " & " + " $" + parallel + "$ & $"
                + cms + "$ & $"
                + g1 + "$ & $ "
                + sb.toString() + " $ ";

        if (dataMode.equals("1.0"))
            latex = latex + "\\\\ \\cline{2-10}";
        else
            latex = " & " + mode + latex;

        if (mode.equals("E1") && dataMode.equals("0.5"))
            latex = applicationName + latex;

        return latex;
    }

    private String getGCName(Application app) {
        String appName = app.getName();
        String collector = "";

        if (appName.contains("Parallel"))
            collector = "P";
        else if (app.getName().contains("CMS"))
            collector = "C";
        else if (app.getName().contains("G1"))
            collector = "G1";

        return collector;
    }

    private String simpleGCCause(String cause) {
        StringBuilder sb = new StringBuilder();

        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(cause);
            JsonArray gcCauseJsonArray = null;

            if (el.isJsonArray())
                gcCauseJsonArray = el.getAsJsonArray();
            else {
                System.err.println("Error in parsing the jobs json!");
                System.exit(1);
            }

            for (JsonElement gcCauseElem : gcCauseJsonArray) {
                JsonObject gcCauseObject = gcCauseElem.getAsJsonObject();
                sb.append(gcCauseObject.get("cause") + " (");
                sb.append(gcCauseObject.get("count") + "), ");
            }

            String simpleCause = sb.toString().replaceAll("\"", "").trim();

            if (!simpleCause.isEmpty())
                simpleCause = simpleCause.substring(0, simpleCause.length() - 1);

            return simpleCause;

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static void main(String args[]) {

        String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/Experiments-11-17/medianProfiles/";

        String[] metrics = {
                "Mode",
                "ID",
                "Executor ID",
                "Duration",
                "GC Time",
                "Spill Time",
                "Memory Spill",
//                "executorDeserializeTime",
//                "resultSerializationTime",
//                "CPU Time",

                "Executor Peak",
                "Executor Old Peak",
                "Executor YoungGC",
                "Executor FullGC",
                //"Executor CPU",
                //"Executor GCCause",
//                "Executor GCTips",
//                "Executor GCpause",
//                "Executor FullGCPause",
                "Shuffled Size/Records",
                "Output Size/Records",
                "Input Size/Records",
                "Shuffle Write Size / Records"
        };

        boolean slowestmode = false;


        String applicationName = "GroupBy";
        int[] selectedStageIds = new int[]{1};

        String appJsonDir0 = appJsonRootDir + "GroupByRDD-0.5";
        String appJsonDir1 = appJsonRootDir + "GroupByRDD-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics, false, slowestmode);
        comparator.computeRelativeDifference();


/*
        String applicationName = "Join";
        int[] selectedStageIds = new int[]{2};
        String appJsonDir0 = appJsonRootDir + "RDDJoin-0.5";
        String appJsonDir1 = appJsonRootDir + "RDDJoin-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(
                applicationName, selectedStageIds,
                appJsonDir0, appJsonDir1, metrics, false, false);
        comparator.computeRelativeDifference();
*/
/*

        String applicationName = "SVM";
        int[] selectedStageIds = new int[]{4, 6, 8, 10, 12, 14, 16, 18, 20, 22};
        // int[] selectedStageIds = new int[]{3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
        String appJsonDir0 = appJsonRootDir + "SVM-0.5";
        String appJsonDir1 = appJsonRootDir + "SVM-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics, false, slowestmode);
        comparator.computeRelativeDifference();
*/

/*
        String applicationName = "PageRank";
        int[] selectedStageIds = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String appJsonDir0 = appJsonRootDir + "PageRank-0.5";
        String appJsonDir1 = appJsonRootDir + "PageRank-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics, false, slowestmode);
        comparator.computeRelativeDifference();
*/

    }
}
