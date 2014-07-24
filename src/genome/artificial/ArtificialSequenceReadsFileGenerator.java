/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genome.artificial;

import common.LineHandler;
import file.CommonInputFile;
import file.CommonOutputFile;
import file.Config;
import file.FileFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import parameter.GlobalParameter;
import parameter.SampleParameterBag;

/**
 *
 * @author sss
 */
public class ArtificialSequenceReadsFileGenerator {
       private CommonInputFile sampleSequenceReadsFile;
       private SampleArtificialGenome sampleArtificialGenome;
       private int readsNumber=0;
       
       public ArtificialSequenceReadsFileGenerator(CommonInputFile sampleSequenceReadsFile,SampleArtificialGenome sampleArtificialGenome) {
              this.setSampleSequenceReadsFilePath(sampleSequenceReadsFile);
              this.setSampleArtificialGenome(sampleArtificialGenome);
       }
    
       private void setSampleSequenceReadsFilePath(CommonInputFile file){
              if(!file.isFile()){
                      common.CExecutor.stopProgram(file.getFilePath()+" does not exist!");
              }
              this.sampleSequenceReadsFile=file;
       }
       private void setSampleArtificialGenome(SampleArtificialGenome genome){
              this.sampleArtificialGenome=genome;
       }
       private CommonInputFile getSampleSequenceReadsFile(){
              return this.sampleSequenceReadsFile;
       }
       private SampleArtificialGenome getSampleArtificialGenome(){
              return this.sampleArtificialGenome;
       }
       /**
        * 
        * @return return the artificial sequence reads file path
        */
       public CommonInputFile generate(){
              //common.CExecutor.println(this.sampleArtificialGenome.getSampleName()+" artificial reads is generating ------"+CExecutor.getRunningTime());
              String path=SampleParameterBag.getIntermediateFilePath()+System.getProperty("file.separator")+this.sampleArtificialGenome.getSampleName()+System.getProperty("file.separator")+Config.getItem("SIM_READS_FILE");
              CommonInputFile inputFile=this.getSampleSequenceReadsFile();
              CommonOutputFile outputFile=FileFactory.getOutputFile(path);
              String line =null;
              //int readsNumber=0;
              do {        //skip annotation            
                     line=inputFile.readLine();
                     if(line.toCharArray()[0]!='@'&&line.split("\t").length>3) break;
                     outputFile.write(line+'\n');                            
              } while (true); 
              //this first readLine information, note just one read
              ArtificialReadGenerator artificialReadGenerator;
              LineHandler lh =new LineHandler();
              do{
                     lh.splitByTab(line);
                     String chrID;
                     try {
                            chrID=lh.linesplit[2]; 
                            if(chrID.equals("*")) {
                                    break;
                            }
                     } catch (java.lang.NumberFormatException e) {
                            break;//in the reference sequence name field ,it may be * . Then this excpetion will occure
                     }  
                     if(chrID.equals("ChrUn")||chrID.equals("ChrSy")) break;
                     SampleArtificialChromosome sampleArtificialChromosome=this.getSampleArtificialGenome().getSampleArtificialChromosome(chrID);
                     artificialReadGenerator = new ArtificialReadGenerator(sampleArtificialChromosome, line);
                     line=artificialReadGenerator.getNewRead();
                     if(line!=null) {
                            outputFile.write(line+'\n');
                  //          readsNumber=readsNumber+1;
                     }
                     this.addReadsNumber(1);
                     break;
              }while(true);
              common.CExecutor.print(common.CExecutor.getRunningTime()+"Reading "+inputFile.getFilePath()+". Please wait");
              //Thread number 
              int threadNumber=GlobalParameter.getThreadsNumber();
              Thread thread[]=new Thread[threadNumber];
              for(int i=0;i<threadNumber;i++){
                       thread[i]=new Thread(new RunnableArtificialReadsGenerator(inputFile, outputFile));
                       thread[i].start();
              }      
              for(int i=0;i<threadNumber;i++){
                       try {
                               thread[i].join();
                       } catch (InterruptedException ex) {
                               Logger.getLogger(ArtificialSequenceReadsFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
                       }
              } 
              outputFile.closeOutput();
              inputFile.closeInput();
              CommonInputFile samFile=FileFactory.getInputFile(path, "SAM");
              common.CExecutor.println("\n"+common.CExecutor.getRunningTime()+"SAM format file: "+samFile.getFilePath());
              common.CExecutor.println(common.CExecutor.getRunningTime()+"Reads number for simulated SNPs: "+this.getReadsNumber()); 
              return samFile;
       }
       private synchronized void addReadsNumber(int number){
              this.readsNumber=this.readsNumber+number;
       }
       private int getReadsNumber(){
              return this.readsNumber;
       }

       
       
       private class RunnableArtificialReadsGenerator implements Runnable{
               private CommonInputFile input;
               private CommonOutputFile output;
               
                public RunnableArtificialReadsGenerator(CommonInputFile input,CommonOutputFile output) {
                        this.input=input;
                        this.output=output;
                }
                @Override
                public void run() {
                         String line =null;
                         int readsNumber=0;
                         ArtificialReadGenerator artificialReadGenerator;
                         LineHandler lh =new LineHandler();
                         while((line=this.input.readLine())!=null){
                                lh.splitByTab(line);
                                String chrID;
                                chrID=lh.linesplit[2].toString(); 
                                if(chrID.equals("*")) continue;
                                if(chrID.equals("ChrUn")||chrID.equals("ChrSy")) continue;
                                SampleArtificialChromosome sampleArtificialChromosome=getSampleArtificialGenome().getSampleArtificialChromosome(chrID);
                                artificialReadGenerator = new ArtificialReadGenerator(sampleArtificialChromosome, line);
                                
                                line=artificialReadGenerator.getNewRead();
                                if(line!=null) {
                                       this.output.write(line+'\n');
                                       readsNumber=readsNumber+1;
                                       int s=readsNumber%100000;
                                       if(s==0){
                                               common.CExecutor.print(".");
                                       }
                                } 
                                
                         }
                         addReadsNumber(readsNumber);
                }
               
       }

       
       
       
}
