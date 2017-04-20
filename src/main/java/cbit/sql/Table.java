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
/**
 * This type was created in VisualAge.
 */
public abstract class Table {
	
	public static final String SQL_GLOBAL_HINT = "";//" /*+ RULE */ ";
	
	public static final String SEQ = "newSeq";
	public static final String NewSEQ = SEQ + ".NEXTVAL";
	public static final String CurrSEQ = SEQ + ".CURRVAL";

	public final String tableName;
	public final String[] tableConstraints;
	private java.util.Vector fields = new java.util.Vector();

	public static final String id_ColumnName = "id";
	public final Field id = new Field(id_ColumnName,"integer","PRIMARY KEY");

/**
 * Table constructor comment.
 */
protected Table(String argTableName) {
	this(argTableName,null);
	//this.tableName = argTableName;
	//addField(this.id);
}


/**
 * Table constructor comment.
 */
protected Table(String argTableName,String[] argTableConstraints) {
	this.tableName = argTableName;
	this.tableConstraints = argTableConstraints;
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


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public final String getCreateSQL() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("CREATE TABLE "+tableName+"(");
	Field[] allFields = getFields();
	for (int i=0;i<allFields.length;i++){
		if (i>0){
			buffer.append(",");
		}
		buffer.append(allFields[i].getUnqualifiedColName()+" "+allFields[i].getSqlType()+" "+allFields[i].getSqlConstraints());
	}
	if(getTableConstraints() != null){
		//buffer.append(", CONSTRAINT ");
		for(int i=0;i<tableConstraints.length;i+= 1){
			//if(i != 0){
				//buffer.append(",");
			//}
			buffer.append(", CONSTRAINT "+tableConstraints[i]);
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
public String[] getTableConstraints() {
	return tableConstraints;
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
