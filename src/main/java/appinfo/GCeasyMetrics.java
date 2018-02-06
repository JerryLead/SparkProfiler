package appinfo;

/**
 * Created by xulijie on 17-9-2.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * https://blog.gceasy.io/2016/06/18/garbage-collection-log-analysis-api/
{
        {
        "isProblem": true,
        "problem": [
        "Our analysis tells that Full GCs are consecutively running in your application. It might cause intermittent OutOfMemoryErrors or degradation in response time or high CPU consumption or even make application unresponsive.",
        "Our analysis tells that your application is suffering from long running GC events. 4 GC events took more than 10 seconds. Long running GCs are unfavourable for application's performance.",
        "342 times application threads were stopped for more than 10 seconds."
        ],
        "jvmHeapSize": {
        "youngGen": {
        "allocatedSize": "7.5 gb",
        "peakSize": "6 gb"
        },
        "oldGen": {
        "allocatedSize": "22.5 gb",
        "peakSize": "22.5 gb"
        },
        "metaSpace": {
        "allocatedSize": "1.04 gb",
        "peakSize": "48.52 mb"
        },
        "total": {
        "allocatedSize": "30 gb",
        "peakSize": "28.5 gb"
        }
        },
        "gcStatistics": {
        "totalCreatedBytes": "249.49 gb",
        "measurementDuration": "7 hrs 32 min 52 sec",
        "avgAllocationRate": "9.4 mb/sec",
        "avgPromotionRate": "1.35 mb/sec",
        "minorGCCount": "62",
        "minorGCTotalTime": "1 min 19 sec",
        "minorGCAvgTime": "1 sec 274 ms",
        "minorGCAvgTimeStdDeviation": "2 sec 374 ms",
        "minorGCMinTIme": "0",
        "minorGCMaxTime": "13 sec 780 ms",
        "minorGCIntervalAvgTime": "7 min 25 sec 442 ms",
        "fullGCCount": "166",
        "fullGCTotalTime": "14 min 11 sec 620 ms",
        "fullGCAvgTime": "5 sec 130 ms",
        "fullGCAvgTimeStdDeviation": "5 sec 207 ms",
        "fullGCMinTIme": "120 ms",
        "fullGCMaxTime": "57 sec 880 ms",
        "fullGCIntervalAvgTime": "2 min 19 sec 104 ms"
        },
        "gcDurationSummary": {
        "groups": [
        {
        "start": "0",
        "end": "6",
        "numberOfGCs": 212
        },
        {
        "start": "6",
        "end": "12",
        "numberOfGCs": 4
        },
        {
        "start": "12",
        "end": "18",
        "numberOfGCs": 2
        },
        {
        "start": "42",
        "end": "48",
        "numberOfGCs": 1
        },
        {
        "start": "54",
        "end": "60",
        "numberOfGCs": 1
        }
        ]
        },
        "gcCauses": [
        {
        "cause": "Allocation Failure",
        "count": 57
        },
        {
        "cause": "Concurrent Mode Failure",
        "count": 162
        },
        {
        "cause": "Full GC - Allocation Failure",
        "count": 369
        }
        ],
        "commandLineFlags": " -XX:CMSInitiatingOccupancyFraction=65 -XX:+CMSScavengeBeforeRemark -XX:CMSWaitDuration=2000 -XX:ConcGCThreads=8 -XX:+DisableExplicitGC -XX:GCLogFileSize=104857600 -XX:+HeapDumpOnOutOfMemoryError -XX:InitialHeapSize=32212254720 -XX:InitialTenuringThreshold=4 -XX:+ManagementServer -XX:MaxHeapSize=32212254720 -XX:MaxTenuringThreshold=4 -XX:NewRatio=3 -XX:NumberOfGCLogFiles=10 -XX:OldPLABSize=16 -XX:ParGCCardsPerStrideChunk=32768 -XX:+PrintClassHistogram -XX:+PrintGC -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintPromotionFailure -XX:+PrintTenuringDistribution -XX:SurvivorRatio=3 -XX:ThreadStackSize=334 -XX:+UnlockDiagnosticVMOptions -XX:+UseCMSInitiatingOccupancyOnly -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseConcMarkSweepGC -XX:+UseGCLogFileRotation -XX:+UseParNewGC ",
        "heapTuningTips": [
        "It looks like you have over allocated Metaspace size. During entire run, Metaspace's peak utilization was only 4.53% of the allocated size. You can consider lowering the Metaspace Size."
        ],
        "tipsToReduceGCTime": [
        {
        "issue": "15.34% of GC time (i.e 54 min 13 sec 710 ms) is caused by 'Concurrent Mode Failure'. The CMS collector uses one or more garbage collector threads that run simultaneously with the application threads with the goal of completing the collection of the tenured generation before it becomes full. In normal operation, the CMS collector does most of its tracing and sweeping work with the application threads still running, so only brief pauses are seen by the application threads. However, if the CMS collector is unable to finish reclaiming the unreachable objects before the tenured generation fills up, or if an allocation cannot be satisfied with the available free space blocks in the tenured generation, then the application is paused and the collection is completed with all the application threads stopped. The inability to complete a collection concurrently is referred to as concurrent mode failure and indicates the need to adjust the CMS collector parameters. Concurrent mode failure typically triggers Full GC..",
        "solution": "The concurrent mode failure can either be avoided by increasing the tenured generation size or initiating the CMS collection at a lesser heap occupancy by setting CMSInitiatingOccupancyFraction to a lower value and setting UseCMSInitiatingOccupancyOnly to true. CMSInitiatingOccupancyFraction should be chosen carefuly, setting it to a low value will result in too frequent CMS collections."
        }
        ],
        "throughputPercentage": 99.996,
        "responseId": "8296b5c3-25c7-4157-92df-a54d9083bab7",
        "graphURL": "http://gceasy.io/my-gc-report.jsp?p=YXJjaGl2ZWQvMjAxNy8wMi8xNy8tLWFwaS1lMDk0YTM0ZS1jM2ViLTRjOWEtODI1NC1mMGRkMTA3MjQ1Y2NjOWU0NGEzMS0yMDg2LTRhMzAtOWU5YS1jMDc0ZWQ4MWNlZjgudHh0LS0=&channel=API"
        }
*/
public class GCeasyMetrics {

