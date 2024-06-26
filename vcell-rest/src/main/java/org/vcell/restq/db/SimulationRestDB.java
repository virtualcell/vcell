package org.vcell.restq.db;

import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.*;
import cbit.vcell.server.SimulationStatusPersistent;
import jakarta.inject.Inject;
import org.vcell.restq.Simulations.SimulationDispatcherEngine;
import cbit.vcell.solver.VCSimulationIdentifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.Simulations.SimulationStatusPersistentRecord;
import org.vcell.restq.Simulations.StatusMessage;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

// Make into seperate class in DB module
@ApplicationScoped
public class SimulationRestDB {
    private final static Logger lg = LogManager.getLogger(SimulationRestDB.class);


    private final DatabaseServerImpl databaseServerImpl;
    private final SimulationDatabaseDirect simulationDatabaseDirect;
    private final SimulationDispatcherEngine simulationDispatcherEngine;
    private final BioModelRestDB bioModelRestDB;

    public SimulationRestDB(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException, SQLException {
        databaseServerImpl = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        simulationDatabaseDirect = new SimulationDatabaseDirect(new AdminDBTopLevel(agroalConnectionFactory), databaseServerImpl, true);
        simulationDispatcherEngine = new SimulationDispatcherEngine();
        bioModelRestDB = new BioModelRestDB(this, databaseServerImpl);
    }

    ConcurrentHashMap<KeyValue, SimulationRep> simMap = new ConcurrentHashMap<KeyValue, SimulationRep>();
    ConcurrentHashMap<KeyValue, SimContextRep> scMap = new ConcurrentHashMap<KeyValue, SimContextRep>();

    public SimulationRep getSimulationRep(KeyValue key) throws DataAccessException, SQLException {
        SimulationRep simulationRep = simMap.get(key);
        if (simulationRep!=null){
            return simulationRep;
        }else{
            lg.info("getting simulation rep for simKey = "+key);
            simulationRep = databaseServerImpl.getSimulationRep(key);
            if (simulationRep!=null){
                lg.info("found simulation key = " + key + " number of cached simulations is " + simMap.size());
                simMap.put(key, simulationRep);
            }else{
                lg.info("couldn't find simulation key = " + key);
            }
            return simulationRep;
        }
    }

    public SimContextRep getSimContextRep(KeyValue key) throws DataAccessException, SQLException{
        SimContextRep simContextRep = scMap.get(key);
        if (simContextRep!=null){
            return simContextRep;
        }else{
            lg.info("getting simulation context rep for scKey = "+key);
            simContextRep = databaseServerImpl.getSimContextRep(key);
            if (simContextRep!=null){
                lg.info("found simulation context key = " + key + " number of cached simContexts is " + simMap.size());
                scMap.put(key, simContextRep);
            }else{
                lg.info("couldn't find simulation key = " + key);
            }
            return simContextRep;
        }
    }

    private SimulationRep getAndCheckSimRep(String simId, User vcellUser) throws DataAccessException, SQLException {
        KeyValue simKey = new KeyValue(simId);
        SimulationRep simRep = getSimulationRep(simKey);
        if (simRep == null){
            throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
        }
        User owner = simRep.getOwner();
        if (!owner.compareEqual(vcellUser)){
            throw new PermissionException("Not authorized to start/stop simulation");
        }
        return simRep;
    }

    public ArrayList<StatusMessage> startSimulation(String simId, User vcellUser) throws PermissionException, ObjectNotFoundException, DataAccessException, SQLException{
        KeyValue simKey = new KeyValue(simId);
        VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKey, vcellUser);
        SimulationRep simRep = getAndCheckSimRep(simId, vcellUser);
        try {
            return simulationDispatcherEngine.onStartRequest(vcSimulationIdentifier, vcellUser, simRep.getScanCount(),
                    simulationDatabaseDirect, null, null);
        } catch (VCMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<StatusMessage> stopSimulation(String simId, User vcellUser) throws PermissionException, DataAccessException, SQLException, VCMessagingException {
        KeyValue simKey = new KeyValue(simId);
        getAndCheckSimRep(simId, vcellUser);
        VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(simKey, vcellUser);
        return simulationDispatcherEngine.onStopRequest(vcSimulationIdentifier, vcellUser, simulationDatabaseDirect, null);
    }

    public SimulationStatusPersistentRecord getBioModelSimulationStatus(String simID, String bioModelID, User vcellUser) throws DataAccessException, SQLException {
        BioModelRep bioModelRep = bioModelRestDB.getBioModelRep(new KeyValue(bioModelID), vcellUser);
        User[] users = bioModelRep.getGroupUsers();
        User owner = bioModelRep.getOwner();
        if (!Arrays.stream(users).toList().contains(vcellUser) && !owner.compareEqual(vcellUser)){
            throw new PermissionException("Not authorized to access BioModel");
        }
        return SimulationStatusPersistentRecord.fromSimulationStatusPersistent(databaseServerImpl.getSimulationStatus(new KeyValue(simID)));
    }

}
