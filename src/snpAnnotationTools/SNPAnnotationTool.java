/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snpAnnotationTools;

import file.CommonInputFile;
import java.util.LinkedList;
import java.util.Map;
import parameter.SampleParameterBag;
import variant.SampleVariant;
import variant.SampleVariantBag;

/**
 *
 * @author sss
 */
public abstract class SNPAnnotationTool {

        private LinkedList<Map.Entry<String, CommonInputFile>> samplesVariantFile = new LinkedList<>();
        protected String annotationToolName;

        public void setSamplesVariantFile(LinkedList<Map.Entry<String, CommonInputFile>> samplesVariantFile) {
                this.samplesVariantFile = samplesVariantFile;
        }
                
        public SampleVariantBag getAnnotatedSampleVariantBag() {
                SampleVariantBag bag = new SampleVariantBag();
                for (Map.Entry<String, CommonInputFile> entry : this.samplesVariantFile) {
                        SampleVariant sampleVariant = this.getSampleAnnotatedVariant(entry.getKey(), entry.getValue());
                        bag.addSampleVariant(sampleVariant);

                }
                return bag;
        }

        public SampleVariant getSampleAnnotatedVariant(String sampleName, CommonInputFile file) {
                return null;
        }

        protected CommonInputFile annotateSNPInVCFFile(String sampleName, CommonInputFile file) {
                return null;
        }
}
