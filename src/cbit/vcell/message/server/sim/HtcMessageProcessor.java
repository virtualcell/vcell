package cbit.vcell.message.server.sim;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.vcell.util.BeanUtils;
import org.vcell.util.ExecutableException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.util.xml.XmlUtil;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.Solver;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverFactory;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.AbstractSolver;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class HtcMessageProcessor implements Callable<Boolean> {
	private final VCMessage vcMessage;
	private final VCMessageSession sharedMessageProducer;
	private final HtcProxy clonedHtcProxy;
	private final SessionLog log;
	
	public HtcMessageProcessor(VCMessage vcMessage, VCMessageSession sharedMessageProducer, HtcProxy clonedHtcProxy, SessionLog log){
		this.vcMessage = vcMessage;
		this.sharedMessageProducer = sharedMessageProducer;
		this.clonedHtcProxy = clonedHtcProxy;
		this.log = log;
	}
	
	@Override
	public Boolean call() {
		SimulationTask simTask = null;
		try {
			SimulationTaskMessage simTaskMessage = new SimulationTaskMessage(vcMessage);
			simTask = simTaskMessage.getSimulationTask();
			File userdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),simTask.getUserName());
			
			HtcJobID pbsId = submit2PBS(simTask, clonedHtcProxy, log, userdir);
			synchronized (sharedMessageProducer) {
				WorkerEventMessage.sendAccepted(sharedMessageProducer, HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), pbsId);
			}
		} catch (Exception e) {
			log.exception(e);
			if (simTask!=null){
				try {
					synchronized (sharedMessageProducer) {
						WorkerEventMessage.sendFailed(sharedMessageProducer,  HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), SimulationMessage.jobFailed(e.getMessage()));
					}
				} catch (VCMessagingException e1) {
					log.exception(e1);
				}
			}
		}
		return true;
	}
	
	private HtcJobID submit2PBS(SimulationTask simTask, HtcProxy clonedHtcProxy, SessionLog log, File userdir) throws XmlParseException, IOException, SolverException, ExecutableException {

		HtcJobID jobid = null;
		
		String subFile = simTask.getSimulationJob().getSimulationJobID() + clonedHtcProxy.getSubmissionFileExtension();
		String jobname = HtcProxy.createHtcSimJobName(new HtcProxy.SimTaskInfo(simTask.getSimKey(), simTask.getSimulationJob().getJobIndex(), simTask.getTaskID()));   //"S_" + simTask.getSimKey() + "_" + simTask.getSimulationJob().getJobIndex()+ "_" + simTask.getTaskID();
		
		Solver realSolver = (AbstractSolver)SolverFactory.createSolver(log, userdir, simTask, true);
		
		String simTaskXmlText = XmlHelper.simTaskToXML(simTask);
		String simTaskFilePath = forceUnixPath(new File(userdir,simTask.getSimulationJobID()+"_"+simTask.getTaskID()+".simtask.xml").toString());
		
		if (clonedHtcProxy.getCommandService() instanceof CommandServiceSsh){
			// write simTask file locally, and send it to server, and delete local copy.
			File tempFile = File.createTempFile("simTask", "xml");
			XmlUtil.writeXMLStringToFile(simTaskXmlText, tempFile.getAbsolutePath(), true);
			clonedHtcProxy.getCommandService().pushFile(tempFile, simTaskFilePath);
			tempFile.delete();
		}else{
			// write final file directly.
			XmlUtil.writeXMLStringToFile(simTaskXmlText, simTaskFilePath, true);
		}
		
		final String SOLVER_EXIT_CODE_REPLACE_STRING = "SOLVER_EXIT_CODE_REPLACE_STRING";

		KeyValue simKey = simTask.getSimKey();
		User simOwner = simTask.getSimulation().getVersion().getOwner();
		String[] postprocessorCmd = new String[] { 
				PropertyLoader.getRequiredProperty(PropertyLoader.simulationPostprocessor), 
				simKey.toString(),
				simOwner.getName(), 
				simOwner.getID().toString(),
				Integer.toString(simTask.getSimulationJob().getJobIndex()),
				Integer.toString(simTask.getTaskID()),
				SOLVER_EXIT_CODE_REPLACE_STRING
		};

		if (realSolver instanceof AbstractCompiledSolver) {
			
			// compiled solver ...used to be only single executable, now we pass 2 commands to PBSUtils.submitJob that invokes SolverPreprocessor.main() and then the native executable
			String[] preprocessorCmd = new String[] { 
					PropertyLoader.getRequiredProperty(PropertyLoader.simulationPreprocessor), 
					simTaskFilePath, 
					forceUnixPath(userdir.getAbsolutePath())
			};
			String[] nativeExecutableCmd = ((AbstractCompiledSolver)realSolver).getMathExecutableCommand();
			for (int i=0;i<nativeExecutableCmd.length;i++){
				nativeExecutableCmd[i] = forceUnixPath(nativeExecutableCmd[i]);
			}
			nativeExecutableCmd = BeanUtils.addElement(nativeExecutableCmd, "-tid");
			nativeExecutableCmd = BeanUtils.addElement(nativeExecutableCmd, String.valueOf(simTask.getTaskID()));
			
			jobid = clonedHtcProxy.submitJob(jobname, subFile, preprocessorCmd, nativeExecutableCmd, 1, simTask.getEstimatedMemorySizeMB(), postprocessorCmd, SOLVER_EXIT_CODE_REPLACE_STRING);
			if (jobid == null) {
				throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
			}
			
		} else {
			
			String[] command = new String[] { 
					PropertyLoader.getRequiredProperty(PropertyLoader.javaSimulationExecutable), 
					simTaskFilePath,
					forceUnixPath(userdir.getAbsolutePath())
			};

			jobid = clonedHtcProxy.submitJob(jobname, subFile, command, 1, simTask.getEstimatedMemorySizeMB(), postprocessorCmd, SOLVER_EXIT_CODE_REPLACE_STRING);
			if (jobid == null) {
				throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
			}
		}
		return jobid;
	}
	private static String forceUnixPath(String filePath){
		return filePath.replace("C:","").replace("D:","").replace("\\","/");
	}

}