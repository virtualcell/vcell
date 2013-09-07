package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.SimulationRepresentation;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.server.bootstrap.RpcDataServerProxy;
import cbit.vcell.message.server.bootstrap.RpcSimServerProxy;
import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.BioModelTable;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DatabaseServerImpl.OrderBy;
import cbit.vcell.modeldb.SimContextRep;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.simdata.DataSetMetadata;
import cbit.vcell.simdata.DataSetTimeSeries;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class RestDatabaseService {
	
	private DatabaseServerImpl databaseServerImpl = null;
	private AdminDBTopLevel adminDbTopLevel = null;
	ConcurrentHashMap<KeyValue, SimContextRep> scMap = new ConcurrentHashMap<KeyValue, SimContextRep>();
	KeyValue mostRecentSimContextKey = new KeyValue("0");
	ConcurrentHashMap<KeyValue, SimulationRep> simMap = new ConcurrentHashMap<KeyValue, SimulationRep>();
	KeyValue mostRecentSimulationKey = new KeyValue("0");
	VCMessagingService vcMessagingService = null;
	SessionLog log = null;

	public RestDatabaseService(DatabaseServerImpl databaseServerImpl, AdminDBTopLevel adminDbTopLevel, VCMessagingService vcMessagingService, SessionLog log) {
		this.databaseServerImpl = databaseServerImpl;
		this.adminDbTopLevel = adminDbTopLevel;
		this.vcMessagingService = vcMessagingService;
		this.log = log;
	}
	
	public SimulationRep startSimulation(BiomodelSimulationStartServerResource resource, User vcellUser) throws DataAccessException, SQLException{
		String simId = resource.getAttribute(VCellApiApplication.SIMULATIONID);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new DataAccessException("Simulation with key "+simKey+" not found");
		}
		User owner = simRep.getOwner();
		if (!owner.compareEqual(vcellUser)){
			throw new DataAccessException("not authorized to start simulation");
		}
		VCMessageSession rpcSession = vcMessagingService.createProducerSession();
		try {
			UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
			try {
				userLoginInfo.setUser(vcellUser);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DataAccessException(e.getMessage());
			}
			RpcSimServerProxy rpcSimServerProxy = new RpcSimServerProxy(userLoginInfo, rpcSession, log);
			VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
			rpcSimServerProxy.startSimulation(vcSimID, simRep.getScanCount());
			return simRep;
		}finally{
			rpcSession.close();
		}
	}

	public SimulationRep stopSimulation(BiomodelSimulationStopServerResource resource, User vcellUser) throws DataAccessException, SQLException{
		String simId = resource.getAttribute(VCellApiApplication.SIMULATIONID);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new DataAccessException("Simulation with key "+simKey+" not found");
		}
		User owner = simRep.getOwner();
		if (!owner.compareEqual(vcellUser)){
			throw new DataAccessException("not authorized to stop simulation");
		}
		VCMessageSession rpcSession = vcMessagingService.createProducerSession();
		try {
			UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
			try {
				userLoginInfo.setUser(vcellUser);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DataAccessException(e.getMessage());
			}
			RpcSimServerProxy rpcSimServerProxy = new RpcSimServerProxy(userLoginInfo, rpcSession, log);
			VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
			rpcSimServerProxy.stopSimulation(vcSimID);
			return simRep;
		}finally{
			rpcSession.close();
		}
	}

	public DataSetMetadata getDataSetMetadata(SimDataServerResource resource, User vcellUser) throws DataAccessException, SQLException{
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
		String simId = resource.getAttribute(VCellApiApplication.SIMDATAID);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new DataAccessException("Simulation with key "+simKey+" not found");
		}
		User owner = simRep.getOwner();
		int jobIndex = 0;
		VCMessageSession rpcSession = vcMessagingService.createProducerSession();
		try {
			RpcDataServerProxy rpcDataServerProxy = new RpcDataServerProxy(userLoginInfo, rpcSession, log);
			VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
			VCDataIdentifier vcdID = new VCSimulationDataIdentifier(vcSimID, jobIndex);
			DataSetMetadata dataSetMetadata = rpcDataServerProxy.getDataSetMetadata(vcdID);
			return dataSetMetadata;
		}finally{
			rpcSession.close();
		}
	}

	public DataSetTimeSeries getDataSetTimeSeries(SimDataValuesServerResource resource, User vcellUser) throws DataAccessException, SQLException{
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
		String simId = resource.getAttribute(VCellApiApplication.SIMDATAID);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		String jobIndexString = resource.getAttribute(VCellApiApplication.JOBINDEX);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new DataAccessException("Simulation with key "+simKey+" not found");
		}
		int jobIndex = Integer.parseInt(jobIndexString);
		String variableNames[] = null; // TODO: pass in variables names from the query parameters.
		User owner = simRep.getOwner();
		VCMessageSession rpcSession = vcMessagingService.createProducerSession();
		try {
			RpcDataServerProxy rpcDataServerProxy = new RpcDataServerProxy(userLoginInfo, rpcSession, log);
			VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
			VCDataIdentifier vcdID = new VCSimulationDataIdentifier(vcSimID, jobIndex);
			DataSetTimeSeries dataSetTimeSeries = rpcDataServerProxy.getDataSetTimeSeries(vcdID, variableNames);
			return dataSetTimeSeries;
		}finally{
			rpcSession.close();
		}
	}

	public BioModelRep[] query(BiomodelsServerResource resource, User vcellUser) throws SQLException, DataAccessException {			
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		String bioModelName = resource.getQueryValue(BiomodelsServerResource.PARAM_BM_NAME);
		Long bioModelID = resource.getLongQueryValue(BiomodelsServerResource.PARAM_BM_ID);
		Long savedLow = resource.getLongQueryValue(BiomodelsServerResource.PARAM_SAVED_LOW);
		Long savedHigh = resource.getLongQueryValue(BiomodelsServerResource.PARAM_SAVED_HIGH);
		Long startRowParam = resource.getLongQueryValue(BiomodelsServerResource.PARAM_START_ROW);
		Long maxRowsParam = resource.getLongQueryValue(BiomodelsServerResource.PARAM_MAX_ROWS);
		String categoryParam = resource.getQueryValue(BiomodelsServerResource.PARAM_CATEGORY); // it is ok if the category is null;
		String ownerParam = resource.getQueryValue(BiomodelsServerResource.PARAM_BM_OWNER); // it is ok if the ownerName is null;
		String orderByParam = resource.getQueryValue(BiomodelsServerResource.PARAM_ORDERBY); // it is ok if the orderBy is null;
		int startRow = 1; // default
		if (startRowParam!=null){
			startRow = startRowParam.intValue();
		}
		int maxRows = 10; // default
		if (maxRowsParam!=null){
			maxRows = maxRowsParam.intValue();
		}
		ArrayList<String> conditions = new ArrayList<String>();
		
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
		
		if (savedLow != null){
			conditions.add("(" + BioModelTable.table.versionDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(savedLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
		}
		if (savedHigh != null){
			conditions.add("(" + BioModelTable.table.versionDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(savedHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
		}
		if (bioModelName != null && bioModelName.trim().length()>0){
			String pattern = bioModelName.trim();
			pattern = pattern.replace("  ", " ");
			pattern = pattern.replace("(", " ");
			pattern = pattern.replace(")", " ");
			pattern = pattern.replace("'", " ");
			pattern = pattern.replace("\"", " ");
			pattern = pattern.replace("*","%");
			pattern = "%"+pattern+"%";
			pattern = pattern.replace("%%","%");
			conditions.add("(" + "lower("+BioModelTable.table.name.getQualifiedColName()+")" + " like " + "lower('"+pattern+"')" + ")");		
		}
		if (bioModelID != null){
			conditions.add("(" + BioModelTable.table.id.getQualifiedColName() + " = " + bioModelID + ")");		
		}
		//
		// if ownerParam and categoryParam are both not specified, then return all models that we have permission for.
		//
		if (categoryParam==null && ownerParam!=null){
			if (ownerParam.equals(VCellApiApplication.PSEUDOOWNER_PUBLIC)){
				categoryParam = VCellApiApplication.CATEGORY_PUBLIC;
				ownerParam = null;
			}else if (ownerParam.equals(VCellApiApplication.PSEUDOOWNER_EDUCATION)){
				categoryParam = VCellApiApplication.CATEGORY_EDUCATION;
				ownerParam = null;
			}else if (ownerParam.equals(VCellApiApplication.PSEUDOOWNER_SHARED)){
				categoryParam = VCellApiApplication.CATEGORY_SHARED;
				ownerParam = null;
			}else if (ownerParam.equals(VCellApiApplication.PSEUDOOWNER_TUTORIAL)){
				categoryParam = VCellApiApplication.CATEGORY_TUTORIAL;
				ownerParam = null;
			}else{
				categoryParam = VCellApiApplication.CATEGORY_ALL;
			}
		}else if (categoryParam==null && ownerParam==null){
			categoryParam = VCellApiApplication.CATEGORY_ALL;
		}

		if (categoryParam.equals(VCellApiApplication.CATEGORY_MINE)){
			//
			// return all models owned by me (none if not authenticated).
			//
			conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " = '" + vcellUser.getName() + "')");
			
		}else if (categoryParam.equals(VCellApiApplication.CATEGORY_PUBLIC)){
			//
			// public models that aren't "Tutorial" or "Education"
			//
			// we will display all public models (even if owned by this user)
			//
			conditions.add("(" + BioModelTable.table.privacy.getQualifiedColName() + " = " + GroupAccess.GROUPACCESS_ALL + ")");
			if (ownerParam!=null && ownerParam.trim().length()>0){
				conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " = '" + ownerParam + "')");		
			}
			conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " != '" + VCellApiApplication.USERNAME_TUTORIAL + "')");
			conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " != '" + VCellApiApplication.USERNAME_EDUCATION + "')");
		}else if (categoryParam.equals(VCellApiApplication.CATEGORY_EDUCATION)){
			//
			// public models from "Education"
			//
			conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " = '" + VCellApiApplication.USERNAME_EDUCATION + "')");
			conditions.add("(" + BioModelTable.table.privacy.getQualifiedColName() + " = " + GroupAccess.GROUPACCESS_ALL + ")");
		}else if (categoryParam.equals(VCellApiApplication.CATEGORY_TUTORIAL)){
			//
			// public models from "tutorial"
			//
			conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " = '" + VCellApiApplication.USERNAME_TUTORIAL + "')");
			conditions.add("(" + BioModelTable.table.privacy.getQualifiedColName() + " = " + GroupAccess.GROUPACCESS_ALL + ")");
		}else if (categoryParam.equals(VCellApiApplication.CATEGORY_SHARED)){
			//
			// only authenticated users can have shared models
			//
			// we will display all shared models (even if owned by this user)
			//
			if (!vcellUser.compareEqual(VCellApiApplication.DUMMY_USER)){
				conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " != '" + vcellUser.getName() + "')");
			}
			if (ownerParam!=null && ownerParam.trim().length()>0){
				conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " = '" + ownerParam + "')");		
			}
			conditions.add("(" + BioModelTable.table.privacy.getQualifiedColName() + " != " + GroupAccess.GROUPACCESS_ALL + ")");
			conditions.add("(" + BioModelTable.table.privacy.getQualifiedColName() + " != " + GroupAccess.GROUPACCESS_NONE + ")");
		}else if (categoryParam.equals(VCellApiApplication.CATEGORY_ALL)){
			//
			// not a pseudo-owner (like shared, public, education, tutorial) return all from this user that we have permission for
			//
			if (ownerParam!=null && ownerParam.length()>0){
				conditions.add("(" + UserTable.table.userid.getQualifiedColName() + " = '" + ownerParam + "')");
			}
		}
	
		StringBuffer conditionsBuffer = new StringBuffer();
		for (String condition : conditions) {
			if (conditionsBuffer.length() > 0) {
				conditionsBuffer.append(" AND ");
			}
			conditionsBuffer.append(condition);
		}
		OrderBy orderBy = OrderBy.date_asc; // default
		if (orderByParam!=null){
			if (orderByParam.equals(BiomodelsServerResource.PARAM_ORDERBY_DATE_ASC)){
				orderBy = OrderBy.date_asc;
			}else if (orderByParam.equals(BiomodelsServerResource.PARAM_ORDERBY_DATE_DESC)){
				orderBy = OrderBy.date_desc;
			}else if (orderByParam.equals(BiomodelsServerResource.PARAM_ORDERBY_NAME_ASC)){
				orderBy = OrderBy.name_asc;
			}else if (orderByParam.equals(BiomodelsServerResource.PARAM_ORDERBY_NAME_DESC)){
				orderBy = OrderBy.name_desc;
			}
		}
		BioModelRep[] bioModelReps = databaseServerImpl.getBioModelReps(vcellUser, conditionsBuffer.toString(), orderBy, startRow, maxRows);
		for (BioModelRep bioModelRep : bioModelReps) {
			KeyValue[] simContextKeys = bioModelRep.getSimContextKeyList();
			for (KeyValue scKey : simContextKeys) {
				SimContextRep scRep = getSimContextRep(scKey);
				if (scRep != null){
					bioModelRep.addSimContextRep(scRep);
				}
			}
			KeyValue[] simulationKeys = bioModelRep.getSimKeyList();
			for (KeyValue simKey : simulationKeys) {
				SimulationRep simulationRep = getSimulationRep(simKey);
				if (simulationRep != null){
					bioModelRep.addSimulationRep(simulationRep);
				}
			}
			
		}
	   	return bioModelReps;
	}
	
	public BioModelRep query(BiomodelServerResource resource, User vcellUser) throws SQLException, DataAccessException {	
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		ArrayList<String> conditions = new ArrayList<String>();
		String bioModelID = (String)resource.getRequestAttributes().get(VCellApiApplication.BIOMODELID);
		if (bioModelID != null){
			conditions.add("(" + BioModelTable.table.id.getQualifiedColName() + " = " + bioModelID + ")");		
		}else{
			throw new RuntimeException("bioModelID not specified");
		}
	
		StringBuffer conditionsBuffer = new StringBuffer();
		for (String condition : conditions) {
			if (conditionsBuffer.length() > 0) {
				conditionsBuffer.append(" AND ");
			}
			conditionsBuffer.append(condition);
		}
		int startRow = 1;
		int maxRows = 1;
		BioModelRep[] bioModelReps = databaseServerImpl.getBioModelReps(vcellUser, conditionsBuffer.toString(), null, startRow, maxRows);
		for (BioModelRep bioModelRep : bioModelReps) {
			KeyValue[] simContextKeys = bioModelRep.getSimContextKeyList();
			for (KeyValue scKey : simContextKeys) {
				SimContextRep scRep = getSimContextRep(scKey);
				if (scRep != null){
					bioModelRep.addSimContextRep(scRep);
				}
			}
			KeyValue[] simulationKeys = bioModelRep.getSimKeyList();
			for (KeyValue simKey : simulationKeys) {
				SimulationRep simulationRep = getSimulationRep(simKey);
				if (simulationRep != null){
					bioModelRep.addSimulationRep(simulationRep);
				}
			}
			
		}
		if (bioModelReps==null || bioModelReps.length!=1){
			throw new RuntimeException("failed to get biomodel");
		}
		return bioModelReps[0];
	}
	
	public SimulationRepresentation query(BiomodelSimulationServerResource resource, User vcellUser) throws SQLException, DataAccessException {	
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		ArrayList<String> conditions = new ArrayList<String>();
		String bioModelID = (String)resource.getRequestAttributes().get(VCellApiApplication.BIOMODELID);
		if (bioModelID != null){
			conditions.add("(" + BioModelTable.table.id.getQualifiedColName() + " = " + bioModelID + ")");		
		}else{
			throw new RuntimeException(VCellApiApplication.BIOMODELID+" not specified");
		}
	
		StringBuffer conditionsBuffer = new StringBuffer();
		for (String condition : conditions) {
			if (conditionsBuffer.length() > 0) {
				conditionsBuffer.append(" AND ");
			}
			conditionsBuffer.append(condition);
		}
		int startRow = 1;
		int maxRows = 1;
		BioModelRep[] bioModelReps = databaseServerImpl.getBioModelReps(vcellUser, conditionsBuffer.toString(), null, startRow, maxRows);
		for (BioModelRep bioModelRep : bioModelReps) {
			KeyValue[] simContextKeys = bioModelRep.getSimContextKeyList();
			for (KeyValue scKey : simContextKeys) {
				SimContextRep scRep = getSimContextRep(scKey);
				if (scRep != null){
					bioModelRep.addSimContextRep(scRep);
				}
			}
			KeyValue[] simulationKeys = bioModelRep.getSimKeyList();
			for (KeyValue simKey : simulationKeys) {
				SimulationRep simulationRep = getSimulationRep(simKey);
				if (simulationRep != null){
					bioModelRep.addSimulationRep(simulationRep);
				}
			}
			
		}
		if (bioModelReps==null || bioModelReps.length!=1){
			throw new RuntimeException("failed to get biomodel");
		}
		
		String simulationId = (String)resource.getRequestAttributes().get(VCellApiApplication.SIMULATIONID);
		if (simulationId == null){
			throw new RuntimeException(VCellApiApplication.SIMULATIONID+" not specified");
		}
		SimulationRep simRep = getSimulationRep(new KeyValue(simulationId));
		return new SimulationRepresentation(simRep, bioModelReps[0]);
	}
	
	public SimContextRep getSimContextRep(KeyValue key) throws DataAccessException, SQLException{
		SimContextRep simContextRep = scMap.get(key);
		if (simContextRep!=null){
			return simContextRep;
		}else if (key.compareTo(mostRecentSimContextKey)>0){
			int maxRows = 100000;
			while (true){
				SimContextRep[] simContextReps = databaseServerImpl.getSimContextReps(mostRecentSimContextKey, maxRows);
				for (SimContextRep simContextRep2 : simContextReps) {
					if (mostRecentSimContextKey==null || mostRecentSimContextKey.compareTo(simContextRep2.getScKey())<0){
						mostRecentSimContextKey = simContextRep2.getScKey();
					}
					scMap.put(simContextRep2.getScKey(), simContextRep2);
				}
				if (simContextReps.length<maxRows){
					break;
				}
			}
			
			return scMap.get(key);
		}else{
			System.out.println("couldn't find application key = " + key + " number of cached applications is " + scMap.size());
			return null;
		}
	}
	
	
	public SimulationRep getSimulationRep(KeyValue key) throws DataAccessException, SQLException{
		SimulationRep simulationRep = simMap.get(key);
		if (simulationRep!=null){
			return simulationRep;
		}else if (key.compareTo(mostRecentSimulationKey)>0){
			int maxRows = 100000;
			while (true){
				SimulationRep[] simulationReps = databaseServerImpl.getSimulationReps(mostRecentSimulationKey, maxRows);
				for (SimulationRep simulationRep2 : simulationReps) {
					if (mostRecentSimulationKey==null || mostRecentSimulationKey.compareTo(simulationRep2.getKey())<0){
						mostRecentSimulationKey = simulationRep2.getKey();
					}
					simMap.put(simulationRep2.getKey(), simulationRep2);
				}
				if (simulationReps.length<maxRows){
					break;
				}
			}
			
			return simMap.get(key);
		}else{
			System.out.println("couldn't find simulation key = " + key + " number of cached simulations is " + simMap.size() + " max simKey is " + mostRecentSimulationKey);
			return null;
		}
	}

    public List<SimpleJobStatus> query(SimulationTasksServerResource resource, User vcellUser) throws SQLException, DataAccessException {	
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		String userID = vcellUser.getName();
		Long simid = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SIM_ID);
		Long jobid = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_JOB_ID);
		Long taskid = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_TASK_ID);
		String computeHost = resource.getQueryValue(SimulationTasksServerResource.PARAM_COMPUTE_HOST);
		String serverID = resource.getQueryValue(SimulationTasksServerResource.PARAM_SERVER_ID);
		String hasData = resource.getQueryValue(SimulationTasksServerResource.PARAM_HAS_DATA);
		boolean statusWaiting = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_WAITING,false);
		boolean statusQueued = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_QUEUED,false);
		boolean statusDispatched = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_DISPATCHED,false);
		boolean statusRunning = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_RUNNING,false);
		boolean statusCompleted = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_COMPLETED,false);
		boolean statusFailed = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_FAILED,false);
		boolean statusStopped = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_STOPPED,false);
		Long submitLow = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SUBMIT_LOW);
		Long submitHigh = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SUBMIT_HIGH);
		Long startLow = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_START_LOW);
		Long startHigh = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_START_HIGH);
		Long endLow = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_END_LOW);
		Long endHigh = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_END_HIGH);
		Long startRowParam = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_START_ROW);
		int startRow = 1; // default
		if (startRowParam!=null){
			startRow = startRowParam.intValue();
		}
		Long maxRowsParam = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_MAX_ROWS);
		int maxRows = 10; // default
		if (maxRowsParam!=null){
			maxRows = maxRowsParam.intValue();
		}
    	ArrayList<String> conditions = new ArrayList<String>();
    	
    	if (simid!=null){
   			conditions.add(SimulationJobTable.table.simRef.getQualifiedColName() + "=" + simid);
     	}

    	if (jobid!=null){
   			conditions.add(SimulationJobTable.table.jobIndex.getQualifiedColName() + "=" + jobid);
     	}

    	if (taskid!=null){
   			conditions.add(SimulationJobTable.table.taskID.getQualifiedColName() + "=" + taskid);
     	}

    	if (computeHost != null && computeHost.length()>0){
     		conditions.add("lower(" + SimulationJobTable.table.computeHost.getQualifiedColName() + ")='" + computeHost.toLowerCase() + "'");
    	}

    	if (serverID!=null && serverID.length()>0){
    		conditions.add("lower(" + SimulationJobTable.table.serverID.getQualifiedColName() + ")='" + serverID + "'");
    	}
    	
    	if (hasData!=null){
    		if (hasData.equalsIgnoreCase("yes") || hasData.equalsIgnoreCase("y") || hasData.equalsIgnoreCase("true") || hasData.equalsIgnoreCase("t")){
    			// return only records that have data
    			conditions.add("lower(" + SimulationJobTable.table.hasData.getQualifiedColName() + ")='y'");
    		} else if (hasData.equalsIgnoreCase("no") || hasData.equalsIgnoreCase("n") || hasData.equalsIgnoreCase("false") || hasData.equalsIgnoreCase("f")){
    			// return only records that don't have data
    			conditions.add(SimulationJobTable.table.hasData.getQualifiedColName() + " is null");
    		}
    	} // else all records.
    	
    	if (userID!=null && userID.length()>0){
    		conditions.add(UserTable.table.userid.getQualifiedColName() + "='" + userID + "'");
    	}

