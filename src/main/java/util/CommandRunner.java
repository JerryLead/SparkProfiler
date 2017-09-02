package util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CommandRunner {

    public static void exec(String cmd) {
        System.out.println(cmd);
        Runtime run = Runtime.getRuntime();
        try {
            Process p = run.exec(cmd);

            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
            String lineStr;
            while ((lineStr = inBr.readLine()) != null)
                System.out.println(lineStr);

            if (p.waitFor() != 0) {
                if (p.exitValue() == 1)
                    System.err.println("Error in executing " + cmd);
            }
            inBr.close();
            in.close();

            System.out.println("[Done] " + cmd);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String execCurl(String cmd) {
        System.out.println("[Executing] " + cmd);
        StringBuilder sb = new StringBuilder();

        Runtime run = Runtime.getRuntime();
        try {
            Process p = run.exec(cmd);

            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
            String lineStr;
            while ((lineStr = inBr.readLine()) != null)
                sb.append(lineStr);

            if (p.waitFor() != 0) {
                if (p.exitValue() == 1)
                    System.err.println("Error in executing " + cmd);
            }
            inBr.close();
            in.close();

            System.out.println("[Done] " + cmd);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }


    public static void main(String[] args) {
        exec("curl --help");
    }

}
