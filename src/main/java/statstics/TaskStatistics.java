package statstics;

import appinfo.Task;
import appinfo.TaskAttempt;
import util.Statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulijie on 17-7-3.
 */

public class TaskStatistics {

    private int taskId;

    private Statistics duration;

    private Statistics executorDeserializeTime;
    private Statistics executorDeserializeCpuTime;
    private Statistics executorRunTime;
    private Statistics executorCpuTime;
    private Statistics resultSize;
    private Statistics jvmGcTime;
    private Statistics resultSerializationTime;
    private Statistics memoryBytesSpilled;
    private Statistics diskBytesSpilled;

    private Statistics inputMetrics_bytesRead;
    private Statistics inputMetrics_recordsRead;

    private Statistics outputMetrics_bytesWritten;
    private Statistics outputMetrics_recordsWritten;

    private Statistics shuffleReadMetrics_remoteBlocksFetched;
    private Statistics shuffleReadMetrics_localBlocksFetched;
    private Statistics shuffleReadMetrics_fetchWaitTime;
    private Statistics shuffleReadMetrics_remoteBytesRead;
    private Statistics shuffleReadMetrics_localBytesRead;
    private Statistics shuffleReadMetrics_recordsRead;

    private Statistics shuffleWriteMetrics_bytesWritten;
    private Statistics shuffleWriteMetrics_writeTime;
    private Statistics shuffleWriteMetrics_recordsWritten;


    // In general, we run each application 5 times, and each stage has multiple tasks.
    // The length of tasksInSameStage depends on how many stages are calculated.

    public TaskStatistics(List<Task> tasksInSameStage) {
        List<TaskAttempt> taskAttempts = new ArrayList<TaskAttempt>();

        for (Task task : tasksInSameStage) {
            TaskAttempt taskAttempt = task.getCompletedTask();
            if (taskAttempt != null)
                taskAttempts.add(taskAttempt);
            else
                System.err.println("Stage " + task.getTaskId() + " does not have completed stage attempt");

        }

        computeStatistics(taskAttempts);
    }

    private void computeStatistics(List<TaskAttempt> taskAttempts) {
        Object[] taskAttemptObjs = taskAttempts.toArray();

        duration = new Statistics(taskAttemptObjs, "getDuration");
        executorDeserializeTime = new Statistics(taskAttemptObjs, "getExecutorDeserializeTime");
        executorDeserializeCpuTime = new Statistics(taskAttemptObjs, "getExecutorDeserializeCpuTime");
        executorRunTime = new Statistics(taskAttemptObjs, "getExecutorRunTime");
        executorCpuTime = new Statistics(taskAttemptObjs, "getExecutorCpuTime");
        resultSize = new Statistics(taskAttemptObjs, "getResultSize");
        jvmGcTime = new Statistics(taskAttemptObjs, "getJvmGcTime");
        resultSerializationTime = new Statistics(taskAttemptObjs, "getResultSerializationTime");
        memoryBytesSpilled = new Statistics(taskAttemptObjs, "getMemoryBytesSpilled");
        diskBytesSpilled = new Statistics(taskAttemptObjs, "getDiskBytesSpilled");

        inputMetrics_bytesRead = new Statistics(taskAttemptObjs, "getInputMetrics_bytesRead");
        inputMetrics_recordsRead = new Statistics(taskAttemptObjs, "getInputMetrics_recordsRead");

        outputMetrics_bytesWritten = new Statistics(taskAttemptObjs, "getOutputMetrics_bytesWritten");
        outputMetrics_recordsWritten = new Statistics(taskAttemptObjs, "getOutputMetrics_recordsWritten");

        shuffleReadMetrics_remoteBlocksFetched = new Statistics(taskAttemptObjs, "getShuffleReadMetrics_remoteBlocksFetched");
        shuffleReadMetrics_localBlocksFetched = new Statistics(taskAttemptObjs, "getShuffleReadMetrics_localBlocksFetched");
        shuffleReadMetrics_fetchWaitTime = new Statistics(taskAttemptObjs, "getShuffleReadMetrics_fetchWaitTime");;
        shuffleReadMetrics_remoteBytesRead = new Statistics(taskAttemptObjs, "getShuffleReadMetrics_remoteBytesRead");
        shuffleReadMetrics_localBytesRead = new Statistics(taskAttemptObjs, "getShuffleReadMetrics_localBytesRead");
        shuffleReadMetrics_recordsRead = new Statistics(taskAttemptObjs, "getShuffleReadMetrics_recordsRead");

        shuffleWriteMetrics_bytesWritten = new Statistics(taskAttemptObjs, "getShuffleWriteMetrics_bytesWritten");
        shuffleWriteMetrics_writeTime = new Statistics(taskAttemptObjs, "getShuffleWriteMetrics_writeTime");
        shuffleWriteMetrics_recordsWritten = new Statistics(taskAttemptObjs, "getShuffleWriteMetrics_recordsWritten");
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("[task.duration] " + duration + "\n");
        sb.append("[task.executorDeserializeTime] " + executorDeserializeTime + "\n");
        sb.append("[task.executorDeserializeCpuTime] " + executorDeserializeCpuTime + "\n");
        sb.append("[task.executorRunTime] " + executorRunTime + "\n");
        sb.append("[task.executorCpuTime] " + executorCpuTime + "\n");
        sb.append("[task.resultSize] " + resultSize + "\n");
        sb.append("[task.jvmGcTime] " + jvmGcTime + "\n");
        sb.append("[task.resultSerializationTime] " + resultSerializationTime + "\n");
        sb.append("[task.memoryBytesSpilled] " + memoryBytesSpilled + "\n");
        sb.append("[task.diskBytesSpilled] " + diskBytesSpilled + "\n");
        sb.append("[task.inputMetrics.bytesRead] " + inputMetrics_bytesRead + "\n");
        sb.append("[task.inputMetrics.recordsRead] " + inputMetrics_recordsRead + "\n");
        sb.append("[task.outputMetrics.bytesWritten] " + outputMetrics_bytesWritten + "\n");
        sb.append("[task.outputMetrics.recordsWritten] " + outputMetrics_recordsWritten + "\n");
        sb.append("[task.shuffleReadMetrics.remoteBlocksFetched] " + shuffleReadMetrics_remoteBlocksFetched + "\n");
        sb.append("[task.shuffleReadMetrics.localBlocksFetched] " + shuffleReadMetrics_localBlocksFetched + "\n");
        sb.append("[task.shuffleReadMetrics.fetchWaitTime] " + shuffleReadMetrics_fetchWaitTime + "\n");
        sb.append("[task.shuffleReadMetrics.remoteBytesRead] " + shuffleReadMetrics_remoteBytesRead + "\n");
        sb.append("[task.shuffleReadMetrics.localBytesRead] " + shuffleReadMetrics_localBytesRead + "\n");
        sb.append("[task.shuffleReadMetrics.recordsRead] " + shuffleReadMetrics_recordsRead + "\n");
        sb.append("[task.shuffleWriteMetrics.bytesWritten] " + shuffleWriteMetrics_bytesWritten + "\n");
        sb.append("[task.shuffleWriteMetrics.writeTime] " + shuffleWriteMetrics_writeTime + "\n");
        sb.append("[task.shuffleWriteMetrics.recordsWritten] " + shuffleWriteMetrics_recordsWritten + "\n");

        return sb.toString();
    }
}
