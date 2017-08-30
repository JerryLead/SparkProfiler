import matplotlib.pyplot as plt
import numpy as np
import random
import matplotlib.cbook as cbook

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

fig, axes = plt.subplots(nrows=2, ncols=3, sharey='row')
fig.subplots_adjust(wspace=0)

for ax, stats in zip(axes[0], [Parallel, CMS, G1]):
    list = [stats['E-1'], stats['E-2'], stats['E-4']]
    ax.bxp(list, showfliers=False, showcaps=False, meanline=False, showmeans=True)
    # ax.set(xticklabels=['E1', 'E2', 'E4'], xlabel=stats['label'])
    ax.set_xticklabels([])

    ax2 = ax.twiny()  # ax2 is responsible for "top" axis and "right" axis
    ax2.set_xticks(ax.get_xticks())
    ax2.set_xticklabels(['5', '4', '3'])
    ax2.set_xlim(ax.get_xlim())

    ax.margins(0.05) # Optional

for ax, stats in zip(axes[1], [Parallel, CMS, G1]):
    list = [stats['E-1'], stats['E-2'], stats['E-4']]
    ax.bxp(list, showfliers=False, showcaps=False, meanline=False, showmeans=True)
    ax.set(xticklabels=['E1', 'E2', 'E4'], xlabel=stats['label'])

    ax2 = ax.twiny()  # ax2 is responsible for "top" axis and "right" axis
    ax2.set_xticks(ax.get_xticks())
    ax2.set_xticklabels(['5', '4', '3'])
    ax2.set_xlim(ax.get_xlim())

    ax.margins(0.05) # Optional

plt.show()