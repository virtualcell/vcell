package cbit.vcell.messaging.server;
import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.util.VCDataIdentifier;
import cbit.vcell.math.DataIdentifier;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;

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
 * @param function cbit.vcell.math.Function
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void addFunction(VCDataIdentifier vcdataID, cbit.vcell.math.AnnotatedFunction function) throws DataAccessException {
	rpc("addFunction",new Object[]{user, vcdataID,function});
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2004 1:01:25 PM)
 * @param function cbit.vcell.math.Function[]
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void addFunctions(VCDataIdentifier vcdID, cbit.vcell.math.AnnotatedFunction[] function) throws DataAccessException {
	rpc("addFunctions",new Object[]{user, vcdID,function});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public DataIdentifier[] getDataIdentifiers(VCDataIdentifier vcdID) throws DataAccessException {
	return (DataIdentifier[])rpc("getDataIdentifiers",new Object[]{user, vcdID});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public double[] getDataSetTimes(VCDataIdentifier vcdID) throws DataAccessException {
	return (double[])rpc("getDataSetTimes",new Object[]{user, vcdID});
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2004 1:01:25 PM)
 * @param function cbit.vcell.math.Function
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.math.AnnotatedFunction[] getFunctions(VCDataIdentifier vcdataID) throws DataAccessException {
	return (cbit.vcell.math.AnnotatedFunction[])rpc("getFunctions",new Object[]{user, vcdataID});
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 1:51:41 PM)
 * @return boolean
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public boolean getIsODEData(VCDataIdentifier vcdID) throws DataAccessException {
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
public cbit.plot.PlotData getLineScan(VCDataIdentifier vcdID, String variable, double time, cbit.util.CoordinateIndex begin, cbit.util.CoordinateIndex end) throws DataAccessException {
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
public cbit.plot.PlotData getLineScan(VCDataIdentifier vcdID, String variable, double time, cbit.vcell.simdata.SpatialSelection spatialSelection) throws DataAccessException {
	return (cbit.plot.PlotData)rpc("getLineScan",new Object[]{user, vcdID,variable,new Double(time),spatialSelection});
}


/**
 * This method was created in VisualAge.
 * @return CartesianMesh
 */
public cbit.vcell.mesh.CartesianMesh getMesh(VCDataIdentifier vcdID) throws DataAccessException {
	return (cbit.vcell.mesh.CartesianMesh)rpc("getMesh",new Object[]{user, vcdID});
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 1:51:41 PM)
 * @param odeSimData cbit.vcell.export.data.ODESimData
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.ODESimData getODEData(VCDataIdentifier vcdID) throws DataAccessException {
	return (cbit.vcell.simdata.ODESimData)rpc("getODEData",new Object[]{user, vcdID});
}


/**
 * This method was created in VisualAge.
 * @return ParticleData
 * @param time double
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdID, double time) throws DataAccessException {
	return (ParticleDataBlock)rpc("getParticleDataBlock",new Object[]{user, vcdID,new Double(time)});
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getParticleDataExists(VCDataIdentifier vcdID) throws DataAccessException {
	Boolean bParticleDataExists = (Boolean)rpc("getParticleDataExists",new Object[]{user, vcdID});
	return bParticleDataExists.booleanValue();
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public SimDataBlock getSimDataBlock(VCDataIdentifier vcdID, String varName, double time) throws DataAccessException {
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
public cbit.util.TimeSeriesJobResults getTimeSeriesValues(VCDataIdentifier vcdID,cbit.util.TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
	return (cbit.util.TimeSeriesJobResults)rpc("getTimeSeriesValues",new Object[]{user, vcdID,timeSeriesJobSpec});
}


/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 3:56:41 PM)
 * @return cbit.rmi.event.ExportEvent
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.export.ExportEvent makeRemoteFile(cbit.vcell.export.ExportSpecs exportSpecs) throws DataAccessException {
	try {
		rpc(cbit.vcell.messaging.MessageConstants.SERVICETYPE_DATA_VALUE, "makeRemoteFile", new Object[]{user, exportSpecs}, false, new String[]{MessageConstants.SERVICE_DATA_ISEXPORTING}, new Object[]{new Boolean(true)});
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
 * @exception DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void removeFunction(VCDataIdentifier vcdataID, cbit.vcell.math.AnnotatedFunction function) throws DataAccessException {
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
		return rpc(cbit.vcell.messaging.MessageConstants.SERVICETYPE_DATA_VALUE, methodName, args, true);
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