package parser;

import appinfo.Job;
import com.google.gson.*;
import util.HtmlFetcher;
import util.HtmlJsonWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class JobJsonParser {

    private String appId;
    private String outputDir;
    private String jobUrl;
    private List<Job> jobsList = new ArrayList<Job>();


    public JobJsonParser(String masterIP, String appId, String outputDir) {
        this.appId = appId;
        this.jobUrl = "http://" + masterIP + ":18080/api/v1/applications/" + appId + "/jobs";
        this.outputDir = outputDir;
    }

    public List<Job> getJobList() {
        return jobsList;
    }

    public void parseJobInfo() {

        try {
            System.out.println("jobUrl = " + jobUrl);
            String html = HtmlFetcher.fetch(jobUrl);

            String jobListInfoFile = outputDir + File.separatorChar + appId + File.separatorChar + "jobs.txt";
            HtmlJsonWriter.write(jobListInfoFile, html);

            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(html);
            JsonArray jobJsonArray = null;

            if (el.isJsonArray())
                jobJsonArray = el.getAsJsonArray();
            else {
                System.err.println("Error in parsing the job's json elements!");
                System.exit(1);
            }


            for (JsonElement jobElem : jobJsonArray) {

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

                JsonObject jobObject = jobElem.getAsJsonObject();

                Job job = new Job(jobObject);

                jobsList.add(job);

            }

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }
}
