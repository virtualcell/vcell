package cbit.vcell.server;

import org.scijava.Context;
import org.scijava.plugin.PluginInfo;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;

public class VCellConnectionRegistrationProvider implements RegistrationService {
	private VCellConnection vcellConnection;
	public VCellConnectionRegistrationProvider(VCellConnection vcellConnection){
		this.vcellConnection = vcellConnection;
	}
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteProxyException,DataAccessException,UseridIDExistsException{
		if(bUpdate){
			newUserInfo.id = vcellConnection.getUserLoginInfo().getUser().getID();
			return vcellConnection.getUserMetaDbServer().userRegistrationOP(
						UserRegistrationOP.createUpdateRegisterOP(newUserInfo)).getUserInfo();								
		}else{
			throw new IllegalArgumentException("INSERT User Info: Not allowed to use existing VCellConnection");
		}
	}
	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteProxyException{
		return vcellConnection.getUserMetaDbServer().userRegistrationOP(UserRegistrationOP.createGetUserInfoOP(userKey)).getUserInfo();
	}					
	@Override
	public void sendLostPassword(String userid) throws DataAccessException,RemoteProxyException{
		vcellConnection.getUserMetaDbServer().userRegistrationOP(UserRegistrationOP.createLostPasswordOP(userid));
	}
	@Override
	public Context context() {
		return null;
	}
	@Override
	public Context getContext() {
		return null;
	}
	@Override
	public double getPriority() {
		return 0;
	}
	@Override
	public void setPriority(double priority) {
	}
	@Override
	public PluginInfo<?> getInfo() {
		return null;
	}
	@Override
	public void setInfo(PluginInfo<?> info) {
	}
}