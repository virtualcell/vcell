package cbit.vcell.messaging.admin;

/**
 * Insert the type's description here.
 * Creation date: (9/11/2003 3:20:06 PM)
 * @author: Fei Gao
 */
public class VCellServiceConfiguration implements ComparableObject {
	private String hostName = null;
	private String type = null;
	private String name = null;
	private String startCommand = null;
	private String stopCommand = null;
	private String logfile = null;
	private boolean bAutoStart = true;
/**
 * VCellServiceConfiguration constructor comment.
 */
public VCellServiceConfiguration(String hostName0, String type0, String name0, String startCommand0, String stopCommand0, String logfile0, boolean autoStart0) {
	this.hostName = hostName0;
	this.type = type0;
	this.name = name0;
	this.startCommand = startCommand0;
	this.stopCommand = stopCommand0;
	this.logfile = logfile0;
	this.bAutoStart = autoStart0;
}
/**
 * VCellServiceConfiguration constructor comment.
 */
public VCellServiceConfiguration(String hostName0, String type0, String name0, String startCommand0, String stopCommand0, boolean bAutoStart0) {
	this.hostName = hostName0;
	this.type = type0;
	this.name = name0;
	this.startCommand = startCommand0;
	this.stopCommand = stopCommand0;
	this.bAutoStart = bAutoStart0;
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:50:40 AM)
 * @return boolean
 */
public boolean equals(Object obj) {
	if (!(obj instanceof VCellServiceConfiguration))
			return false;

	VCellServiceConfiguration s = (VCellServiceConfiguration)obj;

	if (!type.equals(s.type)) {
		return false;
	}

	if (type.equals(ManageConstants.SERVER_TYPE_BOOTSTRAP) && hostName.equals(s.hostName)) {
		return true;
	}
	
	if (hostName.equals(s.hostName) && name.equals(s.name)) {
		return true;
	}

	return false;
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 7:47:58 AM)
 * @return java.lang.String
 */
public String getHostName() {
	return hostName;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 7:47:58 AM)
 * @return java.lang.String
 */
public String getLogfile() {
	return logfile;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 7:47:58 AM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 7:47:58 AM)
 * @return java.lang.String
 */
public String getStartCommand() {
	return startCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 7:47:58 AM)
 * @return java.lang.String
 */
public String getStopCommand() {
	return stopCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2003 7:47:58 AM)
 * @return java.lang.String
 */
public String getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (11/20/2003 11:29:15 AM)
 * @return boolean
 */
public boolean isAutoStart() {
	return bAutoStart;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 3:22:43 PM)
 * @return java.lang.Object[]
 */
public Object[] toObjects() {
	return (type.equals(ManageConstants.SERVER_TYPE_BOOTSTRAP)) ? (new Object[] {hostName, type, name, startCommand, stopCommand, new Boolean(bAutoStart)}) : (new Object[] {hostName, type, name, startCommand, stopCommand, logfile, new Boolean(bAutoStart)});
}
}
