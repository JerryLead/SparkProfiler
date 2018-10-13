package gc;

import generalGC.HeapUsage;
import util.FileTextWriter;
import util.JsonFileReader;

import java.util.List;

/**
 * Created by xulijie on 18-4-4.
 */
public class ParallelGCViewerLogParser {

    private HeapUsage usage = new HeapUsage();

    private double STWPauseTime = 0;
    private double youngGCTime = 0;
    private double fullGCTime = 0;

    public void parse(String logFile) {
        List<String> lines = JsonFileReader.readFileLines(logFile);

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("[201"))
                parseGCRecord(line);
        }

        display();
    }

    public GCStatistics parseStatistics(String logFile) {
        List<String> lines = JsonFileReader.readFileLines(logFile);

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("[201"))
                parseGCRecord(line);
        }

        return new GCStatistics(STWPauseTime, youngGCTime, fullGCTime, 0);
    }

    public void parseGCRecord(String gcRecord) {


        // [2017-11-20T18:54:36.579+0800][9.032]
        int endTime = gcRecord.indexOf(']', gcRecord.indexOf("][") + 2);
        // 9.032
        double offsetTime = Double.parseDouble(gcRecord.substring(gcRecord.indexOf("][") + 2, endTime));
        int gcCauseIndex = gcRecord.indexOf("] [") + 3;
        // GC (Allocation Failure)
        String gcCause = gcRecord.substring(gcCauseIndex, gcRecord.indexOf('[', gcCauseIndex) - 1);

        boolean isFullGC = gcRecord.contains("Full GC");

        // Young GC
        //
        if (isFullGC == false) {
            int PSYoungGenIndex = gcRecord.indexOf("PSYoungGen") + 12;
            // 735138K->257048K(1514496K)
            String PSYoungGen = gcRecord.substring(PSYoungGenIndex, gcRecord.indexOf(',', PSYoungGenIndex));
            double yBeforeMB = computeMB(PSYoungGen.substring(0, PSYoungGen.indexOf('K')));
            double yAfterMB = computeMB(PSYoungGen.substring(PSYoungGen.indexOf('>') + 1, PSYoungGen.indexOf("K(")));
            double youngMB = computeMB(PSYoungGen.substring(PSYoungGen.indexOf('(') + 1, PSYoungGen.indexOf("K)")));
            // System.out.println(PSYoungGen);
            // System.out.println(" yBeforeMB = " + yBeforeMB + ", yAfterMB = " + yAfterMB + ", youngMB = " + youngMB);

            // 129024K->15319K(494592K)
            int heapUsageIndex = gcRecord.indexOf("] ", PSYoungGenIndex) + 2;
            String heapUsage = gcRecord.substring(heapUsageIndex, gcRecord.indexOf(',', heapUsageIndex));
            double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
            double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
            double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

            double oldBeforeMB = heapBeforeMB - yBeforeMB;
            double oldAfterMB = heapAfterMB - yAfterMB;
            double oldMB = heapMB - youngMB;

            double ygcSeconds = Double.parseDouble(gcRecord.substring(gcRecord.lastIndexOf(", ") + 2, gcRecord.lastIndexOf(" secs")));

            // System.out.println(gcRecord);
            // System.out.println("[PSYoungGen: " + yBeforeMB + "M->" + yAfterMB + "M(" + youngMB + "M)] " +
            //        heapBeforeMB + "M->" + heapAfterMB + "M(" + heapMB + "M), " + ygcSeconds + " secs]");

            // addYoungUsage(double time, double usage, double allocated, String gcType, String gcCause) {
            // usage.addYoungUsage(offsetTime, yBeforeMB, youngMB, "YGC");
            usage.addUsage("YGC", offsetTime, yBeforeMB, yAfterMB, youngMB, oldBeforeMB, oldAfterMB, oldMB, ygcSeconds, gcCause);

            // if (oldAfterMB != oldBeforeMB) {
                //usage.addOldUsage(offsetTime, oldBeforeMB, oldMB, "YGC");
                //usage.addOldUsage(offsetTime, oldAfterMB, oldMB, "");
            // }

            youngGCTime += ygcSeconds;
            STWPauseTime += ygcSeconds;
        }
        // Full GC
        // gcRecord = [PSYoungGen: 21483K->0K(150528K)] [ParOldGen: 17561K->38895K(226304K)] 39044K->38895K(376832K), [Metaspace: 20872K->20872K(1067008K)], 0.0736694 secs] [Times: user=0.13 sys=0.01, real=0.08 secs]
        else {
            int PSYoungGenIndex = gcRecord.indexOf("PSYoungGen") + 12;
            // 735138K->257048K(1514496K)
            String PSYoungGen = gcRecord.substring(PSYoungGenIndex, gcRecord.indexOf(',', PSYoungGenIndex));
            double yBeforeMB = computeMB(PSYoungGen.substring(0, PSYoungGen.indexOf('K')));
            double yAfterMB = computeMB(PSYoungGen.substring(PSYoungGen.indexOf('>') + 1, PSYoungGen.indexOf("K(")));
            double youngMB = computeMB(PSYoungGen.substring(PSYoungGen.indexOf('(') + 1, PSYoungGen.indexOf("K)")));

            int ParOldGenIndex = gcRecord.indexOf("ParOldGen") + 11;
            String ParOldGen = gcRecord.substring(ParOldGenIndex, gcRecord.indexOf(',', ParOldGenIndex));
            double oldBeforeMB = computeMB(ParOldGen.substring(0, ParOldGen.indexOf('K')));
            double oldAfterMB = computeMB(ParOldGen.substring(ParOldGen.indexOf('>') + 1, ParOldGen.indexOf("K(")));
            double oldMB = computeMB(ParOldGen.substring(ParOldGen.indexOf('(') + 1, ParOldGen.indexOf("K)")));

            int heapUsageIndex = gcRecord.lastIndexOf("] ") + 2;
            String heapUsage = gcRecord.substring(heapUsageIndex, gcRecord.lastIndexOf(','));
            double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
            double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
            double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

            int MetaspaceIndex = gcRecord.indexOf("Metaspace") + 11;
            String Metaspace = gcRecord.substring(MetaspaceIndex, gcRecord.indexOf(',', MetaspaceIndex));
            double metaBeforeMB = computeMB(Metaspace.substring(0, Metaspace.indexOf('K')));
            double metaAfterMB = computeMB(Metaspace.substring(Metaspace.indexOf('>') + 1, Metaspace.indexOf("K(")));
            double metaMB = computeMB(Metaspace.substring(Metaspace.indexOf('(') + 1, Metaspace.indexOf("K)")));

            double fgcSeconds = Double.parseDouble(gcRecord.substring(gcRecord.lastIndexOf(", ") + 2, gcRecord.lastIndexOf(" secs")));

            // System.out.println("[PSYoungGen: " + yBeforeMB + "M->" + yAfterMB + "M(" + youngMB + "M)] [ParOldGen: "
            //        + oBeforeMB + "M->" + oAfterMB + "M(" + oldMB + "M)] " + heapBeforeMB + "M->" + heapAfterMB + "M(" + heapMB
            //        + "M), [Metaspace: " + metaBeforeMB + "M->" + metaAfterMB + "M(" + metaMB + "M)], " + fgcSeconds + " secs]");
            /*
            usage.addYoungUsage(offsetTime, yBeforeMB, youngMB, "FGC");
            usage.addYoungUsage(offsetTime, yAfterMB, youngMB, "");
            usage.addOldUsage(offsetTime, oBeforeMB, oldMB, "FGC");
            usage.addOldUsage(offsetTime, oAfterMB, oldMB, "");
            usage.addMetaUsage(offsetTime, metaBeforeMB, metaMB, "FGC");
            usage.addMetaUsage(offsetTime, metaAfterMB, metaMB, "");
            */
            usage.addUsage("FGC", offsetTime, yBeforeMB, yAfterMB, youngMB, oldBeforeMB, oldAfterMB, oldMB, fgcSeconds, gcCause);

            fullGCTime += fgcSeconds;
            STWPauseTime += fgcSeconds;
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
