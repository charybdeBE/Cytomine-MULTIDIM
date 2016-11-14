#!bin/bash

#Test repeat x times : (measure the time of)
#Select 1 pixel randomly
#Get the info associated with each wavelength of each pixel for a square 
#where the top left corner is the selected pixel
#the square is y x y pixel large
#Default values x = 100 y = 10

#Options :
# -p | --path define the path to the file
# -u | --url define the url of the iip image server
# -o | --output output file to write the result in
# -n  the size of a square edge
# -t the number of tests

#Configuration
tile=$[(15653/256) * (11296 / 256)] #nr of tile 
tile_per_line=$[11296/256] #nr of tile in a line
wid=256 #width of a tile
hei=256 #heigth of a tile
url=localhost-iip-base #url of the iip server
output=result_test2.txt
path=""

#Default argument
nr_pxl=10
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
let "nr_pxl-=1"

for i in `seq 1 $nr_test`; do
	echo "Test nr ${i}"
	req=""
	#select a pixel
	t=$RANDOM
	let "t%=$tile"
	x=$RANDOM
	let "x%=$wid"
	y=$RANDOM
	let "y%=$hei"
	#build the requests for the square :
	for j in `seq 0 $nr_pxl`; do
		let "xx=$x+$j"
		for k in `seq 0 $nr_pxl`; do
			let "yy=$y+$k"
			tt=$t
			if [ $xx -ge $wid ]; then #square overlaping tiles
				let "tt=$t+1"
				let "xx%=$wid"
			fi
			if [ $xx -ge $hei ]; then 
				let "tt=$t+$tile_per_line"
				let "yy%=$hei"
			fi	
			req=$req"-L http://"$url"/fcgi-bin/iipsrv.fcgi?FIF="$path"&SPECTRA=6,"$tt","$xx","$yy" "
		done
	done
	#echo $req
	#start time measure
	start=$(date +%s.%N)
	curl $req > /dev/null
	end=$(date +%s.%N)
	#end measure and store stats
	res[$i]="$(echo "$end - $start" | bc)\n"
done

echo -e ${res[*]} > $output
	

