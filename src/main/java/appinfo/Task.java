package appinfo;

public class Task {

    private String taskId;
    private String stageId;
    private String executorId;
    private String jvmGcTime;
    private String executorRunTime;
    private String StageStatus;
    private String TaskStatus;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    public String getExecutorRunTime() {
        return executorRunTime;
    }

    public String getJvmGcTime() {
        return jvmGcTime;
    }

    public void setExecutorRunTime(String executorRunTime) {
        this.executorRunTime = executorRunTime;
    }

    public void setJvmGcTime(String jvmGcTime) {
        this.jvmGcTime = jvmGcTime;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getStageStatus() {
        return StageStatus;
    }

    public void setStageStatus(String stageStatus) {
        StageStatus = stageStatus;
    }

    public String getTaskStatus() {
        return TaskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        TaskStatus = taskStatus;
    }
}