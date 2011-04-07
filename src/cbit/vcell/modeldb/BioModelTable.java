package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.biomodel.*;
import java.beans.*;
import cbit.vcell.solver.*;
import java.math.BigDecimal;
import cbit.sql.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

/**
 * This type was created in VisualAge.
 */
public class BioModelTable extends cbit.vcell.modeldb.VersionTable {
	private static final String TABLE_NAME = "vc_biomodel";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field modelRef				= new Field("modelRef",			"integer",	"NOT NULL "+ModelTable.REF_TYPE);
	public final Field childSummaryLarge	= new Field("childSummaryLRG",	"CLOB",				"");
	public final Field childSummarySmall	= new Field("childSummarySML",	"VARCHAR2(4000)",	"");
	
	private final Field fields[] = {modelRef,childSummaryLarge,childSummarySmall};
	
	public static final BioModelTable table = new BioModelTable();
/**
 * ModelTable constructor comment.
 */
private BioModelTable() {
	super(TABLE_NAME,BioModelTable.REF_TYPE);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public BioModelMetaData getBioModelMetaData(ResultSet rset, SessionLog log, BioModelDbDriver bioModelDbDriver, Connection con) 
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	KeyValue bioModelKey = version.getVersionKey();

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));

	//
	// get Simulation Keys for bioModelKey
	//
	KeyValue simKeys[] = bioModelDbDriver.getSimulationEntriesFromBioModel(con, bioModelKey);

	//
	// get SimulationContext Keys for bioModelKey
	//
	KeyValue simContextKeys[] = bioModelDbDriver.getSimContextEntriesFromBioModel(con, bioModelKey);
	
	//
	//Get VCMetaData XML
	//
	String vcMetaDataXML = VCMetaDataTable.getVCMetaDataXML(rset);
	
	BioModelMetaData bioModelMetaData = new BioModelMetaData(version,modelRef,simContextKeys,simKeys,vcMetaDataXML);
	//
	// setMathDescription is done in calling parent
	//
	//simulation.setMathDescription(mathDesc);

	return bioModelMetaData;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public BioModelMetaData getBioModelMetaData(ResultSet rset, Connection con,SessionLog log, KeyValue simContextKeys[], KeyValue simulationKeys[]) 
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));

	String vcMetaDataXML = VCMetaDataTable.getVCMetaDataXML(rset);
	BioModelMetaData bioModelMetaData = new BioModelMetaData(version,modelRef,simContextKeys,simulationKeys,vcMetaDataXML);
	//
	// setMathDescription is done in calling parent
	//
	//simulation.setMathDescription(mathDesc);

	return bioModelMetaData;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset,Connection con,SessionLog log) throws SQLException,org.vcell.util.DataAccessException {

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));
	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	
	String serialDbChildSummary = DbDriver.varchar2_CLOB_get(rset,BioModelTable.table.childSummarySmall,BioModelTable.table.childSummaryLarge);
	
	return new org.vcell.util.document.BioModelInfo(version, modelRef, serialDbChildSummary);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special) {
	
	UserTable userTable = UserTable.table;
	BioModelTable vTable = BioModelTable.table;
	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable)};
	Table[] t = {vTable,userTable};
	String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName();  // links in the userTable
	if (extraConditions != null && extraConditions.trim().length()>0){
		condition += " AND "+extraConditions;
	}
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special,true);
	return sql;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(BioModelMetaData bioModelMetaData, String serialBMChildSummary,Version version) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version) + ",");
	buffer.append(bioModelMetaData.getModelKey() + ",");

	if (serialBMChildSummary==null){
		buffer.append("null,null");
	}else if (DbDriver.varchar2_CLOB_is_Varchar2_OK(serialBMChildSummary)){
		buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE);
	}else{
		buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null");
	}
	
	buffer.append(")");
	return buffer.toString();
}
}
