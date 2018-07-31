import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl

mpl.rcParams['axes.linewidth'] = 1.5 #set the value globally

#plt.rc('font', family='Helvetica')
# font = {'family' : 'Helvetica',
#         'weight' : 'normal',
#         'color'  : 'black',
#         'size'   : '12'}


plt.rc('font', family='Helvetica', size=12)

N = 3
ind = np.arange(N)  # the x locations for the groups

width = 0.23       # the width of the bars

plt.rc('pdf', fonttype=42)
fig = plt.figure(figsize=(3.4, 2.4))
ax = fig.add_subplot(111)
plt.subplots_adjust(left=0.20, bottom=0.11, right=0.96, top=0.87,
                    wspace=0.03, hspace=0.04)

#plt.tight_layout()

xvals = [17, 53, 0]
yvals = [22, 9, 34]
zvals = [14, 2, 28]

rects1 = ax.bar(ind, xvals, width, color='lightpink', edgecolor='black')#, hatch="///")
rects2 = ax.bar(ind+width, yvals, width, color='lightgreen', edgecolor='black', hatch='xxx')
rects3 = ax.bar(ind+width*2, zvals, width, color='deepskyblue', edgecolor='black', hatch='\\\\\\')

ax.set_ylabel('GC time (s)', color='black')
ax.set_xticks(ind+width)
ax.set_xticklabels( ('YGC', 'FGC', 'ConGC'), color='black' )
# ax.legend( (rects1[0], rects2[0], rects3[0]), ('Parallel', 'CMS', 'G1'),
#            frameon=False, loc = "upper right", labelspacing=0.2, markerfirst=False, fontsize=10 )

ax.legend( (rects1[0], rects2[0], rects3[0]), ('Parallel', 'CMS', 'G1'),
           frameon=False, loc = "upper right", labelspacing=0.2, markerfirst=False, #prop=legend_properties,
           fontsize=10, ncol=3, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)#, handlelength=0.8)

ax.set_ylim(0, 100)  # The ceil
#plt.xlim(-0.3, 2.76)  # The ceil
ax.set_xlim(-0.32, 2.78)  # The ceil

#plt.title("(a) GroupBy-task-execution-time", fontsize=12)
plt.title("(b) GroupBy-0.5-task-GC-time", fontsize=12)


def autolabel(rects):
    for rect in rects:
        h = rect.get_height()
        ax.text(rect.get_x()+rect.get_width()/2., 1.05*h, '%d'%int(h),
                ha='center', va='bottom', fontsize=11)

autolabel(rects1)
autolabel(rects2)
autolabel(rects3)


plt.show()