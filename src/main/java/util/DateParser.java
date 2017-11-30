package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by xulijie on 17-6-23.
 */

public class DateParser {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'GMT'", Locale.CHINA);
    // e.g., date = "2017-05-30T16:25:43.699GMT"



    public static long parseDate(String dateString) {
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime();
    }

    /*
     "submissionTime" : "2017-05-30T16:25:43.699GMT"
     "completionTime" : "2017-05-30T16:25:44.455GMT"
     return duration = completionTime - submissionTime (ms)
     */
    public static long durationMS(String submissionTime, String completionTime) {
        long start = parseDate(submissionTime);
        long end = parseDate(completionTime);

        return end - start;
    }

    public static String getDate(long time) {
        SimpleDateFormat sdfChina = new SimpleDateFormat("HH:mm:ss");
        sdfChina.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        Date date = new Date(time);
        return sdfChina.format(date);
    }

    public static int getTimeValue(String hhmmss) {
        int value = 0;

        for (String s : hhmmss.split(":")) {
            value = Integer.parseInt(s.trim()) + value * 60;
        }

        return value;
    }

    public static void main(String[] args) {
        String startTime = "2017-11-20T11:51:06.245GMT";
        long duration = 4677;
        long endMS = DateParser.parseDate(startTime) + duration;

        String endTime = getDate(endMS);
        System.out.println("start = " + getDate(DateParser.parseDate(startTime)));
        System.out.println("end = " + endTime);

        System.out.println("03:02:30 = " + getTimeValue("03:02:30"));
        System.out.println("11:22:30 = " + getTimeValue("11:22:30"));
        System.out.println("23:59:59 = " + getTimeValue("23:59:59"));
        System.out.println("00:00:00 = " + getTimeValue("24:00:00"));
        System.out.println("00:01:59 = " + getTimeValue("00:01:59"));
        System.out.println("00:02:00 = " + getTimeValue("00:02:00"));


    }
}
