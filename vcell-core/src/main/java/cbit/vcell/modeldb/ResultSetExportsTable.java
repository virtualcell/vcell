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

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class ResultSetExportsTable extends cbit.sql.Table {
	//=======================================================================================================
	//  T H I S    T A B L E    I S    N O T    U S E D    I N     V I R T U A L   C E L L    A N Y M O R E
	//=======================================================================================================
	private static final String TABLE_NAME = "vc_rsetexport";
	private static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	private final Field simulationRef	= new Field("simulationRef",	SQLDataType.integer,		"NOT NULL "+SimulationTable.REF_TYPE+" ON DELETE CASCADE");
	private final Field exportDate		= new Field("exportDate",		SQLDataType.date,			"NOT NULL");
	private final Field exportFormat	= new Field("exportFormat",		SQLDataType.varchar2_1024,	"NOT NULL");
	private final Field exportURL		= new Field("exportURL",		SQLDataType.varchar2_1024,	"NOT NULL");
	

	private final Field fields[] = {simulationRef,exportDate,exportFormat,exportURL};
	
	static final ResultSetExportsTable table = new ResultSetExportsTable();
/**
 * ModelTable constructor comment.
 */
private ResultSetExportsTable() {
	super(TABLE_NAME);
	addFields(fields);
}
///**
// * Insert the method's description here.
// * Creation date: (10/12/2003 4:38:41 PM)
// * @return java.lang.String
// * @param sql java.lang.String
// */
//private static String distinctSelectQuery(String argSql) {
//	StringBuffer sql = new StringBuffer(argSql);
//	sql.insert(7," DISTINCT ");
//	return sql.toString();
//}
///**
// * Insert the method's description here.
// * Creation date: (10/8/2003 12:44:47 PM)
// */
//public static cbit.vcell.export.server.ExportLog[] getExportLogs(ResultSet rset,SessionLog log) throws SQLException{
//
//	//Assumes sorted by simRef
//	
//	cbit.vcell.export.server.ExportLog[] exportLogs = null;
//	
//	Vector currentLogEntriesV = new Vector();
//	java.math.BigDecimal oldSimRef = null;
//	Vector exportLogsV = new Vector();
//	if(rset.next()){
//		while(true){
//			java.math.BigDecimal simRef = null;
//			if(!rset.isAfterLast()){
//				simRef = rset.getBigDecimal(ResultSetExportsTable.table.simulationRef.toString());
//				if(oldSimRef != null && simRef.compareTo(oldSimRef) < 0){
//					throw new RuntimeException("ResultSetExportsTable.getExportLogs - ResultSet not sorted by simulationRef");
//				}
//			}
//			if(oldSimRef == null){
//				oldSimRef = simRef;
//			}
//			if(	currentLogEntriesV.size() > 0 &&
//					(
//						rset.isAfterLast() || 
//						!oldSimRef.equals(simRef)
//					)
//			){
//				cbit.vcell.export.server.ExportLogEntry[] currentLogEntryArr =
//					new cbit.vcell.export.server.ExportLogEntry[currentLogEntriesV.size()];
//				currentLogEntriesV.copyInto(currentLogEntryArr);
//				exportLogsV.add(new cbit.vcell.export.server.ExportLog(new KeyValue(oldSimRef),currentLogEntryArr));
//				oldSimRef = simRef;
//				currentLogEntriesV = new Vector();
//			}
//			if(rset.isAfterLast()){
//				break;
//			}
//			
//			String exportFormat =
//				org.vcell.util.TokenMangler.getSQLRestoredString(
//					rset.getString(ResultSetExportsTable.table.exportFormat.toString()));
//			String exportLocation =
//				org.vcell.util.TokenMangler.getSQLRestoredString(
//					rset.getString(ResultSetExportsTable.table.exportURL.toString()));
//			java.net.URL exportLocationURL = null;
//			try{
//				exportLocationURL = new java.net.URL(exportLocation);
//			}catch(java.net.MalformedURLException e){
//				String urlError = "ResultSetExportsTablee.getExportLogs "+e.getMessage();
//				if(log != null){
//					log.alert(urlError);
//				}else{
//					System.out.println(urlError);
//				}
//			}
//			if(exportFormat != null && exportLocationURL != null){
//				java.math.BigDecimal eleKey = rset.getBigDecimal(ResultSetExportsTable.table.id.toString());
//				currentLogEntriesV.add(
//					new cbit.vcell.export.server.ExportLogEntry(
//						exportFormat,exportLocationURL,
//						new KeyValue(simRef),new KeyValue(eleKey)));
//			}
//			rset.next();
//		}
//	}
//	if(exportLogsV.size() > 0){
//		exportLogs = new cbit.vcell.export.server.ExportLog[exportLogsV.size()];
//		exportLogsV.copyInto(exportLogs);
//	}
//
//	return exportLogs;
//}
///**
// * Insert the method's description here.
// * Creation date: (10/8/2003 12:18:46 PM)
// */
//public static String getSQLInfo(User user,KeyValue simulationRef) {
//	
//	String sql;
//	
//	Field[] f = {new StarField(ResultSetExportsTable.table)};
//	Table[] t = {SimulationTable.table,ResultSetExportsTable.table};
//	
//	String condition = 
//		ResultSetExportsTable.table.simulationRef.getQualifiedColName() + " = " + SimulationTable.table.id.getQualifiedColName() +
//		" AND " +
//		SimulationTable.table.id.getQualifiedColName() + " = " + simulationRef.toString();
//
//	String special = "ORDER BY "+ResultSetExportsTable.table.simulationRef.getQualifiedColName();
//	
//	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special);
//	return distinctSelectQuery(sql);
//}
///**
// * Insert the method's description here.
// * Creation date: (10/8/2003 12:18:46 PM)
// */
//public static String getSQLInfo(User user,boolean bAll) {
//	
//	String sql;
//	
//	Field[] f = {new StarField(ResultSetExportsTable.table)};
//	Table[] t = {SimulationTable.table,ResultSetExportsTable.table};
//	
//	String condition = 
//		ResultSetExportsTable.table.simulationRef.getQualifiedColName() + " = " + SimulationTable.table.id.getQualifiedColName() +
//		(!bAll?
//			" AND " + SimulationTable.table.ownerRef.getQualifiedColName() + " = " + user.getID().toString()
//			:"");
//
//	String special = "ORDER BY "+ResultSetExportsTable.table.simulationRef.getQualifiedColName();
//	
//	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special);
//	return distinctSelectQuery(sql);
//}
///**
// * This method was created in VisualAge.
// * @return java.lang.String
// * @param key KeyValue
// * @param modelName java.lang.String
// */
//public static String getSQLValueList(KeyValue simKey, String exportFormat,String exportURL) {
//
//	StringBuffer buffer = new StringBuffer();
//	buffer.append("(");
//	buffer.append(Table.NewSEQ+",");
//	buffer.append(simKey+",");
//	buffer.append("CURRENT_TIMESTAMP"+",");
//	buffer.append("'"+org.vcell.util.TokenMangler.getSQLEscapedString(exportFormat)+"',");
//	buffer.append("'"+org.vcell.util.TokenMangler.getSQLEscapedString(exportURL)+"'");
//	buffer.append(")");
//
//	return buffer.toString();
//}
}
