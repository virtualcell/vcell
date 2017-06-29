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

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.vcell.util.SessionLog;

import cbit.sql.ConnectionFactory;
import cbit.sql.Field;
import cbit.sql.KeyFactory;
import cbit.sql.MysqlConnectionFactory;
import cbit.sql.MysqlKeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.sql.Table;
import cbit.vcell.resource.StdoutSessionLog;
/**
 * This type was created in VisualAge.
 */
public class SQLCreateAllTables {
/**
 * Insert the method's description here.
 * Creation date: (10/2/2003 12:40:00 PM)
 * @param indexName java.lang.String
 * @param vcTable cbit.sql.Table
 * @param vcTable_Field cbit.sql.Field
 */
private static void createIndex(Connection con,String indexName, Table vcTable, Field vcTable_Field) throws SQLException{
	
	//
	// create indices for faster lookup
	//
	java.sql.Statement stmt = null;
	try{
		stmt = con.createStatement();
		try{
			stmt.executeUpdate("DROP INDEX "+indexName);
		}catch (SQLException e){
			if(e.getErrorCode() != 1418){
				throw e;
			}
		}
		stmt.executeUpdate("CREATE INDEX "+indexName+" ON "+vcTable.getTableName()+"("+vcTable_Field.getUnqualifiedColName()+")");
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}
}
/**
 * This method was created in VisualAge.
 */
private static void createSequence(Connection con, KeyFactory keyFactory) throws SQLException {

	//
	// create new sequence
	// 
	String sql = keyFactory.getCreateSQL();
	System.out.println(sql);
	Statement stmt = null;
	try {
		stmt = con.createStatement();
		stmt.execute(sql);
	}finally{
		stmt.close();
	}	
}
/**
 * This method was created in VisualAge.
 */
private static void createTables(Connection con, Table tables[]) throws SQLException {

	//
	// make tables
	// 
	boolean status;
	PreparedStatement pps;
	for (int i=0;i<tables.length;i++){
		String sql = tables[i].getCreateSQL();
		System.out.println(sql);
		pps = con.prepareStatement(sql);
		status = pps.execute();
		pps.close();
	}
}
/**
 * This method was created in VisualAge.
 */
private static void destroyAndRecreateTables(SessionLog log, ConnectionFactory conFactory, KeyFactory keyFactory) {
	try {
		JPanel panel = new JPanel(new BorderLayout());
		JCheckBox c1 = new JCheckBox("Drop all tables");
		JCheckBox c2 = new JCheckBox("Create all tables");
		panel.add(c1, BorderLayout.NORTH);
		panel.add(c2, BorderLayout.SOUTH);
		int ok =
			JOptionPane.showConfirmDialog(
				new JFrame(),
				panel,
				"Select Action(s)",
				JOptionPane.OK_CANCEL_OPTION);
		if (ok == JOptionPane.OK_OPTION && (c1.isSelected() || c2.isSelected())) {
			Connection con = null;
			Object lock = new Object();
			try {
				con = conFactory.getConnection(lock);
				System.out.println("connected....");
				Table tables[] = getVCellTables();
				if (c1.isSelected()) {
					dropTables(con, tables);
					dropSequence(con, keyFactory);
				}
				if (c2.isSelected()) {
					createTables(con, tables);
					createSequence(con, keyFactory);
					//
					// Add special table entries
					//
					Statement s = con.createStatement();
					try {
						// Add void user
						s.executeUpdate(cbit.vcell.modeldb.UserTable.getCreateVoidUserSQL());
						// Add PRIVATE group
						s.executeUpdate(cbit.vcell.modeldb.GroupTable.getCreateGroupPrivateSQL(keyFactory.getNewKey(con)));
						// Add PUBLIC group
						s.executeUpdate(cbit.vcell.modeldb.GroupTable.getCreateGroupPublicSQL(keyFactory.getNewKey(con)));
						// Add Initial Available Status
						s.executeUpdate(cbit.vcell.modeldb.AvailableTable.getCreateInitAvailStatusSQL(keyFactory.getNewKey(con)));
					}finally {
						s.close();
					}
					//
					//Create Indexes
					//
					createIndex(con,"grp_grpid",cbit.vcell.modeldb.GroupTable.table,cbit.vcell.modeldb.GroupTable.table.groupid);
					createIndex(con,"browse_imgref",cbit.vcell.modeldb.BrowseImageDataTable.table,cbit.vcell.modeldb.BrowseImageDataTable.table.imageRef);
					createIndex(con,"geom_extentref",cbit.vcell.modeldb.GeometryTable.table,cbit.vcell.modeldb.GeometryTable.table.extentRef);
					createIndex(con,"geom_imageref",cbit.vcell.modeldb.GeometryTable.table,cbit.vcell.modeldb.GeometryTable.table.imageRef);
					createIndex(con,"mathdesc_geomref",cbit.vcell.modeldb.MathDescTable.table,cbit.vcell.modeldb.MathDescTable.table.geometryRef);
					createIndex(con,"simcstat_simcref",cbit.vcell.modeldb.SimContextStatTable.table,cbit.vcell.modeldb.SimContextStatTable.table.simContextRef);
				}
				con.commit();
			} catch (SQLException exc) {
				con.rollback();
				System.out.println(exc.getMessage());
				exc.printStackTrace(System.out);
			} finally {
				conFactory.release(con, lock);
			}
		} else {
			throw new RuntimeException("Aborted by user");
		}
		System.exit(0);
	} catch (Throwable exc) {
		System.out.println(exc.getMessage());
		exc.printStackTrace(System.out);
	}	
}
/**
 * This method was created in VisualAge.
 */
private static void dropSequence(Connection con, KeyFactory keyFactory) throws SQLException {

	//
	// drop old sequence
	// 
	String sql = keyFactory.getDestroySQL();
	System.out.println(sql);
	Statement stmt = null;
	try {
		stmt = con.createStatement();
		stmt.execute(sql);
	} catch (SQLException e) {
		e.printStackTrace(System.out);
		System.out.println("Exception Dropping Sequence");
	}finally{
		stmt.close();
	}

}
/**
 * This method was created in VisualAge.
 */
private static void dropTables(Connection con, Table tables[]) throws SQLException {
	boolean status;
	PreparedStatement pps = null;
	for (int i = tables.length-1; i >= 0; i--) {
		try {
			String sql = "DROP TABLE " + tables[i].getTableName() + " CASCADE CONSTRAINTS";
			System.out.println(sql);
			pps = con.prepareStatement(sql);
			status = pps.execute();
		} catch (Exception e) {
			//e.printStackTrace(System.out);
			System.out.println(" Table " + tables[i].getTableName()+" Not dropped. "+e.getMessage());
		}finally{
			if (pps != null){
				pps.close();
			}
		}
	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Table[]
 */
public static Table[] getVCellTables() {
	Table tables[] = {
		cbit.vcell.modeldb.UserTable.table,
		cbit.vcell.modeldb.GroupTable.table,
		cbit.vcell.modeldb.ModelTable.table,
		cbit.vcell.modeldb.CellTypeTable.table,
		cbit.vcell.modeldb.StructTable.table,
		cbit.vcell.modeldb.ReactStepTable.table,
		cbit.vcell.modeldb.SpeciesTable.table,
		cbit.vcell.modeldb.SpeciesContextModelTable.table,
		cbit.vcell.modeldb.ModelStructLinkTable.table,
		cbit.vcell.modeldb.ReactPartTable.table,
		cbit.vcell.modeldb.DiagramTable.table,
		cbit.vcell.modeldb.ExtentTable.table,
		cbit.vcell.modeldb.ImageTable.table,
		cbit.vcell.modeldb.ImageDataTable.table,
		cbit.vcell.modeldb.BrowseImageDataTable.table,
		cbit.vcell.modeldb.ImageRegionTable.table,
		cbit.vcell.modeldb.GeometryTable.table,
		cbit.vcell.modeldb.SubVolumeTable.table,
		cbit.vcell.modeldb.MathDescTable.table,
		cbit.vcell.modeldb.SimContextTable.table,
		cbit.vcell.modeldb.SpeciesContextSpecTable.table,
		cbit.vcell.modeldb.StructureMappingTable.table,
		cbit.vcell.modeldb.ReactionSpecTable.table,
		cbit.vcell.modeldb.StimulusTable.table,
		cbit.vcell.modeldb.AnalysisTaskXMLTable.table,
		cbit.vcell.modeldb.FilamentTable.table,
		cbit.vcell.modeldb.CurveTable.table,
		cbit.vcell.modeldb.SimulationTable.table,
		cbit.vcell.modeldb.SimStatTable.table,
		cbit.vcell.modeldb.ResultSetMetaDataTable.table,
		cbit.vcell.modeldb.BioModelTable.table,
		cbit.vcell.modeldb.BioModelSimContextLinkTable.table,
		cbit.vcell.modeldb.BioModelSimulationLinkTable.table,
		cbit.vcell.modeldb.MathModelTable.table,
		cbit.vcell.modeldb.MathModelSimulationLinkTable.table,
		cbit.vcell.modeldb.AvailableTable.table,
		cbit.vcell.modeldb.UserStatTable.table,
		cbit.vcell.modeldb.UserLogTable.table,
		cbit.vcell.dictionary.db.ProteinTable.table,
		cbit.vcell.dictionary.db.ProteinAliasTable.table,
		cbit.vcell.dictionary.db.CompoundTable.table,
		cbit.vcell.dictionary.db.CompoundAliasTable.table,
		cbit.vcell.dictionary.db.EnzymeTable.table,
		cbit.vcell.dictionary.db.EnzymeAliasTable.table,
		cbit.vcell.dictionary.db.EnzymeReactionTable.table,
		cbit.vcell.modeldb.SimContextStatTable.table,
		cbit.vcell.modeldb.SimContextStat2Table.table,
		cbit.vcell.modeldb.StochtestTable.table,
		cbit.vcell.modeldb.StochtestRunTable.table,
		cbit.vcell.modeldb.StochtestCompareTable.table,
		cbit.vcell.dictionary.db.DBSpeciesTable.table,
		cbit.vcell.modeldb.ResultSetExportsTable.table,
		cbit.vcell.messaging.db.SimulationJobTable.table,
		cbit.vcell.modeldb.BioModelXMLTable.table,
		cbit.vcell.modeldb.MathModelXMLTable.table,
		cbit.vcell.modeldb.GeometricRegionTable.table,
		cbit.vcell.modeldb.SurfaceDescriptionTable.table,
		cbit.vcell.modeldb.UserPreferenceTable.table,
		cbit.vcell.modeldb.TFTestSuiteTable.table,
		cbit.vcell.modeldb.TFTestCaseTable.table,
		cbit.vcell.modeldb.TFTestCriteriaTable.table,
		cbit.vcell.modeldb.TFTestResultTable.table,
		cbit.vcell.modeldb.SoftwareVersionTable.table,
		cbit.vcell.modeldb.ExternalDataTable.table,
		cbit.vcell.modeldb.MathDescExternalDataLinkTable.table,
		cbit.vcell.modeldb.MIRIAMTable.table,
		cbit.vcell.messaging.db.ServiceTable.table,
		cbit.vcell.modeldb.ApiClientTable.table,
		cbit.vcell.modeldb.ApiAccessTokenTable.table,
		cbit.vcell.modeldb.StochtestTable.table,
		cbit.vcell.modeldb.StochtestRunTable.table,
		cbit.vcell.modeldb.StochtestCompareTable.table,
		cbit.vcell.modeldb.PublicationTable.table,
		cbit.vcell.modeldb.PublicationModelLinkTable.table
		};
	return tables;
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
    //
    try {
        if (args.length != 5) {
            System.out.println(
                "Usage: (oracle|mysql) host databaseSID schemaUser schemaUserPassword");
            System.exit(0);
        }
        String driverName = "oracle.jdbc.driver.OracleDriver";
        String host = args[1];
        String db = args[2];
        String connectURL = "jdbc:oracle:thin:@" + host + ":1521:" + db;
        String dbSchemaUser = args[3];
        String dbPassword = args[4];
        //
        int ok =
            javax.swing.JOptionPane.showConfirmDialog(
                new JFrame(),
                "EXTREME DANGER!!!!\nAll Tables in Schema below will be DESTROYED and re-created"
                    + "connectURL="
                    + connectURL
                    + "\nUser="
                    + dbSchemaUser
                    + "\npassword="
                    + dbPassword,
                "Confirm",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);
        if (ok != javax.swing.JOptionPane.OK_OPTION) {
            throw new RuntimeException("Aborted by user");
        }

        SessionLog log = new StdoutSessionLog("SQLCreateAllTables");
        ConnectionFactory conFactory = null;
        KeyFactory keyFactory = null;
        new cbit.vcell.resource.PropertyLoader();
        if (args[0].equalsIgnoreCase("ORACLE")) {
            conFactory =
                new OraclePoolingConnectionFactory(
                    log,
                    driverName,
                    connectURL,
                    dbSchemaUser,
                    dbPassword);
            keyFactory = new OracleKeyFactory();
        } else
            if (args[0].equalsIgnoreCase("MYSQL")) {
                conFactory = new MysqlConnectionFactory();
                keyFactory = new MysqlKeyFactory();
            } else {
                System.out.println(
                    "Usage: (oracle|mysql) host databaseSID schemaUser schemaUserPassword");
                System.exit(1);
            }
        destroyAndRecreateTables(log, conFactory, keyFactory);
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }
    System.exit(0);
}
}
