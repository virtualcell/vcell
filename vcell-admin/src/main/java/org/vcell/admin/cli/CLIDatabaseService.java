package org.vcell.admin.cli;

import cbit.sql.CompareDatabaseSchema;
import cbit.sql.QueryHashtable;
import cbit.vcell.modeldb.*;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.xml.XmlParseException;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CLIDatabaseService implements AutoCloseable {
    private ConnectionFactory conFactory = null;
    private List<BioModelInfo> publicBioModelInfos = null;

    public CLIDatabaseService() throws SQLException {
        conFactory = DatabaseService.getInstance().createConnectionFactory();
    }

    public List<BioModelInfo> queryPublicBioModels() throws SQLException, DataAccessException {
        if (publicBioModelInfos == null) {
            AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
            VCInfoContainer vcic = adminDbTopLevel.getPublicOracleVCInfoContainer(false);
            publicBioModelInfos = Arrays.asList(vcic.getBioModelInfos());
        }
        return publicBioModelInfos;
    }

    public List<BioModelInfo> queryPublishedBioModels() throws SQLException, DataAccessException {
        List<BioModelInfo> publicBioModelInfos = queryPublicBioModels();
        return publicBioModelInfos.stream()
                .filter(bmi -> bmi.getPublicationInfos()!=null && bmi.getPublicationInfos().length>0)
                .collect(Collectors.toList());
    }

    public String getPublicBiomodelXML(KeyValue bioModelKey, boolean bRegenerateXML) throws SQLException, DataAccessException {
        List<BioModelInfo> publicBioModelInfos = queryPublicBioModels();
        Optional<BioModelInfo> bioModelInfo = publicBioModelInfos.stream().filter(bmi -> bmi.getVersion().getVersionKey().equals(bioModelKey)).findFirst();
        if (!bioModelInfo.isPresent()){
            throw new RuntimeException("biomodel key "+bioModelKey+" not found as a public bioodel");
        }
        DatabaseServerImpl databaseServer = new DatabaseServerImpl(conFactory, conFactory.getKeyFactory());
        ServerDocumentManager serverDocumentManager = new ServerDocumentManager(databaseServer);
        User user = bioModelInfo.get().getVersion().getOwner(); // impersonate owner for this public model
        String biomodelXML = serverDocumentManager.getBioModelXML(new QueryHashtable(),user,bioModelKey,bRegenerateXML);
        return biomodelXML;
    }

    public String getBioModelVCML(BioModelInfo bioModelInfo, boolean bWithXMLCache) throws SQLException, DataAccessException, XmlParseException {
        DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, conFactory.getKeyFactory());
        KeyValue versionKey = bioModelInfo.getVersion().getVersionKey();
        if (bWithXMLCache) {
            return databaseServerImpl.getBioModelXML(User.tempUser, versionKey).toString();
        }else{
            return databaseServerImpl.getServerDocumentManager().getBioModelUnresolved(
                    new QueryHashtable(), User.tempUser, versionKey);
        }
     }

    public SimulationJobStatusPersistent[] querySimulationJobStatus(KeyValue simKey) throws SQLException, DataAccessException {
        AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
        SimulationJobStatusPersistent[] statuses = adminDbTopLevel.getSimulationJobStatusArray(simKey, false);
        return statuses;
    }

    public MathVerifier getMathVerifier() throws DataAccessException, SQLException {
        LocalAdminDbServer adminDbServer = new LocalAdminDbServer(conFactory, conFactory.getKeyFactory());
        return new MathVerifier(conFactory, adminDbServer);
    }

    public void close() throws SQLException {
        conFactory.close();
    }

    public CompareDatabaseSchema getCompareDatabaseSchemas() {
        return new CompareDatabaseSchema(conFactory);
    }
}
