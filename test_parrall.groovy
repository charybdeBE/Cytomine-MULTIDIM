println "Test 3 parralleslism"
def random = new Random()

def benchmark = { closure ->
    start = System.currentTimeMillis()
    closure.call()
    now = System.currentTimeMillis()
    now - start
}

class Pxl{
    def path="http://localhost-iip-base/fcgi-bin/iipsrv.fcgi?FIF=data/28/brug/" //TODO include it in the constructor
    int tile, x,y, res
    Pxl(t,x,y){
        if(x > 255){
            t++
            x-=255
        }
        if(y > 255){
            t+=(15653/256)
            y-=255
        }
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
                def txt = path + "/" + tid + "_" + cores + "/link&SPECTRA=" + this.res + "," + this.tile + "," + this.x + "," + this.y
//                println "From T $tid : ${txt}"
                def texte = new URL(txt).getText()
                if (tid != cores) {
                    texte = texte.substring(0, texte.indexOf('</spectra>') - 1); //remove last line
                }
                 if (tid != 1) {
                    texte = texte.substring(texte.indexOf('</point>') + 8) //Remove the first wavelentgh (always id 0)
                }
                results[tid - 1] = texte
            }
            threads << t
        }

        def i = 0

        for (Thread t : threads) {
            t.join()
            toret += results[i]
            ++i
        }

        return toret

    }
}



2.upto(8, { core ->

    //Tests for random access
    def tests = new ArrayList<Pxl>();
    def create_test = {
        def xx = random.nextInt(256)
        def yy = random.nextInt(256)
        int maxtile = (15653/256) * (11296 / 256)
        def tt = random.nextInt(maxtile)
        new Pxl(tt, xx, yy)
    }
    /*1.upto(100, { arr << create_test.call() }) //for loop from 1 to 100 include, execute each time create_test


    def i = 0
    def output = new File("result_"+ core + "cores_random_2.txt")
    output.text = "Execution time (ms)\n"
    tests.each { p ->
        def duration = benchmark {
            p.getInfo(core)
        }
        println "Test RANDOM : ${i} with ${core} cores has taken ${duration} ms  "
        output << duration + "\n"
        ++i
    }*/

    // Test to get a square
    def test2 = new ArrayList<Pxl>()
    def create_square = {
        def base = create_test.call()
        0.upto(9, { x ->
            0.upto(9, {y ->
                test2 << new Pxl(base.getTile(), base.getX() + x, base.getY() + y)
            })
        })
    }
    1.upto(100, create_square)
    i = 0
    def output2 = new File("result_"+core + "cores_square_1.txt")
    output2.text = "Execution time (ms)\n"
    test2.each { p->
        def duration = benchmark {
            p.getInfo(core)
        }
        println "Test Square : ${i} with ${core} cores has taken ${duration} ms  "
        output2 << duration + "\n"
        ++i
    }

})