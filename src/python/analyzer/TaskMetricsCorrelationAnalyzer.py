import os

from reader import FileReader
import statistics.TaskAttempt as ta
import plotter.ScatterPlotter as splt


class TaskMetricsCorrelationAnalyzer:

    def __init__(self, appName, taskInfoDir, xmetrics, ymetrics):
        self.appName = appName
        self.taskInfoDir = taskInfoDir
        self.xmetrics = xmetrics
        self.ymetrics = ymetrics
        self.taskList = []

    def analyzeMetrics(self):
        taskInfoFiles = os.listdir(self.taskInfoDir)

        for file in taskInfoFiles:
            if file.endswith("tasks.txt"): # [GroupByRDD-CMS-1-7G-0.5-tasks.txt, GroupByRDD-CMS-2-14G-0.5-tasks.txt, ...]
                # [appName = GroupByRDD-CMS-1-7G-0.5]
                # [appId = app-20170721105922-0025]
                # [stageId = 1]
                #
                # [0.task.index] 0
                # [0.task.attemptId] 0
                # [0.task.executorId] 29
                # [0.task.duration] 4715
                # [0.task.executorDeserializeTime] 60
                # [0.task.executorDeserializeCpuTime] 43705029
                # [0.task.executorRunTime] 4715
                # [0.task.executorCpuTime] 2614115738
                # [0.task.resultSize] 2865
                # [0.task.jvmGcTime] 1305
                GC = ""
                executor = ""
                if (file.find("CMS") != -1):
                    GC = "CMS"
                elif (file.find("G1") != -1):
                    GC = "G1"
                elif (file.find("Parallel") != -1):
                    GC = "Parallel"

                if (file.find("1-7G") != -1):
                    executor = "E1"
                elif (file.find("2-14G") != -1):
                    executor = "E2"
                elif (file.find("4-28G") != -1):
                    executor = "E4"

                for line in FileReader.readLines(os.path.join(self.taskInfoDir, file)):
                    if line.startswith('[appName'):
                        appName = line[line.find('=') + 2: line.find(']')]
                    elif line.startswith('[appId'):
                        appId = line[line.find('=') + 2: line.find(']')]
                    elif line.startswith('[stageId'):
                        stageId = int(line[line.find('=') + 2: line.find(']')])
                    elif line.startswith('['):
                        metricName = line[line.find('task') + 5: line.find(']')]
                        metricValue = float(line[line.find(']') + 2:])
                        if (metricName == 'index'):
                            taskAttempt = ta.TaskAttempt(appId, appName, stageId, int(metricValue), GC, executor)
                        taskAttempt.set(metricName, metricValue)
                        self.taskList.append(taskAttempt)

    def plotTaskInfo(self):
        self.analyzeMetrics()
        for ymetric in self.ymetrics:
            for xmetric in self.xmetrics:
                xLabel = xmetric[0]
                xUnit = xmetric[2]
                yLabel = ymetric[0]
                yUnit = ymetric[2]

                xValues = {} # key = "Parallel", value = [task1.value, task2.value, task3.value, ...]
                yValues = {}

                for task in self.taskList:
                    # print("[" + str(task.taskId) + "] " + str(task.get(xmetrics[0])) + ":" + str(task.get(ymetrics[0])))
                    if (xValues.has_key(task.GC) == False):
                        xValues[task.GC] = []
                    if (yValues.has_key(task.GC) == False):
                        yValues[task.GC] = []

                    xValues[task.GC].append(task.get(xLabel) / float(xUnit))
                    yValues[task.GC].append(task.get(yLabel) / float(yUnit))

                fileName = xLabel + "-" + yLabel
                file = os.path.join(taskInfoDir + "/figures-png", fileName.replace(".", "_") + ".png")
                if not os.path.exists(taskInfoDir + "/figures-png"):
                    os.mkdir(taskInfoDir + "/figures-png")

                splt.ScatterPlotter.plotTaskMetrics(xValues, yValues, xLabel, yLabel, file)
                print("[Done] " + file + " has been generated!")







