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
    public static User.SpecialUser specialUser = new User.SpecialUser("Tim", new KeyValue("2"), new User.SPECIAL_CLAIM[]{User.SpecialUser.SPECIAL_CLAIM.powerUsers});

    private final HashMap<String, User> users = new HashMap<>(){
        {put(specialAdmin.getName(), specialAdmin); put(DispatcherTestUtils.alice.getName(), DispatcherTestUtils.alice);
        put(specialUser.getName(), specialUser);}
    };

    private final HashMap<String, Simulation> simulations = new HashMap<>();

    private final Set<KeyValue> unreferencedSimKeys = new HashSet<>();

    // Return a latest simulation that differs in one of these ways
    public enum BadLatestSimulation{
        HIGHER_TASK_ID,
        RETURN_NULL,
        IS_DONE,
        DO_NOTHING
    }

    public BadLatestSimulation badLatestSimulation = BadLatestSimulation.DO_NOTHING;


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
        switch (badLatestSimulation){
            case RETURN_NULL -> {
                return null;
            } case HIGHER_TASK_ID -> {
                SimulationJobStatus simulationJobStatus = new SimulationJobStatus(latestSim.getServerID(), latestSim.getVCSimulationIdentifier(), latestSim.getJobIndex(),
                        latestSim.getSubmitDate(), latestSim.getSchedulerStatus(), latestSim.getTaskID() + 1, latestSim.getSimulationMessage(), latestSim.getSimulationQueueEntryStatus(), latestSim.getSimulationExecutionStatus());
                return simulationJobStatus;
            } case IS_DONE -> {
                return new SimulationJobStatus(latestSim.getServerID(), latestSim.getVCSimulationIdentifier(), latestSim.getJobIndex(), latestSim.getSubmitDate(), SimulationJobStatus.SchedulerStatus.COMPLETED,
                        latestSim.getTaskID(), latestSim.getSimulationMessage(), latestSim.getSimulationQueueEntryStatus(), null);
            }default -> {
                return latestSim;
            }
        }

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
        throw new ObjectNotFoundException("");
    }

    @Override
    public Map<KeyValue, SimulationRequirements> getSimulationRequirements(Collection<KeyValue> simKeys) throws SQLException {
        HashMap<KeyValue, SimulationRequirements> map = new HashMap<>();
        for (KeyValue simKey : simKeys){
            map.put(simKey, new SimulationRequirements(simKey, 3));
        }
        return map;
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
        return simulations.get(simKey.toString() + user.getName());
    }

    @Override
    public FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs(Simulation sim) throws DataAccessException {
        return new FieldDataIdentifierSpec[0];
    }

    @Override
    public Set<KeyValue> getUnreferencedSimulations() throws SQLException {
        return unreferencedSimKeys;
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
        TreeMap<User.SPECIAL_CLAIM, TreeMap<User, String>> map = new TreeMap<>();
        TreeMap<User, String> subMap = new TreeMap<>(new User.UserNameComparator());
        subMap.put(specialAdmin, "f");
        map.put(User.SPECIAL_CLAIM.admins, subMap);
        return map;
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
        SimulationJobStatus status = dbTable.get(simulationKey.toString()).get(0);
        SimulationStatus simulationStatus = new SimulationStatus(new SimulationJobStatus[]{status});
        return simulationStatus;
    }

    @Override
    public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException {
        throw new ObjectNotFoundException("");
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
        badLatestSimulation = BadLatestSimulation.DO_NOTHING;
        unreferencedSimKeys.clear();
        simulations.clear();
    }

    public void insertSimulation(User user, Simulation sim){
        simulations.put(sim.getKey().toString() + user.getName(), sim);
    }

    public void insertUnreferencedSimKey(KeyValue k){
        unreferencedSimKeys.add(k);
    }

}
