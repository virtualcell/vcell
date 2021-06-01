package cbit.vcell.message.server.dispatcher;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.StateInfo;
import cbit.vcell.server.UpdateSynchronizationException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;

public interface SimulationDatabase {

	public SimulationJobStatus getLatestSimulationJobStatus(KeyValue simKey, int jobIndex) throws DataAccessException, SQLException;

	public void insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws DataAccessException, SQLException;

	/**
	 * 
	 * @param vcellServerID - if null, return all jobs
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public SimulationJobStatus[] getActiveJobs(VCellServerID vcellServerID) throws DataAccessException, SQLException;
	
	public SimulationJobStatus[] queryJobs(SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException;

	public Map<KeyValue,SimulationRequirements> getSimulationRequirements(Collection<KeyValue> simKeys) throws SQLException;
	
	public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus) throws DataAccessException, UpdateSynchronizationException, SQLException;

	public void updateSimulationJobStatus(SimulationJobStatus newSimulationJobStatus, StateInfo runningStateInfo) throws DataAccessException, UpdateSynchronizationException, SQLException;

	public Simulation getSimulation(User user, KeyValue simKey) throws DataAccessException;

	public FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs(Simulation sim) throws DataAccessException;
	
	public Set<KeyValue> getUnreferencedSimulations() throws SQLException;

	public User getUser(String username) throws DataAccessException, SQLException;

	public TreeMap<User.SPECIALS,TreeMap<User,String>> getSpecialUsers() throws DataAccessException, SQLException;

	public SimulationInfo getSimulationInfo(User user, KeyValue simKey) throws ObjectNotFoundException, DataAccessException;

	public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws ObjectNotFoundException, DataAccessException;

	public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws ObjectNotFoundException, DataAccessException;

	public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException;

}
