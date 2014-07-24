/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import common.FileHandler;
import common.LineHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sss
 */
public class MapQualityFilter {
        String filePath;
        int minimumMapQuality;
        public MapQualityFilter(String f,int filter) {
                this.filePath=f;
                this.minimumMapQuality=filter;
        }
        
        
        public void output(String filePath){
                FileHandler fh = new FileHandler();
                fh.readFile(this.filePath);
                fh.writeFile(filePath);
                String line;
                LineHandler lh = new LineHandler();
                try {
                        while((line=fh.br.readLine())!=null){
                                if(line.toCharArray()[0]=='@'){
                                        fh.bw.write(line+'\n');
                                        continue;
                                }
                                lh.splitByTab(line);
                                if(Integer.parseInt(lh.linesplit[4])<this.minimumMapQuality)
                                        continue;
                                fh.bw.write(line+'\n');
                        }
                        fh.bw.flush();
                        fh.bw.close();
                        fh.br.close();
                } catch (IOException ex) {
                        Logger.getLogger(MapQualityFilter.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        
        
}
