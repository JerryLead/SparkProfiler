package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JsonFileReader {

    public static String readFile(String fileName) {
        BufferedReader br = null;
        FileReader fr = null;
        StringBuilder sb = new StringBuilder();

        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));

            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine).append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static List<String> readFileLines(String fileName) {
        BufferedReader br = null;
        FileReader fr = null;
        List<String> lines = new ArrayList<String>();

        if (new File(fileName).exists() == false)
            return lines;

        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));

            while ((sCurrentLine = br.readLine()) != null) {
                lines.add(sCurrentLine);
            }
        } catch (IOException e) {
            System.err.println("Error in reading file: " + fileName);
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return lines;
    }
}
