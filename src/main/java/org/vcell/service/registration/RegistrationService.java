package org.vcell.service.registration;

import java.rmi.RemoteException;

import org.vcell.service.VCellService;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

public interface RegistrationService extends VCellService {
	public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteException,DataAccessException,UseridIDExistsException;
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteException;
	public void sendLostPassword(String userid) throws DataAccessException,RemoteException;
}