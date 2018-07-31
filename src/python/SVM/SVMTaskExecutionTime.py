import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl

mpl.rcParams['axes.linewidth'] = 1.5 #set the value globally

#plt.rc('font', family='Helvetica')
# font = {'family' : 'Helvetica',
#         'weight' : 'normal',
#         'color'  : 'black',
#         'size'   : '12'}

plt.rc('pdf', fonttype=42)
plt.rc('font', family='Helvetica', size=12)

N = 3
ind = np.arange(N)  # the x locations for the groups

width = 0.23       # the width of the bars

fig = plt.figure(figsize=(3.4, 2.4))
ax = fig.add_subplot(111)
plt.subplots_adjust(left=0.21, bottom=0.11, right=0.96, top=0.87,
                    wspace=0.03, hspace=0.04)

#plt.tight_layout()
legend_properties = {'weight':'bold'}

xvals = [503, 0, 70] # Paralell-0004-Executor-2-9.6min(1.1min)
yvals = [473, 0, 68] # CMS-0017-Executor-16-9.6min(1.0min)
#zvals = [0, 0, 0]

rects1 = ax.bar(ind, xvals, width, color='lightpink', edgecolor='black')#, hatch="///")
rects2 = ax.bar(ind+width, yvals, width, color='lightgreen', edgecolor='black', hatch='xxx')
#rects3 = ax.bar(ind+width*2, zvals, width, color='deepskyblue', edgecolor='black', hatch='\\\\\\')

ax.set_ylabel('Time (s)', color='black')
ax.set_xticks(ind+width/2)
ax.set_xticklabels( ('CompTime', 'SpillTime', 'GCTime'), color='black')#, borderaxespad = 'bold')

ax.legend( (rects1[0], rects2[0]), ('Parallel', 'CMS'),
           frameon=False, loc = "upper center", labelspacing=0.2, markerfirst=False, #prop=legend_properties,
           fontsize=10, ncol=3, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)#, handlelength=0.8)
ax.set_ylim(0, 1000)  # The ceil
#plt.xlim(-0.3, 2.76)  # The ceil
ax.set_xlim(-0.32, 2.70)  # The ceil

plt.title("(a) SVM-task-execution-time", fontsize=12)



def autolabel(rects, loc, angle):
    for rect in rects:
        h = rect.get_height()
        ax.text(rect.get_x()+rect.get_width()/2.+loc, 1.03*h, '%d'%int(h),
                ha='center', va='bottom', fontsize=11, rotation=angle)

autolabel(rects1, 0, 45)
autolabel(rects2, 0, 45)


plt.show()