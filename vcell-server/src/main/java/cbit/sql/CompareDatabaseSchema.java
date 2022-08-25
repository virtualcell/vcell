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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;

import cbit.vcell.modeldb.SQLCreateAllTables;
/**
 * This type was created in VisualAge.
 */
public class CompareDatabaseSchema {
/**
 * This method was created in VisualAge.
 */
private static void compareSchemas(ConnectionFactory conFactory, KeyFactory keyFactory, Table tables[], DatabaseSyntax dbSyntax) throws SQLException {
	Connection con = null;
	Object lock = new Object();
	try {
		con = conFactory.getConnection(lock);
		System.out.println("connected....");
		//
		// for each table in VCell software, compare with Database connection
		//
		for (int i = 0; i < tables.length; i++){
			Table table = tables[i];
			Field fields[] = table.getFields();
			java.util.Vector fieldsNotYetFoundList = new java.util.Vector(java.util.Arrays.asList(fields));
			DatabaseMetaData metaData = con.getMetaData();
			ResultSet rs = metaData.getColumns(null,null,table.getTableName().toUpperCase(),null);
			System.out.println("\n\nTABLE: "+table.getTableName());
			//ResultSetMetaData rsMetaData = rs.getMetaData();
			//for (int j = 1; j <= rsMetaData.getColumnCount(); j++) {
				//System.out.println("column(" + j + ") = " + rsMetaData.getColumnName(j));
			//}
			boolean bTableFound = false;
			while (rs.next()){
				bTableFound = true;
				
				String tableCat = rs.getString("TABLE_CAT");
				String tableSchema = rs.getString("TABLE_SCHEM");
				String tableName = rs.getString("TABLE_NAME");
				String columnName = rs.getString("COLUMN_NAME");
				String dataType = rs.getString("DATA_TYPE");
				String typeName = rs.getString("TYPE_NAME");
				String colSize = rs.getString("COLUMN_SIZE");
				String bufferLength = rs.getString("BUFFER_LENGTH");
				String decimalDigits = rs.getString("DECIMAL_DIGITS");
				String numPrecRadix = rs.getString("NUM_PREC_RADIX");
				String nullable = rs.getString("NULLABLE");
				String remarks = rs.getString("REMARKS");
				String colDef = rs.getString("COLUMN_DEF");
				String sqlDataType = rs.getString("SQL_DATA_TYPE");
				String sqlDatetimeSub = rs.getString("SQL_DATETIME_SUB");
				String charOctetLength = rs.getString("CHAR_OCTET_LENGTH");
				String ordinalPosition = rs.getString("ORDINAL_POSITION");
				String isNullable = rs.getString("IS_NULLABLE");

				//System.out.println("TABLE_CAT        ="+tableCat);
				//System.out.println("TABLE_SCHEM      ="+tableSchema);
				//System.out.println("TABLE_NAME       ="+tableName);
				//System.out.println("COLUMN_NAME      ="+columnName);
				//System.out.println("DATA_TYPE        ="+dataType);
				//System.out.println("TYPE_NAME        ="+typeName);
				//System.out.println("COLUMN_SIZE      ="+colSize);
				//System.out.println("BUFFER_LENGTH    ="+bufferLength);
				//System.out.println("DECIMAL_DIGITS   ="+decimalDigits);
				//System.out.println("NUM_PREC_RADIX   ="+numPrecRadix);
				//System.out.println("NULLABLE         ="+nullable);
				//System.out.println("REMARKS          ="+remarks);
				//System.out.println("COLUMN_DEF       ="+colDef);
				//System.out.println("SQL_DATA_TYPE    ="+sqlDataType);
				//System.out.println("SQL_DATETIME_SUB ="+sqlDatetimeSub);
				//System.out.println("CHAR_OCTET_LENGTH="+charOctetLength);
				//System.out.println("ORDINAL_POSITION ="+ordinalPosition);
				//System.out.println("IS_NULLABLE      ="+isNullable);

				//
				//
				//
				Field field = null;
				for (int j = 0; j < fields.length; j++){
					if (fields[j].getUnqualifiedColName().equalsIgnoreCase(columnName)){
						field = fields[j];
					}
				}
				if (field!=null){
					//
					// VCell field found in database
					//
					fieldsNotYetFoundList.remove(field);
					//System.out.println("FIELD: name=\""+field.getUnqualifiedColName()+"\", type=\""+field.getSqlType()+"\", constraints=\""+field.getSqlConstraints()+"\"   : SQL COLUMN_DEF=<"+typeName+">, IS_NULLABLE=<"+isNullable+">");
					//
					// compare detailed data types (especially strings)
					//
					boolean databaseIsVarchar = typeName.startsWith("VARCHAR");
					boolean vcellIsVarchar = field.getSqlType(dbSyntax).toUpperCase().startsWith("VARCHAR");
					if (databaseIsVarchar != vcellIsVarchar){
						System.out.println("vcell field "+field.getQualifiedColName()+" is of type "+field.getSqlType(dbSyntax)+" and database is of type "+typeName);
					}
					if (databaseIsVarchar && vcellIsVarchar && field.getSqlType(dbSyntax).indexOf("("+colSize+")")<0){
						System.out.println("vcell field "+field.getQualifiedColName()+" is of type "+field.getSqlType(dbSyntax)+" and database is of type "+typeName+"("+colSize+")");
					}
					boolean databaseIsNullable = isNullable.equalsIgnoreCase("yes");
					boolean vcellIsNullable = (field.getSqlConstraints().toUpperCase().indexOf("NOT NULL")<0) && (field.getSqlConstraints().toUpperCase().indexOf("PRIMARY KEY")<0);
					if (databaseIsNullable != vcellIsNullable){
						System.out.println("vcell field "+field.getQualifiedColName()+" has constraint allow null="+vcellIsNullable+", database allow null="+isNullable);
					}
						
				}else{
					//
					// database field not found in VCell software
					//
					System.out.println("found database column not in VCell Software: COLUMN_NAME=<"+columnName+">, SQL_DATA_TYPE=<"+sqlDataType+">, IS_NULLABLE=<"+isNullable+">");
				}
			}

			rs.close();
			if (!bTableFound){
				System.out.println("WARNING: table \""+table.getTableName()+"\" not found in database");
				System.out.println("suggested SQL to fix it:");
				System.out.println(table.getCreateSQL(dbSyntax));
			}

			if (fieldsNotYetFoundList.size()>0){
				int fieldCount = fields.length - fieldsNotYetFoundList.size();
				System.out.println("WARNING: table \""+table.getTableName()+"\" has "+fields.length+" columns in VCell software and "+fieldCount+" columns in Database");
				for (int j = 0; j < fieldsNotYetFoundList.size(); j++){
					Field field = (Field)fieldsNotYetFoundList.elementAt(j);
					System.out.println("database didn't contain column: name=\""+field.getUnqualifiedColName()+"\", type=\""+field.getSqlType(dbSyntax)+"\", constraints=\""+field.getSqlConstraints()+"\"");
				}
				System.out.println("suggested SQL to fix it:");
				System.out.print("ALTER TABLE "+table.getTableName().toUpperCase()+" ADD(");
				for (int j = 0; j < fieldsNotYetFoundList.size(); j++){
					Field field = (Field)fieldsNotYetFoundList.elementAt(j);
					if (j>0){
						System.out.print(",");
					}
					System.out.print(field.getUnqualifiedColName().toUpperCase()+" "+field.getSqlType(dbSyntax).toUpperCase()+" "+field.getSqlConstraints().toUpperCase());
				}
				System.out.println(")");
			}
		}
	} catch (SQLException exc) {
		System.out.println(exc.getMessage());
		exc.printStackTrace(System.out);
	} finally {
		conFactory.release(con, lock);
	}
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
    //
    try {
    	final String oracle = "oracle";
    	final String postgres = "postgres";
    	final String usage = "Usage: ("+oracle+"|"+postgres+") connectURL schemaUser schemaUserPassword";
    	
        if (args.length != 4 || (!args[0].equalsIgnoreCase(oracle) && !args[0].equalsIgnoreCase(postgres))) {
            System.out.println(usage);
            System.exit(0);
        }
        String connectURL = args[1];
        String dbSchemaUser = args[2];
        String dbPassword = args[3];
        //

        ConnectionFactory conFactory = null;
        KeyFactory keyFactory = null;
        new cbit.vcell.resource.PropertyLoader();

        //
        // get appropriate database factory objects
        //

        DatabaseSyntax dbSyntax = null;
        if (args[0].equalsIgnoreCase(oracle)) {
        	dbSyntax = DatabaseSyntax.ORACLE;
            String driverName = "oracle.jdbc.driver.OracleDriver";
			conFactory = DatabaseService.getInstance().createConnectionFactory(
                    driverName,
                    connectURL,
                    dbSchemaUser,
                    dbPassword);
            keyFactory = conFactory.getKeyFactory();
        } else if (args[0].equalsIgnoreCase(postgres)) {
        	dbSyntax = DatabaseSyntax.POSTGRES;
            String driverName = "org.postgresql.Driver";
            conFactory = DatabaseService.getInstance().createConnectionFactory(
                    driverName,
                    connectURL,
                    dbSchemaUser,
                    dbPassword);
            keyFactory = conFactory.getKeyFactory();
        } else {
                System.out.println(usage);
                System.exit(1);
        }

        //
        // compare with VCell Software 'tables'
        //
		Table tables[] = SQLCreateAllTables.getVCellTables();
        compareSchemas(conFactory, keyFactory, tables, dbSyntax);
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }
    System.exit(0);
}
}
