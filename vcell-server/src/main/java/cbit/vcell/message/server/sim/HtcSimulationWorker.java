/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.sim;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.exe.ExecutableException;

import cbit.util.xml.XmlUtil;
import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCPooledQueueConsumer;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VtkMeshGenerator;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.AbstractSolver;
import cbit.vcell.solvers.ExecutableCommand;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class HtcSimulationWorker extends ServiceProvider  {

	private HtcProxy htcProxy = null;

	private VCQueueConsumer queueConsumer = null;
	private VCMessageSession sharedMessageProducer = null;
	private VCPooledQueueConsumer pooledQueueConsumer = null;
	public static Logger lg = Logger.getLogger(HtcSimulationWorker.class);

	/**
	 * SimulationWorker constructor comment.
	 * @param argName java.lang.String
	 * @param argParentNode cbit.vcell.appserver.ComputationalNode
	 * @param argInitialContext javax.naming.Context
	 */
public HtcSimulationWorker(HtcProxy htcProxy, VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, SessionLog log, boolean bSlaveMode) throws DataAccessException, FileNotFoundException, UnknownHostException {
	super(vcMessagingService, serviceInstanceStatus, log, bSlaveMode);
	this.htcProxy = htcProxy;
}

public final String getJobSelector() {
	String jobSelector = "(" + VCMessagingConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "')";

	return jobSelector;
}

public void init() {
	initControlTopicListener();
	initQueueConsumer();
}


private static class PostProcessingChores {
	/**
	 * where solver runs
	 */
	final String runDirectory;
	/**
	 * where data ends up
	 */
	final String finalDataDirectory;

	/**
	 * will we need a VTK mesh?
	 */
	private boolean isVtk;

	/**
	 * directories are same
	 * @param runDirectory
	 */
	PostProcessingChores(String runDirectory) {
		this(runDirectory,runDirectory);
	}

	/**
	 * directories are different
	 * @param runDirectory
	 * @param finalDataDirectory
	 */
	PostProcessingChores(String runDirectory, String finalDataDirectory) {
		this.runDirectory = runDirectory;
		this.finalDataDirectory = finalDataDirectory;
		isVtk = false;
	}

	boolean isCopyNeeded( ) {
		return !runDirectory.equals(finalDataDirectory);
	}
	boolean isParallel( ) {
		return !runDirectory.equals(finalDataDirectory);
	}

	public boolean isVtkUser() {
		return isVtk;
	}

	public void setVtkUser(boolean isVtk) {
		this.isVtk = isVtk;
	}

	@Override
	public String toString() {
		return "PostProcessorChores( " +runDirectory + ", "  + finalDataDirectory + ", isVtkUser " + isVtk + ")";
	}
}

/**
 * determine post processing chores to been done after the simulation completes
 * @param simTask
 * @return PostProcessingChores
 */
private PostProcessingChores choresFor(SimulationTask simTask) {
	String userDir = "/" + simTask.getUserName();
	String primaryExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);
	PostProcessingChores chores = null;
	final SolverTaskDescription slvTaskDesc = simTask.getSimulation( ).getSolverTaskDescription();
	if (!slvTaskDesc.isParallel()) {
		chores = new PostProcessingChores(primaryExternal + userDir);
	}
	else {
		String runDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.PARALLEL_DATA_DIR_EXTERNAL);
		chores = new PostProcessingChores(runDirExternal + userDir , primaryExternal + userDir);
	}
	chores.setVtkUser( slvTaskDesc.isVtkUser() ) ;

	if (lg.isDebugEnabled( )) {
		lg.debug("Simulation " + simTask.getSimulation().getDescription() + " task " + simTask.getTaskID()
				+ " with " + slvTaskDesc.getNumProcessors() + " processors using " + chores);
	}
	return chores;
}

