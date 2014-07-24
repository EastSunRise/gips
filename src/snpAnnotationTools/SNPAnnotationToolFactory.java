/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snpAnnotationTools;

/**
 *
 * @author sss
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
