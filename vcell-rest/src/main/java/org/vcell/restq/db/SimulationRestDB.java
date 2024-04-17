package org.vcell.restq.db;

import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.SimContextRep;
import cbit.vcell.modeldb.SimulationRep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

// Make into seperate class in DB module
public class SimulationRestDB {
    private final static Logger lg = LogManager.getLogger(SimulationRestDB.class);


    private final DatabaseServerImpl databaseServerImpl;

    public SimulationRestDB(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
        databaseServerImpl = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
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
}
