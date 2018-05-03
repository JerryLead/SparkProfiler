package parser;

import appinfo.Application;
import appinfo.Executor;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import gc.CMSGCViewerLogParser;
import gc.G1GCViewerLogParser;
import gc.GCStatistics;
import gc.ParallelGCViewerLogParser;
import util.JsonFileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

        Set<String> aliveExecutorIds = new HashSet<String>();
        for (Executor executor : app.getExecutors())
            aliveExecutorIds.add(executor.getId());

        for (File executorDir : new File(executorsDir).listFiles()) {
            if (executorDir.isDirectory()) {
                String executorId = executorDir.getName();

                if (aliveExecutorIds.contains(executorId)) {
                    String gcSummaryFile = executorDir.getAbsolutePath() + File.separatorChar
                            + "gcMetrics-" + executorId + ".csv";

                    List<String> lines = JsonFileReader.readFileLines(gcSummaryFile);
                    for (String line : lines) {
                        String[] metrics = line.split(";");
                        app.getExecutor(executorId).addGCMetric(metrics);
                    }

                    String gceasyFile = executorDir.getAbsolutePath() + File.separatorChar
                            + "gcMetrics-" + executorId + ".json";
                    String gceasyJson = JsonFileReader.readFile(gceasyFile);
                    app.getExecutor(executorId).addGCeasyMetric(gceasyJson);

                    String topFile = executorDir.getAbsolutePath() + File.separatorChar
                            + "topMetrics.txt";
                    List<String> topMetricsLines = JsonFileReader.readFileLines(topFile);
                    app.getExecutor(executorId).addTopMetrics(topMetricsLines);

                    // Parse spill metrics
                    String stderr = executorDir.getAbsolutePath() + File.separatorChar
                            + "stderr";
                    List<String> stderrLines = JsonFileReader.readFileLines(stderr);
                    app.getExecutor(executorId).addSpillMetrics(stderrLines);

                    String gcEventFile = executorDir.getAbsolutePath() + File.separatorChar
                            + "gcEvent-" + executorId + ".txt";
                    List<String> gcEventLines = JsonFileReader.readFileLines(gcEventFile);
                    app.getExecutor(executorId).countGCTimeInShuffleSpill(gcEventLines);

                    String gcPlainEventFile = executorDir.getAbsolutePath() + File.separatorChar
                            + "gcPlainEvent-" + executorId + ".txt";

                    GCStatistics stat = null;

                    if (app.getName().contains("Parallel")) {
                        ParallelGCViewerLogParser parser = new ParallelGCViewerLogParser();
                        stat = parser.parseStatistics(gcPlainEventFile);
                    } else if (app.getName().contains("CMS")) {
                        CMSGCViewerLogParser parser = new CMSGCViewerLogParser();
                        stat = parser.parseStatistics(gcPlainEventFile);
                    } else if (app.getName().contains("G1")) {
                        G1GCViewerLogParser parser = new G1GCViewerLogParser();
                        stat = parser.parseStatistics(gcPlainEventFile);
                    }

                    app.getExecutor(executorId).setGCStatistics(stat);
                }
            }
        }

    }


    public static Set<String> getAliveExecutors(String executorJsonFile) {

        Set<String> executorIds = new HashSet<String>();

        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(JsonFileReader.readFile(executorJsonFile));
            for (JsonElement executorElem : el.getAsJsonArray()) {
                Executor executor = new Executor(executorElem.getAsJsonObject());
                executorIds.add(executor.getId());
            }
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return executorIds;
    }
}
