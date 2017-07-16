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

import org.vcell.db.DatabaseSyntax;

/**
 * This type was created in VisualAge.
 */
public class Field {
	

	public enum SQLDataType {
		
		integer("integer","integer"),
		@Deprecated
		number_as_integer("number","integer"),
		number_as_real("number","real"),
		varchar2_4000("varchar2(4000)","varchar(4000)"),
		varchar2_1024("varchar2(1024)","varchar(1024)"),
		varchar2_512("varchar2(512)","varchar(512)"),
		varchar2_256("varchar2(256)","varchar(256)"),
		varchar2_255("varchar2(255)","varchar(255)"),
		varchar2_128("varchar2(128)","varchar(128)"),
		varchar2_64("varchar2(64)","varchar(64)"),
		varchar2_40("varchar2(40)","varchar(40)"),
		varchar2_32("varchar2(32)","varchar(32)"),
		varchar2_10("varchar2(10)","varchar(10)"),
		varchar2_5("varchar2(5)","varchar(5)"),
		varchar2_1("varchar2(1)","varchar(1)"),

		varchar_10("varchar(10)","varchar(10)"),
		varchar_20("varchar(20)","varchar(20)"),
		varchar_50("varchar(50)","varchar(50)"),
		varchar_64("varchar(64)","varchar(64)"),
		varchar_100("varchar(100)","varchar(100)"),
		varchar_128("varchar(128)","varchar(128)"),
		varchar_255("varchar(255)","varchar(255)"),
		varchar_1024("varchar(1024)","varchar(1024)"),
		varchar_2048("varchar(2048)","varchar(2048)"),
		varchar_4000("varchar(4000)","varchar(4000)"),

		char_1("char(1)","char(1)"),
		
		blob_bytea("blob","bytea"),
		
		clob_text("clob","text"),
		
		date("date","date");
		
		private SQLDataType(String oracleDataType, String postgresDataType){
			this.oracleDataType = oracleDataType;
			this.postgresDataType = postgresDataType;
		}
		
		private final String oracleDataType;
		private final String postgresDataType;
		
		public String getDbSQLType(DatabaseSyntax dbSyntax){
			switch (dbSyntax){
			case ORACLE:{
				return oracleDataType;
			}
			case POSTGRES:{
				return postgresDataType;
			}
			default:{
				throw new RuntimeException("unknown dbSyntax, expecting ORACLE or POSTGRES");
			}
			}
		}
	}
	
	
	private String tableName = null;
	private String name = null;
	private SQLDataType sqlDataType = null;
	private String constraints = null;
	private int maxLength = -1;
/**
 * This method was created in VisualAge.
 * @param name java.lang.String
 * @param sqlType java.lang.String
 * @param sqlConstraints java.lang.String
 */
public Field(String columnName, SQLDataType sqlDataType, int maxLength, String sqlConstraints) {
	this.tableName = null;  // setTableName() should be set in cbit.sql.Table constructor
	this.name = columnName;
	this.sqlDataType = sqlDataType;
	this.constraints = sqlConstraints;
	this.maxLength = maxLength;
}
/**
 * This method was created in VisualAge.
 * @param name java.lang.String
 * @param sqlType java.lang.String
 * @param sqlConstraints java.lang.String
 */
public Field(String columnName, SQLDataType sqlType, String sqlConstraints) {
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
public String getSqlType(DatabaseSyntax dbSyntax) {
	return sqlDataType.getDbSQLType(dbSyntax);
}

public SQLDataType getSqlDataType(){
	return sqlDataType;
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