private void initQueueConsumer() {

	this.sharedMessageProducer = vcMessagingService.createProducerSession();

	QueueListener queueListener = new QueueListener() {

		@Override
		public void onQueueMessage(VCMessage vcMessage, VCMessageSession session) throws RollbackException {
			SimulationTask simTask = null;
			try {
				SimulationTaskMessage simTaskMessage = new SimulationTaskMessage(vcMessage);
				simTask = simTaskMessage.getSimulationTask();
				if (lg.isInfoEnabled()) {
					lg.info("onQueueMessage() run simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName());
				}
				PostProcessingChores rd = choresFor(simTask);
				HtcProxy clonedHtcProxy = htcProxy.cloneThreadsafe();
				if (lg.isInfoEnabled()) {
					lg.info("onQueueMessage() submit job: simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName());
				}
				HtcJobID pbsId = submit2PBS(simTask, clonedHtcProxy, log, rd);
				if (lg.isInfoEnabled()) {
					lg.info("onQueueMessage() sending 'accepted' message for job: simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName());
				}
				synchronized (sharedMessageProducer) {
					WorkerEventMessage.sendAccepted(sharedMessageProducer, HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), pbsId);
				}
				if (lg.isInfoEnabled()) {
					lg.info("onQueueMessage() sent 'accepted' message for job: simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName());
				}
			} catch (Exception e) {
				log.exception(e);
				if (simTask!=null){
					try {
						lg.error("failed to process simTask request: "+e.getMessage()+" for simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName(), e);
						synchronized (sharedMessageProducer) {
							WorkerEventMessage.sendFailed(sharedMessageProducer,  HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), SimulationMessage.jobFailed(e.getMessage()));
						}
						lg.error("sent 'failed' message for simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName(), e);
					} catch (VCMessagingException e1) {
						log.exception(e1);
					}
				}else {
					lg.error("failed to process simTask request: "+e.getMessage(), e);
				}
			}
		}
	};

	int numHtcworkerThreads = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.htcworkerThreadsProperty, "5"));
	this.pooledQueueConsumer = new VCPooledQueueConsumer(queueListener, log, numHtcworkerThreads, sharedMessageProducer);
	this.pooledQueueConsumer.initThreadPool();
	VCellQueue queue = VCellQueue.SimJobQueue;
	VCMessageSelector selector = vcMessagingService.createSelector(getJobSelector());
	String threadName = "SimJob Queue Consumer";
	queueConsumer = new VCQueueConsumer(queue, pooledQueueConsumer, selector, threadName, MessageConstants.PREFETCH_LIMIT_SIM_JOB_HTC);
	vcMessagingService.addMessageConsumer(queueConsumer);
}

private HtcJobID submit2PBS(SimulationTask simTask, HtcProxy clonedHtcProxy, SessionLog log, PostProcessingChores chores) throws XmlParseException, IOException, SolverException, ExecutableException {

	HtcJobID jobid = null;
	String htcLogDirExternalString = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal)).getAbsolutePath();
	if (!htcLogDirExternalString.endsWith("/")){
		htcLogDirExternalString = htcLogDirExternalString+"/";
    }
    String jobname = HtcProxy.createHtcSimJobName(new HtcProxy.SimTaskInfo(simTask.getSimKey(), simTask.getSimulationJob().getJobIndex(), simTask.getTaskID()));   //"S_" + simTask.getSimKey() + "_" + simTask.getSimulationJob().getJobIndex()+ "_" + simTask.getTaskID();
	String subFileExternal = htcLogDirExternalString+jobname + clonedHtcProxy.getSubmissionFileExtension();

	File parallelDir = new File(chores.runDirectory);
	File primaryUserDir = new File(chores.finalDataDirectory);
	Solver realSolver = (AbstractSolver)SolverFactory.createSolver(log, primaryUserDir,parallelDir, simTask, true);
	realSolver.setUnixMode();

	String simTaskXmlText = XmlHelper.simTaskToXML(simTask);
	String simTaskFilePath = ResourceUtil.forceUnixPath(new File(primaryUserDir ,simTask.getSimulationJobID()+"_"+simTask.getTaskID()+".simtask.xml").toString());

	if (clonedHtcProxy.getCommandService() instanceof CommandServiceSsh){
		// write simTask file locally, and send it to server, and delete local copy.
		File tempFile = File.createTempFile("simTask", "xml");
		XmlUtil.writeXMLStringToFile(simTaskXmlText, tempFile.getAbsolutePath(), true);
		clonedHtcProxy.getCommandService().command(new String[] { "mkdir", "-p", ResourceUtil.forceUnixPath(primaryUserDir.getAbsolutePath()) });
		clonedHtcProxy.getCommandService().pushFile(tempFile, simTaskFilePath);
		tempFile.delete();
	}else{
		// write final file directly.
		FileUtils.forceMkdir(primaryUserDir);
		XmlUtil.writeXMLStringToFile(simTaskXmlText, simTaskFilePath, true);
	}

	final String SOLVER_EXIT_CODE_REPLACE_STRING = "SOLVER_EXIT_CODE_REPLACE_STRING";

	KeyValue simKey = simTask.getSimKey();
	User simOwner = simTask.getSimulation().getVersion().getOwner();
	final int jobId = simTask.getSimulationJob().getJobIndex();

	ExecutableCommand.Container commandContainer = new ExecutableCommand.Container( );
	//the post processor command itself is neither messaging nor parallel; it's independent of the previous solver call
	ExecutableCommand postprocessorCmd = new ExecutableCommand(null,false, false,
			PropertyLoader.getRequiredProperty(PropertyLoader.simulationPostprocessor),
			simKey.toString(),
			simOwner.getName(),
			simOwner.getID().toString(),
			Integer.toString(jobId),
			Integer.toString(simTask.getTaskID()),
			SOLVER_EXIT_CODE_REPLACE_STRING,
			subFileExternal);
	postprocessorCmd.setExitCodeToken(SOLVER_EXIT_CODE_REPLACE_STRING);
	commandContainer.add(postprocessorCmd);

	int ncpus = simTask.getSimulation().getSolverTaskDescription().getNumProcessors(); //CBN?

	Collection<PortableCommand> postProcessingCommands = new ArrayList<PortableCommand>();
	if (realSolver instanceof AbstractCompiledSolver) {
		AbstractCompiledSolver compiledSolver = (AbstractCompiledSolver)realSolver;

		List<String> args = new ArrayList<>( 4 );
		args.add( PropertyLoader.getRequiredProperty(PropertyLoader.simulationPreprocessor) );
		args.add( simTaskFilePath );
		args.add( primaryUserDir.getAbsolutePath() );
		if ( chores.isParallel()) {
			args.add(chores.runDirectory);
		}
		// compiled solver ...used to be only single executable, now we pass 2 commands to PBSUtils.submitJob that invokes SolverPreprocessor.main() and then the native executable
		//the pre-processor command itself is neither messaging nor parallel; it's independent of the subsequent solver call
		ExecutableCommand preprocessorCmd = new ExecutableCommand(null, false, false,args);
		commandContainer.add(preprocessorCmd);
		
		for (ExecutableCommand ec  : compiledSolver.getCommands()) {
			if (ec.isMessaging()) {
				ec.addArgument("-tid");
				ec.addArgument(simTask.getTaskID());
			}
			commandContainer.add(ec);
		}

		if (chores.isCopyNeeded()) {
			String logName = chores.finalDataDirectory + '/' + SimulationData.createCanonicalSimLogFileName(simKey, jobId, false);
			CopySimFiles csf = new CopySimFiles(simTask.getSimulationJobID(), chores.runDirectory,chores.finalDataDirectory, logName);
			postProcessingCommands.add(csf);
		}
		if (chores.isVtkUser()) {
			VtkMeshGenerator vmg = new VtkMeshGenerator(simOwner, simKey, jobId);
			postProcessingCommands.add(vmg);
		}

	} else {
		ExecutableCommand ec = new ExecutableCommand(null, false,false,
				PropertyLoader.getRequiredProperty(PropertyLoader.javaSimulationExecutable),
				simTaskFilePath,
				ResourceUtil.forceUnixPath(parallelDir.getAbsolutePath())
		);
		commandContainer.add(ec);
	}

	jobid = clonedHtcProxy.submitJob(jobname, subFileExternal, commandContainer, ncpus, simTask.getEstimatedMemorySizeMB(), postProcessingCommands);
	if (jobid == null) {
		throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
		}
	return jobid;
}

