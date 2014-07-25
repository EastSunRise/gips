package edu.zju.snpAnnotationTools;

import edu.zju.file.CommonInputFile;
import edu.zju.variant.SampleVariant;
import edu.zju.variant.SampleVariantBag;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 ** @author Zhongxu Zhu
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
