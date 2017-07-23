import matplotlib.pyplot as plt

class BoxPlotter:
    @staticmethod
    def plotStatisticsByGCAlgo(statistics, file):

        fig, axes = plt.subplots(ncols=3, sharey=True)
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

            ax.set_xticklabels(['E1*32', 'E2*16', 'E4*8'], fontsize=15)
            # ax.set(xticklabels=['E1*32', 'E2*16', 'E4*8'], xlabel=stats['label'], fontsize=12)
            ax.set_xlabel(xlabel=stats['label'], fontsize=18)
            ax.tick_params(axis='y', labelsize=17)
            ax.margins(0.05) # Optional

        axes[0].set_ylabel(statistics.ylabel, fontsize=17)
        fig.suptitle(statistics.title, fontsize=18)

        plt.savefig(file, dpi=150)