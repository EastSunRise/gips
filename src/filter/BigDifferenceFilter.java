/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.util.Iterator;
import java.util.LinkedList;
import matrix.CodonChange;
import matrix.Matrix;
import parameter.SampleParameter;
import variant.SNP;
import variant.SNPAnnotation;
import variant.SampleVariant;

/**
 *
 * @author zzx
 */
public class BigDifferenceFilter extends FilterSuper{
       private String matrixName;
       private matrix.Matrix matrix;
       private int maxAAsimilarityScore;
       
       
       public BigDifferenceFilter(String strategy) {
              super(strategy);
       }  

        private void setMatrix(String matrixName){
               common.CExecutor.println("\t\tMatrix: "+matrixName);
               this.matrixName=matrixName;
               matrix= new Matrix(this.matrixName);
        }
        private void setMaxAASimilarityScore(int score){
                this.maxAAsimilarityScore=score;
        }
        @Override
        SampleVariant filtrateSampleVariant(SampleVariant sampleVariant){
               LinkedList<SNP> sampleSNPs=sampleVariant.getSnps();
               LinkedList<SNP> filtratedSNPs=new LinkedList<>();
               LinkedList<SNPAnnotation> snpAnnotation ;
               SNP snp;
               for(Iterator<SNP> it=sampleSNPs.iterator();it.hasNext();){
                      snp=it.next();
                      snpAnnotation = snp.getSNPAnnotations();
                      int originAnnotationNumber=snpAnnotation.size();
                      for(Iterator<SNPAnnotation> iterator=snpAnnotation.iterator();iterator.hasNext();){
                             SNPAnnotation annotation=iterator.next();
                             if(annotation.isPassBigDfferenceFilter()) continue;
                             if(annotation.isHighRisk()) continue;
                             if(annotation.isCoding()){
                                    CodonChange codonChange =annotation.getCodonChange();
                                    String originCodon=codonChange.getOriginCodon();
                                    String mutationCodon=codonChange.getMutationCodon();
                                    int score=this.matrix.getScore(originCodon, mutationCodon);
                                    if(score>this.maxAAsimilarityScore){
                                           iterator.remove();
                                    }
                             }
                      }
                      int finalAnnotationNumber=snpAnnotation.size();
                      if(originAnnotationNumber==0||finalAnnotationNumber>0) filtratedSNPs.add(snp);
                              
               }
               sampleVariant.renewSNPsInVariant(filtratedSNPs);
               return sampleVariant;
        }

        @Override
        SampleParameter filtrateSampleParameter(SampleParameter sampleParameter) {
               this.setMatrix(sampleParameter.getMatrix());
               this.setMaxAASimilarityScore(sampleParameter.getMaxAASimilarityScore());
               SampleVariant sampleVariant=sampleParameter.getSampleVariant();
               sampleVariant=this.filtrateSampleVariant(sampleVariant);
               sampleParameter.setSampleVariant(sampleVariant);
               return sampleParameter;
        }


}
