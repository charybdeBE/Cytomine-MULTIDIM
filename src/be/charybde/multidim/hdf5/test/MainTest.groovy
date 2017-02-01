package be.charybde.multidim.hdf5.test

/**
 * Created by laurent on 01.02.17.
 */


def tester = new HDF5Test()
tester.initTest("/home/laurent/cyto_dev/Cytomine-MULTIDIM/test333.h5")
tester.testCube(15653, 11296)
