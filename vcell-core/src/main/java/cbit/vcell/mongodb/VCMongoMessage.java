package cbit.vcell.mongodb;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import org.bson.Document;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationExecutionStatusPersistent;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationQueueEntryStatus;
import cbit.vcell.server.SimulationQueueEntryStatusPersistent;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifierOldStyle;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SimulationMessagePersistent;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solvers.AbstractSolver;

public final class VCMongoMessage {
	public static boolean enabled = true;
	public static boolean bTrace = false;
	private static boolean processingException = false;
	
	public static enum ServiceName {
		unknown,
		client,
		pbsWorker,
		localWorker,
		dispatch,
		bootstrap,
		simData,
		export,
		database,
		serverManager,
		solverPreprocessor,
		solverPostprocessor
	};
	
	private static ServiceName serviceName = ServiceName.unknown;
	private static Integer serviceOrdinal = null;
	private static String[] serviceArgs = new String[0];
	private static long serviceStartupTime = System.currentTimeMillis();
	
	public final static String MongoMessage_msgtype				= "msgtype";
	public static final String MongoMessage_type_testing 								= "testing";	
	public final static String MongoMessage_msgtype_simJobStatusUpdate					= "simJobStatusUpdate";
	public final static String MongoMessage_msgtype_simJobStatusUpdate_DBCacheMiss		= "simJobStatusUpdate_DBCacheMiss";
	public final static String MongoMessage_msgtype_simJobStatusInsert					= "simJobStatusInsert";
	public final static String MongoMessage_msgtype_simJobStatusInsert_AlreadyInserted	= "simJobStatusInsert_AlreadyInserted";
	public final static String MongoMessage_msgtype_solverStatus						= "solverStatus";
	public final static String MongoMessage_msgtype_workerEventMessage					= "workerEventMessage";
	public final static String MongoMessage_msgtype_rpcRequestSent						= "rpcRequestSent";
	public final static String MongoMessage_msgtype_rpcRequestReceived					= "rpcRequestReceived";
	public final static String MongoMessage_msgtype_clientSimStatusQueued				= "clientSimStatusQueued";
	public final static String MongoMessage_msgtype_clientSimStatusDelivered			= "clientSimStatusDelivered";
	public final static String MongoMessage_msgtype_serviceStartup						= "serviceStartup";
	public final static String MongoMessage_msgtype_exception							= "exception";
	public final static String MongoMessage_msgtype_clientConnect						= "clientConnect";
	public final static String MongoMessage_msgtype_clientTimeout						= "clientTimeout";
	public final static String MongoMessage_msgtype_jmsMessageReceived					= "jmsMessageReceived";
	public final static String MongoMessage_msgtype_jmsMessageSent						= "jmsMessageSent";
	public final static String MongoMessage_msgtype_commandServiceCall					= "pbsCall";
	public final static String MongoMessage_msgtype_infoMsg								= "infoMsg";
	public final static String MongoMessage_msgtype_traceMsg							= "traceMsg";
	public final static String MongoMessage_msgtype_obsoleteJob							= "obsoleteJob";
	public final static String MongoMessage_msgtype_zombieJob							= "zombieJob";
	public final static String MongoMessage_msgTime				= "msgTime";
	public final static String MongoMessage_msgTimeNice			= "msgTimeNice";
	
	//
	// SimulationJobStatus
	//
	public final static String MongoMessage_oldSimJobStatus		= "oldSimJobStatus";
	public final static String MongoMessage_cachedSimJobStatus	= "cachedSimJobStatus";
	public final static String MongoMessage_newSimJobStatus		= "newSimJobStatus";
	public final static String MongoMessage_updatedSimJobStatus	= "updatedSimJobStatus";
	
