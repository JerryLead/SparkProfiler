package analyzer;

import appinfo.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SparkAppsAnalyzer {

    // Key: AppName, Value: the same app that runs multiple times
    // Key = RDDJoin-CMS-1-7G-0.5, Value = [app-20170630121954-0025, app-20170630122434-0026, ...]
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
