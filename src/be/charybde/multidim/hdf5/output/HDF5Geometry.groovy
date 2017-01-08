package be.charybde.multidim.hdf5.output

/**
 * Created by laurent on 08.01.17.
 */
interface HDF5Geometry {
    def getValues()
    def void extractValues(HDF5PxlReader p)
    def getDim()
}