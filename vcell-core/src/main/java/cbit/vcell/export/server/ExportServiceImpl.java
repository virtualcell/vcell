/*
 * Copyright (C) 1999-2023 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;
import java.io.IOException;
import java.util.zip.DataFormatException;

import cbit.vcell.export.server.events.ExportEventCommander;
import cbit.vcell.export.server.generators.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;

public class ExportServiceImpl implements ExportConstants, ExportService {
	public static final Logger lg = LogManager.getLogger(ExportServiceImpl.class);
	private final ExportEventCommander eeCommander = new ExportEventCommander();
	private ExportEventGenerator defaultExportEventGenerator;

	/**
	 * Adds an export listener to the class
	 * Creation date: (3/29/2001 5:18:16 PM)
	 * @param listener ExportListener
	 */
	public synchronized void addExportListener(ExportListener listener) {
		this.eeCommander.addExportListener(listener);
		// We need a "default" generator to perform non-specific exports any generator can do; we're going with CSV
		this.defaultExportEventGenerator = new CsvExportEventGenerator(this.eeCommander, this);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/29/2001 5:18:16 PM)
	 * @param listener ExportListener
	 */
	public synchronized void removeExportListener(ExportListener listener) {
		this.eeCommander.removeExportListener(listener);
	}

	public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl,
									  ExportSpecs exportSpecs) throws DataAccessException {
		return makeRemoteFile(outputContext,user, dataServerImpl, exportSpecs, true);
	}

	public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl,
									  ExportSpecs exportSpecs, boolean bSaveAsZip) throws DataAccessException {
		return makeRemoteFile(outputContext, user, dataServerImpl, exportSpecs, bSaveAsZip, null);
	}

	public ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServerImpl,
									  ExportSpecs exportSpecs, boolean bSaveAsZip,
									  ClientTaskStatusSupport clientTaskStatusSupport) throws DataAccessException {
		ExportEventGenerator eeg = switch (exportSpecs.getFormat()) {
			case CSV 				-> this.defaultExportEventGenerator;
			case HDF5 				-> new Hdf5ExportEventGenerator(this.eeCommander, this);
			case QUICKTIME			-> new QuicktimeExportEventGenerator(this.eeCommander, this);
			case GIF, ANIMATED_GIF	-> new GifExportEventGenerator(this.eeCommander, this);
			case FORMAT_JPEG		-> new JpegExportEventGenerator(this.eeCommander, this);
			case NRRD				-> new NrrdExportEventGenerator(this.eeCommander, this);
			//case IMAGEJ:
			case UCD 				-> new UcdExportEventGenerator(this.eeCommander, this);
			case PLY 				-> new StanfordPolyTextureExportEventGenerator(this.eeCommander, this);
			case VTK_IMAGE 			-> new VtkImageExportEventGenerator(this.eeCommander, this);
			case VTK_UNSTRUCT 		-> new VtkUnstructuredExportEventGenerator(this.eeCommander, this);
			default -> throw new DataAccessException("Unknown export format requested");
		};
		return eeg.makeRemoteFile(outputContext, user, dataServerImpl, exportSpecs, bSaveAsZip, clientTaskStatusSupport);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/26/2004 6:47:56 PM)
	 */
	private ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL, NrrdInfo[] nrrdInfos,
									   ExportSpecs exportSpecs, JobRequest newExportJob,
									   AltFileDataContainerManager fileDataContainerManager)
			throws DataFormatException, IOException {
		return this.defaultExportEventGenerator.makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL,
				nrrdInfos, exportSpecs, newExportJob, fileDataContainerManager);
	}


	/**
	 * This function saves the remote file into a compressed zip file.
	 * Creation date: (4/26/2004 6:47:56 PM)
	 */
	private ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
									   ExportOutput[] exportOutputs, ExportSpecs exportSpecs, JobRequest newExportJob,
									   AltFileDataContainerManager fileDataContainerManager)
			throws DataFormatException, IOException {
		return this.defaultExportEventGenerator.makeRemoteFile(fileFormat, exportBaseDir, exportBaseURL, exportOutputs,
				exportSpecs, newExportJob, fileDataContainerManager);
	}

	/**
	 * Save remote file in it original format without compression.
	 */
	private ExportEvent makeRemoteFile_Unzipped(String fileFormat, String exportBaseDir, String exportBaseURL,
												ExportOutput[] exportOutputs, ExportSpecs exportSpecs,
												JobRequest newExportJob,
												AltFileDataContainerManager fileDataContainerManager)
			throws DataFormatException, IOException
	{
		return this.defaultExportEventGenerator.makeRemoteFile_Unzipped(fileFormat, exportBaseDir, exportBaseURL,
				exportOutputs, exportSpecs, newExportJob, fileDataContainerManager);
	}
}
