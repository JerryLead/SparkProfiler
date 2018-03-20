package appinfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

public class Application {
    private String appId;
    private String name;


    private Map<Integer, Job> jobMap = new TreeMap<Integer, Job>();
    private Map<Integer, Stage> stageMap = new TreeMap<Integer, Stage>();
    private Map<String, Executor> executorMap = new TreeMap<String, Executor>();

    private String startTime;
    private String endTime;
    private String lastUpdated;
    private long duration = 0; // ms
    // private boolean completed = false;
    private long startTimeEpoch;
    private long lastUpdatedEpoch;
    private long endTimeEpoch;

    private String status;

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
            System.err.println(json);
            System.exit(1);
        }
    }

    private void parse(JsonObject appObject) {
        appId = appObject.get("id").getAsString();
        name = appObject.get("name").getAsString();
        JsonArray attempts = appObject.get("attempts").getAsJsonArray();

        if (attempts.size() == 1)
            initAppAttempt(attempts.get(0).getAsJsonObject());
        else if (attempts.size() > 0)
            System.err.println(name + "_" + appId + "has multiple application attempts!");
        else
            System.err.println(name + "_" + appId + "does not have any application attempts!");
    }

    public void initAppAttempt(JsonObject attemptObj) {
        startTime = attemptObj.getAsJsonObject().get("startTime").getAsString();
        endTime = attemptObj.getAsJsonObject().get("endTime").getAsString();
        lastUpdated = attemptObj.getAsJsonObject().get("lastUpdated").getAsString();
        duration = attemptObj.getAsJsonObject().get("duration").getAsLong();
        // completed = attemptObj.getAsJsonObject().get("completed").getAsBoolean();
        startTimeEpoch = attemptObj.getAsJsonObject().get("startTimeEpoch").getAsLong();
        lastUpdatedEpoch = attemptObj.getAsJsonObject().get("lastUpdatedEpoch").getAsLong();
        endTimeEpoch = attemptObj.getAsJsonObject().get("endTimeEpoch").getAsLong();
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

        String jobStatus = job.getStatus();
        if (status == null)
            status = jobStatus;
        else if (status.equalsIgnoreCase("SUCCEEDED"))
            status = jobStatus;
        else if (status.equalsIgnoreCase("FINISHED")) {
            if (!jobStatus.equalsIgnoreCase("SUCCEEDED"))
                status = jobStatus;
        }
    }

    public void addStage(JsonObject stageObject) {
        int stageId = stageObject.get("stageId").getAsInt();

        if (stageMap.containsKey(stageId)) {
            Stage stage = stageMap.get(stageId);
            stage.addStageAttempt(stageObject);
        } else {
            Stage stage = new Stage(stageId, appId, name);
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
        if (stageMap.containsKey(stageId))
            return stageMap.get(stageId);
        else {
            System.err.println("[Error] " + appId + " does not have stage " + stageId);
            return null;
        }
    }

    public void addExecutor(Executor executor) {
        if (executor.getId().equals("driver") == false)
            executorMap.put(executor.getId(), executor);
    }

    public Executor getExecutor(String executorId) {
        return executorMap.get(executorId);
    }

    public long getDuration() {
        return duration;
    }

    public List<Executor> getExecutors() {
        return new ArrayList<Executor>(executorMap.values());
    }

    public String getStatus() {
        return status;
    }

    public String getTaskInfosInStage(Set<Integer> stageIds) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Stage> stageEntry : stageMap.entrySet()) {
            if (stageIds.contains(stageEntry.getKey())) {
                Stage selectedStage = stageEntry.getValue();
                StageAttempt stageAttempt = selectedStage.getCompletedStage();
                sb.append("[stageId = " + selectedStage.getStageId() + "]\n");
                for (Task task : stageAttempt.getTaskMap().values()) {
                    TaskAttempt taskAttempt = task.getFirstCompletedTask();
                    sb.append(taskAttempt + "\n");
                }
            }
        }

        return sb.toString();
    }

    public double getMaxCPUUsage() {
        double cpuUsage = 0;
        for(Executor executor : executorMap.values()) {
            double cpu = executor.getMaxCPUusage();
            if (cpu > cpuUsage)
                cpuUsage = cpu;
        }
        return cpuUsage;
    }

    // GB
    public double getMaxMemoryUsage() {
        double memoryUsage = 0;
        for(Executor executor : executorMap.values()) {
            double memory = Math.max(executor.getMaxMemoryUsage(), executor.getgCeasyMetrics().getJvmHeapSize_total_allocatedSize() / 1024);
            if (memory > memoryUsage)
                memoryUsage = memory;
        }
        return memoryUsage;
    }

    public double getMaxAllocatedMemory() {
        double memoryUsage = 0;
        for(Executor executor : executorMap.values()) {
            double memory = executor.getgCeasyMetrics().getJvmHeapSize_total_allocatedSize();
            if (memory > memoryUsage)
                memoryUsage = memory;
        }
        return memoryUsage;
    }

    public double getHeapPeakUsage() {
        double memoryUsage = 0;
        for(Executor executor : executorMap.values()) {
            double memory = executor.getgCeasyMetrics().getJvmHeapSize_total_peakSize();
            if (memory > memoryUsage)
                memoryUsage = memory;
        }
        return memoryUsage;
    }
}
