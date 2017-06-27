package parser;

import appinfo.Application;
import appinfo.Executor;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


public class ExecutorsJsonParser {

    public void parseExecutorsJson(String allexecutorsJson, Application app) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(allexecutorsJson);
            for (JsonElement executorElem : el.getAsJsonArray()) {
                Executor executor = new Executor(executorElem.getAsJsonObject());
                app.addExecutor(executor);
            }

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
}
