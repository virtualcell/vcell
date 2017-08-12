package org.vcell.rest.users;

import java.util.Date;

import org.vcell.util.document.UserInfo;

public class UnverifiedUser {
	public UserInfo submittedUserInfo = null;
	public java.util.Date submitDate = null;
	public java.util.Date verificationTimeoutDate = null;
	public String verificationToken = null;
	
	public UnverifiedUser(UserInfo submittedUserInfo, Date submitDate, Date verificationTimeoutDate, String verificationToken) {
		this.submittedUserInfo = submittedUserInfo;
		this.submitDate = submitDate;
		this.verificationTimeoutDate = verificationTimeoutDate;
		this.verificationToken = verificationToken;
	}
}
