package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.sql.VersionTable;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelInfo;
/**
 * This type was created in VisualAge.
 */
public class ModelTable extends cbit.sql.VersionTable {
	private static final String TABLE_NAME = "vc_model";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	private final Field fields[] = null;
	
	public static final ModelTable table = new ModelTable();
	
/**
 * ModelTable constructor comment.
 */
private ModelTable() {
	super(TABLE_NAME,ModelTable.REF_TYPE);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset, Connection con,SessionLog log) throws SQLException,DataAccessException {
	
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	return new ModelInfo(version);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special) {
	UserTable userTable = UserTable.table;
	ModelTable vTable = ModelTable.table;
	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable)};
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
 * @return Model
 * @param rset ResultSet
 * @param log SessionLog
 */
public Model getModel(ResultSet rset, Connection con,SessionLog log) throws SQLException,DataAccessException {

	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	return new Model(version);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(Model model, Version version) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version)+")");
	return buffer.toString();
}
}
