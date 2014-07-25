package edu.zju.snpAnnotationTools;

/**
 *
 ** @author Zhongxu Zhu
 */
public class SNPAnnotationToolFactory {

        private SNPAnnotationTool snpAnnotationTool;

        public SNPAnnotationTool createSNPAnnotationTool(String toolName) {
                if (toolName.equals("snpEff")) {
                        return snpAnnotationTool = new SNPEff();
                } else {
                        return null;
                }
        }
}
