#! /bin/bash
# $1 PAIRED end reads sample path
# bowte2 version: 2.1.0
bowtie2 -p 6 -x /path/to/hs_ref_GRCh37.p10.Chr -1 "$1"_1.fastq -2 "$1"_2.fastq -S "$1".sam
