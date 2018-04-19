/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.dispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.htc.HtcProxy.PartitionStatistics;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimulationJobStatus;

/**
 * Insert the type's description here.
 * Creation date: (5/11/2006 9:32:17 AM)
 * @author: Jim Schaff
 */
public class BatchScheduler {

	
	public static class WaitingJob {
		public final User user;
		public final Integer numRunningPDEs;
		public final Integer numRunningODEs;
		public final Long waitingTimeStamp;
		public final SimulationJobStatus simJobStatus;
		public final SimulationRequirements simRequirements;
		public final VCellServerID vcellServerId;

		public WaitingJob(User user, int numRunningPDEs, int numRunningODEs, long waitingTimeStamp, SimulationJobStatus simJobStatus, SimulationRequirements simRequirements, VCellServerID vcellServerId) {
			this.user = user;
			this.numRunningPDEs = numRunningPDEs;
			this.numRunningODEs = numRunningODEs;
			this.waitingTimeStamp = waitingTimeStamp;
			this.simJobStatus = simJobStatus;
			this.simRequirements = simRequirements;
			this.vcellServerId = vcellServerId;
		}
		
		public Integer getNumRunningJobs(){
			return new Integer(numRunningODEs+numRunningPDEs);
		}
		
		public String toString() {
			return "WaitingJob("+simRequirements+": waitingTimeStamp="+waitingTimeStamp+", user="+user+", numRunningPde="+numRunningPDEs+", numRunningOdes="+numRunningODEs+", simJobStatus="+simJobStatus+")";
		}
		
	};

/**
 * BatchScheduler constructor comment.
 */
public BatchScheduler() {
	super();
}

public static final int getMaxOdeJobsPerUser() {
	return Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.maxOdeJobsPerUser));
}


public static final int getMaxPdeJobsPerUser() {
	return Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.maxPdeJobsPerUser));
}

//public static int getMaxJobsPerSite() {
//	return Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.maxJobsPerSite));
//}


/**
 * Insert the method's description here.
 * Creation date: (5/11/2006 9:32:58 AM)
 */
