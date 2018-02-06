package parser;

import appinfo.Application;
import com.google.gson.*;
import util.HtmlFetcher;
import util.FileTextWriter;

import java.io.*;

public class StagesJsonParser {

    private String appURL;
    private String appDir;


    public StagesJsonParser() {}

    public StagesJsonParser(String appURL, String appDir) {
        this.appURL = appURL;
        this.appDir = appDir;
    }

    public void saveStagesJson(Application app) {
        // http://masterIP:18080/api/v1/applications/app-20170618202557-0295/stages
        String stagesURL = appURL + "/stages";

        // "profiles/WordCount-CMS-4-28_app-20170618202557-0295/stages.json"
        String stagesJsonFile = appDir + File.separatorChar + "stages.json";

        String stagesJson = HtmlFetcher.fetch(stagesURL);
        FileTextWriter.write(stagesJsonFile, stagesJson);

        parseStagesJson(stagesJson, app);
    }

    public void parseStagesJson(String stagesJson, Application app) {

        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(stagesJson);
            JsonArray stageJsonArray = null;

            if (el.isJsonArray())
                stageJsonArray = el.getAsJsonArray();
            else {
                System.err.println("Error in parsing the stages json!");
                System.exit(1);
            }

            for (JsonElement stageElem : stageJsonArray) {
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
    }

}