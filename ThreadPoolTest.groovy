import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.Future

/**
 * Created by laurent on 28.11.16.
 */

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






class Pxl2 {
    def path = "http://localhost-iip-base/fcgi-bin/iipsrv.fcgi?FIF=data/28/brug/" //TODO include it in the constructor
    int tile, x, y, res

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
    }

    //Nb names for directories inside the path are : path/<X>_<cores>/link
    // X start by 1
    def getInfo(int cores, def threadPool) {
        assert cores > 0
        def sb = new StringBuilder();

        List<Future> futures = (1..cores).collect{num->
            threadPool.submit({-> work(this, num, cores) } as Callable);
        }
        futures.each{sb.append(it.get())}

        return sb.toString();

    }

    def work = { px, tid, cores ->
        def txt = px.path + "/" + tid + "_" + cores + "/link&SPECTRA=" + px.res + "," + px.tile + "," + px.x + "," + px.y
//        println "Here is from ${tid} : ${txt}"
        def texte = new URL(txt).getText()
         if (tid != cores) {
             texte = texte.substring(0, texte.indexOf('</spectra>') - 1); //remove last line
         }
         if (tid != 1) {
             texte = texte.substring(texte.indexOf('</point>') + 8) //Remove the first wavelentgh (always id 0)
         }

        return texte;
    }
}



def threadPool = Executors.newFixedThreadPool(4)
def tst = new ArrayList<Pxl2>();
(1..10).each {tst << create_random_pixel.call()} //PS : le .call() est obligatoire ici
tst.each { px -> println px.getInfo(8, threadPool)}

threadPool.shutdown();