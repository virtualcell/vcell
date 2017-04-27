package cbit.vcell.server;

import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

public class RMIBootstrapRegistrationProvider implements RegistrationProvider{
	private VCellBootstrap vcellBootstrap;
	public RMIBootstrapRegistrationProvider(VCellBootstrap vcellBootstrap){
		if (vcellBootstrap==null){
			throw new IllegalArgumentException("vcellBootstrap cannot be null in RMIBootstrapRegistrationProvider");
		}
		this.vcellBootstrap = vcellBootstrap;
	}
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteException,DataAccessException,UseridIDExistsException{
		if(bUpdate){
			throw new IllegalArgumentException("UPDATE User Info: Must use ClientserverManager NOT VCellBootstrap");
		}else{
			return vcellBootstrap.insertUserInfo(newUserInfo);
		}
	}
	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteException{
		throw new DataAccessException("UserInfo not provided by VCellBootstrap");
	}					
	public void sendLostPassword(String userid) throws DataAccessException,RemoteException{
		vcellBootstrap.sendLostPassword(userid);
	}
}