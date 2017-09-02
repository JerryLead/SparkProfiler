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

    private String jvmHeapSize_youngGen_allocatedSize; // 7.5 gb
    private String jvmHeapSize_youngGen_peakSize; // 6 gb
    private String jvmHeapSize_oldGen_allocatedSize; // 22.5 gb
    private String jvmHeapSize_oldGen_peakSize; // 22.5 gb
    private String jvmHeapSize_metaSpace_allocatedSize; // 1.04 gb
    private String jvmHeapSize_metaSpace_peakSize; // 48.52 mb
    private String jvmHeapSize_total_allocatedSize; // 30 gb
    private String jvmHeapSize_total_peakSize; // 28.5 gb

    private String gcStatistics_totalCreatedBytes; // 249.49 gb
    private String gcStatistics_measurementDuration; // 7 hrs 32 min 52 sec",
    private String gcStatistics_avgAllocationRate; // 9.4 mb/sec
    private String gcStatistics_avgPromotionRate; // 1.35 mb/sec
    private String gcStatistics_minorGCCount; // 62
    private String gcStatistics_minorGCTotalTime; // 1 min 19 sec
    private String gcStatistics_minorGCAvgTime; // 1 sec 274 ms
    private String gcStatistics_minorGCAvgTimeStdDeviation; // 2 sec 374 ms
    private String gcStatistics_minorGCMinTIme; // 0
    private String gcStatistics_minorGCMaxTime; // 13 sec 780 ms
    private String gcStatistics_minorGCIntervalAvgTime; // 7 min 25 sec 442 ms
    private String gcStatistics_fullGCCount; // 166
    private String gcStatistics_fullGCTotalTime; // 14 min 11 sec 620 ms
    private String gcStatistics_fullGCAvgTime; // 5 sec 130 ms
    private String gcStatistics_fullGCAvgTimeStdDeviation; // 5 sec 207 ms
    private String gcStatistics_fullGCMinTIme; // 120 ms
    private String gcStatistics_fullGCMaxTime; // 57 sec 880 ms
    private String gcStatistics_fullGCIntervalAvgTime; // 2 min 19 sec 104 ms
    private List<Group> gcDurationSummary_groups = new ArrayList<Group>();
    private List<GcCause> gcCauses = new ArrayList<GcCause>();
    private String commandLineFlags;
    private String heapTuningTips;
    private String tipsToReduceGCTime;

    private double throughputPercentage;
    private String responseId;
    private String graphURL;

    public GCeasyMetrics(String json) {
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(json);

        if (el.isJsonObject()) {
            JsonObject root = el.getAsJsonObject();
            parseJsonObject(root);
        }

    }

    private void parseJsonObject(JsonObject root) {
        this.isProblem = root.get("isProblem").getAsBoolean();
        this.problem = root.get("problem").getAsJsonArray().toString();

        JsonObject jvmHeapSizeObj = root.getAsJsonObject("jvmHeapSize");
        this.jvmHeapSize_youngGen_allocatedSize = jvmHeapSizeObj.getAsJsonObject("youngGen")
                .get("allocatedSize").getAsString();
        this.jvmHeapSize_youngGen_peakSize = jvmHeapSizeObj.getAsJsonObject("youngGen")
                .get("peakSize").getAsString();
        this.jvmHeapSize_oldGen_allocatedSize = jvmHeapSizeObj.getAsJsonObject("oldGen")
                .get("allocatedSize").getAsString();
        this.jvmHeapSize_oldGen_peakSize = jvmHeapSizeObj.getAsJsonObject("oldGen")
                .get("peakSize").getAsString();
        this.jvmHeapSize_metaSpace_allocatedSize = jvmHeapSizeObj.getAsJsonObject("metaSpace")
                .get("allocatedSize").getAsString();
        this.jvmHeapSize_metaSpace_peakSize = jvmHeapSizeObj.getAsJsonObject("metaSpace")
                .get("peakSize").getAsString();
        this.jvmHeapSize_total_allocatedSize = jvmHeapSizeObj.getAsJsonObject("total")
                .get("allocatedSize").getAsString();
        this.jvmHeapSize_total_peakSize = jvmHeapSizeObj.getAsJsonObject("total")
                .get("peakSize").getAsString();

        JsonObject gcStatisticsObj = root.getAsJsonObject("gcStatistics");
        this.gcStatistics_totalCreatedBytes = gcStatisticsObj.get("totalCreatedBytes").getAsString();
        this.gcStatistics_measurementDuration = gcStatisticsObj.get("measurementDuration").getAsString();
        this.gcStatistics_avgAllocationRate = gcStatisticsObj.get("avgAllocationRate").getAsString();
        this.gcStatistics_avgPromotionRate = gcStatisticsObj.get("avgPromotionRate").getAsString();
        this.gcStatistics_minorGCCount = gcStatisticsObj.get("minorGCCount").getAsString();
        this.gcStatistics_minorGCTotalTime = gcStatisticsObj.get("minorGCTotalTime").getAsString();
        this.gcStatistics_minorGCAvgTime = gcStatisticsObj.get("minorGCAvgTime").getAsString();
        this.gcStatistics_minorGCAvgTimeStdDeviation = gcStatisticsObj.get("minorGCAvgTimeStdDeviation").getAsString();
        this.gcStatistics_minorGCMinTIme = gcStatisticsObj.get("minorGCMinTIme").getAsString();
        this.gcStatistics_minorGCMaxTime = gcStatisticsObj.get("minorGCMaxTime").getAsString();
        this.gcStatistics_minorGCIntervalAvgTime = gcStatisticsObj.get("minorGCIntervalAvgTime").getAsString();
        this.gcStatistics_fullGCCount = gcStatisticsObj.get("fullGCCount").getAsString();
        this.gcStatistics_fullGCTotalTime = gcStatisticsObj.get("fullGCTotalTime").getAsString();
        this.gcStatistics_fullGCAvgTime = gcStatisticsObj.get("fullGCAvgTime").getAsString();
        this.gcStatistics_fullGCAvgTimeStdDeviation = gcStatisticsObj.get("fullGCAvgTimeStdDeviation").getAsString();
        this.gcStatistics_fullGCMinTIme = gcStatisticsObj.get("fullGCMinTIme").getAsString();
        this.gcStatistics_fullGCMaxTime = gcStatisticsObj.get("fullGCMaxTime").getAsString();
        this.gcStatistics_fullGCIntervalAvgTime = gcStatisticsObj.get("fullGCIntervalAvgTime").getAsString();

        JsonObject gcDurationSummaryObj = root.getAsJsonObject("gcDurationSummary");
        JsonArray groupArray = gcDurationSummaryObj.get("groups").getAsJsonArray();
        for (JsonElement elem : groupArray) {
            JsonObject obj = elem.getAsJsonObject();
            int start = obj.get("start").getAsInt();
            int end = obj.get("end").getAsInt();
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

        this.commandLineFlags = root.get("commandLineFlags").getAsString();
        this.heapTuningTips = root.get("heapTuningTips").getAsJsonArray().toString();
        this.tipsToReduceGCTime = root.get("tipsToReduceGCTime").getAsJsonArray().toString();

        this.throughputPercentage = root.get("throughputPercentage").getAsDouble();
        this.responseId = root.get("responseId").getAsString();
        this.graphURL = root.get("graphURL").getAsString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("isProblem = " + isProblem + "\n");

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

        GCeasyMetrics gCeasyMetrics = new GCeasyMetrics(json);
        System.out.println(gCeasyMetrics.toString());
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
    int start; // seconds;
    int end;
    int numberOfGCs;

    public Group(int start, int end, int numberOfGCs) {
        this.start = start;
        this.end = end;
        this.numberOfGCs = numberOfGCs;
    }

    @Override
    public String toString() {
        return "start = " + start + ", end = " + end + ", numberOfGCs = " + numberOfGCs;
    }
}

