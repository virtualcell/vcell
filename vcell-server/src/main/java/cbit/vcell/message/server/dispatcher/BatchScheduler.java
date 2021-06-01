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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.htc.HtcProxy.PartitionStatistics;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;

/**
 * Insert the type's description here.
 * Creation date: (5/11/2006 9:32:17 AM)
 * @author: Jim Schaff
 */
public class BatchScheduler {
	
	static Logger lg = LogManager.getLogger(BatchScheduler.class);
	
	public static class ActiveJob {
		public final User simulationOwner;
		public final SchedulerStatus schedulerStatus;
		public final long submitTimestamp;
		public final VCellServerID serverId;
		public final boolean isPDE;

		public final Object jobObject; // to externally associate this job

		
		public ActiveJob(SimulationJobStatus simJobStatus, boolean isPDE) {
			this.jobObject = simJobStatus;
			this.simulationOwner = simJobStatus.getVCSimulationIdentifier().getOwner();
			this.schedulerStatus = simJobStatus.getSchedulerStatus();
			this.submitTimestamp = simJobStatus.getSubmitDate().getTime();
			this.serverId = simJobStatus.getServerID();
			this.isPDE = isPDE;
		}
		
		
		public ActiveJob(Object jobObject, User simulationOwner, SchedulerStatus schedulerStatus,
				long queueTimestamp, VCellServerID serverId, boolean isPDE) {
			super();
			this.jobObject = jobObject;
			this.simulationOwner = simulationOwner;
			this.schedulerStatus = schedulerStatus;
			this.submitTimestamp = queueTimestamp;
			this.serverId = serverId;
			this.isPDE = isPDE;
		}
	}
	
	public static class UserQuotaInfo {
		public final User user;
		public int numPdeRunningJobsAllSites = 0;
		public int numOdeRunningJobsAllSites = 0;
		
		public UserQuotaInfo(User user) {
			this.user = user;
		}
		
		public int getNumRunningJobs() {
			return numPdeRunningJobsAllSites+numOdeRunningJobsAllSites;
		}
	}

	enum SchedulerDecisionType {
		UNKNOWN,
		INACTIVE_JOB,
		ALREADY_RUNNING_OR_QUEUED,
		
		HELD_USER_QUOTA_ODE,
		HELD_USER_QUOTA_PDE,
		HELD_CLUSTER_RESOURCES,
		
		RUNNABLE_OTHER_SITE,
		
		RUNNABLE_THIS_SITE;
		
		boolean isRequested() {
			return this!=INACTIVE_JOB && this!=ALREADY_RUNNING_OR_QUEUED;
		}
	}
	
	public static class SchedulerDecisions {
		private Map<ActiveJob,SchedulerDecisionType> decisionTypeMap = new HashMap<ActiveJob,SchedulerDecisionType>();
		private Map<ActiveJob,Integer> ordinalMap = new HashMap<ActiveJob,Integer>();
		
		public SchedulerDecisions(List<ActiveJob> activeJobsAllSites) {
			for (ActiveJob activeJob : activeJobsAllSites) {
				this.decisionTypeMap.put(activeJob,SchedulerDecisionType.UNKNOWN);
				this.ordinalMap.put(activeJob, new Integer(-1));
			}
		}
		
		public SchedulerDecisionType getDecisionType(ActiveJob activeJob) {
			return decisionTypeMap.get(activeJob);
		}

		public Integer getOrdinal(ActiveJob activeJob) {
			return ordinalMap.get(activeJob);
		}
		
		public List<ActiveJob> getRunnableThisSite() {
			ArrayList<ActiveJob> runnableThisSite = new ArrayList<ActiveJob>();
			for (ActiveJob job : decisionTypeMap.keySet()) {
				if (decisionTypeMap.get(job) == SchedulerDecisionType.RUNNABLE_THIS_SITE) {
					runnableThisSite.add(job);
				}
			}
			return runnableThisSite;
		}


		private void setInactive(ActiveJob activeJob) {
			this.decisionTypeMap.put(activeJob,SchedulerDecisionType.INACTIVE_JOB);
		}

		private void setAlreadyRunningOrQueued(ActiveJob activeJob) {
			decisionTypeMap.put(activeJob, SchedulerDecisionType.ALREADY_RUNNING_OR_QUEUED);
		}

		private void setOrdinal(ActiveJob activeJob, int index) {
			ordinalMap.put(activeJob, index);
		}

		private void setHeldUserQuotaPDE(ActiveJob waitingJob) {
			decisionTypeMap.put(waitingJob, SchedulerDecisionType.HELD_USER_QUOTA_PDE);
		}

