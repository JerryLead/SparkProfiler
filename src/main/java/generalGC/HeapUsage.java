package generalGC;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulijie on 17-12-22.
 */
public class HeapUsage {

    private List<Usage> youngGen = new ArrayList<Usage>();
    private List<Usage> oldGen = new ArrayList<Usage>();
    private List<Usage> metaGen = new ArrayList<Usage>();

    /*
    public void addYoungUsage(double time, double usage, double allocated, String gcType, String gcCause) {
        Usage yUsage = new Usage(time, usage, allocated, gc, "Young");

        if (!youngGen.isEmpty() && youngGen.get(youngGen.size() - 1).allocated != allocated)
            youngGen.add(new Usage(time, usage, youngGen.get(youngGen.size() - 1).allocated, gc, "Young"));
        youngGen.add(yUsage);
    }

    public void addOldUsage(double time, double usage, double allocated, String gc) {
        Usage oUsage = new Usage(time, usage, allocated, gc, "Old");
        if (!oldGen.isEmpty() && oldGen.get(oldGen.size() - 1).allocated != allocated)
            oldGen.add(new Usage(time, usage, oldGen.get(oldGen.size() - 1).allocated, gc, "Old"));
        oldGen.add(oUsage);
    }

    public void addMetaUsage(double time, double usage, double allocated, String gc) {
        Usage mUsage = new Usage(time, usage, allocated, gc, "Metaspace");
        if (!metaGen.isEmpty() && metaGen.get(metaGen.size() - 1).allocated != allocated)
            metaGen.add(new Usage(time, usage, metaGen.get(metaGen.size() - 1).allocated, gc, "Metaspace"));
        metaGen.add(mUsage);
    }
    */

    public void display() {
        System.out.println("============ Young Generation ============");
        for(Usage usage : youngGen)
            System.out.println(usage);

        System.out.println("============ Old Generation ============");
        for(Usage usage : oldGen)
            System.out.println(usage);

        System.out.println("============ Metaspace Generation ============");
        for(Usage usage : metaGen)
            System.out.println(usage);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(Usage usage : youngGen)
            sb.append(usage + "\n");

        for(Usage usage : oldGen)
            sb.append(usage + "\n");

        for(Usage usage : metaGen)
            sb.append(usage + "\n");

        return sb.toString();
    }

    // usage.addUsage("YGC", offsetTime, yBeforeMB, yAfterMB, youngMB, oldBeforeMB, oldAfterMB, oldMB, ygcSeconds, gcCause);
    public void addUsage(String gcType, double offsetTime, double yBeforeMB, double yAfterMB, double youngMB, double oldBeforeMB,
                              double oldAfterMB, double oldMB, double gcSeconds, String gcCause) {

        Usage yUsage = new Usage("Young", gcType, offsetTime, yBeforeMB, yAfterMB, youngMB, gcSeconds, gcCause);
        youngGen.add(yUsage);

        //if (oldBeforeMB != oldAfterMB) {
        Usage oUsage = new Usage("Old", gcType, offsetTime, oldBeforeMB, oldAfterMB, oldMB, gcSeconds, gcCause);
        oldGen.add(oUsage);
        //}
        //    System.out.println(gcType + " " + offsetTime + " " + yBeforeMB + " " + yAfterMB + " " + youngMB + " " + gcCause);
    }
}

class Usage {
    String gen;
    String gcType;
    double offsetTime;
    double beforeGC;
    double afterGC;
    double allocated;
    double gcPauseSec;
    String gcCause;

    public Usage(String gen, String gcType, double offsetTime, double beforeGC, double afterGC, double allocated, double gcPauseSec, String gcCause) {
        this.gen = gen;
        this.gcType = gcType;
        this.offsetTime = offsetTime;
        this.beforeGC = beforeGC;
        this.afterGC = afterGC;
        this.allocated = allocated;
        this.gcPauseSec = gcPauseSec;
        this.gcCause = gcCause;
    }

    public String toString() {
        return "[" + gen + "](" + gcType + ") time = " + offsetTime + ", beforeGC = "
                + beforeGC + ", afterGC = " + afterGC + ", allocated = " + allocated
                + ", gcPause = " + gcPauseSec + "s, gcCause = " + gcCause;
    }
}