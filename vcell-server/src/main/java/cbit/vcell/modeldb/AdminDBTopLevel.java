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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.messaging.db.SimulationJobDbDriver;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.modeldb.ApiAccessToken.AccessTokenStatus;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.server.SimpleJobStatusPersistent;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.UpdateSynchronizationException;

/**
 * This type was created in VisualAge.
 */
public class AdminDBTopLevel extends AbstractDBTopLevel {

    private UserDbDriver userDB = null;
    private SimulationJobDbDriver jobDB = null;
    private static final int SQL_ERROR_CODE_BADCONNECTION = 1010; //??????????????????????????????????????

    /**
     * DBTopLevel constructor comment.
     */
    public AdminDBTopLevel(ConnectionFactory aConFactory) throws SQLException{
        super(aConFactory);
        userDB = new UserDbDriver();
        jobDB = new SimulationJobDbDriver(aConFactory.getDatabaseSyntax());
    }


    public ExternalDataIdentifier[] getExternalDataIdentifiers(User fieldDataOwner, boolean bEnableRetry) throws SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return DbDriver.fieldDataDBOperation(
                    con, conFactory.getKeyFactory(), null,
                    FieldDataDBOperationSpec.createGetExtDataIDsSpec(fieldDataOwner)).extDataIDArr;
        } catch(Throwable e){
            lg.error("failure in getExternalDataIdentifiers()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getExternalDataIdentifiers(fieldDataOwner, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    public VCInfoContainer getPublicOracleVCInfoContainer(boolean bEnableRetry) throws SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            User user = new User("empty", new KeyValue("223553217"));//temp user with no models so only public are returned
            return DbDriver.getVCInfoContainer(user, con, DatabaseSyntax.ORACLE, false);
        } catch(Throwable e){
            lg.error("failure in getVCInfoContainer()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getPublicOracleVCInfoContainer(false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/6/2005 3:14:40 PM)
     */
    SimulationJobStatusPersistent[] getActiveJobs(Connection con, VCellServerID serverID) throws SQLException{
        SimulationJobStatusPersistent[] jobStatusArray = jobDB.getActiveJobs(con, serverID);
        return jobStatusArray;
    }

    public SimulationJobStatusPersistent[] getActiveJobs(VCellServerID serverID, boolean bEnableRetry) throws java.sql.SQLException{
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return jobDB.getActiveJobs(con, serverID);
        } catch(Throwable e){
            lg.error("failure in getActiveJobs()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getActiveJobs(serverID, false);
            } else {
                handle_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    public Map<KeyValue, SimulationRequirements> getSimulationRequirements(Collection<KeyValue> simKeys, boolean bEnableRetry) throws java.sql.SQLException{
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return jobDB.getSimulationRequirements(con, simKeys);
        } catch(Throwable e){
            lg.error("failure in getSimulationRequirements()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationRequirements(simKeys, false);
            } else {
                handle_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public Set<KeyValue> getUnreferencedSimulations(boolean bEnableRetry) throws java.sql.SQLException{
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            Set<KeyValue> unreferencedSimulations = DBBackupAndClean.getUnreferencedSimulations(con, conFactory.getDatabaseSyntax());
            return unreferencedSimulations;
        } catch(Throwable e){
            lg.error("failure in getUnreferencedSimulations()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getUnreferencedSimulations(false);
            } else {
                handle_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public SimulationJobStatusPersistent getSimulationJobStatus(KeyValue simKey, int jobIndex, int taskID, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            SimulationJobStatusPersistent jobStatus = getSimulationJobStatus(con, simKey, jobIndex, taskID);
            return jobStatus;
        } catch(Throwable e){
            lg.error("failure in getSimulationJobStatus()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationJobStatus(simKey, jobIndex, taskID, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public SimulationJobStatusPersistent[] getSimulationJobStatusArray(KeyValue simKey, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            SimulationJobStatusPersistent[] jobStatus = getSimulationJobStatusArray(con, simKey);
            return jobStatus;
        } catch(Throwable e){
            lg.error("failure in getSimulationJobStatusArray()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationJobStatusArray(simKey, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public SimulationJobStatusPersistent[] getSimulationJobStatusArray(KeyValue simKey, int jobIndex, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            SimulationJobStatusPersistent[] jobStatus = getSimulationJobStatusArray(con, simKey, jobIndex);
            return jobStatus;
        } catch(Throwable e){
            lg.error("failure in getSimulationJobStatusArray()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationJobStatusArray(simKey, jobIndex, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    private static final String QUERY_VALUE = "QUERY_VALUE";

    private static int htmlWithBreak(String descr, StringBuffer sb, Statement stmt, String query) throws SQLException{
        sb.append("<br>" + descr);
        lg.info(query);
        ResultSet rset = stmt.executeQuery(query);
        int val = 0;
        if(rset.next()){
            val = rset.getInt(QUERY_VALUE);
            sb.append(val + "");
        }
        sb.append("</br>\n");
        rset.close();
        return val;
    }

    public synchronized String getBasicStatistics() throws SQLException, DataAccessException{
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        Statement stmt = null;
        try {
            StringBuffer sb = new StringBuffer(
                    "<html>" +
                            "<head>\n" +
                            "<style>\n" +
                            "table, td, th {\n" +
                            "  border: 1px solid;\n" +
                            "}\n" +
                            "table {\n" +
                            "  width: 100%;\n" +
                            "  border-collapse: collapse;\n" +
                            "}\n" +
                            "th, td {\n" +
                            "  padding: 5px;\n" +
                            "  text-align: left;\n" +
                            "}\n" +
                            "</style>\n" +
                            "</head>\n" +
                            "<body>");
            stmt = con.createStatement();
            final int[] pastTime = new int[]{7, 30, 90, 180, 365};
            final String[] pastTimeDescr = new String[]{"last week", "last month", "last 3 month", "last 6 month", "last year"};
            for(int i = 0; i < pastTimeDescr.length; i++){
                htmlWithBreak("Users registered (" + pastTimeDescr[i] + ")=", sb, stmt, "select count(*) " + QUERY_VALUE + " from vc_userinfo where insertdate >= (current_date - " + pastTime[i] + ")");
            }

            String totalUsersQuery = "select count(*) " + QUERY_VALUE + " from vc_userinfo";
            htmlWithBreak("Number users=", sb, stmt, totalUsersQuery);

            String usersWithSimsQuery = "select count(distinct ownerref) " + QUERY_VALUE + " from vc_simulation";
            htmlWithBreak("Users with simulations=", sb, stmt, usersWithSimsQuery);

            String bmQuery = "select count(*) " + QUERY_VALUE + " from vc_biomodel";
            htmlWithBreak("Biomodels=", sb, stmt, bmQuery);

            String mmQuery = "select count(*) " + QUERY_VALUE + " from vc_mathmodel";
            htmlWithBreak("Mathmodels=", sb, stmt, mmQuery);

            String allModelsQuery = "select count(*) " + QUERY_VALUE + " from (select vc_biomodel.id from vc_biomodel union select vc_mathmodel.id from vc_mathmodel) t";
            htmlWithBreak("Total models=", sb, stmt, allModelsQuery);

            String pubbmQuery = "select count(*) " + QUERY_VALUE + " from vc_biomodel where PRIVACY=0";
            htmlWithBreak("Public biomodels=", sb, stmt, pubbmQuery);

            String pubmmQuery = "select count(*) " + QUERY_VALUE + " from vc_mathmodel where PRIVACY=0";
            htmlWithBreak("Public math models=", sb, stmt, pubmmQuery);

            String puballModelsQuery = "select count(*) " + QUERY_VALUE + " from (select vc_biomodel.id from vc_biomodel where PRIVACY=0 union select vc_mathmodel.id from vc_mathmodel where PRIVACY=0) t";
            htmlWithBreak("Total public models=", sb, stmt, puballModelsQuery);

            String allsims = "select count(*) " + QUERY_VALUE + " from vc_simulation";
            htmlWithBreak("Number simulations=", sb, stmt, allsims);

            String pubbmsimsQuery = "SELECT COUNT (*) " + QUERY_VALUE + " FROM VC_SIMULATION WHERE VC_SIMULATION.ID IN ("
                    + "SELECT DISTINCT VC_SIMULATION.ID FROM VC_BIOMODEL, VC_SIMULATION, VC_BIOMODELSIM "
                    + "WHERE VC_SIMULATION.ID = VC_BIOMODELSIM.simref "
                    + "AND VC_BIOMODEL.ID = VC_BIOMODELSIM.biomodelref "
                    + "AND vc_biomodel.privacy = 0)";
            int pubbmsimsval = htmlWithBreak("Public biomodel simulations=", sb, stmt, pubbmsimsQuery);

            String pubmmsimsQuery = "SELECT COUNT (*) " + QUERY_VALUE + " FROM VC_SIMULATION WHERE VC_SIMULATION.ID IN ("
                    + "SELECT DISTINCT VC_SIMULATION.ID FROM VC_MATHMODEL, VC_SIMULATION, VC_MATHMODELSIM "
                    + "WHERE VC_SIMULATION.ID = VC_MATHMODELSIM.simref "
                    + "AND VC_MATHMODEL.ID = VC_MATHMODELSIM.mathmodelref "
                    + "AND VC_MATHMODEL.privacy = 0)";
            int pubmmsimsval = htmlWithBreak("Public math model simulations=", sb, stmt, pubmmsimsQuery);

            String puballsimssQuery = "select sum(t." + QUERY_VALUE + ") " + QUERY_VALUE + " from (" +
                    pubbmsimsQuery +
                    " union " +
                    pubmmsimsQuery +
                    ") t";
            htmlWithBreak("Total public simulations=", sb, stmt, puballsimssQuery);

            sb.append("<br></br>");
            sb.append("<h2>Users running sims over various time periods:</h2>");
            sb.append("<table>");
            sb.append("<tr>");
            for(int j = 0; j < pastTimeDescr.length; j++){
                sb.append("<th>" + pastTimeDescr[j] + "</th><th>Total</th>");
            }
            sb.append("</tr>");
            TreeMap<String, ArrayList<String>> pastMapRows = new TreeMap<>();
            int maxRows = 0;
            for(int i = 0; i < pastTimeDescr.length; i++){
                ResultSet rset = stmt.executeQuery(
                        "SELECT userid,  COUNT(vc_simulationjob.id) simcount FROM vc_userinfo,  vc_simulation,  vc_simulationjob"
                                + " WHERE vc_userinfo.id = vc_simulation.ownerref AND "
                                + "vc_simulationjob.simref = vc_simulation.id AND "
                                + "vc_simulationjob.submitdate >= (CURRENT_DATE -" + pastTime[i] + ")" + " GROUP BY userid ORDER BY simcount desc");
                ArrayList<String> rows = new ArrayList<>();
                pastMapRows.put(pastTimeDescr[i], rows);
                while (rset.next()) {
                    rows.add("<td>" + rset.getString(1) + "</td><td>" + rset.getInt(2) + "</td>");
                }
                rset.close();
                maxRows = Math.max(maxRows, rows.size());
            }
            for(int i = 0; i < maxRows; i++){
                sb.append("<tr>");
                for(int j = 0; j < pastMapRows.size(); j++){
                    ArrayList<String> rows = pastMapRows.get(pastTimeDescr[j]);
                    if(i < rows.size()){
                        sb.append(rows.get(i));
                    } else {
                        sb.append("<td></td><td></td>");
                    }
                }
                sb.append("</tr>");
            }
            sb.append("</table>");
            sb.append("</body></html>");
            return sb.toString();
        } catch(Throwable e){
            lg.error("failure in getSimulationJobStatusArray()", e);
            handle_DataAccessException_SQLException(e);
            return null; // never gets here;
        } finally {
            try {
                if(stmt != null){
                    stmt.close();
                }
            } catch(Exception e){
                lg.error(e.getMessage(), e);
            }
            conFactory.release(con, lock);
        }
        //select vcuserid,count(vc_simulationjob.id) simcount
        //from vc_userinfo,vc_simulation,vc_simulationjob
        //where vc_userinfo.id = vc_simulation.ownerref
        //and vc_simulationjob.simref = vc_simulation.id
        //and vc_simulationjob.submitdate >= to_date('04-SEP-2016','DD-MM-YY')
        //and vc_simulationjob.submitdate <= to_date('05-SEP-2016','DD-MM-YY')
        //group by userid
        //order by simcount desc
    }

    /**
     * Insert the method's description here.
     * Creation date: (9/3/2003 8:59:46 AM)
     *
     * @param conditions java.lang.String
     * @return java.util.List
     */
    public java.util.List<SimpleJobStatusPersistent> getSimpleJobStatus(String conditions, int startRow, int maxNumRows, boolean bEnableRetry) throws java.sql.SQLException, org.vcell.util.DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return jobDB.getSimpleJobStatus(con, conditions, startRow, maxNumRows);
        } catch(Throwable e){
            lg.error("failure in getSimpleJobStatus()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimpleJobStatus(conditions, startRow, maxNumRows, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public java.util.List<SimulationJobStatusPersistent> getSimulationJobStatus(SimpleJobStatusQuerySpec simStatusQuerySpec, boolean bEnableRetry) throws SQLException, DataAccessException{
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return jobDB.getSimulationJobStatus(con, simStatusQuerySpec);
        } catch(Throwable e){
            lg.error("failure in getSimpleJobStatus()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationJobStatus(simStatusQuerySpec, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public java.util.List<SimpleJobStatusPersistent> getSimpleJobStatus(SimpleJobStatusQuerySpec simStatusQuerySpec, boolean bEnableRetry) throws SQLException, DataAccessException{
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return jobDB.getSimpleJobStatus(con, simStatusQuerySpec);
        } catch(Throwable e){
            lg.error("failure in getSimpleJobStatus()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimpleJobStatus(simStatusQuerySpec, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/6/2005 3:08:22 PM)
     */
    SimulationJobStatusPersistent getSimulationJobStatus(Connection con, KeyValue simKey, int jobIndex, int taskID) throws SQLException{
        SimulationJobStatusPersistent jobStatus = jobDB.getSimulationJobStatus(con, simKey, jobIndex, taskID, false);
        return jobStatus;
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/6/2005 3:08:22 PM)
     */
    SimulationJobStatusPersistent[] getSimulationJobStatusArray(Connection con, KeyValue simKey) throws SQLException{
        SimulationJobStatusPersistent[] jobStatus = jobDB.getSimulationJobStatusArray(con, simKey, false);
        return jobStatus;
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/6/2005 3:08:22 PM)
     */
    SimulationJobStatusPersistent[] getSimulationJobStatusArray(Connection con, KeyValue simKey, int jobIndex) throws SQLException{
        SimulationJobStatusPersistent[] jobStatus = jobDB.getSimulationJobStatusArray(con, simKey, jobIndex, false);
        return jobStatus;
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/31/2003 2:35:44 PM)
     *
     * @param bActiveOnly boolean
     * @param owner       cbit.vcell.server.User
     * @return cbit.vcell.solvers.SimulationJobStatus[]
     */
    SimulationJobStatusPersistent[] getSimulationJobStatus(Connection con, boolean bActiveOnly, User owner) throws java.sql.SQLException, DataAccessException{
        return jobDB.getSimulationJobStatus(con, bActiveOnly, owner);
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/31/2003 2:35:44 PM)
     *
     * @param bActiveOnly boolean
     * @param owner       cbit.vcell.server.User
     * @return cbit.vcell.solvers.SimulationJobStatus[]
     */
    SimulationJobStatusPersistent[] getSimulationJobStatus(boolean bActiveOnly, User owner, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return jobDB.getSimulationJobStatus(con, bActiveOnly, owner);
        } catch(Throwable e){
            lg.error("failure in getSimulationJobStatus()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationJobStatus(bActiveOnly, owner, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    SimulationStatusPersistent[] getSimulationStatus(KeyValue simulationKeys[], boolean bEnableRetry) throws java.sql.SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            SimulationJobStatusPersistent[] jobStatuses = jobDB.getSimulationJobStatus(con, simulationKeys);
            SimulationStatusPersistent[] simStatuses = new SimulationStatusPersistent[simulationKeys.length];
            for(int i = 0; i < simulationKeys.length; i++){
                Vector<SimulationJobStatusPersistent> v = new Vector<SimulationJobStatusPersistent>();
                for(SimulationJobStatusPersistent jobStatus : jobStatuses){
                    if(jobStatus.getVCSimulationIdentifier().getSimulationKey().equals(simulationKeys[i])){
                        v.add(jobStatus);
                    }
                }
                if(v.isEmpty()){
                    simStatuses[i] = null;
                } else {
                    simStatuses[i] = new SimulationStatusPersistent(v.toArray(SimulationJobStatusPersistent[]::new));
                }
            }
            return simStatuses;
        } catch(Throwable e){
            lg.error("failure in getSimulationStatus()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationStatus(simulationKeys, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    SimulationStatusPersistent getSimulationStatus(KeyValue simKey, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            SimulationJobStatusPersistent[] jobStatuses = jobDB.getSimulationJobStatus(con, simKey);
            if(jobStatuses.length > 0){
                return new SimulationStatusPersistent(jobStatuses);
            } else {
                return null;
            }
        } catch(Throwable e){
            lg.error("failure in getSimulationStatus()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationStatus(simKey, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    /**
     * @param userid           username
     * @param digestedPassword
     * @param bEnableRetry     try again if first attempt fails
     * @param isLocal          TODO
     * @return User object
     * @throws DataAccessException
     * @throws java.sql.SQLException
     * @throws ObjectNotFoundException
     */
    public User getUser(String userid, UserLoginInfo.DigestedPassword digestedPassword, boolean bEnableRetry, boolean isLocal)
            throws DataAccessException, java.sql.SQLException, ObjectNotFoundException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return userDB.getUserFromUseridAndPassword(con, userid, digestedPassword, isLocal);
        } catch(Throwable e){
            lg.error("failure in getUser()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getUser(userid, digestedPassword, false, isLocal);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    public void setUserIdentityFromIdentityProvider(User user, String identity, UserIdentityTable.IdentityProvider identityProvider, boolean bEnableRetry) throws SQLException, DataAccessException {
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            userDB.setUserIdentity(con, user, identity, identityProvider, conFactory.getKeyFactory());
        } catch(Throwable e){
            lg.error("failure in getUserIdentityFromAuth0()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                setUserIdentityFromIdentityProvider(user, identity, identityProvider, false);
            } else {
                handle_DataAccessException_SQLException(e);
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    public void deleteUserIdentityFromIdentityProvider(User user, UserIdentityTable.IdentityProvider identityProvider, boolean bEnableRetry) throws SQLException, DataAccessException {
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            userDB.removeUserIdentity(con, user, identityProvider);
        } catch(Throwable e){
            lg.error("failure in getUserIdentityFromAuth0()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                deleteUserIdentityFromIdentityProvider(user, identityProvider, false);
            } else {
                handle_DataAccessException_SQLException(e);
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    public void removeAllUsersIdentities(User user, boolean bEnableRetry) throws SQLException, DataAccessException {
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            userDB.cleanAllUserIdentities(con, user);
        } catch(Throwable e){
            lg.error("failure in getUserIdentityFromAuth0()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                removeAllUsersIdentities(user, false);
            } else {
                handle_DataAccessException_SQLException(e);
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    public UserIdentity getUserIdentityFromSubjectAndIdentityProvider(String subject, UserIdentityTable.IdentityProvider identityProvider,boolean bEnableRetry)
            throws DataAccessException, java.sql.SQLException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return userDB.getUserIdentityFromSubjectAndIdentityProvider(con, subject, identityProvider);
        } catch(Throwable e){
            lg.error("failure in getUserIdentityFromAuth0()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getUserIdentityFromSubjectAndIdentityProvider(subject, identityProvider, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    public ArrayList<UserIdentity> getUserIdentitiesFromUser(User user, boolean bEnableRetry)
            throws DataAccessException, java.sql.SQLException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return userDB.getIdentitiesFromUser(con, user);
        } catch(Throwable e){
            lg.error("failure in getUserIdentitiesFromUser()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getUserIdentitiesFromUser(user, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public ApiAccessToken generateApiAccessToken(KeyValue apiClientKey, User user, Date expirationDate, boolean bEnableRetry)
            throws DataAccessException, java.sql.SQLException, ObjectNotFoundException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            ApiAccessToken apiAccessToken = userDB.generateApiAccessToken(con, conFactory.getKeyFactory(), apiClientKey, user, expirationDate);
            con.commit();
            return apiAccessToken;
        } catch(Throwable e){
            lg.error("failure in generateApiAccessToken()", e);
            try {
                con.rollback();
            } catch(Throwable rbe){
                lg.error("failure in rollback in generateApiAccessToken(), bEnableRetry=" + bEnableRetry, rbe);
            }
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return generateApiAccessToken(apiClientKey, user, expirationDate, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public void setApiAccessTokenStatus(ApiAccessToken accessToken, AccessTokenStatus newAccessTokenStatus, boolean bEnableRetry) throws SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            userDB.setApiAccessTokenStatus(con, accessToken.getKey(), newAccessTokenStatus);
            con.commit();
        } catch(Throwable e){
            lg.error("failure in setApiAccessTokenStatus()", e);
            try {
                con.rollback();
            } catch(Throwable rbe){
                lg.error("exception during rollback, bEnableRetry = " + bEnableRetry, rbe);
            }
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                setApiAccessTokenStatus(accessToken, newAccessTokenStatus, false);
            } else {
                handle_DataAccessException_SQLException(e);
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public ApiAccessToken getApiAccessToken(String accessToken, boolean bEnableRetry) throws SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return userDB.getApiAccessToken(con, conFactory.getDatabaseSyntax(), accessToken);
        } catch(Throwable e){
            lg.error("failure in getApiAccessToken()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getApiAccessToken(accessToken, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public ApiClient getApiClient(String clientId, boolean bEnableRetry) throws SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return userDB.getApiClient(con, clientId);
        } catch(Throwable e){
            lg.error("failure in getApiClient()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getApiClient(clientId, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public User.SpecialUser getUser(String userid, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return userDB.getUserFromUserid(con, userid);
        } catch(Throwable e){
            lg.error("failure in getUser()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getUser(userid, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public User getUserFromSimulationKey(KeyValue simKey, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return jobDB.getUserFromSimulationKey(con, simKey);
        } catch(Throwable e){
            lg.error("failure in getUserFromSimulationKey()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getUserFromSimulationKey(simKey, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    UserInfo getUserInfo(KeyValue key, boolean bEnableRetry)
            throws DataAccessException, java.sql.SQLException, ObjectNotFoundException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return userDB.getUserInfo(con, key);
        } catch(Throwable e){
            lg.error("failure in getUserInfo()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getUserInfo(key, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    void sendLostPassword(String userid, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException{
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            userDB.sendLostPassword(con, userid);
        } catch(Throwable e){
            lg.error("failure in sendLostPassword()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                sendLostPassword(userid, false);
            } else {
                handle_DataAccessException_SQLException(e);
            }
        } finally {
            conFactory.release(con, lock);
        }

    }


    public UserInfo[] getUserInfos(boolean bEnableRetry)
            throws DataAccessException, java.sql.SQLException, ObjectNotFoundException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return userDB.getUserInfos(con);
        } catch(Throwable e){
            lg.error("failure in getUserInfos()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getUserInfos(false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public void insertSimulationJobStatus(SimulationJobStatusPersistent simulationJobStatus, boolean bEnableRetry) throws SQLException, DataAccessException, UpdateSynchronizationException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            insertSimulationJobStatus(con, simulationJobStatus);
            con.commit();
        } catch(Throwable e){
            lg.error("failure in insertSimulationJobStatus()", e);
            try {
                con.rollback();
            } catch(Throwable rbe){
                lg.error("exception during rollback, bEnableRetry = " + bEnableRetry, rbe);
            }
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
            } else {
                handle_DataAccessException_SQLException(e);
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/3/2005 3:33:09 PM)
     */
    void insertSimulationJobStatus(Connection con, SimulationJobStatusPersistent simulationJobStatus) throws SQLException, UpdateSynchronizationException{
        jobDB.insertSimulationJobStatus(con, simulationJobStatus, conFactory.getKeyFactory().getNewKey(con));
        VCMongoMessage.sendSimJobStatusInsert(simulationJobStatus);
    }


    /**
     * This method was created in VisualAge.
     *
     * @param newUserInfo cbit.sql.UserInfo
     * @return cbit.sql.UserInfo
     */
    KeyValue insertUserInfo(UserInfo newUserInfo, boolean bEnableRetry) throws SQLException, UseridIDExistsException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            if(userDB.getUserFromUserid(con, newUserInfo.userid) != null){
                throw new UseridIDExistsException("Insert new user failed: username '" + newUserInfo.userid + "' already exists");
            }
            KeyValue key = userDB.insertUserInfo(con, conFactory.getKeyFactory(), newUserInfo);
            con.commit();
            return key;
        } catch(Throwable e){
            lg.error("failure in insertUserInfo()", e);
            if(e instanceof UseridIDExistsException){
                throw (UseridIDExistsException) e;
            }
            try {
                con.rollback();
            } catch(Throwable rbe){
                lg.error("exception during rollback, bEnableRetry = " + bEnableRetry, rbe);
            }
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return insertUserInfo(newUserInfo, false);
            } else {
                handle_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    public void updateSimulationJobStatus(SimulationJobStatusPersistent newSimulationJobStatus, boolean bEnableRetry) throws SQLException, DataAccessException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            updateSimulationJobStatus(con, newSimulationJobStatus);
            con.commit();
        } catch(Throwable e){
            lg.error("failure in updateSimulationJobStatus()", e);
            try {
                con.rollback();
            } catch(Throwable rbe){
                lg.error("exception during rollback, bEnableRetry = " + bEnableRetry, rbe);
            }
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                updateSimulationJobStatus(newSimulationJobStatus, false);
            } else {
                handle_DataAccessException_SQLException(e);
            }
        } finally {
            conFactory.release(con, lock);
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (10/6/2005 3:20:41 PM)
     */
    void updateSimulationJobStatus(Connection con, SimulationJobStatusPersistent newSimulationJobStatus) throws SQLException, UpdateSynchronizationException{
        jobDB.updateSimulationJobStatus(con, newSimulationJobStatus);
        VCMongoMessage.sendSimJobStatusUpdate(newSimulationJobStatus);
    }


    /**
     * This method was created in VisualAge.
     *
     * @param newUserInfo cbit.sql.UserInfo
     * @return cbit.sql.UserInfo
     */
    KeyValue updateUserInfo(UserInfo newUserInfo, boolean bEnableRetry) throws SQLException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            userDB.updateUserInfo(con, newUserInfo);
            con.commit();
            return newUserInfo.id;
        } catch(Throwable e){
            lg.error("failure in updateUserInfo()", e);
            try {
                con.rollback();
            } catch(Throwable rbe){
                lg.error("exception during rollback, bEnableRetry = " + bEnableRetry, rbe);
            }
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return updateUserInfo(newUserInfo, false);
            } else {
                handle_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    void updateUserStat(UserLoginInfo userLoginInfo, boolean bEnableRetry) throws SQLException{

        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            userDB.updateUserStat(con, conFactory.getKeyFactory(), userLoginInfo);
            con.commit();
        } catch(Throwable e){
            lg.error("failure in updateUserStat()", e);
            try {
                con.rollback();
            } catch(Throwable rbe){
                lg.error("exception during rollback, bEnableRetry = " + bEnableRetry, rbe);
            }
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                updateUserStat(userLoginInfo, false);
            } else {
                handle_SQLException(e);
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

    public KeyValue[] getSimulationKeysFromBiomodel(KeyValue biomodelKey, boolean bEnableRetry) throws SQLException, DataAccessException{
        BioModelDbDriver bioModelDbDriver = new BioModelDbDriver(conFactory.getDatabaseSyntax(), conFactory.getKeyFactory());
        Object lock = new Object();
        Connection con = conFactory.getConnection(lock);
        try {
            return bioModelDbDriver.getSimulationEntriesFromBioModel(con, biomodelKey);
        } catch(Throwable e){
            lg.error("failure in getSimulationKeysFromBiomodel()", e);
            if(bEnableRetry && isBadConnection(con)){
                conFactory.failed(con, lock);
                return getSimulationKeysFromBiomodel(biomodelKey, false);
            } else {
                handle_DataAccessException_SQLException(e);
                return null; // never gets here;
            }
        } finally {
            conFactory.release(con, lock);
        }
    }

}
