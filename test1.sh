#!bin/bash

# $1 = path to the file
#Test repeat $2 times : (measure the time of)
#Select $3 pixels randomly
#Get the info associated with each wavelength
#
#Default values $2 = 100 $3 = 100

nr_pxl=100
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

for i in `seq 1 $nr_test`; do
req=""
	for j in `seq 1 $nr_pxl`; do
		x=$RANDOM
		let "x%=$wid"
		y=$RANDOM
		let "y%=$hei"
		req=$req"-L http://localhost-iip-base/fcgi-bin/iipsrv.fcgi?FIF="$path"&SPECTRA=0,0,"$x","$y" "
	done
	#start time measure
	start=$(date +%s.%N)
	curl $req > /dev/null
	end=$(date +%s.%N)
	#end measure and store stats
	res[$i]="$(echo "$end - $start" | bc)\n"
done

echo -e ${res[*]} > results_test1.txt
	

