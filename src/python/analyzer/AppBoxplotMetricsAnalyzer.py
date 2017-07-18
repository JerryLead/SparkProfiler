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
        :param metrics: [app.duration, stage0.duration, s0.task.jvmGcTime, ...]
        """
        statisticsFiles = os.listdir(self.statisticsDir)

        metricsSet = set(metrics)

        for file in statisticsFiles:
            if file.endswith("stat.txt"): # [RDDJoin-CMS-1-7G-stat.txt, RDDJoin-CMS-2-14G-stat.txt, ...]
                # [app.duration] mean = 224339.20, stdVar = 8311.91, median = 225233.00, min = 211999.00, quantile25 = 216682.50, quantile75 = 231549.00, max = 233837.00
                # -------------------------------------------------------------------[Stage 0]-------------------------------------------------------------------
                # [stage0.duration] mean = 42360.60, stdVar = 4069.63, median = 41404.00, min = 37094.00, quantile25 = 38942.50, quantile75 = 46257.00, max = 47801.00
                # [stage0.inputBytes] mean = 8588671743.00, stdVar = 0.00, median = 8588671743.00, min = 8588671743.00, quantile25 = 8588671743.00, quantile75 = 8588671743.00, max = 8588671743.00
                # [stage0.inputRecords] mean = 66000000.00, stdVar = 0.00, median = 66000000.00, min = 66000000.00, quantile25 = 66000000.00, quantile75 = 66000000.00, max = 66000000.00
                for line in FileReader.readLines(os.path.join(self.statisticsDir, file)):
                    metricName = line[line.find('[') + 1: line.find(']')]
                    if metricName in metricsSet:
                        if self.metricsMap.has_key(metricName):
                            self.metricsMap[metricName].addStatistics(line, file)
                        else:
                            statistics = st.BoxPlotStatistics()
                            statistics.addStatistics(line, file)
                            self.metricsMap[metricName] = statistics

    def plotMetrics(self, outputDir):

        if not os.path.exists(outputDir):
            os.mkdir(outputDir)

        for metricName, statistics in self.metricsMap.items():
            file = os.path.join(outputDir, metricName + ".pdf")
            bplt.BoxPlotter.plotStatisticsByGCAlgo(statistics, metricName, 'Time (s)', file)
            print "[Done] The " + file + " has been generated!"



if __name__ == '__main__':

    appName = "GroupByRDD-0.5"
    statisticsDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/" + appName + "/Statistics"
    outputDir = statisticsDir + "/figures-boxplot"
    metrics = ["app.duration",

               "stage0.duration",
               "stage0.jvmGCTime",
               "stage0.task.executorRunTime",
               "stage0.task.jvmGcTime",
               "stage0.task.memoryBytesSpilled",
               "stage0.task.diskBytesSpilled",

               "stage1.duration",
               "stage1.jvmGCTime",
               "stage1.task.executorRunTime",
               "stage1.task.jvmGcTime",
               "stage1.task.memoryBytesSpilled",
               "stage1.task.diskBytesSpilled",

               "executor.memoryUsed",
               "executor.totalDuration",
               "executor.totalGCTime",
               "executor.maxMemory",
               "executor.gc.footprint",
               "executor.gc.freedMemoryByGC",
               "executor.gc.accumPause",
               "executor.gc.gcPause",
               "executor.gc.freedMemory",
               "executor.gc.throughput",
               "executor.gc.totalTime",
               "executor.gc.gcPerformance"]

    appMetricsAnalyzer = AppBoxplotMetricsAnalyzer(appName, statisticsDir)

    appMetricsAnalyzer.analyzeMetrics(metrics)
    appMetricsAnalyzer.plotMetrics(outputDir)
