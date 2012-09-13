package cbit.vcell.desktop;

import cbit.vcell.server.UserLoginInfo;

public interface LoginDelegate {

	void login(String userid, UserLoginInfo.DigestedPassword digestedPassword);

	void registerRequest();

	void lostPasswordRequest(String userid);
	
	void userCancel();

}
