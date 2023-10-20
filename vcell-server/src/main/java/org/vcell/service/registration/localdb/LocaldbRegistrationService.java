package org.vcell.service.registration.localdb;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.service.registration.RegistrationService;
import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;

import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.server.AdminDatabaseServer;

public class LocaldbRegistrationService implements RegistrationService {
	private final static Logger lg = LogManager.getLogger(LocaldbRegistrationService.class);

	private AdminDatabaseServer adminDbServer = null;
	
	public LocaldbRegistrationService() {

	}
	
	private AdminDatabaseServer getAdminDbServer() throws DataAccessException {
		if (adminDbServer==null){
			ConnectionFactory conFactory;
			try {
				conFactory = DatabaseService.getInstance().createConnectionFactory();
				KeyFactory keyFactory = conFactory.getKeyFactory();
				adminDbServer = new LocalAdminDbServer(conFactory, keyFactory);
			} catch (SQLException e) {
				lg.error(e);
				throw new RuntimeException("failed to establish database connection for RegistrationService: "+e.getMessage(),e);
			} catch (DataAccessException e) {
				lg.error(e);
				throw e;
			}
		}
		return adminDbServer;
	}
	@Override
	public UserInfo insertUserInfo(UserInfo newUserInfo,boolean bUpdate) throws RemoteProxyException,DataAccessException,UseridIDExistsException{
		if(bUpdate){
			throw new IllegalArgumentException("UPDATE User Info: Must use ClientserverManager NOT LocalAdminDBServer");
		}else{
			return getAdminDbServer().insertUserInfo(newUserInfo);
		}
	}
	@Override
	public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException,RemoteProxyException{
		return getAdminDbServer().getUserInfo(userKey);
	}					
	@Override
	public void sendLostPassword(String userid) throws DataAccessException,RemoteProxyException{
		getAdminDbServer().sendLostPassword(userid);
	}
}
 