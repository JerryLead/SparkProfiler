package util;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Statistics {

    private String metricName;

    private double mean;
    private double stdVar;
    private double median;
    private double min;
    private double quantile25;
    private double quantile75;
    private double max;

    // [stage0.duration] mean = 34314.40, stdVar = 3316.57, median = 35150.00, min = 30011.00, quantile25 = 30915.50, quantile75 = 37295.50, max = 37922.00
    public Statistics(String line) {
        this.metricName = line.substring(line.indexOf('[') + 1, line.indexOf(']'));
        String[] metrics = line.substring(line.indexOf(']') + 1).replaceAll(" ", "").split(",");

        for (String metric : metrics) {
            String statName = metric.split("=")[0];
            double value = -1;
            if (!metric.split("=")[1].equalsIgnoreCase("NaN"))
                value = Double.parseDouble(metric.split("=")[1]);
            if (statName.equalsIgnoreCase("mean"))
                this.mean = value;
            else if (statName.equalsIgnoreCase("stdVar"))
                this.stdVar = value;
            else if (statName.equalsIgnoreCase("median"))
                this.median = value;
            else if (statName.equalsIgnoreCase("min"))
                this.min = value;
            else if (statName.equalsIgnoreCase("quantile25"))
                this.quantile25 = value;
            else if (statName.equalsIgnoreCase("quantile75"))
                this.quantile75 = value;
            else if (statName.equalsIgnoreCase("max"))
                this.max = value;
        }
    }

    public Statistics(List<Double> doubleValues) {
        DescriptiveStatistics stats = new DescriptiveStatistics();

        for (double value : doubleValues)
            stats.addValue(value);

        mean = stats.getMean();
        stdVar = stats.getStandardDeviation();
        median = stats.getPercentile(50);
    }

    public Statistics(Object[] objects, String methodName) {
        DescriptiveStatistics stats = new DescriptiveStatistics();

        for (Object obj : objects) {
            Class clazz = obj.getClass();
            double value = 0;
            try {
                Method method = clazz.getDeclaredMethod(methodName);
                value = Double.parseDouble(method.invoke(obj).toString());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            stats.addValue(value);
        }
        mean = stats.getMean();
        stdVar = stats.getStandardDeviation();

        min = stats.getMin();
        quantile25 = stats.getPercentile(25);
        median = stats.getPercentile(50);
        quantile75 = stats.getPercentile(75);
        max = stats.getMax();
    }

    public Statistics(Object[] objects, String methodName1, String methodName2) {
        DescriptiveStatistics stats = new DescriptiveStatistics();

        for (Object obj : objects) {
            Class clazz1 = obj.getClass();
            double value = 0;
            try {
                Method method1 = clazz1.getDeclaredMethod(methodName1);
                Object object1 = method1.invoke(obj);
                Class clazz2 = object1.getClass();
                Method method2 = clazz2.getDeclaredMethod(methodName2);
                value = Double.parseDouble(method2.invoke(object1).toString());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            stats.addValue(value);
        }
        mean = stats.getMean();
        stdVar = stats.getStandardDeviation();

        min = stats.getMin();
        quantile25 = stats.getPercentile(25);
        median = stats.getPercentile(50);
        quantile75 = stats.getPercentile(75);
        max = stats.getMax();
    }

    public double getMean() {
        return mean;
    }

    public double getStdVar() {
        return stdVar;
    }

    public double getMedian() {
        return median;
    }

    public double getMin() {
        return min;
    }

    public double getQuantile25() {
        return quantile25;
    }

    public double getQuantile75() {
        return quantile75;
    }

    public double getMax() {
        return max;
    }

    public String getMetricName() {
        return metricName;
    }

    @Override
    public String toString() {
        return "mean = " + format(mean)
                + ", stdVar = " + format(stdVar)
                + ", median = " + format(median)
                + ", min = " + format(min)
                + ", quantile25 = " + format(quantile25)
                + ", quantile75 = " + format(quantile75)
                + ", max = " + format(max);
    }

    public String format(double value) {

        return String.format("%.2f", value).toString();
    }

    public double get(String statName) {
        if (statName.equalsIgnoreCase("mean"))
            return mean;
        else if (statName.equalsIgnoreCase("stdVar"))
            return stdVar;
        else if (statName.equalsIgnoreCase("median"))
            return median;
        else if (statName.equalsIgnoreCase("min"))
            return min;
        else if (statName.equalsIgnoreCase("quantile25"))
            return quantile25;
        else if (statName.equalsIgnoreCase("quantile75"))
            return quantile75;
        else if (statName.equalsIgnoreCase("max"))
            return max;
        return -1;
    }
}
