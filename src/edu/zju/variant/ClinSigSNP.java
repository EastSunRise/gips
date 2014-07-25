package edu.zju.variant;

/**
 *
 ** @author Zhongxu Zhu
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
