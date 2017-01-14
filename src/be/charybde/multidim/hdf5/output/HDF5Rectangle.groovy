package be.charybde.multidim.hdf5.output

import ch.systemsx.cisd.base.mdarray.MDShortArray

import java.util.concurrent.Callable
import java.util.concurrent.Future

/**
 * Created by laurent on 08.01.17.
 */
class HDF5Rectangle implements HDF5Geometry{
    private int x, y, dim, wid, hei


    HDF5Rectangle(int x, int y, int wid, int hei, int dim) {
        this.x = x
        this.y = y
        this.dim = dim
        this.wid = wid
        this.hei = hei
    }

    @Override
    def getDataFromCache(Object array) {
        return null
    }

    void extractValues(HDF5PxlReader reader) {
        if(isDataPresent())
            return;
        def data = []
        def tiles = []
        def tile_d = reader.getTileDepth()
        def tile_w = reader.getTileWidth()
        def tile_h = reader.getTileHeight()

        int[] blockDimensions  //We want to get the complete info of the whole pxl

        long[] blockNumber
        int nr_depth_tiles = dim / tile_d;
        int x_tile = x / tile_w;
        int y_tile = y / tile_h;
        def x_tile_end = (x + wid) / tile_w
        def y_tile_end = (y + wid) / tile_w

        (x_tile..x_tile_end).each { xx ->
            (y_tile..y_tile_end).each { yy ->
                tiles << "/t"+xx+"_"+yy
            }
        }

        ArrayList<Future> spectra =  []
        def k = 0
        tiles.each {
            if(tiles.size() == 1)   {
                int x_nr = (x %tile_w) / wid
                int y_nr = (y %tile_h) / hei
                blockDimensions = [wid,hei, tile_d]
                blockNumber = [x_nr, y_nr,0];
            }
            else{
                if(k == 0){
                    int stock_x = (tile_w - wid % tile_w)
                    int stock_y = (tile_h - hei % tile_h)
                    int x_nr = (x % stock_x) / stock_x
                    int y_nr = (y % stock_y) / stock_y
                    blockDimensions = [stock_x, stock_y, tile_d]
                    blockNumber = [x_nr, y_nr, 0]
                }
                else if(k == tiles.size() - 1){
                    int stock_x = (tile_w - wid % tile_w) % tile_w //Factorisation ?
                    int stock_y = (tile_h- hei % tile_h) % tile_h
                    int x_nr = (x % stock_x) / stock_x
                    int y_nr = (y %stock_y) / stock_y
                    blockDimensions = [stock_x, stock_y, tile_d]
                    blockNumber = [x_nr, y_nr, 0]
                }
                else{
                    blockDimensions = [ tile_w, tile_h, tile_d]
                    blockNumber = [0,0,0]
                }
            }
            (0..nr_depth_tiles-1).each { i ->
                spectra << reader.getThreadPool().submit({ ->
                    String path = "/r" + i + "/t" + x_tile + "_" + y_tile
                    MDShortArray arr = reader.getReader(i).int16().readMDArrayBlock(path, blockDimensions, blockNumber);
                    arr.getAsFlatArray()
                } as Callable)
            }
            ++k
        }


        spectra.each {
            data + it.get() //sometitng to order the array ? or make clear correspodance, maybe a map
        }

        setData(data)
    }

    @Override
    def getDim() {
        return [x,y,dim,wid,hei]
    }
}
