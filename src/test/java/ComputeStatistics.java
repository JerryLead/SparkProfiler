import util.RelativeDifference;
import util.Statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xulijie on 17-11-13.
 */
public class ComputeStatistics {

    public static void computeMean(String name, double[] values) {
        Statistics statistics = new Statistics(values);
        System.out.println("[" + name + "] mean = " +
                String.format("%.1f", statistics.getMean()));
    }

    public static void main(String[] args) {

        double[] values = {3.4, 1.8, 1.7};
        computeMean("GroupBy-Parallel-0.5", values);

        values = new double[]{2.5, 1.6, 1.7};
        computeMean("GroupBy-CMS-0.5", values);

        values = new double[]{2.6, 1.5, 1.3};
        computeMean("GroupBy-G1-0.5", values);

        values = new double[]{6.6, 3.8};
        computeMean("GroupBy-Parallel-1.0", values);

        values = new double[]{2.8, 4.2};
        computeMean("GroupBy-CMS-1.0", values);

        values = new double[]{2.8, 2.7};
        computeMean("GroupBy-G1-1.0", values);


        values = new double[]{4.7, 4.7, 4.7};
        computeMean("Join-Parallel-0.5", values);

        values = new double[]{3.8, 3.7, 3.7};
        computeMean("Join-CMS-0.5", values);

        values = new double[]{4.3, 4.1, 4.1};
        computeMean("Join-G1-0.5", values);

        values = new double[]{70.7, 14.2, 13.4};
        computeMean("Join-Parallel-1.0", values);

        values = new double[]{10.9, 11.0, 11.8};
        computeMean("Join-CMS-1.0", values);

        values = new double[]{11.7, 11.5, 11.2};
        computeMean("Join-G1-1.0", values);




        values = new double[]{19.1, 20.3, 22.7};
        computeMean("PageRank-Parallel-0.5", values);

        values = new double[]{20.7, 18.9, 24.2};
        computeMean("PageRank-CMS-0.5", values);

        values = new double[]{36.5, 31.8, 38.0};
        computeMean("PageRank-G1-0.5", values);


        values = new double[]{6.2, 5.8, 6.0};
        computeMean("SVM-Parallel-0.5", values);

        values = new double[]{6.0, 5.9, 6.1};
        computeMean("SVM-CMS-0.5", values);

        values = new double[]{6.1, 5.8, 5.7};
        computeMean("SVM-G1-0.5", values);

        values = new double[]{15.2, 14.2, 14.0};
        computeMean("SVM-Parallel-1.0", values);

        values = new double[]{14.7, 14.6, 14.3};
        computeMean("SVM-CMS-1.0", values);

        values = new double[]{13.9, 13.6};
        computeMean("SVM-G1-1.0", values);


        double[] doubles = new double[]{2.3, 1.9, 1.8};
        compareAppDuration(doubles);

        doubles = new double[]{5.2, 3.5, 2.8};
        compareAppDuration(doubles);

        doubles = new double[]{4.7, 3.7, 4.2};
        compareAppDuration(doubles);

        doubles = new double[]{32.8, 11.2, 11.5};
        compareAppDuration(doubles);

        doubles = new double[]{20.7, 21.3, 35.4};
        compareAppDuration(doubles);

        doubles = new double[]{14.5, 14.5, 13.8};
        compareAppDuration(doubles);

    }

    public static  void compareAppDuration(double[] values) {
        Arrays.sort(values);

        double initDuration = 0;
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (double d : values) {

            double relativeDiff = RelativeDifference.getRelativeDifference(initDuration, d) * 100;
            String label = "";
            if (relativeDiff > 20)
                label = "<<";
            else if (relativeDiff > 10)
                label = "<";
            else if (relativeDiff >= 0)
                label = "~";
            else
                label = "!";

            initDuration = d;
            if (first) {
                first = false;
                sb.append(d);
            } else {
                sb.append(label + d + "(" + (int) relativeDiff + ")");
            }
        }

        System.out.println("\t" + sb.toString());

    }
}
