import os
import statistics.BoxPlotStatistics as st
import plotter.GroupBoxPlotter as bplt

from reader import FileReader


class GroupAppMetricsAnalyzer:

    def __init__(self, firstAppName, secondAppName, firstStatisticsDir, secondStatisticsDir):
        self.firstAppName = firstAppName
        self.secondAppName = secondAppName
        self.firstStatisticsDir = firstStatisticsDir
        self.secondStatisticsDir = secondStatisticsDir
        self.firstMetricsMap = {}
        self.secondMetricsMap = {}



    def fillStatistics(self, metrics, statisticsDir, statisticsFiles, metricsMap, withMax):
        metricsTupleDict = {} # ["app.duration", ("app.duration", "Time (s)", 1000)]

        for tuple in metrics:
            metricsTupleDict[tuple[0]] = tuple
        for file in statisticsFiles:
            if file.endswith("stat.txt"): # [RDDJoin-CMS-1-7G-stat.txt, RDDJoin-CMS-2-14G-stat.txt, ...]
                # [app.duration] mean = 224339.20, stdVar = 8311.91, median = 225233.00, min = 211999.00, quantile25 = 216682.50, quantile75 = 231549.00, max = 233837.00
                # -------------------------------------------------------------------[Stage 0]-------------------------------------------------------------------
                # [stage0.duration] mean = 42360.60, stdVar = 4069.63, median = 41404.00, min = 37094.00, quantile25 = 38942.50, quantile75 = 46257.00, max = 47801.00
                # [stage0.inputBytes] mean = 8588671743.00, stdVar = 0.00, median = 8588671743.00, min = 8588671743.00, quantile25 = 8588671743.00, quantile75 = 8588671743.00, max = 8588671743.00
                # [stage0.inputRecords] mean = 66000000.00, stdVar = 0.00, median = 66000000.00, min = 66000000.00, quantile25 = 66000000.00, quantile75 = 66000000.00, max = 66000000.00
                for line in FileReader.readLines(os.path.join(statisticsDir, file)):
                    metricName = line[line.find('[') + 1: line.find(']')]
                    if metricsTupleDict.has_key(metricName):
                        if metricsMap.has_key(metricName):
                            metricsMap[metricName].addStatistics(line, file, withMax)
                        else:
                            statistics = st.BoxPlotStatistics(metricsTupleDict[metricName])
                            statistics.addStatistics(line, file, withMax)
                            metricsMap[metricName] = statistics

        # Fill the NaA values
        for metricName, statistics in metricsMap.items():
            statistics.checkAndFillNulls()

    def analyzeMetrics(self, metrics, firstStatisticsDir, secondStatisticsDir, withMax):
        """
        :param metrics: [
               ("app.duration", "Time (s)", 1000),
               ("stage0.duration", "Time (s)", 1000),
               ("stage0.jvmGCTime", "Time (s)", 1000),
               ("stage0.task.executorRunTime", "Time (s)", 1000),
               ...]
        """
        firstStatisticsFiles = os.listdir(self.firstStatisticsDir)
        self.fillStatistics(metrics, firstStatisticsDir, firstStatisticsFiles, self.firstMetricsMap, withMax)

        secondStatisticsFiles = os.listdir(self.secondStatisticsDir)
        self.fillStatistics(metrics, secondStatisticsDir, secondStatisticsFiles, self.secondMetricsMap, withMax)


    def plotMetrics(self, outputDir, firstSucessfulAppNum, secondSucessfulAppNum):

        if not os.path.exists(outputDir):
            os.mkdir(outputDir)

        for metricName in self.firstMetricsMap.keys():
            file = os.path.join(outputDir, metricName.replace(".", "-") + ".pdf")
            firstStatistics = self.firstMetricsMap[metricName]
            secondStatistics = self.secondMetricsMap[metricName]
            bplt.GroupBoxPlotter.plotStatisticsByGCAlgo(file, firstSucessfulAppNum, secondSucessfulAppNum,
                                                   firstStatistics, secondStatistics)
            print "[Done] The " + file + " has been generated!"


