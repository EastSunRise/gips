/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.snpAnnotationTools;

import com.sun.jmx.snmp.daemon.SnmpInformRequest;
import edu.zju.common.CExecutor;
import edu.zju.common.FileHandler;
import edu.zju.common.LineHandler;
import edu.zju.common.ZipUtil;
import edu.zju.file.CommonInputFile;
import edu.zju.file.Config;
import edu.zju.file.FileFactory;
import edu.zju.file.FileFolder;
import edu.zju.matrix.AA2Symbol;
import edu.zju.parameter.GlobalParameter;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.variant.ClinSigSNP;
import edu.zju.variant.SNP;
import edu.zju.variant.SNPAnnotation;
import edu.zju.variant.SampleSNP;
import edu.zju.variant.SampleVariant;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

/**
 *
 * @author sss
 */
public class SNPEff extends edu.zju.snpAnnotationTools.SNPAnnotationTool{
        protected String sampleName;
        protected String annotationToolName = "SNP effect predictor: snpEff";
        protected String snpEffPath;
        protected String genomeVersion;
        private HashSet<String> highRisk;
        private HashSet<String> pastEff;
        protected String effectField;
        /**In old version
         * Impact Effects High SPLICE_SITE_ACCEPTOR SPLICE_SITE_DONOR START_LOST
         * EXON_DELETED FRAME_SHIFT STOP_GAINED STOP_LOST RARE_AMINO_ACID
         *
         * Moderate NON_SYNONYMOUS_CODING CODON_CHANGE CODON_INSERTION
         * CODON_CHANGE_PLUS_CODON_INSERTION CODON_DELETION
         * CODON_CHANGE_PLUS_CODON_DELETION UTR_5_DELETED UTR_3_DELETED
         *
         * Low SYNONYMOUS_START NON_SYNONYMOUS_START START_GAINED
         * SYNONYMOUS_CODING SYNONYMOUS_STOP
         *
         * Modifier UTR_5_PRIME UTR_3_PRIME REGULATION UPSTREAM DOWNSTREAM GENE
         * TRANSCRIPT EXON INTRON_CONSERVED INTRON INTRAGENIC INTERGENIC
         * INTERGENIC_CONSERVED NONE CHROMOSOME CUSTOM CDS
         *
         */
        
