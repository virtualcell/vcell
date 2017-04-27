/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
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
public ReactStepDbDriver(ModelDbDriver modelDB, SessionLog sessionLog,DictionaryDbDriver argDictDB) {
	super(sessionLog);
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
private ReactionParticipant getReactionParticipant(QueryHashtable dbc, Connection con, ResultSet rset, ReactionStep rs) throws SQLException, DataAccessException {
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
	SpeciesContext speciesContext = modelDB.getSpeciesContext(dbc, con,speciesContextKey);
	try {
		rp.setSpeciesContext(speciesContext);
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("PropertyVetoException: "+e.getMessage());
	}
	rp.setReactionStep(rs);

	// ========== Since there is no flux carrier in fluxReaction, this is not required?? ===========
	
//	if (rp instanceof Flux && rs instanceof FluxReaction) {
//		try {
//			((FluxReaction) rs).setFluxCarrier(speciesContext.getSpecies(), null);
//		}catch (ModelException e){
//			e.printStackTrace(System.out);
//			throw new DataAccessException("ModelException: "+e.getMessage());
//		}catch (PropertyVetoException e){
//			e.printStackTrace(System.out);
//			throw new DataAccessException("PropertyVetoException: "+e.getMessage());
//		}
//	}

	//
	// stick ReactionParticipant in object cache
	//
	dbc.put(rpKey,rp);
	
	return rp;
}


/**
 * getModel method comment.
 */
private ReactionParticipant[] getReactionParticipants(QueryHashtable dbc, Connection con, KeyValue reactStepID, ReactionStep rs) throws SQLException, DataAccessException, ObjectNotFoundException {
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
			ReactionParticipant rp = getReactionParticipant(dbc, con, rset, rs);
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
public cbit.vcell.model.Model getReactionStepAsModel(QueryHashtable dbc, Connection con, User user,KeyValue reactionStepKey) throws SQLException, DataAccessException, PropertyVetoException {

	String sql;
	sql = 	" SELECT " + ReactStepTable.table.modelRef.getUnqualifiedColName() +
			" FROM " + ReactStepTable.table.getTableName() + 
			" WHERE " + ReactStepTable.table.id.getUnqualifiedColName() + " = " + reactionStepKey.toString();
	Statement stmt = con.createStatement();
	try{
		KeyValue modelKey = null;
		ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){
			modelKey = new KeyValue(rset.getBigDecimal(1));
		}
		if(modelKey != null){
			return (Model)modelDB.getVersionable(dbc, con, user, VersionableType.Model, modelKey);
		}
		return null;
	} finally {
		stmt.close(); // Release resources include resultset
	}

	
//	Field[] f =
//	{
//		new StarField(ReactStepTable.table)
//	};
//	
//	Table[] t =
//	{
//		BioModelTable.table,
//		ReactStepTable.table
//	};
//	
//	String condition =
//		ReactStepTable.table.id.getQualifiedColName() + " = " + reactionStepKey.toString() +
//		" AND " +
//		ReactStepTable.table.modelRef.getQualifiedColName() + " = " + BioModelTable.table.modelRef.getQualifiedColName();
//		
//	String sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null);
//	
//	Model model = new Model("newModel");
//	ReactionStep[] rsArr = getReactionStepArray(dbc, con, model, sql);
//	if(rsArr != null && rsArr.length > 0){
//		ReactionStep reactionStep = rsArr[0];
//		for (ReactionParticipant rp : reactionStep.getReactionParticipants()){
//			if (!model.contains(rp.getSpecies())){
//				model.addSpecies(rp.getSpecies());
//			}
//			if (!model.contains(rp.getSpeciesContext())){
//				model.addSpeciesContext(rp.getSpeciesContext());
//			}
//		}
//		ArrayList<Structure> structures = new ArrayList<Structure>();
//		structures.add(reactionStep.getStructure());
//		for (ReactionParticipant rp : reactionStep.getReactionParticipants()){
//			if (!structures.contains(rp.getStructure())){
//				structures.add(rp.getStructure());
//			}
//		}
//		model.setStructures(structures.toArray(new Structure[0]));
//		HashMap<KeyValue,StructureKeys> structureKeysMap = new HashMap<KeyValue, StructureKeys>();
//		for (Structure structure : structures){
//			StructureKeys structureKeys = getStructureKeys(dbc, con, structure.getKey());
//			structureKeysMap.put(structure.getKey(),structureKeys);
//		}
//		populateStructureAndElectricalTopology(model, structureKeysMap);
//		model.addReactionStep(reactionStep);
//		return model;
//	}
//	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionStep
 * @param rset java.sql.ResultSet
 * @exception java.sql.SQLException The exception description.
 */
private ReactionStep getReactionStep(QueryHashtable dbc, Connection con, ResultSet rset, Model model) throws SQLException, DataAccessException, PropertyVetoException {

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
	
	Structure structure = getStructure(dbc, con, structKey);
	rs = reactStepTable.getReactionStep(structure, model, rsKey, rset, log);

	//
	// add reaction participants for this reactionStep
	//	
	ReactionParticipant rp_Array[] = getReactionParticipants(dbc, con,rsKey,rs);
	rs.setReactionParticipantsFromDatabase(model, rp_Array);

	try {
		rs.getKinetics().bind(rs);
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("ExpressionException: "+e.getMessage());
	}

	dbc.put(rsKey,rs);
	
//	MIRIAMTable.table.setMIRIAMAnnotation(con, rs, rs.getKey());
	return rs;
}


/**
 * getModels method comment.
 */
private ReactionStep[] getReactionStepArray(QueryHashtable dbc, Connection con, Model model, String sql) throws SQLException, DataAccessException, PropertyVetoException {

	Vector reactStepList = new Vector();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);
		//
		// get all objects
		//
		while (rset.next()) {
			ReactionStep reactionStep = getReactionStep(dbc, con, rset, model);
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
ReactionStep[] getReactionStepsFromModel(QueryHashtable dbc, Connection con, Model model, KeyValue modelKey) throws SQLException, DataAccessException, PropertyVetoException {
	//log.print("ModelDbDriver.getReactionSteps(modelKey=" + modelKey + ")");
	String sql;
	sql = 	" SELECT " + reactStepTable.getTableName()+".*" + 
			" FROM " + reactStepTable.getTableName() + 
			" WHERE " + reactStepTable.getTableName() + "." + reactStepTable.modelRef + " = " + modelKey +
			" ORDER BY " + reactStepTable.id;
			
	return getReactionStepArray(dbc, con, model, sql);
}


/**
 * getModel method comment.
 */
public Species getSpecies(QueryHashtable dbc, Connection con, KeyValue speciesID) throws SQLException, DataAccessException, ObjectNotFoundException {
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
			species = getSpecies(dbc, rset,con);
		} else {
			throw new org.vcell.util.ObjectNotFoundException("Species id=" + speciesID + " not found");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

//	MIRIAMTable.table.setMIRIAMAnnotation(con, species, speciesID);
	return species;
}


/**
 * getModel method comment.
 */
public Species[] getSpecies(QueryHashtable dbc, Connection con,User user) throws SQLException, DataAccessException {
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
			Species s = getSpecies(dbc, rset,con);
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
private Species getSpecies(QueryHashtable dbc, ResultSet rset,Connection con) throws SQLException, DataAccessException {
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
				dbSpeciesRef = dictDB.getDBSpeciesFromKeyValue(dbc, con,dbSpeciesKey);
			}
		}
		
		species = speciesTable.getSpecies(rset,log,dbSpeciesRef);
		//
		// stick new one in cache
		//
		dbc.put(speciesKey,species);
		return species;
	}
}


/**
 * Selects all structures that are topologically neighboring (according to the parentRef field)
 */
public Structure getStructure(QueryHashtable dbc, Connection con, KeyValue structureKey) throws SQLException, DataAccessException, ObjectNotFoundException {
	Object obj = dbc.get(structureKey);
	if (obj instanceof Structure){
		return (Structure)obj;
	}
	String sql;

	//
	// check if in global cache
	//
	if (dbc.get(structureKey)!=null){
		return (Structure)dbc.get(structureKey);
	}

	//log.print("ReactStepDbDriver.getStructureHeirarchy(structKey=" + structKey + ")");
	//
	// get all structures belonging to the same model and store in temporary hashMap
	// reconcile all references between Structures
	// put final objects into global cache
	//

	sql =	" SELECT " + structTable.getTableName()+".* " +
			" FROM " + structTable.getTableName() +
			" WHERE " + structTable.id.getQualifiedColName() + " = " + structureKey;

//System.out.println(sql);

	Statement stmt = con.createStatement();
	Structure struct = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		while (rset.next()) {
			KeyValue key = new KeyValue(rset.getBigDecimal(structTable.id.toString()));
			struct = structTable.getStructure(rset, log, key);
			dbc.put(key, struct);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	return struct;
}

public static class StructureKeys {
	public final KeyValue structKey;
	public final KeyValue parentKey;
	public final KeyValue posKey;
	public final KeyValue negKey;
	StructureKeys(KeyValue structKey, KeyValue parentKey, KeyValue positiveKey, KeyValue negativeKey){
		this.structKey = structKey;
		this.parentKey = parentKey;
		this.posKey = positiveKey;
		this.negKey = negativeKey;
	}
}

/**
 * Selects all structures that are topologically neighboring (according to the parentRef field)
 */
public HashMap<KeyValue, StructureKeys> getStructureParentMapByModel(QueryHashtable dbc, Connection con , KeyValue modelKey) throws SQLException, DataAccessException, ObjectNotFoundException {
	String sql;

	// get all structures/parent relationships belonging to the same model
	
	sql =	" SELECT " + structTable.id.getQualifiedColName()+", " + structTable.parentRef.getQualifiedColName() + "," + structTable.posFeatureRef.getQualifiedColName()+ "," + structTable.negFeatureRef.getQualifiedColName()+
			" FROM " + structTable.getTableName() + ", "+modelStructLinkTable.getTableName() +
			" WHERE " + structTable.id.getQualifiedColName() + " = " + modelStructLinkTable.structRef.getQualifiedColName() +
			   " AND " + modelStructLinkTable.modelRef.getQualifiedColName() + " = " + modelKey;

System.out.println(sql);

	Statement stmt = con.createStatement();

	HashMap<KeyValue,StructureKeys> keyMap = new HashMap<KeyValue,StructureKeys>();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		while (rset.next()) {
			KeyValue structKey = new KeyValue(rset.getBigDecimal(structTable.id.toString()));
			KeyValue parentKey = null;
			KeyValue positiveKey = null;
			KeyValue negativeKey = null;
			java.math.BigDecimal parentKeyValue = rset.getBigDecimal(structTable.parentRef.toString());
			if (!rset.wasNull()){
				parentKey = new KeyValue(parentKeyValue);
			}
			java.math.BigDecimal posKeyValue = rset.getBigDecimal(structTable.posFeatureRef.toString());
			if (!rset.wasNull()){
				positiveKey = new KeyValue(posKeyValue);
			}
			java.math.BigDecimal negKeyValue = rset.getBigDecimal(structTable.negFeatureRef.toString());
			if (!rset.wasNull()){
				negativeKey = new KeyValue(negKeyValue);
			}
			keyMap.put(structKey, new StructureKeys(structKey, parentKey, positiveKey, negativeKey));
		}
		return keyMap;
	} finally {
		stmt.close(); // Release resources include resultset
	}
	
}

/**
 * Selects all structures that are topologically neighboring (according to the parentRef field)
 */
public StructureKeys getStructureKeys(QueryHashtable dbc, Connection con , KeyValue structKey) throws SQLException, DataAccessException, ObjectNotFoundException {
	String sql;

	// get all structures/parent relationships belonging to the same model
	
	sql =	" SELECT " + structTable.parentRef.getQualifiedColName()+ "," + structTable.posFeatureRef.getQualifiedColName()+ "," + structTable.negFeatureRef.getQualifiedColName()+
			" FROM " + structTable.getTableName() + 
			" WHERE " + structTable.id.getQualifiedColName() + " = " + structKey;

System.out.println(sql);

	Statement stmt = con.createStatement();
	
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		KeyValue parentKey = null;
		KeyValue positiveKey = null;
		KeyValue negativeKey = null;
		while (rset.next()) {
			java.math.BigDecimal parentKeyValue = rset.getBigDecimal(structTable.parentRef.toString());
			if (!rset.wasNull()){
				parentKey = new KeyValue(parentKeyValue);
			}
			java.math.BigDecimal posKeyValue = rset.getBigDecimal(structTable.posFeatureRef.toString());
			if (!rset.wasNull()){
				positiveKey = new KeyValue(posKeyValue);
			}
			java.math.BigDecimal negKeyValue = rset.getBigDecimal(structTable.negFeatureRef.toString());
			if (!rset.wasNull()){
				negativeKey = new KeyValue(negKeyValue);
			}
		}
		return new StructureKeys(structKey, parentKey, positiveKey, negativeKey);
	} finally {
		stmt.close(); // Release resources include resultset
	}
}


