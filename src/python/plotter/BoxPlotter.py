import matplotlib.pyplot as plt
import matplotlib

class BoxPlotter:
    @staticmethod
    def plotStatisticsByGCAlgo(statistics, file, sucessfulAppNum):

        fig, axes = plt.subplots(ncols=3, sharey=True, figsize=(8,4))
        fig.subplots_adjust(wspace=0)


        Parallel = statistics.Parallel
        CMS = statistics.CMS
        G1 = statistics.G1

        colors = ['pink', 'lightblue', 'lightgreen']

        i = 0

        for ax, stats in zip(axes, [Parallel, CMS, G1]):
            list = [stats['E-1'], stats['E-2'], stats['E-4']]

            bplot = ax.bxp(list, showfliers=False, showmeans=True, patch_artist=True)
            for patch, color in zip(bplot['boxes'], colors):
                patch.set_facecolor(color)

            # ax.set_xticklabels(['E1', 'E2', 'E4'], fontsize=20)
            ax.set_xticklabels(['', '', ''], fontsize=1)
            # ax.set_xlabel(xlabel=stats['label'], fontsize=22)
            ax.tick_params(axis='y', labelsize=20)

            ax2 = ax.twiny()  # ax2 is responsible for "top" axis and "right" axis
            ax2.set_xticks(ax.get_xticks())
            ax2.set_xticklabels(sucessfulAppNum[i], fontsize=20)
            i += 1
            ax2.set_xlim(ax.get_xlim())

            ax.margins(0.05) # Optional

        axes[0].set_ylabel(statistics.ylabel, fontsize=20)
        fig.suptitle(statistics.title, fontsize=22, y=1.07)



        matplotlib.font_manager._rebuild()

        plt.show()

        #plt.savefig(file, dpi=150, bbox_inches='tight')