# def plotTaskInfo(title, appName):
#
#     taskInfoDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/" + appName + "/TaskInfo"
#     outputDir = taskInfoDir + "/" + title + "-max"
#
#     firstSucessfulAppNum = []
#     secondSucessfulAppNum = []
#     stageid = ""
#     stageName = ""
#
#     if (title == "GroupBy"):
#         stageid = "stage1"
#         stageName = "stage1"
#
#     elif (title == "Join"):
#         stageid = "stage2"
#         stageName = "stage2"
#
#     elif (title == "SVM"):
#         stageid = "stage4+6+8+10+12+14+16+18+20+22"
#         #stageid = "stage3+5+7+9+11+13+15+17+19+21"
#         stageName = "stage10"
#
#     elif (title == "PageRank"):
#         stageid = "stage1+2+3+4+5+6+7+8+9+10"
#         stageName = "stage10"
#
#     xmetrics = [("app.duration", "Time (s)", 1000, title + ".app.duration"), # (metric, ylablel, unit, title)
#
#                (stageid + ".duration", "Time (s)", 1000, title + "." + stageName + ".duration"),
#                (stageid + ".jvmGCTime", "Time (s)", 1000, title + "." + stageName + ".jvmGCTime"),
#                (stageid + ".shuffleReadBytes", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".shuffleReadBytes"),
#                (stageid + ".shuffleWriteBytes", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".shuffleWriteBytes"),
#                (stageid + ".memoryBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".memoryBytesSpilled"),
#                (stageid + ".diskBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".diskBytesSpilled"),
#                (stageid + ".shuffle_write_writeTime", "Time (s)", 1000, title + "." + stageName + ".shuffleWriteTime"),
#                (stageid + ".inputBytes", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".inputBytes"),
#                (stageid + ".outputBytes", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".outputBytes"),
#                (stageid + ".resultSize", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".resultSize"),
#
#                (stageid + ".task.duration", "Time (s)", 1000, title + "." + stageName + ".task.duration"),
#                (stageid + ".task.jvmGcTime", "Time (s)", 1000, title + "." + stageName + ".task.jvmGcTime"),
#                (stageid + ".task.memoryBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".task.memoryBytesSpilled"),
#                (stageid + ".task.diskBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".task.diskBytesSpilled"),
#                (stageid + ".task.inputMetrics.bytesRead", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".task.bytesRead"),
#                (stageid + ".task.outputMetrics.bytesWritten", "Size (GB)", 1024 * 1024 * 1024, title + "." + stageName + ".task.bytesWritten"),
#
#
#                (stageid + ".task.shuffleReadMetrics.recordsRead", "Number (M)", 1000 * 1000, title + "." + stageName + ".task.shuffleReadRecords"),
#                (stageid + ".task.shuffleReadMetrics.recordsWritten", "Number (M)", 1000 * 1000, title + "." + stageName + ".task.shuffleWriteRecords"),
#                (stageid + ".task.shuffleWriteMetrics.writeTime", "Time (s)", 1000, title + "." + stageName + ".task.shuffleWriteTime"),
#
#
#                ("executor.totalShuffleRead", "Size (GB)", 1024 * 1024 * 1024, title + ".executor.totalShuffleRead"),
#                ("executor.totalShuffleWrite", "Size (GB)", 1024 * 1024 * 1024, title + ".executor.totalShuffleWrite"),
#
#                ("executor.gc.footprint", "Memory (GB)", 1024, title + ".executor.footprint"), # Maximal amount of memory allocated
#                ("executor.gc.freedMemoryByGC", "Memory (GB)", 1024, title + ".executor.freedMemoryByGC"), # Total amount of memory that has been freed
#                ("executor.gc.accumPause", "Time (s)", 1, title + ".executor.gc.pause"), # Sum of all pauses due to any kind of GC
#                ("executor.gc.gcPause", "Time (s)", 1, title + ".executor.minorGC.pause"), # This shows all stop-the-world pauses, that are not full gc pauses.
#                ("executor.gc.throughput", "Throughput (%)", 1, title + ".executor.gc.throughput"), # Time percentage the application was NOT busy with GC
#                ("executor.gc.totalTime", "Time (s)", 1, title + ".executor.duration"), # The duration of running executor
#                ("executor.gc.gcPerformance", "Speed (GB/s)", 1024, title + ".executor.minorGC.Performance"), # Performance of minor collections
#
#                ("gceasy.jvmHeapSize_youngGen_peakSize", "Memory (GB)", 1024, title + ".executor.youngGen.peakSize"),
#                ("gceasy.jvmHeapSize_oldGen_peakSize", "Memory (GB)", 1024, title + ".executor.oldGen.peakSize"),
#                ("gceasy.jvmHeapSize_total_allocatedSize", "Memory (GB)", 1024, title + ".executor.total.allocatedSize"), # Young + Old + Perm (or Metaspace)
#                ("gceasy.jvmHeapSize_total_peakSize", "Memory (GB)", 1024, title + ".executor.total.peakSize"), # Peak utilization of the heap size at runtime
#                ("gceasy.gcStatistics_totalCreatedBytes", "Object Size (GB)", 1024, title + ".executor.createdObjectSize"), # Total amount of objects created by the application
#                ("gceasy.gcStatistics_avgPromotionRate", "Rate (MB/s)", 1, title + ".executor.avgPromotionRate"),
#                ("gceasy.gcStatistics_minorGCCount", "Count", 1, title + ".executor.minorGCCount"),
#                ("gceasy.gcStatistics_minorGCTotalTime", "Time (s)", 1, title + ".executor.minorGCTotalTime"),
#                ("gceasy.gcStatistics_fullGCCount", "Count", 1, title + ".executor.fullGCCount"),
#                ("gceasy.gcStatistics_fullGCTotalTime", "Time (s)", 1, title + ".executor.fullGCTotalTime"),
#                ("gceasy.gcStatistics_fullGCMaxTime", "Time (s)", 1, title + ".executor.fullGCMaxTime"),
#                ("gceasy.throughputPercentage", "Throughput (%)", 1, title + ".executor.throughputPercentage")
#                ]
#
#     taskMetricsAnalyzer = TaskMetricsCorrelationAnalyzer(appName, xmetrics, ymetrics)
#
#     appMetricsAnalyzer.analyzeMetrics(metrics, firstStatisticsDir, secondStatisticsDir, withMax)
#     appMetricsAnalyzer.plotMetrics(outputDir, firstSucessfulAppNum, secondSucessfulAppNum)


