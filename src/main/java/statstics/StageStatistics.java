package statstics;

import appinfo.Stage;
import appinfo.StageAttempt;
import appinfo.Task;
import util.Statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulijie on 17-7-3.
 */
public class StageStatistics {

    private int stageId = -1;

    private Statistics duration;

    private Statistics executorRunTime;
    private Statistics executorCpuTime;

    private Statistics inputBytes;
    private Statistics inputRecords;
    private Statistics outputBytes;
    private Statistics outputRecords;
    private Statistics shuffleReadBytes;
    private Statistics shuffleReadRecords;
    private Statistics shuffleWriteBytes;
    private Statistics shuffleWriteRecords;
    private Statistics memoryBytesSpilled;
    private Statistics diskBytesSpilled;

    // internal.metrics.*
    private Statistics resultSize;
    private Statistics resultSerializationTime;
    private Statistics executorDeserializeTime;
    private Statistics jvmGCTime;
    private Statistics shuffle_write_writeTime;
    private Statistics executorDeserializeCpuTime;


    private TaskStatistics taskStatistics;

    // In general, we run each application 5 times, so the length of stageWithSameId is 5
    public StageStatistics(List<Stage> stagesWithSameId) {
        List<StageAttempt> stageAttempts = new ArrayList<StageAttempt>();

        for (Stage stage : stagesWithSameId) {
            StageAttempt stageAttempt = stage.getCompletedStage();
            if (stageAttempt != null)
                stageAttempts.add(stageAttempt);
            else
                System.err.println("Stage " + stage.getStageId() + " does not have completed stage attempt");

            if (stageId == -1)
                stageId = stage.getStageId();
        }

        computeStatistics(stageAttempts);
        computeTaskStatistics(stageAttempts);
    }

    // tasks in stage x from app1, tasks in stage x from app2, tasks in stage x from app3
    private void computeTaskStatistics(List<StageAttempt> stageAttempts) {
        List<Task> tasksInSameStage = new ArrayList<Task>();

        for(StageAttempt stageAttempt : stageAttempts)
            tasksInSameStage.addAll(stageAttempt.getTaskMap().values());

        taskStatistics = new TaskStatistics(tasksInSameStage, stageId);
    }

    private void computeStatistics(List<StageAttempt> stageAttempts) {

        Object[] stageAttemptObjs = stageAttempts.toArray();

        duration = new Statistics(stageAttemptObjs, "getDuration");

        executorRunTime = new Statistics(stageAttemptObjs, "getExecutorRunTime");
        executorCpuTime = new Statistics(stageAttemptObjs, "getExecutorCpuTime");

        inputBytes = new Statistics(stageAttemptObjs, "getInputBytes");
        inputRecords = new Statistics(stageAttemptObjs, "getInputRecords");
        outputBytes = new Statistics(stageAttemptObjs, "getOutputBytes");
        outputRecords = new Statistics(stageAttemptObjs, "getOutputRecords");
        shuffleReadBytes = new Statistics(stageAttemptObjs, "getShuffleReadBytes");
        shuffleReadRecords = new Statistics(stageAttemptObjs, "getShuffleReadRecords");
        shuffleWriteBytes = new Statistics(stageAttemptObjs, "getShuffleWriteBytes");
        shuffleWriteRecords = new Statistics(stageAttemptObjs, "getShuffleWriteRecords");
        memoryBytesSpilled = new Statistics(stageAttemptObjs, "getMemoryBytesSpilled");
        diskBytesSpilled = new Statistics(stageAttemptObjs, "getDiskBytesSpilled");

        // internal.metrics.*
        resultSize = new Statistics(stageAttemptObjs, "getMetrics_resultSize");
        resultSerializationTime = new Statistics(stageAttemptObjs, "getMetrics_resultSerializationTime");
        executorDeserializeTime = new Statistics(stageAttemptObjs, "getMetrics_executorDeserializeTime");
        jvmGCTime = new Statistics(stageAttemptObjs, "getMetrics_jvmGCTime");
        shuffle_write_writeTime = new Statistics(stageAttemptObjs, "getMetrics_shuffle_write_writeTime");
        executorDeserializeCpuTime = new Statistics(stageAttemptObjs, "getMetrics_executorDeserializeCpuTime");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String prefix = "stage" + stageId;

        sb.append("[" + prefix + ".duration] " + duration + "\n");
        sb.append("[" + prefix + ".inputBytes] " + inputBytes + "\n");
        sb.append("[" + prefix + ".inputRecords] " + inputRecords + "\n");
        sb.append("[" + prefix + ".outputBytes] " + outputBytes + "\n");
        sb.append("[" + prefix + ".outputRecords] " + outputRecords + "\n");
        sb.append("[" + prefix + ".shuffleReadBytes] " + shuffleReadBytes + "\n");
        sb.append("[" + prefix + ".shuffleReadRecords] " + shuffleReadRecords + "\n");
        sb.append("[" + prefix + ".shuffleWriteBytes] " + shuffleWriteBytes + "\n");
        sb.append("[" + prefix + ".shuffleWriteRecords] " + shuffleWriteRecords + "\n");
        sb.append("[" + prefix + ".memoryBytesSpilled] " + memoryBytesSpilled + "\n");
        sb.append("[" + prefix + ".diskBytesSpilled] " + diskBytesSpilled + "\n");

        sb.append("[" + prefix + ".resultSize] " + resultSize + "\n");
        sb.append("[" + prefix + ".resultSerializationTime] " + resultSerializationTime + "\n");
        sb.append("[" + prefix + ".executorDeserializeTime] " + executorDeserializeTime + "\n");
        sb.append("[" + prefix + ".jvmGCTime] " + jvmGCTime + "\n");
        sb.append("[" + prefix + ".shuffle_write_writeTime] " + shuffle_write_writeTime + "\n");
        sb.append("[" + prefix + ".executorDeserializeCpuTime] " + executorDeserializeCpuTime + "\n");

        sb.append(taskStatistics);

        return sb.toString();
    }
}
