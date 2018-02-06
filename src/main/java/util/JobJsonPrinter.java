package util;

import java.io.File;

/**
 * Created by xulijie on 17-8-2.
 */

public class JobJsonPrinter {

    public static void main(String[] args) {
        String appJsonDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/SVM-0.5";

        File appDir = new File(appJsonDir);

        for (File appJsonFile : appDir.listFiles()) {
            if (appJsonFile.isDirectory() && appJsonFile.getName().contains("app")) {

                // RDDJoin-CMS-4-28G-0.5_app-20170623114155-0011
                String fileName = appJsonFile.getName();
                String appId = fileName.substring(fileName.lastIndexOf("app"));
                File jobFile = new File(appJsonFile, "jobs.json");

                String lines = JsonFileReader.readFile(jobFile.getAbsolutePath());

                System.out.println("[app] " + fileName);
                System.out.println(lines);
                System.out.println("");
            }
        }
    }
}
