package org.vcell.rest.server;

import java.io.File;
import java.util.ArrayList;

import org.vcell.optimization.CopasiServicePython;
import org.vcell.optimization.OptRunContext;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.optimization.thrift.OptRunStatus;
import org.vcell.service.VCellServiceHelper;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingDelegate;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.ResourceUtil;

public class OptServerMessagingTest {
	public static void main(String[] args){
		try {
			System.setProperty(PropertyLoader.installationRoot,"/Users/schaff/Documents/workspace-modular/vcell");
			System.setProperty(PropertyLoader.pythonExe,"/Users/schaff/anaconda/bin/python");
			try { Thread.sleep(2000); } catch (InterruptedException e) {}
			File optProbFile = new File(ResourceUtil.getVCellOptPythonDir(),"optprob.bin");
			System.out.println("using optProblem: "+optProbFile.getAbsolutePath());
			VCMessagingService vcMessagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class);
			vcMessagingService.setDelegate(new VCMessagingDelegate() {
				
				@Override
				public void onTraceEvent(String string) {
					System.out.println("Trace: "+string);
				}
				
				@Override
				public void onRpcRequestSent(VCRpcRequest vcRpcRequest, UserLoginInfo userLoginInfo, VCMessage vcRpcRequestMessage) {
					System.out.println("request sent:");
				}
				
				@Override
				public void onRpcRequestProcessed(VCRpcRequest vcRpcRequest, VCMessage rpcVCMessage) {
					System.out.println("request processed:");
				}
				
				@Override
				public void onMessageSent(VCMessage message, VCDestination desintation) {
					System.out.println("message sent to "+desintation.getName());
				}
				
				@Override
				public void onMessageReceived(VCMessage vcMessage, VCDestination vcDestination) {
					System.out.println("message received");
				}
				
				@Override
				public void onException(Exception e) {
					System.out.println("Exception: "+e.getMessage());
					e.printStackTrace();
				}
			});

			OptServerMessaging optServerLocalImpl = new OptServerMessaging(vcMessagingService);
			OptProblem optProblem = CopasiServicePython.readOptProblem(optProbFile);
			ArrayList<String> jobIDs = new ArrayList<String>();
			jobIDs.add(optServerLocalImpl.submit(optProblem));
			jobIDs.add(optServerLocalImpl.submit(optProblem));
			jobIDs.add(optServerLocalImpl.submit(optProblem));
			jobIDs.add(optServerLocalImpl.submit(optProblem));
			boolean done = false;
			while (!done){
				done = true;
				for (String jobID : jobIDs){
					OptRunContext optRunContext = optServerLocalImpl.getOptRunContextByOptimizationId(jobID);
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
