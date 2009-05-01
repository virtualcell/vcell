package cbit.vcell.server;

import java.io.Serializable;

import org.vcell.util.document.UserInfo;


public class UserRegistrationResults implements Serializable{
	private UserInfo userInfo;
	public UserRegistrationResults(UserInfo userInfo){
		this.userInfo = userInfo;
	}
	public UserInfo getUserInfo(){
		return userInfo;
	}
}
