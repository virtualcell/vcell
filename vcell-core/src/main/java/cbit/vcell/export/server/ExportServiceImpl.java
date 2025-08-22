/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Collection;
import java.util.Hashtable;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import com.google.common.io.Files;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.modeldb.ExportHistoryDBDriver;
import org.vcell.restq.db.AgroalConnectionFactory;



/**
 * This type was created in VisualAge.
 */



public class ExportServiceImpl implements ExportConstants, ExportService {
	public static final Logger lg = LogManager.getLogger(ExportServiceImpl.class);

	private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

	private Hashtable<Long, User> jobRequestIDs = new Hashtable<Long, User>();
	private Hashtable<ExportSpecs, JobRequest> completedExportRequests = new Hashtable<ExportSpecs, JobRequest>();



	@Inject
	AgroalConnectionFactory agroalConnectionFactory;

	ExportHistoryDBDriver exportHistoryDBDriver = new ExportHistoryDBDriver(null, null);
//	ConnectionFactory connFactory = new ConnectionFactory() {
//		@Override
//		public void close() throws SQLException {}
//
//		@Override
//		public void failed(Connection con, Object lock) throws SQLException {}
//
//		@Override
//		public Connection getConnection(Object lock) throws SQLException {return null;}
//
//		@Override
//		public void release(Connection con, Object lock) throws SQLException {}
//
//		@Override
//		public KeyFactory getKeyFactory() {return null;}
//
//		@Override
//		public DatabaseSyntax getDatabaseSyntax() {return null;}
//	};


	/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 4:09:29 PM)
 * @param log cbit.vcell.server.SessionLog
 */
public ExportServiceImpl() {
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener ExportListener
 */
public synchronized void addExportListener(ExportListener listener) {
	listenerList.add(ExportListener.class, listener);
}



/**
 * Insert the method's description here.
 * Creation date: (4/1/2001 11:20:45 AM)
 * @deprecated
 */
protected ExportEvent fireExportCompleted(long jobID, VCDataIdentifier vcdID, String format, String location,ExportSpecs exportSpecs) throws SQLException, DataAccessException {
	User user = null;
	Object object = jobRequestIDs.get(new Long(jobID));
	if (object != null) {
		user = (User)object;
	}
	TimeSpecs timeSpecs = (exportSpecs!=null)?exportSpecs.getTimeSpecs():(null);
	VariableSpecs varSpecs = (exportSpecs!=null)?exportSpecs.getVariableSpecs():(null);
	final KeyValue dataKey;
	if (vcdID instanceof VCSimulationDataIdentifier) {
		dataKey = ((VCSimulationDataIdentifier)vcdID).getSimulationKey();
	}else if (vcdID instanceof ExternalDataIdentifier) {
		dataKey = ((ExternalDataIdentifier)vcdID).getSimulationKey();
	}else {
		throw new RuntimeException("unexpected VCDataIdentifier");
	}
	ExportEvent event = new ExportEvent(
			this, jobID, user, vcdID.getID(), dataKey, ExportEvent.EXPORT_COMPLETE,
			format, location, null, timeSpecs, varSpecs);
    event.setHumanReadableExportData(exportSpecs != null ? exportSpecs.getHumanReadableExportData() : null);

	assert exportSpecs != null;
	event.setHumanReadableExportData(exportSpecs.getHumanReadableExportData());
	assert user != null;



	try (Connection conn = agroalConnectionFactory.getConnection(null)) {
		exportHistoryDBDriver.addExportHistory(conn, vcdID.getOwner(), new ExportHistoryDBDriver.ExportHistory(jobID, 1, exportSpecs.getFormat(), new Timestamp(System.currentTimeMillis()), location, exportSpecs), agroalConnectionFactory.getKeyFactory());

	}


//exportToDB(
//			jobID,
//			Long.parseLong(user.getID().toString()),
//			Long.parseLong(vcdID.getOwner().getID().toString()),
//			format,
//			new Timestamp(System.currentTimeMillis()),
//			location,
//			exportSpecs
//	);
	fireExportEvent(event);
	return event;
}


protected void fireExportAssembling(long jobID, VCDataIdentifier vcdID, String format) {
	User user = null;
	Object object = jobRequestIDs.get(new Long(jobID));
	if (object != null) {
		user = (User)object;
	}
	ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEvent.EXPORT_ASSEMBLING, format, null, null, null, null);
	fireExportEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected void fireExportEvent(ExportEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==ExportListener.class) {
		((ExportListener)listeners[i+1]).exportMessage(event);
	    }
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2001 11:20:45 AM)
 * @deprecated
 */
