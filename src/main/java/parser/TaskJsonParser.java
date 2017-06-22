package parser;


import appinfo.Task;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YE Xingtong on 2017/2/13.
 * Modified by YE Xingtong on 2017/3/20.
 */
public class TaskJsonParser {

    private List<Task> tasksList = new ArrayList<Task>();
    private String stageId;
    private String taskUrl;
    private String stageStatus;


    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public void taskwsu() throws IOException {

        URL url = new URL(getTaskUrl());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        String line = null;

        StringBuilder response = new StringBuilder();
        try {
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((line = reader.readLine()) != null) {

                response.append(line).append("\r\n");

                continue;
            }
            reader.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        String s = response.toString();

        try {
            JsonParser parser = new JsonParser();  //创建JSON解析器
            JsonElement el = parser.parse(s);
            JsonArray array = null;
            if (el.isJsonArray()) {
                array = el.getAsJsonArray();
            }

            for (int i = 0; i < array.size(); i++) {


                JsonObject subObject = array.get(i).getAsJsonObject();
                JsonObject subsubObject = subObject.get("taskMetrics").getAsJsonObject();

                Task task = new Task();
                task.setTaskId(subObject.get("taskId").getAsString());
                task.setExecutorId(subObject.get("executorId").getAsString());
                task.setExecutorRunTime(subsubObject.get("executorRunTime").getAsString());
                task.setJvmGcTime(subsubObject.get("jvmGcTime").getAsString());
                task.setStageId(getStageId());
                task.setStageStatus(getStageStatus());
                if (subObject.has("errorMessage"))
                    task.setTaskStatus(subObject.get("errorMessage").getAsString());
                else task.setTaskStatus("SUCCESS");


                tasksList.add(task);

            }
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }


    }

    public List<Task> getTaskList() {
        return tasksList;
    }

    public String getStageStatus() {
        return stageStatus;
    }

    public void setStageStatus(String stageStatus) {
        this.stageStatus = stageStatus;
    }
}