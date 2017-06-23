package util;

import java.io.*;

/**
 * Created by xulijie on 17-6-23.
 */
public class HtmlJsonWriter {

    public static void write(String file, String html) {
        File outputFile = new File(file);
        if (!outputFile.getParentFile().exists())
            outputFile.getParentFile().mkdirs();

        try {

            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
            writer.print(html);
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
