/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

/**
 *
 * @author sss
 */
public class GFF extends CommonInputFile{

        public GFF(String path) {
                super(path);
        }
        @Override
        protected void check() {
                super.check();
                if(!this.getFileName().endsWith(".gff3")&&!this.getFileName().endsWith("gff")){
                      common.CExecutor.stopProgram(this.getFileName()+" should end with .gff3. Please make sure GFF file version (version3 required).");
                }
                String line;
                //common.CExecutor.println("\n---------------------------------------------------"); 
                //common.CExecutor.println("Genome gff3 file annotation section:");
                //common.CExecutor.println("Reading genome gff3 file:");
                boolean isHasIDField=false;
                int checkLines=1000;
                while((line=this.readLine())!=null&&checkLines!=0){
                        if(line.trim().length()==0)continue;
                        if(line.startsWith("#")){
                                //common.CExecutor.println(line);
                                continue;
                        }
                        if(line.split("\t")[8].contains("ID")){
                                isHasIDField=true;
                                checkLines=checkLines-1;
                        }
                }
                this.closeInput();
                if(!isHasIDField){
                        common.CExecutor.stopProgram("GFF file does not contain ID information");
                }   
        }        
}
