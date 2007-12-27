package cbit.vcell.server;

import java.io.Serializable;

import cbit.sql.UserInfo;

public class UserRegistrationResults implements Serializable{
	private UserInfo userInfo;
	public UserRegistrationResults(UserInfo userInfo){
		this.userInfo = userInfo;
	}
	public UserInfo getUserInfo(){
		return userInfo;
	}
}
