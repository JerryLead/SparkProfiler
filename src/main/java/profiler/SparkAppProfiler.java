package profiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SparkAppProfiler {

    private boolean useAppList;
    private String appJsonDir;
    private Set<String> appIdSet;

    // e.g.,
    // app-20170622150508-0330
    // app-20170622143730-0331


    public SparkAppProfiler(boolean useAppList, String appJsonDir) {
        this.useAppList = useAppList;
        this.appJsonDir = appJsonDir;
    }

    public void setAppSet(Set<String> appIdSet) {
        this.appIdSet = appIdSet;
    }

    public Set<String> parseAppIdList(String appIdsFile) {
        BufferedReader br;
        Set<String> appIdSet = new TreeSet<String>();

        try {
            br = new BufferedReader(new FileReader(appIdsFile));

            String appId;

            while ((appId = br.readLine()) != null) {
                if (appId.startsWith("app"))
                    appIdSet.add(appId.trim());
            }

            if(appIdSet.size() == 0) {
                System.err.println("None apps to be profile, exit!");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return appIdSet;
    }

    public void profileApps() {

        File appDir = new File(appJsonDir);

        for (File appJsonFile : appDir.listFiles()) {
            if (appJsonFile.isDirectory() && appJsonFile.getName().contains("app")) {
                if (useAppList) {
                    if (appIdSet.contains(appJsonFile.getName()))
                        profileApp(appJsonFile);
                } else {
                    profileApp(appJsonFile);
                }
            }

        }
    }

    private void profileApp(File appJsonFile) {
    }


    public static void main(String args[]) {

        // 1. Users can specify the appIds to be profiled using "useAppList = true" and "appList.txt".
        // 2. If useAppList, all the applications in the appJsonDir will be profiled.
        boolean useAppList = true;
        // Users need to specify the appIds to be profiled
        String appIdsFile = "/Users/xulijie/Documents/GCResearch/Experiments/applists/appList.txt";

        String appJsonDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/";



        SparkAppProfiler profiler = new SparkAppProfiler(useAppList, appJsonDir);

        if (useAppList) {
            // Obtain the appIds from the file (a list of appIds)
            Set<String> appIdSet = profiler.parseAppIdList(appIdsFile);
            profiler.setAppSet(appIdSet);
        }


        // Profile the app based on the saved json and output the profiles
        profiler.profileApps();
    }



}
