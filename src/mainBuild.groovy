

import be.charybde.multidim.hdf5.input.BuildHyperSpectralFile

/**
 * Created by laurent on 14.06.17.
 */

def destination = "/storage/work/multispec/"
def files = []
def pattern = "/storage/work/multispec/jpg/brug_chute_anges-cNUMBER-z0-t0.jpg"
def nrOfFiles = 256
def burstSize = 100
for(int i = 1; i < nrOfFiles; ++i){
    def ff = pattern.replaceAll("NUMBER", i.toString())
    files << ff
}



 BuildHyperSpectralFile h5builder =  new BuildHyperSpectralFile(destination,256, 256, 256, "", files, burstSize);
 h5builder.createFile(4)
