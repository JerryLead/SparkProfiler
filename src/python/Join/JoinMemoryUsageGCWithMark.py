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


def plotHeapUsage(timeOffset, mode, appName, title, gclogFile, outputFile):

    heapUsage = HeapUsage()
    heapUsage.initHeapUsage(gclogFile, timeOffset)

    fig, axes = plt.subplots(nrows=2, ncols=1, sharey=False, sharex= True, figsize=(4.3,3))

    plt.subplots_adjust(left=0.15, bottom=0.11, right=0.89, top=0.88,
                        wspace=0.00, hspace=0.00)
    #gs = gridspec.GridSpec(2, 1)
    #gs.update(wspace=0, hspace=0.05)
    #axes[0] = plt.subplot(gs[0, :])
    # identical to ax1 = plt.subplot(gs.new_subplotspec((0,0), colspan=3))
    #axes[1] = plt.subplot(gs[1, :])



    axes[0].set_ylabel("Old Gen (GB)", color='black')
    axes[1].set_ylabel("GC time (s)")

    axes[0].set_ylim(0, 8)  # The ceil
    axes[1].set_ylim(0, 47)  # The ceil

    CMS_time_list=[92.727, 95.529, 100.894, 110.112, 127.12, 158.669]
    CMS_value_list=[0.191, 0.997, 1.978, 3.299, 7.691, 10.431]
    G1_time_list=[89.753, 91.738, 179.918, 206.114, 224.956, 251.203, 272.556, 287.161, 306.941, 325.256, 339.888, 360.144, 383.191, 403.052, 422.747, 434.203, 457.446, 482.913, 507.21, 535.425, 556.187, 570.755, 589.022, 611.488, 632.832, 658.589]
    G1_value_list=[1.0148105, 0.8292979, 23.9895646, 19.081214, 9.1167319, 7.9938505, 8.5378638, 9.1016146, 7.5888552, 8.0168106, 7.9599445, 9.7424713, 7.6491988, 10.0123086, 8.4451482, 7.8433571, 7.3207595, 7.4504276, 7.8631376, 7.6595128, 8.7071489, 8.076867, 7.879371, 8.5952966, 7.884515, 8.155551]
    CMS_time_list=map(lambda x:x-CMSTimeOffset,CMS_time_list)
    G1_time_list=map(lambda x:x-G1TimeOffset,G1_time_list)
    tes=2
    axes3 = axes[1].twinx()
    axes3.set_ylabel("Concurrent GC time (s)", color='blue')

    #axes[1].plot(G1_time_list,G1_value_list, 'ro')

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
        OldUsageLine, = axes[0].plot(usage[0], usage[1], '-', linewidth=1, label='usage', markersize=0.9)

    allocated = heapUsage.getGenAllocated("Old")
    OldAllocatedLine, = axes[0].plot(allocated[0], allocated[1], '-', linewidth=2, label='Allocated')

    if title.find("CMS")>0:
        axes[1].set_xlim(0,400)
        axes3.set_xlim(0,400)
        axes3.set_ylim(0, 30)
        axes[1].set_ylim(0, 0.85)
        for i in np.arange(len(CMS_time_list)):
            axes3.plot(CMS_time_list[i]-CMS_value_list[i]/2,CMS_value_list[i], 'bo',markersize=CMS_value_list[i]/tes)
    elif title.find("G1")>0:
        axes[1].set_xlim(0,600)
        axes3.set_xlim(0,600)
        axes3.set_ylim(0, 30)
        axes[1].set_ylim(0, 0.85)
        for i in np.arange(len(G1_time_list)):
            axes3.plot(G1_time_list[i]-(G1_value_list[i])/2,G1_value_list[i], 'bo',markersize=G1_value_list[i]/tes)


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

    (ygcTime, ygcPause, fgcTime, fgcPause) = heapUsage.getGCPauseAndTime()

    # for i in range(1, len(ygcPause)):
    #     ygcPause[i] = ygcPause[i-1] + ygcPause[i]
    #
    # for i in range(1, len(fgcPause)):
    #      fgcPause[i] = fgcPause[i-1] + fgcPause[i]


    colors2 = [u'#DDA0DD', u'#6A5ACD', u'#A9A9A9', u'#ADD8E6', u"#cc3333"]
    #axes3 = axes[1].twinx()

    #YGCBar = axes[1].bar(ygcTime, ygcPause, 0.01, color=colors2[2], label="YGC Pause", edgecolor=colors2[2])
    FGCBar = axes[1].bar(fgcTime, fgcPause, 0.1, color=colors2[4], label="FGC Pause", edgecolor=colors2[4])
    if title.find("CMS")>0 or title.find("G1")>0:
        axes[1].plot(-1000,-1000, 'bo',markersize=4,label='Concurrent mark phase')
    #axes3.set_ylabel(r"GC pause time (sec)")
    #axes[1].set_xlabel("Time (s)")
    # ymin, ymax = axes[1].get_ylim()
    # axes[1].set_ylim(ymin, ymax * 1.25)

    plt.suptitle(title, y=0.95)
    handles,labels=axes[1].get_legend_handles_labels()
    axes[1].legend(handles[::-1],labels[::-1],loc='upper right', frameon=False, fontsize=10,
                   labelspacing=0.2, markerfirst=False,
                   ncol=1, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)


    plt.show()
    #fig = plt.gcf()
    #plt.show()
    #fig.savefig(outputFile, dpi=300, bbox_inches='tight')




