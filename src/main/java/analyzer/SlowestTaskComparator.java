package analyzer;

import appinfo.Application;
import appinfo.Executor;
import appinfo.Task;
import appinfo.TaskAttempt;
import profiler.SparkAppProfiler;

import statstics.ApplicationStatistics;
import util.FileTextWriter;
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

    public SlowestTaskComparator(int[] selectedStageIds, String appJsonDir0, String appJsonDir1) {
        this.selectedStageIds = selectedStageIds;
        this.appJsonDir0 = appJsonDir0;
        this.appJsonDir1 = appJsonDir1;
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

                System.out.println("\n\n=============[" + mode + "-" + dataMode + "]============");
                if (mode.equalsIgnoreCase("0.5"))
                    compareSlowestTask(appList, appJsonDir0);
                else
                    compareSlowestTask(appList, appJsonDir1);
            }
        }
    }

    // /Users/xulijie/Documents/GCResearch/NewExperiments/medianProfiles/GroupByRDD-0.5
    private void compareSlowestTask(List<Application> appList, String appJsonDir) {
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
        int slowestTaskId = slowestTask.getFirstTaskAttempt().getTaskAttemptId();

        System.out.println("------------[" + getGCName(slowestApp) + "]------------");
        System.out.println(slowestTask.getFirstTaskAttempt());

        for (int i = appList.size() - 2; i >= 0; i--) {
            Application app = appList.get(i);
            Task task = app.getStage(slowestStageId).getFirstStage().getTaskMap()
                    .get(slowestTask.getTaskId());
            System.out.println("------------[" + getGCName(app) + "]------------");
            System.out.println(task.getFirstTaskAttempt());
        }

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
            collector = "P";
        else if (app.getName().contains("CMS"))
            collector = "C";
        else if (app.getName().contains("G1"))
            collector = "G1";

        return collector;
    }


    public static void main(String args[]) {

        String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/NewExperiments/medianProfiles/";

        String app = "GroupBy";
        int[] selectedStageIds = new int[]{1};

        String appJsonDir0 = appJsonRootDir + "GroupByRDD-0.5";
        String appJsonDir1 = appJsonRootDir + "GroupByRDD-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(selectedStageIds, appJsonDir0, appJsonDir1);
        comparator.computeRelativeDifference();




        /*
        String app = "Join";
        int[] selectedStageIds = new int[]{2};
        String appJsonDir = appJsonRootDir + "RDDJoin-0.5";
        profile(app, appJsonDir, selectedStageIds);
        appJsonDir = appJsonRootDir + "RDDJoin-1.0";
        profile(app, appJsonDir, selectedStageIds);
        */

        /*
        app = "SVM";
        selectedStageIds = new int[]{4, 6, 8, 10, 12, 14, 16, 18, 20, 22};
        appJsonDir = appJsonRootDir + "SVM-0.5";
        profile(app, appJsonDir, selectedStageIds);
        appJsonDir = appJsonRootDir + "SVM-1.0";
        profile(app, appJsonDir, selectedStageIds);

        app = "PageRank";
        selectedStageIds = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        appJsonDir = appJsonRootDir + "PageRank-0.5";
        profile(app, appJsonDir, selectedStageIds);
        appJsonDir = appJsonRootDir + "PageRank-1.0";
        profile(app, appJsonDir, selectedStageIds);
        */
    }
}
