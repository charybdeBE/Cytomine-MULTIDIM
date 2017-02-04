package be.charybde.multidim.hdf5.test

import groovy.json.JsonSlurper

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.image.Raster

/**
 * Created by laurent on 02.02.17.
 */
class RESTAPITest {

    def script = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/listOfFile.sh"

    def filename
    def server
    def datasource
    def directory_source


    def init(def f, def s, def directory_source){
        this.filename = f
        this.server = s
        this.directory_source = directory_source

        def stringScript = "" + script + " " + directory_source
        println stringScript
        def retScript = stringScript.execute().text

        this.datasource = retScript.split("\n")


    }

    def verifyPixel(int x, int y){
        def request = server + "/multidim/pxl.json?fif=" + filename + "&x=" + x + "&y=" + y
        def texte
        try {
            texte = new URL(request).getText()
        }catch(Exception e){
            println "Exception in " + request
            return false
        }
        def trans = new JsonSlurper()
        def list = trans.parseText(texte)
        assert list instanceof Map
        assert false == list.containsKey("error")

        for (int i = 0; i < datasource.size() ; i++) {
            def ras
            try {
                String filename = directory_source + "/"  + datasource[i];
                BufferedImage bf = ImageIO.read(new File(filename));
                ras = bf.getData();
            } catch (IOException e) {
                System.out.println(datasource[i] + " Not found");
            }
            def test = ras.getSample(x,y, 0)
            assert list.spectra[i] == test


            println i + " correct"
        }
        return true
    }


}
