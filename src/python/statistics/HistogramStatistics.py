class HistogramStatistics:

    def __init__(self, metric):
        self.name = metric[0]
        self.ylabel = metric[1]
        self.unit = metric[2]
        # self.title = metric[3]

        self.parallel_means = [0, 0, 0]  # Executor(1-7G), Executor(2-14G), Executor(4-28G)
        self.cms_means = [0, 0, 0]
        self.g1_means = [0, 0, 0]
        self.parallel_stderr = [0, 0, 0]
        self.cms_stderr = [0, 0, 0]
        self.g1_stderr = [0, 0, 0]

        self.exec_1_7G_means = [0, 0, 0]  # Parallel, CMS, G1
        self.exec_2_14G_means = [0, 0, 0]
        self.exec_4_28G_means = [0, 0, 0]
        self.exec_1_7G_stderr = [0, 0, 0]
        self.exec_2_14G_stderr = [0, 0, 0]
        self.exec_4_28G_stderr = [0, 0, 0]

        self.max = 0

    def parseStatistics(self, line, gcAlgo, index):
        self.name = line[line.find('[') + 1: line.find(']')]
        metrics = line[line.find(']') + 1:].replace(' ', '').split(',')

        for metric in metrics:
            metricName = metric.split('=')[0]
            metricValue = float(metric.split('=')[1])

            if(metricName == "mean"):
                if(gcAlgo == "Parallel"):
                    self.parallel_means[index] = metricValue
                elif(gcAlgo == "CMS"):
                    self.cms_means[index] = metricValue
                elif(gcAlgo == "G1"):
                    self.g1_means[index] = metricValue

            if(metricName == "stdVar"):
                if(gcAlgo == "Parallel"):
                    self.parallel_stderr[index] = metricValue
                elif(gcAlgo == "CMS"):
                    self.cms_stderr[index] = metricValue
                elif(gcAlgo == "G1"):
                    self.g1_stderr[index] = metricValue


    def addHistogramStatistics(self, line, fileName):
        self.name = line[line.find('[') + 1: line.find(']')]
        metrics = line[line.find(']') + 1:].replace(' ', '').split(',')

        gcAlgoIndex = 0
        gcAlgo = ""
        if fileName.lower().find("parallel") != -1:
            gcAlgoIndex = 0
            gcAlgo = "Parallel"
        elif fileName.lower().find("cms") != -1:
            gcAlgoIndex = 1
            gcAlgo = "CMS"
        elif fileName.lower().find("g1") != -1:
            gcAlgoIndex = 2
            gcAlgo = "G1"

        executorIndex = 0
        if fileName.lower().find("1-7g") != -1:
            executorIndex = 0
        elif fileName.lower().find("2-14g") != -1:
            executorIndex = 1
        elif fileName.lower().find("4-28g") != -1:
            executorIndex = 2

        for metric in metrics:
            metricName = metric.split('=')[0]
            metricValue = float(metric.split('=')[1]) / self.unit

            if(metricName == "mean"):
                if(gcAlgo == "Parallel"):
                    self.parallel_means[executorIndex] = metricValue
                elif(gcAlgo == "CMS"):
                    self.cms_means[executorIndex] = metricValue
                elif(gcAlgo == "G1"):
                    self.g1_means[executorIndex] = metricValue
                self.addExectuorStatistics(executorIndex, gcAlgoIndex, "mean", metricValue)

            if(metricName == "stdVar"):
                if(gcAlgo == "Parallel"):
                    self.parallel_stderr[executorIndex] = metricValue
                elif(gcAlgo == "CMS"):
                    self.cms_stderr[executorIndex] = metricValue
                elif(gcAlgo == "G1"):
                    self.g1_stderr[executorIndex] = metricValue
                self.addExectuorStatistics(executorIndex, gcAlgoIndex, "stdVar", metricValue)

            if(metricName == "max"):
                if(metricValue > self.max):
                    self.max = metricValue

    def addExectuorStatistics(self, executorIndex, gcAlgoIndex, metricName, metricValue):
        if(executorIndex == 0):
            if(metricName == "mean"):
                self.exec_1_7G_means[gcAlgoIndex] = metricValue
            elif(metricName == "stdVar"):
                self.exec_1_7G_stderr[gcAlgoIndex] = metricValue
        elif(executorIndex == 1):
            if(metricName == "mean"):
                self.exec_2_14G_means[gcAlgoIndex] = metricValue
            elif(metricName == "stdVar"):
                self.exec_2_14G_stderr[gcAlgoIndex] = metricValue
        elif(executorIndex == 2):
            if(metricName == "mean"):
                self.exec_4_28G_means[gcAlgoIndex] = metricValue
            elif(metricName == "stdVar"):
                self.exec_4_28G_stderr[gcAlgoIndex] = metricValue