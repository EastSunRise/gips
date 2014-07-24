/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parameter;

import file.CommonInputFile;
import genome.artificial.ArtificialSNPGenerator;
import genome.artificial.ArtificialSequenceReadsFileGenerator;
import genome.artificial.SampleArtificialGenome;
import genome.effectiveRegion.GenomeEffectiveRegion;
import snpCaller.SNPCaller;
import variant.SampleVariant;

/**
 * This detector not only detect the sensitivity of genotyping but also the
 * false positive rate of genotyping
 *
 * @author sss
 */
public class VariantCallingSensitivityDectectorParameter {
        private int mode=0;

        private GenomeEffectiveRegion genomeEffectiveRegion;
        private SampleVariant sampleVariant;
        private CommonInputFile sequenceReadsFile;
        private CommonInputFile artificialSNPPositionFile;
        private int artificialSNPNumber;
        private SampleArtificialGenome sampleArtificialGenome;
        private String callerScript;
        
        public VariantCallingSensitivityDectectorParameter(SampleParameter sampleParameter){
                this.renewSequenceReadsFile(sampleParameter.getSequenceReadsFile());
                this.setArtificialSNPNumber(sampleParameter.getArtificialSNPNumber());
                this.setCallerScript(sampleParameter.getCallerScript());
        }
        
        public VariantCallingSensitivityDectectorParameter(int artificialSNPNumber) {
                this.setArtificialSNPNumber(artificialSNPNumber);
        }

        public VariantCallingSensitivityDectectorParameter() {
        }

        private void setArtificialSNPNumber(int number) {
                this.artificialSNPNumber = number;
        }

        public int getArtificialSNPNumber() {
                return this.artificialSNPNumber;
        }

        // mode means which type of variant to detect
        //1:homo;2:heter;3:homo and hetero
        public void setGenotypeMode(int mode) {
                this.mode=mode;
        }
        private int getGenotypeMode(){
                return this.mode;
        }
        public void setGenomeEffectiveRegion(GenomeEffectiveRegion genomeEffectiveRegion1) {
                this.genomeEffectiveRegion = genomeEffectiveRegion1;
        }

//        public String getGenotype() {
//                return this.genotype;
//        }

        private GenomeEffectiveRegion getGenomeEffectiveRegion() {
                return this.genomeEffectiveRegion;
        }

        private SampleVariant getSampleVariant() {
                return this.sampleVariant;
        }

        private CommonInputFile getSequenceReadsFile() {
                return this.sequenceReadsFile;
        }

//       private ArtificialSNPGenerator getArtificialSNPGenerator(){
//              return  new ArtificialSNPGenerator(this.getGenomeEffectiveRegion(), this.getSampleVariant(), this.getGenotype(),this.getArtificialSNPNumber());
//       }
        void setArtificialSNPPositionFile(CommonInputFile file) {
                this.artificialSNPPositionFile = file;
        }

        public CommonInputFile getArtificialSNPPositionFile() {
                return this.artificialSNPPositionFile;
        }

        private SampleArtificialGenome getSampleArtificialGenome() {
                SampleArtificialGenome sampleArtificialGenome = null;
                ArtificialSNPGenerator artificialSNPGenerator;
                artificialSNPGenerator = new ArtificialSNPGenerator(this.getGenomeEffectiveRegion(), this.getSampleVariant(), this.getGenotypeMode(), this.getArtificialSNPNumber());
                common.CExecutor.println(common.CExecutor.getRunningTime()+"----- ----- ----- ----- ----- ----- ----- ----- -----\n"+common.CExecutor.getRunningTime()+this.getSampleVariant().getName()+" number of SNPs to simulate: " + this.getArtificialSNPNumber());
                sampleArtificialGenome = artificialSNPGenerator.generate();
                this.setArtificialSNPPositionFile(artificialSNPGenerator.getArtificialSNPPositionFile());
                this.sampleArtificialGenome=sampleArtificialGenome;
                return sampleArtificialGenome;
        }
//       private ArtificialSequenceReadsFileGenerator getArtificialSequenceReadsFileGenerator(){
//               return new ArtificialSequenceReadsFileGenerator(this.getSequenceReadsFile(), this.getSampleArtificialGenome());  
//       }   
        //Before get a caller, artificial reads in sam format file should be generated
        public snpCaller.SNPCaller getSNPCaller() {
                ArtificialSequenceReadsFileGenerator artificialSequenceReadsFileGenerator = new ArtificialSequenceReadsFileGenerator(this.getSequenceReadsFile(), this.getSampleArtificialGenome());
                SNPCaller snpc = new SNPCaller(artificialSequenceReadsFileGenerator.generate(), this.sampleVariant.getName(),this.getCallerScriptFilePath());
                return snpc;
        }
        public void renewSampleVariant(SampleVariant sampleVariant) {
                this.sampleVariant = sampleVariant;
        }
        //when to evaluate a sample's calling sensitivity, its sequence reads file should be updated to its own
        // used in parameter
        public void renewSequenceReadsFile(CommonInputFile file) {
                this.sequenceReadsFile = file;
        }
        //Different sample has different sampleArtificialGenome
        public SampleArtificialGenome getSampleArtificialGenomeForDetecting(){
                return this.sampleArtificialGenome;
        }
        private void setCallerScript(String script){
                this.callerScript=script;
        }

        public String getCallerScriptFilePath(){
                return this.callerScript;
        }
}
