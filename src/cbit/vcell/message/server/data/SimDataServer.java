/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.data;
import static cbit.vcell.message.messages.MessageConstants.MESSAGE_TYPE_PROPERTY;
import static cbit.vcell.message.messages.MessageConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE;
import static cbit.vcell.message.messages.MessageConstants.SERVICE_TYPE_PROPERTY;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCMessagingService.VCMessagingDelegate;
import cbit.vcell.message.VCRpcConsumer;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.message.server.jmx.VCellServiceMXBean;
import cbit.vcell.message.server.jmx.VCellServiceMXBeanImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimDataServer extends ServiceProvider implements ExportListener, DataJobListener {
	private DataServerImpl dataServerImpl = null;
	private VCRpcConsumer rpcConsumer = null;	


/**
 * @param serviceInstanceStatus
 * @param dataServerImpl
 * @param vcMessagingService
 * @param log
 * @throws Exception
 */
public SimDataServer(ServiceInstanceStatus serviceInstanceStatus, DataServerImpl dataServerImpl, VCMessagingService vcMessagingService, SessionLog log) throws Exception {
	super(vcMessagingService,serviceInstanceStatus,log);
	this.dataServerImpl = dataServerImpl;
}

private void init() throws Exception {
	
	String dataRequestFilter = "(" + MESSAGE_TYPE_PROPERTY + "='" + MESSAGE_TYPE_RPC_SERVICE_VALUE  + "') " +
									" AND (" + SERVICE_TYPE_PROPERTY + "='" + ServiceType.DATA.getName() + "')";
	String exportOnlyFilter = "(" + ServiceType.DATAEXPORT.getName() + " is NOT NULL)";
	String dataOnlyFilter = "(" + ServiceType.DATAEXPORT.getName() + " is NULL)";
	
	VCMessageSelector selector;
	ServiceType serviceType = serviceInstanceStatus.getType();
	if (serviceType == ServiceType.DATAEXPORT){
		selector = vcMessagingService.createSelector(dataRequestFilter+" AND "+exportOnlyFilter);
	}else if (serviceType == ServiceType.DATA){
		selector = vcMessagingService.createSelector(dataRequestFilter+" AND "+dataOnlyFilter);
	}else{
		throw new RuntimeException("expecting either Service type of "+ServiceType.DATA+" or "+ServiceType.DATAEXPORT);
	}

	rpcConsumer = new VCRpcConsumer(dataServerImpl, VCellQueue.DataRequestQueue, serviceType, selector, serviceType.getName()+" RPC Server Thread", MessageConstants.PREFETCH_LIMIT_DATA_REQUEST);

	VCMessagingDelegate delegate = new VCMessagingDelegate() {
		public void onMessagingException(Exception e) {
			log.exception(e);
		}
	};
	vcMessagingService.setDelegate(delegate);
	vcMessagingService.addMessageConsumer(rpcConsumer);
	
	initControlTopicListener();
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length < 1) {
		System.out.println("Missing arguments: " + SimDataServer.class.getName() + " serviceOrdinal [EXPORTONLY] [logdir]");
		System.exit(1);
	}
	
	try {
		PropertyLoader.loadProperties();
		
		int serviceOrdinal = Integer.parseInt(args[0]);		
		String logdir = null;
		boolean bExportOnly = false;	
		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("EXPORTONLY")) {
				bExportOnly = true;
				VCMongoMessage.serviceStartup(ServiceName.export, new Integer(serviceOrdinal), args);
				if (args.length > 2) {	
					logdir = args[2];
				}
			} else {
				VCMongoMessage.serviceStartup(ServiceName.simData, new Integer(serviceOrdinal), args);
				logdir = args[1];
			}
		}
		ServiceInstanceStatus serviceInstanceStatus = null;
		ServiceName serviceName = null;
		if (bExportOnly){
			serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.DATAEXPORT, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
			serviceName = ServiceName.export;
		}else{
			serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.DATA, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
			serviceName = ServiceName.simData;
		}
		initLog(serviceInstanceStatus, logdir);
		VCMongoMessage.serviceStartup(serviceName, new Integer(serviceOrdinal), args);

		//
		// JMX registration
		//
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));
 		
		final SessionLog log = new StdoutSessionLog("DataServer");
		Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20);
		DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(log, cacheTable, 
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty)), 
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirProperty)));
		
		ExportServiceImpl exportServiceImpl = new ExportServiceImpl(log);
		
		DataServerImpl dataServerImpl = new DataServerImpl(log, dataSetControllerImpl, exportServiceImpl);

		VCMessagingService vcMessagingService = VCMessagingService.createInstance();
		
        SimDataServer simDataServer = new SimDataServer(serviceInstanceStatus, dataServerImpl, vcMessagingService, log);
        //add dataJobListener
        dataSetControllerImpl.addDataJobListener(simDataServer);
        // add export listener
        exportServiceImpl.addExportListener(simDataServer);
        
        simDataServer.init();
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (3/31/2006 8:48:04 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(cbit.rmi.event.DataJobEvent event) {
	try {
		VCMessageSession dataSession = vcMessagingService.createProducerSession();
		VCMessage dataEventMessage = dataSession.createObjectMessage(event);
		dataEventMessage.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_DATA_EVENT_VALUE);
		dataEventMessage.setStringProperty(MessageConstants.USERNAME_PROPERTY, event.getUser().getName());
		
		dataSession.sendTopicMessage(VCellTopic.ClientStatusTopic, dataEventMessage);
		dataSession.close();
	} catch (VCMessagingException ex) {
		log.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 10:34:11 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void exportMessage(cbit.rmi.event.ExportEvent event) {
	try {
		VCMessageSession dataSession = vcMessagingService.createProducerSession();
		VCMessage exportEventMessage = dataSession.createObjectMessage(event);
		exportEventMessage.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_EXPORT_EVENT_VALUE);
		exportEventMessage.setStringProperty(MessageConstants.USERNAME_PROPERTY, event.getUser().getName());
		
		dataSession.sendTopicMessage(VCellTopic.ClientStatusTopic, exportEventMessage);
		dataSession.close();
	} catch (VCMessagingException ex) {
		log.exception(ex);
	}
}

}
