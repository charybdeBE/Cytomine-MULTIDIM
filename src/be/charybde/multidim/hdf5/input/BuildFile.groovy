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
    private String filename; //Without extention
    private final String extention = ".h5"
    private int cube_width, cube_height, cube_depth, memory;
    private ExtractData ed;
    private IHDF5Writer writer;
    private HDF5IntStorageFeatures ft;
    def to_write_array = []
    def to_write_names = []
    private int max_cube_x, max_cube_y


    //This is just to debug
    //def HashMap<String, Integer> already = new HashMap<>()
    def benchmark = { closure ->
        def start = System.currentTimeMillis()
        closure.call()
        def now = System.currentTimeMillis()
        now - start
    }


    public BuildFile(String filename, int cube_width, int cube_height, int cube_depth, String root, def ex, def brustSize) {
        this.filename = filename;
        this.cube_width = cube_width;
        this.cube_height = cube_height;
        def dimension = ex.size()
        def fn
        if(dimension <= cube_depth){
            this.cube_depth = dimension
            fn = filename + extention
        }
        else  {
            this.cube_depth = cube_depth;
            fn = filename + ".0" + extention
        }
        this.memory = brustSize //Represent the nr of tile we store into memory before writing

        this.ed = new ExtractDataImageIO(root, ex);

        println " " + ed.getImageWidth()  + " "+ ed.getImageHeight()
        max_cube_x = ed.getImageWidth() / this.cube_width
        max_cube_y = ed.getImageHeight() / this. cube_height
        println "M " + max_cube_x + "  " + max_cube_y
        this.writer = HDF5Factory.open(fn);
        this.ft = HDF5IntStorageFeatures.createDeflationUnsigned(HDF5IntStorageFeatures.MAX_DEFLATION_LEVEL);
    }

    public BuildFile(String filename, String root, def ex) {
        this(filename, 256,256, 256, root, ex, 5);
    }



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



    public void createParr(int coco){
        def cores = coco  - 1
        def threadPool = Executors.newFixedThreadPool(coco * 2)
        def names = new ArrayList<ArrayList<String>>()
        def vals = new ArrayList<ArrayList<MDShortArray>>()
        (1..cores).each {
            names << new ArrayList<String>()
            vals << new ArrayList<MDShortArray>()
        }


        int nrB = (int) ((max_cube_y +1) * (max_cube_x + 1) / (memory * cores))
        if(((max_cube_y +1) * (max_cube_x + 1) % (memory * cores))) //Mb not
            nrB++

        int nrF = (int) (ed.getImageDepth() / cube_depth)
       if(ed.getImageDepth() % cube_depth != 0)
           nrF++


        int x, y,i, d
        for(d = 0; d < nrF; ++d){
            println "File " + d
            String meta_group = "/meta";
            int[] meta_info = [cube_width, cube_height, cube_depth];
            writer.int32().writeArray(meta_group, meta_info, ft);
            def startDim = d * cube_depth
            x = 0
            y = 0
            def writeFuture = threadPool.submit( {} as Callable) //initialisation of a future
            for( i=0; i <= nrB; i ++){
                def arrRet = new ArrayList<Future>()


                def res = extractBurstParr(cores, x, y, startDim, names, vals, arrRet, threadPool)
                def time2 = benchmark {
                    writeFuture.get()
                }
                to_write_array = vals.flatten();
                to_write_names = names.flatten();
                names = new ArrayList<ArrayList<String>>()
                vals = new ArrayList<ArrayList<MDShortArray>>()
                (0..cores - 1).each {
                    names[it] =  new ArrayList<String>()
                    vals[it] =  new ArrayList<MDShortArray>()
                }
                writeFuture = threadPool.submit({-> writeIntoDisk() } as Callable)

                def time = res[2] / 1000
                time2 /= 1000
                println("("+i+"/"+nrB+") : reading : " + time  + "(s) + writing late : " + time2 + " (s) " )
                println "Limits = "  + res
                x = res[0]
                y = res[1]
            }

            writer.close()
            if(d < nrF - 1)
                this.writer = HDF5Factory.open(filename+"."+(d+1)+""+extention);
        }


        threadPool.shutdown()
    }

    public int[] extractBurstParr(int cores, int cubeX, int cubeY, int startDim,  ArrayList<ArrayList<String>> names, ArrayList<ArrayList<MDShortArray>> vals, ArrayList<Future> arrRet, def tp){
        int[] nextXy
        def limit = startDim + cube_depth
        if(limit > ed.getImageDepth())
            limit = ed.getImageDepth()

        int time = benchmark {
            for (int d = startDim; d < limit; d++) {
                ed.getImage(d)

                (0..cores - 1).each { k ->

                    arrRet << tp.submit({ -> work(cubeX, cubeY, d,k, names[k], vals[k]) } as Callable)

                }
                arrRet.each { it.get() }
                arrRet = new ArrayList<Future>()
            }
        }

        nextXy = advanceCube(cubeX, cubeY, memory * cores)
        println "Next XY = "  + nextXy
        return [nextXy[0], nextXy[1], time]
    }


    def work = { int startX, int startY, int startD,int k, ArrayList<String> names, ArrayList<MDShortArray> arrs ->
        int inc = memory * k
        int[] xy = advanceCube(startX, startY, inc)

        def ret = extract2DBurst(xy[0],xy[1], startD, k, names, arrs)
        return ret
    }

    public int[] advanceCube(int x, int y, int inc){
        def retY = y + inc
        def retX = x
        while(retY > max_cube_y ){
            retY = retY - (max_cube_y + 1)
            retX++
        }
        return [retX, retY]
    }

    public int[] extract2DBurst(int startX_cube , int startY_cube, int startD, int k, ArrayList<String> names, ArrayList<MDShortArray> arrs){
        int d = (int) (startD / cube_depth)


        def cubeX = startX_cube
        def cubeY = startY_cube
        int xx, yy
        for (def i = 0; i < memory; ++i) {
            if(cubeX > max_cube_x)
                break
            xx = cubeX * cube_width
            yy = cubeY * cube_height

            if (startD % cube_depth == 0) {
                names << "/r" + d + "/t" + cubeX + "_" + cubeY + "";
                /*if(already.containsKey(names[i]))
                    println names[i] + " in " + already.get(names[i]) + " and " + k
                else
                    already.put(names[i], k)
                */
                arrs << ed.extract2DCube(xx, yy, cube_width, cube_height, cube_depth)
            } else {
                arrs[i] = ed.extract2DCube(xx, yy, startD % cube_depth, cube_width, cube_height, arrs[i])
            }


            cubeY++
            if(cubeY > max_cube_y){
                cubeY = cubeY - (max_cube_y +1)
                cubeX++
            }

        }
        return [cubeX, cubeY]
    }

}
