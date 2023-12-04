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

import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.vcell_4_8.MathMapping_4_8;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathCompareResults.Decision;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.VCMLComparator;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.BigString;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.*;

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;


public class MathVerifier {

    public enum DatabaseMode {
        destroy,
        create,
        update,
        skip
    }

    private static final Logger lg = LogManager.getLogger(DbDriver.class);

    private final AdminDatabaseServer adminDbServer;
    private final ConnectionFactory conFactory;
    private final DatabaseServerImpl dbServerImpl;
    private Timestamp timeStamp;

    /**
     * ResultSetCrawler constructor comment.
     */
    public MathVerifier(ConnectionFactory argConFactory,
                        AdminDatabaseServer argAdminDbServer) throws DataAccessException{
        this.conFactory = argConFactory;
        this.adminDbServer = argAdminDbServer;
        this.dbServerImpl = new DatabaseServerImpl(conFactory, conFactory.getKeyFactory());
    }


    public static MathVerifier createMathVerifier(
            String dbHostName, String dbName, String dbSchemaUser, String dbPassword) throws Exception{

        ConnectionFactory conFactory;
        KeyFactory keyFactory;

        String driverName = "oracle.jdbc.driver.OracleDriver";
        String connectURL = "jdbc:oracle:thin:@" + dbHostName + ":1521:" + dbName;

        //
        // get appropriate database factory objects
        //
        PropertyLoader.setProperty(PropertyLoader.dbDriverName, driverName);
        PropertyLoader.setProperty(PropertyLoader.dbConnectURL, connectURL);
        PropertyLoader.setProperty(PropertyLoader.dbUserid, dbSchemaUser);
        PropertyLoader.setProperty(PropertyLoader.dbPasswordValue, dbPassword);
        conFactory = DatabaseService.getInstance().createConnectionFactory();
        keyFactory = conFactory.getKeyFactory();

        AdminDatabaseServer adminDbServer =
                new LocalAdminDbServer(conFactory, keyFactory);

        return new MathVerifier(conFactory, adminDbServer);
    }


    private User[] createUsersFromUserids(String[] scanUserids) throws Exception{
        User[] scanUsers;
        if(scanUserids == null){
            UserInfo[] allUserInfos = adminDbServer.getUserInfos();
            scanUsers = new User[allUserInfos.length];
            for(int i = 0; i < allUserInfos.length; i++){
                scanUsers[i] = new User(allUserInfos[i].userid, allUserInfos[i].id);
            }
        } else {
            scanUsers = new User[scanUserids.length];
            for(int i = 0; i < scanUserids.length; i++){
                scanUsers[i] = adminDbServer.getUser(scanUserids[i]);
            }
        }
        return scanUsers;
    }


    private void closeAllConnections(){
        if(this.conFactory != null){
            try {
                this.conFactory.close();
            } catch(Exception e){
                lg.error(e.getMessage(), e);
            }
        }

    }


    public void runMathTest(String[] scanUserids, KeyValue[] bioModelKeys, String softwareVersion, DatabaseMode databaseMode, Compare.CompareLogger compareLogger) throws Exception{
        Compare.logger = compareLogger;
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        User[] scanUsers = createUsersFromUserids(scanUserids);
        try {
            if(databaseMode == DatabaseMode.update){
                List<KeyValue> keyValues = new ArrayList<>();
                if(bioModelKeys != null) keyValues.addAll(Arrays.asList(bioModelKeys));
                MathVerifier.initLoadModelsStatTable(softwareVersion, (scanUserids == null ? null : scanUsers), keyValues.toArray(new KeyValue[0]), this.timeStamp, this.conFactory);
            }
            this.generateMath(scanUsers, databaseMode, bioModelKeys);
        } finally {
            closeAllConnections();
        }
    }


    public void runLoadTest(String[] scanUserids, KeyValue[] bioModelKeys, KeyValue[] mathModelKeys, String softwareVersion, DatabaseMode databaseMode, Compare.CompareLogger compareLogger) throws Exception{
        Compare.logger = compareLogger;
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        User[] scanUsers = createUsersFromUserids(scanUserids);
        try {
            if(databaseMode == DatabaseMode.create){
                List<KeyValue> keyValues = new ArrayList<>();
                if(bioModelKeys != null) keyValues.addAll(Arrays.asList(bioModelKeys));
                if(mathModelKeys != null) keyValues.addAll(Arrays.asList(mathModelKeys));
                MathVerifier.initLoadModelsStatTable(softwareVersion, (scanUserids == null ? null : scanUsers), keyValues.toArray(new KeyValue[0]), this.timeStamp, this.conFactory);
                return;
            }
            this.loadBioModels(scanUsers, databaseMode, bioModelKeys);
            this.loadMathModels(scanUsers, databaseMode, mathModelKeys);
        } finally {
            closeAllConnections();
        }
    }

    private static void initLoadModelsStatTable(String softwareVersion, User[] users, KeyValue[] bioAndMathModelKeys, Timestamp timestamp,
                                                ConnectionFactory conFactory) throws Exception{
        java.sql.Connection con = null;
        java.sql.Statement stmt = null;
        try {
            con = conFactory.getConnection(new Object());
            StringBuilder sql = new StringBuilder();

            //Make sure timestamp does not exist in table
            stmt = con.createStatement();
            sql.setLength(0);
            sql.append(
                    "SELECT COUNT(*)" +
                            " FROM " + LoadModelsStatTable.table.getTableName() +
                            " WHERE " + LoadModelsStatTable.table.timeStamp + " = " + "'" + timestamp.toString() + "'");
            ResultSet resultSet = stmt.executeQuery(sql.toString());
            if(resultSet.next()){
                if(resultSet.getInt(1) != 0){
                    throw new Exception("Timestamp " + timestamp + " already exists in table '" + LoadModelsStatTable.table.getTableName() + "'");
                }
            } else {
                throw new Exception("Timestamp query returned nothing");
            }
            resultSet.close();
            stmt.close();

            //Scan only users condition
            String onlyUsersClause = null;
            if(users != null && users.length > 0){
                StringBuilder usersSB = new StringBuilder();
                for(int i = 0; i < users.length; i++){
                    usersSB.append((i > 0 ? "," : "") + users[i].getID());
                }
                onlyUsersClause =
                        BioModelTable.table.ownerRef.getUnqualifiedColName() + " IN (" + usersSB.toString() + ")";
            }
            //Scan only model user conditions
            String onlyModelsClause = null;
            if(bioAndMathModelKeys != null && bioAndMathModelKeys.length > 0){
                StringBuffer modelsSB = new StringBuffer();
                for(int i = 0; i < bioAndMathModelKeys.length; i++){
                    modelsSB.append((i > 0 ? "," : "") + bioAndMathModelKeys[i].toString());
                }
                onlyModelsClause =
                        VersionTable.id_ColumnName + " IN (" + modelsSB.toString() + ")";
            }

            String allConditions = "";
            if(onlyModelsClause != null && onlyUsersClause != null){
                allConditions = " WHERE " + onlyUsersClause + " AND " + onlyModelsClause;
            } else if(onlyModelsClause != null){
                allConditions = " WHERE " + onlyModelsClause;
            } else if(onlyUsersClause != null){
                allConditions = " WHERE " + onlyUsersClause;
            }
            //Get ALL BioModel keys
            stmt = con.createStatement();
            sql.setLength(0);
            sql.append(
                    "SELECT " + BioModelTable.table.id.getUnqualifiedColName() +
                            " FROM " + BioModelTable.table.getTableName() + allConditions);
            Vector<BigDecimal> bioModelKeyV = new Vector<>();
            resultSet = stmt.executeQuery(sql.toString());
            while (resultSet.next()) {
                bioModelKeyV.add(resultSet.getBigDecimal(BioModelTable.table.id.getUnqualifiedColName()));
            }
            resultSet.close();
            stmt.close();

            //Get ALL MathModel keys
            stmt = con.createStatement();
            sql.setLength(0);
            sql.append(
                    "SELECT " + MathModelTable.table.id.getUnqualifiedColName() +
                            " FROM " + MathModelTable.table.getTableName() + allConditions);
            Vector<BigDecimal> mathModelKeyV = new Vector<BigDecimal>();
            resultSet = stmt.executeQuery(sql.toString());
            while (resultSet.next()) {
                mathModelKeyV.add(resultSet.getBigDecimal(MathModelTable.table.id.getUnqualifiedColName()));
            }
            resultSet.close();
            stmt.close();

            //Init row for each model
            int totalKeys = bioModelKeyV.size() + mathModelKeyV.size();
            for(int i = 0; i < totalKeys; i++){
                String bioModelKeyS = "NULL";
                String mathModelKeyS = "NULL";
                if(i < bioModelKeyV.size()){
                    bioModelKeyS = bioModelKeyV.elementAt(i).toString();
                } else {
                    mathModelKeyS = mathModelKeyV.elementAt(i - bioModelKeyV.size()).toString();
                }
                sql.setLength(0);
                sql.append(
                        "INSERT INTO " + LoadModelsStatTable.table.getTableName() + " " +
                                " VALUES (" +
                                conFactory.getKeyFactory().nextSEQ() + "," +
                                bioModelKeyS + "," +
                                mathModelKeyS + ",NULL,NULL," +
                                "'" + timestamp.toString() + "'" +
                                ",NULL," +
                                "'" + TokenMangler.getSQLEscapedString(softwareVersion, LoadModelsStatTable.SOFTWARE_VERS_SIZE) + "'" +
                                ",NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL)");
                DbDriver.updateCleanSQL(con, sql.toString());

            }
            con.commit();
        } catch(Exception e2){
            if(con != null){
                try {
                    con.rollback();
                } catch(Exception e){
                    lg.error(e.getMessage(), e);
                }
            }
            throw e2;
        } finally {
            if(stmt != null){
                try {
                    stmt.close();
                } catch(Exception e){
                    lg.error(e.getMessage(), e);
                }
            }
            if(con != null){
                try {
                    con.close();
                } catch(Exception e){
                    lg.error(e.getMessage(), e);
                }
            }
        }
    }

