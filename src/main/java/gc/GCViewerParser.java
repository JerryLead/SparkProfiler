package gc;

import util.GCViewerNoneGUI;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by xulijie on 18-4-6.
 */
public class GCViewerParser {

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
                + File.separatorChar + "parallel-E" + ParallelExecutorID + "1.txt";
        String CMSParsedLog = outputDir + File.separatorChar + "CMS"
                + File.separatorChar + "CMS-E" + CMSExecutorID + ".txt";
        String G1ParsedLog = outputDir + File.separatorChar + "G1"
                + File.separatorChar + "G1-E" + G1ExecutorID + ".txt";
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
        parseExecutorGCLogToSummary(ParallelGCLog, ParallelParsedLog, "CSV_TS");
        parseExecutorGCLogToSummary(CMSG1Log, CMSParsedLog, "CSV_TS");
        parseExecutorGCLogToSummary(G1Log, G1ParsedLog, "CSV_TS");

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
