package cbit.vcell.export;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.PropertyLoader;
import cbit.vcell.solver.*;
import java.io.*;
import java.util.zip.*;
import java.net.*;
import java.util.*;
import cbit.util.*;
import cbit.vcell.simdata.*;
import cbit.vcell.export.nrrd.*;

/**
 * This type was created in VisualAge.
 */
public class ExportServiceImpl implements ExportConstants, ExportService {
	
	private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

	private Hashtable jobRequestIDs = new Hashtable();
	private Hashtable completedExportRequests = new Hashtable();
	private Random random = new Random();
	
	private ASCIIExporter asciiExporter = new ASCIIExporter(this);
	private IMGExporter imgExporter = new IMGExporter(this);
	private QTExporter qtExporter = new QTExporter(this);
	private RasterExporter rrExporter = new RasterExporter(this);

	private SessionLog log = null;

	
		



/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 4:09:29 PM)
 * @param log cbit.vcell.server.SessionLog
 */
public ExportServiceImpl(SessionLog log) {
	this.log = log;
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
protected ExportEvent fireExportCompleted(long jobID, VCDataIdentifier vcdID, String format, String location) {
	User user = null;
	Object object = jobRequestIDs.get(new Long(jobID));
	if (object != null) {
		user = (User)object;
	}
	ExportEvent event = new ExportEvent(this, jobID, user, ((VCSimulationDataIdentifier)vcdID).getVcSimID(), ExportEvent.EXPORT_COMPLETE, format, location, null);
	fireExportEvent(event);
	return event;
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
	ExportEvent event = new ExportEvent(this, jobID, user, ((VCSimulationDataIdentifier)vcdID).getVcSimID(), ExportEvent.EXPORT_FAILURE, format, message, null);
	fireExportEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2001 11:20:45 AM)
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 * @param progress double
 * @deprecated
 */
protected void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress) {
	User user = null;
	Object object = jobRequestIDs.get(new Long(jobID));
	if (object != null) {
		user = (User)object;
	}
	ExportEvent event = new ExportEvent(this, jobID, user, ((VCSimulationDataIdentifier)vcdID).getVcSimID(), ExportEvent.EXPORT_PROGRESS, format, null, new Double(progress));
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
	ExportEvent event = new ExportEvent(this, jobID, user, ((VCSimulationDataIdentifier)vcdID).getVcSimID(), ExportEvent.EXPORT_START, format, null, null);
	fireExportEvent(event);
}


/**
 * This method was created in VisualAge.
 */
