/**
 * Created by laurent on 21.12.16.
 */

import be.charybde.multidim.hdf5.input.*;


def fn = "test.h5"
def dir = "/home/laurent/sample/1-6/"
def files = ["1.jpg", "2.jpg", "3.jpg"]


def worker = new BuildFile(fn, dir, files)

worker.createFile()
