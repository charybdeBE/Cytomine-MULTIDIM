/**
 * Created by laurent on 21.12.16.
 */

import be.charybde.multidim.hdf5.input.*
import be.charybde.multidim.hdf5.output.HDF5Geometry
import be.charybde.multidim.hdf5.output.HDF5Pixel
import be.charybde.multidim.hdf5.output.HDF5PxlReader
import ch.systemsx.cisd.hdf5.HDF5Factory;

def rand = new Random()


def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}




def script = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/listOfFile.sh"
def fn2 = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/test1650"
def dir = "/media/laurent/APOLLOII/TFE/22-LT2-26-01-2011-01-Bruegel/LAM1650/LAM1650HD/"
/*

def stringScript = "" + script + " " + dir
def retScript = stringScript.execute().text
def files = retScript.split("\n")

println "Number of files : " +files.size()


def tt = benchmark {
    def worker = new BuildFile(fn2, dir, files)

    worker.createParr(8)
}
tt /= 1000

println "Time for // " + tt + "(s)"
*/

def randomPair = {
    def x = rand.nextInt(15653)
    def y = rand.nextInt(11296)
    [x,y]
}

def reader = new HDF5PxlReader(fn2)
def pxl = []
def coo = [[0,0],[10,10],[5,5],[500,500],[501,501]]
def times = []
/*
0.upto(5,{
    def cord = randomPair.call()
    coo << cord
    times << benchmark{
        pxl << reader.extractSpectraPixel(cord)
    }
})*/

coo.each {cord ->
    times << benchmark{
        pxl << reader.extractSpectraPixel(cord)
    }
}

reader.close()

def i = 0
times.each { fly ->
    fly /= 1000
    println "It has taken " + fly + " (s) for number " + i + " " + coo[i % coo.size()] + " Control  size : " + pxl[i].getValues().size()
    ++i
}

