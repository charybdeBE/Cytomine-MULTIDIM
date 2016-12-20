package be.charybde.multidim.hdf5.output;

import ch.systemsx.cisd.base.mdarray.MDShortArray;
import ch.systemsx.cisd.hdf5.IHDF5Reader;


/**
 * Created by laurent on 16.12.16.
 */
public class HDF5Pixel {
    private int x,y,dim;
    private String path;
    private Boolean extract; //This flag means that the data is in the ram
    private ArrayList<Short> data;


    //This constructor is use for reading
    public HDF5Pixel(int x, int y, int dim, String path) {
        this(x,y,dim);
        this.extract = false;
        this.data = new ArrayList<>();
        this.path = path;
    }

    //Base constructor
    public HDF5Pixel(int x, int y, int dim){
        this.x = x;
        this.y = y;
        this.dim = dim;
    }


    //If we are sure that data is present we can use this. TODO make it throwing
    public List<Short> getValues(){
        if(extract)
            return data;
        return null;
    }

    public List<Short> getValues(IHDF5Reader reader){
        if(extract)
            return data;

        String meta_group = "/meta";
        int[] meta = reader.int32().readArray(meta_group);

        int[] blockDimensions = [1,1,meta[2]]; //We want to get the complete infosof 1 pxl
        long[] blockNumber = [x,y,0];
        ArrayList<Short> result = new ArrayList<>();
        int nr_depth_tiles = dim / meta[2];
        int x_tile = x / meta[0];
        int y_tile = y / meta[0];

        for(int i=0; i<nr_depth_tiles; ++i) {
            String actual_path = "r" + i + "/t" + x_tile + "_" + y_tile;
            MDShortArray arr = reader.int16().readMDArrayBlock(actual_path, blockDimensions, blockNumber);
            short[] flat =  arr.getAsFlatArray();
            //TODO store the result in the list
        }

        this.extract = true;
        this.data = result;

        return result;
    }

    public void addDimData(int index, short value){
        this.data.add(index, value);
    }

    public void addDimData(short value){
        this.data.add(value);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
