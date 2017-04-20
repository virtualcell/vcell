package cbit.vcell.client;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.sql.SQLException;

import oracle.ucp.UniversalConnectionPoolException;

import org.vcell.util.ConfigurationException;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.DocumentCreationInfo;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.importer.PathwayImportPanel.PathwayImportOption;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.client.FieldDataWindowManager.DataSymbolCallBack;
import cbit.vcell.client.TopLevelWindowManager.OpenModelInfoHolder;
import cbit.vcell.client.server.AsynchMessageManager;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.DataViewerController;
import cbit.vcell.client.server.MergedDatasetViewerController;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCMessagingService.VCMessagingDelegate;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.server.LocalVCellConnectionFactory;
import cbit.vcell.server.RMIVCellConnectionFactory;
import cbit.vcell.server.RMIVCellServerFactory;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnectionFactory;
import cbit.vcell.server.VCellServerFactory;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.xml.merge.XmlTreeDiff;
import cbit.xml.merge.gui.TMLPanel;
/**
 * This type was created in VisualAge.
 */
public class ClientFactory {


	public static ClientServerManager createLocalClientServerManager(String userid, String password) {
		DigestedPassword digestedPassword = new DigestedPassword(password);
		ClientServerInfo csInfo = ClientServerInfo.createLocalServerInfo(userid, digestedPassword);
		ClientServerManager clientServerManager = new ClientServerManager(csInfo);
		clientServerManager.connect(null);
		return clientServerManager;
	}
	
	public static ClientServerManager createRemoteClientServerManager(String hosts[], String username, String password) {
		DigestedPassword digestedPassword = new DigestedPassword(password);
		ClientServerInfo csInfo = ClientServerInfo.createRemoteServerInfo(hosts, username, digestedPassword);
		ClientServerManager clientServerManager = new ClientServerManager(csInfo);
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
		clientServerManager.connect(windowManager);
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
		VCMessagingService messagingService = VCMessagingServiceActiveMQ.createInstance(messagingDelegate);
		VCellServerFactory vcServerFactory = new cbit.vcell.server.LocalVCellServerFactory(adminUserid,digestedPassword,"<<local>>",messagingService,conFactory,keyFactory,log);
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
