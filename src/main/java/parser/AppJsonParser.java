package parser;

import appinfo.Application;
import appinfo.Job;
import appinfo.Stage;
import util.HtmlFetcher;
import util.HtmlJsonWriter;
import util.JsonFileReader;

import java.io.File;

public class AppJsonParser {

    private String appId;
    private String appURL;

    private String appDir;

    public AppJsonParser() {}

    public AppJsonParser(String masterIP, String appId) {
        this.appId = appId;
        // http://masterIP:18080/api/v1/applications/app-20170618202557-0295
        appURL = "http://" + masterIP + ":18080/api/v1/applications/" + appId;
    }

    // application.json
    public Application parseApplication(String appJsonFile) {
        String appJson = JsonFileReader.readFile(appJsonFile);
        return new Application(appJson);
    }

    public void saveAppJson(String outputDir) {
        String appJson = HtmlFetcher.fetch(appURL);

        // in order to get the app name
        Application app = new Application(appJson);

        /**
         * The following app pages will be saved:
         * /applications/[app-id], including the application information.
         * /applications/[app-id]/jobs, including the information of each job.
         * /applications/[app-id]/stages, including the information of each stage.
         * /applications/[app-id]/stages/[stage-id]/[stage-attempt-id], including the tasks and executorSummary.
         * /applications/[app-id]/stages/[stage-id]/[stage-attempt-id]/taskSummary, including the taskSummary.
         * /applications/[app-id]/executors, A list of all active executors for the given application.
         * /applications/[app-id]/allexecutors, A list of all(active and dead) executors for the given application.
         * /applications/[app-id]/storage/rdd, A list of stored RDDs for the given application.
         **/

        // Save the app json "/applications/[app-id]" into "outputDir/appName_appId/application.json"
        appDir = outputDir + File.separatorChar + app.getName() + "_" + appId;
        String appJsonFile = appDir + File.separatorChar + "application.json";
        HtmlJsonWriter.write(appJsonFile, appJson);

        // Save the jobs json "/applications/[app-id]/jobs" into "outputDir/appName_appId/jobs.json"
        JobsJsonParser jobsJsonParser = new JobsJsonParser(appURL, appDir);
        jobsJsonParser.saveJobsJson(app);

        // Save the stages json "/applications/[app-id]/stages" into "outputDir/appName_appId/stages.json"
        StagesJsonParser stagesJsonParser = new StagesJsonParser(appURL, appDir);
        stagesJsonParser.saveStagesJson(app);


        // To save the tasks json "/applications/[app-id]/stages/[stage-id]/[stage-attempt-id]" and
        // "/applications/[app-id]/stages/[stage-id]/[stage-attempt-id]/taskSummary" into
        // "outputDir/appName_appId/jobId/stageId/"
        saveStageAttemptJson(app);


        // To save the executor json "/applications/[app-id]/executors" and
        // "/applications/[app-id]/allexecutors" into
        // "outputDir/appName_appId/"
        saveExecutorJson();

        // To save the tasks json "/applications/[app-id]/storage/rdd" into
        // "outputDir/appName_appId/"
        saveStorageRDDJson();

    }

    private void saveStageAttemptJson(Application app) {
        for (Job job : app.getJobMap().values()) {
            int jobId = job.getJobId();

            for (int stageId : job.getStageIds()) {
                // mkdir outputDir/appName_appId/jobId/stageId
                Stage stage = app.getStage(stageId);

                for (int stageAttemptId : stage.getStageAttemptIds()) {

                    // http://masterIP:18080/api/v1/applications/app-20170618202557-0295/stages/stageId/stageAttemptId
                    String stageAttemptURL = appURL + "/stages/" + stageId + "/" + stageAttemptId;

                    // "outputDir/appName_appId/jobId/stageId/attempt-attemptId.json"
                    String stageAttemptJsonFile = appDir + File.separatorChar + "job-" + jobId +
                            File.separatorChar + "stage-" + stageId + File.separatorChar + "attempt-" + stageAttemptId + ".json";
                    String stageAttemptJson = HtmlFetcher.fetch(stageAttemptURL);
                    HtmlJsonWriter.write(stageAttemptJsonFile, stageAttemptJson);

                    if(stage.getStageAttemptStatus(stageAttemptId).equals("COMPLETE")) {
                        String taskSummaryURL = stageAttemptURL + "/taskSummary";
                        // "outputDir/appName_appId/jobId/stageId/attempt-attemptId-taskSummary.json"
                        String taskSummaryJsonFile = appDir + File.separatorChar + "job-" + jobId +
                                File.separatorChar + "stage-" + stageId + File.separatorChar + "attempt-" +
                                stageAttemptId + "-taskSummary.json";
                        String taskSummaryJson = HtmlFetcher.fetch(taskSummaryURL);
                        HtmlJsonWriter.write(taskSummaryJsonFile, taskSummaryJson);
                    }

                }
            }
        }
    }

    private void saveExecutorJson() {
        String executorURL = appURL + "/executors";
        // "outputDir/appName_appId/executors.json"
        String executorJsonFile = appDir + File.separatorChar + "executors.json";
        String executorJson = HtmlFetcher.fetch(executorURL);
        HtmlJsonWriter.write(executorJsonFile, executorJson);

        String allexecutorURL = appURL + "/allexecutors";
        // "outputDir/appName_appId/allexecutors.json"
        String allexecutorJsonFile = appDir + File.separatorChar + "allexecutors.json";
        String allexecutorJson = HtmlFetcher.fetch(allexecutorURL);
        HtmlJsonWriter.write(allexecutorJsonFile, allexecutorJson);
    }


    private void saveStorageRDDJson() {
        String storageRDDURL = appURL + "/storage/rdd";
        // "outputDir/appName_appId/storageRDD.json"
        String storageRDDJsonFile = appDir + File.separatorChar + "storageRDD.json";
        String storageRDDJson = HtmlFetcher.fetch(storageRDDURL);
        HtmlJsonWriter.write(storageRDDJsonFile, storageRDDJson);
    }

}
