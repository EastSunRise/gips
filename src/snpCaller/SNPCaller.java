/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snpCaller;

import common.CExecutor;
import file.AbstractFile;
import file.CommonInputFile;
import file.Config;
import file.FileFactory;
import parameter.SampleParameterBag;

/**
 *
 * @author sss
 */
public class SNPCaller {

        private AbstractFile artificialReadsFile;
        private String sampleIntermediateFileFolderPath;
        private String sampleName;
        private String callerScript;

        public SNPCaller(AbstractFile artificialReadsFile, String sampleName,String callerScript) {
                this.setAritificialReadsFile(artificialReadsFile);
                this.sampleName = sampleName;
                this.sampleIntermediateFileFolderPath = SampleParameterBag.getIntermediateFilePath() + System.getProperty("file.separator") + sampleName;
                this.callerScript=callerScript;
        }

        private void setAritificialReadsFile(AbstractFile file) {
                this.artificialReadsFile = file;
        }

        public CommonInputFile genotype() {
                CExecutor executor = new CExecutor();
                String samPath=this.artificialReadsFile.getFilePath();
                String vcfPath=this.sampleIntermediateFileFolderPath + System.getProperty("file.separator") + Config.getItem("AR_VAR.VCF");
                executor.execute(this.callerScript+" "+samPath+" "+vcfPath);
                String errorInfo=executor.getErroInformation().replace("\n\n", "\n");
                if(errorInfo.contains("Permission denied")){
                        executor.execute("chmod 755 "+this.callerScript);
                        common.CExecutor.println(common.CExecutor.getRunningTime()+"chmod 755 "+this.callerScript);
                        executor.execute(this.callerScript+" "+samPath+" "+vcfPath);
                        errorInfo=executor.getErroInformation().replace("\n\n", "\n");
                        common.CExecutor.println(errorInfo);

                }else{
                        common.CExecutor.println(errorInfo);

                }
                CommonInputFile file = FileFactory.getInputFile(vcfPath, "VCF");
                return file;
        }
}
