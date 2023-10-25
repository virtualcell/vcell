package cbit.vcell.export.server.factories;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cbit.vcell.export.server.ExportOutput;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.export.server.JobRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.*;

import com.google.common.io.Files;

import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.vcell.export.nrrd.NrrdInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;

public interface ExportEventAbstractFactory {
    public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs) throws DataAccessException;

    public ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs, boolean bSaveAsZip) throws DataAccessException;

    public ExportEvent makeRemoteFile(OutputContext outputContext, User user, DataServerImpl dataServerImpl,
                                      ExportSpecs exportSpecs, boolean bSaveAsZip,
                                      ClientTaskStatusSupport clientTaskStatusSupport) throws DataAccessException;

    public ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
                                      NrrdInfo[] nrrdInfos, ExportSpecs exportSpecs, JobRequest newExportJob,
                                      FileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException;

    public ExportEvent makeRemoteFile(String fileFormat, String exportBaseDir, String exportBaseURL,
                                       ExportOutput[] exportOutputs, ExportSpecs exportSpecs, JobRequest newExportJob,
                                       FileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException;

    public ExportEvent makeRemoteFile_Unzipped(String fileFormat, String exportBaseDir, String exportBaseURL,
                                               ExportOutput[] exportOutputs, ExportSpecs exportSpecs,
                                               JobRequest newExportJob,
                                               FileDataContainerManager fileDataContainerManager)
            throws DataFormatException, IOException;
}
