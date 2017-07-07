import os
import statistics.Statistics as st
import plotter.HistogramPlotter as hplt

from reader import FileReader


class AppMetricsAnalyzer:

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
            if not os.path.isdir(file): # [RDDJoin-CMS-1-7G-stat.txt, RDDJoin-CMS-2-14G-stat.txt, ...]
                fileName = file.title()

                # [app.duration] mean = 224339.20, stdVar = 8311.91, median = 225233.00, min = 211999.00, quantile25 = 216682.50, quantile75 = 231549.00, max = 233837.00
                # -------------------------------------------------------------------[Stage 0]-------------------------------------------------------------------
                # [stage0.duration] mean = 42360.60, stdVar = 4069.63, median = 41404.00, min = 37094.00, quantile25 = 38942.50, quantile75 = 46257.00, max = 47801.00
                # [stage0.inputBytes] mean = 8588671743.00, stdVar = 0.00, median = 8588671743.00, min = 8588671743.00, quantile25 = 8588671743.00, quantile75 = 8588671743.00, max = 8588671743.00
                # [stage0.inputRecords] mean = 66000000.00, stdVar = 0.00, median = 66000000.00, min = 66000000.00, quantile25 = 66000000.00, quantile75 = 66000000.00, max = 66000000.00
                for line in FileReader.readLines():
                    metricName = line[line.find('[') + 1: line.find(']')]
                    if metricsSet.contains(metricName):
                        if self.metricsMap.has_key(metricName):
                            self.metricsMap[metricName].addStatistics(line, fileName)
                        else:
                            statistics = st.Statistics()
                            self.metricsMap[metricName] = statistics

    def plotMetrics(self, outputDir):
        for metricName, statistics in self.metricsMap.items():
            file = os.path.join(outputDir, metricName + ".pdf")
            hplt.HistogramPlotter.plotStatistics(statistics, 'Time', metricName, file)


