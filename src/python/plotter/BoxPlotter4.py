import matplotlib.pyplot as plt
import numpy as np


data = {}
data['Parallel'] = {}
data['CMS'] = {}
data['G1'] = {}

n = 5
for k,v in data.iteritems():
    upper = random.randint(0, 1000)
    #v['E-1'] = cbook.boxplot_stats(np.random.uniform(0, upper, size=n))
    #v['E-2'] = cbook.boxplot_stats(np.random.uniform(0, upper, size=n))
    #v['E-4'] = cbook.boxplot_stats(np.random.uniform(0, upper, size=n))

    v['E-1'] = np.random.uniform(0, upper, size=n)
    v['E-2'] = np.random.uniform(0, upper, size=n)
    v['E-4'] = np.random.uniform(0, upper, size=n)


fig, axes = plt.subplots(ncols=3, sharey=True)
fig.subplots_adjust(wspace=0)

for ax, name in zip(axes, ['Parallel', 'CMS', 'G1']):
    #ax.boxplot()

    list = [cbook.boxplot_stats(data[name][item], labels=[item]) for item in ['E-1', 'E-2', 'E-4']]
    print(list)
    ax.bxp(list)
    #ax.set(xticklabels=['E-1', 'E-2', 'E-4'], xlabel=name)
    ax.margins(0.05) # Optional

plt.show()