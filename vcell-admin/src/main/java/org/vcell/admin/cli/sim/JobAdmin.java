/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.admin.cli.sim;

import cbit.vcell.message.server.dispatcher.BatchScheduler;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.server.SimulationJobStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;
import org.vcell.util.exe.ExecutableException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


/**
 * (1) Query database for simulations which satisfy criteria (included user, active)
 * (2) Display job status with SchedulerDecision for active jobs
 */
public class JobAdmin {
    private static final Logger lg = LogManager.getLogger(JobAdmin.class);

    private final AdminDBTopLevel adminDbTopLevel;
    private final DatabaseServerImpl dbServerImpl;
    private final SimulationDatabase simulationDatabase;

    public JobAdmin(AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl dbServerImpl, SimulationDatabase simulationDatabase) {
        this.adminDbTopLevel = adminDbTopLevel;
        this.dbServerImpl = dbServerImpl;
        this.simulationDatabase = simulationDatabase;
    }

    public void run(HtcProxy.PartitionStatistics partitionStatistics, int userQuotaOde, int userQuotaPde,
                    VCellServerID serverID, User[] quotaExcemptUsers,
                    String username, KeyValue simID)
            throws ExecutableException, HtcException, IOException, SQLException, DataAccessException {

        final SimulationJobStatus[] allActiveJobsAllSites = simulationDatabase.getActiveJobs(null);
        if (allActiveJobsAllSites == null) {
            throw new RuntimeException("simulationDatabase.getActiveJobs() returned null - no jobs?");
        }
        Set<KeyValue> simKeys = new LinkedHashSet<KeyValue>(); //Linked hash set maintains insertion order
        for (SimulationJobStatus simJobStatus : allActiveJobsAllSites) {
            KeyValue simKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey();
            if (!simKeys.contains(simKey)) {
                simKeys.add(simKey);
            }
        }
        final Map<KeyValue, SimulationRequirements> simulationRequirementsMap = simulationDatabase.getSimulationRequirements(simKeys);
        ArrayList<BatchScheduler.ActiveJob> activeJobs = new ArrayList<BatchScheduler.ActiveJob>();
        for (SimulationJobStatus simJobStatus : allActiveJobsAllSites) {
            SimulationRequirements simulationRequirements = simulationRequirementsMap.get(simJobStatus.getVCSimulationIdentifier().getSimulationKey());
            boolean isPDE = (simulationRequirements != null) ? (simulationRequirements.isPDE()) : (false);
            BatchScheduler.ActiveJob activeJob = new BatchScheduler.ActiveJob(simJobStatus, isPDE);
            activeJobs.add(activeJob);
        }

        BatchScheduler.SchedulerDecisions decisons = BatchScheduler.schedule(
                activeJobs, partitionStatistics, userQuotaOde, userQuotaPde, serverID, quotaExcemptUsers);

        decisons.show(username, simID);

        System.out.println("Slurm Partition Statistics: " +
                "CPUs total="+partitionStatistics.numCpusTotal+", " +
                "CPUs allocated="+partitionStatistics.numCpusAllocated+", " +
                "load="+partitionStatistics.load);
    }

    public User[] getQuotaExemptUsers() throws SQLException, DataAccessException {
        ArrayList<User> adminUserList = new ArrayList<User>();
        TreeMap<User.SPECIAL_CLAIM, TreeMap<User,String>> specialUsers = simulationDatabase.getSpecialUsers();
        final Iterator<User.SPECIAL_CLAIM> iterator = specialUsers.keySet().iterator();
        while(iterator.hasNext()) {
            final User.SPECIAL_CLAIM next = iterator.next();
            if(next == User.SPECIAL_CLAIM.admins) {//Admin Users
                final Iterator<User> iter = specialUsers.get(next).keySet().iterator();
                while(iter.hasNext()) {
                    adminUserList.add(iter.next());
                }
                break;
            }
        }
        User[] quotaExemptUsers = adminUserList.toArray(new User[0]);
        return quotaExemptUsers;
    }

}