    private boolean isProblem;
    private String problem;

    private double jvmHeapSize_youngGen_allocatedSize; // 7.5 gb
    private double jvmHeapSize_youngGen_peakSize; // 6 gb
    private double jvmHeapSize_oldGen_allocatedSize; // 22.5 gb
    private double jvmHeapSize_oldGen_peakSize; // 22.5 gb
    private double jvmHeapSize_metaSpace_allocatedSize; // 1.04 gb
    private double jvmHeapSize_metaSpace_peakSize; // 48.52 mb
    private double jvmHeapSize_total_allocatedSize; // 30 gb
    private double jvmHeapSize_total_peakSize; // 28.5 gb

    private double gcStatistics_totalCreatedBytes; // 249.49 gb
    private double gcStatistics_measurementDuration; // 7 hrs 32 min 52 sec,
    private double gcStatistics_avgAllocationRate; // 9.4 mb/sec
    private double gcStatistics_avgPromotionRate; // 1.35 mb/sec
    private long gcStatistics_minorGCCount; // 62
    private double gcStatistics_minorGCTotalTime; // 1 min 19 sec
    private double gcStatistics_minorGCAvgTime; // 1 sec 274 ms
    private double gcStatistics_minorGCAvgTimeStdDeviation; // 2 sec 374 ms
    private double gcStatistics_minorGCMinTIme; // 0
    private double gcStatistics_minorGCMaxTime; // 13 sec 780 ms
    private double gcStatistics_minorGCIntervalAvgTime; // 7 min 25 sec 442 ms
    private long gcStatistics_fullGCCount; // 166
    private double gcStatistics_fullGCTotalTime; // 14 min 11 sec 620 ms
    private double gcStatistics_fullGCAvgTime; // 5 sec 130 ms
    private double gcStatistics_fullGCAvgTimeStdDeviation; // 5 sec 207 ms
    private double gcStatistics_fullGCMinTIme; // 120 ms
    private double gcStatistics_fullGCMaxTime; // 57 sec 880 ms
    private double gcStatistics_fullGCIntervalAvgTime; // 2 min 19 sec 104 ms
    private double throughputPercentage; // 99.996

    // private List<Group> gcDurationSummary_groups = new ArrayList<Group>();
    // private List<GcCause> gcCauses = new ArrayList<GcCause>();
    private String gcDurationSummary_groups;
    private String gcCauses;
    private String commandLineFlags;
    private String heapTuningTips;
    private String tipsToReduceGCTime;

    private String responseId;
    private String graphURL;


