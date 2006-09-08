package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
import java.math.BigDecimal;
import cbit.sql.*;
import java.sql.*;
import cbit.vcell.server.User;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.server.SessionLog;
/**
 * This type was created in VisualAge.
 */
public class SimContextTable extends cbit.sql.VersionTable {
	private static final String TABLE_NAME = "vc_simcontext";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field mathRef 		= new Field("mathRef",		"integer",	MathDescTable.REF_TYPE);
	public final Field modelRef 		= new Field("modelRef",		"integer",	"NOT NULL "+ModelTable.REF_TYPE);
	public final Field geometryRef 	= new Field("geometryRef",	"integer",	"NOT NULL "+GeometryTable.REF_TYPE);
	public final Field charSize		= new Field("charSize",		"NUMBER",	"");

	private final Field fields[] = {mathRef,modelRef,geometryRef,charSize};
	
	public static final SimContextTable table = new SimContextTable();

/**
 * ModelTable constructor comment.
 */
private SimContextTable() {
	super(TABLE_NAME,SimContextTable.REF_TYPE);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public cbit.sql.VersionInfo getInfo(ResultSet rset,Connection con,SessionLog log) throws SQLException,cbit.vcell.server.DataAccessException {

	KeyValue mathRef = null;
	java.math.BigDecimal mathRefValue = rset.getBigDecimal(SimContextTable.table.mathRef.toString());
	if (!rset.wasNull()){
		mathRef = new KeyValue(mathRefValue);
	}
	
	KeyValue geomRef = new KeyValue(rset.getBigDecimal(SimContextTable.table.geometryRef.toString()));

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(SimContextTable.table.modelRef.toString()));

	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);

	return new cbit.vcell.mapping.SimulationContextInfo(mathRef,geomRef,modelRef,version);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special) {
	UserTable userTable = UserTable.table;
	SimContextTable vTable = SimContextTable.table;
	String sql;
	
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable)};
	Table[] t = {vTable,userTable};

	String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName()+" ";  // links in the userTable
	if (extraConditions != null && extraConditions.trim().length()>0){
		condition += " AND "+extraConditions;
	}

	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special);
	return sql;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SimulationContext
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 * @deprecated shouldn't do recursive query
 */
public SimulationContext getSimContext(	Connection con,User user,ResultSet rset,SessionLog log,
										GeomDbDriver geomDB,ModelDbDriver modelDB,MathDescriptionDbDriver mathDB) 
							throws SQLException,cbit.vcell.server.DataAccessException, java.beans.PropertyVetoException {
			
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	KeyValue geomKey = new KeyValue(rset.getBigDecimal(SimContextTable.table.geometryRef.toString()));
	cbit.vcell.geometry.Geometry geom = (cbit.vcell.geometry.Geometry)geomDB.getVersionable(con,user, VersionableType.Geometry,geomKey,false);
	KeyValue modelKey = new KeyValue(rset.getBigDecimal(SimContextTable.table.modelRef.toString()));
	cbit.vcell.model.Model model  = (cbit.vcell.model.Model)modelDB.getVersionable(con,user, VersionableType.Model,modelKey);

	//
	// read characteristic size (may be null)
	//
	Double characteristicSize = null;
	BigDecimal size = rset.getBigDecimal(charSize.toString());
	if (!rset.wasNull() && size!=null){
		characteristicSize = new Double(size.doubleValue());
	}

	//
	// get mathKey (may be null)
	//
	MathDescription mathDesc = null;
	BigDecimal mathKeyValue = rset.getBigDecimal(SimContextTable.table.mathRef.toString());
	if (!rset.wasNull()){
		KeyValue mathKey = new KeyValue(mathKeyValue);
		mathDesc  = (MathDescription)mathDB.getVersionable(con,user, VersionableType.MathDescription,mathKey);
	}
	
	SimulationContext simContext = new SimulationContext(model,geom,mathDesc,version);
	if (characteristicSize!=null){
		simContext.setCharacteristicSize(characteristicSize);
	}
	return simContext;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(SimulationContext simContext,KeyValue mathDescKey,KeyValue modelKey,KeyValue geomKey,Version version) {
			
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version)+",");
	buffer.append(mathDescKey + ",");
	buffer.append(modelKey+",");
	buffer.append(geomKey+",");
	buffer.append(simContext.getCharacteristicSize()+")");
	return buffer.toString();
}
}