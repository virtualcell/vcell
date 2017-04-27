package cbit.vcell.message.server.dispatcher;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.messaging.db.StateInfo;
import cbit.vcell.modeldb.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.UpdateSynchronizationException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;

public interface SimulationDatabase {

	public SimulationJobStatus getLatestSimulationJobStatus(KeyValue simKey, int jobIndex) throws DataAccessException, SQLException;

	public void insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws DataAccessException, SQLException;

	public SimulationJobStatus[] getActiveJobs() throws DataAccessException, SQLException;
	
	public Map<KeyValue,SimulationRequirements> getSimulationRequirements(Collection<KeyValue> simKeys) throws SQLException;
	
	public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus) throws DataAccessException, UpdateSynchronizationException, SQLException;

	public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus, StateInfo runningStateInfo) throws DataAccessException, UpdateSynchronizationException, SQLException;

	public Simulation getSimulation(User user, KeyValue simKey) throws DataAccessException;

	public FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs(Simulation sim) throws DataAccessException;
	
	public Set<KeyValue> getUnreferencedSimulations() throws SQLException;

	public User getUser(String username) throws DataAccessException, SQLException;

	public SimulationInfo getSimulationInfo(User user, KeyValue simKey) throws ObjectNotFoundException, DataAccessException;

	public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws ObjectNotFoundException, DataAccessException;

	public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws ObjectNotFoundException, DataAccessException;

	public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException;

}
