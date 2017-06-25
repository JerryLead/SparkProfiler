package parser;

import appinfo.Application;
import appinfo.Stage;
import com.google.gson.*;
import util.HtmlFetcher;
import util.HtmlJsonWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StagesJsonParser {

    private String appURL;
    private String appDir;
    private Application app;

    public StagesJsonParser(String appURL, String appDir, Application app) {
        this.appURL = appURL;
        this.appDir = appDir;
        this.app = app;
    }

    public void saveStagesJson() {
        // http://masterIP:18080/api/v1/applications/app-20170618202557-0295/stages
        String stagesURL = appURL + "/stages";

        // "profiles/WordCount-CMS-4-28_app-20170618202557-0295/stages.json"
        String stagesJsonFile = appDir + File.separatorChar + "stages.json";

        String stagesJson = HtmlFetcher.fetch(stagesURL);
        HtmlJsonWriter.write(stagesJsonFile, stagesJson);

        parseStagesJson(stagesJson);
    }

    private void parseStagesJson(String stagesJson) {

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