protected void fireExportFailed(long jobID, VCDataIdentifier vcdID, String format, String message) {
	User user = null;
	Object object = jobRequestIDs.get(new Long(jobID));
	if (object != null) {
		user = (User)object;
	}
	ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEvent.EXPORT_FAILURE, format, message, null, null, null);
	fireExportEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2001 11:20:45 AM)
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 * @param progress double
 */
protected void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress) {
	User user = null;
	Object object = jobRequestIDs.get(new Long(jobID));
	if (object != null) {
		user = (User)object;
	}
	ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEvent.EXPORT_PROGRESS, format, null, new Double(progress), null, null);
	fireExportEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2001 11:20:45 AM)
 * @deprecated
 */
protected void fireExportStarted(long jobID, VCDataIdentifier vcdID, String format) {
	User user = null;
	Object object = jobRequestIDs.get(new Long(jobID));
	if (object != null) {
		user = (User)object;
	}
	ExportEvent event = new ExportEvent(this, jobID, user, vcdID, ExportEvent.EXPORT_START, format, null, null, null, null);
	fireExportEvent(event);
}

public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs)throws DataAccessException
{
	return makeRemoteFile(outputContext,user, dataServerImpl, exportSpecs, true);
}

public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs, boolean bSaveAsZip) throws DataAccessException
{
	return makeRemoteFile(outputContext, user, dataServerImpl, exportSpecs, bSaveAsZip, null);
}

public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs, boolean bSaveAsZip, ClientTaskStatusSupport clientTaskStatusSupport) throws DataAccessException {
	// if export completes successfully, we return the generated event for logging
	if (user == null) {
		throw new DataAccessException("ERROR: user is null");
	}
	JobRequest newExportJob = JobRequest.createExportJobRequest(user);
	jobRequestIDs.put(new Long(newExportJob.getExportJobID()), user);
	if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): " + newExportJob + ", " + exportSpecs);
	String fileFormat = null;
	switch (exportSpecs.getFormat()) {
		case CSV:
		case HDF5:
			fileFormat = "CSV";
			break;
		case QUICKTIME:
			fileFormat = "MOV";
			break;
		case GIF:
		case ANIMATED_GIF:
			fileFormat = "GIF";
			break;
		case FORMAT_JPEG:
			fileFormat = "JPEG";
			break;
		case NRRD:
			fileFormat = "NRRD";
			break;
		case N5:
			fileFormat = N5Specs.n5Suffix.toUpperCase();
			break;
//		case IMAGEJ:
//			fileFormat = "IMAGEJ";
//			break;
	}
	fireExportStarted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat);

	try {

		String exportBaseURL = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseURLProperty);
		String exportBaseDir = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirInternalProperty);


//		// see if we've done this before, and try to get it
//		// for now, works only for the life of the server (eventually will be persistent)
//		Object completed = completedExportRequests.get(exportSpecs);
//		if (completed != null) {
//			try {
//				JobRequest previousRequest = (JobRequest)completedExportRequests.get(exportSpecs);
//				File file = new File(exportBaseDir + previousRequest.getJobID() + ".zip");
//				if (file.exists()) {
//					URL url = new URL(exportBaseURL + file.getName());
//					if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Found previously made file: " + file.getName());
//					fireExportCompleted(newExportJob.getJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString());
//					// now that we start logging exports, we should do the persistence thing soon...
//					// so far retutn null, so we at least don't double log during the same server session
//					return null;
//				}
//			} catch (Throwable exc) {
//				completedExportRequests.remove(exportSpecs);
//				if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): WARNING: did not find previous export output; attempting to export again");
//			}
//		}

		// we need to make new output
		if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Starting new export job: " + newExportJob);
		FileDataContainerManager fileDataContainerManager = new FileDataContainerManager();
		try{
			ExportOutput[] exportOutputs = null;
			switch (exportSpecs.getFormat()) {
				case CSV:
				case HDF5:
					ASCIIExporter asciiExporter = new ASCIIExporter(this);
					Collection<ExportOutput> asciiOut = asciiExporter.makeASCIIData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					exportOutputs = asciiOut.toArray(new ExportOutput[asciiOut.size()]);
					if(((ASCIISpecs)exportSpecs.getFormatSpecificSpecs()).isHDF5()) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						exportOutputs[0].writeDataToOutputStream(baos, fileDataContainerManager);//Get location of temp HDF5 file
						File tempHDF5File = new File(baos.toString());
//						File downloadableHDF5File = new File(exportBaseDir +exportOutputs[0].getSimID() + exportOutputs[0].getDataID() + ".hdf5");
						File downloadableHDF5File = new File(exportBaseDir + newExportJob.getExportJobID() + ".hdf5");
						Files.copy(tempHDF5File, downloadableHDF5File);
						tempHDF5File.delete();
						URL url = new URL(exportBaseURL + downloadableHDF5File.getName());
						return fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString(),exportSpecs);
					}
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager);
				case QUICKTIME:
				case GIF:
				case FORMAT_JPEG:
				case ANIMATED_GIF:
					IMGExporter imgExporter = new IMGExporter(this);
					exportOutputs = imgExporter.makeMediaData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,clientTaskStatusSupport,fileDataContainerManager);
					boolean bOverrideZip = exportOutputs.length == 1;
					if(bSaveAsZip && !bOverrideZip){
						return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager);
					}else{
						return makeRemoteFile_Unzipped(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager);
					}
				case NRRD:
