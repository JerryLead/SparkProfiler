package appinfo;


public class Application {

    // private String JobID;
    private String appId;
    private String name;
    private String startTime;
    private String endTime;
    private String lastUpdated;
    private long duration; // ms
    private String completed;

    private long startTimeEpoch;
    private long lastUpdatedEpoch;
    private long endTimeEpoch;

    /*
    {
        "id" : "app-20170623115533-0014",
            "name" : "RDDJoin-G1-4-28G-0.5",
            "attempts" : [ {
                "startTime" : "2017-06-23T03:55:32.099GMT",
                "endTime" : "2017-06-23T07:55:21.408GMT",
                "lastUpdated" : "2017-06-23T07:55:21.459GMT",
                "duration" : 14389309,
                "sparkUser" : "root",
                "completed" : true,
                "startTimeEpoch" : 1498190132099,
                "lastUpdatedEpoch" : 1498204521459,
                "endTimeEpoch" : 1498204521408
    } ]
    }
    */


    /*
    public String getStageUrl() {
        return StageUrl;
    }

    public void setStageUrl(String stageUrl) {
        StageUrl = "47.92.71.43:18080/api/v1/applications/" + stageUrl + "/stages";
    }

    public void setJobUrl(String jobUrl) {
        JobUrl = "47.92.71.43:18080/api/v1/applications/" + jobUrl + "/jobs";
    }

    public String getJobUrl() {
        return JobUrl;
    }
    */
}