package cbit.vcell.export.server.generators;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.export.server.events.ExportEventCommander;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.IOException;
import java.util.zip.DataFormatException;

public class UcdExportEventGenerator extends GeneralExportEventGenerator {
    private final RasterExporter rrExporter;

    public UcdExportEventGenerator(ExportEventCommander eeCommander, ExportServiceImpl exportService){
        super(eeCommander);
        this.rrExporter = new RasterExporter(exportService);
    }
    @Override
    protected String getFileFormat() {
        return "UCD";
    }

    @Override
    protected ExportEvent createDataAndMakeRemoteFile(OutputContext outputContext, JobRequest newExportJob, User user,
                                                      DataServerImpl dataServer, ExportSpecs exportSpecs,
                                                      AltFileDataContainerManager fileDataContainerManager,
                                                      ClientTaskStatusSupport clientTaskStatusSupport,
                                                      boolean bSaveAsZip, String exportBaseDir, String exportBaseURL)
            throws DataAccessException, DataFormatException, IOException {
        ExportOutput[] exportOutputs = rrExporter.makeUCDData(outputContext,newExportJob, user, dataServer,
                exportSpecs, fileDataContainerManager);
        return makeRemoteFile(this.getFileFormat(), exportBaseDir, exportBaseURL, exportOutputs,
                exportSpecs, newExportJob, fileDataContainerManager);
    }
}
