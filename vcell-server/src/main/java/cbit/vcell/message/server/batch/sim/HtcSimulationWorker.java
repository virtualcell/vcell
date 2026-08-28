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

import cbit.util.xml.XmlUtil;
import cbit.vcell.message.*;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.batch.opt.OptimizationBatchServer;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.slurm.SlurmProxy;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.EnvironmentConfigProvider;
import org.vcell.util.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VtkMeshGenerator;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.AbstractSolver;
import cbit.vcell.solvers.ExecutableCommand;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.dependency.server.VCellServerModule;
import org.vcell.solver.langevin.LangevinSolver;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class HtcSimulationWorker implements HtcProxy.HtcProxyFactory  {

	public static final Logger lg = LogManager.getLogger(HtcSimulationWorker.class);

	private VCMessagingService vcMessagingService_int = null;
	private VCMessagingService vcMessagingService_sim = null;

	private HtcProxy htcProxy = null;

	private OptimizationBatchServer optimizationBatchServer = null;

	private VCQueueConsumer queueConsumer = null;
	private VCMessageSession messageProducer_sim = null;
	private VCMessageSession messageProducer_int = null;
	private VCPooledQueueConsumer pooledQueueConsumer_int = null;


public HtcSimulationWorker() {
	this.htcProxy = SlurmProxy.createRemoteProxy();

	this.vcMessagingService_int = new VCMessagingServiceActiveMQ();
	String jmshost_int = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
	int jmsport_int = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
	this.vcMessagingService_int.setConfiguration(new ServerMessagingDelegate(), jmshost_int, jmsport_int);

	this.vcMessagingService_sim = new VCMessagingServiceActiveMQ();
	String jmshost_sim = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostInternal);
	int jmsport_sim = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortInternal));
	this.vcMessagingService_sim.setConfiguration(new ServerMessagingDelegate(), jmshost_sim, jmsport_sim);

	this.optimizationBatchServer = new OptimizationBatchServer(this);
}

	@Override
	public HtcProxy getHtcProxy() {
		return htcProxy;
	}

	public final String getJobSelector() {
	String jobSelector = "(" + VCMessagingConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "')";

	return jobSelector;
}

public void init() {
	initQueueConsumer();

	// Start JMS queue listener for optimization requests from vcell-rest (via Artemis broker)
	String artemisHost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsArtemisHostInternal);
	int artemisPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsArtemisPortInternal));
	optimizationBatchServer.initOptimizationQueue(artemisHost, artemisPort);
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
//	private boolean bStochMultiTrial;


	PostProcessingChores(String runDirectoryInternal, String runDirectoryExternal) {
		this(runDirectoryInternal,runDirectoryExternal,runDirectoryInternal,runDirectoryExternal);
	}


	PostProcessingChores(String runDirectoryInternal, String runDirectoryExternal, String finalDataDirectoryInternal, String finalDataDirectoryExternal) {
		this.runDirectoryInternal = runDirectoryInternal;
		this.runDirectoryExternal = runDirectoryExternal;
		this.finalDataDirectoryInternal = finalDataDirectoryInternal;
		this.finalDataDirectoryExternal = finalDataDirectoryExternal;
		isVtk = false;
//		bStochMultiTrial = false;
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

//	public void setStochMultiTrial(boolean bStochMultiTrial) {
//		this.bStochMultiTrial = bStochMultiTrial;
//	}
//	public boolean isStochMultiTrial() {
//		return bStochMultiTrial;
//	}
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
//	chores.setStochMultiTrial(HtcProxy.isStochMultiTrial(simTask));
	if (lg.isDebugEnabled( )) {
		lg.debug("Simulation " + simTask.getSimulation().getDescription() + " task " + simTask.getTaskID()
				+ " with " + slvTaskDesc.getNumProcessors() + " processors using " + chores);
	}
	return chores;
}



