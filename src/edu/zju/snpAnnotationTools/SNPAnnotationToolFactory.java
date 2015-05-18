package edu.zju.snpAnnotationTools;

import edu.zju.common.CExecutor;
import edu.zju.parameter.GlobalParameter;

/**
 *
 ** @author Zhongxu Zhu
 */
public class SNPAnnotationToolFactory {

        private SNPAnnotationTool snpAnnotationTool;

        public SNPAnnotationTool createSNPAnnotationTool(String toolName) {
                if (toolName.equals("snpEff")) {
                // from January 2015, snpEff updated its annotation field
                // Check snpEff version and determine which object to generate
                        String snpEffPath=GlobalParameter.getSNPEffPath();
                        edu.zju.common.CExecutor executor=new CExecutor();
                        executor.execute("java -jar "+snpEffPath+edu.zju.common.CExecutor.getFileSeparator()+"snpEff.jar");
                        String errorInfo=executor.getErroInformation();
                        //get snpeff version
                        if(errorInfo==null||errorInfo.contains("nable to access jarf"))executor.stopProgram(errorInfo);
                        String snpEffVersion=errorInfo.split("\\(buil")[0].split("Eff")[2].trim();
                        StringBuffer sb=new StringBuffer();
                        for(int i=0;i<snpEffVersion.toCharArray().length;i++){
                                if(Character.isDigit(snpEffVersion.toCharArray()[i])){
                                        sb.append(snpEffVersion.toCharArray()[i]);
                                }
                        }
                        int version = Integer.parseInt(sb.toString());
                        if(version<40){
                                return snpAnnotationTool = new SNPEffEFF();
                        }else{
                                return snpAnnotationTool= new SNPEffANN();
                        }
                } else {
                        return null;
                }
        }
}
