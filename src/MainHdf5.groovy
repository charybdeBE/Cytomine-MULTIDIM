/**
 * Created by laurent on 21.12.16.
 */

import be.charybde.multidim.hdf5.input.*;

def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}

def fn = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/test.h5"
def dir = "/home/laurent/sample/1-6/"
def files = ["1.jpg", "2.jpg", "3.jpg", "4.jpg"]


def worker = new BuildFile(fn, dir, files)

worker.createFile()
