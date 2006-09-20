package cbit.vcell.modeldb;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.ObjectNotFoundException;
import cbit.util.PermissionException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.util.Version;
import cbit.util.VersionFlag;
import cbit.util.Versionable;
import cbit.vcell.model.Feature;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.Structure;
import java.beans.*;
import cbit.vcell.math.BoundaryConditionType;
import java.sql.*;
import java.sql.Statement;

import org.vcell.model.Activator;
import org.vcell.model.analysis.IAnalysisTask;
import org.vcell.model.analysis.IAnalysisTaskFactory;

import cbit.sql.*;
import cbit.vcell.parser.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.server.*;
import cbit.vcell.mapping.*;
import cbit.vcell.model.Model;
/**
 * This type was created in VisualAge.
 */
public class SimulationContextDbDriver extends DbDriver {
	public static final SimContextTable simContextTable = SimContextTable.table;
	public static final StructureMappingTable structureMappingTable = StructureMappingTable.table;
	public static final SpeciesContextSpecTable speciesContextSpecTable = SpeciesContextSpecTable.table;
	public static final ReactionSpecTable reactionSpecTable = ReactionSpecTable.table;
	public static final UserTable userTable = UserTable.table;
	public static final StimulusTable stimulusTable = StimulusTable.table;
	private GeomDbDriver geomDB = null;
	private ModelDbDriver modelDB = null;
	private ReactStepDbDriver reactStepDB = null;
	private MathDescriptionDbDriver mathDescDB = null;

/**
 * SimContextDbDriver constructor comment.
 * @param connectionFactory cbit.sql.ConnectionFactory
 * @param sessionLog cbit.vcell.server.SessionLog
 */
public SimulationContextDbDriver(DBCacheTable argdbc,GeomDbDriver argGeomDB,ModelDbDriver argModelDB,
		MathDescriptionDbDriver argMathDescDB,ReactStepDbDriver argReactStepDB, SessionLog sessionLog) {
	super(argdbc,sessionLog);
	this.geomDB = argGeomDB;
	this.modelDB = argModelDB;
	this.mathDescDB = argMathDescDB;
	this.reactStepDB = argReactStepDB;
}


/**
 * This method was created in VisualAge.
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignAnalysisTasksSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	AnalysisTaskXMLTable analysisTaskTable = AnalysisTaskXMLTable.table;
	
	sql = 	" SELECT " + analysisTaskTable.analysisTaskXML.toString() + 
			" FROM " + analysisTaskTable.getTableName() + 
			" WHERE " + analysisTaskTable.simContextRef + " = " + simContextKey +
			" ORDER BY " + analysisTaskTable.insertDate; // order-by to maintain order ... not really necessary
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			String analysisXML = (String)getLOB(rset,analysisTaskTable.analysisTaskXML.toString());
			org.jdom.Element rootElement = cbit.util.xml.XmlUtil.stringToXML(analysisXML,null);
			IAnalysisTaskFactory[] analysisTaskFactories = Activator.getDefault().getAnalysisTaskFactories(rootElement.getName());
			if (analysisTaskFactories!=null){
				if (analysisTaskFactories.length > 1){
					System.out.println("SimulationContextDbDriver.assignAnalysisTasks(): multiple plugins availlable for analysis task type "+rootElement.getName()+", taking first one");
				}
				IAnalysisTask analysisTask = analysisTaskFactories[0].create(rootElement,simContext);
				simContext.addAnalysisTask(analysisTask);
			 }
		}
	}catch (PropertyVetoException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	} finally {
		if(stmt != null){
			stmt.close();
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param simContextKey cbit.sql.KeyValue
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignReactionSpecsSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	sql = 	" SELECT " + "*" + 
			" FROM " + reactionSpecTable.getTableName() + 
			" WHERE " + reactionSpecTable.simContextRef + " = " + simContextKey;
//System.out.println("SimulationContextDbDriver.assignReactionSpecsSQL(), sql = "+sql);
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			KeyValue reactionStepRef = new KeyValue(rset.getBigDecimal(reactionSpecTable.reactionStepRef.toString()));
			int mapping = rset.getInt(reactionSpecTable.mapping.toString());

			//
			ReactionSpec[] reactionSpecs = simContext.getReactionContext().getReactionSpecs();
			for (int i=0;i<reactionSpecs.length;i++){
				if (reactionSpecs[i].getReactionStep().getKey().compareEqual(reactionStepRef)) {
					try {
						reactionSpecs[i].setReactionMapping(mapping);
					} catch (Exception e) {
						throw new DataAccessException("Error setting ReactionSpec 'mapping' for SimulationContext:"+simContext.getVersion().getName()+" id="+simContextKey);
					}
					break;
				}
			}
		}
	} finally {
		stmt.close();
	}
}


/**
 * This method was created in VisualAge.
 * @param simContextKey cbit.sql.KeyValue
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignSpeciesContextSpecsSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	sql = 	" SELECT " + "*" + 
			" FROM " + speciesContextSpecTable.getTableName() + 
			" WHERE " + speciesContextSpecTable.simContextRef + " = " + simContextKey;
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			KeyValue speciesContextRef = new KeyValue(rset.getBigDecimal(speciesContextSpecTable.specContextRef.toString()));
			//KeyValue simContextRef = new KeyValue(rset.getBigDecimal(speciesContextSpecTable.simContextRef.toString()));
			boolean bEnableDiffusing = rset.getBoolean(speciesContextSpecTable.bEnableDif.toString());
			boolean bForceConstant = rset.getBoolean(speciesContextSpecTable.bForceConst.toString());
			boolean bForceIndep = rset.getBoolean(speciesContextSpecTable.bForceIndep.toString());
			String initCondString = rset.getString(speciesContextSpecTable.initCondExp.toString());
			String diffRateString = rset.getString(speciesContextSpecTable.diffRateExp.toString());
			String boundaryXmString = rset.getString(speciesContextSpecTable.boundaryXmExp.toString());
			if (rset.wasNull()){
				boundaryXmString = null;
			}
			
			String boundaryXpString = rset.getString(speciesContextSpecTable.boundaryXpExp.toString());
			if (rset.wasNull()){
				boundaryXpString = null;
			}
			
			String boundaryYmString = rset.getString(speciesContextSpecTable.boundaryYmExp.toString());
			if (rset.wasNull()){
				boundaryYmString = null;
			}
			
			String boundaryYpString = rset.getString(speciesContextSpecTable.boundaryYpExp.toString());
			if (rset.wasNull()){
				boundaryYpString = null;
			}
			
			String boundaryZmString = rset.getString(speciesContextSpecTable.boundaryZmExp.toString());
			if (rset.wasNull()){
				boundaryZmString = null;
			}
			
			String boundaryZpString = rset.getString(speciesContextSpecTable.boundaryZpExp.toString());
			if (rset.wasNull()){
				boundaryZpString = null;
			}
			
			//
			SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
			for (int i=0;i<speciesContextSpecs.length;i++){
				SpeciesContextSpec scs = speciesContextSpecs[i];
				if (scs.getSpeciesContext().getKey().compareEqual(speciesContextRef)) {
					try {
						scs.setEnableDiffusing(bEnableDiffusing);
						scs.setConstant(bForceConstant);
						scs.getInitialConditionParameter().setExpression(new Expression(initCondString));
						scs.getDiffusionParameter().setExpression(new Expression(diffRateString));
						if (boundaryXmString!=null){
							scs.getBoundaryXmParameter().setExpression(new Expression(boundaryXmString));
						}else{
							scs.getBoundaryXmParameter().setExpression(null);
						}
						if (boundaryXpString!=null){
							scs.getBoundaryXpParameter().setExpression(new Expression(boundaryXpString));
						}else{
							scs.getBoundaryXpParameter().setExpression(null);
						}
						if (boundaryYmString!=null){
							scs.getBoundaryYmParameter().setExpression(new Expression(boundaryYmString));
						}else{
							scs.getBoundaryYmParameter().setExpression(null);
						}
						if (boundaryYpString!=null){
							scs.getBoundaryYpParameter().setExpression(new Expression(boundaryYpString));
						}else{
							scs.getBoundaryYpParameter().setExpression(null);
						}
						if (boundaryZmString!=null){
							scs.getBoundaryZmParameter().setExpression(new Expression(boundaryZmString));
						}else{
							scs.getBoundaryZmParameter().setExpression(null);
						}
						if (boundaryZpString!=null){
							scs.getBoundaryZpParameter().setExpression(new Expression(boundaryZpString));
						}else{
							scs.getBoundaryZpParameter().setExpression(null);
						}
					} catch (Exception e) {
						throw new DataAccessException("Error setting SpeciesContextSpec info for SimulationContext:"+simContext.getVersion().getName()+" id="+simContextKey);
					}
				break;
				}
			}
		}
	} finally {
		stmt.close();
	}
}


/**
 * This method was created in VisualAge.
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignStimuliSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	sql = 	" SELECT " + "*" + 
			" FROM " + stimulusTable.getTableName() + 
			" WHERE " + stimulusTable.simContextRef + " = " + simContextKey;
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			try {
				KeyValue key = new KeyValue(rset.getBigDecimal(stimulusTable.id.toString()));			
				KeyValue structureRef = new KeyValue(rset.getBigDecimal(stimulusTable.structRef.toString()));			
				KeyValue simContextRef = new KeyValue(rset.getBigDecimal(stimulusTable.simContextRef.toString()));			
				//
				// lookup structure from SimulationContext by its key
				//
				// DBCache will not always give same instance consistently (usually this is
				// fixed up later in the ReferenceResolver at the Client).
				//
				Structure theStructure = null;
				Structure structureArray[] = simContext.getModel().getStructures();
				for (int i=0;i<structureArray.length;i++){
					Structure structure = structureArray[i];
					if (structure.getKey().compareEqual(structureRef)) {
						theStructure = structure;
						break;
					}
				}
				String name = rset.getString(stimulusTable.name.toString());			
				if (rset.wasNull()){
					name = null;
				}
				int stimulusType = rset.getInt(stimulusTable.stimulusType.toString());
				Expression exp = null;
				String expString = rset.getString(stimulusTable.expression.toString());
				if (rset.wasNull()){
					exp = null;
				}else{
					exp = new Expression(expString);
				}
				double posX = rset.getBigDecimal(stimulusTable.positionX.toString()).doubleValue();
				double posY = rset.getBigDecimal(stimulusTable.positionY.toString()).doubleValue();
				double posZ = rset.getBigDecimal(stimulusTable.positionZ.toString()).doubleValue();

				cbit.vcell.math.CommentStringTokenizer paramsCST = null;
				String paramsS = rset.getString(StimulusTable.table.params.toString());
				if(!rset.wasNull()){
					paramsCST =
						new cbit.vcell.math.CommentStringTokenizer(
							cbit.util.TokenMangler.getSQLRestoredString(paramsS)
						);
				}
				
				if (stimulusType == StimulusTable.GROUND_ELECTRODE){
					//
					// an Electrode (ground)
					//
					Electrode groundElectrode = new Electrode((Feature)theStructure,new cbit.vcell.geometry.Coordinate(posX,posY,posZ));
					simContext.setGroundElectrode(groundElectrode);
				}else if (stimulusType == StimulusTable.CURRENT_CLAMP_STIMULUS){
					Electrode electrode = new Electrode((Feature)theStructure,new cbit.vcell.geometry.Coordinate(posX,posY,posZ));
					CurrentClampStimulus stimulus = new CurrentClampStimulus(electrode,name,exp,simContext);
					stimulus.parameterVCMLSet(paramsCST);
					ElectricalStimulus newStimuli[] = (ElectricalStimulus[])cbit.util.BeanUtils.addElement(simContext.getElectricalStimuli(),stimulus);
					simContext.setElectricalStimuli(newStimuli);
				}else if (stimulusType == StimulusTable.VOLTAGE_CLAMP_STIMULUS){
					Electrode electrode = new Electrode((Feature)theStructure,new cbit.vcell.geometry.Coordinate(posX,posY,posZ));
					VoltageClampStimulus stimulus = new VoltageClampStimulus(electrode,name,exp,simContext);
					stimulus.parameterVCMLSet(paramsCST);
					ElectricalStimulus newStimuli[] = (ElectricalStimulus[])cbit.util.BeanUtils.addElement(simContext.getElectricalStimuli(),stimulus);
					simContext.setElectricalStimuli(newStimuli);
				}else{
					throw new DataAccessException("unknown stimulusType <"+stimulusType+">");
				}
				
			}catch (ExpressionException e){
				log.exception(e);
			}catch (PropertyVetoException e){
				log.exception(e);
			}
		}
	} finally {
		stmt.close();
	}
}


/**
 * This method was created in VisualAge.
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignStructureMappingsSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	sql = 	" SELECT " + "*" + 
			" FROM " + structureMappingTable.getTableName() + 
			" WHERE " + structureMappingTable.simContextRef + " = " + simContextKey;
	Statement stmt = con.createStatement();
	try {
//log.print(sql);
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			boolean bResolved = rset.getBoolean(structureMappingTable.bResolved.toString());
			KeyValue subVolumeRef = new KeyValue(rset.getBigDecimal(structureMappingTable.subVolumeRef.toString()));
			KeyValue structureRef = new KeyValue(rset.getBigDecimal(structureMappingTable.structRef.toString()));			

			//
			// lookup structure and subVolume from SimulationContext by their keys
			//
			// DBCache will not always give same instance consistently (usually this is
			// fixed up later in the ReferenceResolver at the Client).
			//
			Structure theStructure = null;
			Structure structureArray[] = simContext.getModel().getStructures();
			for (int i=0;i<structureArray.length;i++){
				Structure structure = structureArray[i];
				if (structure.getKey().compareEqual(structureRef)) {
					theStructure = structure;
					break;
				}
			}
			cbit.vcell.geometry.SubVolume theSubVolume = null;
			cbit.vcell.geometry.SubVolume subVolumes[] = simContext.getGeometry().getGeometrySpec().getSubVolumes();
			for (int i=0;i<subVolumes.length;i++){
				if (subVolumes[i].getKey().compareEqual(subVolumeRef)){
					theSubVolume = subVolumes[i];
					break;
				}
			}
			if (theStructure == null) {
				throw new DataAccessException("Can't match structure and subvolume");
			}
			
			if (theSubVolume == null) {
				log.alert("Can't match structure and subvolume, inserting Kludge and let reference resolver fix it later ...<<<<<<BAD>>>>>");
				theSubVolume = geomDB.getSubVolume(con,subVolumeRef);
				if (theSubVolume == null){
					throw new DataAccessException("Can't match structure and subvolume, 'even with subvolume fix'");
				}
			}
			
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(theStructure);
			if (sm instanceof FeatureMapping) {
				FeatureMapping fm = (FeatureMapping) sm;
				try {
					fm.setSubVolume(theSubVolume);
					fm.setResolved(bResolved);
				}catch (PropertyVetoException e){
					log.exception(e);
					throw new DataAccessException(e.getMessage());
				}
				String boundaryTypeXmString = rset.getString(structureMappingTable.boundaryTypeXm.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeXm(new BoundaryConditionType(boundaryTypeXmString));
				}
				String boundaryTypeXpString = rset.getString(structureMappingTable.boundaryTypeXp.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeXp(new BoundaryConditionType(boundaryTypeXpString));
				}
				String boundaryTypeYmString = rset.getString(structureMappingTable.boundaryTypeYm.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeYm(new BoundaryConditionType(boundaryTypeYmString));
				}
				String boundaryTypeYpString = rset.getString(structureMappingTable.boundaryTypeYp.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeYp(new BoundaryConditionType(boundaryTypeYpString));
				}
				String boundaryTypeZmString = rset.getString(structureMappingTable.boundaryTypeZm.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeZm(new BoundaryConditionType(boundaryTypeZmString));
				}
				String boundaryTypeZpString = rset.getString(structureMappingTable.boundaryTypeZp.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeZp(new BoundaryConditionType(boundaryTypeZpString));
				}
			} else if (sm instanceof MembraneMapping) {
				MembraneMapping mm = (MembraneMapping) sm;
				String surfToVolString = rset.getString(structureMappingTable.surfToVolExp.toString());
				if (!rset.wasNull()) {
					try {
						mm.getSurfaceToVolumeParameter().setExpression(new Expression(surfToVolString));
					} catch (ExpressionException e) {
						e.printStackTrace(System.out);
						throw new DataAccessException("parse error in surfaceToVol expression: " + e.getMessage());
					} catch (PropertyVetoException e) {
						e.printStackTrace(System.out);
						throw new DataAccessException("error setting surfaceToVol expression: " + e.getMessage());
					}
				}
				String volFractString = rset.getString(structureMappingTable.volFractExp.toString());
				if (!rset.wasNull()) {
					try {
						mm.getVolumeFractionParameter().setExpression(new Expression(volFractString));
					} catch (ExpressionException e) {
						e.printStackTrace(System.out);
						throw new DataAccessException("parse error in volFract expression: " + e.getMessage());
					} catch (PropertyVetoException e) {
						e.printStackTrace(System.out);
						throw new DataAccessException("error setting volFract expression: " + e.getMessage());
					}
				}
				boolean bCalculateVoltage = rset.getBoolean(structureMappingTable.bCalculateVoltage.toString());
				if (!rset.wasNull()) {
					mm.setCalculateVoltage(bCalculateVoltage);
				}
				java.math.BigDecimal specificCapacitance = rset.getBigDecimal(structureMappingTable.specificCap.toString());
				if (!rset.wasNull()) {
					try {
						mm.getSpecificCapacitanceParameter().setExpression(new Expression(specificCapacitance.doubleValue()));
					}catch (ExpressionBindingException e){
						e.printStackTrace(System.out);
						throw new DataAccessException("error setting membrane specific capacitance: "+e.getMessage());
					}catch (PropertyVetoException e){
						e.printStackTrace(System.out);
						throw new DataAccessException("error setting membrane specific capacitance: "+e.getMessage());
					}
				}
				String initialVoltageString = rset.getString(structureMappingTable.initialVoltage.toString());
				if (!rset.wasNull()) {
					try {
						mm.getInitialVoltageParameter().setExpression(new Expression(initialVoltageString));
					}catch (ExpressionException e){
						e.printStackTrace(System.out);
						throw new DataAccessException("database parse error in initial membrane voltage: "+e.getMessage());
					}catch (PropertyVetoException e){
						e.printStackTrace(System.out);
						throw new DataAccessException("error setting initial membrane voltage: "+e.getMessage());
					}
				}
			} else {
				throw new DataAccessException("unknown structureMapping type");
			}
//System.out.println("Structure Key = " + theStructure + " - " + "SubVolume Key " + theSubVolume.getKey());
		}
	} finally {
		stmt.close();
	}
}


/**
 * removeModel method comment.
 */
private void deleteSimContextSQL(Connection con,User user, KeyValue simContextKey) throws SQLException, PermissionException, DataAccessException, DependencyException {
	//log.print("deleteSimContextSQL(user=" + user + ", simContextKey=" + simContextKey + ")");

	//
	// check for external references (from BioModel)
	//
	failOnExternalRefs(con, BioModelSimContextLinkTable.table.simContextRef, BioModelSimContextLinkTable.table, simContextKey,SimContextTable.table);

	
	KeyValue mathKey = getDeletableMathKeyFromSimContext(con,user,simContextKey);
	
	//
	// delete SimulationContext (Model and Geometry link tables are ON DELETE CASCADE)
	//
	String sql;
	sql = DatabasePolicySQL.enforceOwnershipDelete(user,simContextTable,simContextTable.id.getQualifiedColName() + " = " + simContextKey);
	updateCleanSQL(con, sql);

	//
	// delete MathDescription that was owned by the SimulationContext (note: any Simulations must be deleted first)
	//
	try {
		mathDescDB.deleteVersionable(con,user,VersionableType.MathDescription,mathKey);
		log.print("SimulationContextDbDriver.delete("+simContextKey+") deletion of MathDescription("+mathKey+") succeeded");
	}catch (cbit.util.PermissionException e){
		log.alert("SimulationContextDbDriver.delete("+simContextKey+") deletion of MathDescription("+mathKey+") failed: "+e.getMessage());
	}catch (cbit.vcell.server.DependencyException e){
		log.alert("SimulationContextDbDriver.delete("+simContextKey+") deletion of MathDescription("+mathKey+") failed: "+e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
public void deleteVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
				throws DependencyException, ObjectNotFoundException,
						SQLException,DataAccessException,PermissionException {

	deleteVersionableInit(con, user, vType, vKey);
	if (vType.equals(VersionableType.SimulationContext)){
		deleteSimContextSQL(con, user, vKey);
		dbc.remove(vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * getModels method comment.
 */
private KeyValue getDeletableMathKeyFromSimContext(Connection con,User user,KeyValue simContextKey) throws SQLException, DataAccessException {

	KeyValue mathKey = null;
	String sql;
	
	sql = 	" SELECT " + SimContextTable.table.mathRef.getQualifiedColName()  +
			" FROM " + SimContextTable.table.getTableName() + "," + MathDescTable.table.getTableName() +
			" WHERE " + SimContextTable.table.id.getQualifiedColName() + " = " + simContextKey +
			" AND " + SimContextTable.table.mathRef.getQualifiedColName() + " = " + MathDescTable.table.id.getQualifiedColName() +
			" AND " + MathDescTable.table.versionFlag.getQualifiedColName() + " <> " + VersionFlag.Archived.getIntValue() +
			" AND " + MathDescTable.table.ownerRef.getQualifiedColName() + " = " + user.getID();
			
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		if (rset.next()) {
			mathKey = new KeyValue(rset.getBigDecimal(SimContextTable.table.mathRef.getUnqualifiedColName()));
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	return mathKey;
}


/**
 * getModel method comment.
 */
private SimulationContext getSimulationContextSQL(Connection con,User user, KeyValue simContextKey/*, ReactStepDbDriver reactStepDB*/) 
						throws SQLException, DataAccessException, IllegalMappingException, PropertyVetoException {
							
	SimulationContext simContext = null;
		
	String sql;			
	Field[] f = {new cbit.sql.StarField(simContextTable),userTable.userid};
	Table[] t = {simContextTable,userTable};
	String condition =	simContextTable.id.getQualifiedColName() + " = " + simContextKey + 
					" AND " + 
						simContextTable.ownerRef.getQualifiedColName() + " = " + userTable.id.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null);
//System.out.println(sql);

	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()) {
			simContext = simContextTable.getSimContext(con,user,rset, log, geomDB,modelDB,mathDescDB);
		} else {
			throw new ObjectNotFoundException("SimulationContext id=" + simContextKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close();
	}
	assignStimuliSQL(con,simContextKey, simContext);
	assignStructureMappingsSQL(con,simContextKey, simContext);
	assignSpeciesContextSpecsSQL(con,simContextKey, simContext);
	assignReactionSpecsSQL(con,simContextKey, simContext);
	assignAnalysisTasksSQL(con,simContextKey, simContext);
	simContext.refreshDependencies();  // really needed to calculate MembraneMapping parameters that are not stored (inside/outside flux correction factors).
	
	return simContext;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public Versionable getVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
			throws ObjectNotFoundException, SQLException, DataAccessException {
				
	Versionable versionable = (Versionable) dbc.get(vKey);
	if (versionable != null) {
		return versionable;
	} else {
		try {
			if (vType.equals(VersionableType.SimulationContext)){
				versionable = getSimulationContextSQL(con, user, vKey);
			}else{
				throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
			}
		} catch (IllegalMappingException e) {
			throw new DataAccessException(e.getMessage());
		} catch (PropertyVetoException e) {
			throw new DataAccessException(e.getMessage());
		}
		dbc.putUnprotected(versionable.getVersion().getVersionKey(),versionable);
	}
	return versionable;
}


/**
 * This method was created in VisualAge.
 */
private void insertAnalysisTasksSQL(Connection con, KeyValue simContextKey, SimulationContext simContext) 
			throws SQLException, DataAccessException {
				
	AnalysisTaskXMLTable analysisTaskXMLTable = AnalysisTaskXMLTable.table;

	IAnalysisTask[] analysisTasks = simContext.getAnalysisTasks();
	//
	// store analysisTasks
	//
	if (analysisTasks != null) {
		for (int i=0;i<analysisTasks.length;i++){
			IAnalysisTask analysisTask = analysisTasks[i];
			org.jdom.Element xmlRootElement = analysisTask.getXML();
			String analysisTaskXML = cbit.util.xml.XmlUtil.xmlToString(xmlRootElement);

			KeyValue newID = getNewKey(con);
			
			updateCleanSQL(con,"INSERT INTO "+analysisTaskXMLTable.getTableName()+
				" VALUES (" + newID +","+simContextKey.toString()+",EMPTY_CLOB(),SYSDATE)");
			
			updateCleanLOB(con,analysisTaskXMLTable.id.toString(),newID,analysisTaskXMLTable.getTableName(),analysisTaskXMLTable.analysisTaskXML.toString(),analysisTaskXML);
		}
	}
}


/**
 * This method was created in VisualAge.
 */
private void insertReactionSpecsSQL(Connection con, KeyValue simContextKey, SimulationContext simContext, Model updatedModel) 
			throws SQLException, DataAccessException {
				
	String sql;
	ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
	for (int i=0;i<reactionSpecs.length;i++){
		//
		KeyValue newReactionSpecKey = getNewKey(con);
		KeyValue reactionStepKey = updatedModel.getReactionStep(reactionSpecs[i].getReactionStep().getName()).getKey();
		//
		sql = 	"INSERT INTO " + reactionSpecTable.getTableName() + " " + reactionSpecTable.getSQLColumnList() +
				" VALUES " + reactionSpecTable.getSQLValueList(newReactionSpecKey, simContextKey, reactionSpecs[i], reactionStepKey);
System.out.println("SimulationContextDbDriver.insertReactionSpecsSQL(), sql = "+sql);
		updateCleanSQL(con, sql);
	}
}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void insertSimulationContext(InsertHashtable hash, Connection con, User user, SimulationContext simContext,
									KeyValue updatedMathDescKey, Model updatedModel, KeyValue updatedGeometryKey,
									Version newVersion, boolean bVersionChildren) 
						throws SQLException, DependencyException, DataAccessException,RecordChangedException {
							

	////
	//// Insert MathDescription for this SimContext
	////
	//KeyValue mathDescKey = null;
	//MathDescription mathDesc = simContext.getMathDescription();
	//if (mathDesc!=null){
		//Version mathVersion = mathDesc.getVersion();
		//if (mathVersion != null && mathVersion.getVersionKey() != null) {
			//mathDescKey = mathDescDB.updateVersionable(hash, con, user, mathDesc, bVersionChildren);
		//}else{
			//String mathName = mathDesc.getName(); // + "_generated";
			//while (isNameUsed(con,VersionableType.MathDescription,user,mathName)){
				//mathName = cbit.util.TokenMangler.getNextRandomToken(mathName);
			//}
			//mathDescKey = mathDescDB.insertVersionable(hash, con, user, mathDesc, mathName, bVersionChildren);
		//}
	//}

	////
	//// insert model (if needed)
	////
	//Model model = simContext.getReactionContext().getModel();
	//KeyValue modelKey = hash.getDatabaseKey(model);
	//try {
		//if (modelKey==null){
			//Version modelVersion = model.getVersion();
			//if (modelVersion != null && modelVersion.getVersionKey() != null) {
				//modelKey = modelDB.updateVersionable(hash, con, user, model, bVersionChildren);
			//} else {
				//String modelName = model.getName(); // + "_model";
				//while (isNameUsed(con,VersionableType.Model,user,modelName)){
					//modelName = cbit.util.TokenMangler.getNextEnumeratedToken(modelName);
				//}
				//modelKey = modelDB.insertVersionable(hash, con, user, model, modelName, bVersionChildren);
			//}
		//}
	//} catch (RecordChangedException rce) {
		//throw rce;
	//}
	//Model updatedModel = (Model)modelDB.getVersionable(con,user,VersionableType.Model,modelKey);
	////
	//// insert geometry (if needed)
	////
	//Geometry geom = simContext.getGeometryContext().getGeometry();
	//KeyValue geomKey = hash.getDatabaseKey(geom);
	//try {
		//if (geomKey==null){
			//Version geomVersion = geom.getVersion();
			//if (geomVersion != null && geomVersion.getVersionKey() != null) {
				//geomKey = geomDB.updateVersionable(hash, con, user, geom, bVersionChildren);
			//} else {
				//String geomName = geom.getName(); // + "_geometry";
				//while (isNameUsed(con,VersionableType.Geometry,user,geomName)){
					//geomName = cbit.util.TokenMangler.getNextEnumeratedToken(geomName);
				//}
				//geomKey = geomDB.insertVersionable(hash, con, user, geom, geomName, bVersionChildren);
			//}
		//}
	//} catch (RecordChangedException rce) {
		//throw rce;
	//}
	
	insertSimulationContextSQL(con, user, simContext, updatedMathDescKey, updatedModel.getKey(), updatedGeometryKey, newVersion);

	insertStructureMappingsSQL(hash, con, newVersion.getVersionKey(), simContext);   // links to structure (dictionary)
	insertStimuliSQL(hash,con, newVersion.getVersionKey(), simContext); // inserts Stimuli
	insertSpeciesContextSpecsSQL(con, newVersion.getVersionKey(), simContext, updatedModel); // links to speciesContext
	insertReactionSpecsSQL(con, newVersion.getVersionKey(), simContext, updatedModel); // links to reactionSteps
	insertAnalysisTasksSQL(con, newVersion.getVersionKey(), simContext); // inserts AnalysisTasks

	hash.put(simContext,newVersion.getVersionKey());
}


/**
 * This method was created in VisualAge.
 */
private void insertSimulationContextSQL(Connection con, User user,SimulationContext simContext,KeyValue mathDescKey,
										KeyValue modelKey,KeyValue geomKey,Version version)
									throws SQLException,DataAccessException {
										
	String sql = null;
	Object[] o = {simContext,mathDescKey,modelKey,geomKey};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,simContextTable,o,version);
//System.out.println(sql);
	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 * @param simContextKey cbit.sql.KeyValue
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void insertSpeciesContextSpecsSQL(Connection con, KeyValue simContextKey, SimulationContext simContext, Model updatedModel) throws SQLException {
	String sql;
	SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec speciesContextSpec = speciesContextSpecs[i];
		KeyValue scKey = updatedModel.getSpeciesContext(speciesContextSpec.getSpeciesContext().getName()).getKey();
		KeyValue newSpeciesContextSpecKey = getNewKey(con);
		//
		sql = 	"INSERT INTO " + speciesContextSpecTable.getTableName() + " " + speciesContextSpecTable.getSQLColumnList() + 
				" VALUES " + speciesContextSpecTable.getSQLValueList(newSpeciesContextSpecKey, simContextKey, speciesContextSpec, scKey);
//System.out.println(sql);
		updateCleanSQL(con, sql);
	}
}


/**
 * This method was created in VisualAge.
 */
private void insertStimuliSQL(InsertHashtable hash, Connection con, KeyValue simContextKey, SimulationContext simContext) 
			throws SQLException, DataAccessException {
				
	StimulusTable stimulusTable = StimulusTable.table;

	ElectricalStimulus stimuli[] = simContext.getElectricalStimuli();
	//
	// store stimuli
	//
	for (int i=0;i<stimuli.length;i++){
		ElectricalStimulus stimulus = stimuli[i];
		KeyValue newStimuliKey = getNewKey(con);
		//
		String sql = "INSERT INTO " + stimulusTable.getTableName() + " " + stimulusTable.getSQLColumnList() +
					" VALUES " + stimulusTable.getSQLValueList(hash, newStimuliKey, simContextKey, stimulus);
System.out.println(sql);
		updateCleanSQL(con, sql);
	}
	//
	// store ground electrode
	//
	Electrode groundElectrode = simContext.getGroundElectrode();
	if (groundElectrode != null){
		KeyValue newStimuliKey = getNewKey(con);
		//
		String sql = "INSERT INTO " + stimulusTable.getTableName() + " " + stimulusTable.getSQLColumnList() +
					" VALUES " + stimulusTable.getSQLValueList(hash, newStimuliKey, simContextKey, groundElectrode);
System.out.println(sql);
		updateCleanSQL(con, sql);
	}
}


/**
 * This method was created in VisualAge.
 */
private void insertStructureMappingsSQL(InsertHashtable hash, Connection con, KeyValue simContextKey, SimulationContext simContext) 
			throws SQLException, DataAccessException {
				
	String sql;
	StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping structureMapping = structureMappings[i];
		//
		// check for incomplete StructureMappings, this allows partially mapped SimContext to be saved without Exceptions.
		// it's ok to have missing StructureMappings in the database, assignStructureMappingsSQL() is tolerant of this.
		//
		cbit.vcell.geometry.SubVolume mappedSubVolume = null;
		boolean isResolved = false;
		if (structureMapping instanceof FeatureMapping){
			mappedSubVolume = ((FeatureMapping)structureMapping).getSubVolume();
			isResolved = ((FeatureMapping)structureMapping).getResolved();
		}else if (structureMapping instanceof MembraneMapping){
			FeatureMapping insideFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(((MembraneMapping)structureMapping).getMembrane().getInsideFeature());
			mappedSubVolume = insideFeatureMapping.getSubVolume();
			isResolved = insideFeatureMapping.getResolved();
		}
		if (mappedSubVolume!=null){
			//
			KeyValue newStuctureMappingKey = getNewKey(con);
			//
			sql = 	"INSERT INTO " + structureMappingTable.getTableName() + " " + structureMappingTable.getSQLColumnList() +
					" VALUES " + structureMappingTable.getSQLValueList(hash, newStuctureMappingKey, simContextKey, structureMapping, mappedSubVolume, isResolved);
System.out.println(sql);
			updateCleanSQL(con, sql);
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, SimulationContext simContext, KeyValue updatedMathDescKey, Model updatedModel, KeyValue updatedGeometryKey, String name, boolean bVersion) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	Version newVersion = insertVersionableInit(hash, con, user, simContext, name, simContext.getDescription(), bVersion);
	try{
		insertSimulationContext(hash, con, user, simContext, updatedMathDescKey, updatedModel, updatedGeometryKey, newVersion, bVersion);
		return newVersion.getVersionKey();
	} catch (DependencyException e) {
		log.exception(e);
		throw new DataAccessException("DependencyException: " + e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash,Connection con,User user,SimulationContext simContext,KeyValue updatedMathDescKey,Model updatedModel,KeyValue updatedGeometryKey,boolean bVersion)
    throws DataAccessException, SQLException, RecordChangedException {

    Version newVersion = null;
    try {
        newVersion = updateVersionableInit(hash, con, user, simContext, bVersion);
        insertSimulationContext(hash,con,user,simContext,updatedMathDescKey,updatedModel,updatedGeometryKey,newVersion,bVersion);
    } catch (DependencyException e) {
        log.exception(e);
        throw new DataAccessException("MathException: " + e.getMessage());
    }
    return newVersion.getVersionKey();
}
}