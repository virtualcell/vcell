package cbit.vcell.simdata;
import cbit.util.VCDataIdentifier;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (9/19/2003 3:28:52 PM)
 * @author: Anuradha Lakshminarayana
 */
public class ExternalData extends VCData {
/**
 * ExternalData constructor comment.
 */
public ExternalData() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 3:28:52 PM)
 * @param function cbit.vcell.math.Function
 */
public void addFunction(cbit.vcell.math.AnnotatedFunction function) throws cbit.vcell.parser.ExpressionBindingException {}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 3:28:52 PM)
 * @return long
 * @param dataType int
 * @param timepoint double
 * @exception cbit.util.DataAccessException The exception description.
 */
public long getDataBlockTimeStamp(int dataType, double timepoint) throws cbit.util.DataAccessException {
	return 0;
}


/**
 * This method was created in VisualAge.
 * @return double[]
 */
public double[] getDataTimes() throws cbit.util.DataAccessException {
	return null;
}


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 3:28:52 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public cbit.vcell.math.AnnotatedFunction getFunction(String identifier) {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 3:28:52 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public cbit.vcell.math.AnnotatedFunction[] getFunctions() {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getIsODEData() throws cbit.util.DataAccessException {
	return false;
}


/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public java.io.File getLogFile() throws java.io.FileNotFoundException {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
public CartesianMesh getMesh() throws cbit.util.DataAccessException, cbit.vcell.math.MathException {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 3:28:52 PM)
 * @return cbit.vcell.simdata.ODEDataBlock
 */
public ODEDataBlock getODEDataBlock() throws cbit.util.DataAccessException {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.ParticleDataBlock
 * @param double time
 */
public ParticleDataBlock getParticleDataBlock(double time) throws cbit.util.DataAccessException, java.io.IOException {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getParticleDataExists() throws cbit.util.DataAccessException {
	return false;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.SimResultsInfo
 */
public VCDataIdentifier getResultsInfoObject() {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public SimDataBlock getSimDataBlock(String varName, double time) throws cbit.util.DataAccessException, java.io.IOException {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return long
 */
public long getSizeInBytes() {
	return 0;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public cbit.vcell.math.DataIdentifier[] getVarAndFunctionDataIdentifiers() throws cbit.util.DataAccessException, java.io.IOException {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
int[] getVolumeSize() throws cbit.util.DataAccessException, java.io.IOException {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 3:28:52 PM)
 * @param function cbit.vcell.math.Function
 */
public void removeFunction(cbit.vcell.math.AnnotatedFunction function) throws cbit.util.DataAccessException {}
}