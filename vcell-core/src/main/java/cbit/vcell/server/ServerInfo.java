/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

import org.vcell.util.CacheStatus;
import org.vcell.util.document.User;

/**
 * Insert the type's description here.
 * Creation date: (12/9/2002 12:44:13 AM)
 * @author: Jim Schaff
 */
public class ServerInfo implements java.io.Serializable {

	public static final int VCELL_SLURM_LANGEVIN_TIMEOUTPERTASKSECONDS = 1728000;
	public static final int VCELL_SLURM_LANGEVIN_BATCHMEMORYLIMITPERTASKMB = 1024;
	public static final int VCELL_SLURM_LANGEVIN_MEMORYBLOCKSIZEMB = 256;
	public static final int VCELL_SLURM_LANGEVIN_MAXNUMCONCURRENTTASKS = 51;	// concurrent simulations + 1 watchdog
	public static final int VCELL_SLURM_LANGEVIN_WATCHDOGTIMEOUTSECONDS = 600;
	public static final int VCELL_SLURM_LANGEVIN_WATCHDOGTICKSECONDS = 30;

	// max number of concurrent jobs per node, to avoid overloading the node
	// TODO: no idea where this number comes from, is it a named property? is it the default?
	public static final double VCELL_SLURM_MAX_JOBS_PER_NODE = 20.0;


	// slurm settings, per-Langevin-task wall-clock timeout = 20 days (20*86400) = 1728000
	// kept just under the 20-23:59:00 job wall SlurmProxy clamps to, so a task that runs
	// long times out with exit 124 (a legible message) rather than being killed by slurm
	private int timeoutPerTaskSeconds = VCELL_SLURM_LANGEVIN_TIMEOUTPERTASKSECONDS;
	private int batchMemoryLimitPerTaskMB = VCELL_SLURM_LANGEVIN_BATCHMEMORYLIMITPERTASKMB;
	private int memoryBlockSizeMB = VCELL_SLURM_LANGEVIN_MEMORYBLOCKSIZEMB;
	private int maxNumConcurrentTasks = VCELL_SLURM_LANGEVIN_MAXNUMCONCURRENTTASKS;
	private int watchdogTimeoutSeconds = VCELL_SLURM_LANGEVIN_WATCHDOGTIMEOUTSECONDS;
	private int watchdogTickSeconds = VCELL_SLURM_LANGEVIN_WATCHDOGTICKSECONDS;

	public ServerInfo() {

	}

	public int getTimeoutPerTaskSeconds() {
		return timeoutPerTaskSeconds;
	}
	public int getBatchMemoryLimitPerTaskMB() {
		return batchMemoryLimitPerTaskMB;
	}
	public int getMemoryBlockSizeMB() {
		return memoryBlockSizeMB;
	}
	public int getMaxNumConcurrentTasks() {
		return maxNumConcurrentTasks;
	}
	public int getWatchdogTimeoutSeconds() {
		return watchdogTimeoutSeconds;
	}
	public int getWatchdogTickSeconds() {
		return watchdogTickSeconds;
	}

}