//				case IMAGEJ:
					RasterExporter rrExporter = new RasterExporter(this);
					NrrdInfo[] nrrdInfos = rrExporter.makeRasterData(outputContext,newExportJob, user, dataServerImpl, exportSpecs, fileDataContainerManager);
					return makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL, nrrdInfos, exportSpecs, newExportJob, fileDataContainerManager);
				case UCD:
					RasterExporter rrExporterUCD = new RasterExporter(this);
					exportOutputs = rrExporterUCD.makeUCDData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager);
				case PLY:
					RasterExporter rrExporterPLY = new RasterExporter(this);
					exportOutputs = rrExporterPLY.makePLYWithTexData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager);
				case VTK_IMAGE:
					RasterExporter rrExporterVTK = new RasterExporter(this);
					exportOutputs = rrExporterVTK.makeVTKImageData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager);
				case VTK_UNSTRUCT:
					RasterExporter rrExporterVTKU = new RasterExporter(this);
					exportOutputs = rrExporterVTKU.makeVTKUnstructuredData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager);
				case N5:
					N5Exporter n5Exporter = new N5Exporter(this, user, dataServerImpl,  (VCSimulationDataIdentifier) exportSpecs.getVCDataIdentifier());
					ExportOutput exportOutput = n5Exporter.makeN5Data(outputContext, newExportJob, exportSpecs, fileDataContainerManager);
					return makeRemoteN5File(fileFormat, n5Exporter.getN5FileNameHash(), exportOutput, exportSpecs, newExportJob, n5Exporter.getN5FilePathSuffix());
				default:
					throw new DataAccessException("Unknown export format requested");
			}
		}finally{
			fileDataContainerManager.closeAllAndDelete();
		}
	}catch(UserCancelException ex)
	{
		throw ex;
	}
	catch (Throwable exc) {
		lg.error(exc.getMessage(), exc);
		fireExportFailed(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, exc.getMessage());
		throw new DataAccessException(exc.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 6:47:56 PM)
 */
private ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL, NrrdInfo[] nrrdInfos, ExportSpecs exportSpecs, JobRequest newExportJob, FileDataContainerManager fileDataContainerManager) throws DataFormatException, IOException, MalformedURLException, SQLException, DataAccessException {
			boolean exportValid = true;

	// check outputs and package into zip file
	File zipFile = new File(exportBaseDir + newExportJob.getExportJobID() + ".zip");
	BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(zipFile));
	ZipOutputStream zipOut = new ZipOutputStream(bout);
	try {
		for (int i=0;i<nrrdInfos.length;i++) {
			if (nrrdInfos[i].isHasData()) {
				// nrrdInfo 'header' file contains either just the header or both the header and data together
				ZipEntry zipEntry = new ZipEntry(nrrdInfos[i].getCanonicalFilename((nrrdInfos[i].isSeparateHeader()?true:false)));
				zipOut.putNextEntry(zipEntry);
				fileDataContainerManager.writeAndFlush(nrrdInfos[i].getHeaderFileID(), zipOut);
				if (nrrdInfos[i].isSeparateHeader()) {
						// The data was not saved with the 'header' file so save it separately
						zipEntry = new ZipEntry(nrrdInfos[i].getCanonicalFilename(false));
						zipOut.putNextEntry(zipEntry);
						fileDataContainerManager.writeAndFlush(nrrdInfos[i].getDataFileID(), zipOut);
				}
			} else {
				exportValid = false;
				break;
			}
		}
	} catch (IOException ioexc) {
		throw ioexc;
	} finally {
		zipOut.close();
	}

	if (exportValid) {
		completedExportRequests.put(exportSpecs, newExportJob);
		if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
		URL url = new URL(exportBaseURL + zipFile.getName());
		return fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString(),exportSpecs);
	}
	else {
		throw new DataFormatException("Export Server could not produce valid data !");
	}
}


