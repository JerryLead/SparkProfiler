package util;

/**
 * Created by xulijie on 17-9-21.
 */

// https://en.wikipedia.org/wiki/Relative_change_and_difference
public class RelativeDifference {

    public static double getRelativeDifference(double x, double x_reference) {
        if (x_reference < 0 || x < 0)
            return -1;
        if (x_reference == 0 && x == 0)
            return 0;
        return Math.abs(x - x_reference) / Math.max(x_reference, x);
    }
}
