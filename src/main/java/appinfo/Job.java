package appinfo;

/**
 * Created by Ye on 2017/2/13.
 * Modified by YE on 2017/3/20.
 */
public class Job {

    private String JobID;
    private String Name;
    private String Duration;
    private String StageID;
    private String SubmitTime;
    private String CompleteTime;

    public Job() {
    }

    public String getJobID() {
        return JobID;
    }

    public void setJobID(String jobID) {
        JobID = jobID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }


    public String getStageID() {
        return StageID;
    }

    public void setStageID(String stageID) {
        StageID = stageID;
    }



    public String getSubmitTime() {
        return SubmitTime;
    }

    public void setSubmitTime(String submitTime) {
        SubmitTime = submitTime;
    }

    public String getCompleteTime() {
        return CompleteTime;
    }

    public void setCompleteTime(String completeTime) {
        CompleteTime = completeTime;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
