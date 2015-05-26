#! /bin/bash
# $1 $2 PAIRED end reads sample path   $3 sample name 
 /path/to/bowtie2 -p 7 -x rice -1 "$1" -2 "$2" /path/to/RiceData/out/"$3"/btout.sam

