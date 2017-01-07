#!/bin/bash

name=""
ext=".h5"


if [ $# -lt 1 ]; then
	echo "Too few arguments"
	exit
fi

name=${1}
sub="*/"
nameAlone=$(echo ${name/${sub}/""})
dirr=$(echo ${name/${nameAlone}/""})

cd ${dirr}

for fil in $(ls -1v ${nameAlone}*${ext}) ; do
	if [ "${out}" == "" ]; then
		out=${fil}
	else
		out="${out},${fil}"
	fi
done

echo ${out}