		private void setHeldUserQuotaODE(ActiveJob waitingJob) {
			 decisionTypeMap.put(waitingJob, SchedulerDecisionType.HELD_USER_QUOTA_ODE);
		}

		private void setHeldClusterResources(ActiveJob waitingJob) {
			decisionTypeMap.put(waitingJob, SchedulerDecisionType.HELD_CLUSTER_RESOURCES);
		}

		private void setRunnableThisSite(ActiveJob waitingJob) {
			decisionTypeMap.put(waitingJob, SchedulerDecisionType.RUNNABLE_THIS_SITE);
		}
		
		private void setRunnableOtherSite(ActiveJob waitingJob) {
			decisionTypeMap.put(waitingJob, SchedulerDecisionType.RUNNABLE_OTHER_SITE);
		}
		
		private void verify(PartitionStatistics partitionStatistics) {
			// look for inconsistent decisions
			for (ActiveJob activeJob : decisionTypeMap.keySet()) {
				if (decisionTypeMap.get(activeJob) == SchedulerDecisionType.UNKNOWN) {
					lg.error("activeJob("+activeJob.jobObject+") undecided");
				}
			}
			if (lg.isTraceEnabled()) {
				for (ActiveJob activeJob : decisionTypeMap.keySet()) {
					lg.trace(getDecisionDesc(activeJob));
				}
			}
		}

		public void show() {
			for (ActiveJob activeJob : decisionTypeMap.keySet()) {
				System.out.println(getDecisionDesc(activeJob));
			}
		}
		
