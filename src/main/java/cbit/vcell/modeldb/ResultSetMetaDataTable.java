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
class ResultSetMetaDataTable extends cbit.sql.Table {
	//=======================================================================================================
	//  T H I S    T A B L E    I S    N O T    U S E D    I N     V I R T U A L   C E L L    A N Y M O R E
	//=======================================================================================================
	private static final String TABLE_NAME = "vc_rsetMetaData";
	private static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	private final Field simRef 			= new Field("simRef",		SQLDataType.integer,		"UNIQUE NOT NULL "+SimulationTable.REF_TYPE+" ON DELETE CASCADE");
	private final Field startDate		= new Field("startDate",	SQLDataType.date,			"NOT NULL");
	private final Field endDate			= new Field("endDate",		SQLDataType.date,			"");
	private final Field dataFilePath	= new Field("dataFilePath",	SQLDataType.varchar_255,	"NOT NULL");
	private final Field jobIndex		= new Field("jobIndex",		SQLDataType.integer,		"");	
	

	private final Field fields[] = {simRef,startDate,endDate,dataFilePath,jobIndex};
	
	static final ResultSetMetaDataTable table = new ResultSetMetaDataTable();

/**
 * ModelTable constructor comment.
 */
private ResultSetMetaDataTable() {
	super(TABLE_NAME);
	addFields(fields);
}


///**
// * Insert the method's description here.
// * Creation date: (12/28/00 12:03:15 PM)
// * @return java.sql.Date
// * @param rset java.sql.ResultSet
// * @param columnName java.lang.String
// */
//private java.util.Date getDate(ResultSet rset, String columnName) throws SQLException, DataAccessException {
//	//
//	// Do this stuff because ResultSet only returns java.sql.Date without time information
//	//
//	java.util.Date date = null;
//	java.sql.Date DBDate = rset.getDate(columnName);
//	java.sql.Time DBTime = rset.getTime(columnName);
//	if (rset.wasNull()){
//		return null;
//	}
//	try {
//		java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
//		date = sdf.parse(DBDate + " " + DBTime);
//	} catch (java.text.ParseException e) {
//		throw new DataAccessException(e.getMessage());
//	}
//	return date;
//}
//
//
///**
// * This method was created in VisualAge.
// * @return VCImage
// * @param rset ResultSet
// * @param log SessionLog
// */
//public SolverResultSetInfo getSolverResultSetInfo(ResultSet rset, SessionLog log, SimulationInfo simInfo) throws SQLException, DataAccessException {
//	KeyValue simKey = new KeyValue(rset.getBigDecimal(ResultSetMetaDataTable.table.simRef.toString()));
//	if (rset.wasNull()){
//		simKey = null;
//	}
//	if (!simKey.equals(simInfo.getVersion().getVersionKey())){
//		throw new DataAccessException("ResultSetMetaData.simKey not same as simInfo");
//	}
//	int parsedJobIndex = rset.getInt(jobIndex.toString());
//
//	java.util.Date parsedStartingDate = getDate(rset, startDate.toString());
//	java.util.Date parsedEndingDate	= getDate(rset, endDate.toString());
//	
//	String parsedPathFileName = rset.getString(dataFilePath.toString()).trim();
//	if (parsedPathFileName!=null){
//		parsedPathFileName = parsedPathFileName.trim();
//	}
//
//	SolverResultSetInfo rsetInfo = new SolverResultSetInfo(new VCSimulationDataIdentifier(simInfo.getAuthoritativeVCSimulationIdentifier(), parsedJobIndex), parsedPathFileName, parsedStartingDate, parsedEndingDate);
//	return rsetInfo;
//}
//
//
///**
// * This method was created in VisualAge.
// * @return java.lang.String
// * @param key KeyValue
// * @param modelName java.lang.String
// */
//public String getSQLUpdateList(KeyValue simKey, SolverResultSetInfo rsetInfo) {
//
//	StringBuffer buffer = new StringBuffer();
//	buffer.append(this.simRef.toString() + " = " + simKey+",");
//	if (rsetInfo.getStartingDate() == null){
//		buffer.append(this.startDate.toString() + " = " + "null" + ",");
//	}else{
//		buffer.append(this.startDate.toString() + " = " + VersionTable.formatDateToOracle(rsetInfo.getStartingDate())+",");
//	}
//	if (rsetInfo.getEndingDate() == null){
//		buffer.append(this.endDate.toString() + " = " + "null" + ",");
//	}else{
//		buffer.append(this.endDate.toString() + " = " + VersionTable.formatDateToOracle(rsetInfo.getEndingDate())+",");
//	}
//	buffer.append(this.dataFilePath.toString() + " = '" + rsetInfo.getDataFilePath()+"',");
//	buffer.append(this.jobIndex.toString() + " = " + rsetInfo.getVCSimulationDataIdentifier().getJobIndex());
//
//	return buffer.toString();
//}
//
//
///**
// * This method was created in VisualAge.
// * @return java.lang.String
// * @param key KeyValue
// * @param modelName java.lang.String
// */
//public String getSQLValueList(KeyValue key, SolverResultSetInfo rsetInfo) {
//
//	KeyValue simKey = rsetInfo.getVCSimulationDataIdentifier().getSimulationKey();
//	
//	StringBuffer buffer = new StringBuffer();
//	buffer.append("(");
//	buffer.append(key+",");
//	buffer.append(simKey+",");
//
//	//
//	// store Starting date
//	//
//	if (rsetInfo.getStartingDate() == null){
//		buffer.append("null"+",");
//	}else{
//		buffer.append(VersionTable.formatDateToOracle(rsetInfo.getStartingDate()) + ",");
//	}
//	
//	//
//	// store Ending date
//	//
//	if (rsetInfo.getEndingDate()==null){
//		buffer.append("null"+",");
//	}else{
//		buffer.append(VersionTable.formatDateToOracle(rsetInfo.getEndingDate())+",");
//	}
//
//	buffer.append("'"+rsetInfo.getDataFilePath()+"',");
//	buffer.append(rsetInfo.getVCSimulationDataIdentifier().getJobIndex()+")");
//
//	return buffer.toString();
//}
}
