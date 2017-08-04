import os
import statistics.BoxPlotStatistics as st
import plotter.BoxPlotter as bplt

from reader import FileReader


class AppBoxplotMetricsAnalyzer:

    def __init__(self, appName, statisticsDir):
        self.appName = appName
        self.statisticsDir = statisticsDir
        self.metricsMap = {}

    def analyzeMetrics(self, metrics):
        """
        :param metrics: [
               ("app.duration", "Time (s)", 1000),
               ("stage0.duration", "Time (s)", 1000),
               ("stage0.jvmGCTime", "Time (s)", 1000),
               ("stage0.task.executorRunTime", "Time (s)", 1000),
               ...]
        """
        statisticsFiles = os.listdir(self.statisticsDir)

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
                for line in FileReader.readLines(os.path.join(self.statisticsDir, file)):
                    metricName = line[line.find('[') + 1: line.find(']')]
                    if metricsTupleDict.has_key(metricName):
                        if self.metricsMap.has_key(metricName):
                            self.metricsMap[metricName].addStatistics(line, file)
                        else:
                            statistics = st.BoxPlotStatistics(metricsTupleDict[metricName])
                            statistics.addStatistics(line, file)
                            self.metricsMap[metricName] = statistics

        # Fill the NaA values
        for metricName, statistics in self.metricsMap.items():
           statistics.checkAndFillNulls()
                            

    def plotMetrics(self, outputDir):

        if not os.path.exists(outputDir):
            os.mkdir(outputDir)

        for metricName, statistics in self.metricsMap.items():
            file = os.path.join(outputDir, metricName.replace(".", "-") + ".pdf")
            bplt.BoxPlotter.plotStatisticsByGCAlgo(statistics, file)
            print "[Done] The " + file + " has been generated!"



if __name__ == '__main__':

    appName = "GroupByRDD-0.5-2"
    statisticsDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/" + appName + "/Statistics"
    outputDir = statisticsDir + "/figures-boxplot"

    metrics = [("app.duration", "Time (s)", 1000, "App.duration"), # (metric, ylablel, unit, title)

               # ("stage0.duration", "Time (s)", 1000, "Stage0.duration"),
               # ("stage0.jvmGCTime", "Time (s)", 1000, "Stage0.jvmGCTime"),
               # ("stage0.task.executorRunTime", "Time (s)", 1000, "Stage0.task.executorRunTime"),
               # ("stage0.task.jvmGcTime", "Time (s)", 1000, "Stage0.task.jvmGcTime"),
               # ("stage0.task.memoryBytesSpilled", "Spilled Size (MB)", 1024 * 1024, "Stage0.task.memoryBytesSpilled"),
               # ("stage0.task.diskBytesSpilled", "Spilled Size (MB)", 1024 * 1024, "Stage0.task.diskBytesSpilled"),
               #
               ("stage1.duration", "Time (s)", 1000, "Stage1.duration"),
               ("stage1.jvmGCTime", "Time (s)", 1000, "Stage1.jvmGCTime"),
               ("stage1.task.executorRunTime", "Time (s)", 1000, "Stage1.task.executorRunTime"),
               ("stage1.task.jvmGcTime", "Time (s)", 1000, "Stage1.task.jvmGcTime"),
               ("stage1.task.memoryBytesSpilled", "Spilled Size (MB)", 1024 * 1024, "Stage1.task.memoryBytesSpilled"),
               ("stage1.task.diskBytesSpilled", "Spilled Size (MB)", 1024 * 1024, "Stage1.task.diskBytesSpilled"),

               # for SVM
               # ("stage3+4+5+6+7+8+9+10+11+12+13+14+15+16+17+18+19+20+21+22.duration", "Time (s)", 1000, "Stage10.duration"),
               # ("stage3+4+5+6+7+8+9+10+11+12+13+14+15+16+17+18+19+20+21+22.jvmGCTime", "Time (s)", 1000, "Stage10.jvmGCTime"),
               # ("stage3+4+5+6+7+8+9+10+11+12+13+14+15+16+17+18+19+20+21+22.task.executorRunTime", "Time (s)", 1000, "Stage10.task.executorRunTime"),
               # ("stage3+4+5+6+7+8+9+10+11+12+13+14+15+16+17+18+19+20+21+22.task.jvmGcTime", "Time (s)", 1000, "Stage10.task.jvmGcTime"),
               # ("stage3+4+5+6+7+8+9+10+11+12+13+14+15+16+17+18+19+20+21+22.task.memoryBytesSpilled", "Spilled Size (MB)", 1024 * 1024, "Stage10.task.memoryBytesSpilled"),
               # ("stage3+4+5+6+7+8+9+10+11+12+13+14+15+16+17+18+19+20+21+22.task.diskBytesSpilled", "Spilled Size (MB)", 1024 * 1024, "Stage10.task.diskBytesSpilled"),


               # for PageRank
               # ("stage1+2+3+4+5+6+7+8+9+10.duration", "Time (s)", 1000, "Stage10.duration"),
               # ("stage1+2+3+4+5+6+7+8+9+10.jvmGCTime", "Time (s)", 1000, "Stage10.jvmGCTime"),
               # ("stage1+2+3+4+5+6+7+8+9+10.task.executorRunTime", "Time (s)", 1000, "Stage10.task.executorRunTime"),
               # ("stage1+2+3+4+5+6+7+8+9+10.task.jvmGcTime", "Time (s)", 1000, "Stage10.task.jvmGcTime"),
               # ("stage1+2+3+4+5+6+7+8+9+10.task.memoryBytesSpilled", "Spilled Size (MB)", 1024 * 1024, "Stage10.task.memoryBytesSpilled"),
               # ("stage1+2+3+4+5+6+7+8+9+10.task.diskBytesSpilled", "Spilled Size (MB)", 1024 * 1024, "Stage10.task.diskBytesSpilled"),


               # ("executor.memoryUsed", "GB", 1024 * 1024 * 1024),
               # ("executor.totalDuration", "Time (s)", 1000),
               # ("executor.totalGCTime", "Time (s)", 1000),
               # ("executor.maxMemory", "GB", 1024 * 1024 * 1024),

               ("executor.gc.footprint", "Memory (GB)", 1024, "Executor.footprint"), # Maximal amount of memory allocated
               ("executor.gc.freedMemoryByGC", "Memory (GB)", 1024, "Executor.freedMemoryByGC"), # Total amount of memory that has been freed
               ("executor.gc.accumPause", "Time (s)", 1, "Executor.gc.pause"), # Sum of all pauses due to any kind of GC
               ("executor.gc.gcPause", "Time (s)", 1, "Executor.minorGC.pause"), # This shows all stop-the-world pauses, that are not full gc pauses.
               ("executor.gc.throughput", "Throughput (%)", 1, "Executor.gc.throughput"), # Time percentage the application was NOT busy with GC
               ("executor.gc.totalTime", "Time (s)", 1, "Executor.duration"), # The duration of running executor
               ("executor.gc.gcPerformance", "Speed (MB/s)", 1, "Executor.minorGC.Performance")] # Performance of minor collections

    appMetricsAnalyzer = AppBoxplotMetricsAnalyzer(appName, statisticsDir)

    appMetricsAnalyzer.analyzeMetrics(metrics)
    appMetricsAnalyzer.plotMetrics(outputDir)
