package org.vcell.optimization;

import java.io.File;
import java.util.ArrayList;

import org.vcell.optimization.OptServerImpl.OptRunContext;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.optimization.thrift.OptRunStatus;

import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.PythonSupport.PythonPackage;
import cbit.vcell.resource.PropertyLoader;

public class OptServerImplTest {
	public static void main(String[] args){
		try {
			System.setProperty(PropertyLoader.installationRoot,"/Users/schaff/Documents/workspace-modular/vcell");
			System.setProperty(PropertyLoader.pythonExe,"/Users/schaff/anaconda/bin/python");
			PythonSupport.verifyInstallation(new PythonPackage[] { PythonPackage.COPASI, PythonPackage.LIBSBML, PythonPackage.THRIFT });
			try { Thread.sleep(2000); } catch (InterruptedException e) {}
			File optProbFile = new File(ResourceUtil.getVCellOptPythonDir(),"optprob.bin");
			System.out.println("using optProblem: "+optProbFile.getAbsolutePath());
			OptServerImpl optServerImpl = new OptServerImpl();
			OptProblem optProblem = CopasiServicePython.readOptProblem(optProbFile);
			ArrayList<String> jobIDs = new ArrayList<String>();
			jobIDs.add(optServerImpl.submit(optProblem));
			jobIDs.add(optServerImpl.submit(optProblem));
			optServerImpl.start();
			jobIDs.add(optServerImpl.submit(optProblem));
			jobIDs.add(optServerImpl.submit(optProblem));
			boolean done = false;
			while (!done){
				done = true;
				for (String jobID : jobIDs){
					OptRunContext optRunContext = optServerImpl.getOptRunContextByOptimizationId(jobID);
					if (optRunContext==null){
						throw new RuntimeException("optRunContext was null for id = " + jobID);
					}
					OptRunStatus status = optRunContext.getStatus();
					if (status!=OptRunStatus.Complete && status!=OptRunStatus.Failed){
						done = false;
					}
					
					if (status==OptRunStatus.Complete){
						OptRun optRun = CopasiServicePython.readOptRun(optRunContext.getOptRunBinaryFile());
						System.out.println("job "+jobID+": status "+status+" "+optRun.getOptResultSet().toString());
					}else{
						System.out.println("job "+jobID+": status "+status);
					}
				}
				try {
					Thread.sleep(1000);
				}catch (InterruptedException e){}
			}
			System.out.println("done with all jobs");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
