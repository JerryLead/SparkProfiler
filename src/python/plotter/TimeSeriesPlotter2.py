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
                if (topMetricsFile.startswith("topMetrics")):
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

    first_time = -1

    for line in fileLines:
        if (line.startswith("[Top Metrics][Executor")):
            isExecutorMetric = True
            first_time = -1
        elif (line.startswith("[Top Metrics][aliSlave")):
            isSlaveMetric = True
            isExecutorMetric = False
            first_time = -1
        elif(isExecutorMetric == True and line.strip() != ""):
            time = line[line.find('[') + 1: line.find(']')]
            cpu = line[line.find('=') + 2: line.find(',')]
            memory = line[line.find('Memory') + 9:]

            if first_time == -1:
                first_time = datetime.strptime(time, '%H:%M:%S')
                time = first_time - first_time
            else:
                cur_time = datetime.strptime(time, '%H:%M:%S')
                time = cur_time - first_time

            # executorTime.append(datetime.strptime(time, '%H:%M:%S'))
            executorTime.append(time.seconds)
            executorCPU.append(float(cpu))
            executorMemory.append(float(memory))

        elif(isSlaveMetric == True and line.strip() != ""):
            time = line[line.find('[') + 1: line.find(']')]
            cpu = line[line.find('=') + 2: line.find(',')]
            memory = line[line.find('Memory') + 9:]

            if first_time == -1:
                first_time = datetime.strptime(time, '%H:%M:%S')
                time = first_time - first_time
                print first_time
            else:
                cur_time = datetime.strptime(time, '%H:%M:%S')
                time = cur_time - first_time
            slaveTime.append(time.seconds)
            slaveCPU.append(float(cpu))
            slaveMemory.append(float(memory))


    fig, axes = plt.subplots(nrows=2, ncols=1, sharey=False, sharex= True)
    # locator = mpl.dates.MinuteLocator()
    # xfmt = mdates.DateFormatter('%H:%M:%S')
    #ax.xaxis.set_major_locator(locator)
    # axes[0].xaxis.set_major_formatter(xfmt)
    # axes[1].xaxis.set_major_formatter(xfmt)
    axes[0].set_ylabel("Executor CPU (%)", color='r')
    axes[0].tick_params('y', colors='r')
    axes[1].set_ylabel("Worker CPU (%)", color='r')
    axes[1].tick_params('y', colors='r')
    axes[0].set_ylim(0, 840)  # The ceil
    axes[1].set_ylim(0, 105)  # The ceil
    axes[1].set_xlabel("Time (seconds)", color=u'#000000')
    # plt.ylim(0, statistics.max)  # The ceil
    # plt.legend()
    fig.autofmt_xdate()

    axes[0].plot(executorTime, executorCPU, '-r', label='CPU')
    axes[1].plot(slaveTime, slaveCPU, '-r', label='CPU')
    ax12 = axes[0].twinx()
    ax12.plot(executorTime, executorMemory, '-b', label='Memory')
    ax12.set_ylabel('Executor Memory (GB)', color='b')
    ax12.tick_params('y', colors='b')
    ax12.set_ylim(0, 32)  # The ceil
    # ax12.tick_params('y', colors='r')
    ax22 = axes[1].twinx()
    ax22.plot(slaveTime, slaveMemory, '-b', label='Memory')
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

    dir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/medianProfiles/"
    taskDir = "/SlowestTask"
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
    # appName = "RDDJoin-1.0"
    # plotExecutorAndWorkerUsage(appName, "/Users/jaxon/github/slowestTasks/")
    #
    # # for SVM
    # appName = "SVM-0.5"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    # appName = "SVM-1.0"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    # #
    # # # for PageRank
    appName = "PageRank-0.5"
    plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)
    # appName = "PageRank-1.0"
    # plotExecutorAndWorkerUsage(appName, dir + appName + taskDir)

