package cbit.vcell.message.server.dispatcher;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.server.*;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import java.sql.SQLException;
import java.util.*;

public class MockSimulationDB implements SimulationDatabase{

    private final HashMap<String, ArrayList<SimulationJobStatus>> dbTable = new HashMap<>();

    @Override
    public SimulationJobStatus getLatestSimulationJobStatus(KeyValue simKey, int jobIndex) throws DataAccessException, SQLException {
        ArrayList<SimulationJobStatus> simList = dbTable.get(simKey.toString());
        if (simList == null){
            return null;
        }
        SimulationJobStatus latestSim = simList.get(0);
        for (SimulationJobStatus jobStatus : simList){
            if (jobStatus.getJobIndex() == jobIndex && latestSim.getSubmitDate().after(jobStatus.getSubmitDate())){
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
        throw new SQLException();
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
    public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus, StateInfo runningStateInfo) throws DataAccessException, UpdateSynchronizationException, SQLException {

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
        throw new DataAccessException();
    }

    @Override
    public Set<KeyValue> getUnreferencedSimulations() throws SQLException {
        return Set.of();
    }

    @Override
    public User.SpecialUser getUser(String username) throws DataAccessException, SQLException {
        return null;
    }

    @Override
    public TreeMap<User.SPECIAL_CLAIM, TreeMap<User, String>> getSpecialUsers() throws DataAccessException, SQLException {
        return null;
    }

    @Override
    public SimulationInfo getSimulationInfo(User user, KeyValue simKey) throws ObjectNotFoundException, DataAccessException {
        return null;
    }

    @Override
    public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws ObjectNotFoundException, DataAccessException {
        return new SimulationStatus[0];
    }

    @Override
    public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws ObjectNotFoundException, DataAccessException {
        return null;
    }

    @Override
    public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException {
        return new SimpleJobStatus[0];
    }
}
