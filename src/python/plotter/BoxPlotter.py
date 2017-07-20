import matplotlib.pyplot as plt

class BoxPlotter:
    @staticmethod
    def plotStatisticsByGCAlgo(statistics, file):

        fig, axes = plt.subplots(ncols=3, sharey=True)
        fig.subplots_adjust(wspace=0)

        Parallel = statistics.Parallel
        CMS = statistics.CMS
        G1 = statistics.G1

        for ax, stats in zip(axes, [Parallel, CMS, G1]):
            list = [stats['E-1'], stats['E-2'], stats['E-4']]

            ax.bxp(list, showfliers=False, showmeans=True)
            ax.set(xticklabels=['E-1*32', 'E-2*16', 'E-4*8'], xlabel=stats['label'])
            ax.margins(0.05) # Optional

        axes[0].set_ylabel(statistics.ylabel)
        fig.suptitle(statistics.name)

        plt.savefig(file, dpi=150)