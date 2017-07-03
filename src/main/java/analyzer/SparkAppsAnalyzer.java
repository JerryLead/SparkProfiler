package analyzer;

import appinfo.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SparkAppsAnalyzer {

    // Key: AppName, Value: the same app that runs multiple times
    public Map<String, List<Application>> appMap = new HashMap<String, List<Application>>();

    public void init(List<Application> profiledApps) {
        for (Application app : profiledApps) {
            String appName = app.getName();

            if (!appMap.containsKey(appName)) {
                List<Application> appList = new ArrayList<Application>();
                appList.add(app);
                appMap.put(appName, appList);
            } else {
                List<Application> appList = appMap.get(appName);
                appList.add(app);
            }
        }
    }

    public void analyzeExecutionTime() {
        ExecutionTimeAnalyzer executionTimeAnalyzer = new ExecutionTimeAnalyzer(appMap);
        executionTimeAnalyzer.analyzeAppDuration();
    }
}
