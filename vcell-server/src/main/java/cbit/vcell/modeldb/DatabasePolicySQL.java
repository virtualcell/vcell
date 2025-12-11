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

import cbit.sql.ServerStartUpTasks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.DatabaseSyntax;
import org.vcell.util.document.SpecialUser;
import org.vcell.util.document.User;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.resource.PropertyLoader;
/**
 * This type was created in VisualAge.
 */
public class DatabasePolicySQL {
	public static final Logger lg = LogManager.getLogger(DbDriver.class);
	public static boolean bAllowAdministrativeAccess = false;
	private static final String alias = "_alias";


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
	if (lg.isTraceEnabled()) {
		lg.trace("\nDatabasePolicySQL.enforceOwnershipDelete(), sql = "+sb.toString());
	}
	return sb.toString();
}


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
	}else {
		throw new RuntimeException("database table "+vTable.getTableName() +
				" not supported for DatabasePolicySQL.enforceOwnershipInsert()");
	}
	if (lg.isTraceEnabled()){
		lg.trace("\nDatabasePolicySQL.enforceOwnershipInsert(), sql = "+sb.toString());
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
public static String enforceOwnershipSelect(User user, Field[] fields, Table[] tables, LeftOuterJoin outerJoin, String conditions, String special) {
	return enforceOwnershipSelect(user,fields,tables,outerJoin,conditions,special,false);
}


public static class LeftOuterJoin {
	public final Table leftTable;
	public final Table rightTable;
	public final Field leftField;
	public final Field rightField;
	public LeftOuterJoin(Table leftTable, Table rightTable, Field leftField, Field rightField){
		this.leftTable = leftTable;
		this.rightTable = rightTable;
		this.leftField = leftField;
		this.rightField = rightField;
		if (leftTable==rightTable){
			throw new RuntimeException("table1 and table2 must be unique");
		}
		if (leftField==rightField){
			throw new RuntimeException("left and right fields must be unique");
		}
		boolean LEFT_T1_RIGHT_T1 = (leftField.getTableName().equals(leftTable.getTableName()) && rightField.getTableName().equals(rightTable.getTableName()));
		boolean LEFT_T2_RIGHT_T2 = (leftField.getTableName().equals(rightTable.getTableName()) && rightField.getTableName().equals(leftTable.getTableName()));
		if (!LEFT_T1_RIGHT_T1 && !LEFT_T2_RIGHT_T2){
			throw new RuntimeException("DatabasePolicy.Join left and right fields must match tables");
		}
	}
}

public static String enforceOwnershipSelect(User user, Field[] fields, Table[] tables, LeftOuterJoin leftOuterJoin, String conditions, String special, boolean bCheckPermission) {

	boolean isAdministrator = user.getName().equals(PropertyLoader.ADMINISTRATOR_USERID) && user.getID().equals(new org.vcell.util.document.KeyValue(PropertyLoader.ADMINISTRATOR_ID));
	if (bAllowAdministrativeAccess && isAdministrator){
		bCheckPermission = false;
	}
	
	if (conditions.contains("(+)")){
		throw new RuntimeException("Replace Oracle outer join syntax (+) with LEFT JOIN");
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
	//
	// Add caller's tables not mentioned in outer join
	//
	boolean bFirst = true;
	for(int c = 0;c < tables.length;c+= 1){
		Table table = tables[c];
		if (leftOuterJoin!=null && ((table == leftOuterJoin.leftTable) || (table == leftOuterJoin.rightTable))){
			// add this table later as "LEFT JOIN rightTable ON leftTable.leftField = rightTable.rightField"
			continue;
		}
		if (!bFirst){
			sb.append(",");
		}
		sb.append(table.getTableName());
		bFirst = false;
	}
	//
	// add outer join clause
	//
	if (leftOuterJoin!=null) {
		if (!bFirst){
			sb.append(",");
		}
		List<Table> tableList = Arrays.asList(tables);
		if (!tableList.contains(leftOuterJoin.leftTable) || !tableList.contains(leftOuterJoin.rightTable)) {
			throw new RuntimeException("outerJoin tables must be taken from table list");
		}
		sb.append(leftOuterJoin.leftTable.getTableName() + " LEFT JOIN " + leftOuterJoin.rightTable.getTableName() +
				" ON " + leftOuterJoin.leftField.getQualifiedColName() + " = " + leftOuterJoin.rightField.getQualifiedColName());
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
	if (lg.isTraceEnabled()){
		lg.trace("sql = "+sb.toString());
	}
	return sb.toString();
}


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
	if (lg.isTraceEnabled()){
		lg.trace("\nDatabasePolicySQL.enforceOwnershipUpdate(), sql = "+sb.toString());
	}
	return sb.toString();
}


static String getVTableDirectSelectClause(VersionTable vTable,User user) {

	String vcellSupportClause = "";
	if (user instanceof SpecialUser specialUser && specialUser.isVCellSupport()){
		vcellSupportClause = " OR " + GroupTable.table.userRef.getQualifiedColName() + " = " + ServerStartUpTasks.getVCellSupportID();
	}
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
							//
							//
							vcellSupportClause +
						" ) "+
					" ) ";
	return sql;
}
}
