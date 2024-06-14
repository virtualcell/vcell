package org.vcell.restq.db;

import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.message.*;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ServerDocumentManager;
import cbit.vcell.modeldb.SimContextRep;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.models.simulation.OverrideRepresentationGenerator;
import org.vcell.restq.rpc.RpcSimService;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

// Make into seperate class in DB module
@ApplicationScoped
public class SimulationRestDB {
    private final static Logger lg = LogManager.getLogger(SimulationRestDB.class);


    private final DatabaseServerImpl databaseServerImpl;
    private final VCMessagingService vcMessagingService = new VCMessagingServiceActiveMQ();

    public SimulationRestDB(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
        databaseServerImpl = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        VCMessagingDelegate delegate = new VCMessagingDelegate() {

            @Override
            public void onTraceEvent(String string) {
                if (lg.isTraceEnabled()) lg.trace("onTraceEvent(): "+string);
            }

            @Override
            public void onRpcRequestSent(VCRpcRequest vcRpcRequest, UserLoginInfo userLoginInfo, VCMessage vcRpcRequestMessage) {
                if (lg.isTraceEnabled()) lg.trace("onRpcRequestSent(): "+vcRpcRequest.getMethodName());
            }

            @Override
            public void onRpcRequestProcessed(VCRpcRequest vcRpcRequest, VCMessage rpcVCMessage) {
                if (lg.isTraceEnabled()) lg.trace("onRpcRequestProcessed(): "+vcRpcRequest.getMethodName());
            }

            @Override
            public void onMessageSent(VCMessage message, VCDestination desintation) {
                if (lg.isTraceEnabled()) lg.trace("onMessageSent(): "+message);
            }

            @Override
            public void onMessageReceived(VCMessage vcMessage, VCDestination vcDestination) {
                if (lg.isTraceEnabled()) lg.trace("onMessageReceived(): "+vcMessage);
            }

            @Override
            public void onException(Exception e) {
                lg.error(e.getMessage(), e);
            }
        };
        String jmshost_int = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
        int jmsport_int = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
        vcMessagingService.setConfiguration(delegate, jmshost_int, jmsport_int);
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

    public SimulationRep startSimulation(String simId, User vcellUser) throws PermissionException, ObjectNotFoundException, DataAccessException, SQLException{
        KeyValue simKey = new KeyValue(simId);
        SimulationRep simRep = getSimulationRep(simKey);
        if (simRep == null){
            throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
        }
        User owner = simRep.getOwner();
        if (!owner.compareEqual(vcellUser)){
            throw new PermissionException("not authorized to start simulation");
        }
        VCMessageSession rpcSession = vcMessagingService.createProducerSession();
        try {
            UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
            try {
                userLoginInfo.setUser(vcellUser);
            } catch (Exception e) {
                lg.error(e.getMessage(), e);
                throw new DataAccessException(e.getMessage());
            }
            RpcSimService rpcSimServerProxy = new RpcSimService(userLoginInfo, rpcSession);
            VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
            rpcSimServerProxy.startSimulation(vcellUser, vcSimID, simRep.getScanCount());
            return simRep;
        }finally{
            rpcSession.close();
        }
    }

    public SimulationSaveResponse saveSimulation(User vcellUser, String simId, String biomodelId,
                               List<OverrideRepresentationGenerator.OverrideRepresentation> overrideRepresentations) throws DataAccessException, SQLException, XmlParseException, PropertyVetoException, ExpressionException, MappingException {
        KeyValue simKey = new KeyValue(simId);
        SimulationRep simRep = getSimulationRep(simKey);
        if (simRep == null){
            throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
        }
        boolean myModel = simRep.getOwner().compareEqual(vcellUser);
        // get the bioModel
        KeyValue biomodelKey = new KeyValue(biomodelId);
        BigString bioModelXML = this.databaseServerImpl.getBioModelXML(vcellUser, biomodelKey);
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
        // copy the simulation as new
        Simulation origSimulation = null;
        for (Simulation sim : bioModel.getSimulations()){
            if (sim.getKey().equals(simKey)){
                origSimulation = sim;
            }
        }
        if (origSimulation==null){
            throw new RuntimeException("cannot find original Simulation");
        }

        SimulationContext simContext = bioModel.getSimulationContext(origSimulation);
        Simulation newUnsavedSimulation = simContext.copySimulation(origSimulation);

        // make appropriate changes
        // MATH OVERRIDES
        MathOverrides mathOverrides = new MathOverrides(newUnsavedSimulation);
        for (OverrideRepresentationGenerator.OverrideRepresentation overrideRep : overrideRepresentations){
            OverrideRepresentationGenerator.applyMathOverrides(mathOverrides, overrideRep);
        }
        newUnsavedSimulation.setMathOverrides(mathOverrides);

        // save bioModel
        String editedBioModelXML = XmlHelper.bioModelToXML(bioModel);
        ServerDocumentManager serverDocumentManager = new ServerDocumentManager(this.databaseServerImpl);
        String modelName = bioModel.getName();
        if (!myModel){
            modelName = modelName+"_"+Math.abs(new Random().nextInt());
        }
        String newBioModelXML = serverDocumentManager.saveBioModel(new QueryHashtable(), vcellUser, editedBioModelXML, modelName, null);
        BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(newBioModelXML));
        Simulation savedSimulation = null;
        for (Simulation sim : savedBioModel.getSimulations()){
            if (sim.getName().equals(newUnsavedSimulation.getName())){
                savedSimulation = sim;
            }
        }
        if (savedSimulation==null){
            throw new RuntimeException("cannot find new Simulation");
        }
        return new SimulationSaveResponse(savedBioModel, savedSimulation);
    }

    public SimulationRep stopSimulation(String simId, User vcellUser) throws PermissionException, ObjectNotFoundException, DataAccessException, SQLException{
        KeyValue simKey = new KeyValue(simId);
        SimulationRep simRep = getSimulationRep(simKey);
        if (simRep == null){
            throw new ObjectNotFoundException("Simulation with key "+simKey+" not found");
        }
        User owner = simRep.getOwner();
        if (!owner.compareEqual(vcellUser)){
            throw new PermissionException("not authorized to stop simulation");
        }
        VCMessageSession rpcSession = vcMessagingService.createProducerSession();
        try {
            UserLoginInfo userLoginInfo = new UserLoginInfo(vcellUser.getName(),null);
            try {
                userLoginInfo.setUser(vcellUser);
            } catch (Exception e) {
                lg.error(e.getMessage(), e);
                throw new DataAccessException(e.getMessage());
            }
            RpcSimService rpcSimServerProxy = new RpcSimService(userLoginInfo, rpcSession);
            VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
            rpcSimServerProxy.stopSimulation(vcellUser, vcSimID);
            return simRep;
        }finally{
            rpcSession.close();
        }
    }

    public static class SimulationSaveResponse {
        public final BioModel newBioModel;
        public final Simulation newSimulation;

        public SimulationSaveResponse(BioModel newBioModel, Simulation newSimulation){
            this.newBioModel = newBioModel;
            this.newSimulation = newSimulation;
        }
    }
}
