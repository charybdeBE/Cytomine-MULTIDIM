#!/bin/bash

dir=""
ext=".jpg"


if [ $# -lt 1 ]; then
	echo "Too few arguments"
	exit
fi

dir="${1}/"
out=""

ls -1v ${dir}*${ext} | while read fil; do
	fff=$(echo ${fil/${dir}/""})
	echo "${fff}"
done

