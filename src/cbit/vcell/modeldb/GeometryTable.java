package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;
import java.sql.*;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.sql.*;
import cbit.vcell.server.SessionLog;
import cbit.vcell.geometry.*;
import cbit.vcell.server.*;
/**
 * This type was created in VisualAge.
 */
public class GeometryTable extends cbit.sql.VersionTable {
	private static final String TABLE_NAME = "vc_geometry";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field dimension	= new Field("dimension",	"integer",	"NOT NULL");
	public final Field originX		= new Field("originX",		"NUMBER",	"NOT NULL");
	public final Field originY		= new Field("originY",		"NUMBER",	"NOT NULL");
	public final Field originZ		= new Field("originZ",		"NUMBER",	"NOT NULL");
	public final Field extentRef	= new Field("extentRef",	"integer",	"NOT NULL "+ExtentTable.REF_TYPE);
	public final Field imageRef		= new Field("imageRef",		"integer",	ImageTable.REF_TYPE);
	
	private final Field fields[] = {dimension,originX,originY,originZ,extentRef,imageRef};
	
	public static final GeometryTable table = new GeometryTable();
/**
 * ModelTable constructor comment.
 */
private GeometryTable() {	
	super(TABLE_NAME,GeometryTable.REF_TYPE);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return Model
 * @param rset ResultSet
 * @param log SessionLog
 */
public Geometry getGeometry(ResultSet rset, Connection con,SessionLog log) throws SQLException,DataAccessException,PropertyVetoException{

	int dim = rset.getInt(dimension.toString());

	double ox = rset.getBigDecimal(originX.toString()).doubleValue();
	double oy = rset.getBigDecimal(originY.toString()).doubleValue();
	double oz = rset.getBigDecimal(originZ.toString()).doubleValue();
	
	double ex = rset.getBigDecimal(ExtentTable.table.extentX.toString()).doubleValue();
	double ey = rset.getBigDecimal(ExtentTable.table.extentY.toString()).doubleValue();
	double ez = rset.getBigDecimal(ExtentTable.table.extentZ.toString()).doubleValue();

	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	
	Geometry geometry = new Geometry(version,dim);
	geometry.getGeometrySpec().setOrigin(new cbit.util.Origin(ox,oy,oz));
	geometry.getGeometrySpec().setExtent(new cbit.util.Extent(ex,ey,ez));
	
	return geometry;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset,Connection con, SessionLog log) throws SQLException,DataAccessException {

	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	
	int dim = rset.getInt(GeometryTable.table.dimension.toString());
	
	double oX = rset.getBigDecimal(GeometryTable.table.originX.toString()).doubleValue();
	double oY = rset.getBigDecimal(GeometryTable.table.originY.toString()).doubleValue();
	double oZ = rset.getBigDecimal(GeometryTable.table.originZ.toString()).doubleValue();
	cbit.util.Origin origin = new cbit.util.Origin(oX,oY,oZ);
	
	double extentX = rset.getBigDecimal(ExtentTable.table.extentX.toString()).doubleValue();
	double extentY = rset.getBigDecimal(ExtentTable.table.extentY.toString()).doubleValue();
	double extentZ = rset.getBigDecimal(ExtentTable.table.extentZ.toString()).doubleValue();
	cbit.util.Extent extent = new cbit.util.Extent(extentX,extentY,extentZ);

	KeyValue imgRef = null;
	java.math.BigDecimal bigDecimal = rset.getBigDecimal(GeometryTable.table.imageRef.toString());
	if (!rset.wasNull()){
		imgRef = new KeyValue(bigDecimal);
	}

	GeometryInfo geomInfo = new GeometryInfo(version,dim,extent,origin,imgRef);

	
	return geomInfo;

	/*GeometryInfo geomInfo = new GeometryInfo();

	geomInfo.dimension = rset.getInt(GeometryTable.dimension.toString());
	geomInfo.originX = rset.getDouble(GeometryTable.originX.toString());
	geomInfo.originY = rset.getDouble(GeometryTable.originY.toString());
	geomInfo.originZ = rset.getDouble(GeometryTable.originZ.toString());
	geomInfo.extentRef = new KeyValue(rset.getBigDecimal(GeometryTable.extentRef.toString(), 0));
	geomInfo.imageRef = new KeyValue(rset.getBigDecimal(GeometryTable.imageRef.toString(), 0));

	geomInfo.version = getVersion(rset,log);
	
	return geomInfo;
	*/
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special,boolean bCheckPermission) {
	UserTable userTable = UserTable.table;
	GeometryTable gTable = this;
	ExtentTable eTable = ExtentTable.table;
	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(gTable),eTable.extentX,eTable.extentY,eTable.extentZ};
	Table[] t = {gTable,userTable,eTable};
	String condition = eTable.id.getQualifiedColName() + " = " + gTable.extentRef.getQualifiedColName() +             // links in the extent table
					   " AND " + userTable.id.getQualifiedColName() + " = " + gTable.ownerRef.getQualifiedColName();  // links in the userTable
	if (extraConditions != null && extraConditions.trim().length()>0){
		condition += " AND "+extraConditions;
	}
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special,bCheckPermission);
	return sql;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue imageKey, Geometry geom,KeyValue sizeKey,Version version) {
							
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version) + ",");
	buffer.append(geom.getDimension() + ",");
	buffer.append(geom.getOrigin().getX() + ",");
	buffer.append(geom.getOrigin().getY() + ",");
	buffer.append(geom.getOrigin().getZ() + ",");
	buffer.append(sizeKey + ",");
	buffer.append(imageKey + ")");
	return buffer.toString();
}
}
