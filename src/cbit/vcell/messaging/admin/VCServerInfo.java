package cbit.vcell.messaging.admin;

import java.io.IOException;
import java.util.*;
import org.jdom.*;

/**
 * Insert the type's description here.
 * Creation date: (8/19/2003 1:22:24 PM)
 * @author: Fei Gao
 */
public class VCServerInfo extends VCAbstractServiceInfo {

	private ArrayList serviceList = new ArrayList();
/**
 * VCServer constructor comment.
 * @param jmsFactory0 cbit.vcell.messaging.JmsFactory
 * @exception javax.jms.JMSException The exception description.
 * @exception java.net.UnknownHostException The exception description.
 */
public VCServerInfo(String hostName0, String serverType0, String serverName0){
	super(hostName0, serverType0, serverName0);	
	performance = new ServerPerformance();
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 11:06:34 AM)
 * @param service cbit.vcell.messaging.admin.VCellService
 */
public synchronized boolean addService(VCServiceInfo serviceInfo) {
	if (!serviceList.contains(serviceInfo)) {
		serviceList.add(serviceInfo);
		return true;
	} else {
		System.out.println("VCServer::addService() - Service exists " + serviceInfo);
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 11:06:34 AM)
 * @param service cbit.vcell.messaging.admin.VCService
 */
public synchronized void deleteService(VCServiceInfo serviceInfo) {
	if (!serviceList.contains(serviceInfo)) {
		System.out.println("VCServer::deleteService - Service doesn't exist " + serviceInfo);
		return;
	}
	serviceList.remove(serviceInfo);
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 11:38:00 AM)
 * @param changes java.util.Map
 */
public void doChange(LinkedList changes, String newDir) {
	this.archiveDir = newDir;
	Iterator iter = changes.iterator();
	while (iter.hasNext()) {
		VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
		switch (serviceInfo.getModifier()) {
			case ManageConstants.SERVICE_MODIFIER_NEW: {
				addService(serviceInfo);
				break;
			}
				
			case ManageConstants.SERVICE_MODIFIER_MODIFY: {
				modifyService(serviceInfo);
				break;
			}

			case ManageConstants.SERVICE_MODIFIER_DELETE: {
				deleteService(serviceInfo);
				break;
			}
		}
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 11:23:06 AM)
 * @return java.lang.String
 */
public String getHostName() {
	return hostName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 10:30:14 AM)
 * @return java.lang.String
 */
public String getServerName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 1:16:25 PM)
 * @return java.lang.String
 */
public String getServerType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 10:53:02 AM)
 * @return java.util.ArrayList
 */
public ArrayList getServiceConfigList() {
	Iterator iter = serviceList.iterator();
	ArrayList list = new ArrayList();
	
	while (iter.hasNext()) {
		VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
		list.add(serviceInfo.getConfiguration());
	}

	return list;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 10:54:20 AM)
 * @return cbit.vcell.messaging.admin.VCellService
 * @param index int
 */
public VCServiceInfo getServiceInfoAt(int index) {
	if (index >= serviceList.size() || index < 0) {
		throw new ArrayIndexOutOfBoundsException();
	}

	return (VCServiceInfo)serviceList.get(index);	
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 10:53:02 AM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getServiceList() {
	return serviceList;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 10:53:35 AM)
 * @return int
 * @param service cbit.vcell.messaging.admin.VCellService
 */
public int indexOf(VCServiceInfo serviceInfo) {
	return serviceList.indexOf(serviceInfo);
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 2:11:16 PM)
 * @return boolean
 */
public boolean isBootstrap() {
	return type.equals(ManageConstants.SERVER_TYPE_BOOTSTRAP);
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 2:11:16 PM)
 * @return boolean
 */
public boolean isRMIService() {
	return type.equals(ManageConstants.SERVER_TYPE_RMISERVICE);
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 2:10:48 PM)
 * @return boolean
 */
boolean isServerManager() {
	return type.equals(ManageConstants.SERVER_TYPE_SERVERMANAGER);
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 11:06:34 AM)
 * @param service cbit.vcell.messaging.admin.VCellService
 */
public synchronized boolean modifyService(VCServiceInfo serviceInfo) {
	int index = serviceList.indexOf(serviceInfo);

	if (index >= 0) {
		serviceList.set(index, serviceInfo);
		return true;
	} else {
		//System.out.println("VCServer::modifyService - Service doesn't exit [" + type + "], " + service);
		return false;
	}		
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:43:36 AM)
 */
public Process startAService(VCServiceInfo serviceInfo0, boolean boot) throws java.io.IOException {
	int index = indexOf(serviceInfo0);
	if (index < 0) {		
		return null;
	}

	VCServiceInfo serviceInfo = getServiceInfoAt(index);
	if (serviceInfo.isAlive() && isBootstrap()) { 
		// if I am servermanager and the service to be started is a bootstrap, 
		// no matter whether it is alive or not, start it anyway, it doesn't hurt(sc \\hostname start XXXX)
		// otherwise, if I am bootstrap and the service to be started is already alive, then don't start it
		System.out.println("Service is alive: [" + serviceInfo + "]");
		return null;
	}
		
	if (!serviceInfo.isAutoStart() && boot) {
		return null;
	}
	
	if (isBootstrap()) {
		ManageUtils.archiveByDateAndTime(serviceInfo.getLogfile(), archiveDir);
	}

	serviceInfo.setBootTime(new Date());
	serviceInfo.setAlive(true);
	if (serviceInfo.getStartCommand() != null && serviceInfo.getStartCommand().trim().length() != 0) {
		System.out.println("Start Command: " + serviceInfo.getStartCommand());
		return Runtime.getRuntime().exec(serviceInfo.getStartCommand());
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 9:43:36 AM)
 */
public void stopAService(VCServiceInfo serviceInfo0) throws IOException {
	int index = indexOf(serviceInfo0);
	if (index < 0) {
		return;
	}

	VCServiceInfo serviceInfo = getServiceInfoAt(index);
	/*
	if (!serviceInfo.isAlive()) {
		System.out.println("Service is not alive: [" + serviceInfo + "]");
		return;
	}
	*/

	serviceInfo.setAlive(false);
	String stopCommand = serviceInfo.getStopCommand();
	if (stopCommand != null && stopCommand.trim().length() != 0) {		
		System.out.println("Stop Command: " + serviceInfo.getStopCommand());
		Runtime.getRuntime().exec(stopCommand);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/2003 10:23:08 AM)
 * @return java.lang.String
 */
public Document toDocument() {	
	Element root = null;

	if (type.equals(ManageConstants.SERVER_TYPE_BOOTSTRAP)) {
		root = new Element(ManageConstants.BOOTSTRAP_ROOT_TAG);
	} else {
		root = new Element(ManageConstants.SERVERMANAGER_ROOT_TAG);
	}

	//logfile
	Element element = new Element(ManageConstants.LOGFILE_TAG);
	element.setText(logfile);
	root.addContent(element);

	if (archiveDir != null && archiveDir.trim().length() > 0) {
		element = new Element(ManageConstants.ARCHIVEDIR_TAG);
		element.setText(archiveDir);
		root.addContent(element);
	}

	Iterator iter = serviceList.iterator();
	while (iter.hasNext()) {
		VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
		root.addContent(serviceInfo.toElement());		
	}

	return new Document(root);	
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
