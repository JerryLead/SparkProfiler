package gc;

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
            gcViewerNoneGUI.doMain(new String[]{gcLogFile, exportCVSFile, chartPNGFile});
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String executorFile = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/RDDJoin-Parallel-4-28G-0.5_app-20170623112547-0008/executors/";

        String gcLogFile1 = executorFile + File.separatorChar + "0" + File.separatorChar + "stdout";
        String gcLogFile2 = executorFile + File.separatorChar + "1" + File.separatorChar + "stdout";


        // String gcLogFile = gcLogFile0 + gcLogFile1 + gcLogFile2 + gcLogFile3 + gcLogFile4 +
        //        gcLogFile5 + gcLogFile6 + gcLogFile7;

        String exportCVSFile1 = executorFile + File.separatorChar + "0/export0.csv";
        String chartPNGFile1 = executorFile + File.separatorChar + "0/chart0.png";

        String exportCVSFile2 = executorFile + File.separatorChar + "1/export1.csv";
        String chartPNGFile2 = executorFile + File.separatorChar + "1/chart1.png";


        parseExecutorGCLog(gcLogFile1, exportCVSFile1, chartPNGFile1);

        parseExecutorGCLog(gcLogFile2, exportCVSFile2, chartPNGFile2);

    }
}

/**
 * Max heap after full GC:
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