package be.charybde.multidim.hdf5.test

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by laurent on 01.02.17.
 */


def tester = new HDF5Test()
tester.initTest("/home/laurent/cyto_dev/Cytomine-MULTIDIM/test333.h5")
tester.testCube(15653, 11296)

def tt = new RESTAPITest()
tt.init("/home/laurent/cyto_dev/Cytomine-MULTIDIM/test1650", "http://localhost:9080", "/media/laurent/APOLLOII/TFE/22-LT2-26-01-2011-01-Bruegel/LAM1650/LAM1650HD")
tt.verifyPixel(0,0)
tt.verifyPixel(35,41)
tt.verifyPixel(1000,1000)

def filename = "Blood_vessels.ome_converted-z67-t0-c0.tiff"
Pattern patternZstack = Pattern.compile("-z[0-9]*");
Matcher matcher = patternZstack.matcher(filename);
if (matcher.find())
{
    def o = matcher.group(0).substring(2)
    println o
}

def ap = "/data/28//1490009166355/7c608d9a-f41f-4190-bf13-032dd1c569d3.tif"
def min = "/1490009166355/7c608d9a-f41f-4190-bf13-032dd1c569d3.tif"
def plus = "/1490009166355/stuff.jpg"

println ap - min

def test = " §e1 §e§5Enchanted Book§e (Water Worker I)§f,"
String path = "§e ([A-Z ]*)§f"
Pattern phone = Pattern.compile("§e \\([A-Za-z ]*\\)§f");
Matcher action = phone.matcher(test);
StringBuffer sb = new StringBuffer(test.length());
while (action.find()) {
    String text = action.group(0);
    text = text.replaceAll("§e", "")
    text = text.replaceAll("§f", "§e§f")
    action.appendReplacement(sb, Matcher.quoteReplacement(text));
}
action.appendTail(sb);
System.out.println(sb.toString());