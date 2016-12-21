package be.charybde.multidim.hdf5.input

import ch.systemsx.cisd.base.mdarray.MDShortArray

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.image.Raster

/**
 * Created by laurent on 17.12.16.
 */

public class ExtractDataImageIO extends ExtractData{
    private String directory;
    private int dim;
    private int actual_dim;
    def private filenames;

    private Raster ras;

    public ExtractDataImageIO(String d, def filenames){
        this.filenames = filenames;
        this.actual_dim = 0;
        this.dim = filenames.size();
        directory =d;
        try {
            String filename = directory + "/"  + filenames[0];
          //  System.out.println(filename);
            BufferedImage bf = ImageIO.read(new File(filename));
            ras = bf.getData();
        } catch (IOException e) {
            System.out.println("Not found");
        }
    }


    public int getImageWidth(){
        return ras.getWidth();
    }

    public int getImageHeight(){
        return ras.getHeight();
    }

    public int getImageDepth(){
        println this.dim
        return this.dim;
    }

    //Inititialise l'array en extractant image 0
    private MDShortArray createTile(int startX, int startY, int width, int height, int depth){
        long[] dims = [width, height, depth];
        MDShortArray result = new MDShortArray(dims);
        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j){
                short v = extractPixel(i + startX,j +startY);
                result.set(v, i,j, 0);
            }
        }
        return result;
    }


    //Inititalise le pixel, (par défaut a la dimension 0)
    private short extractPixel(int x, int y){
        if(x >= getImageWidth() || y >= getImageHeight())
            return 0; //TODO temporary fix
        int val =  ras.getSample(x,y,0);
        return (short) val;
    }


    private void nextImage(){
        actual_dim++;
        if(actual_dim >= dim)
            return;
        try {
            String filename = directory + "/"  + filenames[actual_dim];
            BufferedImage bf = ImageIO.read(new File(filename));
            ras = bf.getData();
        } catch (IOException e) {
            println filenames[actual_dim] + " not found"
        }

    }

    //Warning may be too hevay for heap
    public MDShortArray extractImage(){
        int wid = ras.getWidth();
        int hei = ras.getHeight();
        return extractTile(0,0,0,wid, hei, dim);
    }

    public MDShortArray extractTile(int startX, int startY, int startDim, int wid, int hei, int depth){
        MDShortArray result = createTile(startX, startY, wid, hei, depth);
        int limit = depth;
        if(startDim + limit > dim)
            limit = dim;
        nextImage();
        for(int k = 1; k < limit; ++k){
            for(int i = 0; i < wid; ++i){
                for(int j = 0; j < hei; ++j){
                    short v = extractPixel(i + startX,j +startY);
                    result.set(v, i,j, k);
                }
            }
            nextImage(); //Va trop loin  a la derniere iteration
        }
        actual_dim = 0; //Reinit of the image number we work with
        return result;
    }

}
