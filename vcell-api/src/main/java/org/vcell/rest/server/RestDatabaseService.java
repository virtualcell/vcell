package org.vcell.rest.server;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.OverrideRepresentation;
import org.vcell.rest.common.SimulationRepresentation;
import org.vcell.rest.common.SimulationStatusRepresentation;
import org.vcell.rest.rpc.RpcDataServerProxy;
import org.vcell.rest.rpc.RpcSimServerProxy;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCInfoContainer;

import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.model.ModelException;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.BioModelTable;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DatabaseServerImpl.OrderBy;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.modeldb.PublicationRep;
import cbit.vcell.modeldb.PublicationTable;
import cbit.vcell.modeldb.ServerDocumentManager;
import cbit.vcell.modeldb.SimContextRep;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationDocumentLink;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataSetMetadata;
import cbit.vcell.simdata.DataSetTimeSeries;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class RestDatabaseService {
	
	private DatabaseServerImpl databaseServerImpl = null;
	private LocalAdminDbServer localAdminDbServer = null;
	ConcurrentHashMap<KeyValue, SimContextRep> scMap = new ConcurrentHashMap<KeyValue, SimContextRep>();
	ConcurrentHashMap<KeyValue, SimulationRep> simMap = new ConcurrentHashMap<KeyValue, SimulationRep>();
	VCMessagingService vcMessagingService = null;
	SimulationDatabaseDirect simulationDatabaseDirect;

	public RestDatabaseService(DatabaseServerImpl databaseServerImpl, LocalAdminDbServer localAdminDbServer, VCMessagingService vcMessagingService) {
		this.databaseServerImpl = databaseServerImpl;
		this.localAdminDbServer = localAdminDbServer;
		this.vcMessagingService = vcMessagingService;
		this.simulationDatabaseDirect = new SimulationDatabaseDirect(databaseServerImpl.getAdminDBTopLevel(), databaseServerImpl, false);

	}
	
	public static class SimulationSaveResponse {
		public final BioModel newBioModel;
		public final Simulation newSimulation;
		public SimulationSaveResponse(BioModel newBioModel, Simulation newSimulation){
			this.newBioModel = newBioModel;
			this.newSimulation = newSimulation;
		}
	}
	public VCInfoContainer getVCInfoContainer(User vcellUser) throws DataAccessException{
		return databaseServerImpl.getVCInfoContainer(vcellUser);
	}
	public BigString getBioModelXML(KeyValue bmKey,User vcellUser) throws DataAccessException{
		return databaseServerImpl.getBioModelXML(vcellUser, bmKey);
	}
	public BigString getMathModelXML(KeyValue mmKey,User vcellUser) throws DataAccessException{
		return databaseServerImpl.getMathModelXML(vcellUser, mmKey);
	}
	public SimulationStatus[] getSimulationStatus(KeyValue[] simulationKeys,User vcellUser) throws DataAccessException{
		return simulationDatabaseDirect.getSimulationStatus(simulationKeys);
	}
	
	public SimulationSaveResponse saveSimulation(BiomodelSimulationSaveServerResource resource, User vcellUser, List<OverrideRepresentation> overrideRepresentations) throws PermissionException, ObjectNotFoundException, DataAccessException, SQLException, XmlParseException, PropertyVetoException, MappingException, ExpressionException{
		String simId = resource.getAttribute(VCellApiApplication.SIMULATIONID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
		}
		boolean myModel = simRep.getOwner().compareEqual(vcellUser);
		// get the bioModel
		String biomodelId = resource.getAttribute(VCellApiApplication.BIOMODELID);
		KeyValue biomodelKey = new KeyValue(biomodelId);
		BigString bioModelXML = this.databaseServerImpl.getBioModelXML(vcellUser, biomodelKey);
		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
		// copy the simulation as new
		Simulation origSimulation = null;
		for (Simulation sim : bioModel.getSimulations()){
			if (sim.getKey().equals(simKey)){
				origSimulation = sim;
			}
		}
		if (origSimulation==null){
			throw new RuntimeException("cannot find original Simulation");
		}
		
		SimulationContext simContext = bioModel.getSimulationContext(origSimulation);
		Simulation newUnsavedSimulation = simContext.copySimulation(origSimulation);
		
		// make appropriate changes
		// MATH OVERRIDES
		MathOverrides mathOverrides = new MathOverrides(newUnsavedSimulation);
		for (OverrideRepresentation overrideRep : overrideRepresentations){
			overrideRep.applyMathOverrides(mathOverrides);
		}
		newUnsavedSimulation.setMathOverrides(mathOverrides);
		
		// save bioModel
		String editedBioModelXML = XmlHelper.bioModelToXML(bioModel);
		ServerDocumentManager serverDocumentManager = new ServerDocumentManager(this.databaseServerImpl);
		String modelName = bioModel.getName();
		if (!myModel){
			modelName = modelName+"_"+Math.abs(new Random().nextInt());
		}
		String newBioModelXML = serverDocumentManager.saveBioModel(new QueryHashtable(), vcellUser, editedBioModelXML, modelName, null);
		BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(newBioModelXML));
		Simulation savedSimulation = null;
		for (Simulation sim : savedBioModel.getSimulations()){
			if (sim.getName().equals(newUnsavedSimulation.getName())){
				savedSimulation = sim;
			}
		}
		if (savedSimulation==null){
			throw new RuntimeException("cannot find new Simulation");
		}
		return new SimulationSaveResponse(savedBioModel, savedSimulation);
	}

	public SimulationRep startSimulation(BiomodelSimulationStartServerResource resource, User vcellUser) throws PermissionException, ObjectNotFoundException, DataAccessException, SQLException{
		String simId = resource.getAttribute(VCellApiApplication.SIMULATIONID);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
		}
		User owner = simRep.getOwner();
		if (!owner.compareEqual(vcellUser)){
			throw new PermissionException("not authorized to start simulation");
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
			RpcSimServerProxy rpcSimServerProxy = new RpcSimServerProxy(userLoginInfo, rpcSession);
			VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
			rpcSimServerProxy.startSimulation(vcellUser, vcSimID, simRep.getScanCount());
			return simRep;
		}finally{
			rpcSession.close();
		}
	}

	public SimulationRep stopSimulation(BiomodelSimulationStopServerResource resource, User vcellUser) throws PermissionException, ObjectNotFoundException, DataAccessException, SQLException{
		String simId = resource.getAttribute(VCellApiApplication.SIMULATIONID);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
		}
		User owner = simRep.getOwner();
		if (!owner.compareEqual(vcellUser)){
			throw new PermissionException("not authorized to stop simulation");
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
			RpcSimServerProxy rpcSimServerProxy = new RpcSimServerProxy(userLoginInfo, rpcSession);
			VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
			rpcSimServerProxy.stopSimulation(vcellUser, vcSimID);
			return simRep;
		}finally{
			rpcSession.close();
		}
	}

	public DataSetMetadata getDataSetMetadata(SimDataServerResource resource, User vcellUser) throws ObjectNotFoundException, DataAccessException, SQLException{
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
		String simId = resource.getAttribute(VCellApiApplication.SIMDATAID);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
		}
		User owner = simRep.getOwner();
		int jobIndex = 0;
		VCMessageSession rpcSession = vcMessagingService.createProducerSession();
		try {
			RpcDataServerProxy rpcDataServerProxy = new RpcDataServerProxy(userLoginInfo, rpcSession);
			VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
			VCDataIdentifier vcdID = new VCSimulationDataIdentifier(vcSimID, jobIndex);
			DataSetMetadata dataSetMetadata = rpcDataServerProxy.getDataSetMetadata(vcdID);
			return dataSetMetadata;
		}finally{
			rpcSession.close();
		}
	}

	public DataSetTimeSeries getDataSetTimeSeries(SimDataValuesServerResource resource, User vcellUser) throws DataAccessException, ObjectNotFoundException, SQLException{
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
		String simId = resource.getAttribute(VCellApiApplication.SIMDATAID);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		String jobIndexString = resource.getAttribute(VCellApiApplication.JOBINDEX);  // resource.getRequestAttributes().get(VCellApiApplication.SIMDATAID);
		KeyValue simKey = new KeyValue(simId);
		SimulationRep simRep = getSimulationRep(simKey);
		if (simRep == null){
			throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
		}
		int jobIndex = Integer.parseInt(jobIndexString);
		String variableNames[] = null; // TODO: pass in variables names from the query parameters.
		User owner = simRep.getOwner();
		VCMessageSession rpcSession = vcMessagingService.createProducerSession();
		try {
			RpcDataServerProxy rpcDataServerProxy = new RpcDataServerProxy(userLoginInfo, rpcSession);
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
		OrderBy orderBy = OrderBy.date_desc; // default
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
	
	public String query(BiomodelVCMLServerResource resource, User vcellUser) throws SQLException, DataAccessException {			
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		String bioModelID = (String)resource.getRequestAttributes().get(VCellApiApplication.BIOMODELID);
		KeyValue bioModelKey = new KeyValue(bioModelID);
		BigString vcmlBigString = databaseServerImpl.getBioModelXML(vcellUser, bioModelKey);
		return vcmlBigString.toString();
	}
	
	public String query(BiomodelDiagramServerResource resource, User vcellUser) throws SQLException, DataAccessException {			
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		String bioModelID = (String)resource.getRequestAttributes().get(VCellApiApplication.BIOMODELID);
		KeyValue bioModelKey = new KeyValue(bioModelID);
		BigString vcmlBigString = databaseServerImpl.getBioModelXML(vcellUser, bioModelKey);
		return vcmlBigString.toString();
	}
	
	public BioModelRep query(BiomodelServerResource resource, User vcellUser) throws SQLException, ObjectNotFoundException, DataAccessException {	
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		String bioModelID = (String)resource.getRequestAttributes().get(VCellApiApplication.BIOMODELID);
		return getBioModelRep(new KeyValue(bioModelID), vcellUser);
	}
	
	public PublicationRep query(PublicationServerResource resource, User vcellUser) throws NumberFormatException, ObjectNotFoundException, SQLException, DataAccessException {
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		String pubID = (String)resource.getRequestAttributes().get(VCellApiApplication.PUBLICATIONID);
		return getPublicationRep(new KeyValue(pubID), vcellUser);
	}


	
	public BioModelRep getBioModelRep(KeyValue bmKey, User vcellUser) throws SQLException, ObjectNotFoundException, DataAccessException {	
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		ArrayList<String> conditions = new ArrayList<String>();
		if (bmKey != null){
			conditions.add("(" + BioModelTable.table.id.getQualifiedColName() + " = " + bmKey.toString() + ")");		
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
			throw new ObjectNotFoundException("failed to get biomodel");
		}
		return bioModelReps[0];
	}
	
	public PublicationRep getPublicationRep(KeyValue pubKey, User vcellUser) throws SQLException, ObjectNotFoundException, DataAccessException {	
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		ArrayList<String> conditions = new ArrayList<String>();
		if (pubKey != null){
			conditions.add("(" + PublicationTable.table.id.getQualifiedColName() + " = " + pubKey.toString() + ")");		
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
		PublicationRep[] publicationReps = databaseServerImpl.getPublicationReps(vcellUser, conditionsBuffer.toString(), null);
		if (publicationReps==null || publicationReps.length!=1){
			throw new ObjectNotFoundException("failed to get publication");
		}
		return publicationReps[0];
	}
	
	public SimulationRepresentation query(BiomodelSimulationServerResource resource, User vcellUser) throws SQLException, DataAccessException, ExpressionException, XmlParseException, MappingException, MathException, MatrixException, ModelException {	
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
			//
			// try to determine if the current credentials are insufficient, try to fetch BioModel again with administrator privilege.
			//
			User adminUser = new User(PropertyLoader.ADMINISTRATOR_ACCOUNT,new KeyValue(PropertyLoader.ADMINISTRATOR_ID));
			BioModelRep[] allBioModelReps = databaseServerImpl.getBioModelReps(adminUser, conditionsBuffer.toString(), null, startRow, 1);
			if (allBioModelReps!=null && allBioModelReps.length>=0){
				throw new PermissionException("insufficient privilege to retrive BioModel "+bioModelID);
			}else{
				throw new RuntimeException("failed to get biomodel");
			}
		}
		
		String simulationId = (String)resource.getRequestAttributes().get(VCellApiApplication.SIMULATIONID);
		if (simulationId == null){
			throw new RuntimeException(VCellApiApplication.SIMULATIONID+" not specified");
		}
		SimulationRep simRep = getSimulationRep(new KeyValue(simulationId));
		BigString bioModelXML = databaseServerImpl.getBioModelXML(vcellUser, bioModelReps[0].getBmKey());
		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
		return new SimulationRepresentation(simRep, bioModel);
	}
	
	public SimContextRep getSimContextRep(KeyValue key) throws DataAccessException, SQLException{
		SimContextRep simContextRep = scMap.get(key);
		if (simContextRep!=null){
			return simContextRep;
		}else{
			System.out.println("getting simulation context rep for scKey = "+key);
			simContextRep = databaseServerImpl.getSimContextRep(key);
			if (simContextRep!=null){
				System.out.println("found simulation context key = " + key + " number of cached simContexts is " + simMap.size());
				scMap.put(key, simContextRep);
			}else{
				System.out.println("couldn't find simulation key = " + key);
			}
			return simContextRep;
		}
	}
	
	
	public SimulationRep getSimulationRep(KeyValue key) throws DataAccessException, SQLException{
		SimulationRep simulationRep = simMap.get(key);
		if (simulationRep!=null){
			return simulationRep;
		}else{
			System.out.println("getting simulation rep for simKey = "+key);
			simulationRep = databaseServerImpl.getSimulationRep(key);
			if (simulationRep!=null){
				System.out.println("found simulation key = " + key + " number of cached simulations is " + simMap.size());
				simMap.put(key, simulationRep);
			}else{
				System.out.println("couldn't find simulation key = " + key);
			}
			return simulationRep;
		}
	}

    public SimpleJobStatus[] query(SimulationTasksServerResource resource, User vcellUser) throws SQLException, DataAccessException {	
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		String userID = vcellUser.getName();
		SimpleJobStatusQuerySpec simQuerySpec = new SimpleJobStatusQuerySpec();
		simQuerySpec.userid = userID;
		simQuerySpec.simId = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SIM_ID);
		simQuerySpec.jobId = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_JOB_ID);
		simQuerySpec.taskId = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_TASK_ID);
		simQuerySpec.computeHost = resource.getQueryValue(SimulationTasksServerResource.PARAM_COMPUTE_HOST);
		simQuerySpec.serverId = resource.getQueryValue(SimulationTasksServerResource.PARAM_SERVER_ID);
		String hasData = resource.getQueryValue(SimulationTasksServerResource.PARAM_HAS_DATA);
		if (hasData!=null && hasData.equals("yes")){
			simQuerySpec.hasData = true;
		}else if (hasData!=null && hasData.equals("no")){
			simQuerySpec.hasData = false;
		}else{
			simQuerySpec.hasData = null;
		}
		simQuerySpec.waiting = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_WAITING,false);
		simQuerySpec.queued = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_QUEUED,false);
		simQuerySpec.dispatched = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_DISPATCHED,false);
		simQuerySpec.running = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_RUNNING,false);
		simQuerySpec.completed = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_COMPLETED,false);
		simQuerySpec.failed = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_FAILED,false);
		simQuerySpec.stopped = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_STOPPED,false);
		simQuerySpec.submitLowMS = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SUBMIT_LOW);
		simQuerySpec.submitHighMS = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SUBMIT_HIGH);
		simQuerySpec.startLowMS = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_START_LOW);
		simQuerySpec.startHighMS = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_START_HIGH);
		simQuerySpec.endLowMS = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_END_LOW);
		simQuerySpec.endHighMS = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_END_HIGH);
		Long startRowParam = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_START_ROW);
		simQuerySpec.startRow = 1; // default
		if (startRowParam!=null){
			simQuerySpec.startRow = startRowParam.intValue();
		}
		Long maxRowsParam = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_MAX_ROWS);
		simQuerySpec.maxRows = 10; // default
		if (maxRowsParam!=null){
			simQuerySpec.maxRows = maxRowsParam.intValue();
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
			RpcSimServerProxy rpcSimServerProxy = new RpcSimServerProxy(userLoginInfo, rpcSession);
			SimpleJobStatus[] simpleJobStatusArray = rpcSimServerProxy.getSimpleJobStatus(vcellUser, simQuerySpec);
			return simpleJobStatusArray;
		}finally{
			rpcSession.close();
		}
    }

    public SimulationStatusRepresentation[] query(SimulationStatusServerResource resource, User vcellUser) throws SQLException, DataAccessException {	
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		String userID = vcellUser.getName();
		SimpleJobStatusQuerySpec simQuerySpec = new SimpleJobStatusQuerySpec();
		simQuerySpec.userid = userID;
		simQuerySpec.simId = resource.getLongQueryValue(SimulationStatusServerResource.PARAM_SIM_ID);
		String hasData = resource.getQueryValue(SimulationStatusServerResource.PARAM_HAS_DATA);
		if (hasData!=null && hasData.equals("yes")){
			simQuerySpec.hasData = true;
		}else if (hasData!=null && hasData.equals("no")){
			simQuerySpec.hasData = false;
		}else{
			simQuerySpec.hasData = null;
		}
		simQuerySpec.waiting = resource.getBooleanQueryValue(SimulationStatusServerResource.PARAM_STATUS_ACTIVE,false);
		simQuerySpec.queued = resource.getBooleanQueryValue(SimulationStatusServerResource.PARAM_STATUS_ACTIVE,false);
		simQuerySpec.dispatched = resource.getBooleanQueryValue(SimulationStatusServerResource.PARAM_STATUS_ACTIVE,false);
		simQuerySpec.running = resource.getBooleanQueryValue(SimulationStatusServerResource.PARAM_STATUS_ACTIVE,false);
		simQuerySpec.completed = resource.getBooleanQueryValue(SimulationStatusServerResource.PARAM_STATUS_COMPLETED,false);
		simQuerySpec.failed = resource.getBooleanQueryValue(SimulationStatusServerResource.PARAM_STATUS_FAILED,false);
		simQuerySpec.stopped = resource.getBooleanQueryValue(SimulationStatusServerResource.PARAM_STATUS_STOPPED,false);
		simQuerySpec.submitLowMS = resource.getLongQueryValue(SimulationStatusServerResource.PARAM_SUBMIT_LOW);
		simQuerySpec.submitHighMS = resource.getLongQueryValue(SimulationStatusServerResource.PARAM_SUBMIT_HIGH);
		simQuerySpec.startLowMS = resource.getLongQueryValue(SimulationStatusServerResource.PARAM_START_LOW);
		simQuerySpec.startHighMS = resource.getLongQueryValue(SimulationStatusServerResource.PARAM_START_HIGH);
		simQuerySpec.endLowMS = resource.getLongQueryValue(SimulationStatusServerResource.PARAM_END_LOW);
		simQuerySpec.endHighMS = resource.getLongQueryValue(SimulationStatusServerResource.PARAM_END_HIGH);
		Long startRowParam = resource.getLongQueryValue(SimulationStatusServerResource.PARAM_START_ROW);
		simQuerySpec.startRow = 1; // default
		if (startRowParam!=null){
			simQuerySpec.startRow = startRowParam.intValue();
		}
		Long maxRowsParam = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_MAX_ROWS);
		simQuerySpec.maxRows = 10; // default
		if (maxRowsParam!=null){
			simQuerySpec.maxRows = maxRowsParam.intValue();
		}

		SimulationStatus[] simStatuses = null;
		HashMap<KeyValue,SimulationDocumentLink> simDocLinks = new HashMap<KeyValue,SimulationDocumentLink>();

		//
		// ask server for simJobStatuses with above query spec.
		// find set of simulation IDs from the result set of simJobStatus
		// ask server for simulationStatuses from list of sim IDs.
		//
		VCMessageSession rpcSession = vcMessagingService.createProducerSession();
		try {
			UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
			try {
				userLoginInfo.setUser(vcellUser);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DataAccessException(e.getMessage());
			}
			RpcSimServerProxy rpcSimServerProxy = new RpcSimServerProxy(userLoginInfo, rpcSession);
			SimpleJobStatus[] simpleJobStatusArray = rpcSimServerProxy.getSimpleJobStatus(vcellUser, simQuerySpec);
			// gather unique simIDs and go back and ask server for SimulationStatuses
			for (SimpleJobStatus simpleJobStatus : simpleJobStatusArray){
				KeyValue simulationKey = simpleJobStatus.jobStatus.getVCSimulationIdentifier().getSimulationKey();
				SimulationDocumentLink simulationDocumentLink = simpleJobStatus.simulationDocumentLink;
				simDocLinks.put(simulationKey,simulationDocumentLink);
			}
			KeyValue[] simKeys = simDocLinks.keySet().toArray(new KeyValue[0]);
			if (simKeys.length>0){
				simStatuses = rpcSimServerProxy.getSimulationStatus(vcellUser, simKeys);
			}
		}finally{
			rpcSession.close();
		}
		
		ArrayList<SimulationStatusRepresentation> simStatusReps = new ArrayList<SimulationStatusRepresentation>();
		for (int i=0; simStatuses!=null && i<simStatuses.length; i++){
			KeyValue simulationKey = simStatuses[i].getVCSimulationIdentifier().getSimulationKey();
			SimulationRep simRep = getSimulationRep(simulationKey);
			try {
				SimulationRepresentation simRepresentation = new SimulationRepresentation(simRep, simDocLinks.get(simulationKey));
				simStatusReps.add(new SimulationStatusRepresentation(simRepresentation,simStatuses[i]));
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
			}
		}
		return simStatusReps.toArray(new SimulationStatusRepresentation[0]);
    }

	public UserInfo addUser(UserInfo newUserInfo) throws SQLException, ObjectNotFoundException, DataAccessException, UseridIDExistsException {
		return localAdminDbServer.insertUserInfo(newUserInfo);
	}

	public PublicationRep[] query(PublicationsServerResource resource, User vcellUser) throws SQLException, DataAccessException {
		if (vcellUser==null){
			vcellUser = VCellApiApplication.DUMMY_USER;
		}
		Long pubID = resource.getLongQueryValue(PublicationsServerResource.PARAM_PUB_ID);
		String orderByParam = resource.getQueryValue(PublicationsServerResource.PARAM_ORDERBY); // it is ok if the orderBy is null;
		ArrayList<String> conditions = new ArrayList<String>();
		
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
		
		if (pubID != null){
			conditions.add("(" + PublicationTable.table.id.getQualifiedColName() + " = " + pubID + ")");		
		}

	
		StringBuffer conditionsBuffer = new StringBuffer();
		for (String condition : conditions) {
			if (conditionsBuffer.length() > 0) {
				conditionsBuffer.append(" AND ");
			}
			conditionsBuffer.append(condition);
		}
		OrderBy orderBy = OrderBy.year_desc; // default
		if (orderByParam!=null){
			if (orderByParam.equals(BiomodelsServerResource.PARAM_ORDERBY_YEAR_ASC)){
				orderBy = OrderBy.year_asc;
			}else if (orderByParam.equals(BiomodelsServerResource.PARAM_ORDERBY_YEAR_DESC)){
				orderBy = OrderBy.year_desc;
			}else if (orderByParam.equals(BiomodelsServerResource.PARAM_ORDERBY_NAME_ASC)){
				orderBy = OrderBy.name_asc;
			}else if (orderByParam.equals(BiomodelsServerResource.PARAM_ORDERBY_NAME_DESC)){
				orderBy = OrderBy.name_desc;
			}
		}
		PublicationRep[] publicationReps = databaseServerImpl.getPublicationReps(vcellUser, conditionsBuffer.toString(), orderBy);
	   	return publicationReps;
	}

	public void sendLostPassword(String userid) throws DataAccessException {
		localAdminDbServer.sendLostPassword(userid);
	}

}
