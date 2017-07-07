
class Statistics:

    def __init__(self):
        self.name = ''
        self.unit = ''
        self.parallel_means = [0, 0, 0]  # Executor(1-7G), Executor(2-14G), Executor(4-28G)
        self.cms_means = [0, 0, 0]
        self.g1_means = [0, 0, 0]

        self.parallel_stderr = [0, 0, 0]
        self.cms_stderr = [0, 0, 0]
        self.g1_stderr = [0, 0, 0]

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

    def addStatistics(self, line, fileName):
        self.name = line[line.find('[') + 1: line.find(']')]
        metrics = line[line.find(']') + 1:].replace(' ', '').split(',')

        gcAlgo = ""
        if fileName.lower().find("parallel") != -1:
            gcAlgo = "Parallel"
        elif fileName.lower().find("cms") != -1:
            gcAlgo = "CMS"
        elif fileName.lower().find("g1") != -1:
            gcAlgo = "G1"

        index = 0
        if fileName.lower().find("1-7g") != -1:
            index = 0
        elif fileName.lower().find("2-14g") != -1:
            index = 1
        elif fileName.lower().find("4-28g") != -1:
            index = 2

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