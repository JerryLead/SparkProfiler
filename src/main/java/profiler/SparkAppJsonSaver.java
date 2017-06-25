package profiler;

import parser.AppJsonParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SparkAppJsonSaver {
    private String masterIP = "";

    // e.g.,
    // app-20170622150508-0330
    // app-20170622143730-0331
    private List<String> appIdList = new ArrayList<String>();

    private String prefix;


    public SparkAppJsonSaver(String masterIP) {
        this.masterIP = masterIP;
        prefix = "http://" + masterIP + ":18080/api/v1";
    }

    public void parseAppIdList(String appIdsFile) {
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(appIdsFile));

            String appId;

            while ((appId = br.readLine()) != null) {
                if (appId.startsWith("app"))
                    appIdList.add(appId.trim());
            }

            if(appIdList.size() == 0) {
                System.err.println("None apps to be profile, exit!");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * The following app pages will be saved:
     * /applications/[app-id], including the application information.
     * /applications/[app-id]/jobs, including the information of each job.
     * /applications/[app-id]/stages, including the information of each stage.
     * /applications/[app-id]/stages/[stage-id]/[stage-attempt-id], including the tasks and executorSummary.
     * /applications/[app-id]/stages/[stage-id]/[stage-attempt-id]/taskSummary, including the taskSummary.
     **/

    public void saveAppJsonInfo(String outputDir) {

        for (String appId : appIdList) {
            AppJsonParser appJsonParser = new AppJsonParser(masterIP, appId);
            appJsonParser.saveAppJson(outputDir);
            System.out.println("[Done] The json information of " + appId + " has been saved into " + outputDir);
        }
    }

    public static void main(String args[]) {


        String masterIP = "";

        // Users need to specify the appIds to be profiled
        String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments/applists/appList.txt";
        String outputDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/";

        SparkAppJsonSaver saver = new SparkAppJsonSaver(masterIP);

        // Obtain the appIds from the file (a list of appIds)
        saver.parseAppIdList(appIdsFile);

        // Save the app's jsons info into the outputDir
        saver.saveAppJsonInfo(outputDir);
    }

}
