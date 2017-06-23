package parser;

import appinfo.Job;
import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SingleJobJsonParser {


    private List<Job> jobsList = new ArrayList<Job>();

    private String jobUrl;

    public void setJobUrl(String masterIP, String jobUrl) {

        this.jobUrl = "http://" + masterIP + ":18080/api/v1/applications/" + jobUrl + "/jobs";
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public List<Job> getJobList() {
        return jobsList;
    }

    public void jobwsu() throws IOException {
        try {

            URL url = new URL(getJobUrl());
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
            //   System.out.println(s);


            JsonParser parser = new JsonParser();  //创建JSON解析器
            JsonElement el = parser.parse(s);
            JsonArray array = null;
            if (el.isJsonArray()) {
                array = el.getAsJsonArray();
            }

            /*
            for (int i = 0; i < array.size(); i++) {
                //   System.out.println("---------------");
                JsonObject subObject = array.get(i).getAsJsonObject();

                Job job = new Job();
                job.setJobID(subObject.get("jobId").getAsString());
                job.setName(subObject.get("name").getAsString());
                job.setSubmitTime(subObject.get("submissionTime").getAsString());
                job.setCompleteTime(subObject.get("completionTime").getAsString());
                double sh;
                double sm;
                double ss;
                double sd;
                double ch;
                double cm;
                double cs;
                double cd;
                sh = Integer.parseInt(job.getSubmitTime().substring(11, 13));
                sm = Integer.parseInt(job.getSubmitTime().substring(14, 16));
                ss = Integer.parseInt(job.getSubmitTime().substring(17, 19));
                sd = Integer.parseInt(job.getSubmitTime().substring(20, 23));
                ch = Integer.parseInt(job.getCompleteTime().substring(11, 13));
                cm = Integer.parseInt(job.getCompleteTime().substring(14, 16));
                cs = Integer.parseInt(job.getCompleteTime().substring(17, 19));
                cd = Integer.parseInt(job.getCompleteTime().substring(20, 23));

                double duration = (ch - sh) * 3600 + (cm - sm) * 60 + (cs - ss) + (cd - sd) / 1000;
                System.out.println(ch);
                System.out.println(sh);
                job.setDuration(String.valueOf(duration));
                jobsList.add(job);

            }
            for (int i = 0; i < jobsList.size(); i++) {
                System.out.println("---------------");
                System.out.println("id=" + jobsList.get(i).getJobID());
                // System.out.println("time=" + app.getDuration())
                System.out.println("name=" + jobsList.get(i).getName());
                System.out.println("sub=" + jobsList.get(i).getSubmitTime());
                System.out.println("com=" + jobsList.get(i).getCompleteTime());
                System.out.println("duration=" + jobsList.get(i).getDuration());

            }
            */
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}