public ExportEvent makeRemoteFile(User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs)
						throws DataAccessException {
	// if export completes successfully, we return the generated event for logging
	if (user == null) {
		throw new DataAccessException("ERROR: user is null");
	}
	JobRequest newExportJob = JobRequest.createExportJobRequest(user);
	jobRequestIDs.put(new Long(newExportJob.getJobID()), user);
	log.print("ExportServiceImpl.makeRemoteFile(): " + newExportJob + ", " + exportSpecs);
	String fileFormat = null;
	switch (exportSpecs.getFormat()) {
		case FORMAT_CSV:
			fileFormat = "CSV";
			break;
		case FORMAT_QUICKTIME:
			fileFormat = "MOV";
			break;
		case FORMAT_GIF:
		case FORMAT_ANIMATED_GIF:
			fileFormat = "GIF";
			break;
		case FORMAT_NRRD:
			fileFormat = "NRRD";
			break;
	}
	fireExportStarted(newExportJob.getJobID(), exportSpecs.getVCDataIdentifier(), fileFormat);

	try {

		// check required properties first
		String exportBaseURL = null;
		String exportBaseDir = null;
		try {
			exportBaseURL = System.getProperty(PropertyLoader.exportBaseURLProperty);
		} catch (Exception e){
			throw new RuntimeException("required System property \""+PropertyLoader.exportBaseURLProperty+"\" not defined");
		}
		try {
			exportBaseDir = System.getProperty(PropertyLoader.exportBaseDirProperty);
		} catch (Exception e){
			throw new RuntimeException("required System property \""+PropertyLoader.exportBaseDirProperty+"\" not defined");
		}

		
		// see if we've done this before, and try to get it
		// for now, works only for the life of the server (eventually will be persistent)
		Object completed = completedExportRequests.get(exportSpecs);
		if (completed != null) {
			try {
				JobRequest previousRequest = (JobRequest)completedExportRequests.get(exportSpecs);
				File file = new File(exportBaseDir + previousRequest.getJobID() + ".zip");
				if (file.exists()) {
					URL url = new URL(exportBaseURL + file.getName());
					log.print("ExportServiceImpl.makeRemoteFile(): Found previously made file: " + file.getName());
					fireExportCompleted(newExportJob.getJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString());
					// now that we start logging exports, we should do the persistence thing soon...
					// so far retutn null, so we at least don't double log during the same server session
					return null;
				}
			} catch (Throwable exc) {
				completedExportRequests.remove(exportSpecs);
				log.print("ExportServiceImpl.makeRemoteFile(): WARNING: did not find previous export output; attempting to export again");
			}
		}

		// we need to make new output
		log.print("ExportServiceImpl.makeRemoteFile(): Starting new export job: " + newExportJob);
		ExportOutput[] exportOutputs = null;
		switch (exportSpecs.getFormat()) {
			case FORMAT_CSV:
				exportOutputs = asciiExporter.makeASCIIData(newExportJob, user, dataServerImpl, exportSpecs);
				return makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob);
			case FORMAT_QUICKTIME:
				exportOutputs = qtExporter.makeMovieData(newExportJob, user, dataServerImpl, exportSpecs);
				return makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob);
			case FORMAT_GIF:
				exportOutputs = imgExporter.makeImageData(newExportJob, user, dataServerImpl, exportSpecs);
				return makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob);
			case FORMAT_ANIMATED_GIF:
				exportOutputs = imgExporter.makeImageData(newExportJob, user, dataServerImpl, exportSpecs);
				return makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob);
			case FORMAT_NRRD:
				NrrdInfo[] nrrdInfos = rrExporter.makeRasterData(newExportJob, user, dataServerImpl, exportSpecs, exportBaseDir);
				return makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL, nrrdInfos, exportSpecs, newExportJob);
			default:
				throw new DataAccessException("Unknown export format requested");
		}
	} catch (Throwable exc) {
		log.exception(exc);
		fireExportFailed(newExportJob.getJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, exc.getMessage());
		throw new DataAccessException(exc.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 6:47:56 PM)
 */
private ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL, NrrdInfo[] nrrdInfos, ExportSpecs exportSpecs, JobRequest newExportJob) throws DataFormatException, IOException, MalformedURLException {
			boolean exportValid = true;

	// check outputs and package into zip file
	File zipFile = new File(exportBaseDir + newExportJob.getJobID() + ".zip");
	BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(zipFile));
	ZipOutputStream zipOut = new ZipOutputStream(bout);
	try {
		for (int i=0;i<nrrdInfos.length;i++) {
			if (nrrdInfos[i].isHasData()) {
				// add the header (or single) file
				ZipEntry zipEntry = new ZipEntry(nrrdInfos[i].getHeaderfile());
				zipOut.putNextEntry(zipEntry);
				File headerfile = new File(exportBaseDir, nrrdInfos[i].getHeaderfile());
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(headerfile));
				byte[] bytes = new byte[65536];
				try {
					int b = in.read(bytes);
					while (b != -1) {
						zipOut.write(bytes, 0, b);
						b = in.read(bytes);
					}
				} catch (java.io.IOException exc) {
					throw exc;
				} finally {
					// cleanup
					in.close();
					headerfile.delete();
				}
				if (nrrdInfos[i].isSeparateHeader()) {
					// add separate datafile
					zipEntry = new ZipEntry(nrrdInfos[i].getDatafile());
					zipOut.putNextEntry(zipEntry);
					File datafile = new File(exportBaseDir, nrrdInfos[i].getDatafile());
					in = new BufferedInputStream(new FileInputStream(datafile));
					try {
						int b = in.read(bytes);
						while (b != -1) {
							zipOut.write(bytes, 0, b);
							b = in.read(bytes);
						}
					} catch (java.io.IOException exc) {
						throw exc;
					} finally {
						// cleanup
						in.close();
						datafile.delete();
					}
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
		log.print("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
		URL url = new URL(exportBaseURL + zipFile.getName());
		return fireExportCompleted(newExportJob.getJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString());
	}
	else {
		throw new DataFormatException("Export Server could not produce valid data !");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 6:47:56 PM)
 */
private ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL, ExportOutput[] exportOutputs, ExportSpecs exportSpecs, JobRequest newExportJob) throws DataFormatException, IOException, MalformedURLException {
			boolean exportValid = true;

			// check outputs and package into zip file
			File zipFile = new File(exportBaseDir + newExportJob.getJobID() + ".zip");
			FileOutputStream fileOut = new FileOutputStream(zipFile);
			ZipOutputStream zipOut = new ZipOutputStream(fileOut);
			for (int i=0;i<exportOutputs.length;i++) {
				if (exportOutputs[i].isValid()) {
					ZipEntry zipEntry = new ZipEntry(exportOutputs[i].getSimID() + exportOutputs[i].getDataID() + exportOutputs[i].getDataType());
					zipOut.putNextEntry(zipEntry);
					zipOut.write(exportOutputs[i].getData());
				} else {
					exportValid = false;
					break;
				}
			}
			zipOut.close();
			
			if (exportValid) {
				completedExportRequests.put(exportSpecs, newExportJob);
				log.print("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
				URL url = new URL(exportBaseURL + zipFile.getName());
				return fireExportCompleted(newExportJob.getJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString());
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
}