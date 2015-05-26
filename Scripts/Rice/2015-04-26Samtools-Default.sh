#! /bin/bash
# $1 sam $2 vcf. Samtools version: 1.2

#sort and add read group
java -Xms8g -jar /path/to/picard.jar AddOrReplaceReadGroups INPUT="$1" OUTPUT=/path/to/Rice/Working/temporary/btout.bam SORT_ORDER=coordinate RGPL=illumina RGLB="GIPS" RGPU=temp RGSM="GIPS"
# remove duplicate
java -Xms8g -jar /path/to/picard.jar MarkDuplicates I=/path/to/Rice/Working/temporary/btout.bam O=/path/to/Rice/Working/temporary/bt_markdup.bam M=/path/to/Rice/Working/temporary/duplication_metrics REMOVE_DUPLICATES=true AS=true 
# index
/path/to/samtools index /path/to/Rice/Working/temporary/bt_markdup.bam
#########
# samtool call q map quality
/path/to/samtools mpileup -ugf /path/to/all.chrs.con.fa /path/to/Rice/Working/temporary/bt_markdup.bam -go -| /path/to/bcftools call -vmO z  -o - | /path/to/bcftools filter -O v -o "$2" -
