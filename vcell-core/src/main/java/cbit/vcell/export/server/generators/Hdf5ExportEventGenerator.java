package cbit.vcell.export.server.generators;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.export.server.events.ExportEventCommander;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public class Hdf5ExportEventGenerator extends GeneralExportEventGenerator {
    private static final Logger lg = LogManager.getLogger(Hdf5ExportEventGenerator.class);

    private final ASCIIExporter asciiExporter;

    public Hdf5ExportEventGenerator(ExportEventCommander eeCommander, ExportServiceImpl exportService){
        super(eeCommander);
        this.asciiExporter = new ASCIIExporter(exportService);
    }

    @Override
    protected String getFileFormat() { return "HDF5"; }

    @Override
    protected ExportEvent createDataAndMakeRemoteFile(OutputContext outputContext, JobRequest newExportJob, User user,
                                                      DataServerImpl dataServer, ExportSpecs exportSpecs,
                                                      AltFileDataContainerManager fileDataContainerManager,
                                                      ClientTaskStatusSupport clientTaskStatusSupport,
                                                      boolean bSaveAsZip, String exportBaseDir, String exportBaseURL)
            throws DataAccessException, IOException {
        Collection<ExportOutput> asciiOut = asciiExporter.makeASCIIData(outputContext,
                newExportJob, user, dataServer, exportSpecs,fileDataContainerManager);
        ExportOutput[] exportOutputs = asciiOut.toArray(new ExportOutput[0]);

        if (!((ASCIISpecs)exportSpecs.getFormatSpecificSpecs()).isHDF5())
            throw new RuntimeException("ASCIISpecs are for HDF5, but specific specs say CSV");

        // I'm not entirely sure why, but we only process the 0th index. Maybe there's never anything more?
        ExportOutput exportOutput = exportOutputs[0];
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            exportOutput.writeDataToOutputStream(outputStream, fileDataContainerManager); //Get location of temp HDF5 file
            File tempHDF5File = new File(outputStream.toString());
            File downloadableHDF5File = new File(exportBaseDir + newExportJob.getJobID() + ".hdf5");
            Files.copy(tempHDF5File, downloadableHDF5File);
            tempHDF5File.deleteOnExit();
            return this.eeCommander.fireExportCompleted(newExportJob.getJobID(), user,
                    exportSpecs.getVCDataIdentifier(), this.getFileFormat(),
                    (new URL(exportBaseURL + downloadableHDF5File.getName())).toString(), exportSpecs);
        }
    }
}
