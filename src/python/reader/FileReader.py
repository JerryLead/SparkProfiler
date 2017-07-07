# class FileReader:
#
#     @staticmethod
def readLines(textFile):
    lines = []
    file = open(textFile)
    while True:
        line = file.readline()
        if not line:
            break
        lines.append(line.strip('\n'))

    return lines
