package util;

import java.util.List;

/**
 * Created by xulijie on 17-7-18.
 */
public class FileChecker {

    public static boolean isGCFile(String file) {
        List<String> lines = JsonFileReader.readFileLines(file);
        return lines.size() > 1;
    }
}
