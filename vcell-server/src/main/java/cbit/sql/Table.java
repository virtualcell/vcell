/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.DatabaseSyntax;

import cbit.sql.Field.SQLDataType;

/**
 * This type was created in VisualAge.
 */
public abstract class Table {
	
	public static Logger lg = LogManager.getLogger(Table.class);

	public static final String SQL_GLOBAL_HINT = "";//" /*+ RULE */ ";
	
	public static final String SEQ = "newSeq";

	public final String tableName;
	public final String[] tableConstraintsOracle;
	public final String[] tableConstraintsPostgres;
	private java.util.Vector fields = new java.util.Vector();

	public static final String id_ColumnName = "id";
	public final Field id = new Field(id_ColumnName,SQLDataType.integer,"PRIMARY KEY");

/**
 * Table constructor comment.
 */
protected Table(String argTableName) {
	this(argTableName,null,null);
	//this.tableName = argTableName;
	//addField(this.id);
}

/**
 * Table constructor comment.
 */
protected Table(String argTableName,String[] argTableConstraintsOracle, String[] argTableConstraintsPostgres) {
	this.tableName = argTableName;
	if (argTableConstraintsOracle!=null || argTableConstraintsPostgres!=null){
		if ((argTableConstraintsOracle==null) || (argTableConstraintsPostgres==null) || (argTableConstraintsOracle.length != argTableConstraintsPostgres.length)){
			throw new RuntimeException("table constraints for oracle must match table constraints for postgres");
		}
	}
	this.tableConstraintsOracle = argTableConstraintsOracle;
	this.tableConstraintsPostgres = argTableConstraintsPostgres;
	addField(this.id);
}


/**
 * This method was created in VisualAge.
 * @param argField cbit.sql.Field
 */
protected void addField(Field argField) {
	argField.setTableName(this.tableName);
	for(int c = 0;c < fields.size();c+= 1){
		Field currField = (Field)fields.elementAt(c);
		if(currField.getUnqualifiedColName().equals(argField.getUnqualifiedColName()) && currField.getTableName().equals(argField.getTableName())){
			throw new RuntimeException("Field "+argField+" already contained in fields vector at index "+c);
		}
	}
	fields.addElement(argField);
}


/**
 * This method was created in VisualAge.
 * @param argField cbit.sql.Field
 */
protected void addFields(Field[] argFields) {
	if(argFields == null){
		return;
	}
	for (int i = 0; i < argFields.length; i += 1) {
		addField(argFields[i]);
	}
}


public final String getCreateSQL(DatabaseSyntax dbSyntax) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("CREATE TABLE "+tableName+"(");
	Field[] allFields = getFields();
	for (int i=0;i<allFields.length;i++){
		if (i>0){
			buffer.append(",");
		}
		buffer.append(allFields[i].getUnqualifiedColName()+" "+allFields[i].getSqlType(dbSyntax)+" "+allFields[i].getSqlConstraints());
	}
	if(tableConstraintsOracle != null){
		// if tableConstraintsOracle is non-null, then tableConstraintsPostgres must also be non-null
		//buffer.append(", CONSTRAINT ");
		for(int i=0;i<tableConstraintsOracle.length;i+= 1){
			//if(i != 0){
				//buffer.append(",");
			//}
			switch (dbSyntax){
			case ORACLE:{
				buffer.append(", CONSTRAINT "+tableConstraintsOracle[i]);
				break;
			}
			case POSTGRES:{
				buffer.append(", CONSTRAINT "+tableConstraintsPostgres[i]);
				break;
			}
			}
		}
	}
	buffer.append(")");
	
	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Field[]
 */
public Field[] getFields() {
	Field[] allFields =  new Field[fields.size()];
	fields.copyInto(allFields);
	return allFields;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getSQLColumnList() {
	return getSQLColumnList(false, true, null);
}

public String getSQLColumnList(boolean bQualified, boolean bParentheses, String asSuffix) {
	StringBuffer buffer = new StringBuffer();
	if (bParentheses){
		buffer.append("(");
	}
	Field[] allFields = getFields();
	for (int i=0;i<allFields.length;i++){
		if (i>0){
			buffer.append(",");
		}
		if (bQualified){
			buffer.append(allFields[i].getQualifiedColName());
		}else{
			buffer.append(allFields[i].getUnqualifiedColName());
		}
		if (asSuffix!=null){
			buffer.append(" "+allFields[i].getUnqualifiedColName()+asSuffix);
		}
	}
	if (bParentheses){
		buffer.append(")");
	}
	return buffer.toString();
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getTableName() {
	return tableName;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "TABLE: "+tableName;
}
}
