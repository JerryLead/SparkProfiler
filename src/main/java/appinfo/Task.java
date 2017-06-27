package appinfo;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.TreeMap;


/*
   {
       "taskId" : 0,
       "index" : 0,
       "attempt" : 0,
       "launchTime" : "2017-06-18T12:25:59.382GMT",
       "executorId" : "6",
       "host" : "172.26.80.237",
       "taskLocality" : "ANY",
       "speculative" : false,
       "accumulatorUpdates" : [ ],
       "taskMetrics" : {
               "executorDeserializeTime" : 682,
               "executorDeserializeCpuTime" : 66354869,
               "executorRunTime" : 14908,
               "executorCpuTime" : 10840279294,
               "resultSize" : 2572,
               "jvmGcTime" : 572,
               "resultSerializationTime" : 0,
               "memoryBytesSpilled" : 0,
               "diskBytesSpilled" : 0,
               "inputMetrics" : {
                   "bytesRead" : 134283264,
                   "recordsRead" : 4956261
               },
               "outputMetrics" : {
                   "bytesWritten" : 0,
                   "recordsWritten" : 0
               },
               "shuffleReadMetrics" : {
                   "remoteBlocksFetched" : 0,
                   "localBlocksFetched" : 0,
                   "fetchWaitTime" : 0,
                   "remoteBytesRead" : 0,
                   "localBytesRead" : 0,
                   "recordsRead" : 0
               },
               "shuffleWriteMetrics" : {
                   "bytesWritten" : 2636,
                   "writeTime" : 452601,
                   "recordsWritten" : 101
               }
           }
   }
   */

public class Task {

    private int taskId;

    private Map<Integer, TaskAttempt> taskAttemptMap = new TreeMap<Integer, TaskAttempt>();

    public Task(int taskId) {
        this.taskId = taskId;
    }

    public void addTaskAttempt(JsonObject taskObject) {

        TaskAttempt taskAttempt = new TaskAttempt(taskObject);
        // Note that the attemptId may not be consistent with the array index.
        taskAttemptMap.put(taskAttempt.getTaskAttemptId(), taskAttempt);
    }
}


class TaskAttempt {

    private int index;
    private int attempt;
    private String launchTime;
    private int executorId;
    private String host;
    private String taskLocality;
    private boolean speculative;
    // accumulatorUpdates

    private long taskMetrics_executorDeserializeTime;
    private long taskMetrics_executorDeserializeCpuTime;
    private long taskMetrics_executorRunTime;
    private long taskMetrics_executorCpuTime;
    private long taskMetrics_resultSize;
    private long taskMetrics_jvmGcTime;
    private long taskMetrics_resultSerializationTime;
    private long taskMetrics_memoryBytesSpilled;
    private long taskMetrics_diskBytesSpilled;

    private long taskMetrics_inputMetrics_bytesRead;
    private long taskMetrics_inputMetrics_recordsRead;

    private long taskMetrics_outputMetrics_bytesWritten;
    private long taskMetrics_outputMetrics_recordsWritten;

    private long taskMetrics_shuffleReadMetrics_remoteBlocksFetched;
    private long taskMetrics_shuffleReadMetrics_localBlocksFetched;
    private long taskMetrics_shuffleReadMetrics_fetchWaitTime;
    private long taskMetrics_shuffleReadMetrics_remoteBytesRead;
    private long taskMetrics_shuffleReadMetrics_localBytesRead;
    private long taskMetrics_shuffleReadMetrics_recordsRead;

    private long taskMetrics_shuffleWriteMetrics_bytesWritten;
    private long taskMetrics_shuffleWriteMetrics_writeTime;
    private long taskMetrics_shuffleWriteMetrics_recordsWritten;


    public TaskAttempt(JsonObject taskAttemptObject) {
        parseTaskAttempt(taskAttemptObject);
    }

    public int getTaskAttemptId() {
        return attempt;
    }