/**
 * This function saves the remote file into a compressed zip file.
 * Creation date: (4/26/2004 6:47:56 PM)
 */
private ExportEvent saveResultsToRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL, ExportOutput[] exportOutputs, ExportSpecs exportSpecs, JobRequest newExportJob, FileDataContainerManager fileDataContainerManager) throws DataFormatException, IOException, MalformedURLException, SQLException, DataAccessException {
			boolean exportValid = true;
			fireExportAssembling(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat);

			// check outputs and package into zip file
			File zipFile = new File(exportBaseDir + newExportJob.getExportJobID() + ".zip");
			FileOutputStream fileOut = new FileOutputStream(zipFile);
			BufferedOutputStream bos = new BufferedOutputStream(fileOut);
			ZipOutputStream zipOut = new ZipOutputStream(bos);
			for (int i=0;i<exportOutputs.length;i++) {
				if (exportOutputs[i].isValid()) {
					String filename = exportOutputs[i].getSimID() + exportOutputs[i].getDataID();
					if (!filename.endsWith(exportOutputs[i].getDataType())){
						filename = filename + exportOutputs[i].getDataType();
					}
					ZipEntry zipEntry = new ZipEntry(filename);
					zipOut.putNextEntry(zipEntry);
					exportOutputs[i].writeDataToOutputStream(zipOut,fileDataContainerManager);
					//zipOut.write(exportOutputs[i].getData());
				} else {
					exportValid = false;
					break;
				}
			}
			zipOut.close();

			if (exportValid) {
				completedExportRequests.put(exportSpecs, newExportJob);
				if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
				URL url = new URL(exportBaseURL + zipFile.getName());
				return fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString(),exportSpecs);
			}
			else {
				throw new DataFormatException("Export Server could not produce valid data !");
			}
}

/**
 * Save remote file in it original format without compression.
 */
private ExportEvent makeRemoteFile_Unzipped(String fileFormat, String exportBaseDir, String exportBaseURL, ExportOutput[] exportOutputs, ExportSpecs exportSpecs, JobRequest newExportJob,FileDataContainerManager fileDataContainerManager) throws DataFormatException, IOException, MalformedURLException, SQLException, DataAccessException {
	boolean exportValid = true;
	String fileNames = "";
	if(exportOutputs.length > 0  && exportOutputs[0].isValid())
	{
		//do the first file of exportOutputs separately (for VFRAP, there is only one export output)
		String extStr = "." + fileFormat;
		File file = new File(exportBaseDir + newExportJob.getExportJobID() + extStr);
		FileOutputStream fileOut = new FileOutputStream(file);
		BufferedOutputStream out= new BufferedOutputStream(fileOut);
		exportOutputs[0].writeDataToOutputStream(out,fileDataContainerManager);
		//out.write(exportOutputs[0].getData());
		out.close();
		fileNames = fileNames + file.getName();
		//if there are more export outputs, loops through the second till the last.
		for (int i=1;i<exportOutputs.length;i++)
		{
			if (exportOutputs[i].isValid())
			{
				File moreFile = new File(exportBaseDir + newExportJob.getExportJobID()+"_"+ i + extStr);
				FileOutputStream moreFileOut = new FileOutputStream(moreFile);
				ObjectOutputStream moreOut= new ObjectOutputStream(moreFileOut);
				exportOutputs[i].writeDataToOutputStream(moreOut,fileDataContainerManager);
				//moreOut.writeObject(exportOutputs[i].getData());
				moreOut.close();
				fileNames = "\t"+fileNames + moreFile.getName();
			} else {
				exportValid = false;
				break;
			}
		}
	}
	else
	{
		exportValid = false;
	}
	if (exportValid) {
		completedExportRequests.put(exportSpecs, newExportJob);
		if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + fileNames);
		URL url = new URL(exportBaseURL + fileNames);
		return fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString(),exportSpecs);
	}
	else {
		throw new DataFormatException("Export Server could not produce valid data !");
	}
}


