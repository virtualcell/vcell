package cbit.vcell.messaging.server;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:23:36 PM)
 * @author: Fei Gao
 */
public interface ServiceProvider {
public String getHostName();
public cbit.vcell.messaging.VCServiceInfo getServiceInfo();
public String getServiceName();
public String getServiceType();
	public void stop();
}
