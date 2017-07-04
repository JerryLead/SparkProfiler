package analyzer;

import appinfo.Application;
import statstics.ApplicationStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SparkAppsAnalyzer {

    // Key: AppName, Value: the same app that runs multiple times
    // Key = RDDJoin-CMS-1-7G-0.5, Value = [app-20170630121954-0025, app-20170630122434-0026, ...]
    private Map<String, List<Application>> appNameToIdsMap = new HashMap<String, List<Application>>();
    private Map<String, ApplicationStatistics> appStatisticsMap = new HashMap<String, ApplicationStatistics>();

    // profiledApps = [apps with different names in the appList]
    public void aggregateApps(List<Application> profiledApps) {
        for (Application app : profiledApps) {
            String appName = app.getName();

            if (!appNameToIdsMap.containsKey(appName)) {
                List<Application> appList = new ArrayList<Application>();
                appList.add(app);
                appNameToIdsMap.put(appName, appList);
            } else {
                List<Application> appList = appNameToIdsMap.get(appName);
                appList.add(app);
            }
        }
    }

    public void analyzeAppStatistics() {
        for (Map.Entry<String, List<Application>> app : appNameToIdsMap.entrySet()) {
            ApplicationStatistics appStatistics = new ApplicationStatistics(app.getValue());
            appStatisticsMap.put(app.getKey(), appStatistics);
        }
    }
}
