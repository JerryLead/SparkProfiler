package gc;

import generalGC.HeapUsage;
import util.FileTextWriter;
import util.JsonFileReader;

import java.util.List;

/**
 * Created by xulijie on 18-4-5.
 */
public class CMSGCViewerLogParser {
    private HeapUsage usage = new HeapUsage();

    private double STWPauseTime = 0;
    private double youngGCTime = 0;
    private double fullGCTime = 0;

    public void parse(String logFile) {
        List<String> lines = JsonFileReader.readFileLines(logFile);

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("[2017-"))
                parseGCRecord(line);
        }
        display();

    }

    public GCStatistics parseStatistics(String logFile) {
        List<String> lines = JsonFileReader.readFileLines(logFile);

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("[2017-"))
                parseGCRecord(line);
        }

        return new GCStatistics(STWPauseTime, youngGCTime, fullGCTime, 0);
    }

    public void parseGCRecord(String line) {
        // [2017-11-20T18:54:36.579+0800][9.032]
        int endTime = line.indexOf(']', line.indexOf("][") + 2);
        // 9.032
        double offsetTime = Double.parseDouble(line.substring(line.indexOf("][") + 2, endTime));
        int gcCauseIndex = line.indexOf("] [") + 3;
        // GC (Allocation Failure)
        String gcCause = line.substring(gcCauseIndex, line.indexOf('[', gcCauseIndex) - 1);

        // Full GC
        /*
        (STW) [GC (CMS Initial Mark) [1 CMS-initial-mark: 53400K(344064K)] 79710K(498944K), 0.0032015 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
        [CMS-concurrent-mark-start]
        [CMS-concurrent-mark: 0.005/0.005 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
        [CMS-concurrent-preclean-start]
        [CMS-concurrent-preclean: 0.002/0.002 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
        [CMS-concurrent-abortable-preclean-start]
        [CMS-concurrent-abortable-preclean: 0.711/1.578 secs] [Times: user=5.00 sys=0.12, real=1.58 secs]
        (STW) [GC (CMS Final Remark) [YG occupancy: 97189 K (154880 K)]2017-11-20T19:55:50.907+0800: 4.148: [Rescan (parallel) , 0.0325130 secs]2017-11-20T19:55:50.940+0800: 4.181: [weak refs processing, 0.0000407 secs]2017-11-20T19:55:50.940+0800: 4.181: [class unloading, 0.0059425 secs]2017-11-20T19:55:50.946+0800: 4.187: [scrub symbol table, 0.0044211 secs]2017-11-20T19:55:50.950+0800: 4.191: [scrub string table, 0.0006347 secs][1 CMS-remark: 118936K(344064K)] 216125K(498944K), 0.0442861 secs] [Times: user=0.13 sys=0.00, real=0.04 secs]
        [CMS-concurrent-sweep-start]
        [CMS-concurrent-sweep: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
        [CMS-concurrent-reset-start]
        [CMS-concurrent-reset: 0.108/0.109 secs] [Times: user=0.34 sys=0.05, real=0.11 secs]
        */

        // Full GC
        if (line.contains("[CMS-concurrent") || line.contains("[GC (CMS Initial Mark)") || line.contains("[GC (CMS Final Remark)")) {

            if (line.contains("[GC (CMS Initial Mark)")) {
                // 2319565K->2319565K(6134208K)
                int CMS_initial_mark_index = line.indexOf("CMS-initial-mark") + 18;
                String CMS_initial_mark = line.substring(CMS_initial_mark_index, line.indexOf(",", CMS_initial_mark_index));
                double oldBeforeMB = computeMB(CMS_initial_mark.substring(0, CMS_initial_mark.indexOf('K')));
                double oldAfterMB = computeMB(CMS_initial_mark.substring(CMS_initial_mark.indexOf('>') + 1, CMS_initial_mark.indexOf("K(")));
                double oldMB = computeMB(CMS_initial_mark.substring(CMS_initial_mark.indexOf("(") + 1, CMS_initial_mark.indexOf("K)")));

                String heapUsage = line.substring(line.lastIndexOf("] ") + 2, line.lastIndexOf(']'));
                double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
                double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
                double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

                double fgcSeconds = Double.parseDouble(line.substring(line.lastIndexOf(", ") + 2, line.lastIndexOf(" secs")));

                double yBeforeMB = heapBeforeMB - oldBeforeMB;
                double yAfterMB = heapAfterMB - oldAfterMB;
                double youngMB = heapMB - oldMB;

                usage.addUsage("FGC", offsetTime, yBeforeMB, yAfterMB, youngMB, oldBeforeMB, oldAfterMB, oldMB, fgcSeconds, gcCause);

                STWPauseTime += fgcSeconds;
                fullGCTime += fgcSeconds;
            } else if (line.contains("[GC (CMS Final Remark)")) {
                // 240367K->240367K(344064K)
                int CMS_final_mark_index = line.indexOf("CMS-remark") + 12;
                String CMS_final_mark = line.substring(CMS_final_mark_index, line.indexOf(",", CMS_final_mark_index));
                double oldBeforeMB = computeMB(CMS_final_mark.substring(0, CMS_final_mark.indexOf('K')));
                double oldAfterMB = computeMB(CMS_final_mark.substring(CMS_final_mark.indexOf('>') + 1, CMS_final_mark.indexOf("K(")));
                double oldMB = computeMB(CMS_final_mark.substring(CMS_final_mark.indexOf("(") + 1, CMS_final_mark.indexOf("K)")));

                String heapUsage = line.substring(line.lastIndexOf("] ") + 2, line.lastIndexOf(']'));
                double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
                double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
                double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

                double fgcSeconds = Double.parseDouble(line.substring(line.lastIndexOf(", ") + 2, line.lastIndexOf(" secs")));

                double yBeforeMB = heapBeforeMB - oldBeforeMB;
                double yAfterMB = heapAfterMB - oldAfterMB;
                double youngMB = heapMB - oldMB;

                usage.addUsage("FGC", offsetTime, yBeforeMB, yAfterMB, youngMB, oldBeforeMB, oldAfterMB, oldMB, fgcSeconds, gcCause);

                STWPauseTime += fgcSeconds;
                fullGCTime += fgcSeconds;
            }
        }
        // Young GC
        /*
        [GC (Allocation Failure) 2017-11-20T19:55:51.305+0800: 4.546: [ParNew: 148472K->15791K(154880K), 0.0136927 secs] 267406K->137259K(498944K), 0.0138721 secs] [Times: user=0.04 sys=0.01, real=0.02 secs]
        [GC (GCLocker Initiated GC) 2017-11-20T19:55:52.467+0800: 5.708: [ParNew: 2611K->1477K(154880K), 0.0019634 secs] 132956K->131822K(498944K), 0.0020074 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
        CMS: abort preclean due to time 2017-11-20T19:57:04.539+0800: 77.780: [CMS-concurrent-abortable-preclean: 0.651/5.055 secs] [Times: user=0.65 sys=0.01, real=5.06 secs]
         */
        else if (line.contains("[GC") && line.contains("[ParNew:")){
            // 148472K->15791K(154880K), 0.0136927 secs
            int ParNewIndex = line.indexOf("[ParNew:") + 9;
            String ParNew = line.substring(ParNewIndex, line.indexOf(']', ParNewIndex));
            // System.out.println(ParNew);
            double yBeforeMB = computeMB(ParNew.substring(0, ParNew.indexOf('K')));
            double yAfterMB = computeMB(ParNew.substring(ParNew.indexOf('>') + 1, ParNew.indexOf("K(")));
            double youngMB = computeMB(ParNew.substring(ParNew.indexOf('(') + 1, ParNew.indexOf("K)")));

            // System.out.println(ParNew);
            // System.out.println(" yBeforeMB = " + yBeforeMB + ", yAfterMB = " + yAfterMB + ", youngMB = " + youngMB);

            // 267406K->137259K(498944K), 0.0138721 secs
            String heapUsage = line.substring(line.lastIndexOf("] ") + 2, line.lastIndexOf(']'));
            double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
            double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
            double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

            double oldBeforeMB = heapBeforeMB - yBeforeMB;
            double oldAfterMB = heapAfterMB - yAfterMB;
            double oldMB = heapMB - youngMB;

            double ygcSeconds = Double.parseDouble(line.substring(line.lastIndexOf(", ") + 2, line.lastIndexOf(" secs")));

            usage.addUsage("YGC", offsetTime, yBeforeMB, yAfterMB, youngMB, oldBeforeMB, oldAfterMB, oldMB, ygcSeconds, gcCause);

            STWPauseTime += ygcSeconds;
            youngGCTime += ygcSeconds;
        }
    }

    public void outputUsage(String outputFile) {
        FileTextWriter.write(outputFile, usage.toString());
    }

    public double computeMB(String KB) {
        return (double) Long.parseLong(KB) / 1024;
    }

    public void display() {
        System.out.println(usage.toString());
    }
}
