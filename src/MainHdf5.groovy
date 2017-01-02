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


//Read
/*def fp = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/test6.h5"
def dd = 6
def reader = HDF5Factory.openForReading(fp)
def it = 0
def pxl = [ new HDF5Pixel(0,0,dd), new HDF5Pixel(0,1,dd), new HDF5Pixel(5000,500,dd), new HDF5Pixel(250,2500,dd)]
pxl.each { pp ->
    ++it
    def v
    def tt = benchmark{ v= pp.getValues(reader)}
    print it + " (" +  tt + " ms) : "
    v.each { val ->
        print val + " "
    }
    println " "
}

reader.close()*/


def script = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/listOfFile.sh"
def fn = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/testNNew.h5"
def fn2 = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/testNold.h5"
def dir = "/home/laurent/sample/1-6/"

def stringScript = "" + script + " " + dir
def retScript = stringScript.execute().text
def files = retScript.split(",")


def tt = benchmark {
    def worker = new BuildFile(fn2, dir, files)

    worker.createParr(8)
}
tt /= 1000

println "Time for // " + tt + "(s)"

