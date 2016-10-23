#!/bin/bash


#Convertisseur de base pour fichier en images multispectrale usable par IIPimage
#Pas parfait avec des arguments attention au slash (problemes avec espaces)
#arg 1 = future filename
#arg 2 = input directory (optional)
#arg 3 = output directory (optional too)


inp=""
outp=""
f=0
filename=""
template=""

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
			echo "Incorrect number of parameters"
			exit
		fi
	fi
fi

#create a resultN directory 
r=$(ls $outp | grep -o 'result' | wc -l)
rr=$outp"result"$r
mkdir $rr




for fil in $inp*.jpg; do
	zer=""
	f=$(echo ${fil/$inp/""} | grep "[0-9]+" -o -E | head -n1 | cut -d " " -f1) #get the number of the file (should be the first number encountered)
	echo $f
	if [ $f -lt 100 ]; then
		if [ $f -lt 10 ]; then
			zer="00"
		else
			zer="0"
		fi
	fi

	newfile=$rr"/"$filename"_pyr_"$zer""$f"_090.tif"
	echo $fil " -> " $newfile
	#vips tiffsave "$fil" "$newfile" --tile --pyramid --compression lzw --tile-width 256 --tile-height 256 --bigtiff
done
