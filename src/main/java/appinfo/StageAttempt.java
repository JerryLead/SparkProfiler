package appinfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import util.DateParser;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by xulijie on 17-7-3.
 */

public class StageAttempt {

    private String appId;
    private String appName;
    private int stageId;

    private String status;
    private int attemptId;
    private String name;
    private long duration = 0L;
    private int numActiveTasks;
    private int numCompleteTasks;
    private int numFailedTasks;
    private long executorRunTime;
    private long executorCpuTime;
    private String submissionTime;
    private String firstTaskLaunchedTime;
    private String completionTime;
    private long inputBytes;
    private long inputRecords;
    private long outputBytes;
    private long outputRecords;
    private long shuffleReadBytes;
    private long shuffleReadRecords;
    private long shuffleWriteBytes;
    private long shuffleWriteRecords;
    private long memoryBytesSpilled;
    private long diskBytesSpilled;

    // internal.metrics.*
    private long metrics_resultSize;
    private long metrics_input_recordsRead;
    private long metrics_executorCpuTime;
    private long metrics_resultSerializationTime;
    private long metrics_shuffle_write_recordsWritten;
    private long metrics_executorDeserializeTime;
    private long metrics_shuffle_write_bytesWritten;
    private long metrics_input_bytesRead;
    private long metrics_executorRunTime;
    private long metrics_jvmGCTime;
    private long metrics_shuffle_write_writeTime;
    private long metrics_executorDeserializeCpuTime;


    private Map<Integer, Task> taskMap = new TreeMap<Integer, Task>();
    private TaskSummary taskSummary;

    public StageAttempt(String appId, String appName, int stageId, JsonObject stageAttemptObject) {
        this.appId = appId;
        this.appName = appName;
        this.stageId = stageId;
        parseStageAttempt(stageAttemptObject);
    }

    private void parseStageAttempt(JsonObject stageAttemptObject) {
        status = stageAttemptObject.get("status").getAsString();
        attemptId = stageAttemptObject.get("attemptId").getAsInt();

        if (status.equals("COMPLETE")) {
            if (stageAttemptObject.has("submissionTime"))
                submissionTime = stageAttemptObject.get("submissionTime").getAsString();
            if (stageAttemptObject.has("firstTaskLaunchedTime"))
                firstTaskLaunchedTime = stageAttemptObject.get("firstTaskLaunchedTime").getAsString();
            if (stageAttemptObject.has("completionTime"))
                completionTime = stageAttemptObject.get("completionTime").getAsString();
            if (stageAttemptObject.has("submissionTime") && stageAttemptObject.has("completionTime"))
                duration = DateParser.durationMS(firstTaskLaunchedTime, completionTime);
        }

        name = stageAttemptObject.get("name").getAsString();

        numActiveTasks = stageAttemptObject.get("numActiveTasks").getAsInt();
        numCompleteTasks = stageAttemptObject.get("numCompleteTasks").getAsInt();
        numFailedTasks = stageAttemptObject.get("numFailedTasks").getAsInt();
        executorRunTime = stageAttemptObject.get("executorRunTime").getAsLong();
        executorCpuTime = stageAttemptObject.get("executorCpuTime").getAsLong();

        inputBytes = stageAttemptObject.get("inputBytes").getAsLong();
        inputRecords = stageAttemptObject.get("inputRecords").getAsLong();
        outputBytes = stageAttemptObject.get("outputBytes").getAsLong();
        outputRecords = stageAttemptObject.get("outputRecords").getAsLong();
        shuffleReadBytes = stageAttemptObject.get("shuffleReadBytes").getAsLong();
        shuffleReadRecords = stageAttemptObject.get("shuffleReadRecords").getAsLong();
        shuffleWriteBytes = stageAttemptObject.get("shuffleWriteBytes").getAsLong();
        shuffleWriteRecords = stageAttemptObject.get("shuffleWriteRecords").getAsLong();
        memoryBytesSpilled = stageAttemptObject.get("memoryBytesSpilled").getAsLong();
        diskBytesSpilled = stageAttemptObject.get("diskBytesSpilled").getAsLong();


        JsonArray accumulatorUpdatesArray = stageAttemptObject.get("accumulatorUpdates").getAsJsonArray();
        for (JsonElement elem : accumulatorUpdatesArray) {
            String metrics = elem.getAsJsonObject().get("name").getAsString();
            long value = elem.getAsJsonObject().get("value").getAsLong();
            if(metrics.endsWith("resultSize"))
                metrics_resultSize = value;
            else if(metrics.endsWith("recordsRead"))
                metrics_input_recordsRead = value;
            else if(metrics.endsWith("executorCpuTime"))
                metrics_executorCpuTime = value;
            else if(metrics.endsWith("resultSerializationTime"))
                metrics_resultSerializationTime = value;
            else if(metrics.endsWith("recordsWritten"))
                metrics_shuffle_write_recordsWritten = value;
            else if(metrics.endsWith("executorDeserializeTime"))
                metrics_executorDeserializeTime = value;
            else if(metrics.endsWith("bytesWritten"))
                metrics_shuffle_write_bytesWritten = value;
            else if(metrics.endsWith("bytesRead"))
                metrics_input_bytesRead = value;
            else if(metrics.endsWith("executorRunTime"))
                metrics_executorRunTime = value;
            else if(metrics.endsWith("jvmGCTime"))
                metrics_jvmGCTime = value;
            else if(metrics.endsWith("writeTime"))
                metrics_shuffle_write_writeTime = value;
            else if(metrics.endsWith("executorDeserializeCpuTime"))
                metrics_executorDeserializeCpuTime = value;
        }
    }

