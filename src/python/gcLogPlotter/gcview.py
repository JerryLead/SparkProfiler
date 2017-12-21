#!/usr/bin/python
import os
import sys
import re
import time
from stat import *
from pylab import *

def parse(line):
    """
    Parses an input line from gc.log into a set of tokens and returns them.
    There are two patterns we have to look for:
      112829.094: [GC 695486K->557348K(806720K), 0.0191830 secs]
      112776.534: [Full GC 690522K->551411K(817408K), 1.8249860 secs]
    """
    pgre = re.compile("(\d+\.\d+):\s\[.+\]\s+(\d+)K->(\d+)K\((\d+)K\),\s(\d+\.\d+)\ssecs\]")
    fre = re.compile("(\d+\.\d+):\s\[.+\]\s+(\d+)K->(\d+)K\((\d+)K\)\s\[.+\],\s(\d+\.\d+)\ssecs\]")
    # First try matching with the partial GC pattern pgre
    isFullGc = False
    mo = pgre.match(line)
    # Then match with the full GC pattern
    if (mo == None):
        mo = fre.match(line)
        isFullGc = True
    # return tsoffset, heapUsedBeforeGc(Kb), heapUsedAfterGc(Kb), elapsedTime(s), heapSize(Kb), isFullGc
    return float(mo.group(1)), int(mo.group(2)), int(mo.group(3)), float(mo.group(5)), int(mo.group(4)), isFullGc

def drawGraph(x1vals, y1vals, x2vals, y2vals, y3vals, x4vals, y4vals, startTime, endTime, output):
    """
    Draws a graph of the GC behavior we are interested in. There are three
    line graphs and one series of points.
    - Memory in use before GC happens (red line)
    - Memory in use after GC happens (green line)
    - Total JVM heap size (yellow line)
    - Times when full GC happens (blue dots on X-axis)
    - The Y-axis (for memory usage) numbers are shown in MB
    - The X-axis (for time) is plotted in minutes since start
    - The title contains the start and end times for this plot
    """
    xlabel("Time (minutes)")
    ylabel("Heap(Mb)")
    title("GC Log (" + startTime + " to " + endTime + ")")
    # Heap in use graph over time before garbage collection
    plot(x1vals, y1vals, 'r')
    # Heap in use graph over time after garbage collection
    plot(x2vals, y2vals, 'g')
    # Total heap size over time
    plot(x2vals, y3vals, 'y')
    # Full GC over time
    plot(x4vals, y4vals, 'bo')
    savefig(output)

def usage():
    """
    Prints the script's usage guide.
    """
    print "Usage: gcview.py input output [time-start] [time-end]"
    print "input = path to gc.log file"
    print "output = path to gc.png file"
    print "time-start = date in yyyy-MM-dd HH:mm format"
    print "time-end = date in yyyy-MM-dd HH:mm format"
    sys.exit(-1)

def convertISOToUnixTS(isots):
    """
    Takes a timestamp (supplied from the command line) in ISO format, ie
    yyyy-MM-dd HH:mm and converts it to seconds since the epoch.
    """
    isore = re.compile("(\d{4})-(\d{2})-(\d{2})\s(\d{2}):(\d{2})")
    mo = isore.match(isots)
    return time.mktime([int(mo.group(1)), int(mo.group(2)), int(mo.group(3)), int(mo.group(4)), int(mo.group(5)), 0, 0, 0, -1])

def baseTimeStamp(logFile):
    """
    Since the timestamps in the gc.log file are probably in seconds since
    server startup, we want to get an indication of the time the first log
    line was written. We do this by getting the ctime of the gc.log file.
    """
    return os.lstat(logFile)[ST_CTIME]

def minutesElapsed(currentTS, baseTS):
    """
    Convert the timestamp (in seconds since JVM startup) to mins elapsed
    since first timestamp entry.
    """
    return (currentTS - baseTS) / 60

def timeString(ts):
    """
    Return printable version of time represented by seconds since epoch
    """
    return time.strftime("%Y-%m-%d %H:%M", time.localtime(ts))

