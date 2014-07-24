/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import common.FileHandler;
import common.LineHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sss
 */

public class Test {

       
       public void selectReads() throws IOException{
              FileHandler f1= new FileHandler();
              FileHandler f2= new FileHandler();
              f1.readFile("/home/sss/NetBeansProjects/RiceGIPS/notinvcf");
              f2.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempReads.sam");
              LineHandler l1= new LineHandler();
              LineHandler l2= new LineHandler();              
              String line;
              int i=0;
              while((line=f2.br.readLine())!=null){
                     String temp=line;
                     l2.splitByTab(line);
                     String chrtemp=l2.linesplit[2].trim().toString();
                     int postemp=Integer.parseInt(l2.linesplit[3]);
                     f1.readFile("/home/sss/NetBeansProjects/RiceGIPS/notinvcf");
                     while((line=f1.br.readLine())!=null){
                            l1.splitByTab(line);
                            String chr=l1.linesplit[0].trim().toString();
                            int pos=Integer.parseInt(l1.linesplit[1]);
                            if(chr.equals(chrtemp)) {
                                   int temppos=pos-postemp;
                                   if(temppos>=0&&temppos<=89) {
                                          common.CExecutor.println(temp);
                                          break;
                                   }
                            }
                     }      
                     f1.br.close();
              }
              
       }       
       
       
       
       public void selectNotInVcf() throws IOException{
              FileHandler f1= new FileHandler();
              FileHandler f2= new FileHandler();
              f1.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempPosition");
              f2.readFile("/home/sss/NetBeansProjects/RiceGIPS/temp.vcf");
              LineHandler l1= new LineHandler();
              LineHandler l2= new LineHandler();
              String line;
              int j=0;            
              while((line=f1.br.readLine())!=null){
                    l1.splitByTab(line);
                    String temp=line;
                    String snpChr=l1.linesplit[0].trim().toString();
                    int pos=Integer.parseInt(l1.linesplit[1]);
                    int i=0;
                    boolean flag=false;
                    f2.readFile("/home/sss/NetBeansProjects/RiceGIPS/temp.vcf");
                    while((line=f2.br.readLine())!=null){
                           l2.splitByTab(line);
                           String chrtemp=l2.linesplit[0].trim().toString();
                           int postemp =Integer.parseInt(l2.linesplit[1]);
                           if(chrtemp.equals(snpChr)){
                               if((pos-postemp)==0) {
                                      j=j+1;
                                      flag=true;
                                      break;
                               }   
                           }   
                    } 
                    if(!flag) common.CExecutor.println(temp);
              }  
              common.CExecutor.println(String.valueOf(j));
       }
       
       
       
       
       public void count() throws IOException{
              FileHandler f1= new FileHandler();
              FileHandler f2= new FileHandler();
              f1.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempPosition");
              f2.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempReads.sam");
              LineHandler l1= new LineHandler();
              LineHandler l2= new LineHandler();
              int [] ii=new int[800];
              for(int i=0;i<ii.length;i++){
                     ii[i]=0;
              }
              String line;
              int i0=0;
              int i1=0;
              int i2=0;
              int i3=0;
              int i4=0;              
              while((line=f1.br.readLine())!=null){
                    //common.CExecutor.println(line);
                    l1.splitByTab(line);
                    String snpChr=l1.linesplit[0].trim().toString();
                    int pos=Integer.parseInt(l1.linesplit[1]);
                    int i=0;
                    f2.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempReads.sam");
                    while((line=f2.br.readLine())!=null){
                           l2.splitByTab(line);
                           String chrtemp=l2.linesplit[2].trim().toString();
                           int postemp =Integer.parseInt(l2.linesplit[3]);
                           if(chrtemp.equals(snpChr)){
                               if((pos-postemp)>=0&&(pos-postemp)<=89) i=i+1;   
                           }   
                    } 
                    ii[i]=ii[i]+1;
              }
              for(int i=0;i<ii.length;i++){
                     common.CExecutor.println(i+"   "+ii[i]);
              }
              int aa=i0+i1+i2+i3+i4;
              common.CExecutor.println("Total below 4----"+aa);
       
       }
       
