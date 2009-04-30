package cbit.vcell.client.test;

import cbit.sql.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Frame;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellConnectionFactory;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
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
			csInfo = ClientServerInfo.createLocalServerInfo(args[1], args[2]);
		} else {
			csInfo = ClientServerInfo.createRemoteServerInfo(new String[] {args[0]}, args[1], args[2]);
		}
	}else{
		System.err.println("usage: " + programName + " -local userid password");
		//System.err.println("usage: " + programName + " -jms userid password");
		System.err.println("usage: " + programName +" host userid password");
		throw new Exception("cannot connect");
	}
	ClientServerManager clientServerManager = new ClientServerManager();
	clientServerManager.connect(csInfo);
	
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
			csInfo = ClientServerInfo.createLocalServerInfo(args[1], args[2]);
		} else {
			csInfo = ClientServerInfo.createRemoteServerInfo(new String[] {args[0]}, args[1], args[2]);
		}
	}else{
		System.err.println("usage: " + programName + " -local userid password");
		//System.err.println("usage: " + programName + " -jms userid password");
		System.err.println("usage: " + programName +" host userid password");
		throw new Exception("cannot connect");
	}
	ClientServerManager clientServerManager = new ClientServerManager();
	clientServerManager.connect(csInfo);
	
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
	new cbit.vcell.server.PropertyLoader();
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
	new cbit.vcell.server.PropertyLoader();		
	org.vcell.util.document.User user = null;
	if (args[0].startsWith("-")) {
		String userid = args[1];
		String password = args[2];
		cbit.vcell.server.SessionLog log = new cbit.vcell.server.StdoutSessionLog(userid);
		if (args[0].equalsIgnoreCase("-jms")) {
			vcConnFactory = new cbit.vcell.server.LocalVCellConnectionFactory(userid, password, log, false);
		} else if (args[0].equalsIgnoreCase("-local")) {
			vcConnFactory = new cbit.vcell.server.LocalVCellConnectionFactory(userid, password, log, true);
			if (args.length == 7) {
				ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log, args[3], args[4], args[5], args[6]);
				((cbit.vcell.server.LocalVCellConnectionFactory)vcConnFactory).setConnectionFactory(conFactory);
			}
		}
	} else {
		String host = args[0];
		String userid = args[1];
		String password = args[2];
		vcConnFactory = new cbit.vcell.server.RMIVCellConnectionFactory(host, userid, password);
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
	cbit.vcell.server.PropertyLoader.loadProperties();
	org.vcell.util.document.User user = null;
	if (!args[0].equalsIgnoreCase("-local")) {
		String host = args[0];
		String userid = args[1];
		org.vcell.util.document.KeyValue userKey = new org.vcell.util.document.KeyValue(args[2]);
		user = new org.vcell.util.document.User(userid, userKey);
		String password = args[3];
		System.setSecurityManager(new java.rmi.RMISecurityManager());
		vcServerFactory = new cbit.vcell.server.RMIVCellServerFactory(host, user, password);
	} else {
		String userid = args[1];
		String password = args[2];
//		cbit.vcell.server.SessionLog log = new cbit.vcell.server.StdoutSessionLog(userid);
		cbit.vcell.server.SessionLog log = new cbit.vcell.modeldb.NullSessionLog();
		cbit.sql.ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
		cbit.sql.KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
		DBCacheTable dbCacheTable = new DBCacheTable(-1,10000000);
		cbit.vcell.messaging.JmsConnectionFactory jmsConnFactory = new cbit.vcell.messaging.JmsConnectionFactoryImpl();
		vcServerFactory = new cbit.vcell.server.LocalVCellServerFactory(userid,password,"<<local>>",jmsConnFactory,conFactory,keyFactory,dbCacheTable,log);
	}
	return vcServerFactory;
}
}
