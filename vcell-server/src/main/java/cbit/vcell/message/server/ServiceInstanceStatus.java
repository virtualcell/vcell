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
import org.vcell.util.Matchable;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.bootstrap.ServiceType;




public class ServiceInstanceStatus implements Matchable, Serializable, ComparableObject {
	private VCellServerID serverID;
	private ServiceType type;
	private int ordinal;
	private Date startDate;
	private String hostname;	
	private boolean bRunning = false;
		
	public ServiceInstanceStatus(VCellServerID sID, ServiceType t, int o, String h, Date d, boolean br) {
		super();
		this.serverID = sID;
		this.type = t;
		this.ordinal = o;
		hostname = h;
		startDate = d;
		this.bRunning = br;
	}
	
	public int getOrdinal() {
		return ordinal;
	}

	public VCellServerID getServerID() {
		return serverID;
	}

	public ServiceType getType() {
		return type;
	}
	
	public String toString() {
		return "[" + serverID + "," + type + "," + ordinal + "," + "," + hostname + "," + startDate + "," + bRunning + "]";
	}

	public String getID() {
		return getSpecID() + "_" + hostname + "_" + startDate.getTime();
	}
	
	public String getSpecID() {
		return ServiceSpec.getServiceID(serverID, type, ordinal);
	}
	
	public Object[] toObjects() {
		return new Object[] {serverID, type, ordinal, hostname, startDate, bRunning};
	}
		
	public boolean equals(Object obj) {
		if (obj instanceof Matchable) {
			return compareEqual((Matchable)obj);
		}
		return false;
	}
	
	public int hashCode() {
		return getID().hashCode();
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ServiceInstanceStatus) {
			ServiceInstanceStatus ss = (ServiceInstanceStatus)obj;
		
			if (!serverID.equals(ss.serverID)) {
				return false;
			}
			if (!type.equals(ss.type)) {
				return false;
			}
			if (ordinal != ss.ordinal) {
				return false;
			}		
			if (!hostname.equals(ss.hostname)) {
				return false;
			}
			if (!startDate.equals(ss.startDate)) {
				return false;
			}
			return true;
		}		
		return false;
	}

	public boolean isRunning() {
		return bRunning;
	}

	public void setRunning(boolean running) {
		bRunning = running;
	}

	public Date getStartDate() {
		return startDate;
	}
}
