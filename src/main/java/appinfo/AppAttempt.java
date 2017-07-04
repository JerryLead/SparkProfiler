package appinfo;

import com.google.gson.JsonObject;

/**
 * Created by xulijie on 17-7-4.
 */

public class AppAttempt {
    private String startTime;
    private String endTime;
    private String lastUpdated;
    private long duration; // ms
    private boolean completed;
    private long startTimeEpoch;
    private long lastUpdatedEpoch;
    private long endTimeEpoch;


    public AppAttempt(JsonObject attemptObj) {
        startTime = attemptObj.getAsJsonObject().get("startTime").getAsString();
        endTime = attemptObj.getAsJsonObject().get("endTime").getAsString();
        lastUpdated = attemptObj.getAsJsonObject().get("lastUpdated").getAsString();
        duration = attemptObj.getAsJsonObject().get("duration").getAsLong();
        completed = attemptObj.getAsJsonObject().get("completed").getAsBoolean();
        startTimeEpoch = attemptObj.getAsJsonObject().get("startTimeEpoch").getAsLong();
        lastUpdatedEpoch = attemptObj.getAsJsonObject().get("lastUpdatedEpoch").getAsLong();
        endTimeEpoch = attemptObj.getAsJsonObject().get("endTimeEpoch").getAsLong();
    }

    public long getDuration() {
        return duration;
    }

    public boolean getCompleted() {
        return completed;
    }

}
