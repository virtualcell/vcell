package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.math.BigDecimal;
import cbit.sql.*;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.util.Version;
import cbit.util.VersionInfo;
import cbit.vcell.math.MathDescription;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import cbit.vcell.geometry.Geometry;
/**
 * This type was created in VisualAge.
 */
public class MathDescTable extends cbit.sql.VersionTable {
	private static final String TABLE_NAME = "vc_math";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field geometryRef	= new Field("geometryRef",	"integer",	"NOT NULL "+GeometryTable.REF_TYPE);
	//public final Field language 		= new Field("language",		"long raw",	"NOT NULL");
	public final Field language 		= new Field("language",		"CLOB",	"NOT NULL");
	
	private final Field fields[] = {geometryRef,language};
	
	public static final MathDescTable table = new MathDescTable();

/**
 * ModelTable constructor comment.
 */
private MathDescTable() {
	super(TABLE_NAME,MathDescTable.REF_TYPE);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset,Connection con,SessionLog log) throws SQLException,cbit.util.DataAccessException {
	
	KeyValue geomRef = new KeyValue(rset.getBigDecimal(MathDescTable.table.geometryRef.toString()));
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	
	return new cbit.vcell.math.MathInfo(geomRef,version);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special) {
	
	UserTable userTable = UserTable.table;
	MathDescTable vTable = MathDescTable.table;
	String sql;
	//Field[] f = {userTable.userid,new cbit.sql.StarField(vTable)};
	Field[] f = new Field[] {vTable.id,userTable.userid};
	f = (Field[])cbit.util.BeanUtils.addElements(f,vTable.versionFields);
	f = (Field[])cbit.util.BeanUtils.addElement(f,vTable.geometryRef);
	
	Table[] t = {vTable,userTable};
	String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName();  // links in the userTable
	if (extraConditions != null && extraConditions.trim().length()>0){
		condition += " AND "+extraConditions;
	}
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special);
	return sql;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public MathDescription getMathDescription(ResultSet rset, Connection con,SessionLog log) 
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);

	//
	// get MathDescription Data (language) (MUST BE READ FIRST)
	//
	/*
	byte[] mathDescriptionData = null;
	try {
		mathDescriptionData = rset.getBytes(MathDescTable.table.language.toString());
	}catch (SQLException e){
	System.out.println("SQLException error_code = "+e.getErrorCode()+", localized Message = "+e.getLocalizedMessage());
		log.exception(e);
		try {
			rset.close();
		}catch (SQLException e2){
			log.exception(e2);
		}
		throw e;
	}
	if (rset.wasNull() || mathDescriptionData==null || mathDescriptionData.length==0){
		throw new DataAccessException("no data stored for MathDescription");
	}
	String mathDescriptionDataString = new String(mathDescriptionData);
	*/
	//
	String mathDescriptionDataString = (String) DbDriver.getLOB(rset,MathDescTable.table.language.toString());
	if(mathDescriptionDataString == null || mathDescriptionDataString.length() == 0){
		throw new DataAccessException("no data stored for MathDescription");
	}
	//
	//System.out.println("mathDescriptionDataString '"+mathDescriptionDataString+"'");
	
	MathDescription mathDescription = new MathDescription(version);
	
	//
	// setGeometry is done in calling parent
	//
	//mathDescription.setGeometry(geom);
	
	cbit.vcell.math.CommentStringTokenizer tokens = new cbit.vcell.math.CommentStringTokenizer(mathDescriptionDataString);
	try {
		mathDescription.read_database(tokens);
	} catch (cbit.vcell.math.MathException e) {
		e.printStackTrace(System.out);
		//
		// failed reading VCML first time, maybe VCML has out-of-order variables ... try to fix it.
		//
		log.print("MathException '"+e.getMessage()+"' while reading VCML for MathDescription, trying to reorder variables in VCML and reread VCML");
		try {
			String newVCML = MathDescription.getVCML_withReorderedVariables(version,mathDescriptionDataString);
			tokens = new cbit.vcell.math.CommentStringTokenizer(newVCML);
			mathDescription = new MathDescription(version);
			mathDescription.read_database(tokens);
		}catch (Exception e2){
			e2.printStackTrace(System.out);
			throw new cbit.util.DataAccessException(e2.getMessage());
		}
	} catch (Exception e){
		e.printStackTrace(System.out);
		throw new cbit.util.DataAccessException(e.getMessage());
	}
	//
	return mathDescription;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(MathDescription mathDescription,KeyValue geomKey,Version version) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version) + ",");
	buffer.append(geomKey+",");
	buffer.append("EMPTY_CLOB()"+")");
	return buffer.toString();
}
}