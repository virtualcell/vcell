package cbit.vcell.message.server.dispatcher;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.PropertyLoader;
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
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
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
		SimulationJobStatus[] simJobStatusArray = adminDbTopLevel.getSimulationJobStatusArray(simKey, jobIndex, true);
		if (simJobStatusArray.length == 0){
			return null;
		}else if (simJobStatusArray.length == 1){
			return simJobStatusArray[0];
		}
		SimulationJobStatus latestSimJobStatus = simJobStatusArray[0];
		for (SimulationJobStatus simJobStatus : simJobStatusArray){
			if (latestSimJobStatus.getTaskID() < simJobStatus.getTaskID()){
				latestSimJobStatus = simJobStatus;
			}
		}
		return latestSimJobStatus;
	}

	SimulationJobStatus getSimulationJobStatus(KeyValue simKey, int jobIndex, int taskID) throws DataAccessException, SQLException {
		return adminDbTopLevel.getSimulationJobStatus(simKey, jobIndex, taskID, true);
	}

	@Override
	public void insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws DataAccessException, SQLException{
		adminDbTopLevel.insertSimulationJobStatus(simulationJobStatus,true);
	}

	@Override
	public SimulationJobStatus[] getActiveJobs() throws DataAccessException, SQLException{
		SimulationJobStatus[] activeJobs = adminDbTopLevel.getActiveJobs(VCellServerID.getSystemServerID(),true);
		return activeJobs;
	}
	@Override
	public Map<KeyValue,SimulationRequirements> getSimulationRequirements(List<KeyValue> simKeys) throws SQLException {
		Map<KeyValue,SimulationRequirements> simReqMap = adminDbTopLevel.getSimulationRequirements(simKeys,true);
		return simReqMap;
	}
	
	@Override
	public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus) throws DataAccessException, UpdateSynchronizationException, SQLException {
		adminDbTopLevel.updateSimulationJobStatus(newSimulationJobStatus,true);
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
	
}
