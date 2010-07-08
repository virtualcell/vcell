package cbit.vcell.server;

import java.io.Serializable;

public class UserLoginInfo implements Serializable {
	private String userName;
	private String password;
	private String os_name;//os.name Operating system name 
	private String os_arch;// os.arch Operating system architecture 
	private String os_version;// os.version Operating system version
	public UserLoginInfo(String userName,String password) {
		super();
		this.userName = userName;
		this.password = password;
		os_name = System.getProperty("os.name");
		os_arch = System.getProperty("os.arch");
		os_version = System.getProperty("os.version");
	}
	public String getOs_name() {
		return os_name;
	}
	public String getOs_arch() {
		return os_arch;
	}
	public String getOs_version() {
		return os_version;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
}
