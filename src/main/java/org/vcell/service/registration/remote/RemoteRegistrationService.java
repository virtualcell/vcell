package org.vcell.service.registration.remote;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

import cbit.vcell.message.server.bootstrap.client.RMIVCellConnectionFactory;
import cbit.vcell.server.VCellBootstrap;

@Plugin(type = Service.class)
public class RemoteRegistrationService extends AbstractService implements RegistrationService {

	private VCellBootstrap vcellBootstrap = null;
	
	public RemoteRegistrationService() {
		setPriority(Priority.NORMAL_PRIORITY);			
	}
	
	private VCellBootstrap getVCellBootstrap() throws RemoteException {
		if (vcellBootstrap==null){
			String hostList = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerHost);
			String[] hosts = hostList.split(";");
			VCellBootstrap vcellBootstrap = null;
			for (int i = 0; i < hosts.length; i ++) {
				try {
					vcellBootstrap = (VCellBootstrap) java.rmi.Naming.lookup("//" + hosts[i]	+ "/" + RMIVCellConnectionFactory.SERVICE_NAME);
					vcellBootstrap.getVCellSoftwareVersion(); // test connection
					break;
				} catch (RemoteException ex) {
					ex.printStackTrace();
					if (i == hosts.length - 1) {
						throw ex;
					}
				} catch (MalformedURLException ex) {
					ex.printStackTrace();
					throw new RuntimeException("malformed URL "+hosts[i],ex);
				} catch (NotBoundException ex) {
					ex.printStackTrace();
					if (i == hosts.length -1) {
						throw new RuntimeException("malformed URL "+hosts[i],ex);
					}
				}
			}
		}
		return vcellBootstrap;
	}
	
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo, boolean bUpdate) throws RemoteException, DataAccessException, UseridIDExistsException {
		if(bUpdate){
			throw new IllegalArgumentException("UPDATE User Info: Must use ClientServerManager NOT VCellBootstrap");
		}else{
			return getVCellBootstrap().insertUserInfo(newUserInfo);
		}
	}

	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException, RemoteException {
		throw new DataAccessException("UserInfo not provided by VCellBootstrap");
	}

	@Override
	public void sendLostPassword(String userid) throws DataAccessException, RemoteException {
		getVCellBootstrap().sendLostPassword(userid);
	}
	
}
 