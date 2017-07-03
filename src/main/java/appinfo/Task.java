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

    public TaskAttempt getCompletedTask() {
        for (TaskAttempt taskAttempt : taskAttemptMap.values()) {
            if (taskAttempt.getErrorMessage() == null)
                return taskAttempt;
        }

        return null;
    }

    public int getTaskId() {
        return taskId;
    }
}

