package cbit.vcell.desktop;

public interface LoginDelegate {

	void login(String userid, String password);

	void registerRequest();

	void lostPasswordRequest(String userid);
	
	void userCancel();

}
