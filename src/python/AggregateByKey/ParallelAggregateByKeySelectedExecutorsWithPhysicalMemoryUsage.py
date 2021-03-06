import matplotlib.pyplot as plt
import numpy as np
from matplotlib import gridspec
import matplotlib.dates as mdates
import matplotlib as mpl
import os, sys

from datetime import datetime
from reader import FileReader

mpl.rcParams['axes.linewidth'] = 1.2 #set the value globally

#plt.rc('font', family='Helvetica')
# font = {'family' : 'Helvetica',
#         'weight' : 'normal',
#         'color'  : 'black',
#         'size'   : '12'}

plt.rc('pdf', fonttype=42)
plt.rc('font', family='Helvetica', size=10)


class Usage:
    def __init__(self, gcType, time, beforeGC, afterGC, allocated, gcPause, gcCause):
        self.gcType = gcType
        self.time = time
        self.beforeGC = beforeGC
        self.afterGC = afterGC
        self.allocated = allocated
        self.gcPause = gcPause
        self.gcCause = gcCause

    def getGCType(self):
        return self.gcType;

    def getTime(self):
        return self.time

    def getBeforeGC(self):
        return self.beforeGC

    def getAfterGC(self):
        return self.afterGC

    def getAllocated(self):
        return self.allocated

    def getGCPause(self):
        return self.gcPause

    def getGCCause(self):
        return self.gcCause

    def getReclaimed(self):
        return (self.beforeGC - self.afterGC)




