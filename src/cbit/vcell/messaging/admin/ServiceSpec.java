package cbit.vcell.messaging.admin;

import static cbit.vcell.messaging.admin.ManageConstants.SERVICE_STARTUP_TYPES;

import java.io.Serializable;

import cbit.util.Matchable;

public class ServiceSpec implements Matchable, Serializable, ComparableObject {
	private String serverID;
	private String type;
	private int ordinal;
	private int startupType;
	private int memoryMB;	
	
	private boolean bRunning = false;
	
	public ServiceSpec(String sID, String t, int o, int st, int mm) {
		this(sID, t, o, st, mm, false);
	}
	
	public ServiceSpec(String sID, String t, int o, int st, int mm, boolean br) {
		super();
		this.serverID = sID;
		this.type = t;
		this.ordinal = o;
		this.startupType = st;
		this.memoryMB = mm;
		this.bRunning = br;
	}
	
	public int getMemoryMB() {
		return memoryMB;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public String getServerID() {
		return serverID;
	}

	public String getType() {
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
	public static String getServiceID(String serverID, String type, int ordinal) {
		return serverID.charAt(0) + "_" + type + "_" + ordinal;
	}
	
	public Object[] toObjects() {
		return new Object[] {serverID, type, ordinal, SERVICE_STARTUP_TYPES[startupType], memoryMB, bRunning};
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

	public boolean isRunning() {
		return bRunning;
	}

	public void setRunning(boolean running) {
		bRunning = running;
	}
}
