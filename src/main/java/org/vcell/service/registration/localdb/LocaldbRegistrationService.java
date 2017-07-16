package org.vcell.service.registration.localdb;

import java.rmi.RemoteException;
import java.sql.SQLException;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.server.AdminDatabaseServer;

@Plugin(type = Service.class)
public class LocaldbRegistrationService extends AbstractService implements RegistrationService {

	private AdminDatabaseServer adminDbServer = null;
	
	public LocaldbRegistrationService() {
		setPriority(Priority.LOW_PRIORITY);	
	}
	
	private AdminDatabaseServer getAdminDbServer() throws DataAccessException {
		if (adminDbServer==null){
			SessionLog log = new StdoutSessionLog("Local");
			ConnectionFactory conFactory;
			try {
				conFactory = DatabaseService.getInstance().createConnectionFactory(log);
				KeyFactory keyFactory = conFactory.getKeyFactory();
				adminDbServer = new LocalAdminDbServer(conFactory, keyFactory, log);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to establish database connection for RegistrationService: "+e.getMessage(),e);
			} catch (DataAccessException e) {
				e.printStackTrace();
				throw e;
			}
		}
		return adminDbServer;
	}
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteException,DataAccessException,UseridIDExistsException{
		if(bUpdate){
			throw new IllegalArgumentException("UPDATE User Info: Must use ClientserverManager NOT LocalAdminDBServer");
		}else{
			return getAdminDbServer().insertUserInfo(newUserInfo);
		}
	}
	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteException{
		return getAdminDbServer().getUserInfo(userKey);
	}					
	@Override
	public void sendLostPassword(String userid) throws DataAccessException,RemoteException{
		getAdminDbServer().sendLostPassword(userid);
	}
}
 