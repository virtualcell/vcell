package cbit.rmi.event;

import java.io.Serializable;

public class VCellApiRpcBody implements Serializable {
    public final RpcDestination rpcDestination;
    public final VCellApiRpcRequest rpcRequest;
    public final boolean returnedRequired;
    public final int timeoutMS;
    public final String[] specialProperties;
    public final Object[] specialValues;

    public VCellApiRpcBody(RpcDestination rpcDestination, VCellApiRpcRequest rpcRequest, boolean returnedRequired, int timeoutMS, String[] specialProperties, Object[] specialValues) {
        this.rpcDestination = rpcDestination;
        this.rpcRequest = rpcRequest;
        this.returnedRequired = returnedRequired;
        this.timeoutMS = timeoutMS;
        this.specialProperties = specialProperties;
        this.specialValues = specialValues;
    }
    public enum RpcDestination {
        DataRequestQueue, DbRequestQueue, SimReqQueue;
    }
}
