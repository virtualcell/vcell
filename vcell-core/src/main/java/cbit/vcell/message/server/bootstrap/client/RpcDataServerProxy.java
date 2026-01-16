/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap.client;

import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.simdata.*;
import org.vcell.solver.nfsim.NFSimMolecularConfigurations;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

/**
 * Insert the type's description here.
 * Creation date: (12/6/2001 1:51:41 PM)
 * @author: Jim Schaff
 */
public class RpcDataServerProxy extends AbstractRpcServerProxy {
/**
 * DataServerProxy constructor comment.
 */
public RpcDataServerProxy(UserLoginInfo userLoginInfo, RpcSender rpcSender) {
	super(userLoginInfo, rpcSender, VCellQueue.DataRequestQueue);
}


public cbit.vcell.simdata.DataIdentifier[] getDataIdentifiers(OutputContext outputContext,VCDataIdentifier vcdID) throws DataAccessException {
	return (DataIdentifier[])rpc("getDataIdentifiers",new Object[]{outputContext,userLoginInfo.getUser(), vcdID});
}

public DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException {
	return (DataOperationResults)rpc("doDataOperation", new Object[]{userLoginInfo.getUser(), dataOperation});
}


public double[] getDataSetTimes(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (double[])rpc("getDataSetTimes",new Object[]{userLoginInfo.getUser(), vcdID});
}


public cbit.vcell.solver.AnnotatedFunction[] getFunctions(OutputContext outputContext,org.vcell.util.document.VCDataIdentifier vcdataID) throws DataAccessException {
	return (cbit.vcell.solver.AnnotatedFunction[])rpc("getFunctions",new Object[]{outputContext,userLoginInfo.getUser(), vcdataID});
}


public cbit.plot.PlotData getLineScan(OutputContext outputContext,VCDataIdentifier vcdID, String variable, double time, SpatialSelection spatialSelection) throws DataAccessException {
	return (cbit.plot.PlotData)rpc("getLineScan",new Object[]{outputContext,userLoginInfo.getUser(), vcdID,variable,time,spatialSelection});
}

public cbit.vcell.solvers.CartesianMesh getMesh(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.solvers.CartesianMesh)rpc("getMesh",new Object[]{userLoginInfo.getUser(), vcdID});
}

public cbit.vcell.solver.ode.ODESimData getODEData(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.solver.ode.ODESimData)rpc("getODEData",new Object[]{userLoginInfo.getUser(), vcdID});
}

public LangevinBatchResultSet getLangevinBatchResultSet(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (LangevinBatchResultSet)rpc("getLangevinBatchResultSet",new Object[]{userLoginInfo.getUser(), vcdID});
}

public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdID, double time) throws org.vcell.util.DataAccessException {
	return (ParticleDataBlock)rpc("getParticleDataBlock",new Object[]{userLoginInfo.getUser(), vcdID,time});
}

public boolean getParticleDataExists(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	Boolean bParticleDataExists = (Boolean)rpc("getParticleDataExists",new Object[]{userLoginInfo.getUser(), vcdID});
	return bParticleDataExists.booleanValue();
}

public SimDataBlock getSimDataBlock(OutputContext outputContext,VCDataIdentifier vcdID, String varName, double time) throws org.vcell.util.DataAccessException {
	return (SimDataBlock)rpc("getSimDataBlock",new Object[]{outputContext,userLoginInfo.getUser(), vcdID,varName,time});
}

public org.vcell.util.document.TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext,VCDataIdentifier vcdID,org.vcell.util.document.TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
//	return (cbit.util.TimeSeriesJobResults)rpc("getTimeSeriesValues",new Object[]{user, vcdID,timeSeriesJobSpec});
	try {
		return (org.vcell.util.document.TimeSeriesJobResults)rpc("getTimeSeriesValues",new Object[]{outputContext,userLoginInfo.getUser(), vcdID,timeSeriesJobSpec});
	} catch (DataAccessException ex) {
		lg.error(ex.getMessage(),ex);
		throw ex;
	} catch (RuntimeException e){
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Exception e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException(e.getMessage());
	}
}


public cbit.rmi.event.ExportEvent makeRemoteFile(OutputContext outputContext,cbit.vcell.export.server.ExportSpecs exportSpecs) throws DataAccessException {
	try {
		rpc(RpcServiceType.DATA, "makeRemoteFile", new Object[]{outputContext,userLoginInfo.getUser(), exportSpecs}, false, new String[]{RpcServiceType.DATAEXPORT.getName()}, new Object[]{true});
	} catch (DataAccessException ex) {
		lg.error(ex.getMessage(),ex);
		throw ex;
	} catch (RuntimeException e){
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Exception e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException(e.getMessage());
	}
	return null;
}

public DataSetMetadata getDataSetMetadata(VCDataIdentifier vcdataID) throws DataAccessException {
	return (DataSetMetadata)rpc("getDataSetMetadata", new Object[]{userLoginInfo.getUser(), vcdataID});
}


public DataSetTimeSeries getDataSetTimeSeries(VCDataIdentifier vcdataID, String[] variableNames) throws DataAccessException {
	return (DataSetTimeSeries)rpc("getDataSetTimeSeries", new Object[]{userLoginInfo.getUser(), vcdataID, variableNames});
}


private Object rpc(String methodName, Object[] args) throws DataAccessException {
	try {
		return rpc(RpcServiceType.DATA, methodName, args, true);
	} catch (DataAccessException ex) {
		lg.error(ex.getMessage(),ex);
		throw ex;
	} catch (RuntimeException e){
		lg.error(e.getMessage(),e);
		throw e;
	} catch (Exception e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException(e.getMessage());
	}
}



public VtuFileContainer getEmptyVtuMeshFiles(VCDataIdentifier vcdataID, int timeIndex)	throws DataAccessException {
	return (VtuFileContainer)rpc("getEmptyVtuMeshFiles", new Object[]{userLoginInfo.getUser(), vcdataID, timeIndex});
}


public double[] getVtuMeshData(OutputContext outputContext, VCDataIdentifier vcdataID, VtuVarInfo var, double time)	throws DataAccessException {
	return (double[])rpc("getVtuMeshData", new Object[]{userLoginInfo.getUser(), outputContext, vcdataID, var, time});
}



public VtuVarInfo[] getVtuVarInfos(OutputContext outputContext, VCDataIdentifier vcdataID) throws DataAccessException {
	return (VtuVarInfo[])rpc("getVtuVarInfos", new Object[]{userLoginInfo.getUser(), outputContext, vcdataID});
}



public double[] getVtuTimes(VCDataIdentifier vcdataID) throws DataAccessException {
	return (double[])rpc("getVtuTimes", new Object[]{userLoginInfo.getUser(), vcdataID});
}



public NFSimMolecularConfigurations getNFSimMolecularConfigurations(VCDataIdentifier vcdataID)
		throws DataAccessException {
	return (NFSimMolecularConfigurations)rpc("getNFSimMolecularConfigurations", new Object[]{userLoginInfo.getUser(), vcdataID});
}

}
