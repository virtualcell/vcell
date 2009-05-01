package cbit.vcell.applets;

import java.text.DateFormat;
/**
 * Insert the type's description here.
 * Creation date: (12/9/2002 2:22:56 AM)
 * @author: Jim Schaff
 */
public class ServerTableModel extends javax.swing.table.AbstractTableModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private final static int COL_HOSTNAME = 0;
	private final static int COL_SERVERTYPE = 1;
	private final static int COL_JAVA_MEMORY = 2;
	private final static int COL_SYSTEM_MEMORY = 3;
	private final static int COL_CPU_USAGE = 4;
	private final static int COL_JOBS_RUNNING = 5;
	private final static int COL_NUM_PROCESSORS = 6;
	private final static int COL_BOOT_TIME = 7;
	private final static int NUM_COLUMNS = 8;
	private String LABELS[] = { "Host", "Server Type", "Free Java Memory", "Free System Memory", "CPU %", "Jobs Running", "Num Processors", "Boot Time" };
	private cbit.vcell.applets.ServerMonitorInfo[] fieldServerMonitorInfos = new ServerMonitorInfo[0];
/**
 * SolverControllerTableModel constructor comment.
 */
public ServerTableModel() {
	super();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	switch (column){
		case COL_HOSTNAME:{
			return String.class;
		}
		case COL_SERVERTYPE:{
			return String.class;
		}
		case COL_JAVA_MEMORY: {
			return String.class;
		}
		case COL_SYSTEM_MEMORY: {
			return String.class;
		}
		case COL_CPU_USAGE: {
			return String.class;
		}
		case COL_JOBS_RUNNING: {
			return String.class;
		}
		case COL_NUM_PROCESSORS: {
			return String.class;
		}
		case COL_BOOT_TIME: {
			return String.class;
		}
		default:{
			return Object.class;
		}
	}
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return NUM_COLUMNS;
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("SolverControllerTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return (getServerMonitorInfos()!=null)?(getServerMonitorInfos().length):(0);
}
/**
 * Gets the serverMonitorInfos property (cbit.vcell.applets.ServerMonitorInfo[]) value.
 * @return The serverMonitorInfos property value.
 * @see #setServerMonitorInfos
 */
public cbit.vcell.applets.ServerMonitorInfo[] getServerMonitorInfos() {
	return fieldServerMonitorInfos;
}
/**
 * Gets the serverMonitorInfos index property (cbit.vcell.applets.ServerMonitorInfo) value.
 * @return The serverMonitorInfos property value.
 * @param index The index value into the property array.
 * @see #setServerMonitorInfos
 */
public ServerMonitorInfo getServerMonitorInfos(int index) {
	return getServerMonitorInfos()[index];
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	//double percentUsedCPU = 100.0*(1.0 - processStatus.getFractionFreeCPU());
	//long freeMemoryBytes = processStatus.getFreeMemoryBytes();
	//int numJobsRunning = processStatus.getNumJobsRunning();
	//int numProcessors = processStatus.getNumProcessors();
	//long currSize = cacheStatus.getCurrSize();
	//long maxSize = cacheStatus.getMaxSize();
	//int numObjects = cacheStatus.getNumObjects();
	////
	//SlaveServerPanel slaveServerPanel = (SlaveServerPanel) slaveServerPanels.elementAt(i);
	//getSlaveServersTabbedPane().setTitleAt(i, name);
	//slaveServerPanel.setHostnameTextFieldText(name);
	//slaveServerPanel.setCPUUsageTextFieldText(((int) percentUsedCPU) + "%");
	//slaveServerPanel.setFreeMemoryTextFieldText(freeMemoryBytes/1024 + "K");
	//slaveServerPanel.setJobsRunningTextFieldText(Integer.toString (numJobsRunning));
	//slaveServerPanel.setProcessorsTextFieldText(Integer.toString (numProcessors));
	//slaveServerPanel.setCurrentCacheSizeTextFieldText(currSize/1024 + "K");
	//slaveServerPanel.setMaximumCacheSizeTextFieldText(maxSize/1024 + "K");
	//slaveServerPanel.setNumberOfCacheObjectsTextFieldText(Integer.toString (numObjects));
	//javax.swing.DefaultListModel defaultListModel = new javax.swing.DefaultListModel ();
	////
	//if (connectedUsers != null) {
		//for (int n = 0; n < connectedUsers.length; n++) {
			//defaultListModel.addElement(connectedUsers[n]);
		//}
	//}
	//slaveServerPanel.setCurrentUsersListModel(defaultListModel);
	try {
		ServerMonitorInfo serverMonitorInfo = getServerMonitorInfos(row);
		if (serverMonitorInfo == null){
			return "<<null>>";
		}
		cbit.vcell.server.ProcessStatus processStatus = (serverMonitorInfo.getServerInfo()==null)?(null):(serverMonitorInfo.getServerInfo().getProcessStatus());
		switch (col){
			case COL_HOSTNAME: {
				return serverMonitorInfo.getHostName();
			}
			case COL_SERVERTYPE: {
				return serverMonitorInfo.getServerType();
			}
			case COL_JAVA_MEMORY: {
				if (processStatus!=null){
					return Long.toString(processStatus.getFreeJavaMemoryBytes());
				}else{
					return "?";
				}
			}
			case COL_SYSTEM_MEMORY: {
				if (processStatus!=null){
					return Long.toString(processStatus.getFreeMemoryBytes());
				}else{
					return "?";
				}
			}
			case COL_CPU_USAGE: {
				if (processStatus!=null){
					return Double.toString(processStatus.getFractionFreeCPU());
				}else{
					return "?";
				}
			}
			case COL_JOBS_RUNNING: {
				if (processStatus!=null){
					return Integer.toString(processStatus.getNumJobsRunning());
				}else{
					return "?";
				}
			}
			case COL_NUM_PROCESSORS: {
				if (processStatus!=null){
					return Integer.toString(processStatus.getNumProcessors());
				}else{
					return "?";
				}
			}
			case COL_BOOT_TIME: {
				if (processStatus!=null){
					return DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(processStatus.getBootTime());
				}else{
					return "?";
				}
			}
			default:{
				return "unknown column";
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
		return "error";
	}
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 4:52:08 PM)
 * @return boolean
 * @param rmiUrl1 java.lang.String
 * @param rmiUrl2 java.lang.String
 */
public static boolean nameSameWithoutDomain(String rmiUrl1, String rmiUrl2) {
	//
	// look only at machine name (ignore domain)
	//  e.g.  DBS1.vcell.UCHC.edu:2345  ==  "dbs1:2345"  will be equal
	//  e.g.  DBS1.vcell.UCHC.edu:1099  ==  "dbs1"       no port implies 1099
	//  e.g.  DBS1.vcell.UCHC.edu:2345  !=  "dbs1"       implies 1099
	//  e.g.  DBS1.vcell.UCHC.edu:1099  ==  "dbs1.abc"   (artifact of ignoring domain ... this is unlikely)
	//
	int port1 = 1099;
	String host1 = rmiUrl1;
	if (rmiUrl1.indexOf(':')>=0){
		host1 = rmiUrl1.substring(0,rmiUrl1.indexOf(':'));
		String port1String = rmiUrl1.substring(rmiUrl1.indexOf(':')+1,rmiUrl1.length());
		port1 = Integer.parseInt(port1String);
	}
	String name1 = host1;
	if (host1.indexOf('.')>=0){
		name1 = host1.substring(0,host1.indexOf('.'));
	}
	int port2 = 1099;
	String host2 = rmiUrl2;
	if (rmiUrl2.indexOf(':')>=0){
		host2 = rmiUrl2.substring(0,rmiUrl2.indexOf(':'));
		String port2String = rmiUrl2.substring(rmiUrl2.indexOf(':')+1,rmiUrl2.length());
		port2 = Integer.parseInt(port2String);
	}
	String name2 = host2;
	if (host2.indexOf('.')>=0){
		name2 = host2.substring(0,host2.indexOf('.'));
	}
	//System.out.println("'"+rmiUrl1+"' ==> '"+name1+":"+port1+"',    '"+rmiUrl2+"' ==> '"+name2+":"+port2+"'");
	return name1.equalsIgnoreCase(name2) && (port1 == port2);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Sets the serverMonitorInfos property (cbit.vcell.applets.ServerMonitorInfo[]) value.
 * @param serverMonitorInfos The new value for the property.
 * @see #getServerMonitorInfos
 */
public void setServerMonitorInfos(cbit.vcell.applets.ServerMonitorInfo[] serverMonitorInfos) {
	cbit.vcell.applets.ServerMonitorInfo[] oldValue = fieldServerMonitorInfos;
	fieldServerMonitorInfos = serverMonitorInfos;
	firePropertyChange("serverMonitorInfos", oldValue, serverMonitorInfos);
	fireTableDataChanged();
}
/**
 * Sets the serverMonitorInfos index property (cbit.vcell.applets.ServerMonitorInfo[]) value.
 * @param index The index value into the property array.
 * @param serverMonitorInfos The new value for the property.
 * @see #getServerMonitorInfos
 */
public void setServerMonitorInfos(String hostName, ServerMonitorInfo serverMonitorInfos) {
	ServerMonitorInfo oldValue = null;
	int index = -1;
	for (int i = 0; fieldServerMonitorInfos!=null && i < fieldServerMonitorInfos.length; i++){
		if (nameSameWithoutDomain(fieldServerMonitorInfos[i].getHostName(),hostName)){
			oldValue = fieldServerMonitorInfos[i];
			index = i;
			break;
		}
	}
	if (index>=0){
		fieldServerMonitorInfos[index] = serverMonitorInfos;
	}else{
		fieldServerMonitorInfos = (ServerMonitorInfo[])org.vcell.util.BeanUtils.addElement(fieldServerMonitorInfos,serverMonitorInfos);
		index = fieldServerMonitorInfos.length-1;
	}
	if (oldValue == null || !oldValue.equals(serverMonitorInfos)){
		firePropertyChange("serverMonitorInfos", null, fieldServerMonitorInfos);
		fireTableDataChanged();
	}
}
}