        public SNPEff() {
                this.snpEffPath = GlobalParameter.getSNPEffPath();
                this.highRisk = new HashSet<String>() {
                        {
                                add("SPLICE_SITE_ACCEPTOR");
                                add("SPLICE_SITE_DONOR");
                                add("START_LOST");
                                add("EXON_DELETED");
                                add("FRAME_SHIFT");
                                add("STOP_GAINED");
                                add("RARE_AMINO_ACID");
                                add("CODON_INSERTION");
                                add("CODON_DELETION");
                                //the following new effect seq ontology is used in new snpeff version
                                add("chromosome");
                                add("exon_loss_variant");
                                add("frameshift_variant");
                                add("rare_amino_acid_variant");
                                add("splice_acceptor_variant");
                                add("splice_donor_variant");
                                add("start_lost");
                                add("stop_gained");
                                add("stop_lost");
                        }
                ;
                };             
              this.pastEff = new HashSet<String>() {
                        {
                                add("UTR_5_PRIME");
                                add("UTR_3_PRIME");
                                add("REGULATION");
                                add("UPSTREAM");
                                add("DOWNSTREAM");
                                add("GENE");
                                add("TRANSCRIPT");
                                add("EXON");
                                add("INTRON_CONSERVED");
                                add("INTRON");
                                add("INTRAGENIC");
                                add("INTERGENIC_CONSERVED");
                                add("NONE");
                                add("CHROMOSOME");
                                add("CUSTOM");
                                add("CDS");
                                //the following new effect is used in new snpeff version
                                add("downstream_gene_variant");
                                add("upstream_gene_variant");
                                add("intron_variant");
                                add("intergenic_region");
                                add("5_prime_UTR_variant");
                                add("splice_region_variant");
                                add("intron_variant");
                                add("5_prime_UTR_premature_start_codon_gain_variant");
                                add("3_prime_UTR_variant");
                                //
                                add("intragenic_variant");
                                add("sequence_feature");
                        }
                };
                
                
        }
        
        
        
        
        /**
         * 
         * @param sampleName ClinSigVar sample's name is Config.getItem("CLIN_VAR_NAME")
         * @param file
         * @return 
         */
        @Override
        public SampleVariant getSampleAnnotatedVariant(String sampleName, CommonInputFile file) {
                this.sampleName = sampleName;
                SampleVariant sampleVariant = new SampleVariant(sampleName);
                CommonInputFile vcfAnnotatedFile = null;
                vcfAnnotatedFile = this.annotateVCFFile(sampleName, file);
                sampleVariant.setVCFFile(vcfAnnotatedFile);
                String line = null;
                LineHandler lineHandler = new LineHandler();
                while ((line = vcfAnnotatedFile.readLine()) != null) {
                        if (line.trim().isEmpty()||line.toCharArray()[0] == '#') {
                                continue;
                        }
                        SNP snp;
                        if (!sampleName.equals(Config.getItem("CLIN_VAR_NAME"))) {
                                snp = new SampleSNP(line, sampleName);
                        } else {
                                snp = new ClinSigSNP(line, sampleName);
                        }
                        if(line.contains("EFF=")){
                                String eff=lineHandler.regexMatch(line, "EFF=(.*\\))");
                                snp.setSNPRawInfoBeforeAnnotate(line.replace("EFF="+eff,""));
                                snp.setAnnotationFieldInfo(eff);
                                snp = this.annotate1SNPByEff(snp);
                        }else if(line.contains("ANN=")){
                                String ann=lineHandler.regexMatch(line, "ANN=(.*\\t)");
                                snp.setSNPRawInfoBeforeAnnotate(line.replace("ANN="+ann,""));
                                snp.setAnnotationFieldInfo(ann);
                                snp = this.annotate1SNPByAnn(snp);
                        }else{
                                String eff=lineHandler.regexMatch(line, "EFF=(.*\\))");
                                snp.setSNPRawInfoBeforeAnnotate(line.replace("EFF="+eff,""));
                                snp.setAnnotationFieldInfo(eff);
                                snp = this.annotate1SNPByEff(snp);
                        }
                        sampleVariant.addSNP(snp);
                }
                return sampleVariant;
        } 
        /**
         * overwrite
         *
         * @param sampleName
         * @param vcfFilePath
         * @return
         */
        @Override
        protected CommonInputFile annotateVCFFile(String sampleName, CommonInputFile file) {
                String line;
                while ((line = file.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                                continue;
                        }
                        if (line.toCharArray()[0] == '#') {
                                continue;
                        }
                        if (line.contains("EFF=")||line.contains("ANN=")) {
                                file.closeInput();
                                return file;
                        }
                }
                String targetPath = SampleParameterBag.getIntermediateFilePath() + System.getProperty("file.separator") + sampleName + System.getProperty("file.separator") + sampleName + ".eff.vcf";
                targetPath=this.annotateSampleVcfFile(file.getFilePath(), targetPath);
                return FileFactory.getInputFile(targetPath, "VCF");
        }
        
