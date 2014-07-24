/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author sss
 */
public class SAMReadsFilter {
       String samFilePath;
       String outputFilePath;
       public SAMReadsFilter(String pathName,String outputFilePath) {
              this.samFilePath=pathName;
              this.outputFilePath=outputFilePath;
       }
       
       
     public void filtrate() throws IOException, InterruptedException{
            BufferedReader br= new BufferedReader(new FileReader(new File(this.samFilePath)));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.outputFilePath)));

            String line;
            String temp1 = null;
            String temp2 = null;
            String read1=null;
            String read2=null;
            String flag1=null;
            String flag2=null;
            String name1= new String();
            String name2= new String();
            boolean firstRead=true;
            int flag=0;
            String nametemp=null;
            while((line=br.readLine())!=null){
                   if(line.toCharArray()[0]=='@')
                                continue;                   
                   if(firstRead){
                         temp1=line;
                         firstRead=false;
                   }else{
                         temp2=line;
                         firstRead=true;
                   }
                   if(firstRead&&!temp1.split("\t")[0].trim().toString().equals(temp2.split("\t")[0].trim().toString())){
                          firstRead=false;
                          temp1=temp2;
                          continue;
                   }                   
                   if(firstRead){   
                         if(flag==0){
                            read1=temp1;
                            flag1=temp1.split("\t")[1].trim().toString();
                            name1=temp1.split("\t")[0].trim().toString();
                            read2=temp2;
                            flag2=temp2.split("\t")[1].trim().toString();
                            name2=temp2.split("\t")[0].trim().toString();  
                            flag=1;
                            continue;
                         }
                         if(temp1.split("\t")[0].trim().toString().equals(name2)){
                                flag=100;
                         }else {
                            if(flag==1){
                                   if(flag1.equals("99")&&flag2.equals("147")){
                                            bw.write(read1+'\n');
                                            bw.write(read2+'\n');
                                    }
                                    else if(flag1.equals("147")&&flag2.equals("99")){
                                            bw.write(read1+'\n');
                                            bw.write(read2+'\n');
                                    }
                                    else if(flag1.equals("83")&&flag2.equals("163")){
                                            bw.write(read1+'\n');
                                            bw.write(read2+'\n');                                
                                    }
                                    else if(flag1.equals("163")&&flag2.equals("83")){
                                            bw.write(read1+'\n');
                                            bw.write(read2+'\n');
                                    }                                      
                            } 
                            read1=temp1;
                            flag1=temp1.split("\t")[1].trim().toString();
                            name1=temp1.split("\t")[0].trim().toString();
                            read2=temp2;
                            flag2=temp2.split("\t")[1].trim().toString();
                            name2=temp2.split("\t")[0].trim().toString();                            
                            flag=1;
                            bw.flush();                            
                         }

                   }
            }
            br.close();
            bw.flush();
            bw.close();    
     } 
       
       
}
