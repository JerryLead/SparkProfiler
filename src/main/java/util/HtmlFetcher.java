package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HtmlFetcher {

    public static String fetch(String siteURL) {

        HttpURLConnection connection = null;

        StringBuilder response = new StringBuilder();

        try {

            URL url = new URL(siteURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // connection.setConnectTimeout(8000);
            // connection.setReadTimeout(8000);
            String line;

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            while ((line = reader.readLine()) != null) {
                response.append(line).append("\r\n");
            }

            reader.close();

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return response.toString();
    }

    public static List<String> fetchLines(String siteURL) {
        List<String> list = new ArrayList<String>();

        HttpURLConnection connection = null;

        try {

            URL url = new URL(siteURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // connection.setConnectTimeout(8000);
            // connection.setReadTimeout(8000);
            String line;

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            while ((line = reader.readLine()) != null) {
                list.add(line);

            }

            reader.close();

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return list;
    }
}
