package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.QueryHashtable;
import cbit.sql.Table;
import cbit.sql.VersionTable;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContextInfo;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.Model;
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
public VersionInfo getInfo(ResultSet rset,Connection con,SessionLog log) throws SQLException,DataAccessException {

	KeyValue mathRef = null;
	java.math.BigDecimal mathRefValue = rset.getBigDecimal(SimContextTable.table.mathRef.toString());
	if (!rset.wasNull()){
		mathRef = new KeyValue(mathRefValue);
	}
	
	KeyValue geomRef = new KeyValue(rset.getBigDecimal(SimContextTable.table.geometryRef.toString()));

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(SimContextTable.table.modelRef.toString()));

	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);

	return new SimulationContextInfo(mathRef,geomRef,modelRef,version);
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
public SimulationContext getSimContext(QueryHashtable dbc, Connection con,User user,ResultSet rset,SessionLog log,
										GeomDbDriver geomDB,ModelDbDriver modelDB,MathDescriptionDbDriver mathDB) 
							throws SQLException,DataAccessException, java.beans.PropertyVetoException {
			
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	KeyValue geomKey = new KeyValue(rset.getBigDecimal(SimContextTable.table.geometryRef.toString()));
	Geometry geom = (Geometry)geomDB.getVersionable(dbc, con,user, VersionableType.Geometry,geomKey,false);
	KeyValue modelKey = new KeyValue(rset.getBigDecimal(SimContextTable.table.modelRef.toString()));
	Model model  = (Model)modelDB.getVersionable(dbc, con,user, VersionableType.Model,modelKey);

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
		mathDesc  = (MathDescription)mathDB.getVersionable(dbc, con,user, VersionableType.MathDescription,mathKey);
	}
	
	SimulationContext simContext = new SimulationContext(model,geom,mathDesc,version, mathDesc.isStoch());
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