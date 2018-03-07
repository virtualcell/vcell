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
import java.util.Map;

import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

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

		public WaitingJob(User user, int numRunningPDEs, int numRunningODEs, long waitingTimeStamp, SimulationJobStatus simJobStatus, SimulationRequirements simRequirements) {
			this.user = user;
			this.numRunningPDEs = numRunningPDEs;
			this.numRunningODEs = numRunningODEs;
			this.waitingTimeStamp = waitingTimeStamp;
			this.simJobStatus = simJobStatus;
			this.simRequirements = simRequirements;
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

public static int getMaxJobsPerSite() {
	return Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.maxJobsPerSite));
}


/**
 * Insert the method's description here.
 * Creation date: (5/11/2006 9:32:58 AM)
 */
public static WaitingJob[] schedule(SimulationJobStatus[] activeJobsThisSite, Map<KeyValue,SimulationRequirements> simulationRequirementsMap, int siteJobQuota, int userQuotaOde, int userQuotaPde, VCellServerID systemID, SessionLog log) {
	Hashtable<User, Integer> userPdeRunningJobsThisSite = new Hashtable<User, Integer>();
	Hashtable<User, Integer> userOdeRunningJobsThisSite = new Hashtable<User, Integer>();
	

	cbit.vcell.server.SimulationJobStatus jobStatus = null;
	int numRunningJobsThisSite = 0;
	for (int i = 0; i < activeJobsThisSite.length; i++){
		jobStatus = activeJobsThisSite[i];

		if (!jobStatus.getSchedulerStatus().isActive()) {
			continue;
		}
		
		if (jobStatus.getSchedulerStatus().isWaiting()) {
			continue;  // we only do statistics on running jobs;
		}
		
		numRunningJobsThisSite++;
		
		if (jobStatus.getServerID().equals(systemID)) { // the number of running jobs on this site
			User user = activeJobsThisSite[i].getVCSimulationIdentifier().getOwner();
			SimulationRequirements simRequirements = simulationRequirementsMap.get(jobStatus.getVCSimulationIdentifier().getSimulationKey());
			if(simRequirements!=null && simRequirements.isPDE()) {
				Integer numUserPdeJobs = userPdeRunningJobsThisSite.get(user);
				if (numUserPdeJobs == null) {
					userPdeRunningJobsThisSite.put(user, 1);
				} else {
					userPdeRunningJobsThisSite.put(user, numUserPdeJobs.intValue() + 1);
				}
			} else {
				Integer numUserOdeJobs = userOdeRunningJobsThisSite.get(user);
				if (numUserOdeJobs == null) {
					userOdeRunningJobsThisSite.put(user, 1);
				} else {
					userOdeRunningJobsThisSite.put(user, numUserOdeJobs.intValue() + 1);
				}
			}
		}
	}
	ArrayList<WaitingJob> waitingJobs = new ArrayList<WaitingJob>();
	for (int i = 0; i < activeJobsThisSite.length; i++){
		jobStatus = activeJobsThisSite[i];
			
		if (!jobStatus.getSchedulerStatus().isWaiting()) {
			continue; // ignore non-waiting job
		}
		if (!jobStatus.getServerID().equals(systemID)) {
			continue; // doesn't belong
		}

		User user = activeJobsThisSite[i].getVCSimulationIdentifier().getOwner();
		Integer numRunningPDEsThisSite = userPdeRunningJobsThisSite.get(user);
		if (numRunningPDEsThisSite==null){
			numRunningPDEsThisSite = new Integer(0);
		}
		Integer numRunningODEsThisSite = userOdeRunningJobsThisSite.get(user);
		if (numRunningODEsThisSite == null){
			numRunningODEsThisSite = new Integer(0);
		}
		long waitingTimeStamp = jobStatus.getSimulationQueueEntryStatus().getQueueDate().getTime();
		
		KeyValue simKey = jobStatus.getVCSimulationIdentifier().getSimulationKey();
		waitingJobs.add(new WaitingJob(user, numRunningPDEsThisSite, numRunningODEsThisSite, waitingTimeStamp, jobStatus, simulationRequirementsMap.get(simKey)));
	}

	Collections.sort(waitingJobs,new Comparator<WaitingJob>(){

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
	// enforce quota for each user
	//
	HashSet<User> users = new HashSet<User>();
	users.addAll(userPdeRunningJobsThisSite.keySet());
	users.addAll(userOdeRunningJobsThisSite.keySet());
	for (User user : users){
		Integer numRunningPDEsThisSite = userPdeRunningJobsThisSite.get(user);
		int numRunningPDEs = 0;
		if (numRunningPDEsThisSite!=null){
			numRunningPDEs = numRunningPDEsThisSite;
		}
		Integer numRunningODEsThisSite = userOdeRunningJobsThisSite.get(user);
		int numRunningODEs = 0;
		if (numRunningODEsThisSite != null){
			numRunningODEs = numRunningODEsThisSite;
		}
		
		//
		// go full list and remove any jobs that would exceed this users quota
		//
		Iterator<WaitingJob> waitingJobIter = waitingJobs.iterator();
		while (waitingJobIter.hasNext()){
			WaitingJob waitingJob = waitingJobIter.next();
			if (waitingJob.user.equals(user)){
				if (waitingJob.simRequirements.isPDE()){
					if (numRunningPDEs < userQuotaPde){
						numRunningPDEs++;
					}else{
						waitingJobIter.remove();
					}
				}else{
					if (numRunningODEs < userQuotaOde){
						numRunningODEs++;
					}else{
						waitingJobIter.remove();
					}
				}
			}
		}
	}
	//
	// enforce site quota (keep only first N jobs) where currentRunning + N <= quota
	//
	int numJobsSlotsAvailable = Math.max(0, siteJobQuota - numRunningJobsThisSite);
	int numJobsEligible = waitingJobs.size();
	int numJobsToDispatch = Math.min(numJobsSlotsAvailable,numJobsEligible);
	if (numJobsToDispatch == 0){
		return new WaitingJob[0];
	}else{
		return waitingJobs.subList(0, numJobsToDispatch).toArray(new WaitingJob[0]);
	}
}
}
