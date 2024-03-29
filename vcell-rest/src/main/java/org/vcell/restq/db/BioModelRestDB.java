package org.vcell.restq.db;

import cbit.vcell.modeldb.*;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;


import java.sql.SQLException;
import java.util.ArrayList;

public class BioModelRestDB {

    private final DatabaseServerImpl databaseServerImpl;
    private final SimulationRestDB simulationRestDB;
    private final AdminDBTopLevel adminDBTopLevel;


    public BioModelRestDB(OracleAgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
        databaseServerImpl = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        simulationRestDB = new SimulationRestDB(agroalConnectionFactory);
        adminDBTopLevel = databaseServerImpl.getAdminDBTopLevel();
    }


    public UserIdentity getUserFromAuth0ID(String auth0ID) {
        try {
            return adminDBTopLevel.getUserIdentityFromAuth0(auth0ID, true);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BioModelRep getBioModelRep(KeyValue bmKey, User vcellUser) throws SQLException, DataAccessException {
        if (vcellUser==null){
            vcellUser = AuthUtils.DUMMY_USER;
        }
        ArrayList<String> conditions = new ArrayList<String>();
        if (bmKey != null){
            conditions.add("(" + BioModelTable.table.id.getQualifiedColName() + " = " + bmKey.toString() + ")");
        }else{
            throw new RuntimeException("bioModelID not specified");
        }

        StringBuffer conditionsBuffer = new StringBuffer();
        for (String condition : conditions) {
            if (conditionsBuffer.length() > 0) {
                conditionsBuffer.append(" AND ");
            }
            conditionsBuffer.append(condition);
        }
        int startRow = 1;
        int maxRows = 1;
        BioModelRep[] bioModelReps = databaseServerImpl.getBioModelReps(vcellUser, conditionsBuffer.toString(), null, startRow, maxRows);
        for (BioModelRep bioModelRep : bioModelReps) {
            KeyValue[] simContextKeys = bioModelRep.getSimContextKeyList();
            for (KeyValue scKey : simContextKeys) {
                SimContextRep scRep = simulationRestDB.getSimContextRep(scKey);
                if (scRep != null){
                    bioModelRep.addSimContextRep(scRep);
                }
            }
            KeyValue[] simulationKeys = bioModelRep.getSimKeyList();
            for (KeyValue simKey : simulationKeys) {
                SimulationRep simulationRep = simulationRestDB.getSimulationRep(simKey);
                if (simulationRep != null){
                    bioModelRep.addSimulationRep(simulationRep);
                }
            }

        }
        if (bioModelReps==null || bioModelReps.length!=1){
            throw new ObjectNotFoundException("failed to get biomodel");
        }
        return bioModelReps[0];
    }
}
