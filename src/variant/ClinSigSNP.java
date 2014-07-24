/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package variant;

/**
 *
 * @author sss
 */
public class ClinSigSNP extends SNP {

        public ClinSigSNP(String snpInfo, String sampleName) {
                super(snpInfo, sampleName);
        }

        @Override
        protected String getGTInformation(String snpInfo) {
                return null;
        }
}
