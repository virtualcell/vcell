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

import org.vcell.util.ComparableObject;
import org.vcell.util.Matchable;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.bootstrap.ServiceType;




public class ServiceSpec implements Matchable, Serializable, ComparableObject {
	private VCellServerID serverID;
	private ServiceType type;
	private int ordinal;
	private ServiceStartupType serviceStartupType;
	private int memoryMB;	
	
	
	//
	//	public static final int SERVICE_STARTUPTYPE_AUTOMATIC = 0;	// restart it if the service is dead 
	//	public static final int SERVICE_STARTUPTYPE_MANUAL = 1;
	//	
	//	public static final String[] SERVICE_STARTUP_TYPES = {"automatic", "manual"};
	//
	public enum ServiceStartupType {
		StartupTypeAutomatic(0,"automatic"),
		StartupTypeManual(1,"manual");
		
		final int databaseNumber;
		final String description;
		
		private ServiceStartupType(int databaseNumber, String description){
			this.databaseNumber = databaseNumber;
			this.description = description;
		}
		
		public int getDatabaseNumber(){
			return databaseNumber;
		}
		public String getDescription(){
			return description;
		}
	
		public boolean isAutomatic() {
			return this == StartupTypeAutomatic;
		}
		public boolean isManual() {
			return this == StartupTypeManual;
		}
	
		public static ServiceStartupType fromDatabaseNumber(int databaseType) {
			for (ServiceStartupType type : values()){
				if (type.getDatabaseNumber() == databaseType){
					return type;
				}
			}
			throw new RuntimeException("unknown database serialization for ServiceStartupType = "+databaseType);
		}
	
		public static ServiceStartupType fromDescription(String description) {
			for (ServiceStartupType type : values()){
				if (type.getDescription().equals(description)){
					return type;
				}
			}
			throw new RuntimeException("unknown description for ServiceStartupType = "+description);
		}
	}
	
	public ServiceSpec(VCellServerID sID, ServiceType t, int o, ServiceStartupType serviceStartupType, int memoryMB) {
		super();
		this.serverID = sID;
		this.type = t;
		this.ordinal = o;
		this.serviceStartupType = serviceStartupType;
		this.memoryMB = memoryMB;
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
		return "[" + serverID + "," + type + "," + ordinal + "," + serviceStartupType.getDescription() + "," + memoryMB + "M]";
	}

	public ServiceStartupType getStartupType() {
		return serviceStartupType;
	}

	public String getID() {
		return getServiceID(serverID, type, ordinal);
	}
	public static String getServiceID(VCellServerID serverID, ServiceType type, int ordinal) {
		return serverID + "_" + type.getName() + "_" + ordinal;
	}
	
	public Object[] toObjects() {
		return new Object[] {serverID, type, ordinal, serviceStartupType.getDescription(), memoryMB};
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
