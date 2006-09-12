package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;

import cbit.gui.PropertyLoader;
import cbit.util.SessionLog;
/**
 * This type was created in VisualAge.
 */
public class ConnectionPool implements Pingable {
	private Vector simComputeServerHostList = new Vector();
	private int simComputeServerHostIndex = 0;
	private ComputeHost[] potentialSimComputeServerHosts = new ComputeHost[0];
	private SessionLog log = null;
	private Ping ping = null;
/**
 * ConnectionPool constructor comment.
 */
public ConnectionPool(SessionLog sessionLog) {
	this.log = sessionLog;
	long minute = 60000;
	long period = 10*minute;
	ping = new Ping(this, period,"SlaveConnectionReaper");
	ping.start();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.ComputeHost[]
 */
private synchronized ComputeHost[] getSimComputeServerHostsProperty() {
	//
	// test if primary server
	//
	try {
		java.util.Vector hostList = new java.util.Vector();
		String odeComputeServerHosts = System.getProperty(PropertyLoader.odeComputeServerHosts);
		String pdeComputeServerHosts = System.getProperty(PropertyLoader.pdeComputeServerHosts);
		if (odeComputeServerHosts != null){
			StringTokenizer tokens = new StringTokenizer(odeComputeServerHosts, PropertyLoader.hostSeparator);
			while (tokens.hasMoreTokens()){
				String token = tokens.nextToken();
				hostList.addElement(ComputeHost.createODEComputeHost(token));
			}
		}
		if (pdeComputeServerHosts != null){
			StringTokenizer tokens = new StringTokenizer(pdeComputeServerHosts, PropertyLoader.hostSeparator);
			while (tokens.hasMoreTokens()){
				String token = tokens.nextToken();
				hostList.addElement(ComputeHost.createPDEComputeHost(token));
			}
		}
		if (hostList.size()>0){
			ComputeHost hosts[] = new ComputeHost[hostList.size()];
			hostList.copyInto(hosts);
			return hosts;
		}else{
			return null;
		}
	} catch (Exception e) {
		log.exception(e);
		return null;
	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellConnection
 * @param userid java.lang.String
 * @param password java.lang.String
 */
public synchronized VCellConnection getSimDataServerVCellConnection(String userid, String password) throws AuthenticationException {
log.print("ConnectionPool.getSimDataServerVCellConnection("+userid+","+password+")");
	String dataHost = System.getProperty(PropertyLoader.simDataServerHost);
	if (dataHost==null || dataHost.length()==0){
		return null;
	}
	VCellConnection vcConn = null;
	try {
		VCellConnectionFactory vcConnFactory = new RMIVCellConnectionFactory(dataHost,userid,password);
		vcConn = vcConnFactory.createVCellConnection();
log.print("ConnectionPool.getSimDataServerVCellConnection("+userid+","+password+")  .. returning host "+dataHost);
	}catch (ConnectionException e){
log.print("ConnectionPool.getSimDataServerVCellConnection("+userid+","+password+")  .. failed to connect to host "+dataHost);
		vcConn = null;
	}
	return vcConn;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.ConnectionPoolStatus
 */
public ConnectionPoolStatus getStatus() {

	ComputeHost[] activeHosts = null;
	ComputeHost[] potentialHosts = null;
	ComputeHost   nextHost = null;
	
	potentialHosts = getSimComputeServerHostsProperty();
	
	if (simComputeServerHostList.size()>0){
		activeHosts = new ComputeHost[simComputeServerHostList.size()];
		simComputeServerHostList.copyInto(activeHosts);
		nextHost = activeHosts[simComputeServerHostIndex % simComputeServerHostList.size()];
	}
	
	return new ConnectionPoolStatus(potentialHosts, activeHosts, nextHost);
}
/**
 * This method was created in VisualAge.
 */
public void ping() {
	//
	// update list of potential servers from Properties File
	//
	try {
		PropertyLoader.loadProperties();
		potentialSimComputeServerHosts = getSimComputeServerHostsProperty();
		if (potentialSimComputeServerHosts != null) {
			for (int i = 0; i < potentialSimComputeServerHosts.length; i++) {
				log.print("pinging host " + potentialSimComputeServerHosts[i]);
				//
				// if remote host is alive, add to list of active SimComputeServers, else remove from list
				//
				if (RMIVCellConnectionFactory.pingBootstrap(potentialSimComputeServerHosts[i].getHostName())) {
					synchronized (simComputeServerHostList) {
						if (!simComputeServerHostList.contains(potentialSimComputeServerHosts[i])){
							simComputeServerHostList.addElement(potentialSimComputeServerHosts[i]);
						}
					}
				}else{
					log.print("slave host " + potentialSimComputeServerHosts[i] + " not responding");
					synchronized (simComputeServerHostList){
						simComputeServerHostList.remove(potentialSimComputeServerHosts[i]);
					}
				}
			}
		} else {
			log.print("no slave servers availlable");
		}
	} catch (Throwable e) {
		log.exception(e);
	}
}
}
