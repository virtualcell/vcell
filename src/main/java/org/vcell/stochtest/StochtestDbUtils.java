package org.vcell.stochtest;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.stochtest.StochtestCompare.StochtestCompareStatus;
import org.vcell.stochtest.TimeSeriesMultitrialData.SummaryStatistics;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.ConnectionFactory;
import cbit.vcell.modeldb.StochtestCompareTable;
import cbit.vcell.modeldb.StochtestRunTable;
import cbit.vcell.modeldb.StochtestTable;

public class StochtestDbUtils {

	public static StochtestRun getStochtestRun(ConnectionFactory conFactory, KeyValue stochtestRunKey) throws IllegalArgumentException, SQLException, DataAccessException {
		
		//
		// grab next waiting simulation, change status to accepted, commit and return
		//
		java.sql.Connection con = conFactory.getConnection(new Object());
		StochtestRun stochtestRun = null;
		try {
			java.sql.Statement stmt = null;
			try {
				StochtestTable stochtestTable = StochtestTable.table;
				StochtestRunTable stochtestRunTable = StochtestRunTable.table;
				String sql = "SELECT "+stochtestTable.getSQLColumnList(true, false, "_1") + " , " + stochtestRunTable.getSQLColumnList(true, false, "_2") +
						" FROM "+stochtestRunTable.getTableName() + ", " + stochtestTable.getTableName() + 
						" WHERE "+stochtestTable.id.getQualifiedColName()+" = "+stochtestRunTable.stochtestref.getQualifiedColName() +
						" AND "+stochtestRunTable.id.getQualifiedColName()+" = "+stochtestRunKey;
				stmt = con.createStatement();
System.out.println(sql);
			    ResultSet rset = stmt.executeQuery(sql);
			    if (rset.next()) {
			    	Stochtest stochtest = new Stochtest();
java.sql.ResultSetMetaData rsetMetaData = rset.getMetaData();
int numColumns = rsetMetaData.getColumnCount();
for (int i = 0; i < numColumns; i++){
	System.out.println("RESULTS "+i+") table_name="+rsetMetaData.getTableName(i+1)+
			", catalog_name="+rsetMetaData.getCatalogName(i+1)+
			", col_class_name="+rsetMetaData.getColumnClassName(i+1)+
			", col_type_name="+rsetMetaData.getColumnTypeName(i+1)+
			", col_name="+rsetMetaData.getColumnName(i+1)+
			", col_label="+rsetMetaData.getColumnLabel(i+1)+
			", schema_name="+rsetMetaData.getSchemaName(i+1));
}

			    	stochtest.key = new KeyValue(rset.getBigDecimal(stochtestTable.id.getUnqualifiedColName()+"_1"));
			    	stochtest.simContextRef = new KeyValue(rset.getBigDecimal(stochtestTable.simcontextref.getUnqualifiedColName()+"_1"));
			    	stochtest.biomodelRef = new KeyValue(rset.getBigDecimal(stochtestTable.biomodelref.getUnqualifiedColName()+"_1"));
			    	stochtest.mathRef = new KeyValue(rset.getBigDecimal(stochtestTable.mathref.getUnqualifiedColName()+"_1"));
			    	stochtest.dimension = rset.getInt(stochtestTable.dimension.getUnqualifiedColName()+"_1");
			    	stochtest.numcompartments = rset.getInt(stochtestTable.numcompartments.getUnqualifiedColName()+"_1");
			    	stochtest.mathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestTable.mathtype.getUnqualifiedColName()+"_1"));
			        
			    	stochtestRun = new StochtestRun();
			        stochtestRun.key = new KeyValue(rset.getBigDecimal(stochtestRunTable.id.getUnqualifiedColName()+"_2"));
			        stochtestRun.stochtest = stochtest;
			        stochtestRun.parentMathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestRunTable.parentmathtype.getUnqualifiedColName()+"_2"));
			        stochtestRun.mathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestRunTable.mathtype.getUnqualifiedColName()+"_2"));
			        stochtestRun.status = StochtestRun.StochtestRunStatus.valueOf(rset.getString(stochtestRunTable.status.getUnqualifiedColName()+"_2"));
			        stochtestRun.errmsg = rset.getString(stochtestRunTable.errmsg.getUnqualifiedColName()+"_2");
			        stochtestRun.conclusion = rset.getString(stochtestRunTable.errmsg.getUnqualifiedColName()+"_2");
			        stochtestRun.exclude = rset.getString(stochtestRunTable.exclude.getUnqualifiedColName()+"_2");
			        stochtestRun.networkGenProbs = rset.getString(stochtestRunTable.networkGenProbs.getUnqualifiedColName()+"_2");
			    }else{
			    	return null;
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
			
			con.commit();
		}catch (SQLException e){
			e.printStackTrace();
			con.rollback();
			throw e;
		}finally {
			con.close();
		}
		return stochtestRun;
	}

	public static void finalizeAcceptedStochtestRun(ConnectionFactory conFactory, StochtestRun acceptedStochtestRun, StochtestRun.StochtestRunStatus newStatus, String errmsg, String networkGenProbs) throws IllegalArgumentException, SQLException, DataAccessException {
		
		if (newStatus != StochtestRun.StochtestRunStatus.complete && newStatus != StochtestRun.StochtestRunStatus.failed){
			throw new RuntimeException("new status is "+newStatus+", expecting "+StochtestRun.StochtestRunStatus.complete+" or "+StochtestRun.StochtestRunStatus.failed);
		}
		
		//
		// update status
		//
		java.sql.Connection con = conFactory.getConnection(new Object());
		try {
			java.sql.Statement stmt = null;
			try {
				StochtestRunTable stochtestRunTable = StochtestRunTable.table;
				
				String errMsgRHS = (errmsg==null) ? "NULL" : "'"+TokenMangler.getSQLEscapedString(errmsg, 4000)+"'";
				
				String networkGenProbsRHS = (networkGenProbs==null) ? "NULL" : "'"+TokenMangler.getSQLEscapedString(networkGenProbs, 4000)+"'";
				
				String sql = "UPDATE "+stochtestRunTable.getTableName() +
						" SET "+stochtestRunTable.status.getUnqualifiedColName() + " = " + "'"+newStatus.name()+"'" + ", " +
								stochtestRunTable.errmsg.getUnqualifiedColName() + " = " + errMsgRHS + ", " +
								stochtestRunTable.networkGenProbs.getUnqualifiedColName() + " = " + networkGenProbsRHS + " " +
						" WHERE "+stochtestRunTable.id.getUnqualifiedColName()+" = " + acceptedStochtestRun.key.toString();
				stmt = con.createStatement();
System.out.println(sql);
			    int numrows = stmt.executeUpdate(sql);
			    if (numrows != 1){
			    	throw new DataAccessException("failed to update mathgen status for id = "+acceptedStochtestRun.key);
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			con.commit();
		}catch (DataAccessException | SQLException e){
			e.printStackTrace();
			con.rollback();
			throw e;
		}finally {
			con.close();
		}
	}

	public static void finalizeAcceptedStochtestCompare(ConnectionFactory conFactory, StochtestCompare acceptedStochtestCompare, StochtestCompare.StochtestCompareStatus newStatus, String errmsg, SummaryStatistics statistics) throws IllegalArgumentException, SQLException, DataAccessException {
		
		if (newStatus != StochtestCompare.StochtestCompareStatus.verydifferent && newStatus != StochtestCompare.StochtestCompareStatus.not_verydifferent && newStatus != StochtestCompare.StochtestCompareStatus.failed){
			throw new RuntimeException("new status is "+newStatus+", expecting "+StochtestCompare.StochtestCompareStatus.not_verydifferent+" or "+StochtestCompare.StochtestCompareStatus.verydifferent+" or "+StochtestCompare.StochtestCompareStatus.failed);
		}
		
		//
		// update status
		//
		java.sql.Connection con = conFactory.getConnection(new Object());
		try {
			java.sql.Statement stmt = null;
			try {
				StochtestCompareTable stochtestCompareTable = StochtestCompareTable.table;
				String sql = 
					"UPDATE "+stochtestCompareTable.getTableName() +
					" SET "+stochtestCompareTable.status.getUnqualifiedColName() + " = " + "'"+newStatus.name()+"'" + ", ";
					if (errmsg==null){
						sql += stochtestCompareTable.errmsg.getUnqualifiedColName() + " = NULL, ";
					}else{
						sql += stochtestCompareTable.errmsg.getUnqualifiedColName() + " = " + "'"+TokenMangler.getSQLEscapedString(errmsg, 4000)+"', ";
					}
					if (statistics == null){
						sql += stochtestCompareTable.results.getUnqualifiedColName() + " = NULL, ";
						sql += stochtestCompareTable.smallest_pvalue.getUnqualifiedColName() + " = NULL, ";
						sql += stochtestCompareTable.numexperiments.getUnqualifiedColName() + " = NULL, ";
						sql += stochtestCompareTable.numfail_95.getUnqualifiedColName() + " = NULL, ";
						sql += stochtestCompareTable.numfail_99.getUnqualifiedColName() + " = NULL, ";
						sql += stochtestCompareTable.numfail_999.getUnqualifiedColName() + " = NULL ";
					}else{
						sql += stochtestCompareTable.results.getUnqualifiedColName() + " = " + "'"+TokenMangler.getSQLEscapedString(statistics.results(), 4000)+"', ";
						sql += stochtestCompareTable.smallest_pvalue.getUnqualifiedColName() + " = " + statistics.smallestPValue+", ";
						sql += stochtestCompareTable.numexperiments.getUnqualifiedColName() + " = " + statistics.numExperiments+", ";
						sql += stochtestCompareTable.numfail_95.getUnqualifiedColName() + " = " + statistics.numFail_95+", ";
						sql += stochtestCompareTable.numfail_99.getUnqualifiedColName() + " = " + statistics.numFail_99+", ";
						sql += stochtestCompareTable.numfail_999.getUnqualifiedColName() + " = " + statistics.numFail_9999+" ";
					}
					sql += " WHERE "+stochtestCompareTable.id.getUnqualifiedColName()+" = " + acceptedStochtestCompare.key.toString();
				stmt = con.createStatement();
System.out.println(sql);
			    int numrows = stmt.executeUpdate(sql);
			    if (numrows != 1){
			    	throw new DataAccessException("failed to update mathgen status for id = "+acceptedStochtestCompare.key);
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			con.commit();
		}catch (DataAccessException | SQLException e){
			e.printStackTrace();
			con.rollback();
			throw e;
		}finally {
			con.close();
		}
	}

	public static StochtestRun acceptNextWaitingStochtestRun(ConnectionFactory conFactory) throws IllegalArgumentException, SQLException, DataAccessException {
		
		//
		// grab next waiting simulation, change status to accepted, commit and return
		//
		java.sql.Connection con = conFactory.getConnection(new Object());
		StochtestRun stochtestRun = null;
		try {
			java.sql.Statement stmt = null;
			try {
				StochtestTable stochtestTable = StochtestTable.table;
				StochtestRunTable stochtestRunTable = StochtestRunTable.table;
				String sql = "SELECT "+stochtestTable.getSQLColumnList(true, false, "_1") + " , " + stochtestRunTable.getSQLColumnList(true, false, "_2") +
						" FROM "+stochtestRunTable.getTableName() + ", " + stochtestTable.getTableName() + 
						" WHERE "+stochtestTable.id.getQualifiedColName()+" = "+stochtestRunTable.stochtestref.getQualifiedColName() +
						" AND "+stochtestRunTable.status.getQualifiedColName()+" = "+"'"+StochtestRun.StochtestRunStatus.waiting+"'" +
						" AND ROWNUM = 1" +
						" FOR UPDATE " +
						" ORDER BY "+stochtestRunTable.id.getQualifiedColName();
				stmt = con.createStatement();
System.out.println(sql);
			    ResultSet rset = stmt.executeQuery(sql);
			    if (rset.next()) {
			    	Stochtest stochtest = new Stochtest();
java.sql.ResultSetMetaData rsetMetaData = rset.getMetaData();
int numColumns = rsetMetaData.getColumnCount();
for (int i = 0; i < numColumns; i++){
	System.out.println("RESULTS "+i+") table_name="+rsetMetaData.getTableName(i+1)+
			", catalog_name="+rsetMetaData.getCatalogName(i+1)+
			", col_class_name="+rsetMetaData.getColumnClassName(i+1)+
			", col_type_name="+rsetMetaData.getColumnTypeName(i+1)+
			", col_name="+rsetMetaData.getColumnName(i+1)+
			", col_label="+rsetMetaData.getColumnLabel(i+1)+
			", schema_name="+rsetMetaData.getSchemaName(i+1));
}

			    	stochtest.key = new KeyValue(rset.getBigDecimal(stochtestTable.id.getUnqualifiedColName()+"_1"));
			    	stochtest.simContextRef = new KeyValue(rset.getBigDecimal(stochtestTable.simcontextref.getUnqualifiedColName()+"_1"));
			    	stochtest.biomodelRef = new KeyValue(rset.getBigDecimal(stochtestTable.biomodelref.getUnqualifiedColName()+"_1"));
			    	stochtest.mathRef = new KeyValue(rset.getBigDecimal(stochtestTable.mathref.getUnqualifiedColName()+"_1"));
			    	stochtest.dimension = rset.getInt(stochtestTable.dimension.getUnqualifiedColName()+"_1");
			    	stochtest.numcompartments = rset.getInt(stochtestTable.numcompartments.getUnqualifiedColName()+"_1");
			    	stochtest.mathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestTable.mathtype.getUnqualifiedColName()+"_1"));
			        
			    	stochtestRun = new StochtestRun();
			        stochtestRun.key = new KeyValue(rset.getBigDecimal(stochtestRunTable.id.getUnqualifiedColName()+"_2"));
			        stochtestRun.stochtest = stochtest;
			        stochtestRun.parentMathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestRunTable.parentmathtype.getUnqualifiedColName()+"_2"));
			        stochtestRun.mathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestRunTable.mathtype.getUnqualifiedColName()+"_2"));
			        stochtestRun.status = StochtestRun.StochtestRunStatus.valueOf(rset.getString(stochtestRunTable.status.getUnqualifiedColName()+"_2"));
			        stochtestRun.errmsg = rset.getString(stochtestRunTable.errmsg.getUnqualifiedColName()+"_2");
			        stochtestRun.conclusion = rset.getString(stochtestRunTable.conclusion.getUnqualifiedColName()+"_2");
			        stochtestRun.exclude = rset.getString(stochtestRunTable.exclude.getUnqualifiedColName()+"_2");
			        stochtestRun.networkGenProbs = rset.getString(stochtestRunTable.networkGenProbs.getUnqualifiedColName()+"_2");
			    }else{
			    	return null;
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
			
			
			try {
				StochtestRunTable stochtestRunTable = StochtestRunTable.table;
				String sql = "UPDATE "+stochtestRunTable.getTableName() +
						" SET "+stochtestRunTable.status.getUnqualifiedColName() + " = " + "'"+StochtestRun.StochtestRunStatus.accepted+"'" +
						" WHERE "+stochtestRunTable.id.getUnqualifiedColName()+" = " + stochtestRun.key.toString();
				stmt = con.createStatement();
System.out.println(sql);
			    int numrows = stmt.executeUpdate(sql);
			    if (numrows != 1){
			    	throw new DataAccessException("failed to update mathgen status for id = "+stochtestRun.key);
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			con.commit();
		}catch (DataAccessException | SQLException e){
			e.printStackTrace();
			con.rollback();
			throw e;
		}finally {
			con.close();
		}
		return stochtestRun;
	}

	public static StochtestCompare acceptNextWaitingStochtestCompare(ConnectionFactory conFactory) throws IllegalArgumentException, SQLException, DataAccessException {
		
		//
		// grab next waiting simulation, change status to accepted, commit and return
		//
		java.sql.Connection con = conFactory.getConnection(new Object());
		StochtestCompare stochtestCompare = null;
		try {
			java.sql.Statement stmt = null;
			try {
				StochtestCompareTable stochtestCompareTable = StochtestCompareTable.table;
				String sql = "SELECT "+stochtestCompareTable.getTableName()+".* "+
						" FROM "+stochtestCompareTable.getTableName() + 
						" WHERE "+stochtestCompareTable.status.getQualifiedColName()+" = "+"'"+StochtestCompare.StochtestCompareStatus.waiting+"'" +
						" AND ROWNUM = 1" +
						" FOR UPDATE " +
						" ORDER BY "+stochtestCompareTable.id.getQualifiedColName();
				stmt = con.createStatement();
System.out.println(sql);
			    ResultSet rset = stmt.executeQuery(sql);
			    if (rset.next()) {
			    	stochtestCompare = new StochtestCompare();
java.sql.ResultSetMetaData rsetMetaData = rset.getMetaData();
int numColumns = rsetMetaData.getColumnCount();
for (int i = 0; i < numColumns; i++){
	System.out.println("RESULTS "+i+") table_name="+rsetMetaData.getTableName(i+1)+
			", catalog_name="+rsetMetaData.getCatalogName(i+1)+
			", col_class_name="+rsetMetaData.getColumnClassName(i+1)+
			", col_type_name="+rsetMetaData.getColumnTypeName(i+1)+
			", col_name="+rsetMetaData.getColumnName(i+1)+
			", col_label="+rsetMetaData.getColumnLabel(i+1)+
			", schema_name="+rsetMetaData.getSchemaName(i+1));
}

			    	stochtestCompare.key = new KeyValue(rset.getBigDecimal(stochtestCompareTable.id.getUnqualifiedColName()));
			    	stochtestCompare.stochtestRun1ref = new KeyValue(rset.getBigDecimal(stochtestCompareTable.stochtestrunref1.getUnqualifiedColName()));
			    	stochtestCompare.stochtestRun2ref = new KeyValue(rset.getBigDecimal(stochtestCompareTable.stochtestrunref2.getUnqualifiedColName()));
			    	stochtestCompare.status = StochtestCompare.StochtestCompareStatus.valueOf(rset.getString(stochtestCompareTable.status.getUnqualifiedColName()));
			        stochtestCompare.errmsg = rset.getString(stochtestCompareTable.errmsg.getUnqualifiedColName());
			        stochtestCompare.results = rset.getString(stochtestCompareTable.results.getUnqualifiedColName());
			        stochtestCompare.conclusion = rset.getString(stochtestCompareTable.conclusion.getUnqualifiedColName());
			    }else{
			    	return null;
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
			
			
			try {
				StochtestCompareTable stochtestCompareTable = StochtestCompareTable.table;
				String sql = "UPDATE "+stochtestCompareTable.getTableName() +
						" SET "+stochtestCompareTable.status.getUnqualifiedColName() + " = " + "'"+StochtestCompare.StochtestCompareStatus.accepted+"'" +
						" WHERE "+stochtestCompareTable.id.getUnqualifiedColName()+" = " + stochtestCompare.key.toString();
				stmt = con.createStatement();
System.out.println(sql);
			    int numrows = stmt.executeUpdate(sql);
			    if (numrows != 1){
			    	throw new DataAccessException("failed to update mathgen status for id = "+stochtestCompare.key);
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			con.commit();
		}catch (DataAccessException | SQLException e){
			e.printStackTrace();
			con.rollback();
			throw e;
		}finally {
			con.close();
		}
		return stochtestCompare;
	}

}
