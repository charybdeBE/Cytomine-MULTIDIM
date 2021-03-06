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

def test_nr = "1650_1"
def nr_random = 1000
def server = "http://localhost:9080/"
def file = "/home/laurent/cyto_dev/Cytomine-MULTIDIM/test1650"


println "Test random access IMS rest HDF5 "

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

/*def create_square = { storage ->
    def base = create_random_pixel.call()
    0.upto(9, { x ->
        0.upto(9, {y ->
            storage << new Pxl2(base.getTile(), base.getX() + x, base.getY() + y)
        })
    })
}*/


def work = { x, y ->
    def txt = server + "multidim/rect.json?fif=" + file + "&x=" + x + "&y=" + y + "&w=10&h=10"
    // println "Here is from ${txt}"
    def texte = ""
    try {
        texte = new URL(txt).getText()
    }catch(Exception e){
        println "Exception in " + txt
    }
    texte
}

def work2 = { x, y ->
    def txt = server + "multidim/pxl.json?fif=" + file + "&x=" + x + "&y=" + y
    // println "Here is from ${txt}"
    def texte = ""
    try {
        texte = new URL(txt).getText()
    }catch(Exception e){
        println "Exception in " + txt
    }
    texte
}


def error = 0
def output = new File("result_ims_pxlrush_"+ test_nr +".txt");
output.text = "Execution time (ms)\n"

1.upto(nr_random,{ i ->
    def op = ""
    def tst = create_random_pixel.call()
    def ttt = create_square.call(tst[0], tst[1])
    def time = benchmark{

        def j = 0
        ttt.each {
            op = work2.call(ttt[j][0], ttt[j][1])
            ++j
        }
    }
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

