#!/bin/bash

dir=""
ext=".jpg"


if [ $# -lt 1 ]; then
	echo "Too few arguments"
	exit
fi

dir="${1}/"

for fil in $(ls -1v ${dir}*${ext}) ; do
	fff=$(echo ${fil/${dir}/""})
	if [ "${out}" == "" ]; then
		out=${fff}
	else
		out="${out},${fff}"
	fi
done

echo ${out}
