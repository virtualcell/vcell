/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.console;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.service.VCellServiceHelper;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.XmlHelper;

/**
 * Insert the type's description here.
 * Creation date: (8/15/2003 4:19:19 PM)
 * @author: Fei Gao
 */
@SuppressWarnings("serial")
public class ServerManagerCLI {
	private SessionLog log = null;
	
	private VCMessagingService vcMessagingService = null;
	
	private final AdminDBTopLevel adminDbTop;
	private final DatabaseServerImpl dbServerImpl;
	

/**
 * ServerManageConsole constructor comment.
 */
public ServerManagerCLI(VCMessagingService vcMessagingService, AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl dbServerImpl, SessionLog log) throws java.io.IOException, java.io.FileNotFoundException, org.jdom.JDOMException, javax.jms.JMSException {
	super();
	this.vcMessagingService = vcMessagingService;
	this.adminDbTop = adminDbTopLevel;
	this.dbServerImpl = dbServerImpl;
	this.log = log;
}


/**
 * Return the QueryResultTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Object[][] getQueryResultTable(String userid, Number simID) {
	System.out.println("----- user="+userid+" simID="+simID);
	String mongoDbHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostInternal);
	int mongoDbPort = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortInternal)); // default 27017
	Mongo m = new Mongo(mongoDbHost,mongoDbPort);
	String mongoDbDatabaseName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
	DB db = m.getDB(mongoDbDatabaseName);
	String mongoDbLoggingCollectionName = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbLoggingCollection);
	DBCollection dbCollection = db.getCollection(mongoDbLoggingCollectionName);

	BasicDBObject query = new BasicDBObject();

	query.put(VCMongoMessage.MongoMessage_simId, simID.intValue()+"");

	DBCursor cur = dbCollection.find(query);
	TreeMap<String, Integer> mapKeyToColumnIndex = new TreeMap<String, Integer>();
	Vector<DBObject> dbObjV = new Vector<DBObject>();
	while(cur.hasNext()) {
		DBObject dbObject = cur.next();
		dbObjV.add(dbObject);
	    Set<String> keys = dbObject.keySet();
	    Iterator<String> iter = keys.iterator();
	    while(iter.hasNext()){
	    	String key = iter.next();
	    	Integer columnIndex = mapKeyToColumnIndex.get(key);
	    	if(columnIndex == null){
	    		columnIndex = mapKeyToColumnIndex.size();
	    		mapKeyToColumnIndex.put(key, columnIndex);
	    	}
	    }
	}
	int msgTimeColumnIndex = -1;
	if(mapKeyToColumnIndex.size()>0){
		String[] columnNames = new String[mapKeyToColumnIndex.size()];
		Iterator<String> keyIter = mapKeyToColumnIndex.keySet().iterator();
		while(keyIter.hasNext()){
			String key = keyIter.next();
			int columnIndex = mapKeyToColumnIndex.get(key);
			columnNames[columnIndex] = key;
			if(key.equals(VCMongoMessage.MongoMessage_msgTime)){
				msgTimeColumnIndex = columnIndex;
			}
		}
		
		Object[][] rowData = new Object[dbObjV.size()][columnNames.length];
		for (int i = 0; i < rowData.length; i++) {
			DBObject dbObj = dbObjV.get(i);
			Set<String> keys = dbObj.keySet();
		    Iterator<String> iter = keys.iterator();
		    while(iter.hasNext()){
		    	String key = iter.next();
		    	rowData[i][mapKeyToColumnIndex.get(key)] = dbObj.get(key);
		    }
		}
		//sort by msgtime
		final int msgTimeColumnIndexFinal = msgTimeColumnIndex;
		if(msgTimeColumnIndex != -1){
			Arrays.sort(rowData, new Comparator<Object[]>() {
				public int compare(Object[] o1, Object[] o2) {
					Long o1Long = (Long)o1[msgTimeColumnIndexFinal];
					Long o2Long = (Long)o2[msgTimeColumnIndexFinal];
					int result = (int)(o2Long-o1Long);
					return result;
				}
			});
		}
		return rowData;
	}else {
		return null;
	}

}



private SimpleJobStatus[] query(SimpleJobStatusQuerySpec querySpec) throws ObjectNotFoundException, DataAccessException {
	SimulationDatabase simDatabase = new SimulationDatabaseDirect(adminDbTop, dbServerImpl, false, log);
	SimpleJobStatus[] resultList = simDatabase.getSimpleJobStatus(null,querySpec);
	return resultList;
}


/**
 * @throws Exception 
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:32:52 PM)
 * @param simKey cbit.sql.KeyValue
 * @throws  
 */
public void resubmitSimulation(String userid, KeyValue simKey) throws Exception {
	User user = adminDbTop.getUser(userid, true);
	UserLoginInfo userLoginInfo = new UserLoginInfo(user.getName(),null);
	userLoginInfo.setUser(user);
	String apihost = "vcellapi.cam.uchc.edu";
	Integer apiport = 8080;
	RemoteProxyVCellConnectionFactory remoteProxyVCellConnectionFactory = new RemoteProxyVCellConnectionFactory(apihost, apiport, userLoginInfo);
	VCellConnection vcConn = remoteProxyVCellConnectionFactory.createVCellConnection();
	BigString simxml = vcConn.getUserMetaDbServer().getSimulationXML(simKey);
	if (simxml == null) {
		throw new RuntimeException("Simulation [" + simKey + "] doesn't exit, might have been deleted.");
	}
	Simulation sim = XmlHelper.XMLToSim(simxml.toString());
	if (sim == null) {
		throw new RuntimeException("Simulation [" + simKey + "] doesn't exit, might have been deleted.");
	}
	vcConn.getSimulationController().startSimulation(sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(),sim.getScanCount());
}


/**
 * Comment
 * @throws VCMessagingException 
 */
public void sendMessageButton_ActionPerformed(String broadcastMessage, String optionalRecipientUsername) throws VCMessagingException {
	try (VCMessageSession vcMessaging_clientTopicProducerSession = vcMessagingService.createProducerSession();)
	{
		String username = optionalRecipientUsername;
	
		if (username == null || username.equalsIgnoreCase(VCMessagingConstants.USERNAME_PROPERTY_VALUE_ALL)) {
			username = VCMessagingConstants.USERNAME_PROPERTY_VALUE_ALL;
		}
		try (Scanner scanner = new Scanner(System.in);){
			System.out.println("You are going to send message to " + optionalRecipientUsername + ". Continue? (y/N)");
		    String response = scanner.next();
		    if (response==null || !response.equalsIgnoreCase("y")) {
		    		throw new RuntimeException("broadcast message not confirmed"); 
		    }
		}
			
		VCMessage msg = vcMessaging_clientTopicProducerSession.createObjectMessage(new BigString(broadcastMessage));
			
		msg.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_BROADCASTMESSAGE_VALUE);
		msg.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, username);
		
