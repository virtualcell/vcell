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

public class ManageUtils {
public static String getHostName() {
	try {
		String hostName = java.net.InetAddress.getLocalHost().getHostName();
		if (hostName != null) {
			hostName = hostName.toLowerCase();
		}else{
			return "UnknownHost";
		}

		StringTokenizer st = new StringTokenizer(hostName, ".");
		hostName = st.nextToken(); // abbr hostname
		return hostName;
	} catch (UnknownHostException ex) {
		return "UnknownHost";
	}	 
}

}
