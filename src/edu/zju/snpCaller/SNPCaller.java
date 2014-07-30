package edu.zju.snpCaller;

import edu.zju.common.CExecutor;
import edu.zju.file.AbstractFile;
import edu.zju.file.CommonInputFile;
import edu.zju.file.Config;
import edu.zju.file.FileFactory;
import edu.zju.parameter.SampleParameterBag;

/**
 *
 ** @author Zhongxu Zhu
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
                String errorInfo;
                try {
                         errorInfo=executor.getErroInformation().replace("\n\n", "\n");
                } catch (Exception e) {
                        errorInfo="NULL";
                }
                
                if(errorInfo.contains("Permission denied")){
                        executor.execute("chmod 755 "+this.callerScript);
                        edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"chmod 755 "+this.callerScript);
                        executor.execute(this.callerScript+" "+samPath+" "+vcfPath);
                        errorInfo=executor.getErroInformation().replace("\n\n", "\n");
                        edu.zju.common.CExecutor.println(errorInfo);

                }else{
                        if(errorInfo.equals("NULL")){
                        }else{
                                edu.zju.common.CExecutor.println(errorInfo);
                        }
                }
                CommonInputFile file = FileFactory.getInputFile(vcfPath, "VCF");
                return file;
        }
}
