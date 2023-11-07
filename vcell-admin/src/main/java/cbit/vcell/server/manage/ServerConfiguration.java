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

import org.vcell.util.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;/**
 * Insert the type's description here.
 * Creation date: (10/24/2001 2:25:13 PM)
 * @author: Ion Moraru
 */
public class ServerConfiguration {
	private VCellHost[] allHosts = new VCellHost[0];
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 3:29:24 PM)
 * @param configurationFile java.io.File
 * @exception java.io.IOException The exception description.
 * @exception java.io.FileNotFoundException The exception description.
 */
public ServerConfiguration(File configurationFile) throws IOException, FileNotFoundException {
	LineNumberReader reader = new LineNumberReader(new FileReader(configurationFile));
	Vector<VCellHost> hosts = new Vector<>();
	VCellHost host = null;
	String line = reader.readLine();
	while (line != null) {
		if (!line.startsWith("#")) {
			if(line.startsWith("hosttype=")) {
				if (host != null) hosts.add(host);
				String type = line.substring(line.indexOf("=") + 1);
                host = switch (type) {
                    case "vcell" -> new VCellHost(VCellHost.VCELL_SERVER);
                    case "data" -> new VCellHost(VCellHost.DATA_SERVER);
                    case "database" -> new VCellHost(VCellHost.DATABASE_SERVER);
                    case "compute" -> new VCellHost(VCellHost.COMPUTE_SERVER);
                    default -> null;
                };
			} else if (line.startsWith("name=")) {
				if (host != null) host.setName(line.substring(line.indexOf("=") + 1));
			} else if (line.startsWith("port=")) {
				if (host != null) host.setPort(Integer.parseInt(line.substring(line.indexOf("=") + 1)));
			} else if (line.startsWith("startcmd=")) {
				if (host != null) host.setStartCommand(line.substring(line.indexOf("=") + 1));
			} else if (line.startsWith("stopcmd=")) {
				if (host != null) host.setStopCommand(line.substring(line.indexOf("=") + 1));
			} else if (line.startsWith("logfile=")) {
				if (host != null) host.setLogFile(new File(line.substring(line.indexOf("=") + 1)));
			} else if (line.startsWith("restart=")) {
				if (host != null) host.setRestartIfDead(Boolean.parseBoolean(line.substring(line.indexOf("=") + 1)));
			}
		}
		line = reader.readLine();
	}
	if (host != null) hosts.add(host);

	this.allHosts = hosts.toArray(VCellHost[]::new);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:04:58 PM)
 * @return cbit.vcell.server.manage.VCellHost[]
 */
public VCellHost[] getComputeServerHosts() {
	return getHosts(VCellHost.COMPUTE_SERVER);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:04:58 PM)
 * @return cbit.vcell.server.manage.VCellHost[]
 */
public VCellHost[] getDatabaseServerHosts() {
	return getHosts(VCellHost.DATABASE_SERVER);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:04:58 PM)
 * @return cbit.vcell.server.manage.VCellHost[]
 */
public VCellHost[] getDataServerHosts() {
	return getHosts(VCellHost.DATA_SERVER);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:13:10 PM)
 * @return cbit.vcell.server.manage.VCellHost[]
 * @param hostType int
 */
public VCellHost[] getHosts(int hostType) {
	synchronized (allHosts) {
		int n = 0;
		for (int i=0;i<allHosts.length;i++) {
			if (allHosts[i].getType() == hostType) {
				n++;
			}
		}
		VCellHost[] hosts = new VCellHost[n];
		n = 0;
		for (int i=0;i<allHosts.length;i++) {
			if (allHosts[i].getType() == hostType) {
				hosts[n] = allHosts[i];
				n++;
			}
		}
		return hosts;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:04:58 PM)
 * @return cbit.vcell.server.manage.VCellHost[]
 */
public VCellHost[] getVCellServerHosts() {
	return getHosts(VCellHost.VCELL_SERVER);
}
}