if __name__ == '__main__':

    # for GroupBy
    title = "GroupBy"
    appName = "GroupByRDD-0.5-2"
    taskInfoDir = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/" + appName + "/TaskInfo"

    # xmetrics = [("duration", "Time (s)", 1000),
    #             ("executorDeserializeTime", "Time (s)", 1000),
    #             ("executorDeserializeCpuTime", "Time (s)", 1000 * 1000 * 1000),
    #             ("executorRunTime", "Time (s)", 1000),
    #             ("executorCpuTime", "Time (s)", 1000 * 1000 * 1000),
    #             ("resultSize", "Size (MB)", 1024 * 1024),
    #             ("jvmGcTime", "Time (s)", 1000),
    #             ("resultSerializationTime",  "Time (s)", 1000),
    #             ("memoryBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024),
    #             ("diskBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024),
    #             ("inputMetrics.bytesRead", "Size (GB)", 1024 * 1024 * 1024),
    #             ("inputMetrics.recordsRead", "Number (M)", 1000 * 1000),
    #             ("outputMetrics.bytesWritten", "Size (GB)", 1024 * 1024 * 1024),
    #             ("outputMetrics.recordsWritten", "Number (M)", 1000 * 1000),
    #             ("shuffleReadMetrics.remoteBlocksFetched"," Number (M)", 1),
    #             ("shuffleReadMetrics.localBlocksFetched", "Number (M)", 1),
    #             ("shuffleReadMetrics.fetchWaitTime", "Time (s)", 1000),
    #             ("shuffleReadMetrics.remoteBytesRead", "Size (GB)", 1024 * 1024 * 1024),
    #             ("shuffleReadMetrics.recordsRead", "Number (M)", 1000 * 1000),
    #             ("shuffleWriteMetrics.bytesWritten", "Size (GB)", 1024 * 1024 * 1024),
    #             ("shuffleWriteMetrics.writeTime", "Time (s)", 1000),
    #             ("shuffleWriteMetrics.recordsWritten", "Number (M)", 1000 * 1000),
    #             ]

    xmetrics = [
                ("executorDeserializeTime", "Time (s)", 1000),
                ("executorDeserializeCpuTime", "Time (s)", 1000 * 1000 * 1000),
                ("executorRunTime", "Time (s)", 1000),
                ("executorCpuTime", "Time (s)", 1000 * 1000 * 1000),
                ("resultSize", "Size (MB)", 1024 * 1024),
                ("jvmGcTime", "Time (s)", 1000),
                ("resultSerializationTime",  "Time (s)", 1000),
                ("memoryBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024),
                ("diskBytesSpilled", "Spilled Size (GB)", 1024 * 1024 * 1024),
                ("inputMetrics.bytesRead", "Size (GB)", 1024 * 1024 * 1024),
                ("inputMetrics.recordsRead", "Number (M)", 1000 * 1000),
                ("outputMetrics.bytesWritten", "Size (GB)", 1024 * 1024 * 1024),
                ("outputMetrics.recordsWritten", "Number (M)", 1000 * 1000),
                ("shuffleReadMetrics.remoteBlocksFetched"," Number (M)", 1),
                ("shuffleReadMetrics.localBlocksFetched", "Number (M)", 1),
                ("shuffleReadMetrics.fetchWaitTime", "Time (s)", 1000),
                ("shuffleReadMetrics.remoteBytesRead", "Size (GB)", 1024 * 1024 * 1024),
                ("shuffleReadMetrics.bytesRead", "Size (GB)", 1024 * 1024 * 1024),
                ("shuffleReadMetrics.recordsRead", "Number (M)", 1000 * 1000),
                ("shuffleWriteMetrics.bytesWritten", "Size (GB)", 1024 * 1024 * 1024),
                ("shuffleWriteMetrics.writeTime", "Time (s)", 1000),
                ("shuffleWriteMetrics.recordsWritten", "Number (M)", 1000 * 1000),
                ]

    ymetrics = [("duration", "Time (s)", 1000)
                ]
    # can be the different colors in different data, configuration, and gc modes
    taskMetricsAnalyzer = TaskMetricsCorrelationAnalyzer(appName, taskInfoDir, xmetrics, ymetrics)
    taskMetricsAnalyzer.plotTaskInfo()


