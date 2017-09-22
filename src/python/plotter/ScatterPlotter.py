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
    def plotTaskMetrics(xmetric, ymetric):
        plt.scatter(xmetric, ymetric)
        plt.show()