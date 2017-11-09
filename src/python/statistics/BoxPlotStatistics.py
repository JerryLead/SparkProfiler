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


    def addStatistics(self, line, fileName, withMax):
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
        if fileName.lower().find("1-6656m") != -1:
            executorType = 'E-1'
        elif fileName.lower().find("2-13g") != -1:
            executorType = 'E-2'
        elif fileName.lower().find("4-26g") != -1:
            executorType = 'E-4'

        stat[executorType]['label'] = executorType
        stat[executorType]['fliers'] = []

        for metric in metrics:
            metricName = metric.split('=')[0]
            metricValue = float(metric.split('=')[1]) / self.unit

            if(metricName == 'mean'):
                # stat[executorType]['mean'] = metricValue
                pass
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

        if(withMax == False):
            stat[executorType]['whishi'] = stat[executorType]['q3']

    def checkAndFillNulls(self):
        for stat in [self.Parallel, self.CMS, self.G1]:
            for executorType in ['E-1', 'E-2', 'E-4']:
                if(stat[executorType].has_key('label') == False):
                    stat[executorType]['label'] = executorType
                    stat[executorType]['fliers'] = []

                    # stat[executorType]['mean'] = float('NaN')
                    stat[executorType]['med'] = float('NaN')
                    stat[executorType]['whislo'] = float('NaN')
                    stat[executorType]['whishi'] = float('NaN')
                    stat[executorType]['q1'] = float('NaN')
                    stat[executorType]['q3'] = float('NaN')


