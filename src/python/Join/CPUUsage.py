import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import matplotlib as mpl
import os, sys
from datetime import datetime
#from reader import FileReader

def plotExecutorAndWorkerUsage(appName, slowestTasksDir):
    for dir in os.listdir(slowestTasksDir):
        if (dir.startswith(".DS") == False):
            for topMetricsFile in os.listdir(os.path.join(slowestTasksDir, dir)):
                if (topMetricsFile.startswith("topMetrics")):
                    plotResourceUsage(os.path.join(slowestTasksDir, dir, topMetricsFile), slowestTasksDir, dir)


def plotResourceUsage(topMetricsFile, slowestTasksDir, appName):
    fileLines = open(topMetricsFile,"r").readlines()
    print(fileLines)
    isExecutorMetric = False
    isSlaveMetric = False

    executorTime = []
    executorCPU = []
    executorMemory = []

    slaveTime = []
    slaveCPU = []
    slaveMemory = []

    first_time = -1

    max_y=0
    max_x=0

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
            executorCPU.append(float(cpu)/2)
            executorMemory.append(float(memory))
            if float(cpu)>max_y:
                max_x=time.seconds
                max_y=float(cpu)

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

    plt.rc('pdf', fonttype=42)
    plt.rc('font', family='Helvetica', size=10)
    fig = plt.figure(figsize=(3.4, 2.4))
    axes = fig.add_subplot(111)
    plt.subplots_adjust(left=0.18, bottom=0.18, right=0.96, top=0.88,
                        wspace=0.2, hspace=0.2)
    mark="a"
    #plt.subplots(nrows=1, ncols=1, sharey=False, sharex= True)
    if appName=="G1":
        max_x=109
        mark="b"
    # locator = mpl.dates.MinuteLocator()
    # xfmt = mdates.DateFormatter('%H:%M:%S')
    #ax.xaxis.set_major_locator(locator)
    # axes[0].xaxis.set_major_formatter(xfmt)
    # axes[1].xaxis.set_major_formatter(xfmt)
    axes.set_ylabel("Executor CPU (%)", color='k')
    axes.tick_params('y', colors='k')
    #axes[1].set_ylabel("Worker CPU (%)", color='r')
    #axes[1].tick_params('y', colors='r')
    axes.set_ylim(0, 400)  # The ceil
    axes.set_xlim(0, 400)
    #axes[1].set_ylim(0, 105)  # The ceil
    axes.set_xlabel("Time (s)", color=u'#000000')
    #plt.xlim(0, executorTime.max)  # The ceil

    #fig.autofmt_xdate()
    #axes.hlines(100, 0, 400, colors = "black", linestyles = "dashed", linewidth=1)
    #axes.hlines(200, 0, 400, colors = "black", linestyles = ":", linewidth=1)
    axes.vlines(max_x, 0, 400, colors = "grey", linestyles = "--", linewidth=1)

    axes.plot(executorTime, executorCPU, '-r', label=appName+' CPU Usage')
    plt.legend(markerfirst=False,frameon=False)
    axes.grid(True,axis='y')
    axes.spines['bottom'].set_linewidth(1.5)
    axes.spines['left'].set_linewidth(1.5)
    axes.spines['top'].set_linewidth(1.5)
    axes.spines['right'].set_linewidth(1.5)
    #axes[1].plot(slaveTime, slaveCPU, '-r', label='CPU')
    #ax12 = axes.twinx()
    #ax12.plot(executorTime, executorMemory, '-b', label='Memory')
    #ax12.set_ylabel('Executor Memory (GB)', color='b')
    #ax12.tick_params('y', colors='b')
    #ax12.set_ylim(0, 32)  # The ceil
    # ax12.tick_params('y', colors='r')
    #ax22 = axes[1].twinx()
    #ax22.plot(slaveTime, slaveMemory, '-b', label='Memory')
    #ax22.set_ylabel('Worker Memory (GB)', color='b')
    #ax22.tick_params('y', colors='b')
    #ax22.set_ylim(0, 32)  # The ceil


    #ax12.set_xlim(xmin=0)
    #ax22.set_xlim(xmin=0)

    plt.title("("+mark+") Join-1.0-"+appName+"-CPU-usage", y=1)

    outputDir = os.path.join(slowestTasksDir, "topMetricsFigures")
    if not os.path.exists(outputDir):
        os.mkdir(outputDir)
    file = os.path.join(outputDir, appName + ".pdf")
    plt.show()
    #plt.savefig(file, dpi=150, bbox_inches='tight')




if __name__ == '__main__':

    #dir = "D:/plot"
    dir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/medianProfiles/RDDJoin-1.0"

    taskDir = "/SlowestTask/"
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
    plotExecutorAndWorkerUsage(appName, dir + taskDir)
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

