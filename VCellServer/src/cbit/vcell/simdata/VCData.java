package cbit.vcell.simdata;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;

import java.io.*;
import cbit.vcell.solvers.*;
import cbit.vcell.parser.*;
import cbit.util.*;
/**
 * This type was created in VisualAge.
 */
public abstract class VCData implements SimDataConstants {

/**
 * SimResults constructor comment.
 */
protected VCData() {
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:28:51 PM)
 * @param function cbit.vcell.math.Function
 */
public abstract void addFunction(AnnotatedFunction function) throws ExpressionException;


/**
 * Insert the method's description here.
 * Creation date: (1/19/00 11:52:22 AM)
 * @return long
 * @param dataType int
 * @param timepoint double
 * @exception cbit.util.DataAccessException The exception description.
 */
public abstract long getDataBlockTimeStamp(int dataType, double timepoint) throws DataAccessException;


/**
 * This method was created in VisualAge.
 * @return double[]
 */
public abstract double[] getDataTimes() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public abstract AnnotatedFunction getFunction(String identifier);


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public abstract AnnotatedFunction[] getFunctions();


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public abstract boolean getIsODEData() throws DataAccessException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
public abstract CartesianMesh getMesh() throws DataAccessException, MathException;


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 2:28:47 PM)
 * @return cbit.vcell.simdata.ODEDataBlock
 */
public abstract ODEDataBlock getODEDataBlock() throws DataAccessException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.ParticleDataBlock
 * @param double time
 */
public abstract ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException, IOException;


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public abstract boolean getParticleDataExists() throws DataAccessException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.SimResultsInfo
 */
public abstract VCDataIdentifier getResultsInfoObject();


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public abstract SimDataBlock getSimDataBlock(String varName, double time) throws DataAccessException, IOException;


/**
 * This method was created in VisualAge.
 * @return long
 */
public abstract long getSizeInBytes();


/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public abstract DataIdentifier[] getVarAndFunctionDataIdentifiers() throws IOException, DataAccessException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
abstract int[] getVolumeSize() throws IOException, DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:28:51 PM)
 * @param function cbit.vcell.math.Function
 */
public abstract void removeFunction(AnnotatedFunction function) throws DataAccessException;
}