    public void parseJson(String json) {
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(json);

        if (el.isJsonObject()) {
            JsonObject root = el.getAsJsonObject();
            parseJsonObject(root);
        }
    }

    private void parseJsonObject(JsonObject root) {
        this.isProblem = root.get("isProblem").getAsBoolean();
        if (isProblem)
            this.problem = root.get("problem").getAsJsonArray().toString();

        JsonObject jvmHeapSizeObj = root.getAsJsonObject("jvmHeapSize");
        this.jvmHeapSize_youngGen_allocatedSize = parseMemoryString(jvmHeapSizeObj.getAsJsonObject("youngGen")
                .get("allocatedSize").getAsString());
        this.jvmHeapSize_youngGen_peakSize = parseMemoryString(jvmHeapSizeObj.getAsJsonObject("youngGen")
                .get("peakSize").getAsString());
        this.jvmHeapSize_oldGen_allocatedSize = parseMemoryString(jvmHeapSizeObj.getAsJsonObject("oldGen")
                .get("allocatedSize").getAsString());
        this.jvmHeapSize_oldGen_peakSize = parseMemoryString(jvmHeapSizeObj.getAsJsonObject("oldGen")
                .get("peakSize").getAsString());
        if(jvmHeapSizeObj.getAsJsonObject("metaSpace") != null) {
            this.jvmHeapSize_metaSpace_allocatedSize = parseMemoryString(jvmHeapSizeObj.getAsJsonObject("metaSpace")
                    .get("allocatedSize").getAsString());
            this.jvmHeapSize_metaSpace_peakSize = parseMemoryString(jvmHeapSizeObj.getAsJsonObject("metaSpace")
                    .get("peakSize").getAsString());
        }

        this.jvmHeapSize_total_allocatedSize = parseMemoryString(jvmHeapSizeObj.getAsJsonObject("total")
                .get("allocatedSize").getAsString());
        this.jvmHeapSize_total_peakSize = parseMemoryString(jvmHeapSizeObj.getAsJsonObject("total")
                .get("peakSize").getAsString());

        JsonObject gcStatisticsObj = root.getAsJsonObject("gcStatistics");
        this.gcStatistics_totalCreatedBytes = parseMemoryString(gcStatisticsObj.get("totalCreatedBytes").getAsString());
        this.gcStatistics_measurementDuration = parseTimeString(gcStatisticsObj.get("measurementDuration").getAsString());
        if (gcStatisticsObj.get("avgAllocationRate") != null)
            this.gcStatistics_avgAllocationRate = parseRateString(gcStatisticsObj.get("avgAllocationRate").getAsString());
        if (gcStatisticsObj.get("avgPromotionRate") != null)
            this.gcStatistics_avgPromotionRate = parseRateString(gcStatisticsObj.get("avgPromotionRate").getAsString());
        this.gcStatistics_minorGCCount = Long.parseLong(gcStatisticsObj.get("minorGCCount").getAsString());
        this.gcStatistics_minorGCTotalTime = parseTimeString(gcStatisticsObj.get("minorGCTotalTime").getAsString());
        this.gcStatistics_minorGCAvgTime = parseTimeString(gcStatisticsObj.get("minorGCAvgTime").getAsString());
        this.gcStatistics_minorGCAvgTimeStdDeviation = parseTimeString(gcStatisticsObj.get("minorGCAvgTimeStdDeviation").getAsString());
        this.gcStatistics_minorGCMinTIme = parseTimeString(gcStatisticsObj.get("minorGCMinTIme").getAsString());
        this.gcStatistics_minorGCMaxTime = parseTimeString(gcStatisticsObj.get("minorGCMaxTime").getAsString());
        this.gcStatistics_minorGCIntervalAvgTime = parseTimeString(gcStatisticsObj.get("minorGCIntervalAvgTime").getAsString());
        this.gcStatistics_fullGCCount = Long.parseLong(gcStatisticsObj.get("fullGCCount").getAsString());
        if (gcStatisticsObj.get("fullGCTotalTime") != null)
            this.gcStatistics_fullGCTotalTime = parseTimeString(gcStatisticsObj.get("fullGCTotalTime").getAsString());
        if (gcStatisticsObj.get("fullGCAvgTime") != null)
            this.gcStatistics_fullGCAvgTime = parseTimeString(gcStatisticsObj.get("fullGCAvgTime").getAsString());
        if (gcStatisticsObj.get("fullGCAvgTimeStdDeviation") != null)
            this.gcStatistics_fullGCAvgTimeStdDeviation = parseTimeString(gcStatisticsObj.get("fullGCAvgTimeStdDeviation").getAsString());
        if (gcStatisticsObj.get("fullGCMinTIme") != null)
            this.gcStatistics_fullGCMinTIme = parseTimeString(gcStatisticsObj.get("fullGCMinTIme").getAsString());
        if (gcStatisticsObj.get("fullGCMaxTime") != null)
            this.gcStatistics_fullGCMaxTime = parseTimeString(gcStatisticsObj.get("fullGCMaxTime").getAsString());
        if (gcStatisticsObj.get("fullGCIntervalAvgTime") != null)
            this.gcStatistics_fullGCIntervalAvgTime = parseTimeString(gcStatisticsObj.get("fullGCIntervalAvgTime").getAsString());

        if (root.get("throughputPercentage") != null)
            this.throughputPercentage = root.get("throughputPercentage").getAsDouble();

        /*
        JsonObject gcDurationSummaryObj = root.getAsJsonObject("gcDurationSummary");
        JsonArray groupArray = gcDurationSummaryObj.get("groups").getAsJsonArray();
        for (JsonElement elem : groupArray) {
            JsonObject obj = elem.getAsJsonObject();
            double start = Double.parseDouble(obj.get("start").getAsString().replaceAll(",", ""));
            double end = Double.parseDouble(obj.get("end").getAsString().replaceAll(",", ""));
            int numberOfGCs = obj.get("numberOfGCs").getAsInt();
            gcDurationSummary_groups.add(new Group(start, end, numberOfGCs));
        }

        JsonArray gcCausesArray = root.get("gcCauses").getAsJsonArray();
        for (JsonElement elem: gcCausesArray) {
            JsonObject obj = elem.getAsJsonObject();
            String cause = obj.get("cause").getAsString();
            int count = obj.get("count").getAsInt();
            gcCauses.add(new GcCause(cause, count));
        }
        */

        JsonObject gcDurationSummaryObj = root.getAsJsonObject("gcDurationSummary");
        gcDurationSummary_groups = gcDurationSummaryObj.toString();

        JsonArray gcCausesArray = root.get("gcCauses").getAsJsonArray();
        gcCauses = gcCausesArray.toString();

        if (root.get("commandLineFlags") != null)
            this.commandLineFlags = root.get("commandLineFlags").getAsString();
        if (root.get("heapTuningTips") != null)
            this.heapTuningTips = root.get("heapTuningTips").getAsJsonArray().toString();
        if (root.get("tipsToReduceGCTime") != null)
            this.tipsToReduceGCTime = root.get("tipsToReduceGCTime").getAsJsonArray().toString();

        this.responseId = root.get("responseId").getAsString();
        this.graphURL = root.get("graphURL").getAsString();

        /*
        if (isProblem) {
            System.err.println("[Executor.isProblem=true] " + root);
        }
        */
    }

