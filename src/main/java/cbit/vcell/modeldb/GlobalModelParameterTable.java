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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.util.xml.XmlUtil;
import cbit.vcell.model.Model;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.xml.Xmlproducer;

/**
 * This type was created in VisualAge.
 */
public class GlobalModelParameterTable  extends Table{
	private static final String TABLE_NAME = "vc_globalmodelparam";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field modelRef		= new Field("modelRef","integer","NOT NULL "+ModelTable.REF_TYPE+ " ON DELETE CASCADE");
	public final Field xmlFragment	= new Field("xmlFragment","varchar(4000)","NOT NULL");
	
	private final Field fields[] = {modelRef,xmlFragment};
	
	public static final GlobalModelParameterTable table = new GlobalModelParameterTable();
	
/**
 * ModelTable constructor comment.
 */
private GlobalModelParameterTable() {
	super(TABLE_NAME,null);
	addFields(fields);
}
public void insertModelParameters(Connection con,Model.ModelParameter[] modelParametersArr,KeyValue modelKey) throws DataAccessException,SQLException{
	String modelParameeterXML = null;
	if(modelParametersArr != null && modelParametersArr.length > 0){
		Xmlproducer xmlProducer = new Xmlproducer(true);
		modelParameeterXML = XmlUtil.xmlToString(xmlProducer.getXML(modelParametersArr));
	}
	if(modelParameeterXML == null){
		return;
	}

	final int MAX_CHARS = 3000;
	for(int i=0;i<modelParameeterXML.length();i+= MAX_CHARS){
		String modelParameeterXMLS = modelParameeterXML.substring(i,i+Math.min(MAX_CHARS, modelParameeterXML.length()-i));
		if(modelParameeterXMLS != null){
			modelParameeterXMLS = org.vcell.util.TokenMangler.getSQLEscapedString(modelParameeterXMLS);
		}
		String modelParameeterXMLValues =
			GlobalModelParameterTable.table.getSQLValueList(modelKey,modelParameeterXMLS);
		String sql = "INSERT INTO " + GlobalModelParameterTable.table.getTableName() + " " + 
		GlobalModelParameterTable.table.getSQLColumnList() + " VALUES " + modelParameeterXMLValues;
//		System.out.println(sql);
		DbDriver.updateCleanSQL(con,sql);
	}
}

private String getSQLValueList(KeyValue modelKey,String modelParameeterXMLS) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(NewSEQ+",");
	buffer.append(modelKey+",");
	buffer.append("'"+modelParameeterXMLS+"'"+")");
	
	return buffer.toString();
}

public void setModelParameters(Connection con,Model model) throws SQLException,DataAccessException{
	
	String sql =
		" SELECT " +
			GlobalModelParameterTable.table.xmlFragment.getQualifiedColName() +
		" FROM " + GlobalModelParameterTable.table.getTableName() + 
		" WHERE " +
			GlobalModelParameterTable.table.modelRef.getQualifiedColName()+
			" = " + model.getVersion().getVersionKey()
			+" ORDER BY "+GlobalModelParameterTable.table.id.getUnqualifiedColName();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		StringBuffer modelParameeterXMLSB = new StringBuffer();
		while(rset.next()){
			String modelParameeterXMLS = rset.getString(GlobalModelParameterTable.table.xmlFragment.toString());
			if(!rset.wasNull()){
				modelParameeterXMLS = org.vcell.util.TokenMangler.getSQLRestoredString(modelParameeterXMLS);
				modelParameeterXMLSB.append(modelParameeterXMLS);
			}
		}
		if(modelParameeterXMLSB.length() > 0){
			XmlReader xmlReader = new XmlReader(true);
			Element modelParameeterXMLElement = XmlUtil.stringToXML(modelParameeterXMLSB.toString(), null).getRootElement();
			try{
				Model.ModelParameter[] modelParameterArr =
					xmlReader.getModelParams(modelParameeterXMLElement, model);
				model.setModelParameters(modelParameterArr);
			}catch(Exception e){
				throw new DataAccessException("error reading ModelParameters for model "+model.getName(),e);
			}
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
}

}
