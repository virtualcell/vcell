package cbit.vcell.mongodb;

import java.net.URL;

import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.messaging.WorkerEventMessage;
import cbit.vcell.messaging.db.SimulationExecutionStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.server.RpcRequest;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.SimulationMessage.DetailedState;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifierOldStyle;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.AbstractSolver;

import com.mongodb.BasicDBObject;

public final class VCMongoMessage {
	public static boolean enabled = true;
	
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
	public final static String MongoMessage_msgTime				= "msgTime";
	
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
	public final static String MongoMessage_hasData				= "hasData";
	public final static String MongoMessage_jobIndex			= "jobIndex";
	public final static String MongoMessage_schedulerStatus		= "schedulerStatus";
	public final static String MongoMessage_serverId			= "serverId";
	public final static String MongoMessage_computeHost			= "computeHost";
	public final static String MongoMessage_startTime			= "startDate";
	public final static String MongoMessage_simTime				= "simTime";
	public final static String MongoMessage_simProgress			= "simProgress";
	public final static String MongoMessage_latestUpdateTime	= "updateDate";
	public final static String MongoMessage_simMessageState		= "simMessageState";
	public final static String MongoMessage_simMessageMsg		= "simMessageMsg";
	public final static String MongoMessage_simQueueEntryDate	= "simQueueEntryDate";
	public final static String MongoMessage_simQueueEntryId		= "simQueueEntryId";
	public final static String MongoMessage_simQueueEntryPriority	= "simQueueEntryPriority";
	public final static String MongoMessage_simJobStatusTimeStamp	= "simJobStatusTimeStamp";
	public final static String MongoMessage_solverEventType		= "solverEventType";
	public final static String MongoMessage_simComputeResource	= "simComputeResource";
	public final static String MongoMessage_simEstMemory		= "simEstMemory";
	public final static String MongoMessage_pbsJobID			= "pbsJobID";
	public final static String MongoMessage_pbsWorkerMsg		= "pbsWorkerMsg";
	public final static String MongoMessage_rpcRequestMethod	= "rpcMethod";
	public final static String MongoMessage_rpcRequestArgs		= "rpcArgs";
	public final static String MongoMessage_rpcRequestService	= "rpcService";
	public final static String MongoMessage_userName			= "user";
	public final static String MongoMessage_host				= "host";
	

	private BasicDBObject doc = null;
	
	VCMongoMessage(BasicDBObject doc){
		this.doc = doc;
	}
	
	public BasicDBObject getDbObject(){
		return doc;
	}
	
	public String toString() {
		return doc.toString();
	}

	public static void sendSimJobStatusInsertedAlready(SimulationJobStatus newSimulationJobStatus,SimulationJobStatus existingSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_simJobStatusInsert_AlreadyInserted);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());
	
			addObject(dbObject,newSimulationJobStatus);
			
			BasicDBObject oldSimJobStatusObject = new BasicDBObject();
			addObject(oldSimJobStatusObject, existingSimulationJobStatus);
			dbObject.put(MongoMessage_oldSimJobStatus, oldSimJobStatusObject);
	
			BasicDBObject newSimJobStatusObject = new BasicDBObject();
			addObject(newSimJobStatusObject, newSimulationJobStatus);
			dbObject.put(MongoMessage_newSimJobStatus, newSimJobStatusObject);
	
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendSimJobStatusInsert(SimulationJobStatus newSimulationJobStatus,SimulationJobStatus updatedSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_simJobStatusInsert);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());
	
			addObject(dbObject,updatedSimulationJobStatus);
			
//			BasicDBObject newSimJobStatusObject = new BasicDBObject();
//			addObject(newSimJobStatusObject, newSimulationJobStatus);
//			dbObject.put(MongoMessage_newSimJobStatus, newSimJobStatusObject);
//	
//			BasicDBObject updatedSimJobStatusObject = new BasicDBObject();
//			addObject(updatedSimJobStatusObject, updatedSimulationJobStatus);
//			dbObject.put(MongoMessage_updatedSimJobStatus, updatedSimJobStatusObject);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendSimJobStatusUpdateCacheMiss(SimulationJobStatus cachedSimulationJobStatus, SimulationJobStatus oldSimulationJobStatus, SimulationJobStatus newSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_simJobStatusUpdate_DBCacheMiss);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());
	
			addObject(dbObject,newSimulationJobStatus);
			
			BasicDBObject cachedSimJobStatusObject = new BasicDBObject();
			addObject(cachedSimJobStatusObject, cachedSimulationJobStatus);
			dbObject.put(MongoMessage_cachedSimJobStatus, cachedSimJobStatusObject);
			
			BasicDBObject oldSimJobStatusObject = new BasicDBObject();
			addObject(oldSimJobStatusObject, oldSimulationJobStatus);
			dbObject.put(MongoMessage_oldSimJobStatus, oldSimJobStatusObject);
	
			BasicDBObject newSimJobStatusObject = new BasicDBObject();
			addObject(newSimJobStatusObject, newSimulationJobStatus);
			dbObject.put(MongoMessage_newSimJobStatus, newSimJobStatusObject);
	
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}
	
	public static void sendSimJobStatusUpdate(SimulationJobStatus oldSimulationJobStatus, SimulationJobStatus newSimulationJobStatus, SimulationJobStatus updatedSimulationJobStatus) {
		if (!enabled){
			return;
		}
		try {
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_simJobStatusUpdate);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());
	
			addObject(dbObject,updatedSimulationJobStatus);
	
