/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.batch.sim;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;
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
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.bootstrap.ServiceType;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSshNative;
import cbit.vcell.message.server.combined.VCellServices;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.slurm.SlurmProxy;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VtkMeshGenerator;
import cbit.vcell.solver.SolverDescription;
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
	private VCMessageSession messageProducer_sim = null;
	private VCMessageSession messageProducer_int = null;
	private VCPooledQueueConsumer pooledQueueConsumer_int = null;
	public static Logger lg = LogManager.getLogger(HtcSimulationWorker.class);

	/**
	 * SimulationWorker constructor comment.
	 * @param argName java.lang.String
	 * @param argParentNode cbit.vcell.appserver.ComputationalNode
	 * @param argInitialContext javax.naming.Context
	 */
public HtcSimulationWorker(HtcProxy htcProxy, VCMessagingService vcMessagingService_int, VCMessagingService vcMessagingService_sim, ServiceInstanceStatus serviceInstanceStatus, boolean bSlaveMode) throws DataAccessException, FileNotFoundException, UnknownHostException {
	super(vcMessagingService_int, vcMessagingService_sim, serviceInstanceStatus, bSlaveMode);
	this.htcProxy = htcProxy;
}

public final String getJobSelector() {
	String jobSelector = "(" + VCMessagingConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "')";

	return jobSelector;
}

public void init() {
	initQueueConsumer();
}


private static class PostProcessingChores {
	/**
	 * where solver runs
	 */
	final String runDirectoryExternal;
	/**
	 * where data ends up
	 */
	final String finalDataDirectoryExternal;

	/**
	 * where solver runs
	 */
	final String runDirectoryInternal;
	/**
	 * where data ends up
	 */
	final String finalDataDirectoryInternal;

	/**
	 * will we need a VTK mesh?
	 */
	private boolean isVtk;

	/**
	 * directories are same
	 * @param runDirectory
	 */
	PostProcessingChores(String runDirectoryInternal, String runDirectoryExternal) {
		this(runDirectoryInternal,runDirectoryExternal,runDirectoryInternal,runDirectoryExternal);
	}

	/**
	 * directories are different
	 * @param runDirectory
	 * @param finalDataDirectory
	 */
	PostProcessingChores(String runDirectoryInternal, String runDirectoryExternal, String finalDataDirectoryInternal, String finalDataDirectoryExternal) {
		this.runDirectoryInternal = runDirectoryInternal;
		this.runDirectoryExternal = runDirectoryExternal;
		this.finalDataDirectoryInternal = finalDataDirectoryInternal;
		this.finalDataDirectoryExternal = finalDataDirectoryExternal;
		isVtk = false;
	}

	boolean isCopyNeeded( ) {
		return !runDirectoryExternal.equals(finalDataDirectoryExternal);
	}
	boolean isParallel( ) {
		return !runDirectoryExternal.equals(finalDataDirectoryExternal);
	}

	public boolean isVtkUser() {
		return isVtk;
	}

	public void setVtkUser(boolean isVtk) {
		this.isVtk = isVtk;
	}

	@Override
	public String toString() {
		return "PostProcessorChores( " +runDirectoryExternal + ", "  + finalDataDirectoryExternal + ", isVtkUser " + isVtk + ")";
	}
}

/**
 * determine post processing chores to been done after the simulation completes
 * @param simTask
 * @return PostProcessingChores
 */
