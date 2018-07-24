package cbit.vcell.message.server.dispatcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusPersistent;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationExecutionStatusPersistent;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.server.SimulationJobStatus.SimulationQueueID;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationQueueEntryStatus;
import cbit.vcell.server.SimulationQueueEntryStatusPersistent;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.StateInfo;
import cbit.vcell.server.UpdateSynchronizationException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SimulationMessagePersistent;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class SimulationDatabaseDirect implements SimulationDatabase {
	public static final Logger lg = LogManager.getLogger(SimulationDatabaseDirect.class);

	private AdminDBTopLevel adminDbTopLevel = null;
	private DatabaseServerImpl databaseServerImpl = null;
	private Map<KeyValue, FieldDataIdentifierSpec[]> simFieldDataIDMap = Collections.synchronizedMap(new HashMap<KeyValue, FieldDataIdentifierSpec[]>());
	private Map<String, User> userMap = Collections.synchronizedMap(new HashMap<String, User>());
	private SimpleJobStatusCache cache = null;
	
	public static class SimJobStatusKey {
		public final KeyValue simId;
		public final int jobIndex;
		public final int taskId;
		private final String hashString;
		private final int hashCode;
	
		public SimJobStatusKey(KeyValue simId, int jobIndex, int taskId){
			this.simId = simId;
			this.jobIndex = jobIndex;
			this.taskId = taskId;
			this.hashString = simId.toString() + ":"+jobIndex+":"+taskId;
			this.hashCode = this.hashString.hashCode();
		}
		@Override
		public int hashCode(){
			return hashCode;
		}
		@Override
		public boolean equals(Object obj){
			if (obj instanceof SimJobStatusKey){
				return ((SimJobStatusKey)obj).hashString.equals(hashString);
			}
			return false;
		}
		@Override
		public String toString(){
			return hashString;
		}
		public KeyValue getSimKey(){
			return simId;
		}
		public int getJobIndex(){
			return jobIndex;
		}
		public int getTaskId(){
			return taskId;
		}
	}
	
	private static class SimStatusCacheEntry {
		public final SimulationJobStatus jobStatus;
		public final StateInfo stateInfo;
		public final long lastupdate;
		
		public SimStatusCacheEntry(SimulationJobStatus jobStatus, StateInfo stateInfo) {
			super();
			this.jobStatus = jobStatus;
			this.stateInfo = stateInfo;
			this.lastupdate = System.currentTimeMillis();
		}
	}

	private interface SimpleJobStatusCache {
		SimStatusCacheEntry get(SimJobStatusKey simJobStatusKey);
		void put(SimJobStatusKey simJobStatusKey, SimStatusCacheEntry simStatusCacheEntry);
	}
	
	private static class SimpleJobStatusCache_DONT_CACHE implements SimpleJobStatusCache {
		public SimpleJobStatusCache_DONT_CACHE() {
		}

		@Override
		public SimStatusCacheEntry get(SimJobStatusKey simJobStatusKey) {
			return null;
		}

		@Override
		public void put(SimJobStatusKey simJobStatusKey, SimStatusCacheEntry simStatusCacheEntry) {
		}
		
	};

	
	public static class SimpleJobStatusCacheMultithread implements SimpleJobStatusCache {
		private final Cache<SimJobStatusKey, SimStatusCacheEntry> map;
		
		public SimpleJobStatusCacheMultithread(){
			map = CacheBuilder.newBuilder()
				       .maximumSize(1000)
				       .expireAfterAccess(30, TimeUnit.MINUTES)
				       .build();
		}

		@Override
		public SimStatusCacheEntry get(SimJobStatusKey simJobStatusKey) {
			return map.getIfPresent(simJobStatusKey);
		}

		@Override
		public void put(SimJobStatusKey simJobStatusKey, SimStatusCacheEntry simStatusCacheEntry) {
			// don't cache job status from another site
			if (simStatusCacheEntry.jobStatus.getServerID().equals(VCellServerID.getSystemServerID())){
				map.put(simJobStatusKey,simStatusCacheEntry);
			}
		}
		
	};
	
	
	public SimulationDatabaseDirect(AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl databaseServerImpl, boolean bCache){
		this.databaseServerImpl = databaseServerImpl;
		this.adminDbTopLevel = adminDbTopLevel;
		if (bCache){
			this.cache = new SimpleJobStatusCacheMultithread();
		}else{
			this.cache = new SimpleJobStatusCache_DONT_CACHE();
		}
	}

	@Override
	public SimulationJobStatus getLatestSimulationJobStatus(KeyValue simKey, int jobIndex) throws DataAccessException, SQLException {
		SimulationJobStatusPersistent[] simJobStatusArray = adminDbTopLevel.getSimulationJobStatusArray(simKey, jobIndex, true);
		if (simJobStatusArray.length == 0){
			return null;
		}
		int latestTaskID = simJobStatusArray[0].getTaskID();
		for (SimulationJobStatusPersistent simJobStatus : simJobStatusArray){
			if (latestTaskID < simJobStatus.getTaskID()){
				latestTaskID = simJobStatus.getTaskID();
			}
		}
		return getSimulationJobStatus(simKey, jobIndex, latestTaskID);
	}

	SimulationJobStatus getSimulationJobStatus(KeyValue simKey, int jobIndex, int taskID) throws DataAccessException, SQLException {
		SimJobStatusKey key = new SimJobStatusKey(simKey, jobIndex, taskID);
		SimStatusCacheEntry simStatusCacheEntry = cache.get(key);
		if (simStatusCacheEntry!=null){
			return simStatusCacheEntry.jobStatus;
		}else{
			SimulationJobStatusPersistent simJobStatusPersistent = adminDbTopLevel.getSimulationJobStatus(simKey, jobIndex, taskID, true);
			SimulationJobStatus simulationJobStatus = translateToSimulationJobStatusTransient(simJobStatusPersistent);
			cache.put(key, new SimStatusCacheEntry(simulationJobStatus,null));
			return simulationJobStatus;
		}
	}

	@Override
	public void insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws DataAccessException, SQLException{
		SimulationJobStatusPersistent simulationJobStatusDb = translateToSimulationJobStatusPersistent(simulationJobStatus);
		adminDbTopLevel.insertSimulationJobStatus(simulationJobStatusDb,true);
		SimJobStatusKey key = new SimJobStatusKey(simulationJobStatus.getVCSimulationIdentifier().getSimulationKey(),simulationJobStatus.getJobIndex(),simulationJobStatus.getTaskID());
		cache.put(key, new SimStatusCacheEntry(simulationJobStatus, null));
	}

	@Override
	public SimulationJobStatus[] getActiveJobs(VCellServerID vcellServerId) throws DataAccessException, SQLException{
		SimulationJobStatusPersistent[] activeJobsDb = adminDbTopLevel.getActiveJobs(vcellServerId,true);
		SimulationJobStatus[] activeJobs = new SimulationJobStatus[activeJobsDb.length];
		for (int i=0;i<activeJobsDb.length;i++){
			SimJobStatusKey key = new SimJobStatusKey(activeJobsDb[i].getVCSimulationIdentifier().getSimulationKey(),activeJobsDb[i].getJobIndex(),activeJobsDb[i].getTaskID());
			SimStatusCacheEntry entry = cache.get(key);
			if (entry!=null){
				activeJobs[i] = entry.jobStatus;
			}else{
				activeJobs[i] = translateToSimulationJobStatusTransient(activeJobsDb[i]);
				cache.put(key, new SimStatusCacheEntry(activeJobs[i],null));
			}
		}
		return activeJobs;
	}
	
	@Override
	public Map<KeyValue,SimulationRequirements> getSimulationRequirements(Collection<KeyValue> simKeys) throws SQLException {
		Map<KeyValue,SimulationRequirements> simReqMap = adminDbTopLevel.getSimulationRequirements(simKeys,true);
		return simReqMap;
	}
	
	@Override
	public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus, StateInfo stateInfo) throws DataAccessException, UpdateSynchronizationException, SQLException {
		SimulationJobStatusPersistent newSimulationJobStatusDb = translateToSimulationJobStatusPersistent(newSimulationJobStatus);
		adminDbTopLevel.updateSimulationJobStatus(newSimulationJobStatusDb,true);

		SimJobStatusKey key = new SimJobStatusKey(newSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey(), newSimulationJobStatus.getJobIndex(), newSimulationJobStatus.getTaskID());
		cache.put(key, new SimStatusCacheEntry(newSimulationJobStatus,stateInfo));
	}

	@Override
	public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus) throws DataAccessException, UpdateSynchronizationException, SQLException {
		updateSimulationJobStatus(newSimulationJobStatus, null);
	}

	@Override
	public Simulation getSimulation(User user, KeyValue simKey) throws DataAccessException {
		Simulation sim = null;

		BigString simstr = databaseServerImpl.getSimulationXML(user,simKey);	
		if (simstr != null){
			try {
				sim = XmlHelper.XMLToSim(simstr.toString());
			}catch (XmlParseException e){
				lg.error(e.getMessage(),e);
				throw new DataAccessException(e.getMessage());
			}
		}

		return sim;
	}

	@Override
	public FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs(Simulation sim) throws DataAccessException {
		try {		
			KeyValue simKey = sim.getKey();
			if (lg.isTraceEnabled()) lg.trace("Get FieldDataIdentifierSpec for [" + simKey + "]");	
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
			lg.error(ex.getMessage(), ex);
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
		SimulationStatusPersistent[] simStatusDbArray = databaseServerImpl.getSimulationStatus(simKeys);
		ArrayList<SimulationStatus> simStatusList = new ArrayList<SimulationStatus>();
		for (SimulationStatusPersistent simStatusDb : simStatusDbArray){
			if (simStatusDb!=null){
				simStatusList.add(buildSimulationStatus(simStatusDb));
			}else{
				simStatusList.add(null);
			}
		}
		return simStatusList.toArray(new SimulationStatus[0]);
	}

	@Override
	public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws ObjectNotFoundException, DataAccessException {
		SimulationStatusPersistent simStatusDB = databaseServerImpl.getSimulationStatus(simulationKey);
		if (simStatusDB==null) {
			throw new ObjectNotFoundException("simulation status for simkey "+simulationKey+" not found");
		}
		return buildSimulationStatus(simStatusDB);
	}

	private SimulationStatus buildSimulationStatus(SimulationStatusPersistent simStatusDB) throws ObjectNotFoundException, DataAccessException {
		SimulationStatus simulationStatus = translateToSimulationStatusTransient(simStatusDB);
		// replace SimulationJobStatus from cache when possible
		ArrayList<SimulationJobStatus> simJobStatusList = new ArrayList<SimulationJobStatus>();
		int index = 0;
		while (simulationStatus.getJobStatus(index)!=null){
			SimulationJobStatus simJobStatusDb = simulationStatus.getJobStatus(index);
			SimJobStatusKey key = new SimJobStatusKey(simJobStatusDb.getVCSimulationIdentifier().getSimulationKey(), simJobStatusDb.getJobIndex(), simJobStatusDb.getTaskID());
			SimStatusCacheEntry entry = cache.get(key);
			if (entry != null){
				// assume cache is more recent
				simJobStatusList.add(entry.jobStatus);
			}else{
				simJobStatusList.add(simJobStatusDb);
				cache.put(key, new SimStatusCacheEntry(simJobStatusDb,null));
			}
			index++;
		}
		SimulationStatus newSimStatus = new SimulationStatus(simJobStatusList.toArray(new SimulationJobStatus[simJobStatusList.size()]));
		return newSimStatus;
	}

	@Override
	public SimulationJobStatus[] queryJobs(SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException {
		//
		// create corresponding SimpleJobStatus[] from SimpleJobStatusPersistent[]
		// 1) get SimpleJobStatusPersistent from database (with stored status, metadata, and documentLinks - but no stateInfo)
		// 2) if already in cache, use SimulationJobStatus and stateInfo from cache.
		// 3) if not in cache, use SimulationJobStatus from database and no stateInfo, populate cache.
		//
		List<SimulationJobStatusPersistent> simpleJobStatusPersistentList = databaseServerImpl.getSimulationJobStatus(simStatusQuerySpec);
		ArrayList<SimulationJobStatus> simulationJobStatusList = new ArrayList<SimulationJobStatus>();
		for (SimulationJobStatusPersistent simJobStatusDb : simpleJobStatusPersistentList){
			SimJobStatusKey key = new SimJobStatusKey(simJobStatusDb.getVCSimulationIdentifier().getSimulationKey(),simJobStatusDb.getJobIndex(),simJobStatusDb.getTaskID());
			SimStatusCacheEntry simStatusCacheEntry = cache.get(key);
			
			StateInfo cachedStateInfo = null; //TODO need to get this from memory cache.
			SimulationJobStatus latestSimulationJobStatus = null;
			if (simStatusCacheEntry != null){
				cachedStateInfo = simStatusCacheEntry.stateInfo;
				latestSimulationJobStatus = simStatusCacheEntry.jobStatus;
			}else{
				latestSimulationJobStatus = translateToSimulationJobStatusTransient(simJobStatusDb);
				cache.put(key, new SimStatusCacheEntry(latestSimulationJobStatus, null));
			}
			
			SimulationJobStatus simulationJobStatus = translateToSimulationJobStatusTransient(simJobStatusDb);
			
			simulationJobStatusList.add(simulationJobStatus); // uses latest SimulationJobStatus and StateInfo.
		}
		return simulationJobStatusList.toArray(new SimulationJobStatus[simulationJobStatusList.size()]);
	}

	@Override
	public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException {
		//
		// create corresponding SimpleJobStatus[] from SimpleJobStatusPersistent[]
		// 1) get SimpleJobStatusPersistent from database (with stored status, metadata, and documentLinks - but no stateInfo)
		// 2) if already in cache, use SimulationJobStatus and stateInfo from cache.
		// 3) if not in cache, use SimulationJobStatus from database and no stateInfo, populate cache.
		//
		List<SimpleJobStatusPersistent> simpleJobStatusPersistentList = databaseServerImpl.getSimpleJobStatus(simStatusQuerySpec);
		ArrayList<SimpleJobStatus> simpleJobStatusList = new ArrayList<SimpleJobStatus>();
		for (SimpleJobStatusPersistent simpleJobStatusPersistent : simpleJobStatusPersistentList){
			SimulationJobStatusPersistent simJobStatusDb = simpleJobStatusPersistent.jobStatus;
			SimJobStatusKey key = new SimJobStatusKey(simpleJobStatusPersistent.simulationMetadata.vcSimID.getSimulationKey(),simJobStatusDb.getJobIndex(),simJobStatusDb.getTaskID());
			SimStatusCacheEntry simStatusCacheEntry = cache.get(key);
			
			StateInfo cachedStateInfo = null; //TODO need to get this from memory cache.
			SimulationJobStatus latestSimulationJobStatus = null;
			if (simStatusCacheEntry != null){
				cachedStateInfo = simStatusCacheEntry.stateInfo;
				latestSimulationJobStatus = simStatusCacheEntry.jobStatus;
			}else{
				latestSimulationJobStatus = translateToSimulationJobStatusTransient(simJobStatusDb);
				cache.put(key, new SimStatusCacheEntry(latestSimulationJobStatus, null));
			}
			
			SimpleJobStatus bestSimpleJobStatus = translateToSimpleJobStatusTransient(simpleJobStatusPersistent, latestSimulationJobStatus, cachedStateInfo);
			
			simpleJobStatusList.add(bestSimpleJobStatus); // uses latest SimulationJobStatus and StateInfo.
		}
		return simpleJobStatusList.toArray(new SimpleJobStatus[simpleJobStatusList.size()]);
	}

	private SimulationJobStatus translateToSimulationJobStatusTransient(SimulationJobStatusPersistent simJobStatus){
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
	
	private SimulationJobStatusPersistent translateToSimulationJobStatusPersistent(SimulationJobStatus simJobStatus){
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

	private SimpleJobStatus translateToSimpleJobStatusTransient(SimpleJobStatusPersistent simpleJobStatusPersistent, SimulationJobStatus cachedSimulationJobStatus, StateInfo cachedStateInfo){
		SimulationJobStatus simJobStatus = null;
		if (cachedSimulationJobStatus != null){
			simJobStatus = cachedSimulationJobStatus;
		}else{
			simJobStatus = translateToSimulationJobStatusTransient(simpleJobStatusPersistent.jobStatus);
		}
		SimpleJobStatus simpleJobStatus = new SimpleJobStatus(simpleJobStatusPersistent.simulationMetadata,simpleJobStatusPersistent.simulationDocumentLink,simJobStatus,cachedStateInfo);
		return simpleJobStatus;
	}
	
	private SimulationStatus translateToSimulationStatusTransient(SimulationStatusPersistent simStatusPersistent) {
		ArrayList<SimulationJobStatus> simJobStatusList = new ArrayList<SimulationJobStatus>();
		if (simStatusPersistent==null) {	
		}
		SimulationJobStatusPersistent[] simStatusPersistentArray = simStatusPersistent.getJobStatuses();
		if (simStatusPersistentArray==null) {
		}
		for (SimulationJobStatusPersistent stat : simStatusPersistentArray) {
			simJobStatusList.add(translateToSimulationJobStatusTransient(stat));
		}
		SimulationStatus simStatus = new SimulationStatus(simJobStatusList.toArray(new SimulationJobStatus[0]));
		return simStatus;
	}
	
	
}