public static void populateStructureAndElectricalTopology(Model model, HashMap<KeyValue, StructureKeys> structureKeysMap) throws DataAccessException  {
	//
	// populate first pass (Feature parents only if directly present in database)
	//
	for (Structure structure : model.getStructures()) {
		// get parent struct
		KeyValue parentKey = structureKeysMap.get(structure.getKey()).parentKey;
		Structure parentStructure = null;
		if (parentKey != null) {
			for (Structure struct1 : model.getStructures()) {
				if (struct1.getKey().equals(parentKey)) {
					parentStructure = struct1;
					break;
				}
			}
		}
		if (parentStructure != null) {
			StructureTopology structureTopology = model.getStructureTopology();
			if (structure instanceof Feature){
				Feature feature = (Feature)structure;
				if (parentStructure instanceof Feature){
					// feature.setParentFeature((Feature)parent);
					throw new DataAccessException("Feature is not permitted to be the parent of another feature");
				}else if (parentStructure instanceof Membrane){
					structureTopology.setInsideFeature((Membrane)parentStructure, feature);
				}
			}else if (structure instanceof Membrane){
				Membrane membrane = (Membrane)structure;
				if (parentStructure instanceof Feature){
					structureTopology.setOutsideFeature(membrane, (Feature)parentStructure);
				} else {
					throw new DataAccessException("Membrane '" + membrane.getName() + "' is not permitted to be the parent of another membrane '" + parentStructure.getName());
				}
			}
		}
		if (structure instanceof Membrane){
			Membrane membrane = (Membrane)structure;
			KeyValue negKey = structureKeysMap.get(structure.getKey()).negKey;
			if (negKey != null) {
				for (Structure struct : model.getStructures()) {
					if (struct instanceof Feature && struct.getKey().equals(negKey)) {
						Feature feature = (Feature)struct;
						model.getElectricalTopology().setNegativeFeature(membrane, feature);
						break;
					}
				}
			}
			KeyValue posKey = structureKeysMap.get(structure.getKey()).posKey;
			if (posKey != null) {
				for (Structure struct : model.getStructures()) {
					if (struct instanceof Feature && struct.getKey().equals(posKey)) {
						Feature feature = (Feature)struct;
						model.getElectricalTopology().setPositiveFeature(membrane, feature);
						break;
					}
				}
			}
		}
	}
}


