/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parameter;

import java.util.LinkedList;

/**
 *
 * @author sss
 */
public class ParameterList {
        private LinkedList<String> globalParaList;
        private LinkedList<String> sampleSpecificParaList;

        public ParameterList() {
                initiateParaList();
        }
        public boolean isInSampleSpecificItemList(String item){
                for(String line:sampleSpecificParaList){
                        if(line.trim().startsWith(item))return true;
                }
                common.CExecutor.stopProgram("Faile to load "+item);
                return false;
                
        }
        public boolean isInGlobParaList(String item){
                for(String line: globalParaList){
                        if(line.trim().startsWith(item))return true;
                }
                common.CExecutor.stopProgram("Faile to load "+item);
                return false;
        }
        
        public LinkedList<String> getGlobalParaList(){
                return this.globalParaList;
        }
        public LinkedList<String> getSampleBasicParaList(){
                LinkedList<String> temp=new LinkedList<>();
                temp.add(" ");
                temp.add(this.sampleSpecificParaList.get(0));
                temp.add(this.sampleSpecificParaList.get(1));
                temp.add(this.sampleSpecificParaList.get(2));
                temp.add(this.sampleSpecificParaList.get(3));
                return temp;
        }
        
        void initiateParaList(){
               this.globalParaList = new LinkedList<String>() {
                        {
                                add("[GLOBAL]");
                                add("PROJECT :                     ");
                                add("SNPEFF_GENOME_VERSION     :                  #version");
                                add("SNPEFF  :                                    #SNPEff foler path ");
                                add("REF_GENOME_ANNOTATION.GFF :                  #genome GFF file Path");                                
                                add("HUMAN_GRCH37_ANNOTATION.GFF :                #homospiens GFF file (to filter clinical variant library) path");
                                add("CANDIDATE_CRITERIA :                         #criteria to report candidate gene");
                                add("VAR_CALL_SCRIPT :                            #caller script (following GIPS requirement) path");
                                add("EFF_REGION  : CDS|SpliceSite=2               # '|'-separated effective region ");
                                add("VAR_FILTERS :  EBA                           #filter strategy ");
                                add("SCORE_MATRIX: DEFAULT");
                                add("MAX_AA_SCORE: 0");
                                add("NUM_SIM_SNPS : 10000        #number of artificial snp to evaluate vcs");
                                add("LIB_PHENOTYPE_VAR :");
                                add("CONTROL :");
                                add("MAX_VAR_DENSITY   : 3");
                        };
                };  
                
               this.sampleSpecificParaList=new LinkedList<String>(){
                       {
                               add("[SAMPLE]");
                               add("SAMPLE_NAME :");
                               add("SAMPLE.VCF  :");
                               add("READS_ALIGNMENT.SAM :");
                               add("SPECIFY_HOMO_VDS    :");
                               add("SPECIFY_HETERO_VDS  :");
                               add("SPECIFY_BVF :");
                               add("VAR_CALL_SCRIPT :");
                               add("SCORE_MATRIX:");
                               add("MAX_AA_SCORE: ");
                               add("CONTROL :");
                               add("NUM_SIM_SNPS :");
                               add("MAX_VAR_DENSITY :");
                               add("ESTIMATED_HOMO_VDS :");
                               add("ESTIMATED_HETERO_VDS:");
                               add("ESTIMATED_HOMO_VCS :");
                               add("ESTIMATED_HETERO_VCS:");  
                               add("ESTIMATED_BVF:");
                               add("VCF_MD5:");
                               add("SAM_MD5");
                               add("SCRIPT_MD5");
                       };
               };
                    
        }         
}
