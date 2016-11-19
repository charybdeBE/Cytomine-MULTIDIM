#!/bin/bash

#Vanosmael Laurent 16/11/2016

#This utility is use to create several subset of a multidim image (according to IIP rules)
#in order to access them in parrallel

#Utilisation :
#./build_for_n_cores.sh <path> <nr_cores> options


#Options :
# -d | --dirr Define where to store the results (default = path dirr)



#Configuration
path=$1
cores=$2
dirr=$1"/" #TODO enlever le nom du fichier


while [[ $# -gt 3 ]] ;do
key="$3"
case $key in
	-d|--dirr)
	mkdir -p $4
	dirr=$4"/"
	shift 
	;;
	*)
	    # unknown option
	;;
esac
shift 
done

beg=0
nr_wave=1650 #TODO detect that
step=$[(${nr_wave} / ${cores})]
stop=$step
rest=$[${nr_wave} % ${cores}]
#TODO make verification to not execute the script if the work is already done
for i in `seq 1 $cores`; do
	bash build_range.sh $path $beg $stop -d "${dirr}/${i}_${cores}/"
	beg=$[$stop + 1]
	if [ $rest -gt 0 ]; then 
		stop=$[$stop + $step + 1]
		rest=$[$rest - 1]
	else
		stop=$[$stop + $step]
	fi
done 

	
	
