package cbit.vcell.server;

import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

public class DBRegistrationProvider implements RegistrationProvider {
	private AdminDatabaseServer adminDbServer;
	public DBRegistrationProvider(AdminDatabaseServer adminDbServer){
		if (adminDbServer==null){
			throw new IllegalArgumentException("adminDbServer cannot be null in LocalDBRegistrationProvider");
		}
		this.adminDbServer = adminDbServer;
	}
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteException,DataAccessException,UseridIDExistsException{
		if(bUpdate){
			throw new IllegalArgumentException("UPDATE User Info: Must use ClientserverManager NOT LocalAdminDBServer");
		}else{
			return adminDbServer.insertUserInfo(newUserInfo);
		}
	}
	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteException{
		return adminDbServer.getUserInfo(userKey);
	}					
	@Override
	public void sendLostPassword(String userid) throws DataAccessException,RemoteException{
		adminDbServer.sendLostPassword(userid);
	}
}