private ExportEvent makeRemoteN5File(String fileFormat, String fileName, ExportOutput exportOutput, ExportSpecs exportSpecs, JobRequest newExportJob, String pathSuffix) throws DataFormatException, IOException, SQLException, DataAccessException {
	if (exportOutput.isValid()) {
		completedExportRequests.put(exportSpecs, newExportJob);
		String url = PropertyLoader.getRequiredProperty(PropertyLoader.s3ExportBaseURLProperty);
		url += "/" + pathSuffix;
		N5Specs n5Specs = (N5Specs) exportSpecs.getFormatSpecificSpecs();
		url += "?dataSetName=" + newExportJob.getExportJobID();
		if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + fileName);
		return fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url, exportSpecs);
	}
	else {
		throw new DataFormatException("Export Server could not produce valid data !");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener ExportListener
 */
public synchronized void removeExportListener(ExportListener listener) {
	listenerList.remove(ExportListener.class, listener);
}

//private void exportToDB(long   jobID,
//						long   userRef,
//						long   modelRef,
//						String exportFormat,
//						Timestamp exportDate,
//						String uri,
//						ExportSpecs exportSpecs) {
//	Connection conn      = null;
//	PreparedStatement ps = null;
//	PreparedStatement psParam = null;
//	ResultSet rsSeq      = null;
//	boolean passed;
//	try {
//		conn = DriverManager.getConnection("DB_URL", "quarkus", "quarkus");
//
//
//		String vcmExpHiSQL =		// SQL statement for inserting into vc_model_export_history table in VCell server
//				"INSERT INTO vc_model_export_history (" +
//						"id, job_id, user_ref, model_ref, export_format, export_date, uri," +
//						"data_id, simulation_name, application_name, biomodel_name, variables," +
//						"start_time, end_time, saved_file_name, application_type, non_spatial," +
//						"z_slices, t_slices, num_variables" +
//						") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		ps = conn.prepareStatement(vcmExpHiSQL);
//
//		HumanReadableExportData meta = exportSpecs.getHumanReadableExportData();
//		TimeSpecs       ts   = exportSpecs.getTimeSpecs();
//		String[]        vars = exportSpecs.getVariableSpecs().getVariableNames();
//
//		String timeRange = ts.toString();
//		String[] time_parts   = timeRange.split("/");
//		BigDecimal startTime = new BigDecimal(time_parts[0]);
//		BigDecimal endTime   = new BigDecimal(time_parts[1]);
//
//
//		ps.setLong       (2, jobID);
//		ps.setLong       (3, userRef);
//		ps.setLong       (4, modelRef);
//		ps.setString     (5, exportFormat);
//		ps.setTimestamp  (6, exportDate);
//		ps.setString     (7, uri);
//		ps.setString     (8, exportSpecs.getVCDataIdentifier().getID());
//		ps.setString     (9, meta.simulationName);
//		ps.setString     (10, meta.applicationName);
//		ps.setString     (11, meta.biomodelName);
//		ps.setArray      (12, conn.createArrayOf("text", vars));
//		ps.setBigDecimal (13, startTime);
//		ps.setBigDecimal (14, endTime);
//		ps.setString     (15, meta.serverSavedFileName);
//		ps.setString     (16, meta.applicationType);
//		ps.setBoolean    (17, meta.nonSpatial);
//		ps.setInt        (18, meta.zSlices);
//		ps.setInt        (19, meta.tSlices);
//		ps.setInt        (20, meta.numChannels);
//		ps.executeUpdate();
//
//
//		String vcmExpHisPVSQL =		// SQL statement for inserting into vc_model_parameter_value table in VCell server
//				"INSERT INTO vc_model_parameter_values (" +
//						"export_id, user_ref, model_ref, parameter_name," +
//						"parameter_original, parameter_changed" +
//						") VALUES (?,?,?,?,?,?)";
//		psParam = conn.prepareStatement(vcmExpHisPVSQL);
//
//
//		for (String entry : meta.differentParameterValues) {
//			String[] parts = entry.split(":");
//			if (parts.length == 3) {
//				//psParam.setLong       (1, historyId);
//				psParam.setLong       (2, userRef);
//				psParam.setLong       (3, modelRef);
//				psParam.setString     (4, parts[0]);
//				psParam.setBigDecimal (5, new BigDecimal(parts[1]));
//				psParam.setBigDecimal (6, new BigDecimal(parts[2]));
//				psParam.addBatch();
//			}
//		}
//		psParam.executeBatch();
//	}
//	catch(SQLException e){
//		lg.error("Error exporting to database",e);
//	}
//	finally {
//		try{ if(rsSeq  !=null) rsSeq.close();  }catch(Exception ign){}
//		try{ if(psParam !=null) psParam.close(); }catch(Exception ign){}
//		try{ if(ps     !=null) ps.close();   }catch(Exception ign){}
//		try{ if(conn   !=null) conn.close();  }catch(Exception ign){}
//	}
//}
//
//
//}