       public void printReadsAbout1SNP(String chr,int pos) throws IOException{
              FileHandler fileHandler= new FileHandler();
              fileHandler.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempReads.sam");
              String line;
              LineHandler lh= new LineHandler();
              while((line=fileHandler.br.readLine())!=null){
                    lh.splitByTab(line);
                     String chrtemp= lh.linesplit[2].trim().toString();
                     int postemp=0;;
                     try {
                         postemp=Integer.parseInt(lh.linesplit[3]);                            
                     } catch (Exception e) {
                            continue;
                     }

                     if(chr.equals(chrtemp)&&(pos-postemp)>=0&&(pos-postemp)<=89)
                            common.CExecutor.println(line);
              }
              
       }
       public void getAllele(int pos,String path) throws IOException{
              FileHandler fileHandler = new FileHandler();
              fileHandler.readFile(path);
              LineHandler lh = new LineHandler();
              String line;
              while((line=fileHandler.br.readLine())!=null){
                     lh.splitByTab(line);
                     int postemp =Integer.parseInt(lh.linesplit[3].trim().toLowerCase());
                     char [] reads=lh.linesplit[9].trim().toString().toCharArray();
                     common.CExecutor.println(String.valueOf(reads[pos-postemp]));
              }
       }
        public void countNoInVcf() throws IOException{
              FileHandler f1= new FileHandler();
              FileHandler f2= new FileHandler();
              f1.readFile("/home/sss/NetBeansProjects/RiceGIPS/notinvcf");
              f2.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempReads.sam");
              LineHandler l1= new LineHandler();
              LineHandler l2= new LineHandler();
              int [] ii=new int[50];
              for(int i=0;i<ii.length;i++){
                     ii[i]=0;
              }
              String line;
              int i0=0;
              int i1=0;
              int i2=0;
              int i3=0;
              int i4=0;              
              while((line=f1.br.readLine())!=null){
                    common.CExecutor.println(line+"         ");
                    l1.splitByTab(line);
                    String snpChr=l1.linesplit[0].trim().toString();
                    int pos=Integer.parseInt(l1.linesplit[1]);
                    int i=0;
                    f2.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempReads.sam");
                    while((line=f2.br.readLine())!=null){
                           l2.splitByTab(line);
                           String chrtemp=l2.linesplit[2].trim().toString();
                           int postemp =Integer.parseInt(l2.linesplit[3]);
                           if(chrtemp.equals(snpChr)){
                               if((pos-postemp)>=0&&(pos-postemp)<=89) i=i+1;   
                           }   
                    } 
                    common.CExecutor.println(String.valueOf(i));
              }
              
       
       }  
        
        
        public void printOther(){
               FileHandler fileHandler1= new FileHandler();
               fileHandler1.readFile("/home/sss/NetBeansProjects/RiceGIPS/toy.vcf");
               FileHandler fileHandler2 =new FileHandler();
               String chr;
               String position;
               HashMap<String ,HashSet<String>> map = new HashMap<>();
               LineHandler lh =new LineHandler();
               String line;
              try {
                     while((line=fileHandler1.br.readLine())!=null){
                            lh.splitByTab(line);
                            String temp=line;
                            boolean flag=false;
                            chr=lh.linesplit[0].trim().toString();
                            position=lh.linesplit[1].trim().toString();
                            fileHandler2.readFile("/home/sss/NetBeansProjects/RiceGIPS/tempPosition");                            
                            while((line=fileHandler2.br.readLine())!=null){
                                   String chrTemp=line.split("\t")[0].trim().toString();
                                   String posTemp=line.split("\t")[1].trim().toString();
                                   if(chr.equals(chrTemp)&&position.equals(posTemp)){
                                         flag=true;
                                         break;
                                   }
                                          
                            }
                            if(!flag)
                                   common.CExecutor.println(temp);
                     }
              } catch (IOException ex) {
                     Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
              }
               
               
        }
        
        public void searchSamFormateFile(String chr,int pos,String path) throws IOException{
               FileHandler fileHandler= new FileHandler();
               fileHandler.readFile(path);
               String line ;
               LineHandler lh = new LineHandler();
               while((line=fileHandler.br.readLine())!=null){
                      if(line.toCharArray()[0]=='@')
                             continue;
                      lh.splitByTab(line);
                      String chrTemp=lh.linesplit[2].trim().toString();
                      int postemp=Integer.parseInt(lh.linesplit[3].trim().toString());
                      if(chr.equals(chrTemp)&&(pos-postemp)>=0&&(pos-postemp)<=89){
                             common.CExecutor.println(line);
                      }
               } 
               common.CExecutor.println("---------------------");
        }
        public void printReadsByName(String name,String filePath) throws IOException{
               FileHandler fileHandler= new FileHandler();
               fileHandler.readFile(filePath);
               String line ;
               LineHandler lh = new LineHandler();
               while((line=fileHandler.br.readLine())!=null){
                      if(line.toCharArray()[0]=='@')
                             continue;
                      lh.splitByTab(line);
                      String nameTemp=lh.linesplit[0].trim().toString();
                      if(name.equals(nameTemp))
                             common.CExecutor.println(line);
               }     
        }
}