        /**
         * To be overrided by SNPEffEFF and SNPEffANN
         * @param filePath
         * @param targetPath
         * @return 
         */
        protected String annotateSampleVcfFile(String filePath,String targetPath){
                return null;
        }
        
        
        /**
         * 
         * @param effInfo
         * @param snp correspond with SNP class
         * @return 
         */
        private SNP annotate1SNPByEff(SNP snp){
                LineHandler lineHandler=new LineHandler();
                String effInfo= snp.getAnnotationFieldInfo();
                //not in coding region
                if (effInfo == null) {
                        SNPAnnotation temp = new SNPAnnotation();
                        temp.setPassBigDifferenceFilter();
                        snp.addSNPAnnotation(temp);
                        return snp;
                }
                //note: if snp has no annotation, it will be filtered by bigdifference
                for (int i = 0; i < effInfo.split(",").length; i++) {
                        SNPAnnotation snpAnnotation = new SNPAnnotation();
                        String effectInfo = effInfo.split(",")[i];
                        String geneName = effectInfo.split("\\|")[5];
                        snpAnnotation.setItsGeneName(geneName);
                        String effect = effectInfo.split("\\(")[0];
                        snpAnnotation.setEffect(effect);
                        if (this.pastEff.contains(effect)) {
                                // modified along with BigDifferenceFilter how to filter SNP by annotation
                                snpAnnotation.setPassBigDifferenceFilter();
                                snp.addSNPAnnotation(snpAnnotation);
                                continue;
                        }
                        if (this.highRisk.contains(effect)) {
                                snpAnnotation.setHighRisk();
                                snp.addSNPAnnotation(snpAnnotation);
                                continue;
                        }
                        String codonChange = effectInfo.split("\\|")[3];
                        String originAA = null, mutationAA = null;
                        String middle = lineHandler.regexMatch(codonChange, "(\\d+)");
                        //in D45G,  middle means 45
                        if (middle == null) {
                                snp.addSNPAnnotation(snpAnnotation);
                                continue;
                        }
                        String temp[] = codonChange.split(middle);
                        if (temp.length == 1) {
                                originAA = mutationAA = codonChange.split(middle)[0];
                        } else {
                                originAA = temp[0];
                                mutationAA = temp[1];
                        }
                        if (originAA != null && mutationAA != null) {
                                snpAnnotation.setAA_Symbol_Change(originAA.toUpperCase(), mutationAA.toUpperCase());
                        }
                        snp.addSNPAnnotation(snpAnnotation);
                }
                return snp;
        }
        private SNP annotate1SNPByAnn( SNP snp){
                LineHandler lineHandler=new LineHandler();
               // String annInfo = lineHandler.regexMatch(snp.getSNPInfoInVcf(), "ANN=(.*\\|)");
                String annInfo=snp.getAnnotationFieldInfo();
                //not in coding region
                if (annInfo == null) {
                        SNPAnnotation temp = new SNPAnnotation();
                        temp.setPassBigDifferenceFilter();
                        snp.addSNPAnnotation(temp);
                        return snp;
                }
                AA2Symbol a2Symbol=new AA2Symbol();
                //note: if snp has no annotation, it will be filtered by bigdifference
                for (int i = 0; i < annInfo.split(",").length; i++) {
                        SNPAnnotation snpAnnotation = new SNPAnnotation();
                        String annField = annInfo.split(",")[i];
                        String effect = annField.split("\\|")[1];
                        
                        
                        
                        snpAnnotation.setEffect(effect);
                        // length may less than 4, e.g.  
                        //string "C|intergenic_region|MODIFIER|||||||||||||" was split by "\\|", the result arrary length is 3
                        //do not need this type annotation that without gene name information
                        String geneName=null;
                        try {
                                geneName = annField.split("\\|")[3];
                        } catch (Exception e) {
                                snpAnnotation.setItsGeneName("");
                                snpAnnotation.setPassBigDifferenceFilter();
                                snp.addSNPAnnotation(snpAnnotation);
                                continue;
                        }
                        snpAnnotation.setItsGeneName(geneName);
                        // whether to filter was determined by effective region filter
                        //multiple effects are linked with '&'
                        boolean is=true;
                        for(int j=0;j<effect.split("&").length;j++){
                                if(!this.pastEff.contains(effect.split("&")[j].trim())){
                                        is=false;
                                }
                        }
                        if(is){// all in effPass set
                                 // modified along with BigDifferenceFilter how to filter SNP by annotation
                                snpAnnotation.setPassBigDifferenceFilter();
                                snp.addSNPAnnotation(snpAnnotation);
                                continue;      
                        }
                        is=false;
                        
                        for(int j=0;j<effect.split("&").length;j++){
                                if(this.highRisk.contains(effect.split("&")[j].trim())){
                                        snpAnnotation.setHighRisk();
                                        snp.addSNPAnnotation(snpAnnotation);
                                        is=true;
                                        break;
                                }
                        }
                        if(is){//if in high risk set
                                continue;
                        }
                        // hit a transcript  e.g.  T|transcript|MODIFIER|TTLL10|ENSG00000162571|transcript|ENST00000379289|protein_coding||c.*85_*2173delACAAAC||||||
                        String codonChange=null;
                        try {
                                codonChange=annField.split("\\|")[10];
                        } catch (Exception e) {
                            snpAnnotation.setPassBigDifferenceFilter();
                            snp.addSNPAnnotation(snpAnnotation);
                            continue;
                        }
                        if(codonChange.contains("del")||codonChange.contains("ins")||codonChange.contains("dup")||codonChange.contains("?")){
                                snp.addSNPAnnotation(snpAnnotation);continue;
                        }
                        String originAA = null, mutationAA = null;
                        codonChange=codonChange.replace("p.", "");
                        String middle = lineHandler.regexMatch(codonChange, "(\\d+)");
                        //D45G  middle means45
                        String temp[]=null;
                        try {
                            temp= codonChange.split(middle);
                        } catch (Exception e) {//e.g. A|transcript|MODIFIER|DIDO1|ENSG00000101191|transcript|ENST00000354665|protein_coding||c.-205_-200+1delCCTGCGG||||||;LOF=(DIDO1|ENSG00000101191|9|
                            snpAnnotation.setPassBigDifferenceFilter();
                            snp.addSNPAnnotation(snpAnnotation);
                            continue;
                        }
                        if (temp.length == 1) {//synonymous mutation
                                originAA = mutationAA = codonChange.split(middle)[0];
                        } else {
                                originAA = temp[0];
                                mutationAA = temp[1];
                        }
                        
                        if (originAA != null && mutationAA != null) {
                                originAA=a2Symbol.getSymbol(originAA);
                                mutationAA=a2Symbol.getSymbol(mutationAA);
                                snpAnnotation.setAA_Symbol_Change(originAA.toUpperCase(), mutationAA.toUpperCase());
                        }
                        snp.addSNPAnnotation(snpAnnotation);
                }
                // free memory
                snp.clearAnnotationFieldInfo();
                return snp;
        }
        protected String getSNPEffData_dir(){
                String snpEffConfigFilePath = this.snpEffPath + System.getProperty("file.separator") + "snpEff.config";
                File file = new File(snpEffConfigFilePath);
                if (!file.isFile()) {
                        CExecutor.stopProgram("Do not find snpEff.config in " + this.snpEffPath);
                }
                FileHandler fileHandler = new FileHandler();
                fileHandler.readFile(snpEffConfigFilePath);
                String line = null;
                try {
                        while ((line = fileHandler.br.readLine()) != null) {
                                if (line.trim().startsWith("data_dir")||line.trim().startsWith("data.dir")) {
                                        break;
                                }
                        }
                        fileHandler.br.close();
                } catch (IOException ex) {
                        Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(line==null){
                        CExecutor.stopProgram("Do not find data.dir (after version4 )or data_dir (before version4) field in snpEff.config file");
                }
                String snpEffDataPath = line.split("=")[1].trim();
                if(snpEffDataPath.startsWith(CExecutor.getFileSeparator())){
                
                }else if(snpEffDataPath.startsWith("~")){
                        
                }else{
                        snpEffDataPath=this.snpEffPath+CExecutor.getFileSeparator()+snpEffDataPath;
                }
                return snpEffDataPath;
        }
        protected void addGIPSTestInfoIntoSnpEffConfig(){
                String snpEffConfigPath=GlobalParameter.getSNPEffPath()+System.getProperty("file.separator")+"snpEff.config";
                File file=new File(snpEffConfigPath);
                BufferedReader br=null;
                BufferedWriter bw=null;
                boolean isNeedToAppendInfo=true;
                try {
                        br=new BufferedReader(new FileReader(file));
                        String line;
                        try {
                                while((line=br.readLine())!=null){
                                        if(line.contains("Test.genome")){
                                                isNeedToAppendInfo=false;
                                        }
                                }
                        } catch (IOException ex) {
                                Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                        }
                } catch (FileNotFoundException ex) {
                        Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                }

                if(isNeedToAppendInfo){//add GIPS Test information into snpEff config file
                        try {
                                bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(snpEffConfigPath),true)));
                        } catch (FileNotFoundException ex) {
                                Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                                bw.write("\n# GIPS Test Genome\nTest.genome : Test\n");
                                bw.flush();
                                bw.close();
                                this.buildTestGenomeDatabase();
                        } catch (IOException ex) {
                                Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }
        
        
        protected void buildTestGenomeDatabase(){
                String snpEffFolderPath=GlobalParameter.getSNPEffPath();
                String snpEffDataFolderPath=snpEffFolderPath+System.getProperty("file.separator")+"data";
                //check snpEffFolder exist?
                FileFolder snpEffFolder= new FileFolder(snpEffFolderPath);
                //check data folder exist?
                File snpEffDataFolder=new File(snpEffDataFolderPath);
                if(!snpEffDataFolder.isDirectory()){
                        boolean b=snpEffDataFolder.mkdir();
                        if(!b){
                              CExecutor.stopProgram("Fail to creat \'data\' folder in "+snpEffFolderPath+"\n Please \'mkdir "+snpEffDataFolderPath+'\'');
                        }
                        
                }
                //mkdir /data/Test
                String testFolderPath=this.getSNPEffData_dir()+System.getProperty("file.separator")+GlobalParameter.getGenomeVersion();
                File testFolder=new File(testFolderPath);
                if(!testFolder.isDirectory()){
                        boolean b=testFolder.mkdirs();
                        if(!b){
                              edu.zju.common.CExecutor.stopProgram("Fail to creat \'data\' folder in "+testFolderPath+"\n Please \'mkdir "+testFolderPath+'\'');  
                        }
                }                        
                //copy Test's genome database from resource package
                int temp=0;
                //The snpEff database is named after the item in Config
                String sourceFilePathInPackage = null;
                sourceFilePathInPackage=Config.getItem("Test.Genes.Gff");
                InputStream is=this.getClass().getResourceAsStream(sourceFilePathInPackage);
                File binFile=new File(testFolderPath+System.getProperty("file.separator")+"genes.gff");
                OutputStream os=null;
                try {
                        os= new FileOutputStream(binFile);
                } catch (FileNotFoundException ex) {
                        Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                        while((temp=is.read())!=-1){
                               os.write(temp);
                        }
                        os.flush();
                        os.close();
                        is.close();
                } catch (IOException ex) {
                        Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                }
                CExecutor executor=new CExecutor();
                executor.execute("cd "+this.snpEffPath+"\n java -jar snpEff.jar build -gff3 -v Test\n");
        }
        
