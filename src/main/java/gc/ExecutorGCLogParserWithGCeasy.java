package gc;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import util.CommandRunner;
import util.FileTextWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xulijie on 17-9-2.
 */
public class ExecutorGCLogParserWithGCeasy {

    public static void parseExecutorGCLog(String gcLogFile, String gcMetricsFile) {
        String curl = "curl -XPOST --data-binary @" + gcLogFile
                + " http://localhost:8080/analyzeGC?apiKey=e094a34e-c3eb-4c9a-8254-f0dd107245cc --header Content-Type:text";
        String metricsJson = CommandRunner.execCurl(curl);

        if(metricsJson.trim().isEmpty())
            System.err.println("Error in parsing " + gcLogFile);
        else {

            FileTextWriter.write(gcMetricsFile, metricsJson);
        }

    }


    public static void main(String[] args) {
        String gcLogFile = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/GroupByRDD-0.5-2/GroupByRDD-CMS-4-28G-0.5_app-20170721104729-0019/executors/0/stdout";

        String curl = "curl -XPOST --data-binary @" + gcLogFile
                + " http://localhost:8080/analyzeGC?apiKey=e094a34e-c3eb-4c9a-8254-f0dd107245cc --header Content-Type:text";

        long start = System.currentTimeMillis();
        String metricsJson = CommandRunner.execCurl(curl);
        System.out.println(metricsJson);
        long end = System.currentTimeMillis();

        System.out.println((end - start) / 1000 + "s");
        // execCurl();
    }
}

// curl -X POST --data-binary @/Users/xulijie/Documents/GCResearch/Experiments/profiles/GroupByRDD-0.5-2/GroupByRDD-Parallel-2-14G-0.5_app-20170721101243-0006/executors/12/stdout http://localhost:8080/analyzeGC?apiKey=e094a34e-c3eb-4c9a-8254-f0dd107245cc --header "Content-Type:text"