	public final static String MongoMessage_simId				= "simId";
	public final static String MongoMessage_taskId				= "taskId";
	public final static String MongoMessage_endTime				= "endDate";
	public final static String MongoMessage_endTimeNice			= "endDateNice";
	public final static String MongoMessage_hasData				= "hasData";
	public final static String MongoMessage_jobIndex			= "jobIndex";
	public final static String MongoMessage_schedulerStatus		= "schedulerStatus";
	public final static String MongoMessage_serverId			= "serverId";
	public final static String MongoMessage_computeHost			= "computeHost";
	public final static String MongoMessage_startTime			= "startDate";
	public final static String MongoMessage_startTimeNice		= "startDateNice";
	public final static String MongoMessage_simTime				= "simTime";
	public final static String MongoMessage_simTimeNice			= "simTimeNice";
	public final static String MongoMessage_simProgress			= "simProgress";
	public final static String MongoMessage_latestUpdateTime	= "updateDate";
	public final static String MongoMessage_latestUpdateTimeNice	= "updateDateNice";
	public final static String MongoMessage_simMessageState		= "simMessageState";
	public final static String MongoMessage_simMessageMsg		= "simMessageMsg";
	public final static String MongoMessage_simQueueEntryDate	= "simQueueEntryDate";
	public final static String MongoMessage_simQueueEntryDateNice	= "simQueueEntryDateNice";
	public final static String MongoMessage_simQueueEntryId		= "simQueueEntryId";
	public final static String MongoMessage_simQueueEntryPriority	= "simQueueEntryPriority";
	public final static String MongoMessage_simJobStatusTimeStamp	= "simJobStatusTimeStamp";
	public final static String MongoMessage_simJobStatusTimeStampNice	= "simJobStatusTimeStampNice";
	public final static String MongoMessage_solverEventType		= "solverEventType";
	public final static String MongoMessage_simComputeResource	= "simComputeResource";
	public final static String MongoMessage_simEstMemory		= "simEstMemory";
	public final static String MongoMessage_htcJobID			= "htcJobID";
	public final static String MongoMessage_htcWorkerMsg		= "htcWorkerMsg";
	public final static String MongoMessage_rpcRequestMethod	= "rpcMethod";
	public final static String MongoMessage_rpcRequestArgs		= "rpcArgs";
	public final static String MongoMessage_rpcRequestService	= "rpcService";
	public final static String MongoMessage_rpcRequestDelay		= "rpcDelay";
	public final static String MongoMessage_rpcServiceTime		= "rpcServiceTime";
	public final static String MongoMessage_userName			= "user";
	public final static String MongoMessage_host				= "host";
	public final static String MongoMessage_serviceName			= "serviceName";
	public final static String MongoMessage_serviceOrdinal		= "serviceOrdinal";
	public final static String MongoMessage_serviceStartTime	= "serviceStartTime";
	public final static String MongoMessage_serviceStartTimeNice	= "serviceStartTimeNice";
	public final static String MongoMessage_serviceArgs			= "serviceArgs";
	public final static String MongoMessage_exceptionMessage	= "exceptionMessage";
	public final static String MongoMessage_exceptionStack		= "exceptionStack";
	public final static String MongoMessage_clientInfo			= "clientInfo";
	public final static String MongoMessage_clientId			= "clientId";
	public final static String MongoMessage_javaVersion			= "javaVersion";
	public final static String MongoMessage_osArch				= "osArch";
	public final static String MongoMessage_osName				= "osName";
	public final static String MongoMessage_osVersion			= "osVersion";
	public final static String MongoMessage_vcSoftwareVersion	= "vcSoftwareVersion";
	public final static String MongoMessage_destination			= "destination";
	public final static String MongoMessage_jmsMessage			= "jmsMessage";
	public final static String MongoMessage_shellCmd			= "shellCmd";
	public final static String MongoMessage_cmdString			= "cmdString";
	public final static String MongoMessage_elapsedTimeMS		= "elapsedTimeMS";
	public final static String MongoMessage_stdout				= "stdout";
	public final static String MongoMessage_stderr				= "stderr";
	public final static String MongoMessage_exitCode			= "exitCode";
	public final static String MongoMessage_info				= "info";
	public final static String MongoMessage_simStateMachineDump = "simStateMachine";
	private Document doc = null;
	
	VCMongoMessage(Document doc){
		this.doc = doc;
	}
	
	public Document getDbObject(){
		return doc;
	}
	
	public String toString() {
		return doc.toString();
	}
	
	public static void serviceStartup(ServiceName serviceName, Integer serviceOrdinal, String[] args){
		VCMongoMessage.serviceName = serviceName;
		VCMongoMessage.serviceOrdinal = serviceOrdinal;
		VCMongoMessage.serviceArgs = args;
		VCMongoMessage.serviceStartupTime = System.currentTimeMillis();
		
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_serviceStartup);
			
			if (serviceOrdinal!=null){
				dbObject.put(MongoMessage_serviceOrdinal, serviceOrdinal.intValue());
			}

