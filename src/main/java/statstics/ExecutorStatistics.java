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
    }


    public void display() {

        System.out.println("[executor.rddBlocks] " + rddBlocks);
        System.out.println("[executor.memoryUsed] " + memoryUsed);
        System.out.println("[executor.diskUsed] " + diskUsed);
        System.out.println("[executor.totalCores] " + totalCores);
        System.out.println("[executor.maxTasks] " + maxTasks);
        System.out.println("[executor.activeTasks] " + activeTasks);
        System.out.println("[executor.failedTasks] " + failedTasks);
        System.out.println("[executor.completedTasks] " + completedTasks);
        System.out.println("[executor.totalTasks] " + totalTasks);
        System.out.println("[executor.totalDuration] " + totalDuration);
        System.out.println("[executor.totalGCTime] " + totalGCTime);
        System.out.println("[executor.totalInputBytes] " + totalInputBytes);
        System.out.println("[executor.totalShuffleRead] " + totalShuffleRead);
        System.out.println("[executor.totalShuffleWrite] " + totalShuffleWrite);
        System.out.println("[executor.maxMemory] " + maxMemory);

        System.out.println("");
        System.out.println("[executor.gc.footprint] " + footprint);
        System.out.println("[executor.gc.avgfootprintAfterFullGC] " + avgfootprintAfterFullGC);
        System.out.println("[executor.gc.freedMemoryByFullGC] " + freedMemoryByFullGC);
        System.out.println("[executor.gc.freedMemoryByFullGCpc] " + freedMemoryByFullGCpc);
        System.out.println("[executor.gc.avgFreedMemoryByFullGC] " + avgFreedMemoryByFullGC);
        System.out.println("[executor.gc.slopeAfterFullGC] " + slopeAfterFullGC);
        System.out.println("[executor.gc.avgRelativePostFullGCInc] " + avgRelativePostFullGCInc);
        System.out.println("[executor.gc.avgfootprintAfterGC] " + avgfootprintAfterGC);
        System.out.println("[executor.gc.slopeAfterGC] " + slopeAfterGC);
        System.out.println("[executor.gc.avgRelativePostGCInc] " + avgRelativePostGCInc);
        System.out.println("[executor.gc.freedMemoryByGC] " + freedMemoryByGC);
        System.out.println("[executor.gc.freedMemoryByGCpc] " + freedMemoryByGCpc);
        System.out.println("[executor.gc.avgFreedMemoryByGC] " + avgFreedMemoryByGC);
        System.out.println("[executor.gc.avgPause] " + avgPause);
        System.out.println("[executor.gc.avgPauseσ] " + avgPauseσ);
        System.out.println("[executor.gc.minPause] " + minPause);
        System.out.println("[executor.gc.maxPause] " + maxPause);
        System.out.println("[executor.gc.avgGCPause] " + avgGCPause);
        System.out.println("[executor.gc.avgGCPauseσ] " + avgGCPauseσ);
        System.out.println("[executor.gc.avgFullGCPause] " + avgFullGCPause);
        System.out.println("[executor.gc.avgFullGCPauseσ] " + avgFullGCPauseσ);
        System.out.println("[executor.gc.minFullGCPause] " + minFullGCPause);
        System.out.println("[executor.gc.maxFullGCPause] " + maxFullGCPause);
        System.out.println("[executor.gc.accumPause] " + accumPause);
        System.out.println("[executor.gc.fullGCPause] " + fullGCPause);
        System.out.println("[executor.gc.fullGCPausePc] " + fullGCPausePc);
        System.out.println("[executor.gc.gcPause] " + gcPause);
        System.out.println("[executor.gc.gcPausePc] " + gcPausePc);
        System.out.println("[executor.gc.freedMemory] " + freedMemory);
        System.out.println("[executor.gc.throughput] " + throughput);
        System.out.println("[executor.gc.totalTime] " + totalTime);
        System.out.println("[executor.gc.freedMemoryPerMin] " + freedMemoryPerMin);
        System.out.println("[executor.gc.gcPerformance] " + gcPerformance);
        System.out.println("[executor.gc.fullGCPerformance] " + fullGCPerformance);
    }
}