    // return MB
    private double parseMemoryString(String memorySizeStr) {
        // 7.5 gb 48.52 mb 3.5 kb
        String[] sizes = memorySizeStr.replaceAll(",", "").trim().split("\\s+");
        double size = 0;
        if (sizes.length < 2) {
            System.err.println("[Error] while parsing memorySizeStr " + memorySizeStr);
            return size;
        }

        for (int i = sizes.length - 1; i > 0; i--) {
            String unit = sizes[i];
            if (unit.equalsIgnoreCase("kb"))
                size += Double.parseDouble(sizes[i - 1]) / 1024;
            else if (unit.equalsIgnoreCase("mb"))
                size += Double.parseDouble(sizes[i - 1]);
            else if (unit.equalsIgnoreCase("gb"))
                size += Double.parseDouble(sizes[i - 1]) * 1024;
            i--;
        }

        return size;
    }

    // return seconds
    private double parseTimeString(String timeStr) {
        // 7 hrs 32 min 52 sec 32 ms
        String[] times = timeStr.trim().split("\\s+");
        double seconds = 0;
        if (times.length < 2) {
            // System.err.println("[Error] while parsing parseTimeString " + timeStr);
            return seconds;
        }

        for (int i = times.length - 1; i > 0; i--) {
            String unit = times[i];
            if (unit.equalsIgnoreCase("ms"))
                seconds += Double.parseDouble(times[i - 1]) / 1000;
            else if (unit.equalsIgnoreCase("sec"))
                seconds += Double.parseDouble(times[i - 1]);
            else if (unit.equalsIgnoreCase("min"))
                seconds += Double.parseDouble(times[i - 1]) * 60;
            else if (unit.equalsIgnoreCase("hrs"))
                seconds += Double.parseDouble(times[i - 1]) * 60 * 60;
            else if (unit.equalsIgnoreCase("hr"))
                seconds += Double.parseDouble(times[i - 1]) * 60 * 60;
            i--;
        }

        return seconds;
    }

