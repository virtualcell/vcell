package cbit.vcell.export.server.generators;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.export.server.events.ExportEventCommander;
import cbit.vcell.math.MathException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.zip.DataFormatException;

public class N5ExportEventGenerator extends GeneralExportEventGenerator {
    private static final Logger lg = LogManager.getLogger(Hdf5ExportEventGenerator.class);

    private final N5Exporter n5Exporter;

    public N5ExportEventGenerator(ExportEventCommander eeCommander, ExportServiceImpl exportService){
        super(eeCommander);
        this.n5Exporter = new N5Exporter(exportService);
    }

    @Override
    protected String getFileFormat() { return N5Specs.n5Suffix.toUpperCase(); }

    @Override
    protected ExportEvent createDataAndMakeRemoteFile(OutputContext outputContext, JobRequest newExportJob, User user,
                                                      DataServerImpl dataServer, ExportSpecs exportSpecs,
                                                      AltFileDataContainerManager fileDataContainerManager,
                                                      ClientTaskStatusSupport clientTaskStatusSupport,
                                                      boolean bSaveAsZip, String exportBaseDir, String exportBaseURL)
            throws DataAccessException, IOException {
        String ignored = exportBaseDir;
        long jobID = ((VCSimulationDataIdentifier) exportSpecs.getVCDataIdentifier()).getJobIndex();
        n5Exporter.initalizeDataControllers(exportSpecs.getVCDataIdentifier().getDataKey().toString() ,user.getName(), user.getID().toString(), jobID);
        ExportOutput exportOutput;
        try {
            exportOutput = n5Exporter.makeN5Data(outputContext, newExportJob, exportSpecs, fileDataContainerManager);
        } catch (MathException e){
            throw new RuntimeException(e);
        }
        URI uri;
        try {
            uri = new URI(exportBaseURL);
        } catch (java.net.URISyntaxException e){
            throw new RuntimeException("Error creating URI", e);
        }
        String url = uri.getScheme() + "://" + uri.getHost() + ":" + PropertyLoader.getRequiredProperty(PropertyLoader.s3ProxyExternalPort) + "/" + n5Exporter.n5BucketName + "/";
        if (!exportOutput.isValid()) {
            throw new RuntimeException(new DataFormatException("Export Server could not produce valid data !"));
        }

        completedExportRequests.put(exportSpecs, newExportJob);
        if (lg.isTraceEnabled()) lg.trace("ExportServiceImpl.makeRemoteFile(): Successfully exported to file: " + n5Exporter.getN5FileNameHash());
        return this.eeCommander.fireExportCompleted(newExportJob.getJobID(), user, exportSpecs.getVCDataIdentifier(), this.getFileFormat(), url, exportSpecs);
    }

    @Override
    protected String getExportBaseURL() {
        return PropertyLoader.getRequiredProperty(PropertyLoader.s3ExportBaseURLProperty);
    }
}
