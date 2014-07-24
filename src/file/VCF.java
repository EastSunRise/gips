/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.BufferedReader;

/**
 *
 * @author sss
 */
public class VCF extends CommonInputFile{

        public VCF(String path) {
                super(path);
        }
        public VCF(String path,BufferedReader br){
                super(path,br);
        }
        @Override
        public void check(){
              super.check();
              if(!this.getFilePath().equals(Config.getItem("FAKE_PAHT"))&&!this.getFileName().endsWith(".vcf")){
                      common.CExecutor.stopProgram(this.getFileName()+" should end with .vcf");
              }
//              String line;
//              while((line=this.readLine())!=null){
//                       if(line.toCharArray()[0]=='#') continue;
//                       if(line.contains("GT")) {}
//                       else {
//                               common.CExecutor.println(this.getFilePath()+" does not contain GT information");
//                               this.closeInput();
//                               common.CExecutor.stopProgram();
//                       }
//                       break;
//              }
//              this.closeInput();
        }
}
