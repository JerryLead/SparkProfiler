package appinfo;


/*
  "id" : "2",
  "hostPort" : "172.26.80.236:40498",
  "isActive" : true,
  "rddBlocks" : 0,
  "memoryUsed" : 0,
  "diskUsed" : 0,
  "totalCores" : 4,
  "maxTasks" : 4,
  "activeTasks" : 0,
  "failedTasks" : 0,
  "completedTasks" : 28,
  "totalTasks" : 28,
  "totalDuration" : 284608,
  "totalGCTime" : 11757,
  "totalInputBytes" : 3222798336,
  "totalShuffleRead" : 62090,
  "totalShuffleWrite" : 63259,
  "maxMemory" : 17808280780,
  "executorLogs" : {
    "stdout" : "http://172.26.80.236:8081/logPage/?appId=app-20170618202557-0295&executorId=2&logType=stdout",
    "stderr" : "http://172.26.80.236:8081/logPage/?appId=app-20170618202557-0295&executorId=2&logType=stderr"
  }
 */

import com.google.gson.JsonObject;

public class Executor {

    private String id; // n or driver
    private String hostPort;
    private boolean isActive;
    private int rddBlocks;
    private long memoryUsed;
    private long diskUsed;
    private int totalCores;
    private int maxTasks;
    private int activeTasks;
    private int failedTasks;
    private int completedTasks;
    private int totalTasks;
    private long totalDuration;
    private long totalGCTime;
    private long totalInputBytes;
    private long totalShuffleRead;
    private long totalShuffleWrite;
    private long maxMemory;

    private String executorLogs_stdout;
    private String executorLogs_stderr;

    public Executor(JsonObject executorJson) {
        id = executorJson.getAsJsonObject("id").getAsString();
        hostPort = executorJson.getAsJsonObject("hostPort").getAsString();
        rddBlocks = executorJson.getAsJsonObject("rddBlocks").getAsInt();
        memoryUsed = executorJson.getAsJsonObject("memoryUsed").getAsLong();
        diskUsed = executorJson.getAsJsonObject("diskUsed").getAsLong();
        totalCores = executorJson.getAsJsonObject("totalCores").getAsInt();
        maxTasks = executorJson.getAsJsonObject("maxTasks").getAsInt();
        activeTasks = executorJson.getAsJsonObject("activeTasks").getAsInt();
        failedTasks = executorJson.getAsJsonObject("failedTasks").getAsInt();
        completedTasks = executorJson.getAsJsonObject("completedTasks").getAsInt();
        totalTasks = executorJson.getAsJsonObject("totalTasks").getAsInt();
        totalDuration = executorJson.getAsJsonObject("totalDuration").getAsLong();
        totalGCTime = executorJson.getAsJsonObject("totalGCTime").getAsLong();
        totalInputBytes = executorJson.getAsJsonObject("totalInputBytes").getAsLong();
        totalShuffleRead = executorJson.getAsJsonObject("totalShuffleRead").getAsLong();
        totalShuffleWrite = executorJson.getAsJsonObject("totalShuffleWrite").getAsLong();
        maxMemory = executorJson.getAsJsonObject("maxMemory").getAsLong();

        executorLogs_stdout = executorJson.getAsJsonObject("executorLogs").getAsJsonObject("stdout").getAsString();
        executorLogs_stderr = executorJson.getAsJsonObject("executorLogs").getAsJsonObject("stderr").getAsString();
    }





}
