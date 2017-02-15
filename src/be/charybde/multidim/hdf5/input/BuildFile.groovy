package be.charybde.multidim.hdf5.input

import ch.systemsx.cisd.base.mdarray.MDShortArray
import ch.systemsx.cisd.hdf5.HDF5Factory
import ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures
import ch.systemsx.cisd.hdf5.IHDF5Writer

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Created by laurent on 18.12.16.
 */

public class BuildFile {
    String filename; //Without extention
    final String extention = ".h5"
    int cube_width, cube_height, cube_depth, memory;
    ExtractData ed;
    IHDF5Writer writer;
    HDF5IntStorageFeatures ft;
    def to_write_array = []
    def to_write_names = []
    int max_cube_x, max_cube_y
    int x_start, y_start


    //This is just to debug
    //def HashMap<String, Integer> already = new HashMap<>()
    def benchmark = { closure ->
        def start = System.currentTimeMillis()
        closure.call()
        def now = System.currentTimeMillis()
        now - start
    }

    public void createFromPartOfImage(int cores, boolean divide_source = true) {}
    public void createFile(int coco, boolean divide = true) {}

    private void writeIntoDisk(){
        if(to_write_array.size() <= 0 )
            return;
        0.upto(to_write_array.size() - 1,{ i ->
                writer.int16().writeMDArray(to_write_names[i], to_write_array[i], ft)
        })
        to_write_names = new ArrayList<String>()
        to_write_array = new ArrayList<MDShortArray>()
        println "Done"
    }


}
