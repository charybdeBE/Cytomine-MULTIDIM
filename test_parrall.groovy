println "Test 3 parralleslism"
def random = new Random()


class Pxl{
    int tile, x,y, res
    Pxl(t,x,y){
        this.x = x
        this.y = y
        this.tile = t
        this.res = 6
    }
}

def path="http://localhost-iip-base/fcgi-bin/iipsrv.fcgi?FIF=data/28/brug/"

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


println "This has take ${duration} ms"