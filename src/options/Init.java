/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package options;

import file.CommonOutputFile;
import file.Config;
import file.FileFactory;
import java.io.File;
import java.util.LinkedList;
import parameter.ParameterList;
import parameter.ParameterLoader;

/**
 *
 * @author sss
 */
public class Init {
        private static String projectString;//path or name
        private static String projectName;
        private static String projectDirectory;
        //gips result will in working dirctory
        private static String workingDirectory;
        private static String callerScriptDirectory;
        private static String dataDirectory;
        private static String parameterFilePath;
        private static String referenceDirectory;
        private static String temporaryFolder;
        private static String resultArchiveFolder;
        
        
        private LinkedList<String> GlobalParaList;
                
                
        public Init(String project) {
                projectString=project;
                this.initiateFilePath(project);
        }
        
        private void parseProjectPath(String project){
                File p=new File(project);
                if(p.isDirectory()){
                        common.CExecutor.stopProgram("Project : \t"+project+" has been created before\n");
                }else if(p.isFile()){
                        common.CExecutor.stopProgram("Project : \t"+project+" should not be a file\n");
                }else if(p.isHidden()){
                        common.CExecutor.stopProgram("Project : \t"+project+" should not be hidden\n");
                }
        }
        
        public static String getParameterFilePath(){
                if(parameterFilePath==null) {
                        common.CExecutor.stopProgram("Do not find project, please specify a project following \'-p\' option");
                }
                return parameterFilePath;
        }
        public static String getProjectName(){
                return projectName;
        }
        public static String getProjectString(){
                return projectString;
        }
        public static String getProjectDirectory(){
                if(projectDirectory==null)common.CExecutor.stopProgram("Can't find project directory path");
                return projectDirectory;
        }        
        public void createProjectDirectory(){
                String projectDirectory=getProjectDirectory();
                this.parseProjectPath(projectDirectory);
                File file = new java.io.File(projectDirectory);
                file.mkdirs();
                new File(this.workingDirectory).mkdirs();
                new java.io.File(this.callerScriptDirectory).mkdirs();
                new java.io.File(this.dataDirectory).mkdirs();
                new java.io.File(this.referenceDirectory).mkdirs();
                new java.io.File(this.temporaryFolder).mkdirs();
                new java.io.File(this.resultArchiveFolder).mkdirs();
                
                this.initParameterFile(this.parameterFilePath);
        }
        private void initiateFilePath(String project){
                File p=new File(project);
                String projectDirectory=p.getAbsolutePath();
                this.setProjectDirectory(projectDirectory);
                this.workingDirectory=projectDirectory+common.CExecutor.getFileSeparator()+"Working";
                this.callerScriptDirectory=projectDirectory+common.CExecutor.getFileSeparator()+"Script";
                this.dataDirectory=projectDirectory+common.CExecutor.getFileSeparator()+"Data";
                this.parameterFilePath=projectDirectory+common.CExecutor.getFileSeparator()+"PROJECT.ini";
                this.referenceDirectory=projectDirectory+common.CExecutor.getFileSeparator()+"Ref";
                this.temporaryFolder=workingDirectory+common.CExecutor.getFileSeparator()+Config.getItem("IntermediateFile");
                this.resultArchiveFolder=workingDirectory+common.CExecutor.getFileSeparator()+"Archive";
                common.CExecutor.println("Project: "+projectName+"\nDirectory: "+projectDirectory);
        }
        private void setProjectDirectory(String path){
                projectDirectory=path;
                projectName=path.split(common.CExecutor.getFileSeparator())[path.split(common.CExecutor.getFileSeparator()).length-1];
        }
        public String getWorkingDirectory(){
                return this.workingDirectory;
        }
        private void initParameterFile(String path){
                CommonOutputFile parameterFile=FileFactory.getOutputFile(path);
                ParameterLoader parameterLoader=new ParameterLoader();
                ParameterList parameterList= new ParameterList();
                LinkedList<String> globalParameter=parameterList.getGlobalParaList();
                for(String item: globalParameter){
                        if(item.startsWith("PROJECT")){
                                parameterFile.write("PROJECT :"+projectName+item.split(":")[1]+'\n');
                                continue;
                        }
                        if(item.startsWith("LIB_PH")){
                                parameterFile.write("LIB_PHENOTYPE_VAR : "+common.CExecutor.getCurrentDirectoy()+common.CExecutor.getFileSeparator()+"GIPS.jar"+Config.getItem("CLIN_VAR_LIB")+"\n");
                                continue;
                        }
                        parameterFile.write(item+"\n");
                }
                parameterFile.write("\n[SAMPLE_LIST]\n\n");
                LinkedList<String> sampleBasicParater=parameterList.getSampleBasicParaList();
                for(String item:sampleBasicParater){
                        parameterFile.write(item+'\n');
                }
                parameterFile.closeOutput();
        
        }
        //in case .ini's projectName not corresponded with projectname in path
        public static void setProjectName(String name){
                projectName=name;
        }
        public static String getWorkingDirectroy(){
                return workingDirectory;
        }
        public static String getResultArchiveFolderDirectory(){
                return resultArchiveFolder;
        }
        public static String getRefDirectory(){
                return referenceDirectory;
        }
        public static String getScriptDirectory(){
                return callerScriptDirectory;
        }
        public static String getDataDirectory(){
                return dataDirectory;
        }
        public static String getTemporaryFolderPath(){
                return temporaryFolder;
        }
}        