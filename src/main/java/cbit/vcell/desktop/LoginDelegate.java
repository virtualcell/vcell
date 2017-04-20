package cbit.vcell.desktop;

import org.vcell.util.document.UserLoginInfo;

public interface LoginDelegate {

	void login(String userid, UserLoginInfo.DigestedPassword digestedPassword);

	void registerRequest();

	void lostPasswordRequest(String userid);
	
	void userCancel();

}
