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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cbit.rmi.event.ExportStatusEventCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.User;

import com.google.common.io.Files;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.VCSimulationDataIdentifier;

/**
 * This type was created in VisualAge.
 */
public class ExportServiceImpl implements ExportService {
	public static final Logger lg = LogManager.getLogger(ExportServiceImpl.class);
	
	private final OldExportEventCreator eventCreator = new OldExportEventCreator();

	public ExportStatusEventCreator getEventCreator() {
		return eventCreator;
	}


public ExportServiceImpl() {
}


public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs)throws DataAccessException {
	return makeRemoteFile(outputContext,user, dataServerImpl, exportSpecs, eventCreator, JobRequest.createExportJobRequest(user).getExportJobID());
}

public ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServer, ExportSpecs exportSpecs, boolean zip, ClientTaskStatusSupport clientTaskStatusSupport)throws DataAccessException {
		return makeRemoteFile(outputContext, user, dataServer, exportSpecs, eventCreator, zip, clientTaskStatusSupport, JobRequest.createExportJobRequest(user).getExportJobID());
}

public static ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServer, ExportSpecs exportSpecs, ExportStatusEventCreator eventCreator, long jobRequestID) throws DataAccessException{
	return makeRemoteFile(outputContext, user, dataServer, exportSpecs, eventCreator, true, null, jobRequestID);
}

private static ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs, ExportStatusEventCreator eventCreator, boolean bSaveAsZip, ClientTaskStatusSupport clientTaskStatusSupport, long jobRequestID) throws DataAccessException {
	// if export completes successfully, we return the generated event for logging
	if (user == null) {
		throw new DataAccessException("ERROR: user is null");
	}
	JobRequest newExportJob = JobRequest.createExportJobRequest(user, jobRequestID);
	if (eventCreator instanceof OldExportEventCreator evc){
		evc.putJobRequest(newExportJob.getExportJobID(), user);
	}
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
	}
	eventCreator.fireExportStarted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat);

	try {

		String exportBaseURL = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseURLProperty);
		String exportBaseDir = PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseDirInternalProperty);

		// we need to make new output
		if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Starting new export job: " + newExportJob);
		FileDataContainerManager fileDataContainerManager = new FileDataContainerManager();
		try{
			ExportOutput[] exportOutputs = null;
			switch (exportSpecs.getFormat()) {
				case CSV:
				case HDF5:
					ASCIIExporter asciiExporter = new ASCIIExporter(eventCreator);
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
						return eventCreator.fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString(),exportSpecs);
					}
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager, eventCreator);
				case QUICKTIME:
				case GIF:
				case FORMAT_JPEG:
				case ANIMATED_GIF:
					IMGExporter imgExporter = new IMGExporter(eventCreator);
					exportOutputs = imgExporter.makeMediaData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,clientTaskStatusSupport,fileDataContainerManager);
					boolean bOverrideZip = exportOutputs.length == 1;
					if(bSaveAsZip && !bOverrideZip){
						return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager, eventCreator);
					}else{
						return makeRemoteFile_Unzipped(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager, eventCreator);
					}
				case NRRD:
					RasterExporter rrExporter = new RasterExporter(eventCreator);
					NrrdInfo[] nrrdInfos = rrExporter.makeRasterData(outputContext,newExportJob, user, dataServerImpl, exportSpecs, fileDataContainerManager);
					return makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL, nrrdInfos, exportSpecs, newExportJob, fileDataContainerManager, eventCreator);
				case UCD:
					RasterExporter rrExporterUCD = new RasterExporter(eventCreator);
					exportOutputs = rrExporterUCD.makeUCDData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager, eventCreator);
				case PLY:
					RasterExporter rrExporterPLY = new RasterExporter(eventCreator);
					exportOutputs = rrExporterPLY.makePLYWithTexData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager, eventCreator);
				case VTK_IMAGE:
					RasterExporter rrExporterVTK = new RasterExporter(eventCreator);
					exportOutputs = rrExporterVTK.makeVTKImageData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager, eventCreator);
				case VTK_UNSTRUCT:
					RasterExporter rrExporterVTKU = new RasterExporter(eventCreator);
					exportOutputs = rrExporterVTKU.makeVTKUnstructuredData(outputContext,newExportJob, user, dataServerImpl, exportSpecs,fileDataContainerManager);
					return saveResultsToRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs, exportSpecs, newExportJob,fileDataContainerManager, eventCreator);
				case N5:
					N5Exporter n5Exporter = new N5Exporter(eventCreator, user, dataServerImpl,  (VCSimulationDataIdentifier) exportSpecs.getVCDataIdentifier());
					ExportOutput exportOutput = n5Exporter.makeN5Data(outputContext, newExportJob, exportSpecs, fileDataContainerManager);
					return makeRemoteN5File(fileFormat, n5Exporter.getN5FileNameHash(), exportOutput, exportSpecs, newExportJob, n5Exporter.getN5FilePathSuffix(), eventCreator);
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
		eventCreator.fireExportFailed(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, exc.getMessage());
		throw new DataAccessException(exc.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 6:47:56 PM)
 */
