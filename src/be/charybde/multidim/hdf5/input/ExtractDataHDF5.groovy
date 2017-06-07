package be.charybde.multidim.hdf5.input

import ch.systemsx.cisd.base.mdarray.MDShortArray
import ch.systemsx.cisd.hdf5.HDF5Factory
import ch.systemsx.cisd.hdf5.IHDF5Reader

/**
 * Created by laurent on 10.02.17.
 */
class ExtractDataHDF5 extends ExtractData{
    IHDF5Reader reader;
    def width, height
    int[] cube_dimensions
    String generic_filename

    ExtractDataHDF5(String filename, width, height, divide = true) {
        this.generic_filename = filename
        if(!divide)
            this.reader = HDF5Factory.openForReading(filename + ".h5")
        else
            this.reader = HDF5Factory.openForReading(filename + ".0.h5")
        this.width = width
        this.height = height

        int[] meta = reader.int32().readArray("/meta");
        this.cube_dimensions = meta
    }


    void getImage(int i){
        def fn = generic_filename + "." + i + ".h5"
        println fn
        this.reader = HDF5Factory.openForReading(fn)
    }


    MDShortArray extractCube(int startX, int startY, int wid, int hei, int startDepth){
        def mdcubes = []
        int x_cube = startX / cube_dimensions[0];
        int y_cube = startY / cube_dimensions[1];
        int x_cube_end = (startX + wid) / cube_dimensions[0]
        int y_cube_end = (startY + hei) / cube_dimensions[1]
        def dep = "/r" + startDepth / this.cube_dimensions[2]


        //If we work only with one cube we can go faster than build and debuild the rebuild MD array
        if(x_cube - x_cube_end == 0 && y_cube - y_cube_end == 0) {
            long x_in_cube = startX % cube_dimensions[0]
            long y_in_cube = startY % cube_dimensions[1]
            String cube_name = dep + "/t" + x_cube + "_" + y_cube

            return reader.int16().readMDArrayBlockWithOffset(cube_name, [wid, hei, cube_dimensions[2]] as int[], [x_in_cube, y_in_cube, 0] as long[])

        }

        MDShortArray toRet = new MDShortArray([wid, hei, cube_dimensions[2]] as int[])

        int wid_left = wid
        int hei_left = hei
        def x = 0

        (x_cube..x_cube_end).eachWithIndex { int xx, int iX ->
            def y = 0
            long x_in_cube = Math.max(startX, xx * cube_dimensions[0]) % cube_dimensions[0]
            int wid_in_cube = Math.min(wid_left, cube_dimensions[0] - x_in_cube)
            wid_left -= wid_in_cube

            (y_cube..y_cube_end).eachWithIndex { int yy, int iJ ->
                String cube_name =  dep + "/t" + xx + "_" + yy
                long y_in_cube = Math.max(startY, yy * cube_dimensions[1]) % cube_dimensions[1]
                int hei_in_cube = Math.min(hei_left, cube_dimensions[1] - y_in_cube)
                hei_left -= hei_in_cube

                //Todo //
                def cube = reader.int16().readMDArrayBlockWithOffset(cube_name, [wid_in_cube, hei_in_cube, cube_dimensions[2]] as int[], [x_in_cube, y_in_cube, 0] as long[])
                for (int i = 0; i < wid_in_cube; i++) {
                    for (int j = 0; j < hei_in_cube; j++) {
                        for (int k = 0; k < cube_dimensions[2]; k++) { //TODO bug when last image
                            toRet.set(cube.get(i,j,k), x+i, y+j, k)
                        }
                    }
                }
                y += hei_in_cube
            }
            x += wid_in_cube
        }


        toRet
    }

    @Override
    int getImageWidth() {
        this.width
    }

    @Override
    int getImageHeight() {
        this.height
    }

    @Override
    int getImageDepth() {
        1650 //TODO
        //this.cube_dimensions[2]
    }

}
