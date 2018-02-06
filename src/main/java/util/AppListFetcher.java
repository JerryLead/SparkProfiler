package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulijie on 17-7-22.
 */
public class AppListFetcher {

    // [startId, endId]
    public static List<String> fetch(String siteURL) {
        List<String> appList = new ArrayList<String>();
        List<String> lines = HtmlFetcher.fetchLines(siteURL);
        for (String line: lines) {
            if (line.trim().startsWith("<a href=\"app?appId=")) {
                int start = line.indexOf(">") + 1;
                int end = line.lastIndexOf("<");
                String appId = line.substring(start, end);
                System.out.println(appId);
            }
        }


        // <a href="app?appId=app-20170721181818-0089">app-20170721181818-0089</a>
        return appList;
    }

    public static List<String> fetchLocalFile(String siteURL, int startId, int endId) {
        List<String> appList = new ArrayList<String>();
        List<String> lines = JsonFileReader.readFileLines(siteURL);

        for (String line: lines) {
            if (line.trim().contains("app?appId=")) {
                int start = line.indexOf(">") + 1;
                int end = line.lastIndexOf("<");
                String appId = line.substring(start, end);
                System.out.println(appId);
            }
        }


        // <a href="app?appId=app-20170721181818-0089">app-20170721181818-0089</a>
        return appList;
    }

    public static void main(String[] args) {
        String url = "http://aliMaster:8080/";
        url = "/Users/xulijie/Documents/GCResearch/Experiments-11-17/MasterUI/SVM-1.0-6.5G-G1/ISCAS Spark Master at spark___master_7077.htm";
        // fetch(url);
        fetchLocalFile(url, 0, 0);
    }
}