			if (args!=null){
				dbObject.put(MongoMessage_serviceArgs, Arrays.asList(args).toString());
			}
	
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}
	
	public static ServiceName getServiceName() {
		return VCMongoMessage.serviceName;
	}

	public static long getServiceStartupTime() {
		return VCMongoMessage.serviceStartupTime;
	}
	
	public static int getServiceOrdinal(){
		return VCMongoMessage.serviceOrdinal;
	}
	
	private static void addHeader(Document dbObject, String messageType) throws UnknownHostException{
		dbObject.put(MongoMessage_serverId, cbit.vcell.resource.PropertyLoader.getProperty(cbit.vcell.resource.PropertyLoader.vcellServerIDProperty,"unknown"));
		dbObject.put(MongoMessage_serviceName,serviceName.name());
		dbObject.put(MongoMessage_serviceOrdinal,serviceOrdinal);
		dbObject.put(MongoMessage_serviceStartTime,serviceStartupTime);
		dbObject.put(MongoMessage_serviceStartTimeNice,new Date(serviceStartupTime).toString());
		dbObject.put(MongoMessage_msgtype,messageType);
		
		long msgTime = System.currentTimeMillis();
		dbObject.put(MongoMessage_msgTime, msgTime);
		dbObject.put(MongoMessage_msgTimeNice, new Date(msgTime).toString());
		
		dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());
	}

	public static void sendInfo(String infoString) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_infoMsg);
			
			dbObject.put(MongoMessage_info, infoString);
							
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e); 
		}
	}

	public static void sendTrace(String infoString) {
		if (!enabled){
			return;
		}
		if (!bTrace){
			return;
		}
		try {
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_traceMsg);
			
			dbObject.put(MongoMessage_info, infoString);
							
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e); 
		}
	}

	public static void sendClientConnectionNew(UserLoginInfo userLoginInfo) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_clientConnect);
			
			addObject(dbObject, userLoginInfo);
							
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e); 
		}
	}

	public static void sendClientConnectionClosing(UserLoginInfo userLoginInfo) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_clientTimeout);
			
			addObject(dbObject, userLoginInfo);
							
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e); 
		}
	}

	public static void sendClientException(Throwable exception,	UserLoginInfo userLoginInfo) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_exception);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(bos);
			exception.printStackTrace(pw);
			pw.close();
			String stack = bos.toString();

			dbObject.put(MongoMessage_exceptionMessage,exception.getMessage());
			dbObject.put(MongoMessage_exceptionStack,stack);
			
			addObject(dbObject, userLoginInfo);
							
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}


	public static void sendException(Throwable exception) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_exception);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(bos);
			exception.printStackTrace(pw);
			pw.close();
			String stack = bos.toString();

			dbObject.put(MongoMessage_exceptionMessage,exception.getMessage());
			dbObject.put(MongoMessage_exceptionStack,stack);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e); 
		}
	}

	public static void sendException(String stack, String message) {
		if (!enabled){
			return;
		}
		if (processingException){
			System.out.println("recursively invoking sendException() for " +message +", " + stack + ", exception not logged to MongoDB.");
			return;
		}
		try {
			processingException = true;
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_exception);
			
			dbObject.put(MongoMessage_exceptionMessage,message);
			dbObject.put(MongoMessage_exceptionStack,stack);
	
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			e.printStackTrace(System.out);
			// cannot put exception to SessionLog ... infinite recursion.
			// VCMongoDbDriver.getInstance().getSessionLog().exception(e); 
			//
		} finally {
			processingException = false;
		}
	}

	public static void sendSimJobStatusInsertedAlready(SimulationJobStatus newSimulationJobStatus,SimulationJobStatus existingSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();
	
			addHeader(dbObject,MongoMessage_msgtype_simJobStatusInsert_AlreadyInserted);
			
			addObject(dbObject,newSimulationJobStatus);
			
			Document oldSimJobStatusObject = new Document();
			addObject(oldSimJobStatusObject, existingSimulationJobStatus);
			dbObject.put(MongoMessage_oldSimJobStatus, oldSimJobStatusObject);

			Document newSimJobStatusObject = new Document();
			addObject(newSimJobStatusObject, newSimulationJobStatus);
			dbObject.put(MongoMessage_newSimJobStatus, newSimJobStatusObject);
	
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	/**
	 * @param jobStatus non null
	 * @param failureMessage
	 * @param htcJobID
	 */
	public static void sendZombieJob(SimulationJobStatus jobStatus, String failureMessage, HtcJobID htcJobID) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_zombieJob);

			dbObject.put(MongoMessage_info,failureMessage);
			dbObject.put(MongoMessage_htcJobID,htcJobID.toDatabase());
			
			if (jobStatus!=null) {
				addObject(dbObject,jobStatus);
			}
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendObsoleteJob(SimulationJobStatus jobStatus, String failureMessage) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_obsoleteJob);

			dbObject.put(MongoMessage_info,failureMessage);
