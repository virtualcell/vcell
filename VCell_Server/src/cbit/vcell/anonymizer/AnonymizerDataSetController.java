package cbit.vcell.anonymizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.vcell.mesh.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @author: Jim Schaff
 */
public class AnonymizerDataSetController extends AnonymizerService implements cbit.vcell.server.DataSetController {
/**
 * AnonymizerDataSetController constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
protected AnonymizerDataSetController(AnonymizerVCellConnection arg_anonymizerVCellConnection, SessionLog arg_sessionLog) throws java.rmi.RemoteException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortDataSetController,0), arg_anonymizerVCellConnection, arg_sessionLog);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void addFunction(org.vcell.util.VCDataIdentifier vcdataID, cbit.vcell.math.AnnotatedFunction function) throws DataAccessException, java.rmi.RemoteException {	
	remoteCall("addFunction", new Class[] {org.vcell.util.VCDataIdentifier.class, cbit.vcell.math.AnnotatedFunction.class}, new Object[] {vcdataID, function});	
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function[]
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void addFunctions(org.vcell.util.VCDataIdentifier vcdID, cbit.vcell.math.AnnotatedFunction[] function) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	remoteCall("addFunctions", new Class[] {org.vcell.util.VCDataIdentifier.class, cbit.vcell.math.AnnotatedFunction[].class}, new Object[] {vcdID, function});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.math.DataIdentifier[] getDataIdentifiers(org.vcell.util.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {    
	return (cbit.vcell.math.DataIdentifier[])remoteCall("getDataIdentifiers", new Class[] {org.vcell.util.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public double[] getDataSetTimes(org.vcell.util.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {	
	return (double[])remoteCall("getDataSetTimes", new Class[] {org.vcell.util.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.math.AnnotatedFunction[] getFunctions(org.vcell.util.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {	
	return (cbit.vcell.math.AnnotatedFunction[])remoteCall("getFunctions", new Class[] {org.vcell.util.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @return boolean
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public boolean getIsODEData(org.vcell.util.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return ((Boolean)remoteCall("getIsODEData", new Class[] {org.vcell.util.VCDataIdentifier.class}, new Object[] {vcdataID})).booleanValue();
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
public cbit.plot.PlotData getLineScan(org.vcell.util.VCDataIdentifier vcdataID, String variable, double time, org.vcell.util.CoordinateIndex begin, org.vcell.util.CoordinateIndex end) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.plot.PlotData)remoteCall("getLineScan", 
		new Class[] {org.vcell.util.VCDataIdentifier.class, String.class, double.class, org.vcell.util.CoordinateIndex.class, org.vcell.util.CoordinateIndex.class}, 
		new Object[] {vcdataID, variable, new Double(time), begin, end});
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.plot.PlotData
 * @param variable java.lang.String
 * @param time double
 * @param spatialSelection cbit.vcell.simdata.gui.SpatialSelection
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.plot.PlotData getLineScan(org.vcell.util.VCDataIdentifier vcdataID, String variable, double time, cbit.vcell.simdata.SpatialSelection spatialSelection) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.plot.PlotData)remoteCall("getLineScan", 
		new Class[] {org.vcell.util.VCDataIdentifier.class, String.class, double.class, cbit.vcell.simdata.SpatialSelection.class}, 
		new Object[] {vcdataID, variable, new Double(time), spatialSelection});
}


/**
 * This method was created in VisualAge.
 * @return CartesianMesh
 */
public CartesianMesh getMesh(org.vcell.util.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (CartesianMesh)remoteCall("getMesh", new Class[] {org.vcell.util.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param odeSimData cbit.vcell.export.data.ODESimData
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.ODESimData getODEData(org.vcell.util.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.simdata.ODESimData)remoteCall("getODEData", new Class[] {org.vcell.util.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * This method was created in VisualAge.
 * @return ParticleData
 * @param time double
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.ParticleDataBlock getParticleDataBlock(org.vcell.util.VCDataIdentifier vcdataID, double time) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.simdata.ParticleDataBlock)remoteCall("getParticleDataBlock", 
		new Class[] {org.vcell.util.VCDataIdentifier.class, double.class}, new Object[] {vcdataID, new Double(time)});
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getParticleDataExists(org.vcell.util.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return ((Boolean)remoteCall("getParticleDataExists", new Class[] {org.vcell.util.VCDataIdentifier.class}, new Object[] {vcdataID})).booleanValue();
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.SimDataBlock getSimDataBlock(org.vcell.util.VCDataIdentifier vcdataID, String varName, double time) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.simdata.SimDataBlock)remoteCall("getSimDataBlock", 
		new Class[] {org.vcell.util.VCDataIdentifier.class, String.class, double.class}, new Object[] {vcdataID, varName, new Double(time)});

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
public org.vcell.util.TimeSeriesJobResults getTimeSeriesValues(org.vcell.util.VCDataIdentifier vcdataID, org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (org.vcell.util.TimeSeriesJobResults)remoteCall("getTimeSeriesValues", 
		new Class[] {org.vcell.util.VCDataIdentifier.class, org.vcell.util.TimeSeriesJobSpec.class}, new Object[] {vcdataID, timeSeriesJobSpec});

}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @return cbit.rmi.event.ExportEvent
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.rmi.event.ExportEvent makeRemoteFile(cbit.vcell.export.ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (cbit.rmi.event.ExportEvent)remoteCall("makeRemoteFile", new Class[] {cbit.vcell.export.ExportSpecs.class}, new Object[] {exportSpecs});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
private Object remoteCall(String methodName, Class[] argClasses, Object[] args) throws java.rmi.RemoteException, org.vcell.util.DataAccessException {
	return remoteCall(anonymizerVCellConnection.getRemoteDataSetController(), methodName, argClasses, args);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void removeFunction(org.vcell.util.VCDataIdentifier vcdataID, cbit.vcell.math.AnnotatedFunction function) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	remoteCall("removeFunction", new Class[] {org.vcell.util.VCDataIdentifier.class, cbit.vcell.math.AnnotatedFunction.class}, new Object[] {vcdataID, function});	
}
}