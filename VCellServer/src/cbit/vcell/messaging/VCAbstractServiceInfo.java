package cbit.vcell.messaging;
import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (8/20/2003 8:27:17 AM)
 * @author: Fei Gao
 */
public abstract class VCAbstractServiceInfo implements java.io.Serializable, ComparableObject {
	protected Date bootTime = null;
	protected boolean alive = false;
	protected String logfile = null;
	protected String archiveDir = null;
	protected String hostName = null;
	protected String type = null;
	protected String name = null;
		
	protected Performance performance = null;

/**
 * AbstractService constructor comment.
 */
public VCAbstractServiceInfo(String hostName0, String type0, String name0) {
	super();
	this.hostName = hostName0;
	this.type = type0;
	this.name = name0;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:50:40 AM)
 * @return boolean
 */
public boolean equals(Object obj) {
	if (!(obj instanceof VCAbstractServiceInfo)) {
			return false;
	}

	VCAbstractServiceInfo s = (VCAbstractServiceInfo)obj;
	if (!type.equalsIgnoreCase(s.type)) {
		return false;
	}

	if (type.equalsIgnoreCase(ManageConstants.SERVER_TYPE_BOOTSTRAP) && hostName.equalsIgnoreCase(s.hostName)) {
		return true;
	}
	
	if (hostName.equalsIgnoreCase(s.hostName) && name.equalsIgnoreCase(s.name)) {
		return true;
	}

	return false;
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 10:01:11 AM)
 * @return java.lang.String
 */
public String getArchiveDir() {
	return archiveDir;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 8:33:58 AM)
 * @return java.util.Date
 */
public Date getBootTime() {
	return bootTime;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 10:03:21 AM)
 * @return java.lang.String
 */
public String getHostName() {
	return hostName;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 10:01:11 AM)
 * @return java.lang.String
 */
public String getLogfile() {
	return logfile;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:55:15 AM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 8:46:10 AM)
 * @return cbit.vcell.messaging.admin.PerformanceStatus
 */
public Performance getPerformance() {
	return performance;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:55:15 AM)
 * @return java.lang.String
 */
public String getType() {
	return type;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 8:33:58 AM)
 * @return boolean
 */
public boolean isAlive() {
	return alive;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 8:33:58 AM)
 * @param newAlive boolean
 */
public void setAlive(boolean newAlive) {
	alive = newAlive;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 10:01:11 AM)
 * @param newArchiveDir java.lang.String
 */
public void setArchiveDir(String newArchiveDir) {
	archiveDir = newArchiveDir;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 8:33:58 AM)
 * @param newBootTime java.util.Date
 */
public void setBootTime(Date newBootTime) {
	bootTime = newBootTime;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 10:01:11 AM)
 * @param newLogfile java.lang.String
 */
public void setLogfile(String newLogfile) {
	logfile = newLogfile;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 8:46:10 AM)
 * @param newPerformanceStatus cbit.vcell.messaging.admin.PerformanceStatus
 */
public void setPerformance(Performance newPerformance) {
	performance = newPerformance;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 9:15:37 AM)
 * @return java.lang.Object[]
 */
public abstract Object[] toObjects();


/**
 * Insert the method's description here.
 * Creation date: (8/11/2003 10:23:08 AM)
 * @return java.lang.String
 */
public String toString() {
	Object[] values = toObjects();
	StringBuffer sb = new StringBuffer();
	sb.append("[");
	for (int i = 0; i < values.length; i++){
		if (values[i] == null) {
			sb.append(" ");
		} else {
			sb.append(values[i].toString());
		}
			
		if (i < values.length - 1) {
			sb.append(",");
		}
	}
	sb.append("]");
	return sb.toString();	
}
}