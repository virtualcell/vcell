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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.Table;
import cbit.vcell.solver.Simulation;
/**
 * This type was created in VisualAge.
 */
public class SimulationDbDriver extends DbDriver {
	public static final UserTable userTable = UserTable.table;
	public static final MathDescTable mathDescTable = MathDescTable.table;
	public static final SimulationTable simTable = SimulationTable.table;
	private MathDescriptionDbDriver mathDB = null;

/**
 * MathDescrDbDriver constructor comment.
 * @param connectionFactory cbit.sql.ConnectionFactory
 * @param sessionLog cbit.vcell.server.SessionLog
 */
public SimulationDbDriver(MathDescriptionDbDriver argMathDB,SessionLog sessionLog) {
	super(sessionLog);
	this.mathDB = argMathDB;
}

/**
 * removeModel method comment.
 */
private void deleteSimulationSQL(Connection con,User user, KeyValue simKey) throws SQLException, DataAccessException, DependencyException {

	//
	// check for external references (from a BioModel or a MathModel)
	//
	failOnExternalRefs(con, MathModelSimulationLinkTable.table.simRef, MathModelSimulationLinkTable.table, simKey,SimulationTable.table);
	failOnExternalRefs(con, BioModelSimulationLinkTable.table.simRef, BioModelSimulationLinkTable.table, simKey,SimulationTable.table);

	//
	// delete the Simulation (the ResultSetMetaData should be deleted by ON DELETE CASCADE)
	//	
	String sql = DatabasePolicySQL.enforceOwnershipDelete(user,simTable,simTable.id.getQualifiedColName() + " = " + simKey);
	updateCleanSQL(con, sql);

	//
	// try to delete the parentSimulation ... it's OK if it fails
	//
	KeyValue parentSimKey = getParentSimulation(con,user,simKey);
	if (parentSimKey!=null){
		try {
			deleteVersionable(con,user,VersionableType.Simulation,parentSimKey);
		}catch (Exception e){
			System.out.println("failed to delete parent simulation, key="+parentSimKey+": "+e.getMessage());
		}
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
	if (vType.equals(VersionableType.Simulation)){
		deleteSimulationSQL(con, user, vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * getModels method comment.
 */
private KeyValue getParentSimulation(Connection con,User user,KeyValue simKey) throws SQLException, DataAccessException {

	KeyValue parentSimKey = null;
	String sql;
	
	sql = 	" SELECT " + SimulationTable.table.versionParentSimRef.getQualifiedColName()  +
			" FROM " + SimulationTable.table.getTableName() +
			" WHERE " + SimulationTable.table.id.getQualifiedColName() + " = " + simKey;
			
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		if (rset.next()) {
			parentSimKey = new KeyValue(rset.getBigDecimal(SimulationTable.table.versionParentSimRef.getUnqualifiedColName()));
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	return parentSimKey;
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param mathDescKey cbit.sql.KeyValue
 */
private Simulation getSimulationSQL(QueryHashtable dbc, Connection con,User user, KeyValue simKey) 
				throws SQLException,DataAccessException,ObjectNotFoundException {

	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(simTable)};
	Table[] t = {simTable,userTable};
	String condition = simTable.id.getQualifiedColName() + " = " + simKey +
			" AND " + userTable.id.getQualifiedColName() + " = " + simTable.ownerRef.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null);
//System.out.println(sql);
	Simulation simulation = null;
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		if (rset.next()) {
			//
			// note: must call simulationTable.getSimulation() first (rset.getBytes(language) must be called first)
			//
			try {
				simulation = simTable.getSimulation(dbc, rset,log,con,user,mathDB);
			}catch (PropertyVetoException e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}			
		} else {
			throw new org.vcell.util.ObjectNotFoundException("Simulation id=" + simKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return simulation;
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
		if (vType.equals(VersionableType.Simulation)){
			versionable = getSimulationSQL(dbc, con, user, vKey);
		}else{
			throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
		}
		dbc.put(versionable.getVersion().getVersionKey(),versionable);
	}
	return versionable;
}

/**
 * This method was created in VisualAge.
 * @return MathDescribption
 * @param user User
 * @param mathDescription MathDescription
 */
private void insertSimulationSQL(InsertHashtable hash, Connection con, User user, Simulation simulation, KeyValue updatedMathKey,
									Version newVersion, boolean bVersionChildren)
				throws SQLException, DataAccessException, RecordChangedException {
	
	String sql = null;
	Object[] o = {simulation, updatedMathKey};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,simTable,o,newVersion);

	varchar2_CLOB_update(
						con,
						sql,
						simulation.getMathOverrides().getVCML(),
						SimulationTable.table,
						newVersion.getVersionKey(),
						SimulationTable.table.mathOverridesLarge,
						SimulationTable.table.mathOverridesSmall);

	hash.put(simulation,newVersion.getVersionKey());
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, Simulation simulation, KeyValue updatedMathDescriptionKey, String name, boolean bVersion, boolean bMathematicallyEquivalent) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	SimulationVersion newSimulationVersion = insertVersionableInit(hash, con, user, simulation, name, simulation.getDescription(), bVersion, bMathematicallyEquivalent);

	insertSimulationSQL(hash, con, user, simulation, updatedMathDescriptionKey, newSimulationVersion, bVersion);
	return newSimulationVersion.getVersionKey();
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
protected SimulationVersion insertVersionableInit(InsertHashtable hash, Connection con, User user, Versionable versionable, String name, String annot, boolean bVersion, boolean bMathematicallyEquivalent) 
			throws DataAccessException, SQLException, RecordChangedException{

	SimulationVersion dbSimulationVersion = ((Simulation)versionable).getSimulationVersion();
	Version newVersion = insertVersionableInit(hash,con,user,versionable,name,annot,bVersion);
	
	//
	// point to oldest mathematically equivalent ancestor, this will be used for all identification (status/data).
	//
	KeyValue parentSimRef = null;
	if (bMathematicallyEquivalent){
		if (dbSimulationVersion==null){
			throw new RuntimeException("Simulation must have been saved for bMathematicallyEquivalent to be true");
		}
		if (dbSimulationVersion.getParentSimulationReference()!=null){
			////
			//// mathematically equivalent, prior link is transitive  (B->C and A==B, then A->C)
			////
			parentSimRef = dbSimulationVersion.getParentSimulationReference();
		}else{
			////
			//// mathematically equivalent, no prior link (A==B, then A->B)
			////
			parentSimRef = dbSimulationVersion.getVersionKey();
		}
	}
	
	return new SimulationVersion(newVersion.getVersionKey(),
								newVersion.getName(),
								newVersion.getOwner(),
								newVersion.getGroupAccess(),
								newVersion.getBranchPointRefKey(),
								newVersion.getBranchID(),
								newVersion.getDate(),
								newVersion.getFlag(),
								newVersion.getAnnot(),
								parentSimRef);
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash, Connection con, User user, Simulation simulation, KeyValue updatedMathDescriptionKey, boolean bVersion, boolean bMathematicallyEquivalent) 
			throws DataAccessException, SQLException, RecordChangedException {
				
	SimulationVersion newSimulationVersion = updateVersionableInit(hash, con, user, simulation, bVersion, bMathematicallyEquivalent);
	insertSimulationSQL(hash, con, user, simulation, updatedMathDescriptionKey, newSimulationVersion, bVersion);
	return newSimulationVersion.getVersionKey();
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
protected SimulationVersion updateVersionableInit(InsertHashtable hash, Connection con, User user, Versionable versionable, boolean bVersion, boolean bMathematicallyEquivalent) 
			throws DataAccessException, SQLException, RecordChangedException{

	SimulationVersion dbSimulationVersion = ((Simulation)versionable).getSimulationVersion();
	Version newVersion = updateVersionableInit(hash,con,user,versionable,bVersion);
	
	//
	// point to oldest mathematically equivalent ancestor, this will be used for all identification (status/data).
	//
	KeyValue parentSimRef = null;
	if (bMathematicallyEquivalent){
		if (dbSimulationVersion==null){
			throw new RuntimeException("Simulation must have been saved for bMathematicallyEquivalent to be true");
		}
		if (dbSimulationVersion.getParentSimulationReference()!=null){
			////
			//// mathematically equivalent, prior link is transitive  (B->C and A==B, then A->C)
			////
			parentSimRef = dbSimulationVersion.getParentSimulationReference();
		}else{
			////
			//// mathematically equivalent, no prior link (A==B, then A->B)
			////
			parentSimRef = dbSimulationVersion.getVersionKey();
		}
	}
	
	return new SimulationVersion(newVersion.getVersionKey(),
								newVersion.getName(),
								newVersion.getOwner(),
								newVersion.getGroupAccess(),
								newVersion.getBranchPointRefKey(),
								newVersion.getBranchID(),
								newVersion.getDate(),
								newVersion.getFlag(),
								newVersion.getAnnot(),
								parentSimRef);
}


public SimulationRep[] getSimulationReps(Connection con, KeyValue startingSimKey, int numRows)
		throws SQLException, DataAccessException, ObjectNotFoundException {
	String sql = simTable.getPreparedStatement_SimulationReps();
	
	PreparedStatement stmt = con.prepareStatement(sql);

//	System.out.println(sql);
	simTable.setPreparedStatement_SimulationReps(stmt, startingSimKey, numRows);

	ArrayList<SimulationRep> simulationReps = new ArrayList<SimulationRep>();
	try {
		ResultSet rset = stmt.executeQuery();

		//showMetaData(rset);

		while (rset.next()) {
			SimulationRep simulationRep = simTable.getSimulationRep(rset);
			simulationReps.add(simulationRep);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return simulationReps.toArray(new SimulationRep[simulationReps.size()]);
}


public SimulationRep getSimulationRep(Connection con, KeyValue simKey)
		throws SQLException, DataAccessException, ObjectNotFoundException {
	String sql = simTable.getPreparedStatement_SimulationRep();
	
	PreparedStatement stmt = con.prepareStatement(sql);

	//System.out.println(sql);
	simTable.setPreparedStatement_SimulationRep(stmt, simKey);

	ArrayList<SimulationRep> simulationReps = new ArrayList<SimulationRep>();
	try {
		ResultSet rset = stmt.executeQuery();

		//showMetaData(rset);

		while (rset.next()) {
			SimulationRep simulationRep = simTable.getSimulationRep(rset);
			simulationReps.add(simulationRep);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	if (simulationReps.size()==1){
		return simulationReps.get(0);
	}else if (simulationReps.size()==0){
		return null;
	}else{
		throw new RuntimeException("expected 0 or 1 simulationReps for simKey="+simKey);
	}
}



}
