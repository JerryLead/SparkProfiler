import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import matplotlib as mpl
import os, sys

from datetime import datetime
from reader import FileReader

def plotExecutorAndWorkerUsage(appName, slowestTasksDir):
    for dir in os.listdir(slowestTasksDir):
        if (dir.startswith(".DS") == False):
            for topMetricsFile in os.listdir(os.path.join(slowestTasksDir, dir)):
                if (topMetricsFile.endswith(".txt")):
                    plotResourceUsage(os.path.join(slowestTasksDir, dir, topMetricsFile), slowestTasksDir, dir)


def plotResourceUsage(topMetricsFile, slowestTasksDir, appName):
    fileLines = FileReader.readLines(topMetricsFile)

    isExecutorMetric = False
    isSlaveMetric = False

    executorTime = []
    executorCPU = []
    executorMemory = []

    slaveTime = []
    slaveCPU = []
    slaveMemory = []

    for line in fileLines:
        if (line.startswith("[Top Metrics][Executor")):
            isExecutorMetric = True
        elif (line.startswith("[Top Metrics][aliSlave")):
            isSlaveMetric = True
            isExecutorMetric = False

        elif(isExecutorMetric == True and line.strip() != ""):
            time = line[line.find('[') + 1: line.find(']')]
            cpu = line[line.find('=') + 2: line.find(',')]
            memory = line[line.find('Memory') + 9:]
            executorTime.append(datetime.strptime(time, '%H:%M:%S'))
            executorCPU.append(float(cpu))
            executorMemory.append(float(memory))

        elif(isSlaveMetric == True and line.strip() != ""):
            time = line[line.find('[') + 1: line.find(']')]
            cpu = line[line.find('=') + 2: line.find(',')]
            memory = line[line.find('Memory') + 9:]
            slaveTime.append(datetime.strptime(time, '%H:%M:%S'))
            slaveCPU.append(float(cpu))
            slaveMemory.append(float(memory))


    fig, axes = plt.subplots(nrows=2, ncols=1, sharey=False, sharex= True)
    # locator = mpl.dates.MinuteLocator()
    xfmt = mdates.DateFormatter('%H:%M:%S')
    #ax.xaxis.set_major_locator(locator)
    axes[0].xaxis.set_major_formatter(xfmt)
    axes[1].xaxis.set_major_formatter(xfmt)
    axes[0].set_ylabel("Executor CPU (%)", color='r')
    axes[0].tick_params('y', colors='r')
    axes[1].set_ylabel("Worker CPU (%)", color='r')
    axes[1].tick_params('y', colors='r')
    axes[0].set_ylim(0, 840)  # The ceil
    axes[1].set_ylim(0, 105)  # The ceil
    # plt.ylim(0, statistics.max)  # The ceil
    # plt.legend()
    fig.autofmt_xdate()

    axes[0].plot_date(executorTime, executorCPU, '-r', label='CPU')
    axes[1].plot_date(slaveTime, slaveCPU, '-r', label='CPU')


    ax12 = axes[0].twinx()
    ax12.plot_date(executorTime, executorMemory, '-b', label='Memory')
    ax12.set_ylabel('Executor Memory (GB)', color='b')
    ax12.tick_params('y', colors='b')
    ax12.set_ylim(0, 32)  # The ceil
    # ax12.tick_params('y', colors='r')
    ax22 = axes[1].twinx()
    ax22.plot_date(slaveTime, slaveMemory, '-b', label='Memory')
    ax22.set_ylabel('Worker Memory (GB)', color='b')
    ax22.tick_params('y', colors='b')
    ax22.set_ylim(0, 32)  # The ceil

    plt.suptitle(appName)

    outputDir = os.path.join(slowestTasksDir, "topMetricsFigures")
    if not os.path.exists(outputDir):
        os.mkdir(outputDir)
    file = os.path.join(outputDir, appName + ".pdf")
    # plt.show()
    plt.savefig(file, dpi=150, bbox_inches='tight')




if __name__ == '__main__':

    dir = "/Users/xulijie/Documents/GCResearch/Experiments-11-17/medianProfiles/"
    taskDir = "/slowestTasks"
    #taskDir = "/failedTasks"

    # # for GroupByRDD
    # appName = "GroupByRDD-0.5"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    # appName = "GroupByRDD-1.0"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    #
    # # for RDDJoin
    # appName = "RDDJoin-0.5"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    appName = "RDDJoin-1.0"
    plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    #
    # # for SVM
    # appName = "SVM-0.5"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    # appName = "SVM-1.0"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    # #
    # # # for PageRank
    # appName = "PageRank-0.5"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    # appName = "PageRank-1.0"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)

