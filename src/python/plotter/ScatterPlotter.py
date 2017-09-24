import numpy as np
import matplotlib.pyplot as plt

# #basic
#
# fig, axes = plt.subplots(nrows=2, ncols=1, sharey=True) # sharey='row')
#
# plt.subplot(211)
# plt.scatter([1, 2, 3], [4, 5, 6])
#
# # with label
# plt.subplot(212)
# plt.scatter([7, 8, 9], [10, 11, 12])
#
# plt.show()

class ScatterPlotter:
    @staticmethod
    def plotTaskMetrics(xValues, yValues, xLabel, yLabel, file):
        parallel = plt.scatter(xValues["Parallel"], yValues["Parallel"], marker='o', color="red", alpha=0.1, edgecolors='none')
        cms = plt.scatter(xValues["CMS"], yValues["CMS"], marker='o', color='blue', alpha=0.1, edgecolors='none')
        g1  = plt.scatter(xValues["G1"], yValues["G1"], marker='o', color='green', alpha=0.1, edgecolors='none')

        plt.xlabel(xLabel)
        plt.ylabel(yLabel)
        plt.title(xLabel + "-" + yLabel)
        plt.legend((parallel, cms, g1),
                   ("Parallel", "CMS", "G1"),
                   scatterpoints=1,
                   loc='lower right',
                   ncol=1,
                   fontsize=8)
        # plt.show()
        plt.savefig(file, dpi=150, bbox_inches='tight')