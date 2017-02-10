/**
 * Created by laurent on 21.12.16.
 */

import be.charybde.multidim.hdf5.input.*

def rand = new Random()


def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}




def script = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/listOfFile.sh"
def fn2 = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/test333"
def dir = "/home/laurent/sample/1-6/"

def stringScript = "" + script + " " + dir
def retScript = stringScript.execute().text
def files = retScript.split("\n")

println "Number of files : " +files.size()


def tt = benchmark {
    def worker = new BuildFile(fn2, dir, files)

    worker.createParr(8)
}
tt /= 1000

println "Time for // " + tt + "(s)"
/*

def randomPair = {
    def x = rand.nextInt(15653)
    def y = rand.nextInt(11296)
    [x,y]
}

def reader = new HDF5PxlReader(fn2)
def pxl = []
def coo = [[500000,0],[0,1],[1,0],[0,500],[0,501]]
//def coo = []
def times = []

*/
/*0.upto(5,{
    def cord = randomPair.call()
    coo << cord
    times << benchmark{
        pxl << reader.extractSpectraSquare(cord[0], coo[1], 10)
    }
})*//*

def i = 0
coo.each {cord ->
    times << benchmark{
        try{
            pxl << reader.extractSpectraPixel(cord)
            println pxl[i].getCSV()
            ++i
        }catch (IndexOutOfBoundsException e){
            println "Shit happens"
        }

    }

}

reader.close()

i = 0
times.each { fly ->
    fly /= 1000
    println "It has taken " + fly + " (s) for number " + i + " " + coo[i % coo.size()] + " Control  size : " + pxl[i].getValues().size()
    ++i
}

*/
