#!/bin/bash


#Convertisseur de base pour fichier en images multispectrale usable par IIPimage
#Pas parfait avec des arguments attention au slash (problemes avec espaces)
#Order of file bug
#arg 1 = future filename
#arg 2 = input directory (optional)
#arg 3 = output directory (optional too)
#arg 4 = first number (seems to be useless as it seems to be mandatory to start by 0)


inp=""
outp=""
f=0
filename=""

if [ $# -lt 1]; then
	echo "Too few arguments"
	exit
else
	filename=$1	
fi
if [ $# -gt 1 ]; then
	inp=$2
	if [ $# -gt 2 ]; then
		outp=$3
		if [ $# -gt 3 ]; then
			f=$4
			if [ $# -gt 4 ]; then
				echo "Incorrect number of parameters"
				exit
			fi
		fi
	fi
fi

#create a resultN directory 
r=$(ls $outp | grep -o 'result' | wc -l)
rr=$outp"result"$r
mkdir $rr




for fil in $inp*.jpg; do
	zer=""
	if [ $f -lt 100 ]; then
		if [ $f -lt 10 ]; then
			zer="00"
		else
			zer="0"
		fi
	fi

	newfile=$rr"/"$filename"_pyr_"$zer""$f"_090.tif"
	echo $fil " -> " $newfile
	f=$(($f + 1))
	vips tiffsave "$fil" "$newfile" --tile --pyramid --compression lzw --tile-width 256 --tile-height 256 --bigtiff
done
