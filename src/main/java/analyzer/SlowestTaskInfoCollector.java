package analyzer;

import appinfo.TaskAttempt;
import com.google.gson.*;
import util.JsonFileReader;

import java.io.File;
import java.util.List;

/**
 * Created by xulijie on 17-9-30.
 */
public class SlowestTaskInfoCollector {

    private String appName;
    private String dataMode;
    private String statisticsDir;
    private String[] metrics;

    private TaskAttempt Parallel_E1_task;
    private TaskAttempt Parallel_E2_task;
    private TaskAttempt Parallel_E4_task;
    private TaskAttempt CMS_E1_task;
    private TaskAttempt CMS_E2_task;
    private TaskAttempt CMS_E4_task;
    private TaskAttempt G1_E1_task;
    private TaskAttempt G1_E2_task;
    private TaskAttempt G1_E4_task;

    public SlowestTaskInfoCollector(String appName, String dataMode, String statisticsDir, String[] metrics) {
        this.appName = appName;
        this.dataMode = dataMode;
        this.statisticsDir = statisticsDir;
        this.metrics = metrics;
        init();
    }

    public void init() {
        String appNameFile = appName + "-Parallel-1-7G-" + dataMode + "-slowestTask.txt";
        String stat = statisticsDir + File.separatorChar + appNameFile;
        Parallel_E1_task = getTaskAttempt(stat);

        appNameFile = appName + "-Parallel-2-14G-" + dataMode + "-slowestTask.txt";
        stat = statisticsDir + File.separatorChar + appNameFile;
        Parallel_E2_task = getTaskAttempt(stat);

        appNameFile = appName + "-Parallel-4-28G-" + dataMode + "-slowestTask.txt";
        stat = statisticsDir + File.separatorChar + appNameFile;
        Parallel_E4_task = getTaskAttempt(stat);

        appNameFile = appName + "-CMS-1-7G-" + dataMode + "-slowestTask.txt";
        stat = statisticsDir + File.separatorChar + appNameFile;
        CMS_E1_task = getTaskAttempt(stat);

        appNameFile = appName + "-CMS-2-14G-" + dataMode + "-slowestTask.txt";
        stat = statisticsDir + File.separatorChar + appNameFile;
        CMS_E2_task = getTaskAttempt(stat);

        appNameFile = appName + "-CMS-4-28G-" + dataMode + "-slowestTask.txt";
        stat = statisticsDir + File.separatorChar + appNameFile;
        CMS_E4_task = getTaskAttempt(stat);

        appNameFile = appName + "-G1-1-7G-" + dataMode + "-slowestTask.txt";
        stat = statisticsDir + File.separatorChar + appNameFile;
        G1_E1_task = getTaskAttempt(stat);

        appNameFile = appName + "-G1-2-14G-" + dataMode + "-slowestTask.txt";
        stat = statisticsDir + File.separatorChar + appNameFile;
        G1_E2_task = getTaskAttempt(stat);

        appNameFile = appName + "-G1-4-28G-" + dataMode + "-slowestTask.txt";
        stat = statisticsDir + File.separatorChar + appNameFile;
        G1_E4_task = getTaskAttempt(stat);

    }

