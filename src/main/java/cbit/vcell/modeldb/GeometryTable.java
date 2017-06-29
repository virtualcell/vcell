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

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
/**
 * This type was created in VisualAge.
 */
public class GeometryTable extends cbit.vcell.modeldb.VersionTable {
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
	geometry.getGeometrySpec().setOrigin(new org.vcell.util.Origin(ox,oy,oz));
	geometry.getGeometrySpec().setExtent(new org.vcell.util.Extent(ex,ey,ez));
	
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
	org.vcell.util.Origin origin = new org.vcell.util.Origin(oX,oY,oZ);
	
	double extentX = rset.getBigDecimal(ExtentTable.table.extentX.toString()).doubleValue();
	double extentY = rset.getBigDecimal(ExtentTable.table.extentY.toString()).doubleValue();
	double extentZ = rset.getBigDecimal(ExtentTable.table.extentZ.toString()).doubleValue();
	org.vcell.util.Extent extent = new org.vcell.util.Extent(extentX,extentY,extentZ);

	KeyValue imgRef = null;
	java.math.BigDecimal bigDecimal = rset.getBigDecimal(GeometryTable.table.imageRef.toString());
	if (!rset.wasNull()){
		imgRef = new KeyValue(bigDecimal);
	}
	
	String softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.toString());

	GeometryInfo geomInfo = new GeometryInfo(version,dim,extent,origin,imgRef,VCellSoftwareVersion.fromString(softwareVersion));

	
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
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(gTable),eTable.extentX,eTable.extentY,eTable.extentZ,swvTable.softwareVersion};
	Table[] t = {gTable,userTable,eTable,swvTable};
	String condition = eTable.id.getQualifiedColName() + " = " + gTable.extentRef.getQualifiedColName() +             // links in the extent table
					   " AND " + userTable.id.getQualifiedColName() + " = " + gTable.ownerRef.getQualifiedColName() +  // links in the userTable
			           " AND " + gTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
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
public static String getParentsPermissionSQL(KeyValue geomKey,User user){
	return "select 'math' type,"+MathModelTable.table.name.getQualifiedColName()+","+MathModelTable.table.privacy.getQualifiedColName()+" " +
	"from "+MathModelTable.table.getTableName()+","+MathDescTable.table.getTableName()+","+GeometryTable.table.getTableName()+" " +
	"where " +
	GeometryTable.table.id.getQualifiedColName()+" = "+geomKey+" and " +
	MathDescTable.table.geometryRef.getQualifiedColName()+"="+GeometryTable.table.id.getQualifiedColName()+" and " +
	MathModelTable.table.mathRef.getQualifiedColName()+" = "+MathDescTable.table.id.getQualifiedColName()+" and " +
	"("+MathModelTable.table.ownerRef.getQualifiedColName()+" = "+user.getID()+" or "+MathModelTable.table.privacy.getQualifiedColName()+" = "+GroupAccess.GROUPACCESS_ALL.intValue()+" or " +
	"("+MathModelTable.table.privacy.getQualifiedColName()+" != "+GroupAccess.GROUPACCESS_NONE.intValue()+" and "+user.getID()+" in (select "+GroupTable.table.userRef.getUnqualifiedColName()+" from "+GroupTable.table.getTableName()+" where "+GroupTable.table.groupid.getUnqualifiedColName()+" = "+MathModelTable.table.privacy.getQualifiedColName()+"))) " +
	"union " +
	"select 'bio' type,"+BioModelTable.table.name.getQualifiedColName()+","+BioModelTable.table.privacy.getQualifiedColName()+" " +
	"from "+BioModelTable.table.getTableName()+","+BioModelSimContextLinkTable.table.getTableName()+","+SimContextTable.table.getTableName()+","+GeometryTable.table.getTableName()+" " +
	"where " +
	GeometryTable.table.id.getQualifiedColName()+" = "+geomKey+" and " +
	SimContextTable.table.geometryRef.getQualifiedColName()+" = "+GeometryTable.table.id.getQualifiedColName()+" and " +
	BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+" = "+SimContextTable.table.id.getQualifiedColName()+" and " +
	BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+" = "+BioModelTable.table.id.getQualifiedColName()+" and " +
	"( " +
	BioModelTable.table.ownerRef.getQualifiedColName()+" = "+user.getID()+" or "+BioModelTable.table.privacy.getQualifiedColName()+" = "+GroupAccess.GROUPACCESS_ALL.intValue()+" or " +
	"("+BioModelTable.table.privacy.getQualifiedColName()+" != "+GroupAccess.GROUPACCESS_NONE.intValue()+" and "+user.getID()+" in (select "+GroupTable.table.userRef.getUnqualifiedColName()+" from "+GroupTable.table.getTableName()+" where "+GroupTable.table.groupid.getUnqualifiedColName()+" = "+BioModelTable.table.privacy.getQualifiedColName()+")))";

}
}