    private void updateLoadModelsStatTable_LoadTest(long loadTimeMilliSec, KeyValue BioOrMathModelKey, Exception exception){
        String sql =
                "UPDATE " + LoadModelsStatTable.table.getTableName() +
                        " SET " +
                        LoadModelsStatTable.table.resultFlag + " = " +
                        (exception == null ? LoadModelsStatTable.RESULTFLAG_SUCCESS : LoadModelsStatTable.RESULTFLAG_FAILURE) + "," +
                        LoadModelsStatTable.table.errorMessage + " = " +
                        (exception == null ? "NULL" : "'" + TokenMangler.getSQLEscapedString(exception.getClass().getName() + "::" + exception.getMessage(), LoadModelsStatTable.MAX_ERROR_MSG_SIZE) + "'") + "," +
                        LoadModelsStatTable.table.loadTime + " = " +
                        (exception == null ? loadTimeMilliSec : "NULL") +
                        " WHERE " +
                        "(" +
                        "(" + LoadModelsStatTable.table.bioModelRef + " IS NOT NULL" +
                        " AND " + LoadModelsStatTable.table.bioModelRef + " = " + BioOrMathModelKey.toString() + ")" +
                        " OR " +
                        "(" + LoadModelsStatTable.table.mathModelRef + " IS NOT NULL" +
                        " AND " + LoadModelsStatTable.table.mathModelRef + " = " + BioOrMathModelKey.toString() + ")" +
                        ") AND " + LoadModelsStatTable.table.timeStamp + " = '" + timeStamp.toString() + "'";
        try {
            Connection con = null;
            Object lock = new Object();
            try {
                con = conFactory.getConnection(lock);
                DbDriver.updateCleanSQL(con, sql);
                con.commit();
            } finally {
                if(con != null){
                    conFactory.release(con, lock);
                }
            }
        } catch(Exception e){
            lg.error(e.getMessage(), e);
        }
    }

    private void updateLoadModelsStatTable_CompareTest(KeyValue BioOrMathModelKey,
                                                       Long loadOriginalXMLTime, Long loadUnresolvedTime,
                                                       Boolean bSameCachedAndNotCachedXML, Boolean bSameCachedAndNotCachedObj, Boolean bSameSelfXMLCachedRoundtrip,
                                                       Exception bSameCachedAndNotCachedXMLExc, Exception bSameCachedAndNotCachedObjExc, Exception bSameSelfXMLCachedRoundtripExc){
        String sql =
                "UPDATE " + LoadModelsStatTable.table.getTableName() +
                        " SET " +
                        LoadModelsStatTable.table.bSameCachedAndNotCachedXML + " = " +
                        (bSameCachedAndNotCachedXML == null ? "NULL" : (bSameCachedAndNotCachedXML ? 1 : 0)) + "," +
                        LoadModelsStatTable.table.bSameCachedAndNotCachedXMLExc + " = " +
                        (bSameCachedAndNotCachedXMLExc == null ? "NULL" : "'" + TokenMangler.getSQLEscapedString(bSameCachedAndNotCachedXMLExc.getClass().getName() + "::" + bSameCachedAndNotCachedXMLExc.getMessage(), LoadModelsStatTable.MAX_ERROR_MSG_SIZE) + "'") + "," +

                        LoadModelsStatTable.table.bSameCachedAndNotCachedObj + " = " +
                        (bSameCachedAndNotCachedObj == null ? "NULL" : (bSameCachedAndNotCachedObj ? 1 : 0)) + "," +
                        LoadModelsStatTable.table.bSameCachedAndNotCachedObjExc + " = " +
                        (bSameCachedAndNotCachedObjExc == null ? "NULL" : "'" + TokenMangler.getSQLEscapedString(bSameCachedAndNotCachedObjExc.getClass().getName() + "::" + bSameCachedAndNotCachedObjExc.getMessage(), LoadModelsStatTable.MAX_ERROR_MSG_SIZE) + "'") + "," +

                        LoadModelsStatTable.table.bSameSelfXMLCachedRoundtrip + " = " +
                        (bSameSelfXMLCachedRoundtrip == null ? "NULL" : (bSameSelfXMLCachedRoundtrip ? 1 : 0)) + "," +
                        LoadModelsStatTable.table.bSameSelfXMLCachedRoundtripExc + " = " +
                        (bSameSelfXMLCachedRoundtripExc == null ? "NULL" : "'" + TokenMangler.getSQLEscapedString(bSameSelfXMLCachedRoundtripExc.getClass().getName() + "::" + bSameSelfXMLCachedRoundtripExc.getMessage(), LoadModelsStatTable.MAX_ERROR_MSG_SIZE) + "'") + "," +

                        LoadModelsStatTable.table.loadOriginalXMLTime + " = " +
                        (loadOriginalXMLTime != null ? loadOriginalXMLTime : "NULL") + "," +
                        LoadModelsStatTable.table.loadUnresolvedTime + " = " +
                        (loadUnresolvedTime != null ? loadUnresolvedTime : "NULL") +

                        " WHERE " +
                        "(" +
                        "(" + LoadModelsStatTable.table.bioModelRef + " IS NOT NULL" +
                        " AND " + LoadModelsStatTable.table.bioModelRef + " = " + BioOrMathModelKey.toString() + ")" +
                        " OR " +
                        "(" + LoadModelsStatTable.table.mathModelRef + " IS NOT NULL" +
                        " AND " + LoadModelsStatTable.table.mathModelRef + " = " + BioOrMathModelKey.toString() + ")" +
                        ") AND " + LoadModelsStatTable.table.timeStamp + " = '" + timeStamp.toString() + "'";
        try {
            Connection con = null;
            Object lock = new Object();
            try {
                con = conFactory.getConnection(lock);
                DbDriver.updateCleanSQL(con, sql);
                con.commit();
            } finally {
                if(con != null){
                    conFactory.release(con, lock);
                }
            }
        } catch(Exception e){
            lg.error(e.getMessage(), e);
        }
    }

