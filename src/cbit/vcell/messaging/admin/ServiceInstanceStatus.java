package cbit.vcell.messaging.admin;

import java.io.Serializable;
import java.util.Date;

import cbit.util.Matchable;
import cbit.vcell.messaging.MessageConstants.ServiceType;

public class ServiceInstanceStatus implements Matchable, Serializable, ComparableObject {
	private String serverID;
	private ServiceType type;
	private int ordinal;
	private Date startDate;
	private String hostname;	
	private boolean bRunning = false;
		
	public ServiceInstanceStatus(String sID, ServiceType t, int o, String h, Date d, boolean br) {
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

	public String getServerID() {
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
