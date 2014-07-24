/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import common.CExecutor;
import common.StringSecurity;
import java.io.File;

/**
 *
 * @author sss
 */
public class SAM extends CommonInputFile {

        public SAM(String path) {
                super(path.trim());
        }

        @Override
        public void check() {
                super.check();
                if(!this.getFileName().endsWith(".sam")){
                      common.CExecutor.stopProgram(this.getFileName()+" should end with .sam");
                }
        }
        @Override
        public String getFileMD5(){
                return String.valueOf(new File(this.getFilePath()).length());
        }
}
