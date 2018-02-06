package appinfo;

/**
 * Created by xulijie on 17-7-5.
 */

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

public class GCMetrics {

    private double footprint; // 15,850 M
    private double avgfootprintAfterFullGC; // 3,702.314 M
    private double freedMemoryByFullGC; // 1,014.641 M
    private double freedMemoryByFullGCpc; // 0.0 %
    private double avgFreedMemoryByFullGC; // 12.683 M/coll
    private double slopeAfterFullGC; // 71.764 M/s
    private double avgRelativePostFullGCInc; // 15.81 M/coll
    private double avgfootprintAfterGC; // 6,325.308 M
    private double slopeAfterGC; // 41.902 M/s
    private double avgRelativePostGCInc; // 47.386 M/coll
    private double freedMemoryByGC; // 2,087,900.694 M
    private double freedMemoryByGCpc; // 100.0 %
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
    private double throughput; // -136.27 %
    private long totalTime; // 275 s
    private double freedMemoryPerMin; // 455,608.948 M/min
    private double gcPerformance; // 11,776.4 M/s
    private double fullGCPerformance; // 2,198.114 K/s


    public void set(String name, String value) {
        if (name.equals("footprint"))
            footprint = Double.parseDouble(value);
        else if (name.equals("avgfootprintAfterFullGC"))
            avgfootprintAfterFullGC = Double.parseDouble(value);
        else if (name.equals("freedMemoryByFullGC"))
            freedMemoryByFullGC = Double.parseDouble(value);

        else if (name.equals("freedMemoryByFullGCpc"))
            freedMemoryByFullGCpc = Double.parseDouble(value);

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
            freedMemoryByGCpc = Double.parseDouble(value);

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
            throughput = Double.parseDouble(value);

        else if (name.equals("totalTime"))
            totalTime = Long.parseLong(value);
        else if (name.equals("freedMemoryPerMin"))
            freedMemoryPerMin = Double.parseDouble(value);
        else if (name.equals("freedMemoryPerMin"))
            freedMemoryPerMin = Double.parseDouble(value);
        else if (name.equals("gcPerformance"))
            gcPerformance = Double.parseDouble(value);
        else if (name.equals("fullGCPerformance"))
            fullGCPerformance = Double.parseDouble(value);

    }

    public double getFootprint() {
        return footprint;
    }

    public double getAvgfootprintAfterFullGC() {
        return avgfootprintAfterFullGC;
    }

    public double getFreedMemoryByFullGC() {
        return freedMemoryByFullGC;
    }

    public double getFreedMemoryByFullGCpc() {
        return freedMemoryByFullGCpc;
    }

    public double getAvgFreedMemoryByFullGC() {
        return avgFreedMemoryByFullGC;
    }

    public double getSlopeAfterFullGC() {
        return slopeAfterFullGC;
    }

    public double getAvgRelativePostFullGCInc() {
        return avgRelativePostFullGCInc;
    }

    public double getAvgfootprintAfterGC() {
        return avgfootprintAfterGC;
    }

    public double getSlopeAfterGC() {
        return slopeAfterGC;
    }

    public double getAvgRelativePostGCInc() {
        return avgRelativePostGCInc;
    }

    public double getFreedMemoryByGC() {
        return freedMemoryByGC;
    }

    public double getFreedMemoryByGCpc() {
        return freedMemoryByGCpc;
    }

    public double getAvgFreedMemoryByGC() {
        return avgFreedMemoryByGC;
    }

    public double getAvgPause() {
        return avgPause;
    }

    public double getAvgPauseσ() {
        return avgPauseσ;
    }

    public double getMinPause() {
        return minPause;
    }

    public double getMaxPause() {
        return maxPause;
    }

    public double getAvgGCPause() {
        return avgGCPause;
    }

    public double getAvgGCPauseσ() {
        return avgGCPauseσ;
    }

    public double getAvgFullGCPause() {
        return avgFullGCPause;
    }

    public double getAvgFullGCPauseσ() {
        return avgFullGCPauseσ;
    }

    public double getMinFullGCPause() {
        return minFullGCPause;
    }

    public double getMaxFullGCPause() {
        return maxFullGCPause;
    }

    public double getAccumPause() {
        return accumPause;
    }

    public double getFullGCPause() {
        return fullGCPause;
    }

    public double getFullGCPausePc() {
        return fullGCPausePc;
    }

    public double getGcPause() {
        return gcPause;
    }

    public double getGcPausePc() {
        return gcPausePc;
    }

    public double getFreedMemory() {
        return freedMemory;
    }

    public double getThroughput() {
        return throughput;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public double getFreedMemoryPerMin() {
        return freedMemoryPerMin;
    }

    public double getGcPerformance() {
        return gcPerformance;
    }

    public double getFullGCPerformance() {
        return fullGCPerformance;
    }
}