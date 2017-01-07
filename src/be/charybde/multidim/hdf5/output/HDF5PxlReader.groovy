package be.charybde.multidim.hdf5.output

import ch.systemsx.cisd.hdf5.HDF5Factory
import ch.systemsx.cisd.hdf5.IHDF5Reader

/**
 * Created by laurent on 07.01.17.
 */
class HDF5PxlReader {
    private String name
    def private relatedFilenames
    def private reader //could be an array ?
    def private tile_width, tile_height, tile_depth
     private int dimensions

    public HDF5PxlReader(String name) {
        this.name = name
        def script = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/relatedFiles.sh"
        def stringScript = "" + script + " " + name
        def retScript = stringScript.execute().text
        retScript = retScript.replace("\n", "")
        relatedFilenames = retScript.split(",")

        reader = new HDF5Factory().openForReading(relatedFilenames[0])
        String meta_group = "/meta";
        int[] meta = reader.int32().readArray(meta_group);
        tile_width = meta[0]
        tile_height = meta[1]
        tile_depth = meta[2]
        dimensions = relatedFilenames.size() * tile_depth //Note this is only ok if we have one file per tile depth
    }


    short[] getSpectra(int x, int y) {
        def pp = new HDF5Pixel(x,y, dimensions)
        return getSpectra(pp)
    }

    short[] getSpectra(HDF5Pixel pxl) {
        pxl.extractValues(this)
        return  pxl.getValues()
    }

    IHDF5Reader getReader(){
        return  reader
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
}


