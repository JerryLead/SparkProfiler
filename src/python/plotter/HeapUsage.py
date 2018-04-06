import matplotlib.pyplot as plt
from matplotlib import gridspec
import matplotlib.dates as mdates
import matplotlib as mpl
import os, sys

from datetime import datetime
from reader import FileReader


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




class HeapUsage:
    def __init__(self):
        self.youngGen = []
        self.oldGen = []

    def parseUsage(self, line):
        gcType = line[line.find('(') + 1: line.find(')')]
        commaIndex = line.find(',')
        time = float(line[line.find('time') + 7: commaIndex])
        commaIndex = line.find(',', commaIndex + 1)
        beforeGC = float(line[line.find('beforeGC') + 11: commaIndex])
        commaIndex = line.find(',', commaIndex + 1)
        afterGC = float(line[line.find('afterGC') + 10: commaIndex])
        commaIndex = line.find(',', commaIndex + 1)
        allocated = float(line[line.find('allocated') + 12: commaIndex])
        commaIndex = line.find(',', commaIndex + 1)
        gcPause = float(line[line.find('gcPause') + 10: commaIndex - 1])
        gcCause = line[line.find('gcCause') + 10: ]

        return Usage(gcType, time, beforeGC, afterGC, allocated, gcPause, gcCause)

    def initHeapUsage(self, gclogFile):
        fileLines = FileReader.readLines(gclogFile)
        for line in fileLines:
            if (line.strip != ""):
                heapUsage = self.parseUsage(line)
                if(line.startswith("[Young]")):
                    self.youngGen.append(heapUsage)
                elif(line.startswith("[Old]")):
                    self.oldGen.append(heapUsage)


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


def plotHeapUsage(appName, gclogFile, outputFile):

    heapUsage = HeapUsage()
    heapUsage.initHeapUsage(gclogFile)

    fig, axes = plt.subplots(nrows=2, ncols=1, sharey=False, sharex= True) #, figsize=(8,7.6))

    gs = gridspec.GridSpec(3, 1)
    axes[0] = plt.subplot(gs[0, :])
    # identical to ax1 = plt.subplot(gs.new_subplotspec((0,0), colspan=3))
    axes[1] = plt.subplot(gs[1:, :])

    axes[0].set_ylabel("Young Gen (MB)")
    axes[1].set_ylabel("Old Gen (MB)")

    axes[0].set_ylim(0, 3000)  # The ceil
    axes[1].set_ylim(0, 7000)  # The ceil
    # axes[0].set_xlim(0, 5000)

    #colors = [u'#1f77b4', u'#ff7f0e', u'#2ca02c', u'#d62728', u'#9467bd', u'#8c564b', u'#e377c2', u'#7f7f7f', u'#bcbd22', u'#17becf']

    #colors = [u'#009999', u'#cc0033', u'#663366']

    axes[0].plot(heapUsage.getGenTime("Young"), heapUsage.getGenBeforeGC("Young"), '--o', linewidth=0.9, label='BeforeGC', markersize=0.9)
    axes[0].plot(heapUsage.getGenTime("Young"), heapUsage.getGenAfterGC("Young"), '-*', linewidth=0.9, label='AfterGC', markersize=0.9)
    allocated = heapUsage.getGenAllocated("Young")
    axes[0].plot(allocated[0], allocated[1], '-', label='Allocated')

    axes[1].plot(heapUsage.getGenTime("Old"), heapUsage.getGenBeforeGC("Old"), '--o', linewidth=0.9, label='BeforeGC', markersize=0.9)
    axes[1].plot(heapUsage.getGenTime("Old"), heapUsage.getGenAfterGC("Old"), '-*', linewidth=0.9, label='AfterGC', markersize=0.9)
    allocated = heapUsage.getGenAllocated("Old")
    axes[1].plot(allocated[0], allocated[1], '-', label='Allocated')


    axes[0].grid(False)
    axes[1].grid(False)
    axes[0].set_ylim(ymin=0)
    axes[1].set_ylim(ymin=0)
    axes[0].set_xlim(xmin=0)
    axes[1].set_xlim(xmin=0)
    axes[0].get_xaxis().set_visible(False)

    axes[0].legend(loc='upper left')

    # draw GCPause time bar plot

    (ygcTime, ygcPause, fgcTime, fgcPause) = heapUsage.getGCPauseAndTime()

    colors2 = [u'#DDA0DD', u'#6A5ACD', u'#A9A9A9', u'#ADD8E6', u"#cc3333"]
    axes3 = axes[1].twinx()

    bar1 = axes3.bar(ygcTime, ygcPause, 0.01, color=colors2[2], label="YGC Pause", edgecolor=colors2[2])
    bar2 = axes3.bar(fgcTime, fgcPause, 0.005, color=colors2[4], label="FGC Pause", edgecolor=colors2[4])

    axes3.set_ylabel(r"GC pause time (sec)")
    axes[1].set_xlabel("Time (sec)")
    ymin, ymax = axes3.get_ylim()
    axes3.set_ylim(ymin, ymax * 2)

    plt.suptitle(appName)
    #axes[1].legend(loc='upper left')
    axes3.legend(loc='upper left')

    fig = plt.gcf()
    #plt.show()
    fig.savefig(outputFile, dpi=300, bbox_inches='tight')




if __name__ == '__main__':

    gcViewerParsedLogDir = "/Users/xulijie/Documents/GCResearch/PaperExperiments/medianProfiles/"
    appName = "GroupByRDD-0.5"
    inputFile = gcViewerParsedLogDir + appName + "/SlowestTask/"
    parallelExecutorID = 30
    cmsExecutorID = 17
    g1ExecutorID = 16
    plotHeapUsage(appName, inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + "-parsed.txt", inputFile + "Parallel/parallel-E" + str(parallelExecutorID) + ".pdf")
    plotHeapUsage(appName, inputFile + "CMS/CMS-E" + str(cmsExecutorID) + "-parsed.txt", inputFile + "CMS/CMS-E" + str(cmsExecutorID) + ".pdf")
    plotHeapUsage(appName, inputFile + "G1/G1-E" + str(g1ExecutorID) + "-parsed.txt", inputFile + "G1/G1-E" + str(g1ExecutorID) + ".pdf")



