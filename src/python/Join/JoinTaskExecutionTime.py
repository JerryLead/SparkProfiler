"""
Broken axis example, where the y-axis will have a portion cut out.
"""
import matplotlib.pyplot as plt
import numpy as np

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

# fig = plt.figure(figsize=(3.2, 2.4))
# ax = fig.add_subplot(111)
# plt.subplots_adjust(left=0.19, bottom=0.11, right=0.98, top=0.87,
#                     wspace=0.03, hspace=0.04)

xvals = [4716, 164, 4552]
yvals = [386, 340, 46]
zvals = [580, 550, 30]


# If we were to simply plot pts, we'd lose most of the interesting
# details due to the outliers. So let's 'break' or 'cut-out' the y-axis
# into two portions - use the top (ax) for the outliers, and the bottom
# (ax2) for the details of the majority of our data
f, (ax, ax2) = plt.subplots(2, 1, sharex=True, figsize=(3.4, 2.4))
plt.subplots_adjust(left=0.22, bottom=0.11, right=0.97, top=0.87,
                    wspace=0.03, hspace=0.04)


rects1 = ax.bar(ind, xvals, width, color='lightpink', edgecolor='black')#, hatch="///")
rects2 = ax.bar(ind+width, yvals, width, color='lightgreen', edgecolor='black', hatch='xxx')
rects3 = ax.bar(ind+width*2, zvals, width, color='deepskyblue', edgecolor='black', hatch='\\\\\\')

#ax.set_xticks(ind+width)
#ax.set_xticklabels( ('CompTime', 'SpillTime', 'GCTime'), color='black')#, borderaxespad = 'bold')

ax.legend( (rects1[0], rects2[0], rects3[0]), ('Parallel', 'CMS', 'G1'),
           frameon=False, loc = "upper right", labelspacing=0.2, markerfirst=False, #prop=legend_properties,
           fontsize=10, ncol=3, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)#, handlelength=0.8)

#plt.xlim(-0.3, 2.76)  # The ceil
ax2.set_xlim(-0.32, 2.78)  # The ceil



rects4 = ax2.bar(ind, xvals, width, color='lightpink', edgecolor='black')#, hatch="///")
rects5 = ax2.bar(ind+width, yvals, width, color='lightgreen', edgecolor='black', hatch='xxx')
rects6 = ax2.bar(ind+width*2, zvals, width, color='deepskyblue', edgecolor='black', hatch='\\\\\\')


ax2.set_xticks(ind+width)
ax2.set_xticklabels( ('CompTime', 'SpillTime', 'GCTime'), color='black')#, borderaxespad = 'bold')
#
# ax2.legend( (rects1[0], rects2[0], rects3[0]), ('Parallel', 'CMS', 'G1'),
#            frameon=False, loc = "upper right", labelspacing=0.2, markerfirst=False, #prop=legend_properties,
#            fontsize=10, ncol=3, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)#, handlelength=0.8)

#plt.xlim(-0.3, 2.76)  # The ceil
ax2.set_xlim(-0.32, 2.78)  # The ceil

# zoom-in / limit the view to different portions of the data
ax.set_ylim(4050, 6000)  # outliers only
ax2.set_ylim(0, 1550)  # most of the data

# hide the spines between ax and ax2
ax.spines['bottom'].set_visible(False)
ax2.spines['top'].set_visible(False)
ax.xaxis.set_ticks_position('none')
ax.tick_params(labeltop='off')  # don't put tick labels at the top
ax2.xaxis.tick_bottom()

# This looks pretty good, and was fairly painless, but you can get that
# cut-out diagonal lines look with just a bit more work. The important
# thing to know here is that in axes coordinates, which are always
# between 0-1, spine endpoints are at these locations (0,0), (0,1),
# (1,0), and (1,1).  Thus, we just need to put the diagonals in the
# appropriate corners of each of our axes, and so long as we use the
# right transform and disable clipping.

d = .015  # how big to make the diagonal lines in axes coordinates
# arguments to pass to plot, just so we don't keep repeating them
kwargs = dict(transform=ax.transAxes, color='k', clip_on=False)
ax.plot((-d, +d), (-d, +d), **kwargs)        # top-left diagonal
ax.plot((1 - d, 1 + d), (-d, +d), **kwargs)  # top-right diagonal

kwargs.update(transform=ax2.transAxes)  # switch to the bottom axes
ax2.plot((-d, +d), (1 - d, 1 + d), **kwargs)  # bottom-left diagonal
ax2.plot((1 - d, 1 + d), (1 - d, 1 + d), **kwargs)  # bottom-right diagonal

# What's cool about this is that now if we vary the distance between
# ax and ax2 via f.subplots_adjust(hspace=...) or plt.subplot_tool(),
# the diagonal lines will move accordingly, and stay right at the tips
# of the spines they are 'breaking'

ax.set_title("(a) Join-task-execution-time", fontsize=12)



def autolabel(rects, ax):
    for rect in rects:
        h = rect.get_height()
        ax.text(rect.get_x()+rect.get_width()/2., 1.03*h, '%d'%int(h),
                ha='center', va='bottom', fontsize=10)#, rotation='vertical')#, rotation='45')

autolabel(rects1, ax)
autolabel(rects2, ax2)
autolabel(rects3, ax2)

yaxis_label = plt.ylabel('Time (s)', color='black')
yaxis_label.set_position((-0.05,1))
#f.tight_layout()
plt.show()