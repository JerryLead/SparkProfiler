import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import matplotlib as mpl
import os, sys

from datetime import datetime
from reader import FileReader

class Usage:
    def __init__(self, time, usage, allocated, gc):
        self.time = time
        self.usage = usage
        self.allocated = allocated
        self.gc = gc

    def getTime(self):
        return self.time

    def getUsage(self):
        return self.usage

    def getAllocated(self):
        return self.allocated

    def getGC(self):
        return self.gc

class HeapUsage:
    def __init__(self):
        self.youngGen = []
        self.oldGen = []
        self.metaGen = []


    def parseUsage(self, line):
        time = float(line[line.find('time') + 7: line.find(',')])
        usage = float(line[line.find('usage') + 8: line.rfind(',')])
        allocated = float(line[line.find('allocated') + 12: ])
        gc = line[line.find('(') + 1: line.find(')')]

        return Usage(time, usage, allocated, gc)

    def initHeapUsage(self, gclogFile):
        fileLines = FileReader.readLines(gclogFile)
        for line in fileLines:
            if (line.strip != ""):
                heapUsage = self.parseUsage(line)
                if(line.startswith("[Young]")):
                    self.youngGen.append(heapUsage)
                elif(line.startswith("[Old]")):
                    self.oldGen.append(heapUsage)
                elif(line.startswith("[Metaspace]")):
                    self.metaGen.append(heapUsage)

    def getGenUsage(self, genLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen
        elif (genLabel == "Metaspace"):
            gen = self.metaGen

        genUsage = []
        for usage in gen:
            genUsage.append(usage.getUsage())
        return genUsage

    def getGenAllocated(self, genLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen
        elif (genLabel == "Metaspace"):
            gen = self.metaGen

        genAllocated = []
        for usage in gen:
            genAllocated.append(usage.getAllocated())
        return genAllocated

    def getGenTime(self, genLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen
        elif (genLabel == "Metaspace"):
            gen = self.metaGen

        genTime = []
        for usage in gen:
            genTime.append(usage.getTime())
        return genTime

    def getGC(self, genLabel, gcLabel):
        if (genLabel == "Young"):
            gen = self.youngGen
        elif (genLabel == "Old"):
            gen = self.oldGen
        elif (genLabel == "Metaspace"):
            gen = self.metaGen

        genTime = []
        genUsage = []
        for usage in gen:
            if (usage.gc == gcLabel):
                genTime.append(usage.getTime())
                genUsage.append(usage.getUsage())
        return (genTime, genUsage)


def plotHeapUsage(appName, gclogFile):

    heapUsage = HeapUsage()
    heapUsage.initHeapUsage(gclogFile)

    fig, axes = plt.subplots(nrows=2, ncols=1, sharey=False, sharex= True)

    axes[0].set_ylabel("Young Gen (MB)")
    axes[1].set_ylabel("Old Gen (MB)")

    axes[0].set_ylim(0, 1500)  # The ceil
    axes[1].set_ylim(0, 5000)  # The ceil
    axes[0].set_xlim(0, 5000)


    axes[0].plot(heapUsage.getGenTime("Young"), heapUsage.getGenUsage("Young"), '-', label='Usage', linewidth=0.5, markersize=0.8)
    axes[0].plot(heapUsage.getGenTime("Young"), heapUsage.getGenAllocated("Young"), '-', label='Allocated')
    (YGCTime, YGCUsage) = heapUsage.getGC("Young", "YGC")
    (FGCTime, FGCUsage) = heapUsage.getGC("Young", "FGC")
    axes[0].plot(YGCTime, YGCUsage, 'o', markersize=0.8, label='YGC')
    axes[0].plot(FGCTime, FGCUsage, 'x', markersize=0.8, label='FGC')

    axes[1].plot(heapUsage.getGenTime("Old"), heapUsage.getGenUsage("Old"), '-', label='Usage', linewidth=0.8, markersize=1)
    axes[1].plot(heapUsage.getGenTime("Old"), heapUsage.getGenAllocated("Old"), '-', label='Allocated')
    (YGCTime, YGCUsage) = heapUsage.getGC("Old", "YGC")
    (FGCTime, FGCUsage) = heapUsage.getGC("Old", "FGC")
    axes[1].plot(YGCTime, YGCUsage, 'o', markersize=0.8, label='YGC')
    axes[1].plot(FGCTime, FGCUsage, 'x', markersize=0.8, label='FGC')

    axes[0].grid(True)
    axes[1].grid(True)
    plt.suptitle(appName)
    plt.legend(loc='lower right')

    plt.show()

    # outputDir = os.path.join(slowestTasksDir, "topMetricsFigures")
    # if not os.path.exists(outputDir):
    #     os.mkdir(outputDir)
    #     file = os.path.join(outputDir, appName + ".pdf")
    #     plt.savefig(file, dpi=150, bbox_inches='tight')




if __name__ == '__main__':

    #dir = "/Users/xulijie/Documents/GCResearch/Experiments-11-17/Abnormal/Join-1.0-E1/gclogs"
    dir = "/Users/xulijie/dev/IdeaProjects/SparkProfiler/src/test/gclogs/"
    #fileName = "Join-1.0-E1-P-12-23.txt"

    fileName = "ParsedParallelLog.txt"
    appName = "Join-1.0-E1-Parallel"
    plotHeapUsage(appName, dir + fileName)

