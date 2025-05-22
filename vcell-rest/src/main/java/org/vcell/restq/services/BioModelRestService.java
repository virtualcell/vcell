package org.vcell.restq.services;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.modeldb.*;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.util.ArrayList;

@ApplicationScoped
public class BioModelRestService {
    private final DatabaseServerImpl databaseServerImpl;
    private final SimulationRestService simulationRestService;

    @Inject
    public BioModelRestService(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException, SQLException {
        databaseServerImpl = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        simulationRestService = new SimulationRestService(agroalConnectionFactory);
    }

    public BioModelRestService(SimulationRestService simulationRestService, DatabaseServerImpl databaseServer) {
        databaseServerImpl = databaseServer;
        this.simulationRestService = simulationRestService;
    }

    public BioModelRep getBioModelRep(KeyValue bmKey, User vcellUser) throws DataAccessException {
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
        BioModelRep[] bioModelReps;
        try{
            bioModelReps = databaseServerImpl.getBioModelReps(vcellUser, conditionsBuffer.toString(), null, startRow, maxRows);
            for (BioModelRep bioModelRep : bioModelReps) {
                KeyValue[] simContextKeys = bioModelRep.getSimContextKeyList();
                for (KeyValue scKey : simContextKeys) {
                    SimContextRep scRep = simulationRestService.getSimContextRep(scKey);
                    if (scRep != null){
                        bioModelRep.addSimContextRep(scRep);
                    }
                }
                KeyValue[] simulationKeys = bioModelRep.getSimKeyList();
                for (KeyValue simKey : simulationKeys) {
                    SimulationRep simulationRep = simulationRestService.getSimulationRep(simKey);
                    if (simulationRep != null){
                        bioModelRep.addSimulationRep(simulationRep);
                    }
                }

            }
        } catch (SQLException e){
            throw new DataAccessException("Unable to retrieve BioModel representations.", e);
        }

        if (bioModelReps==null || bioModelReps.length!=1){
            throw new ObjectNotFoundException("failed to get biomodel");
        }
        return bioModelReps[0];
    }

    public String getBioModel(User user, KeyValue id) throws DataAccessException {
        return databaseServerImpl.getBioModelXML(user, id).toString();
    }

    /**
     * A new Bio-Model is created if the "name" parameter is not null, and not equal to any other name present.
     */
    public BioModel save(User user, String bioModelXML, String newName, String[] independentSims) throws DataAccessException, XmlParseException {
        BigString savedModel = databaseServerImpl.saveBioModelAs(user, new BigString(bioModelXML), newName, independentSims);
        return XmlHelper.XMLToBioModel(new XMLSource(savedModel.toString()));
    }

    public void deleteBioModel(User user, KeyValue bioModelKey) throws DataAccessException {
        if (user == null){
            throw new PermissionException("vcell user not specified");
        }
        databaseServerImpl.deleteBioModel(user, bioModelKey);
    }
}