/**
 * 		w = WAITING(0,"waiting"),
 *		q = QUEUED(1,"queued"),
 *		d = DISPATCHED(2,"dispatched"),
 *		r = RUNNING(3,"running"),
 *		c = COMPLETED(4,"completed"),
 *		s = STOPPED(5,"stopped"),
 *		f = FAILED(6,"failed");
 *
 */
    	ArrayList<String> statusConditions = new ArrayList<String>();
    	if (statusWaiting){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.WAITING.getDatabaseNumber());
    	}
    	if (statusQueued){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.QUEUED.getDatabaseNumber());
    	}
    	if (statusDispatched){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.DISPATCHED.getDatabaseNumber());
    	}
    	if (statusRunning){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.RUNNING.getDatabaseNumber());
    	}
    	if (statusCompleted){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.COMPLETED.getDatabaseNumber());
    	}
    	if (statusStopped){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.STOPPED.getDatabaseNumber());
    	}
    	if (statusFailed){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.FAILED.getDatabaseNumber());
    	}
    	if (statusConditions.size()>0){
	       	StringBuffer statusConditionsBuffer = new StringBuffer();
	    	for (String statusCondition : statusConditions) {
	    		if (statusConditionsBuffer.length() > 0) {
	    			statusConditionsBuffer.append(" OR ");
	    		}
	    		statusConditionsBuffer.append(statusCondition);
			}
     		conditions.add("(" + statusConditionsBuffer + ")");
    	}
     	
    	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
    	
    	if (submitLow != null){
    		conditions.add("(" + SimulationJobTable.table.submitDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(submitLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (submitHigh != null){
    		conditions.add("(" + SimulationJobTable.table.submitDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(submitHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (startLow != null){
    		conditions.add("(" + SimulationJobTable.table.startDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(startLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (startHigh != null){
    		conditions.add("(" + SimulationJobTable.table.startDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(startHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (endLow != null){
    		conditions.add("(" + SimulationJobTable.table.endDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(endLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (endHigh != null){
    		conditions.add("(" + SimulationJobTable.table.endDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(endHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}

//    	conditions.add("(" + "rownum" + " <= " + maxNumRows + ")");
    	
    	StringBuffer conditionsBuffer = new StringBuffer();
    	for (String condition : conditions) {
    		if (conditionsBuffer.length() > 0) {
    			conditionsBuffer.append(" AND ");
    		}
			conditionsBuffer.append(condition);
		}
    	
    	if (statusConditions.size()==0){
    		// no status conditions wanted ... nothing to query
    		return new ArrayList<SimpleJobStatus>();
    	}else{
	   		List<SimpleJobStatus> resultList = adminDbTopLevel.getSimulationJobStatus(conditionsBuffer.toString(), startRow, maxRows, true);
	   		return resultList;
    	}
    }

	public UserInfo addUser(UserInfo newUserInfo) throws SQLException, ObjectNotFoundException, DataAccessException {
		KeyValue key = adminDbTopLevel.insertUserInfo(newUserInfo,true);
		return adminDbTopLevel.getUserInfo(key,true);

	}

}
