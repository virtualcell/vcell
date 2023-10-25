package cbit.vcell.export.server.factories;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.export.server.*;
import cbit.vcell.export.server.events.ExportEventCommander;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class GeneralExportEventFactory {
    private static final Logger lg = LogManager.getLogger(GeneralExportEventFactory.class);
    protected final Map<ExportSpecs, JobRequest> completedExportRequests = new HashMap<>();
    protected final Map<Long, User> jobRequestIDToUserMap = new HashMap<>();
    protected final ExportEventCommander eeCommander = new ExportEventCommander();

    public ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs) throws DataAccessException{
        return this.makeRemoteFile(outputContext,user, dataServerImpl, exportSpecs, true);
    }

    public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs, boolean bSaveAsZip) throws DataAccessException{
        return this.makeRemoteFile(outputContext, user, dataServerImpl, exportSpecs, bSaveAsZip, null);
    }

    public abstract ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs, boolean bSaveAsZip,
                                      ClientTaskStatusSupport clientTaskStatusSupport) throws DataAccessException;

    public ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
                                      NrrdInfo[] nrrdInfos, ExportSpecs exportSpecs, JobRequest newExportJob,
                                      FileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException{
        boolean exportValid = true;

        // check outputs and package into zip file
        File zipFile = new File(exportBaseDir + newExportJob.getJobID() + ".zip");
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
        } finally {
            zipOut.close();
        }

        if (exportValid) {
            completedExportRequests.put(exportSpecs, newExportJob);
            if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + zipFile.getName());
            URL url = new URL(exportBaseURL + zipFile.getName());
            return this.eeCommander.fireExportCompleted(newExportJob.getJobID(),
                    this.jobRequestIDToUserMap.get(newExportJob.getJobID()), exportSpecs.getVCDataIdentifier(), fileFormat,
                    url.toString(), exportSpecs);
        } else {
            throw new DataFormatException("Export Server could not produce valid data !");
        }
    }

    public ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
                                      ExportOutput[] exportOutputs, ExportSpecs exportSpecs, JobRequest newExportJob,
                                      FileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException{
        boolean exportValid = true;
        long newExportJobID = newExportJob.getJobID();
        LinkedList<ExportOutput> exportOutputList = new LinkedList<>(Arrays.asList(exportOutputs));
        this.eeCommander.fireExportAssembling(newExportJobID, this.jobRequestIDToUserMap.get(newExportJobID),
                exportSpecs.getVCDataIdentifier(), fileFormat);

        // check outputs and package into zip file
        File zipFile = new File(exportBaseDir + newExportJobID + ".zip");
        try (ZipOutputStream zipOutputStream =
                     new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))){
            for (ExportOutput output : exportOutputList){
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
                                               FileDataContainerManager fileDataContainerManager)
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
