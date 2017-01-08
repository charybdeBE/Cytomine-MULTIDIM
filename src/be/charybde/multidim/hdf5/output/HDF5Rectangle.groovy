package be.charybde.multidim.hdf5.output

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
    void extractValues(HDF5PxlReader p) {

    }

    @Override
    def getDim() {
        return [x,y,dim,wid,hei]
    }
}
