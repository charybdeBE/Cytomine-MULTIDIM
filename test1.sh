#!bin/bash

# $1 = path to the file
#Test repeat $2 times : (measure the time of)
#Select $3 pixels randomly
#Get the info associated with each wavelength
#
#Default values $2 = 100 $3 = 1

#Configuration
tile=$[(15653/256) * (11296 / 256)] #nr of tile 
tile_per_line=$[11296/256] #nr of tile in a line
wid=256 #width of a tile
hei=256 #heigth of a tile
url=localhost-iip-base #url of the iip server

#Default argument
nr_pxl=1
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



res=("Execution time(s)\n")

for i in `seq 1 $nr_test`; do
	echo "Test nr "$i
	req=""
	for j in `seq 1 $nr_pxl`; do
		t=$RANDOM
		let "t%=$tile"
		x=$RANDOM
		let "x%=$wid"
		y=$RANDOM
		let "y%=$hei"
		req=$req"-L http://"$url"/fcgi-bin/iipsrv.fcgi?FIF="$path"&SPECTRA=6,"$t","$x","$y" "
	done
	#start time measure
	#echo $req
	start=$(date +%s.%N)
	curl $req > /dev/null
	end=$(date +%s.%N)
	#end measure and store stats
	res[$i]="$(echo "$end - $start" | bc)\n"
done

echo -e ${res[*]} > results_test1_moo.txt
	

