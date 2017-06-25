package appinfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
}

class StageAttempt {


    private String status;
    private int attemptId;
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

    public StageAttempt(JsonObject stageAttemptObject) {
        parseStageAttempt(stageAttemptObject);
    }

    private void parseStageAttempt(JsonObject stageAttemptObject) {
        status = stageAttemptObject.get("status").getAsString();
        attemptId = stageAttemptObject.get("attemptId").getAsInt();

        if (status.equals("COMPLETE")) {
            submissionTime = stageAttemptObject.get("submissionTime").getAsString();
            firstTaskLaunchedTime = stageAttemptObject.get("firstTaskLaunchedTime").getAsString();
            completionTime = stageAttemptObject.get("completionTime").getAsString();
        }

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

    public int getAttemptId() {
        return attemptId;
    }

    public String getStatus() {
        return status;
    }
}