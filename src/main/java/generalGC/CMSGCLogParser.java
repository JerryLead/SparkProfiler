package generalGC;

import util.FileTextWriter;
import util.JsonFileReader;

import java.util.List;

/**
 * Created by xulijie on 17-12-21.
 */
public class CMSGCLogParser {

    private HeapUsage usage = new HeapUsage();

    public void parse(String logFile) {
        List<String> lines = JsonFileReader.readFileLines(logFile);

        for (String line : lines) {
            line = line.trim();

            if (line.contains("[CMS") || line.contains("[GC")) {
                String timestamp = line.substring(0, line.indexOf(':', line.indexOf(": ") + 1));
                timestamp = timestamp.substring(timestamp.lastIndexOf(':') + 2);
                parseGCRecord(Double.parseDouble(timestamp), line.substring(line.indexOf('[')));
            }
        }
    }

    public void parseGCRecord(double timestamp, String line) {
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
        if (line.startsWith("[CMS-concurrent") || line.startsWith("[GC (CMS Initial Mark)") || line.startsWith("[GC (CMS Final Remark)")) {

            if (line.startsWith("[GC (CMS Initial Mark)")) {
                // 53400K(344064K)] 79710K(498944K), 0.0032015 secs
                String CMS_initial_mark = line.substring(line.indexOf(':') + 2, line.indexOf("secs]") + 4);
                double oCurrentMB = computeMB(CMS_initial_mark.substring(0, CMS_initial_mark.indexOf('K')));
                double oldMB = computeMB(CMS_initial_mark.substring(CMS_initial_mark.indexOf("(") + 1, CMS_initial_mark.indexOf("K)")));

                // 79710K(498944K), 0.0032015 secs
                CMS_initial_mark = CMS_initial_mark.substring(CMS_initial_mark.indexOf(']') + 2);
                double heapCurrentMB = computeMB(CMS_initial_mark.substring(0, CMS_initial_mark.indexOf('K')));
                double heapMB = computeMB(CMS_initial_mark.substring(CMS_initial_mark.indexOf('(') + 1, CMS_initial_mark.indexOf("K)")));

                double fgcSeconds = Double.parseDouble(CMS_initial_mark.substring(CMS_initial_mark.indexOf(", ") + 2, CMS_initial_mark.indexOf(" secs")));

//                double yCurrentMB = heapCurrentMB - oCurrentMB;
//                double youngMB = heapMB - oldMB;
//
//                usage.addYoungUsage(timestamp, yCurrentMB, youngMB, "FGC");
                usage.addOldUsage(timestamp, oCurrentMB, oldMB, "FGC");

            } else if (line.startsWith("[GC (CMS Final Remark)")) {
                // 97189 K (154880 K)
                String YG_occupancy = line.substring(line.indexOf(':') + 2, line.indexOf(']'));
                double yCurrentMB = computeMB(YG_occupancy.substring(0, YG_occupancy.indexOf('K') - 1));
                double youngMB = computeMB(YG_occupancy.substring(YG_occupancy.indexOf("(") + 1, YG_occupancy.indexOf("K)") - 1));

                // 118936K(344064K)] 216125K(498944K), 0.0442861 secs] [Times: user=0.13 sys=0.00, real=0.04 secs]
                String CMS_remark = line.substring(line.indexOf("CMS-remark") + 12);
                double oCurrentMB = computeMB(CMS_remark.substring(0, CMS_remark.indexOf('K')));
                double oldMB = computeMB(CMS_remark.substring(CMS_remark.indexOf("(") + 1, CMS_remark.indexOf("K)")));

                // 216125K(498944K), 0.0442861 secs] [Times: user=0.13 sys=0.00, real=0.04 secs]
                CMS_remark = CMS_remark.substring(CMS_remark.indexOf(']') + 2);
                double heapCurrentMB = computeMB(CMS_remark.substring(0, CMS_remark.indexOf('K')));
                double heapMB = computeMB(CMS_remark.substring(CMS_remark.indexOf('(') + 1, CMS_remark.indexOf("K)")));

                double fgcSeconds = Double.parseDouble(CMS_remark.substring(CMS_remark.indexOf(", ") + 2, CMS_remark.indexOf(" secs")));

                usage.addYoungUsage(timestamp, yCurrentMB, youngMB, "FGC");
                usage.addOldUsage(timestamp, oCurrentMB, oldMB, "FGC");

            }
        }
        // Young GC
        /*
        [GC (Allocation Failure) 2017-11-20T19:55:51.305+0800: 4.546: [ParNew: 148472K->15791K(154880K), 0.0136927 secs] 267406K->137259K(498944K), 0.0138721 secs] [Times: user=0.04 sys=0.01, real=0.02 secs]
        [GC (GCLocker Initiated GC) 2017-11-20T19:55:52.467+0800: 5.708: [ParNew: 2611K->1477K(154880K), 0.0019634 secs] 132956K->131822K(498944K), 0.0020074 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
        CMS: abort preclean due to time 2017-11-20T19:57:04.539+0800: 77.780: [CMS-concurrent-abortable-preclean: 0.651/5.055 secs] [Times: user=0.65 sys=0.01, real=5.06 secs]
         */
        else if (line.startsWith("[GC") && line.contains("[ParNew:")){
            String ygcCause = line.substring(line.indexOf('(') + 1, line.indexOf(')'));

            // 148472K->15791K(154880K), 0.0136927 secs
            String ParNew = line.substring(line.indexOf("[ParNew:") + 9, line.indexOf(']'));
            // System.out.println(ParNew);
            double yBeforeMB = computeMB(ParNew.substring(0, ParNew.indexOf('K')));
            double yAfterMB = computeMB(ParNew.substring(ParNew.indexOf('>') + 1, ParNew.indexOf("K(")));
            double youngMB = computeMB(ParNew.substring(ParNew.indexOf('(') + 1, ParNew.indexOf("K)")));

            double ygcSeconds = Double.parseDouble(ParNew.substring(ParNew.indexOf(", ") + 2, ParNew.indexOf(" secs")));
            // System.out.println(ParNew);
            // System.out.println(" yBeforeMB = " + yBeforeMB + ", yAfterMB = " + yAfterMB + ", youngMB = " + youngMB);

            // 267406K->137259K(498944K), 0.0138721 secs
            String heapUsage = line.substring(line.indexOf("]") + 2, line.indexOf("] [Times:"));
            double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
            double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
            double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

            double oldBeforeMB = heapBeforeMB - yBeforeMB;
            double oldAfterMB = heapAfterMB - yAfterMB;
            double oldMB = heapMB - youngMB;

            usage.addYoungUsage(timestamp, yBeforeMB, youngMB, "YGC");
            usage.addYoungUsage(timestamp, yAfterMB, youngMB, "");

            if (oldAfterMB != oldBeforeMB) {
                usage.addOldUsage(timestamp, oldBeforeMB, oldMB, "YGC");
                usage.addOldUsage(timestamp, oldAfterMB, oldMB, "");
            }

        }
    }

    private void outputUsage(String outputFile) {
        FileTextWriter.write(outputFile, usage.toString());
    }

    public double computeMB(String KB) {
        return (double) Long.parseLong(KB) / 1024;
    }

    public static void main(String[] args) {
        String logFile = "src/test/gclogs/CMSLog.txt";
        String outputFile = "src/test/gclogs/ParsedCMSLog.txt";
        CMSGCLogParser parser = new CMSGCLogParser();
        parser.parse(logFile);
        parser.outputUsage(outputFile);
    }
}