if __name__ == '__main__':

    mode = "="

    gcViewerParsedLogDir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/medianProfiles/"
    # gcViewerParsedLogDir = "D:/plot/"
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


    appName = "RDDJoin-1.0"
    inputFile = gcViewerParsedLogDir + appName + "/SlowestTask/"
    parallelExecutorID = 12
    cmsExecutorID = 25
    g1ExecutorID = 13
    parallelTimeOffset = 89
    CMSTimeOffset = 88
    G1TimeOffset = 83
    #plotHeapUsage(parallelTimeOffset, mode, appName, "(a) Join-1.0-Slowest-Parallel-task", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + "-parsed.txt", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + ".pdf")
    #plotHeapUsage(CMSTimeOffset, mode, appName, "(b) Join-1.0-Slowest-CMS-task", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + "-parsed.txt", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + ".pdf")
    plotHeapUsage(G1TimeOffset, mode, appName, "(c) Join-1.0-Slowest-G1-task", inputFile + "G1/G1-E" + str(g1ExecutorID) + "-parsed.txt", inputFile + "G1/G1-E" + str(g1ExecutorID) + ".pdf")

    # appName = "SVM-1.0"
    # inputFile = gcViewerParsedLogDir + appName + "/SlowestTask/"
    # parallelExecutorID = 31
    # cmsExecutorID = 3
    # g1ExecutorID = 18
    # plotHeapUsage(mode, appName, "(a) SVM-1.0-Parallel-task", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + "-parsed.txt", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + ".pdf")
    # plotHeapUsage(mode, appName, "(b) SVM-1.0-CMS-task", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + "-parsed.txt", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + ".pdf")
    # plotHeapUsage(mode, appName, "(c) SVM-1.0-G1-task", inputFile + "G1/G1-E" + str(g1ExecutorID) + "-parsed.txt", inputFile + "G1/G1-E" + str(g1ExecutorID) + ".pdf")

    # appName = "PageRank-0.5"
    # inputFile = gcViewerParsedLogDir + appName + "/SlowestTask/"
    # parallelExecutorID = 1
    # cmsExecutorID = 14
    # g1ExecutorID = 28
    # plotHeapUsage(mode, appName, "(a) PageRank-0.5-Parallel-task", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + "-parsed.txt", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + ".pdf")
    # plotHeapUsage(mode, appName, "(a) PageRank-0.5-CMS-task", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + "-parsed.txt", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + ".pdf")
    # plotHeapUsage(mode, appName, "(a) PageRank-0.5-G1-task", inputFile + "G1/G1-E" + str(g1ExecutorID) + "-parsed.txt", inputFile + "G1/G1-E" + str(g1ExecutorID) + ".pdf")