private PostProcessingChores choresFor(SimulationTask simTask) {
	String userDir = "/" + simTask.getUserName();
	String primaryInternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty);
	String primaryExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);
	PostProcessingChores chores = null;
	final SolverTaskDescription slvTaskDesc = simTask.getSimulation( ).getSolverTaskDescription();
	if (!slvTaskDesc.isParallel()) {
		chores = new PostProcessingChores(primaryInternal + userDir, primaryExternal + userDir);
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

	this.messageProducer_sim = vcMessagingService_sim.createProducerSession();
	this.messageProducer_int = vcMessagingService_int.createProducerSession();

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
				HtcJobID pbsId = submit2PBS(simTask, clonedHtcProxy, rd);
				if (lg.isInfoEnabled()) {
					lg.info("onQueueMessage() sending 'accepted' message for job: simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName());
				}
				synchronized (messageProducer_sim) {
					WorkerEventMessage.sendAccepted(messageProducer_sim, HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), pbsId);
				}
				if (lg.isInfoEnabled()) {
					lg.info("onQueueMessage() sent 'accepted' message for job: simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName());
				}
			} catch (Exception e) {
				lg.error(e.getMessage(), e);
				if (simTask!=null){
					try {
						lg.error("failed to process simTask request: "+e.getMessage()+" for simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName(), e);
						synchronized (messageProducer_sim) {
							WorkerEventMessage.sendFailed(messageProducer_sim,  HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), SimulationMessage.jobFailed(e.getMessage()));
						}
						lg.error("sent 'failed' message for simulation key="+simTask.getSimKey()+", job="+simTask.getSimulationJobID()+", task="+simTask.getTaskID()+" for user "+simTask.getUserName(), e);
					} catch (VCMessagingException e1) {
						lg.error(e1.getMessage(),e);
					}
				}else {
					lg.error("failed to process simTask request: "+e.getMessage(), e);
				}
			}
		}
	};

	int numHtcworkerThreads = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.htcworkerThreadsProperty, "5"));
	this.pooledQueueConsumer_int = new VCPooledQueueConsumer(queueListener, numHtcworkerThreads, messageProducer_int);
	this.pooledQueueConsumer_int.initThreadPool();
	VCellQueue queue = VCellQueue.SimJobQueue;
	VCMessageSelector selector = vcMessagingService_int.createSelector(getJobSelector());
	String threadName = "SimJob Queue Consumer";
	queueConsumer = new VCQueueConsumer(queue, pooledQueueConsumer_int, selector, threadName, MessageConstants.PREFETCH_LIMIT_SIM_JOB_HTC);
	vcMessagingService_int.addMessageConsumer(queueConsumer);
}

