/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server;

import java.io.Serializable;
import java.util.Date;

import org.vcell.util.ComparableObject;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.message.server.htc.HtcJobID;

import static cbit.vcell.message.server.ManageConstants.*;

public class ServiceStatus implements ComparableObject, Matchable, Serializable {
	private ServiceSpec serviceSpec = null;
	private Date date = null;
	private int status;
	private String statusMsg;
	private HtcJobID htcJobId;
	
	public ServiceStatus(ServiceSpec ss, Date d, int s, String sm, HtcJobID htcJobID) {
		super();
		this.serviceSpec = ss;
		this.date = d;
		this.status = s;
		this.statusMsg = sm;
		this.htcJobId = htcJobID;
	}

	public Date getDate() {
		return date;
	}

	public ServiceSpec getServiceSpec() {
		return serviceSpec;
	}

	public int getStatus() {
		return status;
	}

	public String getStatusMsg() {
		return statusMsg;
	}
	
	public boolean isRunning() {
		return status == ManageConstants.SERVICE_STATUS_RUNNING;
	}
	public Object[] toObjects(){
		return new Object[]{serviceSpec.getServerID(), serviceSpec.getType(), serviceSpec.getOrdinal(), 
				SERVICE_STARTUP_TYPES[serviceSpec.getStartupType()], serviceSpec.getMemoryMB(), date, SERVICE_STATUSES[status], statusMsg, htcJobId};		
	}

	public boolean equals(Object obj) {
		if (obj instanceof Matchable) {
			return compareEqual((Matchable)obj);
		}
		return false;
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ServiceStatus) {
			ServiceStatus ss = (ServiceStatus)obj;
		
			if (!serviceSpec.compareEqual(ss.serviceSpec)) {
				return false;
			}
			if (!date.equals(ss.date)) {
				return false;
			}
			if (status != ss.status) {
				return false;
			}
			if (!statusMsg.equals(ss.statusMsg)) {
				return false;
			}
			if (!Compare.isEqualOrNull(htcJobId, ss.htcJobId)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public HtcJobID getHtcJobId() {
		return htcJobId;
	}
	
	public String toString() {
		return serviceSpec.toString();
	}
}
