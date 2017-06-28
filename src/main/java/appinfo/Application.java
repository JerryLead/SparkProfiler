package appinfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Application {

    // private String JobID;
    private String appId;
    private String name;
    private List<AppAttempt> attemptList = new ArrayList<AppAttempt>();

    private Map<Integer, Job> jobMap = new TreeMap<Integer, Job>();
    private Map<Integer, Stage> stageMap = new TreeMap<Integer, Stage>();
    private List<Executor> executors = new ArrayList<Executor>();

    /*
    {
        "id" : "app-20170623115533-0014",
            "name" : "RDDJoin-G1-4-28G-0.5",
            "attempts" : [ {
                "startTime" : "2017-06-23T03:55:32.099GMT",
                "endTime" : "2017-06-23T07:55:21.408GMT",
                "lastUpdated" : "2017-06-23T07:55:21.459GMT",
                "duration" : 14389309,
                "sparkUser" : "root",
                "completed" : true,
                "startTimeEpoch" : 1498190132099,
                "lastUpdatedEpoch" : 1498204521459,
                "endTimeEpoch" : 1498204521408
    } ]
    }
    */

    public Application(String json) {
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(json);

        if (el.isJsonObject())
            parse(el.getAsJsonObject());
        else {
            System.err.println("Error in parsing the app json html!");
            System.exit(1);
        }
    }

    private void parse(JsonObject appObject) {
        appId = appObject.get("id").getAsString();
        name = appObject.get("name").getAsString();
        JsonArray attempts = appObject.get("attempts").getAsJsonArray();

        for (JsonElement attemptElem : attempts) {
            AppAttempt attempt = new AppAttempt(attemptElem.getAsJsonObject());
            attemptList.add(attempt);
            if (attemptList.size() > 1) {
                System.err.println("WARNING: This application has more than one application attempts!!!");
            }
        }
    }

    public String getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public void addJob(Job job) {
        int jobId = job.getJobId();
        jobMap.put(jobId, job);
    }

    public void addStage(JsonObject stageObject) {
        int stageId = stageObject.get("stageId").getAsInt();

        if (stageMap.containsKey(stageId)) {
            Stage stage = stageMap.get(stageId);
            stage.addStageAttempt(stageObject);
        } else {
            Stage stage = new Stage(stageId);
            stage.addStageAttempt(stageObject);
            stageMap.put(stageId, stage);
        }
    }

    public Map<Integer, Job> getJobMap() {
        return jobMap;
    }

    public Map<Integer, Stage> getStageMap() {
        return stageMap;
    }

    public Stage getStage(int stageId) {
        return stageMap.get(stageId);
    }

    public void addExecutor(Executor executor) {
        executors.add(executor);
    }

    public Executor getExecutor(int executorId) {
        return executors.get(executorId);
    }
}

class AppAttempt {
    private String startTime;
    private String endTime;
    private String lastUpdated;
    private long duration; // ms
    private String completed;

    private long startTimeEpoch;
    private long lastUpdatedEpoch;
    private long endTimeEpoch;


    public AppAttempt(JsonObject attemptObj) {
        startTime = attemptObj.getAsJsonObject().get("startTime").getAsString();
        endTime = attemptObj.getAsJsonObject().get("endTime").getAsString();
        lastUpdated = attemptObj.getAsJsonObject().get("lastUpdated").getAsString();
        duration = attemptObj.getAsJsonObject().get("duration").getAsLong();
        completed = attemptObj.getAsJsonObject().get("completed").getAsString();
        startTimeEpoch = attemptObj.getAsJsonObject().get("startTimeEpoch").getAsLong();
        lastUpdatedEpoch = attemptObj.getAsJsonObject().get("lastUpdatedEpoch").getAsLong();
        endTimeEpoch = attemptObj.getAsJsonObject().get("endTimeEpoch").getAsLong();
    }
}