package edu.zju.variant;

import edu.zju.common.LineHandler;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 *
 * @author zzx
 */
public abstract class SNP {

        private String chr;//chromosome id
        private int position;//position in chromosome
        private String ref;//reference
        private String alt;//alternate nucleic acid
        private String genotype="-1";//genotype information, if genotype=-1 means there no GT information
                                     // control filter will take use of -1
        private String id;
        private LinkedList<SNPAnnotation> snpAnnotations;
        private String sampleName;
        private String snpRawInfoBeforeAnnotateInVcf;
        private String annotationFieldInfo;
        private boolean inWindow = false;
        private String depth="0";
        private String quality="0";
        /**
         *
         * @param snpInfo full sno information
         */
        public SNP(String snpInfo, String sampleName) {
                this.setSampleName(sampleName);
                this.setSNP(snpInfo);
                snpAnnotations = new LinkedList<>();

        }

        
        /**
         *
         * @param snpInfo contains one variant's concrete information
         */
        private void setSNP(String snpInfo) {
                LineHandler lineHandler = new LineHandler();
                lineHandler.splitByTab(snpInfo);
                String chr;
                if ((chr = lineHandler.linesplit[0].trim().toString()).isEmpty()) {
                        this.setChr(null);
                        return;
                } else {
                        this.setChr(chr);
                }
                try {
                        this.setPosition(Integer.parseInt(lineHandler.linesplit[1]));
                } catch (java.lang.NumberFormatException e) {
                        edu.zju.common.CExecutor.stopProgram(lineHandler.linesplit[1]+" is not a position in "+this.getSampleName()+"\n"+snpInfo);
                        this.setPosition(0);
                }
                this.setId(lineHandler.linesplit[2]);
                this.setRef(lineHandler.linesplit[3]);
                this.setAlt(lineHandler.linesplit[4]);
                //snp quality or depth may null,so we use string formate
                try{
                        //For we haven't taken use of quality and depth information
                        //this.setQuality(lineHandler.linesplit[5]);
                        this.setDepth(lineHandler.regexMatch(lineHandler.linesplit[7], "DP=(\\d*)\\;"));
                        String genotype = getGTInformation(snpInfo);
                        this.setGenotype(genotype);
                }catch(java.lang.ArrayIndexOutOfBoundsException e){
                        return;
                }
                //if(genotype!=null&&genotype.equals("-1")) this.setChr(null);
        }

        /**
         *
         * @param chr an identifier from the reference genome chromosome
         */
        private void setChr(String chr) {
                this.chr = chr;
        }

        /**
         *
         * @param position the reference position .If an Indel happens , the
         * positon is the first base's position
         */
        private void setPosition(int position) {
                this.position = position;
        }

        /**
         *
         * @param ref reference base ,must be one of A,G,C,T,N.
         */
        private void setRef(String ref) {
                this.ref = ref;
        }

        /**
         *
         * @param alt comma separated list of alternate non-reference alleles
         * called on the sample
         */
        private void setAlt(String alt) {
                this.alt = alt;
        }

        /**
         * "-1" means no genotype information
         * @param genotype genotype information
         */
        private void setGenotype(String genotype) {
                this.genotype = genotype;
        }

        /**
         *
         * @return an identifier from the reference genome chromosome
         */
        public String getChr() {
                return this.chr;
        }

        /**
         *
         * @return the reference position .If an Indel happens , the positon is
         * the first base's position
         */
        public int getPosition() {
                return this.position;
        }

        /**
         *
         * @return reference base ,must be one of A,G,C,T,N.
         */
        public String getRef() {
                return this.ref;
        }

        /**
         *
         * @return comma separated list of alternate non-reference alleles
         * called on the sample
         */
        public String getAlt() {
                return this.alt;
        }

        /**
         *
         * @return genotype information. example: 1/0 or 1/1
         */
        public String getGenotype() {
                return this.genotype;
        }

        public void addSNPAnnotation(SNPAnnotation snpAnnotation) {
                this.snpAnnotations.add(snpAnnotation);
        }

        public LinkedList<SNPAnnotation> getSNPAnnotations() {
                return this.snpAnnotations;
        }

        private void setSampleName(String name) {
                this.sampleName = name;
        }

        public String getSampleName() {
                return this.sampleName;
        }

        public String getSNPInfoInVcf() {
                return this.snpRawInfoBeforeAnnotateInVcf;
        }

        public void setSNPRawInfoBeforeAnnotate(String rawInfo){
                this.snpRawInfoBeforeAnnotateInVcf=rawInfo;
                
        }
        public void setAnnotationFieldInfo(String info){
                this.annotationFieldInfo=info;
        }
        public String getAnnotationFieldInfo(){
                return this.annotationFieldInfo;
        }
        public boolean isInWindow() {
                return this.inWindow;
        }

        public void setInWindow() {
                this.inWindow = true;
        }

        private void setDepth(String dp) {
                this.depth = dp;
        }

        public String getDepth() {
                if(this.depth.equals("0"))return "";
                return this.depth;
        }

        private void setQuality(String qual) {
                this.quality = qual;
        }

        public String getSNPQuality() {
                return this.quality;
        }

        protected abstract String getGTInformation(String info);
        /**
         * 
         * @return result is used in output file.
         */
        public String getAnnotationInfo() {
                TreeSet<String> tree = new TreeSet<>();
                String info = new String();
                for (SNPAnnotation annotation : this.snpAnnotations) {
                        if (annotation.getEffect() == null) {
                                continue;
                        } else {
                                tree.add(annotation.getEffect() + annotation.getCodonChangeInfo());
                        }

                }
                for (String effect : tree) {
                        info = info + ',' + effect;
                }
                if (info.isEmpty()) {
                        return "";
                }
                info = info.substring(1, info.length());
                return info;
        }
        
        public boolean isIndel(){
                return !(ref.length()==alt.length());
        }
        private void setId(String id){
            this.id=id;
        }
        public String getId(){
            return this.id;
        }
        /**
         * To free memory
         */
        public void clearAnnotationFieldInfo(){
            this.annotationFieldInfo=null;
        }
        public void  clearRawSNPInfoBeforeAnnotate(){
            this.snpRawInfoBeforeAnnotateInVcf=null;
        }

}
