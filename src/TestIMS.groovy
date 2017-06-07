/**
 * Created by laurent on 06.06.17.
 */

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.Future

/**
 * Created by laurent on 28.11.16.
 */

//Parameters
def test_nr = "1650core"
def nr_random = 1
def server = "http://demo-ims.cytomine.be/"
def file = "/data/images/15660178/hdf5_24188462"
def print_bool = true //Print the output
def test = "pixel" // Should be pixel|pixel_square|square
//end parameters


def methods = ["pixel", "square", "pixel_square"]
assert methods.contains(test)

println "Test HDF5"
def random = new Random()

def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}


def create_random_pixel = {
    def xx = random.nextInt(15653)
    def yy = random.nextInt(11296)
    [xx, yy]
}


def create_square = { x, y ->
    def ret = []
    0.upto(9, { xx ->
        0.upto(9, { yy ->
            ret <<  [x + xx, y+yy]
        })
    })
    return ret
}


def workRect = { x, y ->
    def txt = server + "multidim/rectangle.json?fif=" + file + "&x=" + x + "&y=" + y + "&w=10&h=10"
    // println "Here is from ${txt}"
    def texte = ""
    try {
        texte = new URL(txt).getText()
    }catch(Exception e){
        println "Exception in " + txt
    }
    texte
}

def workPxl = { x, y ->
    def txt = server + "multidim/pixel.json?fif=" + file + "&x=" + x + "&y=" + y
    //  println "Requestt ${txt}"
    def texte = ""
    try {
        texte = new URL(txt).getText()
    }catch(Exception e){
        println "Exception in " + txt
    }
    texte
}



def error = 0
def output = new File("result_"+ test_nr +".txt");
output.text = "Execution time (ms)\n"
d
1.upto(nr_random,{ i ->
    def op = ""
    def tst = create_random_pixel.call()
    def time = 0
    if(test == "pixel"){
        time = benchmark{
            op = workPxl.call(tst[0], tst[1])
        }
    }
    if(test == "pixel_square"){
        def ttt = create_square.call(tst[0], tst[1])
        time = benchmark{
            ttt.each { px ->
                op = workPxl.call(px[0], px[1])
            }
        }
    }
    if(test == "square"){
        time = benchmark{
            op = workRect.call(tst[0], tst[1])
        }
    }

    if(print_bool)
        println op
    def trans = new JsonSlurper()
    def list = trans.parseText(op)
    assert list instanceof Map
    if(list.containsKey("error")){
        println op
        error++
    }
    else{
        output << time + "\n"
    }

    println "Test nr " + i + " has taken " + time / 1000 + "(s)"
})

output << "Nr of errors : " + error + "(their times is not included)"
