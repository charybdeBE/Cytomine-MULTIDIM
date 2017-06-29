package be.charybde.multidim.hdf5.input

/**
 * Created by laurent on 14.06.17.
 */

def destination = "/storage/work/tmp"
def files = []
def pattern = "/state/partition1/multidim/brug_chute_anges-cNUMBER-z0-t0.jpg"
def nrOfFiles = 256
def burstSize = 50
def threads = 16
for(int i = 1; i < nrOfFiles; ++i){
    def ff = pattern.replaceAll("NUMBER", i.toString())
    files << ff
}


//println "Starting with files : $files"
 BuildHyperSpectralFile h5builder =  new BuildHyperSpectralFile(destination,256, 256, 256, "", files, burstSize);
 h5builder.createFile(threads)
