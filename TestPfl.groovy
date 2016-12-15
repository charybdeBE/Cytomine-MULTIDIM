/**
 * Created by laurent on 15.12.16.
 */

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.Future

/**
 * Created by laurent on 28.11.16.
 */

def size_list = [ 32 ]
def nr_square = 1


println "Test 7 // with threadpool"

def random = new Random()

def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}


def create_random_pixel = {
    def xx = random.nextInt(11296)
    def yy = random.nextInt(15653)
    new Pxl3(xx, yy)
}



class Pxl3 {
    def path = "http://localhost-iip-base/fcgi-bin/iipsrv.fcgi?FIF=/data/28/brug/" //TODO include it in the constructor
    int x, y, res

    Pxl3(x, y) {
        this.x = x
        this.y = y
        this.res = 6
    }

    def getSquareInfo(def threadPool, int size) {
        assert size > 0
        def arrRet = new ArrayList<ArrayList<Future> >()

        def sb = new StringBuilder();
        def x_end = this.x + size - 1
        def y_end = this.y + size - 1

        arrRet << (y..y_end).collect{yy->
            threadPool.submit({-> work(this, x_end, yy) } as Callable);
        }

        arrRet.each { futures ->
            futures.each{sb.append(it.get() + "\n")}
        }

        return sb.toString();

    }

    def work = { px, x_end, y ->
        def txt = px.path + "/brug&PFL=" + px.res + ":" + px.x + "," + y + "-" + x_end + "," + y
        // println "Here is from ${txt}"

        def texte = new URL(txt).getText()


        //return ""
        return texte;
    }
}

size_list.each { size ->
    def tst = new ArrayList<Pxl3>();
    def threadPool = Executors.newFixedThreadPool(size)
    def output = new File("result_"+size+"tp_PFL_1.txt");
    output.text = "Execution time (ms)"
    (1..nr_square).each { tst << create_random_pixel.call() }
    def i = 0
    tst.each { px ->
        def duration = benchmark{ output << px.getSquareInfo(threadPool, 10) }
        println "Test nr ${i} Square Threadpool of size ${size} has taken ${duration} ms  "
        output << duration  + "\n"
        ++i

    }
    threadPool.shutdown();
}