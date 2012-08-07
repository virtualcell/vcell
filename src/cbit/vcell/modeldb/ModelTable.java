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

import java.sql.*;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

import cbit.sql.*;
import cbit.util.xml.XmlUtil;
import cbit.vcell.model.*;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.xml.Xmlproducer;
/**
 * This type was created in VisualAge.
 */
public class ModelTable extends cbit.vcell.modeldb.VersionTable {
	private static final String TABLE_NAME = "vc_model";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field unitSystemXML	= new Field("unitSystemXML",	"VARCHAR2(4000)",	"");
	
	private final Field fields[] = { unitSystemXML };
	
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
	String softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.toString());
	
	return new ModelInfo(version,VCellSoftwareVersion.fromString(softwareVersion));
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special) {
	UserTable userTable = UserTable.table;
	ModelTable vTable = ModelTable.table;
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable),swvTable.softwareVersion};
	Table[] t = {vTable,userTable,swvTable};
	String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() +  // links in the userTable
	           " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
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

	ModelUnitSystem modelUnitSystem = ModelUnitSystem.createDefaultVCModelUnitSystem();
	
	String unitSystemXML = rset.getString(ModelTable.table.unitSystemXML.toString());
	if(!rset.wasNull()){
		unitSystemXML = org.vcell.util.TokenMangler.getSQLRestoredString(unitSystemXML);
		XmlReader xmlReader = new XmlReader(false);
		modelUnitSystem = xmlReader.getUnitSystem(XmlUtil.stringToXML(unitSystemXML, null).getRootElement());
	}

	return new Model(version, modelUnitSystem);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(Model model, Version version) {
	Xmlproducer xmlProducer = new Xmlproducer(false);
	String modelUnitSystemXML = TokenMangler.getSQLEscapedString(XmlUtil.xmlToString(xmlProducer.getXML(model.getUnitSystem())));
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version) + ",");
	buffer.append("'"+modelUnitSystemXML+"'");
	buffer.append(")");
	return buffer.toString();
}
}
