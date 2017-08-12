package cbit.vcell.client.test;

import java.awt.Frame;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.service.VCellServiceHelper;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.client.VCellGuiInteractiveContextDefaultProvider;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessagingService;
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
		} else {
			csInfo = ClientServerInfo.createRemoteServerInfo(new String[] {args[0]}, args[1], new UserLoginInfo.DigestedPassword(args[2]));
		}
	}else{
		System.err.println("usage: " + programName + " -local userid password");
		//System.err.println("usage: " + programName + " -jms userid password");
		System.err.println("usage: " + programName +" host userid password");
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
		} else {
			csInfo = ClientServerInfo.createRemoteServerInfo(new String[] {args[0]}, args[1], new UserLoginInfo.DigestedPassword(args[2]));
		}
	}else{
		System.err.println("usage: " + programName + " -local userid password");
		//System.err.println("usage: " + programName + " -jms userid password");
		System.err.println("usage: " + programName +" host userid password");
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
protected static cbit.vcell.server.VCellBootstrap VCellBootstrapInit(String args[], String programName) throws Exception {
	if (args.length != 5) {
		System.err.println("usage: "+programName+" host port AdminUserid AdminUserKey AdminPassword");
		throw new Exception("cannot connect");
	}
	cbit.vcell.server.VCellServerFactory vcServerFactory = null;
	new cbit.vcell.resource.PropertyLoader();
	if (!args[0].equalsIgnoreCase("-local")) {
		try {
			String SERVICE_NAME = "VCellBootstrapServer";
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			String connectString = "//"+host+":"+port+"/"+SERVICE_NAME;
			cbit.vcell.server.VCellBootstrap vcellBootstrap = (cbit.vcell.server.VCellBootstrap)java.rmi.Naming.lookup(connectString);
			return vcellBootstrap;
		} catch (Throwable e){
			throw new Exception("cannot contact server: "+e.getMessage());
		}
	} else {
		throw new Exception("must use remote connection");
	}
}
/**
 * This method was created in VisualAge.
 * @return VCellConnection
 * @param args java.lang.String[]
 */
protected static cbit.vcell.server.VCellConnectionFactory VCellConnectionFactoryInit(String args[], String programName) throws Exception {
	if (args.length != 3 && args.length != 7) {
		System.err.println("usage: " + programName + " -local userid password [driverName connectionURL userid password]");
		System.err.println("usage: " + programName + " -jms userid password");
		System.err.println("usage: " + programName +" host userid password");
		throw new Exception("cannot connect");
	}
	cbit.vcell.server.VCellConnectionFactory vcConnFactory = null;
	new cbit.vcell.resource.PropertyLoader();		
	UserLoginInfo userLoginInfo = new UserLoginInfo(args[1], new UserLoginInfo.DigestedPassword(args[2]));
	if (args[0].startsWith("-")) {
		org.vcell.util.SessionLog log = new cbit.vcell.resource.StdoutSessionLog(userLoginInfo.getUserName());
		if (args[0].equalsIgnoreCase("-jms")) {
			vcConnFactory = new cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory(userLoginInfo, log);
		} else if (args[0].equalsIgnoreCase("-local")) {
			vcConnFactory = new cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory(userLoginInfo, log);
			if (args.length == 7) {
				ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(
						log, args[3], args[4], args[5], args[6]);
				((cbit.vcell.message.server.bootstrap.LocalVCellConnectionFactory)vcConnFactory).setConnectionFactory(conFactory);
			}
		}
	} else {
		String host = args[0];
		vcConnFactory = new cbit.vcell.message.server.bootstrap.client.RMIVCellConnectionFactory(host,userLoginInfo);
	} 
	return vcConnFactory;
}
/**
 * This method was created in VisualAge.
 * @return VCellConnection
 * @param args java.lang.String[]
 */
protected static cbit.vcell.server.VCellServerFactory VCellServerFactoryInit(String args[], String programName) throws Exception {
	if (args.length != 3 && args.length != 4) {
		System.err.println("usage: "+programName+" [-local] userid password");
		System.err.println("usage: "+programName+" host userid userkey password");
		throw new Exception("cannot connect");
	}
	cbit.vcell.server.VCellServerFactory vcServerFactory = null;
	cbit.vcell.resource.PropertyLoader.loadProperties();
	org.vcell.util.document.User user = null;
	if (!args[0].equalsIgnoreCase("-local")) {
		String host = args[0];
		String userid = args[1];
		org.vcell.util.document.KeyValue userKey = new org.vcell.util.document.KeyValue(args[2]);
		user = new org.vcell.util.document.User(userid, userKey);
		String password = args[3];
		System.setSecurityManager(new java.rmi.RMISecurityManager());
		vcServerFactory = new cbit.vcell.message.server.bootstrap.client.RMIVCellServerFactory(host, user, new UserLoginInfo.DigestedPassword(password));
	} else {
		String userid = args[1];
		String password = args[2];
//		cbit.vcell.server.SessionLog log = new cbit.vcell.server.StdoutSessionLog(userid);
		org.vcell.util.SessionLog log = new org.vcell.util.NullSessionLog();
		ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(log);
		KeyFactory keyFactory = conFactory.getKeyFactory();
		VCMessagingService vcMessagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class);
		vcMessagingService.setDelegate(new SimpleMessagingDelegate());
		vcServerFactory = new cbit.vcell.message.server.bootstrap.LocalVCellServerFactory(userid,new UserLoginInfo.DigestedPassword(password),"<<local>>",vcMessagingService,conFactory,keyFactory,log);
	}
	return vcServerFactory;
}
}
