package cbit.vcell.export.server.generators;
import java.io.IOException;
import java.util.zip.DataFormatException;

import cbit.rmi.event.ExportListener;
import cbit.vcell.export.server.ExportOutput;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.AltFileDataContainerManager;
import cbit.vcell.export.server.JobRequest;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.*;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;

public interface ExportEventGenerator {

    public /*synchronized*/ void addExportListener(ExportListener listener);

    public /*synchronized*/ void removeExportListener(ExportListener listener);

    public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs) throws DataAccessException;

    public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs, boolean bSaveAsZip) throws DataAccessException;

    public ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs, boolean bSaveAsZip,
                                      ClientTaskStatusSupport clientTaskStatusSupport) throws DataAccessException;

    public ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
                                      NrrdInfo[] nrrdInfos, ExportSpecs exportSpecs, JobRequest newExportJob,
                                      AltFileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException;

    public ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
                                       ExportOutput[] exportOutputs, ExportSpecs exportSpecs, JobRequest newExportJob,
                                       AltFileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException;

    public ExportEvent makeRemoteFile_Unzipped(String fileFormat, String exportBaseDir, String exportBaseURL,
                                               ExportOutput[] exportOutputs, ExportSpecs exportSpecs,
                                               JobRequest newExportJob,
                                               AltFileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException;
}
