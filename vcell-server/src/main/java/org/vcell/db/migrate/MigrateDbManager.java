package org.vcell.db.migrate;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.DatabaseSyntax;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.modeldb.SQLCreateAllTables;
import cbit.vcell.modeldb.SimContextStat2Table;
import cbit.vcell.modeldb.SimContextStatTable;

public class MigrateDbManager {

	public static void main(String[] args) throws SQLException {
		ConnectionFactory conFactory_Oracle = null;
		ConnectionFactory conFactory_Postgres = null;
		try {

			String driverName_Oracle = "oracle.jdbc.driver.OracleDriver";
			String connectionURL_Oracle = "jdbc:oracle:thin:@VCELL-DB.cam.uchc.edu:1521/vcelldborcl.cam.uchc.edu";
			String userid_Oracle = "vcell";
			String password_Oracle = null;
			conFactory_Oracle = DatabaseService.getInstance().createConnectionFactory(driverName_Oracle,connectionURL_Oracle,userid_Oracle,password_Oracle);
			
			String driverName_Postgres = "org.postgresql.Driver";
			String connectionURL_Postgres = "jdbc:postgresql://localhost:5432/schaff";
			String userid_Postgres = "schaff";
			String password_Postgres = null;
			conFactory_Postgres = DatabaseService.getInstance().createConnectionFactory(driverName_Postgres,connectionURL_Postgres,userid_Postgres,password_Postgres);
			
			int retryCount = 0;
			boolean done = false;
			while (!done && retryCount < 100){
				try {
					Table[] tables = SQLCreateAllTables.getVCellTables();
					for (Table table : tables){
						if (!table.getTableName().toUpperCase().startsWith("VC_")){
							continue;
						}
						if (table==SimContextStatTable.table ||
							table==SimContextStat2Table.table){
							continue;
						}
						migrate(table,conFactory_Oracle,conFactory_Postgres);
					}
					done = true;
				}catch (Exception e){
					e.printStackTrace();
					Thread.sleep(10000); // sleep 10 seconds
					retryCount++;
				}
			}	
			System.out.println("done");
			System.exit(0);
			
		}catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		}finally{
			if (conFactory_Oracle!=null){
				conFactory_Oracle.close();
			}
			if (conFactory_Postgres!=null){
				conFactory_Postgres.close();
			}
		}
	}
	
	public static void migrate(Table table, ConnectionFactory conFactory_from, ConnectionFactory conFactory_to) throws SQLException{

		System.out.println("starting migration of table "+table.getTableName());
		assert(conFactory_from.getDatabaseSyntax()==DatabaseSyntax.ORACLE);
		assert(conFactory_to.getDatabaseSyntax()==DatabaseSyntax.POSTGRES);
		
		
		Connection con_from=null;
		Connection con_to=null;
		try {
			con_from = conFactory_from.getConnection(new Object());
			con_to = conFactory_to.getConnection(new Object());
			con_from.setReadOnly(true);
			con_to.setReadOnly(false);
			
			//
			// get Last key in target table (using the "to" connection) - NULL if no records.
			//
			String sql = "SELECT ID FROM "+table.getTableName()+" ORDER BY ID DESC LIMIT 1";
			System.out.println(sql);

			
			//
			// should disable self-referential reference in vc_struct table (field "parentRef")
			//
			KeyValue lastkey = null;
			try (
				Statement stmt_to_init = con_to.createStatement();
				ResultSet rset_to_init = stmt_to_init.executeQuery(sql);
			){
				if (rset_to_init.next()){
					BigDecimal keyBigDecimal = rset_to_init.getBigDecimal(1);
					if (!rset_to_init.wasNull()){
						lastkey = new KeyValue(keyBigDecimal);
					}
				}
			}
			
			//
			// gather fields to copy
			//
			ArrayList<Field> fieldsToCopy = new ArrayList<Field>();
			for (Field f : table.getFields()){
//				if (f.getSqlDataType() == SQLDataType.clob_text || f.getSqlDataType() == SQLDataType.blob_bytea){
//					continue; // insert in another pass
//				}
				fieldsToCopy.add(f);
			}

			//
			// form the query using the "from" connection
			//
			StringBuffer selectBuffer = new StringBuffer();
			selectBuffer.append("SELECT ");
			for (int i=0;i<fieldsToCopy.size();i++){
				selectBuffer.append(fieldsToCopy.get(i).getUnqualifiedColName());
				if (i<fieldsToCopy.size()-1){
					selectBuffer.append(",");
				}
			}
			selectBuffer.append(" FROM "+table.getTableName());
			if (lastkey!=null){
				selectBuffer.append(" WHERE id > "+lastkey+" ");
			}
			selectBuffer.append(" ORDER BY id");
			
			System.out.println(selectBuffer.toString());
			
			//
			// form the prepared INSERT statement for the "to" connection
			//
			StringBuffer insertBuffer = new StringBuffer();
			insertBuffer.append("INSERT INTO "+table.getTableName()+" values (");
			for (int i=0;i<fieldsToCopy.size();i++){
				insertBuffer.append("?");
				if (i<fieldsToCopy.size()-1){
					insertBuffer.append(",");
				}
			}
			insertBuffer.append(")");

			System.out.println(insertBuffer.toString());
			
			
			
			try (	Statement stmt_from = con_from.createStatement();
					ResultSet rset_from = stmt_from.executeQuery(selectBuffer.toString());
					PreparedStatement stmt_to = con_to.prepareStatement(insertBuffer.toString());
				){
				
				long rowCount = 0;
				long batchCount = 0;
				while (rset_from.next()){
					for (int i=0;i<fieldsToCopy.size();i++){
						Field f = fieldsToCopy.get(i);
						switch (f.getSqlDataType().basicDataType){
						case BLOB:{
							boolean found = false;
							Object lob_object = rset_from.getObject(i+1);
							if(!rset_from.wasNull()){
								if (lob_object instanceof java.sql.Blob) {
									java.sql.Blob blob_object = (java.sql.Blob) lob_object;
									byte[] bytes = blob_object.getBytes((long) 1, (int) blob_object.length());
									stmt_to.setBytes(i+1, bytes);
									found = true;
								}
							}
							if (!found){
								stmt_to.setNull(i+1, java.sql.Types.LONGVARBINARY);
							}
							break;
						}
						case CLOB:{
							boolean found = false;
							Object lob_object = rset_from.getObject(i+1);
							if(!rset_from.wasNull()){
								if (lob_object instanceof java.sql.Clob) {
									java.sql.Clob clob_object = (java.sql.Clob) lob_object;
									byte[] ins = new byte[(int) clob_object.length()];
									try {
										clob_object.getAsciiStream().read(ins);
										String str = new String(ins);
										stmt_to.setString(i+1, str);
										found = true;
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
							if (!found){
								stmt_to.setNull(i+1, java.sql.Types.LONGVARCHAR);
							}
							break;
						}
						case CHAR:{
							String str = rset_from.getString(i+1);
							if (!rset_from.wasNull()){
								stmt_to.setString(i+1, str);
							}else{
								stmt_to.setNull(i+1, java.sql.Types.CHAR);
							}
							break;
						}
						case VARCHAR:{
							String str = rset_from.getString(i+1);
							if (!rset_from.wasNull()){
								stmt_to.setString(i+1, str);
							}else{
								stmt_to.setNull(i+1, java.sql.Types.VARCHAR);
							}
							break;
						}
						case DATE:{
							Date date = rset_from.getDate(i+1);
							if (!rset_from.wasNull()){
								stmt_to.setDate(i+1, date);
							}else{
								stmt_to.setNull(i+1, java.sql.Types.DATE);
							}
							break;
						}
						case BIGINT: {
							BigDecimal bigDecimal = rset_from.getBigDecimal(i+1);
							if (!rset_from.wasNull()){
								stmt_to.setBigDecimal(i+1, bigDecimal);
							}else{
								stmt_to.setNull(i+1, java.sql.Types.BIGINT);
							}
							break;
						}
						case NUMERIC:{
							BigDecimal bigDecimal = rset_from.getBigDecimal(i+1);
							if (!rset_from.wasNull()){
								stmt_to.setBigDecimal(i+1, bigDecimal);
							}else{
								stmt_to.setNull(i+1, java.sql.Types.NUMERIC);
							}
							break;
						}
						default:{
							throw new RuntimeException("support for JDBC Type "+f.getSqlDataType().basicDataType+" not yet supported");
						}
						}
					}
					stmt_to.addBatch();
					rowCount++;
					batchCount++;
					if (batchCount >= 300){
						System.out.println("writing "+batchCount+" of "+rowCount+" records into table "+table.getTableName());
						batchCount = 0;
						stmt_to.executeBatch();
						con_to.commit();
					}
				}
				if (batchCount>0){
					stmt_to.executeBatch();
					con_to.commit();
				}
			} // end try
			
			System.out.println("starting migration of table "+table.getTableName());
			
			
		}finally{
			if (con_from!=null) con_from.close();
			if (con_to!=null) con_to.close();
		}
		
	}

}
