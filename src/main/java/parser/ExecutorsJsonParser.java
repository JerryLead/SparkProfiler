package parser;

import appinfo.Application;
import appinfo.Executor;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import util.JsonFileReader;

import java.io.File;
import java.util.List;


public class ExecutorsJsonParser {

    public void parseExecutorsJson(String allexecutorsJson, Application app) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(allexecutorsJson);
            for (JsonElement executorElem : el.getAsJsonArray()) {
                Executor executor = new Executor(executorElem.getAsJsonObject());
                app.addExecutor(executor);
            }

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void parseExecutorGCSummary(String executorsDir, Application app) {

        for (File executorDir : new File(executorsDir).listFiles()) {
            if (executorDir.isDirectory()) {
                int executorId = Integer.parseInt(executorDir.getName());
                String gcSummaryFile = executorDir.getAbsolutePath() + File.separatorChar
                        + "gcMetrics-" + executorId + ".csv";

                List<String> lines = JsonFileReader.readFileLines(gcSummaryFile);
                for (String line : lines) {
                    String[] metrics = line.split(";");
                    app.getExecutor(executorId).addGCMetric(metrics);
                }
            }
        }

    }
}
