package util;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import appinfo.Application;
import com.google.gson.*;

import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;


/**
 * Created by Lijie on 2017/2/13.
 * Modified by YE on 2017/3/20.
 */
public class JsonFetcher {

    private List<Application> applicationsList = new ArrayList<Application>();

    public void applicationwsu() throws IOException {
        URL url = new URL("http://47.92.71.43:18080/api/v1/applications/");
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
        System.out.println(s);

        try {

            JsonParser parser = new JsonParser();  //创建JSON解析器
            JsonElement el = parser.parse(s);
            JsonArray array = null;
            if (el.isJsonArray()) {
                array = el.getAsJsonArray();
            }

            System.out.println(array.size());
            for (int i = 0; i < array.size(); i++) {
                Application app = new Application();
                System.out.println("---------------");
                JsonObject subObject = array.get(i).getAsJsonObject();
                JsonArray subArray = subObject.get("attempts").getAsJsonArray();
                JsonObject subsubObject = subArray.get(0).getAsJsonObject();
                //  JsonArray  subArray=array.get(i).ge
                app.setApplicationID(subObject.get("id").getAsString());
                app.setDuration(subsubObject.get("duration").getAsString());
                app.setName(subObject.get("name").getAsString());
                app.setStageUrl(subObject.get("id").getAsString());
                app.setJobUrl(subObject.get("id").getAsString());

                System.out.println("id=" + app.getApplicationID());
                System.out.println("time=" + app.getDuration());
                System.out.println("url=" + app.getStageUrl());
                System.out.println("url=" + app.getJobUrl());
                System.out.println("name=" + app.getName());
                applicationsList.add(app);
            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public List<Application> getApplicationsList() {
        return applicationsList;
    }
}
