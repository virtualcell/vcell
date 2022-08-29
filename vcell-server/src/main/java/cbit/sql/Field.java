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
	
	public enum BasicDataType {
		VARCHAR,
		CHAR,
		BLOB,
		CLOB,
		DATE,
		BIGINT,
		NUMERIC
	}
	

	public enum SQLDataType {
		
		integer("integer","bigint",BasicDataType.BIGINT),
		integer_as_numeric("integer","numeric",BasicDataType.BIGINT),
		@Deprecated
		number_as_integer("number","bigint",BasicDataType.BIGINT),
		number_as_real("number","numeric",BasicDataType.NUMERIC),
		varchar2_4000("varchar2(4000)","varchar(4000)",BasicDataType.VARCHAR),
		varchar2_2000("varchar2(2000)","varchar(2000)",BasicDataType.VARCHAR),
		varchar2_1024("varchar2(1024)","varchar(1024)",BasicDataType.VARCHAR),
		varchar2_512("varchar2(512)","varchar(512)",BasicDataType.VARCHAR),
		varchar2_256("varchar2(256)","varchar(256)",BasicDataType.VARCHAR),
		varchar2_255("varchar2(255)","varchar(255)",BasicDataType.VARCHAR),
		varchar2_128("varchar2(128)","varchar(128)",BasicDataType.VARCHAR),
		varchar2_64("varchar2(64)","varchar(64)",BasicDataType.VARCHAR),
		varchar2_40("varchar2(40)","varchar(40)",BasicDataType.VARCHAR),
		varchar2_32("varchar2(32)","varchar(32)",BasicDataType.VARCHAR),
		varchar2_10("varchar2(10)","varchar(10)",BasicDataType.VARCHAR),
		varchar2_5("varchar2(5)","varchar(5)",BasicDataType.VARCHAR),
		varchar2_1("varchar2(1)","varchar(1)",BasicDataType.VARCHAR),

		varchar_10("varchar(10)","varchar(10)",BasicDataType.VARCHAR),
		varchar_20("varchar(20)","varchar(20)",BasicDataType.VARCHAR),
		varchar_50("varchar(50)","varchar(50)",BasicDataType.VARCHAR),
		varchar_64("varchar(64)","varchar(64)",BasicDataType.VARCHAR),
		varchar_100("varchar(100)","varchar(100)",BasicDataType.VARCHAR),
		varchar_128("varchar(128)","varchar(128)",BasicDataType.VARCHAR),
		varchar_255("varchar(255)","varchar(255)",BasicDataType.VARCHAR),
		varchar_1024("varchar(1024)","varchar(1024)",BasicDataType.VARCHAR),
		varchar_2048("varchar(2048)","varchar(2048)",BasicDataType.VARCHAR),
		varchar_4000("varchar(4000)","varchar(4000)",BasicDataType.VARCHAR),

		char_1("char(1)","char(1)",BasicDataType.CHAR),
		
		blob_bytea("blob","bytea",BasicDataType.BLOB),
		
		clob_text("clob","text",BasicDataType.CLOB),
		
		date("date","date",BasicDataType.DATE);
		
		private SQLDataType(String oracleDataType, String postgresDataType, BasicDataType basicDataType){
			this.oracleDataType = oracleDataType;
			this.postgresDataType = postgresDataType;
			this.basicDataType = basicDataType;
		}
		
		private final String oracleDataType;
		private final String postgresDataType;
		public final BasicDataType basicDataType;
		
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
