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
import java.sql.Statement;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.db.DatabaseSyntax;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.sql.Field.SQLDataType;
import cbit.util.xml.XmlUtil;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.modeldb.DatabasePolicySQL.JoinOp;
import cbit.vcell.modeldb.DatabasePolicySQL.OuterJoin;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelInfo;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.xml.Xmlproducer;
/**
 * This type was created in VisualAge.
 */
public class ModelTable extends cbit.vcell.modeldb.VersionTable {
	private static final String TABLE_NAME = "vc_model";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field unitSystemXML	= new Field("unitSystemXML",	SQLDataType.varchar2_4000,	"");
	
	public final Field rbmLarge			= new Field("rbmLRG",			SQLDataType.clob_text,		"");
	public final Field rbmSmall			= new Field("rbmSML",			SQLDataType.varchar2_4000,	"");

	private final Field fields[] = { unitSystemXML,rbmLarge,rbmSmall };
	
	public static final ModelTable table = new ModelTable();
	
/**
 * ModelTable constructor comment.
 */
private ModelTable() {
	super(TABLE_NAME,ModelTable.REF_TYPE);
	addFields(fields);
}

public static String getRbmForDatabase(Model model) {
	// add the rbmModelContainer elements
	RbmModelContainer rbmModelContainer = model.getRbmModelContainer();
	if(rbmModelContainer != null && !rbmModelContainer.isEmpty()) {
		Xmlproducer xmlProducer = new Xmlproducer(false);
		Element rbmModelContainerElement = xmlProducer.getXML(rbmModelContainer);
		Document doc = new Document();
		Element clone = (Element)rbmModelContainerElement.clone();
		doc.setRootElement(clone);
		String xmlString = XmlUtil.xmlToString(doc, false);
		return xmlString;
//		System.out.println(xmlString);
	}
	return null;
}
public static void readRbmElement(Connection con,Model model,DatabaseSyntax dbSyntax) throws SQLException, DataAccessException {
	Statement stmt = null;
	try{
		stmt = con.createStatement();
		ResultSet rset =
			stmt.executeQuery(
				"SELECT * FROM "+ModelTable.table.getTableName() +
				" WHERE "+
				ModelTable.table.id.getUnqualifiedColName()+" = "+model.getVersion().getVersionKey().toString());
		if(rset.next()){
			String rbmXMLStr = DbDriver.varchar2_CLOB_get(rset, ModelTable.table.rbmSmall, ModelTable.table.rbmLarge,dbSyntax);
			rset.close();
			if(rbmXMLStr != null){
				Element rbmElement = XmlUtil.stringToXML(rbmXMLStr, null).getRootElement();
				XmlReader reader = new XmlReader(false);
				try{
					reader.getRbmModelContainer(rbmElement, model);
				}catch(ModelException | PropertyVetoException | XmlParseException e) {
					throw new DataAccessException(e.getMessage(),e);
				}
			}
		}
	}finally{
		if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
	}
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
public String getInfoSQL(User user,String extraConditions,String special,DatabaseSyntax dbSyntax) {
	UserTable userTable = UserTable.table;
	ModelTable vTable = ModelTable.table;
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable),swvTable.softwareVersion};
	Table[] t = {vTable,userTable,swvTable};
	
	switch (dbSyntax){
	case ORACLE:{
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() +  // links in the userTable
		           " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
		if (extraConditions != null && extraConditions.trim().length()>0){
			condition += " AND "+extraConditions;
		}
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,(OuterJoin)null,condition,special,dbSyntax);
		return sql;
	}
	case POSTGRES:{
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() + " "; // links in the userTable
		         //  " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
		if (extraConditions != null && extraConditions.trim().length()>0){
			condition += " AND "+extraConditions;
		}
		OuterJoin outerJoin = new OuterJoin(vTable, swvTable, JoinOp.LEFT_OUTER_JOIN, vTable.id, swvTable.versionableRef);
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,outerJoin,condition,special,dbSyntax);
		return sql;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
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
public String getSQLValueList(Model model,String rbm, Version version) {
	Xmlproducer xmlProducer = new Xmlproducer(false);
	String modelUnitSystemXML = TokenMangler.getSQLEscapedString(XmlUtil.xmlToString(xmlProducer.getXML(model.getUnitSystem())));
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version) + ",");
	buffer.append("'"+modelUnitSystemXML+"'" + ",");
	
	if (rbm==null){
		buffer.append("null,null");
	}else if (DbDriver.varchar2_CLOB_is_Varchar2_OK(rbm)){
		buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE);
	}else{
		buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null");
	}buffer.append(")");
	
	return buffer.toString();
}
}
