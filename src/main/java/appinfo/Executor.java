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

    private GCMetrics gcMetrics = new GCMetrics();

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


    public void addGCMetric(String[] metrics) {
        String name = metrics[0];
        String value = metrics[1];
        String unit = metrics[2];

        gcMetrics.set(name, value);
    }
}

/**
 gcLogFile; stdout; -
 footprint; 15,850; M
 avgfootprintAfterFullGC; 3,702.314; M
 avgfootprintAfterFullGCσ; 3,198.801; M
 avgfootprintAfterFullGCisSig; false; bool
 freedMemoryByFullGC; 1,014.641; M
 freedMemoryByFullGCpc; 0.0; %
 avgFreedMemoryByFullGC; 12.683; M/coll
 avgFreedMemoryByFullGCσ; 12.591; M/coll
 avgFreedMemoryByFullGCisSig; false; bool
 slopeAfterFullGC; 71.764; M/s
 avgRelativePostFullGCInc; 15.81; M/coll
 avgfootprintAfterGC; 6,325.308; M
 avgfootprintAfterGCσ; 3,704.091; M
 avgfootprintAfterGCisSig; false; bool
 slopeAfterGC; 41.902; M/s
 avgRelativePostGCInc; 47.386; M/coll
 freedMemoryByGC; 2,087,900.694; M
 freedMemoryByGCpc; 100.0; %
 avgFreedMemoryByGC; 1,935.033; M/coll
 avgFreedMemoryByGCσ; 1,083.774; M/coll
 avgFreedMemoryByGCisSig; false; bool
 freedMemory; 2,088,915.335; M
 avgPauseIsSig; false; bool
 avgPause; 0.5608; s
 avgPauseσ; 2.05657; s
 minPause; 0.00269; s
 maxPause; 18.61921; s
 avgGCPauseIsSig; false; bool
 avgGCPause; 0.16431; s
 avgGCPauseσ; 0.32184; s
 avgFullGCPauseIsSig; false; bool
 avgFullGCPause; 5.90843; s
 avgFullGCPauseσ; 5.42927; s
 minFullGCPause; 0.02228; s
 maxFullGCPause; 18.61921; s
 accumPause; 649.97; s
 fullGCPause; 472.67; s
 fullGCPausePc; 72.7; %
 gcPause; 177.3; s
 gcPausePc; 27.3; %
 accumPause; 649.97; s
 footprint; 15,850; M
 freedMemory; 2,088,915.335; M
 throughput; -136.27; %
 totalTime; 275; s
 freedMemoryPerMin; 455,608.948; M/min
 gcPerformance; 11,776.4; M/s
 fullGCPerformance; 2,198.114; K/s
 */

class GCMetrics {

    private int footprint; // 15,850 M
    private double avgfootprintAfterFullGC; // 3,702.314 M
    private double freedMemoryByFullGC; // 1,014.641 M
    private float freedMemoryByFullGCpc; // 0.0 %
    private double avgFreedMemoryByFullGC; // 12.683 M/coll
    private double slopeAfterFullGC; // 71.764 M/s
    private double avgRelativePostFullGCInc; // 15.81 M/coll
    private double avgfootprintAfterGC; // 6,325.308 M
    private double slopeAfterGC; // 41.902 M/s
    private double avgRelativePostGCInc; // 47.386 M/coll
    private double freedMemoryByGC; // 2,087,900.694 M
    private float freedMemoryByGCpc; // 100.0 %
    private double avgFreedMemoryByGC; // 1,935.033 M/coll
    private double avgPause; // 0.5608 s
    private double avgPauseσ; // 2.05657 s
    private double minPause; // 0.00269 s
    private double maxPause; // 18.61921 s
    private double avgGCPause; // 0.16431 s
    private double avgGCPauseσ; // 0.32184 s
    private double avgFullGCPause; // 5.90843 s
    private double avgFullGCPauseσ; // 5.42927 s
    private double minFullGCPause; // 0.02228 s
    private double maxFullGCPause; // 18.61921 s
    private double accumPause; // 649.97 s
    private double fullGCPause; // 472.67 s
    private double fullGCPausePc; // 72.7 %
    private double gcPause; // 177.3 s
    private double gcPausePc; // 27.3 %
    private double freedMemory; // 2,088,915.335 M
    private float throughput; // -136.27 %
    private long totalTime; // 275 s
    private double freedMemoryPerMin; // 455,608.948 M/min
    private double gcPerformance; // 11,776.4 M/s
    private double fullGCPerformance; // 2,198.114 K/s


