package cbit.vcell.modeldb;
import cbit.vcell.dictionary.*;
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
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.vcell.model.*;
import cbit.vcell.mapping.*;
import cbit.vcell.xml.MIRIAMAnnotation;
/**
 * This type was created in VisualAge.
 */
public class ReactStepDbDriver extends DbDriver {
	public static final UserTable userTable = UserTable.table;
	public static final ReactStepTable reactStepTable = ReactStepTable.table;
	public static final StructTable structTable = StructTable.table;
	public static final ReactPartTable reactPartTable = ReactPartTable.table;
	public static final SpeciesContextModelTable speciesContextTable = SpeciesContextModelTable.table;
	public static final SpeciesTable speciesTable = SpeciesTable.table;
	public static final ModelStructLinkTable modelStructLinkTable = ModelStructLinkTable.table;
	private ModelDbDriver modelDB = null;
	private DictionaryDbDriver dictDB = null;

/**
 * LocalDBManager constructor comment.
 */
public ReactStepDbDriver(DBCacheTable argdbc,ModelDbDriver modelDB, SessionLog sessionLog,DictionaryDbDriver argDictDB) {
	super(argdbc,sessionLog);
	this.modelDB = modelDB;
	this.dictDB = argDictDB;
}


/**
 * getModels method comment.
 */
public void deleteReactionStepsFromModel(Connection con,KeyValue modelKey) throws SQLException {
	//
	// deletes modelReactLinkTable entries (the reactionSteps get deleted by cascade)
	//
	//log.print("ReactStepDbDriver.deleteReactionStepsFromModel(modelKey=" + modelKey + ")");
	String sql;
	sql = 	" DELETE FROM " + reactStepTable.getTableName() +
			" WHERE " + reactStepTable.getTableName() + "." + reactStepTable.modelRef + " = " + modelKey;
					

	updateCleanSQL(con,sql);
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

	throw new RuntimeException("vType "+vType+" not supported by "+this.getClass());
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 * @exception java.sql.SQLException The exception description.
 */
private ReactionParticipant getReactionParticipant(Connection con, ResultSet rset, ReactionStep rs) throws SQLException, DataAccessException {
	//
	// try to get ReactionParticipant from object cache
	//
	KeyValue rpKey = new KeyValue(rset.getBigDecimal(ReactPartTable.table.id.toString()));
	ReactionParticipant rp = (ReactionParticipant)dbc.get(rpKey);
	if (rp!=null){
		return rp;
	}
	
	//
	// get foreign keys
	//
	KeyValue speciesContextKey = new KeyValue(rset.getBigDecimal(ReactPartTable.table.scRef.toString()));
	//
	// get ReactionParticipant
	//
	rp = reactPartTable.getReactionParticipant(rpKey, rset, log);

	//
	// get referenced Objects (Species and Structure)
	//
	SpeciesContext speciesContext = modelDB.getSpeciesContext(con,speciesContextKey);
	try {
		rp.setSpeciesContext(speciesContext);
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("PropertyVetoException: "+e.getMessage());
	}
	rp.setReactionStep(rs);
	if (rp instanceof Flux && rs instanceof FluxReaction) {
		try {
			((FluxReaction) rs).setFluxCarrier(speciesContext.getSpecies(), null);
		}catch (ModelException e){
			e.printStackTrace(System.out);
			throw new DataAccessException("ModelException: "+e.getMessage());
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new DataAccessException("PropertyVetoException: "+e.getMessage());
		}
	}

	//
	// stick ReactionParticipant in object cache
	//
	dbc.putUnprotected(rpKey,rp);
	
	return rp;
}


/**
 * getModel method comment.
 */
private ReactionParticipant[] getReactionParticipants(Connection con, KeyValue reactStepID, ReactionStep rs) throws SQLException, DataAccessException, ObjectNotFoundException {
	if (reactStepID == null) {
		throw new IllegalArgumentException("Improper parameters for getReactionParticipants");
	}
	//log.print("ModelDbDriver.getReactionParticipants(reactStepID=" + reactStepID + ")");
	String sql;
	sql = 	" SELECT * " + 
			" FROM " + reactPartTable.getTableName() + 
			" WHERE " + reactPartTable.reactStepRef + " = " + reactStepID;

//System.out.println(sql);

	java.util.Vector rpList = new java.util.Vector();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		//showMetaData(rset);
		//
		// get the ReactionParticipants
		//
		while (rset.next()) {
			ReactionParticipant rp = getReactionParticipant(con, rset, rs);
			rpList.addElement(rp);
		}

		// return enumeration of ReactionParticipants
		//
		if (rpList.size() == 0) {
			System.out.println("WARNING:::::ReactionParticipants for reactionStep(id=" + reactStepID + ") not found");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
		
	ReactionParticipant reactionParticipants[] = new ReactionParticipant[rpList.size()];
	rpList.copyInto(reactionParticipants); 

	return reactionParticipants; 	
}


/**
 * getModels method comment.
 */
public cbit.vcell.model.ReactionStep getReactionStep(Connection con, User user,KeyValue reactionStepKey) throws SQLException, DataAccessException, PropertyVetoException {

	Field[] f =
	{
		new StarField(ReactStepTable.table)
	};
	
	Table[] t =
	{
		BioModelTable.table,
		reactStepTable.table
	};
	
	String condition =
		ReactStepTable.table.id.getQualifiedColName() + " = " + reactionStepKey.toString() +
		" AND " +
		ReactStepTable.table.modelRef.getQualifiedColName() + " = " + BioModelTable.table.modelRef.getQualifiedColName();
		
	String sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null);
	
	ReactionStep[] rsArr = getReactionStepArray(con,sql);
	if(rsArr != null && rsArr.length > 0){
		return rsArr[0];
	}
	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionStep
 * @param rset java.sql.ResultSet
 * @exception java.sql.SQLException The exception description.
 */
private ReactionStep getReactionStep(Connection con, ResultSet rset) throws SQLException, DataAccessException, PropertyVetoException {

	//
	// try to get ReactionStep from the object cache
	//
	KeyValue rsKey = new KeyValue(rset.getBigDecimal(ReactStepTable.table.id.toString()));
	ReactionStep rs = (ReactionStep)dbc.get(rsKey);
	if (rs!=null){
		return rs;
	}

	//
	// construct reactionStep (from ReactStepTable results)
	//
	KeyValue structKey = new KeyValue(rset.getBigDecimal(ReactStepTable.table.structRef.toString()));
	
	Structure structure = getStructureHeirarchy(con, structKey);
	rs = reactStepTable.getReactionStep(structure, rsKey, rset, log);

	//
	// add reaction participants for this reactionStep
	//
	ReactionParticipant rp_Array[] = getReactionParticipants(con,rsKey,rs);
	for (int i = 0; i < rp_Array.length; i++) {
		rs.addReactionParticipant(rp_Array[i]);
	}

	try {
		rs.getKinetics().bind(rs);
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("ExpressionException: "+e.getMessage());
	}

	dbc.putUnprotected(rsKey,rs);
	
	MIRIAMTable.table.setMIRIAMAnnotation(con, rs, rs.getKey());
	return rs;
}


/**
 * getModels method comment.
 */
private cbit.vcell.model.ReactionStep[] getReactionStepArray(Connection con,String sql) throws SQLException, DataAccessException, PropertyVetoException {

	java.util.Vector reactStepList = new java.util.Vector();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);
		//
		// get all objects
		//
		while (rset.next()) {
			ReactionStep reactionStep = getReactionStep(con, rset);
			reactStepList.addElement(reactionStep);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	//
	// duplicate reaction steps were allowed in database (identified on Oct 31, 2002).
	//
	//
	// check for duplicate reactionSteps (and log) ... .later remove from database and add unique constraint.
	//
	for (int i = 0; i < reactStepList.size(); i++){
		ReactionStep rs1 = (ReactionStep)reactStepList.elementAt(i);
		for (int j = i+1; j < reactStepList.size(); j++){
			ReactionStep rs2 = (ReactionStep)reactStepList.elementAt(j);
			if (rs1.compareEqual(rs2)){
				log.alert("ReactStepDbDriver.getReactionStepArray, detected duplicate reactionStep id="+rs2.getKey()+" name='"+rs2.getName()+"', should be removed and proper SQL constraint added to schema");
			}
		}
	}
		
	// put results in an array
	if (reactStepList.size() > 0) {
		ReactionStep reactStepArray[] = new ReactionStep[reactStepList.size()];
		reactStepList.copyInto(reactStepArray);
		return reactStepArray;
	} else {
		return null;
	}
}


/**
 * getModels method comment.
 */
cbit.vcell.model.ReactionStep[] getReactionStepsFromModel(Connection con,KeyValue modelKey) throws SQLException, DataAccessException, PropertyVetoException {
	//log.print("ModelDbDriver.getReactionSteps(modelKey=" + modelKey + ")");
	String sql;
	sql = 	" SELECT " + reactStepTable.getTableName()+".*" + 
			" FROM " + reactStepTable.getTableName() + 
			" WHERE " + reactStepTable.getTableName() + "." + reactStepTable.modelRef + " = " + modelKey;
			
	return getReactionStepArray(con,sql);
}


/**
 * getModel method comment.
 */
public cbit.vcell.model.Species getSpecies(Connection con, KeyValue speciesID) throws SQLException, DataAccessException, ObjectNotFoundException {
	//
	// try to get Species from the object cache
	//
	Species species = (Species)dbc.get(speciesID);
	if (species!=null){
		return species;
	}
	
	if (speciesID == null) {
		throw new IllegalArgumentException("Improper parameters for getSpecies");
	}
	//log.print("ReactStepDbDriver.getSpecies(speciesID=" + speciesID + ")");
	String sql;
	sql =	" SELECT * " + 
			" FROM " + speciesTable.getTableName() + 
			" WHERE " + speciesTable.id + " = " + speciesID;

	//System.out.println(sql);

	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		if (rset.next()) {
			species = getSpecies(rset,con);
		} else {
			throw new org.vcell.util.ObjectNotFoundException("Species id=" + speciesID + " not found");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	MIRIAMTable.table.setMIRIAMAnnotation(con, species, speciesID);
	return species;
}


/**
 * getModel method comment.
 */
public Species[] getSpecies(Connection con,User user) throws SQLException, DataAccessException {
	//log.print("ReactStepDbDriver.getSpecies()");
	
	String sql;
	sql = 	" SELECT "+speciesTable.getTableName()+".* " + 
			" FROM " + 
				speciesTable.getTableName() + "," +
				ModelTable.table.getTableName() + ","+
				SpeciesContextModelTable.table.getTableName() +
			" WHERE " + 
				ModelTable.table.ownerRef.getQualifiedColName() + " = " + user.getID() +
				" AND " + SpeciesContextModelTable.table.modelRef.getQualifiedColName() +" = " + ModelTable.table.id.getQualifiedColName() +
				" AND " + SpeciesContextModelTable.table.speciesRef.getQualifiedColName() +" = " + speciesTable.id.getQualifiedColName() +
				" ORDER BY "+ speciesTable.commonName.getQualifiedColName();

	//System.out.println(sql);

	Statement stmt = con.createStatement();
	java.util.Vector speciesList = new java.util.Vector();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		while (rset.next()) {
			Species s = getSpecies(rset,con);
			speciesList.addElement(s);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	//
	// return array of Species
	//
	if (speciesList.size() == 0) {
		return null;
	} else {
		Species speciesArray[] = new Species[speciesList.size()];
		speciesList.copyInto(speciesArray);
		return speciesArray;
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Species
 * @param rset java.sql.ResultSet
 * @exception java.sql.SQLException The exception description.
 */
private Species getSpecies(ResultSet rset,Connection con) throws SQLException, DataAccessException {
	//
	// look in object cache (don't instantiate a new one if possible)
	//
	KeyValue speciesKey = new KeyValue(rset.getBigDecimal(speciesTable.id.toString()));
	Species species = (Species)dbc.get(speciesKey);
	if (species!=null){
		return species;
	}else{
		DBSpecies dbSpeciesRef = null;
		KeyValue dbSpeciesKey = null;
		java.math.BigDecimal dbSpeciesKeyS = rset.getBigDecimal(speciesTable.dbSpeciesRef.toString());
		if(dbSpeciesKeyS != null){
			dbSpeciesKey = new KeyValue(dbSpeciesKeyS);
		}
		if(dbSpeciesKey != null){
			dbSpeciesRef = (DBSpecies)dbc.get(dbSpeciesKey);
			if(dbSpeciesRef == null){
				dbSpeciesRef = dictDB.getDBSpeciesFromKeyValue(con,dbSpeciesKey);
			}
		}
		
		species = speciesTable.getSpecies(rset,log,dbSpeciesRef);
		//
		// stick new one in cache
		//
		dbc.putUnprotected(speciesKey,species);
		return species;
	}
}


/**
 * Selects all structures that are topologically neighboring (according to the parentRef field)
 */
public cbit.vcell.model.Structure getStructureHeirarchy(Connection con,KeyValue structKey) throws SQLException, DataAccessException, ObjectNotFoundException {
	String sql;

	//
	// check if in global cache
	//
	Structure structure = (Structure)dbc.get(structKey);
	if (structure!=null){
		return structure;
	}

	//log.print("ReactStepDbDriver.getStructureHeirarchy(structKey=" + structKey + ")");
	//
	// get all 'touching' structures from the database and store in a temporaray hashtable
	// reconcile all references between Structures
	// put final objects into global cache
	//

	//
	// note:
	//
	// this heirarhical query always returns results in top-down order (first record has no parent ...)
	//
	
	sql =	" SELECT * " +
			" FROM " + structTable.getTableName() +
			" START WITH "+ structTable.id + " = " +
				"(SELECT "+ structTable.id +
				" FROM  " + structTable.getTableName()+
				" WHERE " + structTable.parentRef + " IS NULL " +
				" START WITH " + structTable.id + " = " + structKey +
				" CONNECT BY " + structTable.id + " = " + " PRIOR " + structTable.parentRef +
				")" +
			" CONNECT BY " + " PRIOR " + structTable.id + " = " + structTable.parentRef;

//System.out.println(sql);

	Statement stmt = con.createStatement();

	Hashtable tempHash = new Hashtable();
	
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		while (rset.next()) {
			try {
				getStructureHeirarchy(con, rset, tempHash);
			} catch (ModelException e) {
				throw new DataAccessException("ModelException: " + e.getMessage());
			}
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	//
	// for all membranes, must resolve innerFeature references (membranes point up AND down)
	//
	Enumeration enum1 = tempHash.elements();
	while (enum1.hasMoreElements()){
		Structure struct = (Structure)enum1.nextElement();
		if (struct instanceof Membrane){
			boolean bFound = false;
			Membrane membrane = (Membrane)struct;
			Enumeration enumInner = tempHash.elements();
			while (enumInner.hasMoreElements()){
				Structure innerStruct = (Structure)enumInner.nextElement();
				if (innerStruct instanceof Feature && innerStruct.getParentStructure() == membrane){
					Feature innerFeature = (Feature)innerStruct;
					bFound = true;
					membrane.setInsideFeature(innerFeature);
				}
			}
			if (!bFound){
				throw new DataAccessException("inner structure for membrane "+membrane+" not resolved");
			}
		}
	}

	//
	// stick all structures on the global cache
	//
	enum1 = tempHash.elements();
	while (enum1.hasMoreElements()){
		Structure struct = (Structure)enum1.nextElement();
		dbc.putUnprotected(struct.getKey(),struct);
	}

	//
	// return the structure that you asked for
	//
	return (Structure)dbc.get(structKey);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure
 * @param rset java.sql.ResultSet
 * @exception java.sql.SQLException The exception description.
 */
private void getStructureHeirarchy(Connection con, ResultSet rset, Hashtable tempHash) throws SQLException, DataAccessException, ModelException {

	//
	// get next highest Structure (in heirarchy)
	// parent should already exist in temporary hashtable
	//
	KeyValue structKey = new KeyValue(rset.getBigDecimal(structTable.id.toString()));
	Structure structure = structTable.getStructure(rset, log, structKey);

	//
	// set Structures parent (should be in hashtable)
	//
	Structure parent = null;
	java.math.BigDecimal parentKeyValue = rset.getBigDecimal(structTable.parentRef.toString());
	if (!rset.wasNull()){
		KeyValue parentKey = new KeyValue(parentKeyValue);
		parent = (Structure)tempHash.get(parentKey);
		if (parent != null) {
			structure.setParentStructure(parent);
		}else{
			throw new DataAccessException("parent structure wasn't in temporary hashtable (resolving heirarhical query)");
		}
	}
	tempHash.put(structKey,structure);
}


/**
 * getModel method comment.
 */
public Structure[] getStructures0(Connection con) throws SQLException, DataAccessException, ObjectNotFoundException {
	//log.print("ReactStepDbDriver.getStructures()");
	String sql;
	sql =	" SELECT id " + 
			" FROM " + structTable.getTableName();

	//System.out.println(sql);

	Vector structList = new Vector();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		while (rset.next()) {
			Structure structure = null;
			KeyValue structKey = new KeyValue(rset.getBigDecimal(structTable.id.toString()));
			structure = getStructureHeirarchy(con, structKey);
			structList.addElement(structure);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	if (structList.size() == 0) {
		return null;
	} else {
		Structure structures[] = new Structure[structList.size()];
		structList.copyInto(structures);
		return structures;
	}
}


/**
 * getModel method comment.
 */
public Structure[] getStructuresFromModel(Connection con,KeyValue modelKey) throws SQLException, DataAccessException, ObjectNotFoundException {
	//log.print("ReactStepDbDriver.getStructures()");
	String sql;
	sql =	" SELECT " + modelStructLinkTable.structRef + 
			" FROM " + structTable.getTableName() + "," + modelStructLinkTable.getTableName() + 
			" WHERE " + modelStructLinkTable.getTableName() + "." + modelStructLinkTable.modelRef +
						" = " + modelKey +
				" AND " + modelStructLinkTable.getTableName() + "." + modelStructLinkTable.structRef + 
						" = " + 
						structTable.getTableName() + "." + structTable.id;

	//System.out.println(sql);

	//Connection con = conFact.getConnection();
	Vector<Structure> structList = new Vector<Structure>();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		while (rset.next()) {
			Structure structure = null;
			KeyValue structKey = new KeyValue(rset.getBigDecimal(modelStructLinkTable.structRef.toString()));
			structure = getStructureHeirarchy(con, structKey);
			structList.addElement(structure);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	if (structList.size() == 0) {
		return null;
	}
	Structure structures[] = new Structure[structList.size()];
	structList.copyInto(structures);
	for (int i = 0; i < structures.length; i++) {
		MIRIAMTable.table.setMIRIAMAnnotation(con, structures[i], structures[i].getKey());
	}
	return structures;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public Versionable getVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
			throws ObjectNotFoundException, SQLException, DataAccessException {
				
	throw new RuntimeException("vType " + vType + " not supported by " + this.getClass());
}


/**
 * This method was created in VisualAge.
 * @param modelDB ModelDBDriver
 */
void init(ModelDbDriver modelDB) {
	this.modelDB = modelDB;
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertReactionParticipantSQL(InsertHashtable hash, Connection con, KeyValue key, ReactionParticipant reactionParticipant, KeyValue reactStepKey) throws SQLException, DataAccessException {

	String sql;
	sql = "INSERT INTO " + reactPartTable.getTableName() + " " + 
			reactPartTable.getSQLColumnList() + " VALUES " + 
			reactPartTable.getSQLValueList(hash, key, reactionParticipant, reactStepKey);

//System.out.println(sql);

	updateCleanSQL(con,sql);
	//Statement stat = con.createStatement();
	//stat.executeUpdate(sql);
	//stat.close();
}


/**
 * addModel method comment.
 */
KeyValue insertReactionStep(InsertHashtable hash, Connection con, User user, ReactionStep reactionStep, KeyValue modelKey) 
				throws SQLException, DataAccessException {
			
	//log.print("ReactStepDbDriver.insertReactionStep(user="+user+", reacitonStep="+reactionStep+")");
	//
	// make sure all species and structures are in the database
	//
	ReactionParticipant rp_Array[] = reactionStep.getReactionParticipants();
	for (int i = 0; i < rp_Array.length; i++) {
		//
		// insert/update species for this reactionParticipant
		//
		Species species = rp_Array[i].getSpecies();
		KeyValue speciesKey = hash.getDatabaseKey(species);
		if (speciesKey == null) {
			speciesKey = insertSpecies(hash,con,species,user);
			//throw new DataAccessException("Database error: species "+species.getName()+" has null key");
		}//else if (species.getOwnerKey()!=null && species.getOwnerKey()==user.getID()){
		//	updateSpecies(con,species,user);
		//}
		if (hash.getDatabaseKey(species)==null){
			hash.put(species, speciesKey);
		}

		//
		// may insert structure for this reactionParicipant
		//
		Structure structure = rp_Array[i].getStructure();
		if (hash.getDatabaseKey(structure) == null) {
			insertStructure(hash,con,structure);
		}		
	}

	//
	// make sure structure holding reactionStep is also in database
	//
	Structure structure = reactionStep.getStructure();
	if (hash.getDatabaseKey(structure) == null) {
		insertStructure(hash,con,structure);
	}

	//
	// insert reactionStep
	//
	KeyValue rsKey = getNewKey(con);
	insertReactionStepSQL(con,modelKey,hash.getDatabaseKey(structure),reactionStep,rsKey);

	//
	// insert reactionParticipants
	//
	ReactionParticipant react_participants[] = reactionStep.getReactionParticipants();
	for (int i = 0; i < react_participants.length; i++) {
		KeyValue rpKey = getNewKey(con);
		insertReactionParticipantSQL(hash, con,rpKey,react_participants[i],rsKey);
	}
	
	return rsKey;
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
private void insertReactionStepSQL(Connection con, KeyValue modelKey, KeyValue structKey, ReactionStep reactionStep, KeyValue newKey) throws SQLException, DataAccessException {
	
	String sql;
	sql =
		"INSERT INTO "+ReactStepTable.table.getTableName()+" "+
		ReactStepTable.table.getSQLColumnList()+
		" VALUES "+ReactStepTable.table.getSQLValueList(reactionStep,modelKey,structKey,newKey);
		
	varchar2_CLOB_update(
						con,
						sql,
						reactionStep.getKinetics().getVCML(),
						ReactStepTable.table,
						newKey,
						ReactStepTable.table.kineticsLarge,
						ReactStepTable.table.kineticsSmall);
	
	MIRIAMTable.table.insertMIRIAM(con,reactionStep,newKey);
}


/**
 * addModel method comment.
 */
public KeyValue insertSpecies(InsertHashtable hash, Connection con,cbit.vcell.model.Species species,User user) throws SQLException, DataAccessException {
	//log.print("ReactStepDbDriver.insertSpecies(species="+species+")");
	KeyValue key = hash.getDatabaseKey(species);
	if (key!=null){
		return key;
	}else{
		key = getNewKey(con);
	}
	KeyValue ownerKey = user.getID();
	String sql;
	sql = "INSERT INTO " + speciesTable.getTableName() + " " + 
			speciesTable.getSQLColumnList() + " VALUES " + 
			speciesTable.getSQLValueList(key, ownerKey, species);
//System.out.println(sql);

	updateCleanSQL(con,sql);
	hash.put(species,key);
	
	MIRIAMTable.table.insertMIRIAM(con,species,key);
	
	return key;
}


/**
 * addModel method comment.
 */
public KeyValue insertStructure(InsertHashtable hash, Connection con, cbit.vcell.model.Structure structure) throws SQLException,DataAccessException {
	
	//log.print("ReactStepDbDriver.insertStructure(structure=" + structure + ")");
	
	KeyValue parentKey = null;
	Structure parentStructure = structure.getParentStructure();
	if (parentStructure == null) {
		parentKey = null;
	} else {
		parentKey = hash.getDatabaseKey(parentStructure);
		if (parentKey == null) {
			parentKey = insertStructure(hash,con,parentStructure);
			//throw new DataAccessException("parent Structure " + parentStructure + " doesn't have a key");
		}
	}
	KeyValue key = hash.getDatabaseKey(structure);
	if (key != null){
		return key;
	}else{
		key = getNewKey(con);
	}
	KeyValue cellTypeKey = null;
	String sql;
	sql = "INSERT INTO " + structTable.getTableName() + " " + 
			structTable.getSQLColumnList() + " VALUES " + 
			structTable.getSQLValueList(key, structure,parentKey,cellTypeKey);
//System.out.println(sql);

	updateCleanSQL(con,sql);
	hash.put(structure,key);
	
	MIRIAMTable.table.insertMIRIAM(con,structure,key);
	
	return key;
}
}