class HeapUsage:
    def __init__(self):
        self.youngGen = []
        self.oldGen = []

    def parseUsage(self, line, timeOffset):
        gcType = line[line.find('(') + 1: line.find(')')]
        commaIndex = line.find(',')
        time = float(line[line.find('time') + 7: commaIndex]) - timeOffset
        commaIndex = line.find(',', commaIndex + 1)
        beforeGC = float(line[line.find('beforeGC') + 11: commaIndex]) / 1024
        commaIndex = line.find(',', commaIndex + 1)
        afterGC = float(line[line.find('afterGC') + 10: commaIndex]) / 1024
        commaIndex = line.find(',', commaIndex + 1)
        allocated = float(line[line.find('allocated') + 12: commaIndex]) / 1024
        commaIndex = line.find(',', commaIndex + 1)
        gcPause = float(line[line.find('gcPause') + 10: commaIndex - 1])
        gcCause = line[line.find('gcCause') + 10: ]

        return Usage(gcType, time, beforeGC, afterGC, allocated, gcPause, gcCause)

    def initHeapUsage(self, gclogFile, timeOffset):
        fileLines = FileReader.readLines(gclogFile)
        for line in fileLines:
            if (line.strip != ""):
                heapUsage = self.parseUsage(line, timeOffset)
                if(line.startswith("[Young]")):
                    self.youngGen.append(heapUsage)
                elif(line.startswith("[Old]")):
                    self.oldGen.append(heapUsage)


    def getUsageAndTime (self, genLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen

        genTime = []
        genUsage = []

        for usage in gen:
            genTime.append(usage.getTime())
            genUsage.append(usage.getBeforeGC())
            genTime.append(usage.getTime())
            genUsage.append(usage.getAfterGC())

        return (genTime, genUsage)


    def getGenBeforeGC(self, genLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen

        genUsage = []
        for usage in gen:
            genUsage.append(usage.getBeforeGC())
        return genUsage

    def getGenAfterGC(self, genLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen

        genUsage = []
        for usage in gen:
            genUsage.append(usage.getAfterGC())
        return genUsage

    def getGenAllocated(self, genLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen

        genTime = []
        genAllocated = []
        for usage in gen:
            if (len(genAllocated) > 0):
                genTime.append(usage.getTime())
                genAllocated.append(genAllocated[len(genAllocated) - 1])
            genTime.append(usage.getTime())
            genAllocated.append(usage.getAllocated())

        return (genTime, genAllocated)

    def getGenTime(self, genLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen

        genTime = []
        for usage in gen:
            genTime.append(usage.getTime())
        return genTime

    def getGCType(self, genLabel, gcLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen

        genTime = []
        genUsage = []
        for usage in gen:
            if (usage.gc == gcLabel):
                genTime.append(usage.getTime())
                genUsage.append(usage.getUsage())
        return (genTime, genUsage)

    def getGCPauseAndTime(self):
        ygcTime = []
        ygcPause = []
        fgcTime = []
        fgcPause = []
        gcTimeSet = set()

        for usage in self.youngGen:
            if (usage.getGCType() == "FGC"):
                fgcTime.append(usage.getTime())
                fgcPause.append(usage.getGCPause())
                gcTimeSet.add(usage.getTime())
            elif (usage.getGCType() == "YGC"):
                ygcTime.append(usage.getTime())
                ygcPause.append(usage.getGCPause())
                gcTimeSet.add(usage.getTime())

        for usage in self.oldGen:
            time = usage.getTime()
            if (time not in gcTimeSet):
                if (usage.getGCType() == "FGC"):
                    fgcTime.append(usage.getTime())
                    fgcPause.append(usage.getGCPause())
                elif (usage.getGCType() == "YGC"):
                    ygcTime.append(usage.getTime())
                    ygcPause.append(usage.getGCPause())
        return (ygcTime, ygcPause, fgcTime, fgcPause)


def plotHeapUsage(heapTimeOffset, cpuTimeOffset, mode, appName, title, gclogFile, topMetricsFile, outputFile):

    heapUsage = HeapUsage()
    heapUsage.initHeapUsage(gclogFile, heapTimeOffset)

    if (topMetricsFile == ""):
        fig, axes = plt.subplots(nrows=2, ncols=1, sharey=False, sharex= True, figsize=(4,3))
    else:
        fig, axes = plt.subplots(nrows=3, ncols=1, sharey=False, sharex= True, figsize=(4,4.4))
    plt.subplots_adjust(wspace=0, hspace=0)


    axes[0].set_ylabel("Old Gen (GB)", color='black')
    axes[1].set_ylabel("GC time (s)", color='black')



    # YoungUsageLine = None
    # if (mode == "="):
    #     axes[0].plot(heapUsage.getGenTime("Young"), heapUsage.getGenBeforeGC("Young"), '--o', linewidth=0.95, label='BeforeGC', markersize=0.9)
    #     axes[0].plot(heapUsage.getGenTime("Young"), heapUsage.getGenAfterGC("Young"), '-*', linewidth=0.95, label='AfterGC', markersize=0.9)
    # elif (mode == "-"):
    #     usage = heapUsage.getUsageAndTime("Young")
    #     YoungUsageLine, = axes[0].plot(usage[0], usage[1], '-', linewidth=0.95, label='usage', markersize=0.9)


    # allocated = heapUsage.getGenAllocated("Young")
    # YoungAllocatedLine, = axes[0].plot(allocated[0], allocated[1], '-', label='Allocated')

    OldUsageLine = None
    if (mode == "="):
        oldTimeList = heapUsage.getGenTime("Old")
        axes[0].plot(oldTimeList, heapUsage.getGenBeforeGC("Old"), '--o', linewidth=1, label='BeforeGC', markersize=1)
        axes[0].plot(oldTimeList, heapUsage.getGenAfterGC("Old"), '-*', linewidth=1, label='AfterGC', markersize=1)
    elif (mode == "-"):
        usage = heapUsage.getUsageAndTime("Old")
        print(usage[0], usage[1])
        OldUsageLine, = axes[0].plot(usage[0], usage[1], '-', linewidth=0.95, label='usage', markersize=0.9)

    allocated = heapUsage.getGenAllocated("Old")

    OldAllocatedLine, = axes[0].plot(allocated[0], allocated[1], '-', label='Allocated')



    axes[0].grid(False)
    axes[1].grid(False)
    axes[0].set_ylim(ymin=0)
    axes[1].set_ylim(ymin=0)
    axes[0].set_xlim(xmin=0)
    axes[1].set_xlim(xmin=0)
    axes[0].get_xaxis().set_visible(False)

    axes[0].legend(#(OldAllocatedLine, OldUsageLine),
        #("Allocated", "Usage"),
        loc='upper left', ncol=3, frameon=False, fontsize=10,
        labelspacing=0.1, markerfirst=False,
        borderaxespad=0.2, columnspacing=1.1, handletextpad=0.3)

    # draw GCPause time bar plot

    (ygcTime, ygcPause, fgcTime, fgcPause) = heapUsage.getGCPauseAndTime()

    # for i in range(1, len(ygcPause)):
    #     ygcPause[i] = ygcPause[i-1] + ygcPause[i]
    #
    # for i in range(1, len(fgcPause)):
    #      fgcPause[i] = fgcPause[i-1] + fgcPause[i]


    colors2 = [u'#DDA0DD', u'#6A5ACD', u'#A9A9A9', u'#ADD8E6', u"#cc3333"]
    #axes3 = axes[1].twinx()


    FGCBar = axes[1].bar(fgcTime, fgcPause, 0.3, color=colors2[4],  edgecolor=colors2[4])



    axes[1].plot(-10000, -10000, '-k^',markersize=5, color=colors2[4], label="FGC Pause")
    YGCBar = axes[1].bar(ygcTime, ygcPause, 0.1, color=colors2[2], label="YGC Pause", edgecolor=colors2[2])
    FGCPoint = axes[1].plot(fgcTime, fgcPause,'k^', color=colors2[4], markersize=5)
    print(fgcTime)
    #axes3.set_ylabel(r"GC pause time (sec)")
    #axes[1].set_xlabel("Time (s)")
    # ymin, ymax = axes[1].get_ylim()
    # axes[1].set_ylim(ymin, ymax * 1.25)

    plt.suptitle(title, y=0.93)


    handles,labels=axes[1].get_legend_handles_labels()
    axes[1].legend(handles[::-1],labels[::-1],loc='upper right', frameon=False, fontsize=10,
                   labelspacing=0.2, markerfirst=False,
                   ncol=1, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)

    axes[0].set_ylim(0, 8)  # The ceil
    #axes[1].set_ylim(0, 6)#9)#4.8)  # The ceil
    maxPause = max(max(fgcPause), max(ygcPause))
    axes[1].set_ylim(0, maxPause * 1.25)

    #### plot the CPU usage
    if (topMetricsFile != ""):
        fileLines = open(topMetricsFile, "r").readlines()
        isExecutorMetric = True
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
            if(isExecutorMetric == True and line.strip() != ""):
                timeStr = line[line.find('[') + 1: line.find(']')]
                cpu = line[line.find('=') + 2: line.find(',')]
                memory = line[line.find('Memory') + 9:]

                if first_time == -1:
                    first_time = datetime.strptime(timeStr, '%H:%M:%S')
                    time = first_time - first_time
                else:
                    cur_time = datetime.strptime(timeStr, '%H:%M:%S')
                    time = cur_time - first_time
                    if (time.seconds < 0):
                        print("Time span" + time.seconds)

                # executorTime.append(datetime.strptime(time, '%H:%M:%S'))
                executorTime.append(time.seconds - cpuTimeOffset)
                executorCPU.append(float(cpu))
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
                slaveTime.append(time.seconds - cpuTimeOffset)
                slaveCPU.append(float(cpu))
                slaveMemory.append(float(memory))


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
        axes[2].set_ylabel("CPU utilization (%)", color='k')
        axes[2].tick_params('y', colors='k')
        #axes[1].set_ylabel("Worker CPU (%)", color='r')
        #axes[1].tick_params('y', colors='r')
        axes[2].set_ylim(0, 800)  # The ceil
        #axes[2].set_xlim(0, 400)
        #axes[1].set_ylim(0, 105)  # The ceil
        #axes[2].set_xlabel("Time (s)", color=u'#000000')
        #plt.xlim(0, executorTime.max)  # The ceil

        #fig.autofmt_xdate()
        #axes.hlines(100, 0, 400, colors = "black", linestyles = "dashed", linewidth=1)
        #axes.hlines(200, 0, 400, colors = "black", linestyles = ":", linewidth=1)
        #axes[2].vlines(max_x, 0, 800, colors = "grey", linestyles = "--", linewidth=1)


        print(executorTime)

        axes[2].grid(True, axis='y', color='lightgray')

        ax12 = axes[2].twinx()
        ax12.plot(executorTime, executorMemory, '--b')
        ax12.set_ylabel('Memory usage (GB)', color='b')


        axes[2].plot(executorTime, executorCPU, '-r', label='CPU Usage', linewidth=0.9)
        axes[2].plot(np.nan, '--b', label='Memory Usage')  # Make an agent in ax
        axes[2].legend(markerfirst=False,frameon=False, labelspacing=0.2,
                       ncol=1, borderaxespad=1.4, columnspacing=1.2, handletextpad=0.5, loc="best")
        ax12.tick_params('y', colors='b')
        ax12.set_ylim(0, 8)  # The ceil
        print("avgCPU = " + str(sum(executorCPU) / float(len(executorCPU))))
        print("maxMemory = " + str(max(executorMemory)))

#plt.show()
    fig = plt.gcf()
    #plt.show()
    fig.savefig(outputFile, dpi=300, bbox_inches='tight')




if __name__ == '__main__':

    mode = "="

    #gcViewerParsedLogDir = "D:/plot/"
    gcViewerParsedLogDir = "/Users/xulijie/Documents/GCResearch/Experiments-2018/profiles/"

    appName = "AggregateByKey-1.0"
    #inputFile = gcViewerParsedLogDir + appName + "/SlowestExecutors/"
    inputFile = gcViewerParsedLogDir + appName + "/SelectedExecutors/"

    for file in os.listdir(inputFile):
        if file.startswith("Parallel"):
            for executor in os.listdir(os.path.join(inputFile, file)):
                if executor.startswith("E"):
                    plotHeapUsage(1142, 1147, mode, appName, "(a) GroupBy-1.0-Slowest-Parallel-Task",
                                  os.path.join(inputFile, file, executor, executor + "-parsed.txt"),
                                  os.path.join(inputFile, file, executor, "topMetrics.txt"),
                                  os.path.join(inputFile, file, executor, executor + ".pdf"))