    // return mb/sec
    private double parseRateString(String rateStr) {
        // 1.35 mb/sec
        String[] rates = rateStr.replaceAll(",", "").trim().split("\\s+");
        double rate = Double.parseDouble(rates[0]);

        if (rates.length < 2) {
            System.err.println("[Error] while parsing rateStr " + rateStr);
            return rate;
        }

        String unit = rates[1];

        if (unit.equalsIgnoreCase("gb/sec"))
            rate *= 1024;
        else if (unit.equalsIgnoreCase("kb/sec"))
            rate /= 1024;
        else if (unit.endsWith("min"))
            rate *= 60;

        return rate;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("isProblem = " + isProblem + "\n");
        sb.append("problem = " + problem + "\n");
        sb.append("jvmHeapSize_youngGen_allocatedSize = " + jvmHeapSize_youngGen_allocatedSize + "\n");
        sb.append("jvmHeapSize_youngGen_peakSize = " + jvmHeapSize_youngGen_peakSize + "\n");
        sb.append("jvmHeapSize_oldGen_allocatedSize = " + jvmHeapSize_oldGen_allocatedSize + "\n");
        sb.append("jvmHeapSize_oldGen_peakSize = " + jvmHeapSize_oldGen_peakSize + "\n");
        sb.append("jvmHeapSize_metaSpace_allocatedSize = " + jvmHeapSize_metaSpace_allocatedSize + "\n");
        sb.append("jvmHeapSize_metaSpace_peakSize = " + jvmHeapSize_metaSpace_peakSize + "\n");
        sb.append("jvmHeapSize_total_allocatedSize = " + jvmHeapSize_total_allocatedSize + "\n");
        sb.append("jvmHeapSize_total_peakSize = " + jvmHeapSize_total_peakSize + "\n");
        sb.append("gcStatistics_totalCreatedBytes = " + gcStatistics_totalCreatedBytes + "\n");
        sb.append("gcStatistics_measurementDuration = " + gcStatistics_measurementDuration + "\n");
        sb.append("gcStatistics_avgAllocationRate = " + gcStatistics_avgAllocationRate + "\n");
        sb.append("gcStatistics_avgPromotionRate = " + gcStatistics_avgPromotionRate + "\n");
        sb.append("gcStatistics_minorGCCount = " + gcStatistics_minorGCCount + "\n");
        sb.append("gcStatistics_minorGCTotalTime = " + gcStatistics_minorGCTotalTime + "\n");
        sb.append("gcStatistics_minorGCAvgTime = " + gcStatistics_minorGCAvgTime + "\n");
        sb.append("gcStatistics_minorGCAvgTimeStdDeviation = " + gcStatistics_minorGCAvgTimeStdDeviation + "\n");
        sb.append("gcStatistics_minorGCMinTIme = " + gcStatistics_minorGCMinTIme + "\n");
        sb.append("gcStatistics_minorGCMaxTime = " + gcStatistics_minorGCMaxTime + "\n");
        sb.append("gcStatistics_minorGCIntervalAvgTime = " + gcStatistics_minorGCIntervalAvgTime + "\n");
        sb.append("gcStatistics_fullGCCount = " + gcStatistics_fullGCCount + "\n");
        sb.append("gcStatistics_fullGCTotalTime = " + gcStatistics_fullGCTotalTime + "\n");
        sb.append("gcStatistics_fullGCAvgTime = " + gcStatistics_fullGCAvgTime + "\n");
        sb.append("gcStatistics_fullGCAvgTimeStdDeviation = " + gcStatistics_fullGCAvgTimeStdDeviation + "\n");
        sb.append("gcStatistics_fullGCMinTIme = " + gcStatistics_fullGCMinTIme + "\n");
        sb.append("gcStatistics_fullGCMaxTime = " + gcStatistics_fullGCMaxTime + "\n");
        sb.append("gcStatistics_fullGCIntervalAvgTime = " + gcStatistics_fullGCIntervalAvgTime + "\n");
        sb.append("throughputPercentage = " + throughputPercentage + "\n");

        sb.append("gcDurationSummary_groups = " + gcDurationSummary_groups + "\n");
        sb.append("gcCauses = " + gcCauses + "\n");

        sb.append("commandLineFlags = " + commandLineFlags + "\n");
        sb.append("heapTuningTips = " + heapTuningTips + "\n");
        sb.append("tipsToReduceGCTime = " + tipsToReduceGCTime + "\n");

        sb.append("responseId = " + responseId + "\n");
        sb.append("graphURL = " + graphURL + "\n");

        return sb.toString();
    }


