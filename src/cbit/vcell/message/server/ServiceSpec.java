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

import static cbit.vcell.message.server.ManageConstants.SERVICE_STARTUP_TYPES;

import java.io.Serializable;

import org.vcell.util.ComparableObject;
import org.vcell.util.Matchable;
import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.document.VCellServerID;



public class ServiceSpec implements Matchable, Serializable, ComparableObject {
	private VCellServerID serverID;
	private ServiceType type;
	private int ordinal;
	private int startupType;
	private int memoryMB;	
	
	public ServiceSpec(VCellServerID sID, ServiceType t, int o, int st, int mm) {
		super();
		this.serverID = sID;
		this.type = t;
		this.ordinal = o;
		this.startupType = st;
		this.memoryMB = mm;
	}
	
	public int getMemoryMB() {
		return memoryMB;
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
		return "[" + serverID + "," + type + "," + ordinal + "," + ManageConstants.SERVICE_STARTUP_TYPES[startupType] + "," + memoryMB + "M]";
	}

	public int getStartupType() {
		return startupType;
	}

	public String getID() {
		return getServiceID(serverID, type, ordinal);
	}
	public static String getServiceID(VCellServerID serverID, ServiceType type, int ordinal) {
		return serverID + "_" + type.getName() + "_" + ordinal;
	}
	
	public Object[] toObjects() {
		return new Object[] {serverID, type, ordinal, SERVICE_STARTUP_TYPES[startupType], memoryMB};
	}
		
	public boolean equals(Object obj) {
		if (obj instanceof Matchable) {
			return compareEqual((Matchable)obj);
		}
		return false;
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ServiceSpec) {
			ServiceSpec ss = (ServiceSpec)obj;
		
			if (!serverID.equals(ss.serverID)) {
				return false;
			}
			if (!type.equals(ss.type)) {
				return false;
			}
			if (ordinal != ss.ordinal) {
				return false;
			}			
			return true;
		}		
		return false;
	}
}
