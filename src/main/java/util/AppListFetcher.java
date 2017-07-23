package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulijie on 17-7-22.
 */
public class AppListFetcher {

    // [startId, endId]
    public static List<String> fetch(String siteURL, int startId, int endId) {
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

    public static void main(String[] args) {
        String url = "http://aliMaster:8080/";
        fetch(url, 0, 0);
    }
}
