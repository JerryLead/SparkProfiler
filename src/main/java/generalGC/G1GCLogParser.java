package generalGC;

import util.FileTextWriter;
import util.JsonFileReader;

import java.util.List;

/**
 * Created by xulijie on 17-12-21.
 */
public class G1GCLogParser {

    private HeapUsage usage = new HeapUsage();

    public void parse(String logFile) {
        List<String> lines = JsonFileReader.readFileLines(logFile);

        String timestamp = "";

        String gcCause = null;

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("2017-")) {
                timestamp = line.substring(0, line.indexOf(':', line.indexOf(": ") + 1));
                timestamp = timestamp.substring(timestamp.lastIndexOf(':') + 2);
            }

            if (line.contains("[GC pause") && line.contains("(young)")) {
                gcCause = "YGC";
            }

            if (line.contains("[Full GC")
                    || line.contains("(young) (initial-mark)")
                    || line.contains("[GC cleanup")
                    || line.contains("[GC remark")
                    || line.contains("[GC concurrent")
                    || line.contains("(mixed)")) {
                gcCause = "FGC";
            }

            if (line.startsWith("[Eden"))
                parseGCRecord(Double.parseDouble(timestamp), line, gcCause);
        }
    }

    private void parseGCRecord(double timestamp, String line, String gcCause) {

        // [Eden: 25.0M(25.0M)->0.0B(35.0M) Survivors: 0.0B->4096.0K Heap: 25.0M(504.0M)->5192.0K(504.0M)]

        // 25.0M(25.0M)->0.0B(35.0M)
        String Eden = line.substring(line.indexOf(':') + 2, line.indexOf("Survivors") - 1);
        double edenBeforeMB = computeMB(Eden.substring(0, Eden.indexOf('(')));
        double edenBeforeTotalMB = computeMB(Eden.substring(Eden.indexOf('(') + 1, Eden.indexOf(')')));
        // 0.0B(35.0M)
        Eden = Eden.substring(Eden.indexOf('>') + 1);
        double edenAfterMB = computeMB(Eden.substring(0, Eden.indexOf('(')));
        double edenAfterTotalMB = computeMB(Eden.substring(Eden.indexOf('(') + 1, Eden.indexOf(')')));

        // 0.0B->4096.0K
        String Survivors = line.substring(line.indexOf("Survivors") + 11, line.indexOf("Heap") - 1);
        double survivorBeforeMB = computeMB(Survivors.substring(0, Survivors.indexOf('-')));
        double survivorAfterMB = computeMB(Survivors.substring(Survivors.indexOf('>') + 1));

        // 25.0M(504.0M)->5192.0K(504.0M)
        String Heap = line.substring(line.indexOf("Heap") + 6, line.lastIndexOf(']'));
        double heapBeforeMB = computeMB(Heap.substring(0, Heap.indexOf('(')));
        double heapBeforeTotalMB = computeMB(Heap.substring(Heap.indexOf('(') + 1, Heap.indexOf(')')));
        // 5192.0K(504.0M)
        Heap = Heap.substring(Heap.indexOf('>') + 1);
        double heapAfterMB = computeMB(Heap.substring(0, Heap.indexOf('(')));
        double heapAfterTotalMB = computeMB(Heap.substring(Heap.indexOf('(') + 1, Heap.indexOf(')')));


        double yBeforeMB = edenBeforeMB + survivorBeforeMB;
        double yAfterMB = edenAfterMB + survivorAfterMB;
        double youngBeforeMB = edenBeforeTotalMB + survivorBeforeMB;
        double youngAfterMB = edenAfterTotalMB + survivorAfterMB;
        // System.out.println(PSYoungGen);
        // System.out.println(" yBeforeMB = " + yBeforeMB + ", yAfterMB = " + yAfterMB + ", youngMB = " + youngMB);

        // 129024K->15319K(494592K)
        double oldBeforeMB = heapBeforeMB - yBeforeMB;
        double oldAfterMB = heapAfterMB - yAfterMB;
        double oldBeforeTotalMB = heapBeforeTotalMB - youngBeforeMB;
        double oldAfterTotalMB = heapAfterTotalMB - youngAfterMB;


        /*
        if (gcCause.equals("YGC")) {
            usage.addYoungUsage(timestamp, yBeforeMB, youngBeforeMB, gcCause);
            usage.addYoungUsage(timestamp, yAfterMB, youngAfterMB, "");

            if (oldAfterMB != oldBeforeMB) {
                usage.addOldUsage(timestamp, oldBeforeMB, oldBeforeTotalMB, gcCause);
                usage.addOldUsage(timestamp, oldAfterMB, oldAfterTotalMB, "");
            }
        } else if (gcCause.equals("FGC")){
            usage.addYoungUsage(timestamp, yBeforeMB, youngBeforeMB, gcCause);
            usage.addYoungUsage(timestamp, yAfterMB, youngAfterMB, "");

            usage.addOldUsage(timestamp, oldBeforeMB, oldBeforeTotalMB, gcCause);
            usage.addOldUsage(timestamp, oldAfterMB, oldAfterTotalMB, "");
        }
        */
    }

    /*
    2017-11-22T10:03:14.403+0800: 596.322: [GC pause (G1 Evacuation Pause) (young) 596.322: [G1Ergonomics (CSet Construction) start choosing CSet, _pending_cards: 64042, predicted base time: 43.99 ms, remaining time: 156.01 ms, target pause time: 200.00 ms]

     */

    public double computeMB(String size) {
        double mb = Double.parseDouble(size.substring(0, size.length() - 1));
        if (size.endsWith("K"))
            mb =  mb / 1024;
        else if (size.endsWith("B"))
            mb = mb / 1024 / 1024;
        else if (size.endsWith("G"))
            mb = mb * 1024;

        return mb;
    }

    private void outputUsage(String outputFile) {
        FileTextWriter.write(outputFile, usage.toString());
    }

    public static void main(String[] args) {
        String logFile = "src/test/gclogs/SVM-1.0-E1-G1-19.txt";
        String outputFile = "src/test/gclogs/Parsed-SVM-1.0-E1-G1-19.txt";
        G1GCLogParser parser = new G1GCLogParser();
        parser.parse(logFile);
        parser.outputUsage(outputFile);
    }
}
