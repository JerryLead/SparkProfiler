package gc;

import generalGC.HeapUsage;
import util.FileTextWriter;
import util.JsonFileReader;

import java.util.List;

/**
 * Created by xulijie on 18-4-5.
 */
public class G1GCViewerLogParser {
    private HeapUsage usage = new HeapUsage();

    public void parse(String logFile) {
        List<String> lines = JsonFileReader.readFileLines(logFile);

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("[2017-"))
                parseGCRecord(line);
        }
        display();
    }

    private void parseGCRecord(String line) {
        String gcType = "";

        if (line.contains("[GC pause") && line.contains("(young)")) {
            gcType = "YGC";
        }

        if (line.contains("[Full GC")
                || line.contains("(young) (initial-mark)")
                || line.contains("[GC cleanup")
                || line.contains("[GC remark")
                || line.contains("[GC concurrent")
                || line.contains("(mixed)")) {
            gcType = "FGC";
        }

        if (line.contains("[Eden")) {
            // [2017-11-20T18:54:36.579+0800][9.032]
            int endTime = line.indexOf(']', line.indexOf("][") + 2);
            // 9.032
            double offsetTime = Double.parseDouble(line.substring(line.indexOf("][") + 2, endTime));
            int gcCauseIndex = line.indexOf("] [") + 3;
            // GC (Allocation Failure)
            String gcCause = line.substring(gcCauseIndex, line.indexOf('[', gcCauseIndex) - 1);


            int EdenIndex = line.indexOf("Eden") + 6;
            // 735138K->257048K(1514496K)
            String Eden = line.substring(EdenIndex, line.indexOf(',', EdenIndex));
            double yBeforeMB = computeMB(Eden.substring(0, Eden.indexOf('K')));
            double yAfterMB = computeMB(Eden.substring(Eden.indexOf('>') + 1, Eden.indexOf("K(")));
            double youngMB = computeMB(Eden.substring(Eden.indexOf('(') + 1, Eden.indexOf("K)")));
            // System.out.println(PSYoungGen);
            // System.out.println(" yBeforeMB = " + yBeforeMB + ", yAfterMB = " + yAfterMB + ", youngMB = " + youngMB);

            // 129024K->15319K(494592K)
            int heapUsageIndex = line.lastIndexOf("] ") + 2;
            // int heapUsageIndex = line.indexOf("] ", EdenIndex) + 2;
            String heapUsage = line.substring(heapUsageIndex, line.indexOf(',', heapUsageIndex));
            double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
            double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
            double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

            double oldBeforeMB = heapBeforeMB - yBeforeMB;
            double oldAfterMB = heapAfterMB - yAfterMB;
            double oldMB = heapMB - youngMB;

            double gcSeconds = Double.parseDouble(line.substring(line.lastIndexOf(", ") + 2, line.lastIndexOf(" secs")));

            usage.addUsage(gcType, offsetTime, yBeforeMB, yAfterMB, youngMB, oldBeforeMB, oldAfterMB, oldMB, gcSeconds, gcCause);

        }

    }

    /*
    2017-11-22T10:03:14.403+0800: 596.322: [GC pause (G1 Evacuation Pause) (young) 596.322: [G1Ergonomics (CSet Construction) start choosing CSet, _pending_cards: 64042, predicted base time: 43.99 ms, remaining time: 156.01 ms, target pause time: 200.00 ms]

     */

    public double computeMB(String KB) {
        return (double) Long.parseLong(KB) / 1024;
    }

    public void outputUsage(String outputFile) {
        FileTextWriter.write(outputFile, usage.toString());
    }

    public void display() {
        System.out.println(usage.toString());
    }

}
