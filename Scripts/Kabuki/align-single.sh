#! /bin/bash
# $1 sigle end reads file path
# bowte2 version: 2.1.0
bowtie2 -p 6 -x /path/to/hs_ref_GRCh37.p10.Chr -U "$1".fastq -S "$1".sam
