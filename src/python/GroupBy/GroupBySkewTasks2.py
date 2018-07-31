import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl
import os
import json
import io
plt.rc('pdf', fonttype=42)
plt.rc('font', family='Helvetica', size=10)
fig = plt.figure(figsize=(5.1, 2.4))
ax = fig.add_subplot(111)
plt.subplots_adjust(left=0.10, bottom=0.18, right=0.98, top=0.87,
                    wspace=0.03, hspace=0.04)
file_Dir="D:/plot/executors_0/"
jump=4

#file_list=os.listdir(file_Dir)
data_list=[]
name_list=[]
text_list=[]
# for file in file_list:
#     json_dir=file_Dir+file+"/gcMetrics-"+file+".json"
#     json_file=json.load(io.open(json_dir,'r',encoding='utf-8'))
#     data=json_file["jvmHeapSize"]["oldGen"]["peakSize"]
#     type=data.split(" ")
#     if type[1]=="mb":
#         data=(float(type[0])/1024)
#     else:
#         data=(float(type[0]))
#     text_list.append((data,file))
#text_list=sorted(text_list,cmp = lambda x,y: cmp(x[0],y[0]),reverse=True)
#print(text_list)
data_list=map(lambda x:x[0],text_list)
name_list=map(lambda x:x[1],text_list)
print(data_list)
print(name_list)

data_list_1=[3.9]
data_list_2=[3.65, 3.5, 3.4]
data_list_3=[3.2, 3.1, 2.99, 2.85, 2.66, 2.43, 2.29, 1.98, 1.84, 1.43, 1.34, 1.23, 0.858984375, 0.677841796875, 0.537353515625, 0.394541015625, 0.227578125, 0.204443359375, 0.111806640625, 0.111796875, 0.111767578125, 0.11162109375, 0.1072265625, 0.10693359375, 0.106728515625, 0.0958203125, 0.09580078125, 0.091171875]
name_list=['27', '11', '30', '19', '5', '26', '28', '18', '24', '13', '20', '2', '17', '29', '16', '15', '12', '3', '22', '14', '31', '7', '0', '21', '1', '9', '23', '6', '4', '8', '25', '10']
print(data_list)
print(name_list)
width = 1       # the width of the bars
N = 32 # len(file_list)
ind_1 = np.arange(len(data_list_1))  # the x locations for the groups
ind_2 = np.arange(len(data_list_2))
ind_3 = np.arange(len(data_list_3))
xlab_int=np.arange(0,N,jump)

xlab=map(lambda x:str(x+1),xlab_int)
xlab.append('32')
xlab_i=map(lambda x:int(x)+width,xlab_int)
xlab_i.append(32)


ax.bar(ind_1+width, data_list_1, width, color='lightgreen', edgecolor='black', hatch='xxxx',label="Trigger shuffle spill with any GC")
ax.bar(ind_2+width+len(data_list_1), data_list_2, width, color='deepskyblue', edgecolor='black', hatch='\\\\\\\\',label="Trigger shuffle spill with Parallel GC")
ax.bar(ind_3+width+len(data_list_1)+len(data_list_2), data_list_3, width, color='lightpink', edgecolor='black',label="Without shuffle spill")

ax.hlines(3.29, 0, 33, colors = "black", linestyles = "dashed", linewidth=1)
ax.hlines(3.69, 0, 33, colors = "black", linestyles = ":", linewidth=1)
ax.hlines(3.70, 0, 33, colors = "black", linestyles = "-.", linewidth=1)
ax.set_ylabel('Shuffled records (GB)', color='black', fontsize=10)
ax.set_xlabel('Task number', color='black', fontsize=10)
ax.set_xticks(xlab_i)
ax.set_xticklabels(xlab, color='black' )
ax.legend(loc = "right",frameon=False,fontsize=9)
#            frameon=False, loc = "upper right", labelspacing=0.2, markerfirst=False, fontsize=10 )

#ax.legend( (rects1[0], rects2[0], rects3[0]), ('Parallel', 'CMS', 'G1'),
#           frameon=False, loc = "upper right", labelspacing=0.2, markerfirst=False, #prop=legend_properties,
#           fontsize=10, ncol=3, borderaxespad=0.3, columnspacing=1.2, handletextpad=0.5)#, handlelength=0.8)

ax.set_ylim(0, 4.2)  # The ceil

#ax.set_xlim(-0.32, 2.78)  # The ceil

#plt.title("(a) GroupBy-task-execution-time", fontsize=12)
#plt.title("The distribution of shuffled records in 32 reduce tasks", fontsize=12)

plt.show()