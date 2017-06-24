package parser;

import appinfo.Application;
import appinfo.Job;
import com.google.gson.*;
import util.HtmlFetcher;
import util.HtmlJsonWriter;

import java.io.*;


public class JobsJsonParser {

    private Application app;

    private String jobsURL;
    private String appDir;
    private String appURL;

    public JobsJsonParser(String appURL, String appDir, Application app) {
        // http://masterIP:18080/api/v1/applications/app-20170618202557-0295/jobs
        this.jobsURL = appURL + "/jobs";
        this.appDir = appDir;
        this.app = app;
        this.appURL = appURL;
    }

    public JobsJsonParser(String jobsJson, Application app) {

    }

    public void parseJobsInfo(String jobsJson) {

        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(jobsJson);
            JsonArray jobJsonArray = null;

            if (el.isJsonArray())
                jobJsonArray = el.getAsJsonArray();
            else {
                System.err.println("Error in parsing the jobs json!");
                System.exit(1);
            }


            /*
                jobObject represents
                {
                  "jobId" : 16,
                  "name" : "aggregate at AreaUnderCurve.scala:45",
                  "submissionTime" : "2017-05-30T16:25:43.699GMT",
                  "completionTime" : "2017-05-30T16:25:44.455GMT",
                  "stageIds" : [ 33, 31, 32 ],
                  "status" : "SUCCEEDED",
                  "numTasks" : 160,
                  "numActiveTasks" : 0,
                  "numCompletedTasks" : 33,
                  "numSkippedTasks" : 127,
                  "numFailedTasks" : 0,
                  "numActiveStages" : 0,
                  "numCompletedStages" : 1,
                  "numSkippedStages" : 2,
                  "numFailedStages" : 0
                }
            */

            for (JsonElement jobElem : jobJsonArray) {
                JsonObject jobObject = jobElem.getAsJsonObject();
                Job job = new Job(jobObject);
                app.addJob(job);
            }

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    public void saveJobsJson() {
        // "profiles/WordCount-CMS-4-28_app-20170618202557-0295/jobs.json"
        String jobsJsonFile = appDir + File.separatorChar + "jobs.json";

        String jobsJson = HtmlFetcher.fetch(jobsURL);
        HtmlJsonWriter.write(jobsJsonFile, jobsJson);

        parseJobsInfo(jobsJson);

        saveStagesJson();

        for (Job job : app.getJobList()) {
            saveStagesJsonPerJob(job);
        }
    }

    private void saveStagesJson() {
        // http://masterIP:18080/api/v1/applications/app-20170618202557-0295/stages
        String stagesURL = appURL + "/stages";

        // "profiles/WordCount-CMS-4-28_app-20170618202557-0295/stages.json"
        String stagesJsonFile = appDir + File.separatorChar + "stages.json";

        String stagesJson = HtmlFetcher.fetch(stagesURL);
        HtmlJsonWriter.write(stagesJsonFile, stagesJson);
    }

    // "profiles/WordCount-CMS-4-28_app-20170618202557-0295/jobId/stageId/"
    private void saveStagesJsonPerJob(Job job) {

    }
}