    public void parseTaskAttempt(JsonObject taskAttemptObject) {
        this.index = taskAttemptObject.get("index").getAsInt();
        this.attempt = taskAttemptObject.get("attempt").getAsInt();
        this.launchTime = taskAttemptObject.get("launchTime").getAsString();
        this.executorId = taskAttemptObject.get("executorId").getAsInt();
        this.host = taskAttemptObject.get("host").getAsString();
        this.taskLocality = taskAttemptObject.get("taskLocality").getAsString();
        this.speculative = taskAttemptObject.get("speculative").getAsBoolean();
        // accumulatorUpdates

        JsonObject taskMetricsObj = taskAttemptObject.getAsJsonObject("taskMetrics");

        this.taskMetrics_executorDeserializeTime = taskMetricsObj.get("executorDeserializeTime").getAsLong();
        this.taskMetrics_executorDeserializeCpuTime = taskMetricsObj.get("executorDeserializeCpuTime").getAsLong();
        this.taskMetrics_executorRunTime = taskMetricsObj.get("executorRunTime").getAsLong();
        this.taskMetrics_executorCpuTime = taskMetricsObj.get("executorCpuTime").getAsLong();
        this.taskMetrics_resultSize = taskMetricsObj.get("resultSize").getAsLong();
        this.taskMetrics_jvmGcTime = taskMetricsObj.get("jvmGcTime").getAsLong();
        this.taskMetrics_resultSerializationTime = taskMetricsObj.get("resultSerializationTime").getAsLong();
        this.taskMetrics_memoryBytesSpilled = taskMetricsObj.get("memoryBytesSpilled").getAsLong();
        this.taskMetrics_diskBytesSpilled = taskMetricsObj.get("diskBytesSpilled").getAsLong();

        JsonObject inputMetricsObj = taskMetricsObj.getAsJsonObject("inputMetrics");
        this.taskMetrics_inputMetrics_bytesRead = inputMetricsObj.get("bytesRead").getAsLong();
        this.taskMetrics_inputMetrics_recordsRead = inputMetricsObj.get("recordsRead").getAsLong();


        JsonObject outputMetricsObj = taskMetricsObj.getAsJsonObject("outputMetrics");

        this.taskMetrics_outputMetrics_bytesWritten = outputMetricsObj.get("bytesWritten").getAsLong();
        this.taskMetrics_outputMetrics_recordsWritten = outputMetricsObj.get("recordsWritten").getAsLong();


        JsonObject shuffleReadMetricsObj = taskMetricsObj.getAsJsonObject("shuffleReadMetrics");

        this.taskMetrics_shuffleReadMetrics_remoteBlocksFetched = shuffleReadMetricsObj.get("remoteBlocksFetched").getAsLong();
        this.taskMetrics_shuffleReadMetrics_localBlocksFetched = shuffleReadMetricsObj.get("localBlocksFetched").getAsLong();
        this.taskMetrics_shuffleReadMetrics_fetchWaitTime = shuffleReadMetricsObj.get("fetchWaitTime").getAsLong();
        this.taskMetrics_shuffleReadMetrics_remoteBytesRead = shuffleReadMetricsObj.get("remoteBytesRead").getAsLong();
        this.taskMetrics_shuffleReadMetrics_localBytesRead = shuffleReadMetricsObj.get("localBytesRead").getAsLong();
        this.taskMetrics_shuffleReadMetrics_recordsRead = shuffleReadMetricsObj.get("recordsRead").getAsLong();

        JsonObject shuffleWriteMetricsObj = taskMetricsObj.getAsJsonObject("shuffleWriteMetrics");

        this.taskMetrics_shuffleWriteMetrics_bytesWritten = shuffleWriteMetricsObj.get("bytesWritten").getAsLong();
        this.taskMetrics_shuffleWriteMetrics_writeTime = shuffleWriteMetricsObj.get("writeTime").getAsLong();
        this.taskMetrics_shuffleWriteMetrics_recordsWritten = shuffleWriteMetricsObj.get("recordsWritten").getAsLong();
    }
}