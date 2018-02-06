class TaskAttempt:
    def __init__(self, appId, appName, stageId, index, GC, executor):
        self.appId = appId
        self.appName = appName
        self.stageId = stageId
        self.taskId = index
        self.attemptId = -1

        self.executorId = -1
        self.host = ""
        self.taskLocality = ""

        self.duration = -1
        self.executorDeserializeTime = -1
        self.executorDeserializeCpuTime = -1
        self.executorRunTime = -1
        self.executorCpuTime = -1
        self.resultSize = -1
        self.jvmGcTime = -1
        self.resultSerializationTime = -1
        self.memoryBytesSpilled = -1
        self.diskBytesSpilled = -1

        self.inputMetrics_bytesRead = -1
        self.inputMetrics_recordsRead = -1

        self.outputMetrics_bytesWritten = -1
        self.outputMetrics_recordsWritten = -1

        self.shuffleReadMetrics_remoteBlocksFetched = -1
        self.shuffleReadMetrics_localBlocksFetched = -1
        self.shuffleReadMetrics_fetchWaitTime = -1
        self.shuffleReadMetrics_remoteBytesRead = -1
        self.shuffleReadMetrics_localBytesRead = -1
        self.shuffleReadMetrics_recordsRead = -1
        self.shuffleReadMetrics_bytesRead = -1

        self.shuffleWriteMetrics_bytesWritten = -1
        self.shuffleWriteMetrics_writeTime = -1
        self.shuffleWriteMetrics_recordsWritten = -1

        self.errorMessage = ''

        self.GC = GC
        self.executor = executor

    def set(self, metricName, metricValue):
        if (metricName == 'attemptId'):
            self.attemptId = int(metricValue)
        elif (metricName == 'executorId'):
            self.executorId = int(metricValue)
        elif (metricName == 'duration'):
            self.duration = float(metricValue)
        elif (metricName == 'executorDeserializeTime'):
            self.executorDeserializeTime = float(metricValue)
        elif (metricName == 'executorDeserializeCpuTime'):
            self.executorDeserializeCpuTime = float(metricValue)
        elif (metricName == 'executorRunTime'):
            self.executorRunTime = float(metricValue)
        elif (metricName == 'executorCpuTime'):
            self.executorCpuTime = float(metricValue)
        elif (metricName == 'resultSize'):
            self.resultSize = float(metricValue)
        elif (metricName == 'jvmGcTime'):
            self.jvmGcTime = float(metricValue)
        elif (metricName == 'resultSerializationTime'):
            self.resultSerializationTime = float(metricValue)
        elif (metricName == 'memoryBytesSpilled'):
            self.memoryBytesSpilled = float(metricValue)
        elif (metricName == 'diskBytesSpilled'):
            self.diskBytesSpilled = float(metricValue)
        elif (metricName == 'inputMetrics.bytesRead'):
            self.inputMetrics_bytesRead = float(metricValue)
        elif (metricName == 'inputMetrics.recordsRead'):
            self.inputMetrics_recordsRead = float(metricValue)
        elif (metricName == 'outputMetrics.bytesWritten'):
            self.outputMetrics_bytesWritten = float(metricValue)
        elif (metricName == 'outputMetrics.recordsWritten'):
            self.outputMetrics_recordsWritten = float(metricValue)
        elif (metricName == 'shuffleReadMetrics.remoteBlocksFetched'):
            self.shuffleReadMetrics_remoteBlocksFetched = float(metricValue)
        elif (metricName == 'shuffleReadMetrics.localBlocksFetched'):
            self.shuffleReadMetrics_localBlocksFetched = float(metricValue)
        elif (metricName == 'shuffleReadMetrics.fetchWaitTime'):
            self.shuffleReadMetrics_fetchWaitTime = float(metricValue)
        elif (metricName == 'shuffleReadMetrics.bytesRead'):
            self.shuffleReadMetrics_bytesRead += float(metricValue)
        elif (metricName == 'shuffleReadMetrics.remoteBytesRead'):
            self.shuffleReadMetrics_remoteBytesRead = float(metricValue)
        elif (metricName == 'shuffleReadMetrics.localBytesRead'):
            self.shuffleReadMetrics_localBytesRead = float(metricValue)
        elif (metricName == 'shuffleReadMetrics.recordsRead'):
            self.shuffleReadMetrics_recordsRead = float(metricValue)
        elif (metricName == 'shuffleWriteMetrics.bytesWritten'):
            self.shuffleWriteMetrics_bytesWritten = float(metricValue)
        elif (metricName == 'shuffleWriteMetrics.writeTime'):
            self.shuffleWriteMetrics_writeTime = float(metricValue)
        elif (metricName == 'shuffleWriteMetrics.recordsWritten'):
            self.shuffleWriteMetrics_recordsWritten = float(metricValue)

    def get(self, metricName):
        if (metricName == 'attemptId'):
            return self.attemptId
        elif (metricName == 'executorId'):
            return self.executorId
        elif (metricName == 'duration'):
            return self.duration
        elif (metricName == 'executorDeserializeTime'):
            return self.executorDeserializeTime
        elif (metricName == 'executorDeserializeCpuTime'):
            return self.executorDeserializeCpuTime
        elif (metricName == 'executorRunTime'):
            return self.executorRunTime
        elif (metricName == 'executorCpuTime'):
            return self.executorCpuTime
        elif (metricName == 'resultSize'):
            return self.resultSize
        elif (metricName == 'jvmGcTime'):
            return self.jvmGcTime
        elif (metricName == 'resultSerializationTime'):
            return self.resultSerializationTime
        elif (metricName == 'memoryBytesSpilled'):
            return self.memoryBytesSpilled
        elif (metricName == 'diskBytesSpilled'):
            return self.diskBytesSpilled
        elif (metricName == 'inputMetrics.bytesRead'):
            return self.inputMetrics_bytesRead
        elif (metricName == 'inputMetrics.recordsRead'):
            return self.inputMetrics_recordsRead
        elif (metricName == 'outputMetrics.bytesWritten'):
            return self.outputMetrics_bytesWritten
        elif (metricName == 'outputMetrics.recordsWritten'):
            return self.outputMetrics_recordsWritten
        elif (metricName == 'shuffleReadMetrics.remoteBlocksFetched'):
            return self.shuffleReadMetrics_remoteBlocksFetched
        elif (metricName == 'shuffleReadMetrics.localBlocksFetched'):
            return self.shuffleReadMetrics_localBlocksFetched
        elif (metricName == 'shuffleReadMetrics.fetchWaitTime'):
            return self.shuffleReadMetrics_fetchWaitTime
        elif (metricName == 'shuffleReadMetrics.bytesRead'):
            return self.shuffleReadMetrics_bytesRead
        elif (metricName == 'shuffleReadMetrics.remoteBytesRead'):
            return self.shuffleReadMetrics_remoteBytesRead
        elif (metricName == 'shuffleReadMetrics.localBytesRead'):
            return self.shuffleReadMetrics_localBytesRead
        elif (metricName == 'shuffleReadMetrics.bytesRead'):
            return self.shuffleReadMetrics_bytesRead
        elif (metricName == 'shuffleReadMetrics.recordsRead'):
            return self.shuffleReadMetrics_recordsRead
        elif (metricName == 'shuffleWriteMetrics.bytesWritten'):
            return self.shuffleWriteMetrics_bytesWritten
        elif (metricName == 'shuffleWriteMetrics.writeTime'):
            return self.shuffleWriteMetrics_writeTime
        elif (metricName == 'shuffleWriteMetrics.recordsWritten'):
            return self.shuffleWriteMetrics_recordsWritten