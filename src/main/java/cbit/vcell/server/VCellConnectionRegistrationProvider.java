package cbit.vcell.server;

import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

public class VCellConnectionRegistrationProvider implements RegistrationProvider {
	private VCellConnection vcellConnection;
	public VCellConnectionRegistrationProvider(VCellConnection vcellConnection){
		this.vcellConnection = vcellConnection;
	}
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteException,DataAccessException,UseridIDExistsException{
		if(bUpdate){
			newUserInfo.id = vcellConnection.getUserLoginInfo().getUser().getID();
			return vcellConnection.getUserMetaDbServer().userRegistrationOP(
						UserRegistrationOP.createUpdateRegisterOP(newUserInfo)).getUserInfo();								
		}else{
			throw new IllegalArgumentException("INSERT User Info: Not allowed to use existing VCellConnection");
		}
	}
	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteException{
		return vcellConnection.getUserMetaDbServer().userRegistrationOP(UserRegistrationOP.createGetUserInfoOP(userKey)).getUserInfo();
	}					
	@Override
	public void sendLostPassword(String userid) throws DataAccessException,RemoteException{
		vcellConnection.getUserMetaDbServer().userRegistrationOP(UserRegistrationOP.createLostPasswordOP(userid));
	}
}