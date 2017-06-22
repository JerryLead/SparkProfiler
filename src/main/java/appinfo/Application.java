package appinfo;

/**
 * Created by Ye on 2017/2/13.
 * Modified by YE on 2017/3/20.
 */
public class Application {

    // private String JobID;
    private String ApplicationID;
    private String Name;
    private String Duration;
    private String JobUrl;
    private String StageUrl;

    public void setApplicationID(String applicationID) {
        ApplicationID = applicationID;
    }

    public String getApplicationID() {
        return ApplicationID;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

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

    @Override
    public String toString() {
        return super.toString();
    }
}