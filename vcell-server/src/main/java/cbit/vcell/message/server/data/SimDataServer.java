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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.httpclient.URI;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.ssl.SSLContexts;
import org.vcell.service.VCellServiceHelper;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCPooledQueueConsumer;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCRpcMessageHandler;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.bootstrap.ServiceType;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.LibraryLoaderThread;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.PythonSupport.PythonPackage;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.Hdf5Utils;
import cbit.vcell.simdata.Hdf5Utils.HDF5WriteHelper;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimDataServer extends ServiceProvider implements ExportListener, DataJobListener {
	

	public enum SimDataServiceType {
		ExportDataOnly,
		SimDataOnly,
		CombinedData
	}
	
	private DataServerImpl dataServerImpl = null;
	private VCQueueConsumer rpcConsumer = null;	
	private VCRpcMessageHandler rpcMessageHandler = null;
	private VCPooledQueueConsumer pooledQueueConsumer = null;
	private VCMessageSession sharedProducerSession = null;
	private final SimDataServiceType simDataServiceType;
	private HttpServer server;


/**
 * @param serviceInstanceStatus
 * @param dataServerImpl
 * @param vcMessagingService
 * @param log
 * @throws Exception
 */
public SimDataServer(ServiceInstanceStatus serviceInstanceStatus, DataServerImpl dataServerImpl, VCMessagingService vcMessagingService, SimDataServiceType simDataServiceType, boolean bSlaveMode) throws Exception {
	super(vcMessagingService,null,serviceInstanceStatus,bSlaveMode);
	this.dataServerImpl = dataServerImpl;
	this.simDataServiceType = simDataServiceType;
	
	try (InputStream inputStream = new FileInputStream(new File(System.getProperty(PropertyLoader.vcellapiKeystoreFile)))) {
		final KeyStore serverKeyStore = KeyStore.getInstance("jks");
		String pwd = Files.readAllLines(new File(System.getProperty(PropertyLoader.vcellapiKeystorePswdFile)).toPath()).get(0);
		serverKeyStore.load(inputStream, pwd.toCharArray());
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(serverKeyStore, pwd.toCharArray());
		KeyManager[] serverKeyManagers = keyManagerFactory.getKeyManagers();
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(serverKeyStore);
		TrustManager[] serverTrustManagers = trustManagerFactory.getTrustManagers();
		final SSLContext sslContext = SSLContexts.createDefault();
		sslContext.init(serverKeyManagers, serverTrustManagers, new SecureRandom());
		int listenPort = Integer.parseInt(System.getProperty(PropertyLoader.webDataServerPort));
		server = ServerBootstrap.bootstrap().registerHandler("/simhdf5/*", new HttpRequestHandler() {
			@Override
			public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
            	try {
					URI uri = new URI(request.getRequestLine().getUri(),true);
					final List<NameValuePair> parse = URLEncodedUtils.parse(uri.getQuery(),Charset.forName("utf-8"));
					System.out.println(uri.getQuery());
					System.out.println(uri.getPath());
					final Path path = Paths.get(uri.getPath());
					final Iterator<Path> iterator = path.iterator();
					final String SIMDATADDF5 = "simhdf5";
					final String ODE = "ode";
					String simdataRoute = iterator.next().toString();
					String dataType = iterator.next().toString();
					if(simdataRoute.toLowerCase().equals(SIMDATADDF5) && dataType.toLowerCase().equals(ODE)) {
						String simID = null;
						TreeSet<Integer> jobIDs = new TreeSet<Integer>();
						String userKey = null;
						String userid = null;
						double blank = -1.0;
						for(NameValuePair nvp:parse) {
							if(nvp.getName().toLowerCase().equals("simid")) {
								simID = nvp.getValue();
							}else if(nvp.getName().toLowerCase().equals("jobid")) {
								String jobStr = URLDecoder.decode(nvp.getValue(), "UTF-8");// integer Separated by commas
								StringTokenizer st = new StringTokenizer(jobStr,",");
								while(st.hasMoreElements()) {
									jobIDs.add(Integer.parseInt(st.nextToken()));
								}
							}else if(nvp.getName().toLowerCase().equals("userkey")) {
								userKey = nvp.getValue();
							}else if(nvp.getName().toLowerCase().equals("userid")) {
								userid = URLDecoder.decode(nvp.getValue(), "UTF-8");
							}else if(nvp.getName().toLowerCase().equals("blank")) {
								blank = Double.parseDouble(nvp.getValue());
							}
						}
						User user = new User(userid,new KeyValue(userKey));
						VCSimulationIdentifier vcsid = new VCSimulationIdentifier(new KeyValue(simID), user);
						File hdf5File = createHdf5(vcsid, jobIDs.toArray(new Integer[0]), blank);
						
						String exportBaseURL = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseURLProperty);
						URL url = new URL(exportBaseURL + hdf5File.getName());
						response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
						response.addHeader("Location",url.toString());
						response.setEntity(null);
						return;
					}
					response.setStatusCode(HttpStatus.SC_NOT_FOUND);
					response.setEntity(new StringEntity("Not Found"));
				} catch (Exception e) {
					e.printStackTrace();
					response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
					response.setEntity(new StringEntity(e.getMessage()));

				}
			}
		}).setListenerPort(listenPort).setSslContext(sslContext).create();
		server.start();
		
	}catch(Exception e) {
		e.printStackTrace();
	}
	
	
}

