#!/bin/bash
tolerance="0.001"

for endf_file in ./endf/*; do
	mat=$(basename "$endf_file")
	echo "mat: $mat"
	cp $endf_file tape20
	njoy21 <<EOF
reconr
20 21
"MAT: $mat PENDF from ENDF/B-VII"/
$mat 1/
$tolerance/
"processed with NJOY"/
0/
stop
EOF
	mv tape21 ./pendf/$mat
done

rm tape20 tape21