        protected void unzipGenomeToDataFile(String path) {
                try {
                        String snpEffDataPath = this.getSNPEffData_dir();
                        if (!new File(snpEffDataPath).isDirectory()) {
                                new File(snpEffDataPath).mkdirs();
                        }
                        new ZipUtil().unzipFiles(new File(path),new File(snpEffDataPath).getParentFile().getAbsolutePath());
                        
                } catch (ZipException ex) {
                        edu.zju.common.CExecutor.stopProgram("Failt unzip "+path);
                        Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                        edu.zju.common.CExecutor.stopProgram("Failt unzip "+path);
                        Logger.getLogger(SNPEffEFF.class.getName()).log(Level.SEVERE, null, ex);
                }        
        }  
       /**
         * 
         * @param vcfPath
         * @param snpEffFilePath
         * @return 
         */
        protected boolean compareTwoFiles(String vcfPath,String snpEffFilePath){
                int number=0;
                CommonInputFile file=FileFactory.getInputFile(vcfPath, "VCF");
                String line;
                //count how many snps in vcf
                while((line=file.readLine())!=null){
                        if(line.startsWith("#"))continue;
                        number++;
                }
                file.closeInput();
                //count how many snps in eff.vcf
                file=FileFactory.getInputFile(snpEffFilePath, "VCF");
                while((line=file.readLine())!=null){
                        if(line.startsWith("#"))continue;
                        number--;
                }
                file.closeInput();
                if(number>=-2&&number<=2){
                        return true;
                }
                return false;
        }
        
}
//in new snpeff version
//Effect	Effect	Note & Example	Impact
//Seq. Ontology	Classic		
//coding_sequence_variant	CDS	The variant hits a CDS.	MODIFIER
//chromosome	CHROMOSOME_LARGE DELETION	A large parte (over 1%) of the chromosome was deleted.	HIGH
//coding_sequence_variant	CODON_CHANGE	One or many codons are changed 	MODERATE
//		e.g.: An MNP of size multiple of 3	
//inframe_insertion	CODON_INSERTION	One or many codons are inserted 	MODERATE
//		e.g.: An insert multiple of three in a codon boundary	
//disruptive_inframe_insertion	CODON_CHANGE_PLUS CODON_INSERTION	One codon is changed and one or many codons are inserted 	MODERATE
//		e.g.: An insert of size multiple of three, not at codon boundary	
//inframe_deletion	CODON_DELETION	One or many codons are deleted 	MODERATE
//		e.g.: A deletion multiple of three at codon boundary	
//disruptive_inframe_deletion	CODON_CHANGE_PLUS CODON_DELETION	One codon is changed and one or more codons are deleted 	MODERATE
//		e.g.: A deletion of size multiple of three, not at codon boundary	
//downstream_gene_variant	DOWNSTREAM	Downstream of a gene (default length: 5K bases)	MODIFIER
//exon_variant	EXON	The variant hits an exon (from a non-coding transcript) or a retained intron.	MODIFIER
//exon_loss_variant	EXON_DELETED	A deletion removes the whole exon.	HIGH
//frameshift_variant	FRAME_SHIFT	Insertion or deletion causes a frame shift 	HIGH
//		e.g.: An indel size is not multple of 3	
//gene_variant	GENE	The variant hits a gene.	MODIFIER
//intergenic_region	INTERGENIC	The variant is in an intergenic region	MODIFIER
//conserved_intergenic_variant	INTERGENIC_CONSERVED	The variant is in a highly conserved intergenic region	MODIFIER
//intragenic_variant	INTRAGENIC	The variant hits a gene, but no transcripts within the gene	MODIFIER
//intron_variant	INTRON	Variant hits and intron. Technically, hits no exon in the transcript.	MODIFIER
//conserved_intron_variant	INTRON_CONSERVED	The variant is in a highly conserved intronic region	MODIFIER
//miRNA	MICRO_RNA	Variant affects an miRNA	MODIFIER
//missense_variant	NON_SYNONYMOUS_CODING	Variant causes a codon that produces a different amino acid 	MODERATE
//		e.g.: Tgg/Cgg, W/R	
//initiator_codon_variant	NON_SYNONYMOUS_START	Variant causes start codon to be mutated into another start codon (the new codon produces a different AA). 	LOW
//		e.g.: Atg/Ctg, M/L (ATG and CTG can be START codons)	
//stop_retained_variant	NON_SYNONYMOUS_STOP	Variant causes stop codon to be mutated into another stop codon (the new codon produces a different AA). 	LOW
//		e.g.: Atg/Ctg, M/L (ATG and CTG can be START codons)	
//rare_amino_acid_variant	RARE_AMINO_ACID	The variant hits a rare amino acid thus is likely to produce protein loss of function	HIGH
//splice_acceptor_variant	SPLICE_SITE_ACCEPTOR	The variant hits a splice acceptor site (defined as two bases before exon start, except for the first exon).	HIGH
//splice_donor_variant	SPLICE_SITE_DONOR	The variant hits a Splice donor site (defined as two bases after coding exon end, except for the last exon).	HIGH
//splice_region_variant	SPLICE_SITE_REGION	A sequence variant in which a change has occurred within the region of the splice site, either within 1-3 bases of the exon or 3-8 bases of the intron.	LOW
//splice_region_variant	SPLICE_SITE_BRANCH	A varaint affective putative (Lariat) branch point, located in the intron.	LOW
//splice_region_variant	SPLICE_SITE_BRANCH_U12	A varaint affective putative (Lariat) branch point from U12 splicing machinery, located in the intron.	MODERATE
//stop_lost	STOP_LOST	Variant causes stop codon to be mutated into a non-stop codon 	HIGH
//		e.g.: Tga/Cga, */R	
//5_prime_UTR_premature start_codon_gain_variant	START_GAINED	A variant in 5'UTR region produces a three base sequence that can be a START codon.	LOW
//start_lost	START_LOST	Variant causes start codon to be mutated into a non-start codon. 	HIGH
//		e.g.: aTg/aGg, M/R	
//stop_gained	STOP_GAINED	Variant causes a STOP codon 	HIGH
//		e.g.: Cag/Tag, Q/*	
//synonymous_variant	SYNONYMOUS_CODING	Variant causes a codon that produces the same amino acid 	LOW
//		e.g.: Ttg/Ctg, L/L	
//start_retained	SYNONYMOUS_START	Variant causes start codon to be mutated into another start codon. 	LOW
//		e.g.: Ttg/Ctg, L/L (TTG and CTG can be START codons)	
//stop_retained_variant	SYNONYMOUS_STOP	Variant causes stop codon to be mutated into another stop codon. 	LOW
//		e.g.: taA/taG, */*	
//transcript_variant	TRANSCRIPT	The variant hits a transcript.	MODIFIER
//regulatory_region_variant	REGULATION	The variant hits a known regulatory feature (non-coding).	MODIFIER
//upstream_gene_variant	UPSTREAM	Upstream of a gene (default length: 5K bases)	MODIFIER
//3_prime_UTR_variant	UTR_3_PRIME	Variant hits 3'UTR region	MODIFIER
//3_prime_UTR_truncation +exon_loss	UTR_3_DELETED	The variant deletes an exon which is in the 3'UTR of the transcript	MODERATE
//5_prime_UTR_variant	UTR_5_PRIME	Variant hits 5'UTR region	MODIFIER
//5_prime_UTR_truncation +exon_loss_variant	UTR_5_DELETED	The variant deletes an exon which is in the 5'UTR of the transcript	MODERATE
//sequence_feature +exon_loss_variant	NEXT_PROT	A 'NextProt' based annotation. Details are provided in the 'feature type' sub-field (ANN), or in the effect details (EFF).	MODERATE