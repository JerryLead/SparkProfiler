package gc;

import util.GCViewerNoneGUI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


public class ExecutorGCLogParser {

    /**
     * Welcome to GCViewer with cmdline"
     * java -jar gcviewer.jar [<gc-log-file|url>] -> opens gui and loads given file
     * java -jar gcviewer.jar [<gc-log-file|url>];[<gc-log-file|url>];[...] -> opens gui and loads given files as series of rotated logfiles
     * java -jar gcviewer.jar [<gc-log-file>] [<export.csv>] -> cmdline: writes report to <export.csv>");
     * java -jar gcviewer.jar [<gc-log-file|url>];[<gc-log-file|url>];[...] [<export.csv>] -> cmdline: loads given files as series of rotated logfiles and writes report to <export.csv>
     * java -jar gcviewer.jar [<gc-log-file>] [<export.csv>] [<chart.png>] -> cmdline: writes report to <export.csv> and renders gc chart to <chart.png>
     * java -jar gcviewer.jar [<gc-log-file|url>];[<gc-log-file|url>];[...] [<export.csv>] [<chart.png>] -> cmdline: loads given files as series of rotated logfiles and writes report to <export.csv> and renders gc chart to <chart.png>
     * java -jar gcviewer.jar [<gc-log-file|url>] [<export.csv>] [<chart.png>] [-t <SUMMARY, CSV, CSV_TS, PLAIN, SIMPLE>]
     * java -jar gcviewer.jar [<gc-log-file|url>];[<gc-log-file|url>];[...] [<export.csv>] [<chart.png>] [-t <SUMMARY, CSV, CSV_TS, PLAIN, SIMPLE>]
     */

    public static GCViewerNoneGUI gcViewerNoneGUI = new GCViewerNoneGUI();

