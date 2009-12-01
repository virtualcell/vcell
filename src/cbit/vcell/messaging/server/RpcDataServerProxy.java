package cbit.vcell.messaging.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.field.FieldDataFileOperationResults;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.DataProcessingOutput;

/**
 * Insert the type's description here.
 * Creation date: (12/6/2001 1:51:41 PM)
 * @author: Jim Schaff
 */
public class RpcDataServerProxy extends AbstractRpcServerProxy implements cbit.vcell.server.DataSetController {
/**
 * DataServerProxy constructor comment.
 */
public RpcDataServerProxy(User argUser, JmsClientMessaging clientMessaging, SessionLog log) throws javax.jms.JMSException {
	super(argUser, clientMessaging, cbit.vcell.messaging.JmsUtils.getQueueSimDataReq(), log);
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2004 1:01:25 PM)
 * @param function cbit.vcell.math.Function[]
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void addFunctions(org.vcell.util.document.VCDataIdentifier vcdID, cbit.vcell.math.AnnotatedFunction[] functionArr,boolean[] bReplaceArr) throws org.vcell.util.DataAccessException {
	rpc("addFunctions",new Object[]{user, vcdID,functionArr,bReplaceArr});
}

public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws org.vcell.util.DataAccessException {
	return (FieldDataFileOperationResults)rpc("fieldDataFileOperation",new Object[]{user, fieldDataFileOperationSpec});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.DataIdentifier[] getDataIdentifiers(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (DataIdentifier[])rpc("getDataIdentifiers",new Object[]{user, vcdID});
}

public DataProcessingOutput getDataProcessingOutput(VCDataIdentifier vcdID) throws DataAccessException {
	return (DataProcessingOutput)rpc("getDataProcessingOutput", new Object[]{user, vcdID});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public double[] getDataSetTimes(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (double[])rpc("getDataSetTimes",new Object[]{user, vcdID});
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2004 1:01:25 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.math.AnnotatedFunction[] getFunctions(org.vcell.util.document.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.math.AnnotatedFunction[])rpc("getFunctions",new Object[]{user, vcdataID});
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 1:51:41 PM)
 * @return boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public boolean getIsODEData(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	Boolean bIsODEData = (Boolean)rpc("getIsODEData",new Object[]{user, vcdID});
	return bIsODEData.booleanValue();
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.plot.PlotData
 * @param variable java.lang.String
 * @param time double
 * @param begin cbit.vcell.math.CoordinateIndex
 * @param end cbit.vcell.math.CoordinateIndex
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.plot.PlotData getLineScan(VCDataIdentifier vcdID, String variable, double time, org.vcell.util.CoordinateIndex begin, org.vcell.util.CoordinateIndex end) throws org.vcell.util.DataAccessException {
	return (cbit.plot.PlotData)rpc("getLineScan",new Object[]{user, vcdID,variable,new Double(time),begin, end});
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.plot.PlotData
 * @param variable java.lang.String
 * @param time double
 * @param spatialSelection cbit.vcell.simdata.gui.SpatialSelection
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.plot.PlotData getLineScan(VCDataIdentifier vcdID, String variable, double time, cbit.vcell.simdata.gui.SpatialSelection spatialSelection) throws org.vcell.util.DataAccessException {
	return (cbit.plot.PlotData)rpc("getLineScan",new Object[]{user, vcdID,variable,new Double(time),spatialSelection});
}


/**
 * This method was created in VisualAge.
 * @return CartesianMesh
 */
public cbit.vcell.solvers.CartesianMesh getMesh(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.solvers.CartesianMesh)rpc("getMesh",new Object[]{user, vcdID});
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 1:51:41 PM)
 * @param odeSimData cbit.vcell.export.data.ODESimData
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.solver.ode.ODESimData getODEData(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	return (cbit.vcell.solver.ode.ODESimData)rpc("getODEData",new Object[]{user, vcdID});
}


/**
 * This method was created in VisualAge.
 * @return ParticleData
 * @param time double
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdID, double time) throws org.vcell.util.DataAccessException {
	return (ParticleDataBlock)rpc("getParticleDataBlock",new Object[]{user, vcdID,new Double(time)});
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getParticleDataExists(VCDataIdentifier vcdID) throws org.vcell.util.DataAccessException {
	Boolean bParticleDataExists = (Boolean)rpc("getParticleDataExists",new Object[]{user, vcdID});
	return bParticleDataExists.booleanValue();
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public SimDataBlock getSimDataBlock(VCDataIdentifier vcdID, String varName, double time) throws org.vcell.util.DataAccessException {
	return (SimDataBlock)rpc("getSimDataBlock",new Object[]{user, vcdID,varName,new Double(time)});
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
public org.vcell.util.document.TimeSeriesJobResults getTimeSeriesValues(VCDataIdentifier vcdID,org.vcell.util.document.TimeSeriesJobSpec timeSeriesJobSpec) throws org.vcell.util.DataAccessException {
//	return (cbit.util.TimeSeriesJobResults)rpc("getTimeSeriesValues",new Object[]{user, vcdID,timeSeriesJobSpec});
	try {
		if(!timeSeriesJobSpec.getVcDataJobID().isBackgroundTask()){
			return (org.vcell.util.document.TimeSeriesJobResults)rpc("getTimeSeriesValues",new Object[]{user, vcdID,timeSeriesJobSpec});
		}else{
			rpc(ServiceType.DATA, "getTimeSeriesValues", new Object[]{user, vcdID,timeSeriesJobSpec}, false);
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
public cbit.rmi.event.ExportEvent makeRemoteFile(cbit.vcell.export.server.ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException {
	try {
		rpc(ServiceType.DATA, "makeRemoteFile", new Object[]{user, exportSpecs}, false, new String[]{ServiceType.DATAEXPORT.getName()}, new Object[]{new Boolean(true)});
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
 * Creation date: (2/26/2004 1:01:25 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void removeFunction(org.vcell.util.document.VCDataIdentifier vcdataID, cbit.vcell.math.AnnotatedFunction function) throws org.vcell.util.DataAccessException {
	rpc("removeFunction",new Object[]{user, vcdataID,function});
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
}