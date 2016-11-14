#!bin/bash

#Test repeat x times : (measure the time of)
#Select y pixels randomly
#Get the info associated with each wavelength
#
#Default values x = 100 y = 1

#Options :
# -p | --path define the path to the file
# -u | --url define the url of the iip image server
# -o | --output output file to write the result in
# -n  the number of pixel to access by test
# -t the number of test

#Configuration
path=""
tile=$[(15653/256) * (11296 / 256)] #nr of tile 
tile_per_line=$[11296/256] #nr of tile in a line
wid=256 #width of a tile
hei=256 #heigth of a tile
url=localhost-iip-base #url of the iip server
output=result_test1.txt

#Default argument
nr_pxl=1
nr_test=100

while [[ $# -gt 1 ]] ;do
key="$1"
case $key in
	-p|--path)
	path="$2"
	shift 
	;;
	-u|--url)
	url="$2"
	shift
	;;
	-o|--output)
	output="$2"
	shift # past argument
	;;
	-n)
	nr_pxl="$2"
	shift
	;;
	-t)
	nr_test="$2"
	shift
	;;
	*)
	    # unknown option
	;;
esac
shift # past argument or value
done




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

echo -e ${res[*]} > $output
	

