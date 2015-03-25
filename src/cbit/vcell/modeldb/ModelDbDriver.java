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
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.Table;
import cbit.vcell.model.Diagram;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ElectricalTopology;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.modeldb.ReactStepDbDriver.StructureKeys;
import cbit.vcell.xml.XmlReader;
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
public ModelDbDriver(ReactStepDbDriver argReactStepDB,SessionLog sessionLog) {
	super(sessionLog);
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
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Diagram
 * @param rset java.sql.ResultSet
 */
private Diagram getDiagram(QueryHashtable dbc, Connection con,ResultSet rset, StructureTopology structureTopology) throws SQLException, DataAccessException {

	KeyValue structKey = new KeyValue(rset.getBigDecimal(diagramTable.structRef.toString()));
	Diagram diagram = diagramTable.getDiagram(rset,log);
	diagram.setStructure(reactStepDB.getStructure(dbc, con,structKey));
	
	return diagram;
}


/**
 * getModels method comment.
 */
private cbit.vcell.model.Diagram[] getDiagramsFromModel(QueryHashtable dbc, Connection con,KeyValue modelKey, StructureTopology structureTopology) throws SQLException, DataAccessException {
	//log.print("ModelDbDriver.getDiagramsFromModel(modelKey=" + modelKey + ")");
	String sql;
	
	sql = 	" SELECT *" + 
			" FROM " + diagramTable.getTableName() + "," + SoftwareVersionTable.table.getTableName() +
			" WHERE " + diagramTable.modelRef + " = " + modelKey +
			" AND " + diagramTable.modelRef + " = " + SoftwareVersionTable.table.versionableRef.getUnqualifiedColName() +"(+)" +
			" ORDER BY "+diagramTable.id.getQualifiedColName();
			
	Statement stmt = con.createStatement();
	Vector<Diagram> diagramList = new Vector<Diagram>();
	String softwareVersion = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all objects
		//
		while (rset.next()) {
			Diagram diagram = getDiagram(dbc, con,rset, structureTopology);
			diagramList.addElement(diagram);
			softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.getUnqualifiedColName());
			if(rset.wasNull()){
				softwareVersion = null;
			}
		}		
	} finally {
		stmt.close(); // Release resources include resultset
	}
	//
	// put results in an array
	//
	Diagram diagramArray[] = new Diagram[diagramList.size()];
	diagramList.copyInto(diagramArray);
	
	VCellSoftwareVersion vCellSoftwareVersion = VCellSoftwareVersion.fromString(softwareVersion);
	XmlReader.reorderDiagramsInPlace_UponRead(vCellSoftwareVersion, diagramArray, structureTopology);

//	//Check this----------------------------
//	System.out.println("-------------------------------------------Starting Diagram check------------------------------------------------------------------------");
//	int count = 0;
//	boolean bOrderMatched = true;
//	sql = 	" SELECT *" + 
//			" FROM " + diagramTable.getTableName() + 
//			" WHERE " + diagramTable.modelRef + " = " + modelKey +
//			" ORDER BY "+diagramTable.id.getUnqualifiedColName();
//	stmt = con.createStatement();
//	try{
//		ResultSet rset = stmt.executeQuery(sql);
//		while(rset.next()){
//			Diagram diagram = getDiagram(dbc, con,rset, structureTopology);
//			System.out.println(rset.getBigDecimal(diagramTable.id.getUnqualifiedColName())+" "+
//					"ord='"+diagram.getStructure().getName()+"' "+
//					"unord='"+diagramArray[count].getStructure().getName()+"' "+
//					rset.getString(diagramTable.name.getUnqualifiedColName())+" "+
//					rset.getBigDecimal(diagramTable.structRef.getUnqualifiedColName()));
//			if(!diagram.getName().equals(diagramArray[count].getName()) ||
//				!(diagram.getStructure().getName().equals(diagramArray[count].getStructure().getName()))){
//				bOrderMatched = false;
//			}
//			count++;
//		}
//	}finally{
//		stmt.close();
//	}
//	if(!bOrderMatched){
//		System.out.println("Order not match!!!!!");
//	}
//	//----------------------------------------
	return diagramArray;
}


/**
 * getModel method comment.
 */
