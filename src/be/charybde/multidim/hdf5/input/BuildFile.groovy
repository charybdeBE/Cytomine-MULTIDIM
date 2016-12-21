package be.charybde.multidim.hdf5.input

import ch.systemsx.cisd.base.mdarray.MDShortArray
import ch.systemsx.cisd.hdf5.HDF5Factory
import ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures
import ch.systemsx.cisd.hdf5.IHDF5Writer

/**
 * Created by laurent on 18.12.16.
 */

//NOTE for future me : a Build file can work easily in //, one juste need to have several extract data instances with different set of file for exemple
    // and we can write in HDF5 at several olace at once (not really but theroetically)
//Note bis : x coordinate represent the heigth and y the width
//TODO do not write on disk each time we extract a tile, wait for the ram to be full

public class BuildFile {
    private String filename;
    private int tile_width, tile_height, tile_depth;
    private ExtractData ed;
    private IHDF5Writer writer;
    private HDF5IntStorageFeatures ft;

    public BuildFile(String filename, int tile_width, int tile_height, int tile_depth, String root, def ex) {
        this.filename = filename;
        this.tile_width = tile_width;
        this.tile_height = tile_height;
        this.tile_depth = tile_depth;
        this.ed = new ExtractDataImageIO(root, ex);
        this.writer = HDF5Factory.open(filename);
        this.ft = HDF5IntStorageFeatures.createDeflationUnsigned(HDF5IntStorageFeatures.MAX_DEFLATION_LEVEL);
    }

    public BuildFile(String filename, String root, def ex) {
        this(filename, 256,256,256, root, ex);
    }


    public void createFile(){
        int nr_width = (ed.getImageWidth() / tile_width) + 1
        int nr_height = (ed.getImageHeight() / tile_height) + 1
        int nr_depth = (ed.getImageDepth() / tile_depth) + 1

        String meta_group = "/meta";
        int[] meta_info = [tile_width, tile_depth, tile_height];
        writer.int32().writeArray(meta_group, meta_info, ft);

        println nr_width +"," + nr_height + "," + nr_depth
        for(int d = 0; d < nr_depth; d++){
            for(int x = 0; x < nr_width; x++){
                for(int y = 0; y < nr_height; y++){
                    long start =  System.currentTimeMillis();
                    String group_name = "/r"+d+"/t"+x+"_"+y+"";
                    MDShortArray actual_tile = ed.extractTile(x,y,d,tile_width,tile_height, tile_depth);
                    writer.int16().writeMDArray(group_name,  actual_tile,ft);
                    long stop =  System.currentTimeMillis() - start;
                    println("Tile done : " + group_name + " in (ms) " + stop);

                }
            }
        }


    }



}
