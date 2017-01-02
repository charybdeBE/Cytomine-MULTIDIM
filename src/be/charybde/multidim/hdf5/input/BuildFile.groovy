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

//NOTE for future me : a Build file can work easily in //, one juste need to have several extract data instances with different set of file for exemple
    // and we can write in HDF5 at several olace at once (not really but theroetically)
//Note bis : x coordinate represent the heigth and y the width

public class BuildFile {
    private String filename;
    private int tile_width, tile_height, tile_depth, memory;
    private ExtractData ed;
    private IHDF5Writer writer;
    private HDF5IntStorageFeatures ft;
    def ArrayList<MDShortArray> to_write_array = []
    def ArrayList<String> to_write_names = []

    //This is just to debug
    def benchmark = { closure ->
        def start = System.currentTimeMillis()
        closure.call()
        def now = System.currentTimeMillis()
        now - start
    }



    public BuildFile(String filename, int tile_width, int tile_height, int tile_depth, String root, def ex, def brustSize) {
        this.filename = filename;
        this.tile_width = tile_width;
        this.tile_height = tile_height;
        def dimension = ex.size()
        if(dimension < tile_depth)
            this.tile_depth = dimension
        else
            this.tile_depth = tile_depth;
        this.memory = brustSize //Represent the nr of tile we store into memory before writing

        this.ed = new ExtractDataImageIO(root, ex);
        this.writer = HDF5Factory.open(filename);
        this.ft = HDF5IntStorageFeatures.createDeflationUnsigned(HDF5IntStorageFeatures.MAX_DEFLATION_LEVEL);
        println "My lfe is potato"
    }

    public BuildFile(String filename, String root, def ex) {
        this(filename, 256,256,256, root, ex, 40);
    }


    public void createFile(){
        String meta_group = "/meta";
        int[] meta_info = [tile_width, tile_height, tile_depth];
        writer.int32().writeArray(meta_group, meta_info, ft);

        def ret  = [0,0,0]

        def i = 0
        int nrB = ((ed.getImageWidth() / tile_width) * (ed.getImageHeight() / tile_height) * (ed.getImageDepth() / tile_depth))  / memory
        while(ret[0] < ed.getImageWidth() || ret[2] < ed.getImageDepth()){
            def time = benchmark {ret = extractBurst(ret[0], ret[1], 0 ) }
            def time2 = benchmark {                writeIntoDisk() }
            ++i
            time /= 1000
            time2 /= 1000
            println("("+i+"/"+nrB+") : reading : " + time  + "(s) + writing : " + time2 + " (s) " + ret[0] + " " + ret[1]  + " " + ret[2] )

        }

        writer.close()
    }


    //Return an array with the next coordinate to do
    private int[] extractBurst(int startX, int startY, int startD){
        def xx, yy, x,y,d,dd
        d = (int) (startD / tile_depth)
        int limit = startD + tile_depth
        if(limit > ed.getImageDepth())
            limit = ed.getImageDepth()

        for ( dd = startD; dd < limit; ++dd) {
            ed.getImage(dd)
            x = (int) (startX / tile_width)
            y = (int) (startY / tile_height)
            xx = startX
            yy = startY

            for (def i = 0; i < memory; ++i) {
                if (dd % tile_depth == 0) {
                    to_write_names << "/r" + d + "/t" + x + "_" + y + "";
                    to_write_array << ed.extract2DTile(xx, yy, tile_width, tile_height, tile_depth)
                } else {
                    to_write_array[i] = ed.extract2DTile(xx, yy, dd, tile_width, tile_height, to_write_array[i])
                }


                yy += tile_height
                y++
                if (yy >= ed.getImageHeight()) {
                    yy = 0
                    y = 0
                    x++
                    if (xx != ed.getImageWidth()){
                        xx += tile_width
                        if(xx > ed.getImageWidth())
                            xx = ed.getImageWidth()
                    }
                    else
                        break
                }

            }
        }

        return [xx, yy, dd]

    }


