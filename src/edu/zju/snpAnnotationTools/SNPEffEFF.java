package edu.zju.snpAnnotationTools;

import edu.zju.common.CExecutor;
import edu.zju.file.AbstractFile;
import edu.zju.file.Config;
import edu.zju.file.FileFolder;
import edu.zju.parameter.GlobalParameter;
import java.io.File;

/**
 *
 * @author zzx
 */
public class SNPEffEFF extends edu.zju.snpAnnotationTools.SNPEff {

        public SNPEffEFF() {
                this.effectField="EFF";
        }
        
   
        @Override
        protected String annotateSampleVcfFile(String vcfPath, String targetFilePath)  {
                if(GlobalParameter.getGenomeVersion().equals("Test")){
                        this.addGIPSTestInfoIntoSnpEffConfig();
                }
                edu.zju.common.CExecutor executor = new CExecutor();
                if(this.sampleName.equals(Config.getItem("CLIN_VAR_NAME").trim())){
                        targetFilePath=vcfPath.replace(".vcf", ".eff.vcf");
                        
                        this.genomeVersion=GlobalParameter.getLibVarGenomeVersion();
               }else{
                        this.genomeVersion = GlobalParameter.getGenomeVersion();
               }
                //If the eff file already exist, return directly
               if(new File(targetFilePath).exists()){
                         if(this.compareTwoFiles(vcfPath, targetFilePath)){
                                return targetFilePath;
                         }
               }
                executor.execute("cd " + this.snpEffPath + "\n java -Xmx4G -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar eff -no-downstream -no-upstream -1 -no-intergenic -no-utr -no-intron -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath + '\n');
                String erroInf = executor.getErroInformation();
                if (erroInf.contains("ERROR: Cannot read file ") && erroInf.contains("snpEffectPredictor.bin")) {
                        edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"SNPEff is downloading genome " + this.genomeVersion + " data");
                        this.downloadGenome();
                        executor.execute("cd " + this.snpEffPath + "\n java -jar -Xmx5G " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar eff -no-downstream -no-upstream -1 -no-intergenic -no-intron -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath + '\n');
                } else  if (erroInf.contains("genome' not found")) {
                        CExecutor.stopProgram("Genome not found, available genome can be found in website snpeff.sourceforge.net or run java -jar snpEff.jar databases");
                } else if(erroInf.contains("OutOfMemoryError")){
                        executor.execute("cd " + this.snpEffPath + "\n java -Xmx6G -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar eff -no-downstream -no-upstream -1 -no-intergenic -no-intron -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath + '\n');
                        if(executor.getErroInformation().contains("OutOfMemoryErro")){
                             CExecutor.stopProgram("Out of meory error occurs for snpeff.jar\nPlease run the following command, set the file path in sample specific section and rerun GIPS\n"
                                + "cd " + this.snpEffPath + "\n java -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar eff -no-downstream -no-upstream -1 -no-intergenic -no-intron -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath);   
                        }
                } else{
                        //CExecutor.print(erroInf);
                }
                return targetFilePath;
        }
        
        /**
         * must cd first
         * 
        */
        private void downloadGenome() {
                edu.zju.common.CExecutor executor = new CExecutor();
                FileFolder folder = new FileFolder(this.snpEffPath);
                AbstractFile temp1[] = folder.getAllFile();
                if(this.genomeVersion.trim().equals("Test")){
                        this.buildTestGenomeDatabase();
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



        private void printNote() {
                edu.zju.common.CExecutor.println("This process may take for a while, but if the download has not been finished for a long tiem, please stop GIPS and execut the following command:\n"
                        + "cd " + this.snpEffPath + "\njava -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar" + " download " + this.genomeVersion + '\n'
                        + "Don't not forget unzip the file and rerun GIPS.");
        }


        

        

}
