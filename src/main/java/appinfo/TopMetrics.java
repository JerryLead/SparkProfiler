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

    public String getTime() {
        return time;
    }

    public double getCPUusage() {
        return CPUusage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    @Override
    public String toString() {
        return "[" + time + "] CPU = "
                + String.format("%.2f", CPUusage)
                + ", Memory = "
                + String.format("%.2f", memoryUsage);
    }
}
