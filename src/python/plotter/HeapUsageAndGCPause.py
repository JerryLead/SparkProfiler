# coding=utf-8
import matplotlib.pyplot as plt
from matplotlib import gridspec
import re
import numpy as np
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

class GCPause:

    def __init__(self, time, used,total, pause, gc):
        self.time = time
        self.used = used
        self.total = total
        self.pause = pause
        self.gc = gc


class HeapUsageAndGCPause:
    def __init__(self):
        self.youngGen = []
        self.oldGen = []
        self.metaGen = []

        self.gcTotal = {}
        self.gcTimeLine = {}


    def parseUsage(self, line):
        time = float(line[line.find('time') + 7: line.find(',')])
        usage = float(line[line.find('usage') + 8: line.rfind(',')])
        allocated = float(line[line.find('allocated') + 12: ])
        gc = line[line.find('(') + 1: line.find(')')]

        return Usage(time, usage, allocated, gc)

    def initHeapUsageAndGCPause(self, gclogFile, gcpauseFile):
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

        fileLines2 = FileReader.readLines(gcpauseFile)
        for line in fileLines2:
            if not line[0].isdigit():
                continue
            items = re.split(",", line)
            gc_type = items[-1]
            if gc_type == "NONE":
                continue
            if gc_type not in self.gcTotal.keys():
                self.gcTotal[gc_type] = []
            self.gcTotal[gc_type].append(float(items[3]))
            if gc_type not in self.gcTimeLine.keys():
                self.gcTimeLine[gc_type] = []
            self.gcTimeLine[gc_type].append(float(items[0]))

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

    def getGCPauseTime(self):
        return self.gcTotal
    def getGCTimeline(self):
        return self.gcTimeLine


def plotHeapUsage(appName, gclogFile, gcpauseFile,outputFile):

    heapUsageAndGCPause = HeapUsageAndGCPause()
    heapUsageAndGCPause.initHeapUsageAndGCPause(gclogFile, gcpauseFile)

    fig, axes = plt.subplots(nrows=2, ncols=1, sharey=False, sharex= True) #, figsize=(8,7.6))

    gs = gridspec.GridSpec(3, 1)
    axes[0] = plt.subplot(gs[0, :])
    # identical to ax1 = plt.subplot(gs.new_subplotspec((0,0), colspan=3))
    axes[1] = plt.subplot(gs[1:,:])

    axes[0].set_ylabel("Young Gen (MB)")
    axes[1].set_ylabel("Old Gen (MB)")

    # axes[0].set_ylim(0, 1500)  # The ceil
    # axes[1].set_ylim(0, 5000)  # The ceil
    # axes[0].set_xlim(0, 5000)

    colors = [u'#1f77b4', u'#ff7f0e', u'#2ca02c', u'#d62728', u'#9467bd', u'#8c564b', u'#e377c2', u'#7f7f7f', u'#bcbd22', u'#17becf']

    axes[0].plot(heapUsageAndGCPause.getGenTime("Young"), heapUsageAndGCPause.getGenUsage("Young"), '-', label='Used', linewidth=0.1, markersize=0.8, color=colors[0])
    axes[0].plot(heapUsageAndGCPause.getGenTime("Young"), heapUsageAndGCPause.getGenAllocated("Young"), '-', label='Allocated', color=colors[1])
    (YGCTime, YGCUsage) = heapUsageAndGCPause.getGC("Young", "YGC")
    (FGCTime, FGCUsage) = heapUsageAndGCPause.getGC("Young", "FGC")
    axes[0].plot(YGCTime, YGCUsage, 'o', markersize=0.8, label='YGC', color=colors[0])
    axes[0].plot(FGCTime, FGCUsage, '*', markersize=0.8, label='FGC', color=colors[3])

    line1, = axes[1].plot(heapUsageAndGCPause.getGenTime("Old"), heapUsageAndGCPause.getGenUsage("Old"), '-', label='Usage', linewidth=0.8, markersize=1, color=colors[0])
    line2, = axes[1].plot(heapUsageAndGCPause.getGenTime("Old"), heapUsageAndGCPause.getGenAllocated("Old"), '-', label='Allocated', color=colors[1])
    (YGCTime, YGCUsage) = heapUsageAndGCPause.getGC("Old", "YGC")
    (FGCTime, FGCUsage) = heapUsageAndGCPause.getGC("Old", "FGC")
    line3, = axes[1].plot(YGCTime, YGCUsage, 'o', markersize=0.8, label='YGC', color=colors[0])
    line4, = axes[1].plot(FGCTime, FGCUsage, '*', markersize=2, label='FGC', color=colors[3])
    axes[0].grid(False)
    axes[1].grid(False)
    axes[0].set_ylim(ymin=0)
    axes[1].set_ylim(ymin=0)
    axes[0].set_xlim(xmin=0)
    axes[1].set_xlim(xmin=0)
    axes[0].get_xaxis().set_visible(False)
    plt.suptitle(appName)
    # plt.legend(loc='center right')
    # draw GCPause time bar plot
    # y label
    gcTotal = heapUsageAndGCPause.getGCPauseTime()
    # x label
    gcTimeLine = heapUsageAndGCPause.getGCTimeline()
    colors2 = [u'#DDA0DD', u'#6A5ACD', u'#A9A9A9', u'#ADD8E6']
    keys = gcTotal.keys()
    axes3 = axes[1].twinx()
    time0 = gcTimeLine[keys[0]]
    time1 = gcTimeLine[keys[1]]
    time2 = gcTimeLine[keys[2]]
    time3 = gcTimeLine[keys[3]]
    value0 = gcTotal[keys[0]]
    value1 = gcTotal[keys[1]]
    value2 = gcTotal[keys[2]]
    value3 = gcTotal[keys[3]]
    bar1 = axes3.bar(time0, value0, 0.0001, color=colors2[0], label="1", edgecolor=colors2[0])
    bar2 = axes3.bar(time1, value1, 0.0001, color=colors2[1], label="2", edgecolor=colors2[1])
    bar3 = axes3.bar(time2, value2, 0.0001, color=colors2[2], label="3", edgecolor=colors2[2])
    bar4 = axes3.bar(time3, value3, 0.0001, color=colors2[3], label="4", edgecolor=colors2[3])
    axes3.set_ylabel(r"GCPause/$u$s")
    axes[1].set_xlabel(r"TimeLine/s")
    ymin, ymax = axes3.get_ylim()
    axes3.set_ylim(ymin, ymax * 2.5)
    lns = []
    lns.append(line1)
    lns.append(line2)
    lns.append(line3)
    lns.append(line4)
    lns.append(bar1)
    lns.append(bar2)
    lns.append(bar3)
    lns.append(bar4)
    keys = gcTotal.keys()
    labels = ("Usage", "Allocated", "YGC", "FGC", keys[0], keys[1], keys[2], keys[3])
    axes3.legend((line1, line2, line3, line4, bar1, bar2, bar3, bar4), labels, loc=0)
    plt.show()
    plt.savefig(outputFile, dpi=150, bbox_inches='tight')




