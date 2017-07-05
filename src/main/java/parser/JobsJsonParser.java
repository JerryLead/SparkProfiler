package parser;

import appinfo.Application;
import appinfo.Job;

import com.google.gson.*;
import util.HtmlFetcher;
import util.FileTextWriter;


import java.io.File;
import java.util.List;


public class JobsJsonParser {


    private String appDir;
    private String appURL;

    public JobsJsonParser(String appURL, String appDir) {
        this.appDir = appDir;
        this.appURL = appURL;
    }

    public JobsJsonParser() {}

    public void parseJobsJson(String jobsJson, Application app) {

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

    public void saveJobsJson(Application app) {
        // "profiles/WordCount-CMS-4-28_app-20170618202557-0295/jobs.json"
        String jobsJsonFile = appDir + File.separatorChar + "jobs.json";

        String jobsJson = HtmlFetcher.fetch(appURL + "/jobs");
        FileTextWriter.write(jobsJsonFile, jobsJson);

        parseJobsJson(jobsJson, app);
    }


    // "profiles/WordCount-CMS-4-28_app-20170618202557-0295/jobId/stageId/"
    private void saveStagesJsonPerJob(Job job) {
        List<Integer> stageIds = job.getStageIds();
        for (Integer stageId : stageIds) {

            String stageURL = appURL + "/stages/" + stageId + "/";
        }


    }
}
