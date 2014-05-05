package cbit.vcell.message.server.dispatcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.messaging.db.SimulationExecutionStatusPersistent;
import cbit.vcell.messaging.db.SimulationExecutionStatus;
import cbit.vcell.messaging.db.SimulationJobStatusPersistent;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SimulationQueueID;
import cbit.vcell.messaging.db.SimulationQueueEntryStatusPersistent;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationMessagePersistent;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatusPersistent;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class SimulationDatabaseDirect implements SimulationDatabase {

	private AdminDBTopLevel adminDbTopLevel = null;
	private DatabaseServerImpl databaseServerImpl = null;
	private SessionLog log = null;
	private Map<KeyValue, FieldDataIdentifierSpec[]> simFieldDataIDMap = Collections.synchronizedMap(new HashMap<KeyValue, FieldDataIdentifierSpec[]>());
	private Map<String, User> userMap = Collections.synchronizedMap(new HashMap<String, User>());

	public SimulationDatabaseDirect(AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl databaseServerImpl, SessionLog log){
		this.databaseServerImpl = databaseServerImpl;
		this.adminDbTopLevel = adminDbTopLevel;
		this.log = log;
	}

	@Override
	public SimulationJobStatus getLatestSimulationJobStatus(KeyValue simKey, int jobIndex) throws DataAccessException, SQLException {
		SimulationJobStatusPersistent[] simJobStatusArray = adminDbTopLevel.getSimulationJobStatusArray(simKey, jobIndex, true);
		if (simJobStatusArray.length == 0){
			return null;
		}else if (simJobStatusArray.length == 1){
			return getSimulationJobStatusTransient(simJobStatusArray[0]);
		}
		SimulationJobStatusPersistent latestSimJobStatus = simJobStatusArray[0];
		for (SimulationJobStatusPersistent simJobStatus : simJobStatusArray){
			if (latestSimJobStatus.getTaskID() < simJobStatus.getTaskID()){
				latestSimJobStatus = simJobStatus;
			}
		}
		return getSimulationJobStatusTransient(latestSimJobStatus);
	}

	SimulationJobStatus getSimulationJobStatus(KeyValue simKey, int jobIndex, int taskID) throws DataAccessException, SQLException {
		SimulationJobStatusPersistent simJobStatusDb = adminDbTopLevel.getSimulationJobStatus(simKey, jobIndex, taskID, true);
		return getSimulationJobStatusTransient(simJobStatusDb);
	}

	@Override
	public void insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws DataAccessException, SQLException{
		SimulationJobStatusPersistent simulationJobStatusDb = getSimulationJobStatusPersistent(simulationJobStatus);
		adminDbTopLevel.insertSimulationJobStatus(simulationJobStatusDb,true);
	}

	@Override
	public SimulationJobStatus[] getActiveJobs() throws DataAccessException, SQLException{
		SimulationJobStatusPersistent[] activeJobsDb = adminDbTopLevel.getActiveJobs(VCellServerID.getSystemServerID(),true);
		SimulationJobStatus[] activeJobs = new SimulationJobStatus[activeJobsDb.length];
		for (int i=0;i<activeJobs.length;i++){
			activeJobs[i] = getSimulationJobStatusTransient(activeJobsDb[i]);
		}
		return activeJobs;
	}
	
	@Override
	public Map<KeyValue,SimulationRequirements> getSimulationRequirements(List<KeyValue> simKeys) throws SQLException {
		Map<KeyValue,SimulationRequirements> simReqMap = adminDbTopLevel.getSimulationRequirements(simKeys,true);
		return simReqMap;
	}
	
	@Override
	public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus) throws DataAccessException, UpdateSynchronizationException, SQLException {
		SimulationJobStatusPersistent newSimulationJobStatusDb = getSimulationJobStatusPersistent(newSimulationJobStatus);
		adminDbTopLevel.updateSimulationJobStatus(newSimulationJobStatusDb,true);
	}

	@Override
	public Simulation getSimulation(User user, KeyValue simKey) throws DataAccessException {
		Simulation sim = null;

		BigString simstr = databaseServerImpl.getSimulationXML(user,simKey);	
		if (simstr != null){
			try {
				sim = XmlHelper.XMLToSim(simstr.toString());
			}catch (XmlParseException e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		return sim;
	}

	@Override
	public FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs(Simulation sim) throws DataAccessException {
		try {		
			KeyValue simKey = sim.getKey();
			log.print("Get FieldDataIdentifierSpec for [" + simKey + "]");	
			FieldDataIdentifierSpec[] fieldDataIDSs = (FieldDataIdentifierSpec[])simFieldDataIDMap.get(simKey);

			if (fieldDataIDSs != null) {
				return fieldDataIDSs;
			}

			FieldFunctionArguments[] fieldFuncArgs =  FieldUtilities.getFieldFunctionArguments(sim.getMathDescription());
			if (fieldFuncArgs == null || fieldFuncArgs.length == 0) {
				fieldDataIDSs = new FieldDataIdentifierSpec[0];
				simFieldDataIDMap.put(simKey, fieldDataIDSs);
				return fieldDataIDSs;
			}
			fieldDataIDSs = new FieldDataIdentifierSpec[0];
			User owner = sim.getVersion().getOwner();
			FieldDataDBOperationSpec fieldDataDbOperationSpec = FieldDataDBOperationSpec.createGetExtDataIDsSpec(owner);
			FieldDataDBOperationResults fieldDataDBOperationResults = databaseServerImpl.fieldDataDBOperation(owner,fieldDataDbOperationSpec);
			ExternalDataIdentifier[] externalDataIDs = fieldDataDBOperationResults.extDataIDArr;
			if (externalDataIDs != null && externalDataIDs.length != 0 &&
					fieldFuncArgs != null && fieldFuncArgs.length>0	) {
				Vector<FieldDataIdentifierSpec> fieldDataIdV = new Vector<FieldDataIdentifierSpec>();
				for(int j=0;fieldFuncArgs != null && j<fieldFuncArgs.length;j+= 1){
					for(int i=0;i<externalDataIDs.length;i+= 1){
						if(externalDataIDs[i].getName().equals(fieldFuncArgs[j].getFieldName())){
							fieldDataIdV.add(
									new FieldDataIdentifierSpec(fieldFuncArgs[j],externalDataIDs[i])
									);
							break;
						}
					}
				}
				if(fieldDataIdV.size() > 0){
					fieldDataIDSs = new FieldDataIdentifierSpec[fieldDataIdV.size()];
					fieldDataIdV.copyInto(fieldDataIDSs);
				}
			}

			simFieldDataIDMap.put(simKey, fieldDataIDSs);		
			return fieldDataIDSs;
		} catch (Exception ex) {
			log.exception(ex);
			throw new DataAccessException(ex.getMessage());
		}
	}
	
	@Override
	public Set<KeyValue> getUnreferencedSimulations() throws SQLException{
		return adminDbTopLevel.getUnreferencedSimulations(true);
	}

	@Override
	public User getUser(String username) throws DataAccessException, SQLException {
		User user = null;

		synchronized(userMap) {
			user = (User)userMap.get(username);
			if (user!=null){
				return user;
			}
		}
		
		user = adminDbTopLevel.getUser(username,true);
		
		synchronized(userMap) {
			if (user != null) {
				userMap.put(username, user);
			}else{
				throw new RuntimeException("username "+username+" not found");
			}
		}
		return user;
	}

	@Override
	public SimulationInfo getSimulationInfo(User user, KeyValue simKey) throws ObjectNotFoundException, DataAccessException {
		SimulationInfo simInfo = databaseServerImpl.getSimulationInfo(user, simKey);
		return simInfo;
	}

	@Override
	public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws ObjectNotFoundException, DataAccessException {
		SimulationStatusPersistent[] simStatusDB = databaseServerImpl.getSimulationStatus(simKeys);
		ArrayList<SimulationStatus> simStatusList = new ArrayList<SimulationStatus>();
		for (SimulationStatusPersistent simStatus : simStatusDB){
			if (simStatus!=null){
				simStatusList.add(getSimulationStatusTransient(simStatus));
			}else{
				simStatusList.add(null);
			}
		}
		return simStatusList.toArray(new SimulationStatus[0]);
	}

	@Override
	public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws ObjectNotFoundException, DataAccessException {
		SimulationStatusPersistent simStatusDB = databaseServerImpl.getSimulationStatus(simulationKey);
		return getSimulationStatusTransient(simStatusDB);
	}

	private SimulationStatus getSimulationStatusTransient(SimulationStatusPersistent simStatusPersistent) {
		SimulationJobStatus[] simJobStatusArray = new SimulationJobStatus[simStatusPersistent.getJobStatuses().length];
		for (int i=0;i<simJobStatusArray.length;i++){
			simJobStatusArray[i] = getSimulationJobStatusTransient(simStatusPersistent.getJobStatus(i));
		}
		SimulationStatus simStatus = new SimulationStatus(simJobStatusArray);
		return simStatus;
	}
	
	private SimulationJobStatus getSimulationJobStatusTransient(SimulationJobStatusPersistent simJobStatus){
		VCellServerID serverID = simJobStatus.getServerID();
		VCSimulationIdentifier vcSimID = simJobStatus.getVCSimulationIdentifier();
		int jobIndex = simJobStatus.getJobIndex();
		Date submitDate = simJobStatus.getSubmitDate();
		SchedulerStatus schedulerStatus = SimulationJobStatus.SchedulerStatus.valueOf(simJobStatus.getSchedulerStatus().name());
		int taskID = simJobStatus.getTaskID();
		SimulationMessage simMessage = SimulationMessage.fromSerializedMessage(simJobStatus.getSimulationMessage().toSerialization());
		Date queueDate = simJobStatus.getSimulationQueueEntryStatus().getQueueDate();
		int queuePriority = simJobStatus.getSimulationQueueEntryStatus().getQueuePriority();
		SimulationQueueID queueId = SimulationJobStatus.SimulationQueueID.valueOf(simJobStatus.getSimulationQueueEntryStatus().getQueueID().name());
		SimulationQueueEntryStatus simQueueStatus = new SimulationQueueEntryStatus(queueDate,queuePriority,queueId);
		SimulationExecutionStatus simExecStatus = new SimulationExecutionStatus(simJobStatus.getSimulationExecutionStatus().getStartDate(),
				simJobStatus.getSimulationExecutionStatus().getComputeHost(),
				simJobStatus.getSimulationExecutionStatus().getLatestUpdateDate(),
				simJobStatus.getSimulationExecutionStatus().getEndDate(),
				simJobStatus.getSimulationExecutionStatus().hasData(),
				simJobStatus.getSimulationExecutionStatus().getHtcJobID());
		SimulationJobStatus simJobStatusTransient = new SimulationJobStatus(serverID,vcSimID,jobIndex,submitDate,schedulerStatus,taskID,simMessage,simQueueStatus,simExecStatus);
				
		return simJobStatusTransient;
	}
	
	private SimulationJobStatusPersistent getSimulationJobStatusPersistent(SimulationJobStatus simJobStatus){
		VCellServerID serverID = simJobStatus.getServerID();
		VCSimulationIdentifier vcSimID = simJobStatus.getVCSimulationIdentifier();
		int jobIndex = simJobStatus.getJobIndex();
		Date submitDate = simJobStatus.getSubmitDate();
		SimulationJobStatusPersistent.SchedulerStatus schedulerStatus = SimulationJobStatusPersistent.SchedulerStatus.valueOf(simJobStatus.getSchedulerStatus().name());
		int taskID = simJobStatus.getTaskID();
		SimulationMessagePersistent simMessage = SimulationMessagePersistent.fromSerializedMessage(simJobStatus.getSimulationMessage().toSerialization());
		Date queueDate = simJobStatus.getSimulationQueueEntryStatus().getQueueDate();
		int queuePriority = simJobStatus.getSimulationQueueEntryStatus().getQueuePriority();
		SimulationJobStatusPersistent.SimulationQueueID queueId = SimulationJobStatusPersistent.SimulationQueueID.valueOf(simJobStatus.getSimulationQueueEntryStatus().getQueueID().name());
		SimulationQueueEntryStatusPersistent simQueueStatus = new SimulationQueueEntryStatusPersistent(queueDate,queuePriority,queueId);
		SimulationExecutionStatusPersistent simExecStatus = new SimulationExecutionStatusPersistent(simJobStatus.getSimulationExecutionStatus().getStartDate(),
				simJobStatus.getSimulationExecutionStatus().getComputeHost(),
				simJobStatus.getSimulationExecutionStatus().getLatestUpdateDate(),
				simJobStatus.getSimulationExecutionStatus().getEndDate(),
				simJobStatus.getSimulationExecutionStatus().hasData(),
				simJobStatus.getSimulationExecutionStatus().getHtcJobID());
		SimulationJobStatusPersistent simJobStatusPersistent = new SimulationJobStatusPersistent(serverID,vcSimID,jobIndex,submitDate,schedulerStatus,taskID,simMessage,simQueueStatus,simExecStatus);
				
		return simJobStatusPersistent;
	}
	
}
