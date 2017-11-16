package analyzer;

import appinfo.Application;
import appinfo.Executor;
import profiler.SparkAppProfiler;
import util.RelativeDifference;

import java.util.*;

/**
 * Created by xulijie on 17-11-13.
 */
public class ExecutorMemoryComparator {

    // key = E1-Parallel-0.5
    private Map<String, Application> appMap = new HashMap<String, Application>();
    private int[] selectedStageIds;
    private String appJsonDir0;
    private String appJsonDir1;

    private String[] metrics;
    private String applicationName;

    public ExecutorMemoryComparator(String applicationName, int[] selectedStageIds, String appJsonDir0, String appJsonDir1, String[] metrics) {
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

    // <E1-Parallel-0.5, E1-CMS-0.5, E1-G1-0.5>
    private void compareAppMaxMemory(String dataMode, String mode, List<Application> appList) {
        appList.sort(new Comparator<Application>() {
            @Override
            public int compare(Application app1, Application app2) {
                return (int) (app1.getMaxMemoryUsage() - app2.getMaxMemoryUsage());
            }
        });

        double initMaxMemory = 0;
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        System.out.println("[" + mode + "-" + dataMode + "]");
        for (Application app : appList) {
            // double maxMemory = app.getMaxMemoryUsage();
            double maxMemory = app.getMaxMemoryUsage();

            /*
            if (!app.getStatus().equalsIgnoreCase("SUCCEEDED")) {
                maxMemory = -1;
            }
            */

            double relativeDiff = RelativeDifference.getRelativeDifference(initMaxMemory, maxMemory) * 100;
            String label = "";
            if (relativeDiff > 20)
                label = "<<";
            else if (relativeDiff > 10)
                label = "<";
            else if (relativeDiff >= 0)
                label = "~";
            else
                label = "!";

            System.out.println("\t" + getGCName(app) + " = " + String.format("%.1f", maxMemory));
            initMaxMemory = maxMemory;
            if (first) {
                sb.append(getGCName(app));
                first = false;
            } else {
                sb.append(label + getGCName(app) + "(" + (int) relativeDiff + ")");
            }
        }

        System.out.println("\t" + sb.toString());

    }

    // <E1-Parallel-0.5, E1-CMS-0.5, E1-G1-0.5>
    private void compareAppMaxAllocatedMemory(String dataMode, String mode, List<Application> appList) {
        appList.sort(new Comparator<Application>() {
            @Override
            public int compare(Application app1, Application app2) {
                return (int) (app1.getMaxAllocatedMemory() - app2.getMaxAllocatedMemory());
            }
        });

        double initMaxMemory = 0;
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        System.out.println("[" + mode + "-" + dataMode + "]");
        for (Application app : appList) {
            // double maxMemory = app.getMaxMemoryUsage();
            double maxMemory = app.getMaxAllocatedMemory();

            /*
            if (!app.getStatus().equalsIgnoreCase("SUCCEEDED")) {
                maxMemory = -1;
            }
            */

            double relativeDiff = RelativeDifference.getRelativeDifference(initMaxMemory, maxMemory) * 100;
            String label = "";
            if (relativeDiff > 20)
                label = "<<";
            else if (relativeDiff > 10)
                label = "<";
            else if (relativeDiff >= 0)
                label = "~";
            else
                label = "!";

            System.out.println("\t" + getGCName(app) + " = " + String.format("%.1f", maxMemory));
            initMaxMemory = maxMemory;
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

                // compareAppMaxMemory(dataMode, mode, appList);
                compareAppMaxAllocatedMemory(dataMode, mode, appList);


                List<Application> successfulAppList = new ArrayList<Application>();

                for (Application app : appList) {
                    if (app.getStatus().equalsIgnoreCase("SUCCEEDED"))
                        successfulAppList.add(app);
                }

                if (!successfulAppList.isEmpty()) {
                    compareHighestMemoryExecutor(dataMode, mode, successfulAppList);
                }

            }
        }
    }


    // /Users/xulijie/Documents/GCResearch/NewExperiments/medianProfiles/GroupByRDD-0.5
    private void compareHighestMemoryExecutor(String dataMode, String mode, List<Application> appList) {


        List<Executor> maxMemoryExecutors = new ArrayList<Executor>();

        for (Application app : appList) {
            double maxMemory = 0;
            Executor maxMemoryExecutor = null;
            for (Executor executor : app.getExecutors()) {
                if (executor.getMaxMemoryUsage() > maxMemory) {
                    maxMemory = executor.getMaxMemoryUsage();
                    maxMemoryExecutor = executor;
                }
            }

            maxMemoryExecutors.add(maxMemoryExecutor);
        }

        for (String metric : metrics) {
            System.out.print(metric + " & ");
        }

        for (int i = 0; i < maxMemoryExecutors.size(); i++) {
            Application app = appList.get(i);
            Executor executor = maxMemoryExecutors.get(i);

            String collector = getGCName(app);

            System.out.println();
            displayExecutor(executor, dataMode, mode, collector);

        }




    }

