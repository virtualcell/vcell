package cbit.vcell.message.server.dispatcher;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import org.vcell.util.BigString;
import org.vcell.util.CacheException;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.sql.DBCacheTable;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatusInfo;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ResultSetCrawler;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class SimulationDatabase {

	private AdminDBTopLevel adminDbTopLevel = null;
	private DatabaseServerImpl databaseServerImpl = null;
	private SessionLog log = null;
	private Map<KeyValue, FieldDataIdentifierSpec[]> simFieldDataIDMap = Collections.synchronizedMap(new HashMap<KeyValue, FieldDataIdentifierSpec[]>());
	private DBCacheTable simulationMap = null;
	private Map<KeyValue, User> simUserMap = Collections.synchronizedMap(new HashMap<KeyValue, User>());
	protected HashSet<VCSimulationDataIdentifier> resultSetSavedSet = new HashSet<VCSimulationDataIdentifier>();
	private ResultSetCrawler resultSetCrawler = null;


	public SimulationDatabase(ResultSetCrawler resultSetCrawler, AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl databaseServerImpl, SessionLog log){
		this.resultSetCrawler = resultSetCrawler;
		this.databaseServerImpl = databaseServerImpl;
		this.adminDbTopLevel = adminDbTopLevel;
		this.log = log;
	}

	public SimulationJobStatus[] getSimulationJobStatusArray(KeyValue simKey, int jobIndex) throws DataAccessException, SQLException {
		return adminDbTopLevel.getSimulationJobStatusArray(simKey, jobIndex, true);
	}

	public SimulationJobStatus getSimulationJobStatus(KeyValue simKey, int jobIndex, int taskID) throws DataAccessException, SQLException {
		return adminDbTopLevel.getSimulationJobStatus(simKey, jobIndex, taskID, true);
	}

	public SimulationJobStatus insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws DataAccessException, SQLException{
		return adminDbTopLevel.insertSimulationJobStatus(simulationJobStatus,true);
	}

	public SimulationJobStatusInfo[] getActiveJobs(VCellServerID[] serverIDs) throws DataAccessException, SQLException{
		SimulationJobStatusInfo[] activeJobs = adminDbTopLevel.getActiveJobs(serverIDs,true);
		return activeJobs;
	}
	public SimulationJobStatus updateSimulationJobStatus(SimulationJobStatus oldSimulationJobStatus, SimulationJobStatus newSimulationJobStatus) throws DataAccessException, UpdateSynchronizationException, SQLException {
		if (oldSimulationJobStatus==null){
			return adminDbTopLevel.insertSimulationJobStatus(newSimulationJobStatus,true);
		}else{
			return adminDbTopLevel.updateSimulationJobStatus(oldSimulationJobStatus,newSimulationJobStatus,true);
		}
	}

	public Simulation getSimulation(User user, KeyValue simKey) throws DataAccessException {
		if (simulationMap == null) {
			log.print("Initializaing DBCacheTable!");
			simulationMap = new DBCacheTable(3600 * 1000);
		}

		log.print("Get simulation [" + simKey + ","  + user + "]");	
		Simulation sim = (Simulation)simulationMap.getCloned(simKey);

		if (sim != null) {
			return sim;
		}

		BigString simstr = databaseServerImpl.getSimulationXML(user,simKey);	

		if (simstr != null){
			try {
				sim = XmlHelper.XMLToSim(simstr.toString());
			}catch (XmlParseException e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
			if (sim != null) {
				try {
					simulationMap.putProtected(simKey, sim);
				} catch (CacheException e) {
					// if can't cache the simulation, it is ok
					e.printStackTrace();
				}
			}
		}

		return sim;
	}

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
				return null;
			}
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

			if (fieldDataIDSs != null){
				simFieldDataIDMap.put(simKey, fieldDataIDSs);		
			}

			return fieldDataIDSs;
		} catch (Exception ex) {
			log.exception(ex);
			throw new DataAccessException(ex.getMessage());
		}
	}
	
	public SimulationJobStatus getNextObsoleteSimulation(long interval) throws SQLException{
		return adminDbTopLevel.getNextObsoleteSimulation(interval,true);
	}

	public User getUser(KeyValue simKey, String username) throws DataAccessException, SQLException {
		User user = null;

		synchronized(simUserMap) {
			user = (User)simUserMap.get(simKey);

			if (user != null && username != null && !user.getName().equals(username)) {
				throw new DataAccessException("Wrong user [" + user.getName() + "," + username + "] for the simulation [" + simKey + "]");
			}

			if (user == null) {
				if (username != null) {
					user = adminDbTopLevel.getUser(username,true);
				} else {
					user = adminDbTopLevel.getUserFromSimulationKey(simKey,true);
				}
				if (user != null) {
					simUserMap.put(simKey, user);
				}			
			}
		}

		return user;
	}

	public SimulationInfo getSimulationInfo(User user, KeyValue simKey) throws ObjectNotFoundException, DataAccessException {
		if (simulationMap == null) {
			log.print("Initializaing DBCacheTable!");
			simulationMap = new DBCacheTable(3600 * 1000);
		}

		log.print("Get simulation [" + simKey + ","  + user + "]");	
		Simulation sim = (Simulation)simulationMap.get(simKey);

		if (sim != null) {
			return sim.getSimulationInfo();
		}

		SimulationInfo simInfo = databaseServerImpl.getSimulationInfo(user, simKey);

		return simInfo;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (2/3/2004 8:34:36 AM)
	 * @return cbit.vcell.messaging.SimulationTask
	 * @param simKey cbit.sql.KeyValue
	 * @throws XmlParseException 
	 * @throws DataAccessException 
	 * @throws ObjectNotFoundException 
	 * @throws SQLException 
	 */
	public SimulationTask getSimulationTask(SimulationJobStatus jobStatus) throws XmlParseException, ObjectNotFoundException, DataAccessException, SQLException {
		VCSimulationIdentifier vcSimID = jobStatus.getVCSimulationIdentifier();
		User user = getUser(vcSimID.getSimulationKey(), null);				
		Simulation sim = getSimulation(user, vcSimID.getSimulationKey());
		SimulationTask simTask = new SimulationTask(new SimulationJob(sim, jobStatus.getJobIndex(), getFieldDataIdentifierSpecs(sim)), jobStatus.getTaskID());

		return simTask;
	}

	public int getNumSimulationJobs(User user, KeyValue simKey) throws DataAccessException {
		Simulation sim = getSimulation(user, simKey);
		return sim.getScanCount();
	}

	public void dataMoved(VCSimulationDataIdentifier vcSimDataID, User user) {
		// called by data mover thread after successful move operations
		try {		
			if (!resultSetSavedSet.contains(vcSimDataID)){
				try {
					resultSetCrawler.updateSimResults(user,vcSimDataID);
					resultSetSavedSet.add(vcSimDataID);
				} catch (Throwable exc) {
					log.exception(exc);
				}
			}
		} catch (Throwable e){
			log.exception(e);
		}
	}

	public SimulationJob getSimulationJob(KeyValue simKey, int jobIndex) throws DataAccessException, SQLException {
		User user = getUser(simKey, null);				
		Simulation sim = getSimulation(user, simKey);
		return new SimulationJob(sim, jobIndex, getFieldDataIdentifierSpecs(sim));
	}

}
