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

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.document.KeyValue;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
/**
 * This type was created in VisualAge.
 */
public class SQLCreateAllTables {
	private final static Logger lg = LogManager.getLogger(SQLCreateAllTables.class);

	public static final String POSTGRES_DUAL_VIEW = "public.dual";
	public static final String DEFAULT_APICLIENT_ID = "85133f8d-26f7-4247-8356-d175399fc2e6";
	public static final String DEFAULT_APICLIENT_NAME = "defaultApiClient";

	/**
 * Insert the method's description here.
 * Creation date: (10/2/2003 12:40:00 PM)
 * @param indexName java.lang.String
 * @param vcTable cbit.sql.Table
 * @param vcTable_Field cbit.sql.Field
 */
private static void createIndex(Connection con,String indexName, Table vcTable, Field vcTable_Field, DatabaseSyntax dbSyntax) throws SQLException{

	//
	// create indices for faster lookup
	//
	java.sql.Statement stmt = null;
	try{
		stmt = con.createStatement();
		try{
			if (dbSyntax==DatabaseSyntax.ORACLE){
				stmt.executeUpdate("DROP INDEX "+indexName);
			}else if (dbSyntax==DatabaseSyntax.POSTGRES){
				stmt.executeUpdate("DROP INDEX IF EXISTS " + indexName);
			}else{
				throw new RuntimeException("unexpected Database Syntax '"+dbSyntax+"'");
			}
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
	private static String getCreateIndexSQL(String indexName, Table vcTable, Field vcTable_Field) {
		return "CREATE INDEX "+indexName+" ON "+vcTable.getTableName()+"("+vcTable_Field.getUnqualifiedColName()+")";
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
private static void createTables(Connection con, Table tables[], DatabaseSyntax dbSyntax) throws SQLException {

	//
	// make tables
	// 
	boolean status;
	PreparedStatement pps;
	for (int i=0;i<tables.length;i++){
		String sql = tables[i].getCreateSQL(dbSyntax);
		System.out.println(sql);
		pps = con.prepareStatement(sql);
		status = pps.execute();
		pps.close();
	}
}

	private static void createViews(Connection con, DatabaseSyntax dbSyntax) throws SQLException {
		if (dbSyntax != DatabaseSyntax.POSTGRES){
			return;
		}
		//
		// add fake DUAL table for ORACLE compatibility
		//
		String sql = getCreateViewSql(dbSyntax);
		System.out.println(sql);
		PreparedStatement pps = con.prepareStatement(sql);
		boolean status = pps.execute();
		pps.close();
	}

	private static String getCreateViewSql(DatabaseSyntax dbSyntax) {
		if (dbSyntax != DatabaseSyntax.POSTGRES){
			return null;
		}
		return "CREATE VIEW " + POSTGRES_DUAL_VIEW + " AS SELECT CAST('X' as varchar) AS dummy";
	}


private static void destroyAndRecreateTables(ConnectionFactory conFactory, KeyFactory keyFactory, DatabaseSyntax dbSyntax) {
	try {
		JPanel panel = new JPanel(new BorderLayout());
		JCheckBox c1 = new JCheckBox("Drop all tables");
		JCheckBox c2 = new JCheckBox("Create all tables");
		JCheckBox c3 = new JCheckBox("initialize User, Group, and Available tables");
		panel.add(c1, BorderLayout.NORTH);
		panel.add(c2, BorderLayout.CENTER);
		panel.add(c3, BorderLayout.SOUTH);
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
					dropViews(con, dbSyntax);
					dropTables(con, tables, dbSyntax);
					dropSequence(con, keyFactory);
				}
				if (c2.isSelected()) {
					createTables(con, tables, dbSyntax);
					createViews(con, dbSyntax);
					createSequence(con, keyFactory);
					//
					// Add special table entries
					//
					if (c3.isSelected()){
						Statement s = con.createStatement();
						try {
							// Add void user
							s.executeUpdate(cbit.vcell.modeldb.UserTable.getCreateVoidUserSQL());
							// Add test user
							s.executeUpdate(cbit.vcell.modeldb.UserTable.getCreateTestUserSQL(keyFactory));
							// Add VCellSupport user
							s.executeUpdate(cbit.vcell.modeldb.UserTable.getCreateVCellSupportUserSQL(keyFactory));
							// Add PRIVATE group
							s.executeUpdate(cbit.vcell.modeldb.GroupTable.getCreateGroupPrivateSQL(keyFactory.getNewKey(con)));
							// Add PUBLIC group
							s.executeUpdate(cbit.vcell.modeldb.GroupTable.getCreateGroupPublicSQL(keyFactory.getNewKey(con)));
							// Add Initial Available Status
							s.executeUpdate(cbit.vcell.modeldb.AvailableTable.getCreateInitAvailStatusSQL(keyFactory.getNewKey(con)));
							// Add Default API Client
							final String DEFAULT_APICLIENTID_PSWD = UUID.randomUUID().toString();
							ApiClient defaultApiClient = new ApiClient(null,DEFAULT_APICLIENT_NAME, DEFAULT_APICLIENT_ID, DEFAULT_APICLIENTID_PSWD);
							s.executeUpdate(ApiClientTable.getCreateApiClientSQL(keyFactory.getNewKey(con), defaultApiClient));
						}finally {
							s.close();
						}
					}
					//
					//Create Indexes
					//
					createIndex(con,"grp_grpid",cbit.vcell.modeldb.GroupTable.table,cbit.vcell.modeldb.GroupTable.table.groupid, dbSyntax);
					createIndex(con,"browse_imgref",cbit.vcell.modeldb.BrowseImageDataTable.table,cbit.vcell.modeldb.BrowseImageDataTable.table.imageRef, dbSyntax);
					createIndex(con,"geom_extentref",cbit.vcell.modeldb.GeometryTable.table,cbit.vcell.modeldb.GeometryTable.table.extentRef, dbSyntax);
					createIndex(con,"geom_imageref",cbit.vcell.modeldb.GeometryTable.table,cbit.vcell.modeldb.GeometryTable.table.imageRef, dbSyntax);
					createIndex(con,"mathdesc_geomref",cbit.vcell.modeldb.MathDescTable.table,cbit.vcell.modeldb.MathDescTable.table.geometryRef, dbSyntax);
					createIndex(con,"simcstat_simcref",cbit.vcell.modeldb.SimContextStatTable.table,cbit.vcell.modeldb.SimContextStatTable.table.simContextRef, dbSyntax);
				}
				con.commit();
			} catch (SQLException exc) {
				con.rollback();
				lg.error(exc);
			} finally {
				conFactory.release(con, lock);
			}
		} else {
			throw new RuntimeException("Aborted by user");
		}
		System.exit(0);
	} catch (Throwable exc) {
		lg.error(exc);
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
		lg.error("Exception Dropping Sequence", e);
	}finally{
		stmt.close();
	}

}

	private static void dropViews(Connection con, DatabaseSyntax dbSyntax) throws SQLException {
		if (dbSyntax != DatabaseSyntax.POSTGRES){
			return;
		}
		PreparedStatement pps = null;
		try {
			String sql = "DROP VIEW IF EXISTS "+POSTGRES_DUAL_VIEW;
			System.out.println(sql);
			pps = con.prepareStatement(sql);
			boolean status = pps.execute();
		} catch (Exception e) {
			//lg.error(e);
			System.out.println(" View " + POSTGRES_DUAL_VIEW +" Not dropped. "+e.getMessage());
		}finally{
			if (pps != null){
				pps.close();
			}
		}
	}



/**
 * This method was created in VisualAge.
 */
private static void dropTables(Connection con, Table tables[], DatabaseSyntax dbSyntax) throws SQLException {
	boolean status;
	PreparedStatement pps = null;
	for (int i = tables.length-1; i >= 0; i--) {
		try {
			String sql = "DROP TABLE " + tables[i].getTableName() + " CASCADE CONSTRAINTS";
			if (dbSyntax==DatabaseSyntax.POSTGRES){
				sql = "DROP TABLE IF EXISTS " + tables[i].getTableName() + " CASCADE";
			}
			System.out.println(sql);
			pps = con.prepareStatement(sql);
			status = pps.execute();
		} catch (Exception e) {
			//lg.error(e);
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
		cbit.vcell.dictionary.db.ProteinTable.table,
		cbit.vcell.dictionary.db.ProteinAliasTable.table,
		cbit.vcell.dictionary.db.CompoundTable.table,
		cbit.vcell.dictionary.db.CompoundAliasTable.table,
		cbit.vcell.dictionary.db.EnzymeTable.table,
		cbit.vcell.dictionary.db.EnzymeAliasTable.table,
		cbit.vcell.dictionary.db.EnzymeReactionTable.table,
		cbit.vcell.dictionary.db.DBSpeciesTable.table,
		cbit.vcell.modeldb.SpecialUsersTable.table,
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
		cbit.vcell.modeldb.SurfaceClassTable.table, // new
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
		cbit.vcell.modeldb.SimStatTable.table,  // not production
		//cbit.vcell.modeldb.ResultSetMetaDataTable.table,
		cbit.vcell.modeldb.BioModelTable.table,
		cbit.vcell.modeldb.BioModelSimContextLinkTable.table,
		cbit.vcell.modeldb.BioModelSimulationLinkTable.table,
		cbit.vcell.modeldb.MathModelTable.table,
		cbit.vcell.modeldb.MathModelSimulationLinkTable.table,
		cbit.vcell.modeldb.AvailableTable.table,
		cbit.vcell.modeldb.UserStatTable.table,
		cbit.vcell.modeldb.UserLogTable.table,
		cbit.vcell.modeldb.SimContextStatTable.table, // not production
		cbit.vcell.modeldb.SimContextStat2Table.table, // not production
		cbit.vcell.modeldb.StochtestTable.table,
		cbit.vcell.modeldb.StochtestRunTable.table,
		cbit.vcell.modeldb.StochtestCompareTable.table,
		//cbit.vcell.modeldb.ResultSetExportsTable.table, // not used anymore
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
		cbit.vcell.modeldb.ApiClientTable.table,
		cbit.vcell.modeldb.ApiAccessTokenTable.table,
		cbit.vcell.modeldb.PublicationTable.table,
		cbit.vcell.modeldb.PublicationModelLinkTable.table,
		cbit.vcell.modeldb.ApplicationMathTable.table, // new
		cbit.vcell.modeldb.DataSymbolTable.table, // new
		cbit.vcell.modeldb.GlobalModelParameterTable.table, // new
		LoadModelsStatTable.table, // new
		cbit.vcell.modeldb.UserLoginInfoTable.table, // new
		cbit.vcell.modeldb.VCMetaDataTable.table, // new
		cbit.vcell.modeldb.SimDelFromDiskTable.table, // new
		};
	return tables;
}

public static void writeScript(DatabaseSyntax dbSyntax, boolean bootstrapData, Writer scriptWriter) throws IOException {
	Table[] tables = getVCellTables();
	for (int i=0;i<tables.length;i++){
		String sql = tables[i].getCreateSQL(dbSyntax);
		scriptWriter.write(sql + ";\n");
	}
	String createViewSql = getCreateViewSql(dbSyntax);
	if (createViewSql != null){
		scriptWriter.write("\n");
		scriptWriter.write(createViewSql + ";\n");
	}
	scriptWriter.write("\n");
	if (dbSyntax == DatabaseSyntax.ORACLE || dbSyntax == DatabaseSyntax.POSTGRES) {
		scriptWriter.write("CREATE SEQUENCE " + Table.SEQ + ";\n");
	}
	scriptWriter.write("\n");
	scriptWriter.write(getCreateIndexSQL("grp_grpid", GroupTable.table, GroupTable.table.groupid) + ";\n");
	scriptWriter.write(getCreateIndexSQL("browse_imgref", BrowseImageDataTable.table, BrowseImageDataTable.table.imageRef) + ";\n");
	scriptWriter.write(getCreateIndexSQL("geom_extentref", GeometryTable.table, GeometryTable.table.extentRef) + ";\n");
	scriptWriter.write(getCreateIndexSQL("geom_imageref", GeometryTable.table, GeometryTable.table.imageRef) + ";\n");
	scriptWriter.write(getCreateIndexSQL("mathdesc_geomref", MathDescTable.table, MathDescTable.table.geometryRef) + ";\n");
	scriptWriter.write(getCreateIndexSQL("simcstat_simcref", SimContextStatTable.table, SimContextStatTable.table.simContextRef) + ";\n");
	scriptWriter.write("\n");
	if (bootstrapData) {
		KeyFactory bootstrapKeyFactory = new KeyFactory() {
			int currentKey = -1;
			@Override public String getCreateSQL() { throw new RuntimeException("not implemented"); }
			@Override public String getDestroySQL() { throw new RuntimeException("not implemented"); }
			@Override public String nextSEQ() { currentKey++; return Integer.toString(currentKey); }
			@Override public String currSEQ() { return Integer.toString(currentKey); }
			@Override public KeyValue getNewKey(Connection _con) { return new KeyValue(getUniqueBigDecimal(_con)); }
			@Override public BigDecimal getUniqueBigDecimal(Connection _con) { currentKey++; return new BigDecimal(currentKey); }
		};
		final Connection bootstrapConnection = null;
		// Add void user
		bootstrapKeyFactory.nextSEQ(); // for void user with key 0
		scriptWriter.write(UserTable.getCreateVoidUserSQL() + ";\n");
		// Add admin user
		bootstrapKeyFactory.nextSEQ();
		bootstrapKeyFactory.nextSEQ(); // for admin user with key 2
		scriptWriter.write(UserTable.getCreateAdministratorUserSQL() + ";\n");
		// Add test user
		scriptWriter.write(UserTable.getCreateTestUserSQL(bootstrapKeyFactory) + ";\n");
		// Add VCellSupport user
		scriptWriter.write(UserTable.getCreateVCellSupportUserSQL(bootstrapKeyFactory) + ";\n");
		try {
			// Add PRIVATE group
			scriptWriter.write(GroupTable.getCreateGroupPrivateSQL(bootstrapKeyFactory.getNewKey(bootstrapConnection)) + ";\n");
			// Add PUBLIC group
			scriptWriter.write(GroupTable.getCreateGroupPublicSQL(bootstrapKeyFactory.getNewKey(bootstrapConnection)) + ";\n");
			// Add Initial Available Status
			scriptWriter.write(AvailableTable.getCreateInitAvailStatusSQL(bootstrapKeyFactory.getNewKey(bootstrapConnection)) + ";\n");
			// Add Default API Client
			final String DEFAULT_APICLIENTID_PSWD = UUID.randomUUID().toString();
			ApiClient defaultApiClient = new ApiClient(null, DEFAULT_APICLIENT_NAME, DEFAULT_APICLIENT_ID, DEFAULT_APICLIENTID_PSWD);
			scriptWriter.write(ApiClientTable.getCreateApiClientSQL(bootstrapKeyFactory.getNewKey(bootstrapConnection), defaultApiClient) + ";\n");
		} catch (SQLException e){
			throw new RuntimeException("impossible SQLException in bootstrap code", e);
		}
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
    	final String usage = "\nUsage: ("+oracle+"|"+postgres+") connectUrl schemaUser schemaUserPassword\n";
    	
        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }
        String connectURL = args[1];
        String dbSchemaUser = args[2];
        String dbPassword = args[3];
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

		PropertyLoader.setProperty(PropertyLoader.dbConnectURL, connectURL);
		PropertyLoader.setProperty(PropertyLoader.dbUserid, dbSchemaUser);
		PropertyLoader.setProperty(PropertyLoader.dbPasswordValue, dbPassword);

        ConnectionFactory conFactory = null;
        KeyFactory keyFactory = null;
        if (args[0].equalsIgnoreCase(oracle)) {
        	PropertyLoader.setProperty(PropertyLoader.dbDriverName, "oracle.jdbc.driver.OracleDriver");
            conFactory = DatabaseService.getInstance().createConnectionFactory();
        }else if (args[0].equalsIgnoreCase(postgres)){
    		PropertyLoader.setProperty(PropertyLoader.dbDriverName, "org.postgresql.Driver");
            conFactory = DatabaseService.getInstance().createConnectionFactory();
        } else {
            System.out.println(usage);
            System.exit(1);
        }
        destroyAndRecreateTables(conFactory, conFactory.getKeyFactory(), conFactory.getDatabaseSyntax());
    } catch (Throwable e) {
        lg.error(e);
    }
    System.exit(0);
}
}
