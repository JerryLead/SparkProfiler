import util.JsonFileReader;

import java.util.List;

/**
 * Created by xulijie on 17-11-3.
 */
public class TopMetricsAnalyzer {
    public static void main(String[] args) {
        String file = "/Users/xulijie/Documents/GCResearch/NewExperiments/profiles/RDDJoin-0.5/topMetrics/aliSlave1/rjoin-CMS-1-6656m-0.5-n1.top";

        List<String> lines = JsonFileReader.readFileLines(file);
        String time = "";

        for (String line : lines) {
            if (line.startsWith("top")) {
                time = line.substring(line.indexOf("-") + 2, line.indexOf("up") - 1);

            } else if (line.trim().endsWith("java")) {
                String[] metrics = line.trim().split("\\s+");
                String PID = metrics[0];
                double CPU = Double.parseDouble(metrics[8]);
                String memoryStr = metrics[5];
                double memory;
                if (memoryStr.endsWith("g")) {
                    memory = Double.parseDouble(memoryStr.substring(0, memoryStr.indexOf("g")));
                } else if (memoryStr.endsWith("t")) {
                    memory = Double.parseDouble(memoryStr.substring(0, memoryStr.indexOf("t")));
                    memory = memory * 1024;
                } else {
                    memory = Double.parseDouble(memoryStr);
                    memory = memory / 1024 / 1024;
                }




                System.out.println("[" + time + "] PID = " + PID + ", CPU = "
                        + CPU + ", Memory = " + memory);
            }
        }
    }
}
