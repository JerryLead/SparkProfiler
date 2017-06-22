package parser;

import appinfo.Stage;
import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class StageJsonParser {

    private List<Stage> stageList = new ArrayList<Stage>();
    private String stageUrl;


    public void setStageUrl(String a) {

        stageUrl = "http://47.92.71.43:18080/api/v1/applications/" + a + "/stages";

    }

    public String getStageUrl() {
        return stageUrl;
    }

    public void stagewsu() throws IOException {

        System.out.println(getStageUrl());
        URL url = new URL(getStageUrl());
        //    URL url = new URL( getStageUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        //  connection.setConnectTimeout(8000);
        //  connection.setReadTimeout(8000);
        String line = null;
        String b = "";
        StringBuilder response = new StringBuilder();
        try {
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            // StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // b+=line;
                //   System.out.println(line);
                response.append(line).append("\r\n");
                // System.out.println(b);
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
        System.out.println(s);

        try {
            JsonParser parser = new JsonParser();  //创建JSON解析器
            JsonElement el = parser.parse(s);
            JsonArray array = null;
            if (el.isJsonArray()) {
                array = el.getAsJsonArray();
            }

            for (int i = 0; i < array.size(); i++) {

                System.out.println("---------------");
                JsonObject subObject = array.get(i).getAsJsonObject();
                // JsonArray subArray = subObject.get("stageIds").getAsJsonArray();
                Stage stage = new Stage();
                String label;
                if (!"0".equals(subObject.get("attemptId").getAsString()))
                    label = subObject.get("stageId").getAsString() + "(" + subObject.get("attemptId").getAsString() + ")";
                else
                    label = subObject.get("stageId").getAsString();
                stage.setStageId(label);
                stage.setName(subObject.get("name").getAsString());
                stage.setAttemptId(subObject.get("attemptId").getAsString());
                //     System.out.println(stage.getAttemptId());
                stage.setNumCompleteTasks(subObject.get("numCompleteTasks").getAsString());
                stage.setNumFailedTasks(subObject.get("numFailedTasks").getAsString());
                int a = Integer.parseInt(stage.getNumFailedTasks());
                int c = Integer.parseInt(stage.getNumCompleteTasks());
                int length = a + c;
                String l = String.valueOf(length);
                stage.setTaskUrl(getStageUrl() + "/" + subObject.get("stageId").getAsString() + "/" + stage.getAttemptId() + "/taskList?length=" + l);
                stage.setStatus(subObject.get("status").getAsString());
                stage.setSubmitTime(subObject.get("submissionTime").getAsString());
                stage.setCompleteTime(subObject.get("completionTime").getAsString());
                stage.setFirstTaskSubmit(subObject.get("firstTaskLaunchedTime").getAsString());

                double sh;
                double sm;
                double ss;
                double sd;
                double ch;
                double cm;
                double cs;
                double cd;

                sh = Integer.parseInt(stage.getFirstTaskSubmit().substring(11, 13));
                sm = Integer.parseInt(stage.getFirstTaskSubmit().substring(14, 16));
                ss = Integer.parseInt(stage.getFirstTaskSubmit().substring(17, 19));
                sd = Integer.parseInt(stage.getFirstTaskSubmit().substring(20, 23));

                ch = Integer.parseInt(stage.getCompleteTime().substring(11, 13));
                cm = Integer.parseInt(stage.getCompleteTime().substring(14, 16));
                cs = Integer.parseInt(stage.getCompleteTime().substring(17, 19));
                cd = Integer.parseInt(stage.getCompleteTime().substring(20, 23));

                double duration = (ch - sh) * 3600 + (cm - sm) * 60 + (cs - ss) + (cd - sd) / 1000;
                //System.out.println(ch);
                // System.out.println(sh);
                stage.setDuration(String.valueOf(duration));

                stageList.add(stage);

            }
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    public List<Stage> getStageList() {
        return stageList;
    }
}