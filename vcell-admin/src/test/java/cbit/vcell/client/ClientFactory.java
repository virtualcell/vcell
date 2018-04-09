package cbit.vcell.client;

import java.awt.Component;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ClientServerManager.InteractiveContext;
import cbit.vcell.client.server.ClientServerManager.InteractiveContextDefaultProvider;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnectionFactory;
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
	
	public static ClientServerManager createRemoteClientServerManager(String apihost, Integer apiport, String username, String password) {
		DigestedPassword digestedPassword = new DigestedPassword(password);
		ClientServerInfo csInfo = ClientServerInfo.createRemoteServerInfo(apihost, apiport, username, digestedPassword);
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

	public static VCellConnectionFactory createRemoteVCellConnectionFactory(String host, int port, String userid, String password) throws Exception {
		DigestedPassword digestedPassword = new DigestedPassword(password);
		org.vcell.util.document.UserLoginInfo userLoginInfo = new org.vcell.util.document.UserLoginInfo(userid,digestedPassword);
		VCellConnectionFactory vcConnFactory = new RemoteProxyVCellConnectionFactory(host,port,userLoginInfo);
		return vcConnFactory;
	}
	
}
