/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.util.LinkedList;
import parameter.FilterParameter;
import parameter.GlobalParameter;
import parameter.SampleParameter;
import parameter.SampleParameterBag;
import variant.SampleVariant;

/**
 *
 * @author sss
 */
public class VariantFilter {
        private FilterParameter filterParameter;

        
        
        public VariantFilter(FilterParameter filterParameter) {
                this.filterParameter=filterParameter;
        }

        public SampleParameterBag filtrateAndEstimateStudyFir(SampleParameterBag sampleParameterBag){
//                common.CExecutor.println("Origin variants number:    "+common.CExecutor.getRunningTime());
//                sampleParameterBag.printSNPNumberInSample();                
                LinkedList<FilterSuper> filters=this.filterParameter.getFilter();
                common.CExecutor.println(common.CExecutor.getRunningTime()+"Filtering variants. Please wait.");
                for(FilterSuper filter: filters){
                        sampleParameterBag=filter.filterSampleVariantsInParameterBag(sampleParameterBag);
                        filter.printFiltering();                        
                        sampleParameterBag.printSNPNumberInSample();   
                }
                sampleParameterBag.estimateStudyFir(this.filterParameter.getGenomeEffectiveRegion());
                return sampleParameterBag;
        }  

         public SampleParameterBag estimateFunctionalFalseDiscoveryRate(SampleParameterBag sampleParameterBag){
                LinkedList<FilterSuper> filters=this.filterParameter.getFilter();
                for(String sampleName: sampleParameterBag.getSamplesNamesList()){
                        //swap
                        //take out true sample variant
                        SampleParameter sampleParameter=sampleParameterBag.getSample(sampleName);
                        SampleVariant sampleVariantInSample=sampleParameter.getSampleVariant();
                        //put in clinical variant
                        SampleVariant clinicalVariantSample=new GlobalParameter().getClinicalVariant();
                        sampleParameter.setSampleVariant(clinicalVariantSample);
                        int originalClinivalVariantNumber=clinicalVariantSample.getSNPCounts();
                        for(FilterSuper filter: filters){
                                String filterName=filter.getFilterName();
                                if(filterName.equals("Congestion")||filterName.equals("AncestryReference")||filterName.equals("Control")) continue;
                                sampleParameter=filter.filtrateSampleParameter(sampleParameter);
                        }        
                        int afterFilering=sampleParameter.getSampleVariant().getSNPCounts();
                        sampleParameter.setFunctionalFIR((double)(originalClinivalVariantNumber-afterFilering)/originalClinivalVariantNumber);
                        //put back true sample variant
                        sampleParameter.setSampleVariant(sampleVariantInSample);
                }
                return sampleParameterBag;
        }        

        
}
