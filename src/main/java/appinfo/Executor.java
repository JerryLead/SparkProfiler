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

import java.util.ArrayList;
import java.util.List;

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

    private double maxCPUusage = 0;
    private double maxMemoryUsage = 0; // GB

    private boolean hasFailedTasks = false;

    private GCMetrics gcMetrics = new GCMetrics();

    private GCeasyMetrics gCeasyMetrics = new GCeasyMetrics();

    private List<TopMetrics> topMetricsList = new ArrayList<TopMetrics>();

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

    public void addTopMetrics(List<String> topMetricsLines) {
        for (String line : topMetricsLines) {
            String time = line.substring(1, line.indexOf(']'));
            double cpu = Double.parseDouble(line.substring(line.indexOf('=') + 2, line.indexOf(',')));
            double memory = Double.parseDouble(line.substring(line.lastIndexOf('=') + 2));
            TopMetrics topMetrics = new TopMetrics(time, cpu, memory);
            topMetricsList.add(topMetrics);

            if (cpu > maxCPUusage)
                maxCPUusage = cpu;
            if (memory > maxMemoryUsage)
                maxMemoryUsage = memory;
        }
    }

    public void addGCeasyMetric(String json) {
        gCeasyMetrics.parseJson(json);
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

    public GCeasyMetrics getgCeasyMetrics() {
        return gCeasyMetrics;
    }

    public List<TopMetrics> getTopMetricsList() {
        return topMetricsList;
    }

    public double getMaxMemoryUsage() {
        return maxMemoryUsage;
    }

    public double getMaxCPUusage() {
        return maxCPUusage;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("[executor.maxCPUUsage] " + maxCPUusage + "\n");
        sb.append("[executor.maxMemoryUsage] " + maxMemoryUsage + "\n");

        sb.append("[executor.rddBlocks] " + rddBlocks + "\n");
        sb.append("[executor.memoryUsed] " + memoryUsed + "\n");
        sb.append("[executor.diskUsed] " + diskUsed + "\n");
        sb.append("[executor.totalCores] " + totalCores + "\n");
        sb.append("[executor.maxTasks] " + maxTasks + "\n");
        sb.append("[executor.activeTasks] " + activeTasks + "\n");
        sb.append("[executor.failedTasks] " + failedTasks + "\n");
        sb.append("[executor.completedTasks] " + completedTasks + "\n");
        sb.append("[executor.totalTasks] " + totalTasks + "\n");
        sb.append("[executor.totalDuration] " + totalDuration + "\n");
        sb.append("[executor.totalGCTime] " + totalGCTime + "\n");
        sb.append("[executor.totalInputBytes] " + totalInputBytes + "\n");
        sb.append("[executor.totalShuffleRead] " + totalShuffleRead + "\n");
        sb.append("[executor.totalShuffleWrite] " + totalShuffleWrite + "\n");
        sb.append("[executor.maxMemory] " + maxMemory + "\n");

        sb.append("\n");
        sb.append("[executor.gc.footprint] " + gcMetrics.getFootprint() + "\n");
        sb.append("[executor.gc.avgfootprintAfterFullGC] " + gcMetrics.getAvgfootprintAfterFullGC() + "\n");
        sb.append("[executor.gc.freedMemoryByFullGC] " +  gcMetrics.getFreedMemoryByFullGC() + "\n");
        sb.append("[executor.gc.freedMemoryByFullGCpc] " +  gcMetrics.getFreedMemoryByFullGCpc() + "\n");
        sb.append("[executor.gc.avgFreedMemoryByFullGC] " +  gcMetrics.getAvgFreedMemoryByFullGC() + "\n");
        sb.append("[executor.gc.slopeAfterFullGC] " +  gcMetrics.getSlopeAfterFullGC() + "\n");
        sb.append("[executor.gc.avgRelativePostFullGCInc] " +  gcMetrics.getAvgRelativePostFullGCInc() + "\n");
        sb.append("[executor.gc.avgfootprintAfterGC] " +  gcMetrics.getAvgfootprintAfterGC() + "\n");
        sb.append("[executor.gc.slopeAfterGC] " +  gcMetrics.getSlopeAfterGC() + "\n");
        sb.append("[executor.gc.avgRelativePostGCInc] " +  gcMetrics.getAvgRelativePostGCInc() + "\n");
        sb.append("[executor.gc.freedMemoryByGC] " +  gcMetrics.getFreedMemoryByGC() + "\n");
        sb.append("[executor.gc.freedMemoryByGCpc] " +  gcMetrics.getFreedMemoryByGCpc() + "\n");
        sb.append("[executor.gc.avgFreedMemoryByGC] " +  gcMetrics.getAvgFreedMemoryByGC() + "\n");
        sb.append("[executor.gc.avgPause] " +  gcMetrics.getAvgPause() + "\n");
        sb.append("[executor.gc.avgPauseσ] " +  gcMetrics.getAvgPauseσ() + "\n");
        sb.append("[executor.gc.minPause] " +  gcMetrics.getMinPause() + "\n");
        sb.append("[executor.gc.maxPause] " +  gcMetrics.getMaxPause() + "\n");
        sb.append("[executor.gc.avgGCPause] " +  gcMetrics.getAvgGCPause() + "\n");
        sb.append("[executor.gc.avgGCPauseσ] " +  gcMetrics.getAvgGCPauseσ() + "\n");
        sb.append("[executor.gc.avgFullGCPause] " +  gcMetrics.getAvgFullGCPause() + "\n");
        sb.append("[executor.gc.avgFullGCPauseσ] " +  gcMetrics.getAvgFullGCPauseσ() + "\n");
        sb.append("[executor.gc.minFullGCPause] " +  gcMetrics.getMinFullGCPause() + "\n");
        sb.append("[executor.gc.maxFullGCPause] " +  gcMetrics.getMaxFullGCPause() + "\n");
        sb.append("[executor.gc.accumPause] " +  gcMetrics.getAccumPause() + "\n");
        sb.append("[executor.gc.fullGCPause] " +  gcMetrics.getFullGCPause() + "\n");
        sb.append("[executor.gc.fullGCPausePc] " +  gcMetrics.getFullGCPausePc() + "\n");
        sb.append("[executor.gc.gcPause] " +  gcMetrics.getGcPause() + "\n");
        sb.append("[executor.gc.gcPausePc] " +  gcMetrics.getGcPausePc() + "\n");
        sb.append("[executor.gc.freedMemory] " +  gcMetrics.getFreedMemory() + "\n");
        sb.append("[executor.gc.throughput] " +  gcMetrics.getThroughput() + "\n");
        sb.append("[executor.gc.totalTime] " +  gcMetrics.getTotalTime() + "\n");
        sb.append("[executor.gc.freedMemoryPerMin] " + gcMetrics.getFreedMemoryPerMin() + "\n");
        sb.append("[executor.gc.gcPerformance] " +  gcMetrics.getGcPerformance() + "\n");
        sb.append("[executor.gc.fullGCPerformance] " +  gcMetrics.getFullGCPerformance() + "\n");

        sb.append("\n");
        sb.append("[gceasy.jvmHeapSize_youngGen_allocatedSize] " + gCeasyMetrics.getJvmHeapSize_youngGen_allocatedSize() + "\n");
        sb.append("[gceasy.jvmHeapSize_youngGen_peakSize] " + gCeasyMetrics.getJvmHeapSize_youngGen_peakSize() + "\n");
        sb.append("[gceasy.jvmHeapSize_oldGen_allocatedSize] " + gCeasyMetrics.getJvmHeapSize_oldGen_allocatedSize() + "\n");
        sb.append("[gceasy.jvmHeapSize_oldGen_peakSize] " + gCeasyMetrics.getJvmHeapSize_oldGen_peakSize() + "\n");
        sb.append("[gceasy.jvmHeapSize_metaSpace_allocatedSize] " + gCeasyMetrics.getJvmHeapSize_metaSpace_allocatedSize() + "\n");
        sb.append("[gceasy.jvmHeapSize_metaSpace_peakSize] " + gCeasyMetrics.getJvmHeapSize_metaSpace_peakSize() + "\n");
        sb.append("[gceasy.jvmHeapSize_total_allocatedSize] " + gCeasyMetrics.getJvmHeapSize_total_allocatedSize() + "\n");
        sb.append("[gceasy.jvmHeapSize_total_peakSize] " + gCeasyMetrics.getJvmHeapSize_total_peakSize() + "\n");
        sb.append("[gceasy.gcStatistics_totalCreatedBytes] " + gCeasyMetrics.getGcStatistics_totalCreatedBytes() + "\n");
        sb.append("[gceasy.gcStatistics_measurementDuration] " + gCeasyMetrics.getGcStatistics_measurementDuration() + "\n");
        sb.append("[gceasy.gcStatistics_avgAllocationRate] " + gCeasyMetrics.getGcStatistics_avgAllocationRate() + "\n");
        sb.append("[gceasy.gcStatistics_avgPromotionRate] " + gCeasyMetrics.getGcStatistics_avgPromotionRate() + "\n");
        sb.append("[gceasy.gcStatistics_minorGCCount] " + gCeasyMetrics.getGcStatistics_minorGCCount() + "\n");
        sb.append("[gceasy.gcStatistics_minorGCTotalTime] " + gCeasyMetrics.getGcStatistics_minorGCTotalTime() + "\n");
        sb.append("[gceasy.gcStatistics_minorGCAvgTime] " + gCeasyMetrics.getGcStatistics_minorGCAvgTime() + "\n");
        sb.append("[gceasy.gcStatistics_minorGCAvgTimeStdDeviation] " + gCeasyMetrics.getGcStatistics_minorGCAvgTimeStdDeviation() + "\n");
        sb.append("[gceasy.gcStatistics_minorGCMinTIme] " + gCeasyMetrics.getGcStatistics_minorGCMinTIme() + "\n");
        sb.append("[gceasy.gcStatistics_minorGCMaxTime] " + gCeasyMetrics.getGcStatistics_minorGCMaxTime() + "\n");
        sb.append("[gceasy.gcStatistics_minorGCIntervalAvgTime] " + gCeasyMetrics.getGcStatistics_minorGCIntervalAvgTime() + "\n");
        sb.append("[gceasy.gcStatistics_fullGCCount] " + gCeasyMetrics.getGcStatistics_fullGCCount() + "\n");
        sb.append("[gceasy.gcStatistics_fullGCTotalTime] " + gCeasyMetrics.getGcStatistics_fullGCTotalTime() + "\n");
        sb.append("[gceasy.gcStatistics_fullGCAvgTime] " + gCeasyMetrics.getGcStatistics_fullGCAvgTime() + "\n");
        sb.append("[gceasy.gcStatistics_fullGCAvgTimeStdDeviation] " + gCeasyMetrics.getGcStatistics_fullGCAvgTimeStdDeviation() + "\n");
        sb.append("[gceasy.gcStatistics_fullGCMinTIme] " + gCeasyMetrics.getGcStatistics_fullGCMinTIme() + "\n");
        sb.append("[gceasy.gcStatistics_fullGCMaxTime] " + gCeasyMetrics.getGcStatistics_fullGCMaxTime() + "\n");
        sb.append("[gceasy.gcStatistics_fullGCIntervalAvgTime] " + gCeasyMetrics.getGcStatistics_fullGCIntervalAvgTime() + "\n");
        sb.append("[gceasy.throughputPercentage] " + gCeasyMetrics.getThroughputPercentage() + "\n");

        sb.append("[gceasy.gcCause] " + gCeasyMetrics.getGcCauses() + "\n");
        sb.append("[gceasy.gcDurationSummary] " + gCeasyMetrics.getGcDurationSummary_groups() + "\n");
        sb.append("[gceasy.heapTuningTips] " + gCeasyMetrics.getHeapTuningTips() + "\n");
        sb.append("[gceasy.problem] " + gCeasyMetrics.getProblem() + "\n");
        sb.append("[gceasy.tipsToReduceGCTime] " + gCeasyMetrics.getTipsToReduceGCTime() + "\n");
        return sb.toString();
    }


}
