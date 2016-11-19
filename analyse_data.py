#!bin/python

import math
import matplotlib.pyplot as plt
import numpy

f = open('results_test1.txt', 'r')

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
print('Temps moyen par pixel (s) : {0}'.format(moy))
et = numpy.std(list_value)
print('Ecart type (s) : {0}'.format(et))

#n, bins, patches = plt.hist(list_value, 20)
#plt.title('Result Test number 1')
#plt.xlabel('Time (s/pixel)')
#plt.savefig('hist_test1.png', format = 'png')
#plt.show()


f = open('results_test2.txt', 'r')

line = f.readline()
list_value = []

for line in f :
	try :
		list_value += [float(line) / 100.0 ]
	except ValueError :
		continue
f.close()

print "Test nr 2"
moy = numpy.mean(list_value) 
print('Temps moyen par pixel (s) : {0}'.format(moy))
et = numpy.std(list_value)
print('Ecart type (s) : {0}'.format(et))


#n, bins, patches = plt.hist(list_value, 20)
#plt.title('Result Test number 2')
#plt.xlabel('Time(s/pixel)')
#plt.savefig('hist_test2.png', format = 'png')
#plt.show()


#below are the results of the parrallel tests


#test // random access then square access

means_r = []
stds_r = []
means_s = []
stds_s = []



for i in xrange(2,9) :
	fil = 'result_'+str(i)+'cores_random_1.txt'
	f = open(fil, 'r')

	line = f.readline()
	list_value = []

	for line in f :
		try :
			list_value += [float(line) / 1000.0]
		except ValueError :
			continue
	f.close()

	print "Test nr 3 with " + str(i)  + " cores : "
	moy = numpy.mean(list_value) 
	print('Temps moyen par pixel (s) : {0}'.format(moy))
	et = numpy.std(list_value)
	print('Ecart type (s) : {0}'.format(et))

	means_r += [moy]
	stds_r += [et]

	fil = 'result_'+str(i)+'cores_square_1.txt'
	f = open(fil, 'r')

	line = f.readline()
	list_value = []

	for line in f :
		try :
			list_value += [float(line) / 1000.0]
		except ValueError :
			continue
	f.close()

	print "Test nr 4  with " + str(i)  + " cores : "
	moy = numpy.mean(list_value) 
	print('Temps moyen par pixel (s) : {0}'.format(moy))
	et = numpy.std(list_value)
	print('Ecart type (s) : {0}'.format(et))

	means_s += [moy]
	stds_s += [et]



	#n, bins, patches = plt.hist(list_value, 20)
	#plt.title('Result Test number 3.'+str(i))
	#plt.xlabel('Time(s/pixel)')
	#plt.show()



plt.plot(xrange(2,9), means_r)
plt.title('Scalability test : means per core random access')
plt.ylabel('Mean time (s/pixel)')
plt.xlabel('Number of cores')
plt.show()

plt.plot(xrange(2,9), means_s)
plt.title('Scalability test : means per core square access')
plt.ylabel('Mean time (s/pixel)')
plt.xlabel('Number of cores')
plt.show()

plt.plot(xrange(2,9), means_s, 'r-')
plt.plot(xrange(2,9), means_r, 'b-')
plt.title('Scalability test : means per core ')
plt.ylabel('Mean time (s/pixel)')
plt.xlabel('Number of cores')
plt.show()




