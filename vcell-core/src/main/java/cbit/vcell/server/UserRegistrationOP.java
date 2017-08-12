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

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

@SuppressWarnings("serial")
public class UserRegistrationOP implements Serializable{
	
//	public static final String USERREGOP_NEWREGISTER = "USERREGOP_NEWREGISTER";
	public static final String USERREGOP_UPDATE = "USERREGOP_UPDATE";
	public static final String USERREGOP_GETINFO = "USERREGOP_GETINFO";
	public static final String USERREGOP_LOSTPASSWORD = "USERREGOP_LOSTPASSWORD";
	public static final String USERREGOP_ISUSERIDUNIQUE = "USERREGOP_ISUSERIDUNIQUE";
	
	private UserInfo userInfo;
	private String operationType;
	private String userid;
	private String password;
	private KeyValue userKey;
	
	public static UserRegistrationOP createGetUserInfoOP(KeyValue keyValue){
		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
		userRegistrationOP.operationType = USERREGOP_GETINFO;
//		userRegistrationOP.userid  = user.getName();
		userRegistrationOP.userKey = keyValue;//user.getID();
		return userRegistrationOP;
	}
	public static UserRegistrationOP createIsUserIdUniqueOP(String userid){
		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
		userRegistrationOP.operationType = USERREGOP_ISUSERIDUNIQUE;
		userRegistrationOP.userid  = userid;
		return userRegistrationOP;
	}

	public static UserRegistrationOP createLostPasswordOP(String userid){
		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
		userRegistrationOP.operationType = USERREGOP_LOSTPASSWORD;
		userRegistrationOP.userid  = userid;
		return userRegistrationOP;
	}

	public static UserRegistrationOP createUpdateRegisterOP(UserInfo userInfo){
		UserRegistrationOP userRegistrationOP = new UserRegistrationOP();
		userRegistrationOP.operationType = USERREGOP_UPDATE;
		userRegistrationOP.userInfo = userInfo;
		userRegistrationOP.userid  = userInfo.userid;
		userRegistrationOP.userKey = userInfo.id;
		return userRegistrationOP;		
	}

	public static boolean hasIllegalCharacters(String apo){
		if((apo.indexOf('\'') != -1) || (apo.indexOf('<') != -1) || (apo.indexOf('>') != -1) || (apo.indexOf('&') != -1) || (apo.indexOf('\"') != -1)){
			return true;
		}
		return false;
	}

	public static class NewPasswordUserInfo extends UserInfo{
		public DigestedPassword otherDigestedPassword;
	}
	public UserInfo getUserInfo(){
		return userInfo;
	}
	
	public String getOperationType(){
		return operationType;
	}
	public String getUserid(){
		return userid;
	}
	public String getPassword(){
		return password;
	}
	public KeyValue getUserKey(){
		return userKey;
	}
}
