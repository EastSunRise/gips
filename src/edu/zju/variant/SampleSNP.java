package edu.zju.variant;

import edu.zju.common.LineHandler;

/**
 *
 ** @author Zhongxu Zhu
 */
public class SampleSNP extends SNP {

        public SampleSNP(String snpInfo, String sampleName) {
                super(snpInfo, sampleName);
        }

        @Override
        protected String getGTInformation(String snpInfo) {
                LineHandler lineHandler = new LineHandler();
                int genotypeSiteInGenotypeField = -1;
                if (!(snpInfo.split("\t").length < 9)) {
                        lineHandler.splitByColon(snpInfo.split("\t")[8]);
                        for (int i = 0; i <= lineHandler.linesplit.length; i++) {//if GT:AD:DP then genotypeSiteInGenotypeField is 0 
                                if (lineHandler.linesplit[i].equals("GT")) {
                                        genotypeSiteInGenotypeField = i;
                                        break;
                                }
                               
                        }
                }
                if (genotypeSiteInGenotypeField == -1) {//If no genotype field 
                        return "-1";
                }
                lineHandler.splitByTab(snpInfo);
                lineHandler.splitByColon(lineHandler.linesplit[9]);
                String genotypeTemp = lineHandler.linesplit[genotypeSiteInGenotypeField];
                return genotypeTemp;
        }
}
