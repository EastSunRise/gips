/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

/**
 *
 * @author sss
 */
public class VariantControl extends CommonInputFile{
        public VariantControl(String path) {
                super(path.trim());
        }

        @Override
        public void check() {
                super.check();
                String line;
                int i=0;
                while((line=this.readLine())!=null){
                        if(line.startsWith("#")) continue;
                        if(line.trim().length()==0) break;
                        i++;
                        if(line.split("\t").length>2){
                                common.CExecutor.println("!   This line will not be considered:       "+line);
                                common.CExecutor.stopProgram("Variants control file should have only two coloum, for example\n"
                                        + "#Chr\tPos\n"
                                        + "Chr1\t189237");
                        }
                        if(i==1000) break;
                }
                common.CExecutor.println("Please note: \"Chr\" in control file should be correspond to variant file");
                this.closeInput();
        }        
}
