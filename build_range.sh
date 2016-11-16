#!bin/bash
#Vanosmael Laurent 16/11/2016

#This utility is use to create a subset of a multidim image (according to IIP rules)

#Utilisation :
#./build_range.sh <path> <seq beg> <seq end> options


#Options :
# -d | --dirr Define where to store the results (default = current dir)



#Configuration
path=$1
start=$2
stop=$3
dirr=$(pwd)"/"


while [[ $# -gt 4 ]] ;do
key="$4"
case $key in
	-d|--dirr)
	mkdir -p $5
	dirr=$5"/"
	shift 
	;;
	*)
	    # unknown option
	;;
esac
shift 
done


for i in `seq ${start} ${stop}`; do
	zer=""
	if [ $i -lt 100 ]; then
		if [ $i -lt 10 ]; then
			zer="00"
		else
			zer="0"
		fi
	fi
	ln -T "${path}_pyr_${zer}${i}_090.tif" "${dirr}link_pyr_${zer}${i}_090.tif"
done

if [ $(ls ${dirr} | grep "link_pyr_000_090.tif" -c) -lt 1 ];then #IIP absolutly need a 000 file
	ln -T "${path}_pyr_000_090.tif" "${dirr}link_pyr_000_090.tif"
fi
