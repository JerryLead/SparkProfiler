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


Parallel = {}
CMS = {}
G1 = {}

Parallel['label'] = 'Parallel'
Parallel['E-1'] = {}
Parallel['E-1']['label'] = 'Parallel_1_7G'
Parallel['E-1']['q1'] = 2.35
Parallel['E-1']['med'] = 3.33
Parallel['E-1']['q3'] = 14.85
Parallel['E-1']['whishi'] = Parallel['E-1']['q3']
Parallel['E-1']['whislo'] = Parallel['E-1']['q1']
Parallel['E-1']['mean'] = 13.00
Parallel['E-1']['fliers'] = []

Parallel['E-2'] = {}
Parallel['E-2']['label'] = 'Parallel_2_14G'
Parallel['E-2']['q1'] = 2
Parallel['E-2']['med'] = 3.33
Parallel['E-2']['q3'] = 14.85
Parallel['E-2']['whishi'] = Parallel['E-2']['q3']
Parallel['E-2']['whislo'] = Parallel['E-2']['q1']
Parallel['E-2']['mean'] = 13.00
Parallel['E-2']['fliers'] = []

Parallel['E-4'] = {}
Parallel['E-4']['label'] = 'Parallel_4_28G'
Parallel['E-4']['q1'] = 1.90
Parallel['E-4']['med'] = 3.33
Parallel['E-4']['q3'] = 14.85
Parallel['E-4']['whishi'] = Parallel['E-4']['q3']
Parallel['E-4']['whislo'] = Parallel['E-4']['q1']
Parallel['E-4']['mean'] = 13.00
Parallel['E-4']['fliers'] = []

CMS['label'] = 'CMS'
CMS['E-1'] = {}
CMS['E-1']['label'] = 'CMS_1_7G'
CMS['E-1']['q1'] = 1.8
CMS['E-1']['med'] = 3.33
CMS['E-1']['q3'] = 14.85
CMS['E-1']['whishi'] = CMS['E-1']['q3']
CMS['E-1']['whislo'] = CMS['E-1']['q1']
CMS['E-1']['mean'] = 13.00
CMS['E-1']['fliers'] = []

CMS['E-2'] = {}
CMS['E-2']['label'] = 'CMS_2_14G'
CMS['E-2']['q1'] = 1.7
CMS['E-2']['med'] = 3.33
CMS['E-2']['q3'] = 14.85
CMS['E-2']['whishi'] = CMS['E-2']['q3']
CMS['E-2']['whislo'] = CMS['E-2']['q1']
CMS['E-2']['mean'] = 13.00
CMS['E-2']['fliers'] = []

CMS['E-4'] = {}
CMS['E-4']['label'] = 'CMS_4_28G'
CMS['E-4']['q1'] = 1.1
CMS['E-4']['med'] = 3.33
CMS['E-4']['q3'] = 14.85
CMS['E-4']['whishi'] = CMS['E-4']['q3']
CMS['E-4']['whislo'] = CMS['E-4']['q1']
CMS['E-4']['mean'] = 13.00
CMS['E-4']['fliers'] = []


G1['label'] = 'G1'
G1['E-1'] = {}
G1['E-1']['label'] = 'G1_1_7G'
G1['E-1']['q1'] = 2.35
G1['E-1']['med'] = 3.33
G1['E-1']['q3'] = 14.85
G1['E-1']['whishi'] = G1['E-1']['q3']
G1['E-1']['whislo'] = G1['E-1']['q1']
G1['E-1']['mean'] = 13.00
G1['E-1']['fliers'] = []

G1['E-2'] = {}
G1['E-2']['label'] = 'G1_2_14G'
G1['E-2']['q1'] = 1.5
G1['E-2']['med'] = 3.33
G1['E-2']['q3'] = 14.85
G1['E-2']['whishi'] = G1['E-2']['q3']
G1['E-2']['whislo'] = G1['E-2']['q1']
G1['E-2']['mean'] = 13.00
G1['E-2']['fliers'] = []

G1['E-4'] = {}
G1['E-4']['label'] = 'G1_4_28G'
G1['E-4']['q1'] = 1.2
G1['E-4']['med'] = 3.33
G1['E-4']['q3'] = 14.85
G1['E-4']['whishi'] = G1['E-4']['q3']
G1['E-4']['whislo'] = G1['E-4']['q1']
G1['E-4']['mean'] = 13.00
G1['E-4']['fliers'] = []