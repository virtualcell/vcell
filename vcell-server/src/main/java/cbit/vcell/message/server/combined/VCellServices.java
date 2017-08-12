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
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.service.VCellServiceHelper;
import org.vcell.util.LifeSignThread;
import org.vcell.util.SessionLog;
import org.vcell.util.document.VCellServerID;
import org.vcell.util.logging.WatchLogging;

import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.data.SimDataServer;
import cbit.vcell.message.server.db.DatabaseServer;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.message.server.dispatcher.SimulationDispatcher;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.pbs.PbsProxy;
import cbit.vcell.message.server.htc.sge.SgeProxy;
import cbit.vcell.message.server.htc.slurm.SlurmProxy;
import cbit.vcell.message.server.jmx.VCellServiceMXBean;
import cbit.vcell.message.server.jmx.VCellServiceMXBeanImpl;
import cbit.vcell.message.server.sim.HtcSimulationWorker;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.LibraryLoaderThread;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.resource.StdoutSessionLogConcurrent;
import cbit.vcell.resource.StdoutSessionLogConcurrent.LifeSignInfo;
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
	public VCellServices(HtcProxy htcProxy, VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, DatabaseServerImpl databaseServerImpl, DataServerImpl dataServerImpl, SimulationDatabase simulationDatabase, SessionLog log) throws Exception {
		super(vcMessagingService,serviceInstanceStatus,log,false);
		this.htcProxy = htcProxy;
		this.vcMessagingService = vcMessagingService;
		this.databaseServerImpl = databaseServerImpl;
		this.dataServerImpl = dataServerImpl;
		this.simulationDatabase = simulationDatabase;
	}


	public void init() throws Exception{
		initControlTopicListener();

		ServiceInstanceStatus dispatcherServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.DISPATCH,99,ManageUtils.getHostName(), new Date(), true);
		simulationDispatcher = new SimulationDispatcher(htcProxy, vcMessagingService, dispatcherServiceInstanceStatus, simulationDatabase, new StdoutSessionLog("DISPATCH"), true);
		simulationDispatcher.init();

		ServiceInstanceStatus databaseServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.DB,99,ManageUtils.getHostName(), new Date(), true);
		databaseServer = new DatabaseServer(databaseServiceInstanceStatus,databaseServerImpl,vcMessagingService,new StdoutSessionLog("DB"), true);
		databaseServer.init();

		ServiceInstanceStatus simDataServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.DATA,99,ManageUtils.getHostName(), new Date(), true);
		simDataServer = new SimDataServer(simDataServiceInstanceStatus,dataServerImpl,vcMessagingService,new StdoutSessionLog("DATA"), true);
		simDataServer.init();

		ServiceInstanceStatus dataExportServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.DATAEXPORT,99,ManageUtils.getHostName(), new Date(), true);
		exportDataServer = new SimDataServer(dataExportServiceInstanceStatus,dataServerImpl,vcMessagingService,new StdoutSessionLog("EXPORTDATA"), true);
		exportDataServer.init();

		ServiceInstanceStatus htcServiceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),ServiceType.PBSCOMPUTE,99,ManageUtils.getHostName(), new Date(), true);
		htcSimulationWorker = new HtcSimulationWorker(htcProxy, vcMessagingService, htcServiceInstanceStatus,new StdoutSessionLog("PBSCOMPUTE"), true);
		htcSimulationWorker.init();

		dataSession = vcMessagingService.createProducerSession();
		exportSession = vcMessagingService.createProducerSession();
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
		WatchLogging.init(TimeUnit.MINUTES.toMillis(5), "vcell.watchLog4JInterval");
		OperatingSystemInfo.getInstance();

		if (args.length != 3 && args.length != 6) {
			System.out.println("Missing arguments: " + VCellServices.class.getName() + " serviceOrdinal (logdir|-) (PBS|SGE|SLURM) [pbshost userid pswd] ");
			System.exit(1);
		}

		try {
			PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);
			CommandService.bQuiet = true;
			String libDir = PropertyLoader.getRequiredProperty(PropertyLoader.NATIVE_LIB_DIR);
			ResourceUtil.setNativeLibraryDirectory(libDir);
			new LibraryLoaderThread(false).start( );

			int serviceOrdinal = Integer.parseInt(args[0]);
			String logdir = null;
			if (args.length > 1) {
				logdir = args[1];
			}

			BatchSystemType batchSystemType = BatchSystemType.valueOf(args[2]);
			CommandService commandService = null;
			if (args.length==6){
				String htc_ssh_host = args[3];
				String htc_ssh_user = args[4];
				File htc_ssh_dsaKeyFile = new File(args[5]);
				commandService = new CommandServiceSsh(htc_ssh_host,htc_ssh_user,htc_ssh_dsaKeyFile);
				AbstractSolver.bMakeUserDirs = false; // can't make user directories, they are remote.
			}else{
				commandService = new CommandServiceLocal();
			}
			HtcProxy htcProxy = null;
			switch(batchSystemType){
				case PBS:{
					htcProxy = new PbsProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
					break;
				}
				case SGE:{
					htcProxy = new SgeProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
					break;
				}
				case SLURM:{
					htcProxy = new SlurmProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
					break;
				}
				default: {
					throw new RuntimeException("unrecognized batch scheduling option :"+batchSystemType);
				}
			}

			VCMongoMessage.serviceStartup(ServiceName.dispatch, new Integer(serviceOrdinal), args);

			//
			// JMX registration
			//
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));

			ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(),
					ServiceType.MASTER, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);

			OutputStream os = initLog(serviceInstanceStatus, logdir);
			SessionLog log  = null;
			if (os != null) {
				Objects.requireNonNull(os);

				final StdoutSessionLogConcurrent sslc = new StdoutSessionLogConcurrent(serviceInstanceStatus.getID(),os, new LifeSignInfo( ));
				final PrintStream concurrentPrintStream = sslc.printStreamFacade();
				System.setOut(concurrentPrintStream);
				System.setErr(concurrentPrintStream);
				log = sslc;
			}
			else {
				int lifeSignMessageInterval_MS = 3*60000; //3 minutes -- possibly make into a property later
				log = new StdoutSessionLog(serviceInstanceStatus.getID());
				new LifeSignThread(log,lifeSignMessageInterval_MS).start();
			}

			ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(log);
			KeyFactory keyFactory = conFactory.getKeyFactory();
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, log);
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory, log);
			SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(adminDbTopLevel, databaseServerImpl, true, log);

			Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20);
			DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(log, cacheTable,
					new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty)),
					new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirProperty)));

			ExportServiceImpl exportServiceImpl = new ExportServiceImpl(log);

			DataServerImpl dataServerImpl = new DataServerImpl(log, dataSetControllerImpl, exportServiceImpl);        //add dataJobListener

			VCMessagingService vcMessagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class);
			vcMessagingService.setDelegate(new ServerMessagingDelegate());

			VCellServices vcellServices = new VCellServices(htcProxy, vcMessagingService, serviceInstanceStatus, databaseServerImpl, dataServerImpl, simulationDatabase, log);

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
				log.exception(ex);
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
				log.exception(ex);
			}
		}
	}

	private static final String REQUIRED_SERVICE_PROPERTIES[] = {
		PropertyLoader.primarySimDataDirProperty,
		PropertyLoader.secondarySimDataDirProperty,
		PropertyLoader.htcUser,
		PropertyLoader.NATIVE_LIB_DIR
	};


}
