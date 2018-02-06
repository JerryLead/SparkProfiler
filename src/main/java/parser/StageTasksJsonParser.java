package parser;

import appinfo.Application;
import appinfo.Stage;
import com.google.gson.*;
import util.JsonFileReader;

import java.io.File;
import java.util.Map;


public class StageTasksJsonParser {

    /**
     *
     * stageDir contains attempt-id.json and attempt-id-taskSummary.json
     * @param stageDir profiles/appName_appId/job-0/stage-0
     * @param jobId 0
     * @param stageId 0
     * @param app
     */
    public void parseStageTasksJson(File stageDir, int jobId, int stageId, Application app) {

        Stage stage = app.getStage(stageId);

        for (File attemptFile : stageDir.listFiles()) {
            String attemptFileName = attemptFile.getName();

            if (attemptFile.getName().contains("taskSummary")) {
                int stageAttemptId = Integer.parseInt(attemptFileName.substring(attemptFileName.indexOf('-') + 1,
                        attemptFileName.lastIndexOf('-')));

                parseTaskSummary(attemptFile, stage, stageAttemptId);
            } else {
                int stageAttemptId = Integer.parseInt(attemptFileName.substring(attemptFileName.indexOf('-') + 1,
                        attemptFileName.lastIndexOf("json") - 1));

                parseTasksJson(attemptFile, stage, stageAttemptId);
            }
        }

    }

    private void parseTaskSummary(File attemptFile, Stage stage, int stageAttemptId) {
        String taskSummaryJson = JsonFileReader.readFile(attemptFile.getAbsolutePath());

        if (!taskSummaryJson.trim().isEmpty()) {
            try {
                JsonParser parser = new JsonParser();
                JsonElement el = parser.parse(taskSummaryJson);
                stage.addTaskSummary(stageAttemptId, el.getAsJsonObject());

            } catch (JsonIOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * @param attemptFile attempt-0.json
     */
    private void parseTasksJson(File attemptFile, Stage stage, int stageAttemptId) {

        String stageTasksJson = JsonFileReader.readFile(attemptFile.getAbsolutePath());

        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(stageTasksJson);
            JsonObject tasksObject = el.getAsJsonObject().get("tasks").getAsJsonObject();

            for (Map.Entry<String, JsonElement> taskEntry : tasksObject.entrySet()) {
                JsonObject taskObject = taskEntry.getValue().getAsJsonObject();
                stage.addTask(stageAttemptId, taskObject);
            }

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

   
}
