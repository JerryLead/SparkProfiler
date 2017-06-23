package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
}
