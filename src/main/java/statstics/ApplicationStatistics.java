package statstics;

import appinfo.AppAttempt;
import appinfo.Application;

import util.Statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulijie on 17-7-3.
 */
public class ApplicationStatistics {
    private Statistics duration;

    // In general, we run each application 5 times, so the length of stageWithSameId is 5
    public ApplicationStatistics(List<Application> appsWithSameName) {
        List<AppAttempt> appAttempts = new ArrayList<AppAttempt>();

        for (Application app : appsWithSameName) {
            AppAttempt appAttempt = app.getCompletedApp();
            if (appAttempt != null)
                appAttempts.add(appAttempt);
            else
                System.err.println("Application " + app.getAppId() + " does not have completed app attempt");

        }

        computeStatistics(appAttempts);
    }

    private void computeStatistics(List<AppAttempt> appAttempts) {
        Object[] appAttemptObjs = appAttempts.toArray(new Object[0]);

        duration = new Statistics(appAttemptObjs, "getDuration");
    }
}

