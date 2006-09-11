package cbit.vcell.messaging;

import org.jdom.*;

import cbit.vcell.messaging.ManageConstants;
import cbit.vcell.messaging.VCAbstractServiceInfo;
import cbit.vcell.messaging.VCellServiceConfiguration;

/**
 * Insert the type's description here.
 * Creation date: (10/24/2001 6:46:47 PM)
 * @author: Ion Moraru
 */
public class VCServiceInfo extends VCAbstractServiceInfo {
	private String startCommand = null;
	private String stopCommand = null;	
	private int modifier = 0;
	private boolean bAutoStart = true;
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:00:22 PM)
 * @param type int
 */
public VCServiceInfo(VCellServiceConfiguration config) {	
	this(config.getHostName(), config.getType(), config.getName(), config.getStartCommand(), config.getStopCommand(), config.getLogfile(), config.isAutoStart());
	performance = new ServicePerformance();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:00:22 PM)
 * @param type int
 */
public VCServiceInfo(String hostName0, String type0, String name0) {	
	super(hostName0, type0, name0);
	performance = new ServicePerformance();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:00:22 PM)
 * @param type int
 */
public VCServiceInfo(String hostName0, String type0, String name0, String startCommand0, 
	String stopCommand0, String logfile0, boolean autoStart0) {	
	super(hostName0, type0, name0);
	this.startCommand = startCommand0;
	this.stopCommand = stopCommand0;
	this.logfile = logfile0;
	this.bAutoStart = autoStart0;
	
	performance = new ServicePerformance();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:00:22 PM)
 * @param type int
 */
public VCServiceInfo(String hostName0, String type0, String name0, String startCommand0, String stopCommand0, boolean bAutoStart0) {	
	super(hostName0, type0, name0);
	this.startCommand = startCommand0;
	this.stopCommand = stopCommand0;
	this.bAutoStart = bAutoStart0;
	
	performance = new ServicePerformance();
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 10:05:43 AM)
 * @return java.lang.String[]
 */
public VCellServiceConfiguration getConfiguration() {
	if (type.equals(ManageConstants.SERVER_TYPE_BOOTSTRAP)) {
		return new VCellServiceConfiguration(hostName, type, name, startCommand, stopCommand, bAutoStart);
	}

	return new VCellServiceConfiguration(hostName, type, name, startCommand, stopCommand, logfile, bAutoStart);		
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 2:18:28 PM)
 * @return int
 */
public int getModifier() {
	return modifier;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return java.lang.String
 */
public String getServiceName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return java.lang.String
 */
public String getServiceType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return java.lang.String
 */
public String getStartCommand() {
	return startCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return java.lang.String
 */
public String getStopCommand() {
	return stopCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (11/20/2003 11:34:25 AM)
 * @return boolean
 */
public boolean isAutoStart() {
	return bAutoStart;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 2:18:28 PM)
 * @param newModifier int
 */
public void setModifier(int newModifier) {
	modifier = newModifier;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/2003 10:23:08 AM)
 * @return java.lang.String
 */
public Element toElement() {
	Element root = null, element = null;
	if (type.equals(ManageConstants.SERVER_TYPE_BOOTSTRAP)) {
		root = new Element(ManageConstants.BOOTSTRAP_TAG);
		element = new Element(ManageConstants.HOST_NAME_TAG);
		element.setText(hostName);
		root.addContent(element);

		element = new Element(ManageConstants.BOOTSTRAP_NAME_TAG);
		element.setText(name);
		root.addContent(element);
		
		element = new Element(ManageConstants.SERVICE_START_TAG);
		element.setText(startCommand);
		root.addContent(element);
		
		element = new Element(ManageConstants.SERVICE_STOP_TAG);
		element.setText(stopCommand);
		root.addContent(element);

		element = new Element(ManageConstants.SERVICE_AUTOSTART_TAG);
		element.setText(bAutoStart ? "true" : "false");
		root.addContent(element);		
	} else if (type.equals(ManageConstants.SERVER_TYPE_RMISERVICE)) {
		root = new Element(ManageConstants.RMISERVICE_TAG);
		element = new Element(ManageConstants.HOST_NAME_TAG);
		element.setText(hostName);
		root.addContent(element);

		element = new Element(ManageConstants.RMISERVICE_NAME_TAG);
		element.setText(name);
		root.addContent(element);
		
		element = new Element(ManageConstants.SERVICE_START_TAG);
		element.setText(startCommand);
		root.addContent(element);
		
		element = new Element(ManageConstants.SERVICE_STOP_TAG);
		element.setText(stopCommand);
		root.addContent(element);
		
		element = new Element(ManageConstants.SERVICE_AUTOSTART_TAG);
		element.setText(bAutoStart ? "true" : "false");
		root.addContent(element);
	} else {
		root = new Element(ManageConstants.SERVICE_TAG);
		element = new Element(ManageConstants.SERVICE_TYPE_TAG);
		element.setText(type);
		root.addContent(element);

		element = new Element(ManageConstants.SERVICE_NAME_TAG);
		element.setText(name);
		root.addContent(element);
		
		element = new Element(ManageConstants.SERVICE_START_TAG);
		element.setText(startCommand);
		root.addContent(element);
		
		element = new Element(ManageConstants.SERVICE_STOP_TAG);
		element.setText(stopCommand);
		root.addContent(element);

		element = new Element(ManageConstants.LOGFILE_TAG);
		element.setText(logfile);
		root.addContent(element);

		element = new Element(ManageConstants.SERVICE_AUTOSTART_TAG);
		element.setText(bAutoStart ? "true" : "false");
		root.addContent(element);
	}


	return root;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/2003 10:23:08 AM)
 * @return java.lang.String
 */
public Object[] toObjects() {
	Object[] pvalues = performance.toObjects();	

	Object[] values = new Object[5 + pvalues.length];
	values[0] = hostName;
	values[1] = type;
	values[2] = name;	
	values[3] = new Boolean(alive);
	values[4] = bootTime;
	for (int i = 0; i < pvalues.length; i++){
		values[5 + i] = pvalues[i];
	}

	return values;
}
}
