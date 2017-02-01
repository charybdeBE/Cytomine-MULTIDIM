#!bin/python

import math
import matplotlib.pyplot as plt
import numpy

band = [ 6, 256, 512, 768, 1024, 1280, 1536, 1650]
times = []

f = open('result_ims_square_1650_1.txt', 'r')

f.readline()
list_value = []

for line in f :
	try :
		list_value += [float(line)]
	except ValueError :
		continue
f.close()

print "Test nr 1"
moy = numpy.mean(list_value)
times += [ moy ]
print('Temps moyen par pixel (ms) : {0}'.format(moy))
et = numpy.std(list_value)
print('Ecart type (s) : {0}'.format(et))

n, bins, patches = plt.hist(list_value, 20)
plt.title('Result Test number 1')
plt.xlabel('Time (ms/pixel)')
plt.savefig('hist_ims_ask_square.png', format = 'png')
plt.show()

f = open('result_ims_pxlrush_1650_1.txt', 'r')

f.readline()
list_value = []

for line in f :
	try :
		list_value += [float(line)]
	except ValueError :
		continue
f.close()

print "Test nr 2"
moy = numpy.mean(list_value)
times += [ moy ]
print('Temps moyen par pixel (ms) : {0}'.format(moy))
et = numpy.std(list_value)
print('Ecart type (s) : {0}'.format(et))

n, bins, patches = plt.hist(list_value, 20)
plt.title('Result Test number 1')
plt.xlabel('Time (ms/pixel)')
plt.savefig('hist_ims_anon_square.png', format = 'png')
plt.show()