    private void writeIntoDisk(){
        if(to_write_array.size() <= 0 )
            return;
        0.upto(to_write_array.size() - 1,{ i ->
                writer.int16().writeMDArray(to_write_names[i], to_write_array[i], ft)
        })
        to_write_names = new ArrayList<>()
        to_write_array = new ArrayList<>()
    }


    public void createParr(int cores){
        String meta_group = "/meta";
        int[] meta_info = [tile_width, tile_height, tile_depth];
        writer.int32().writeArray(meta_group, meta_info, ft);

        def ret  = [0,0,0]
        def threadPool = Executors.newFixedThreadPool(cores)
        def names = new ArrayList<ArrayList<String>>()
        def vals = new ArrayList<ArrayList<MDShortArray>>()
        (1..cores).each {
            names << new ArrayList<String>()
            vals << new ArrayList<MDShortArray>()
        }


        int nrB = ((ed.getImageWidth() / tile_width) * (ed.getImageHeight() / tile_height) * (ed.getImageDepth() / tile_depth))  / memory

        int xx, yy, x,y, xxx, yyy
        x = 0
        y = 0
        for(int i=0; i < nrB; i += cores){
            def arrRet = new ArrayList<Future>()

            def time = benchmark {
                for (int d = 0; d < tile_depth; d++) {
                    xx = x
                    yy = y
                    ed.getImage(d)
                    (1..4).each { k ->
                            arrRet << threadPool.submit({ -> work(xx, yy, d, memory, names[k-1], vals[k-1]) } as Callable)
                            def d1 = memory * tile_width * tile_height
                            yy = (yy + d1) % ed.getImageHeight()
                            xx += (d1 / ed.getImageWidth())

                    }
                    arrRet.each { it.get() }
                    xxx = xx
                    yyy = yy
                }
            }
            def time2 = benchmark {
                to_write_array = vals.flatten();
                to_write_names = names.flatten();
                writeIntoDisk()
                /*names = new ArrayList<ArrayList<String>>()
                 vals = new ArrayList<ArrayList<MDShortArray>>()*/
                (0..cores - 1).each {
                    names[it] =  new ArrayList<String>()
                    vals[it] =  new ArrayList<MDShortArray>()
                }
            }
            time /= 1000
            time2 /= 1000
            println("("+i+"/"+nrB+") : reading : " + time  + "(s) + writing : " + time2 + " (s) " )
            x = xxx
            y = yyy
        }


        writer.close()
    }

    def work = { int startX, int startY, int d, int nr, ArrayList<String> names, ArrayList<MDShortArray> arrs ->
        def x, y
        x = startX
        y = startY
        def ret = []
        ret = extract2DBurst(x,y, d, names, arrs)
        x = ret[0]
        y = ret[1]
        return ret
    }

    public int[] extract2DBurst(int startX, int startY, int startD, ArrayList<String> names, ArrayList<MDShortArray> arrs){
        def xx, yy, x,y,d
        d = (int) (startD / tile_depth)
        x = (int) (startX / tile_width)
        y = (int) (startY / tile_height)
        xx = startX
        yy = startY
        for (def i = 0; i < memory; ++i) {
            if(names == null){
                if(arrs == null)
                    println "Error on " + startX + " " + startY
                else
                    println "Error in " + startX + " " + startY
            }
            if (startD % tile_depth == 0) {
                names << "/r" + d + "/t" + x + "_" + y + "";
                arrs << ed.extract2DTile(xx, yy, tile_width, tile_height, tile_depth)
            } else {
                arrs[i] = ed.extract2DTile(xx, yy, startD, tile_width, tile_height, arrs[i])
            }


            yy += tile_height
            y++
            if (yy >= ed.getImageHeight()) {
                yy = 0
                y = 0
                x++
                if (xx != ed.getImageWidth()) {
                    xx += tile_width
                    if (xx > ed.getImageWidth())
                        xx = ed.getImageWidth()
                } else
                    break
            }
        }
        return [xx, yy]
    }

}
