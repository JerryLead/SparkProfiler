import matplotlib.pyplot as plt
from matplotlib import gridspec
import matplotlib.dates as mdates
import matplotlib as mpl
import os, sys
import numpy as np
from datetime import datetime
from reader import FileReader

mpl.rcParams['axes.linewidth'] = 1.2 #set the value globally

#plt.rc('font', family='Helvetica')
# font = {'family' : 'Helvetica',
#         'weight' : 'normal',
#         'color'  : 'black',
#         'size'   : '12'}


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
        mark=[]
        gcTimeSet = set()

        for usage in self.youngGen:
            if (usage.getGCType() == "FGC"):
                if usage.getGCCause().find("mixed")>0:
                    mark.append(1)
                else:
                    mark.append(0)
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
                    if usage.getGCCause().find("mixed")>0:
                        mark.append(1)
                    else:
                        mark.append(0)
                    fgcTime.append(usage.getTime())
                    fgcPause.append(usage.getGCPause())
                elif (usage.getGCType() == "YGC"):
                    ygcTime.append(usage.getTime())
                    ygcPause.append(usage.getGCPause())
        return (ygcTime, ygcPause, fgcTime, fgcPause,mark)


def plotHeapUsage(timeOffset, mode, appName, title, gclogFile, outputFile):

    heapUsage = HeapUsage()
    heapUsage.initHeapUsage(gclogFile, timeOffset)

    fig, axes = plt.subplots(nrows=2, ncols=1, sharey=False, sharex= True, figsize=(4,3))

    plt.subplots_adjust(left=0.13, bottom=0.11, right=0.98, top=0.88,
                        wspace=0.00, hspace=0.00)
    #gs = gridspec.GridSpec(2, 1)
    #gs.update(wspace=0, hspace=0.05)
    #axes[0] = plt.subplot(gs[0, :])
    # identical to ax1 = plt.subplot(gs.new_subplotspec((0,0), colspan=3))
    #axes[1] = plt.subplot(gs[1, :])



    axes[0].set_ylabel("Old Gen (GB)", color='black')
    axes[1].set_ylabel("GC time (s)")

    axes[0].set_ylim(0, 8)  # The ceil
    axes[1].set_ylim(0, 24)  # The ceil



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
        axes[0].plot(heapUsage.getGenTime("Old"), heapUsage.getGenBeforeGC("Old"), '--o', linewidth=1, label='BeforeGC', markersize=1)
        axes[0].plot(heapUsage.getGenTime("Old"), heapUsage.getGenAfterGC("Old"), '-*', linewidth=1, label='AfterGC', markersize=1)
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
        loc='upper center', ncol=3, frameon=False, fontsize=10, markerfirst=False,
        #labelspacing=0.2,
        borderaxespad=0.3,
        columnspacing=1.2, handletextpad=0.5)

    # draw GCPause time bar plot

    (ygcTime, ygcPause, fgcTime, fgcPause,fgcmark) = heapUsage.getGCPauseAndTime()
    t=0.0
    for i in fgcTime:
        if t<i:
            t=i
        else:
            print("error")
            print(t)
            print(i)
    fgc_x_black_new=[]
    fgc_y_black_new=[]
    fgc_x_ori_new=[]
    fgc_y_ori_new=[]
    CMS_time_list=[58.733, 60.513, 63.202, 67.24, 81.626, 88.928, 99.276, 132.802, 153.148, 223.424, 254.079, 324.382, 348.93, 365.899, 429.918, 452.708, 482.405, 535.731, 559.471, 577.353, 641.72, 667.49, 688.424, 747.857, 773.719, 794.172, 853.032, 878.515, 901.698, 959.179, 984.668, 1007.119, 1063.934, 1094.58, 1116.677]

    CMS_value_list=[0.058, 0.379, 0.427, 0.89, 2.655, 1.053, 1.143, 2.724, 3.894, 7.349, 4.094, 8.143, 3.531, 3.017, 8.116, 3.762, 3.055, 7.853, 3.286, 3.047, 8.249, 3.553, 3.126, 7.955, 3.585, 3.137, 9.06, 3.725, 3.298, 9.139, 3.72, 3.307, 8.162, 3.576, 3.67]

    CMS_time_list=map(lambda x:x-CMSTimeOffset,CMS_time_list)
    tes=2
    if title.find("CMS")>0:
        plt.subplots_adjust(right=0.88)
        axes3 = axes[1].twinx()
        axes3.set_ylabel("Concurrent time (s)")
        axes[1].set_ylim(0,0.7)
        axes3.set_ylim(0, 30)
        axes[1].set_ylim(0, 0.85)
        axes[1].plot(-1000,-1000, 'bo',markersize=4,label='Concurrent sweep')
        for i in np.arange(len(CMS_time_list)):
            axes3.plot(CMS_time_list[i]-CMS_value_list[i]/2,CMS_value_list[i], 'bo',markersize=CMS_value_list[i]/tes)
    elif title.find("G1")>0:
        axes[1].set_ylim(0,3.9)
        for i in np.arange(len(fgcTime)):
            if fgcmark[i]>0:
                fgc_x_black_new.append(fgcTime[i])
                fgc_y_black_new.append(fgcPause[i])
            else:
                fgc_x_ori_new.append(fgcTime[i])
                fgc_y_ori_new.append(fgcPause[i])
        fgcTime=fgc_x_ori_new
        fgcPause=fgc_y_ori_new
    # for i in range(1, len(ygcPause)):
    #     ygcPause[i] = ygcPause[i-1] + ygcPause[i]
    #
    # for i in range(1, len(fgcPause)):
    #      fgcPause[i] = fgcPause[i-1] + fgcPause[i]


    colors2 = [u'#DDA0DD', u'#6A5ACD', u'#A9A9A9', u'#ADD8E6', u"#cc3333"]
    #axes3 = axes[1].twinx()

    #YGCBar = axes[1].bar(ygcTime, ygcPause, 0.01, color=colors2[2], label="YGC Pause", edgecolor=colors2[2])
    FGCBar = axes[1].bar(fgcTime, fgcPause, 0.01, color=colors2[4], label="FGC Pause", edgecolor=colors2[4])
    if title.find("G1")>0:
        axes[1].bar(fgc_x_black_new, fgc_y_black_new, 0.01, color="k", edgecolor="k")
        axes[1].plot(fgc_x_black_new, fgc_y_black_new, 'k^',markersize=4)
        axes[1].plot(-10000, -10000, '-k^',markersize=4,label="FGC Pause(mixed sweep)")


    #axes3.set_ylabel(r"GC pause time (sec)")
    #axes[1].set_xlabel("Time (s)")
    # ymin, ymax = axes[1].get_ylim()
    # axes[1].set_ylim(ymin, ymax * 1.25)

    plt.suptitle(title, y=0.95)
    if title.find("CMS")>1 or title.find("G1")>1:
        handles,labels=axes[1].get_legend_handles_labels()
        axes[1].legend(handles[::-1],labels[::-1],loc='upper right', frameon=False, fontsize=10,
                       labelspacing=0.2, markerfirst=False,
                       ncol=1, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)
    else:
        axes[1].legend(loc='upper right', frameon=False, fontsize=10,
                       labelspacing=0.2, markerfirst=False,
                       ncol=1, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)


    plt.show()
    #fig = plt.gcf()
    #plt.show()
    #fig.savefig(outputFile, dpi=300, bbox_inches='tight')