		public String getDecisionDesc(ActiveJob activeJob) {
			SchedulerDecisionType schedulerDecisionType = decisionTypeMap.get(activeJob);
			Integer ordinal = ordinalMap.get(activeJob);
			String jobType = activeJob.isPDE?"pde":"ode";
			return "activeJob("+activeJob.jobObject+"): user="+activeJob.simulationOwner.getName()+", site="+activeJob.serverId+", status="+activeJob.schedulerStatus+", time="+activeJob.submitTimestamp+", type="+jobType+", globalOrdinal="+ordinal+", decision="+schedulerDecisionType;
		}
	}
	

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
public static SchedulerDecisions schedule(List<ActiveJob> activeJobsAllSites, PartitionStatistics partitionStatistics, int userQuotaOde, int userQuotaPde, VCellServerID systemID,User[] quotaExemptUsers) {
	Hashtable<User, UserQuotaInfo> userQuotaInfoMap = new Hashtable<User, UserQuotaInfo>();
	//
	// gather statistics about all currently active jobs across all sites (per user and aggregate).
	//
	int numPendingJobsAllSites = 0;
	for (ActiveJob activeJob : activeJobsAllSites) {

		UserQuotaInfo userQuotaInfo = userQuotaInfoMap.get(activeJob.simulationOwner);
		if (userQuotaInfo==null) {
			userQuotaInfo = new UserQuotaInfo(activeJob.simulationOwner);
			userQuotaInfoMap.put(activeJob.simulationOwner, userQuotaInfo);
		}

		if (!activeJob.schedulerStatus.isActive()) {
			continue;
		}
		
		if (activeJob.schedulerStatus.isWaiting()) {
			continue;  // we only do statistics on running jobs;
		}
		
		if (activeJob.schedulerStatus.isDispatched() || activeJob.schedulerStatus.isQueued()) {
			numPendingJobsAllSites++;
		}
		
		if(activeJob.isPDE) {
			userQuotaInfo.numPdeRunningJobsAllSites++;
		} else {
			userQuotaInfo.numOdeRunningJobsAllSites++;
		}
	}
	
	//
	// gather all waiting jobs (all sites)
	//
	SchedulerDecisions schedulerDecisions = new SchedulerDecisions(activeJobsAllSites);
	ArrayList<ActiveJob> prioritizedJobList = new ArrayList<ActiveJob>(activeJobsAllSites);
	
	for (ActiveJob activeJob : activeJobsAllSites) {

		if (!activeJob.schedulerStatus.isActive()) {
			schedulerDecisions.setInactive(activeJob);
			prioritizedJobList.remove(activeJob);
			continue;
		}

		if (!activeJob.schedulerStatus.isWaiting()) {
			schedulerDecisions.setAlreadyRunningOrQueued(activeJob);
			prioritizedJobList.remove(activeJob);
			continue;
		}
	}

	//
	// sort requested jobs according to priority
	//
	Collections.sort(prioritizedJobList,new Comparator<ActiveJob>(){

		@Override
		public int compare(ActiveJob o1, ActiveJob o2) {
			//
			// user with fewer jobs running should take precedence
			//
			UserQuotaInfo userQuotaInfo1 = userQuotaInfoMap.get(o1.simulationOwner);
			UserQuotaInfo userQuotaInfo2 = userQuotaInfoMap.get(o2.simulationOwner);
			if (userQuotaInfo1.getNumRunningJobs() != userQuotaInfo2.getNumRunningJobs()){
				return Integer.compare(userQuotaInfo1.getNumRunningJobs(), userQuotaInfo2.getNumRunningJobs());
			}
			//
			// ODEs take precedence over PDEs (they should be faster)
			//
			if (o1.isPDE != o2.isPDE){
				if (o1.isPDE){
					return 1;
				}else{
					return -1;
				}
			}
			//
			// both are odes or both are pdes ... sort by waiting time
			//
			return Long.compare(o1.submitTimestamp, o2.submitTimestamp);
		}
		
	});
	
	//
	// set the job priority (ineligible jobs keep an ordinal of -1)
	//
	int index=0;
	for (ActiveJob activeJob : prioritizedJobList) {
		schedulerDecisions.setOrdinal(activeJob, index);
		index++;
	}
	
	
	//
	// enforce quota for each user (remove jobs which exceed user quotas)
	//
	HashSet<User> users = new HashSet<User>();
	users.addAll(userQuotaInfoMap.keySet());
	for (User user : users){
		UserQuotaInfo userQuotaInfo = userQuotaInfoMap.get(user);
		int numDesiredRunningPDEsAllSites = userQuotaInfo.numPdeRunningJobsAllSites;
		int numDesiredRunningODEsAllSites = userQuotaInfo.numOdeRunningJobsAllSites;
		
		//
		// go full list and remove any jobs that would exceed this users quota
		//
		Iterator<ActiveJob> prioritizedJobIter = prioritizedJobList.iterator();
		while (prioritizedJobIter.hasNext()){
			ActiveJob waitingJob = prioritizedJobIter.next();
			if (waitingJob.simulationOwner.equals(user)){
				if (waitingJob.isPDE){
					//(quotaExemptUsers==null?(userQuotaPde:Arrays.asList(quotaExemptUsers).contains(user)?Integer.MAX_VALUE:userQuotaPde)
					if (numDesiredRunningPDEsAllSites < (quotaExemptUsers==null?userQuotaPde:(Arrays.asList(quotaExemptUsers).contains(user)?Integer.MAX_VALUE:userQuotaPde))){
						numDesiredRunningPDEsAllSites++;
					}else{
						schedulerDecisions.setHeldUserQuotaPDE(waitingJob);
						prioritizedJobIter.remove();
					}
				}else{
					if (numDesiredRunningODEsAllSites < (quotaExemptUsers==null?userQuotaOde:(Arrays.asList(quotaExemptUsers).contains(user)?Integer.MAX_VALUE:userQuotaOde))){
						numDesiredRunningODEsAllSites++;
					}else{
						schedulerDecisions.setHeldUserQuotaODE(waitingJob);
						prioritizedJobIter.remove();
					}
				}
			}
		}
	}
	//
	// each time allocate 50% of available estimated job slots.
	// assume that each job takes 2 cpus (don't want to wait on Slurm queue)
	//
	//int inUseCPUs = Math.max(partitionStatistics.numCpusAllocated, (int)Math.ceil(partitionStatistics.load));
	int inUseCPUs = partitionStatistics.numCpusAllocated;
	int cpusAvailable = Math.max(0, partitionStatistics.numCpusTotal - inUseCPUs);
	int numJobsSlotsAvailable = Math.max(0, cpusAvailable - numPendingJobsAllSites);
	
	for (int i=0;i<prioritizedJobList.size();i++) {
		ActiveJob nextWaitingJob = prioritizedJobList.get(i);
		if (i<numJobsSlotsAvailable) {
			//
			// trim global prioritized list of jobs to be submitted across all sites.
			// only submit the jobs (of the N global jobs) which are from this site.
			// the other sites will submit their own jobs in time.
			//
			if (nextWaitingJob.serverId.equals(systemID)){
				schedulerDecisions.setRunnableThisSite(nextWaitingJob);
			}else {
				schedulerDecisions.setRunnableOtherSite(nextWaitingJob);
			}
		}else {
			schedulerDecisions.setHeldClusterResources(nextWaitingJob);
		}
	}

	schedulerDecisions.verify(partitionStatistics);

	return schedulerDecisions;
}

}
