package gc;

import generalGC.HeapUsage;
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


}
