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

n, bins, patches = plt.hist(list_value, 20)
plt.title('Result Test number 1')
plt.xlabel('Time (s/pixel)')
plt.savefig('hist_test1.png', format = 'png')
plt.show()


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


n, bins, patches = plt.hist(list_value, 20)
plt.title('Result Test number 2')
plt.xlabel('Time(s/pixel)')
plt.savefig('hist_test2.png', format = 'png')
plt.show()


