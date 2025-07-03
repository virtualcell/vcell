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

import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.*;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.bootstrap.ServiceType;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimDataServer implements ExportListener, DataJobListener {
	

	public enum SimDataServiceType {
		ExportDataOnly,
		SimDataOnly,
		CombinedData
	}

	public static final Logger lg = LogManager.getLogger(SimDataServer.class);

	private final VCMessagingService vcMessagingService_int;
	private final DataServerImpl dataServerImpl;
	private VCQueueConsumer rpcConsumer = null;	
	private VCRpcMessageHandler rpcMessageHandler = null;
	private VCPooledQueueConsumer pooledQueueConsumer = null;
	private VCMessageSession sharedProducerSession = null;
	private HttpServer server;


public SimDataServer() throws Exception {
	String cacheSize = PropertyLoader.getRequiredProperty(PropertyLoader.simdataCacheSizeProperty);
	long maxMemSize = Long.parseLong(cacheSize);

	Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20,maxMemSize);
	DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(cacheTable,
			new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty)),
			new File(PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirInternalProperty, PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty))));

	ExportServiceImpl exportServiceImpl = new ExportServiceImpl();

	this.dataServerImpl = new DataServerImpl(dataSetControllerImpl, exportServiceImpl);

	this.vcMessagingService_int = new VCMessagingServiceActiveMQ();
	String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
	int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
	this.vcMessagingService_int.setConfiguration(new ServerMessagingDelegate(), jmshost, jmsport);

	dataSetControllerImpl.addDataJobListener(this);
	// add export listener
	exportServiceImpl.getEventCreator().addExportListener(this);
}

public void init(SimDataServiceType serviceType) throws Exception {
	
	String dataRequestFilter = "(" + VCMessagingConstants.MESSAGE_TYPE_PROPERTY + "='" + VCMessagingConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE  + "') " +
									" AND (" + VCMessagingConstants.SERVICE_TYPE_PROPERTY + "='" + ServiceType.DATA.getName() + "')";
	String exportOnlyFilter = "(" + ServiceType.DATAEXPORT.getName() + " is NOT NULL)";
	String dataOnlyFilter = "(" + ServiceType.DATAEXPORT.getName() + " is NULL)";
	
	VCMessageSelector selector;
	int numThreads;
	switch (serviceType) {
	case CombinedData: {
		selector = vcMessagingService_int.createSelector(dataRequestFilter);
		int exportThreads = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.exportdataThreadsProperty, "3"));
		int simdataThreads = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.simdataThreadsProperty, "5"));
		numThreads = exportThreads + simdataThreads;
		break;
	}
	case ExportDataOnly: {
		selector = vcMessagingService_int.createSelector(dataRequestFilter+" AND "+exportOnlyFilter);
		numThreads = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.exportdataThreadsProperty, "3"));
		break;
	}
	case SimDataOnly: {
		selector = vcMessagingService_int.createSelector(dataRequestFilter+" AND "+dataOnlyFilter);
		numThreads = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.simdataThreadsProperty, "5"));
		break;
	}
	default: {
		throw new RuntimeException("expecting either Service type of "+ServiceType.DATA+" or "+ServiceType.DATAEXPORT);
	}
	}

	this.sharedProducerSession = vcMessagingService_int.createProducerSession();
	rpcMessageHandler = new VCRpcMessageHandler(dataServerImpl, VCellQueue.DataRequestQueue);
	this.pooledQueueConsumer = new VCPooledQueueConsumer(rpcMessageHandler, numThreads, sharedProducerSession);
	this.pooledQueueConsumer.initThreadPool();
	rpcConsumer = new VCQueueConsumer(VCellQueue.DataRequestQueue, pooledQueueConsumer, selector, serviceType.name()+" RPC Server Thread", MessageConstants.PREFETCH_LIMIT_DATA_REQUEST);

	vcMessagingService_int.addMessageConsumer(rpcConsumer);
}

public void dataJobMessage(cbit.rmi.event.DataJobEvent event) {
	try {
		VCMessageSession dataSession = vcMessagingService_int.createProducerSession();
		VCMessage dataEventMessage = dataSession.createObjectMessage(event);
		dataEventMessage.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_DATA_EVENT_VALUE);
		dataEventMessage.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, event.getUser().getName());
		
		dataSession.sendTopicMessage(VCellTopic.ClientStatusTopic, dataEventMessage);
		dataSession.close();
	} catch (VCMessagingException ex) {
		lg.error(ex.getMessage(), ex);
	}
}

public void exportMessage(cbit.rmi.event.ExportEvent event) {
	try {
		VCMessageSession dataSession = vcMessagingService_int.createProducerSession();
		VCMessage exportEventMessage = dataSession.createObjectMessage(event);
		exportEventMessage.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_EXPORT_EVENT_VALUE);
		exportEventMessage.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, event.getUser().getName());
		
		dataSession.sendTopicMessage(VCellTopic.ClientStatusTopic, exportEventMessage);
		dataSession.close();
	} catch (VCMessagingException ex) {
		lg.error(ex.getMessage(), ex);
	}
}

}
