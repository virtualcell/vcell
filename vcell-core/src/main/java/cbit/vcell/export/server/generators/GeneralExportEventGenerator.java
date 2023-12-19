package cbit.vcell.export.server.generators;

import cbit.image.ImageException;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.export.server.*;
import cbit.vcell.export.server.events.ExportEventCommander;
import cbit.vcell.math.MathException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.User;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class GeneralExportEventGenerator implements ExportEventGenerator {
    private static final Logger lg = LogManager.getLogger(GeneralExportEventGenerator.class);
    protected final Map<ExportSpecs, JobRequest> completedExportRequests = new HashMap<>();
    protected final Map<Long, User> jobRequestIDToUserMap = new HashMap<>();
    protected final ExportEventCommander eeCommander;

    public GeneralExportEventGenerator(ExportEventCommander eeCommander){
        this.eeCommander = eeCommander;
    }

    public synchronized void addExportListener(ExportListener listener) {
        this.eeCommander.addExportListener(listener);
    }

    /**
     * Insert the method's description here.
     * Creation date: (3/29/2001 5:18:16 PM)
     * @param listener ExportListener
     */
    public synchronized void removeExportListener(ExportListener listener) {
        this.eeCommander.removeExportListener(listener);
    }

    public ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs) throws DataAccessException{
        return this.makeRemoteFile(outputContext,user, dataServerImpl, exportSpecs, true);
    }

    public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs, boolean bSaveAsZip) throws DataAccessException{
        return this.makeRemoteFile(outputContext, user, dataServerImpl, exportSpecs, bSaveAsZip, null);
    }

    public ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs, boolean bSaveAsZip,
                                      ClientTaskStatusSupport clientTaskStatusSupport) throws DataAccessException {
        // if export completes successfully, we return the generated event for logging
        if (user == null) throw new DataAccessException("ERROR: user is null");

        JobRequest newExportJob = JobRequest.createExportJobRequest(user);
        this.jobRequestIDToUserMap.put(newExportJob.getJobID(), user);
        if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): " + newExportJob + ", " + exportSpecs);
        String fileFormat = this.getFileFormat();
        this.eeCommander.fireExportStarted(newExportJob.getJobID(), user, exportSpecs.getVCDataIdentifier(), fileFormat);

        try {
            String exportBaseURL = this.getExportBaseURL();
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
            AltFileDataContainerManager fileDataContainerManager = new AltFileDataContainerManager();
            try {
                return this.createDataAndMakeRemoteFile(outputContext, newExportJob, user, dataServerImpl, exportSpecs,
                        fileDataContainerManager, clientTaskStatusSupport, bSaveAsZip, exportBaseDir, exportBaseURL);
                    //throw new DataAccessException("Unknown export format requested");
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            } finally {
                fileDataContainerManager.closeAllAndDelete();
            }
        } catch (UserCancelException ex){
            throw ex;
        } catch (Throwable exc) {
            lg.error(exc.getMessage(), exc);
            this.eeCommander.fireExportFailed(newExportJob.getJobID(), user, exportSpecs.getVCDataIdentifier(), fileFormat, exc.getMessage());
            throw new DataAccessException(exc.getMessage());
        }
    }

    protected abstract String getFileFormat();

    protected abstract ExportEvent createDataAndMakeRemoteFile(OutputContext outputContext, JobRequest newExportJob,
                                                               User user, DataServerImpl dataServer, ExportSpecs exportSpecs,
                                                               AltFileDataContainerManager fileDataContainerManager,
                                                               ClientTaskStatusSupport clientTaskStatusSupport,
                                                               boolean bSaveAsZip, String exportBaseDir, String exportBaseURL)
            throws DataAccessException, DataFormatException, IOException, ImageException, MathException;

    protected abstract String getExportBaseURL();

    public ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
                                      NrrdInfo[] nrrdInfos, ExportSpecs exportSpecs, JobRequest newExportJob,
                                      AltFileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException {
        // check outputs and package into zip file
        File zipFile = new File(exportBaseDir + newExportJob.getJobID() + ".zip");
        try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))){
            for (NrrdInfo nInfo : nrrdInfos) {
                if (!nInfo.isHasData()) throw new DataFormatException("Export Server could not produce valid data!");
                // nrrdInfo 'header' file contains either just the header or both the header and data together
                zipOut.putNextEntry(new ZipEntry(nInfo.getCanonicalFilename((nInfo.isSeparateHeader()))));
                fileDataContainerManager.writeAndFlush(nInfo.getHeaderFileID(), zipOut);

                if (!nInfo.isSeparateHeader()) continue;
                // The data was not saved with the 'header' file, so we must save it separately
                zipOut.putNextEntry(new ZipEntry(nInfo.getCanonicalFilename(false)));
                fileDataContainerManager.writeAndFlush(nInfo.getDataFileID(), zipOut);
            }
        }
        this.completedExportRequests.put(exportSpecs, newExportJob);
        if (lg.isTraceEnabled())
            lg.trace(this.getClass().getSimpleName() + ".makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
        return this.eeCommander.fireExportCompleted(newExportJob.getJobID(),
                this.jobRequestIDToUserMap.get(newExportJob.getJobID()), exportSpecs.getVCDataIdentifier(), fileFormat,
                (new URL(exportBaseURL + zipFile.getName())).toString(), exportSpecs);
    }

    public ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
                                      ExportOutput[] exportOutputs, ExportSpecs exportSpecs, JobRequest newExportJob,
                                      AltFileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException {
        long newExportJobID = newExportJob.getJobID();
        this.eeCommander.fireExportAssembling(newExportJobID, this.jobRequestIDToUserMap.get(newExportJobID),
                exportSpecs.getVCDataIdentifier(), fileFormat);

        // check outputs and package into zip file
        File zipFile = new File(exportBaseDir + newExportJobID + ".zip");
        try (ZipOutputStream zipOutputStream =
                     new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))){
            for (ExportOutput output : exportOutputs){
                if (!output.isValid()) throw new DataFormatException("Export Server could not produce valid data!");
                StringBuilder filename = (new StringBuilder()).append(output.getSimID()).append(output.getDataID());
                if (!filename.toString().endsWith(output.getDataType())) filename.append(output.getDataType());
                ZipEntry zipEntry = new ZipEntry(filename.toString());
                zipOutputStream.putNextEntry(zipEntry);
                output.writeDataToOutputStream(zipOutputStream, fileDataContainerManager);
            }
        }
        this.completedExportRequests.put(exportSpecs, newExportJob);
        if (lg.isTraceEnabled())
            lg.trace(this.getClass().getSimpleName() + ".makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
        return this.eeCommander.fireExportCompleted(newExportJobID, this.jobRequestIDToUserMap.get(newExportJobID),
                exportSpecs.getVCDataIdentifier(), fileFormat,
                (new URL(exportBaseURL + zipFile.getName())).toString(), exportSpecs);
    }

    public ExportEvent makeRemoteFile_Unzipped(String fileFormat, String exportBaseDir,
                                               String exportBaseURL, ExportOutput[] exportOutputs,
                                               ExportSpecs exportSpecs, JobRequest newExportJob,
                                               AltFileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException {
        StringBuilder fileNames = new StringBuilder();
        LinkedList<ExportOutput> exportOutputList = new LinkedList<>(Arrays.asList(exportOutputs));
        if (exportOutputList.isEmpty()) throw new DataFormatException("Export Server received no data!");
        //do the first file of exportOutputs separately (for VFRAP, there is only one export output)
        DataFormatException dfe = new DataFormatException("Export Server could not produce valid data!");
        ExportOutput firstOutput = exportOutputList.remove();
        if (!firstOutput.isValid()) throw dfe;

        // We have a valid first entry, now let's process it.
        String extStr = "." + fileFormat;
        long nextExportJobID = newExportJob.getJobID();

        File firstFile = new File(exportBaseDir + nextExportJobID + extStr);
        try (BufferedOutputStream firstOutStream = new BufferedOutputStream(new FileOutputStream(firstFile))){
            firstOutput.writeDataToOutputStream(firstOutStream, fileDataContainerManager);
        }
        fileNames.append(firstFile.getName());
        //if there are more export outputs, loops through the second till the last.
        for (int i = 0; i < exportOutputList.size(); i++){
            ExportOutput output = exportOutputList.get(i);
            if (!output.isValid()) throw dfe;
            File nextFile = new File(exportBaseDir + nextExportJobID + "_" + (i + 1) + extStr);
            try (BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(nextFile))){
                firstOutput.writeDataToOutputStream(outStream, fileDataContainerManager);
            }
            fileNames.append("\t").append(nextFile.getName());
        }
        this.completedExportRequests.put(exportSpecs, newExportJob);
        if (lg.isTraceEnabled())
            lg.trace(this.getClass().getSimpleName() + ".makeRemoteFile_Unzipped(): Successfully exported to file: " + fileNames);
        return this.eeCommander.fireExportCompleted(nextExportJobID, this.jobRequestIDToUserMap.get(nextExportJobID),
                exportSpecs.getVCDataIdentifier(), fileFormat,
                (new URL(exportBaseURL + fileNames)).toString(), exportSpecs);
    }
}
