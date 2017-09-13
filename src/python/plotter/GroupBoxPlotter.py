import matplotlib.pyplot as plt

class GroupBoxPlotter:
    @staticmethod
    def plotStatisticsByGCAlgo(file, firstSucessfulAppNum, secondSucessfulAppNum,
                               firstStatistics, secondStatistics):

        fig, axes = plt.subplots(nrows=2, ncols=3, sharey=True, figsize=(8,7.6)) # sharey='row')
        fig.subplots_adjust(wspace=0)


        Parallel = firstStatistics.Parallel
        CMS = firstStatistics.CMS
        G1 = firstStatistics.G1

        colors = ['pink', 'lightblue', 'lightgreen']

        i = 0

        for ax, stats in zip(axes[0], [Parallel, CMS, G1]):
            list = [stats['E-1'], stats['E-2'], stats['E-4']]

            bplot = ax.bxp(list, showfliers=False, showmeans=True, patch_artist=True)
            for patch, color in zip(bplot['boxes'], colors):
                patch.set_facecolor(color)

            # ax.set_xticklabels(['E1', 'E2', 'E4'], fontsize=20)
            ax.set_xticklabels([])
            # ax.set_xlabel(xlabel=stats['label'], fontsize=22)
            ax.tick_params(axis='y', labelsize=20)

            ax2 = ax.twiny()  # ax2 is responsible for "top" axis and "right" axis
            ax2.set_xticks(ax.get_xticks())
            ax2.set_xticklabels(firstSucessfulAppNum[i], fontsize=20, y=0.98)
            i += 1
            ax2.set_xlim(ax.get_xlim())

            ax.margins(0.05) # Optional
            # if (firstStatistics.title.endswith("shuffleReadBytes")):
            #     print(secondStatistics.title)
            #     print(list)
            # if (firstStatistics.title.endswith("shuffleWriteBytes")):
            #     print(secondStatistics.title)
            #     print(list)
            # if (firstStatistics.title.endswith("inputBytes")):
            #     print(secondStatistics.title)
            #     print(list)
            # if (firstStatistics.title.endswith("outputBytes")):
            #     print(secondStatistics.title)
            #     print(list)
            # if (secondStatistics.title.endswith("resultSize")):
            #     print(secondStatistics.title)
            #     print(list)


        axes[0][0].set_ylabel(firstStatistics.ylabel, fontsize=20)
        fig.suptitle(firstStatistics.title, fontsize=22)#, y=1.02)



        Parallel = secondStatistics.Parallel
        CMS = secondStatistics.CMS
        G1 = secondStatistics.G1

        colors = ['pink', 'lightblue', 'lightgreen']

        i = 0

        for ax, stats in zip(axes[1], [Parallel, CMS, G1]):
            list = [stats['E-1'], stats['E-2'], stats['E-4']]

            bplot = ax.bxp(list, showfliers=False, showmeans=True, patch_artist=True)
            for patch, color in zip(bplot['boxes'], colors):
                patch.set_facecolor(color)

            ax.set_xticklabels(['E1', 'E2', 'E4'], fontsize=20)
            ax.set_xlabel(xlabel=stats['label'], fontsize=22)
            ax.tick_params(axis='y', labelsize=20)

            ax2 = ax.twiny()  # ax2 is responsible for "top" axis and "right" axis
            ax2.set_xticks(ax.get_xticks())
            ax2.set_xticklabels(secondSucessfulAppNum[i], fontsize=20, y=0.98)
            i += 1
            ax2.set_xlim(ax.get_xlim())

            ax.margins(0.05) # Optional

            # if (secondStatistics.title.endswith("shuffleReadBytes")):
            #     print(secondStatistics.title)
            #     print(list)
            # if (secondStatistics.title.endswith("shuffleWriteBytes")):
            #     print(secondStatistics.title)
            #     print(list)
            # if (secondStatistics.title.endswith("inputBytes")):
            #     print(secondStatistics.title)
            #     print(list)
            # if (secondStatistics.title.endswith("outputBytes")):
            #     print(secondStatistics.title)
            #     print(list)
            # if (secondStatistics.title.endswith("resultSize")):
            #     print(secondStatistics.title)
            #     print(list)


        axes[1][0].set_ylabel(secondStatistics.ylabel, fontsize=20)


        plt.savefig(file, dpi=150, bbox_inches='tight')