private static ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
								   NrrdInfo[] nrrdInfos, ExportSpecs exportSpecs, JobRequest newExportJob,
								   FileDataContainerManager fileDataContainerManager,
								   ExportStatusEventCreator eventCreator) throws DataFormatException, IOException, MalformedURLException {
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
		if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
		URL url = new URL(exportBaseURL + zipFile.getName());
		return eventCreator.fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString(),exportSpecs);
	}
	else {
		throw new DataFormatException("Export Server could not produce valid data !");
	}
}


/**
 * This function saves the remote file into a compressed zip file.
 * Creation date: (4/26/2004 6:47:56 PM)
 */
private static ExportEvent saveResultsToRemoteFile(String fileFormat, String exportBaseDir,
												   String exportBaseURL, ExportOutput[] exportOutputs,
												   ExportSpecs exportSpecs, JobRequest newExportJob,
												   FileDataContainerManager fileDataContainerManager,
												   ExportStatusEventCreator eventCreator) throws DataFormatException, IOException, MalformedURLException {
			boolean exportValid = true;
			eventCreator.fireExportAssembling(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat);

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
				if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
				URL url = new URL(exportBaseURL + zipFile.getName());
				return eventCreator.fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString(),exportSpecs);
			}
			else {
				throw new DataFormatException("Export Server could not produce valid data !");
			}
}

/**
 * Save remote file in it original format without compression.
 */
private static ExportEvent makeRemoteFile_Unzipped(String fileFormat, String exportBaseDir,
												   String exportBaseURL, ExportOutput[] exportOutputs,
												   ExportSpecs exportSpecs, JobRequest newExportJob,
												   FileDataContainerManager fileDataContainerManager,
												   ExportStatusEventCreator eventCreator) throws DataFormatException, IOException, MalformedURLException
{
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
		if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + fileNames);
		URL url = new URL(exportBaseURL + fileNames);
		return eventCreator.fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url.toString(),exportSpecs);
	}
	else {
		throw new DataFormatException("Export Server could not produce valid data !");
	}
}


private static ExportEvent makeRemoteN5File(String fileFormat, String fileName, ExportOutput exportOutput,
											ExportSpecs exportSpecs, JobRequest newExportJob, String pathSuffix,
											ExportStatusEventCreator eventCreator) throws DataFormatException, IOException{
	if (exportOutput.isValid()) {
		String url = PropertyLoader.getRequiredProperty(PropertyLoader.s3ExportBaseURLProperty);
		url += "/" + pathSuffix;
		N5Specs n5Specs = (N5Specs) exportSpecs.getFormatSpecificSpecs();
		url += "?dataSetName=" + newExportJob.getExportJobID();
		if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + fileName);
		return eventCreator.fireExportCompleted(newExportJob.getExportJobID(), exportSpecs.getVCDataIdentifier(), fileFormat, url, exportSpecs);
	}
	else {
		throw new DataFormatException("Export Server could not produce valid data !");
	}
}

}
