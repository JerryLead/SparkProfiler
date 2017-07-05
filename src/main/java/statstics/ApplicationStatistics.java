package statstics;

import appinfo.Application;

import appinfo.Executor;
import appinfo.Stage;
import util.FileTextWriter;
import util.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by xulijie on 17-7-3.
 */

public class ApplicationStatistics {

    private String appName;
    private Statistics duration;

    // each stage has its stage statistics
    private Map<Integer, StageStatistics> stageStatisticsMap = new TreeMap<Integer, StageStatistics>();
    private ExecutorStatistics executorStatistics;

    private List<Application> completedApps = new ArrayList<Application>();

    // In general, we run  application 5 times, so the length of stageWithSameId is 5
    public ApplicationStatistics(List<Application> appsWithSameName) {

        // check if all the applications are completed
        for (Application app : appsWithSameName) {
            if (app.isCompleted() == false) {
                System.err.println("[Error]:" + app.getAppId() + " is not completed!");
            } else {
                completedApps.add(app);
            }
        }

        computeAppStatistics();
        computeStageStatistics();
        computeExecutorStatistics();
    }

    private void computeAppStatistics() {
        Object[] appObjs = completedApps.toArray();
        duration = new Statistics(appObjs, "getDuration");
    }

    private void computeStageStatistics() {

        // <stageId, [stage from app1, stage from app2, stage from appN]>
        Map<Integer, List<Stage>> stagesWithSameId = new TreeMap<Integer, List<Stage>>();
        for (Application app : completedApps) {
            for (Map.Entry<Integer, Stage> stageEntry : app.getStageMap().entrySet()) {
                int stageId = stageEntry.getKey();
                Stage stage = stageEntry.getValue();

                if (stagesWithSameId.containsKey(stageId)) {
                    stagesWithSameId.get(stageId).add(stage);
                } else {
                    List<Stage> stageList = new ArrayList<Stage>();
                    stageList.add(stage);
                    stagesWithSameId.put(stageId, stageList);
                }
            }
        }

        for (Map.Entry<Integer, List<Stage>> stagesEntry : stagesWithSameId.entrySet()) {
            StageStatistics stageStatistics = new StageStatistics(stagesEntry.getValue());
            stageStatisticsMap.put(stagesEntry.getKey(), stageStatistics);
        }
    }

    private void computeExecutorStatistics() {
        List<Executor> executorsMultipleApps = new ArrayList<Executor>();
        for (Application app : completedApps) {
            List<Executor> executorsPerApp = app.getExecutors();
            executorsMultipleApps.addAll(executorsPerApp);
        }

        executorStatistics = new ExecutorStatistics(executorsMultipleApps);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append("===============================================================" + appName
                + "===============================================================\n");
        sb.append("[app.duration] " + duration + "\n");

        for (Map.Entry<Integer, StageStatistics> stageStatisticsEntry : stageStatisticsMap.entrySet()) {
            int stageId = stageStatisticsEntry.getKey();
            StageStatistics stageStatistics = stageStatisticsEntry.getValue();

            sb.append("-------------------------------------------------------------------[Stage "
                    + stageId
                    + "]-------------------------------------------------------------------\n");
            sb.append(stageStatistics);
        }

        sb.append("-------------------------------------------------------------------------"
                + "[Executor Statistics]-------------------------------------------------------------------------\n");
        sb.append(executorStatistics);

        sb.append("=========================================================================="
                + "===============================================================\n");

        return sb.toString();
    }
}

