package statstics;

import appinfo.Executor;
import util.Statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulijie on 17-7-3.
 */

public class ExecutorStatistics {

    private Statistics rddBlocks;
    private Statistics memoryUsed;
    private Statistics diskUsed;
    private Statistics totalCores;
    private Statistics maxTasks;
    private Statistics activeTasks;
    private Statistics failedTasks;
    private Statistics completedTasks;
    private Statistics totalTasks;
    private Statistics totalDuration;
    private Statistics totalGCTime;
    private Statistics totalInputBytes;
    private Statistics totalShuffleRead;
    private Statistics totalShuffleWrite;
    private Statistics maxMemory;

    private Statistics footprint; // 15,850 M
    private Statistics avgfootprintAfterFullGC; // 3,702.314 M
    private Statistics freedMemoryByFullGC; // 1,014.641 M
    private Statistics freedMemoryByFullGCpc; // 0.0 %
    private Statistics avgFreedMemoryByFullGC; // 12.683 M/coll
    private Statistics slopeAfterFullGC; // 71.764 M/s
    private Statistics avgRelativePostFullGCInc; // 15.81 M/coll
    private Statistics avgfootprintAfterGC; // 6,325.308 M
    private Statistics slopeAfterGC; // 41.902 M/s
    private Statistics avgRelativePostGCInc; // 47.386 M/coll
    private Statistics freedMemoryByGC; // 2,087,900.694 M
    private Statistics freedMemoryByGCpc; // 100.0 %
    private Statistics avgFreedMemoryByGC; // 1,935.033 M/coll
    private Statistics avgPause; // 0.5608 s
    private Statistics avgPauseσ; // 2.05657 s
    private Statistics minPause; // 0.00269 s
    private Statistics maxPause; // 18.61921 s
    private Statistics avgGCPause; // 0.16431 s
    private Statistics avgGCPauseσ; // 0.32184 s
    private Statistics avgFullGCPause; // 5.90843 s
    private Statistics avgFullGCPauseσ; // 5.42927 s
    private Statistics minFullGCPause; // 0.02228 s
    private Statistics maxFullGCPause; // 18.61921 s
    private Statistics accumPause; // 649.97 s
    private Statistics fullGCPause; // 472.67 s
    private Statistics fullGCPausePc; // 72.7 %
    private Statistics gcPause; // 177.3 s
    private Statistics gcPausePc; // 27.3 %
    private Statistics freedMemory; // 2,088,915.335 M
    private Statistics throughput; // -136.27 %
    private Statistics totalTime; // 275 s
    private Statistics freedMemoryPerMin; // 455,608.948 M/min
    private Statistics gcPerformance; // 11,776.4 M/s
    private Statistics fullGCPerformance; // 2,198.114 K/s

    private Statistics jvmHeapSize_youngGen_allocatedSize; // 7.5 gb
    private Statistics jvmHeapSize_youngGen_peakSize; // 6 gb
    private Statistics jvmHeapSize_oldGen_allocatedSize; // 22.5 gb
    private Statistics jvmHeapSize_oldGen_peakSize; // 22.5 gb
    private Statistics jvmHeapSize_metaSpace_allocatedSize; // 1.04 gb
    private Statistics jvmHeapSize_metaSpace_peakSize; // 48.52 mb
    private Statistics jvmHeapSize_total_allocatedSize; // 30 gb
    private Statistics jvmHeapSize_total_peakSize; // 28.5 gb

