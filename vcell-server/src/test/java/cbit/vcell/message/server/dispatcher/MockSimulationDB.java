package cbit.vcell.message.server.dispatcher;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.server.*;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

public class MockSimulationDB implements SimulationDatabase{

    private HashMap<String, ArrayList<SimulationJobStatus>> dbTable = new HashMap<>();

    public static User.SpecialUser specialAdmin = new User.SpecialUser("Tom", new KeyValue("999"), new User.SPECIAL_CLAIM[User.SPECIAL_CLAIM.admins.ordinal()]);

    private final HashMap<String, User> users = new HashMap<>(){
        {put(specialAdmin.getName(), specialAdmin); put(DispatcherTestUtils.alice.getName(), DispatcherTestUtils.alice);}
    };

    @Override
    public SimulationJobStatus getLatestSimulationJobStatus(KeyValue simKey, int jobIndex) throws DataAccessException, SQLException {
        ArrayList<SimulationJobStatus> simList = dbTable.get(simKey.toString());
        if (simList == null){
            return null;
        }
        SimulationJobStatus latestSim = null;
        for (SimulationJobStatus jobStatus : simList){
            boolean equalJobIndex = jobStatus.getJobIndex() == jobIndex;
            boolean isLatestSimNull = latestSim == null;
            if ((equalJobIndex && isLatestSimNull) || (!isLatestSimNull && equalJobIndex && latestSim.getSubmitDate().after(jobStatus.getSubmitDate()))){
                latestSim = jobStatus;
            }
        }
        return latestSim;
    }

    @Override
    public void insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws DataAccessException, SQLException {
        String simKey = simulationJobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
        if (dbTable.containsKey(simKey)){
            dbTable.get(simKey).add(simulationJobStatus);
        } else {
            dbTable.put(simKey, new ArrayList<>(){{add(simulationJobStatus);}});
        }
    }

    @Override
    public SimulationJobStatus[] getActiveJobs(VCellServerID vcellServerID) throws DataAccessException, SQLException {
        ArrayList<SimulationJobStatus> allActiveJobs = new ArrayList<>();
        for (ArrayList<SimulationJobStatus> jobStatuses : dbTable.values()){
            for (SimulationJobStatus jobStatus: jobStatuses){
                if (jobStatus.getSchedulerStatus().isActive()){
                    allActiveJobs.add(jobStatus);
                }
            }
        }
        return allActiveJobs.toArray(new SimulationJobStatus[]{});
    }

    @Override
    public SimulationJobStatus[] queryJobs(SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException {
        return new SimulationJobStatus[0];
    }

    @Override
    public Map<KeyValue, SimulationRequirements> getSimulationRequirements(Collection<KeyValue> simKeys) throws SQLException {
        return Map.of();
    }

    @Override
    public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus) throws DataAccessException, UpdateSynchronizationException, SQLException {
        updateSimulationJobStatus(newSimulationJobStatus, null);
    }

    @Override
    public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus, StateInfo runningStateInfo) throws DataAccessException, UpdateSynchronizationException, SQLException {
        String simKey = newSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
        ArrayList<SimulationJobStatus> jobStatuses = dbTable.get(simKey);
        for (int i = 0; i < jobStatuses.size(); i++){
            SimulationJobStatus jobStatus = jobStatuses.get(i);
            boolean sameJob = jobStatus.getJobIndex() == newSimulationJobStatus.getJobIndex();
            if (sameJob){
                jobStatuses.set(i,newSimulationJobStatus);
                break;
            }
        }
    }

    @Override
    public KeyValue[] getSimulationKeysFromBiomodel(KeyValue biomodelKey) throws SQLException, DataAccessException {
        throw new SQLException();
    }

    @Override
    public Simulation getSimulation(User user, KeyValue simKey) throws DataAccessException {
        throw new DataAccessException();
    }

    @Override
    public FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs(Simulation sim) throws DataAccessException {
        return null;
    }

    @Override
    public Set<KeyValue> getUnreferencedSimulations() throws SQLException {
        return Set.of();
    }

    @Override
    public User.SpecialUser getUser(String username) throws DataAccessException, SQLException {
        User user = users.get(username);
        if (user instanceof User.SpecialUser){
            return (User.SpecialUser) user;
        }
        User.SpecialUser specialUser = new User.SpecialUser(user.getName(), user.getID(), new User.SPECIAL_CLAIM[]{});
        return specialUser;
    }

    @Override
    public TreeMap<User.SPECIAL_CLAIM, TreeMap<User, String>> getSpecialUsers() throws DataAccessException, SQLException {
        return null;
    }

    @Override
    public SimulationInfo getSimulationInfo(User user, KeyValue simKey) throws ObjectNotFoundException, DataAccessException {
        return mockSimulationInfo(user, simKey);
    }

    @Override
    public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws ObjectNotFoundException, DataAccessException {
        return new SimulationStatus[0];
    }

    @Override
    public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws ObjectNotFoundException, DataAccessException {
//        dbTable.get(simulationKey.toString()).get(0);
        return null;
    }

    @Override
    public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException {
        return new SimpleJobStatus[0];
    }


    private SimulationInfo mockSimulationInfo(User user, KeyValue simKey){
        KeyValue versionKey = new KeyValue("22");
        KeyValue versionBranchPoint = new KeyValue("23");
        VersionFlag versionFlag = VersionFlag.fromInt(0);
        KeyValue parentSimulationRef = new KeyValue("24");
        SimulationVersion simulationVersion = new SimulationVersion(versionKey, "Mock Sim Info", user, null,
               versionBranchPoint, new BigDecimal(22), Date.from(Instant.now()), versionFlag, "Version annot",
                parentSimulationRef);
        SimulationInfo simulationInfo = new SimulationInfo(simKey, simulationVersion, VCellSoftwareVersion.fromString("50"));
        return simulationInfo;
    }

    public void resetDataBase(){
        dbTable = new HashMap<>();
    }

}
