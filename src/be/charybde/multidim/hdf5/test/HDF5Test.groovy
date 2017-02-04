package be.charybde.multidim.hdf5.test

import ch.systemsx.cisd.hdf5.HDF5Factory
import ncsa.hdf.hdf5lib.exceptions.HDF5SymbolTableException

/**
 * Created by laurent on 01.02.17.
 */

//Those test are here to test the file created
class HDF5Test{

    def readers
    def cube_size

    def initTest(String filename){
        readers = new HDF5Factory().openForReading(filename)
        cube_size = [256,256,256]
    }

    def isCubePresent(int x, int y){
        def cube_name = "/r0/t" + x + "_" + y
        try{
            def res = readers.int16().readMDArray(cube_name)
        }
        catch(HDF5SymbolTableException e) {
                println "cube_name " + cube_name + " is not there"
                return false
            }

        return true
    }

    def testCube(int maxX, int maxY){
        int maxX_cube = maxX / cube_size[0]
        int maxY_cube = maxY / cube_size[1]

        for (int i = 0; i <= maxX_cube ; i++) {
            for (int j = 0; j <= maxY_cube ; j++) {
                 assert isCubePresent(i,j) == true
            }
        }

    }
}