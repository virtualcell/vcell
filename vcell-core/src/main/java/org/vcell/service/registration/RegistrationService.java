package org.vcell.service.registration;

import cbit.vcell.message.server.bootstrap.client.RemoteProxyException;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

public interface RegistrationService {
	public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteProxyException,DataAccessException,UseridIDExistsException;
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteProxyException;
	public void sendLostPassword(String userid) throws DataAccessException,RemoteProxyException;
}