    private void checkMathForBioModel(BigString bioModelXMLFromDB, BioModel bioModelFromDB, User user, DatabaseMode databaseMode) throws Exception{
        BioModel bioModelNewMath = XmlHelper.XMLToBioModel(new XMLSource(bioModelXMLFromDB.toString()));
        bioModelFromDB.refreshDependencies();
        bioModelNewMath.refreshDependencies();

        //
        // get all Simulations for this model
        //
        Simulation[] modelSimsFromDB = bioModelFromDB.getSimulations();

        //
        // for each application, recompute mathDescription, and verify it is equivalent
        // then check each associated simulation to ensure math overrides are applied in an equivalent manner also.
        //
        SimulationContext[] simContextsFromDB = bioModelFromDB.getSimulationContexts();
        SimulationContext[] simContextsNewMath = bioModelNewMath.getSimulationContexts();
        for(int k = 0; k < simContextsFromDB.length; k++){
            SimulationContext simContextFromDB = simContextsFromDB[k];
            Simulation[] appSimsFromDB = simContextFromDB.getSimulations();
            SimulationContext simContextNewMath = simContextsNewMath[k];
            Simulation[] appSimsNewMath = simContextNewMath.getSimulations();
            MathCompareResults mathCompareResults_latest = null;
            MathCompareResults mathCompareResults_4_8 = null;
            try {
                MathDescription origMathDesc = simContextFromDB.getMathDescription();
                //
                // find out if any simulation belonging to this Application has data
                //
                boolean bApplicationHasData = false;
                for(int l = 0; l < modelSimsFromDB.length; l++){
                    SimulationStatusPersistent simulationStatus = dbServerImpl.getSimulationStatus(modelSimsFromDB[l].getKey());
                    if(simulationStatus != null && simulationStatus.getHasData()){
                        bApplicationHasData = true;
                    }
                }

                //
                // make sure geometry is up to date on "simContextNewMath"
                //
                try {
                    if(simContextNewMath.getGeometry().getDimension() > 0 && simContextNewMath.getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
                        simContextNewMath.getGeometry().getGeometrySurfaceDescription().updateAll();
                    }
                } catch(Exception e){
                    lg.error(e.getMessage(), e);
                }

                //
                // updated mathdescription loaded into copy of biomodel, then test for equivalence.
                //
                cbit.vcell.mapping.MathMapping mathMapping = simContextNewMath.createNewMathMapping();
                MathDescription newMathDesc = mathMapping.getMathDescription();
                String issueString = null;
                org.vcell.util.Issue[] issues = mathMapping.getIssues();
                if(issues != null && issues.length > 0){
                    StringBuffer buffer = new StringBuffer("Issues(" + issues.length + "):");
                    for(int l = 0; l < issues.length; l++){
                        buffer.append(" <<" + issues[l].toString() + ">>");
                    }
                    issueString = buffer.toString();
                }

                mathCompareResults_latest = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), origMathDesc, newMathDesc);
                lg.info(">>>BioModel(" + bioModelFromDB.getVersion().getVersionKey() + ") '" + bioModelFromDB.getName() + "':" + bioModelFromDB.getVersion().getDate() + ", Application(" + simContextFromDB.getKey() + ") '" + simContextFromDB.getName() + "' <<EQUIV=" + mathCompareResults_latest.isEquivalent() + ">>: " + mathCompareResults_latest.toDatabaseStatus());
                MathDescription mathDesc_4_8 = null;
                try {
                    mathDesc_4_8 = new MathMapping_4_8(simContextNewMath).getMathDescription();
                    mathCompareResults_4_8 = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), origMathDesc, mathDesc_4_8);
                } catch(Exception e){
                    lg.error(e.getMessage(), e);
                    mathCompareResults_4_8 = new MathCompareResults(Decision.MathDifferent_FAILURE_UNKNOWN, e.getMessage());
                }

                simContextNewMath.setMathDescription(newMathDesc);
                //
                // update Database Status for SimContext
                //
                if(databaseMode == DatabaseMode.update){
                    java.sql.Connection con = null;
                    java.sql.Statement stmt = null;
                    try {
                        con = conFactory.getConnection(new Object());
                        stmt = con.createStatement();
                        //KeyValue mathKey = origMathDesc.getKey();
                        String UPDATESTATUS = "UPDATE " + SimContextStat2Table.table.getTableName() +
                                " SET " + SimContextStat2Table.table.hasData.getUnqualifiedColName() + " = " + ((bApplicationHasData) ? (1) : (0)) + ", " +
                                SimContextStat2Table.table.equiv.getUnqualifiedColName() + " = " + (mathCompareResults_latest.isEquivalent() ? (1) : (0)) + ", " +
                                SimContextStat2Table.table.status.getUnqualifiedColName() + " = '" + org.vcell.util.TokenMangler.getSQLEscapedString(mathCompareResults_latest.toDatabaseStatus(), 255) + "', " +
                                SimContextStat2Table.table.equiv_4_8.getUnqualifiedColName() + " = " + (mathCompareResults_4_8.isEquivalent() ? (1) : (0)) + ", " +
                                SimContextStat2Table.table.status_4_8.getUnqualifiedColName() + " = '" + org.vcell.util.TokenMangler.getSQLEscapedString(mathCompareResults_4_8.toDatabaseStatus(), 255) + "'" +
                                // ((issueString!=null)?(", "+SimContextStat2Table.table.comments.getUnqualifiedColName()+" = '"+org.vcell.util.TokenMangler.getSQLEscapedString(issueString,255)+"'"):(""))+
                                " WHERE " + SimContextStat2Table.table.simContextRef.getUnqualifiedColName() + " = " + simContextFromDB.getKey();
                        if(lg.isDebugEnabled()){
                            lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                        }
                        int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                        if(numRowsChanged > 1){
                            lg.warn("failed to update status for simcontext: " + simContextFromDB.getKey());
                        } else if(numRowsChanged == 0){
                            /**
                             * KeyValue key,
                             * KeyValue simContextKey,
                             * Boolean hasData,
                             * Boolean equiv,
                             * String status,
                             * Boolean curatorEquiv,
                             * String comments,
                             * Boolean equiv_4_8,
                             * String status_4_8
                             */

                            String INSERTSTATUS = "INSERT INTO " + SimContextStat2Table.table.getTableName() +
                                    " VALUES " + SimContextStat2Table.getSQLValueList(
                                    conFactory.getKeyFactory().getNewKey(con),
                                    simContextFromDB.getKey(),
                                    bApplicationHasData,
                                    mathCompareResults_latest.isEquivalent(),
                                    mathCompareResults_latest.toDatabaseStatus(),
                                    null,
                                    null,
                                    mathCompareResults_4_8.isEquivalent(),
                                    mathCompareResults_4_8.toDatabaseStatus());
                            lg.info(INSERTSTATUS);
                            if(lg.isDebugEnabled()){
                                lg.debug("executeUpdate() SQL: '" + INSERTSTATUS + "'", new DbDriver.StackTraceGenerationException());
                            }
                            int numRowsInserted = stmt.executeUpdate(INSERTSTATUS); // jcs: added logging
                            if(numRowsInserted != 1){
                                lg.warn("failed to insert status for simcontext: " + simContextFromDB.getKey());
                            }
                        }
                        con.commit();
                        if(lg.isTraceEnabled())
                            lg.trace("-------------- Update=true, saved 'newMath' for Application '" + simContextFromDB.getName() + "'");
                    } catch(SQLException e){
                        lg.error(e.getMessage(), e);
                        if(lg.isWarnEnabled())
                            lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE MATH for Application '" + simContextFromDB.getName() + "'");
                    } finally {
                        if(stmt != null){
                            stmt.close();
                        }
                        con.close();
                    }
                }
            } catch(Throwable e){
                lg.error(e.getMessage(), e); // exception in SimContext
                if(databaseMode == DatabaseMode.update){
                    java.sql.Connection con = null;
                    java.sql.Statement stmt = null;
                    try {
                        con = conFactory.getConnection(new Object());
                        stmt = con.createStatement();
                        //KeyValue mathKey = origMathDesc.getKey();
                        String UPDATESTATUS = "UPDATE " + SimContextStat2Table.table.getTableName() +
                                " SET " + SimContextStat2Table.table.status.getUnqualifiedColName() + " = 'EXCEPTION: " + org.vcell.util.TokenMangler.getSQLEscapedString(e.toString()) + "'" +
                                " WHERE " + SimContextStat2Table.table.simContextRef.getUnqualifiedColName() + " = " + simContextFromDB.getKey();
                        if(lg.isDebugEnabled()){
                            lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                        }
                        int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                        if(numRowsChanged != 1){
                            lg.info("failed to update status with exception");
                        }
                        con.commit();
                        if(lg.isTraceEnabled())
                            lg.trace("-------------- Update=true, saved exception for Application '" + simContextFromDB.getName() + "'");
                    } catch(SQLException e2){
                        lg.error(e2.getMessage(), e2);
                        if(lg.isWarnEnabled())
                            lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for Application '" + simContextFromDB.getName() + "'");
                    } finally {
                        if(stmt != null){
                            stmt.close();
                        }
                        con.close();
                    }
                }
            }
            //
            // now, verify each associated simulation will apply overrides in an equivalent way
            //
            for(int l = 0; l < appSimsFromDB.length; l++){
                try {
                    boolean bSimEquivalent = Simulation.testEquivalency(appSimsNewMath[l], appSimsFromDB[l], mathCompareResults_latest);
                    if(lg.isTraceEnabled())
                        lg.trace("Application(" + simContextFromDB.getKey() + ") '" + simContextFromDB.getName() + "', " +
                                "Simulation(" + modelSimsFromDB[l].getKey() + ") '" + modelSimsFromDB[l].getName() + "':" + modelSimsFromDB[l].getVersion().getDate() +
                                "mathEquivalency=" + mathCompareResults_latest.isEquivalent() + ", simEquivalency=" + bSimEquivalent);
                    //
                    // update Database Status for Simulation
                    //
                    if(databaseMode == DatabaseMode.update){
                        java.sql.Connection con = null;
                        java.sql.Statement stmt = null;
                        try {
                            con = conFactory.getConnection(new Object());
                            stmt = con.createStatement();
                            String UPDATESTATUS = "UPDATE " + SimStatTable.table.getTableName() +
                                    " SET " + SimStatTable.table.equiv.getUnqualifiedColName() + " = " + ((bSimEquivalent) ? (1) : (0)) + ", " +
                                    SimStatTable.table.status.getUnqualifiedColName() + " = '" + org.vcell.util.TokenMangler.getSQLEscapedString(mathCompareResults_latest.decision.description) + "'" +
                                    " WHERE " + SimStatTable.table.simRef.getUnqualifiedColName() + " = " + appSimsFromDB[l].getKey();
                            if(lg.isDebugEnabled()){
                                lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                            }
                            int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                            if(numRowsChanged != 1){
                                lg.info("failed to update status");
                            }
                            con.commit();
                            if(lg.isTraceEnabled())
                                lg.trace("-------------- Update=true, saved 'simulation status for simulation '" + appSimsFromDB[l].getName() + "'");
                        } catch(SQLException e){
                            lg.error(e.getMessage(), e);
                            if(lg.isWarnEnabled())
                                lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE status for simulation '" + appSimsFromDB[l].getName() + "'");
                        } finally {
                            if(stmt != null){
                                stmt.close();
                            }
                            con.close();
                        }
                    }
                } catch(Throwable e){
                    lg.error(e.getMessage(), e); // exception in SimContext
                    if(databaseMode == DatabaseMode.update){
                        java.sql.Connection con = null;
                        java.sql.Statement stmt = null;
                        try {
                            con = conFactory.getConnection(new Object());
                            stmt = con.createStatement();
                            //KeyValue mathKey = origMathDesc.getKey();
                            String UPDATESTATUS = "UPDATE " + SimStatTable.table.getTableName() +
                                    " SET " + SimStatTable.table.status.getUnqualifiedColName() + " = 'EXCEPTION: " + org.vcell.util.TokenMangler.getSQLEscapedString(e.toString()) + "'" +
                                    " WHERE " + SimStatTable.table.simRef.getUnqualifiedColName() + " = " + appSimsFromDB[l].getKey();
                            if(lg.isDebugEnabled()){
                                lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                            }
                            int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                            if(numRowsChanged != 1){
                                lg.info("failed to update status with exception");
                            }
                            con.commit();
                            if(lg.isTraceEnabled())
                                lg.trace("-------------- Update=true, saved exception for Simulation '" + appSimsFromDB[l].getName() + "'");
                        } catch(SQLException e2){
                            lg.error(e2.getMessage(), e2);
                            if(lg.isWarnEnabled())
                                lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for simulation '" + appSimsFromDB[l].getName() + "'");
                        } finally {
                            if(stmt != null){
                                stmt.close();
                            }
                            con.close();
                        }
                    }
                }
            }
        }
    }

    private Comparator<KeyValue> keyValueCpmparator =
            new Comparator<KeyValue>() {
                public int compare(KeyValue o1, KeyValue o2){
                    return Integer.parseInt(o1.toString()) - Integer.parseInt(o2.toString());
                }
            };

    public static class MathGenerationResults {
        public final BioModel bioModelFromDB;
        public final SimulationContext simContextFromDB;
        public final MathDescription mathDesc_original;
        public final MathDescription mathDesc_latest;
        public final MathCompareResults mathCompareResults_latest;
        public final MathDescription mathDesc_4_8;
        public final MathCompareResults mathCompareResults_4_8;

        public MathGenerationResults(BioModel bioModelFromDB, SimulationContext simContextFromDB, MathDescription math_orig, MathDescription math_latest, MathCompareResults mathCompareResults_latest, MathDescription math_4_8, MathCompareResults mathCompareResults_4_8){
            this.bioModelFromDB = bioModelFromDB;
            this.simContextFromDB = simContextFromDB;
            this.mathDesc_original = math_orig;
            this.mathDesc_latest = math_latest;
            this.mathCompareResults_latest = mathCompareResults_latest;
            this.mathDesc_4_8 = math_4_8;
            this.mathCompareResults_4_8 = mathCompareResults_4_8;
        }
    }

    public MathGenerationResults testMathGeneration(KeyValue simContextKey) throws SQLException, DataAccessException, XmlParseException, MappingException, MathException, MatrixException, ExpressionException, ModelException, PropertyVetoException{

        User adminUser = new User(PropertyLoader.ADMINISTRATOR_ACCOUNT, new org.vcell.util.document.KeyValue(PropertyLoader.ADMINISTRATOR_ID));

        if(lg.isTraceEnabled()) lg.trace("Testing SimContext with key '" + simContextKey + "'");
        // get biomodel refs
        java.sql.Connection con = null;
        java.sql.Statement stmt = null;
        con = conFactory.getConnection(new Object());
        cbit.vcell.modeldb.BioModelSimContextLinkTable bmscTable = cbit.vcell.modeldb.BioModelSimContextLinkTable.table;
        cbit.vcell.modeldb.BioModelTable bmTable = cbit.vcell.modeldb.BioModelTable.table;
        cbit.vcell.modeldb.UserTable userTable = cbit.vcell.modeldb.UserTable.table;
        String sql = "SELECT " + bmscTable.bioModelRef.getQualifiedColName() + "," + bmTable.ownerRef.getQualifiedColName() + "," + userTable.userid.getQualifiedColName() +
                " FROM " + bmscTable.getTableName() + "," + bmTable.getTableName() + "," + userTable.getTableName() +
                " WHERE " + bmscTable.simContextRef.getQualifiedColName() + " = " + simContextKey +
                " AND " + bmTable.id.getQualifiedColName() + " = " + bmscTable.bioModelRef.getQualifiedColName() +
                " AND " + bmTable.ownerRef.getQualifiedColName() + " = " + userTable.id.getQualifiedColName();
        ArrayList<KeyValue> bioModelKeys = new ArrayList<>();
        stmt = con.createStatement();
        User owner = null;
        try {
            ResultSet rset = stmt.executeQuery(sql);
            while (rset.next()) {
                KeyValue key = new KeyValue(rset.getBigDecimal(bmscTable.bioModelRef.getUnqualifiedColName()));
                bioModelKeys.add(key);
                KeyValue ownerRef = new KeyValue(rset.getBigDecimal(bmTable.ownerRef.getUnqualifiedColName()));
                String userid = rset.getString(userTable.userid.getUnqualifiedColName());
                owner = new User(userid, ownerRef);
            }
        } finally {
            if(stmt != null){
                stmt.close();
            }
            con.close();
        }

        // use the first biomodel...
        if(bioModelKeys.size() == 0){
            throw new RuntimeException("zombie simContext ... no biomodels");
        }
        BioModelInfo bioModelInfo = dbServerImpl.getBioModelInfo(owner, bioModelKeys.get(0));
        //
        // read in the BioModel from the database
        //
        BigString bioModelXML = dbServerImpl.getBioModelXML(owner, bioModelInfo.getVersion().getVersionKey());
        BioModel bioModelFromDB = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
        BioModel bioModelNewMath = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
        bioModelFromDB.refreshDependencies();
        bioModelNewMath.refreshDependencies();

        //
        // get all Simulations for this model
        //
        Simulation modelSimsFromDB[] = bioModelFromDB.getSimulations();

        //
        // ---> only for the SimContext we started with...
        // recompute mathDescription, and verify it is equivalent
        // then check each associated simulation to ensure math overrides are applied in an equivalent manner also.
        //
        SimulationContext[] simContextsFromDB = bioModelFromDB.getSimulationContexts();
        SimulationContext[] simContextsNewMath = bioModelNewMath.getSimulationContexts();
        SimulationContext simContextFromDB = null;
        SimulationContext simContextNewMath = null;
        for(int k = 0; k < simContextsFromDB.length; k++){
            // find it...
            if(simContextsFromDB[k].getKey().equals(simContextKey)){
                simContextFromDB = simContextsFromDB[k];
                simContextNewMath = simContextsNewMath[k];
                break;
            }
        }

        if(simContextFromDB == null){
            throw new RuntimeException("BioModel referred to by this SimContext does not contain this SimContext");
        } else {
            MathDescription origMathDesc = simContextFromDB.getMathDescription();
            //
            // find out if any simulation belonging to this Application has data
            //
            //
            // make sure geometry is up to date on "simContextNewMath"
            //
            try {
                if(simContextNewMath.getGeometry().getDimension() > 0 && simContextNewMath.getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
                    simContextNewMath.getGeometry().getGeometrySurfaceDescription().updateAll();
                }
            } catch(Exception e){
                lg.error(e.getMessage(), e);
            }

            //
            // updated mathDescription loaded into copy of bioModel, then test for equivalence.
            //
            cbit.vcell.mapping.MathMapping mathMapping = simContextNewMath.createNewMathMapping();
            MathDescription mathDesc_latest = mathMapping.getMathDescription();
            MathMapping_4_8 mathMapping_4_8 = new MathMapping_4_8(simContextNewMath);
            MathDescription mathDesc_4_8 = mathMapping_4_8.getMathDescription();
            String issueString = null;
            org.vcell.util.Issue issues[] = mathMapping.getIssues();
            if(issues != null && issues.length > 0){
                StringBuffer buffer = new StringBuffer("Issues(" + issues.length + "):\n");
                for(int l = 0; l < issues.length; l++){
                    buffer.append(" <<" + issues[l].toString() + ">>\n");
                }
                issueString = buffer.toString();
            }
            simContextNewMath.setMathDescription(mathDesc_latest);

            MathCompareResults mathCompareResults_latest = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), origMathDesc, mathDesc_latest);
            MathCompareResults mathCompareResults_4_8 = null;
            try {
                mathCompareResults_4_8 = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), origMathDesc, mathDesc_4_8);
            } catch(Exception e){
                lg.error(e.getMessage(), e);
                mathCompareResults_4_8 = new MathCompareResults(Decision.MathDifferent_FAILURE_UNKNOWN, e.getMessage());
            }
            return new MathGenerationResults(bioModelFromDB, simContextFromDB, origMathDesc, mathDesc_latest, mathCompareResults_latest, mathDesc_4_8, mathCompareResults_4_8);
        }
    }

    private void generateMath(User users[], DatabaseMode databaseMode, KeyValue[] bioModelKeys) throws DataAccessException{

        KeyValue[] sortedBioModelKeys = null;
        if(bioModelKeys != null){
            sortedBioModelKeys = bioModelKeys.clone();
            Arrays.sort(sortedBioModelKeys, keyValueCpmparator);
        }
        for(User user : users){
            BioModelInfo[] bioModelInfos = dbServerImpl.getBioModelInfos(user, false);
            if(lg.isTraceEnabled()) lg.trace("Testing user '" + user + "'");

            //
            // for each bioModel, load BioModelMetaData (to get list of keys for simulations and simContexts
            //
            for(BioModelInfo bioModelInfo : bioModelInfos){
                //
                // if certain Bio or Math models are requested, then filter all else out
                //
                KeyValue versionKey = bioModelInfo.getVersion().getVersionKey();
                if(sortedBioModelKeys != null && sortedBioModelKeys.length > 0){
                    int srch = Arrays.binarySearch(sortedBioModelKeys, versionKey, keyValueCpmparator);
                    if(srch < 0){
                        continue;
                    }
                }

                try {
                    //
                    // read in the BioModel from the database
                    //
                    BioModel bioModelFromDBCache = null;
                    BigString bioModelXMLFromDBCache = null;
                    try {
                        long startTime = System.currentTimeMillis();
                        bioModelXMLFromDBCache =
                                new BigString(dbServerImpl.getServerDocumentManager().getBioModelXML(
                                        new QueryHashtable(), user, versionKey, false));
                        bioModelFromDBCache = XmlHelper.XMLToBioModel(new XMLSource(bioModelXMLFromDBCache.toString()));
                        if(databaseMode == DatabaseMode.update){
                            updateLoadModelsStatTable_LoadTest(System.currentTimeMillis() - startTime,
                                    versionKey, null);
                        }
                    } catch(Exception e){
                        lg.error(e.getMessage(), e);
                        if(databaseMode == DatabaseMode.update){
                            updateLoadModelsStatTable_LoadTest(0, versionKey, e);
                        }
                    }

                    checkMathForBioModel(bioModelXMLFromDBCache, bioModelFromDBCache, user, databaseMode);

                } catch(Throwable e){
                    lg.error(e.getMessage(), e); // exception in whole BioModel
                    // can't update anything in database, since we don't know what simcontexts are involved
                }
            }
        }
    }

    private void loadBioModels(User[] users, DatabaseMode databaseMode, KeyValue[] bioModelKeys) throws DataAccessException{

        KeyValue[] sortedBioModelKeys = null;
        if(bioModelKeys != null){
            sortedBioModelKeys = bioModelKeys.clone();
            Arrays.sort(sortedBioModelKeys, keyValueCpmparator);
        }
        for(User user : users){
            BioModelInfo[] bioModelInfos = dbServerImpl.getBioModelInfos(user, false);
            if(lg.isTraceEnabled()) lg.trace("Testing user '" + user + "'");

            //
            // for each bioModel, load BioModelMetaData (to get list of keys for simulations and simContexts
            //
            for(BioModelInfo bioModelInfo : bioModelInfos){
                //
                // if certain Bio or Math models are requested, then filter all else out
                //
                KeyValue versionKey = bioModelInfo.getVersion().getVersionKey();
                if(sortedBioModelKeys != null && sortedBioModelKeys.length > 0){
                    int srch = Arrays.binarySearch(sortedBioModelKeys, versionKey, keyValueCpmparator);
                    if(srch < 0){
                        continue;
                    }
                }

                try {
                    //
                    // read in the BioModel from the database
                    //
                    BioModel bioModelFromDBCache = null;
                    BigString bioModelXMLFromDBCache = null;
                    try {
                        long startTime = System.currentTimeMillis();
                        bioModelXMLFromDBCache =
                                new BigString(dbServerImpl.getServerDocumentManager().getBioModelXML(
                                        new QueryHashtable(), user, versionKey, false));
                        bioModelFromDBCache = XmlHelper.XMLToBioModel(new XMLSource(bioModelXMLFromDBCache.toString()));
                        if(databaseMode == DatabaseMode.update){
                            updateLoadModelsStatTable_LoadTest(System.currentTimeMillis() - startTime,
                                    versionKey, null);
                        }
                    } catch(Exception e){
                        lg.error(e.getMessage(), e);
                        if(databaseMode == DatabaseMode.update){
                            updateLoadModelsStatTable_LoadTest(0, versionKey, e);
                        }
                    }

                    if(bioModelXMLFromDBCache != null){
                        testDocumentLoad(databaseMode, user, bioModelInfo, bioModelFromDBCache);
                    }

                } catch(Throwable e){
                    lg.error(e.getMessage(), e); // exception in whole BioModel
                    // can't update anything in database, since we don't know what simcontexts are involved
                }
            }
        }
    }

    private void loadMathModels(User[] users, DatabaseMode databaseMode, KeyValue[] mathModelKeys) throws DataAccessException{

        KeyValue[] sortedMathModelKeys = null;
        if(mathModelKeys != null){
            sortedMathModelKeys = mathModelKeys.clone();
            Arrays.sort(sortedMathModelKeys, keyValueCpmparator);
        }
        for(User user : users){
            MathModelInfo[] mathModelInfos = dbServerImpl.getMathModelInfos(user, false);
            if(lg.isTraceEnabled()) lg.trace("Testing user '" + user + "'");

            //
            // for each bioModel, load BioModelMetaData (to get list of keys for simulations and simContexts
            //
            for(MathModelInfo mathModelInfo : mathModelInfos){
                //
                // if certain Bio or Math models are requested, then filter all else out
                //
                KeyValue versionKey = mathModelInfo.getVersion().getVersionKey();
                if(sortedMathModelKeys != null && sortedMathModelKeys.length > 0
                        && Arrays.binarySearch(sortedMathModelKeys, versionKey, keyValueCpmparator) < 0){
                    continue;
                }

                try {
                    //
                    // read in the BioModel and MathModel from the database
                    //
                    MathModel mathModelFromDBCache = null;
                    BigString mathModelXMLFromDBCache = null;
                    try {
                        long startTime = System.currentTimeMillis();
                        mathModelXMLFromDBCache =
                                new BigString(dbServerImpl.getServerDocumentManager().getMathModelXML(
                                        new QueryHashtable(), user, versionKey, false));
                        mathModelFromDBCache = XmlHelper.XMLToMathModel(new XMLSource(mathModelXMLFromDBCache.toString()));
                        if(databaseMode == DatabaseMode.update){
                            updateLoadModelsStatTable_LoadTest(System.currentTimeMillis() - startTime,
                                    versionKey, null);
                        }
                    } catch(Exception e){
                        lg.error(e.getMessage(), e);
                        if(databaseMode == DatabaseMode.update){
                            updateLoadModelsStatTable_LoadTest(0, versionKey, e);
                        }
                    }

                    //
                    // OPERATION = LOADTEST (there is no math generation for math models)
                    //
                    if(mathModelXMLFromDBCache != null){
                        testDocumentLoad(databaseMode, user, mathModelInfo, mathModelFromDBCache);
                    }

                } catch(Throwable e){
                    lg.error(e.getMessage(), e); // exception in whole BioModel
                    // can't update anything in database, since we don't know what simcontexts are involved
                }
            }
        }
    }

    private void testDocumentLoad(DatabaseMode databaseMode, User user, VCDocumentInfo documentInfo, VCDocument vcDocumentFromDBCache){
        KeyValue versionKey = documentInfo.getVersion().getVersionKey();
        //	try{
        //		String vcDocumentXMLFromDBCacheRoundtrip = null;

        //Compare self same
        Exception bSameCachedAndNotCachedXMLExc = null;
        Exception bSameCachedAndNotCachedObjExc = null;
        Exception bSameSelfXMLCachedRoundtripExc = null;
        Boolean bSameCachedAndNotCachedXML = null;
        Boolean bSameCachedAndNotCachedObj = null;
        Boolean bSameSelfCachedRoundtrip = null;
        //	Boolean bSameSelfObjCachedRoundTrip = null;
        long startTime = 0;
        // Long compareXMLTime = null;
        // Long compareObjTime = null;
        // Long loadOriginalXMLTime = null;
        Long loadUnresolvedTime = null;

        if(documentInfo instanceof BioModelInfo){
            try {
                String xmlRndTrip0 = XmlHelper.bioModelToXML((BioModel) vcDocumentFromDBCache);
                BioModel bioModelRndTrip0 = XmlHelper.XMLToBioModel(new XMLSource(xmlRndTrip0));
                String xmlRndTrip1 = XmlHelper.bioModelToXML((BioModel) bioModelRndTrip0);
                BioModel bioModelRndTrip1 = XmlHelper.XMLToBioModel(new XMLSource(xmlRndTrip1));
                if(Compare.logger != null){
                    Compare.loggingEnabled = true;
                }
                bSameSelfCachedRoundtrip = VCMLComparator.compareEquals(xmlRndTrip0, xmlRndTrip1, true);
                lg.info("----------XML same=" + bSameSelfCachedRoundtrip);
                boolean objectSame = bioModelRndTrip0.compareEqual(bioModelRndTrip1);
                lg.info("----------Objects same=" + objectSame);
                bSameSelfCachedRoundtrip = bSameSelfCachedRoundtrip && objectSame;
            } catch(Exception e){
                bSameSelfCachedRoundtrip = null;
                lg.error(e.getMessage(), e);
                bSameSelfXMLCachedRoundtripExc = e;
            } finally {
                Compare.loggingEnabled = false;
            }

            String fromDBBioModelUnresolvedXML = null;
            try {
                startTime = System.currentTimeMillis();
                fromDBBioModelUnresolvedXML =
                        dbServerImpl.getServerDocumentManager().getBioModelUnresolved(
                                new QueryHashtable(), user, versionKey);
                BioModel vcDocumentFromDBNotCached = XmlHelper.XMLToBioModel(new XMLSource(fromDBBioModelUnresolvedXML));
                loadUnresolvedTime = System.currentTimeMillis() - startTime;
                bSameCachedAndNotCachedObj = vcDocumentFromDBCache.compareEqual(vcDocumentFromDBNotCached);
            } catch(Exception e){
                lg.error(e.getMessage(), e);
                bSameCachedAndNotCachedObjExc = e;
            }

            if(fromDBBioModelUnresolvedXML != null){
                try {
                    String vcDocumentXMLFromDBCacheRegenerate =
                            XmlHelper.bioModelToXML((BioModel) vcDocumentFromDBCache);
                    bSameCachedAndNotCachedXML = VCMLComparator.compareEquals(vcDocumentXMLFromDBCacheRegenerate, fromDBBioModelUnresolvedXML, true);
                } catch(Exception e){
                    lg.error(e.getMessage(), e);
                    bSameCachedAndNotCachedXMLExc = e;
                }
            }
        } else {
            try {
                String xmlRndTrip0 = XmlHelper.mathModelToXML((MathModel) vcDocumentFromDBCache);
                MathModel mathModelRndTrip0 = XmlHelper.XMLToMathModel(new XMLSource(xmlRndTrip0));
                String xmlRndTrip1 = XmlHelper.mathModelToXML((MathModel) mathModelRndTrip0);
                MathModel mathModelRndTrip1 = XmlHelper.XMLToMathModel(new XMLSource(xmlRndTrip1));
                if(Compare.logger != null){
                    Compare.loggingEnabled = true;
                }
                bSameSelfCachedRoundtrip = VCMLComparator.compareEquals(xmlRndTrip0, xmlRndTrip1, true);
                bSameSelfCachedRoundtrip = bSameSelfCachedRoundtrip && mathModelRndTrip0.compareEqual(mathModelRndTrip1);
            } catch(Exception e){
                bSameSelfCachedRoundtrip = null;
                lg.error(e.getMessage(), e);
                bSameSelfXMLCachedRoundtripExc = e;
            } finally {
                Compare.loggingEnabled = false;
            }

            String fromDBMathModelUnresolvedXML = null;
            try {
                startTime = System.currentTimeMillis();
                MathModel vcDocumentFromDBNotCached =
                        dbServerImpl.getServerDocumentManager().getMathModelUnresolved(
                                new QueryHashtable(), user, versionKey);
                loadUnresolvedTime = System.currentTimeMillis() - startTime;
                fromDBMathModelUnresolvedXML = XmlHelper.mathModelToXML(vcDocumentFromDBNotCached);
                bSameCachedAndNotCachedObj = vcDocumentFromDBCache.compareEqual(vcDocumentFromDBNotCached);
            } catch(Exception e){
                lg.error(e.getMessage(), e);
                bSameCachedAndNotCachedObjExc = e;
            }

            if(fromDBMathModelUnresolvedXML != null){
                try {
                    String vcDocumentXMLFromDBCacheRegenerate =
                            XmlHelper.mathModelToXML((MathModel) vcDocumentFromDBCache);
                    bSameCachedAndNotCachedXML = VCMLComparator.compareEquals(vcDocumentXMLFromDBCacheRegenerate, fromDBMathModelUnresolvedXML, true);
                } catch(Exception e){
                    lg.error(e.getMessage(), e);
                    bSameCachedAndNotCachedXMLExc = e;
                }
            }
        }

        if(databaseMode == DatabaseMode.update){
            updateLoadModelsStatTable_CompareTest(versionKey,
                    null/*loadOriginalXMLTime*/, loadUnresolvedTime,
                    bSameCachedAndNotCachedXML, bSameCachedAndNotCachedObj, bSameSelfCachedRoundtrip,
                    bSameCachedAndNotCachedXMLExc, bSameCachedAndNotCachedObjExc, bSameSelfXMLCachedRoundtripExc);
        } else {
            lg.info("loadOriginalXMLTime=" + null/*loadOriginalXMLTime*/ + " loadUnresolvedTime=" + loadUnresolvedTime);
            lg.info("bSameCachedAndNotCachedXML=" + bSameCachedAndNotCachedXML +
                    " bSameCachedAndNotCachedObj=" + bSameCachedAndNotCachedObj +
                    " bSameSelfXMLCachedRoundtrip=" + bSameSelfCachedRoundtrip);
            lg.info("bSameCachedAndNotCachedXMLExc=" + bSameCachedAndNotCachedXMLExc +
                    "\nbSameCachedAndNotCachedObjExc=" + bSameCachedAndNotCachedObjExc +
                    "\nbSameSelfXMLCachedRoundtripExc=" + bSameSelfXMLCachedRoundtripExc);
        }
    }


    @Deprecated
    private void scanBioModels_not_used(boolean bUpdateDatabase, KeyValue[] bioModelKeys) throws DataAccessException{
        java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
//	calendar.set(2002,java.util.Calendar.MAY,7+1);
        calendar.set(2002, java.util.Calendar.JULY, 1);
        final java.util.Date fluxCorrectionOrDisablingBugFixDate = calendar.getTime();
//	calendar.set(2001,java.util.Calendar.JUNE,13+1);
        calendar.set(2002, java.util.Calendar.JANUARY, 1);
        final java.util.Date totalVolumeCorrectionFixDate = calendar.getTime();


        User user = new User(PropertyLoader.ADMINISTRATOR_ACCOUNT, new KeyValue(PropertyLoader.ADMINISTRATOR_ID));
        for(KeyValue bioModelKey : bioModelKeys){
            BioModelInfo bioModelInfo = dbServerImpl.getBioModelInfo(user, bioModelKey);
            if(lg.isTraceEnabled()) lg.trace("Testing bioModel with key '" + bioModelKey + "'");
            Connection con;
            java.sql.Statement stmt;

            try {
                //
                // read in the BioModel from the database
                //
                BigString bioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfo.getVersion().getVersionKey());
                BioModel bioModelFromDB = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
                BioModel bioModelNewMath = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
                bioModelFromDB.refreshDependencies();
                bioModelNewMath.refreshDependencies();

                //
                // get all Simulations for this model
                //
                Simulation[] modelSimsFromDB = bioModelFromDB.getSimulations();

                //
                // for each application, recompute mathDescription, and verify it is equivalent
                // then check each associated simulation to ensure math overrides are applied in an equivalent manner also.
                //
                SimulationContext[] simContextsFromDB = bioModelFromDB.getSimulationContexts();
                SimulationContext[] simContextsNewMath = bioModelNewMath.getSimulationContexts();
                for(int k = 0; k < simContextsFromDB.length; k++){
                    SimulationContext simContextFromDB = simContextsFromDB[k];
                    Simulation[] appSimsFromDB = simContextFromDB.getSimulations();
                    SimulationContext simContextNewMath = simContextsNewMath[k];
                    Simulation[] appSimsNewMath = simContextNewMath.getSimulations();
                    MathCompareResults mathCompareResults = null;
                    try {
                        MathDescription origMathDesc = simContextFromDB.getMathDescription();
                        //
                        // find out if any simulation belonging to this Application has data
                        //
                        boolean bApplicationHasData = false;
                        for(Simulation simulation : modelSimsFromDB){
                            SimulationStatusPersistent simulationStatus = dbServerImpl.getSimulationStatus(simulation.getKey());
                            if(simulationStatus != null && simulationStatus.getHasData()){
                                bApplicationHasData = true;
                            }
                        }
                        //
                        // bug compatability mode off
                        //
//					cbit.vcell.mapping.MembraneStructureAnalyzer.bResolvedFluxCorrectionBug = false;
//					cbit.vcell.mapping.MembraneMapping.bFluxCorrectionBugMode = false;
//					cbit.vcell.mapping.FeatureMapping.bTotalVolumeCorrectionBug = false;
//					cbit.vcell.mapping.MembraneStructureAnalyzer.bNoFluxIfFixed = false;

                        //
                        // make sure geometry is up to date on "simContextNewMath"
                        //
                        try {
                            if(simContextNewMath.getGeometry().getDimension() > 0 && simContextNewMath.getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
                                simContextNewMath.getGeometry().getGeometrySurfaceDescription().updateAll();
                            }
                        } catch(Exception e){
                            lg.error(e.getMessage(), e);
                        }

                        //
                        // updated mathdescription loaded into copy of biomodel, then test for equivalence.
                        //
                        cbit.vcell.mapping.MathMapping mathMapping = simContextNewMath.createNewMathMapping();
                        MathDescription newMathDesc = mathMapping.getMathDescription();
                        String issueString = null;
                        org.vcell.util.Issue[] issues = mathMapping.getIssues();
                        if(issues != null && issues.length > 0){
                            StringBuffer buffer = new StringBuffer("Issues(" + issues.length + "):");
                            for(org.vcell.util.Issue issue : issues){
                                buffer.append(" <<" + issue.toString() + ">>");
                            }
                            issueString = buffer.toString();
                        }
                        simContextNewMath.setMathDescription(newMathDesc);

                        mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), origMathDesc, newMathDesc);
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(">>>BioModel(" + bioModelFromDB.getVersion().getVersionKey() + ") '" + bioModelFromDB.getName() + "':" + bioModelFromDB.getVersion().getDate() + ", Application(" + simContextFromDB.getKey() + ") '" + simContextFromDB.getName() + "' <<EQUIV=" + mathCompareResults.isEquivalent() + ">>: " + mathCompareResults.toDatabaseStatus());
                        //
                        // update Database Status for SimContext
                        //
                        if(bUpdateDatabase){
                            con = null;
                            stmt = null;
                            try {
                                con = conFactory.getConnection(new Object());
                                stmt = con.createStatement();
                                //KeyValue mathKey = origMathDesc.getKey();
                                String UPDATESTATUS = "UPDATE " + SimContextStat2Table.table.getTableName() +
                                        " SET " + SimContextStat2Table.table.hasData.getUnqualifiedColName() + " = " + ((bApplicationHasData) ? (1) : (0)) + ", " +
                                        SimContextStat2Table.table.equiv.getUnqualifiedColName() + " = " + (mathCompareResults.isEquivalent() ? (1) : (0)) + ", " +
                                        SimContextStat2Table.table.status.getUnqualifiedColName() + " = '" + TokenMangler.getSQLEscapedString(mathCompareResults.toDatabaseStatus()) + "'" +
                                        ((issueString != null) ? (", " + SimContextStat2Table.table.comments.getUnqualifiedColName() + " = '" + TokenMangler.getSQLEscapedString(issueString, 255) + "'") : ("")) +
                                        " WHERE " + SimContextStat2Table.table.simContextRef.getUnqualifiedColName() + " = " + simContextFromDB.getKey();
                                if(lg.isDebugEnabled()){
                                    lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                                }
                                int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                                if(numRowsChanged != 1){
                                    lg.info("failed to update status");
                                }
                                con.commit();
                                if(lg.isTraceEnabled())
                                    lg.trace("-------------- Update=true, saved 'newMath' for Application '" + simContextFromDB.getName() + "'");
                            } catch(SQLException e){
                                lg.error(e.getMessage(), e);
                                if(lg.isWarnEnabled())
                                    lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE MATH for Application '" + simContextFromDB.getName() + "'");
                            } finally {
                                if(stmt != null){
                                    stmt.close();
                                }
                                con.close();
                            }
                        }
                    } catch(Throwable e){
                        lg.error(e.getMessage(), e); // exception in SimContext
                        if(bUpdateDatabase){
                            con = null;
                            stmt = null;
                            try {
                                con = conFactory.getConnection(new Object());
                                stmt = con.createStatement();
                                String status = "'EXCEPTION: " + TokenMangler.getSQLEscapedString(e.toString()) + "'";
                                if(status.length() > 255) status = status.substring(0, 254) + "'";
                                String UPDATESTATUS = "UPDATE " + SimContextStat2Table.table.getTableName() +
                                        " SET " + SimContextStat2Table.table.status.getUnqualifiedColName() + " = " + status +
                                        " WHERE " + SimContextStat2Table.table.simContextRef.getUnqualifiedColName() + " = " + simContextFromDB.getKey();
                                if(lg.isDebugEnabled()){
                                    lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                                }
                                int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                                if(numRowsChanged != 1){
                                    lg.info("failed to update status with exception");
                                }
                                con.commit();
                                if(lg.isTraceEnabled())
                                    lg.trace("-------------- Update=true, saved exception for Application '" + simContextFromDB.getName() + "'");
                            } catch(SQLException e2){
                                lg.error(e2.getMessage(), e2);
                                if(lg.isWarnEnabled())
                                    lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for Application '" + simContextFromDB.getName() + "'");
                            } finally {
                                if(stmt != null){
                                    stmt.close();
                                }
                                con.close();
                            }
                        }
                    }
                    //
                    // now, verify each associated simulation will apply overrides in an equivalent way
                    //
                    for(int l = 0; l < appSimsFromDB.length; l++){
                        try {
                            boolean bSimEquivalent = Simulation.testEquivalency(appSimsNewMath[l], appSimsFromDB[l], mathCompareResults);
                            if(lg.isTraceEnabled())
                                lg.trace("Application(" + simContextFromDB.getKey() + ") '" + simContextFromDB.getName() + "', " +
                                        "Simulation(" + modelSimsFromDB[l].getKey() + ") '" + modelSimsFromDB[l].getName() + "':" + modelSimsFromDB[l].getVersion().getDate() +
                                        "mathEquivalency=" + mathCompareResults.isEquivalent() + ", simEquivalency=" + bSimEquivalent);
                            //
                            // update Database Status for Simulation
                            //
                            if(bUpdateDatabase){
                                con = null;
                                stmt = null;
                                try {
                                    con = conFactory.getConnection(new Object());
                                    stmt = con.createStatement();
                                    String UPDATESTATUS = "UPDATE " + SimStatTable.table.getTableName() +
                                            " SET " + SimStatTable.table.equiv.getUnqualifiedColName() + " = " + ((bSimEquivalent) ? (1) : (0)) + ", " +
                                            SimStatTable.table.status.getUnqualifiedColName() + " = '" + TokenMangler.getSQLEscapedString(mathCompareResults.decision.description) + "'" +
                                            " WHERE " + SimStatTable.table.simRef.getUnqualifiedColName() + " = " + appSimsFromDB[l].getKey();
                                    if(lg.isDebugEnabled()){
                                        lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                                    }
                                    int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                                    if(numRowsChanged != 1){
                                        lg.info("failed to update status");
                                    }
                                    con.commit();
                                    if(lg.isTraceEnabled())
                                        lg.trace("-------------- Update=true, saved 'simulation status for simulation '" + appSimsFromDB[l].getName() + "'");
                                } catch(SQLException e){
                                    lg.error(e.getMessage(), e);
                                    if(lg.isWarnEnabled())
                                        lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE status for simulation '" + appSimsFromDB[l].getName() + "'");
                                } finally {
                                    if(stmt != null){
                                        stmt.close();
                                    }
                                    con.close();
                                }
                            }
                        } catch(Throwable e){
                            lg.error(e.getMessage(), e); // exception in SimContext
                            if(bUpdateDatabase){
                                con = null;
                                stmt = null;
                                try {
                                    con = conFactory.getConnection(new Object());
                                    stmt = con.createStatement();
                                    String status = "'EXCEPTION: " + TokenMangler.getSQLEscapedString(e.toString()) + "'";
                                    if(status.length() > 255) status = status.substring(0, 254) + "'";
                                    String UPDATESTATUS = "UPDATE " + SimStatTable.table.getTableName() +
                                            " SET " + SimStatTable.table.status.getUnqualifiedColName() + " = " + status +
                                            " WHERE " + SimStatTable.table.simRef.getUnqualifiedColName() + " = " + appSimsFromDB[l].getKey();
                                    if(lg.isDebugEnabled()){
                                        lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                                    }
                                    int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                                    if(numRowsChanged != 1){
                                        lg.info("failed to update status with exception");
                                    }
                                    con.commit();
                                    if(lg.isTraceEnabled())
                                        lg.trace("-------------- Update=true, saved exception for Simulation '" + appSimsFromDB[l].getName() + "'");
                                } catch(SQLException e2){
                                    lg.error(e2.getMessage(), e2);
                                    if(lg.isWarnEnabled())
                                        lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for simulation '" + appSimsFromDB[l].getName() + "'");
                                } finally {
                                    if(stmt != null){
                                        stmt.close();
                                    }
                                    con.close();
                                }
                            }
                        }
                    }
                }
            } catch(Throwable e){
                lg.error(e.getMessage(), e); // exception in whole BioModel
                // can't update anything in database, since we don't know what simcontexts are involved
            }
        }
    }


    @Deprecated
    private void scanSimContexts_not_used(boolean bUpdateDatabase, KeyValue[] simContextKeys) throws SQLException{
        java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
        //	calendar.set(2002,java.util.Calendar.MAY,7+1);
        calendar.set(2002, java.util.Calendar.JULY, 1);
        final java.util.Date fluxCorrectionOrDisablingBugFixDate = calendar.getTime();
        //	calendar.set(2001,java.util.Calendar.JUNE,13+1);
        calendar.set(2002, java.util.Calendar.JANUARY, 1);
        final java.util.Date totalVolumeCorrectionFixDate = calendar.getTime();

        User user = new User(PropertyLoader.ADMINISTRATOR_ACCOUNT, new KeyValue(PropertyLoader.ADMINISTRATOR_ID));
        for(KeyValue simContextKey : simContextKeys){
            if(lg.isTraceEnabled()) lg.trace("Testing SimContext with key '" + simContextKey + "'");
            // get biomodel refs
            Connection con = null;
            java.sql.Statement stmt = null;
            con = conFactory.getConnection(new Object());
            BioModelSimContextLinkTable bmscTable = BioModelSimContextLinkTable.table;
            String sql = "SELECT " + bmscTable.bioModelRef.getQualifiedColName() +
                    " FROM " + bmscTable.getTableName() +
                    " WHERE " + bmscTable.simContextRef.getQualifiedColName() + " = " + simContextKey;
            Vector<KeyValue> keys = new Vector<>();
            stmt = con.createStatement();
            try {
                ResultSet rset = stmt.executeQuery(sql);
                while (rset.next()) {
                    KeyValue key = new KeyValue(rset.getBigDecimal(bmscTable.bioModelRef.getUnqualifiedColName()));
                    keys.addElement(key);
                }
            } finally {
                if(stmt != null){
                    stmt.close();
                }
                con.close();
            }

            KeyValue[] bmKeys = keys.toArray(KeyValue[]::new);
            try {
                // use the first biomodel...
                BioModelInfo bioModelInfo = dbServerImpl.getBioModelInfo(user, bmKeys[0]);
                //
                // read in the BioModel from the database
                //
                BigString bioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfo.getVersion().getVersionKey());
                BioModel bioModelFromDB = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
                BioModel bioModelNewMath = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
                bioModelFromDB.refreshDependencies();
                bioModelNewMath.refreshDependencies();

                //
                // get all Simulations for this model
                //
                Simulation[] modelSimsFromDB = bioModelFromDB.getSimulations();

                //
                // ---> only for the SimContext we started with...
                // recompute mathDescription, and verify it is equivalent
                // then check each associated simulation to ensure math overrides are applied in an equivalent manner also.
                //
                SimulationContext[] simContextsFromDB = bioModelFromDB.getSimulationContexts();
                SimulationContext[] simContextsNewMath = bioModelNewMath.getSimulationContexts();
                SimulationContext simContextFromDB = null;
                SimulationContext simContextNewMath = null;
                for(int k = 0; k < simContextsFromDB.length; k++){
                    // find it...
                    if(simContextsFromDB[k].getKey().equals(simContextKey)){
                        simContextFromDB = simContextsFromDB[k];
                        simContextNewMath = simContextsNewMath[k];
                        break;
                    }
                }
                if(simContextFromDB == null){
                    throw new RuntimeException("BioModel referred to by this SimContext does not contain this SimContext");
                } else {
                    Simulation[] appSimsFromDB = simContextFromDB.getSimulations();
                    Simulation[] appSimsNewMath = simContextNewMath.getSimulations();
                    MathCompareResults mathCompareResults = null;
                    try {
                        MathDescription origMathDesc = simContextFromDB.getMathDescription();
                        //
                        // find out if any simulation belonging to this Application has data
                        //
                        boolean bApplicationHasData = false;
                        for(int l = 0; l < modelSimsFromDB.length; l++){
                            SimulationStatusPersistent simulationStatus = dbServerImpl.getSimulationStatus(modelSimsFromDB[l].getKey());
                            if(simulationStatus != null && simulationStatus.getHasData()){
                                bApplicationHasData = true;
                            }
                        }
                        //
                        // bug compatability mode off
                        //
//                    cbit.vcell.mapping.MembraneStructureAnalyzer.bResolvedFluxCorrectionBug = false;
//                    cbit.vcell.mapping.MembraneMapping.bFluxCorrectionBugMode = false;
//                    cbit.vcell.mapping.FeatureMapping.bTotalVolumeCorrectionBug = false;
//                    cbit.vcell.mapping.MembraneStructureAnalyzer.bNoFluxIfFixed = false;

                        //
                        // make sure geometry is up to date on "simContextNewMath"
                        //
                        try {
                            if(simContextNewMath.getGeometry().getDimension() > 0 && simContextNewMath.getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null){
                                simContextNewMath.getGeometry().getGeometrySurfaceDescription().updateAll();
                            }
                        } catch(Exception e){
                            lg.error(e.getMessage(), e);
                        }

                        //
                        // updated mathdescription loaded into copy of biomodel, then test for equivalence.
                        //
                        cbit.vcell.mapping.MathMapping mathMapping = simContextNewMath.createNewMathMapping();
                        MathDescription newMathDesc = mathMapping.getMathDescription();
                        String issueString = null;
                        org.vcell.util.Issue[] issues = mathMapping.getIssues();
                        if(issues != null && issues.length > 0){
                            StringBuffer buffer = new StringBuffer("Issues(" + issues.length + "):");
                            for(int l = 0; l < issues.length; l++){
                                buffer.append(" <<" + issues[l].toString() + ">>");
                            }
                            issueString = buffer.toString();
                        }
                        simContextNewMath.setMathDescription(newMathDesc);

                        mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), origMathDesc, newMathDesc);
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(">>>BioModel(" + bioModelFromDB.getVersion().getVersionKey() + ") '" + bioModelFromDB.getName() + "':" + bioModelFromDB.getVersion().getDate() + ", Application(" + simContextFromDB.getKey() + ") '" + simContextFromDB.getName() + "' <<EQUIV=" + mathCompareResults.isEquivalent() + ">>: " + mathCompareResults.toDatabaseStatus());
                        //
                        // update Database Status for SimContext
                        //
                        if(bUpdateDatabase){
                            con = null;
                            stmt = null;
                            try {
                                con = conFactory.getConnection(new Object());
                                stmt = con.createStatement();
                                //KeyValue mathKey = origMathDesc.getKey();
                                String UPDATESTATUS = "UPDATE " + SimContextStat2Table.table.getTableName() +
                                        " SET " + SimContextStat2Table.table.hasData.getUnqualifiedColName() + " = " + ((bApplicationHasData) ? (1) : (0)) + ", "
                                        + SimContextStat2Table.table.equiv.getUnqualifiedColName() + " = " + (mathCompareResults.isEquivalent() ? (1) : (0)) + ", "
                                        + SimContextStat2Table.table.status.getUnqualifiedColName() + " = '" + TokenMangler.getSQLEscapedString(mathCompareResults.toDatabaseStatus()) + "'"
                                        + ((issueString != null) ? (", " + SimContextStat2Table.table.comments.getUnqualifiedColName() + " = '" + TokenMangler.getSQLEscapedString(issueString, 255) + "'") : ("")) +
                                        " WHERE " + SimContextStat2Table.table.simContextRef.getUnqualifiedColName() + " = " + simContextFromDB.getKey();
                                if(lg.isDebugEnabled()){
                                    lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                                }
                                int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                                if(numRowsChanged != 1){
                                    lg.info("failed to update status");
                                }
                                con.commit();
                                if(lg.isTraceEnabled())
                                    lg.trace("-------------- Update=true, saved 'newMath' for Application '" + simContextFromDB.getName() + "'");
                            } catch(SQLException e){
                                lg.error(e.getMessage(), e);
                                if(lg.isWarnEnabled())
                                    lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE MATH for Application '" + simContextFromDB.getName() + "'");
                            } finally {
                                if(stmt != null){
                                    stmt.close();
                                }
                                con.close();
                            }
                        }
                    } catch(Throwable e){
                        lg.error(e.getMessage(), e); // exception in SimContext
                        if(bUpdateDatabase){
                            con = null;
                            stmt = null;
                            try {
                                con = conFactory.getConnection(new Object());
                                stmt = con.createStatement();
                                String status = "'EXCEPTION: " + TokenMangler.getSQLEscapedString(e.toString()) + "'";
                                if(status.length() > 255) status = status.substring(0, 254) + "'";
                                String UPDATESTATUS = "UPDATE " + SimContextStat2Table.table.getTableName() +
                                        " SET " + SimContextStat2Table.table.status.getUnqualifiedColName() + " = " + status +
                                        " WHERE " + SimContextStat2Table.table.simContextRef.getUnqualifiedColName() + " = " + simContextFromDB.getKey();
                                if(lg.isDebugEnabled()){
                                    lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                                }
                                int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                                if(numRowsChanged != 1){
                                    lg.info("failed to update status with exception");
                                }
                                con.commit();
                                if(lg.isTraceEnabled())
                                    lg.trace("-------------- Update=true, saved exception for Application '" + simContextFromDB.getName() + "'");
                            } catch(SQLException e2){
                                lg.error(e2.getMessage(), e2);
                                if(lg.isWarnEnabled())
                                    lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for Application '" + simContextFromDB.getName() + "'");
                            } finally {
                                if(stmt != null){
                                    stmt.close();
                                }
                                con.close();
                            }
                        }
                    }
                    //
                    // now, verify each associated simulation will apply overrides in an equivalent way
                    //
                    for(int l = 0; l < appSimsFromDB.length; l++){
                        try {
                            boolean bSimEquivalent = Simulation.testEquivalency(appSimsNewMath[l], appSimsFromDB[l], mathCompareResults);
                            if(lg.isTraceEnabled())
                                lg.trace("Application(" + simContextFromDB.getKey() + ") '" + simContextFromDB.getName() + "', " + "Simulation(" + modelSimsFromDB[l].getKey() + ") '" + modelSimsFromDB[l].getName() + "':" + modelSimsFromDB[l].getVersion().getDate() + "mathEquivalency=" + mathCompareResults.isEquivalent() + ", simEquivalency=" + bSimEquivalent);
                            //
                            // update Database Status for Simulation
                            //
                            if(bUpdateDatabase){
                                con = null;
                                stmt = null;
                                try {
                                    con = conFactory.getConnection(new Object());
                                    stmt = con.createStatement();
                                    String UPDATESTATUS = "UPDATE " + SimStatTable.table.getTableName() +
                                            " SET " + SimStatTable.table.equiv.getUnqualifiedColName() + " = " + ((bSimEquivalent) ? (1) : (0)) + ", " + SimStatTable.table.status.getUnqualifiedColName() + " = '" + TokenMangler.getSQLEscapedString(mathCompareResults.toDatabaseStatus()) + "'" +
                                            " WHERE " + SimStatTable.table.simRef.getUnqualifiedColName() + " = " + appSimsFromDB[l].getKey();
                                    if(lg.isDebugEnabled()){
                                        lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                                    }
                                    int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                                    if(numRowsChanged != 1){
                                        lg.info("failed to update status");
                                    }
                                    con.commit();
                                    if(lg.isTraceEnabled())
                                        lg.trace("-------------- Update=true, saved 'simulation status for simulation '" + appSimsFromDB[l].getName() + "'");
                                } catch(SQLException e){
                                    lg.error(e.getMessage(), e);
                                    if(lg.isWarnEnabled())
                                        lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE status for simulation '" + appSimsFromDB[l].getName() + "'");
                                } finally {
                                    if(stmt != null){
                                        stmt.close();
                                    }
                                    con.close();
                                }
                            }
                        } catch(Throwable e){
                            lg.error(e.getMessage(), e); // exception in SimContext
                            if(bUpdateDatabase){
                                con = null;
                                stmt = null;
                                try {
                                    con = conFactory.getConnection(new Object());
                                    stmt = con.createStatement();
                                    String status = "'EXCEPTION: " + TokenMangler.getSQLEscapedString(e.toString()) + "'";
                                    if(status.length() > 255) status = status.substring(0, 254) + "'";
                                    String UPDATESTATUS = "UPDATE " + SimStatTable.table.getTableName() +
                                            " SET " + SimStatTable.table.status.getUnqualifiedColName() + " = " + status +
                                            " WHERE " + SimStatTable.table.simRef.getUnqualifiedColName() + " = " + appSimsFromDB[l].getKey();
                                    if(lg.isDebugEnabled()){
                                        lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                                    }
                                    int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                                    if(numRowsChanged != 1){
                                        lg.info("failed to update status with exception");
                                    }
                                    con.commit();
                                    if(lg.isTraceEnabled())
                                        lg.trace("-------------- Update=true, saved exception for Simulation '" + appSimsFromDB[l].getName() + "'");
                                } catch(SQLException e2){
                                    lg.error(e2.getMessage(), e2);
                                    if(lg.isWarnEnabled())
                                        lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for simulation '" + appSimsFromDB[l].getName() + "'");
                                } finally {
                                    if(stmt != null){
                                        stmt.close();
                                    }
                                    con.close();
                                }
                            }
                        }
                    }
                }
            } catch(Throwable e){
                lg.error(e.getMessage(), e); // exception in whole BioModel
                // update database, since we know the simcontext...
                if(bUpdateDatabase){
                    con = null;
                    stmt = null;
                    try {
                        con = conFactory.getConnection(new Object());
                        stmt = con.createStatement();
                        //KeyValue mathKey = origMathDesc.getKey();
                        String UPDATESTATUS = "UPDATE " + SimContextStat2Table.table.getTableName() +
                                " SET " + SimContextStat2Table.table.status.getUnqualifiedColName() + " = 'BIOMODEL EXCEPTION: " + TokenMangler.getSQLEscapedString(e.toString()) + "'" +
                                " WHERE " + SimContextStat2Table.table.simContextRef.getUnqualifiedColName() + " = " + simContextKey;
                        if(lg.isDebugEnabled()){
                            lg.debug("executeUpdate() SQL: '" + UPDATESTATUS + "'", new DbDriver.StackTraceGenerationException());
                        }
                        int numRowsChanged = stmt.executeUpdate(UPDATESTATUS); // jcs: added logging
                        if(numRowsChanged != 1){
                            lg.info("failed to update status with exception");
                        }
                        con.commit();
                        if(lg.isTraceEnabled())
                            lg.trace("-------------- Update=true, saved exception for Application with key '" + simContextKey + "'");
                    } catch(SQLException e2){
                        lg.error(e2.getMessage(), e2);
                        if(lg.isWarnEnabled())
                            lg.warn("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for Application with key '" + simContextKey + "'");
                    } finally {
                        if(stmt != null){
                            stmt.close();
                        }
                        con.close();
                    }
                }
            }
        }
    }

}
