package analyzer;

import appinfo.Application;
import profiler.SparkAppProfiler;
import statstics.ComputedAppStatistics;
import util.JsonFileReader;
import util.RelativeDifference;
import util.Statistics;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xulijie on 17-9-21.
 */
public class StatisticsAnalyzer {

    private ComputedAppStatistics Parallel_E1_stat;
    private ComputedAppStatistics Parallel_E2_stat;
    private ComputedAppStatistics Parallel_E4_stat;
    private ComputedAppStatistics CMS_E1_stat;
    private ComputedAppStatistics CMS_E2_stat;
    private ComputedAppStatistics CMS_E4_stat;
    private ComputedAppStatistics G1_E1_stat;
    private ComputedAppStatistics G1_E2_stat;
    private ComputedAppStatistics G1_E4_stat;

    private String app;
    private String dataMode;
    private String statisticsDir;
    private String selectedStageIds;

    private StatisticsAnalyzer(String app, String dataMode, String statisticsDir, String selectedStageIds) {
        this.app = app;
        this.dataMode = dataMode;
        this.statisticsDir = statisticsDir;
        this.selectedStageIds = selectedStageIds;
    }

    public void init() {
        String appName = app + "-Parallel-1-7G-" + dataMode + "-stat.txt";
        String stat = statisticsDir + File.separatorChar + appName;
        Parallel_E1_stat = new ComputedAppStatistics(appName, "Parallel", dataMode, JsonFileReader.readFileLines(stat));

        appName = app + "-Parallel-2-14G-" + dataMode + "-stat.txt";
        stat = statisticsDir + File.separatorChar + appName;
        Parallel_E2_stat = new ComputedAppStatistics(appName, "Parallel", dataMode, JsonFileReader.readFileLines(stat));

        appName = app + "-Parallel-4-28G-" + dataMode + "-stat.txt";
        stat = statisticsDir + File.separatorChar + appName;
        Parallel_E4_stat = new ComputedAppStatistics(appName, "Parallel", dataMode, JsonFileReader.readFileLines(stat));

        appName = app + "-CMS-1-7G-" + dataMode + "-stat.txt";
        stat = statisticsDir + File.separatorChar + appName;
        CMS_E1_stat = new ComputedAppStatistics(appName, "CMS", dataMode, JsonFileReader.readFileLines(stat));

        appName = app + "-CMS-2-14G-" + dataMode + "-stat.txt";
        stat = statisticsDir + File.separatorChar + appName;
        CMS_E2_stat = new ComputedAppStatistics(appName, "CMS", dataMode, JsonFileReader.readFileLines(stat));

        appName = app + "-CMS-4-28G-" + dataMode + "-stat.txt";
        stat = statisticsDir + File.separatorChar + appName;
        CMS_E4_stat = new ComputedAppStatistics(appName, "CMS", dataMode, JsonFileReader.readFileLines(stat));

        appName = app + "-G1-1-7G-" + dataMode + "-stat.txt";
        stat = statisticsDir + File.separatorChar + appName;
        G1_E1_stat = new ComputedAppStatistics(appName, "G1", dataMode, JsonFileReader.readFileLines(stat));

        appName = app + "-G1-2-14G-" + dataMode + "-stat.txt";
        stat = statisticsDir + File.separatorChar + appName;
        G1_E2_stat = new ComputedAppStatistics(appName, "G1", dataMode, JsonFileReader.readFileLines(stat));

        appName = app + "-G1-4-28G-" + dataMode + "-stat.txt";
        stat = statisticsDir + File.separatorChar + appName;
        G1_E4_stat = new ComputedAppStatistics(appName, "G1", dataMode, JsonFileReader.readFileLines(stat));

    }

    private void compareMetricDifference() {
        // GroupBy-0.5: In E1, Parallel >> CMS ~ G1
        System.out.println("\n[Comparison][" + app + "-" + dataMode + "]");
        compareGCWithSameMemory();
    }

    private void compareGCWithSameMemory() {
        String metricName = "app.duration";
        String statName = "max";

        // compare in E1 mode
        List<ComputedAppStatistics> appList = new ArrayList<ComputedAppStatistics>();
        appList.add(Parallel_E1_stat);
        appList.add(CMS_E1_stat);
        appList.add(G1_E1_stat);
        System.out.println("[" + metricName + "." + statName + "][E1]");
        computeRelativeDifference(appList, metricName, statName);

        // compare in E2 mode
        appList = new ArrayList<ComputedAppStatistics>();
        appList.add(Parallel_E2_stat);
        appList.add(CMS_E2_stat);
        appList.add(G1_E2_stat);
        System.out.println("\n[" + metricName + "." + statName + "][E2]");
        computeRelativeDifference(appList, metricName, statName);

        // compare in E4 mode
        appList = new ArrayList<ComputedAppStatistics>();
        appList.add(Parallel_E4_stat);
        appList.add(CMS_E4_stat);
        appList.add(G1_E4_stat);
        System.out.println("\n[" + metricName + "." + statName + "][E4]");
        computeRelativeDifference(appList, metricName, statName);

    }

