package parser;

import appinfo.Application;
import com.google.gson.*;
import util.JsonFileReader;

import java.io.File;


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

        for (File attemptFile : stageDir.listFiles()) {
            if (attemptFile.getName().contains("taskSummary")) {
                parseTaskSummary(attemptFile);
            } else {
                parseTaskJson(attemptFile);
            }
        }

    }

    private void parseTaskSummary(File attemptFile) {
    }

    /**
     * @param attemptFile attempt-0.json
     */
    private void parseTaskJson(File attemptFile) {
        String stageTasksJson = JsonFileReader.readFile(attemptFile.getAbsolutePath());

        /*
        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(stageTasksJson);
            JsonElement tasksElem = el.getAsJsonObject().get("tasks");

            JsonArray taskJsonArray = null;

            if (tasksElem.isJsonArray())
                taskJsonArray = tasksElem.getAsJsonArray();
            else {
                System.err.println("Error in parsing the " + attemptFile.getAbsolutePath());
                System.exit(1);
            }

            for (JsonElement stageElem : taskJsonArray) {
                JsonObject stageObject = stageElem.getAsJsonObject();
                // stageId = 0, attemptId = 1
                // stageId = 0, attemptId = 0
                app.addStage(stageObject);
            }

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        */

    }
}
