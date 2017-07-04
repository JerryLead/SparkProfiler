package appinfo;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/*
{
  "status" : "COMPLETE",
  "stageId" : 0,
  "attemptId" : 9,
  "numActiveTasks" : 0,
  "numCompleteTasks" : 6,
  "numFailedTasks" : 0,
  "executorRunTime" : 96944,
  "executorCpuTime" : 50190625794,
  "submissionTime" : "2017-06-22T10:41:45.611GMT",
  "firstTaskLaunchedTime" : "2017-06-22T10:41:53.195GMT",
  "completionTime" : "2017-06-22T10:42:24.194GMT",
  "inputBytes" : 805699584,
  "inputRecords" : 6191346,
  "outputBytes" : 0,
  "outputRecords" : 0,
  "shuffleReadBytes" : 0,
  "shuffleReadRecords" : 0,
  "shuffleWriteBytes" : 770542166,
  "shuffleWriteRecords" : 6191346,
  "memoryBytesSpilled" : 0,
  "diskBytesSpilled" : 0,
  "name" : "map at RDDJoinTest.scala:61",
  "details" : "org.apache.spark.rdd.RDD.map(RDD.scala:369)\napplications.sql.rdd.RDDJoinTest$.main(RDDJoinTest.scala:61)\napplications.sql.rdd.RDDJoinTest.main(RDDJoinTest.scala)\nsun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\nsun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\nsun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\njava.lang.reflect.Method.invoke(Method.java:498)\norg.apache.spark.deploy.SparkSubmit$.org$apache$spark$deploy$SparkSubmit$$runMain(SparkSubmit.scala:743)\norg.apache.spark.deploy.SparkSubmit$.doRunMain$1(SparkSubmit.scala:187)\norg.apache.spark.deploy.SparkSubmit$.submit(SparkSubmit.scala:212)\norg.apache.spark.deploy.SparkSubmit$.main(SparkSubmit.scala:126)\norg.apache.spark.deploy.SparkSubmit.main(SparkSubmit.scala)",
  "schedulingPool" : "default",
  "accumulatorUpdates" : [ {
    "id" : 10132,
    "name" : "internal.metrics.resultSize",
    "value" : "14247"
  }, {
    "id" : 10149,
    "name" : "internal.metrics.input.recordsRead",
    "value" : "6191346"
  }, {
    "id" : 10131,
    "name" : "internal.metrics.executorCpuTime",
    "value" : "50190625794"
  }, {
    "id" : 10134,
    "name" : "internal.metrics.resultSerializationTime",
    "value" : "5"
  }, {
    "id" : 10146,
    "name" : "internal.metrics.shuffle.write.recordsWritten",
    "value" : "6191346"
  }, {
    "id" : 10128,
    "name" : "internal.metrics.executorDeserializeTime",
    "value" : "156"
  }, {
    "id" : 10145,
    "name" : "internal.metrics.shuffle.write.bytesWritten",
    "value" : "770542166"
  }, {
    "id" : 10148,
    "name" : "internal.metrics.input.bytesRead",
    "value" : "805699584"
  }, {
    "id" : 10130,
    "name" : "internal.metrics.executorRunTime",
    "value" : "96944"
  }, {
    "id" : 10133,
    "name" : "internal.metrics.jvmGCTime",
    "value" : "2769"
  }, {
    "id" : 10147,
    "name" : "internal.metrics.shuffle.write.writeTime",
    "value" : "27463776768"
  }, {
    "id" : 10129,
    "name" : "internal.metrics.executorDeserializeCpuTime",
    "value" : "71185751"
  } ]
},
 */

public class Stage {

    private int stageId;
    private Map<Integer, StageAttempt> stageAttemptMap = new TreeMap<Integer, StageAttempt>();

    public Stage(int stageId) {
        this.stageId = stageId;
    }

    public void addStageAttempt(JsonObject stageObject) {
        // stageId = stageObject.get("stageId").getAsInt();
        StageAttempt stageAttempt = new StageAttempt(stageObject);
        // Note that the attemptId may not be consistent with the array index.
        stageAttemptMap.put(stageAttempt.getAttemptId(), stageAttempt);
    }

    public Set<Integer> getStageAttemptIds() {
        return stageAttemptMap.keySet();
    }

    public String getStageAttemptStatus(int stageAttemptId) {
        return stageAttemptMap.get(stageAttemptId).getStatus();
    }


    public void addTask(int stageAttemptId, JsonObject taskObject) {
        StageAttempt stageAttempt = stageAttemptMap.get(stageAttemptId);
        stageAttempt.addTask(taskObject);
    }

    public void addTaskSummary(int stageAttemptId, JsonObject taskSummaryJsonObject) {
        StageAttempt stageAttempt = stageAttemptMap.get(stageAttemptId);
        stageAttempt.addTaskSummary(taskSummaryJsonObject);
    }

    public StageAttempt getCompletedStage() {
        for (StageAttempt stageAttempt : stageAttemptMap.values()) {
            if (stageAttempt.getStatus().equals("COMPLETE"))
                return stageAttempt;
        }

        return null;
    }

    public int getStageId() {
        return stageId;
    }
}
