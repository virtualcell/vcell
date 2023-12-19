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
import java.util.zip.DataFormatException;

public class QuicktimeExportEventGenerator extends GeneralExportEventGenerator {
    private final IMGExporter imgExporter;

    public QuicktimeExportEventGenerator(ExportEventCommander eeCommander, ExportServiceImpl exportService){
        super(eeCommander);
        this.imgExporter = new IMGExporter(exportService);
    }

    @Override
    protected String getFileFormat() {
        return "MOV";
    }

    @Override
    protected ExportEvent createDataAndMakeRemoteFile(OutputContext outputContext, JobRequest newExportJob, User user,
                                                      DataServerImpl dataServer, ExportSpecs exportSpecs,
                                                      AltFileDataContainerManager fileDataContainerManager,
                                                      ClientTaskStatusSupport clientTaskStatusSupport,
                                                      boolean bSaveAsZip, String exportBaseDir, String exportBaseURL)
            throws DataAccessException, DataFormatException, IOException {
        ExportOutput[] exportOutputs = imgExporter.makeMediaData(outputContext,newExportJob, user, dataServer,
                exportSpecs, clientTaskStatusSupport, fileDataContainerManager);
        boolean bOverrideZip = (exportOutputs.length == 1);
        if (bSaveAsZip && !bOverrideZip){
            return makeRemoteFile(this.getFileFormat(), exportBaseDir, exportBaseURL, exportOutputs,
                    exportSpecs, newExportJob, fileDataContainerManager);
        } else {
            return makeRemoteFile_Unzipped(this.getFileFormat(), exportBaseDir, exportBaseURL, exportOutputs,
                    exportSpecs, newExportJob,fileDataContainerManager);
        }
    }

    @Override
    protected String getExportBaseURL() {
        return PropertyLoader.getRequiredProperty(PropertyLoader.exportBaseURLProperty);
    }
}
