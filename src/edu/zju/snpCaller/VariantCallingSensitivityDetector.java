package edu.zju.snpCaller;

import edu.zju.common.CExecutor;
import edu.zju.file.CommonInputFile;
import edu.zju.genome.artificial.SampleArtificialGenome;
import edu.zju.parameter.VariantCallingSensitivityDectectorParameter;

/**
 *
 ** @author Zhongxu Zhu
 */
public class VariantCallingSensitivityDetector {

        private VariantCallingSensitivityDectectorParameter genotypingSensitivityDectectorParameter;
        private int artificialSNPNumber;
        private CommonInputFile vcfFile;
        private String callerScript;
        
        public VariantCallingSensitivityDetector(edu.zju.parameter.VariantCallingSensitivityDectectorParameter genotypingSensitivityDectectorParameter) {
                this.setGenotypingSensitivityDectectorParameter(genotypingSensitivityDectectorParameter);
                this.setArtificialSNPNumber(genotypingSensitivityDectectorParameter.getArtificialSNPNumber());
                this.setCallerScript(genotypingSensitivityDectectorParameter.getCallerScriptFilePath());
        }

        private void setGenotypingSensitivityDectectorParameter(VariantCallingSensitivityDectectorParameter genotypingSensitivityDectectorParameter1) {
                this.genotypingSensitivityDectectorParameter = genotypingSensitivityDectectorParameter1;
        }

        protected VariantCallingSensitivityDectectorParameter getGenotypingSensitivityDectectorParameter() {
                return this.genotypingSensitivityDectectorParameter;
        }

        public void genotyping(int mode) {
                if(mode!=0){
                        //artificial reads will be generated while get a caller
                        SNPCaller snpCaller = this.genotypingSensitivityDectectorParameter.getSNPCaller();
                        edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"Detecting variant calling sensitivity" );
                        this.vcfFile=snpCaller.genotype();
                }
        }
        private void setArtificialSNPNumber(int number) {
                this.artificialSNPNumber = number;
        }

        protected int getArtificialSNPNumber() {
                return this.artificialSNPNumber;
        }
        public double getHomoVariantCallingSensitivty(){
                //Because pos file is generated in genotying ---> get artificial genome ---> artificialSNPGenerator. Then pos file will be set .
                CommonInputFile vcfFile = this.vcfFile;
                String line;
                int snpNumber=0;
                while ((line = vcfFile.readLine()) != null) {
                        String snpVCFChr;
                        int snpVCFPos=0;
                        String variantType;
                        if (line.toCharArray()[0] == '#') {
                                continue;
                        }
                        snpVCFChr=line.split("\t")[0].trim().toString();
                        
                        try {
                            snpVCFPos=Integer.valueOf(line.split("\t")[1].trim().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            CExecutor.stopProgram("\n########################\nCan't read "+this.vcfFile.getFilePath()+"\n\t\tIs a VCF format file?");
                        }
                        if(line.split("\t")[9].contains("1/1")){
                                variantType="1/1";
                        }else continue;
                        SampleArtificialGenome temp=this.genotypingSensitivityDectectorParameter.getSampleArtificialGenomeForDetecting();
                        if(temp.isArtificialSNP(snpVCFChr, snpVCFPos, variantType)){
                                snpNumber=snpNumber+1;
                        }
                }       
                vcfFile.closeInput();
                return (double) snpNumber / this.getArtificialSNPNumber();   
        }
        public double getHeteroVariantCallingSensitivty(){
                //Because pos file is generated in genotying ---> get artificial genome ---> artificialSNPGenerator. Then pos file will be set .
                CommonInputFile vcfFile = this.vcfFile;
                String line;
                int snpNumber=0;
                while ((line = vcfFile.readLine()) != null) {
                        String snpVCFChr;
                        int snpVCFPos;
                        String variantType;
                        if (line.toCharArray()[0] == '#') {
                                continue;
                        }
                        snpVCFChr=line.split("\t")[0].trim().toString();
                        snpVCFPos=Integer.valueOf(line.split("\t")[1].trim().toString());
                        if(line.split("\t")[9].contains("0/1")){
                                variantType="0/1";
                        }else continue;
                        SampleArtificialGenome temp=this.genotypingSensitivityDectectorParameter.getSampleArtificialGenomeForDetecting();
                        if(temp.isArtificialSNP(snpVCFChr, snpVCFPos, variantType)){
                                snpNumber=snpNumber+1;
                        }
                }       
                vcfFile.closeInput();
                return (double) snpNumber / this.getArtificialSNPNumber();   
        }
        private void setCallerScript(String script){
                this.callerScript=script;
        }

        private String getCallerScriptFile(){
                return this.callerScript;
        }
}
