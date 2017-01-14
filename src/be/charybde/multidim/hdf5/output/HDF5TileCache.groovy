package be.charybde.multidim.hdf5.output;

import ch.systemsx.cisd.base.mdarray.MDShortArray;
import ch.systemsx.cisd.hdf5.IHDF5Reader

import java.util.concurrent.Callable
import java.util.concurrent.Future;


/**
 * Created by laurent on 16.12.16.
 */
public class HDF5TileCache  {
    private int dim
    def cache
    private String name
    public HDF5TileCache(int dim, def name){
        this.cache = new ArrayList<MDShortArray>()
        this.dim = dim;
        this.name = name
    }

    def benchmark = { closure ->
        def start = System.currentTimeMillis()
        closure.call()
        def now = System.currentTimeMillis()
        now - start
    }


    public void extractValues(HDF5PxlReader reader){
        def tile_d = reader.getTileDepth()
        int nr_depth_tiles = dim / tile_d;

        ArrayList<Future> spectra =  []
        (0..nr_depth_tiles - 1).each { i ->
            spectra << reader.getThreadPool().submit({ ->
                MDShortArray arr
                def t1 = benchmark{
                    arr = reader.getReader(i).int16().readMDArray("/r"+i+"/"+name)
                }

                println "Thread "+ i + " Extr " + t1
                return  arr
            } as Callable)
        }

        spectra.each { res ->
            cache << res.get()
        }
    }


    def getPixelInCache(int x, int y){
        def res = []
        cache.each{ cc ->
            for(int i=0; i  < 256; ++i){
                res << cc.get(x%256,y%256,i)
            }
        }
        return res
    }
}
