/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
