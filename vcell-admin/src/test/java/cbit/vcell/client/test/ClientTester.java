package cbit.vcell.client.test;

import java.awt.Frame;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.client.VCellGuiInteractiveContextDefaultProvider;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory;
/**
 * This type was created in VisualAge.
 */
public class ClientTester {
/**
 * ClientTester constructor comment.
 */
public ClientTester() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return VCellConnection
 * @param args java.lang.String[]
 */
public static cbit.vcell.client.server.ClientServerManager mainInit(String args[], String programName) throws Exception {
	ClientServerInfo csInfo = null;
	if (args.length == 3) {
		if (args[0].equalsIgnoreCase("-local")) {
			csInfo = ClientServerInfo.createLocalServerInfo(args[1], new UserLoginInfo.DigestedPassword(args[2]));
		}
	} else if (args.length == 4) {
		csInfo = ClientServerInfo.createRemoteServerInfo(args[0], Integer.parseInt(args[1]), args[2], new UserLoginInfo.DigestedPassword(args[3]));
	}else{
		System.err.println("usage: " + programName + " -local userid password");
		//System.err.println("usage: " + programName + " -jms userid password");
		System.err.println("usage: " + programName +" apihost apiport userid password");
		throw new Exception("cannot connect");
	}
	VCellGuiInteractiveContextDefaultProvider defaultRequester = new VCellGuiInteractiveContextDefaultProvider();
	ClientServerManager clientServerManager = new ClientServerManager(csInfo,defaultRequester);
	clientServerManager.connect(null);
	
	return clientServerManager;
}
/**
 * This method was created in VisualAge.
 * @return VCellConnection
 * @param args java.lang.String[]
 */
public static cbit.vcell.client.server.ClientServerManager mainInit(String args[], String programName, Frame mainWindow) throws Exception {
	ClientServerInfo csInfo = null;
	if (args.length == 3) {
		if (args[0].equalsIgnoreCase("-local")) {
			csInfo = ClientServerInfo.createLocalServerInfo(args[1], new UserLoginInfo.DigestedPassword(args[2]));
		}
	}else if (args.length == 4) {
		csInfo = ClientServerInfo.createRemoteServerInfo(args[0], Integer.parseInt(args[1]), args[2], new UserLoginInfo.DigestedPassword(args[3]));
	}else{
		System.err.println("usage: " + programName + " -local userid password");
		//System.err.println("usage: " + programName + " -jms userid password");
		System.err.println("usage: " + programName +" apihost apiport userid password");
		throw new Exception("cannot connect");
	}
	VCellGuiInteractiveContextDefaultProvider defaultRequester = new VCellGuiInteractiveContextDefaultProvider();
	ClientServerManager clientServerManager = new ClientServerManager(csInfo, defaultRequester);
	clientServerManager.connect(null);
	
	return clientServerManager;
}
/**
 * This method was created in VisualAge.
 * @return VCellConnection
 * @param args java.lang.String[]
 */
protected static cbit.vcell.server.VCellConnectionFactory VCellConnectionFactoryInit(String args[], String programName) throws Exception {
	if (args.length != 3 && args.length != 4 && args.length != 7) {
		System.err.println("usage: " + programName + " -local userid password [driverName connectionURL userid password]");
		System.err.println("usage: " + programName + " -jms userid password");
		System.err.println("usage: " + programName +" host port userid password");
		throw new Exception("cannot connect");
	}
	cbit.vcell.server.VCellConnectionFactory vcConnFactory = null;
	new cbit.vcell.resource.PropertyLoader();		
	if (args[0].startsWith("-")) {
		UserLoginInfo userLoginInfo = new UserLoginInfo(args[1], new UserLoginInfo.DigestedPassword(args[2])); 
		if (args[0].equalsIgnoreCase("-jms")) {
			vcConnFactory = new cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory(userLoginInfo);
		} else if (args[0].equalsIgnoreCase("-local")) {
			vcConnFactory = new cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory(userLoginInfo);
			if (args.length == 7) {
				ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(
						args[3], args[4], args[5], args[6]);
				((cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory)vcConnFactory).setConnectionFactory(conFactory);
			}
		}
	} else {
		String apihost = args[0];
		Integer apiport = Integer.parseInt(args[1]);
		UserLoginInfo userLoginInfo = new UserLoginInfo(args[2], new UserLoginInfo.DigestedPassword(args[3]));
		vcConnFactory = new RemoteProxyVCellConnectionFactory(apihost,apiport,userLoginInfo);
	} 
	return vcConnFactory;
}
}