@Override
public void stopService(){
	this.pooledQueueConsumer.shutdownAndAwaitTermination();
	super.stopService();
}

///**
// * Starts the application.
// * @param args an array of command-line arguments
// */
//public static void main(java.lang.String[] args) {
//	if (args.length != 3 && args.length != 6) {
//		System.out.println("Missing arguments: " + HtcSimulationWorker.class.getName() + " serviceOrdinal (logdir|-) (PBS|SGE|SLURM) [pbshost userid pswd] ");
//		System.exit(1);
//	}
//
//	//
//	// Create and install a security manager
//	//
//	try {
//		PropertyLoader.loadProperties();
//
//		int serviceOrdinal = Integer.parseInt(args[0]);
//		VCMongoMessage.serviceStartup(ServiceName.pbsWorker, new Integer(serviceOrdinal), args);
//		String logdir = args[1];
//		BatchSystemType batchSystemType = BatchSystemType.valueOf(args[2]);
//
//		CommandService commandService = null;
//		if (args.length==6){
//			String pbsHost = args[3];
//			String pbsUser = args[4];
//			String pbsPswd = args[5];
//			commandService = new CommandServiceSsh(pbsHost,pbsUser,pbsPswd);
//			AbstractSolver.bMakeUserDirs = false; // can't make user directories, they are remote.
//		}else{
//			commandService = new CommandServiceLocal();
//		}
//		HtcProxy htcProxy = null;
//		switch(batchSystemType){
//			case PBS:{
//				htcProxy = new PbsProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
//				break;
//			}
//			case SGE:{
//				htcProxy = new SgeProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
//				break;
//			}
//			case SLURM:{
//				htcProxy = new SlurmProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
//				break;
//			}
//		}
//
//		ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.PBSCOMPUTE, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
//		initLog(serviceInstanceStatus, logdir);
//
//		//
//		// JMX registration
//		//
//		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//		mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));
//
//        VCMessagingService vcMessagingService = VCMessagingService.createInstance(new ServerMessagingDelegate());
//
//		SessionLog log = new StdoutSessionLog(serviceInstanceStatus.getID());
//		HtcSimulationWorker simulationWorker = new HtcSimulationWorker(htcProxy, vcMessagingService, serviceInstanceStatus, log, false);
//		simulationWorker.init();
//
//	} catch (Throwable e) {
//		e.printStackTrace(System.out);
//		VCMongoMessage.sendException(e);
//		VCMongoMessage.flush();
//		System.exit(-1);
//	}
//}


}
