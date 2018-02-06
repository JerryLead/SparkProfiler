import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by xulijie on 17-8-29.
 */
public class HeapSizeDifferences {

    static Collection<Object> objects = new ArrayList<Object>();
    static long lastMaxMemory = 0;

    public static void main(String[] args) {
        try {
            List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            System.out.println("Running with: " + inputArguments);
            while (true) {
                printMaxMemory();
                consumeSpace();
            }
        } catch (OutOfMemoryError e) {
            freeSpace();
            printMaxMemory();
        }
    }

    static void printMaxMemory() {
        long currentMaxMemory = Runtime.getRuntime().maxMemory();
        if (currentMaxMemory != lastMaxMemory) {
            lastMaxMemory = currentMaxMemory;
            System.out.format("Runtime.getRuntime().maxMemory(): %,dK, %.2fMB, %.2fGB.%n",
                    currentMaxMemory / 1024, (float) currentMaxMemory / 1024 / 1024,
                    (float) currentMaxMemory / 1024 / 1024 / 1024);

            double memoryStore = (currentMaxMemory - 300 * 1024 * 1024) * 0.6 * 0.5;
            System.out.format("memoryStore: %.2fGB.%n", memoryStore / 1024 / 1024 / 1024);
        }
    }

    static void consumeSpace() {
        objects.add(new int[1_000_000]);
    }

    static void freeSpace() {
        objects.clear();
    }

    // Runtime.getRuntime().maxMemory(): 4,194,304K. G1-4G
    // Runtime.getRuntime().maxMemory(): 4,160,256K. CMS-4G
    // Runtime.getRuntime().maxMemory(): 3,728,384K. Parallel-4G
}
