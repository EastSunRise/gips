/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import common.CExecutor;
import java.util.HashMap;

/**
 *
 * @author sss
 */
public class Config {
        public static final String path=CExecutor.getCurrentDirectoy()+System.getProperty("file.separator")+"config";
        private static final CommonInputFile inputFile=new CommonInputFile(path);
        
        public static String getItem(String item){
                if(!inputFile.isFile()){
                        common.CExecutor.stopProgram("GIPS config file can't find. Please check whether GIPS.jar and config are in the same directory");
                }
                String line;
                while((line=inputFile.readLine())!=null){
                        if(line.startsWith("#")) continue;
                        if(line.trim().startsWith(item)) {
                                inputFile.closeInput();                        
                                return line.split(":")[1].toString().trim();
                        }
                        else continue;
                }
                inputFile.closeInput(); 
                common.CExecutor.println("Do not find "+item +" in config");
                return null;
        }
        public static HashMap<String,Integer> getMatrix(String matrixName){
              HashMap<String,Integer> matrix = new HashMap<>();
              String line;
              while((line=inputFile.readLine())!=null){
                     if(line.trim().equals("@ "+matrixName)){
                              boolean flag=true;
                              String []aminoAcids = null;
                              while((line=inputFile.readLine()).trim().length()!=0&&line.toCharArray()[0]!='@'){
                                      if(line.toCharArray()[0]=='#')
                                          continue;
                                      line=line.replace("       ", "\t");
                                      line=line.replace("      ", "\t");
                                      line=line.replace("     ", "\t");
                                      line=line.replace("    ", "\t");
                                      line=line.replace("   ", "\t");
                                      line=line.replace("  ", "\t");
                                      line=line.replace(" ", "\t");
                                      if(flag){
                                           aminoAcids=line.split("\t");
                                           flag=false;
                                           continue;
                                      }
                                      String lineSplit[]=line.split("\t");
                                      for(int i=1;i<lineSplit.length;i++){
                                             String tempAA=aminoAcids[i]+lineSplit[0];
                                             int score=Integer.parseInt(lineSplit[i].trim().toString());
                                             matrix.put(tempAA, score);       
                                      }     
                                }  
                                break;
                     }
                     else continue;
              }
              if(matrix.size()==0){
                        matrix=null;
                        common.CExecutor.stopProgram("Don't find "+matrixName);
              }
              inputFile.closeInput();
              return matrix;
        }
//        public static double getGenomeLength(){
//                String genome=parameter.LocalUserVariantion.getGenomeVersion();
//                if(genome==null){
//                        common.CExecutor.println("Genome has not been set!");
//                        CExecutor.stopProgram();
//                }
//                genome = genome.replaceAll("\\d", "");// regardless of genome version
//                String line;
//                double length=0;
//                while((line=inputFile.readLine())!=null){
//                        if(line.trim().split(":")[0].replaceAll("\\d", "").equals(genome)){
//                                length=Double.parseDouble(line.split(":")[1].trim());
//                                break;
//                        }
//                }
//                inputFile.closeInput();
//                if(length==0){
//                        common.CExecutor.println("Don't find "+genome+"'s genome length.\n"
//                                + "You can add this length in the config file. \n"
//                                + "For instance rice7:373245519");
//                        CExecutor.stopProgram();
//                }
//                return length;
//        }
}
