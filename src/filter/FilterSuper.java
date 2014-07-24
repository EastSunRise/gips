/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.util.LinkedList;
import parameter.SampleParameter;
import parameter.SampleParameterBag;
import variant.SampleVariant;

/**
 *
 * @author zzx
 */
public abstract class FilterSuper {
    private String filterStrategy;  
    
    
        public FilterSuper(String stragtegy) {
                this.filterStrategy=stragtegy;
        }
       
    
    
    void printFiltering(){
            common.CExecutor.println(common.CExecutor.getRunningTime()+"After "+this.filterStrategy+" filter, retained variants:");
    }
     
    public SampleParameterBag filterSampleVariantsInParameterBag(SampleParameterBag sampleParameterBag){
                SampleParameterBag bag=sampleParameterBag;  
                LinkedList<String> sampleNameList=bag.getSamplesNamesList();
                for(String sampleName:sampleNameList){
                       SampleParameter sampleParameter=bag.getSample(sampleName);
                       sampleParameter=this.filtrateSampleParameter(sampleParameter);
                       bag.updateSampleParameter(sampleParameter);
                }
                return bag;   
    }
    abstract SampleParameter filtrateSampleParameter(SampleParameter sampleParameter);
    
    
    
    
    
    public String getFilterName(){
            return this.filterStrategy;
    }
    abstract SampleVariant filtrateSampleVariant(SampleVariant sampleVariant);


}
