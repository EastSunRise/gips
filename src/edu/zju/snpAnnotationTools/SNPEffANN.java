/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.snpAnnotationTools;

import edu.zju.common.CExecutor;
import edu.zju.common.RunnablePrinter;
import edu.zju.file.Config;
import edu.zju.parameter.GlobalParameter;
import java.io.File;

/**
 *
 * @author Zhongxu Zhu
 */
public class SNPEffANN extends edu.zju.snpAnnotationTools.SNPEff {

        public SNPEffANN() {
                this.effectField="ANN";
        }
        
        
        @Override
        protected String annotateSampleVcfFile(String vcfPath,String targetFilePath){
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
               //start a thread. tell user software not  be blocked
               CExecutor.print(CExecutor.getRunningTime());
               RunnablePrinter printer=new RunnablePrinter();
               printer.setWelcomeWords("Annotating "+vcfPath+"  ");
               printer.setContent2Print("*");
               Thread printerThread=new Thread(printer);
               printerThread.start();
               //check whether genome database has been already downloaded
               String predictBinPath=this.getSNPEffData_dir()+CExecutor.getFileSeparator()+this.genomeVersion+CExecutor.getFileSeparator()+"snpEffectPredictor.bin";
               if(!new File(predictBinPath).isFile()&&
                       !this.genomeVersion.equals(Config.getItem("CLIN_VAR_NAME").trim())){
                       CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"The first time to use SNPEff for "+this.genomeVersion+" may take some time, "
                               + "because SNPEff need to download corresponding genome database.\n"
                               + edu.zju.common.CExecutor.getRunningTime()+"User can also download manually by running\n"
                               + edu.zju.common.CExecutor.getRunningTime()+"cd "+this.snpEffPath+"\njava -jar snpEff.jar download -v "+this.genomeVersion);
               
               }
               //must cd first?
               executor.execute("cd " + this.snpEffPath + "\n java -Xms4G -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar ann -onlyProtein -noMotif -noNextProt -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath + '\n');
               String erroInf = executor.getErroInformation();
               if(erroInf==null) return targetFilePath;
               if (erroInf.contains("genome' not found")) {
                       CExecutor.stopProgram("Genome not found, available genome can be found in website snpeff.sourceforge.net or run java -jar snpEff.jar databases");
               } else if(erroInf.contains("OutOfMemoryError")){
                       executor.execute("cd " + this.snpEffPath + "\n java -Xms6G -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar ann -onlyProtein -noMotif -noNextProt -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath + '\n');
                       if(executor.getErroInformation().contains("OutOfMemoryErro")){
                            CExecutor.stopProgram("Out of meory error occurs for snpeff.jar\nPlease run the following command, set the file path in sample specific section and rerun GIPS\n"
                               + "cd " + this.snpEffPath + "\n java -Xms4G -jar " + this.snpEffPath + System.getProperty("file.separator") + "snpEff.jar ann -onlyProtein -noMotif -noNextProt -v " + this.genomeVersion + " " + vcfPath + "  > " + targetFilePath );   
                       }
               } else{
                       //CExecutor.print(erroInf);
               }
               CExecutor.println("");
               printerThread.stop();
               return targetFilePath;
        }
        

        
        
}
