/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parameter;

import common.LineHandler;
import file.CommonInputFile;
import file.FileFactory;
import filter.AncestryReferenceFilter;
import filter.BigDifferenceFilter;
import filter.CongestionFilter;
import filter.ControlFilter;
import filter.EffectiveRegionFilter;
import genome.effectiveRegion.GenomeEffectiveRegion;
import java.util.LinkedList;

/**
 *
 * @author sss
 */
public class FilterParameter {

        private LinkedList<String> filterStrategy;
        private GenomeEffectiveRegion genomeEffectiveRegion;
        private boolean isNeedEffectiveRegion = false;
        private genome.gffGenome.Genome genome;
        private String genomeName;
        private EffectiveRegionParameter regionParameter;
        private boolean isNeedEffectiveRegionParameter = false;
        private int snpDensity = 10;
        private CommonInputFile variantControl = null;
        private int controlFilterMode=0;

        public FilterParameter() {
                this.filterStrategy = new LinkedList<>();
                this.isNeedEffectiveRegion = true;
                this.isNeedEffectiveRegionParameter = true;
        }


        public LinkedList<filter.FilterSuper> getFilter() {
                LinkedList<filter.FilterSuper> filters = new LinkedList<>();
                //the following determine the order of filter

                if (this.filterStrategy.contains("AncestryReference")) {
                        filters.add(new AncestryReferenceFilter("AncestryReference",this.getGenomeEffectiveRegion().getLengt()));
                }
                if (this.filterStrategy.contains("EffectiveRegion")) {
                        filters.add(new EffectiveRegionFilter(this.getGenomeEffectiveRegion(), "EffectiveRegion"));
                }
                if (this.filterStrategy.contains("Congestion")) {
                        filters.add(new CongestionFilter("Congestion"));
                }
                if (this.filterStrategy.contains("BigDifference")) {
                        filters.add(new BigDifferenceFilter("BigDifference"));
                }
                filters.add(new ControlFilter("Control",this.controlFilterMode));
                return filters;
        }

        public void setFilterStrategy(String info) {
                info=info.replace("|", "");
                if (GlobalParameter.getToolType().equals("gips") && !info.contains("E")) {
                        info = info + "E";
                }
                StringBuffer stringTemp = new StringBuffer();
                if (info.contains("A")) {
                        stringTemp.append("A");
                }
                if (info.contains("C")) {
                        stringTemp.append("C");
                }
                if (info.contains("E")) {
                        stringTemp.append("E");
                }
                if (info.contains("B")) {
                        stringTemp.append("B");
                }

                info = stringTemp.toString();
                this.isNeedEffectiveRegion = false;
                this.isNeedEffectiveRegionParameter = false;
                for (int i = 0; i < info.toString().length(); i++) {
                        String filtrationStrategy = String.valueOf(info.toCharArray()[i]).toUpperCase().toString();
                        switch (filtrationStrategy) {
                                case "E": {
                                        if (this.filterStrategy.contains("EffectiveRegion")) {
                                                break;
                                        }
                                        this.filterStrategy.add("EffectiveRegion");
                                        this.isNeedEffectiveRegionParameter = true;
                                        this.isNeedEffectiveRegion = true;
                                        break;
                                }
                                case "A": {
                                        if (this.filterStrategy.contains("AncestryReference")) {
                                                break;
                                        }
                                        this.filterStrategy.add("AncestryReference");
                                        break;
                                }
                                case "B": {
                                        if (this.filterStrategy.contains("BigDifference")) {
                                                break;
                                        }
                                        this.filterStrategy.add("BigDifference");
                                        break;
                                }
                                case "C": {
                                        if (this.filterStrategy.contains("Congestion")) {
                                                break;
                                        }
                                        this.filterStrategy.add("Congestion");
                                        break;
                                }
                                default: {
                                        common.CExecutor.stopProgram("Don't find filter strategy: " + filtrationStrategy);
                                }
                        }
                }
                //Effective region filter is requested.
                if (!this.filterStrategy.contains("EffectiveRegion") && GlobalParameter.getToolType().equals("gips")) {
                        this.filterStrategy.add("EffectiveRegion");
                        this.isNeedEffectiveRegionParameter = true;
                        this.isNeedEffectiveRegion = true;
                }
                common.CExecutor.println(common.CExecutor.getRunningTime()+"Filter strategy: " + this.filterStrategy);
        }

        public void setGenomeEffectiveRegion(GenomeEffectiveRegion g) {
                this.genomeEffectiveRegion = g;
        }


        public LinkedList<String> getFilterStrategy() {
                return this.filterStrategy;
        }

        boolean isNeedEffectiveRegion() {
                return isNeedEffectiveRegion;
        }


        private String getGenomeName() {
                if (this.genomeName == null) {
                        this.genomeName = GlobalParameter.getGenomeVersion();
                }
                return this.genomeName;
        }

        /**
         * return effective region in genome due to the strategies choosed before this
         * function will check whether genome has been set before
         */
        public GenomeEffectiveRegion getGenomeEffectiveRegion() {
                return this.genomeEffectiveRegion;
        }

        public boolean isNeedEffectiveRegionParameter() {
                return isNeedEffectiveRegionParameter;
        }

        public void setEffectiveRegionParameter(EffectiveRegionParameter effectiveRegionParameter) {
                this.regionParameter = effectiveRegionParameter;
        }
        public void setWindowDensity(int density) {
                this.snpDensity = density;
        }

        private int getWindowDensity() {
                return this.snpDensity;
        }

        private void setVariantControl(CommonInputFile variantControl) {
                this.variantControl = variantControl;
                this.filterStrategy.addFirst("Control");
                common.CExecutor.println("Control Filter: True");
        }
        public void setControlFilter(String line){
                LineHandler lineHandler=new LineHandler();
                lineHandler.splitByComma(line);
                CommonInputFile control = FileFactory.getInputFile(lineHandler.linesplit[0], "VCF");
                this.setVariantControl(control);
                if(lineHandler.linesplit.length==3){
                       common.CExecutor.stopProgram("-c /path/to/control.vcf");
                }
                if(lineHandler.linesplit.length==2){
                        this.setControlFilterMode(Integer.valueOf(lineHandler.linesplit[1]));
                }
        }
        private void setControlFilterMode(int mode){
                this.controlFilterMode=mode;
        }

}
