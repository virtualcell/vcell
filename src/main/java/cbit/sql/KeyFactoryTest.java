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

import java.sql.*;

import org.vcell.util.StdoutSessionLog;
/**
 * This type was created in VisualAge.
 */
public class KeyFactoryTest {
/**
 * This method was created in VisualAge.
 */
private static void createSequence(Connection con, KeyFactory keyFactory) throws SQLException {

	//
	// drop old sequence
	// 
	String sql = keyFactory.getDestroySQL();
	System.out.println(sql);
	Statement stmt = null;
	try {
		stmt = con.createStatement();
		stmt.execute(sql);
	}finally{
		stmt.close();
	}

	//
	// create new sequence
	// 
	sql = keyFactory.getCreateSQL();
	System.out.println(sql);
	stmt = null;
	try {
		stmt = con.createStatement();
		stmt.execute(sql);
	}finally{
		stmt.close();
	}	
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {
	ConnectionFactory conFactory = null;
	Connection con = null;
	Object lock = new Object();
	try {
		if (args.length != 1){
			System.out.println("usage: KeyFactoryTest (oracle|mysql)");
			System.exit(1);
		}

		new org.vcell.util.PropertyLoader();
		
		KeyFactory keyFactory = null;
		if (args[0].equalsIgnoreCase("ORACLE")){
			conFactory = new OraclePoolingConnectionFactory(new StdoutSessionLog("KeyFactoryTest"));
			keyFactory = new OracleKeyFactory();
		}else if (args[0].equalsIgnoreCase("MYSQL")){
			conFactory = new MysqlConnectionFactory();
			keyFactory = new MysqlKeyFactory();
		}else{
			System.out.println("usage: KeyFactoryTest (oracle|mysql)");
			System.exit(1);
		}
		
		con = conFactory.getConnection(lock);

		try {

			System.out.println("connected....");
			
			//createSequence(con, keyFactory);

			for (int i=0;i<10;i++){
				System.out.println("key["+i+"] = "+keyFactory.getNewKey(con));
			}
			
			con.commit();

		} catch (Exception e) {
			conFactory.release(con,lock);
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}finally{
		try {
			conFactory.release(con,lock);
		}catch (Throwable e){}
	}
	System.exit(0);
}
}
