package be.charybde.multidim.hdf5.input

import ch.systemsx.cisd.hdf5.HDF5Factory
import ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures

import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Created by laurent on 15.02.17.
 */
class BuildFileHDF5 extends BuildFile{

    public BuildFileHDF5(String filename, String original_filename, int x_start, int y_start, int width, int height, int burst){
        this.filename = filename
        this.memory = burst
        this.ed = new ExtractDataHDF5(original_filename, width, height, true)

        this.cube_width  = Math.min(width, 256)
        this.cube_height  = Math.min(height, 256)
        this.cube_depth  = Math.min(this.ed.getImageDepth(), 256)


        this.max_cube_x = width / cube_width
        if(width % cube_width)
            max_cube_x++
        this.max_cube_y = height / cube_height
        if(height % cube_height)
            max_cube_y++

        println cube_width + " " + cube_height + " "  + cube_depth + " " + max_cube_x + " " + max_cube_y

        this.writer = HDF5Factory.open(this.filename + this.extention);
        this.ft = HDF5IntStorageFeatures.createDeflationUnsigned(HDF5IntStorageFeatures.MAX_DEFLATION_LEVEL);

        this.x_start = x_start
        this.y_start = y_start

    }

    public void createFromPartOfImage(int cores, boolean divide_source = true){
        //TODO //
        def threadPool = Executors.newFixedThreadPool(cores)

        int nrDepthCube = (int) (ed.getImageDepth() / cube_depth)
        if(ed.getImageDepth() % cube_depth != 0)
            nrDepthCube++

        int x, y,i, d
        String meta_group = "/meta";
        int[] meta_info = [cube_width, cube_height, cube_depth];

        for(d = 0; d < nrDepthCube; ++d) {
            this.ed.getImage(d)
            println "Depth " + d

            writer.int32().writeArray(meta_group, meta_info, ft);
            def dim = d * cube_depth
            x = x_start
            def writeFuture = threadPool.submit({} as Callable) //initialisation of a future
            for (i = 0; i < max_cube_x; i++) {
                y = y_start
                for (int j = 0; j < max_cube_y; j++) { //TODO separate into bursts
                    println "/r" + d + "/t" + i + "_" + j
                    to_write_names << "/r" + d + "/t" + i + "_" + j
                    to_write_array << ed.extractCube(x, y, cube_width, cube_height, dim)
                    writeIntoDisk()
                    y += cube_height
                }
                x += cube_width
            }

        }

        writer.close()
        threadPool.shutdown()
    }


}
