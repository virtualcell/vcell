/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;
import java.rmi.RemoteException;

import org.vcell.solver.nfsim.NFSimMolecularConfigurations;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetMetadata;
import cbit.vcell.simdata.DataSetTimeSeries;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SpatialSelection;

/**
 * Insert the type's description here.
 * Creation date: (12/6/2001 1:51:41 PM)
 * @author: Jim Schaff
 */
public class RpcDataServerProxy extends AbstractRpcServerProxy implements cbit.vcell.server.DataSetController {
/**
 * DataServerProxy constructor comment.
 */
public RpcDataServerProxy(UserLoginInfo userLoginInfo, VCMessageSession vcMessageSession, SessionLog log) {
	super(userLoginInfo, vcMessageSession, VCellQueue.DataRequestQueue, log);
}



public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws org.vcell.util.DataAccessException {
	return (FieldDataFileOperationResults)rpc("fieldDataFileOperation",new Object[]{userLoginInfo.getUser(), fieldDataFileOperationSpec});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.DataIdentifier[] getDataIdentifiers(OutputContext outputContext,VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (DataIdentifier[])rpc("getDataIdentifiers",new Object[]{outputContext,userLoginInfo.getUser(), vcdID});
}

public DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException {
	return (DataOperationResults)rpc("doDataOperation", new Object[]{userLoginInfo.getUser(), dataOperation});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public double[] getDataSetTimes(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (double[])rpc("getDataSetTimes",new Object[]{userLoginInfo.getUser(), vcdID});
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2004 1:01:25 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.solver.AnnotatedFunction[] getFunctions(OutputContext outputContext,org.vcell.util.document.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.solver.AnnotatedFunction[])rpc("getFunctions",new Object[]{outputContext,userLoginInfo.getUser(), vcdataID});
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.plot.PlotData
 * @param variable java.lang.String
 * @param time double
 * @param spatialSelection cbit.vcell.simdata.gui.SpatialSelection
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.plot.PlotData getLineScan(OutputContext outputContext,VCDataIdentifier vcdID, String variable, double time, SpatialSelection spatialSelection) throws org.vcell.util.DataAccessException {
	return (cbit.plot.PlotData)rpc("getLineScan",new Object[]{outputContext,userLoginInfo.getUser(), vcdID,variable,new Double(time),spatialSelection});
}


/**
 * This method was created in VisualAge.
 * @return CartesianMesh
 */
public cbit.vcell.solvers.CartesianMesh getMesh(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.solvers.CartesianMesh)rpc("getMesh",new Object[]{userLoginInfo.getUser(), vcdID});
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 1:51:41 PM)
 * @param odeSimData cbit.vcell.export.data.ODESimData
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.solver.ode.ODESimData getODEData(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.solver.ode.ODESimData)rpc("getODEData",new Object[]{userLoginInfo.getUser(), vcdID});
}


/**
 * This method was created in VisualAge.
 * @return ParticleData
 * @param time double
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdID, double time) throws org.vcell.util.DataAccessException {
	return (ParticleDataBlock)rpc("getParticleDataBlock",new Object[]{userLoginInfo.getUser(), vcdID,new Double(time)});
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getParticleDataExists(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	Boolean bParticleDataExists = (Boolean)rpc("getParticleDataExists",new Object[]{userLoginInfo.getUser(), vcdID});
	return bParticleDataExists.booleanValue();
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public SimDataBlock getSimDataBlock(OutputContext outputContext,VCDataIdentifier vcdID, String varName, double time) throws org.vcell.util.DataAccessException {
	return (SimDataBlock)rpc("getSimDataBlock",new Object[]{outputContext,userLoginInfo.getUser(), vcdID,varName,new Double(time)});
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 * @param x int
 * @param y int
 * @param z int
 * @exception java.rmi.RemoteException The exception description.
 */
public org.vcell.util.document.TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext,VCDataIdentifier vcdID,org.vcell.util.document.TimeSeriesJobSpec timeSeriesJobSpec) throws org.vcell.util.DataAccessException {
//	return (cbit.util.TimeSeriesJobResults)rpc("getTimeSeriesValues",new Object[]{user, vcdID,timeSeriesJobSpec});
	try {
		if(!timeSeriesJobSpec.getVcDataJobID().isBackgroundTask()){
			return (org.vcell.util.document.TimeSeriesJobResults)rpc("getTimeSeriesValues",new Object[]{outputContext,userLoginInfo.getUser(), vcdID,timeSeriesJobSpec});
		}else{
			rpc(ServiceType.DATA, "getTimeSeriesValues", new Object[]{outputContext,userLoginInfo.getUser(), vcdID,timeSeriesJobSpec}, false);
		}
	} catch (DataAccessException ex) {
		log.exception(ex);
		throw ex;
	} catch (RuntimeException e){
		log.exception(e);
		throw e;
	} catch (Exception e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 3:56:41 PM)
 * @return cbit.rmi.event.ExportEvent
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.rmi.event.ExportEvent makeRemoteFile(OutputContext outputContext,cbit.vcell.export.server.ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException {
	try {
		rpc(ServiceType.DATA, "makeRemoteFile", new Object[]{outputContext,userLoginInfo.getUser(), exportSpecs}, false, new String[]{ServiceType.DATAEXPORT.getName()}, new Object[]{new Boolean(true)});
	} catch (DataAccessException ex) {
		log.exception(ex);
		throw ex;
	} catch (RuntimeException e){
		log.exception(e);
		throw e;
	} catch (Exception e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
	return null;
}

@Override
public DataSetMetadata getDataSetMetadata(VCDataIdentifier vcdataID) throws DataAccessException {
	return (DataSetMetadata)rpc("getDataSetMetadata", new Object[]{userLoginInfo.getUser(), vcdataID});
}


@Override
public DataSetTimeSeries getDataSetTimeSeries(VCDataIdentifier vcdataID, String[] variableNames) throws DataAccessException {
	return (DataSetTimeSeries)rpc("getDataSetTimeSeries", new Object[]{userLoginInfo.getUser(), vcdataID, variableNames});
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
private Object rpc(String methodName, Object[] args) throws DataAccessException {
	try {
		return rpc(ServiceType.DATA, methodName, args, true);
	} catch (DataAccessException ex) {
		log.exception(ex);
		throw ex;
	} catch (RuntimeException e){
		log.exception(e);
		throw e;
	} catch (Exception e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}



@Override
public VtuFileContainer getEmptyVtuMeshFiles(VCDataIdentifier vcdataID, int timeIndex)	throws DataAccessException {
	return (VtuFileContainer)rpc("getEmptyVtuMeshFiles", new Object[]{userLoginInfo.getUser(), vcdataID, new Integer(timeIndex)});
}


@Override
public double[] getVtuMeshData(OutputContext outputContext, VCDataIdentifier vcdataID, VtuVarInfo var, double time)	throws DataAccessException {
	return (double[])rpc("getVtuMeshData", new Object[]{userLoginInfo.getUser(), outputContext, vcdataID, var, new Double(time)});
}



@Override
public VtuVarInfo[] getVtuVarInfos(OutputContext outputContext, VCDataIdentifier vcdataID) throws DataAccessException {
	return (VtuVarInfo[])rpc("getVtuVarInfos", new Object[]{userLoginInfo.getUser(), outputContext, vcdataID});
}



@Override
public double[] getVtuTimes(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
	return (double[])rpc("getVtuTimes", new Object[]{userLoginInfo.getUser(), vcdataID});
}



@Override
public NFSimMolecularConfigurations getNFSimMolecularConfigurations(VCDataIdentifier vcdataID)
		throws RemoteException, DataAccessException {
	return (NFSimMolecularConfigurations)rpc("getNFSimMolecularConfigurations", new Object[]{userLoginInfo.getUser(), vcdataID});
}

}
