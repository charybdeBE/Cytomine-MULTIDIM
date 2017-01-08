package be.charybde.multidim.hdf5.output

import ch.systemsx.cisd.hdf5.HDF5Factory
import ch.systemsx.cisd.hdf5.IHDF5Reader
import sun.nio.ch.ThreadPool

import java.util.concurrent.Executors

/**
 * Created by laurent on 07.01.17.
 */
class HDF5PxlReader {
    private String name
    def private relatedFilenames
    private ArrayList<IHDF5Reader> readers
    def private tile_width, tile_height, tile_depth
    private ThreadPool tp
    private int dimensions

    public HDF5PxlReader(String name) {
        this.name = name
        def script = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/relatedFiles.sh"
        def stringScript = "" + script + " " + name
        def retScript = stringScript.execute().text
        retScript = retScript.replace("\n", "")
        relatedFilenames = retScript.split(",")

        relatedFilenames.each {
            readers << new HDF5Factory().openForReading(it)
        }

        String meta_group = "/meta";
        int[] meta = readers[0].int32().readArray(meta_group);
        tile_width = meta[0]
        tile_height = meta[1]
        tile_depth = meta[2]
        dimensions = relatedFilenames.size() * tile_depth //Note this is only ok if we have one file per tile depth
        this.tp = Executors.newFixedThreadPool(8)

    }


    HDF5Geometry extractSpectra(int x, int y) {
        def pp = new HDF5Pixel(x,y, dimensions)
        extractSpectra(pp)
        return pp
    }

    HDF5Geometry extractSpectra(int x, int y, int wid, int hei){
        def rec = new HDF5Rectangle(x,y,wid,hei, dimensions)
        extractSpectra(rec)
        return rec
    }

    HDF5Geometry extractSpectra(int x, int y, int size){
        return extractSpectra(x,y,size,size)
    }

    HDF5Geometry extractSpectra(HDF5Geometry pxl) {
        pxl.extractValues(this)
        return  pxl
    }

    IHDF5Reader getReader(int i){
        assert i >= 0
        if(readers.size() <= i)
            return null
        return readers[i]
    }

    def getTileWidth() {
        return tile_width
    }

    def getTileHeight() {
        return tile_height
    }

    def getTileDepth() {
        return tile_depth
    }

    public ThreadPool getThreadPool(){
        return tp
    }
}