//			BasicDBObject oldSimJobStatusObject = new BasicDBObject();
//			addObject(oldSimJobStatusObject, oldSimulationJobStatus);
//			dbObject.put(MongoMessage_oldSimJobStatus, oldSimJobStatusObject);
//	
//			BasicDBObject newSimJobStatusObject = new BasicDBObject();
//			addObject(newSimJobStatusObject, newSimulationJobStatus);
//			dbObject.put(MongoMessage_newSimJobStatus, newSimJobStatusObject);
//	
//			BasicDBObject updatedSimJobStatusObject = new BasicDBObject();
//			addObject(updatedSimJobStatusObject, updatedSimulationJobStatus);
//			dbObject.put(MongoMessage_updatedSimJobStatus, updatedSimJobStatusObject);
			
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
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_solverStatus);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());
	
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
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_workerEventMessage);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());
	
			addObject(dbObject,workerEventMessage);
				
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendPBSWorkerMessage(SimulationTask simulationTask, String pbsJobID, String pbsWorkerMsg) {
		if (!enabled){
			return;
		}
		try {
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_workerEventMessage);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_pbsWorkerMsg, pbsWorkerMsg);
			dbObject.put(MongoMessage_pbsJobID, pbsJobID);
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());
	
			addObject(dbObject,simulationTask);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}


	public static void sendRpcRequestReceived(RpcRequest rpcRequest) {
		if (!enabled){
			return;
		}
		try {
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_rpcRequestReceived);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());

			addObject(dbObject,rpcRequest);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	public static void sendRpcRequestSent(RpcRequest rpcRequest) {
		if (!enabled){
			return;
		}
		try {
			
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put(MongoMessage_serverId, VCellServerID.getSystemServerID().toString());
			dbObject.put(MongoMessage_msgtype,MongoMessage_msgtype_rpcRequestSent);
			dbObject.put(MongoMessage_msgTime, System.currentTimeMillis());
			dbObject.put(MongoMessage_host,java.net.InetAddress.getLocalHost().getHostName());

			addObject(dbObject,rpcRequest);
			
			VCMongoDbDriver.getInstance().addMessage(new VCMongoMessage(dbObject));
		} catch (Exception e){
			VCMongoDbDriver.getInstance().getSessionLog().exception(e);
		}
	}

	private static void addObject(BasicDBObject dbObject, RpcRequest rpcRequest){
		dbObject.put(MongoMessage_rpcRequestArgs,rpcRequest.getArguments().toString());
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
	}
	
	private static void addObject(BasicDBObject dbObject, SimulationTask simulationTask){
		dbObject.put(MongoMessage_simId,simulationTask.getSimulationJob().getVCDataIdentifier().getSimulationKey().toString());
		dbObject.put(MongoMessage_jobIndex, simulationTask.getSimulationJob().getJobIndex());
		dbObject.put(MongoMessage_taskId, simulationTask.getTaskID());
		dbObject.put(MongoMessage_simComputeResource,simulationTask.getComputeResource());
		dbObject.put(MongoMessage_simEstMemory,simulationTask.getEstimatedMemorySizeMB());
	}
	

	private static void addObject(BasicDBObject dbObject, WorkerEventMessage workerEventMessage){
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
	
	private static void addObject(BasicDBObject dbObject, SolverEvent solverEvent){
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
	
	private static void addObject(BasicDBObject dbObject, SimulationJobStatus newSimulationJobStatus){
		dbObject.put(MongoMessage_simId,newSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey().toString());
		dbObject.put(MongoMessage_taskId, newSimulationJobStatus.getTaskID());
		dbObject.put(MongoMessage_jobIndex, newSimulationJobStatus.getJobIndex());
		dbObject.put(MongoMessage_schedulerStatus, newSimulationJobStatus.getSchedulerStatus());
		dbObject.put(MongoMessage_serverId, newSimulationJobStatus.getServerID().toString());
		dbObject.put(MongoMessage_simJobStatusTimeStamp,newSimulationJobStatus.getTimeDateStamp().getTime());

		addObject(dbObject,newSimulationJobStatus.getSimulationExecutionStatus());

		addObject(dbObject,newSimulationJobStatus.getSimulationMessage());

		addObject(dbObject,newSimulationJobStatus.getSimulationQueueEntryStatus());
	}
	
	private static void addObject(BasicDBObject dbObject, SimulationExecutionStatus simExeStatus){
		if (simExeStatus==null){
			return;
		}
		dbObject.put(MongoMessage_computeHost, simExeStatus.getComputeHost());
		dbObject.put(MongoMessage_hasData, simExeStatus.hasData());
		if (simExeStatus.getEndDate()!=null){
			dbObject.put(MongoMessage_endTime, simExeStatus.getEndDate().getTime());
		}
		if (simExeStatus.getStartDate()!=null){
			dbObject.put(MongoMessage_startTime, simExeStatus.getStartDate().getTime());
		}
		if (simExeStatus.getLatestUpdateDate()!=null){
			dbObject.put(MongoMessage_latestUpdateTime, simExeStatus.getLatestUpdateDate().getTime());
		}
	}
	
	private static void addObject(BasicDBObject dbObject, SimulationMessage simMessage){
		if (simMessage==null){
			return;
		}
		DetailedState detailedState = simMessage.getDetailedState();
		dbObject.put(MongoMessage_simMessageState,detailedState.name());
		dbObject.put(MongoMessage_simMessageMsg,simMessage.getDisplayMessage());
	}

	private static void addObject(BasicDBObject dbObject, SimulationQueueEntryStatus simQueueEntryStatus){
		if (simQueueEntryStatus==null){
			return;
		}
		if (simQueueEntryStatus.getQueueDate()!=null){
			dbObject.put(MongoMessage_simQueueEntryDate,simQueueEntryStatus.getQueueDate().getTime());
		}
		dbObject.put(MongoMessage_simQueueEntryId,simQueueEntryStatus.getQueueID());
		dbObject.put(MongoMessage_simQueueEntryPriority,simQueueEntryStatus.getQueuePriority());
	}

}