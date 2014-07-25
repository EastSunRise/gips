package edu.zju.variant;

import edu.zju.matrix.CodonChange;

/**
 *
 ** @author Zhongxu Zhu
 */
public class SNPAnnotation {

        private CodonChange codonChange;
        private boolean coding = false;
        private boolean highRisk = false;
        private boolean isExon = false;
        private String effect;
        private String geneName = "";
        private boolean isPassBigDifferenceFilter = false;

        public boolean isCoding() {
                return this.coding;
        }

        public void setCodonChange(String origin, String mutation) {
                this.coding = true;
                this.codonChange = new CodonChange();
                this.codonChange.setOriginCodon(origin);
                this.codonChange.setMutationCodon(mutation);
        }

        public CodonChange getCodonChange() {
                return this.codonChange;
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
                if (this.codonChange == null || this.codonChange.getOriginCodon().isEmpty()) {
                        return "";
                }
                return '(' + this.codonChange.getOriginCodon() + ":" + this.codonChange.getMutationCodon() + ')';
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
