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

    public void addYoungUsage(double time, double usage, double allocated, String gc) {
        Usage yUsage = new Usage(time, usage, allocated, gc);
        youngGen.add(yUsage);
    }

    public void addOldUsage(double time, double usage, double allocated, String gc) {
        Usage oUsage = new Usage(time, usage, allocated, gc);
        oldGen.add(oUsage);
    }

    public void addMetaUsage(double time, double usage, double allocated, String gc) {
        Usage mUsage = new Usage(time, usage, allocated, gc);
        metaGen.add(mUsage);
    }

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
}

class Usage {
    double time;
    double usage;
    double allocated;
    String gc;

    public Usage(double time, double usage, double allocated, String gc) {
        this.time = time;
        this.usage = usage;
        this.allocated = allocated;
        this.gc = gc;
    }

    public String toString() {
        return "[" + time + "] usage = " + usage + ", allocated = " + allocated;
    }
}