    private Statistics gcStatistics_totalCreatedBytes; // 249.49 gb
    private Statistics gcStatistics_measurementDuration; // 7 hrs 32 min 52 sec",
    private Statistics gcStatistics_avgAllocationRate; // 9.4 mb/sec
    private Statistics gcStatistics_avgPromotionRate; // 1.35 mb/sec
    private Statistics gcStatistics_minorGCCount; // 62
    private Statistics gcStatistics_minorGCTotalTime; // 1 min 19 sec
    private Statistics gcStatistics_minorGCAvgTime; // 1 sec 274 ms
    private Statistics gcStatistics_minorGCAvgTimeStdDeviation; // 2 sec 374 ms
    private Statistics gcStatistics_minorGCMinTIme; // 0
    private Statistics gcStatistics_minorGCMaxTime; // 13 sec 780 ms
    private Statistics gcStatistics_minorGCIntervalAvgTime; // 7 min 25 sec 442 ms
    private Statistics gcStatistics_fullGCCount; // 166
    private Statistics gcStatistics_fullGCTotalTime; // 14 min 11 sec 620 ms
    private Statistics gcStatistics_fullGCAvgTime; // 5 sec 130 ms
    private Statistics gcStatistics_fullGCAvgTimeStdDeviation; // 5 sec 207 ms
    private Statistics gcStatistics_fullGCMinTIme; // 120 ms
    private Statistics gcStatistics_fullGCMaxTime; // 57 sec 880 ms
    private Statistics gcStatistics_fullGCIntervalAvgTime; // 2 min 19 sec 104 ms
    private Statistics throughputPercentage;

    private Statistics maxCPUusage;
    private Statistics maxMemoryUsage;

    // In general, we run each application 5 times and each application has N executors,
    // so the length of executorList is 5N
    public ExecutorStatistics(List<Executor> executorList) {
        List<Executor> slaveExecturos = new ArrayList<Executor>();

        for (Executor executor : executorList) {
            if (!executor.getId().equals("driver"))
                slaveExecturos.add(executor);
        }
        computeStatistics(executorList);
    }

