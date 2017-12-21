package generalGC;

import util.JsonFileReader;

import java.util.List;

/**
 * Created by xulijie on 17-12-21.
 */
public class ParallelGCLogParser {

    public void parse(String logFile) {
        List<String> lines = JsonFileReader.readFileLines(logFile);

        String timestamp = "";

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("2017-"))
                timestamp = line.substring(0, line.indexOf(':', line.indexOf(": ") + 1));
            if (line.startsWith("[PSYoungGen"))
                parseGCRecord(timestamp, line);
        }
    }

    public void parseGCRecord(String timestamp, String gcRecord) {
        String time = timestamp.substring(0, timestamp.indexOf(": "));
        double offsetTime = Double.parseDouble(timestamp.substring(timestamp.indexOf(" ") + 1));
        boolean isFullGC = gcRecord.contains("ParOldGen");

        // Young GC
        // gcRecord = [PSYoungGen: 129024K->15311K(150528K)] 129024K->15319K(494592K), 0.0200576 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
        if (isFullGC == false) {
            // 129024K->15311K(150528K)
            String PSYoungGen = gcRecord.substring(gcRecord.indexOf(":") + 2, gcRecord.indexOf(']'));
            double yBeforeMB = computeMB(PSYoungGen.substring(0, PSYoungGen.indexOf('K')));
            double yAfterMB = computeMB(PSYoungGen.substring(PSYoungGen.indexOf('>') + 1, PSYoungGen.indexOf("K(")));
            double youngMB = computeMB(PSYoungGen.substring(PSYoungGen.indexOf('(') + 1, PSYoungGen.indexOf("K)")));
            // System.out.println(PSYoungGen);
            // System.out.println(" yBeforeMB = " + yBeforeMB + ", yAfterMB = " + yAfterMB + ", youngMB = " + youngMB);

            // 129024K->15319K(494592K)
            String heapUsage = gcRecord.substring(gcRecord.indexOf("] ") + 2, gcRecord.indexOf(','));
            double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
            double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
            double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

            double ygcSeconds = Double.parseDouble(gcRecord.substring(gcRecord.indexOf(", ") + 2, gcRecord.indexOf(" secs")));

            System.out.println(gcRecord);
            System.out.println("[PSYoungGen: " + yBeforeMB + "M->" + yAfterMB + "M(" + youngMB + "M)] " +
                    heapBeforeMB + "M->" + heapAfterMB + "M(" + heapMB + "M), " + ygcSeconds + " secs]");
        }
        // Full GC
        // gcRecord = [PSYoungGen: 21483K->0K(150528K)] [ParOldGen: 17561K->38895K(226304K)] 39044K->38895K(376832K), [Metaspace: 20872K->20872K(1067008K)], 0.0736694 secs] [Times: user=0.13 sys=0.01, real=0.08 secs]
        else {
            System.out.println(gcRecord);
            // PSYoungGen: 21483K->0K(150528K)
            String PSYoungGen = gcRecord.substring(gcRecord.indexOf(":") + 2, gcRecord.indexOf(']'));
            double yBeforeMB = computeMB(PSYoungGen.substring(0, PSYoungGen.indexOf('K')));
            double yAfterMB = computeMB(PSYoungGen.substring(PSYoungGen.indexOf('>') + 1, PSYoungGen.indexOf("K(")));
            double youngMB = computeMB(PSYoungGen.substring(PSYoungGen.indexOf('(') + 1, PSYoungGen.indexOf("K)")));

            gcRecord = gcRecord.substring(gcRecord.indexOf("]") + 1);
            String ParOldGen = gcRecord.substring(gcRecord.indexOf(":") + 2, gcRecord.indexOf("]"));
            double oBeforeMB = computeMB(ParOldGen.substring(0, ParOldGen.indexOf('K')));
            double oAfterMB = computeMB(ParOldGen.substring(ParOldGen.indexOf('>') + 1, ParOldGen.indexOf("K(")));
            double oldMB = computeMB(ParOldGen.substring(ParOldGen.indexOf('(') + 1, ParOldGen.indexOf("K)")));

            gcRecord = gcRecord.substring(gcRecord.indexOf("]") + 2);
            String heapUsage = gcRecord.substring(0, gcRecord.indexOf(','));
            double heapBeforeMB = computeMB(heapUsage.substring(0, heapUsage.indexOf('K')));
            double heapAfterMB = computeMB(heapUsage.substring(heapUsage.indexOf('>') + 1, heapUsage.indexOf("K(")));
            double heapMB = computeMB(heapUsage.substring(heapUsage.indexOf('(') + 1, heapUsage.indexOf("K)")));

            String Metaspace = gcRecord.substring(gcRecord.indexOf(":") + 2, gcRecord.indexOf(']'));
            double metaBeforeMB = computeMB(Metaspace.substring(0, Metaspace.indexOf('K')));
            double metaAfterMB = computeMB(Metaspace.substring(Metaspace.indexOf('>') + 1, Metaspace.indexOf("K(")));
            double metaMB = computeMB(Metaspace.substring(Metaspace.indexOf('(') + 1, Metaspace.indexOf("K)")));

            gcRecord = gcRecord.substring(gcRecord.indexOf("["));
            double fgcSeconds = Double.parseDouble(gcRecord.substring(gcRecord.indexOf(", ") + 2, gcRecord.indexOf(" secs")));

            System.out.println("[PSYoungGen: " + yBeforeMB + "M->" + yAfterMB + "M(" + youngMB + "M)] [ParOldGen: "
                    + oBeforeMB + "M->" + oAfterMB + "M(" + oldMB + "M)] " + heapBeforeMB + "M->" + heapAfterMB + "M(" + heapMB
                    + "M), [Metaspace: " + metaBeforeMB + "M->" + metaAfterMB + "M(" + metaMB + "M)], " + fgcSeconds + " secs]");
        }

    }

    public double computeMB(String KB) {
        return (double) Long.parseLong(KB) / 1024;
    }

    public static void main(String[] args) {
        String logFile = "src/test/gclogs/ParallelLog.txt";
        String outputFile = "src/test/gclogs/ParsedParallelLog.txt";
        ParallelGCLogParser parser = new ParallelGCLogParser();
        parser.parse(logFile);
    }


}