private cbit.vcell.model.Model getModel(QueryHashtable dbc, Connection con,User user, KeyValue modelKey) 
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
			model = getModel(dbc, rset,con,user);
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
private Model getModel(QueryHashtable dbc, ResultSet rset,Connection con,User user) throws SQLException, DataAccessException {
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
		StructureTopology structureTopology = model.getStructureTopology();
		Structure structures[] = reactStepDB.getStructuresFromModel(dbc, con,modelKey);
		if (structures!=null && structures.length>0){
			model.setStructures(structures);
		}
		HashMap<KeyValue, StructureKeys> structureKeysMap = reactStepDB.getStructureParentMapByModel(dbc, con, modelKey);
		ReactStepDbDriver.populateStructureAndElectricalTopology(model, structureKeysMap);

		//
		// set species for this model
		//
		SpeciesContext speciesContexts[] = getSpeciesContextFromModel(dbc, con,user,modelKey, structureTopology);
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
		ReactionStep reactSteps[] = reactStepDB.getReactionStepsFromModel(dbc, con, model, modelKey);
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
							reactSteps[i].addCatalyst(speciesContext);
							log.alert("ModelDbDriver.getModel(), Parameter '"+params[j].getName()+"' in Reaction "+reactSteps[i].getName()+" in Model("+model.getKey()+") conflicts with SpeciesContext, added as a catalyst");
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
		Diagram diagrams[] = getDiagramsFromModel(dbc, con,modelKey, structureTopology);
		model.setDiagrams(diagrams);
		
		//
		//add rbm
		//
		ModelTable.readRbmElement(con, model);
		
		if(!model.getRbmModelContainer().isEmpty()) {
			for(SpeciesContext sc : model.getSpeciesContexts()) {
				sc.parseSpeciesPatternString(model);
			}
		}
		
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
public SpeciesContext getSpeciesContext(QueryHashtable dbc, Connection con, KeyValue speciesContextID) throws SQLException, DataAccessException {
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
			speciesContext = getSpeciesContext(dbc, con,rset);
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
private SpeciesContext getSpeciesContext(QueryHashtable dbc, Connection con, ResultSet rset) throws SQLException, DataAccessException {

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
	String speciesPatternString = speciesContext.getSpeciesPatternString();
	//
	// add objects corresponding to foreign keys
	//
	Structure structure = reactStepDB.getStructure(dbc, con,structKey);
	Species species = reactStepDB.getSpecies(dbc, con,speciesKey);
	speciesContext = new SpeciesContext(scKey,speciesContext.getName(),species,structure);
	speciesContext.setSpeciesPatternString(speciesPatternString);

	//
	// put SpeciesContext into object cache
	//
	dbc.put(scKey,speciesContext);
	
	return speciesContext;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.SpeciesContext
 * @param speciesContextID cbit.sql.KeyValue
 */
private SpeciesContext[] getSpeciesContextFromModel(QueryHashtable dbc, Connection con,User user, KeyValue modelKey, StructureTopology structureTopology) throws SQLException, DataAccessException {
	if (user == null || modelKey == null) {
		throw new IllegalArgumentException("Improper parameters for getSpeciesContextFromModel");
	}
	//log.print("ModelDbDriver.getSpeciesContextFromModel(user=" + user + ", modelKey=" + modelKey);
	String sql;
	
	sql =	" SELECT * " +
			" FROM " + speciesContextModelTable.getTableName() + 
			" WHERE " + speciesContextModelTable.getTableName()+"."+speciesContextModelTable.modelRef+" = "+modelKey +
			" ORDER BY " + speciesContextModelTable.id;
	
//System.out.println(sql);
	//Connection con = conFact.getConnection();
	Vector<SpeciesContext> speciesContextList = new Vector<SpeciesContext>();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//showMetaData(rset);

		SpeciesContext speciesContext = null;
		while (rset.next()) {
			speciesContext = getSpeciesContext(dbc, con,rset);
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
public Versionable getVersionable(QueryHashtable dbc, Connection con, User user, VersionableType vType, KeyValue vKey) 
			throws ObjectNotFoundException, SQLException, DataAccessException {
				
	Versionable versionable = (Versionable) dbc.get(vKey);
	if (versionable != null) {
		return versionable;
	} else {
		if (vType.equals(VersionableType.Model)){
			versionable = getModel(dbc, con, user, vKey);
		}else{
			throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
		}
		dbc.put(versionable.getVersion().getVersionKey(),versionable);
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
	StructureTopology structureTopology = model.getStructureTopology();
	ElectricalTopology electricalTopology = model.getElectricalTopology();
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
	// this does not populate the parent, negativeFeature, and positiveFeature columns ... done later with updateStructureKeys()
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
	// add structure references (parent, negativeFeature, positiveFeature) are updated into the newly inserted Structure records
	//   (they were inserted above without their references to parent,negative,positive structures).
	//
	for (Structure structure : structures){
		KeyValue structKey = hash.getDatabaseKey(structure);
		
		KeyValue parentKey = null;
		Structure parentStruct = structureTopology.getParentStructure(structure);
		if (parentStruct!=null){
			parentKey = hash.getDatabaseKey(parentStruct);
		}
		
		KeyValue negKey = null;
		KeyValue posKey = null;
		if (structure instanceof Membrane){
			Membrane membrane = (Membrane)structure;

			Feature negFeature = electricalTopology.getNegativeFeature(membrane);
			if (negFeature != null){
				negKey = hash.getDatabaseKey(negFeature);
			}
			
			Feature posFeature = electricalTopology.getPositiveFeature(membrane);
			if (posFeature != null){
				posKey = hash.getDatabaseKey(posFeature);
			}
		}
				
		StructureKeys structureKeys = new StructureKeys(structKey,parentKey,posKey,negKey);
		reactStepDB.updateStructureKeys(con, structureKeys);
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
	String rbmXmlStr = ModelTable.getRbmForDatabase(model);
	Object[] o = {model,rbmXmlStr};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,modelTable,o,newVersion);
//System.out.println(sql);
	
	if (rbmXmlStr!=null){
		varchar2_CLOB_update(
			con,
			sql,
			rbmXmlStr,
			ModelTable.table,
			newVersion.getVersionKey(),
			ModelTable.table.rbmLarge,
			ModelTable.table.rbmSmall
			);
	}else{
		updateCleanSQL(con,sql);
	}

//	updateCleanSQL(con,sql);
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