    private void computeRelativeDifference(List<ComputedAppStatistics> appList, String metricName, String statName) {
        appList.sort(new Comparator<ComputedAppStatistics>() {
            @Override
            public int compare(ComputedAppStatistics o1, ComputedAppStatistics o2) {
                double value1 = o1.getMetric(metricName, statName);
                double value2 = o2.getMetric(metricName, statName);

                return (int)(value1 - value2);
            }
        });

        double initValue = 0;


        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (ComputedAppStatistics appStatistics : appList) {
            double value = appStatistics.getMetric(metricName, statName);
            double relativeDiff = RelativeDifference.getRelativeDifference(value, initValue) * 100;
            String label = "";
            if (relativeDiff > 20)
                label = "<<";
            else if (relativeDiff > 10)
                label = "<";
            else if (relativeDiff >= 0)
                label = "~";
            else
                label = "!";

            System.out.println("\t" + appStatistics.getGcName() + " = " + value / 1000);
            initValue = value;
            if (first) {
                sb.append(appStatistics.getGcName());
                first = false;
            } else {
                sb.append(label + appStatistics.getGcName() + "(" + (int) relativeDiff + ")");
            }
        }
        System.out.println("\t" + sb.toString());

    }


    public static void main(String args[]) {

        String appJsonRootDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/";

        String app = "GroupByRDD";
        String selectedStageIds = "1";
        String appJsonDir = appJsonRootDir + "GroupByRDD-0.5-2" + File.separatorChar + "Abnormal";
        StatisticsAnalyzer analyzer = new StatisticsAnalyzer(app, "0.5", appJsonDir, selectedStageIds);
        analyzer.init();
        analyzer.compareMetricDifference();

        appJsonDir = appJsonRootDir + "GroupByRDD-1.0-2" + File.separatorChar + "Abnormal";
        analyzer = new StatisticsAnalyzer(app, "1.0", appJsonDir, selectedStageIds);
        analyzer.init();
        analyzer.compareMetricDifference();


        app = "RDDJoin";
        selectedStageIds = "2";
        appJsonDir = appJsonRootDir + "RDDJoin-0.5-2" + File.separatorChar + "Abnormal";
        analyzer = new StatisticsAnalyzer(app,  "0.5", appJsonDir, selectedStageIds);
        analyzer.init();
        analyzer.compareMetricDifference();

        appJsonDir = appJsonRootDir + "RDDJoin-1.0" + File.separatorChar + "Abnormal";
        analyzer = new StatisticsAnalyzer(app, "1.0",  appJsonDir, selectedStageIds);
        analyzer.init();
        analyzer.compareMetricDifference();

        app = "SVM";
        selectedStageIds = "4+6+8+10+12+14+16+18+20+22";
        appJsonDir = appJsonRootDir + "SVM-0.5" + File.separatorChar + "Statistics";
        analyzer = new StatisticsAnalyzer(app,  "0.5", appJsonDir, selectedStageIds);
        analyzer.init();
        analyzer.compareMetricDifference();

        appJsonDir = appJsonRootDir + "SVM-1.0" + File.separatorChar + "Statistics";
        analyzer = new StatisticsAnalyzer(app, "1.0",  appJsonDir, selectedStageIds);
        analyzer.init();
        analyzer.compareMetricDifference();

        app = "PageRank";
        selectedStageIds = "1+2+3+4+5+6+7+8+9+10";
        appJsonDir = appJsonRootDir + "PageRank-0.5" + File.separatorChar + "Statistics";
        analyzer = new StatisticsAnalyzer(app,  "0.5", appJsonDir, selectedStageIds);
        analyzer.init();
        analyzer.compareMetricDifference();

        appJsonDir = appJsonRootDir + "PageRank-1.0" + File.separatorChar + "Statistics";
        analyzer = new StatisticsAnalyzer(app,  "1.0", appJsonDir, selectedStageIds);
        analyzer.init();
        analyzer.compareMetricDifference();

    }



}
