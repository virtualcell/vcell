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
public class Field {
	private String tableName = null;
	private String name = null;
	private String type = null;
	private String constraints = null;
	private int maxLength = -1;
/**
 * This method was created in VisualAge.
 * @param name java.lang.String
 * @param sqlType java.lang.String
 * @param sqlConstraints java.lang.String
 */
public Field(String columnName, String sqlType, int maxLength, String sqlConstraints) {
	this.tableName = null;  // setTableName() should be set in cbit.sql.Table constructor
	this.name = columnName;
	this.type = sqlType;
	this.constraints = sqlConstraints;
	this.maxLength = maxLength;
}
/**
 * This method was created in VisualAge.
 * @param name java.lang.String
 * @param sqlType java.lang.String
 * @param sqlConstraints java.lang.String
 */
public Field(String columnName, String sqlType, String sqlConstraints) {
	this(columnName,sqlType,-1,sqlConstraints);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getMaxLength() {
	return maxLength;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getQualifiedColName() {
	//
	// tableName should be set from cbit.sql.Table constructor before used.
	//
	return getTableName()+"."+getUnqualifiedColName();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getSqlConstraints() {
	return constraints;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getSqlType() {
	return type;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getTableName() {
	//
	// tableName should be set from cbit.sql.Table constructor before used.
	//
	if (tableName==null){
		throw new RuntimeException("table name should be set in Table constructor");
	}
	return tableName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getUnqualifiedColName() {
	return name;
}
/**
 * This method was created in VisualAge.
 * @param tableName java.lang.String
 */
public void setTableName(String argTableName) {
	//
	// should be set from cbit.sql.Table constructor before used.
	//
	if(this.tableName != null){
		Thread.dumpStack();
		throw new RuntimeException("Change existing tableName="+this.tableName+" to "+argTableName+" Not Allowed for "+this.getClass().toString());
	}
	this.tableName = argTableName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return getUnqualifiedColName();
}
}
