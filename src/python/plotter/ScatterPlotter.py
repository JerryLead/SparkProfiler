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
    def plotTaskMetrics(xValues, yValues, xLabel, yLabel, colors, file):
        plt.scatter(xValues, yValues, c=colors)
        plt.xlabel(xLabel)
        plt.ylabel(yLabel)
        plt.title(xLabel + "-" + yLabel)
        # plt.show()
        plt.savefig(file, dpi=150, bbox_inches='tight')