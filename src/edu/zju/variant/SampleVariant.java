package edu.zju.variant;

import edu.zju.file.CommonInputFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author zzx
 */
public class SampleVariant {

        private String name;
        private HashMap<String, LinkedList<SNP>> snps;
        private int snpCountsBeforeFiltering;
        private int snpCounts;
        private CommonInputFile vcfFilePath;
        private HashMap<String, Integer> snpGenotypeCount;
        private LinkedList<String> chrSequenceName;

        public SampleVariant(String sampleName) {
                this.snps = new HashMap<>();
                this.chrSequenceName = new LinkedList<>();
                this.setName(sampleName);
                this.snpCounts = 0;
                this.snpCountsBeforeFiltering = 0;
                this.snpGenotypeCount = new HashMap<String, Integer>() {
                        {
                                put("0/1", 0);
                                put("1/1", 0);
                                put("1/2", 0);
                        }
                };
        }

        /**
         *
         * @param path vcf file path
         */
        public void setVCFFile(CommonInputFile path) {
                this.vcfFilePath = path;
        }

        /**
         *
         * @return return this sample vcf file path
         */
        public CommonInputFile getVCFFile() {
                return this.vcfFilePath;
        }

        /**
         *
         * @param name sample name
         */
        private void setName(String name) {
                this.name = name;
        }

        /**
         *
         * @return return this sample name
         */
        public String getName() {
                return this.name;
        }

        private void addChrNameInSequence(String chrName) {
                this.chrSequenceName.add(chrName.toString());
        }

        public LinkedList<String> getChrNameInSequence() {
                return this.chrSequenceName;
        }

        /**
         * add one snp and snp count added
         *
         * @param snp
         */
        public void addSNP(SNP snp) {
                if (snp.getChr() == null || snp == null || snp.getChr().equals("ChrSy") || snp.getChr().equals("ChrUn")) {
                } else {
                        String chr = snp.getChr();
                        LinkedList<SNP> snpsInChr;
                        if (this.snps.keySet().contains(chr)) {
                                snpsInChr = this.snps.get(chr);
                                snpsInChr.add(snp);
                        } else {
                                snpsInChr = new LinkedList();
                                snpsInChr.add(snp);
                                this.addChrNameInSequence(chr);
                        }
                        this.snps.put(chr, snpsInChr);
                        if (this.snpCounts == this.snpCountsBeforeFiltering) {
                                this.snpCountsBeforeFiltering = this.snpCounts = this.getSNPCounts() + 1;
                        } else {
                                this.snpCounts = this.getSNPCounts() + 1;
                        }
//                  this.countGenotype(snp.getGenotype());
                }
        }

        /**
         *
         * @return return all the snp in this sample
         */
        public LinkedList<SNP> getSnps() {
                LinkedList<SNP> listSNP = new LinkedList<>();
                for (String chrName : this.getChrNameInSequence()) {
                        listSNP.addAll(this.snps.get(chrName));
                }
                return listSNP;
        }

        /**
         *
         * @return the total snp number in this sample
         */
        public int getSNPCounts() {
                return this.snpCounts;
        }

        public int getSNPCountsBeforeFiltering() {
                return this.snpCountsBeforeFiltering;
        }

        /**
         * remove a snp from list
         *
         * @param snp
         */
        public void removeSNP(String chr, int pos) {
                boolean flag = false;//true means this sample has snp in this position
                LinkedList<SNP> listSNPInChr = this.get1ChrSNP(chr);
                for (Iterator<SNP> iterator = listSNPInChr.iterator(); iterator.hasNext();) {
                        if (iterator.next().getPosition() == pos) {
                                iterator.remove();
                                flag = true;
                                break;
                        }
                }
                if (flag) {
                        this.snps.put(chr, listSNPInChr);
                        this.snpCounts = this.getSNPCounts() - 1;
                }

        }//

        public SNP get1SNP(String chr, int pos) {
                LinkedList<SNP> listSNPInChr = this.get1ChrSNP(chr);
                for (Iterator<SNP> iterator = listSNPInChr.iterator(); iterator.hasNext();) {
                        SNP snp = iterator.next();
                        if (snp.getPosition() == pos) {
                                return snp;
                        }
                }
                return null;
        }

        /**
         * judge if this position has a snp
         *
         * @param chr
         * @param position
         * @return
         */
        public boolean isSNP(String chr, int position) {
                if (this.snpCounts == 0) {
                        return false;
                }
                for (Iterator<SNP> iterator = this.get1ChrSNP(chr).iterator(); iterator.hasNext();) {
                        SNP snp = iterator.next();
                        if (snp.getChr().equals(chr) && snp.getPosition() == position) {
                                return true;
                        }
                }
                return false;
        }

        private LinkedList<SNP> get1ChrSNP(String chr) {
                LinkedList<SNP> list = this.snps.get(chr);
                if (list == null) {
                        list = new LinkedList<>();
                }
                return list;
        }

        public void renewSNPsInVariant(LinkedList<SNP> list) {
                this.snps = new HashMap<>();
                this.chrSequenceName = new LinkedList<>();
                this.snpCounts = 0;
                for (Iterator<SNP> iterator = list.iterator(); iterator.hasNext();) {
                        this.addSNP(iterator.next());
                }
        }

        public LinkedList<SNP> get1ChromosomeSNPs(String chr) {
                return this.snps.get(chr);
        }


}