if __name__ == '__main__':

    mode = "="

    gcViewerParsedLogDir = "D:/plot/"

    # appName = "GroupByRDD-0.5"
    # inputFile = gcViewerParsedLogDir + appName + "/SlowestTask/"
    # parallelExecutorID = 29
    # cmsExecutorID = 21
    # g1ExecutorID = 15 #28 #18 #16
    # parallelTimeOffset = 46#33
    # CMSTimeOffset = 36
    # G1TimeOffset = 36
    # plotHeapUsage(parallelTimeOffset, mode, appName, "(a) GroupBy-0.5-Slowest-Parallel-task", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + "-parsed.txt", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + ".pdf")
    #plotHeapUsage(CMSTimeOffset, mode, appName, "(b) GroupBy-0.5-Slowest-CMS-task",inputFile + "CMS/CMS-E" + str(cmsExecutorID) + "-parsed.txt", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + ".pdf")
    #plotHeapUsage(G1TimeOffset, mode, appName, "(c) GroupBy-0.5-Slowest-G1-task",inputFile + "G1/G1-E" + str(g1ExecutorID) + "-parsed.txt", inputFile + "G1/G1-E" + str(g1ExecutorID) + ".pdf")


    # appName = "RDDJoin-1.0"
    # inputFile = gcViewerParsedLogDir + appName + "/SlowestTask/"
    # parallelExecutorID = 12
    # cmsExecutorID = 25
    # g1ExecutorID = 13
    # parallelTimeOffset = 89
    # CMSTimeOffset = 88
    # G1TimeOffset = 83
    # plotHeapUsage(parallelTimeOffset, mode, appName, "(a) Join-1.0-Slowest-Parallel-task", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + "-parsed.txt", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + ".pdf")
    # #plotHeapUsage(CMSTimeOffset, mode, appName, "(b) Join-1.0-Slowest-CMS-task", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + "-parsed.txt", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + ".pdf")
    #plotHeapUsage(G1TimeOffset, mode, appName, "(c) Join-1.0-Slowest-G1-task", inputFile + "G1/G1-E" + str(g1ExecutorID) + "-parsed.txt", inputFile + "G1/G1-E" + str(g1ExecutorID) + ".pdf")

    # appName = "SVM-1.0"
    # inputFile = gcViewerParsedLogDir + appName + "/SlowestTask/"
    # parallelExecutorID = 31
    # cmsExecutorID = 3
    # g1ExecutorID = 18
    # plotHeapUsage(mode, appName, "(a) SVM-1.0-Parallel-task", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + "-parsed.txt", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + ".pdf")
    # plotHeapUsage(mode, appName, "(b) SVM-1.0-CMS-task", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + "-parsed.txt", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + ".pdf")
    # plotHeapUsage(mode, appName, "(c) SVM-1.0-G1-task", inputFile + "G1/G1-E" + str(g1ExecutorID) + "-parsed.txt", inputFile + "G1/G1-E" + str(g1ExecutorID) + ".pdf")

    appName = "PageRank-0.5"
    inputFile = gcViewerParsedLogDir + appName + "/SlowestTask/"
    parallelExecutorID = 4
    cmsExecutorID = 14
    g1ExecutorID = 28
    parallelTimeOffset = 53
    CMSTimeOffset = 54
    G1TimeOffset = 57
    #plotHeapUsage(parallelTimeOffset, mode, appName, "(a) PageRank-0.5-Parallel-task", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + "-parsed.txt", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + ".pdf")
    #plotHeapUsage(CMSTimeOffset, mode, appName, "(a) PageRank-0.5-CMS-task", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + "-parsed.txt", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + ".pdf")
    plotHeapUsage(G1TimeOffset, mode, appName, "(a) PageRank-0.5-G1-task", inputFile + "G1/G1-E" + str(g1ExecutorID) + "-parsed.txt", inputFile + "G1/G1-E" + str(g1ExecutorID) + ".pdf")

