import os
import statistics.HistogramStatistics as st
import plotter.HistogramPlotter as hplt

from reader import FileReader


class AppHistogramMetricsAnalyzer:

    def __init__(self, appName, statisticsDir):
        self.appName = appName
        self.statisticsDir = statisticsDir
        self.metricsMap = {}

    def analyzeMetrics(self, metrics):
        """
        :param metrics: [app.duration, stage0.duration, s0.task.jvmGcTime, ...]
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
                            self.metricsMap[metricName].addHistogramStatistics(line, file)
                        else:
                            statistics = st.HistogramStatistics(metricsTupleDict[metricName])
                            statistics.addHistogramStatistics(line, file)
                            self.metricsMap[metricName] = statistics


    def plotMetrics(self, outputDir, metrics, title):

        if not os.path.exists(outputDir):
            os.mkdir(outputDir)
        file = os.path.join(outputDir, title + ".pdf")

        statisticsList = []

        for metric in metrics:
            statistics = self.metricsMap[metric[0]]
            statisticsList.append(statistics)
        ylabel = statisticsList[0].ylabel
        hplt.HistogramPlotter.plotMultiStatisticsByGCAlgo(statisticsList, title, ylabel, file)
        print "[Done] The " + file + " has been generated!"

if __name__ == '__main__':

    appName = "PageRank-0.5"
    statisticsDir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/profiles/" + appName + "/Statistics"
    outputDir = statisticsDir + "/figures-histo"




    metrics = [#("app.duration", "Time (s)", 1000),

        #("stage0.duration", "Time (s)", 1000, "stage0.duartion"),
               #("stage0.jvmGCTime", "Time (s)", 1000),
               #("stage0.task.executorRunTime", "Time (s)", 1000),
               #("stage0.task.jvmGcTime", "Time (s)", 1000),
               #("stage0.task.memoryBytesSpilled", "MB", 1024 * 1024, "SpilledBytes"),
               #("stage0.task.diskBytesSpilled", "MB", 1024 * 1024),
               #
        #("stage4+6+8+10+12+14+16+18+20+22.duration", "Time (s)", 1000, "stage2.duration"),
        #("stage1+2+3+4+5+6+7+8+9+10.duration", "Time (s)", 1000, "stage1.duration"),
        #("stage2.duration", "Time (s)", 1000),
               #("stage1.jvmGCTime", "Time (s)", 1000),
        #("stage2.task.duration", "Time (s)", 1000),
        #("stage0.task.duration", "Time (s)", 1000, "task.duration"),
               #("stage1.task.executorRunTime", "Time (s)", 1000),
        #("stage2.task.jvmGcTime", "Time (s)", 1000),
        #("stage1+2+3+4+5+6+7+8+9+10.task.jvmGcTime", "Time (s)", 1000, "task.jvmGcTime"),
        ("stage1+2+3+4+5+6+7+8+9+10.task.memoryBytesSpilled", "MB", 1024 * 1024, "SpilledBytes"),
               #("stage1.task.memoryBytesSpilled", "MB", 1024 * 1024),
               #("stage1.task.diskBytesSpilled", "MB", 1024 * 1024),

               # ("executor.memoryUsed", "GB", 1024 * 1024 * 1024),
               #("executor.totalDuration", "Time (s)", 1000),
               #("executor.totalGCTime", "Time (s)", 1000),
               #("executor.maxMemory", "GB", 1024 * 1024 * 1024),

               #("executor.maxMemoryUsage", "GB", 1, "maxMemoryUsage"),
               #("executor.maxCPUUsage", "100%", 100, "maxCPUUsage"),

               # ("executor.gc.footprint", "GB", 1024), # Maximal amount of memory allocated
               # ("executor.gc.freedMemoryByGC", "GB", 1024), # Total amount of memory that has been freed
        #("executor.gc.accumPause", "Time (s)", 1, "totalPause"), # Sum of all pauses due to any kind of GC
        #("executor.gc.gcPause", "Time (s)", 1, "YGCpause"), # This shows all stop-the-world pauses, that are not full gc pauses.
        #("executor.gc.fullGCPause", "Time (s)", 1, "fullGCPause"), # This shows all stop-the-world pauses, that are
               # ("executor.gc.throughput", "%", 1), # Time percentage the application was NOT busy with GC
               # ("executor.gc.totalTime", "Time (s)", 1), # The duration of running executor
               #("executor.gc.gcPerformance", "MB/s", 1), # Performance of minor collections
               #("executor.gc.fullGCPerformance", "MB/s", 1) # Performance of minor collections
               ]
    appMetricsAnalyzer = AppHistogramMetricsAnalyzer(appName, statisticsDir)

    appMetricsAnalyzer.analyzeMetrics(metrics)
    appMetricsAnalyzer.plotMetrics(outputDir, metrics, appName + "-" + "task-spilled")
    #appMetricsAnalyzer.plotMetrics(outputDir, metrics, appName + "-" + "task-duration")
    #appMetricsAnalyzer.plotMetrics(outputDir, metrics, appName + "-" + "stage-duration")