private File createHdf5(VCSimulationIdentifier vcsid,Integer[] scanJobs,double blankCellValue/*for Histogram*/) throws Exception{
	File hdf5TempFile = null;
	int hdf5FileID = -1;
	int jobGroupID = -1;
	try {
		String exportBaseDir = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirInternalProperty);
		hdf5TempFile = File.createTempFile("webexport_"+vcsid.getSimulationKey()+"_", ".hdf", new File(exportBaseDir));
		hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);				
		Hdf5Utils.writeHDF5Dataset(hdf5FileID, "simID", null,vcsid.getSimulationKey().toString() , true);
		Hdf5Utils.writeHDF5Dataset(hdf5FileID, "exportUser", null,vcsid.getOwner().getName() , true);
		
		for(int scan=0;scan<scanJobs.length;scan++) {
			VCDataIdentifier vcdid = new VCSimulationDataIdentifier(vcsid, scanJobs[scan]);
			ODESimData odeSimData = dataServerImpl.getODEData(vcsid.getOwner() , vcdid);
			int allColumnsCount = odeSimData.getColumnDescriptionsCount();
			int allRowsCount = odeSimData.getRowCount();
			TreeSet<ColumnDescription> orderedColumnNames = new TreeSet<ColumnDescription>(new Comparator<ColumnDescription>() {
				@Override
				public int compare(ColumnDescription o1, ColumnDescription o2) {
					if(o1.getName().equals("t") && o2.getName().equals("t")) {//'t' as first in the list
						return 0;
					}else if(o1.getName().equals("t")) {
						return -1;
					}else if(o2.getName().equals("t")) {
						return 1;
					}
					return o1.getName().compareToIgnoreCase(o2.getName());
				}});
			orderedColumnNames.addAll(Arrays.asList(odeSimData.getColumnDescriptions()));
			jobGroupID = (int) Hdf5Utils.writeHDF5Dataset(hdf5FileID, "Set "+scan, null, null, false);
			Hdf5Utils.HDF5WriteHelper help0 =  (HDF5WriteHelper) Hdf5Utils.writeHDF5Dataset(jobGroupID, "data", new long[] {allColumnsCount,allRowsCount}, new Object[] {}, false);
			double[] fromData = new double[allColumnsCount*allRowsCount];
			int index = 0;
			ArrayList<String> dataTypes = new ArrayList<String>();
			ArrayList<String> dataIDs = new ArrayList<String>();
			ArrayList<String> dataShapes = new ArrayList<String>();
			ArrayList<String> dataLabels = new ArrayList<String>();
			ArrayList<String> dataNames = new ArrayList<String>();
			Iterator<ColumnDescription> columnNamesIterator = orderedColumnNames.iterator();
			while(columnNamesIterator.hasNext()) {
			
				ColumnDescription colDescr = columnNamesIterator.next();
				final String columnName = colDescr.getName();
				final int columnIndex = odeSimData.findColumn(columnName);
				dataTypes.add("float64");
				dataIDs.add("data_set_"+columnName);
				dataShapes.add(allRowsCount+"");
				dataLabels.add(columnName);
				dataNames.add(columnName/*name*/);
				double[] columnData = odeSimData.extractColumn(columnIndex);
				for(int myrows=0;myrows<allRowsCount;myrows++) {
					fromData[index] = columnData[myrows];
					index++;
				}
			}
			Object[] objArr = new Object[] {fromData,new long[] {0,0},
					new long[] {allColumnsCount,allRowsCount},new long[] {allColumnsCount,
							allRowsCount},new long[] {0,0},new long[] {allColumnsCount,allRowsCount},help0.hdf5DataSpaceID};
			//			double[] copyFromData = (double[])((Object[])data)[0];
			//			long[] copyToStart = (long[])((Object[])data)[1];
			//			long[] copyToLength = (long[])((Object[])data)[2];
			//			long[] copyFromDims = (long[])((Object[])data)[3];
			//			long[] copyFromStart = (long[])((Object[])data)[4];
			//			long[] copyFromLength = (long[])((Object[])data)[5];
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, null, null, objArr, false);
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "_type", null, "ODE Data Export", true);
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetDataTypes", null, dataTypes, true);
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetIds", null,dataIDs , true);
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetLabels", null,dataLabels , true);
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetNames", null,dataNames , true);
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetShapes", null,dataShapes , true);
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "id", null,"report" , true);
			Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "scanJobID", null,""+scanJobs[scan] , true);
			help0.close();
			H5.H5Gclose(jobGroupID);
			jobGroupID = -1;
		}
		H5.H5Fclose(hdf5FileID);
		hdf5FileID = -1;
		return hdf5TempFile;
	} finally {
		if(jobGroupID != -1) {try{H5.H5Gclose(jobGroupID);}catch(Exception e2){e2.printStackTrace();}}
		if(hdf5FileID != -1) {try{H5.H5Fclose(hdf5FileID);}catch(Exception e2){e2.printStackTrace();}}
	}
}
public void init() throws Exception {
	
	String dataRequestFilter = "(" + VCMessagingConstants.MESSAGE_TYPE_PROPERTY + "='" + VCMessagingConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE  + "') " +
									" AND (" + VCMessagingConstants.SERVICE_TYPE_PROPERTY + "='" + ServiceType.DATA.getName() + "')";
	String exportOnlyFilter = "(" + ServiceType.DATAEXPORT.getName() + " is NOT NULL)";
	String dataOnlyFilter = "(" + ServiceType.DATAEXPORT.getName() + " is NULL)";
	
	VCMessageSelector selector;
	ServiceType serviceType = serviceInstanceStatus.getType();
	int numThreads;
	switch (simDataServiceType) {
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
	rpcConsumer = new VCQueueConsumer(VCellQueue.DataRequestQueue, pooledQueueConsumer, selector, serviceType.getName()+" RPC Server Thread", MessageConstants.PREFETCH_LIMIT_DATA_REQUEST);

	vcMessagingService_int.addMessageConsumer(rpcConsumer);
}

@Override
public void stopService() {
	this.pooledQueueConsumer.shutdownAndAwaitTermination();
	super.stopService();
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	OperatingSystemInfo.getInstance();

	if (args.length != 1) {
		System.out.println("Missing arguments: " + SimDataServer.class.getName() + " (CombinedData | ExportDataOnly | SimDataOnly)");
		System.exit(1);
	}
	
	try {
		PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);
		ResourceUtil.setNativeLibraryDirectory();
		new LibraryLoaderThread(false).start( );

		PythonSupport.verifyInstallation(new PythonPackage[] { PythonPackage.VTK, PythonPackage.THRIFT});

		int serviceOrdinal = 99;
		
		SimDataServiceType simDataServiceType = SimDataServiceType.valueOf(args[0]);
		if (simDataServiceType==null) {
			throw new RuntimeException("expecting argument (CombinedData | ExportDataOnly | SimDataOnly)");
		}
		final ServiceInstanceStatus serviceInstanceStatus;
		final ServiceName serviceName;
		switch (simDataServiceType) {
		case ExportDataOnly: {
			serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.DATAEXPORT, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
			serviceName = ServiceName.export;
			break;
		}
		case SimDataOnly: {
			serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.DATA, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
			serviceName = ServiceName.simData;
			break;
		}
		case CombinedData: {
			serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.COMBINEDDATA, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
			serviceName = ServiceName.combinedData;
			break;
		}
		default: {
			throw new RuntimeException("unexpected SimDataServiceType "+simDataServiceType);
		}
		}

		VCMongoMessage.serviceStartup(serviceName, new Integer(serviceOrdinal), args);

//		//
//		// JMX registration
//		//
//        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//        mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));
		
		String cacheSize = PropertyLoader.getRequiredProperty(PropertyLoader.simdataCacheSizeProperty);
		long maxMemSize = Long.parseLong(cacheSize);

		Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20,maxMemSize);
		DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(cacheTable,
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty)),
				new File(PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirInternalProperty, PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty))));
		
		ExportServiceImpl exportServiceImpl = new ExportServiceImpl();
		
		DataServerImpl dataServerImpl = new DataServerImpl(dataSetControllerImpl, exportServiceImpl);

		VCMessagingService vcMessagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class);
		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
		int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
		vcMessagingService.setConfiguration(new ServerMessagingDelegate(), jmshost, jmsport);
		
        SimDataServer simDataServer = new SimDataServer(serviceInstanceStatus, dataServerImpl, vcMessagingService, simDataServiceType, false);
        //add dataJobListener
        dataSetControllerImpl.addDataJobListener(simDataServer);
        // add export listener
        exportServiceImpl.addExportListener(simDataServer);
        
        simDataServer.init();
    } catch (Throwable e) {
		lg.error("uncaught exception initializing SimDataServer: "+e.getLocalizedMessage(), e);
		System.exit(1);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (3/31/2006 8:48:04 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
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


/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 10:34:11 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
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

private static final String REQUIRED_SERVICE_PROPERTIES[] = {
		PropertyLoader.primarySimDataDirInternalProperty,
		PropertyLoader.primarySimDataDirExternalProperty,
		PropertyLoader.vcellServerIDProperty,
		PropertyLoader.installationRoot,
		PropertyLoader.mongodbHostInternal,
		PropertyLoader.mongodbPortInternal,
		PropertyLoader.mongodbDatabase,
		PropertyLoader.jmsIntHostInternal,
		PropertyLoader.jmsIntPortInternal,
		PropertyLoader.jmsUser,
		PropertyLoader.jmsPasswordFile,
		PropertyLoader.pythonExe,
		PropertyLoader.jmsBlobMessageUseMongo,
		PropertyLoader.exportBaseURLProperty,
		PropertyLoader.exportBaseDirInternalProperty,
		PropertyLoader.simdataCacheSizeProperty
	};

}
