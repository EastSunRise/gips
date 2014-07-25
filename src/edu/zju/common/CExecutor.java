package edu.zju.common;

import edu.zju.file.LogFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 ** @author Zhongxu Zhu
 *  Because JAVA has a class name CExecutor, so this class renamed CExecutor
 */

public class CExecutor {

        private String resultInf;
        private String erroInf;
        private String scriptPath;
        private static String currentDirectory;
        private static long processStartTime=0;
                
        public CExecutor() {
                // Get current address and initiate a script for execute
                this.scriptPath = getCurrentDirectoy()+getFileSeparator() + ".script.sh";
               
        }

        /**
         * Linux command use line break '\n' for more command This command is
         * writed into a .sh file ,then run this shell and delete after running.
         * So this file should be synchronized, only one command can be cunning
         * at the same time.
         *
         * @param command Linux 
         */
        public void execute(String command) {
                this.writeCommandIntoScript(command);
                this.excuteCommand();
                new FileHandler().deleteFile(this.scriptPath);
        }
        
        
        private void excuteCommand() {
                try {
                        Process ps = null;
                        //file has a function named set executable . see 
                        Runtime.getRuntime().exec("chmod 711 " + this.scriptPath);
                        ps = Runtime.getRuntime().exec(this.scriptPath);
                        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
                        BufferedReader brEeror = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
                        StringBuffer temp = new StringBuffer();
                        String line;
                        while ((line = br.readLine()) != null) {
                                temp.append(line + '\n');
                        }
                        br.close();
                        resultInf = temp.toString();
                        temp = new StringBuffer();
                        while ((line = brEeror.readLine()) != null) {
                                temp.append(line + '\n');
                        }
                        brEeror.close();
                        erroInf = temp.toString();
                        temp = null;
                } catch (IOException ex) {
                        //Logger.getLogger(CExecutor.class.getName()).log(Level.SEVERE, null, ex);
                        this.excuteCommand();
                }
        }

        private void writeCommandIntoScript(String command) {
                try {
                        FileHandler fileHandler = new FileHandler();
                        fileHandler.writeFile(this.scriptPath);
                        fileHandler.bw.write(command);
                        fileHandler.bw.flush();
                        fileHandler.bw.close();
                } catch (IOException ex) {
                        Logger.getLogger(CExecutor.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        /**
         * check whether the script is running successfully
         * @return true: success; false: unsuccessful
         */
        private boolean isSucceed() {
                if (this.getErroInformation().isEmpty()) {
                        return true;
                } else {
                        return false;
                }
        }

        public String getErroInformation() {
                return this.erroInf;
        }

        public String getResultInformation() {
                return this.resultInf;
        }
        public static String getFileSeparator(){
                return System.getProperty("file.separator");
        }
        public static String getCurrentDirectoy() {
                if (currentDirectory == null) {
                        String temp = new CExecutor().getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                        currentDirectory = new File(temp).getParent().toString().trim();
                        currentDirectory = currentDirectory.replace(System.getProperty("file.separator") + "build", "");
                }
                return currentDirectory;

        }

        public static String getRunningTime() {
                if(processStartTime==0){
                        processStartTime=System.currentTimeMillis();
                        println("\nJob start time: "+getCurrentTime());
                }
                return new String((new SimpleDateFormat("HH:mm:ss.SS")).format(System.currentTimeMillis()-processStartTime-28800000)+"\t");
        }
        public static String getCurrentTime(){
                return new String((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis()));
        }
        public static void stopProgram(String info){
                println(getRunningTime()+info+"\n");
                System.exit(1);
        }
        public static void println(String content) {
                System.out.println(content);
                LogFile log = new LogFile(getCurrentDirectoy() + System.getProperty("file.separator") + "log.gips");
                log.write(content + "\n");
        }
        public static void print(String content){
                System.out.print(content);
                LogFile log = new LogFile(getCurrentDirectoy() + System.getProperty("file.separator") + "log.gips");
                log.write(content);
        }
}
