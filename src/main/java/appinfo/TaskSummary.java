package appinfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;


public class TaskSummary {

    private JsonObject taskSummaryJsonObject;
    private List<TaskSummaryItem> taskSummaryList = new ArrayList<TaskSummaryItem>();

    public TaskSummary(JsonObject taskSummaryJsonObject) {

        JsonArray quantiles = taskSummaryJsonObject.getAsJsonArray("quantiles");

        for (JsonElement quantileElem : quantiles) {
            float quantile = quantileElem.getAsFloat();
            taskSummaryList.add(new TaskSummaryItem(quantile));
        }

        this.taskSummaryJsonObject = taskSummaryJsonObject;

        parseTaskSummaryJson();
    }

    private void parseTaskSummaryJson() {

        pasreJsonArray("executorDeserializeTime");
        pasreJsonArray("executorDeserializeCpuTime");
        pasreJsonArray("executorRunTime");
        pasreJsonArray("executorCpuTime");
        pasreJsonArray("resultSize");
        pasreJsonArray("jvmGcTime");
        pasreJsonArray("resultSerializationTime");
        pasreJsonArray("memoryBytesSpilled");
        pasreJsonArray("diskBytesSpilled");

        pasreJsonArray("inputMetrics", "bytesRead");
        pasreJsonArray("inputMetrics", "recordsRead");

        pasreJsonArray("outputMetrics", "bytesWritten");
        pasreJsonArray("outputMetrics", "recordsWritten");

        pasreJsonArray("shuffleReadMetrics", "readBytes");
        pasreJsonArray("shuffleReadMetrics", "readRecords");
        pasreJsonArray("shuffleReadMetrics", "remoteBlocksFetched");
        pasreJsonArray("shuffleReadMetrics", "localBlocksFetched");
        pasreJsonArray("shuffleReadMetrics", "fetchWaitTime");
        pasreJsonArray("shuffleReadMetrics", "remoteBytesRead");
        pasreJsonArray("shuffleReadMetrics", "totalBlocksFetched");


        pasreJsonArray("shuffleWriteMetrics", "writeBytes");
        pasreJsonArray("shuffleWriteMetrics", "writeRecords");
        pasreJsonArray("shuffleWriteMetrics", "writeTime");

    }

    private void pasreJsonArray(String metric) {
        JsonArray metricsArray = taskSummaryJsonObject.getAsJsonArray(metric);
        for (int i = 0; i < metricsArray.size(); i++) {
            double value = metricsArray.get(i).getAsDouble();
            taskSummaryList.get(i).set(metric, value);
        }
    }

    private void pasreJsonArray(String metric, String subMetric) {

        JsonArray metricsArray = taskSummaryJsonObject.getAsJsonObject(metric).getAsJsonArray(subMetric);
        for (int i = 0; i < metricsArray.size(); i++) {
            double value = metricsArray.get(i).getAsDouble();
            taskSummaryList.get(i).set(metric + "_" + subMetric, value);
        }
    }
}

class TaskSummaryItem {
    private float quantile;

    private double executorDeserializeTime;
    private double executorDeserializeCpuTime;

    private double executorRunTime;
    private double executorCpuTime;
    private double resultSize;
    private double jvmGcTime;
    private double resultSerializationTime;
    private double memoryBytesSpilled;
    private double diskBytesSpilled;

    private double inputMetrics_bytesRead;
    private double inputMetrics_recordsRead;

    private double outputMetrics_bytesWritten;
    private double outputMetrics_recordsWritten;

    private double shuffleReadMetrics_readBytes;
    private double shuffleReadMetrics_readRecords;
    private double shuffleReadMetrics_remoteBlocksFetched;
    private double shuffleReadMetrics_localBlocksFetched;
    private double shuffleReadMetrics_fetchWaitTime;
    private double shuffleReadMetrics_remoteBytesRead;
    private double shuffleReadMetrics_totalBlocksFetched;

    private double shuffleWriteMetrics_writeBytes;
    private double shuffleWriteMetrics_writeRecords;
    private double shuffleWriteMetrics_writeTime;


    public TaskSummaryItem(float quantile) {
        this.quantile = quantile;
    }

