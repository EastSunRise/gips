/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parameter;

import file.CommonInputFile;
import file.Config;
import file.FileFactory;
import genome.effectiveRegion.GenomeEffectiveRegion;
import genome.gffGenome.Genome;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import options.Init;
import variant.SampleVariant;

/**
 *
 * @author sss
 */
public class GlobalParameter {
        private static String projectName;
        private static String genomeVersion;
        private String callerScriptPath;
        private String matrix="DEFAULT";
        private static String snpEffPath;
        private static file.CommonInputFile genomeGffFile;
        private static String effectiveRegion="CDS|SpliceSite=2";
        private static String filters="EBA";
        private static int anticipation=0;
        private int artificialSNPNumber=10000;
        private static String clinicalVariantLibrary;
        private static file.CommonInputFile homosapiensGffFile;
        //not in .ini file, but needed here
        private String controlFilePath;
        private int snpDensity=3;
        private static String toolType="gips";
        private static String cmdStr;
        private static int sampleNumber=0;
        private int maxAAsimilarityScore=0;
        
        public GlobalParameter(HashMap<String,String> map) {
                ParameterList pl=new ParameterList();
                for(Map.Entry<String,String> entry:map.entrySet()){
                        String item=entry.getKey().trim();
                        String info=entry.getValue().trim();
                        pl.isInGlobParaList(item);
                        switch (item) {
                                case "PROJECT" :{
                                        projectName=info;
                                        Init.setProjectName(info);
                                        break;
                                }
                                case "SNPEFF_GENOME_VERSION": {
                                        if(info==null)common.CExecutor.stopProgram("Please set SNPEFF_GENOME_VERSION");
                                        genomeVersion=info;
                                        break;
                                }
                                case "REF_GENOME_ANNOTATION.GFF":{
                                        if(info==null)common.CExecutor.stopProgram("Please set REF_GENOME_ANNOTATION.GFF");
                                        if(!info.contains(common.CExecutor.getFileSeparator())){
                                                info=Init.getRefDirectory()+common.CExecutor.getFileSeparator()+info;
                                        }
                                        genomeGffFile=FileFactory.getInputFile(info,"GFF");
                                        break;
                                }
                                case "VAR_CALL_SCRIPT":{
                                        if(!info.contains(common.CExecutor.getFileSeparator())){
                                                info=Init.getScriptDirectory()+common.CExecutor.getFileSeparator()+info;
                                        }
                                        new CommonInputFile(info).readLine();
                                        callerScriptPath=info;
                                        break;
                                }
                                case "SCORE_MATRIX":{
                                        matrix=info;
                                        break;
                                }                                        
                                case "EFF_REGION":{
                                        if(info!=null&&!info.isEmpty())effectiveRegion=info;
                                        break;
                                }
                                case "VAR_FILTERS":{
                                        if(info!=null&&!info.isEmpty())filters=info;
                                        break;
                                }
                                case "CANDIDATE_CRITERIA":{
                                        try {
                                                if(info!=null&&!info.isEmpty())anticipation=Integer.parseInt(info);
                                                break;                                        
                                        } catch (Exception e) {
                                                
                                        }
                                }
                                case "NUM_SIM_SNPS":{
                                        if(info!=null&&!info.isEmpty())artificialSNPNumber=Integer.parseInt(info);
                                        break;
                                }
                                case "SNPEFF":{
                                        if(info==null)common.CExecutor.stopProgram("Please set SnpEff folder path ");                                        
                                        if(info.trim().endsWith(".jar"))common.CExecutor.stopProgram("Please set SnpEff folder path,not a jar file ");                                        
                                        snpEffPath=info;
                                        break;
                                }
                                case "HUMAN_GRCH37_ANNOTATION.GFF":{
                                        if(info==null)common.CExecutor.stopProgram("Please set Genome.gff");
                                        homosapiensGffFile=FileFactory.getInputFile(entry.getValue(),"GFF");
                                        break;
                                }
                                case "LIB_PHENOTYPE_VAR":{
                                        clinicalVariantLibrary=info;
                                        break;
                                }
                                case "CONTROL" :{
                                        controlFilePath=info;
                                        break;
                                }        
                                case  "MAX_VAR_DENSITY": {
                                        if(info!=null&&!info.isEmpty())snpDensity=Integer.parseInt(entry.getValue());
                                        break;                                
                                } 
                                case  "MAX_AA_SCORE": {
                                        if(info!=null&&!info.isEmpty())this.maxAAsimilarityScore=Integer.parseInt(entry.getValue());
                                        break;                                
                                }         
                                        
                                        
                                        
                        }
                        
                }
                
        }
        
        public GlobalParameter() {
        }
        
        
        
        
        
        public static String getGenomeVersion(){
                if(genomeVersion==null){
                        common.CExecutor.stopProgram("Please set SNPFF_GENOME_VERSION");
                }
                return genomeVersion.trim();
        }
        public static String getProjectName(){
                return projectName;
        }
        
