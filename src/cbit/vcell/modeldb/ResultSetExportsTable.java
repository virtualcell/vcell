package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.text.*;
import cbit.vcell.solver.*;
import cbit.sql.*;
import cbit.image.*;
import cbit.vcell.server.*;
import java.sql.*;
import java.util.Vector;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
/**
 * This type was created in VisualAge.
 */
public class ResultSetExportsTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_rsetexport";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simulationRef	= new Field("simulationRef",	"integer",			"NOT NULL "+SimulationTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field exportDate		= new Field("exportDate",		"date",				"NOT NULL");
	public final Field exportFormat		= new Field("exportFormat",		"varchar2(1024)",	"NOT NULL");
	public final Field exportURL		= new Field("exportURL",		"varchar2(1024)",	"NOT NULL");
	

	private final Field fields[] = {simulationRef,exportDate,exportFormat,exportURL};
	
	public static final ResultSetExportsTable table = new ResultSetExportsTable();
/**
 * ModelTable constructor comment.
 */
private ResultSetExportsTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/2003 4:38:41 PM)
 * @return java.lang.String
 * @param sql java.lang.String
 */
private static String distinctSelectQuery(String argSql) {
	StringBuffer sql = new StringBuffer(argSql);
	sql.insert(7," DISTINCT ");
	return sql.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 12:44:47 PM)
 */
public static cbit.vcell.export.server.ExportLog[] getExportLogs(ResultSet rset,SessionLog log) throws SQLException{

	//Assumes sorted by simRef
	
	cbit.vcell.export.server.ExportLog[] exportLogs = null;
	
	Vector currentLogEntriesV = new Vector();
	java.math.BigDecimal oldSimRef = null;
	Vector exportLogsV = new Vector();
	if(rset.next()){
		while(true){
			java.math.BigDecimal simRef = null;
			if(!rset.isAfterLast()){
				simRef = rset.getBigDecimal(ResultSetExportsTable.table.simulationRef.toString());
				if(oldSimRef != null && simRef.compareTo(oldSimRef) < 0){
					throw new RuntimeException("ResultSetExportsTable.getExportLogs - ResultSet not sorted by simulationRef");
				}
			}
			if(oldSimRef == null){
				oldSimRef = simRef;
			}
			if(	currentLogEntriesV.size() > 0 &&
					(
						rset.isAfterLast() || 
						!oldSimRef.equals(simRef)
					)
			){
				cbit.vcell.export.server.ExportLogEntry[] currentLogEntryArr =
					new cbit.vcell.export.server.ExportLogEntry[currentLogEntriesV.size()];
				currentLogEntriesV.copyInto(currentLogEntryArr);
				exportLogsV.add(new cbit.vcell.export.server.ExportLog(new KeyValue(oldSimRef),currentLogEntryArr));
				oldSimRef = simRef;
				currentLogEntriesV = new Vector();
			}
			if(rset.isAfterLast()){
				break;
			}
			
			String exportFormat =
				cbit.util.TokenMangler.getSQLRestoredString(
					rset.getString(ResultSetExportsTable.table.exportFormat.toString()));
			String exportLocation =
				cbit.util.TokenMangler.getSQLRestoredString(
					rset.getString(ResultSetExportsTable.table.exportURL.toString()));
			java.net.URL exportLocationURL = null;
			try{
				exportLocationURL = new java.net.URL(exportLocation);
			}catch(java.net.MalformedURLException e){
				String urlError = "ResultSetExportsTablee.getExportLogs "+e.getMessage();
				if(log != null){
					log.alert(urlError);
				}else{
					System.out.println(urlError);
				}
			}
			if(exportFormat != null && exportLocationURL != null){
				java.math.BigDecimal eleKey = rset.getBigDecimal(ResultSetExportsTable.table.id.toString());
				currentLogEntriesV.add(
					new cbit.vcell.export.server.ExportLogEntry(
						exportFormat,exportLocationURL,
						new KeyValue(simRef),new KeyValue(eleKey)));
			}
			rset.next();
		}
	}
	if(exportLogsV.size() > 0){
		exportLogs = new cbit.vcell.export.server.ExportLog[exportLogsV.size()];
		exportLogsV.copyInto(exportLogs);
	}

	return exportLogs;
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 12:18:46 PM)
 */
public static String getSQLInfo(User user,KeyValue simulationRef) {
	
	String sql;
	
	Field[] f = {new StarField(ResultSetExportsTable.table)};
	Table[] t = {SimulationTable.table,ResultSetExportsTable.table};
	
	String condition = 
		ResultSetExportsTable.table.simulationRef.getQualifiedColName() + " = " + SimulationTable.table.id.getQualifiedColName() +
		" AND " +
		SimulationTable.table.id.getQualifiedColName() + " = " + simulationRef.toString();

	String special = "ORDER BY "+ResultSetExportsTable.table.simulationRef.getQualifiedColName();
	
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special);
	return distinctSelectQuery(sql);
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 12:18:46 PM)
 */
public static String getSQLInfo(User user,boolean bAll) {
	
	String sql;
	
	Field[] f = {new StarField(ResultSetExportsTable.table)};
	Table[] t = {SimulationTable.table,ResultSetExportsTable.table};
	
	String condition = 
		ResultSetExportsTable.table.simulationRef.getQualifiedColName() + " = " + SimulationTable.table.id.getQualifiedColName() +
		(!bAll?
			" AND " + SimulationTable.table.ownerRef.getQualifiedColName() + " = " + user.getID().toString()
			:"");

	String special = "ORDER BY "+ResultSetExportsTable.table.simulationRef.getQualifiedColName();
	
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special);
	return distinctSelectQuery(sql);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public static String getSQLValueList(KeyValue simKey, String exportFormat,String exportURL) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Table.NewSEQ+",");
	buffer.append(simKey+",");
	buffer.append("SYSDATE"+",");
	buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(exportFormat)+"',");
	buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(exportURL)+"'");
	buffer.append(")");

	return buffer.toString();
}
}