    public static void main(String[] args) {
        String json =
                "{\n" +
                "  \"isProblem\": true,\n" +
                "  \"problem\": [\n" +
                "    \"Our analysis tells that Full GCs are consecutively running in your application. It might cause intermittent OutOfMemoryErrors or degradation in response time or high CPU consumption or even make application unresponsive.\",\n" +
                "    \"Our analysis tells that your application is suffering from long running GC events. 4 GC events took more than 10 seconds. Long running GCs are unfavourable for application's performance.\",\n" +
                "    \"342 times application threads were stopped for more than 10 seconds.\"\n" +
                "  ],\n" +
                "  \"jvmHeapSize\": {\n" +
                "    \"youngGen\": {\n" +
                "      \"allocatedSize\": \"7.5 gb\",\n" +
                "      \"peakSize\": \"6 gb\"\n" +
                "    },\n" +
                "    \"oldGen\": {\n" +
                "      \"allocatedSize\": \"22.5 gb\",\n" +
                "      \"peakSize\": \"22.5 gb\"\n" +
                "    },\n" +
                "    \"metaSpace\": {\n" +
                "      \"allocatedSize\": \"1.04 gb\",\n" +
                "      \"peakSize\": \"48.52 mb\"\n" +
                "    },\n" +
                "    \"total\": {\n" +
                "      \"allocatedSize\": \"30 gb\",\n" +
                "      \"peakSize\": \"28.5 gb\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"gcStatistics\": {\n" +
                "    \"totalCreatedBytes\": \"249.49 gb\",\n" +
                "    \"measurementDuration\": \"7 hrs 32 min 52 sec\",\n" +
                "    \"avgAllocationRate\": \"9.4 mb/sec\",\n" +
                "    \"avgPromotionRate\": \"1.35 mb/sec\",\n" +
                "    \"minorGCCount\": \"62\",\n" +
                "    \"minorGCTotalTime\": \"1 min 19 sec\",\n" +
                "    \"minorGCAvgTime\": \"1 sec 274 ms\",\n" +
                "    \"minorGCAvgTimeStdDeviation\": \"2 sec 374 ms\",\n" +
                "    \"minorGCMinTIme\": \"0\",\n" +
                "    \"minorGCMaxTime\": \"13 sec 780 ms\",\n" +
                "    \"minorGCIntervalAvgTime\": \"7 min 25 sec 442 ms\",\n" +
                "    \"fullGCCount\": \"166\",\n" +
                "    \"fullGCTotalTime\": \"14 min 11 sec 620 ms\",\n" +
                "    \"fullGCAvgTime\": \"5 sec 130 ms\",\n" +
                "    \"fullGCAvgTimeStdDeviation\": \"5 sec 207 ms\",\n" +
                "    \"fullGCMinTIme\": \"120 ms\",\n" +
                "    \"fullGCMaxTime\": \"57 sec 880 ms\",\n" +
                "    \"fullGCIntervalAvgTime\": \"2 min 19 sec 104 ms\"\n" +
                "  },\n" +
                "  \"gcDurationSummary\": {\n" +
                "    \"groups\": [\n" +
                "      {\n" +
                "        \"start\": \"0\",\n" +
                "        \"end\": \"6\",\n" +
                "        \"numberOfGCs\": 212\n" +
                "      },\n" +
                "      {\n" +
                "        \"start\": \"6\",\n" +
                "        \"end\": \"12\",\n" +
                "        \"numberOfGCs\": 4\n" +
                "      },\n" +
                "      {\n" +
                "        \"start\": \"12\",\n" +
                "        \"end\": \"18\",\n" +
                "        \"numberOfGCs\": 2\n" +
                "      },\n" +
                "      {\n" +
                "        \"start\": \"42\",\n" +
                "        \"end\": \"48\",\n" +
                "        \"numberOfGCs\": 1\n" +
                "      },\n" +
                "      {\n" +
                "        \"start\": \"54\",\n" +
                "        \"end\": \"60\",\n" +
                "        \"numberOfGCs\": 1\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"gcCauses\": [\n" +
                "    {\n" +
                "      \"cause\": \"Allocation Failure\",\n" +
                "      \"count\": 57\n" +
                "    },\n" +
                "    {\n" +
                "      \"cause\": \"Concurrent Mode Failure\",\n" +
                "      \"count\": 162\n" +
                "    },\n" +
                "    {\n" +
                "      \"cause\": \"Full GC - Allocation Failure\",\n" +
                "      \"count\": 369\n" +
                "    }\n" +
                "  ],\n" +
                "  \"commandLineFlags\": \" -XX:CMSInitiatingOccupancyFraction=65 -XX:+CMSScavengeBeforeRemark -XX:CMSWaitDuration=2000 -XX:ConcGCThreads=8 -XX:+DisableExplicitGC -XX:GCLogFileSize=104857600 -XX:+HeapDumpOnOutOfMemoryError -XX:InitialHeapSize=32212254720 -XX:InitialTenuringThreshold=4 -XX:+ManagementServer -XX:MaxHeapSize=32212254720 -XX:MaxTenuringThreshold=4 -XX:NewRatio=3 -XX:NumberOfGCLogFiles=10 -XX:OldPLABSize=16 -XX:ParGCCardsPerStrideChunk=32768 -XX:+PrintClassHistogram -XX:+PrintGC -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintPromotionFailure -XX:+PrintTenuringDistribution -XX:SurvivorRatio=3 -XX:ThreadStackSize=334 -XX:+UnlockDiagnosticVMOptions -XX:+UseCMSInitiatingOccupancyOnly -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseConcMarkSweepGC -XX:+UseGCLogFileRotation -XX:+UseParNewGC \",\n" +
                "  \"heapTuningTips\": [\n" +
                "    \"It looks like you have over allocated Metaspace size. During entire run, Metaspace's peak utilization was only 4.53% of the allocated size. You can consider lowering the Metaspace Size.\"\n" +
                "  ],\n" +
                "  \"tipsToReduceGCTime\": [\n" +
                "    {\n" +
                "      \"issue\": \"15.34% of GC time (i.e 54 min 13 sec 710 ms) is caused by 'Concurrent Mode Failure'. The CMS collector uses one or more garbage collector threads that run simultaneously with the application threads with the goal of completing the collection of the tenured generation before it becomes full. In normal operation, the CMS collector does most of its tracing and sweeping work with the application threads still running, so only brief pauses are seen by the application threads. However, if the CMS collector is unable to finish reclaiming the unreachable objects before the tenured generation fills up, or if an allocation cannot be satisfied with the available free space blocks in the tenured generation, then the application is paused and the collection is completed with all the application threads stopped. The inability to complete a collection concurrently is referred to as concurrent mode failure and indicates the need to adjust the CMS collector parameters. Concurrent mode failure typically triggers Full GC..\",\n" +
                "      \"solution\": \"The concurrent mode failure can either be avoided by increasing the tenured generation size or initiating the CMS collection at a lesser heap occupancy by setting CMSInitiatingOccupancyFraction to a lower value and setting UseCMSInitiatingOccupancyOnly to true. CMSInitiatingOccupancyFraction should be chosen carefuly, setting it to a low value will result in too frequent CMS collections.\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"throughputPercentage\": 99.996,\n" +
                "  \"responseId\": \"8296b5c3-25c7-4157-92df-a54d9083bab7\",\n" +
                "  \"graphURL\": \"http://gceasy.io/my-gc-report.jsp?p=YXJjaGl2ZWQvMjAxNy8wMi8xNy8tLWFwaS1lMDk0YTM0ZS1jM2ViLTRjOWEtODI1NC1mMGRkMTA3MjQ1Y2NjOWU0NGEzMS0yMDg2LTRhMzAtOWU5YS1jMDc0ZWQ4MWNlZjgudHh0LS0=&channel=API\"\n" +
                "}";

        GCeasyMetrics gCeasyMetrics = new GCeasyMetrics();
        gCeasyMetrics.parseJson(json);
        System.out.println(gCeasyMetrics.toString());

    }

