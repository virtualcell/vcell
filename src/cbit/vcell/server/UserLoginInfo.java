package cbit.vcell.server;

import java.io.Serializable;

import org.vcell.util.PropertyLoader;
import org.vcell.util.document.User;

public class UserLoginInfo implements Serializable {
	private String userName;
	private String password;
	private String os_name;//os.name Operating system name 
	private String os_arch;// os.arch Operating system architecture 
	private String os_version;// os.version Operating system version
	private String vcellSoftwareVersion;//VCell client logging in from
	private User user;
	public UserLoginInfo(String userName,String password) {
		super();
		this.userName = userName;
		this.password = password;
		os_name = System.getProperty("os.name");
		os_arch = System.getProperty("os.arch");
		os_version = System.getProperty("os.version");
		vcellSoftwareVersion = System.getProperty(PropertyLoader.vcellSoftwareVersion);
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
	public String getVCellSoftwareVersion(){
		return vcellSoftwareVersion;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) throws Exception{
		if(user.compareEqual(this.user)){
			//Happens during 'reconnect'
			return;
		}
		if(this.user != null){
			//Different user never allowed to be reset
			//During 'login' or 'change user' a new UserLoginInfo should be created
			throw new Exception("UserLoginInfo: unexpected 'set' of different user.");
		}
		//set 'user' name must be the same as the 'login' name
		if(!user.getName().equals(this.userName)){
			throw new Exception("UserLoginInfo: 'set' user "+user.getName()+" does not equal login name "+this.userName);
		}
		this.user = user;
	}
}
