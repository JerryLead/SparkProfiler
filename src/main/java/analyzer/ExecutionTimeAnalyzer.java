package analyzer;

import appinfo.Application;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExecutionTimeAnalyzer {

    // key = appName, value = apps that run multiple times
    Map<String, List<Application>> appMap;
    Map<String, AppDurationStatistics> appDurationStatisticsMap;

    public ExecutionTimeAnalyzer(Map<String, List<Application>> appMap) {
        this.appMap = appMap;
        appDurationStatisticsMap = new HashMap<String, AppDurationStatistics>();
    }


    public void analyzeAppDuration() {
        for (Map.Entry<String, List<Application>> appEntry : appMap.entrySet()) {
            List<Application> appsWithSameName = appEntry.getValue();
            AppDurationStatistics durationStatistics = new AppDurationStatistics(appsWithSameName);
            appDurationStatisticsMap.put(appEntry.getKey(), durationStatistics);
        }
    }

    public void analyzeStageDuration() {

    }
}


class AppDurationStatistics {
    private double mean;
    private double stdvar;
    private double median;

    public AppDurationStatistics(List<Application> appsWithSameName) {
        DescriptiveStatistics durationStats = new DescriptiveStatistics();

        for (Application app : appsWithSameName)
            durationStats.addValue(app.getDuartion());

        mean = durationStats.getMean();
        stdvar = durationStats.getStandardDeviation();
        median = durationStats.getPercentile(50);
    }
}

class StageDurationStatistics {
    private long executorRunTime; // \sum_{i=1}^{n} (task_i.executorRunTime)
    private long jvmGcTime; // \sum_{i=1}^{n} (task_i.jvmGcTime)
    private long memoryBytesSpilled;
    private long diskBytesSpilled;
}