import numpy as np
import matplotlib.pyplot as plt



class Statistics:

    def __init__(self):
        self.name = ''
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


def plotStatistics(statistics):
    n_groups = 3

    parallel_means = statistics.parallel_means
    cms_means = statistics.cms_means
    g1_means = statistics.g1_means

    parallel_stderr = statistics.parallel_stderr
    cms_stderr = statistics.cms_stderr
    g1_stderr = statistics.g1_stderr

    bar_width = 0.3
    fig, ax = plt.subplots()
    index = np.arange(n_groups)

    opacity = 0.4
    error_config = {'ecolor': '0.3'}

    rects1 = plt.bar(index, parallel_means, bar_width / 2, alpha=opacity, color='b', yerr=parallel_stderr, error_kw=error_config, label='Parallel')
    rects2 = plt.bar(index + bar_width / 2, cms_means, bar_width / 2, alpha=opacity, color='r', yerr=cms_stderr, error_kw=error_config, label='CMS')
    rects3 = plt.bar(index + bar_width, g1_means, bar_width / 2, alpha=opacity, color='y', yerr=g1_stderr, error_kw=error_config, label='G1')

    # plt.xlabel('Category')
    plt.ylabel('Execution time (s)')
    plt.title('Application Duration')

    # x_text=["PS-1-7G","CMS-1-7G","G1-1-7G","PS-2-14G","CMS-2-14G","G1-2-14G","PS-4-28G","CMS-4-28G","G1-4-28G"]
    # plt.xticks(index - 0.2+ 2*bar_width, ('balde', 'bunny', 'dragon', 'happy', 'pillow'))
    # plt.xticks(index - 0.2 + 2 * bar_width, ('balde', 'bunny', 'dragon'), fontsize = 18)

    # plt.yticks(fontsize=18)  # change the num axis size

    plt.xticks(index + 0.75 * bar_width, ('Executor (1 task, 7GB)', 'Executor (2 tasks, 14GB)', 'Executor (4 tasks, 28GB)'))

    # plt.ylim(0, 50)  # The ceil
    plt.legend()
    plt.tight_layout()
    plt.show()


if __name__ == '__main__':

    CMS_1_7G_line = '[app.duration] mean = 224339.20, stdVar = 8311.91, median = 225233.00, min = 211999.00, quantile25 = 216682.50, quantile75 = 231549.00, max = 233837.00'
    CMS_2_14G_line = '[app.duration] mean = 222100.80, stdVar = 4980.08, median = 221792.00, min = 216099.00, quantile25 = 218053.50, quantile75 = 226302.50, max = 229723.00'
    CMS_4_28G_line = '[app.duration] mean = 236539.00, stdVar = 4510.62, median = 235419.00, min = 231085.00, quantile25 = 232774.00, quantile75 = 240864.00, max = 242945.00'
    Parallel_1_7G_line = '[app.duration] mean = 289888.20, stdVar = 3613.19, median = 290094.00, min = 285679.00, quantile25 = 286867.50, quantile75 = 292806.00, max = 295459.00'
    Parallel_2_14G_line = '[app.duration] mean = 286638.80, stdVar = 5092.88, median = 285176.00, min = 280449.00, quantile25 = 282331.00, quantile75 = 291678.00, max = 293498.00'
    Parallel_4_28G_line = '[app.duration] mean = 291306.40, stdVar = 6454.79, median = 289418.00, min = 282424.00, quantile25 = 285909.00, quantile75 = 297648.00, max = 297877.00'

    statistics = Statistics()
    statistics.parseStatistics(CMS_1_7G_line, "CMS", 0)
    statistics.parseStatistics(CMS_2_14G_line, "CMS", 1)
    statistics.parseStatistics(CMS_4_28G_line, "CMS", 2)
    statistics.parseStatistics(Parallel_1_7G_line, "Parallel", 0)
    statistics.parseStatistics(Parallel_2_14G_line, "Parallel", 1)
    statistics.parseStatistics(Parallel_4_28G_line, "Parallel", 2)

    plotStatistics(statistics)


