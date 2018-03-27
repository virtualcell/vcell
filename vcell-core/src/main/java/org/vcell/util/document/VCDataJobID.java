/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

import java.io.Serializable;
import java.util.Random;


@SuppressWarnings("serial")
public class VCDataJobID implements Serializable{

	private long jobID;
	private User jobOwner;
	private boolean isBackgroundTask;
	
	public VCDataJobID(long jobID, User jobOwner, boolean isBackgroundTask) {
		this.jobID = jobID;
		this.jobOwner = jobOwner;
		this.isBackgroundTask = isBackgroundTask;
	}

	public static VCDataJobID createVCDataJobID(User argJobOwner,boolean argIsBackgroundTask){
		return new VCDataJobID(new Random().nextLong(),argJobOwner,argIsBackgroundTask);
	}
	public boolean isBackgroundTask() {
		return isBackgroundTask;
	}

	public long getJobID() {
		return jobID;
	}

	public User getJobOwner() {
		return jobOwner;
	}

	@Override
	public String toString() {
		return "own="+jobOwner.toString()+":id="+jobID+":bg="+isBackgroundTask;
	}

	@Override
	public boolean equals(Object obj) {
		return
			(obj instanceof VCDataJobID)
			&&
			getJobID() == ((VCDataJobID)obj).getJobID();
	}

	@Override
	public int hashCode() {
		return new Long(jobID).hashCode();
	}
}
