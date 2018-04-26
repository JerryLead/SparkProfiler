import numpy as np
import matplotlib.pyplot as plt

class HistogramPlotter:

    @staticmethod
    def plotStatisticsByExecutor(statistics, title, ylabel, file, bar_width = 0.3):
        plt.rc('font', family='Arial')

        n_groups = 3
        parallel_means = statistics.parallel_means
        cms_means = statistics.cms_means
        g1_means = statistics.g1_means

        parallel_stderr = statistics.parallel_stderr
        cms_stderr = statistics.cms_stderr
        g1_stderr = statistics.g1_stderr

        fig, ax = plt.subplots()
        index = np.arange(n_groups)

        opacity = 0.4
        error_config = {'ecolor': '0.3'}

        rects1 = plt.bar(index, parallel_means, bar_width / 2, alpha=opacity, color='b', yerr=parallel_stderr, error_kw=error_config, label='Parallel')
        rects2 = plt.bar(index + bar_width / 2, cms_means, bar_width / 2, alpha=opacity, color='r', yerr=cms_stderr, error_kw=error_config, label='CMS')
        rects3 = plt.bar(index + bar_width, g1_means, bar_width / 2, alpha=opacity, color='y', yerr=g1_stderr, error_kw=error_config, label='G1')

        # plt.xlabel('Category')
        plt.ylabel(ylabel)
        plt.title(title)

        # x_text=["PS-1-7G","CMS-1-7G","G1-1-7G","PS-2-14G","CMS-2-14G","G1-2-14G","PS-4-28G","CMS-4-28G","G1-4-28G"]
        # plt.xticks(index - 0.2+ 2*bar_width, ('balde', 'bunny', 'dragon', 'happy', 'pillow'))
        # plt.xticks(index - 0.2 + 2 * bar_width, ('balde', 'bunny', 'dragon'), fontsize = 18)

        # plt.yticks(fontsize=18)  # change the num axis size

        plt.xticks(index + 0.75 * bar_width, ('Executor (1 task, 7GB)', 'Executor (2 tasks, 14GB)', 'Executor (4 tasks, 28GB)'))

        # plt.ylim(0, statistics.max)  # The ceil
        # plt.legend()
        # plt.tight_layout()
        # plt.show()
        plt.savefig(file, dpi=150)


    @staticmethod
    def plotStatisticsByGCAlgo(statistics, title, ylabel, file, bar_width = 0.3):
        n_groups = 3
        exec_1_7G_means = statistics.exec_1_7G_means
        exec_2_14G_means = statistics.exec_2_14G_means
        exec_4_28G_means = statistics.exec_4_28G_means

        exec_1_7G_stderr = statistics.exec_1_7G_stderr
        exec_2_14G_stderr = statistics.exec_2_14G_stderr
        exec_4_28G_stderr = statistics.exec_4_28G_stderr

        fig, ax = plt.subplots()
        index = np.arange(n_groups)

        opacity = 0.4
        error_config = {'ecolor': '0.3'}

        rects1 = plt.bar(index, exec_1_7G_means, bar_width / 2, alpha=opacity, color='b', yerr=exec_1_7G_stderr, error_kw=error_config, label='Executor-1-7GB')
        rects2 = plt.bar(index + bar_width / 2, exec_2_14G_means, bar_width / 2, alpha=opacity, color='r', yerr=exec_2_14G_stderr, error_kw=error_config, label='Executor-2-14GB')
        rects3 = plt.bar(index + bar_width, exec_4_28G_means, bar_width / 2, alpha=opacity, color='y', yerr=exec_4_28G_stderr, error_kw=error_config, label='Executor-4-28GB')

        # plt.xlabel('Category')
        plt.ylabel(ylabel)
        plt.title(title)

        # x_text=["PS-1-7G","CMS-1-7G","G1-1-7G","PS-2-14G","CMS-2-14G","G1-2-14G","PS-4-28G","CMS-4-28G","G1-4-28G"]
        # plt.xticks(index - 0.2+ 2*bar_width, ('balde', 'bunny', 'dragon', 'happy', 'pillow'))
        # plt.xticks(index - 0.2 + 2 * bar_width, ('balde', 'bunny', 'dragon'), fontsize = 18)

        # plt.yticks(fontsize=18)  # change the num axis size

        plt.xticks(index + 0.75 * bar_width, ('Parallel', 'CMS', 'G1'))

        # plt.ylim(0, statistics.max)  # The ceil
        # plt.legend()
        plt.tight_layout()
        # plt.show()
        plt.savefig(file, dpi=150)

    # https://matplotlib.org/gallery/api/barchart.html#sphx-glr-gallery-api-barchart-py
    @staticmethod
    def plotMultiStatisticsByGCAlgo(statisticsList, title, ylabel, file, ylim, legend, topLabel, bar_width=0.5):
        plt.rc('font', family='Helvetica')

        n_groups = 3
        fig, ax = plt.subplots(figsize=(4, 3), dpi=80)
        index = np.arange(n_groups)
        opacity = 0.6
        error_config = {'ecolor': '0.3'}
        max = 0

        for i in range(0, len(statisticsList)):
            statistics = statisticsList[i]
            exec_1_7G_means = statistics.exec_1_7G_means
            exec_1_7G_stderr = statistics.exec_1_7G_stderr

            rects1 = plt.bar(index + bar_width/2*i, exec_1_7G_means, bar_width / 2, alpha=opacity,
                             label=statistics.legend, yerr=exec_1_7G_stderr, error_kw=error_config)
            HistogramPlotter.autolabel(rects1, topLabel, 'center')
            for value in exec_1_7G_means:
                if value > max:
                    max = value
        # plt.xlabel('Category')
        plt.ylabel(ylabel, fontsize=12)
        plt.title(title, fontsize=16)

        # x_text=["PS-1-7G","CMS-1-7G","G1-1-7G","PS-2-14G","CMS-2-14G","G1-2-14G","PS-4-28G","CMS-4-28G","G1-4-28G"]
        # plt.xticks(index - 0.2+ 2*bar_width, ('balde', 'bunny', 'dragon', 'happy', 'pillow'))
        # plt.xticks(index - 0.2 + 2 * bar_width, ('balde', 'bunny', 'dragon'), fontsize = 18)

        plt.yticks(fontsize=12)  # change the num axis size

        interval = 0.5/2
        if (len(statisticsList) == 3):
            interval = 0.5
        plt.xticks(index + interval * bar_width, ('Parallel', 'CMS', 'G1'), fontsize=16)
        # print max
        plt.ylim(0, max * ylim)  # The ceil
        plt.legend(fontsize=12, loc=legend, frameon=False)
        plt.tight_layout()

        #plt.show()
        plt.savefig(file)

    @staticmethod
    def autolabel(rects, topLabel, xpos='center'):
        """
        Attach a text label above each bar in *rects*, displaying its height.

        *xpos* indicates which side to place the text w.r.t. the center of
        the bar. It can be one of the following {'center', 'right', 'left'}.
        """

        xpos = xpos.lower()  # normalize the case of the parameter
        ha = {'center': 'center', 'right': 'left', 'left': 'right'}
        offset = {'center': 0.5, 'right': 0.5, 'left': 0.5}  # x_txt = x + w*off

        for rect in rects:
            height = rect.get_height()
            plt.text(rect.get_x() + rect.get_width()*offset[xpos], topLabel*height,
                    '{}'.format(int(round(height, 1))), ha=ha[xpos], va='bottom',
                     fontsize=12)

