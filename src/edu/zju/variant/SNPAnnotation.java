package edu.zju.variant;

import edu.zju.matrix.AAChange;

/**
 *
 ** @author Zhongxu Zhu
 */
public class SNPAnnotation {

        private AAChange aaChange;
        private boolean coding = false;
        private boolean highRisk = false;
        private boolean isExon = false;
        private String effect;
        private String geneName = "";
        private boolean isPassBigDifferenceFilter = false;

        public boolean isCoding() {
                return this.coding;
        }

        public void setAA_Symbol_Change(String origin, String mutation) {
                this.coding = true;
                this.aaChange = new AAChange();
                this.aaChange.setOriginCodon(origin);
                this.aaChange.setMutationCodon(mutation);
        }

        public AAChange getCodonChange() {
                return this.aaChange;
        }

        public void setHighRisk() {
                this.highRisk = true;
        }

        public boolean isHighRisk() {
                return this.highRisk;
        }

        public void setEffect(String effect) {
                this.effect = effect;
                if (effect.contains("EXON")) {
                        this.isExon = true;
                }
        }

        public String getEffect() {
                return this.effect;
        }

        public String getCodonChangeInfo() {
                if (this.aaChange == null || this.aaChange.getOriginAA().isEmpty()) {
                        return "";
                }
                return '(' + this.aaChange.getOriginAA() + ":" + this.aaChange.getMutationAA() + ')';
        }

        public String getItsGeneName() {
                return this.geneName;
        }

        public void setItsGeneName(String name) {
                this.geneName = name;
        }

        public void setPassBigDifferenceFilter() {
                this.isPassBigDifferenceFilter = true;
        }

        public boolean isPassBigDfferenceFilter() {
                return this.isPassBigDifferenceFilter;
        }

        public boolean isExon() {
                return isExon;
        }
}