    public boolean isProblem() {
        return isProblem;
    }

    public String getProblem() {
        return problem;
    }

    public double getJvmHeapSize_youngGen_allocatedSize() {
        return jvmHeapSize_youngGen_allocatedSize;
    }

    public double getJvmHeapSize_youngGen_peakSize() {
        return jvmHeapSize_youngGen_peakSize;
    }

    public double getJvmHeapSize_oldGen_allocatedSize() {
        return jvmHeapSize_oldGen_allocatedSize;
    }

    public double getJvmHeapSize_oldGen_peakSize() {
        return jvmHeapSize_oldGen_peakSize;
    }

    public double getJvmHeapSize_metaSpace_allocatedSize() {
        return jvmHeapSize_metaSpace_allocatedSize;
    }

    public double getJvmHeapSize_metaSpace_peakSize() {
        return jvmHeapSize_metaSpace_peakSize;
    }

    public double getJvmHeapSize_total_allocatedSize() {
        return jvmHeapSize_total_allocatedSize;
    }

    public double getJvmHeapSize_total_peakSize() {
        return jvmHeapSize_total_peakSize;
    }

    public double getGcStatistics_totalCreatedBytes() {
        return gcStatistics_totalCreatedBytes;
    }

    public double getGcStatistics_measurementDuration() {
        return gcStatistics_measurementDuration;
    }