def main():
    """
    This is how we are called. Reads the command line args, reads the input
    file line by line, calling out to parse() for each line, processing and
    pushing the tokens into arrays that are passed into the drawGraph() method.
    Example call:
    ./gcview.py ../tmp/gc.log gc-24h.png
    ./gcview.py ../tmp/gc.log gc-6h.png "2006-08-13 05:00" "2006-08-13 11:00"
    ./gcview.py ../tmp/gc.log gc-2h.png "2006-08-13 09:00" "2006-08-13 11:00"
    ./gcview.py ../tmp/gc.log gc-1h.png "2006-08-13 10:00" "2006-08-13 11:00"
    """
    if (len(sys.argv) != 3 and len(sys.argv) != 5):
        usage()
    input = sys.argv[1]
    output = sys.argv[2]
    # optional start and end times provided
    if (len(sys.argv) == 5):
        sliceLogFile = True
        startTime = convertISOToUnixTS(sys.argv[3])
        endTime = convertISOToUnixTS(sys.argv[4])
    else:
        sliceLogFile = False
        startTime = 0
        endTime = 0
    # The base time is the ctime for the log file
    baseTS = baseTimeStamp(input)
    # initialize local variables
    timeStampsBeforeGc = []
    usedBeforeGc = []
    timeStampsAfterGc = []
    usedAfterGc = []
    heapSizes = []
    timeStampsForFullGc = []
    fullGcIndicators = []
    gcStartTS = -1
    gcEndTS = -1
    # read input and parse line by line
    fin = open(input, 'r')
    while (True):
        line = fin.readline()
        if (line == ""):
            break
        (tsoffset, usedBefore, usedAfter, elapsed, heapSize, isFullGc) = parse(line.rstrip())
        # Set the first timestamp once for the very first record, and keep
        # updating the last timestamp until we run out of lines to read
        if (gcStartTS == -1):
            gcStartTS = tsoffset
        gcEndTS = tsoffset
        # If start and end times are specified, then we should ignore data
        # that are outside the range
        if (sliceLogFile):
            actualTime = baseTS - gcStartTS + tsoffset
            if (actualTime < startTime or actualTime > endTime):
                continue
        # X and Y arrays for before GC line, X will need postprocessing
        timeStampsBeforeGc.append(tsoffset)
        usedBeforeGc.append(usedBefore / 1024)
        # X and Y arrays for after GC line, X will need postprocessing
        timeStampsAfterGc.append(tsoffset + elapsed)
        usedAfterGc.append(usedAfter / 1024)
        # Y array for heap size (use minOffSetBeforeGC for X), will use
        # Y axis for after GC line
        heapSizes.append(heapSize / 1024)
        # X and Y arrays for Full GC line, X will need postprocessing
        if (isFullGc):
            timeStampsForFullGc.append(tsoffset)
            fullGcIndicators.append(1)
    fin.close()
    # Convert log start and end time stamps to printable format
    if (sliceLogFile):
        logStartTS = sys.argv[3]
        logEndTS = sys.argv[4]
    else:
        logStartTS = timeString(baseTS)
        logEndTS = timeString(baseTS + gcEndTS - gcStartTS)
    # convert timestamps from seconds since JVM startup to minutes elapsed
    # since first timestamp entry
    startTime = timeStampsBeforeGc[0]
    for i in range(len(timeStampsBeforeGc)):
        timeStampsBeforeGc[i] = minutesElapsed(timeStampsBeforeGc[i], startTime)
        timeStampsAfterGc[i] = minutesElapsed(timeStampsAfterGc[i], startTime)
    for i in range(len(timeStampsForFullGc)):
        timeStampsForFullGc[i] = minutesElapsed(timeStampsForFullGc[i], startTime)
    # Send off to graph results
    drawGraph(timeStampsBeforeGc, usedBeforeGc, timeStampsAfterGc, usedAfterGc, heapSizes, timeStampsForFullGc, fullGcIndicators, logStartTS, logEndTS, output)


if __name__ == "__main__":
    main()