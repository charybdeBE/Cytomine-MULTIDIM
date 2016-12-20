#!bin/python

import math
import matplotlib.pyplot as plt
import numpy


def stat_from_file(filename, graph, save):
	f = open(filename, 'r')
	f.readline()
	list_value = []
	for line in f :
		try :
			list_value += [float(line) / 100.0]
		except ValueError :
			continue
	f.close()
	print "Test nr 1"
	moy = numpy.mean(list_value)
	print('Temps moyen par pixel (s) : {0}'.format(moy))
	et = numpy.std(list_value)
	print('Ecart type (s) : {0}'.format(et))
	
	if(graph):
		n, bins, patches = plt.hist(list_value, 20)
		plt.xlabel('Time (s/pixel)')
		
		if(save):
			plt.savefig('hist_test1_8.png', format = 'png')
		plt.show()
	return;

stat_from_file('result_16size_threadpool_1.txt', 1, 0)
stat_from_file('result_32size_threadpool_1.txt', 1, 0)
stat_from_file('result_48size_threadpool_1.txt', 1, 0)
stat_from_file('result_64size_threadpool_1.txt', 1, 0)




#test // random access then square access
"""
means_r = []
stds_r = []
means_s = []
stds_s = []
means_so = []
stds_so = []


for i in xrange(2,9) :
	fil = 'result_'+str(i)+'cores_random_demo.txt'
	f = open(fil, 'r')

	line = f.readline()
	list_value = []

	for line in f :
		try :
			list_value += [float(line) / 1000.0]
		except ValueError :
			continue
	f.close()
#test nr 3 = test nr 1 in //
	print "Test nr 3 with " + str(i)  + " cores : "
	moy = numpy.mean(list_value) 
	print('Temps moyen par pixel (s) : {0}'.format(moy))
	et = numpy.std(list_value)
	print('Ecart type (s) : {0}'.format(et))

	means_r += [moy]
	stds_r += [et]

	fil = 'result_'+str(i)+'cores_square_demo.txt'
	f = open(fil, 'r')

	line = f.readline()
	list_value = []

	for line in f :
		try :
			list_value += [float(line) / 1000.0]
		except ValueError :
			continue
	f.close()
#test  nr 4 = test nr 2 in  //
	print "Test nr 4  with " + str(i)  + " cores : "
	moy = numpy.mean(list_value) 
	print('Temps moyen par pixel (s) : {0}'.format(moy))
	et = numpy.std(list_value)
	print('Ecart type (s) : {0}'.format(et))

	means_s += [moy]
	stds_s += [et]

	fil = 'result_'+str(i)+'cores_oncessquare_demo.txt'
	f = open(fil, 'r')

	line = f.readline()
	list_value = []

	for line in f :
		try :
			list_value += [float(line) / (1000.0 * 100.0)]
		except ValueError :
			continue
	f.close()

#Test nr 5 = test nr 4 but the threads are only spawn once per square
	print "Test nr 5  with " + str(i)  + " cores : "
	moy = numpy.mean(list_value) 
	print('Temps moyen par pixel (s) : {0}'.format(moy))
	et = numpy.std(list_value)
	print('Ecart type (s) : {0}'.format(et))

	means_so += [moy]
	stds_so += [et]



plt.plot(xrange(2,9), means_r)
plt.title('Scalability test : means per core random access')
plt.ylabel('Mean time (s/pixel)')
plt.xlabel('Number of cores')
plt.show()

plt.plot(xrange(2,9), means_s, 'b-')
plt.plot(xrange(2,9), means_so, 'r-')
plt.legend( ["Thread spawn by pxl", "Thread spawn by square" ])
plt.title('Scalability test : means per core square access')
plt.ylabel('Mean time (s/pixel)')
plt.xlabel('Number of cores')
plt.show()
"""





