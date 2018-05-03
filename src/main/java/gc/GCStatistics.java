package gc;

/**
 * Created by xulijie on 18-5-3.
 */
public class GCStatistics {

    private double STWPauseTime;
    private double youngGCTime;
    private double fullGCTime;
    private double concurrentGCTime;

    public GCStatistics(double STWPauseTime, double youngGCTime, double fullGCTime, double concurrentGCTime) {
        this.STWPauseTime = STWPauseTime;
        this.youngGCTime = youngGCTime;
        this.fullGCTime = fullGCTime;
        this.concurrentGCTime = concurrentGCTime;
    }

    public double getSTWPauseTime() {
        return STWPauseTime;
    }

    public double getYoungGCTime() {
        return youngGCTime;
    }

    public double getFullGCTime() {
        return fullGCTime;
    }

    public double getConcurrentGCTime() {
        return concurrentGCTime;
    }
}