private HtcJobID submit2PBS(SimulationTask simTask, HtcProxy clonedHtcProxy, PostProcessingChores chores) throws XmlParseException, IOException, SolverException, ExecutableException {

	HtcJobID jobid = null;
	File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));
	File htcLogDirInternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirInternal));
    String jobname = HtcProxy.createHtcSimJobName(new HtcProxy.SimTaskInfo(simTask.getSimKey(), simTask.getSimulationJob().getJobIndex(), simTask.getTaskID()));   //"S_" + simTask.getSimKey() + "_" + simTask.getSimulationJob().getJobIndex()+ "_" + simTask.getTaskID();
	File subFileExternal = new File(htcLogDirExternal, jobname + clonedHtcProxy.getSubmissionFileExtension());
	File subFileInternal = new File(htcLogDirInternal, jobname + clonedHtcProxy.getSubmissionFileExtension());

	File parallelDirInternal = new File(chores.runDirectoryInternal);
	File parallelDirExternal = new File(chores.runDirectoryExternal);
	File primaryUserDirInternal = new File(chores.finalDataDirectoryInternal);
	File primaryUserDirExternal = new File(chores.finalDataDirectoryExternal);
	boolean bNonSingularity =
		simTask.getSimulation().getSolverTaskDescription().getSolverDescription() == SolverDescription.HybridEuler ||
		simTask.getSimulation().getSolverTaskDescription().getSolverDescription() == SolverDescription.HybridMilstein ||
		simTask.getSimulation().getSolverTaskDescription().getSolverDescription() == SolverDescription.HybridMilAdaptive;
	Solver realSolver = (AbstractSolver)SolverFactory.createSolver((bNonSingularity?primaryUserDirExternal:primaryUserDirInternal),parallelDirInternal, simTask, true);
	realSolver.setUnixMode();

	String simTaskXmlText = XmlHelper.simTaskToXML(simTask);
	String simTaskFilePathInternal = ResourceUtil.forceUnixPath(new File(primaryUserDirInternal ,simTask.getSimulationJobID()+"_"+simTask.getTaskID()+".simtask.xml").toString());
	String simTaskFilePathExternal = ResourceUtil.forceUnixPath(new File(primaryUserDirExternal ,simTask.getSimulationJobID()+"_"+simTask.getTaskID()+".simtask.xml").toString());

	if (!primaryUserDirInternal.exists()){
		FileUtils.forceMkdir(primaryUserDirInternal);
		//
		// directory create from container (possibly) as root, make this user directory accessible from user "vcell" 
		//
		primaryUserDirInternal.setWritable(true,false);
		primaryUserDirInternal.setExecutable(true,false);
		primaryUserDirInternal.setReadable(true,false);
	}		 
	XmlUtil.writeXMLStringToFile(simTaskXmlText, simTaskFilePathInternal, true);

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
			subFileExternal.getAbsolutePath());
	postprocessorCmd.setExitCodeToken(SOLVER_EXIT_CODE_REPLACE_STRING);
	commandContainer.add(postprocessorCmd);

	int ncpus = simTask.getSimulation().getSolverTaskDescription().getNumProcessors(); //CBN?

	Collection<PortableCommand> postProcessingCommands = new ArrayList<PortableCommand>();
	if (realSolver instanceof AbstractCompiledSolver) {
		AbstractCompiledSolver compiledSolver = (AbstractCompiledSolver)realSolver;

		List<String> args = new ArrayList<>( 4 );
		args.add( PropertyLoader.getRequiredProperty(PropertyLoader.simulationPreprocessor) );
		args.add( simTaskFilePathExternal );
		args.add( primaryUserDirExternal.getAbsolutePath() );
		if ( chores.isParallel()) {
			args.add(chores.runDirectoryExternal);
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
			String logName = chores.finalDataDirectoryInternal + '/' + SimulationData.createCanonicalSimLogFileName(simKey, jobId, false);
			CopySimFiles csf = new CopySimFiles(simTask.getSimulationJobID(), chores.runDirectoryInternal, chores.finalDataDirectoryInternal, logName);
			postProcessingCommands.add(csf);
		}
		if (chores.isVtkUser()) {
			VtkMeshGenerator vmg = new VtkMeshGenerator(simOwner, simKey, jobId);
			postProcessingCommands.add(vmg);
		}

	} else {
		ExecutableCommand ec = new ExecutableCommand(null, false,false,
				PropertyLoader.getRequiredProperty(PropertyLoader.javaSimulationExecutable),
				simTaskFilePathExternal,
				ResourceUtil.forceUnixPath(parallelDirExternal.getAbsolutePath())
		);
		commandContainer.add(ec);
	}

	jobid = clonedHtcProxy.submitJob(jobname, subFileInternal, subFileExternal, commandContainer, ncpus, simTask.getEstimatedMemorySizeMB(), postProcessingCommands, simTask);
	if (jobid == null) {
		throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
		}
	return jobid;
}