//			dbObject.put(MongoMessage_simStateMachineDump,simStateMachine.show());
			
			addObject(dbObject,jobStatus);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendSimJobStatusInsert(SimulationJobStatusPersistent newSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_simJobStatusInsert);

			addObject(dbObject,newSimulationJobStatus);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendSimJobStatusInsert(SimulationJobStatus newSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_simJobStatusInsert);

			addObject(dbObject,newSimulationJobStatus);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendSimJobStatusUpdate(SimulationJobStatus newSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_simJobStatusUpdate);

			addObject(dbObject,newSimulationJobStatus);
				
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}
		
	public static void sendSimJobStatusUpdate(SimulationJobStatusPersistent newSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_simJobStatusUpdate);

			addObject(dbObject,newSimulationJobStatus);
				
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}
		
	
	public static void sendSolverEvent(SolverEvent solverEvent) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_solverStatus);

			addObject(dbObject,solverEvent);
				
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendWorkerEvent(WorkerEventMessage workerEventMessage) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_workerEventMessage);

			addObject(dbObject,workerEventMessage.getWorkerEvent());
				
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendPBSWorkerMessage(SimulationTask simulationTask, HtcJobID htcJobID, String htcWorkerMsg) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_workerEventMessage);

			dbObject.put(MongoMessage_htcWorkerMsg, htcWorkerMsg);
			if (htcJobID!=null){
				dbObject.put(MongoMessage_htcJobID, htcJobID.toDatabase());
			}
	
			addObject(dbObject,simulationTask);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}
	
	public static void sendJmsMessageReceived(VCMessage vcMessage,	VCDestination vcDestination) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_jmsMessageReceived);

			dbObject.put(MongoMessage_destination, vcDestination.getName());

			addObject(dbObject, vcMessage);
			
			dbObject.put(MongoMessage_elapsedTimeMS, new Long(System.currentTimeMillis()-vcMessage.getTimestampMS()));
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendJmsMessageSent(VCMessage vcMessage,	VCDestination vcDestination) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_jmsMessageSent);

			dbObject.put(MongoMessage_destination, vcDestination.getName());

			addObject(dbObject, vcMessage);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendCommandServiceCall(CommandOutput commandOutput) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_commandServiceCall);

			addObject(dbObject, commandOutput);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendRpcRequestProcessed(VCRpcRequest rpcRequest, VCMessage vcMessage) {
		if (!enabled){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_rpcRequestReceived);
			
			addObject(dbObject,vcMessage);

			addObject(dbObject,rpcRequest);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendRpcRequestSent(VCRpcRequest rpcRequest, UserLoginInfo userLoginInfo, VCMessage vcMessage) {
		if (!enabled){
			return;
		}
		try {
			
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_rpcRequestSent);
			
			addObject(dbObject,vcMessage);

			addObject(dbObject,rpcRequest);
			
			if (userLoginInfo!=null){
				addObject(dbObject,userLoginInfo);
			}
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendClientMessageEventQueued(MessageEvent messageEvent) {
		if (!enabled || !(messageEvent instanceof SimulationJobStatusEvent)){
			return;
		}
		try {
			Document dbObject = new Document();

			addHeader(dbObject,MongoMessage_msgtype_clientSimStatusQueued);

			addObject(dbObject,(SimulationJobStatusEvent)messageEvent);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendClientMessageEventsDelivered(MessageEvent[] messageEvents, UserLoginInfo userLoginInfo) {
		if (!enabled || messageEvents==null){
			return;
		}
		for (MessageEvent messageEvent : messageEvents){
			if (messageEvent instanceof SimulationJobStatusEvent){
				try {
					Document dbObject = new Document();

					addHeader(dbObject,MongoMessage_msgtype_clientSimStatusDelivered);

					addObject(dbObject,(SimulationJobStatusEvent)messageEvent);
					
					addObject(dbObject, userLoginInfo);
					
					VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
				} catch (Exception e){
					VCMongoDbDriver.getInstance().getSessionLog().exception(e);
				}
			}
		}
	}

	private static void addObject(Document dbObject, UserLoginInfo userLoginInfo){
		if (userLoginInfo == null){
			return;
		}
		if (userLoginInfo.getUser()!=null){
			dbObject.put(MongoMessage_userName, userLoginInfo.getUser().getName());
		}
		dbObject.put(MongoMessage_clientId, userLoginInfo.getClientId());
		
		Document dbObjectClientInfo = new Document();

		if (userLoginInfo.getUser()!=null){
			dbObjectClientInfo.put(MongoMessage_userName,userLoginInfo.getUser().getName());
		}
		dbObjectClientInfo.put(MongoMessage_clientId, userLoginInfo.getClientId());
		if (userLoginInfo.getJava_version()!=null){
			dbObjectClientInfo.put(MongoMessage_javaVersion, userLoginInfo.getJava_version());
		}			
		if (userLoginInfo.getOs_arch()!=null){
			dbObjectClientInfo.put(MongoMessage_osArch, userLoginInfo.getOs_arch());
		}			
		if (userLoginInfo.getOs_name()!=null){
			dbObjectClientInfo.put(MongoMessage_osName, userLoginInfo.getOs_name());
		}			
		if (userLoginInfo.getOs_version()!=null){
			dbObjectClientInfo.put(MongoMessage_osVersion, userLoginInfo.getOs_version());
		}			
		if (userLoginInfo.getVCellSoftwareVersion()!=null){
			dbObjectClientInfo.put(MongoMessage_vcSoftwareVersion, userLoginInfo.getVCellSoftwareVersion());
		}
		
		dbObject.put(MongoMessage_clientInfo, dbObjectClientInfo);
	}
	
	private static void addObject(Document dbObject, CommandOutput commandOutput){
		if (commandOutput == null){
			return;
		}
		Document dbObjectShellCmd = new Document();
		
		if (commandOutput.getCommandStrings()!=null){
			dbObjectShellCmd.put(MongoMessage_cmdString, commandOutput.getCommand());
		}
		dbObjectShellCmd.put(MongoMessage_elapsedTimeMS, commandOutput.getElapsedTimeMS());
		if (commandOutput.getStandardOutput()!=null){
			dbObjectShellCmd.put(MongoMessage_stdout, commandOutput.getStandardOutput());
		}
		if (commandOutput.getStandardError()!=null){
			dbObjectShellCmd.put(MongoMessage_stderr, commandOutput.getStandardError());
		}
		if (commandOutput.getExitStatus()!=null){
			dbObjectShellCmd.put(MongoMessage_exitCode, commandOutput.getExitStatus());
		}
		
		dbObject.put(MongoMessage_shellCmd, dbObjectShellCmd);
	}
	
	private static void addObject(Document dbObject, SimulationJobStatusEvent simJobStatusEvent){
		addObject(dbObject, simJobStatusEvent.getSimulationMessage());
		addObject(dbObject, simJobStatusEvent.getJobStatus());
		if (simJobStatusEvent.getUser()!=null){
			dbObject.put(MongoMessage_userName,simJobStatusEvent.getUser().getName());
		}
		if (simJobStatusEvent.getProgress()!=null){
			dbObject.put(MongoMessage_simProgress, simJobStatusEvent.getProgress());
		}
		if (simJobStatusEvent.getTimepoint()!=null){
			dbObject.put(MongoMessage_simTime, simJobStatusEvent.getTimepoint());
		}			
	}
	
	private static void addObject(Document dbObject, VCRpcRequest rpcRequest){
		dbObject.put(MongoMessage_rpcRequestArgs,Arrays.asList(rpcRequest.getArguments()).toString());
		for (Object arg : rpcRequest.getArguments()){
			//
			// look for simulation IDs in rpcRequest arguments ... add to field.
			//
			if (arg instanceof VCSimulationIdentifier){
				dbObject.put(MongoMessage_simId, ((VCSimulationIdentifier)arg).getSimulationKey().toString());
			}else if (arg instanceof VCSimulationDataIdentifier){
				dbObject.put(MongoMessage_simId, ((VCSimulationDataIdentifier)arg).getSimulationKey().toString());
			}else if (arg instanceof VCSimulationDataIdentifierOldStyle){
				dbObject.put(MongoMessage_simId, ((VCSimulationDataIdentifierOldStyle)arg).getSimulationKey().toString());
			}
		}
		dbObject.put(MongoMessage_rpcRequestMethod,rpcRequest.getMethodName());
		dbObject.put(MongoMessage_rpcRequestService,rpcRequest.getRequestedServiceType().getName());
		dbObject.put(MongoMessage_userName,rpcRequest.getUserName());
		if (rpcRequest.getRequestTimestampMS()!=null && rpcRequest.getBeginProcessingTimestampMS()!=null){
			dbObject.put(MongoMessage_rpcRequestDelay,rpcRequest.getBeginProcessingTimestampMS().longValue() - rpcRequest.getRequestTimestampMS().longValue());
		}
		if (rpcRequest.getBeginProcessingTimestampMS()!=null && rpcRequest.getEndProcessingTimestampMS()!=null){
			dbObject.put(MongoMessage_rpcServiceTime,rpcRequest.getEndProcessingTimestampMS().longValue() - rpcRequest.getBeginProcessingTimestampMS().longValue());
		}
	}
		
	private static void addObject(Document dbObject, SimulationTask simulationTask){
		dbObject.put(MongoMessage_simId,simulationTask.getSimulationJob().getVCDataIdentifier().getSimulationKey().toString());
		dbObject.put(MongoMessage_jobIndex, simulationTask.getSimulationJob().getJobIndex());
		dbObject.put(MongoMessage_taskId, simulationTask.getTaskID());
		dbObject.put(MongoMessage_simComputeResource,simulationTask.getComputeResource());
		dbObject.put(MongoMessage_simEstMemory,simulationTask.getEstimatedMemorySizeMB());
	}
	

	private static void addObject(Document dbObject, VCMessage message){
		Object objectContent = message.getObjectContent();
		if (objectContent instanceof WorkerEvent){
			addObject(dbObject, (WorkerEvent)objectContent);
		}else if (objectContent instanceof SimulationJobStatus){
			addObject(dbObject, (SimulationJobStatus)objectContent);
		}
		dbObject.put(MongoMessage_jmsMessage, message.show());
		if (message.propertyExists(MessageConstants.SIMKEY_PROPERTY)){
			dbObject.put(MongoMessage_simId,String.valueOf(message.getLongProperty(MessageConstants.SIMKEY_PROPERTY)));
		}
		if (message.propertyExists(MessageConstants.JOBINDEX_PROPERTY)){
			dbObject.put(MongoMessage_jobIndex, message.getIntProperty(MessageConstants.JOBINDEX_PROPERTY));
		}
		if (message.propertyExists(MessageConstants.TASKID_PROPERTY)){
			dbObject.put(MongoMessage_taskId, message.getIntProperty(MessageConstants.TASKID_PROPERTY));
		}
		if (message.propertyExists(VCMessagingConstants.USERNAME_PROPERTY)){
			dbObject.put(MongoMessage_userName, message.getStringProperty(VCMessagingConstants.USERNAME_PROPERTY));
		}
		if (message.propertyExists(MessageConstants.SIMULATION_STATUS_PROGRESS_PROPERTY)){
			dbObject.put(MongoMessage_simProgress, message.getDoubleProperty(MessageConstants.SIMULATION_STATUS_PROGRESS_PROPERTY));
		}
	}
	
	private static void addObject(Document dbObject, WorkerEventMessage workerEventMessage){
		WorkerEvent workerEvent = workerEventMessage.getWorkerEvent();
		dbObject.put(MongoMessage_computeHost, workerEvent.getHostName());
		dbObject.put(MongoMessage_simId,workerEvent.getVCSimulationDataIdentifier().getSimulationKey().toString());
		dbObject.put(MongoMessage_jobIndex, workerEvent.getJobIndex());
		dbObject.put(MongoMessage_taskId, workerEvent.getTaskID());
		//workerEvent.getEventTypeID();
		//workerEvent.getMessageData();
		//workerEvent.getMessageSource();
		addObject(dbObject, workerEvent.getSimulationMessage());
		dbObject.put(MongoMessage_simProgress,workerEvent.getProgress());
		dbObject.put(MongoMessage_simTime,workerEvent.getTimePoint());
	}
	
	private static void addObject(Document dbObject, WorkerEvent workerEvent){
		dbObject.put(MongoMessage_computeHost, workerEvent.getHostName());
		dbObject.put(MongoMessage_simId,workerEvent.getVCSimulationDataIdentifier().getSimulationKey().toString());
		dbObject.put(MongoMessage_jobIndex, workerEvent.getJobIndex());
		dbObject.put(MongoMessage_taskId, workerEvent.getTaskID());
		if (workerEvent.getHtcJobID()!=null){
			dbObject.put(MongoMessage_htcJobID, workerEvent.getHtcJobID().toDatabase());
		}
		//workerEvent.getEventTypeID();
		//workerEvent.getMessageData();
		//workerEvent.getMessageSource();
		addObject(dbObject, workerEvent.getSimulationMessage());
		dbObject.put(MongoMessage_simProgress,workerEvent.getProgress());
		dbObject.put(MongoMessage_simTime,workerEvent.getTimePoint());
	}
	
	private static void addObject(Document dbObject, SolverEvent solverEvent){
		AbstractSolver solver = (AbstractSolver)solverEvent.getSource();
		dbObject.put(MongoMessage_simProgress,solverEvent.getProgress());
		dbObject.put(MongoMessage_simTime,solverEvent.getTimePoint());
		addObject(dbObject, solverEvent.getSimulationMessage());
		dbObject.put(MongoMessage_solverEventType, solverEvent.getType());
		SimulationJob simJob = solver.getSimulationJob();
		dbObject.put(MongoMessage_simId,simJob.getVCDataIdentifier().getSimulationKey().toString());
		dbObject.put(MongoMessage_jobIndex, solver.getJobIndex());
		dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
	}
	
	private static void addObject(Document dbObject, SimulationJobStatus newSimulationJobStatus){
		dbObject.put(MongoMessage_simId,newSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey().toString());
		dbObject.put(MongoMessage_taskId, newSimulationJobStatus.getTaskID());
		dbObject.put(MongoMessage_jobIndex, newSimulationJobStatus.getJobIndex());
		dbObject.put(MongoMessage_schedulerStatus, newSimulationJobStatus.getSchedulerStatus().getDescription());
		dbObject.put(MongoMessage_serverId, newSimulationJobStatus.getServerID().toString());
		if (newSimulationJobStatus.getTimeDateStamp()!=null){
			dbObject.put(MongoMessage_simJobStatusTimeStamp,newSimulationJobStatus.getTimeDateStamp().getTime());
			dbObject.put(MongoMessage_simJobStatusTimeStampNice,newSimulationJobStatus.getTimeDateStamp().toString());
		}

		addObject(dbObject,newSimulationJobStatus.getSimulationExecutionStatus());

		addObject(dbObject,newSimulationJobStatus.getSimulationMessage());

		addObject(dbObject,newSimulationJobStatus.getSimulationQueueEntryStatus());
	}
	
	private static void addObject(Document dbObject, SimulationJobStatusPersistent newSimulationJobStatus){
		dbObject.put(MongoMessage_simId,newSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey().toString());
		dbObject.put(MongoMessage_taskId, newSimulationJobStatus.getTaskID());
		dbObject.put(MongoMessage_jobIndex, newSimulationJobStatus.getJobIndex());
		dbObject.put(MongoMessage_schedulerStatus, newSimulationJobStatus.getSchedulerStatus().getDescription());
		dbObject.put(MongoMessage_serverId, newSimulationJobStatus.getServerID().toString());
		if (newSimulationJobStatus.getTimeDateStamp()!=null){
			dbObject.put(MongoMessage_simJobStatusTimeStamp,newSimulationJobStatus.getTimeDateStamp().getTime());
			dbObject.put(MongoMessage_simJobStatusTimeStampNice,newSimulationJobStatus.getTimeDateStamp().toString());
		}

		addObject(dbObject,newSimulationJobStatus.getSimulationExecutionStatus());

		addObject(dbObject,newSimulationJobStatus.getSimulationMessage());

		addObject(dbObject,newSimulationJobStatus.getSimulationQueueEntryStatus());
	}
	
	private static void addObject(Document dbObject, SimulationExecutionStatus simExeStatus){
		if (simExeStatus==null){
			return;
		}
		dbObject.put(MongoMessage_computeHost, simExeStatus.getComputeHost());
		dbObject.put(MongoMessage_hasData, simExeStatus.hasData());
		if (simExeStatus.getEndDate()!=null){
			dbObject.put(MongoMessage_endTime, simExeStatus.getEndDate().getTime());
			dbObject.put(MongoMessage_endTimeNice, simExeStatus.getEndDate().toString());
		}
		if (simExeStatus.getStartDate()!=null){
			dbObject.put(MongoMessage_startTime, simExeStatus.getStartDate().getTime());
			dbObject.put(MongoMessage_startTimeNice, simExeStatus.getStartDate().toString());
		}
		if (simExeStatus.getLatestUpdateDate()!=null){
			dbObject.put(MongoMessage_latestUpdateTime, simExeStatus.getLatestUpdateDate().getTime());
			dbObject.put(MongoMessage_latestUpdateTimeNice, simExeStatus.getLatestUpdateDate().toString());
		}
		if (simExeStatus.getHtcJobID()!=null){
			dbObject.put(MongoMessage_htcJobID, simExeStatus.getHtcJobID().toDatabase());
		}
	}
	
	private static void addObject(Document dbObject, SimulationExecutionStatusPersistent simExeStatus){
		if (simExeStatus==null){
			return;
		}
		dbObject.put(MongoMessage_computeHost, simExeStatus.getComputeHost());
		dbObject.put(MongoMessage_hasData, simExeStatus.hasData());
		if (simExeStatus.getEndDate()!=null){
			dbObject.put(MongoMessage_endTime, simExeStatus.getEndDate().getTime());
			dbObject.put(MongoMessage_endTimeNice, simExeStatus.getEndDate().toString());
		}
		if (simExeStatus.getStartDate()!=null){
			dbObject.put(MongoMessage_startTime, simExeStatus.getStartDate().getTime());
			dbObject.put(MongoMessage_startTimeNice, simExeStatus.getStartDate().toString());
		}
		if (simExeStatus.getLatestUpdateDate()!=null){
			dbObject.put(MongoMessage_latestUpdateTime, simExeStatus.getLatestUpdateDate().getTime());
			dbObject.put(MongoMessage_latestUpdateTimeNice, simExeStatus.getLatestUpdateDate().toString());
		}
		if (simExeStatus.getHtcJobID()!=null){
			dbObject.put(MongoMessage_htcJobID, simExeStatus.getHtcJobID().toDatabase());
		}
	}
	
	private static void addObject(Document dbObject, SimulationMessage simMessage){
		if (simMessage==null){
			return;
		}
		SimulationMessage.DetailedState detailedState = simMessage.getDetailedState();
		dbObject.put(MongoMessage_simMessageState,detailedState.name());
		dbObject.put(MongoMessage_simMessageMsg,simMessage.getDisplayMessage());
		if (simMessage.getHtcJobId()!=null){
			dbObject.put(MongoMessage_htcJobID,simMessage.getHtcJobId().toDatabase());
		}
	}

	private static void addObject(Document dbObject, SimulationMessagePersistent simMessage){
		if (simMessage==null){
			return;
		}
		SimulationMessagePersistent.DetailedState detailedState = simMessage.getDetailedState();
		dbObject.put(MongoMessage_simMessageState,detailedState.name());
		dbObject.put(MongoMessage_simMessageMsg,simMessage.getDisplayMessage());
		if (simMessage.getHtcJobId()!=null){
			dbObject.put(MongoMessage_htcJobID,simMessage.getHtcJobId().toDatabase());
		}
	}

	private static void addObject(Document dbObject, SimulationQueueEntryStatus simQueueEntryStatus){
		if (simQueueEntryStatus==null){
			return;
		}
		if (simQueueEntryStatus.getQueueDate()!=null){
			dbObject.put(MongoMessage_simQueueEntryDate,simQueueEntryStatus.getQueueDate().getTime());
			dbObject.put(MongoMessage_simQueueEntryDateNice,simQueueEntryStatus.getQueueDate().toString());
		}
		dbObject.put(MongoMessage_simQueueEntryId,simQueueEntryStatus.getQueueID().name());
		dbObject.put(MongoMessage_simQueueEntryPriority,simQueueEntryStatus.getQueuePriority());
	}

	private static void addObject(Document dbObject, SimulationQueueEntryStatusPersistent simQueueEntryStatus){
		if (simQueueEntryStatus==null){
			return;
		}
		if (simQueueEntryStatus.getQueueDate()!=null){
			dbObject.put(MongoMessage_simQueueEntryDate,simQueueEntryStatus.getQueueDate().getTime());
			dbObject.put(MongoMessage_simQueueEntryDateNice,simQueueEntryStatus.getQueueDate().toString());
		}
		dbObject.put(MongoMessage_simQueueEntryId,simQueueEntryStatus.getQueueID().name());
		dbObject.put(MongoMessage_simQueueEntryPriority,simQueueEntryStatus.getQueuePriority());
	}

	public static void flush() {
		VCMongoDbDriver.getInstance().flush();
	}
	
	/**
	 * @see VCMongoDbDriver#shutdown()
	 */
	public static void shutdown() {
		VCMongoDbDriver.getInstance().shutdown();
	}


}