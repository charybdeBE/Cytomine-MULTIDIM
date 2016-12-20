import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.Future

/**
 * Created by laurent on 28.11.16.
 */

def size_list = [16, 32, 48, 64, 96]
def nr_square = 100


println "Test 6 // with threadpool"

def random = new Random()

def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}


def create_random_pixel = {
    def xx = random.nextInt(256)
    def yy = random.nextInt(256)
    int maxtile = (15653/256) * (11296 / 256)
    def tt = random.nextInt(maxtile)
    new Pxl2(tt, xx, yy)
}

def create_square = { storage ->
    def base = create_random_pixel.call()
    0.upto(9, { x ->
        0.upto(9, {y ->
            storage << new Pxl2(base.getTile(), base.getX() + x, base.getY() + y)
        })
    })
}




class Pxl2 {
    def path = "http://localhost-iip-base/fcgi-bin/iipsrv.fcgi?FIF=data/28/brug/" //TODO include it in the constructor
    int tile, x, y, res
    def division;

    Pxl2(t, x, y) {
        if (x > 255) {
            t++
            x -= 255
        }
        if (y > 255) {
            t += (15653 / 256)
            y -= 255
        }
        this.x = x
        this.y = y
        this.tile = t
        this.res = 6
        this.division = 8;
    }

    //Nb names for directories inside the path are : path/<X>_<cores>/link
    // X start by 1
    def getSquareInfo(def threadPool, int size) {
        assert size > 0

        def arrRet = new ArrayList<ArrayList<Future> >()


        def sb = new StringBuilder();
        0.upto(size - 1, { x->
            0.upto(size - 1, { y ->
                def xx = this.x + x % 256
                def yy = this.y + y % 256
                def tt = this.tile
                if(this.x + x > 256)
                    tt++
                if(this.y + y > 256)
                    tt += (11296 / 256)
                 arrRet << (1..division).collect{num->
                    threadPool.submit({-> work(new Pxl2(tt, xx, yy), num, division) } as Callable);
                }
            })

        })

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        arrRet.each { futures ->
            sb.append("\n<pixel>\n<x>XXX</x>\n<y>YYY</y>")
            futures.each{sb.append(it.get() + "\n")}
            sb.append("</pixel>")
        }

        return sb.toString();

    }

    def work = { px, tid, cores ->
        def txt = px.path + "/" + tid + "_" + cores + "/link&SPECTRA=" + px.res + "," + px.tile + "," + px.x + "," + px.y
       // println "Here is from ${tid} : ${txt}"

        def texte = new URL(txt).getText()
         if (tid != cores) {
             texte = texte.substring(0, texte.indexOf('</spectra>') - 1); //remove last line
         }
         if (tid != 1) {
             texte = texte.substring(texte.indexOf('</point>') + 8) //Remove the first wavelentgh (always id 0)
         }
      if(tid == 1){
            texte = texte.substring(texte.indexOf('?>') + 2) // remove <? xml ...
        }


        return texte;
    }
}




def values = size_list
values.each { size ->
    def tst = new ArrayList<Pxl2>();
    def threadPool = Executors.newFixedThreadPool(size)
    def output = new File("result_"+size+"size_threadpool_1.txt");
    output.text = "Execution time (ms)"
    (1..nr_square).each { tst << create_random_pixel.call() }
    def i = 0
    tst.each { px ->
        def duration = benchmark{  px.getSquareInfo(threadPool, 10) }
        println "Test nr ${i} Square Threadpool of size ${size} has taken ${duration} ms  "
        output << duration  + "\n"
        ++i

    }
    threadPool.shutdown();
}