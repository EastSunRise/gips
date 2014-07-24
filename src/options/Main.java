/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package options;

import gips.GIPS;
import gips.ResultOutputer;
import java.io.IOException;
import parameter.GIPSJob;
import parameter.GlobalParameter;
import parameter.ParameterLoader;

/**
 *
 * @author sss
 */
public class Main {

        private static String gipsHeader=""
                                 +"==============================================================================\n"
                                 +"        Gene Identification via Phenotype Sequencing  (Version 1.x.x)\n"                            
                                 +"     Copyright(c) 2013-2014, Zhongxu Zhu, Xin Chen. All Rights Reserved.\n"   
                                 +"==============================================================================\n";
        
        public static void main(String[] args) throws IOException, Exception {
                common.CExecutor.getRunningTime();
                boolean toolType = false;
                GIPSJob job=new GIPSJob();
                ParameterLoader ploader=new ParameterLoader();
                for (int i = 0; i < args.length; i++) {
                        switch (args[i++]) {
                                case "-T" :{
                                        GlobalParameter.setToolType(args[i]);
                                        job.setJobType(args[i]);break;
                                }
                                case "-init":{
                                        Init init=new Init(args[i]);
                                        init.createProjectDirectory();
                                        job.setJobType("init");
                                        break;
                                }
                                case "-p" :{
                                        Init init=new Init(args[i]);break;
                                } 
                                case "-update" :{
                                       job.setJobNeedUpdate();i=i-1;break;
                                }
                                case "-Test" :{
                                        TESTProject testp=new TESTProject();
                                        testp.initiateTestProject(common.CExecutor.getCurrentDirectoy());
                                        job.setJobType("Test");
                                        break;
                                }
                                case "-h" :{
                                        new HelpOption().showHelp();
                                        common.CExecutor.stopProgram("");
                                }
                                case "-H":{
                                        new HelpOption().showHelp();
                                        common.CExecutor.stopProgram("");
                                }
                                default:{
                                        common.CExecutor.stopProgram("Do not find option "+args[i-1]);
                                }
                        }
                }
                System.out.println(gipsHeader);
                job.setGlobalParameter(ploader.loadGlobalParameter(options.Init.getParameterFilePath()));
                job.setSampleParameterBag(ploader.loadSampleSpecificParameter(options.Init.getParameterFilePath()));
                job.jobCheck();
                GIPS gips=new GIPS(job);
                job=gips.gipsRun();
                ResultOutputer ro=new ResultOutputer(job);
                ro.outputResult();
        }

}
