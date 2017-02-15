/**
 * Created by laurent on 21.12.16.
 */

import be.charybde.multidim.hdf5.input.*

def rand = new Random()


def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}




def script = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/listOfFile.sh"
def fn2 = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/test1650"
def dir = "/home/laurent/sample/1-6/"

def build = new BuildFileHDF5("/home/laurent/cyto_dev/Cytomine-MULTIDIM/part2", fn2, 1, 1, 200, 500, 10)
build.createFromPartOfImage(4, true)