		log.print("sending broadcast message [" + msg.show() + "]");		
		vcMessaging_clientTopicProducerSession.sendTopicMessage(VCellTopic.ClientStatusTopic, msg);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:32:52 PM)
 * @param simKey cbit.sql.KeyValue
 * @throws Exception 
 */
public void stopSimulation(String userid, KeyValue simKey) throws Exception {
	User user = adminDbTop.getUser(userid, true);
	UserLoginInfo userLoginInfo = new UserLoginInfo(user.getName(),null);
	userLoginInfo.setUser(user);
	String apihost = "vcellapi.cam.uchc.edu";
	Integer apiport = 8080;
	RemoteProxyVCellConnectionFactory remoteProxyVCellConnectionFactory = new RemoteProxyVCellConnectionFactory(apihost, apiport, userLoginInfo);
	VCellConnection vcConn = remoteProxyVCellConnectionFactory.createVCellConnection();
	BigString simxml = vcConn.getUserMetaDbServer().getSimulationXML(simKey);
	if (simxml == null) {
		throw new RuntimeException("Simulation [" + simKey + "] doesn't exit, might have been deleted.");
	}
	cbit.vcell.solver.Simulation sim = cbit.vcell.xml.XmlHelper.XMLToSim(simxml.toString());
	if (sim == null) {
		throw new RuntimeException("Simulation [" + simKey + "] doesn't exit, might have been deleted.");
	}
	vcConn.getSimulationController().stopSimulation(sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier());	
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	
	// create the command line parser
	CommandLineParser parser = new DefaultParser();

	// create the Options
	Options options = new Options();
	options.addOption( "q", "query", true, "JSON encoded query spec" );
	options.addOption( "a", "age" , true, "Age in hours");
	options.addOption( "h", "help", false, "display this help message" );

	try {
	    // parse the command line arguments
	    CommandLine line = parser.parse( options, args );
	    if (line.hasOption("help")) {
	    		HelpFormatter formatter = new HelpFormatter();
	    		formatter.printHelp("serverManagerCLI", options);
	    		System.exit(0);
	    }
	    if (line.getArgList().size()!=0) {
	    		HelpFormatter formatter = new HelpFormatter();
	    		formatter.printHelp("serverManagerCLI", options);
	    		System.exit(-1);
	    }
	    //String[] positionalArgs = line.getArgs();
	    //String command = positionalArgs[0];
	    // validate that block-size has been set
	    if (line.hasOption("query")) {
	    		String queryString = line.getOptionValue("query");
	    		System.out.println("query string = '"+queryString+"'");
	    		Gson gson = new Gson();
	    		SimpleJobStatusQuerySpec querySpec = gson.fromJson(queryString, SimpleJobStatusQuerySpec.class);
	    		String age = line.getOptionValue("age", "24");
	    		long ageInHours = Long.parseLong(age);
	    		querySpec.submitLowMS=System.currentTimeMillis()-(1000L*3600L*ageInHours);
	    		System.out.println("parsedQuerySpec="+gson.toJson(querySpec));
	    		try {
	    			PropertyLoader.loadProperties();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    			System.exit(-1);
	    		}
	    		try (VCMessagingService vcMessagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class))
	    		{		
	    			vcMessagingService.setDelegate(new ServerMessagingDelegate());

	    			SessionLog log = new StdoutSessionLog("Console");

	    			ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(log);
	    			KeyFactory keyFactory = conFactory.getKeyFactory();
	    			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory,keyFactory,log);
	    			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory,log);

	    			ServerManagerCLI manager = new ServerManagerCLI(vcMessagingService, adminDbTopLevel, databaseServerImpl, log);
	    			SimpleJobStatus[] simpleJobStatus = manager.query(querySpec);
	    			for (SimpleJobStatus s : simpleJobStatus) {
	    				System.out.println(Arrays.toString(new Object[] { (s.stateInfo!=null)?s.stateInfo.getShortDesc():null, s.jobStatus.getSchedulerStatus(), s.jobStatus.getSimulationExecutionStatus() }));
	    			}
	    			//manager.getQueryResultTable(userid, simID);
	    			conFactory.close();
	    		} catch (Throwable exception) {
	    			exception.printStackTrace(System.out);
	    		}
	    		System.exit(0);
	    }else {
	    		System.out.println("unknown command");
	    		HelpFormatter formatter = new HelpFormatter();
	    		formatter.printHelp("serverManagerCLI", options);
	    		System.exit(-1);
	    }
	}
	catch( Exception exp ) {
	    System.out.println( "Unexpected exception:" + exp.getMessage() );
	    System.exit(-1);
	}
}


}
