package cbit.vcell.messaging.admin;

import static cbit.vcell.messaging.admin.ManageConstants.SERVICE_STARTUP_TYPES;
import java.io.Serializable;

import org.vcell.util.ComparableObject;
import org.vcell.util.Matchable;
import org.vcell.util.MessageConstants.ServiceType;


public class ServiceSpec implements Matchable, Serializable, ComparableObject {
	private String serverID;
	private ServiceType type;
	private int ordinal;
	private int startupType;
	private int memoryMB;	
	
	public ServiceSpec(String sID, ServiceType t, int o, int st, int mm) {
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

	public String getServerID() {
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
	public static String getServiceID(String serverID, ServiceType type, int ordinal) {
		return serverID.charAt(0) + "_" + type.getName() + "_" + ordinal;
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
