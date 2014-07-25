package edu.zju.snpAnnotationTools;

import edu.zju.common.CExecutor;
import edu.zju.common.FileHandler;
import edu.zju.common.LineHandler;
import edu.zju.file.AbstractFile;
import edu.zju.file.CommonInputFile;
import edu.zju.file.Config;
import edu.zju.file.FileFactory;
import edu.zju.file.FileFolder;
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

/**
 *
 * @author zzx
 */
public class SNPEff extends edu.zju.snpAnnotationTools.SNPAnnotationTool {

        protected String annotationToolName = "SNP effect predictor: snpEff";
        private String snpEffPath;
        private HashSet<String> highRisk;
        private HashSet<String> pastEff;
        private String sampleName;
        private String genomeVersion;

        /**
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
                        }
                };

        }

        /**
         * 
         * @param sampleName ClinSigVar sample's name is HomoSapiens
         * @param file
         * @return 
         */
        @Override
        public SampleVariant getSampleAnnotatedVariant(String sampleName, CommonInputFile file) {
                this.sampleName = sampleName;
                SampleVariant sampleVariant = new SampleVariant(sampleName);
                CommonInputFile vcfAnnotatedFile = null;
                vcfAnnotatedFile = this.annotateSNPInVCFFile(sampleName, file);
                sampleVariant.setVCFFile(vcfAnnotatedFile);
                String line = null;
                LineHandler lineHandler = new LineHandler();
                while ((line = vcfAnnotatedFile.readLine()) != null) {
                        if (line.toCharArray()[0] == '#') {
                                continue;
                        }
                        SNP snp;
                        if (!sampleName.equals(Config.getItem("CLIN_VAR_NAME"))) {
                                snp = new SampleSNP(line, sampleName);
                        } else {
                                snp = new ClinSigSNP(line, sampleName);
                        }
                        String effInfo = lineHandler.regexMatch(line, "EFF=(.*\\))");
                        snp = this.annotate1SNP(effInfo, snp);
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
        protected CommonInputFile annotateSNPInVCFFile(String sampleName, CommonInputFile file) {
                String line;
                while ((line = file.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                                continue;
                        }
                        if (line.toCharArray()[0] == '#') {
                                continue;
                        }
                        if (line.contains("EFF=")) {
                                file.closeInput();
                                return file;
                        }
                }
                String targetPath = SampleParameterBag.getIntermediateFilePath() + System.getProperty("file.separator") + sampleName + System.getProperty("file.separator") + sampleName + ".eff.vcf";
                this.annotateSampleVcfFile(file.getFilePath(), targetPath);
                return FileFactory.getInputFile(targetPath, "VCF");
        }

        private SNP annotate1SNP(String effInfo, SNP snp) {
                LineHandler lineHandler = new LineHandler();
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
                        String originCodon = null, mutationCodon = null;
                        String middle = lineHandler.regexMatch(codonChange, "(\\d+)");
                        //D45G  middle means45
                        if (middle == null) {
                                snp.addSNPAnnotation(snpAnnotation);
                                continue;
                        }
                        String temp[] = codonChange.split(middle);
                        if (temp.length == 1) {
                                originCodon = mutationCodon = codonChange.split(middle)[0];
                        } else {
                                originCodon = temp[0];
                                mutationCodon = temp[1];
                        }
                        if (originCodon != null && mutationCodon != null) {
                                snpAnnotation.setCodonChange(originCodon.toUpperCase(), mutationCodon.toUpperCase());
                        }
                        snp.addSNPAnnotation(snpAnnotation);
                }
                return snp;
        }

        private void annotateSampleVcfFile(String vcfPath, String targetFilePath) {
                if(GlobalParameter.getGenomeVersion().equals("Test")){
                        this.addGIPSTestInfoIntoSnpEffConfig();
                }
                edu.zju.common.CExecutor executor = new CExecutor();
                this.genomeVersion = GlobalParameter.getGenomeVersion();
//                common.CExecutor.println("");
//                common.CExecutor.println("SNPEff runing info(" + this.sampleName + "):");
//                common.CExecutor.println("SnpEff start running");
                executor.execute("cd " + this.snpEffPath + "\n java -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar eff -no-downstream -no-upstream -1 -no-intergenic -no-intron -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath + '\n');

                String erroInf = executor.getErroInformation();
                if (erroInf.contains("ERROR: Cannot read file ") && erroInf.contains("snpEffectPredictor.bin")) {
                        System.out.println(erroInf);
                        edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"SNPEff is downloading genome " + this.genomeVersion + " data");
                        this.downloadGenome();
                        executor.execute("cd " + this.snpEffPath + "\n java -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar eff -no-downstream -no-upstream -1 -no-intergenic -no-intron -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath + '\n');
                } else  if (erroInf.contains("genome' not found")) {
                        CExecutor.stopProgram("Genome not found, please verify in SnpEff website snpeff.sourceforge.net/download.html");
                }
        }
        //must cd first

        private void downloadGenome() {
                edu.zju.common.CExecutor executor = new CExecutor();
                FileFolder folder = new FileFolder(this.snpEffPath);
                AbstractFile temp1[] = folder.getAllFile();
                if(GlobalParameter.getGenomeVersion().trim().equals("Test")){
                        this.addTestGenomeDatabase();
                }else{  
                        this.printNote();
                        executor.execute("cd " + this.snpEffPath + "\njava -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar" + " download " + this.genomeVersion + '\n');
                        String erro = executor.getErroInformation();
                        if (erro.contains("genome' not found")) {
                                CExecutor.stopProgram("Genome not found, please verify in SnpEff website snpeff.sourceforge.net/download.html");
                        }
                        AbstractFile temp2[] = folder.getAllFile();
                        boolean isSameFile = false;
                        for (int i = 0; i < temp2.length; i++) {
                                for (int j = 0; j < temp1.length; j++) {
                                        if (temp2[i].getFileName().equals(temp1[j].getFileName())) {
                                                isSameFile = true;
                                                break;
                                        }
                                }
                                if (!isSameFile) {
                                        this.unzipGenomeToDataFile(temp2[i].getFilePath());
                                        break;
                                }
                                isSameFile = false;
                        }
                        if (isSameFile) {
                                edu.zju.common.CExecutor.println("Please delete the new downloaded file (" + this.genomeVersion + ".zip) in " + this.snpEffPath);
                        }
                }
        }

        private void unzipGenomeToDataFile(String path) {
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
                                if (line.startsWith("data_dir")) {
                                        break;
                                }
                        }
                } catch (IOException ex) {
                        Logger.getLogger(SNPEff.class.getName()).log(Level.SEVERE, null, ex);
                }
                String snpEffDataPath = line.split("=")[1].trim();
                if (!new File(snpEffDataPath).isDirectory()) {
                        new File(snpEffDataPath).mkdirs();
                }
                CExecutor executor = new CExecutor();
                executor.execute("cd " + snpEffDataPath + "\nunzip " + path + "\n");
//not rm the file              
                executor.execute("cd " + snpEffDataPath + "\ncd data\nmv " + this.genomeVersion + " " + snpEffDataPath + "\ncd " + snpEffDataPath + "\n");
//rm the file              
//              executor.execute("cd "+snpEffDataPath+"\ncd data\nmv "+this.genomeVersion+" "+snpEffDataPath+"\ncd "+snpEffDataPath+"\nrm -r data\n");
        }

