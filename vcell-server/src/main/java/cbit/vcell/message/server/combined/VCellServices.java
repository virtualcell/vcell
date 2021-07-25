/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.combined;
import java.io.File;
import java.util.Date;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.batch.sim.HtcSimulationWorker;
import cbit.vcell.message.server.bootstrap.ServiceType;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSshNative;
import cbit.vcell.message.server.data.SimDataServer;
import cbit.vcell.message.server.data.SimDataServer.SimDataServiceType;
import cbit.vcell.message.server.db.DatabaseServer;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.message.server.dispatcher.SimulationDispatcher;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.slurm.SlurmProxy;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.LibraryLoaderThread;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.PythonSupport.PythonPackage;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solvers.AbstractSolver;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class VCellServices extends ServiceProvider implements ExportListener, DataJobListener {
	private SimulationDispatcher simulationDispatcher = null;
	private DatabaseServer databaseServer = null;
	private SimDataServer simDataServer = null;
	private SimDataServer exportDataServer = null;
	private HtcSimulationWorker htcSimulationWorker = null;

	private DataServerImpl dataServerImpl = null;
	private DatabaseServerImpl databaseServerImpl = null;
	private SimulationDatabase simulationDatabase = null;
	private HtcProxy htcProxy = null;

	VCMessageSession dataSession = null;
	VCMessageSession exportSession = null;


	/**
	 * Scheduler constructor comment.
	 */
	public VCellServices(HtcProxy htcProxy, VCMessagingService vcMessagingService_int, VCMessagingService vcMessagingService_sim, ServiceInstanceStatus serviceInstanceStatus, DatabaseServerImpl databaseServerImpl, DataServerImpl dataServerImpl, SimulationDatabase simulationDatabase) throws Exception {
		super(vcMessagingService_int,vcMessagingService_sim,serviceInstanceStatus,false);
		this.htcProxy = htcProxy;
		this.vcMessagingService_int = vcMessagingService_int;
		this.vcMessagingService_sim = vcMessagingService_sim;
		this.databaseServerImpl = databaseServerImpl;
		this.dataServerImpl = dataServerImpl;
		this.simulationDatabase = simulationDatabase;
	}


	public void init() throws Exception{
		ServiceInstanceStatus dispatcherServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.DISPATCH,99,ManageUtils.getHostName(), new Date(), true);
		simulationDispatcher = new SimulationDispatcher(htcProxy, vcMessagingService_int, vcMessagingService_sim, dispatcherServiceInstanceStatus, simulationDatabase, true);
		simulationDispatcher.init();

		ServiceInstanceStatus databaseServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.DB,99,ManageUtils.getHostName(), new Date(), true);
		databaseServer = new DatabaseServer(databaseServiceInstanceStatus,databaseServerImpl,vcMessagingService_int, true);
		databaseServer.init();

		ServiceInstanceStatus simDataServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.DATA,99,ManageUtils.getHostName(), new Date(), true);
		simDataServer = new SimDataServer(simDataServiceInstanceStatus,dataServerImpl,vcMessagingService_int,SimDataServiceType.SimDataOnly, true);
		simDataServer.init();

		ServiceInstanceStatus dataExportServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.DATAEXPORT,99,ManageUtils.getHostName(), new Date(), true);
		exportDataServer = new SimDataServer(dataExportServiceInstanceStatus,dataServerImpl,vcMessagingService_int, SimDataServiceType.ExportDataOnly, true);
		exportDataServer.init();

		ServiceInstanceStatus htcServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.PBSCOMPUTE,99,ManageUtils.getHostName(), new Date(), true);
		htcSimulationWorker = new HtcSimulationWorker(htcProxy, vcMessagingService_int, vcMessagingService_sim, htcServiceInstanceStatus, true);
		htcSimulationWorker.init();

		dataSession = vcMessagingService_int.createProducerSession();
		exportSession = vcMessagingService_int.createProducerSession();
	}

	@Override
	public void stopService(){
		super.stopService();

		simulationDispatcher.stopService();
		databaseServer.stopService();
		simDataServer.stopService();
		exportDataServer.stopService();
		htcSimulationWorker.stopService();
		dataSession.close();
		exportSession.close();
	}


	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(java.lang.String[] args) {
		OperatingSystemInfo.getInstance();

		if (args.length != 3 && args.length != 0) {
			System.out.println("Missing arguments: " + VCellServices.class.getName() + " [sshHost sshUser sshKeyFile] ");
			System.exit(1);
		}

		try {
			PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);
			ResourceUtil.setNativeLibraryDirectory();
			new LibraryLoaderThread(false).start( );
			PythonSupport.verifyInstallation(new PythonPackage[] { PythonPackage.VTK, PythonPackage.THRIFT});
			
			HtcProxy htcProxy = SlurmProxy.creatCommandService(args);

			int serviceOrdinal = 0;
			VCMongoMessage.serviceStartup(ServiceName.dispatch, new Integer(serviceOrdinal), args);

//			//
//			// JMX registration
//			//
//			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//			mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));

			ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),
					ServiceType.MASTER, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);

			ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory();
			KeyFactory keyFactory = conFactory.getKeyFactory();
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory);
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
			SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(adminDbTopLevel, databaseServerImpl, true);

			String cacheSize = PropertyLoader.getRequiredProperty(PropertyLoader.simdataCacheSizeProperty);
			long maxMemSize = Long.parseLong(cacheSize);

			Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20,maxMemSize);
			DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(cacheTable,
					new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty)),
					new File(PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirInternalProperty, PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty))));

			ExportServiceImpl exportServiceImpl = new ExportServiceImpl();

			DataServerImpl dataServerImpl = new DataServerImpl(dataSetControllerImpl, exportServiceImpl);        //add dataJobListener

			VCMessagingService vcMessagingService_int = new VCMessagingServiceActiveMQ();
			String jmshost_int = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
    		int jmsport_int = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
			vcMessagingService_int.setConfiguration(new ServerMessagingDelegate(), jmshost_int, jmsport_int);

			VCMessagingService vcMessagingService_sim = new VCMessagingServiceActiveMQ();
			String jmshost_sim = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostInternal);
    		int jmsport_sim = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortInternal));
			vcMessagingService_sim.setConfiguration(new ServerMessagingDelegate(), jmshost_sim, jmsport_sim);

			VCellServices vcellServices = new VCellServices(htcProxy, vcMessagingService_int, vcMessagingService_sim, serviceInstanceStatus, databaseServerImpl, dataServerImpl, simulationDatabase);

			dataSetControllerImpl.addDataJobListener(vcellServices);
	        exportServiceImpl.addExportListener(vcellServices);

			vcellServices.init();
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
	}

	public void dataJobMessage(cbit.rmi.event.DataJobEvent event) {
		synchronized(dataSession){
			try {
				//VCMessageSession dataSession = vcMessagingService.createProducerSession();
				VCMessage dataEventMessage = dataSession.createObjectMessage(event);
				dataEventMessage.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_DATA_EVENT_VALUE);
				dataEventMessage.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, event.getUser().getName());

				dataSession.sendTopicMessage(VCellTopic.ClientStatusTopic, dataEventMessage);
				//dataSession.close();
			} catch (VCMessagingException ex) {
				lg.error(ex.getMessage(),ex);
			}
		}
	}

	public void exportMessage(cbit.rmi.event.ExportEvent event) {
		synchronized(exportSession){
			try {
				//VCMessageSession dataSession = vcMessagingService.createProducerSession();
				VCMessage exportEventMessage = exportSession.createObjectMessage(event);
				exportEventMessage.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_EXPORT_EVENT_VALUE);
				exportEventMessage.setStringProperty(VCMessagingConstants.USERNAME_PROPERTY, event.getUser().getName());

				exportSession.sendTopicMessage(VCellTopic.ClientStatusTopic, exportEventMessage);
				//dataSession.close();
			} catch (VCMessagingException ex) {
				lg.error(ex.getMessage(),ex);
			}
		}
	}

	private static final String REQUIRED_SERVICE_PROPERTIES[] = {
		PropertyLoader.primarySimDataDirInternalProperty,
		PropertyLoader.primarySimDataDirExternalProperty,
		PropertyLoader.nativeSolverDir_External,
		PropertyLoader.vcellServerIDProperty,
		PropertyLoader.installationRoot,
		PropertyLoader.dbConnectURL,
		PropertyLoader.dbDriverName,
		PropertyLoader.dbUserid,
		PropertyLoader.dbPasswordFile,
		PropertyLoader.mongodbHostInternal,
		PropertyLoader.mongodbPortInternal,
		PropertyLoader.mongodbHostExternal,
		PropertyLoader.mongodbPortExternal,
		PropertyLoader.mongodbDatabase,
		PropertyLoader.jmsIntHostInternal,
		PropertyLoader.jmsIntPortInternal,
		PropertyLoader.jmsSimHostInternal,
		PropertyLoader.jmsSimPortInternal,
		PropertyLoader.jmsSimHostExternal,
		PropertyLoader.jmsSimPortExternal,
		PropertyLoader.jmsSimRestPortExternal,
		PropertyLoader.jmsUser,
		PropertyLoader.jmsPasswordFile,
		PropertyLoader.htcUser,
		PropertyLoader.htcLogDirExternal,
		PropertyLoader.htcLogDirInternal,
		PropertyLoader.slurm_tmpdir,
		PropertyLoader.pythonExe,
		PropertyLoader.jmsBlobMessageUseMongo,
		PropertyLoader.maxJobsPerScan,
		PropertyLoader.exportBaseURLProperty,
		PropertyLoader.exportBaseDirInternalProperty,
		PropertyLoader.simdataCacheSizeProperty
	};


}