    private void displayExecutor(Executor executor, String dataMode, String mode, String collector) {
        StringBuilder sb = new StringBuilder();
        sb.append(applicationName + "-" + dataMode + "-" + mode + " & ");

        for (String metric: metrics) {
            if (metric.equalsIgnoreCase("Mode"))
                sb.append(collector + " & ");
            else if (metric.equalsIgnoreCase("ID"))
                sb.append(executor.getId() + " & ");
            else if (metric.equalsIgnoreCase("Duration"))
                sb.append(executor.getTotalDuration() / 1000 + " s & ");
            else if (metric.equalsIgnoreCase("TaskNum"))
                sb.append(executor.getCompletedTasks() + " & ");
            else if (metric.equalsIgnoreCase("Memory"))
                sb.append(String.format("%.1f", executor.getMaxMemoryUsage()) + " GB & ");
            else if (metric.equalsIgnoreCase("Heap"))
                sb.append(String.format("%.1f", (double) executor.getMaxMemory() / 1024 / 1024 / 1024) + " GB & ");
            else if (metric.equalsIgnoreCase("Peak"))
                sb.append(String.format("%.1f", (double) executor.getgCeasyMetrics().getJvmHeapSize_total_peakSize() / 1024) + " GB & ");
            else if (metric.equalsIgnoreCase("Allocated"))
                sb.append(String.format("%.1f", (double) executor.getgCeasyMetrics().getJvmHeapSize_total_allocatedSize() / 1024) + " GB & ");
            else if (metric.equalsIgnoreCase("Young Peak"))
                sb.append(String.format("%.1f", executor.getgCeasyMetrics().getJvmHeapSize_youngGen_peakSize()) + " GB & ");
            else if (metric.equalsIgnoreCase("Old Peak"))
                sb.append(String.format("%.1f", executor.getgCeasyMetrics().getJvmHeapSize_oldGen_peakSize()) + " GB & ");

            else if (metric.equalsIgnoreCase("GC Time"))
                sb.append(executor.getTotalGCTime() / 1000 + " s & ");
            else if (metric.equalsIgnoreCase("YoungGC"))
                sb.append((long) executor.getgCeasyMetrics().getGcStatistics_minorGCTotalTime() + " s & ");
            else if (metric.equalsIgnoreCase("FullGC"))
                sb.append((long) executor.getgCeasyMetrics().getGcStatistics_fullGCTotalTime() + " s & ");

            else if (metric.equalsIgnoreCase("Shuffled Records"))
                sb.append(executor.getTotalShuffleRead() + " & ");
            else if (metric.equalsIgnoreCase("Input Bytes"))
                sb.append(executor.getTotalInputBytes() / 1024 / 1024 + " MB & ");
            else if (metric.equalsIgnoreCase("Output Records"))
                sb.append(executor.getTotalShuffleWrite() + " & ");
            else if (metric.equalsIgnoreCase("Executor GCCause"))
                sb.append(executor.getgCeasyMetrics().getGcCauses() + " & ");
            else if (metric.equalsIgnoreCase("Executor GCTips"))
                sb.append(executor.getgCeasyMetrics().getTipsToReduceGCTime() + " & ");
            else if (metric.equalsIgnoreCase("Executor GCpause"))
                sb.append((long) executor.getGcMetrics().getAccumPause() + " s & ");
            else if (metric.equalsIgnoreCase("Executor FullGCPause"))
                sb.append((long) executor.getGcMetrics().getFullGCPause() + " s & ");
            else if (metric.equalsIgnoreCase("Footprint"))
                sb.append(String.format("%.1f", (double) executor.getGcMetrics().getFootprint() / 1024) + " GB & ");
            else if (metric.equalsIgnoreCase("YGCT"))
                sb.append((long) executor.getGcMetrics().getGcPause() + " s & ");
            else if (metric.equalsIgnoreCase("FGCT"))
                sb.append((long) executor.getGcMetrics().getFullGCPause() + " s & ");

        }

        System.out.println(sb.toString() + " \\\\ \\hline");

    }


    public static void main(String args[]) {

        String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/NewExperiments/medianProfiles/";

        String[] metrics = {
                "Mode",
                "ID",
                "Duration",
                "TaskNum",

                "Allocated",
                "Peak",
                "YoungGC",
                "FullGC",
                "GC Time",
                "YGCT",
                "FGCT"

        };


        String applicationName = "GroupBy";
        int[] selectedStageIds = new int[]{1};

        String appJsonDir0 = appJsonRootDir + "GroupByRDD-0.5";
        String appJsonDir1 = appJsonRootDir + "GroupByRDD-1.0";
        ExecutorMemoryComparator comparator = new ExecutorMemoryComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics);
        comparator.computeRelativeDifference();



        /*
        String applicationName = "Join";
        int[] selectedStageIds = new int[]{2};
        String appJsonDir0 = appJsonRootDir + "RDDJoin-0.5";
        String appJsonDir1 = appJsonRootDir + "RDDJoin-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics);
        comparator.computeRelativeDifference();
        */


        /*
        String applicationName = "SVM";
        int[] selectedStageIds = new int[]{4, 6, 8, 10, 12, 14, 16, 18, 20, 22};
        String appJsonDir0 = appJsonRootDir + "SVM-0.5";
        String appJsonDir1 = appJsonRootDir + "SVM-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics);
        comparator.computeRelativeDifference();
        */

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
