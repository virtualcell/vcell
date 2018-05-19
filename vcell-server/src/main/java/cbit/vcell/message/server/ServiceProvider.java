/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;

/**
 * Insert the type's description here.
 * Creation date: (7/2/2003 3:00:11 PM)
 * @author: Fei Gao
 */
public abstract class ServiceProvider {
	public static final Logger lg = LogManager.getLogger(ServiceProvider.class);

	protected ServiceInstanceStatus serviceInstanceStatus = null;
	protected VCMessagingService vcMessagingService_int = null;
	protected VCMessagingService vcMessagingService_sim = null;
	public final boolean bSlaveMode;

/**
 * JmsMessaging constructor comment.
 */
protected ServiceProvider(VCMessagingService vcMessageService_int, VCMessagingService vcMessageService_sim, ServiceInstanceStatus serviceInstanceStatus, boolean bSlaveMode) {
	this.vcMessagingService_int = vcMessageService_int;
	this.vcMessagingService_sim = vcMessageService_sim;
	this.serviceInstanceStatus = serviceInstanceStatus;
	this.bSlaveMode = bSlaveMode;
}


/**
 * Insert the method's description here.
 * Creation date: (12/10/2003 8:42:49 AM)
 */
public void stopService() {
	if (bSlaveMode){
		return;
	}
	try {
		Thread t_int = new Thread() {
			public void run() {
				try {
					if (vcMessagingService_int!=null) {
						vcMessagingService_int.close();
					}
				} catch (VCMessagingException e) {
					e.printStackTrace();
				}
			}
		};
		Thread t_sim = new Thread() {
			public void run() {
				try {
					if (vcMessagingService_sim!=null) {
						vcMessagingService_sim.close();
					}
				} catch (VCMessagingException e) {
					e.printStackTrace();
				}
			}
		};
		t_int.start();
		t_sim.start();
		t_int.join(3000);	
		t_sim.join(3000);	
	} catch (InterruptedException ex) {
	} finally {
		System.exit(0);
	}
}

}