/**
 * getModel method comment.
 */
public Structure[] getStructuresFromModel(QueryHashtable dbc, Connection con,KeyValue modelKey) throws SQLException, DataAccessException, ObjectNotFoundException {
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
			structure = getStructure(dbc, con, structKey);
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
//	for (int i = 0; i < structures.length; i++) {
//		MIRIAMTable.table.setMIRIAMAnnotation(con, structures[i], structures[i].getKey());
//	}
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
		if (structure!=null && hash.getDatabaseKey(structure) == null) {
			throw new DataAccessException("ReactionParticipant '"+rp_Array[i].getName()+"' for reaction '"+reactionStep.getName()+"' refers to structure '"+structure.getName()+"' which is not found in database");
		}		
	}

	//
	// make sure structure holding reactionStep is also in database
	//
	Structure structure = reactionStep.getStructure();
	if (hash.getDatabaseKey(structure) == null) {
		throw new DataAccessException("Structure '"+structure.getName()+"' for reaction '"+reactionStep.getName()+"' not found in database");
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
	
//	MIRIAMTable.table.insertMIRIAM(con,reactionStep,newKey);
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
	
//	MIRIAMTable.table.insertMIRIAM(con,species,key);
	
	return key;
}


/**
 * addModel method comment.
 */
public KeyValue insertStructure(InsertHashtable hash, Connection con, cbit.vcell.model.Structure structure) throws SQLException,DataAccessException {
	
	//log.print("ReactStepDbDriver.insertStructure(structure=" + structure + ")");
	
	// parentKey
	KeyValue parentKey = null;
	KeyValue negFeatureKey = null;
	KeyValue posFeatureKey = null;

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
			structTable.getSQLValueList(key, structure,parentKey,cellTypeKey, negFeatureKey, posFeatureKey);
	
	
//	sql = "UPDATE " + structTable.getTableName() + 
//	" SET " + structTable.getSQLUpdateList(parentKey, insideKey, outsideKey) + 
//	" WHERE " + structTable.id.getUnqualifiedColName() + " = " + structKey;

//System.out.println(sql);

	updateCleanSQL(con,sql);
	hash.put(structure,key);
	
//	MIRIAMTable.table.insertMIRIAM(con,structure,key);
	
	return key;
}

/**
 * addModel method comment.
 */
public void updateStructureKeys(Connection con, StructureKeys structureKeys) throws SQLException,DataAccessException {
	
	KeyValue parentKey = structureKeys.parentKey;
	KeyValue negFeatureKey = structureKeys.negKey;
	KeyValue posFeatureKey = structureKeys.posKey;
	KeyValue structKey = structureKeys.structKey;
	String sql;
	sql = "UPDATE " + structTable.getTableName() + " " + 
		 	" SET " + structTable.parentRef.getQualifiedColName() + " = "+parentKey + " " + "," +
					  structTable.negFeatureRef.getQualifiedColName() + " = "+negFeatureKey + " " + "," +
					  structTable.posFeatureRef.getQualifiedColName() + " = "+posFeatureKey + " " +
			" WHERE " + structTable.id.getQualifiedColName() + " = " + structKey;	

//System.out.println(sql);

	updateCleanSQL(con,sql);
}
}
