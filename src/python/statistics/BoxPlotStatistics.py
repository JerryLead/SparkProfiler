class BoxPlotStatistics:
    # metric = ("app.duration", "Time (s)", 1000)
    def __init__(self, metric):
        self.name = metric[0]
        self.ylabel = metric[1]
        self.unit = metric[2]
        self.title = metric[3]

        self.Parallel = {}
        self.Parallel['label'] = 'Parallel'
        self.Parallel['E-1'] = {}
        self.Parallel['E-2'] = {}
        self.Parallel['E-4'] = {}

        self.CMS = {}
        self.CMS['label'] = 'CMS'
        self.CMS['E-1'] = {}
        self.CMS['E-2'] = {}
        self.CMS['E-4'] = {}

        self.G1 = {}
        self.G1['label'] = 'G1'
        self.G1['E-1'] = {}
        self.G1['E-2'] = {}
        self.G1['E-4'] = {}


    def addStatistics(self, line, fileName):
        self.name = line[line.find('[') + 1: line.find(']')]
        metrics = line[line.find(']') + 1:].replace(' ', '').split(',')

        stat = {}
        if fileName.lower().find("parallel") != -1:
            stat = self.Parallel
        elif fileName.lower().find("cms") != -1:
            stat = self.CMS
        elif fileName.lower().find("g1") != -1:
            stat = self.G1

        executorType = ''
        if fileName.lower().find("1-7g") != -1:
            executorType = 'E-1'
        elif fileName.lower().find("2-14g") != -1:
            executorType = 'E-2'
        elif fileName.lower().find("4-28g") != -1:
            executorType = 'E-4'

        stat[executorType]['label'] = executorType
        stat[executorType]['fliers'] = []

        for metric in metrics:
            metricName = metric.split('=')[0]
            metricValue = float(metric.split('=')[1]) / self.unit

            if(metricName == 'mean'):
                stat[executorType]['mean'] = metricValue
            elif(metricName == 'median'):
                stat[executorType]['med'] = metricValue
            elif(metricName == 'min'):
                stat[executorType]['whislo'] = metricValue
            elif(metricName == 'max'):
                stat[executorType]['whishi'] = metricValue
            elif(metricName == 'quantile25'):
                stat[executorType]['q1'] = metricValue
            elif(metricName == 'quantile75'):
                stat[executorType]['q3'] = metricValue

        stat[executorType]['whislo'] = stat[executorType]['q1']
        stat[executorType]['whishi'] = stat[executorType]['q3']