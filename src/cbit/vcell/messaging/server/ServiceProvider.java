package cbit.vcell.messaging.server;

import org.vcell.util.MessageConstants.ServiceType;

import cbit.vcell.messaging.admin.ServiceInstanceStatus;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:23:36 PM)
 * @author: Fei Gao
 */
public interface ServiceProvider {
public ServiceInstanceStatus getServiceInstanceStatus();
public String getServiceInstanceID();
public ServiceType getServiceType();
public void stop();
}
