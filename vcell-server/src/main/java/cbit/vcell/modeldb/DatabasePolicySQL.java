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
import java.util.Arrays;
import java.util.List;

import org.vcell.db.DatabaseSyntax;
import org.vcell.util.document.User;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.resource.PropertyLoader;
/**
 * This type was created in VisualAge.
 */
public class DatabasePolicySQL {
	public static boolean bSilent = false;
	public static boolean bAllowAdministrativeAccess = false;
	private static final String alias = "_alias";

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param fields cbit.sql.Field[]
 * @param conditions java.lang.String[]
 * @param special java.lang.String
 */
public static String enforceOwnershipDelete(User user, VersionTable vTable, String conditions) {
	StringBuffer sb = new StringBuffer();
	sb.append("DELETE FROM " + vTable.getTableName());
	sb.append(" WHERE ");
	sb.append(" ( " + conditions + " ) ");
	String ownerCondition = " ( " + vTable.getTableName() + "." + vTable.ownerRef + " = " + user.getID() + " ) ";
	sb.append(" AND " + ownerCondition);
	//	if((special != null) && (special.length() != 0)){
	//		sb.append (special);
	//	}
	if (!bSilent){
		System.out.println("\nDatabasePolicySQL.enforceOwnershipDelete(), sql = "+sb.toString());
	}
	return sb.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param fields cbit.sql.Field[]
 * @param conditions java.lang.String[]
 * @param special java.lang.String
 */
public static String enforceOwnershipInsert(User user, VersionTable vTable, Object[] valueData, org.vcell.util.document.Version version, DatabaseSyntax dbSyntax) 
									throws org.vcell.util.DataAccessException {
	//
	if (!version.getOwner().compareEqual(user)) {
		throw new org.vcell.util.DataAccessException("enforceOwnershipInsert User " + user + " Not Equal to Version owner " + version.getOwner());
	}
	StringBuffer sb = new StringBuffer();
	sb.append("INSERT INTO " + vTable.getTableName() + " ");
	sb.append(vTable.getSQLColumnList());
	sb.append(" VALUES ");
	if (vTable instanceof GeometryTable) {
		sb.append(((GeometryTable) vTable).getSQLValueList(	(org.vcell.util.document.KeyValue) valueData[0], 
															(cbit.vcell.geometry.Geometry) valueData[1], 
															(org.vcell.util.document.KeyValue) valueData[2], 
															version));
	}else if (vTable instanceof ImageTable) {
		sb.append(((ImageTable) vTable).getSQLValueList(	(cbit.image.VCImage) valueData[0], 
															(org.vcell.util.document.KeyValue) valueData[1], 
															version));
	}else if (vTable instanceof SimContextTable) {
		sb.append(((SimContextTable) vTable).getSQLValueList(	(cbit.vcell.mapping.SimulationContext) valueData[0], 
																(org.vcell.util.document.KeyValue) valueData[1],
																(org.vcell.util.document.KeyValue) valueData[2],
																(org.vcell.util.document.KeyValue) valueData[3],
																(String) valueData[4],
																version));
	}else if (vTable instanceof MathDescTable) {
		sb.append(((MathDescTable) vTable).getSQLValueList(	(cbit.vcell.math.MathDescription) valueData[0], 
															(org.vcell.util.document.KeyValue) valueData[1], 
															version, dbSyntax));
	}else if (vTable instanceof SimulationTable) {
		sb.append(((SimulationTable) vTable).getSQLValueList(	(cbit.vcell.solver.Simulation) valueData[0], 
															(org.vcell.util.document.KeyValue) valueData[1], 
															version, dbSyntax));
	}else if (vTable instanceof BioModelTable) {
		sb.append(((BioModelTable) vTable).getSQLValueList(	(cbit.vcell.biomodel.BioModelMetaData) valueData[0],
															(String) valueData[1],
															version));
	}else if (vTable instanceof MathModelTable) {
		sb.append(((MathModelTable) vTable).getSQLValueList((cbit.vcell.mathmodel.MathModelMetaData) valueData[0],
															(String) valueData[1],
															version));
	}else if (vTable instanceof ModelTable) {
		sb.append(((ModelTable) vTable).getSQLValueList(	(cbit.vcell.model.Model) valueData[0],
															(String) valueData[1],
															version));
	}
	if (!bSilent){
		System.out.println("\nDatabasePolicySQL.enforceOwnershipInsert(), sql = "+sb.toString());
	}
	return sb.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param fields cbit.sql.Field[]
 * @param conditions java.lang.String[]
 * @param special java.lang.String
 */ 
public static String enforceOwnershipSelect(User user, Field[] fields, Table[] tables, OuterJoin outerJoin, String conditions, String special, DatabaseSyntax dbSyntax) {
	return enforceOwnershipSelect(user,fields,tables,outerJoin,conditions,special,dbSyntax,false);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param fields cbit.sql.Field[]
 * @param conditions java.lang.String[]
 * @param special java.lang.String
 */ 
public enum JoinOp {
	RIGHT_OUTER_JOIN("RIGHT OUTER JOIN"),
	LEFT_OUTER_JOIN("LEFT OUTER JOIN");
	
	final String opString;
	private JoinOp(String op){
		this.opString = op;
	}
}

public static class OuterJoin {
	public final Table table1;
	public final Table table2;
	public final JoinOp joinOp;
	public final Field leftField;
	public final Field rightField;
	public OuterJoin(Table t1, Table t2, JoinOp op, Field left, Field right){
		this.table1 = t1;
		this.table2 = t2;
		this.joinOp = op;
		this.leftField = left;
		this.rightField = right;
		if (t1==t2){
			throw new RuntimeException("table1 and table2 must be unique");
		}
		if (left==right){
			throw new RuntimeException("left and right fields must be unique");
		}
		boolean LEFT_T1_RIGHT_T1 = (leftField.getTableName().equals(table1.getTableName()) && rightField.getTableName().equals(table2.getTableName()));
		boolean LEFT_T2_RIGHT_T2 = (leftField.getTableName().equals(table2.getTableName()) && rightField.getTableName().equals(table1.getTableName()));
		if (!LEFT_T1_RIGHT_T1 && !LEFT_T2_RIGHT_T2){
			throw new RuntimeException("DatabasePolicy.Join left and right fields must match tables");
		}
	}
}

public static String enforceOwnershipSelect(User user, Field[] fields, Table[] tables, OuterJoin outerJoin, String conditions, String special, DatabaseSyntax dbSyntax, boolean bCheckPermission) {

	boolean isAdministrator = user.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT) && user.getID().equals(new org.vcell.util.document.KeyValue(PropertyLoader.ADMINISTRATOR_ID));
	if (bAllowAdministrativeAccess && isAdministrator){
		bCheckPermission = false;
	}
	
	if (dbSyntax==DatabaseSyntax.POSTGRES && conditions.contains("(+)")){
		throw new RuntimeException("Postgres does not support (+) syntax for outer joins");
	}
		
 /**

	List of Versionables
 
 	DB Table		VersionableType		class									TopLevel	Reachablity
 	---------------------------------------------------------------------------------------------------
	VC_IMAGE		VCImage				cbit.image.VCImage						Y			Geometry, MathModelMetaData->MathDescription->Geometry, BioModelMetaData->SimulationContext->Geometry
	VC_GEOMETRY		Geometry			cbit.vcell.geometry.Geometry			Y			MathModelMetaData->MathDescription,BioModelMetaData->SimulationContext
	VC_MATH			MathDescription		cbit.vcell.math.MathDescription			N			MathModelMetaData,BioModelMetaData->SimulationContext
	VC_MODEL		Model				cbit.vcell.model.Model					N			BioModelMetaData
	VC_SIMCONTEXT	SimulationContext	cbit.vcell.mapping.SimulationContext	N			BioModelMetaData
	VC_SIMULATION	Simulation			cbit.vcell.solver.Simulation			N			BioModelMetaData,MathModelMetaData
	VC_BIOMODEL		BioModelMetaData	cbit.vcell.biomodel.BioModelMetaData	Y			-
	VC_MATHMODEL	MathModelMetaData	cbit.vcell.mathmodel.MathModelMetaData	Y			-

	Reachablity per Versionable
 
 **/
	StringBuffer sb = new StringBuffer();
	if (conditions == null){
		throw new IllegalArgumentException("null conditions");
	}
	//
	sb.append ("SELECT ");
	//
	// Add fields caller wants selected
	//
	for(int c = 0;c < fields.length;c+= 1){
		sb.append(fields[c].getQualifiedColName());
		if(c != (fields.length-1)){
			sb.append(",");
		}
	}
	//
	// Check for duplicate table names
	// Disallow GroupTable if we are responsible for Permisions
	// Create AccessEnforment if we are responsible for Permisions
	//
	String accessEnforcement = null;
	for(int c = 0;c < tables.length;c+= 1){
		//Check for duplicated table types
		for(int i = 0; i < tables.length;i+= 1){
			if(i != c){
				if(tables[i].getClass().getName().equals(tables[c].getClass().getName())){
					throw new RuntimeException("Duplicate table types not allowed");
				}
			}
		}
		if(bCheckPermission && tables[c] instanceof GroupTable){
			throw new RuntimeException(tables[c].getClass().getName()+" not allowed in table list");
		}
		if(bCheckPermission && tables[c] instanceof VersionTable){
			if(accessEnforcement != null){
				throw new RuntimeException("DatabasePolicySQL.enforceOwnershipSelect: Only one VersionTable allowed in table list");
			}
			//
			// Create query sql allow select if:
			// 1. user is owner of vTable
			// 2. vTAble has PUBLIC access
			// 3. user is in group for vTable
			//
			VersionTable vTable = (VersionTable)tables[c];
			accessEnforcement = getVTableDirectSelectClause(vTable,user);
		}
	}

	//
	sb.append(" FROM ");
	//
	// Add Group table if we are responsible for permissions
	//
	if (bCheckPermission){
		sb.append(GroupTable.table.getTableName()+",");
	}
	switch (dbSyntax){
	case ORACLE:{
		//
		// Add caller's tables
		//
		for(int c = 0;c < tables.length;c+= 1){
			Table table = tables[c];
			sb.append(table.getTableName());
			if(c != (tables.length-1)){
				sb.append(",");
			}
		}
		if (outerJoin!=null){
			throw new RuntimeException("explicit outer join syntax not yet implemented for ORACLE");
		}
		break;
	}
	case POSTGRES:{
		//
		// Add caller's tables not mentioned in outer join
		//
		for(int c = 0;c < tables.length;c+= 1){
			Table table = tables[c];
			if (outerJoin!=null && ((table == outerJoin.table1) || (table == outerJoin.table2))){
				continue;
			}
			sb.append(table.getTableName());
			if(c != (tables.length-1)){
				sb.append(",");
			}
		}
		//
		// add outer join clause
		//
		if (outerJoin!=null){
			List<Table> tableList = Arrays.asList(tables);
			if (!tableList.contains(outerJoin.table1) || !tableList.contains(outerJoin.table2)){
				throw new RuntimeException("outerJoin tables must be taken from table list");
			}
			sb.append(outerJoin.table1.getTableName()+" "+outerJoin.joinOp.opString+" "+outerJoin.table2.getTableName()+
					" ON "+outerJoin.leftField.getQualifiedColName()+" = "+outerJoin.rightField.getQualifiedColName());
		}
		break;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
	//
	sb.append(" WHERE ");
	//
	// Add caller's conditions for select
	//
	if (conditions!=null && conditions.trim().length()!=0){
		sb.append(" ( "+conditions+" )");
	}
	
	//
	// Add AccessEnforcement if we are responsible for permissions
	//
	if (bCheckPermission){
		sb.append(" AND " +accessEnforcement);
	}
	
	//
	// Add special conditions (e.g. ORDER BY)
	//
	if((special != null) && (special.length() != 0)){
		sb.append (" "+special);
	}
	if (!bSilent){
		System.out.println("\nDatabasePolicySQL.enforceOwnershipSelect(), sql = "+sb.toString());
	}
	return sb.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param fields cbit.sql.Field[]
 * @param conditions java.lang.String[]
 * @param special java.lang.String
 */
public static String enforceOwnershipUpdate(User user, VersionTable vTable, String setValues, String conditions) {
	StringBuffer sb = new StringBuffer();
	sb.append("UPDATE " + vTable.getTableName());
	sb.append(" SET " + setValues);
	sb.append(" WHERE ");
	sb.append(" ( " + conditions + " ) ");
	String ownerCondition = " ( " + vTable.getTableName() + "." + vTable.ownerRef + " = " + user.getID() + " ) ";
	sb.append(" AND " + ownerCondition);
	//	if((special != null) && (special.length() != 0)){
	//		sb.append (special);
	//	}
	if (!bSilent){
		System.out.println("\nDatabasePolicySQL.enforceOwnershipUpdate(), sql = "+sb.toString());
	}
	return sb.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/2001 5:09:00 PM)
 * @return java.lang.String
 * @param vTable cbit.sql.VersionTable
 * @param gTable cbit.vcell.modeldb.GroupTable
 * @param user cbit.vcell.server.User
 */
static String getVTableDirectSelectClause(VersionTable vTable,User user) {

	String sql = 	" ( "+
						vTable.privacy.getQualifiedColName() + " = " + GroupTable.table.groupid.getQualifiedColName() +
						" AND " +
						" ( "+
							//
							// this user is the owner
							//
							vTable.ownerRef.getQualifiedColName() + " = " + user.getID() +
							" OR " +
							//
							// the object is public
							//
							vTable.privacy.getQualifiedColName() + " = " + org.vcell.util.document.GroupAccess.GROUPACCESS_ALL +
							" OR " +
							//
							// this user is in the access control list for this object
							//
							GroupTable.table.userRef.getQualifiedColName() + " = " + user.getID() +
						" ) "+
					" ) ";
	return sql;
}
}