//------------------------------Job Monitor Section BEGIN
//------------------------------Job Monitor Section END




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
					WorkerEventMessage.sendStarting(messageProducer_sim, HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), SimulationMessage.MESSAGE_WORKEREVENT_STARTING);
					WorkerEventMessage.sendProgress(messageProducer_sim, HtcSimulationWorker.class.getName(), simTask, ManageUtils.getHostName(), 0, 0, SimulationMessage.MESSAGE_JOB_RUNNING_UNKNOWN);
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

	boolean is_langevin_batch = (realSolver instanceof LangevinSolver && simTask.getSimulation().getSolverTaskDescription().getNumTrials() > 1);
	if (realSolver instanceof AbstractCompiledSolver && !is_langevin_batch) {
		AbstractCompiledSolver compiledSolver = (AbstractCompiledSolver) realSolver;

		List<String> args = new ArrayList<>(4);
		args.add(PropertyLoader.getRequiredProperty(PropertyLoader.simulationPreprocessor));
		args.add(simTaskFilePathExternal);
		args.add(primaryUserDirExternal.getAbsolutePath());
		if (chores.isParallel()) {
			args.add(chores.runDirectoryExternal);
		}
		// compiled solver ...used to be only single executable, now we pass 2 commands to PBSUtils.submitJob that invokes SolverPreprocessor.main() and then the native executable
		//the pre-processor command itself is neither messaging nor parallel; it's independent of the subsequent solver call
		ExecutableCommand preprocessorCmd = new ExecutableCommand(null, false, false, args);
		commandContainer.add(preprocessorCmd);

		for (ExecutableCommand ec : compiledSolver.getCommands()) {
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
//		if(chores.isStochMultiTrial()) {
//			final String logName = chores.finalDataDirectoryInternal + '/' + SimulationData.createCanonicalSimLogFileName(simKey, jobId, false);
//			postProcessingCommands.add(new AvgStochMultiTrial(primaryUserDirInternal.getAbsolutePath(), XmlHelper.simTaskToXML(simTask)));
//		}
	} else if (realSolver instanceof LangevinSolver langevinSolver && is_langevin_batch){
		List<String> args = new ArrayList<>(4);
		args.add(PropertyLoader.getRequiredProperty(PropertyLoader.simulationPreprocessor));
		args.add(simTaskFilePathExternal);
		args.add(primaryUserDirExternal.getAbsolutePath());
		if (chores.isParallel()) {
			args.add(chores.runDirectoryExternal);
		}
		// compiled solver ...used to be only single executable, now we pass 2 commands to PBSUtils.submitJob that invokes SolverPreprocessor.main() and then the native executable
		//the pre-processor command itself is neither messaging nor parallel; it's independent of the subsequent solver call
		ExecutableCommand preprocessorCmd = new ExecutableCommand(null, false, false, args);
		commandContainer.add(preprocessorCmd);

		for (ExecutableCommand ec : langevinSolver.getCommands()) {
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
//		if(chores.isStochMultiTrial()) {
//			final String logName = chores.finalDataDirectoryInternal + '/' + SimulationData.createCanonicalSimLogFileName(simKey, jobId, false);
//			postProcessingCommands.add(new AvgStochMultiTrial(primaryUserDirInternal.getAbsolutePath(), XmlHelper.simTaskToXML(simTask)));
//		}

	} else {
		ExecutableCommand ec = new ExecutableCommand(null, false,false,
				PropertyLoader.getRequiredProperty(PropertyLoader.javaSimulationExecutable),
				simTaskFilePathExternal,
				ResourceUtil.forceUnixPath(parallelDirExternal.getAbsolutePath())
		);
		commandContainer.add(ec);
	}
	commandContainer.translatePaths(primaryUserDirInternal,primaryUserDirExternal);
	jobid = clonedHtcProxy.submitJob(jobname, subFileInternal, subFileExternal, commandContainer, ncpus, simTask.getEstimatedMemorySizeMB(), postProcessingCommands, simTask,primaryUserDirExternal);
	if (jobid == null) {
		throw new RuntimeException("Failed. (error message: submitting to job scheduler failed).");
		}
	return jobid;
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(String[] args) throws IOException {
	try {
		if (args.length != 0) {
			System.out.println("No arguments expected: " + HtcSimulationWorker.class.getName());
			System.exit(1);
		}

		OperatingSystemInfo.getInstance();
		// A standalone service takes its configuration from the container environment. The desktop
		// client, the CLI and the admin tools deliberately do not -- they run on machines whose
		// environment VCell does not control -- so this is installed per service rather than being
		// the default in PropertyLoader. vcell-rest installs CDIVCellConfigProvider for the same reason.
		PropertyLoader.setConfigProvider(new EnvironmentConfigProvider());
		PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);

		Injector injector = Guice.createInjector(new VCellServerModule());
		HtcSimulationWorker htcSimulationWorker = injector.getInstance(HtcSimulationWorker.class);

		htcSimulationWorker.init();
	} catch (Throwable e) {
		lg.error("HtcSimulationWorker failed to start: "+e.getMessage(), e);
	}
}

private static final String REQUIRED_SERVICE_PROPERTIES[] = {

		PropertyLoader.vcellSoftwareVersion,
		PropertyLoader.primarySimDataDirInternalProperty,
		PropertyLoader.primarySimDataDirExternalProperty,
		PropertyLoader.secondarySimDataDirExternalProperty,
		PropertyLoader.simDataDirArchiveExternal,
		PropertyLoader.simDataDirArchiveInternal,
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
		PropertyLoader.htc_vcellbatch_apptainer_image,
		PropertyLoader.htc_vcellbatch_solver_list,
		PropertyLoader.htc_vcellsolvers_apptainer_image,
		PropertyLoader.htc_vcellsolvers_solver_list,
		PropertyLoader.htc_vcellbatch_apptainer_image,
		PropertyLoader.htc_vcellbatch_solver_list,
		PropertyLoader.htc_vcellopt_apptainer_image,
		PropertyLoader.slurm_singularity_module_name,
		PropertyLoader.slurm_reservation,
		PropertyLoader.slurm_qos,
		PropertyLoader.slurm_partition_pu,
		PropertyLoader.slurm_reservation_pu,
		PropertyLoader.slurm_qos_pu,
		PropertyLoader.htcMinMemoryMB,
		PropertyLoader.htcMaxMemoryMB,
		PropertyLoader.htcPowerUserMemoryFloorMB,
		PropertyLoader.htcPowerUserMemoryMaxMB,
		PropertyLoader.slurm_langevin_timeoutPerTaskSeconds,
		PropertyLoader.slurm_langevin_batchMemoryLimitPerTaskMB,
		PropertyLoader.slurm_langevin_memoryBlockSizeMB,

		// Fetched with getRequiredProperty() but previously absent from this list, so
		// PropertyLoader logged "not marked required" on every fetch. Declaring them
		// silences that honestly and makes a missing value fail at startup, where it
		// can be read, rather than at first use.
		PropertyLoader.htc_singularity_imagedir,
		PropertyLoader.htc_vcellfvsolver_solver_list,
		PropertyLoader.slurm_singularity_pullfolder,
		PropertyLoader.slurm_singularity_cachedir,
		PropertyLoader.jmsArtemisPortInternal,
		PropertyLoader.jmsArtemisHostInternal,
		PropertyLoader.htcUserKeyFile,
		PropertyLoader.htcHosts
	};


}