def plotApp(title, firstAppName, secondAppName, withMax):

    firstStatisticsDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/" + firstAppName + "/Statistics"
    secondStatisticsDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/" + secondAppName + "/Statistics"

    if (withMax == True):
        outputDir = secondStatisticsDir + "/" + title + "-max"
    else:
        outputDir = secondStatisticsDir + "/" + title + "-nomax"

    firstSucessfulAppNum = []
    secondSucessfulAppNum = []
    stageid = ""
    stageName = ""

    # for GroupBy
    if (title == "GroupBy"):
        firstSucessfulAppNum = [[5, 5, 5], [5, 5, 5], [4, 5, 5]] # Parallel, CMS, G1, RDDJoin-0.5-2
        secondSucessfulAppNum = [[0, 5, 5], [0, 5, 5], [0, 5, 5]] # Parallel, CMS, G1, RDDJoin-1.0

    elif (title == "Join"):
        firstSucessfulAppNum = [[5, 5, 5], [5, 5, 5], [5, 5, 5]] # Parallel, CMS, G1, RDDJoin-0.5-2
        secondSucessfulAppNum = [[5, 5, 5], [5, 5, 5], [0, 0, 0]] # Parallel, CMS, G1, RDDJoin-1.0

    elif (title == "SVM"):
        firstSucessfulAppNum = [[5, 5, 5], [5, 5, 5], [0, 5, 5]] # Parallel, CMS, G1, RDDJoin-0.5-2
        secondSucessfulAppNum = [[4, 5, 5], [5, 5, 5], [0, 5, 5]] # Parallel, CMS, G1, RDDJoin-1.0

    elif (title == "PageRank"):
        firstSucessfulAppNum = [[5, 5, 5], [5, 5, 5], [5, 5, 5]] # Parallel, CMS, G1, RDDJoin-0.5-2
        secondSucessfulAppNum = [[0, 0, 5], [0, 1, 1], [0, 0, 0]] # Parallel, CMS, G1, RDDJoin-1.0

    if (title == "GroupBy"):
        stageid = "stage1"
        stageName = "stage1"

    elif (title == "Join"):
        stageid = "stage2"
        stageName = "stage2"

    elif (title == "SVM"):
        stageid = "stage4+6+8+10+12+14+16+18+20+22"
        #stageid = "stage3+5+7+9+11+13+15+17+19+21"
        stageName = "stage10"

    elif (title == "PageRank"):
        stageid = "stage1+2+3+4+5+6+7+8+9+10"
        stageName = "stage10"

    metrics = [("app.duration", "Time (s)", 1000, title + ".app.duration"), # (metric, ylablel, unit, title)

               (stageid + ".duration", "Time (s)", 1000, title + "." + stageName + ".duration"),
               (stageid + ".jvmGCTime", "Time (s)", 1000, title + "." + stageName + ".jvmGCTime"),
               (stageid + ".shuffleReadBytes", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".shuffleReadBytes"),
               (stageid + ".shuffleWriteBytes", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".shuffleWriteBytes"),
               (stageid + ".memoryBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".memoryBytesSpilled"),
               (stageid + ".diskBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".diskBytesSpilled"),
               (stageid + ".shuffle_write_writeTime", "Time (s)", 1000, title + "." + stageName + ".shuffleWriteTime"),
               (stageid + ".inputBytes", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".inputBytes"),
               (stageid + ".outputBytes", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".outputBytes"),
               (stageid + ".resultSize", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".resultSize"),

               (stageid + ".task.duration", "Time (s)", 1000, title + "." + stageName + ".task.duration"),
               (stageid + ".task.jvmGcTime", "Time (s)", 1000, title + "." + stageName + ".task.jvmGcTime"),
               (stageid + ".task.memoryBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".task.memoryBytesSpilled"),
               (stageid + ".task.diskBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".task.diskBytesSpilled"),
               (stageid + ".task.inputMetrics.bytesRead", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".task.bytesRead"),
               (stageid + ".task.outputMetrics.bytesWritten", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".task.bytesWritten"),


               (stageid + ".task.shuffleReadMetrics.recordsRead", "Number (M)", 1000 * 1000, title + "." + stageName + ".task.shuffleReadRecords"),
               (stageid + ".task.shuffleReadMetrics.recordsWritten", "Number (M)", 1000 * 1000, title + "." + stageName + ".task.shuffleWriteRecords"),
               (stageid + ".task.shuffleWriteMetrics.writeTime", "Time (s)", 1000, title + "." + stageName + ".task.shuffleWriteTime"),


               ("executor.totalShuffleRead", "Size (GB)", 1024 * 1024 * 1024, title + ".executor.totalShuffleRead"),
               ("executor.totalShuffleWrite", "Size (GB)", 1024 * 1024 * 1024, title + ".executor.totalShuffleWrite"),

               ("executor.gc.footprint", "Memory (GB)", 1024, title + ".executor.footprint"), # Maximal amount of memory allocated
               ("executor.gc.freedMemoryByGC", "Memory (GB)", 1024, title + ".executor.freedMemoryByGC"), # Total amount of memory that has been freed
               ("executor.gc.accumPause", "Time (s)", 1, title + ".executor.gc.pause"), # Sum of all pauses due to any kind of GC
               ("executor.gc.gcPause", "Time (s)", 1, title + ".executor.minorGC.pause"), # This shows all stop-the-world pauses, that are not full gc pauses.
               ("executor.gc.throughput", "Throughput (%)", 1, title + ".executor.gc.throughput"), # Time percentage the application was NOT busy with GC
               ("executor.gc.totalTime", "Time (s)", 1, title + ".executor.duration"), # The duration of running executor
               ("executor.gc.gcPerformance", "Speed (GB/s)", 1024, title + ".executor.minorGC.Performance"), # Performance of minor collections

               ("gceasy.jvmHeapSize_youngGen_peakSize", "Memory (GB)", 1024, title + ".executor.youngGen.peakSize"),
               ("gceasy.jvmHeapSize_oldGen_peakSize", "Memory (GB)", 1024, title + ".executor.oldGen.peakSize"),
               ("gceasy.jvmHeapSize_total_allocatedSize", "Memory (GB)", 1024, title + ".executor.total.allocatedSize"), # Young + Old + Perm (or Metaspace)
               ("gceasy.jvmHeapSize_total_peakSize", "Memory (GB)", 1024, title + ".executor.total.peakSize"), # Peak utilization of the heap size at runtime
               ("gceasy.gcStatistics_totalCreatedBytes", "Object Size (GB)", 1024, title + ".executor.createdObjectSize"), # Total amount of objects created by the application
               ("gceasy.gcStatistics_avgPromotionRate", "Rate (MB/s)", 1, title + ".executor.avgPromotionRate"),
               ("gceasy.gcStatistics_minorGCCount", "Count", 1, title + ".executor.minorGCCount"),
               ("gceasy.gcStatistics_minorGCTotalTime", "Time (s)", 1, title + ".executor.minorGCTotalTime"),
               ("gceasy.gcStatistics_fullGCCount", "Count", 1, title + ".executor.fullGCCount"),
               ("gceasy.gcStatistics_fullGCTotalTime", "Time (s)", 1, title + ".executor.fullGCTotalTime"),
               ("gceasy.gcStatistics_fullGCMaxTime", "Time (s)", 1, title + ".executor.fullGCMaxTime"),
               ("gceasy.throughputPercentage", "Throughput (%)", 1, title + ".executor.throughputPercentage")
               ]

    appMetricsAnalyzer = GroupAppMetricsAnalyzer(firstAppName, secondAppName, firstStatisticsDir, secondStatisticsDir)

    appMetricsAnalyzer.analyzeMetrics(metrics, firstStatisticsDir, secondStatisticsDir, withMax)
    appMetricsAnalyzer.plotMetrics(outputDir, firstSucessfulAppNum, secondSucessfulAppNum)


if __name__ == '__main__':
    withMax = False

    # for GroupBy
    title = "GroupBy"
    firstAppName = "GroupByRDD-0.5-2"
    secondAppName = "GroupByRDD-1.0-2"
    plotApp(title, firstAppName, secondAppName, withMax)

    # for Join
    title = "Join"
    firstAppName = "RDDJoin-0.5-2"
    secondAppName = "RDDJoin-1.0"
    plotApp(title, firstAppName, secondAppName, withMax)

    # for SVM
    title = "SVM"
    firstAppName = "SVM-0.5"
    secondAppName = "SVM-1.0"
    plotApp(title, firstAppName, secondAppName, withMax)

    # for PageRank
    title = "PageRank"
    firstAppName = "PageRank-0.5"
    secondAppName = "PageRank-1.0"

    plotApp(title, firstAppName, secondAppName, withMax)