if __name__ == '__main__':

    dir = "/Users/xulijie/Documents/GCResearch/Experiments-11-17/Abnormal/Join-1.0-E1/gclogs"
    #dir = "/Users/jaxon/github/SparkProfiler/src/test/gclogs/"
    # dir = "/Users/xulijie/dev/IdeaProjects/SparkProfiler/src/test/gclogs/"
    #outputDir = "/Users/xulijie/Documents/Texlipse/GC-Study/figures/SVM-1.0-E1/"
    # outputDir = "/Users/xulijie/Documents/Texlipse/GC-Study/figures/Join-1.0-E1/"
    #fileName = "Join-1.0-E1-P-12-23.txt"
    outputDir = "/Users/jaxon/github/SparkProfiler/"
    fileName = "ParsedParallelLog.txt"
    appName = "Join-1.0-E1-Parallel"
    dir2 = "/Users/jaxon/github/SparkProfiler/src/test/GCPause/"
    fileName2 = "JoinParallel.csv"

    plotHeapUsage(appName, dir + fileName, dir2 + fileName2, outputDir + "Parallel.pdf")

    # fileName = "ParsedCMSLog.txt"
    # appName = "Join-1.0-E1-CMS"
    # plotHeapUsage(appName, dir + fileName, outputDir + "CMS.pdf")
    #
    # fileName = "ParsedG1Log.txt"
    # appName = "Join-1.0-E1-G1"
    # plotHeapUsage(appName, dir + fileName, outputDir + "G1.pdf")


    # fileName = "Parsed-SVM-1.0-E1-G1-19.txt"
    # appName = "SVM-1.0-E1-G1"
    # plotHeapUsage(appName, dir + fileName, outputDir + "G1.pdf")

