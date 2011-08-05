/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.visit;
import java.io.File;
import java.io.Serializable;
import java.util.Date;

import org.vcell.util.PropertyLoader;
import org.vcell.util.document.User;


public class VisitConnectionInfo implements Serializable {

	private String auxSessionKey;
	private String ipAddress;
	private User user;
	private Date initTime;
	private String dataPath;
	
	public VisitConnectionInfo(String auxSessionKey, String ipAddress,
			User user, Date initTime, String dataPath) {
		super();
		this.auxSessionKey = auxSessionKey;
		this.ipAddress = ipAddress;
		this.user = user;
		this.initTime = initTime;
		this.dataPath = dataPath;
	}
	
	// only use on server ... to be removed.
	@Deprecated
	public static VisitConnectionInfo createHardCodedVisitConnectionInfo(User user){
		return new VisitConnectionInfo("10",PropertyLoader.getRequiredProperty(PropertyLoader.visitMDServerHostProperty),user,new Date(),PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty));
	}
	
	public String getAuxSessionKey() {
		return auxSessionKey;
	}
	public String getIPAddress() {
		return ipAddress;
	}

	public User getUser() {
		return user;
	}
	public Date getInitTime() {
		return initTime;
	}	
	
	public String getDataPath(){
		return dataPath;
	}
	
	public String getDatabaseOpenPath(User user,String simLogName){
		String s = getDataPath();
		if (!(s.endsWith("/"))) s=s+"/";
		s=s+user.getName()+"/"+simLogName;
		return s;
	}
}
