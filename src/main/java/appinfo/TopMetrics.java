package appinfo;

/**
 * Created by xulijie on 17-11-4.
 */
public class TopMetrics {

    String time;
    double CPUusage;
    double memoryUsage; // GB

    public TopMetrics(String time, double CPUusage, double memoryUsage) {
        this.time = time;
        this.CPUusage = CPUusage;
        this.memoryUsage = memoryUsage;
    }

    @Override
    public String toString() {
        return "[" + time + "] CPU = " + CPUusage + ", Memory = " + memoryUsage;
    }
}
