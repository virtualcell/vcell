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

public class ServiceStatus implements ComparableObject, Matchable, Serializable {
	private ServiceSpec serviceSpec = null;
	private Date date = null;
	private ServiceStatusType serviceStatusType;
	private String statusMsg;
	private HtcJobID htcJobId;
	
	//
	//	public static final int SERVICE_STATUS_RUNNING = 0;	// restart it if the service is dead 
	//	public static final int SERVICE_STATUS_NOTRUNNING = 1;	// restart it if the service is dead
	//	public static final int SERVICE_STATUS_FAILED = 2; 	
	//
	//	public static final String[] SERVICE_STATUSES = {"running", "not running", "failed"};
	//

	public enum ServiceStatusType {
		ServiceRunning(0,"running"),
		ServiceNotRunning(1,"not running"),
		ServiceFailed(2,"failed");
		
		final int databaseNumber;
		final String description;
		
		private ServiceStatusType(int databaseNumber, String description){
			this.databaseNumber = databaseNumber;
			this.description = description;
		}
		
		public int getDatabaseNumber(){
			return databaseNumber;
		}
		public String getDescription(){
			return description;
		}
	
		public boolean isRunning() {
			return this == ServiceRunning;
		}
		public boolean isNotRunning() {
			return this == ServiceNotRunning;
		}
		public boolean isFailed() {
			return this == ServiceFailed;
		}
	
		public static ServiceStatusType fromDatabaseNumber(int databaseType) {
			for (ServiceStatusType type : values()){
				if (type.getDatabaseNumber() == databaseType){
					return type;
				}
			}
			throw new RuntimeException("unknown database serialization for ServiceStatusType = "+databaseType);
		}
	}	
	
	public ServiceStatus(ServiceSpec ss, Date d, ServiceStatusType serviceStatusType, String sm, HtcJobID htcJobID) {
		super();
		this.serviceSpec = ss;
		this.date = d;
		this.serviceStatusType = serviceStatusType;
		this.statusMsg = sm;
		this.htcJobId = htcJobID;
	}

	public Date getDate() {
		return date;
	}

	public ServiceSpec getServiceSpec() {
		return serviceSpec;
	}

	public ServiceStatusType getStatus() {
		return this.serviceStatusType;
	}

	public String getStatusMsg() {
		return statusMsg;
	}
	
	public Object[] toObjects(){
		return new Object[]{serviceSpec.getServerID(), serviceSpec.getType(), serviceSpec.getOrdinal(), 
				serviceSpec.getStartupType().getDescription(), serviceSpec.getMemoryMB(), date, serviceStatusType.getDescription(), statusMsg, htcJobId};		
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
			if (serviceStatusType != ss.serviceStatusType) {
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
