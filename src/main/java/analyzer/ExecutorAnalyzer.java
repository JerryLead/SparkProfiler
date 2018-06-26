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
public class ExecutorAnalyzer {

    public void analyzeSlaveTopMetrics(String executorTopMetricsPath, String slave) {
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

        StringBuilder sb = new StringBuilder();
        sb.append("\n[Top Metrics][" + slave + "]\n");
        for (TopMetrics topMetrics : slaveTopMetrics)
            sb.append(topMetrics + "\n");

        System.out.println(sb.toString());
    }

    public static void main(String args[]) {

        String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/Experiments-11-17/profiles/";

        /*
        String applicationName = "GroupBy";
        int[] selectedStageIds = new int[]{1};

        String appJsonDir = appJsonRootDir + "GroupByRDD-0.5";
        appJsonDir = appJsonRootDir + "GroupByRDD-1.0";
        TaskComparator comparator = new TaskComparator(applicationName, selectedStageIds, appJsonDir, metrics, true);
        comparator.computeRelativeDifference();
        */

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


        String appName = "RDDJoin-1.0";
        // String applicationName = "rjoin-Parallel-1-6656m-1.0-n1";
        // String slave = "aliSlave4";

        // String applicationName = "rjoin-CMS-1-6656m-1.0-n4";
        // String slave = "aliSlave6";

        String applicationName = "rjoin-G1-1-6656m-1.0-n3";
        String slave = "aliSlave1";


/*

        String applicationName = "SVM";
        int[] selectedStageIds = new int[]{4, 6, 8, 10, 12, 14, 16, 18, 20, 22};
        // int[] selectedStageIds = new int[]{3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
        String appJsonDir0 = appJsonRootDir + "SVM-0.5";
        String appJsonDir1 = appJsonRootDir + "SVM-1.0";
        SlowestTaskComparator comparator = new SlowestTaskComparator(applicationName, selectedStageIds, appJsonDir0, appJsonDir1, metrics, false, slowestmode);
        comparator.computeRelativeDifference();
*/

        // String appName = "PageRank-0.5";
        // String applicationName = "PageRank-Parallel-1-6656m-0.5-n4";
        // String slave = "aliSlave7";

        // String applicationName = "PageRank-CMS-1-6656m-0.5-n2";
        // String slave = "aliSlave7";

        // String applicationName = "PageRank-G1-1-6656m-0.5-n3";
        // String slave = "aliSlave5";

        String slaveTopMetricsFile = appJsonRootDir + appName + "/topMetrics/" + slave + "/" + applicationName + ".txt";

        ExecutorAnalyzer analyzer = new ExecutorAnalyzer();
        analyzer.analyzeSlaveTopMetrics(slaveTopMetricsFile, slave);


    }
}
