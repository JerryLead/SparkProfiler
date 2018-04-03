package appinfo;

/**
 * Created by xulijie on 18-4-3.
 */

// [Task 84 SpillMetrics] release = 3.7 GB, writeTime = 40 s, recordsWritten = 86, bytesWritten = 567.5 MB

public class SpillMetrics {
    private int taskId;
    private double spilledMemoryGB;
    private double spillDuration;
    private long recordsWritten;
    private double bytesWrittenMB;

    public SpillMetrics(int taskId, double spilledMemoryGB, double spillDuration,
                         long recordsWritten, double bytesWrittenMB) {
        this.taskId = taskId;
        this.spilledMemoryGB = spilledMemoryGB;
        this.spillDuration = spillDuration;
        this.recordsWritten = recordsWritten;
        this.bytesWrittenMB = bytesWrittenMB;
    }

    public int getTaskId() {
        return taskId;
    }

    public double getSpilledMemoryGB() {
        return spilledMemoryGB;
    }

    public double getSpillDuration() {
        return spillDuration;
    }

    public long getRecordsWritten() {
        return recordsWritten;
    }

    public double getBytesWrittenMB() {
        return bytesWrittenMB;
    }
}