    public static void parseExecutorGCLog(String gcLogFile, String exportCVSFile, String chartPNGFile) {

        try {
            System.out.println("[GCLogParsing] " + gcLogFile);
            gcViewerNoneGUI.doMain(new String[]{gcLogFile, exportCVSFile, chartPNGFile});
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // java -jar gcviewer-1.3x.jar gc.log summary.csv [chart.png] [-t PLAIN|CSV|CSV_TS|SIMPLE|SUMMARY]
    public static void parseExecutorGCLogToSummary(String gcLogFile, String summaryCSV, String type) {

        try {
            System.out.println("[GCLogParsing] " + gcLogFile);
            gcViewerNoneGUI.doMain(new String[]{gcLogFile, summaryCSV, "-t", type});
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void parseExecutorLogByGCViewer(String baseDir, String appName,
                                           String medianParallelApp, String medianCMSApp, String medianG1App,
                                           int ParallelExecutorID, int CMSExecutorID, int G1ExecutorID) {

        String executorDir = baseDir + appName + File.separatorChar;
        String ParallelGCLog = executorDir + medianParallelApp + File.separatorChar + "executors"
                + File.separatorChar + ParallelExecutorID + File.separatorChar + "stdout";
        String CMSG1Log = executorDir + medianCMSApp + File.separatorChar + "executors"
                + File.separatorChar + CMSExecutorID + File.separatorChar + "stdout";
        String G1Log = executorDir + medianG1App + File.separatorChar + "executors"
                + File.separatorChar + G1ExecutorID + File.separatorChar + "stdout";

        String outputDir = baseDir + appName + File.separatorChar + "SlowestTask";
        String ParallelParsedLog = outputDir + File.separatorChar + "Parallel"
                + File.separatorChar + "parallel-E" + ParallelExecutorID + ".csv";
        String CMSParsedLog = outputDir + File.separatorChar + "CMS"
                + File.separatorChar + "CMS-E" + CMSExecutorID + ".csv";
        String G1ParsedLog = outputDir + File.separatorChar + "G1"
                + File.separatorChar + "G1-E" + G1ExecutorID + ".csv";
        File file = new File(ParallelParsedLog);
        file = file.getParentFile();
        if (!file.exists())
            file.mkdirs();
        file = new File(CMSParsedLog);
        file = file.getParentFile();
        if (!file.exists())
            file.mkdirs();
        file = new File(G1ParsedLog);
        file = file.getParentFile();
        if (!file.exists())
            file.mkdirs();

        // java -jar gcviewer-1.3x.jar gc.log summary.csv [chart.png] [-t PLAIN|CSV|CSV_TS|SIMPLE|SUMMARY]
        parseExecutorGCLogToSummary(ParallelGCLog, ParallelParsedLog, "PLAIN");
        parseExecutorGCLogToSummary(CMSG1Log, CMSParsedLog, "PLAIN");
        parseExecutorGCLogToSummary(G1Log, G1ParsedLog, "PLAIN");

        //parseExecutorGCLogToSummary(ParallelGCLog, ParallelParsedLog, "CSV");
        //parseExecutorGCLogToSummary(CMSG1Log, CMSParsedLog, "CSV");
        //parseExecutorGCLogToSummary(G1Log, G1ParsedLog, "CSV");


        ParallelGCViewerLogParser parser = new ParallelGCViewerLogParser();
        parser.parse(ParallelParsedLog);
        String outputFile = outputDir + File.separatorChar + "Parallel"
                + File.separatorChar + "Parallel-E" + ParallelExecutorID + "-parsed.txt";
        parser.outputUsage(outputFile);


        CMSGCViewerLogParser cmsParser = new CMSGCViewerLogParser();
        cmsParser.parse(CMSParsedLog);
        outputFile = outputDir + File.separatorChar + "CMS"
                + File.separatorChar + "CMS-E" + CMSExecutorID + "-parsed.txt";
        cmsParser.outputUsage(outputFile);

        G1GCViewerLogParser g1Parser = new G1GCViewerLogParser();
        g1Parser.parse(G1ParsedLog);
        outputFile = outputDir + File.separatorChar + "G1"
                + File.separatorChar + "G1-E" + G1ExecutorID + "-parsed.txt";
        g1Parser.outputUsage(outputFile);
    }

    private static void parseAllExecutorLogByGCViewer(String baseDir, String appName, String medianApp) {
        baseDir = baseDir + appName + File.separatorChar;
        String executorsDir = baseDir + medianApp + File.separatorChar + "executors";

        for (File executorIdDir : new File(executorsDir).listFiles()) {
            if (executorIdDir.isDirectory()) {
                String executorId = executorIdDir.getName();
                String gcLog = executorIdDir.getAbsolutePath() + File.separatorChar + "stdout";
                String parsedLog = executorIdDir.getAbsolutePath() + File.separatorChar +
                        "gcPause-E" + executorId + ".txt";
                // java -jar gcviewer-1.3x.jar gc.log summary.csv [chart.png] [-t PLAIN|CSV|CSV_TS|SIMPLE|SUMMARY]
                parseExecutorGCLogToSummary(gcLog, parsedLog, "PLAIN");
            }
        }
    }

    public  static void parseExecutorLogByGCViewer(String profilesDir, String slowestExecutorsDir) {

        for (File executorsDir : new File(slowestExecutorsDir).listFiles()) {
            String executorDirName = executorsDir.getName();

            if (executorDirName.startsWith("Parallel")) { // Parallel-n3-D53m-R32m
                for (File selectedExecutor : executorsDir.listFiles()) { // E8-T22-D18m-G41s-S3.8G
                    if (selectedExecutor.getName().startsWith("E")) {
                        // copyFiles (Parallel-n1, E16, E8-T22-D18m-G41s-S3.8G)
                        if (!profilesDir.isEmpty())
                            copyFiles(profilesDir, executorDirName.substring(0, executorDirName.indexOf("-n") + 3),
                                selectedExecutor.getName().substring(1, selectedExecutor.getName().indexOf("-")),
                                selectedExecutor);
                        String ParallelGCLog = selectedExecutor.getAbsolutePath() + File.separatorChar + "stdout";
                        String ParallelParsedLog = selectedExecutor.getAbsolutePath() + File.separatorChar
                                + selectedExecutor.getName() + ".csv";
                        parseExecutorGCLogToSummary(ParallelGCLog, ParallelParsedLog, "PLAIN");

                        ParallelGCViewerLogParser parser = new ParallelGCViewerLogParser();
                        parser.parse(ParallelParsedLog);
                        String outputFile = selectedExecutor.getAbsolutePath() + File.separatorChar
                                + selectedExecutor.getName() + "-parsed.txt";
                        parser.outputUsage(outputFile);
                    }
                }
            }

            else if (executorDirName.startsWith("CMS")) {
                for (File selectedExecutor : executorsDir.listFiles()) {
                    if (selectedExecutor.getName().startsWith("E")) {
                        if (!profilesDir.isEmpty())
                            copyFiles(profilesDir, executorDirName.substring(0, executorDirName.indexOf("-n") + 3),
                                    selectedExecutor.getName().substring(1, selectedExecutor.getName().indexOf("-")),
                                    selectedExecutor);

                        String CMSGCLog = selectedExecutor.getAbsolutePath() + File.separatorChar + "stdout";
                        String CMSParsedLog = selectedExecutor.getAbsolutePath() + File.separatorChar
                                + selectedExecutor.getName() + ".csv";
                        parseExecutorGCLogToSummary(CMSGCLog, CMSParsedLog, "PLAIN");

                        CMSGCViewerLogParser parser = new CMSGCViewerLogParser();
                        parser.parse(CMSParsedLog);
                        String outputFile = selectedExecutor.getAbsolutePath() + File.separatorChar
                                + selectedExecutor.getName() + "-parsed.txt";
                        parser.outputUsage(outputFile);
                    }
                }
            }

            else if (executorDirName.startsWith("G1")) {
                for (File selectedExecutor : executorsDir.listFiles()) {
                    if (selectedExecutor.getName().startsWith("E")) {
                        if (!profilesDir.isEmpty())
                            copyFiles(profilesDir, executorDirName.substring(0, executorDirName.indexOf("-n") + 3),
                                    selectedExecutor.getName().substring(1, selectedExecutor.getName().indexOf("-")),
                                    selectedExecutor);

                        String G1GCLog = selectedExecutor.getAbsolutePath() + File.separatorChar + "stdout";
                        String G1ParsedLog = selectedExecutor.getAbsolutePath() + File.separatorChar
                                + selectedExecutor.getName() + ".csv";
                        parseExecutorGCLogToSummary(G1GCLog, G1ParsedLog, "PLAIN");

                        G1GCViewerLogParser parser = new G1GCViewerLogParser();
                        parser.parse(G1ParsedLog);
                        String outputFile = selectedExecutor.getAbsolutePath() + File.separatorChar
                                + selectedExecutor.getName() + "-parsed.txt";
                        parser.outputUsage(outputFile);
                    }
                }
            }
        }


    }

    private static void copyFiles(String profilesDir, String collector, String executorId, File selectedExecutor) {
        List<File> files = new ArrayList();

        for (File appDir : new File(profilesDir).listFiles()) {
            String appName = appDir.getName().toLowerCase();
            String collectorName = collector.substring(0, collector.indexOf("-")).toLowerCase();
            String collectorId = collector.substring(collector.indexOf("-") + 1).toLowerCase();

            if (appName.contains(collectorName) && appName.contains(collectorId)) {
                String executorDir = appDir.getAbsolutePath() + File.separatorChar + "executors"
                        + File.separatorChar + executorId;
                for (File f : new File(executorDir).listFiles())
                    files.add(f);
            }
        }

        for(File file : files) {

            try {
                Files.copy(file.toPath(),
                        new File(selectedExecutor, file.getName()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public static void main(String[] args) {
        String baseDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/";

        //String appName = "AggregateByKey-1.0";
        //String appName = "Join-1.0-200G-2";
        //String appName = "SQLGroupBy-1.0-200G";
        String appName = "SQLJoin-1.0-200G";
        String slowestExecutorsDir = baseDir + appName + File.separatorChar + "SlowestExecutors";
        //String slowestExecutorsDir = baseDir + appName + File.separatorChar + "SelectedExecutors";

        boolean copyOriginalExecutorsGClogs = true;

        if (copyOriginalExecutorsGClogs == false)
            parseExecutorLogByGCViewer("", slowestExecutorsDir);
        else
            parseExecutorLogByGCViewer(baseDir + appName, slowestExecutorsDir);
    }



    public static void main2(String[] args) {

        String baseDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/";

        String appName = "AggregateByKey-1.0";
        //String appName = "Join-1.0-200G";
        //String appName = "GroupByRDD-0.5";
        //String medianParallelApp = "GroupByRDD-Parallel-1-6656m-0.5-n1_app-20171120185427-0000";
        //String medianCMSApp = "GroupByRDD-CMS-1-6656m-0.5-n5_app-20171120195033-0019";
        //String medianG1App = "GroupByRDD-G1-1-6656m-0.5-n1_app-20171120201509-0030";

        String medianParallelApp = "AggregateByKey-Parallel-1-6656m-1.0-n2_app-20181010012212-0001";
        String medianCMSApp = "AggregateByKey-CMS-1-6656m-1.0-n4_app-20181010085707-0010";
        // String medianG1App = "GroupByRDD-G1-1-6656m-0.5-n4_app-20171120202507-0033";
        // String medianG1App = "GroupByRDD-G1-1-6656m-0.5-n1_app-20171120201509-0030";
        // String medianG1App = "GroupByRDD-G1-1-6656m-0.5-n2_app-20171120201823-0031";
        // String medianG1App = "GroupByRDD-G1-1-6656m-0.5-n3_app-20171120202154-0032";
        String medianG1App = "AggregateByKey-G1-1-6656m-1.0-n4_app-20181010093223-0011";

        int ParallelExectuorID = 26;
        int CMSExecutorID = 21;
        int G1ExecutorID = 10; //28; //18; //16;

        parseExecutorLogByGCViewer(baseDir, appName, medianParallelApp, medianCMSApp, medianG1App,
                ParallelExectuorID, CMSExecutorID, G1ExecutorID);


        /*
        String appName = "RDDJoin-1.0";
        String medianParallelApp = "RDDJoin-Parallel-1-6656m-1.0-n1_app-20171121172308-0000";
        String medianCMSApp = "RDDJoin-CMS-1-6656m-1.0-n4_app-20171122070634-0018";
        String medianG1App = "RDDJoin-G1-1-6656m-1.0-n3_app-20171122095318-0032";

        int ParallelExectuorID = 14; // 12;
        int CMSExecutorID = 23; // 25;
        int G1ExecutorID = 28; // 13; //no spill 13

        parseExecutorLogByGCViewer(baseDir, appName, medianParallelApp, medianCMSApp, medianG1App,
                ParallelExectuorID, CMSExecutorID, G1ExecutorID);


        parseAllExecutorLogByGCViewer(baseDir, appName, medianParallelApp);
        parseAllExecutorLogByGCViewer(baseDir, appName, medianCMSApp);
        parseAllExecutorLogByGCViewer(baseDir, appName, medianG1App);
        */

        /*
        String appName = "SVM-1.0";
        String medianParallelApp = "SVM-Parallel-1-6656m-1.0-n1_app-20171117141140-0000";
        String medianCMSApp = "SVM-CMS-1-6656m-1.0-n1_app-20171117175448-0015";
        String medianG1App = "SVM-G1-1-6656m-1.0-n2_app-20171117214908-0031";
        int ParallelExectuorID = 31;
        int CMSExecutorID = 3;
        int G1ExecutorID = 18; //12; // no spill 13

        parseExecutorLogByGCViewer(baseDir, appName, medianParallelApp, medianCMSApp, medianG1App,
                ParallelExectuorID, CMSExecutorID, G1ExecutorID);
        */

        /*
        String appName = "PageRank-0.5";
        String medianParallelApp = "PageRank-Parallel-1-6656m-0.5-n4_app-20171124224112-0003";
        String medianCMSApp = "PageRank-CMS-1-6656m-0.5-n2_app-20171125032818-0016";
        String medianG1App = "PageRank-G1-1-6656m-0.5-n3_app-20171125095838-0032";
        int ParallelExectuorID = 1; //28;
        int CMSExecutorID = 14;
        int G1ExecutorID = 28;//27; //12; // no spill 13

        parseExecutorLogByGCViewer(baseDir, appName, medianParallelApp, medianCMSApp, medianG1App,
                ParallelExectuorID, CMSExecutorID, G1ExecutorID);

        */

    }


}

/**
 * Max heap after full GC:ParallelParsedLog
 * Max used heap after full gc. Indicates max live object size and can help to determine heap size.
 * <p>
 * Acc Pauses:
 * Sum of all pauses due to GC
 * <p>
 * Throughput:
 * Time percentage the application was NOT busy with GC
 * <p>
 * Full GC Performance:
 * Performance of full collections. Note that all collections that include a collection of the tenured generation or are marked with "Full GC" are considered Full GC.
 * <p>
 * GC Performance:
 * Performance of minor collections. These are collections that are not full according to the definition above.
 * <p>
 * Freed Memory:
 * Total amount of memory that has been freed
 * <p>
 * Freed by full GC:
 * Amount of memory that has been freed by full collections
 * <p>
 * Freed by GC:
 * Amount of memory that has been freed by minor collections
 * <p>
 * total promotion
 * Total promotion shows the total amount of memory that is promoted from young to tenured with all young collections in a file (only available with PrintGCDetails)
 * <p>
 * Acc Pauses:
 * Sum of all pauses due to any kind of GC
 * <p>
 * Number of Pauses:
 * Count of all pauses due to any kind of GC
 * <p>
 * Acc full GC:
 * Sum of all pauses due to full collections
 * <p>
 * Number of full GC pauses:
 * Count of all pauses due to full collections
 */