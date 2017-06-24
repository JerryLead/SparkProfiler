package parser;

import appinfo.Application;
import util.HtmlFetcher;
import util.HtmlJsonWriter;

import java.io.File;

/**
 * Created by xulijie on 17-6-24.
 */
public class AppJsonParser {

    private String appId;
    private String appURL;

    private Application app;
    private String appDir;

    public AppJsonParser(String masterIP, String appId) {
        this.appId = appId;
        // http://masterIP:18080/api/v1/applications/app-20170618202557-0295
        appURL = "http://" + masterIP + ":18080/api/v1/applications/" + appId;
    }

    public void saveAppJson(String outputDir) {
        String appJson = HtmlFetcher.fetch(appURL);

        app = new Application(appJson);

        // Save the app json to "profiles/WordCount-CMS-4-28_app-20170618202557-0295/"
        appDir = outputDir + File.separatorChar + app.getName() + "_" + appId;
        String appJsonFile = appDir + File.separatorChar + "application.json";
        HtmlJsonWriter.write(appJsonFile, appJson);

        // Save jobs json
        JobsJsonParser jobJsonParser = new JobsJsonParser(appURL, appDir, app);
        jobJsonParser.saveJobsJson();
    }
}
