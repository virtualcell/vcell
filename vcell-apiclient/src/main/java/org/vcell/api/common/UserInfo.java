/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.api.common;

import java.util.Date;

@SuppressWarnings("serial")
public class UserInfo implements java.io.Serializable {	
	public String id = null;
	public String userid = null;
	public String digestedPassword0;
	public String email = null;
	public String wholeName = null;
	public String title = null;
	public String company = null;
	public String country = null;
	public boolean notify = false;
	public java.util.Date insertDate = null;

	public UserInfo() {
	}
	
	public UserInfo(String id, String userid, String digestedPassword0, String email, String wholeName, String title,
			String company, String country, boolean notify, Date insertDate) {
		this.id = id;
		this.userid = userid;
		this.digestedPassword0 = digestedPassword0;
		this.email = email;
		this.wholeName = wholeName;
		this.title = title;
		this.company = company;
		this.country = country;
		this.notify = notify;
		this.insertDate = insertDate;
	}

	public String toString() {
		return "["+id+","+userid+","+/*password+*/","+email+","+wholeName+","+title+","+company+","+country+","+notify+","+insertDate+"]";
	}
}
