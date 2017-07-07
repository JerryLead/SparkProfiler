import numpy as np
import matplotlib.pyplot as plt

class HistogramPlotter:

    @staticmethod
    def plotStatistics(statistics, title, ylabel, file, bar_width = 0.3):
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

        # plt.ylim(0, 50)  # The ceil
        plt.legend()
        # plt.tight_layout()
        # plt.show()
        plt.savefig(file, dpi=150)
