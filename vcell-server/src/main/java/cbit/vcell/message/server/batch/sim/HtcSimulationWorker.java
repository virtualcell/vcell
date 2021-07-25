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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.optimization.CopasiServicePython;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRunStatus;
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
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSshNative;
import cbit.vcell.message.server.combined.VCellServices;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.slurm.SlurmJobStatus;
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
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VtkMeshGenerator;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.SundialsSolver;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.stoch.GibsonSolver;
import cbit.vcell.solver.stoch.StochFileWriter;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.AbstractSolver;
import cbit.vcell.solvers.ExecutableCommand;
import cbit.vcell.util.ColumnDescription;
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
	initOptimizationSocket();
}

private ServerSocket optimizationServersocket;
private static class OptServerJobInfo {
	private String optID;
	private HtcJobInfo htcJobInfo;
	public OptServerJobInfo(String optID, HtcJobInfo htcJobInfo) {
		super();
		this.optID = optID;
		this.htcJobInfo = htcJobInfo;
	}
	public String getOptID() {
		return optID;
	}
	public HtcJobInfo getHtcJobInfo() {
		return htcJobInfo;
	}
}
private void initOptimizationSocket() {
	Thread optThread = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				optimizationServersocket = new ServerSocket(8877);
				while(true) {
					Socket optSocket = optimizationServersocket.accept();
					
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							OptServerJobInfo optServerJobInfo = null;
							try (ObjectInputStream is = new ObjectInputStream(optSocket.getInputStream());
									ObjectOutputStream oos = new ObjectOutputStream(optSocket.getOutputStream());
									Socket myOptSocket = optSocket) {
								long jobStart = 0;
								while(true) {
									try {
										Object obj = is.readObject();
										Boolean bStop = (Boolean)is.readObject();
										if(bStop) {
											optServerStopJob(optServerJobInfo);
											oos.writeObject(new Boolean(true));
											return;
										}
										if(obj instanceof String && obj.toString().equals("checkIfDone")) {//check and remove connection and job status
											if(sendOptResults(optServerJobInfo.getOptID(),oos)) {
												return;
											}
											HtcJobStatus htcJobStatus = optServerGetJobStatus(optServerJobInfo.getHtcJobInfo());
											if(htcJobStatus == null) {//pending
												oos.writeObject(new Boolean(true));
											}else {
												if(htcJobStatus.isFailed()) {
													throw new Exception("slurm job "+optServerJobInfo.getHtcJobInfo().getHtcJobID()+" failed");
												}else if(htcJobStatus.isComplete()) {
													if(!sendOptResults(optServerJobInfo.optID,oos)) {
														throw new Exception("job done but results missing");
													}else {
														return;
													}
												}else {//running
													oos.writeObject(OptRunStatus.Running.name()+":");
												}
											}
										}else if(obj instanceof OptProblem) {//Start opt job
											OptProblem optProblem = (OptProblem) obj;
											optServerJobInfo = submitOptProblem(optProblem);
											oos.writeObject(optServerJobInfo.getOptID());
											jobStart = System.currentTimeMillis();
										}else if(obj instanceof String) {//Get opt job status
											String optID = obj.toString();
											if(sendOptResults(optID,oos)) {
												return;
											}
											File f = generateOptInterresultsFilePath(optID);
											boolean bExist = hackFileExists(f);//make container read results status
											long lastModified = f.lastModified();
											if(lastModified == 0 && (System.currentTimeMillis()-jobStart) > 60000) {
												throw new Exception("results progress timed out");
											}else if(lastModified != 0 && (lastModified-jobStart) > 60000) {
												throw new Exception("results progress timed out");
											}else if(lastModified != 0) {
												jobStart = lastModified;
											}
											if(bExist/*f.exists()*/) {
												List<String> progressLines = Files.readAllLines(f.toPath());
												if(progressLines != null && progressLines.size()>0) {
													String optRunstatus = progressLines.get(progressLines.size()-1);
													if(optRunstatus.toLowerCase().trim().startsWith("except")) {
														throw new Exception("python script error "+optRunstatus);
													}
													optRunstatus = OptRunStatus.Running.name()+":"+optRunstatus;
													oos.writeObject(optRunstatus);
												}else {
													oos.writeObject(OptRunStatus.Queued.name()+":0:0:0");
												}
												
											}else {
												oos.writeObject(OptRunStatus.Queued.name()+":0:0:0");
											}

										}else {
											throw new Exception("Unexpected paramOpt command "+obj.getClass().getName());
										}
									} catch (Exception e) {
										oos.writeObject(OptRunStatus.Failed.name()+":"+e.getMessage());
										throw e;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}finally {
								//cleanup
								if(optServerJobInfo != null && optServerJobInfo.getOptID() != null) {
									File optDir = generateOptimizeDirName(optServerJobInfo.getOptID());
									if(optDir.exists()) {
										generateOptProblemFilePath(optServerJobInfo.getOptID()).delete();
										generateOptOutputFilePath(optServerJobInfo.getOptID()).delete();
										generateOptInterresultsFilePath(optServerJobInfo.getOptID()).delete();
										optDir.delete();
									}
								}
							}
						}
					},"paramOptProblem");
					thread.setDaemon(true);
					thread.start();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
	optThread.setDaemon(true);
	optThread.start();
}
private boolean sendOptResults(String optID,ObjectOutputStream oos) throws IOException {
	File f = generateOptOutputFilePath(optID);
	if(f.exists()) {// opt job done
		byte[] bytes = FileUtils.readFileToByteArray(f);
		oos.writeObject(bytes);
		return true;
	}
	return false;
}
private File generateOptimizeDirName(String optID) {
	File primaryUserDirInternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty));
	File optProblemDir = new File(primaryUserDirInternal,generateOptFilePrefix(optID));
	return optProblemDir;
}
private File generateOptOutputFilePath(String optID) {
	String optOutputFileName = generateOptFilePrefix(optID)+"_optRun.bin";
	return new File(generateOptimizeDirName(optID), optOutputFileName);
}
private File generateOptProblemFilePath(String optID) {
	String optOutputFileName = generateOptFilePrefix(optID)+"_optProblem.bin";
	return new File(generateOptimizeDirName(optID), optOutputFileName);
}
private File generateOptInterresultsFilePath(String optID) {
	return new File(generateOptimizeDirName(optID), "interresults.txt");
}
private String generateOptFilePrefix(String optID) {
	return "ParamOptemize_"+optID;

}
private Random random = new Random(System.currentTimeMillis());
private OptServerJobInfo submitOptProblem(OptProblem optProblem) throws IOException, ExecutableException {
		HtcProxy htcProxyClone = htcProxy.cloneThreadsafe();
		File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));
		File htcLogDirInternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirInternal));
		int optID = random.nextInt(1000000);
		String optSubFileName = generateOptFilePrefix(optID+"")+".sub";
		File sub_file_external = new File(htcLogDirExternal, optSubFileName);
		File sub_file_internal = new File(htcLogDirInternal, optSubFileName);
		File optProblemFile = generateOptProblemFilePath(optID+"");
		File optOutputFile = generateOptOutputFilePath(optID+"");
		CopasiServicePython.writeOptProblem(optProblemFile, optProblem);//save param optimization problem to user dir
		//make sure all can read and write
		File optDir = generateOptimizeDirName(optID+"");
		optDir.setReadable(true,false);
		optDir.setWritable(true,false);
		
		String slurmOptJobName = generateOptFilePrefix(optID+"");
		HtcJobID htcJobID = htcProxyClone.submitOptimizationJob(slurmOptJobName, sub_file_internal, sub_file_external,optProblemFile,optOutputFile);
		return new OptServerJobInfo(optID+"", new HtcJobInfo(htcJobID, slurmOptJobName));
}
private void optServerStopJob(OptServerJobInfo optServerJobInfo) {
	try {
		HtcProxy htcProxyClone = htcProxy.cloneThreadsafe();
		htcProxyClone.killJobSafe(optServerJobInfo.getHtcJobInfo());
//		CommandOutput commandOutput = htcProxyClone.getCommandService().command(new String[] {"scancel",optServerJobInfo.htcJobID.getJobNumber()+""});
//		return commandOutput.getExitStatus()==0;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
private HtcJobStatus optServerGetJobStatus(HtcJobInfo htcJobInfo) {
	HtcProxy htcProxyClone = htcProxy.cloneThreadsafe();
	try {
		return htcProxyClone.getJobStatus(Arrays.asList(new HtcJobInfo[] {htcJobInfo})).get(htcJobInfo);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
}
private static boolean hackFileExists(File watchThisFile) {
	try {
		//Force container bind mount to update file status
		ProcessBuilder pb = new ProcessBuilder("sh","-c","ls "+watchThisFile.getAbsolutePath()+"*");
		pb.redirectErrorStream(true);
		Process p = pb.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//		StringBuffer sb = new StringBuffer();
		String line = null;
		while((line = br.readLine()) != null) {
			//sb.append(line+"\n");
			System.out.println("'"+line+"'");
			if(line.trim().startsWith("ls: ")) {
//				System.out.println("false");
				break;
			}else if(line.trim().equals(watchThisFile.getAbsolutePath())) {
//				System.out.println("true");
				return true;
			}
		}
		p.waitFor(10,TimeUnit.SECONDS);
		br.close();
//		System.out.println("false");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;
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
private static class MonitorJobInfo {
	private long slurmJobID;
	private boolean bDelete;
	private VCSimulationIdentifier vcsimID;
	private int jobIndex;
	private int taskID;
	public MonitorJobInfo(long slurmJobID,VCSimulationIdentifier vcsimID, int jobIndex, int taskID) {
		this(slurmJobID);
		this.bDelete = false;
		this.vcsimID = vcsimID;
		this.jobIndex = jobIndex;
		this.taskID = taskID;
	}
	public MonitorJobInfo(long slurmJobID) {
		this.slurmJobID = slurmJobID;
		this.bDelete = true;;
	}
	public static MonitorJobInfo fromString(String str) {
		StringTokenizer st = new StringTokenizer(str," \n\r");
		boolean bDelete = (st.nextToken().equals("+")?false:true);// + or -
		long slurmJobID = Long.parseLong(st.nextToken());// slurmJobID
		if(!bDelete) {
			KeyValue simKey = new KeyValue(st.nextToken());// simkey
			String username = st.nextToken();// username
			KeyValue userkey = new KeyValue(st.nextToken());// userkey
			int simJobIndex = Integer.parseInt(st.nextToken());// simjobidindex
			int simTaskID = Integer.parseInt(st.nextToken());// simtaskid
			return new MonitorJobInfo(slurmJobID,new VCSimulationIdentifier(simKey, new User(username, userkey)), simJobIndex, simTaskID);
		}else {
			return new MonitorJobInfo(slurmJobID);
		}
	}
	public String toString() {
		return ((bDelete?"-":"+")+" "+
		slurmJobID+" "+
			(!bDelete?
					vcsimID.getSimulationKey().toString()+" "+	//simkey
					"'"+vcsimID.getOwner().getName()+"' "+		//username
					vcsimID.getOwner().getID().toString()+" "+	//userkey
					jobIndex+" "+								//simjobidindex
					taskID										//simtaskid
			:""));																		
	}
	
}
private static String MONITOR_JOBS_FILE_NAME = "monitorJobsList";
private static File monitorJobsFile = new File(System.getProperty(PropertyLoader.primarySimDataDirInternalProperty), MONITOR_JOBS_FILE_NAME+"_"+System.getProperty(PropertyLoader.vcellServerIDProperty)+".txt");
private static Hashtable<String,MonitorJobInfo> getMonitorJobs(){
	Hashtable<String,MonitorJobInfo> result = new Hashtable<>();
	ArrayList<String> theseJobsAreDone = new ArrayList<>();
	try {
		if(monitorJobsFile.exists()) {
			List<String> monitorJobsList = Files.readAllLines(monitorJobsFile.toPath());
			for (Iterator<String> iterator = monitorJobsList.iterator(); iterator.hasNext();) {
				String slurmJobInfoStr = (String) iterator.next();
				if(slurmJobInfoStr != null && slurmJobInfoStr.trim().length() > 0) {
					MonitorJobInfo monitorJobInfo = MonitorJobInfo.fromString(slurmJobInfoStr);
					if(monitorJobInfo.bDelete) {
						theseJobsAreDone.add(monitorJobInfo.slurmJobID+"");
					}else {
						result.put(monitorJobInfo.slurmJobID+"", monitorJobInfo);
					}
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	for (String string : theseJobsAreDone) {
		result.remove(string);
	}
	System.out.println("----------removed "+theseJobsAreDone.size()+ " left with "+result.size());
	return result;
}
private void addMonitorJob(long slurmJobID,SimulationTask simTask,boolean bDelete) {
	try {
		MonitorJobInfo newJobInfo = null;
		if(bDelete) {
			monitorTheseJobs.remove(slurmJobID+"");
			newJobInfo = new MonitorJobInfo(slurmJobID);
		}else {
			newJobInfo = new MonitorJobInfo(slurmJobID, simTask.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simTask.getSimulationJob().getJobIndex(), simTask.getTaskID());
			monitorTheseJobs.put(slurmJobID+"", newJobInfo);
		}
		Files.write(monitorJobsFile.toPath(),(newJobInfo.toString()+"\n").getBytes(),StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.APPEND);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
private void removeMonitorJob(long slurmJobID) {
	addMonitorJob(slurmJobID,null,true);
}
private Hashtable<String,MonitorJobInfo> monitorTheseJobs;
private Thread monitorJobsThread = null;
public void startJobMonitor() {
	monitorTheseJobs = getMonitorJobs();
	try {
		System.out.println("----------Resetting slurm monitorJobsFile");
		//clean jobs file
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iterator = monitorTheseJobs.keySet().iterator(); iterator.hasNext();) {
			sb.append((iterator.next())+"\n");	
		}
		Files.write(monitorJobsFile.toPath(),sb.toString().getBytes(),StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);
	} catch (IOException e1) {
		e1.printStackTrace();
	}
	monitorJobsThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while(true) {
				try {
					int sleeptime=60000;
					Thread.sleep(sleeptime);
					StringBuffer slurmJobidSB = new StringBuffer();
					for(String jobid:monitorTheseJobs.keySet()) {
						slurmJobidSB.append((slurmJobidSB.length()>0?",":"")+jobid);
					}
					if(slurmJobidSB.length() == 0) {
						continue;
					}
					StringBuffer slurmJobInfoSB = new StringBuffer();
					try {
						HtcSimulationWorker.this.htcProxy.getCommandService();
						String[] tryStr = new String[] {"sacct","--format=jobid%25,jobname%40,state%30 -n -j "+slurmJobidSB.toString()+" | grep -v \".batch\""+" | grep -v \".extern\""};
						CommandOutput commandOutput = htcProxy.getCommandService().command(tryStr);
						slurmJobInfoSB.append(commandOutput.getStandardOutput());
//						System.out.println("-----sacct stdoutput:\n"+commandOutput.getStandardOutput());
//						System.out.println("-----sacct stderror:\n"+commandOutput.getStandardError());
					}catch(Exception e) {
						e.printStackTrace();
					}
//					Process p = null;
//					try{
//						String[] cmd = new String[] {"ssh","-i","/run/secrets/batchuserkeyfile","vcell@172.16.246.118","sacct --format=jobid,jobname%40,state -n -j "+slurmJobidSB.toString()+"| grep -v \".batch\""};
//						ProcessBuilder pb = new ProcessBuilder(Arrays.asList(cmd));
//						pb.redirectErrorStream(true);
//						p = pb.start();
//						int ioByte = -1;
//						while((ioByte = p.getInputStream().read()) != -1) {
//							sb.append((char)ioByte);
//						}
//						p.waitFor();
//					}catch(Exception e) {
//						e.printStackTrace();
//						continue;
//					}
					
//					System.out.println("-----");
//					System.out.println("-----"+sb.toString());
//					System.out.println("-----");
					
					StringTokenizer st = new StringTokenizer(slurmJobInfoSB.toString()," \n\r\t");
					while(st.hasMoreTokens()) {
						String slurmJobID = st.nextToken();
						String jobName = st.nextToken();
						String jobState = st.nextToken();
						if(jobState.equalsIgnoreCase("FAILED") ||
							jobState.startsWith("CANCELLED") ||
							jobState.equalsIgnoreCase("BOOT_FAIL") ||
							jobState.equalsIgnoreCase("DEADLINE") ||
							jobState.equalsIgnoreCase("NODE_FAIL") ||
							jobState.equalsIgnoreCase("OUT_OF_MEMORY") ||
							jobState.equalsIgnoreCase("PREEMPTED") ||
							jobState.equalsIgnoreCase("TIMEOUT")) {
							MonitorJobInfo failedMonitorJobInfo = monitorTheseJobs.get(slurmJobID);
							WorkerEventMessage.sendWorkerExitError(messageProducer_sim, HtcSimulationWorker.class.getName(), ManageUtils.getHostName(),
								failedMonitorJobInfo.vcsimID, failedMonitorJobInfo.jobIndex, failedMonitorJobInfo.taskID,
								SimulationMessage.jobFailed("Fail found by monitor, slrmJobID="+slurmJobID+" jobName="+jobName+" jobState="+jobState));
							removeMonitorJob(Long.parseLong(slurmJobID));
						}else if(jobState.equalsIgnoreCase("COMPLETED")) {
							MonitorJobInfo completedMonitorJobInfo = monitorTheseJobs.get(slurmJobID);
							WorkerEventMessage.sendAlternateCompleted(messageProducer_sim, HtcSimulationWorker.class.getName(), completedMonitorJobInfo.vcsimID,
								ManageUtils.getHostName(), completedMonitorJobInfo.jobIndex, completedMonitorJobInfo.taskID);
							removeMonitorJob(Long.parseLong(slurmJobID));
						}
					}
//					BF BOOT_FAIL
//					Job terminated due to launch failure, typically due to a hardware failure (e.g. unable to boot the node or block and the job can not be requeued).
//					CA CANCELLED
//					Job was explicitly cancelled by the user or system administrator. The job may or may not have been initiated.
//					CD COMPLETED
//					Job has terminated all processes on all nodes with an exit code of zero.
//					DL DEADLINE
//					Job terminated on deadline.
//					F FAILED
//					Job terminated with non-zero exit code or other failure condition.
//					NF NODE_FAIL
//					Job terminated due to failure of one or more allocated nodes.
//					OOM OUT_OF_MEMORY
//					Job experienced out of memory error.
//					PD PENDING
//					Job is awaiting resource allocation.
//					PR PREEMPTED
//					Job terminated due to preemption.
//					R RUNNING
//					Job currently has an allocation.
//					RQ REQUEUED
//					Job was requeued.
//					RS RESIZING
//					Job is about to change size.
//					RV REVOKED
//					Sibling was removed from cluster due to other cluster starting the job.
//					S SUSPENDED
//					Job has an allocation, but execution has been suspended and CPUs have been released for other jobs.
//					TO TIMEOUT
//					Job terminated upon reaching its time limit.
					
//					CommandOutput commandOutput = htcProxy.getCommandService().command(cmd);
//					System.out.println("-----sacct stdoutput:\n"+commandOutput.getStandardOutput());
//					System.out.println("-----sacct stderror:\n"+commandOutput.getStandardError());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});
	monitorJobsThread.setDaemon(true);
	monitorJobsThread.start();

}
//------------------------------Job Monitor Section END




private void initQueueConsumer() {

	startJobMonitor();
	
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
				addMonitorJob(pbsId.getJobNumber(), simTask, false);
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

	jobid = clonedHtcProxy.submitJob(jobname, subFileInternal, subFileExternal, commandContainer, ncpus, simTask.getEstimatedMemorySizeMB(), postProcessingCommands, simTask,primaryUserDirExternal);
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

		HtcProxy htcProxy = SlurmProxy.creatCommandService(args);

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

//private static HtcProxy creatCommandServiceSSH(java.lang.String[] args) throws IOException {
//	PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);
//
//	CommandService commandService = null;
//	if (args.length==3){
//		String sshHost = args[0];
//		String sshUser = args[1];
//		File sshKeyFile = new File(args[2]);
//		try {
//			commandService = new CommandServiceSshNative(sshHost,sshUser,sshKeyFile);
//			commandService.command(new String[] { "/usr/bin/env bash -c ls | head -5" });
//			lg.trace("SSH Connection test passed with installed keyfile, running ls as user "+sshUser+" on "+sshHost);
//		} catch (Exception e) {
//			e.printStackTrace();
//			try {
//				commandService = new CommandServiceSshNative(sshHost,sshUser,sshKeyFile,new File("/root"));
//				commandService.command(new String[] { "/usr/bin/env bash -c ls | head -5" });
//				lg.trace("SSH Connection test passed after installing keyfile, running ls as user "+sshUser+" on "+sshHost);
//			} catch (Exception e2) {
//				e.printStackTrace();
//				throw new RuntimeException("failed to establish an ssh command connection to "+sshHost+" as user '"+sshUser+"' using key '"+sshKeyFile+"'",e);
//			}
//		}
//		AbstractSolver.bMakeUserDirs = false; // can't make user directories, they are remote.
//	}else{
//		commandService = new CommandServiceLocal();
//	}
//	BatchSystemType batchSystemType = BatchSystemType.SLURM;
//	HtcProxy htcProxy = null;
//	switch(batchSystemType){
//		case SLURM:{
//			htcProxy = new SlurmProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
//			break;
//		}
//		default: {
//			throw new RuntimeException("unrecognized batch scheduling option :"+batchSystemType);
//		}
//	}
//	return htcProxy;
//}

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
		PropertyLoader.jmsRestPasswordFile,
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