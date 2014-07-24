/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import parameter.SampleParameter;
import parameter.SampleParameterBag;
import variant.SNP;
import variant.SampleVariant;

/**
 *
 * @author zzx
 */
public class AncestryReferenceFilter extends FilterSuper{
        private HashMap<String,Double> backgroundMutationRateBeforeFilter;
        private boolean isRemoveAllSnpInSamePosition=false;
        private int effectiveRegionLength=0;
        private HashMap<String,HashSet<Integer>> studyPosition2Filter;
        
        
        
        public AncestryReferenceFilter(String filterStrategy,int effectiveRegionLegth) {
                super(filterStrategy);
                this.effectiveRegionLength=effectiveRegionLegth;
        }
   
        @Override
        public SampleParameterBag filterSampleVariantsInParameterBag(SampleParameterBag sampleParameterBag){
               Set<String> sampleNames=new HashSet<>(sampleParameterBag.getSamplesNamesList());
               HashSet<String> usedSampleNames = new HashSet<>();
               for(Iterator<String> iteratorOuter=sampleNames.iterator();iteratorOuter.hasNext();){
                      String sampleNameOuter=iteratorOuter.next();
                      usedSampleNames.add(sampleNameOuter);
                      if(sampleNames.size()==usedSampleNames.size()) break;
                      SampleVariant sampleVariant=sampleParameterBag.getSample(sampleNameOuter).getSampleVariant();
                      for(Iterator<SNP> snpIteratorOuter=sampleVariant.getSnps().iterator();snpIteratorOuter.hasNext();){
                             SNP snp=snpIteratorOuter.next();
                             String chr=snp.getChr();
                             int position=snp.getPosition();
                             LinkedList<SampleVariant> sampleVariantsList= new LinkedList<>();
                             sampleVariantsList.add(sampleVariant);
                             for(Iterator<String> iteratorInner=sampleNames.iterator();iteratorInner.hasNext();){
                                  String sampleNameInner=iteratorInner.next();
                                  if(usedSampleNames.contains(sampleNameInner)) continue;
                                  sampleVariantsList.add(sampleParameterBag.getSample(sampleNameInner).getSampleVariant());
                             }                           
                             LinkedList<SNP> snpInSamePosition=this.searchForSNPInSamePosition(chr, position, sampleVariantsList);
                             if(this.isRemoveSNP(snpInSamePosition)){
//                                    if(!VariantFilter.isFilterHomoSapiensSNPDataSet()){
//                                            double tempDiscardRate=1;
//                                            for(SNP snpTemp:snpInSamePosition){
//                                                    tempDiscardRate=tempDiscardRate*this.backgroundMutationRateBeforeFilter.get(snpTemp.getSampleName());
//                                            }
//                                            for(SNP snpTemp:snpInSamePosition){
//                                                    sampleParameterBag.getSample(snpTemp.getSampleName()).getSampleVariant().falseIgnoranceRate.addAncestryReferenceIgnoranceRate(tempDiscardRate);
//                                            }                                            
//                                    } 
                                    sampleParameterBag.removeSNPInSamePosition(chr, position);
                                    //add sample ancestry position
                                    for(SNP snp2Remove: snpInSamePosition){
                                            sampleParameterBag.getSample(snp2Remove.getSampleName()).addStudyPositionToFilter(snp2Remove.getChr(), snp2Remove.getPosition());
                                    }
                             }
                      }
               }
               return sampleParameterBag;
        }        
        
        
        
        
        
        
        
        
        //In this class ,filtrateSampleVariant is not used.
        @Override
        SampleVariant filtrateSampleVariant(SampleVariant sampleVariant) {
                throw new UnsupportedOperationException("Not supported yet.");
        }        
        /**
         * 
         * @param snps SNP in the same position but for sample
         * @return true means snp would like to be removed.
         */
        private boolean isRemoveSNP(LinkedList<SNP> snps){
               if(snps.size()<=1) {return false;}
               else if(!this.isRemoveAllSnpInSamePosition&&snps.size()==2) {
                       HashSet<String> altSet=new HashSet<>();
                       String minimumLengthAlt="NNNNN";// the variant is not used
                       String alt;
                       for (SNP snp: snps){
                               alt=snp.getAlt();
                               if(alt.length()<minimumLengthAlt.length()){
                                       minimumLengthAlt=alt;
                               }
                               altSet.add(snp.getAlt());
                       }
                       if(minimumLengthAlt.length()>=5) return true;
                       if(altSet.size()==2){
                               return false;
                       }
               }
               return true;
//               else {
//                       HashSet<String> gpSet=new HashSet<>();
//                       for(SNP snp:snps){
//                               gpSet.add(snp.getAlt());
//                       }
//                       if(gpSet.size()>=2){
//                               for(String gp:gpSet){
//                                       if(gp.length()>3)return true;
//                               }
//                               return false;
//                       }
//                       return true;
//               }
//               if(this.isRemoveAllSNPInSamePosition)return true;// if size is more or equal to 2, then remove all snp in this position regardless of the genotype
//               else {
//                      String genotype;
//                      for (Iterator<SNP> it = snps.iterator(); it.hasNext();) {
//                             SNP snp = it.next();
//                             genotype=snp.getGenotype() ;
//                             if(genotype.equals("0/1")) return true; 
//                      }
//               }
//               return false;          
        }

        private LinkedList<SNP> searchForSNPInSamePosition(String chr,int position,LinkedList<SampleVariant> sampleVariants){
               LinkedList<SNP> snps= new LinkedList<>();  
               for(Iterator<SampleVariant> sampleVariantIterator=sampleVariants.iterator();sampleVariantIterator.hasNext();){
                      SampleVariant sampleVariant=sampleVariantIterator.next();
                      if(sampleVariant.isSNP(chr, position))
                             snps.add(sampleVariant.get1SNP(chr, position));      
               }
               return snps;
        }

        @Override
        SampleParameter filtrateSampleParameter(SampleParameter sampleParameter) {
                throw new UnsupportedOperationException("Not supported yet.");
        }
        

        public void addStudyPositionToFilter(String chr, int pos){
                if(this.studyPosition2Filter==null){
                        this.studyPosition2Filter= new HashMap<>();
                }
                HashSet<Integer> chrPosSet=new HashSet<>();
                String chrInMap;
                if(!this.studyPosition2Filter.containsKey(chr)){
                        this.studyPosition2Filter.put(chr, chrPosSet);
                }
                chrPosSet=this.studyPosition2Filter.get(chr);
                chrPosSet.add(pos);
                this.studyPosition2Filter.put(chr, chrPosSet);
        }

    
}
