package cbit.vcell.messaging.admin;

import java.io.Serializable;
import java.util.Date;
import cbit.util.Compare;
import cbit.util.Matchable;
import static cbit.vcell.messaging.admin.ManageConstants.*;

public class ServiceStatus implements ComparableObject, Matchable, Serializable {
	private ServiceSpec serviceSpec = null;
	private Date date = null;
	private int status;
	private String statusMsg;
	private String pbsJobId;
	
	public ServiceStatus(ServiceSpec ss, Date d, int s, String sm, String pji) {
		super();
		this.serviceSpec = ss;
		this.date = d;
		this.status = s;
		this.statusMsg = sm;
		this.pbsJobId = pji;
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
				SERVICE_STARTUP_TYPES[serviceSpec.getStartupType()], serviceSpec.getMemoryMB(), date, SERVICE_STATUSES[status], statusMsg, pbsJobId};		
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
			if (!Compare.isEqualOrNull(pbsJobId, ss.pbsJobId)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public String getPbsJobId() {
		return pbsJobId;
	}
	
	public String toString() {
		return serviceSpec.toString();
	}
}
