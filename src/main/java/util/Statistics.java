package util;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Statistics {

    private double mean;
    private double stdVar;
    private double median;
    private double min;
    private double quantile25;
    private double quantile75;
    private double max;

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
}