        private void printNote() {
                edu.zju.common.CExecutor.println("This process may take for a while, but if the download has not been finished for a long tiem, please stop GIPS and execut the following command:\n"
                        + "cd " + this.snpEffPath + "\njava -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar" + " download " + this.genomeVersion + '\n'
                        + "Don't not forget unzip the file and rerun GIPS.");
        }
        
        private void addTestGenomeDatabase(){
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
                String testFolderPath=snpEffDataFolderPath+System.getProperty("file.separator")+GlobalParameter.getGenomeVersion();
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
                InputStream is=this.getClass().getResourceAsStream(Config.getItem("Test.genome"));
                File binFile=new File(testFolderPath+System.getProperty("file.separator")+"snpEffectPredictor.bin");
                OutputStream os=null;
                try {
                        os= new FileOutputStream(binFile);
                } catch (FileNotFoundException ex) {
                        Logger.getLogger(SNPEff.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                        while((temp=is.read())!=-1){
                               os.write(temp);
                        }
                        os.flush();
                        os.close();
                        is.close();
                } catch (IOException ex) {
                        Logger.getLogger(SNPEff.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        private void addGIPSTestInfoIntoSnpEffConfig(){
                String snpEffConfigPath=GlobalParameter.getSNPEffPath()+System.getProperty("file.separator")+"snpEff.config";
                File file=new File(snpEffConfigPath);
                BufferedReader br=null;
                BufferedWriter bw=null;
                boolean isAppendInfo=true;
                try {
                        br=new BufferedReader(new FileReader(file));
                        String line;
                        try {
                                while((line=br.readLine())!=null){
                                        if(line.contains("Test.genome")){
                                                isAppendInfo=false;
                                        }
                                }
                        } catch (IOException ex) {
                                Logger.getLogger(SNPEff.class.getName()).log(Level.SEVERE, null, ex);
                        }
                } catch (FileNotFoundException ex) {
                        Logger.getLogger(SNPEff.class.getName()).log(Level.SEVERE, null, ex);
                }

                if(isAppendInfo){//add GIPS Test information into snpEff config file
                        try {
                                bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(snpEffConfigPath),true)));
                        } catch (FileNotFoundException ex) {
                                Logger.getLogger(SNPEff.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                                bw.write("\n# GIPS Test Genome\nTest.genome : Test\n");
                                bw.flush();
                                bw.close();
                                this.addTestGenomeDatabase();
                        } catch (IOException ex) {
                                Logger.getLogger(SNPEff.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }

}
