import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulijie on 17-7-11.
 */
public class MemoryTest {
    public static void main(String args[]) {
        int[] array = new int[1024];

        List<int[]> arrayList = new ArrayList<int[]>();

        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            arrayList.add(array);
        }

        System.out.println(arrayList.size());
    }
}
