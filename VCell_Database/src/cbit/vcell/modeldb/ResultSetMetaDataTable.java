package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import cbit.rmi.event.VCSimulationDataIdentifier;
import cbit.sql.Field;
import cbit.sql.Table;
import cbit.sql.VersionTable;
import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.document.KeyValue;
import cbit.vcell.simulation.SimulationInfo;

/**
 * This type was created in VisualAge.
 */
public class ResultSetMetaDataTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_rsetMetaData";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simRef 		= new Field("simRef",		"integer",		"UNIQUE NOT NULL "+SimulationTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field startDate	= new Field("startDate",	"date",			"NOT NULL");
	public final Field endDate		= new Field("endDate",		"date",			"");
	public final Field dataFilePath	= new Field("dataFilePath",	"varchar(255)",	"NOT NULL");
	public final Field jobIndex		= new Field("jobIndex",		"integer",		"");	
	

	private final Field fields[] = {simRef,startDate,endDate,dataFilePath,jobIndex};
	
	public static final ResultSetMetaDataTable table = new ResultSetMetaDataTable();

/**
 * ModelTable constructor comment.
 */
private ResultSetMetaDataTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * Insert the method's description here.
 * Creation date: (12/28/00 12:03:15 PM)
 * @return java.sql.Date
 * @param rset java.sql.ResultSet
 * @param columnName java.lang.String
 */
private java.util.Date getDate(ResultSet rset, String columnName) throws SQLException, DataAccessException {
	//
	// Do this stuff because ResultSet only returns java.sql.Date without time information
	//
	java.util.Date date = null;
	java.sql.Date DBDate = rset.getDate(columnName);
	java.sql.Time DBTime = rset.getTime(columnName);
	if (rset.wasNull()){
		return null;
	}
	try {
		java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
		date = sdf.parse(DBDate + " " + DBTime);
	} catch (java.text.ParseException e) {
		throw new DataAccessException(e.getMessage());
	}
	return date;
}


/**
 * This method was created in VisualAge.
 * @return VCImage
 * @param rset ResultSet
 * @param log SessionLog
 */
public SolverResultSetInfo getSolverResultSetInfo(ResultSet rset, SessionLog log, SimulationInfo simInfo) throws SQLException, DataAccessException {
	KeyValue simKey = new KeyValue(rset.getBigDecimal(ResultSetMetaDataTable.table.simRef.toString()));
	if (rset.wasNull()){
		simKey = null;
	}
	if (!simKey.equals(simInfo.getVersion().getVersionKey())){
		throw new DataAccessException("ResultSetMetaData.simKey not same as simInfo");
	}
	int parsedJobIndex = rset.getInt(jobIndex.toString());
	SolverResultSetInfo rsetInfo = new SolverResultSetInfo(new VCSimulationDataIdentifier(simInfo.getAuthoritativeVCSimulationIdentifier(), parsedJobIndex));
	rsetInfo.setDataFilePath(rset.getString(dataFilePath.toString()));

	try {
		rsetInfo.setStartingDate(getDate(rset, startDate.toString()));
		rsetInfo.setEndingDate(getDate(rset, endDate.toString()));
	}catch (java.beans.PropertyVetoException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
	
	String pathFileName = rset.getString(dataFilePath.toString()).trim();

	return rsetInfo;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLUpdateList(KeyValue simKey, SolverResultSetInfo rsetInfo) {

	StringBuffer buffer = new StringBuffer();
	buffer.append(this.simRef.toString() + " = " + simKey+",");
	if (rsetInfo.getStartingDate() == null){
		buffer.append(this.startDate.toString() + " = " + "null" + ",");
	}else{
		buffer.append(this.startDate.toString() + " = " + VersionTable.formatDateToOracle(rsetInfo.getStartingDate())+",");
	}
	if (rsetInfo.getEndingDate() == null){
		buffer.append(this.endDate.toString() + " = " + "null" + ",");
	}else{
		buffer.append(this.endDate.toString() + " = " + VersionTable.formatDateToOracle(rsetInfo.getEndingDate())+",");
	}
	buffer.append(this.dataFilePath.toString() + " = '" + rsetInfo.getDataFilePath()+"',");
	buffer.append(this.jobIndex.toString() + " = " + rsetInfo.getVCSimulationDataIdentifier().getJobIndex());

	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue simKey, SolverResultSetInfo rsetInfo) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(simKey+",");

	//
	// store Starting date
	//
	if (rsetInfo.getStartingDate() == null){
		buffer.append("null"+",");
	}else{
		buffer.append(VersionTable.formatDateToOracle(rsetInfo.getStartingDate()) + ",");
	}
	
	//
	// store Ending date
	//
	if (rsetInfo.getEndingDate()==null){
		buffer.append("null"+",");
	}else{
		buffer.append(VersionTable.formatDateToOracle(rsetInfo.getEndingDate())+",");
	}

	buffer.append("'"+rsetInfo.getDataFilePath()+"',");
	buffer.append(rsetInfo.getVCSimulationDataIdentifier().getJobIndex()+")");

	return buffer.toString();
}
}