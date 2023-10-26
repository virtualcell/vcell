package org.vcell.admin.cli;

import cbit.sql.CompareDatabaseSchema;
import cbit.sql.QueryHashtable;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.*;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.xml.XmlParseException;
import org.vcell.admin.cli.sim.JobAdmin;
import org.vcell.admin.cli.sim.ResultSetCrawler;
import org.vcell.admin.cli.sim.SimDataVerifier;
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

    public ResultSetCrawler getResultSetCrawler() throws DataAccessException, SQLException {
        AdminDBTopLevel adminDBTopLevel = new AdminDBTopLevel(conFactory);
        DatabaseServerImpl dbServerImpl = new DatabaseServerImpl(conFactory, conFactory.getKeyFactory());
        return new ResultSetCrawler(adminDBTopLevel, dbServerImpl);
    }

    public SimDataVerifier getSimDataVerifier() throws DataAccessException, SQLException {
        AdminDBTopLevel adminDBTopLevel = new AdminDBTopLevel(conFactory);
        DatabaseServerImpl dbServerImpl = new DatabaseServerImpl(conFactory, conFactory.getKeyFactory());
        SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(adminDBTopLevel, dbServerImpl,false);
        return new SimDataVerifier(adminDBTopLevel, dbServerImpl, simulationDatabase);
    }

    public JobAdmin getJobAdmin() throws DataAccessException, SQLException {
        AdminDBTopLevel adminDBTopLevel = new AdminDBTopLevel(conFactory);
        DatabaseServerImpl dbServerImpl = new DatabaseServerImpl(conFactory, conFactory.getKeyFactory());
        SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(adminDBTopLevel, dbServerImpl,false);
        return new JobAdmin(adminDBTopLevel, dbServerImpl, simulationDatabase);
    }

    public GroupAccess addGroupAccess(BioModelInfo bioModelInfo, User userToAdd) throws DataAccessException {
        boolean bHidden = false;
        VersionInfo newVersionInfo = getDatabaseServer().groupAddUser(
                bioModelInfo.getVersion().getOwner(),
                VersionableType.BioModelMetaData, bioModelInfo.getVersion().getVersionKey(),
                userToAdd.getName(), bHidden);
        return newVersionInfo.getVersion().getGroupAccess();
    }

    public GroupAccess removeGroupAccess(BioModelInfo bioModelInfo, User userToAdd) throws DataAccessException {
        boolean bHidden = false;
        VersionInfo newVersionInfo = getDatabaseServer().groupRemoveUser(
                bioModelInfo.getVersion().getOwner(),
                VersionableType.BioModelMetaData, bioModelInfo.getVersion().getVersionKey(),
                userToAdd.getName(), bHidden);
        return newVersionInfo.getVersion().getGroupAccess();
    }

    public GroupAccess addGroupAccess(MathModelInfo mathModelInfo, User userToAdd) throws DataAccessException {
        boolean bHidden = false;
        VersionInfo newVersionInfo = getDatabaseServer().groupAddUser(
                mathModelInfo.getVersion().getOwner(),
                VersionableType.MathModelMetaData, mathModelInfo.getVersion().getVersionKey(),
                userToAdd.getName(), bHidden);
        return newVersionInfo.getVersion().getGroupAccess();
    }

    public GroupAccess removeGroupAccess(MathModelInfo mathModelInfo, User userToAdd) throws DataAccessException {
        boolean bHidden = false;
        VersionInfo newVersionInfo = getDatabaseServer().groupRemoveUser(
                mathModelInfo.getVersion().getOwner(),
                VersionableType.MathModelMetaData, mathModelInfo.getVersion().getVersionKey(),
                userToAdd.getName(), bHidden);
        return newVersionInfo.getVersion().getGroupAccess();
    }

    public BioModelInfo queryBiomodelInfo(User owner, KeyValue biomodelId) throws DataAccessException {
        return getDatabaseServer().getBioModelInfo(owner, biomodelId);
    }

    public MathModelInfo queryMathmodelInfo(User owner, KeyValue mathmodelId) throws DataAccessException {
        return getDatabaseServer().getMathModelInfo(owner, mathmodelId);
    }

    public List<BioModelInfo> queryBiomodelsByOwner(User owner) throws DataAccessException {
        return Arrays.asList(getDatabaseServer().getBioModelInfos(owner, false));
    }

    public List<MathModelInfo> queryMathmodelsByOwner(User owner) throws DataAccessException {
        return Arrays.asList(getDatabaseServer().getMathModelInfos(owner, false));
    }
}