        String getCallerScriptPath(){
                return this.callerScriptPath;
        }
        String getMatrix(){
                return this.matrix;
        }
        int getArtificialSNPNumber(){
                return this.artificialSNPNumber;
        }
        public static int getAnticipation(){
                if(anticipation==0){
                        anticipation=sampleNumber;
                }
                return anticipation;
        }
        public static CommonInputFile getSampleGffFile(){
                if(genomeGffFile==null)common.CExecutor.stopProgram("Please set \"REF_GENOME_ANNOTATION.GFF\"");
                return genomeGffFile;
        }
        public static String getEffectiveRegionString(){
                return effectiveRegion;
        }
        public static EffectiveRegionParameter getEffectiveRegionParameter() {
                return new EffectiveRegionParameter(effectiveRegion);
        }

        public static GenomeEffectiveRegion getGenomeEffectiveRegion(CommonInputFile file) {
                GenomeEffectiveRegion genomeEffectiveRegion;
                LinkedList<String> genomeInformation = new LinkedList<>();
                String line = null;
                genome.gffGenome.Genome genome = null;
                try {
                        while ((line = file.readLine()) != null) {//add all the genome information to LinkedList
                                if (line.startsWith("#")) {
                                        continue;
                                }
                                //contig or mitochondiron do not consider
                                if(line.startsWith("NT_"))continue;
                                if(line.startsWith("NW_"))continue;
                                if(line.startsWith("NC_012920"))continue;
                                if(line.contains("#FASTA")) break;
                                genomeInformation.add(line);
                        }
                        genome = new Genome(GlobalParameter.getGenomeVersion(), genomeInformation);
                        genomeInformation = null;//release 
                        file.closeInput();
                } catch (Exception ex) {
                        common.CExecutor.println("Genome could not be established!");
                }
                genomeEffectiveRegion=genomeEffectiveRegion = new GenomeEffectiveRegion(GlobalParameter.getEffectiveRegionParameter(), genome);
                return genomeEffectiveRegion;
        }    
        public static FilterParameter getFilterParameter() {
                FilterParameter filterParameter=new FilterParameter();
                filterParameter.setFilterStrategy(filters);
                return filterParameter;
        }
        public static CommonInputFile getHomoSapiensGenomeGffFile(){
                if(homosapiensGffFile==null)common.CExecutor.stopProgram("Please set \"HUMAN_GRCH37_ANNOTATION.GFF\""
                        + "\n Available in http://ftp.ncbi.nih.gov//genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/GFF/ref_GRCh37.p13_top_level.gff3.gz");
                return homosapiensGffFile;
        }
        public  SampleVariant getClinicalVariant(){
                CommonInputFile file=null;
                if(clinicalVariantLibrary.endsWith(Config.getItem("CLIN_VAR_LIB"))){
                        InputStream is=this.getClass().getResourceAsStream(Config.getItem("CLIN_VAR_LIB"));
                        BufferedReader br=new BufferedReader( new InputStreamReader(is));
                        file=FileFactory.getInputFile(br, "VCF");
                }else{
                        file=FileFactory.getInputFile(clinicalVariantLibrary, "VCF");
                }
                String clinicalVarSampleName=Config.getItem("CLIN_VAR_NAME");
                SampleVariant sampleVariant=new SampleVariant(clinicalVarSampleName);
                sampleVariant.setVCFFile(file);
                snpAnnotationTools.SNPAnnotationTool snpAnnotationTool = new snpAnnotationTools.SNPAnnotationToolFactory().createSNPAnnotationTool("snpEff");
                sampleVariant=snpAnnotationTool.getSampleAnnotatedVariant(clinicalVarSampleName,file);
                return sampleVariant;
        }
        String getControlFilePath(){
                return this.controlFilePath;
        }
        int getSNPDensity(){
                return this.snpDensity;
        }
        public static void setToolType(String type){
                HashSet<String> tools=new HashSet<String>(){
                    {
                            add("gips");
                            add("filter");
                            add("vcs");
                    };
                                
                };
                if(!tools.contains(type.trim())){
                        common.CExecutor.stopProgram("Do not find tool ["+type+"]");
                }
                toolType=type;
        }
        public static String getToolType(){
                return toolType;
        }
        public static String getSNPEffPath(){
                return snpEffPath;
        }
        public static int getThreadsNumber(){
                return Integer.parseInt(Config.getItem("THREADS"));
        }
        public static String getFilersString(){
                return filters;
        }
        public static String dumpDataForTracing(){
                StringBuffer sb=new StringBuffer();
                sb.append("[GLOBAL]");
                sb.append("\nPROJECT:"+projectName);
                sb.append("\nSNPEFF_GENOME_VERSION:"+genomeVersion);
                if(genomeGffFile!=null){
                        sb.append("\nREF_GENOME_ANNOTATION.GFF:"+genomeGffFile.getFilePath());
                }
                sb.append("\nEFF_REGION:"+effectiveRegion);
                sb.append("\nVAR_FILTERS:"+filters);
                sb.append("\nCANDIDATE_CRITERIA:"+getAnticipation());
                sb.append("\nSNPEFF:"+snpEffPath);
                if(homosapiensGffFile!=null) sb.append("\nHUMAN_GRCH37_ANNOTATION.GFF:"+homosapiensGffFile.getFilePath());
                sb.append("\nLIB_PHENOTYPE_VAR:"+clinicalVariantLibrary);
                return sb.toString();
        }
        public static void setSampleNumber(int number){
                sampleNumber=number;
        }
        public int getMaxAASimilarityScore(){
                return this.maxAAsimilarityScore;
        }
}