    public void addTask(JsonObject taskObject) {
        int taskId = taskObject.get("taskId").getAsInt();

        if (taskMap.containsKey(taskId)) {
            Task task = taskMap.get(taskId);
            task.addTaskAttempt(taskObject);
        } else {
            Task task = new Task(appId, appName, stageId, taskId);
            task.addTaskAttempt(taskObject);
            taskMap.put(taskId, task);
        }
    }

    public int getAttemptId() {
        return attemptId;
    }

    public String getStatus() {
        return status;
    }

    public void addTaskSummary(JsonObject taskSummaryJsonObject) {
        this.taskSummary = new TaskSummary(taskSummaryJsonObject);

    }

    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public long getDuration() {
        return duration;
    }

    public int getNumActiveTasks() {
        return numActiveTasks;
    }

    public int getNumCompleteTasks() {
        return numCompleteTasks;
    }

    public int getNumFailedTasks() {
        return numFailedTasks;
    }

    public long getExecutorRunTime() {
        return executorRunTime;
    }

    public long getExecutorCpuTime() {
        return executorCpuTime;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }

    public String getFirstTaskLaunchedTime() {
        return firstTaskLaunchedTime;
    }

    public String getCompletionTime() {
        return completionTime;
    }

    public long getInputBytes() {
        return inputBytes;
    }

    public long getInputRecords() {
        return inputRecords;
    }

    public long getOutputBytes() {
        return outputBytes;
    }

    public long getOutputRecords() {
        return outputRecords;
    }

    public long getShuffleReadBytes() {
        return shuffleReadBytes;
    }

    public long getShuffleReadRecords() {
        return shuffleReadRecords;
    }

    public long getShuffleWriteBytes() {
        return shuffleWriteBytes;
    }

    public long getShuffleWriteRecords() {
        return shuffleWriteRecords;
    }

    public long getMemoryBytesSpilled() {
        return memoryBytesSpilled;
    }

    public long getDiskBytesSpilled() {
        return diskBytesSpilled;
    }

    public long getMetrics_resultSize() {
        return metrics_resultSize;
    }

    public long getMetrics_input_recordsRead() {
        return metrics_input_recordsRead;
    }

    public long getMetrics_executorCpuTime() {
        return metrics_executorCpuTime;
    }

    public long getMetrics_resultSerializationTime() {
        return metrics_resultSerializationTime;
    }

    public long getMetrics_shuffle_write_recordsWritten() {
        return metrics_shuffle_write_recordsWritten;
    }

    public long getMetrics_executorDeserializeTime() {
        return metrics_executorDeserializeTime;
    }

    public long getMetrics_shuffle_write_bytesWritten() {
        return metrics_shuffle_write_bytesWritten;
    }

    public long getMetrics_input_bytesRead() {
        return metrics_input_bytesRead;
    }

    public long getMetrics_executorRunTime() {
        return metrics_executorRunTime;
    }

    public long getMetrics_jvmGCTime() {
        return metrics_jvmGCTime;
    }

    public long getMetrics_shuffle_write_writeTime() {
        return metrics_shuffle_write_writeTime;
    }

    public long getMetrics_executorDeserializeCpuTime() {
        return metrics_executorDeserializeCpuTime;
    }

    public String getName() {
        return name;
    }
}
