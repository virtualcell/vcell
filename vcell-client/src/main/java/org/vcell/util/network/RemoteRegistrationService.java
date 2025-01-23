package org.vcell.util.network;

import cbit.vcell.message.server.bootstrap.client.RemoteProxyException;
import cbit.vcell.resource.PropertyLoader;
import org.vcell.api.client.VCellApiClient;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;
import org.vcell.api.types.utils.DTOOldAPI;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class RemoteRegistrationService implements RegistrationService {

	public RemoteRegistrationService() {
	}
		
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo, boolean bUpdate) throws RemoteProxyException, DataAccessException, UseridIDExistsException {
		try (VCellApiClient apiClient = getvCellApiClient()){
			org.vcell.api.types.common.UserInfo apiUserInfo = apiClient.insertUserInfo(DTOOldAPI.getApiUserInfo(newUserInfo));
			return DTOOldAPI.fromApiUserInfo(apiUserInfo);
		} catch (IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
			throw new RemoteProxyException("failed to insert user: "+e.getMessage(), e);
		}
	}

	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException, RemoteProxyException {
		throw new DataAccessException("getUserInfo() requires authentication");
	}

	@Override
	public void sendLostPassword(String userid) throws DataAccessException, RemoteProxyException {
		try (VCellApiClient apiClient = getvCellApiClient()){
			apiClient.sendLostPassword(userid);
		} catch (Exception e) {
			throw new RemoteProxyException("failed to request lost password: "+e.getMessage(), e);
		}
	}

	private static VCellApiClient getvCellApiClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		String serverHost = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerHost);
		String pathPrefixV0 = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerPrefixV0);
		String[] parts = serverHost.split(":");
		String host = parts[0];
		int port = Integer.parseInt(parts[1]);
		boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems, false);
		boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch, false);
		return new VCellApiClient(host, port, pathPrefixV0, bIgnoreCertProblems, bIgnoreHostMismatch);
	}
}
 