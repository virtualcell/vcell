package cbit.vcell.export.server.generators;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.export.server.events.ExportEventCommander;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.IOException;
import java.util.Collection;
import java.util.zip.DataFormatException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CsvExportEventGenerator extends GeneralExportEventGenerator {
    private static final Logger lg = LogManager.getLogger(CsvExportEventGenerator.class);

    private final ASCIIExporter asciiExporter;

    public CsvExportEventGenerator(ExportEventCommander eeCommander, ExportServiceImpl exportService){
        super(eeCommander);
        this.asciiExporter = new ASCIIExporter(exportService);
    }
    @Override
    protected String getFileFormat() {
        return "CSV";
    }

    @Override
    protected ExportEvent createDataAndMakeRemoteFile(OutputContext outputContext, JobRequest newExportJob, User user,
                                                      DataServerImpl dataServer, ExportSpecs exportSpecs,
                                                      AltFileDataContainerManager fileDataContainerManager,
                                                      ClientTaskStatusSupport clientTaskStatusSupport,
                                                      boolean bSaveAsZip, String exportBaseDir, String exportBaseURL)
            throws DataAccessException, DataFormatException, IOException {
        Collection<ExportOutput> asciiOut = this.asciiExporter.makeASCIIData(outputContext,
                newExportJob, user, dataServer, exportSpecs,fileDataContainerManager);

        if (((ASCIISpecs)exportSpecs.getFormatSpecificSpecs()).isHDF5())
            throw new RuntimeException("ASCIISpecs are for CSV, but specific specs say HDF5");

        return makeRemoteFile(this.getFileFormat(), exportBaseDir, exportBaseURL, asciiOut.toArray(new ExportOutput[0]),
                exportSpecs, newExportJob, fileDataContainerManager);
    }

    @Override
    protected String getExportBaseURL() {
        return PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseURLProperty);
    }
}
