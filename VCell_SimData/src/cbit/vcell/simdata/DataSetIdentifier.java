package cbit.vcell.simdata;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.expression.ExpressionException;
import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;
import org.vcell.expression.SymbolTableEntry;

import cbit.vcell.math.VariableType;
import edu.uchc.vcell.expression.internal.*;
/**
 * Insert the type's description here.
 * Creation date: (10/11/00 4:28:07 PM)
 * @author: 
 */
public class DataSetIdentifier implements SymbolTableEntry, java.io.Serializable {
	private int symbolTableIndex = -1;
	private String name = "un-named";
	private VariableType variableType = null;
	private boolean bFunction = false;

/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 10:03:57 AM)
 */
public DataSetIdentifier(String argName, VariableType argVariableType) {
	this(argName, argVariableType, false);
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 10:04:34 AM)
 */
public DataSetIdentifier(String argName, VariableType argVariableType, boolean arg_bFunction) {
	super();
	name = argName;
	variableType = argVariableType;
	bFunction = arg_bFunction;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 10:19:24 AM)
 */
public boolean equals(Object obj) {
	if (!(obj instanceof DataSetIdentifier)) {
		return false;
	}

	DataSetIdentifier dsi = (DataSetIdentifier)obj;
	if (dsi.name.equals(name)) {
		return true;
	}
	
	return false;
}


/**
 * This method was created in VisualAge.
 * @return double
 */
public double getConstantValue() throws ExpressionException {
	throw new ExpressionException("getConstantValue() not supported for cbit.vcell.simdata.DataSetIdentifier");
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public IExpression getExpression() throws ExpressionException {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getIndex() {
	return symbolTableIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 4:46:24 PM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (7/31/2003 10:25:49 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 12:07:46 PM)
 * @return cbit.vcell.units.VCUnitDefinition
 */
public org.vcell.units.VCUnitDefinition getUnitDefinition() {
	return null;
}


/**
 * This method returns the value corresponding to the symbol
 * @return Double
 * @param featureIndex int
 * @param neighborIndex int
*/
public double getValue() throws ExpressionException {
	throw new RuntimeException("getValue() not supported for cbit.vcell.simdata.DataSetIdentifier");
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/01 3:01:49 PM)
 * @return cbit.vcell.simdata.VariableType
 */
public VariableType getVariableType() {
	return variableType;
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @exception java.lang.Exception The exception description.
 */
public boolean isConstant() throws ExpressionException {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2004 5:20:14 PM)
 * @return boolean
 */
public boolean isFunction() {
	return bFunction;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 4:35:41 PM)
 * @param dataIndex int
 */
public void setIndex(int argSymbolTableIndex) {
	this.symbolTableIndex = argSymbolTableIndex;
}


/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
public String toString() {
	return getName();
}
}