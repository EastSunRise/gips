#!/bin/sh
# Kabuki exome sequencing data have paired end and single end data. After alignment, these files merged by sample.

cp /path/to/SRP003183/SRS086455/SRX025372.sam /path/to/SRP003183/SRS086455/SRS086455.sam
sed -n "27,\$p" /path/to/SRP003183/SRS086455/SRX025378.sam >> /path/to/SRP003183/SRS086455/SRS086455.sam
echo "1"
cp /path/to/SRP003183/SRS086456/SRX025373.sam /path/to/SRP003183/SRS086456/SRS086456.sam
sed -n "27,\$p" /path/to/SRP003183/SRS086456/SRX025379.sam >> /path/to/SRP003183/SRS086456/SRS086456.sam
echo "2"
cp /path/to/SRP003183/SRS086457/SRX025374.sam /path/to/SRP003183/SRS086457/SRS086457.sam
sed -n "27,\$p" /path/to/SRP003183/SRS086457/SRX025380.sam >> /path/to/SRP003183/SRS086457/SRS086457.sam
echo "3"
cp /path/to/SRP003183/SRS086458/SRX025375.sam /path/to/SRP003183/SRS086458/SRS086458.sam
sed -n "27,\$p" /path/to/SRP003183/SRS086458/SRX025381.sam >> /path/to/SRP003183/SRS086458/SRS086458.sam
echo "4"
cp /path/to/SRP003183/SRS086459/SRX025376.sam /path/to/SRP003183/SRS086459/SRS086459.sam
sed -n "27,\$p"  /path/to/SRP003183/SRS086459/SRX025382.sam >> /path/to/SRP003183/SRS086459/SRS086459.sam
echo "5"
cp /path/to/SRP003183/SRS086460/SRX025377.sam /path/to/SRP003183/SRS086460/SRS086460.sam
sed -n "27,\$p" /path/to/SRP003183/SRS086460/SRX025383.sam >> /path/to/SRP003183/SRS086460/SRS086460.sam
echo "6done"





