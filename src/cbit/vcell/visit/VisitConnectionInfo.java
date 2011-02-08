package cbit.vcell.visit;
import java.io.File;
import java.io.Serializable;
import java.util.Date;

import org.vcell.util.document.User;


public class VisitConnectionInfo implements Serializable {

	private String auxSessionKey;
	private String ipAddress;
	private User user;
	private Date initTime;
	
	public VisitConnectionInfo(String auxSessionKey, String ipAddress,
			User user, Date initTime) {
		super();
		this.auxSessionKey = auxSessionKey;
		this.ipAddress = ipAddress;
		this.user = user;
		this.initTime = initTime;
	}
	
	// only use on server ... to be removed.
	@Deprecated
	public static VisitConnectionInfo createHardCodedVisitConnectionInfo(User user){
		return new VisitConnectionInfo("10","10.84.11.40",user,new Date());
	}
	
	public String getAuxSessionKey() {
		return auxSessionKey;
	}
	public String getIPAddress() {
		return ipAddress;
	}

	public User getUser() {
		return user;
	}
	public Date getInitTime() {
		return initTime;
	}	
	
	public String getDatabaseOpenPath(User user,String simLogName){
		return ipAddress+":/share/apps/vcell/users/"+user.getName()+"/"+simLogName;
	}
}
