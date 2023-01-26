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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CLIDatabaseService implements AutoCloseable {
    private ConnectionFactory conFactory = null;
    private List<BioModelInfo> publicBioModelInfos = null;
    private DatabaseServerImpl databaseServer = null;

    public CLIDatabaseService() throws SQLException {
        conFactory = DatabaseService.getInstance().createConnectionFactory();
    }

    private DatabaseServerImpl getDatabaseServer() throws DataAccessException {
        if (databaseServer==null){
            databaseServer = new DatabaseServerImpl(conFactory, conFactory.getKeyFactory());
        }
        return databaseServer;
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
        List<BioModelInfo> publishedBioModelInfos = publicBioModelInfos.stream()
                .filter(bmi -> bmi.getPublicationInfos()!=null && bmi.getPublicationInfos().length>0)
                .collect(Collectors.toList());

        //
        // the publishedBioModelInfos may include multiple versions of the same BioModel, let's return only the
        // most recent version of each published BioModel (Same BranchID, take BioModel with biggest key .. latest date)
        //
        Map<BigDecimal, BioModelInfo> latestBioModelForBranch = new HashMap<>();
        for (BioModelInfo bioModelInfo : publishedBioModelInfos){
            BigDecimal branchID = bioModelInfo.getVersion().getBranchID();
            BioModelInfo bmi = latestBioModelForBranch.get(branchID);
            if (bmi == null || bmi.getVersion().getVersionKey().compareTo(bioModelInfo.getVersion().getVersionKey()) < 0){
                latestBioModelForBranch.put(branchID, bioModelInfo);
            }
        }
        return new ArrayList<>(latestBioModelForBranch.values());
    }

    public String getPublicBiomodelXML(KeyValue bioModelKey, boolean bRegenerateXML) throws SQLException, DataAccessException {
        List<BioModelInfo> publicBioModelInfos = queryPublicBioModels();
        Optional<BioModelInfo> bioModelInfo = publicBioModelInfos.stream().filter(bmi -> bmi.getVersion().getVersionKey().equals(bioModelKey)).findFirst();
        if (!bioModelInfo.isPresent()){
            throw new RuntimeException("biomodel key "+bioModelKey+" not found as a public bioodel");
        }
        return getBioModelXML(bioModelKey, bRegenerateXML, bioModelInfo.get().getVersion().getOwner());
    }

    public String getBioModelXML(KeyValue bioModelKey, boolean bRegenerateXML, User owner) throws DataAccessException {
        ServerDocumentManager serverDocumentManager = getDatabaseServer().getServerDocumentManager();
        User user = owner; // impersonate owner for this model
        String biomodelXML = serverDocumentManager.getBioModelXML(new QueryHashtable(), user, bioModelKey, bRegenerateXML);
        return biomodelXML;
    }

    public String getBioModelVCML(BioModelInfo bioModelInfo, boolean bWithXMLCache) throws SQLException, DataAccessException, XmlParseException {
        DatabaseServerImpl databaseServerImpl = getDatabaseServer();
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

    public String getBasicStatistics() throws SQLException, DataAccessException {
        AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
        return adminDbTopLevel.getBasicStatistics();
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