    public void set(String name, String value) {
        if (name.equals("footprint"))
            footprint = Integer.parseInt(value);
        else if (name.equals("avgfootprintAfterFullGC"))
            avgfootprintAfterFullGC = Double.parseDouble(value);
        else if (name.equals("freedMemoryByFullGC"))
            freedMemoryByFullGC = Double.parseDouble(value);

        else if (name.equals("freedMemoryByFullGCpc"))
            freedMemoryByFullGCpc = Float.parseFloat(value);

        else if (name.equals("avgFreedMemoryByFullGC"))
            avgFreedMemoryByFullGC = Double.parseDouble(value);
        else if (name.equals("slopeAfterFullGC"))
            slopeAfterFullGC = Double.parseDouble(value);
        else if (name.equals("avgRelativePostFullGCInc"))
            avgRelativePostFullGCInc = Double.parseDouble(value);
        else if (name.equals("avgfootprintAfterGC"))
            avgfootprintAfterGC = Double.parseDouble(value);

        else if (name.equals("slopeAfterGC"))
            slopeAfterGC = Double.parseDouble(value);
        else if (name.equals("avgRelativePostGCInc"))
            avgRelativePostGCInc = Double.parseDouble(value);
        else if (name.equals("freedMemoryByGC"))
            freedMemoryByGC = Double.parseDouble(value);
        else if (name.equals("freedMemoryByGCpc"))
            freedMemoryByGCpc = Float.parseFloat(value);

        else if (name.equals("avgFreedMemoryByGC"))
            avgFreedMemoryByGC = Double.parseDouble(value);
        else if (name.equals("avgPause"))
            avgPause = Double.parseDouble(value);
        else if (name.equals("avgPauseσ"))
            avgPauseσ = Double.parseDouble(value);
        else if (name.equals("minPause"))
            minPause = Double.parseDouble(value);
        else if (name.equals("maxPause"))
            maxPause = Double.parseDouble(value);
        else if (name.equals("avgGCPause"))
            avgGCPause = Double.parseDouble(value);
        else if (name.equals("avgGCPauseσ"))
            avgGCPauseσ = Double.parseDouble(value);

        else if (name.equals("avgFullGCPause"))
            avgFullGCPause = Double.parseDouble(value);
        else if (name.equals("avgFullGCPauseσ"))
            avgFullGCPauseσ = Double.parseDouble(value);
        else if (name.equals("minFullGCPause"))
            minFullGCPause = Double.parseDouble(value);
        else if (name.equals("maxFullGCPause"))
            maxFullGCPause = Double.parseDouble(value);
        else if (name.equals("accumPause"))
            accumPause = Double.parseDouble(value);
        else if (name.equals("fullGCPause"))
            fullGCPause = Double.parseDouble(value);
        else if (name.equals("fullGCPausePc"))
            fullGCPausePc = Double.parseDouble(value);
        else if (name.equals("gcPause"))
            gcPause = Double.parseDouble(value);
        else if (name.equals("gcPausePc"))
            gcPausePc = Double.parseDouble(value);
        else if (name.equals("freedMemory"))
            freedMemory = Double.parseDouble(value);
        else if (name.equals("throughput"))
            throughput = Float.parseFloat(value);

        else if (name.equals("totalTime"))
            totalTime = Long.parseLong(value);
        else if (name.equals("freedMemoryPerMin"))
            freedMemoryPerMin = Double.parseDouble(value);
        else if (name.equals("freedMemoryPerMin"))
            freedMemoryPerMin = Double.parseDouble(value);
        else if (name.equals("fullGCPerformance"))
            fullGCPerformance = Double.parseDouble(value);

    }
}