package appinfo;

import com.google.gson.JsonObject;

/**
 * Created by xulijie on 17-7-3.
 */

public class TaskAttempt {

    private int index;
    private int attempt;
    private String launchTime;
    private int executorId;
    private String host;
    private String taskLocality;
    private boolean speculative;
    // accumulatorUpdates
    private long duration;

    private long executorDeserializeTime;
    private long executorDeserializeCpuTime;
    private long executorRunTime;
    private long executorCpuTime;
    private long resultSize;
    private long jvmGcTime;
    private long resultSerializationTime;
    private long memoryBytesSpilled;
    private long diskBytesSpilled;

    private long inputMetrics_bytesRead;
    private long inputMetrics_recordsRead;

    private long outputMetrics_bytesWritten;
    private long outputMetrics_recordsWritten;

    private long shuffleReadMetrics_remoteBlocksFetched;
    private long shuffleReadMetrics_localBlocksFetched;
    private long shuffleReadMetrics_fetchWaitTime;
    private long shuffleReadMetrics_remoteBytesRead;
    private long shuffleReadMetrics_localBytesRead;
    private long shuffleReadMetrics_recordsRead;

    private long shuffleWriteMetrics_bytesWritten;
    private long shuffleWriteMetrics_writeTime;
    private long shuffleWriteMetrics_recordsWritten;

    private String errorMessage;


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
        if (taskAttemptObject.has("errorMessage"))
            this.errorMessage = taskAttemptObject.get("errorMessage").getAsString();

        JsonObject taskMetricsObj = taskAttemptObject.getAsJsonObject("taskMetrics");

        this.executorDeserializeTime = taskMetricsObj.get("executorDeserializeTime").getAsLong();
        this.executorDeserializeCpuTime = taskMetricsObj.get("executorDeserializeCpuTime").getAsLong();
        this.executorRunTime = taskMetricsObj.get("executorRunTime").getAsLong();
        this.executorCpuTime = taskMetricsObj.get("executorCpuTime").getAsLong();
        this.resultSize = taskMetricsObj.get("resultSize").getAsLong();
        this.jvmGcTime = taskMetricsObj.get("jvmGcTime").getAsLong();
        this.resultSerializationTime = taskMetricsObj.get("resultSerializationTime").getAsLong();
        this.memoryBytesSpilled = taskMetricsObj.get("memoryBytesSpilled").getAsLong();
        this.diskBytesSpilled = taskMetricsObj.get("diskBytesSpilled").getAsLong();

        JsonObject inputMetricsObj = taskMetricsObj.getAsJsonObject("inputMetrics");
        this.inputMetrics_bytesRead = inputMetricsObj.get("bytesRead").getAsLong();
        this.inputMetrics_recordsRead = inputMetricsObj.get("recordsRead").getAsLong();


        JsonObject outputMetricsObj = taskMetricsObj.getAsJsonObject("outputMetrics");

        this.outputMetrics_bytesWritten = outputMetricsObj.get("bytesWritten").getAsLong();
        this.outputMetrics_recordsWritten = outputMetricsObj.get("recordsWritten").getAsLong();


        JsonObject shuffleReadMetricsObj = taskMetricsObj.getAsJsonObject("shuffleReadMetrics");

        this.shuffleReadMetrics_remoteBlocksFetched = shuffleReadMetricsObj.get("remoteBlocksFetched").getAsLong();
        this.shuffleReadMetrics_localBlocksFetched = shuffleReadMetricsObj.get("localBlocksFetched").getAsLong();
        this.shuffleReadMetrics_fetchWaitTime = shuffleReadMetricsObj.get("fetchWaitTime").getAsLong();
        this.shuffleReadMetrics_remoteBytesRead = shuffleReadMetricsObj.get("remoteBytesRead").getAsLong();
        this.shuffleReadMetrics_localBytesRead = shuffleReadMetricsObj.get("localBytesRead").getAsLong();
        this.shuffleReadMetrics_recordsRead = shuffleReadMetricsObj.get("recordsRead").getAsLong();

        JsonObject shuffleWriteMetricsObj = taskMetricsObj.getAsJsonObject("shuffleWriteMetrics");

        this.shuffleWriteMetrics_bytesWritten = shuffleWriteMetricsObj.get("bytesWritten").getAsLong();
        this.shuffleWriteMetrics_writeTime = shuffleWriteMetricsObj.get("writeTime").getAsLong();
        this.shuffleWriteMetrics_recordsWritten = shuffleWriteMetricsObj.get("recordsWritten").getAsLong();

        this.duration = executorRunTime;
    }

    public int getIndex() {
        return index;
    }

    public int getAttempt() {
        return attempt;
    }

    public String getLaunchTime() {
        return launchTime;
    }

    public int getExecutorId() {
        return executorId;
    }

    public String getHost() {
        return host;
    }

    public String getTaskLocality() {
        return taskLocality;
    }

    public boolean isSpeculative() {
        return speculative;
    }

    public long getExecutorDeserializeTime() {
        return executorDeserializeTime;
    }

    public long getExecutorDeserializeCpuTime() {
        return executorDeserializeCpuTime;
    }

    public long getExecutorRunTime() {
        return executorRunTime;
    }

    public long getExecutorCpuTime() {
        return executorCpuTime;
    }

    public long getResultSize() {
        return resultSize;
    }

    public long getJvmGcTime() {
        return jvmGcTime;
    }

    public long getResultSerializationTime() {
        return resultSerializationTime;
    }

    public long getMemoryBytesSpilled() {
        return memoryBytesSpilled;
    }

    public long getDiskBytesSpilled() {
        return diskBytesSpilled;
    }

    public long getInputMetrics_bytesRead() {
        return inputMetrics_bytesRead;
    }

    public long getInputMetrics_recordsRead() {
        return inputMetrics_recordsRead;
    }

    public long getOutputMetrics_bytesWritten() {
        return outputMetrics_bytesWritten;
    }

    public long getOutputMetrics_recordsWritten() {
        return outputMetrics_recordsWritten;
    }

    public long getShuffleReadMetrics_remoteBlocksFetched() {
        return shuffleReadMetrics_remoteBlocksFetched;
    }

    public long getShuffleReadMetrics_localBlocksFetched() {
        return shuffleReadMetrics_localBlocksFetched;
    }

    public long getShuffleReadMetrics_fetchWaitTime() {
        return shuffleReadMetrics_fetchWaitTime;
    }

    public long getShuffleReadMetrics_remoteBytesRead() {
        return shuffleReadMetrics_remoteBytesRead;
    }

    public long getShuffleReadMetrics_localBytesRead() {
        return shuffleReadMetrics_localBytesRead;
    }

    public long getShuffleReadMetrics_recordsRead() {
        return shuffleReadMetrics_recordsRead;
    }

    public long getShuffleWriteMetrics_bytesWritten() {
        return shuffleWriteMetrics_bytesWritten;
    }

    public long getShuffleWriteMetrics_writeTime() {
        return shuffleWriteMetrics_writeTime;
    }

    public long getShuffleWriteMetrics_recordsWritten() {
        return shuffleWriteMetrics_recordsWritten;
    }

    public long getDuration() {
        return duration;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}