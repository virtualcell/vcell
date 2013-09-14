/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

import java.io.Serializable;

import org.vcell.util.document.UserInfo;


public class UserRegistrationResults implements Serializable{
	private UserInfo userInfo;
	private boolean bUserIdUnique;
	public UserRegistrationResults(UserInfo userInfo){
		this.userInfo = userInfo;
		this.bUserIdUnique = true;
	}
	public UserRegistrationResults(boolean bUserIdUnique){
		this.userInfo = null;
		this.bUserIdUnique = bUserIdUnique;
	}
	public UserInfo getUserInfo(){
		return userInfo;
	}
	public boolean isUserIdUnique(){
		return bUserIdUnique;
	}
}