@Override
public void stopService(){
	this.pooledQueueConsumer_int.shutdownAndAwaitTermination();
	super.stopService();
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	OperatingSystemInfo.getInstance();

	if (args.length != 3 && args.length != 0) {
		System.out.println("Missing arguments: " + VCellServices.class.getName() + " [sshHost sshUser sshKeyFile] ");
		System.exit(1);
	}

	try {
		PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);

		CommandService commandService = null;
		if (args.length==3){
			String sshHost = args[0];
			String sshUser = args[1];
			File sshKeyFile = new File(args[2]);
			try {
				commandService = new CommandServiceSshNative(sshHost,sshUser,sshKeyFile);
				commandService.command(new String[] { "/usr/bin/env bash -c ls | head -5" });
				lg.trace("SSH Connection test passed with installed keyfile, running ls as user "+sshUser+" on "+sshHost);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					commandService = new CommandServiceSshNative(sshHost,sshUser,sshKeyFile,new File("/root"));
					commandService.command(new String[] { "/usr/bin/env bash -c ls | head -5" });
					lg.trace("SSH Connection test passed after installing keyfile, running ls as user "+sshUser+" on "+sshHost);
				} catch (Exception e2) {
					e.printStackTrace();
					throw new RuntimeException("failed to establish an ssh command connection to "+sshHost+" as user '"+sshUser+"' using key '"+sshKeyFile+"'",e);
				}
			}
			AbstractSolver.bMakeUserDirs = false; // can't make user directories, they are remote.
		}else{
			commandService = new CommandServiceLocal();
		}
		BatchSystemType batchSystemType = BatchSystemType.SLURM;
		HtcProxy htcProxy = null;
		switch(batchSystemType){
			case SLURM:{
				htcProxy = new SlurmProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
				break;
			}
			default: {
				throw new RuntimeException("unrecognized batch scheduling option :"+batchSystemType);
			}
		}

		int serviceOrdinal = 0;
		VCMongoMessage.serviceStartup(ServiceName.pbsWorker, new Integer(serviceOrdinal), args);

//		//
//		// JMX registration
//		//
//		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//		mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));

		ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),
				ServiceType.PBSCOMPUTE, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);

		VCMessagingService vcMessagingService_int = new VCMessagingServiceActiveMQ();
		String jmshost_int = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
		int jmsport_int = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
		vcMessagingService_int.setConfiguration(new ServerMessagingDelegate(), jmshost_int, jmsport_int);
		
		VCMessagingService vcMessagingService_sim = new VCMessagingServiceActiveMQ();
		String jmshost_sim = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostInternal);
		int jmsport_sim = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortInternal));
		vcMessagingService_sim.setConfiguration(new ServerMessagingDelegate(), jmshost_sim, jmsport_sim);

		HtcSimulationWorker htcSimulationWorker = new HtcSimulationWorker(htcProxy, vcMessagingService_int, vcMessagingService_sim, serviceInstanceStatus, false);

		htcSimulationWorker.init();
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}

private static final String REQUIRED_SERVICE_PROPERTIES[] = {
		PropertyLoader.vcellSoftwareVersion,
		PropertyLoader.primarySimDataDirInternalProperty,
		PropertyLoader.primarySimDataDirExternalProperty,
		PropertyLoader.nativeSolverDir_External,
		PropertyLoader.vcellServerIDProperty,
		PropertyLoader.installationRoot,
		PropertyLoader.mongodbHostInternal,
		PropertyLoader.mongodbPortInternal,
		PropertyLoader.mongodbHostExternal,
		PropertyLoader.mongodbPortExternal,
		PropertyLoader.mongodbDatabase,
		PropertyLoader.jmsIntHostInternal,
		PropertyLoader.jmsIntPortInternal,
		PropertyLoader.jmsSimHostInternal,
		PropertyLoader.jmsSimPortInternal,
		PropertyLoader.jmsSimHostExternal,
		PropertyLoader.jmsSimPortExternal,
		PropertyLoader.jmsSimRestPortExternal,
		PropertyLoader.jmsUser,
		PropertyLoader.jmsPasswordFile,
		PropertyLoader.htcUser,
		PropertyLoader.htcLogDirExternal,
		PropertyLoader.htcLogDirInternal,
		PropertyLoader.slurm_tmpdir,
		PropertyLoader.jmsBlobMessageUseMongo,
		PropertyLoader.simulationPostprocessor,
		PropertyLoader.simulationPreprocessor,
		PropertyLoader.slurm_partition,
		PropertyLoader.vcellbatch_singularity_image,
		PropertyLoader.vcellbatch_docker_name,
		PropertyLoader.slurm_local_singularity_dir,
		PropertyLoader.slurm_central_singularity_dir
	};


}
