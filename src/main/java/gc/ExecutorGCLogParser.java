package gc;

import generalGC.ParallelGCLogParser;
import util.GCViewerNoneGUI;

import java.io.File;
import java.lang.reflect.InvocationTargetException;


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
                                           int ParallelExectuorID, int CMSExecutorID, int G1ExecutorID) {

        String executorDir = baseDir + appName + File.separatorChar;
        String ParallelGCLog = executorDir + medianParallelApp + File.separatorChar + "executors"
                + File.separatorChar + ParallelExectuorID + File.separatorChar + "stdout";
        String CMSG1Log = executorDir + medianCMSApp + File.separatorChar + "executors"
                + File.separatorChar + CMSExecutorID + File.separatorChar + "stdout";
        String G1Log = executorDir + medianG1App + File.separatorChar + "executors"
                + File.separatorChar + G1ExecutorID + File.separatorChar + "stdout";

        String outputDir = baseDir + appName + File.separatorChar + "SlowestTask";
        String ParallelParsedLog = outputDir + File.separatorChar + "Parallel"
                + File.separatorChar + "parallel-E" + ParallelExectuorID + ".csv";
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


        parseExecutorGCLogToSummary(ParallelGCLog, ParallelParsedLog, "PLAIN");
        parseExecutorGCLogToSummary(CMSG1Log, CMSParsedLog, "PLAIN");
        parseExecutorGCLogToSummary(G1Log, G1ParsedLog, "PLAIN");

        ParallelGCViewLogParser parser = new ParallelGCViewLogParser();
        parser.parse(ParallelParsedLog);
        String outputFile = outputDir + File.separatorChar + "Parallel"
                + File.separatorChar + "parallel-E" + ParallelExectuorID + "-parsed.txt";
        parser.outputUsage(outputFile);
    }

    public static void main(String[] args) {

        String baseDir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/medianProfiles/";

        String appName = "GroupByRDD-0.5";
        String medianParallelApp = "GroupByRDD-Parallel-1-6656m-0.5-n1_app-20171120185427-0000";
        String medianCMSApp = "GroupByRDD-CMS-1-6656m-0.5-n5_app-20171120195033-0019";
        String medianG1App = "GroupByRDD-G1-1-6656m-0.5-n1_app-20171120201509-0030";
        int ParallelExectuorID = 30;
        int CMSExecutorID = 17;
        int G1ExecutorID = 16;

        parseExecutorLogByGCViewer(baseDir, appName, medianParallelApp, medianCMSApp, medianG1App,
                ParallelExectuorID, CMSExecutorID, G1ExecutorID);

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