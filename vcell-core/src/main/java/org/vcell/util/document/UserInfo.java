/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

import org.vcell.util.document.UserLoginInfo.DigestedPassword;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class UserInfo implements java.io.Serializable,java.lang.Cloneable {	
	public KeyValue id = null;
	public String userid = null;
	public UserLoginInfo.DigestedPassword digestedPassword0;
	public String email = null;
	public String wholeName = null;
	public String title = null;
	public String company = null;
	public String country = null;
	public boolean notify = false;
	public java.util.Date insertDate = null;

	public static int FIELDLENGTH_USERID = 255;
	public static int FIELDLENGTH_PASSWORD = 255;
	public static int FIELDLENGTH_EMAIL = 255;
	public static int FIELDLENGTH_FIRSTNAME = 255;
	public static int FIELDLENGTH_LASTNAME = 255;
	public static int FIELDLENGTH_TITLE = 255;
	public static int FIELDLENGTH_COMPANY = 255;
	public static int FIELDLENGTH_ADDRESS1 = 255;
	public static int FIELDLENGTH_ADDRESS2 = 255;
	public static int FIELDLENGTH_CITY = 255;
	public static int FIELDLENGTH_STATE = 255;
	public static int FIELDLENGTH_COUNTRY = 255;
	public static int FIELDLENGTH_ZIP = 255;
	
/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 */
public Object clone() {
	UserInfo newUI = null;
	try {
		newUI = (UserInfo) super.clone();
	} catch (CloneNotSupportedException e) {
		// this shouldn't happen, since we are Cloneable
		throw new InternalError();
	}
	
	return newUI;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	UserInfo userInfo = null;
	if (object == null){
		return false;
	}
	if (!(object instanceof UserInfo)){
		return false;
	}else{
		userInfo = (UserInfo)object;
	}
	
System.out.println("this="+toString());
System.out.println("other="+userInfo.toString());
	
	if (toString().equals(userInfo.toString())){
		return true;
	}else{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 */
public String toString() {
	return "["+id+","+userid+","+/*password+*/","+email+","+wholeName+","+title+","+company+","+country+","+notify+","+insertDate+"]";
}

public org.vcell.api.common.UserInfo getApiUserInfo(){
	org.vcell.api.common.UserInfo apiUserInfo = new org.vcell.api.common.UserInfo(
			id.toString(),userid,digestedPassword0.getString(),email,wholeName,title,company,country,notify,insertDate);
	return apiUserInfo;
}


public static UserInfo fromApiUserInfo(org.vcell.api.common.UserInfo apiUserInfo) {
	UserInfo userInfo = new UserInfo();
	userInfo.id = new KeyValue(apiUserInfo.id);
	userInfo.userid = apiUserInfo.userid;
	userInfo.digestedPassword0 = DigestedPassword.createAlreadyDigested(apiUserInfo.digestedPassword0);
	userInfo.email = apiUserInfo.email;
	userInfo.wholeName = apiUserInfo.wholeName;
	userInfo.title = apiUserInfo.title;
	userInfo.company = apiUserInfo.company;
	userInfo.country = apiUserInfo.country;
	userInfo.notify = apiUserInfo.notify;
	userInfo.insertDate = apiUserInfo.insertDate;
	return userInfo;
}
}