    public void set(String metric, double value) {

        if (metric.equals("executorDeserializeTime"))
            executorDeserializeTime = value;
        else if (metric.equals("executorDeserializeCpuTime"))
            executorDeserializeCpuTime = value;
        else if (metric.equals("executorRunTime"))
            executorRunTime = value;
        else if (metric.equals("executorCpuTime"))
            executorCpuTime = value;
        else if (metric.equals("resultSize"))
            resultSize = value;
        else if (metric.equals("jvmGcTime"))
            jvmGcTime = value;
        else if (metric.equals("resultSerializationTime"))
            resultSerializationTime = value;
        else if (metric.equals("memoryBytesSpilled"))
            memoryBytesSpilled = value;
        else if (metric.equals("diskBytesSpilled"))
            diskBytesSpilled = value;

        else if (metric.equals("inputMetrics_bytesRead"))
            inputMetrics_bytesRead = value;
        else if (metric.equals("inputMetrics_recordsRead"))
            inputMetrics_recordsRead = value;

        else if (metric.equals("outputMetrics_bytesWritten"))
            outputMetrics_bytesWritten = value;
        else if (metric.equals("outputMetrics_recordsWritten"))
            outputMetrics_recordsWritten = value;

        else if (metric.equals("shuffleReadMetrics_readBytes"))
            shuffleReadMetrics_readBytes = value;
        else if (metric.equals("shuffleReadMetrics_readRecords"))
            shuffleReadMetrics_readRecords = value;
        else if (metric.equals("shuffleReadMetrics_remoteBlocksFetched"))
            shuffleReadMetrics_remoteBlocksFetched = value;
        else if (metric.equals("shuffleReadMetrics_localBlocksFetched"))
            shuffleReadMetrics_localBlocksFetched = value;
        else if (metric.equals("shuffleReadMetrics_fetchWaitTime"))
            shuffleReadMetrics_fetchWaitTime = value;
        else if (metric.equals("shuffleReadMetrics_remoteBytesRead"))
            shuffleReadMetrics_remoteBytesRead = value;
        else if (metric.equals("shuffleReadMetrics_totalBlocksFetched"))
            shuffleReadMetrics_totalBlocksFetched = value;

        else if (metric.equals("shuffleWriteMetrics_writeBytes"))
            shuffleWriteMetrics_writeBytes = value;
        else if (metric.equals("shuffleWriteMetrics_writeRecords"))
            shuffleWriteMetrics_writeRecords = value;
        else if (metric.equals("shuffleWriteMetrics_writeTime"))
            shuffleWriteMetrics_writeTime = value;
    }
}




/*
taskSummaryJsonObject:

            "quantiles" : [ 0.05, 0.25, 0.5, 0.75, 0.95 ],
            "executorDeserializeTime" : [ 2.0, 2.0, 3.0, 5.0, 655.0 ],
            "executorDeserializeCpuTime" : [ 1987480.0, 2349341.0, 2858453.0, 4028054.0, 1.49370033E8 ],
            "executorRunTime" : [ 9715.0, 10460.0, 11139.0, 11977.0, 15149.0 ],
            "executorCpuTime" : [ 8.866386598E9, 9.205956361E9, 9.481287521E9, 9.977366109E9, 1.1322396785E10 ],
            "resultSize" : [ 1757.0, 1757.0, 1757.0, 1757.0, 2572.0 ],
            "jvmGcTime" : [ 371.0, 431.0, 474.0, 515.0, 602.0 ],
            "resultSerializationTime" : [ 0.0, 0.0, 0.0, 0.0, 1.0 ],
            "memoryBytesSpilled" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
            "diskBytesSpilled" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
            "inputMetrics" : {
                "bytesRead" : [ 1.34283264E8, 1.34283264E8, 1.34283264E8, 1.34283264E8, 1.34283264E8 ],
                "recordsRead" : [ 4955820.0, 4956122.0, 4956340.0, 4956577.0, 4956930.0 ]
            },
            "outputMetrics" : {
                "bytesWritten" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
                "recordsWritten" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ]
            },
            "shuffleReadMetrics" : {
                "readBytes" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
                "readRecords" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
                "remoteBlocksFetched" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
                "localBlocksFetched" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
                "fetchWaitTime" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
                "remoteBytesRead" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ],
                "totalBlocksFetched" : [ 0.0, 0.0, 0.0, 0.0, 0.0 ]
            },
            "shuffleWriteMetrics" : {
                "writeBytes" : [ 2634.0, 2636.0, 2636.0, 2636.0, 2636.0 ],
                "writeRecords" : [ 101.0, 101.0, 101.0, 101.0, 101.0 ],
                "writeTime" : [ 285815.0, 319997.0, 358530.0, 447136.0, 1805635.0 ]
            }
*/