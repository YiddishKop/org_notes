from matplotlib import pyplot as plt
from numpy import genfromtxt
import numpy as np
from matplotlib.font_manager import _rebuild

_rebuild()

FILE_PATH = './se1_data.csv'

def read_data(file_path):
    csv = genfromtxt(file_path, delimiter=',', encoding='utf8', dtype=np.str)
     
    # 获取架构姓名
    names = csv[:,0]
     
    # 获取工作时间, 并转为 float 存入 x
    x = csv[:,1].astype(np.float)
     
    # 构建姓名和工作时间的关系,用于绘图
    relation = dict((zip(x, names)))
     
    # 无作用,辅助描点
    y = np.zeros(np.shape(x))

    return x, y, relation


def scatter_data(x, y, relation):
     
    # 初始化画布
    ax = plt.subplot(111)
     
    # 画布细节处理
    ax.spines['right'].set_visible(False)
    ax.spines['left'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.axes.get_yaxis().set_visible(False)
    ax.xaxis.set_ticks_position('bottom')
    ax.spines['bottom'].set_position(('data',0))
     
    # 描点
    ax.scatter(x, y, c='k')
    
    # 描点与名字关联
    for period, name in relation.items():
        ax.annotate(name, (period, 0))

    # 显示画布
    plt.show()


def classify(x, y, relation, metrics):
    # 初始化画布
    ax = plt.subplot(111)
    
    # 画布细节处理
    ax.spines['right'].set_visible(False)
    ax.spines['left'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.axes.get_yaxis().set_visible(False)
    ax.xaxis.set_ticks_position('bottom')
    ax.spines['bottom'].set_position(('data',0))

    
    # 描点
    ax.scatter(x, y, c='b')
    
    # 描点与名字关联
    for period, name in relation.items():
        ax.annotate(name, (period, 0))

    # 根据分类标准进行分类

    metrics_x_bad= np.linspace(5, metrics, 1000)
    metrics_x_good= np.linspace(metrics, 14, 1000)

    metrics_y_bad= np.full_like(metrics_x_bad, 1)
    metrics_y_good= np.full_like(metrics_x_good, 1)
    metrics_bottom_bad= np.full_like(metrics_x_bad, 0)
    metrics_bottom_good= np.full_like(metrics_x_good, 0)

    # 填充两个函数之间颜色

    plt.fill_between(metrics_x_bad, metrics_y_bad, metrics_bottom_bad, facecolor='red',alpha=0.3 )
    plt.fill_between(metrics_x_good, metrics_y_good, metrics_bottom_good, facecolor='green' ,alpha=0.3)

    # 显示说明
    ax.text((metrics-5)/2+5 , 0.1, 'BAD', fontsize=15)
    ax.text((13-metrics)/2+metrics,0.1, 'GOOD', fontsize=15)


    
    plt.ylim((0,1))
        
    # 显示画布
    plt.show()
 
    
    
    
if __name__ == '__main__':
    x, y, relation = read_data(FILE_PATH)
    scatter_data(x, y, relation)
    classify(x,y,relation,11.8)