public static WaitingJob[] schedule(SimulationJobStatus[] activeJobsAllSites, Map<KeyValue,SimulationRequirements> simulationRequirementsMap, PartitionStatistics partitionStatistics, int userQuotaOde, int userQuotaPde, VCellServerID systemID) {
	Hashtable<User, Integer> userPdeRunningJobsAllSites = new Hashtable<User, Integer>();
	Hashtable<User, Integer> userOdeRunningJobsAllSites = new Hashtable<User, Integer>();

	//
	// gather statistics about all currently active jobs across all sites (per user and aggregate).
	//
	int numPendingJobsAllSites = 0;
	for (SimulationJobStatus jobStatus : activeJobsAllSites) {

		if (!jobStatus.getSchedulerStatus().isActive()) {
			continue;
		}
		
		if (jobStatus.getSchedulerStatus().isWaiting()) {
			continue;  // we only do statistics on running jobs;
		}
		
		if (jobStatus.getSchedulerStatus().isDispatched() || jobStatus.getSchedulerStatus().isQueued()) {
			numPendingJobsAllSites++;
		}
		
		User user = jobStatus.getVCSimulationIdentifier().getOwner();
		SimulationRequirements simRequirements = simulationRequirementsMap.get(jobStatus.getVCSimulationIdentifier().getSimulationKey());
		if(simRequirements!=null && simRequirements.isPDE()) {
			Integer numUserPdeJobs = userPdeRunningJobsAllSites.get(user);
			if (numUserPdeJobs == null) {
				userPdeRunningJobsAllSites.put(user, 1);
			} else {
				userPdeRunningJobsAllSites.put(user, numUserPdeJobs.intValue() + 1);
			}
		} else {
			Integer numUserOdeJobs = userOdeRunningJobsAllSites.get(user);
			if (numUserOdeJobs == null) {
				userOdeRunningJobsAllSites.put(user, 1);
			} else {
				userOdeRunningJobsAllSites.put(user, numUserOdeJobs.intValue() + 1);
			}
		}
	}
	
	//
	// gather all waiting jobs (all sites)
	//
	ArrayList<WaitingJob> waitingJobsAllSites = new ArrayList<WaitingJob>();
	for (SimulationJobStatus jobStatus : activeJobsAllSites) {
			
		if (!jobStatus.getSchedulerStatus().isActive()) {
			continue;
		}

		if (!jobStatus.getSchedulerStatus().isWaiting()) {
			continue; // ignore non-waiting job
		}

		User user = jobStatus.getVCSimulationIdentifier().getOwner();
		long waitingTimeStamp = jobStatus.getSimulationQueueEntryStatus().getQueueDate().getTime();
		
		KeyValue simKey = jobStatus.getVCSimulationIdentifier().getSimulationKey();
		Integer numPdeRunningJobsThisUser = userPdeRunningJobsAllSites.get(user);
		Integer numOdeRunningJobsThisUser = userOdeRunningJobsAllSites.get(user);
		waitingJobsAllSites.add(new WaitingJob(user, (numPdeRunningJobsThisUser!=null)?numPdeRunningJobsThisUser:0, (numOdeRunningJobsThisUser!=null)?numOdeRunningJobsThisUser:0, waitingTimeStamp, jobStatus, simulationRequirementsMap.get(simKey), jobStatus.getServerID()));
	}

	//
	// sort jobs according to priority
	//
	Collections.sort(waitingJobsAllSites,new Comparator<WaitingJob>(){

		@Override
		public int compare(WaitingJob o1, WaitingJob o2) {
			//
			// user with fewer jobs running should take precedence
			//
			if (!o1.getNumRunningJobs().equals(o2.getNumRunningJobs())){
				return o1.getNumRunningJobs().compareTo(o2.getNumRunningJobs());
			}
			//
			// ODEs take precedence over PDEs (they should be faster)
			//
			if (o1.simRequirements.isPDE() != o2.simRequirements.isPDE()){
				if (o1.simRequirements.isPDE()){
					return 1;
				}else{
					return -1;
				}
			}
			//
			// both are odes or both are pdes ... sort by waiting time
			//
			return o1.waitingTimeStamp.compareTo(o2.waitingTimeStamp);
		}
		
	});
	
	//
	// enforce quota for each user (remove jobs which exceed user quotas)
	//
	HashSet<User> users = new HashSet<User>();
	users.addAll(userPdeRunningJobsAllSites.keySet());
	users.addAll(userOdeRunningJobsAllSites.keySet());
	for (User user : users){
		int numRunningPDEsAllSites = 0;
		if (userPdeRunningJobsAllSites.get(user)!=null){
			numRunningPDEsAllSites = userPdeRunningJobsAllSites.get(user);
		}
		int numRunningODEsAllSites = 0;
		if (userOdeRunningJobsAllSites.get(user) != null){
			numRunningODEsAllSites = userOdeRunningJobsAllSites.get(user);
		}
		
		//
		// go full list and remove any jobs that would exceed this users quota
		//
		Iterator<WaitingJob> waitingJobIter = waitingJobsAllSites.iterator();
		while (waitingJobIter.hasNext()){
			WaitingJob waitingJob = waitingJobIter.next();
			if (waitingJob.user.equals(user)){
				if (waitingJob.simRequirements.isPDE()){
					if (numRunningPDEsAllSites < userQuotaPde){
						numRunningPDEsAllSites++;
					}else{
						waitingJobIter.remove();
					}
				}else{
					if (numRunningODEsAllSites < userQuotaOde){
						numRunningODEsAllSites++;
					}else{
						waitingJobIter.remove();
					}
				}
			}
		}
	}
	//
	// each time allocate 50% of available estimated job slots.
	// assume that each job takes 2 cpus (don't want to wait on Slurm queue)
	//
	int inUseCPUs = Math.max(partitionStatistics.numCpusAllocated, (int)Math.ceil(partitionStatistics.load));
	int cpusAvailable = Math.max(0, partitionStatistics.numCpusTotal - inUseCPUs);
	int numJobsSlotsAvailable = Math.max(0, cpusAvailable/2 - numPendingJobsAllSites);
	
	int numJobsEligibleAllSites = waitingJobsAllSites.size();
	int numJobsToDispatchAllSites = Math.min(numJobsSlotsAvailable,numJobsEligibleAllSites);
	if (numJobsToDispatchAllSites == 0){
		return new WaitingJob[0];
	}else{
		//
		// trim global prioritized list of jobs to be submitted across all sites.
		// only submit the jobs (of the N global jobs) which are from this site.
		// the other sites will submit their own jobs in time.
		//
		List<WaitingJob> waitingJobsToRunAllSites = waitingJobsAllSites.subList(0, numJobsToDispatchAllSites);
		ArrayList<WaitingJob> waitingJobsToRunThisSite = new ArrayList<WaitingJob>();
		for (WaitingJob waitingJobToRun : waitingJobsToRunAllSites) {
			if (waitingJobToRun.vcellServerId.equals(systemID)){
				waitingJobsToRunThisSite.add(waitingJobToRun);
			}
		}
		return waitingJobsToRunThisSite.toArray(new WaitingJob[0]);
	}
}
}
