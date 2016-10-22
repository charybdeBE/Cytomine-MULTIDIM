#!bin/bash


# $1 = path to the image
#Test repeat $2 times : (measure the time of)
#Select 1 pixel randomly
#Get the info associated with each wavelength of each pixel for a square 
#where the top left corner is the selected pixel
#the square is $3 x $3 pixel large
#Default values $2 = 100 $3 = 10

nr_pxl=10
nr_test=100

if [ $# -lt 1 ]; then
	echo "Too few arguments"
	exit
else
	path=$1
fi
if [ $# -gt 1 ]; then
	nr_test=$2
	if [ $# -gt 2 ]; then
		nr_pxl=$3
		if [ $# -gt 3 ]; then 
			echo "Too much arguments"
			exit
		fi
	fi
fi

wid=1563 #x todo
hei=1129 #y todo

res=("Execution time(s)\n")
let "nr_pxl-=1"

for i in `seq 1 $nr_test`; do
	req=""
	#select a pixel
	x=$RANDOM
	let "x%=$wid"
	y=$RANDOM
	let "y%=$hei"
	#build the requests for the square :
	for j in `seq 0 $nr_pxl`; do
		for k in `seq 0 $nr_pxl`; do
			let "xx=$x+$j"
			let "yy=$y+$k"
			req=$req"-L http://localhost-iip-base/fcgi-bin/iipsrv.fcgi?FIF="$path"&SPECTRA=0,0,"$xx","$yy" "
		done
	done
	#start time measure
	start=$(date +%s.%N)
	curl $req > /dev/null
	end=$(date +%s.%N)
	#end measure and store stats
	res[$i]="$(echo "$end - $start" | bc)\n"
done

echo -e ${res[*]} > results_test2.txt
	