    public TaskAttempt getTaskAttempt(String slowestTaskFile) {

        List<String> lines = JsonFileReader.readFileLines(slowestTaskFile);

        if (!lines.isEmpty()) {
            String line1 = lines.get(0);
            String appId = line1.substring(line1.indexOf("app"), line1.lastIndexOf("stage") - 1);
            String appName = line1.substring(line1.indexOf("in") + 3, line1.indexOf("_"));
            int stageId = 0;

            String taskInfo = lines.get(1);

            try {
                JsonParser parser = new JsonParser();
                JsonElement el = parser.parse(taskInfo);
                return new TaskAttempt(appId, appName, stageId, el.getAsJsonObject());
            } catch (JsonIOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void displayTaskInfo() {
        System.out.println("================ [Parallel_E1_task] =================");
        System.out.println(Parallel_E1_task);
        System.out.println("================ [Parallel_E2_task] =================");
        System.out.println(Parallel_E2_task);
        System.out.println("================ [Parallel_E4_task] =================");
        System.out.println(Parallel_E4_task);
        System.out.println("================ [CMS_E1_task] =================");
        System.out.println(CMS_E1_task);
        System.out.println("================ [CMS_E2_task] =================");
        System.out.println(CMS_E2_task);
        System.out.println("================ [CMS_E4_task] =================");
        System.out.println(CMS_E4_task);
        System.out.println("================ [G1_E1_task] =================");
        System.out.println(G1_E1_task);
        System.out.println("================ [G1_E2_task] =================");
        System.out.println(G1_E2_task);
        System.out.println("================ [G1_E4_task] =================");
        System.out.println(G1_E4_task);
    }

    private void compare(String[] app1s, String[] app2s) {
        for (int i = 0; i < app1s.length; i++) {
            String app1 = app1s[i];
            String app2 = app2s[i];

            display(app1);
            display(app2);
        }
    }

    private void display(String app) {
        if (app.equalsIgnoreCase("E1-P"))
            displayMetrics(app, Parallel_E1_task);
        else if (app.equalsIgnoreCase("E2-P"))
            displayMetrics(app, Parallel_E2_task);
        else if (app.equalsIgnoreCase("E4-P"))
            displayMetrics(app, Parallel_E4_task);
        else if (app.equalsIgnoreCase("E1-C"))
            displayMetrics(app, CMS_E1_task);
        else if (app.equalsIgnoreCase("E2-C"))
            displayMetrics(app, CMS_E2_task);
        else if (app.equalsIgnoreCase("E4-C"))
            displayMetrics(app, CMS_E4_task);
        else if (app.equalsIgnoreCase("E1-G"))
            displayMetrics(app, G1_E1_task);
        else if (app.equalsIgnoreCase("E2-G"))
            displayMetrics(app, G1_E2_task);
        else if (app.equalsIgnoreCase("E4-G"))
            displayMetrics(app, G1_E4_task);
    }

    private void displayMetrics(String app, TaskAttempt task) {
        StringBuilder sb = new StringBuilder();
        sb.append(appName + " & ");
        for (String metric: metrics) {
            if (metric.equalsIgnoreCase("Mode"))
                sb.append(app + " & ");
            else if (metric.equalsIgnoreCase("ID"))
                sb.append(task.getIndex() + " & ");
            else if (metric.equalsIgnoreCase("Duration"))
                sb.append(String.format("%.1f", (double) task.getDuration() / 1000 / 60) + " min & ");
            else if (metric.equalsIgnoreCase("CPU Time"))
                sb.append(String.format("%.0f", (double) task.getExecutorCpuTime() / 1000 / 1000 / 1000) + " s & ");
            else if (metric.equalsIgnoreCase("GC Time"))
                sb.append(String.format("%.0f", (double) task.getJvmGcTime() / 1000) + " s & ");
            else if (metric.equalsIgnoreCase("Shuffled Size/Records"))
                sb.append(task.getShuffleReadMetrics_recordsRead() + " / " + task.getShuffleReadMetrics_bytesRead() / 1024 / 1024 + " MB & ");
            else if (metric.equalsIgnoreCase("Memory Spill"))
                sb.append(String.format("%.1f", (double)  task.getMemoryBytesSpilled() / 1024 / 1024 / 1024) + " GB & ");
            else if (metric.equalsIgnoreCase("Output Size/Records"))
                sb.append(task.getOutputMetrics_recordsWritten() + " / " + task.getOutputMetrics_bytesWritten() / 1024 / 1024 + " MB");
        }
        System.out.println(sb.toString() + " \\\\ \\hline");
    }

    public static void main(String args[]) {

        String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/NewExperiments/medianProfiles/";
        String[] metrics = {
          "Mode",
          "ID",
          "Duration",

          "CPU Time",
          "GC Time",
          "Shuffled Size/Records",
          "Memory Spill",
          "Output Size/Records"
        };


        String app = "GroupByRDD";
        String[] app1s = {"E1-C"};
        String[] app2s = {"E1-P"};
        String appJsonDir = appJsonRootDir + "GroupByRDD-0.5" + File.separatorChar + "Abnormal-json";
        SlowestTaskInfoCollector collector = new SlowestTaskInfoCollector(app, "0.5", appJsonDir, metrics);
        collector.compare(app1s, app2s);

        app1s = new String[]{"E2-G"};
        app2s = new String[]{"E2-P"};
        appJsonDir = appJsonRootDir + "GroupByRDD-1.0" + File.separatorChar + "Abnormal-json";
        collector = new SlowestTaskInfoCollector(app, "1.0", appJsonDir, metrics);
        collector.compare(app1s, app2s);



        app = "RDDJoin";
        app1s = new String[]{"E1-C"};
        app2s = new String[]{"E1-P"};
        appJsonDir = appJsonRootDir + "RDDJoin-1.0" + File.separatorChar + "Abnormal-json";
        collector = new SlowestTaskInfoCollector(app, "1.0", appJsonDir, metrics);
        collector.compare(app1s, app2s);

        app1s = new String[]{"E2-C"};
        app2s = new String[]{"E2-P"};
        appJsonDir = appJsonRootDir + "RDDJoin-1.0" + File.separatorChar + "Abnormal-json";
        collector = new SlowestTaskInfoCollector(app, "1.0", appJsonDir, metrics);
        collector.compare(app1s, app2s);

        app1s = new String[]{"E4-C"};
        app2s = new String[]{"E4-P"};
        appJsonDir = appJsonRootDir + "RDDJoin-1.0" + File.separatorChar + "Abnormal-json";
        collector = new SlowestTaskInfoCollector(app, "1.0", appJsonDir, metrics);
        collector.compare(app1s, app2s);


        /*

        app = "PageRank";
        app1s = new String[]{"E1-P"};
        app2s = new String[]{"E1-G"};
        appJsonDir = appJsonRootDir + "PageRank-0.5" + File.separatorChar + "Abnormal-json";
        collector = new SlowestTaskInfoCollector(app, "0.5", appJsonDir, metrics);
        collector.compare(app1s, app2s);

        app1s = new String[]{"E2-C"};
        app2s = new String[]{"E2-G"};
        appJsonDir = appJsonRootDir + "PageRank-0.5" + File.separatorChar + "Abnormal-json";
        collector = new SlowestTaskInfoCollector(app, "0.5", appJsonDir, metrics);
        collector.compare(app1s, app2s);

        app1s = new String[]{"E4-C"};
        app2s = new String[]{"E4-G"};
        appJsonDir = appJsonRootDir + "PageRank-0.5" + File.separatorChar + "Abnormal-json";
        collector = new SlowestTaskInfoCollector(app, "0.5", appJsonDir, metrics);
        collector.compare(app1s, app2s);
        */

    }


}
