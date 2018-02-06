package statstics;

import util.Statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xulijie on 17-9-21.
 */
public class ComputedAppStatistics {
    // e.g., GroupByRDD-CMS-1-7G-0.5
    private String appName;
    private String gcName;
    private String dataMode;

    Map<String, Statistics> statisticsMap = new HashMap<String, Statistics>();


    public ComputedAppStatistics(String appName, String gcName, String dataMode, List<String> lines) {
        this.appName = appName;
        this.gcName = gcName;
        this.dataMode = dataMode;
        // k: [stage0.duration], v: Statistics(mean, etc.)
        for (String line: lines)
            if (line.startsWith("["))
                parseStatisticsLine(line);
    }

    private void parseStatisticsLine(String line) {
        Statistics statistics = new Statistics(line);
        statisticsMap.put(statistics.getMetricName(), statistics);
    }

    public double getMetric(String metricName, String statName) {
        return statisticsMap.get(metricName).get(statName);
    }

    public String getAppName() {
        return appName;
    }

    public String getGcName() {
        return gcName;
    }

    public String getDataMode() {
        return dataMode;
    }
}
