#!/bin/bash
base=https://t2.lanl.gov/nis
page=$base/data/endf/endfvii.1-n.html
mats=$(curl $page | pcregrep -iM -o1 -o2 --om-separator=" " "<td>(\d+)<\/td>(?:.|\n)*?<td>.*<\/td>(?:.|\n)*?<td><a href=\"\.\.\/\.\.\/(.*?)\">raw eval<\/a><\/td>")

while IFS= read -r line; do
	read -ra arr <<< "$line"
	mat=${arr[0]}
	url=${arr[1]}
	full_url="$base/$url"
	echo "material: ${mat}"
	echo "url: ${full_url}"
	curl $full_url > "endf/$mat"
done <<< "$mats"