    public double getGcStatistics_avgAllocationRate() {
        return gcStatistics_avgAllocationRate;
    }

    public double getGcStatistics_avgPromotionRate() {
        return gcStatistics_avgPromotionRate;
    }

    public long getGcStatistics_minorGCCount() {
        return gcStatistics_minorGCCount;
    }

    public double getGcStatistics_minorGCTotalTime() {
        return gcStatistics_minorGCTotalTime;
    }

    public double getGcStatistics_minorGCAvgTime() {
        return gcStatistics_minorGCAvgTime;
    }

    public double getGcStatistics_minorGCAvgTimeStdDeviation() {
        return gcStatistics_minorGCAvgTimeStdDeviation;
    }

    public double getGcStatistics_minorGCMinTIme() {
        return gcStatistics_minorGCMinTIme;
    }

    public double getGcStatistics_minorGCMaxTime() {
        return gcStatistics_minorGCMaxTime;
    }

    public double getGcStatistics_minorGCIntervalAvgTime() {
        return gcStatistics_minorGCIntervalAvgTime;
    }

    public long getGcStatistics_fullGCCount() {
        return gcStatistics_fullGCCount;
    }

    public double getGcStatistics_fullGCTotalTime() {
        return gcStatistics_fullGCTotalTime;
    }

    public double getGcStatistics_fullGCAvgTime() {
        return gcStatistics_fullGCAvgTime;
    }

    public double getGcStatistics_fullGCAvgTimeStdDeviation() {
        return gcStatistics_fullGCAvgTimeStdDeviation;
    }

    public double getGcStatistics_fullGCMinTIme() {
        return gcStatistics_fullGCMinTIme;
    }

    public double getGcStatistics_fullGCMaxTime() {
        return gcStatistics_fullGCMaxTime;
    }

    public double getGcStatistics_fullGCIntervalAvgTime() {
        return gcStatistics_fullGCIntervalAvgTime;
    }

    public double getThroughputPercentage() {
        return throughputPercentage;
    }

    /*
    public List<Group> getGcDurationSummary_groups() {
        return gcDurationSummary_groups;
    }

    public List<GcCause> getGcCauses() {
        return gcCauses;
    }
    */

    public String getGcDurationSummary_groups() {
        return gcDurationSummary_groups;
    }

    public String getGcCauses() {
        return gcCauses;
    }

    public String getCommandLineFlags() {
        return commandLineFlags;
    }

    public String getHeapTuningTips() {
        return heapTuningTips;
    }

    public String getTipsToReduceGCTime() {
        return tipsToReduceGCTime;
    }

    public String getResponseId() {
        return responseId;
    }

    public String getGraphURL() {
        return graphURL;
    }
}

class GcCause {
    String cause;
    int count;

    public GcCause(String cause, int count) {
        this.cause = cause;
        this.count = count;
    }

    public String toString() {
        return "cause = " + cause + ", count = " + count;
    }
}

class Group {
    double start; // seconds;
    double end;
    int numberOfGCs;

    public Group(double start, double end, int numberOfGCs) {
        this.start = start;
        this.end = end;
        this.numberOfGCs = numberOfGCs;
    }

    @Override
    public String toString() {
        return "start = " + start + ", end = " + end + ", numberOfGCs = " + numberOfGCs;
    }
}

