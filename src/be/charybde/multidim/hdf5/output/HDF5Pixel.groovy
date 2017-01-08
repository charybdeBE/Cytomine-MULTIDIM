package be.charybde.multidim.hdf5.output;

import ch.systemsx.cisd.base.mdarray.MDShortArray;
import ch.systemsx.cisd.hdf5.IHDF5Reader

import java.util.concurrent.Callable
import java.util.concurrent.Future;


/**
 * Created by laurent on 16.12.16.
 */
public class HDF5Pixel implements  HDF5Geometry {
    private int x,y,dim;
    //Base constructor
    public HDF5Pixel(int x, int y, int dim){
        this.x = x;
        this.y = y;
        this.dim = dim;
    }

    public void extractValues(HDF5PxlReader reader){
        if(isDataPresent())
            return;

        def tile_d = reader.getTileDepth()
        def tile_w = reader.getTileWidth()
        def tile_h = reader.getTileHeight()

        int[] blockDimensions = [1,1,tile_d]; //We want to get the complete info of 1 pxl
        long[] blockNumber = [x % tile_w ,y %tile_h,0];
        int nr_depth_tiles = dim / tile_d;
        int x_tile = x / tile_w;
        int y_tile = y / tile_h;


        def data = []
        ArrayList<Future> spectra =  []
        (1..nr_depth_tiles).each { i ->
            spectra << reader.getThreadPool().submit({ ->
                String path = "/r" + i + "/t" + x_tile + "_" + y_tile
                MDShortArray arr = reader.getReader(i).int16().readMDArrayBlock(path, blockDimensions, blockNumber);
                arr.getAsFlatArray()
            } as Callable)
        }

        spectra.each {
            data + it.get()
        }

        setData(data)

    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    def getDim(){
        return [x,y,dim]
    }
}
