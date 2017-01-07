package be.charybde.multidim.hdf5.output;

import ch.systemsx.cisd.base.mdarray.MDShortArray;
import ch.systemsx.cisd.hdf5.IHDF5Reader;


/**
 * Created by laurent on 16.12.16.
 */
public class HDF5Pixel {
    private int x,y,dim;
    private Boolean extract; //This flag means that the data is in the ram
    def private data
    //Base constructor
    public HDF5Pixel(int x, int y, int dim){
        this.x = x;
        this.y = y;
        this.dim = dim;
        this.data = []
    }


    //If we are sure that data is present we can use this. TODO make it throwing
    public List<Short> getValues(){
        if(extract)
            return data;
        return null;
    }

    protected void extractValues(HDF5PxlReader reader){
        if(extract)
            return data;

        def tile_d = reader.getTileDepth()
        def tile_w = reader.getTileWidth()
        def tile_h = reader.getTileHeight()

        int[] blockDimensions = [1,1,tile_d]; //We want to get the complete infosof 1 pxl
        long[] blockNumber = [x % tile_w ,y %tile_h,0];
        int nr_depth_tiles = dim / tile_d;
        int x_tile = x / tile_w;
        int y_tile = y / tile_h;


        //Todo //
        for(int i=0; i<nr_depth_tiles; ++i) {
            String actual_path = "/r" + i + "/t" + x_tile + "_" + y_tile;
            println(actual_path)
            MDShortArray arr = reader.getReader().int16().readMDArrayBlock(actual_path, blockDimensions, blockNumber);
            arr.getAsFlatArray().each { val ->
                data << val
            }
        }

        this.extract = true;

        return data;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
