package org.vcell.service.registration.remote;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.vcell.api.client.VCellApiClient;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.resource.PropertyLoader;

@Plugin(type = Service.class)
public class RemoteRegistrationService extends AbstractService implements RegistrationService {

	public RemoteRegistrationService() {
		setPriority(Priority.NORMAL_PRIORITY);
	}
		
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo, boolean bUpdate) throws RemoteProxyException, DataAccessException, UseridIDExistsException {
		String host = PropertyLoader.getRequiredProperty(PropertyLoader.vcellapiHost);
		int port = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.vcellapiPort));
		String clientID = PropertyLoader.getSecretValue(PropertyLoader.vcellapiClientid,  PropertyLoader.vcellapiClientidFile);
		boolean bIgnoreCertProblems = false;
		boolean bIgnoreHostMismatch = false;
		VCellApiClient apiClient;
		try {
			apiClient = new VCellApiClient(host, port, clientID, bIgnoreCertProblems, bIgnoreHostMismatch);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
			throw new RemoteProxyException("failure inserting user: "+e.getMessage(), e);
		}
		org.vcell.api.common.UserInfo apiUserInfo;
		try {
			apiUserInfo = apiClient.insertUserInfo(newUserInfo.getApiUserInfo());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RemoteProxyException("failed to insert user: "+e.getMessage(), e);
		}
		return UserInfo.fromApiUserInfo(apiUserInfo);
	}

	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException, RemoteProxyException {
		throw new DataAccessException("getUserInfo() requires authentication");
	}

	@Override
	public void sendLostPassword(String userid) throws DataAccessException, RemoteProxyException {
		String host = PropertyLoader.getRequiredProperty(PropertyLoader.vcellapiHost);
		int port = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.vcellapiPort));
		String clientID = PropertyLoader.getSecretValue(PropertyLoader.vcellapiClientid,  PropertyLoader.vcellapiClientidFile);
		boolean bIgnoreCertProblems = false;
		boolean bIgnoreHostMismatch = false;
		VCellApiClient apiClient;
		try {
			apiClient = new VCellApiClient(host, port, clientID, bIgnoreCertProblems, bIgnoreHostMismatch);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
			throw new RemoteProxyException("failure in send lost password request: "+e.getMessage(), e);
		}
		try {
			apiClient.sendLostPassword(userid);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RemoteProxyException("failed to request lost password: "+e.getMessage(), e);
		}
	}
	
}
 