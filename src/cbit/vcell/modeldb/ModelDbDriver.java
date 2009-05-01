package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;
import java.sql.*;
import java.util.*;
import cbit.sql.*;
import java.sql.Statement;

import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class ModelDbDriver extends DbDriver {
	public static final ModelTable modelTable = ModelTable.table;
	public static final UserTable userTable = UserTable.table;
	public static final ModelStructLinkTable modelStructLinkTable = ModelStructLinkTable.table;
	public static final SpeciesContextModelTable speciesContextModelTable = SpeciesContextModelTable.table;
	public static final DiagramTable diagramTable = DiagramTable.table;
	private ReactStepDbDriver reactStepDB = null;

/**
 * LocalDBManager constructor comment.
 */
public ModelDbDriver(DBCacheTable argdbc,ReactStepDbDriver argReactStepDB,SessionLog sessionLog) {
	super(argdbc,sessionLog);
	this.reactStepDB = argReactStepDB;
}


/**
 * only the owner can delete a Model
 */
private void deleteModelSQL(Connection con, User user, KeyValue modelKey) 
				throws SQLException,org.vcell.util.DependencyException,ObjectNotFoundException {

	//
	// first the reactions are deleted, then the model, the order is important because both the 
	// reactionStep's and the model reference the same speciesContext records
	//
	// 1) the reactions are deleted (which doesn't cascade to the speciesContext)
	// 2) the model is deleted (which DOES cascade to the speciesContext)
	//
	failOnExternalRefs(con, SimContextTable.table.modelRef, VersionTable.getVersionTable(VersionableType.SimulationContext), modelKey,ModelTable.table);
	failOnExternalRefs(con, BioModelTable.table.modelRef, BioModelTable.table, modelKey,ModelTable.table);

	//
	// delete reaction steps and accompanied link-table entries
	//
	reactStepDB.deleteReactionStepsFromModel(con,modelKey);

	//
	// delete model (takes care of speciesContexts)
	//
	String sql;
	sql = DatabasePolicySQL.enforceOwnershipDelete(user,modelTable,modelTable.id.getQualifiedColName()+" = "+modelKey);
			
	updateCleanSQL(con, sql);
}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
public void deleteVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
				throws org.vcell.util.DependencyException, ObjectNotFoundException,
						SQLException,DataAccessException,org.vcell.util.PermissionException {

	deleteVersionableInit(con, user, vType, vKey);
	if (vType.equals(VersionableType.Model)){
		deleteModelSQL(con, user, vKey);
		dbc.remove(vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Diagram
 * @param rset java.sql.ResultSet
 */
private Diagram getDiagram(Connection con,ResultSet rset) throws SQLException, DataAccessException {

	KeyValue structKey = new KeyValue(rset.getBigDecimal(diagramTable.structRef.toString()));
	Diagram diagram = diagramTable.getDiagram(rset,log);
	diagram.setStructure(reactStepDB.getStructureHeirarchy(con,structKey));
	
	return diagram;
}


/**
 * getModels method comment.
 */
private cbit.vcell.model.Diagram[] getDiagramsFromModel(Connection con,KeyValue modelKey) throws SQLException, DataAccessException {
	//log.print("ModelDbDriver.getDiagramsFromModel(modelKey=" + modelKey + ")");
	String sql;
	
	sql = 	" SELECT *" + 
			" FROM " + diagramTable.getTableName() + 
			" WHERE " + diagramTable.modelRef + " = " + modelKey;
			
	Statement stmt = con.createStatement();
	Vector<Diagram> diagramList = new Vector<Diagram>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all objects
		//
		while (rset.next()) {
			Diagram diagram = getDiagram(con,rset);
			diagramList.addElement(diagram);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	//
	// put results in an array
	//
	Diagram diagramArray[] = new Diagram[diagramList.size()];
	diagramList.copyInto(diagramArray);
	return diagramArray;
}


/**
 * getModel method comment.
 */
private cbit.vcell.model.Model getModel(Connection con,User user, KeyValue modelKey) 
					throws SQLException, DataAccessException, ObjectNotFoundException {
	if (user == null || modelKey == null) {
		throw new IllegalArgumentException("Improper parameters for getModel");
	}
	//log.print("ModelDbDriver.getModel(user=" + user + ", id=" + modelKey + ")");
	String sql;
	Field[] f = {new cbit.sql.StarField(modelTable),userTable.userid};
	Table[] t = {modelTable,userTable};
	String condition =	modelTable.id.getQualifiedColName() + " = " + modelKey + 
					" AND " + 
						userTable.id.getQualifiedColName() + " = " + modelTable.ownerRef.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null);

	Statement stmt = con.createStatement();
	Model model = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		if (rset.next()) {
			model = getModel(rset,con,user);
		} else {
			throw new org.vcell.util.ObjectNotFoundException("Model id=" + modelKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	// moved adding global parameters to model to getModel(rset, con, user) method invoked above.
	//	GlobalModelParameterTable.table.setModelParameters(con, model);
	return model;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Model
 * @param rset java.sql.ResultSet
 */
private Model getModel(ResultSet rset,Connection con,User user) throws SQLException, DataAccessException {
	//String ownerName = rset.getString(userTable.userid.toString()).trim();
	//KeyValue ownerRef = new KeyValue(rset.getBigDecimal(ModelTable.ownerRef.toString(), 0));
	//User owner = new User(ownerName, ownerRef);
	try {
		Model model = modelTable.getModel(rset,con,log);
		//model.setOwner(owner);
		KeyValue modelKey = model.getVersion().getVersionKey();
		
		//
		// set structures for this model
		//
		Structure structures[] = reactStepDB.getStructuresFromModel(con,modelKey);
		if (structures!=null && structures.length>0){
			model.setStructures(structures);
		}

		//
		// set species for this model
		//
		SpeciesContext speciesContexts[] = getSpeciesContextFromModel(con,user,modelKey);
		if (speciesContexts!=null){
			Vector<Species> speciesList = new Vector<Species>();
			for (int i=0;i<speciesContexts.length;i++){
				if (!speciesList.contains(speciesContexts[i].getSpecies())){
					speciesList.addElement(speciesContexts[i].getSpecies());
				}
			}
			Species speciesArray[] = new Species[speciesList.size()];
			speciesList.copyInto(speciesArray);
			model.setSpecies(speciesArray);
		}

		//
		// set speciesContexts for this model
		//
		if (speciesContexts!=null){
			model.setSpeciesContexts(speciesContexts);
		}

		//
		// Add global parameters to the model
		//
		GlobalModelParameterTable.table.setModelParameters(con, model);
		
		//
		// add reactionSteps for this model
		//
		ReactionStep reactSteps[] = reactStepDB.getReactionStepsFromModel(con,modelKey);
		if (reactSteps != null) {
			model.setReactionSteps(reactSteps);
			for (int i=0; i < reactSteps.length; i ++){
				try {
					//
					// fix any improperly defined reactionSteps (which have parameters that should be catalysts)
					// name space of kinetic parameters should be unique with respect to SpeciesContexts (so if they overlap, should be a catalyst).
					//
					Kinetics.KineticsParameter params[] = reactSteps[i].getKinetics().getKineticsParameters();
					for (int j = 0; j < params.length; j++){
						SpeciesContext speciesContext = model.getSpeciesContext(params[j].getName());
						if (speciesContext!=null){
							//
							// if spatially coincident, then make it a catalyst (else just complain to log and let user sort it out).
							// they are coincident if:
							//   a) in the same structure
							//   b) reactionStep is in membrane and speciesContext is adjacent
							//
							if (reactSteps[i].getStructure().getName().equals(speciesContext.getStructure().getName()) ||
								(reactSteps[i].getStructure() instanceof Membrane &&
									(((Membrane)reactSteps[i].getStructure()).getInsideFeature().getName().equals(speciesContext.getStructure().getName()) ||
									 ((Membrane)reactSteps[i].getStructure()).getOutsideFeature().getName().equals(speciesContext.getStructure().getName())))) {
								reactSteps[i].addCatalyst(speciesContext);
								log.alert("ModelDbDriver.getModel(), Parameter '"+params[j].getName()+"' in Reaction "+reactSteps[i].getName()+" in Model("+model.getKey()+") conflicts with SpeciesContext, added as a catalyst");
							}else{
								log.alert("ModelDbDriver.getModel(), Parameter '"+params[j].getName()+"' in Reaction "+reactSteps[i].getName()+" in Model("+model.getKey()+") conflicts with non-adjacent SpeciesContext, can't fix");
							}
						}
					}
				}catch (Throwable e){
					log.exception(e);
				}
				try {
					reactSteps[i].rebindAllToModel(model);
				}catch (cbit.vcell.parser.ExpressionBindingException e){
					throw new DataAccessException("bindingException: "+e.getMessage());
				}catch (cbit.vcell.parser.ExpressionException e){
					throw new DataAccessException(e.getMessage());
				}catch (PropertyVetoException e){
					throw new DataAccessException("PropertyVetoException: "+e.getMessage());
				}catch (cbit.vcell.model.ModelException e){
					throw new DataAccessException(e.getMessage());
				}
			}
		}
		
		//
		// add diagrams for this model
		//
		Diagram diagrams[] = getDiagramsFromModel(con,modelKey);
		model.setDiagrams(diagrams);
		
		return model;
	}catch (PropertyVetoException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.SpeciesContext
 * @param speciesContextID cbit.sql.KeyValue
 */
public SpeciesContext getSpeciesContext(Connection con, KeyValue speciesContextID) throws SQLException, DataAccessException {
	//
	// try to get Model from object cache
	//
	SpeciesContext speciesContext = (SpeciesContext)dbc.get(speciesContextID);
	if (speciesContext!=null){
		return speciesContext;
	}
	
	if (speciesContextID == null) {
		throw new IllegalArgumentException("Improper parameters for getSpeciesContext");
	}
	//log.print("ModelDbDriver.getSpeciesContext(speciesContextID=" + speciesContextID + ")");
	String sql;
	sql =	" SELECT * " + 
			" FROM " + speciesContextModelTable.getTableName() + 
			" WHERE " + speciesContextModelTable.id + " = " + speciesContextID;

	//System.out.println(sql);

	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		if (rset.next()) {
			speciesContext = getSpeciesContext(con,rset);
		} else {
			throw new org.vcell.util.ObjectNotFoundException("SpeciesContext id=" + speciesContextID + " not found");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return speciesContext;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.SpeciesContext
 * @param rset java.sql.ResultSet
 */
private SpeciesContext getSpeciesContext(Connection con, ResultSet rset) throws SQLException, DataAccessException {

	//
	// if in object cache, no need to create it
	//
	KeyValue scKey = new KeyValue(rset.getBigDecimal(SpeciesContextModelTable.table.id.toString()));
	SpeciesContext speciesContext = (SpeciesContext)dbc.get(scKey);
	if (speciesContext!=null){
		return speciesContext;
	}
		
	//
	// get foreign keys
	//
	KeyValue structKey = new KeyValue(rset.getBigDecimal(SpeciesContextModelTable.table.structRef.toString()));
	KeyValue speciesKey = new KeyValue(rset.getBigDecimal(SpeciesContextModelTable.table.speciesRef.toString()));
	//
	// get object (ignoring foreign keys)
	//
	speciesContext = speciesContextModelTable.getSpeciesContext(rset,log,scKey);
	//
	// add objects corresponding to foreign keys
	//
	Structure structure = reactStepDB.getStructureHeirarchy(con,structKey);
	Species species = reactStepDB.getSpecies(con,speciesKey);
	speciesContext = new SpeciesContext(scKey,speciesContext.getName(),species,structure,speciesContext.getHasOverride());

	//
	// put SpeciesContext into object cache
	//
	dbc.putUnprotected(scKey,speciesContext);
	
	return speciesContext;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.SpeciesContext
 * @param speciesContextID cbit.sql.KeyValue
 */
private SpeciesContext[] getSpeciesContextFromModel(Connection con,User user, KeyValue modelKey) throws SQLException, DataAccessException {
	if (user == null || modelKey == null) {
		throw new IllegalArgumentException("Improper parameters for getSpeciesContextFromModel");
	}
	//log.print("ModelDbDriver.getSpeciesContextFromModel(user=" + user + ", modelKey=" + modelKey);
	String sql;
	
	sql =	" SELECT * " +
			" FROM " + speciesContextModelTable.getTableName() + 
			" WHERE " + speciesContextModelTable.getTableName()+"."+speciesContextModelTable.modelRef+" = "+modelKey;
	
//System.out.println(sql);
	//Connection con = conFact.getConnection();
	Vector<SpeciesContext> speciesContextList = new Vector<SpeciesContext>();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		SpeciesContext speciesContext = null;
		while (rset.next()) {
			speciesContext = getSpeciesContext(con,rset);
			speciesContextList.addElement(speciesContext);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	if (speciesContextList.size() > 0) {
		SpeciesContext speciesContextArray[] = new SpeciesContext[speciesContextList.size()];
		speciesContextList.copyInto(speciesContextArray);
		return speciesContextArray;
	} else {
		return null;
	}
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
		if (vType.equals(VersionableType.Model)){
			versionable = getModel(con, user, vKey);
		}else{
			throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
		}
		dbc.putUnprotected(versionable.getVersion().getVersionKey(),versionable);
	}
	return versionable;
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertDiagramSQL(Connection con, KeyValue key, Diagram diagram, KeyValue modelKey, KeyValue structKey) throws SQLException,DataAccessException {

	String sql =
		"INSERT INTO " + diagramTable.getTableName() + " " + diagramTable.getSQLColumnList() + 
		" VALUES " + diagramTable.getSQLValueList(key, diagram, modelKey, structKey);
			
	varchar2_CLOB_update(
						con,
						sql,
						diagram.getVCML(),
						DiagramTable.table,
						key,
						DiagramTable.table.diagramLarge,
						DiagramTable.table.diagramSmall);
}


/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 */
private void insertModel(InsertHashtable hash, Connection con,User user ,Model model,Version newVersion) 
						throws SQLException, DataAccessException,RecordChangedException {
	
	//
	// insert model record
	//
	insertModelSQL(con,user,model,newVersion);
	hash.put(model,newVersion.getVersionKey());
	//
	// make sure all species are in the database and the hashtable
	//
	Species speciesArray[] = model.getSpecies();
	for (int i=0;i<speciesArray.length;i++){
		KeyValue speciesKey = null;//speciesArray[i].getKey();
		//if (speciesKey == null) {
		speciesKey = reactStepDB.insertSpecies(hash,con,speciesArray[i],user);
			//throw new DataAccessException("Database error: species "+species.getName()+" has null key");
		//}else if (speciesArray[i].getOwnerKey()!=null && speciesArray[i].getOwnerKey()==user.getID()){
		//	reactStepDB.updateSpecies(con,speciesArray[i],user);
		//}
		//if (hash.getDatabaseKey(speciesArray[i])==null){
		hash.put(speciesArray[i], speciesKey);
		//}
	}

	//
	// make sure all structures are in the database and the hashtable (add entry to link table)
	//
	Structure structures[] = model.getStructures();
	for (int i=0;i<structures.length;i++){
		Structure structure = (Structure) structures[i];
		KeyValue structureKey = null;
		if (hash.getDatabaseKey(structure) == null) {
			structureKey = reactStepDB.insertStructure(hash,con,structure);
		}
		KeyValue linkKey = getNewKey(con);
		insertModelStructLinkSQL(con, linkKey, newVersion.getVersionKey()/*modelKey*/, hash.getDatabaseKey(structure));
	}

	//
	// make sure all speciesContexts are in database and the hashtable
	//
	SpeciesContext speciesContexts[] = model.getSpeciesContexts();
	for (int i=0;i<speciesContexts.length;i++){
		SpeciesContext sc = speciesContexts[i];
		if (hash.getDatabaseKey(sc) == null) {
			insertSpeciesContextSQL(hash,con,getNewKey(con),sc,newVersion.getVersionKey());
		}
	}

	//
	// insert all reactionSteps
	//
	ReactionStep reactionSteps[] = model.getReactionSteps();
	for (int i=0;i<reactionSteps.length;i++){
		ReactionStep rs = reactionSteps[i];
		if (hash.getDatabaseKey(rs) == null){
			reactStepDB.insertReactionStep(hash,con,user,rs,newVersion.getVersionKey());
		}
	}

	//
	// insert diagrams
	//
	Diagram diagrams[] = model.getDiagrams();
	for (int i=0;i<diagrams.length;i++){
		Diagram diagram = diagrams[i];
		KeyValue key = getNewKey(con);
		KeyValue structKey = hash.getDatabaseKey(diagram.getStructure());
		if (structKey != null){
			insertDiagramSQL(con, key, diagram, newVersion.getVersionKey()/*modelKey*/, hash.getDatabaseKey(diagram.getStructure()));
		}else{
			log.alert("ModelDbDriver.insertModel(),  diagram "+diagram.toString()+" is orphaned, check Model logic");
		}
	}
	//
	//insert GlobalModelParameters
	//
//	//-----------------testing remove
//	try{
//		Model.ModelParameter testParam = model.new ModelParameter("test",new Expression("1.0"),Model.ROLE_UserDefined,VCUnitDefinition.UNIT_molecules);
//		model.setModelParameters(new Model.ModelParameter[] {testParam});
//	}catch(Exception e){
//		e.printStackTrace();
//	}
//	//-----------------
	
	GlobalModelParameterTable.table.insertModelParameters(con, model.getModelParameters(),newVersion.getVersionKey());
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertModelSQL(Connection con,User user, Model model,Version newVersion) 
					throws SQLException,DataAccessException {

	String sql;
	Object[] o = {model};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,modelTable,o,newVersion);
//System.out.println(sql);
	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertModelStructLinkSQL(Connection con, KeyValue key, KeyValue modelKey, KeyValue structKey) throws SQLException, DataAccessException {
	String sql;
	sql = 	"INSERT INTO " + modelStructLinkTable.getTableName() + " " + 
				modelStructLinkTable.getSQLColumnList() + 
			" VALUES " + modelStructLinkTable.getSQLValueList(key, modelKey, structKey);
//System.out.println(sql);

	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertSpeciesContextSQL(InsertHashtable hash, Connection con, KeyValue key, SpeciesContext speciesContext, KeyValue modelKey) throws SQLException, DataAccessException {

	String sql;
	sql = "INSERT INTO " + speciesContextModelTable.getTableName() + " " + 
			speciesContextModelTable.getSQLColumnList() + " VALUES " + 
			speciesContextModelTable.getSQLValueList(hash, key, speciesContext, modelKey);
//System.out.println(sql);

	updateCleanSQL(con,sql);
	hash.put(speciesContext,key);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, Model model, String name, boolean bVersion) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	Version newVersion = insertVersionableInit(hash, con, user, model, name, model.getDescription(), bVersion);
	insertModel(hash, con, user, model, newVersion);
	return newVersion.getVersionKey();
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash, Connection con, User user, Model model, boolean bVersion) 
			throws DataAccessException, SQLException, RecordChangedException {
				
	Version newVersion = updateVersionableInit(hash, con, user, model, bVersion);
	insertModel(hash, con, user, model, newVersion);
	return newVersion.getVersionKey();
}
}