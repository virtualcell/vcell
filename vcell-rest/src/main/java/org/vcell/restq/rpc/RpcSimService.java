package org.vcell.restq.rpc;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

public class RpcSimService extends AbstractRpcServerProxy {
    private final static Logger lg = LogManager.getLogger(RpcSimService.class);

    public RpcSimService(UserLoginInfo userLoginInfo, VCMessageSession vcMessageSession) {
        super(userLoginInfo, vcMessageSession, VCellQueue.SimReqQueue);
    }

    private Object rpc(String methodName, Object[] args) throws DataAccessException {
        try {
            return rpc(VCRpcRequest.RpcServiceType.DISPATCH, methodName, args, true);
        } catch (DataAccessException ex) {
            lg.error(ex.getMessage(),ex);
            throw ex;
        } catch (Exception e){
            lg.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }


    public SimulationStatus startSimulation(User user, VCSimulationIdentifier vcSimID, int numSimulationScanJobs) {
        try {
            return (SimulationStatus)rpc("startSimulation",new Object[]{user, vcSimID, new Integer(numSimulationScanJobs)});
        }catch (DataAccessException e){
            lg.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public SimulationStatus stopSimulation(User user, VCSimulationIdentifier vcSimID) {
        try {
            return (SimulationStatus)rpc("stopSimulation",new Object[]{user, vcSimID});
        } catch (DataAccessException e) {
            lg.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }


    public SimulationStatus[] getSimulationStatus(User user, KeyValue[] simKeys) throws DataAccessException {
        try {
            return (SimulationStatus[])rpc("getSimulationStatus",new Object[]{user, simKeys});
        } catch (DataAccessException e) {
            lg.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }


    public SimulationStatus getSimulationStatus(User user, KeyValue simulationKey) throws DataAccessException {
        try {
            return (SimulationStatus)rpc("getSimulationStatus",new Object[]{user, simulationKey});
        } catch (DataAccessException e) {
            lg.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simJobStatusQuerySpec) throws DataAccessException {
        try {
            return (SimpleJobStatus[])rpc("getSimpleJobStatus",new Object[]{user, simJobStatusQuerySpec});
        } catch (DataAccessException e) {
            lg.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
