package cbit.vcell.client;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.sql.SQLException;

import org.vcell.service.VCellServiceHelper;
import org.vcell.util.ConfigurationException;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ClientServerManager.InteractiveContext;
import cbit.vcell.client.server.ClientServerManager.InteractiveContextDefaultProvider;
import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessagingDelegate;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory;
import cbit.vcell.message.server.bootstrap.VCellServerFactory;
import cbit.vcell.message.server.bootstrap.client.RMIVCellConnectionFactory;
import cbit.vcell.message.server.bootstrap.client.RMIVCellServerFactory;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnectionFactory;
import oracle.ucp.UniversalConnectionPoolException;
/**
 * This type was created in VisualAge.
 */
public class ClientFactory {


	public static ClientServerManager createLocalClientServerManager(String userid, String password) {
		DigestedPassword digestedPassword = new DigestedPassword(password);
		ClientServerInfo csInfo = ClientServerInfo.createLocalServerInfo(userid, digestedPassword);
		InteractiveContextDefaultProvider defaultInteractiveContextProvider = new VCellGuiInteractiveContextDefaultProvider();
		ClientServerManager clientServerManager = new ClientServerManager(csInfo, defaultInteractiveContextProvider);
		clientServerManager.connect(null);
		return clientServerManager;
	}
	
	public static ClientServerManager createRemoteClientServerManager(String hosts[], String username, String password) {
		DigestedPassword digestedPassword = new DigestedPassword(password);
		ClientServerInfo csInfo = ClientServerInfo.createRemoteServerInfo(hosts, username, digestedPassword);
		InteractiveContextDefaultProvider defaultInteractiveContextProvider = new VCellGuiInteractiveContextDefaultProvider();
		ClientServerManager clientServerManager = new ClientServerManager(csInfo, defaultInteractiveContextProvider);
		RequestManagerAdapter requestManager = new RequestManagerAdapter();
		TopLevelWindowManager windowManager = new TopLevelWindowManager(requestManager){
			@Override
			public Component getComponent() {
				return null;
			}
			@Override
			public String getManagerID() {
				return null;
			}
			@Override
			public boolean isRecyclable() {
				return false;
			}
		};
		InteractiveContext requester = new VCellGuiInteractiveContext(windowManager);
		clientServerManager.connect(requester);
		return clientServerManager;
	}
	
	public static VCellBootstrap getRemoteVCellBootstrap(String host, int port) throws MalformedURLException, RemoteException, NotBoundException {
		String SERVICE_NAME = "VCellBootstrapServer";
		String connectString = "//"+host+":"+port+"/"+SERVICE_NAME;
		VCellBootstrap vcellBootstrap = (VCellBootstrap)Naming.lookup(connectString);
		return vcellBootstrap;
	}

	public static VCellConnectionFactory createLocalVCellConnectionFactory(String userid, String password) throws Exception {
		SessionLog log = new StdoutSessionLog(userid);
		DigestedPassword digestedPassword = new DigestedPassword(password);
		UserLoginInfo userLoginInfo = new UserLoginInfo(userid,digestedPassword);
		VCellConnectionFactory vcConnFactory = new LocalVCellConnectionFactory(userLoginInfo, log);
		return vcConnFactory;
	}
	
	public static VCellConnectionFactory createRemoteVCellConnectionFactory(String host, int port, String userid, String password) throws Exception {
		DigestedPassword digestedPassword = new DigestedPassword(password);
		org.vcell.util.document.UserLoginInfo userLoginInfo = new org.vcell.util.document.UserLoginInfo(userid,digestedPassword);
		VCellConnectionFactory vcConnFactory = new RMIVCellConnectionFactory(host+":"+port,userLoginInfo);
		return vcConnFactory;
	}
	
	public static VCellServerFactory createLocalVCellServerFactory(String adminUserid, String adminPassword) 
			throws FileNotFoundException, SQLException, DataAccessException, ClassNotFoundException, 
					IllegalAccessException, InstantiationException, ConfigurationException, 
					UniversalConnectionPoolException, VCMessagingException{
		
		SessionLog log = new StdoutSessionLog(adminUserid);
		DigestedPassword digestedPassword = new DigestedPassword(adminPassword);
		ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
		KeyFactory keyFactory = new OracleKeyFactory();
		VCMessagingDelegate messagingDelegate = new SimpleMessagingDelegate();
		VCMessagingService messagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class);
		messagingService.setDelegate(messagingDelegate);
		VCellServerFactory vcServerFactory = new cbit.vcell.message.server.bootstrap.LocalVCellServerFactory(adminUserid,digestedPassword,"<<local>>",messagingService,conFactory,keyFactory,log);
		return vcServerFactory;
	}
	
	public static VCellServerFactory getRemoteVCellServerFactory(String host, String adminUserid, String adminUserKey, String adminPassword){
		DigestedPassword digestedPassword = new DigestedPassword(adminPassword);
		org.vcell.util.document.KeyValue userKey = new KeyValue(adminUserKey);
		User user = new User(adminUserid, userKey);
		System.setSecurityManager(new RMISecurityManager());
		VCellServerFactory vcServerFactory = new RMIVCellServerFactory(host, user, digestedPassword);
		return vcServerFactory;
	}
}
