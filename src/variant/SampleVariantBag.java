/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package variant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author zzx
 */
public class SampleVariantBag {

        private HashMap<String, SampleVariant> bag;//sample name and variant
        private int sampleNum = 0;

        public SampleVariantBag() {
                bag = new HashMap<>();
        }

        /**
         * Check sample variant bag is created or not. If bag has sample
         * variant, it means bag is created.
         *
         * @return
         */
        public boolean isReady() {
                if (this.sampleNum == 0) {
                        return false;
                } else {
                        return true;
                }
        }

        /**
         *
         * @return return all sample names
         */
        public Set<String> getSampleNames() {
                return new TreeSet<>(this.bag.keySet());
        }

        /**
         * sample total number plus 1 while adding
         *
         * @param sampleVariant add one sampleVariant object
         */
        public void addSampleVariant(SampleVariant sampleVariant) {
                this.bag.put(sampleVariant.getName(), sampleVariant);
                sampleNum = sampleNum + 1;
                common.CExecutor.println(common.CExecutor.getRunningTime()+sampleVariant.getName() + "\t" + sampleVariant.getSNPCounts() + " variants");
        }

        /**
         *
         * @return return a set contains all sample variant ,not return a
         * hashmap
         */
        public HashSet<SampleVariant> getsampleVariantBag() {
                HashSet<SampleVariant> samplesVariant = new HashSet<>();
                for (Iterator<String> iterator = this.getSampleNames().iterator(); iterator.hasNext();) {
                        samplesVariant.add(this.bag.get(iterator.next()));
                }
                return samplesVariant;
        }

        public SampleVariant getSampleVariant(String name) {
                if (!this.bag.containsKey(name)) {
                        common.CExecutor.println("No" + name + "'s variant ");
                        return null;
                }
                return this.bag.get(name);
        }

        /**
         * renew function is like addSampleVariant but for the function name.
         *
         * @param sampleVariant
         */
        public void renewSampleVariant(SampleVariant sampleVariant) {
                String name = sampleVariant.getName();
                if (this.bag.keySet().contains(name)) {
                        this.bag.put(name, sampleVariant);
                } else {
                        common.CExecutor.println("Could not renew sample varaice----" + name);
                }
        }

        public void removeSNPInSamePosition(String chr, int position) {
                if (chr.equals("ChrSy") || chr.equals("ChrUn")) {
                        return;
                }
                for (Iterator<String> sampleNameIterator = this.getSampleNames().iterator(); sampleNameIterator.hasNext();) {
                        SampleVariant sampleVariant = this.getSampleVariant(sampleNameIterator.next());
                        sampleVariant.removeSNP(chr, position);
                        this.renewSampleVariant(sampleVariant);
                }
        }

        public void printSNPNumberInSample() {
                for (Iterator<String> sampleNameIterator = this.getSampleNames().iterator(); sampleNameIterator.hasNext();) {
                        SampleVariant sampleVariant = this.getSampleVariant(sampleNameIterator.next());
                        common.CExecutor.println(sampleVariant.getName() + "\t"+String.valueOf(sampleVariant.getSNPCounts()));
                }
        }

        public HashMap<String, Integer> getSNPNumberInSample() {
                HashMap<String, Integer> snpNumber = new HashMap<>();
                for (Iterator<String> iterator = this.getSampleNames().iterator(); iterator.hasNext();) {
                        String sampleName = iterator.next();
                        int number = this.getSampleVariant(sampleName).getSNPCounts();
                        snpNumber.put(sampleName, number);
                }
                return snpNumber;

        }

        public int getSampleNumber() {
                return this.sampleNum;
        }

}
