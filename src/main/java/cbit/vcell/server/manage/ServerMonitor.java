/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server.manage;

import cbit.vcell.message.server.bootstrap.client.RMIVCellConnectionFactory;
import cbit.vcell.server.*;
/**
 * Insert the type's description here.
 * Creation date: (10/23/2001 3:49:24 PM)
 * @author: Ion Moraru
 */
public class ServerMonitor extends Thread {
	private ServerManager serverManager = null;
	private int timeInterval = 10;  // in minutes
	private int vcellHostType = VCellHost.UNKNOWN_SERVER;
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:03:43 PM)
 * @param serverManager cbit.vcell.server.manage.ServerManager
 */
ServerMonitor(ServerManager serverManager, int argVCellHostType) {
	if (serverManager != null) {
		this.serverManager = serverManager;
	} else {
		throw new RuntimeException("serverManager cannot be null");
	}
	this.vcellHostType = argVCellHostType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:26:47 PM)
 * @return cbit.vcell.server.manage.VCellHost[]
 */
public cbit.vcell.server.manage.VCellHost[] getHosts() {
	return serverManager.getServerConfiguration().getHosts(vcellHostType);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:02:57 PM)
 * @return int
 */
public int getTimeInterval() {
	return timeInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:12:22 PM)
 * author: Ion Moraru
 */
public void run() {
	try {
		while (true) {
			VCellHost[] hosts = serverManager.getServerConfiguration().getHosts(vcellHostType);
			for (int i=0;hosts!=null && i<hosts.length;i++) {
				boolean alive = RMIVCellConnectionFactory.pingBootstrap(hosts[i].getName() + ":" + hosts[i].getPort());
				if (alive) {
					serverManager.log(getName() + " checking " + hosts[i].getName() + " at port " + hosts[i].getPort() + " -- alive");
				} else {
					serverManager.log(getName() + " checking " + hosts[i].getName() + " at port " + hosts[i].getPort() + " -- dead ? trying again in 5 seconds");
					try { sleep(5000);} catch (InterruptedException exc) {}
					alive = RMIVCellConnectionFactory.pingBootstrap(hosts[i].getName() + ":" + hosts[i].getPort());
					if (alive) {
						serverManager.log(getName() + " checking " + hosts[i].getName() + " at port " + hosts[i].getPort() + " -- alive");
					} else {
						serverManager.log(getName() + " checking " + hosts[i].getName() + " at port " + hosts[i].getPort() + " -- dead");
						if (hosts[i].isRestartIfDead()) {
							serverManager.startServers(new VCellHost[] {hosts[i]});
						}
					}
				}
			}
			try { sleep(60000 * getTimeInterval());} catch (InterruptedException exc) {}
		}
	} catch (Throwable exc) {
		serverManager.log(getName() + " <<<EXCEPTION>>> thread will stop, message is:" + exc.getMessage());
		exc.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:02:57 PM)
 * @param newTimeInterval int
 */
void setTimeInterval(int newTimeInterval) {
	timeInterval = newTimeInterval;
}
}
