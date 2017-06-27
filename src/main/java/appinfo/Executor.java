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

    public Executor(JsonObject executorJson) {
        id = executorJson.get("id").getAsString();
        hostPort = executorJson.get("hostPort").getAsString();
        rddBlocks = executorJson.get("rddBlocks").getAsInt();
        memoryUsed = executorJson.get("memoryUsed").getAsLong();
        diskUsed = executorJson.get("diskUsed").getAsLong();
        totalCores = executorJson.get("totalCores").getAsInt();
        maxTasks = executorJson.get("maxTasks").getAsInt();
        activeTasks = executorJson.get("activeTasks").getAsInt();
        failedTasks = executorJson.get("failedTasks").getAsInt();
        completedTasks = executorJson.get("completedTasks").getAsInt();
        totalTasks = executorJson.get("totalTasks").getAsInt();
        totalDuration = executorJson.get("totalDuration").getAsLong();
        totalGCTime = executorJson.get("totalGCTime").getAsLong();
        totalInputBytes = executorJson.get("totalInputBytes").getAsLong();
        totalShuffleRead = executorJson.get("totalShuffleRead").getAsLong();
        totalShuffleWrite = executorJson.get("totalShuffleWrite").getAsLong();
        maxMemory = executorJson.get("maxMemory").getAsLong();
    }





}
