/**
 * Created by laurent on 21.12.16.
 */

import be.charybde.multidim.hdf5.input.*
import be.charybde.multidim.hdf5.output.HDF5Pixel
import ch.systemsx.cisd.hdf5.HDF5Factory;

def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}

/*
//Read
def fp = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/ok1.4.h5"
def reader = HDF5Factory.openForReading(fp)
def it = 0
def pxl = [ new HDF5Pixel(0,0,4), new HDF5Pixel(0,10,4), new HDF5Pixel(5000,500,4), new HDF5Pixel(250,2500,4)]
pxl.each { pp ->
    ++it
    def v
    def tt = benchmark{ v= pp.getValues(reader)}
    print it + " ( " +  tt + ") : "
    v.each { val ->
        print val + " "
    }
    println " "
}



*/
/*
//Read
def fp = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/ok1.4.h5"
def reader = HDF5Factory.openForReading(fp)
def it = 0
def pxl = [ new HDF5Pixel(0,0,4), new HDF5Pixel(0,10,4), new HDF5Pixel(5000,500,4), new HDF5Pixel(250,2500,4)]
pxl.each { pp ->
    ++it
    def v
    def tt = benchmark{ v= pp.getValues(reader)}
    print it + " ( " +  tt + ") : "
    v.each { val ->
        print val + " "
    }
    println " "
}



*/

def fn = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/test.h5"
def dir = "/home/laurent/sample/1-6/"
def files = ["1.jpg", "2.jpg", "3.jpg", "4.jpg"]


def worker = new BuildFile(fn, dir, files)

worker.createFile()