    private void computeStatistics(List<Executor> executorList) {

        Object[] executorObjs = executorList.toArray();

        rddBlocks = new Statistics(executorObjs,"getRddBlocks");
        memoryUsed = new Statistics(executorObjs,"getMemoryUsed");
        diskUsed = new Statistics(executorObjs,"getDiskUsed");
        totalCores = new Statistics(executorObjs,"getTotalCores");
        maxTasks = new Statistics(executorObjs,"getMaxTasks");
        activeTasks = new Statistics(executorObjs,"getActiveTasks");
        failedTasks = new Statistics(executorObjs,"getFailedTasks");
        completedTasks = new Statistics(executorObjs,"getCompletedTasks");
        totalTasks = new Statistics(executorObjs,"getTotalTasks");
        totalDuration = new Statistics(executorObjs,"getTotalDuration");
        totalGCTime = new Statistics(executorObjs,"getTotalGCTime");
        totalInputBytes = new Statistics(executorObjs,"getTotalInputBytes");
        totalShuffleRead = new Statistics(executorObjs,"getTotalShuffleRead");
        totalShuffleWrite = new Statistics(executorObjs,"getTotalShuffleWrite");
        maxMemory = new Statistics(executorObjs,"getMaxMemory");


        // GC metrics parsed from gc logs
        footprint = new Statistics(executorObjs, "getGcMetrics", "getFootprint");
        avgfootprintAfterFullGC = new Statistics(executorObjs, "getGcMetrics", "getAvgfootprintAfterFullGC");
        freedMemoryByFullGC = new Statistics(executorObjs, "getGcMetrics", "getFreedMemoryByFullGC");
        freedMemoryByFullGCpc = new Statistics(executorObjs, "getGcMetrics", "getFreedMemoryByFullGCpc");
        avgFreedMemoryByFullGC = new Statistics(executorObjs, "getGcMetrics", "getAvgFreedMemoryByFullGC");
        slopeAfterFullGC = new Statistics(executorObjs, "getGcMetrics", "getSlopeAfterFullGC");
        avgRelativePostFullGCInc = new Statistics(executorObjs, "getGcMetrics", "getAvgRelativePostFullGCInc");
        avgfootprintAfterGC = new Statistics(executorObjs, "getGcMetrics", "getAvgfootprintAfterGC");
        slopeAfterGC = new Statistics(executorObjs, "getGcMetrics", "getSlopeAfterGC");
        avgRelativePostGCInc = new Statistics(executorObjs, "getGcMetrics", "getAvgRelativePostGCInc");
        freedMemoryByGC = new Statistics(executorObjs, "getGcMetrics", "getFreedMemoryByGC");
        freedMemoryByGCpc = new Statistics(executorObjs, "getGcMetrics", "getFreedMemoryByGCpc");
        avgFreedMemoryByGC = new Statistics(executorObjs, "getGcMetrics", "getAvgFreedMemoryByGC");
        avgPause = new Statistics(executorObjs, "getGcMetrics", "getAvgPause");
        avgPauseσ = new Statistics(executorObjs, "getGcMetrics", "getAvgPauseσ");
        minPause = new Statistics(executorObjs, "getGcMetrics", "getMinPause");
        maxPause = new Statistics(executorObjs, "getGcMetrics", "getMaxPause");
        avgGCPause = new Statistics(executorObjs, "getGcMetrics", "getAvgGCPause");
        avgGCPauseσ = new Statistics(executorObjs, "getGcMetrics", "getAvgGCPauseσ");
        avgFullGCPause = new Statistics(executorObjs, "getGcMetrics", "getAvgFullGCPause");
        avgFullGCPauseσ = new Statistics(executorObjs, "getGcMetrics", "getAvgFullGCPauseσ");
        minFullGCPause = new Statistics(executorObjs, "getGcMetrics", "getMinFullGCPause");
        maxFullGCPause = new Statistics(executorObjs, "getGcMetrics", "getMaxFullGCPause");
        accumPause = new Statistics(executorObjs, "getGcMetrics", "getAccumPause");
        fullGCPause = new Statistics(executorObjs, "getGcMetrics", "getFullGCPause");
        fullGCPausePc = new Statistics(executorObjs, "getGcMetrics", "getFullGCPausePc");
        gcPause = new Statistics(executorObjs, "getGcMetrics", "getGcPause");
        gcPausePc = new Statistics(executorObjs, "getGcMetrics", "getGcPausePc");
        freedMemory = new Statistics(executorObjs, "getGcMetrics", "getFreedMemory");
        throughput = new Statistics(executorObjs, "getGcMetrics", "getThroughput");
        totalTime = new Statistics(executorObjs, "getGcMetrics", "getTotalTime");
        freedMemoryPerMin = new Statistics(executorObjs, "getGcMetrics", "getFreedMemoryPerMin");
        gcPerformance = new Statistics(executorObjs, "getGcMetrics", "getGcPerformance");
        fullGCPerformance = new Statistics(executorObjs, "getGcMetrics", "getFullGCPerformance");


        jvmHeapSize_youngGen_allocatedSize = new Statistics(executorObjs, "getgCeasyMetrics", "getJvmHeapSize_youngGen_allocatedSize");
        jvmHeapSize_youngGen_peakSize = new Statistics(executorObjs, "getgCeasyMetrics", "getJvmHeapSize_youngGen_peakSize");
        jvmHeapSize_oldGen_allocatedSize = new Statistics(executorObjs, "getgCeasyMetrics", "getJvmHeapSize_oldGen_allocatedSize");
        jvmHeapSize_oldGen_peakSize = new Statistics(executorObjs, "getgCeasyMetrics", "getJvmHeapSize_oldGen_peakSize");
        jvmHeapSize_metaSpace_allocatedSize = new Statistics(executorObjs, "getgCeasyMetrics", "getJvmHeapSize_metaSpace_allocatedSize");
        jvmHeapSize_metaSpace_peakSize = new Statistics(executorObjs, "getgCeasyMetrics", "getJvmHeapSize_metaSpace_peakSize");
        jvmHeapSize_total_allocatedSize = new Statistics(executorObjs, "getgCeasyMetrics", "getJvmHeapSize_total_allocatedSize");
        jvmHeapSize_total_peakSize = new Statistics(executorObjs, "getgCeasyMetrics", "getJvmHeapSize_total_peakSize");

        gcStatistics_totalCreatedBytes = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_totalCreatedBytes");
        gcStatistics_measurementDuration = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_measurementDuration");
        gcStatistics_avgAllocationRate = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_avgAllocationRate");
        gcStatistics_avgPromotionRate = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_avgPromotionRate");
        gcStatistics_minorGCCount = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_minorGCCount");
        gcStatistics_minorGCTotalTime = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_minorGCTotalTime");
        gcStatistics_minorGCAvgTime = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_minorGCAvgTime");
        gcStatistics_minorGCAvgTimeStdDeviation = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_minorGCAvgTimeStdDeviation");
        gcStatistics_minorGCMinTIme = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_minorGCMinTIme");
        gcStatistics_minorGCMaxTime = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_minorGCMaxTime");
        gcStatistics_minorGCIntervalAvgTime = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_minorGCIntervalAvgTime");
        gcStatistics_fullGCCount = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_fullGCCount");
        gcStatistics_fullGCTotalTime = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_fullGCTotalTime");
        gcStatistics_fullGCAvgTime = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_fullGCAvgTime");
        gcStatistics_fullGCAvgTimeStdDeviation = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_fullGCAvgTimeStdDeviation");
        gcStatistics_fullGCMinTIme = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_fullGCMinTIme");
        gcStatistics_fullGCMaxTime = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_fullGCMaxTime");
        gcStatistics_fullGCIntervalAvgTime = new Statistics(executorObjs, "getgCeasyMetrics", "getGcStatistics_fullGCIntervalAvgTime");

        throughputPercentage = new Statistics(executorObjs, "getgCeasyMetrics", "getThroughputPercentage");

        maxCPUusage = new Statistics(executorObjs, "getMaxCPUusage");
        maxMemoryUsage = new Statistics(executorObjs, "getMaxMemoryUsage");
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
        sb.append("[executor.gc.footprint] " + footprint + "\n");
        sb.append("[executor.gc.avgfootprintAfterFullGC] " + avgfootprintAfterFullGC + "\n");
        sb.append("[executor.gc.freedMemoryByFullGC] " + freedMemoryByFullGC + "\n");
        sb.append("[executor.gc.freedMemoryByFullGCpc] " + freedMemoryByFullGCpc + "\n");
        sb.append("[executor.gc.avgFreedMemoryByFullGC] " + avgFreedMemoryByFullGC + "\n");
        sb.append("[executor.gc.slopeAfterFullGC] " + slopeAfterFullGC + "\n");
        sb.append("[executor.gc.avgRelativePostFullGCInc] " + avgRelativePostFullGCInc + "\n");
        sb.append("[executor.gc.avgfootprintAfterGC] " + avgfootprintAfterGC + "\n");
        sb.append("[executor.gc.slopeAfterGC] " + slopeAfterGC + "\n");
        sb.append("[executor.gc.avgRelativePostGCInc] " + avgRelativePostGCInc + "\n");
        sb.append("[executor.gc.freedMemoryByGC] " + freedMemoryByGC + "\n");
        sb.append("[executor.gc.freedMemoryByGCpc] " + freedMemoryByGCpc + "\n");
        sb.append("[executor.gc.avgFreedMemoryByGC] " + avgFreedMemoryByGC + "\n");
        sb.append("[executor.gc.avgPause] " + avgPause + "\n");
        sb.append("[executor.gc.avgPauseσ] " + avgPauseσ + "\n");
        sb.append("[executor.gc.minPause] " + minPause + "\n");
        sb.append("[executor.gc.maxPause] " + maxPause + "\n");
        sb.append("[executor.gc.avgGCPause] " + avgGCPause + "\n");
        sb.append("[executor.gc.avgGCPauseσ] " + avgGCPauseσ + "\n");
        sb.append("[executor.gc.avgFullGCPause] " + avgFullGCPause + "\n");
        sb.append("[executor.gc.avgFullGCPauseσ] " + avgFullGCPauseσ + "\n");
        sb.append("[executor.gc.minFullGCPause] " + minFullGCPause + "\n");
        sb.append("[executor.gc.maxFullGCPause] " + maxFullGCPause + "\n");
        sb.append("[executor.gc.accumPause] " + accumPause + "\n");
        sb.append("[executor.gc.fullGCPause] " + fullGCPause + "\n");
        sb.append("[executor.gc.fullGCPausePc] " + fullGCPausePc + "\n");
        sb.append("[executor.gc.gcPause] " + gcPause + "\n");
        sb.append("[executor.gc.gcPausePc] " + gcPausePc + "\n");
        sb.append("[executor.gc.freedMemory] " + freedMemory + "\n");
        sb.append("[executor.gc.throughput] " + throughput + "\n");
        sb.append("[executor.gc.totalTime] " + totalTime + "\n");
        sb.append("[executor.gc.freedMemoryPerMin] " + freedMemoryPerMin + "\n");
        sb.append("[executor.gc.gcPerformance] " + gcPerformance + "\n");
        sb.append("[executor.gc.fullGCPerformance] " + fullGCPerformance + "\n");

        sb.append("\n");
        sb.append("[gceasy.jvmHeapSize_youngGen_allocatedSize] " + jvmHeapSize_youngGen_allocatedSize + "\n");
        sb.append("[gceasy.jvmHeapSize_youngGen_peakSize] " + jvmHeapSize_youngGen_peakSize + "\n");
        sb.append("[gceasy.jvmHeapSize_oldGen_allocatedSize] " + jvmHeapSize_oldGen_allocatedSize + "\n");
        sb.append("[gceasy.jvmHeapSize_oldGen_peakSize] " + jvmHeapSize_oldGen_peakSize + "\n");
        sb.append("[gceasy.jvmHeapSize_metaSpace_allocatedSize] " + jvmHeapSize_metaSpace_allocatedSize + "\n");
        sb.append("[gceasy.jvmHeapSize_metaSpace_peakSize] " + jvmHeapSize_metaSpace_peakSize + "\n");
        sb.append("[gceasy.jvmHeapSize_total_allocatedSize] " + jvmHeapSize_total_allocatedSize + "\n");
        sb.append("[gceasy.jvmHeapSize_total_peakSize] " + jvmHeapSize_total_peakSize + "\n");
        sb.append("[gceasy.gcStatistics_totalCreatedBytes] " + gcStatistics_totalCreatedBytes + "\n");
        sb.append("[gceasy.gcStatistics_measurementDuration] " + gcStatistics_measurementDuration + "\n");
        sb.append("[gceasy.gcStatistics_avgAllocationRate] " + gcStatistics_avgAllocationRate + "\n");
        sb.append("[gceasy.gcStatistics_avgPromotionRate] " + gcStatistics_avgPromotionRate + "\n");
        sb.append("[gceasy.gcStatistics_minorGCCount] " + gcStatistics_minorGCCount + "\n");
        sb.append("[gceasy.gcStatistics_minorGCTotalTime] " + gcStatistics_minorGCTotalTime + "\n");
        sb.append("[gceasy.gcStatistics_minorGCAvgTime] " + gcStatistics_minorGCAvgTime + "\n");
        sb.append("[gceasy.gcStatistics_minorGCAvgTimeStdDeviation] " + gcStatistics_minorGCAvgTimeStdDeviation + "\n");
        sb.append("[gceasy.gcStatistics_minorGCMinTIme] " + gcStatistics_minorGCMinTIme + "\n");
        sb.append("[gceasy.gcStatistics_minorGCMaxTime] " + gcStatistics_minorGCMaxTime + "\n");
        sb.append("[gceasy.gcStatistics_minorGCIntervalAvgTime] " + gcStatistics_minorGCIntervalAvgTime + "\n");
        sb.append("[gceasy.gcStatistics_fullGCCount] " + gcStatistics_fullGCCount + "\n");
        sb.append("[gceasy.gcStatistics_fullGCTotalTime] " + gcStatistics_fullGCTotalTime + "\n");
        sb.append("[gceasy.gcStatistics_fullGCAvgTime] " + gcStatistics_fullGCAvgTime + "\n");
        sb.append("[gceasy.gcStatistics_fullGCAvgTimeStdDeviation] " + gcStatistics_fullGCAvgTimeStdDeviation + "\n");
        sb.append("[gceasy.gcStatistics_fullGCMinTIme] " + gcStatistics_fullGCMinTIme + "\n");
        sb.append("[gceasy.gcStatistics_fullGCMaxTime] " + gcStatistics_fullGCMaxTime + "\n");
        sb.append("[gceasy.gcStatistics_fullGCIntervalAvgTime] " + gcStatistics_fullGCIntervalAvgTime + "\n");
        sb.append("[gceasy.throughputPercentage] " + throughputPercentage + "\n");

        return sb.toString();
    }
}
