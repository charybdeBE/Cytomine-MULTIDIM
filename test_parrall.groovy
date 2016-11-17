println "Test 3 parralleslism"
def random = new Random()


class Pxl{
    def path="http://localhost-iip-base/fcgi-bin/iipsrv.fcgi?FIF=data/28/brug/" //TODO include it in the constructor
    int tile, x,y, res
    Pxl(t,x,y){
        this.x = x
        this.y = y
        this.tile = t
        this.res = 6
    }

    //Nb names for directories inside the path are : path/<X>_<cores>/link
    // X start by 1
    def getInfo(int cores){
        assert cores > 0
        def ids = new int[cores]
        def threads = new ArrayList<Thread>()
        def results = new String[cores]
        def toret = ""
        1.upto(cores, { i -> ids[i-1] = i})
        ids.each { tid ->
            def t = Thread.start {
                def txt = path + "/" + tid + "_4/link&SPECTRA=" + this.res + "," + this.tile + "," + this.x + "," + this.y
                //println "From T $tid : ${txt}"
                def texte = new URL(txt).getText()
                if (tid != cores) {
                    texte = texte.substring(0, texte.indexOf('</spectra>') - 1); //remove last line
                }
             if (tid != 1) {
                    texte = texte.substring(texte.indexOf('</point>') + 8) //Remove the first wavelentgh (always id 0)
                }
                results[tid - 1] = texte
            }
            threads.add(t)
        }

        def i = 0
        for (Thread t : threads){
            t.join()
            toret += results[i]
            ++i
        }

        return toret

    }
}


def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}//problem

def tests = new ArrayList<Pxl>();
def create_test = {
    def xx = random.nextInt(256)
    def yy = random.nextInt(256)
    def tt = random.nextInt(6)
    tests.add(new Pxl(tt,xx,yy))
}
1.upto(100, create_test) //for loop from 1 to 10 include, execute each time create_test
/*
def threads = new ArrayList<Thread>()
println "Start the measure"
def duration = benchmark {
    [1, 2, 3, 4].each { tid ->
        def t = Thread.start {
            //println "Thread $tid says Hello World!"
            for (Pxl p : tests) {
                def txt = path + "/" + tid + "_4/link&SPECTRA=" + p.res + "," + p.tile + "," + p.x + "," + p.y
                //  println "From T $tid : ${txt}"
                new URL(txt).getText()
            }
        }
        threads.add(t)
    }

    for (Thread t : threads)
        t.join()
}
*/

def times = new ArrayList<Integer>()

for(Pxl p : tests){
    def duration = benchmark{
          p.getInfo(4)
    }
    println "This has take ${duration} ms"
    times.add(duration)
}

