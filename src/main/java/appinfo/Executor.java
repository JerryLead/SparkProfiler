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

    private boolean hasFailedTasks = false;

    private GCMetrics gcMetrics = new GCMetrics();

    public Executor(JsonObject executorJson) {
        id = executorJson.get("id").getAsString();
        hostPort = executorJson.get("hostPort").getAsString();
        isActive = executorJson.get("isActive").getAsBoolean();
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

        if (failedTasks > 0)
            hasFailedTasks = true;
    }


    public void addGCMetric(String[] metrics) {
        String name = metrics[0];
        String value = metrics[1].replace(",", "").trim();
        String unit = metrics[2];

        if (!value.equals("n.a."))
            gcMetrics.set(name, value.replace(",", ""));
        else
            gcMetrics.set(name, "-1");



    }

    public String getId() {
        return id;
    }

    public String getHostPort() {
        return hostPort;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getRddBlocks() {
        return rddBlocks;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public long getDiskUsed() {
        return diskUsed;
    }

    public int getTotalCores() {
        return totalCores;
    }

    public int getMaxTasks() {
        return maxTasks;
    }

    public int getActiveTasks() {
        return activeTasks;
    }

    public int getFailedTasks() {
        return failedTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public long getTotalGCTime() {
        return totalGCTime;
    }

    public long getTotalInputBytes() {
        return totalInputBytes;
    }

    public long getTotalShuffleRead() {
        return totalShuffleRead;
    }

    public long getTotalShuffleWrite() {
        return totalShuffleWrite;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public GCMetrics getGcMetrics() {
        return gcMetrics;
    }
}
