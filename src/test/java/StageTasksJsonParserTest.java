import com.google.gson.*;
import util.FileTextWriter;
import util.JsonFileReader;

import java.io.File;
import java.util.Map;


public class StageTasksJsonParserTest {

    public static void parseTaskJson(File attemptFile) {
        String stageTasksJson = JsonFileReader.readFile(attemptFile.getAbsolutePath());

        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(stageTasksJson);
            JsonObject tasksObject = el.getAsJsonObject().get("tasks").getAsJsonObject();

            for (Map.Entry<String, JsonElement> taskEntry : tasksObject.entrySet()) {
                JsonObject taskObject = taskEntry.getValue().getAsJsonObject();


            }

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }


    }

    public static void main (String[] args) {
        String attemptFile = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/RDDJoin-CMS-4-28G-0.5_app-20170623114155-0011/job-0/stage-0/attempt-0.json";

        // parseTaskJson(new File(attemptFile));
    }
}
