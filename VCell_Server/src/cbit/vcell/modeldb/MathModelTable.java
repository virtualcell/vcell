package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.sql.VersionTable;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.util.Version;
import cbit.util.VersionInfo;
import cbit.vcell.mathmodel.MathModelChildSummary;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.mathmodel.MathModelMetaData;
/**
 * This type was created in VisualAge.
 */
public class MathModelTable extends cbit.sql.VersionTable {
	private static final String TABLE_NAME = "vc_mathmodel";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field mathRef			= new Field("mathRef",			"integer",	"NOT NULL "+MathDescTable.REF_TYPE);
	public final Field childSummaryLarge	= new Field("childSummaryLRG",	"CLOB",				"");
	public final Field childSummarySmall	= new Field("childSummarySML",	"VARCHAR2(4000)",	"");
	
	private final Field fields[] = {mathRef,childSummaryLarge,childSummarySmall};
	
	public static final MathModelTable table = new MathModelTable();

/**
 * ModelTable constructor comment.
 */
private MathModelTable() {
	super(TABLE_NAME,MathModelTable.REF_TYPE);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset,Connection con,SessionLog log) throws SQLException,cbit.util.DataAccessException {

	String serialDbChildSummary =
		DbDriver.varchar2_CLOB_get(rset,MathModelTable.table.childSummarySmall,MathModelTable.table.childSummaryLarge);

	MathModelChildSummary mathModelChildSummary = null;
	if (serialDbChildSummary!=null){
		mathModelChildSummary = mathModelChildSummary.fromDatabaseSerialization(serialDbChildSummary);
	}

	KeyValue mathRef = new KeyValue(rset.getBigDecimal(table.mathRef.toString()));
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	
	return new MathModelInfo(version, mathRef, mathModelChildSummary);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special) {
	
	UserTable userTable = UserTable.table;
	MathModelTable vTable = MathModelTable.table;
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
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public MathModelMetaData getMathModelMetaData(ResultSet rset, SessionLog log, MathModelDbDriver mathModelDbDriver, Connection con) 
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	KeyValue mathModelKey = version.getVersionKey();

	KeyValue mathRef = new KeyValue(rset.getBigDecimal(table.mathRef.toString()));

	//
	// get Simulation Keys for bioModelKey
	//
	KeyValue simKeys[] = mathModelDbDriver.getSimulationEntriesFromMathModel(con, mathModelKey);

	MathModelMetaData mathModelMetaData = new MathModelMetaData(version,mathRef,simKeys);

	return mathModelMetaData;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public MathModelMetaData getMathModelMetaData(ResultSet rset, Connection con,SessionLog log, KeyValue simulationKeys[]) 
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);

	KeyValue mathRef = new KeyValue(rset.getBigDecimal(table.mathRef.toString()));
	
	MathModelMetaData mathModelMetaData = new MathModelMetaData(version,mathRef,simulationKeys);

	return mathModelMetaData;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(MathModelMetaData mathModelMetaData,String serialMMChildSummary, Version version) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version) + ",");
	buffer.append(mathModelMetaData.getMathKey() + ",");

	if (serialMMChildSummary==null){
		buffer.append("null,null");
	}else if (DbDriver.varchar2_CLOB_is_Varchar2_OK(serialMMChildSummary)){
		buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE);
	}else{
		buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null");
	}
	
	buffer.append(")");
	return buffer.toString();
}
}