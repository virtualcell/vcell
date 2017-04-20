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
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.vcell.util.Executable;

import cbit.vcell.resource.ResourceUtil;

/**
 * Insert the type's description here.
 * Creation date: (8/11/2003 11:41:43 AM)
 * @author: Fei Gao
 */
public class ManageUtils {
	private static java.text.SimpleDateFormat dateTimeFormatter = new java.text.SimpleDateFormat(" yyyy_MM_dd 'at' HH-mm-ss", java.util.Locale.US);

/**
 * ManageUtils constructor comment.
 */
public ManageUtils() {
	super();
}

public static String getHostName() {
	try {
		String hostname = cbit.vcell.message.server.ManageUtils.getLocalHostName();
		StringTokenizer st = new StringTokenizer(hostname, ".");
		hostname = st.nextToken(); // abbr hostname
		return hostname;
	} catch (UnknownHostException ex) {
		ex.printStackTrace();
		return "UnknownHost";
	}	 
}

/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 5:49:02 PM)
 * @return boolean
 * @param file java.io.File
 * @param archiveDirectory java.io.File
 */
public static void archiveByDateAndTime(String fileName, String arcDir) {
	try {
		if (fileName == null) {
			return;
		}
		
		java.io.File archiveDirectory = null;
		java.io.File file = new java.io.File(fileName);
			
		if (arcDir == null) {
			archiveDirectory = new java.io.File("." + java.io.File.separator);
		} else {
			archiveDirectory = new java.io.File(arcDir);
		}
		
		archiveDirectory.mkdir(); // in case it isn't there...
		if (file.exists()) {
			String archivedName = file.getName() + dateTimeFormatter.format(new java.util.Date());
			file.renameTo(new java.io.File(archiveDirectory, archivedName));
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 9:32:25 AM)
 * @return java.lang.String
 */
public static String getFullLocalHostName() throws java.net.UnknownHostException {
	java.net.InetAddress inet = java.net.InetAddress.getLocalHost();	
	String hostName = java.net.InetAddress.getByName(inet.getHostAddress()).getHostName();
	return hostName;
}


/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 9:32:25 AM)
 * @return java.lang.String
 */
public static String getLocalHostName() throws java.net.UnknownHostException {
	String hostName = java.net.InetAddress.getLocalHost().getHostName();
	if (hostName != null) {
		hostName = hostName.toLowerCase();
	}
	return hostName;
}


/**
 * Insert the method's description here.
 * Creation date: (12/4/2003 7:38:11 AM)
 * @return java.lang.String
 */
public static String readLog(java.io.File file) throws java.io.IOException {
	java.io.FileReader reader = new java.io.FileReader(file);
	char[] content = new char[10000];
	String out = "";
	while (true) {
		int n = reader.read(content, 0, 10000);
		if (n == -1) {
			break;
		} else
			if (n > 0) {
				out += new String(content, 0, n);
			}
	}
	reader.close();
	return out;
}
}
