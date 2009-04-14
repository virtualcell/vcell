package cbit.vcell.anonymizer;
import cbit.vcell.field.FieldDataFileOperationResults;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.solver.DataProcessingOutput;

import java.lang.reflect.*;
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
 * @param function cbit.vcell.math.Function[]
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void addFunctions(cbit.vcell.server.VCDataIdentifier vcdID, cbit.vcell.math.AnnotatedFunction[] function,boolean[] bReplaceArr) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	remoteCall("addFunctions", new Class[] {cbit.vcell.server.VCDataIdentifier.class, cbit.vcell.math.AnnotatedFunction[].class}, new Object[] {vcdID, function,bReplaceArr});
}

public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (FieldDataFileOperationResults)remoteCall("fieldDataFileOperation", 
		new Class[] {FieldDataFileOperationSpec.class}, new Object[] {fieldDataFileOperationSpec});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.DataIdentifier[] getDataIdentifiers(cbit.vcell.server.VCDataIdentifier vcdataID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {    
	return (cbit.vcell.simdata.DataIdentifier[])remoteCall("getDataIdentifiers", new Class[] {cbit.vcell.server.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public double[] getDataSetTimes(cbit.vcell.server.VCDataIdentifier vcdataID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {	
	return (double[])remoteCall("getDataSetTimes", new Class[] {cbit.vcell.server.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.math.AnnotatedFunction[] getFunctions(cbit.vcell.server.VCDataIdentifier vcdataID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {	
	return (cbit.vcell.math.AnnotatedFunction[])remoteCall("getFunctions", new Class[] {cbit.vcell.server.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @return boolean
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public boolean getIsODEData(cbit.vcell.server.VCDataIdentifier vcdataID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return ((Boolean)remoteCall("getIsODEData", new Class[] {cbit.vcell.server.VCDataIdentifier.class}, new Object[] {vcdataID})).booleanValue();
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
public cbit.plot.PlotData getLineScan(cbit.vcell.server.VCDataIdentifier vcdataID, String variable, double time, cbit.vcell.math.CoordinateIndex begin, cbit.vcell.math.CoordinateIndex end) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.plot.PlotData)remoteCall("getLineScan", 
		new Class[] {cbit.vcell.server.VCDataIdentifier.class, String.class, double.class, cbit.vcell.math.CoordinateIndex.class, cbit.vcell.math.CoordinateIndex.class}, 
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
public cbit.plot.PlotData getLineScan(cbit.vcell.server.VCDataIdentifier vcdataID, String variable, double time, cbit.vcell.simdata.gui.SpatialSelection spatialSelection) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.plot.PlotData)remoteCall("getLineScan", 
		new Class[] {cbit.vcell.server.VCDataIdentifier.class, String.class, double.class, cbit.vcell.simdata.gui.SpatialSelection.class}, 
		new Object[] {vcdataID, variable, new Double(time), spatialSelection});
}


/**
 * This method was created in VisualAge.
 * @return CartesianMesh
 */
public cbit.vcell.solvers.CartesianMesh getMesh(cbit.vcell.server.VCDataIdentifier vcdataID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.solvers.CartesianMesh)remoteCall("getMesh", new Class[] {cbit.vcell.server.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param odeSimData cbit.vcell.export.data.ODESimData
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.solver.ode.ODESimData getODEData(cbit.vcell.server.VCDataIdentifier vcdataID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.solver.ode.ODESimData)remoteCall("getODEData", new Class[] {cbit.vcell.server.VCDataIdentifier.class}, new Object[] {vcdataID});
}


/**
 * This method was created in VisualAge.
 * @return ParticleData
 * @param time double
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.ParticleDataBlock getParticleDataBlock(cbit.vcell.server.VCDataIdentifier vcdataID, double time) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.simdata.ParticleDataBlock)remoteCall("getParticleDataBlock", 
		new Class[] {cbit.vcell.server.VCDataIdentifier.class, double.class}, new Object[] {vcdataID, new Double(time)});
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public DataProcessingOutput getDataProcessingOutput(cbit.vcell.server.VCDataIdentifier vcdataID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return ((DataProcessingOutput)remoteCall("getDataProcessingOutput", new Class[] {cbit.vcell.server.VCDataIdentifier.class}, new Object[] {vcdataID}));
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getParticleDataExists(cbit.vcell.server.VCDataIdentifier vcdataID) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return ((Boolean)remoteCall("getParticleDataExists", new Class[] {cbit.vcell.server.VCDataIdentifier.class}, new Object[] {vcdataID})).booleanValue();
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.simdata.SimDataBlock getSimDataBlock(cbit.vcell.server.VCDataIdentifier vcdataID, String varName, double time) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.vcell.simdata.SimDataBlock)remoteCall("getSimDataBlock", 
		new Class[] {cbit.vcell.server.VCDataIdentifier.class, String.class, double.class}, new Object[] {vcdataID, varName, new Double(time)});

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
public cbit.util.TimeSeriesJobResults getTimeSeriesValues(cbit.vcell.server.VCDataIdentifier vcdataID, cbit.util.TimeSeriesJobSpec timeSeriesJobSpec) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.util.TimeSeriesJobResults)remoteCall("getTimeSeriesValues", 
		new Class[] {cbit.vcell.server.VCDataIdentifier.class, cbit.util.TimeSeriesJobSpec.class}, new Object[] {vcdataID, timeSeriesJobSpec});

}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @return cbit.rmi.event.ExportEvent
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.rmi.event.ExportEvent makeRemoteFile(cbit.vcell.export.server.ExportSpecs exportSpecs) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	return (cbit.rmi.event.ExportEvent)remoteCall("makeRemoteFile", new Class[] {cbit.vcell.export.server.ExportSpecs.class}, new Object[] {exportSpecs});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
private Object remoteCall(String methodName, Class[] argClasses, Object[] args) throws java.rmi.RemoteException, cbit.vcell.server.DataAccessException {
	return remoteCall(anonymizerVCellConnection.getRemoteDataSetController(), methodName, argClasses, args);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.vcell.server.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public void removeFunction(cbit.vcell.server.VCDataIdentifier vcdataID, cbit.vcell.math.AnnotatedFunction function) throws cbit.vcell.server.DataAccessException, java.rmi.RemoteException {
	remoteCall("removeFunction", new Class[] {cbit.vcell.server.VCDataIdentifier.class, cbit.vcell.math.AnnotatedFunction.class}, new Object[] {vcdataID, function});	
}
}