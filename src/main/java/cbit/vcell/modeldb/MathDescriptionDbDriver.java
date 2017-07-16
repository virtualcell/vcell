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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.Table;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
/**
 * This type was created in VisualAge.
 */
public class MathDescriptionDbDriver extends DbDriver {
	public static final UserTable userTable = UserTable.table;
	public static final MathDescTable mathDescTable = MathDescTable.table;
	public static final GeometryTable geomTable = GeometryTable.table;
	private GeomDbDriver geomDB = null;
/**
 * MathDescrDbDriver constructor comment.
 * @param connectionFactory cbit.sql.ConnectionFactory
 * @param sessionLog cbit.vcell.server.SessionLog
 */
public MathDescriptionDbDriver(GeomDbDriver argGeomDB,SessionLog sessionLog) {
	super(argGeomDB.dbSyntax,argGeomDB.keyFactory,sessionLog);
	this.geomDB = argGeomDB;
}
/**
 * removeModel method comment.
 */
private void deleteMathDescriptionSQL(Connection con,User user, KeyValue mathDescKey) throws SQLException, DependencyException, DataAccessException {

	//
	// check for external references (from MathModel and SimContext and Simulation)
	//
	failOnExternalRefs(con, MathModelTable.table.mathRef, MathModelTable.table, mathDescKey,MathDescTable.table);
	failOnExternalRefs(con, SimContextTable.table.mathRef, SimContextTable.table, mathDescKey,MathDescTable.table);
	failOnExternalRefs(con, SimulationTable.table.mathRef, SimulationTable.table, mathDescKey,MathDescTable.table);

	//
	// delete MathDescription (ResultSetMetaData is also deleted  ... ON DELETE CASCADE)
	//
	String sql = DatabasePolicySQL.enforceOwnershipDelete(user,mathDescTable,mathDescTable.id.getQualifiedColName() + " = " + mathDescKey);
	updateCleanSQL(con, sql);
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
	if (vType.equals(VersionableType.MathDescription)){
		deleteMathDescriptionSQL(con, user, vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param mathDescKey cbit.sql.KeyValue
 */
private MathDescription getMathDescriptionSQL(QueryHashtable dbc, Connection con,User user, KeyValue mathDescKey) 
				throws SQLException,DataAccessException,ObjectNotFoundException {

	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(mathDescTable)};
	Table[] t = {mathDescTable,userTable};
	String condition = mathDescTable.id.getQualifiedColName() + " = " + mathDescKey +
			" AND " + userTable.id.getQualifiedColName() + " = " + mathDescTable.ownerRef.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null);
//System.out.println(sql);
	MathDescription mathDescription = null;
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		if (rset.next()) {
			//
			// note: must call mathDescTable.getMathDescription() first (rset.getBytes(language) must be called first)
			//
			mathDescription = mathDescTable.getMathDescription(rset,con,log,dbSyntax);
			//
			// get Geometry reference and assign to mathDescription
			//
			java.math.BigDecimal bigD = rset.getBigDecimal(MathDescTable.table.geometryRef.toString());
			KeyValue geomRef = null;
			if (!rset.wasNull()) {
				geomRef = new KeyValue(bigD);
			}else{
				throw new DataAccessException("Error:  Geometry Reference Cannot be Null for MathDescription");
			}
			Geometry geom = (Geometry)geomDB.getVersionable(dbc, con,user,VersionableType.Geometry,geomRef,false);
			try {
				mathDescription.setGeometry(geom);
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				throw new DataAccessException("DataAccess Exception: "+e.getMessage());
			}
			
		} else {
			throw new ObjectNotFoundException("MathDescription id=" + mathDescKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return mathDescription;
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
		if (vType.equals(VersionableType.MathDescription)){
			versionable = getMathDescriptionSQL(dbc, con, user, vKey);
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
private void insertMathDescriptionSQL(InsertHashtable hash, Connection con, User user, MathDescription mathDescription, KeyValue updatedGeometryKey, 
									Version newVersion, boolean bVersionChildren)
				throws MathException, SQLException, DataAccessException, RecordChangedException {
					
	String sql = null;
	Object[] o = {mathDescription, updatedGeometryKey};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,mathDescTable,o,newVersion,dbSyntax);
	//
	updateCleanSQL(con,sql);
	updateCleanLOB(	con,mathDescTable.id.toString(),newVersion.getVersionKey(),
			mathDescTable.tableName,
			mathDescTable.language,
			mathDescription.getVCML_database(),dbSyntax);

	hash.put(mathDescription,newVersion.getVersionKey());
	
	insertMathDescExternalDataLink(con,user,mathDescription,newVersion.getVersionKey());

}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, MathDescription mathDescription, KeyValue updatedGeometryKey, String name, boolean bVersion) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	Version newVersion = insertVersionableInit(hash, con, user, mathDescription, name, mathDescription.getDescription(), bVersion);
	try {
		insertMathDescriptionSQL(hash, con, user, mathDescription, updatedGeometryKey, newVersion, bVersion);
		return newVersion.getVersionKey();
	} catch (MathException e) {
		log.exception(e);
		throw new DataAccessException("MathException: " + e.getMessage());
	}
}


private void insertMathDescExternalDataLink(Connection con,User user,MathDescription mathDesc,KeyValue newMathDescKey)throws DataAccessException{
	try{
		ExternalDataIdentifier[] extDataIDArr =
			fieldDataDBOperation(
					con, keyFactory, user, FieldDataDBOperationSpec.createGetExtDataIDsSpec(user)).extDataIDArr;
		boolean bExtDataInserted[] = new boolean[extDataIDArr.length];
		FieldFunctionArguments[] fieldFuncArgsArr = FieldUtilities.getFieldFunctionArguments(mathDesc);
		for(int i=0;i<fieldFuncArgsArr.length;i+= 1){
			for(int k=0;k<extDataIDArr.length;k+= 1){
				if( !bExtDataInserted[k] && extDataIDArr[k].getName().equals(fieldFuncArgsArr[i].getFieldName())){
					bExtDataInserted[k] = true;
					KeyValue newKey = keyFactory.getNewKey(con);
					updateCleanSQL(con,
							"INSERT INTO "+
							MathDescExternalDataLinkTable.table.getTableName()+
							" VALUES "+
							MathDescExternalDataLinkTable.table.getSQLValueList(
									newKey, newMathDescKey, extDataIDArr[k].getKey())
							);
					break;
				}
			}
		}
	}catch(Exception e){
		e.printStackTrace(System.out);
		throw new DataAccessException("Error inserting MathDescription-ExtrnalData link\n"+e.getMessage());
	}
}

/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash, Connection con, User user, MathDescription mathDescription, KeyValue updatedGeometryKey, boolean bVersion) 
			throws DataAccessException, SQLException, RecordChangedException {
				
	Version newVersion = null;
	try {
		newVersion = updateVersionableInit(hash, con, user, mathDescription, bVersion);
		insertMathDescriptionSQL(hash, con, user, mathDescription, updatedGeometryKey, newVersion, bVersion);
	} catch (MathException e) {
		log.exception(e);
		throw new DataAccessException("MathException: " + e.getMessage());
	}
	return newVersion.getVersionKey();
}
}
