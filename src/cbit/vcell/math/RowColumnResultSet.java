/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vcell.util.BeanUtils;

import cbit.vcell.math.Variable.Domain;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionDomainException;
import cbit.vcell.parser.VariableSymbolTable;
import cbit.vcell.util.ColumnDescription;
/**
 *  This will have a list of Variables (NB: ReservedVariable.TIME is a ReservedVariable,
 *  and a ReservedVariable is a Variable...also, StateVariables are NOT Variables, but
 *  they are equivalent to them, and indeed are constructed from them.  So, the cols of
 *  this class will be represented by a vector of Variables, and the rows will be a
 *  vector of double[]...
 *  This guy probably has some synchronization problems...
 */
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:02 PM)
 * @author: John Wagner
 */
@SuppressWarnings("serial")
public class RowColumnResultSet implements java.io.Serializable {
	private Vector<ColumnDescription> fieldDataColumnDescriptions = new Vector<ColumnDescription>();
	private Vector<ColumnDescription> fieldFunctionColumnDescriptions = new Vector<ColumnDescription>();
	private Vector<double[]> fieldValues = new Vector<double[]> ();  // vector of rows (each row is a double[])
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ColumnDescription[] fieldColumnDescriptions = null;
	
	private transient VariableSymbolTable resultSetSymbolTableWithFunction = null;
	private transient VariableSymbolTable resultSetSymbolTableWithoutFunction = null; 
	private static final Logger lg = Logger.getLogger(RowColumnResultSet.class);

/**
 *  construct empty, add columns via {@link #addDataColumn(ColumnDescription)} et. al. after creation 
 */
public RowColumnResultSet() {
}
public RowColumnResultSet(RowColumnResultSet copyThisRowColumnResultSet) {
	this.fieldDataColumnDescriptions = new Vector<ColumnDescription>(copyThisRowColumnResultSet.fieldDataColumnDescriptions);
	this.fieldFunctionColumnDescriptions = new Vector<ColumnDescription>(copyThisRowColumnResultSet.fieldFunctionColumnDescriptions);
	this.fieldValues = new Vector<double[]>(copyThisRowColumnResultSet.fieldValues);
}

/**
 * SimpleODEData constructor comment.
 *  JMW : THIS NEEDS TO BE FIXED...THIS CONSTRUCTOR SHOULD NOT
 *  BE DOING ANY COLUMN CONSTRUCTION AT ALL!
 */
public RowColumnResultSet(String[] dataColumnNames) {
	for (int i = 0; i < dataColumnNames.length; i ++) {
		addDataColumn(new ODESolverResultSetColumnDescription(dataColumnNames[i]));
	}
}

/**
 * getVariableNames method comment.
 *  THIS WILL LATER BE DELETED PROBABLY...THE COLUMNS
 *  WILL BE SPECIFIED AT CONSTRUCTION TIME!
 */
public final void addDataColumn(ColumnDescription columnDescription) {
	// cbit.util.Assertion.assert(getRowCount() == 0);
	ColumnDescription[] oldValue = fieldColumnDescriptions;
	fieldDataColumnDescriptions.addElement(columnDescription);
	
	fieldColumnDescriptions = null;
	resultSetSymbolTableWithFunction = null;
	resultSetSymbolTableWithoutFunction = null;

	if (getPropertyChange().getPropertyChangeListeners().length > 0) {
		firePropertyChange("columnDescriptions", oldValue, getColumnDescriptions());
	}
}

/**
 * getVariableNames method comment.
 *  THIS WILL LATER BE DELETED PROBABLY...THE COLUMNS
 *  WILL BE SPECIFIED AT CONSTRUCTION TIME!
 */
public final void addFunctionColumn(FunctionColumnDescription functionColumnDescription) throws ExpressionException {
	ColumnDescription[] oldValue = fieldColumnDescriptions;
	addFunctionColumnInternal(functionColumnDescription);
	if (getPropertyChange().getPropertyChangeListeners().length > 0) {
		firePropertyChange("columnDescriptions", oldValue, getColumnDescriptions());
	}
}

/**
 * getVariableNames method comment.
 *  THIS WILL LATER BE DELETED PROBABLY...THE COLUMNS
 *  WILL BE SPECIFIED AT CONSTRUCTION TIME!
 */
private final void addFunctionColumnInternal(FunctionColumnDescription functionColumnDescription) throws ExpressionException {
	//
	// bind and substitute functions (resulting in expressions of only data columns).
	//
	functionColumnDescription.getExpression().bindExpression(getResultSetSymbolTableWithFunction());
	Expression exp1 = MathUtilities.substituteFunctions(functionColumnDescription.getExpression(),getResultSetSymbolTableWithFunction());
	functionColumnDescription.setExpression(exp1.flatten());

	//
	// didn't throw a binding exception, add to result set.
	//
	fieldFunctionColumnDescriptions.addElement(functionColumnDescription);
	
	Domain domain = null; //TODO domain
	Function func = new Function(functionColumnDescription.getName(),new Expression(functionColumnDescription.getExpression()),domain);
	getResultSetSymbolTableWithFunction().addVar(func);	
	func.setIndex(getColumnDescriptionsCount() - 1);
	
	fieldColumnDescriptions = null;
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

/**
 * getVariableNames method comment.
 */
public synchronized void addRow (double[] values) {
	// cbit.util.Assertion.assert(values.length == getDataColumnCount());
	double[] v = new double[getDataColumnCount()];
	if (values.length != getDataColumnCount()) {
		throw new RuntimeException("number of values in row is not equal to number of columns");
	}
	for (int c = 0; c < getDataColumnCount(); c++) v[c] = values[c];
	fieldValues.addElement(v);
}


/**
 * getVariableNames method comment.
 */
private double calculateErrorFactor(int t, double a[], double b[], double c[], double scale[]) {
    double errorFactor = 0.0;
    // we need to average coming into the point otherwise funny things happen in systems close to equilibrium situations	
	double c_minus_a_t = (c[t] - a[t])/scale[t];
    double b_minus_a_t = (b[t] - a[t])/scale[t];
    double c_minus_a_t_2 = c_minus_a_t*c_minus_a_t;
    if (c[t] == b[t]){
	    return 1.0;
    }
    //
    for (int i = 0; i < getDataColumnCount(); i++){
        if (i != t) {
            double c_minus_a_y = (c[i] - a[i])/scale[i];
            double F = Math.abs((b_minus_a_t)*(c_minus_a_y)-((b[i]-a[i])/scale[i])*(c_minus_a_t)) /
            					(c_minus_a_t_2 + c_minus_a_y*c_minus_a_y);
            errorFactor = Math.max(errorFactor,F);
        }
    }
    return errorFactor;
}


/**
 *  checkFunctionValidity method
 *  This method is used to check if a user defined function expression is valid.
 *  Takes a FunctionColumnDescription as argument. It substitutes the functions and binds the expression of the new function
 *  to check the functions validity. If it is not valid, it throws an ExpressionException, which is caught and handled by
 *  by the addFunction method in ODESolverPlotSpecificationPanel.
 */
public void checkFunctionValidity(FunctionColumnDescription fcd) throws ExpressionException {
	double[] values = null;
	if (getRowCount() > 0) {
		Expression exp = ((FunctionColumnDescription)fcd).getExpression();
		//
		// must rebind expression due to transient nature of expression binding (see ASTIdNode.symbolTableEntry)
		//
		exp.bindExpression(getResultSetSymbolTableWithFunction());
		Expression exp1 = MathUtilities.substituteFunctions(exp, getResultSetSymbolTableWithFunction());
		
		values = new double[getRowCount()];
		for (int r = 0; r < getRowCount(); r++) {
			values[r] = exp1.evaluateVector(getRow(r));
		}	
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/19/2003 4:32:00 PM)
 * @return cbit.vcell.parser.SymbolTable
 */
private VariableSymbolTable createResultSetSymbolTable(boolean bIncludeFunctions) {
	//
	// create symbol table for binding expression against data columns and functions (of data columns)
	//
	VariableSymbolTable resultSetSymbolTable = new VariableSymbolTable();
	for (int i = 0; i < getColumnDescriptionsCount(); i++) {
		ColumnDescription colDesc = getColumnDescriptions(i);
		if (colDesc instanceof ODESolverResultSetColumnDescription){
			Domain domain = null;
			VolVariable vVar = new VolVariable(colDesc.getName(),domain);
			vVar.setIndex(i);
			resultSetSymbolTable.addVar(vVar);
		}else if (bIncludeFunctions && colDesc instanceof FunctionColumnDescription){
			FunctionColumnDescription funcColDesc = (FunctionColumnDescription)colDesc;
			Domain domain = null;
			Function func = new Function(funcColDesc.getName(),new Expression(funcColDesc.getExpression()),domain);
			func.setIndex(i);
			resultSetSymbolTable.addVar(func);
		}
	}
	return resultSetSymbolTable;
}


/**
 * getVariableNames method comment.
 *  If column is empty, return null or an empty array?
 *  For now, null...
 */
public synchronized double[] extractColumn(int c) throws ExpressionException {
	double[] values = null;
	if (getRowCount() > 0) {
		ColumnDescription colDescription = getColumnDescriptions(c);
		if (colDescription instanceof FunctionColumnDescription){
			Expression exp = ((FunctionColumnDescription)colDescription).getExpression();
			//
			// must rebind expression due to transient nature of expression binding (see ASTIdNode.symbolTableEntry)
			//
			exp.bindExpression(getResultSetSymbolTableWithoutFunction());
			
			values = new double[getRowCount()];
			for (int r = 0; r < getRowCount(); r++) {
				try {
					values[r] = exp.evaluateVector(getRow(r));
				}catch (DivideByZeroException e){
					e.printStackTrace(System.out);
					values[r] = Double.NaN;
				}catch (FunctionDomainException e){
					e.printStackTrace(System.out);
					values[r] = Double.NaN;
				}
			}			
		}else{				
			values = new double[getRowCount()];
			for (int r = 0; r < getRowCount(); r++) {
				values[r] = getRow(r)[c];
			}
		}
	}
	return (values);
}


/**
 * getVariableNames method comment.
 */
public int findColumn(String columnName) {
	for (int i = 0; i < getColumnDescriptionsCount(); i++) {
		if (columnName.equals(getColumnDescriptions(i).getName())) return (i);
	}
	if (lg.isDebugEnabled()) {
		lg.debug("ODEIntegratorResultSet.findColumn() COULD NOT FIND : " + columnName);
	}
	return -1;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Gets the columnDescriptions property (cbit.vcell.util.ColumnDescription[]) value.
 * @return The columnDescriptions property value.
 * @see #setColumnDescriptions
 */
public final ColumnDescription[] getColumnDescriptions() {
	if (fieldColumnDescriptions == null) {
		fieldColumnDescriptions = new ColumnDescription[getDataColumnCount()+getFunctionColumnCount()];
		int index = 0;
		for (int i = 0; i < fieldDataColumnDescriptions.size(); i++){
			fieldColumnDescriptions[index++] = fieldDataColumnDescriptions.elementAt(i);
		}
		for (int i = 0; i < fieldFunctionColumnDescriptions.size(); i++){
			fieldColumnDescriptions[index++] = fieldFunctionColumnDescriptions.elementAt(i);
		}
	}
	return fieldColumnDescriptions;
}


/**
 * Gets the columnDescriptions index property (cbit.vcell.util.ColumnDescription) value.
 * @return The columnDescriptions property value.
 * @param index The index value into the property array.
 * @see #setColumnDescriptions
 */
public ColumnDescription getColumnDescriptions(int index) {
	if (index < fieldDataColumnDescriptions.size()) {
		return fieldDataColumnDescriptions.get(index);
	} else if (index < getColumnDescriptionsCount()) {
		return fieldFunctionColumnDescriptions.get(index - fieldDataColumnDescriptions.size());
	} else {
		throw new ArrayIndexOutOfBoundsException("RowColumnResultSet:getColumnDescriptions(int index), index=" + index + " total count=" + getColumnDescriptionsCount());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 11:48:56 AM)
 * @return int
 */
public int getColumnDescriptionsCount() {
	return fieldDataColumnDescriptions.size() + fieldFunctionColumnDescriptions.size();
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/2003 2:06:44 PM)
 * @return int
 */
public int getDataColumnCount() {
	return fieldDataColumnDescriptions.size();
}


/**
 * getVariableNames method comment.
 */
public ColumnDescription[] getDataColumnDescriptions() {
	return (ColumnDescription[])BeanUtils.getArray(fieldDataColumnDescriptions,ColumnDescription.class);
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/2003 2:06:44 PM)
 * @return int
 */
public int getFunctionColumnCount() {
	return fieldFunctionColumnDescriptions.size();
}


/**
 * getVariableNames method comment.
 */
public FunctionColumnDescription[] getFunctionColumnDescriptions() {
	return (FunctionColumnDescription[])BeanUtils.getArray(fieldFunctionColumnDescriptions,FunctionColumnDescription.class);
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/2003 3:18:26 PM)
 * @return double[]
 * @param row int
 */
public double[] getRow(int row) {
	return (double []) fieldValues.elementAt(row);
}


/**
 * getVariableNames method comment.
 */
public int getRowCount () {
	return (fieldValues.size());
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * getVariableNames method comment.
 */
private boolean isCorner(int t, double a[], double b[], double c[], double scale[], double minSquaredRatio, double maxSquaredRatio) {
	double c_minus_b_t = (c[t] - b[t])/scale[t];
    double b_minus_a_t = (b[t] - a[t])/scale[t];
    double c_minus_b_t_2 = c_minus_b_t*c_minus_b_t;
    double b_minus_a_t_2 = b_minus_a_t*b_minus_a_t;
    if (c[t] == b[t]){
	    return false;
    }
    //
    for (int i = 0; i < getDataColumnCount(); i++){
        if (i != t) {
            double c_minus_b_y = (c[i] - b[i])/scale[i];
            double b_minus_a_y = (b[i] - a[i])/scale[i];
            double ratio = (b_minus_a_y*b_minus_a_y + b_minus_a_t_2)/
						(c_minus_b_y*c_minus_b_y + c_minus_b_t_2);
			if (ratio < minSquaredRatio || ratio > maxSquaredRatio){
				if (lg.isDebugEnabled()) {
					lg.debug("corner with ratio = "+ratio+" at b[t]="+b[t]+", a["+i+"]="+a[i]+", b["+i+"]="+b[i]+", c["+i+"]="+c[i]);
				}
				return true;
			}
			if (lg.isDebugEnabled()) {
				lg.debug("NOT A CORNER with ratio = "+ratio+" at b[t]="+b[t]+", a["+i+"]="+a[i]+", b["+i+"]="+b[i]+", c["+i+"]="+c[i]);
			}
        }
    }
    return false;
}

/**
 * getVariableNames method comment.
 */
public void removeAllRows() {
	fieldValues.removeAllElements();
}


/**
 * getVariableNames method comment.
 *  THIS WILL LATER BE DELETED PROBABLY...THE COLUMNS
 *  WILL BE SPECIFIED AT CONSTRUCTION TIME!
 */
public final void removeFunctionColumn(FunctionColumnDescription functionColumnDescription) throws ExpressionException {

	//
	// Remove the corresponding FunctionColumnDescription from the fieldFunctionColumnDescriptions vector
	// and refresh column descriptions, which fires a property change event while changing the 
	// column descriptions array.
	//
	
	ColumnDescription[] oldValue = fieldColumnDescriptions;
	
	fieldFunctionColumnDescriptions.removeElement(functionColumnDescription);

	fieldColumnDescriptions = null;
	resultSetSymbolTableWithFunction = null;
	
	if (getPropertyChange().getPropertyChangeListeners().length > 0) {
		firePropertyChange("columnDescriptions", oldValue, getColumnDescriptions());
	}
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}

/**
 * getData method comment.
 */
public void setValue(int r, int c, double value) {
	double[] values = (double []) fieldValues.elementAt(r);
	values[c] = value;
}


/**
 * getVariableNames method comment.
 */
public synchronized void trimRows(int maxRowCount) {
	if (maxRowCount > getRowCount() - 1) {
		return; //nothing to do
	}
	if (lg.isInfoEnabled()) {
		lg.info("rowCount="+getRowCount());
	}
	if (maxRowCount <= 0) {
		throw new IllegalArgumentException("must keep at least one row");
	}
	//
	// identify appropriate scaling for time and each variable
	//
	double min[] = new double[getDataColumnCount()];
	double max[] = new double[getDataColumnCount()];
	double scale[] = new double[getDataColumnCount()];
	for (int i = 0; i < getDataColumnCount(); i++){
		min[i] = Double.MAX_VALUE;
		max[i] = -Double.MAX_VALUE;
	}
	for (int i = 0; i < getRowCount()-2; i++){
		double values[] = (double[])fieldValues.elementAt(i);
		for (int j = 0; j < getDataColumnCount(); j++){
			min[j] = Math.min(min[j],values[j]);
			max[j] = Math.max(max[j],values[j]);
		}	
	}
	for (int i = 0; i < getDataColumnCount(); i++){
		scale[i] = max[i]-min[i];
		if (scale[i] == 0){
			scale[i] = 1;
		}
		if (lg.isInfoEnabled()) {
			lg.info("scale["+i+"] = "+scale[i]);
		}
	}
	double threshold = 0.01;
	double minSquaredRatio = 0.1*0.1;
	double maxSquaredRatio = 10*10;
	int t = findColumn("t");
	final boolean haveT = t >= 0;
	double TOLERANCE = 0.1;
	LinkedList<double[]> linkedList = new LinkedList<double[]>(fieldValues);
	while (maxRowCount<linkedList.size() && threshold<TOLERANCE){
		ListIterator<double[]> iter = linkedList.listIterator(0);
		double a[] = iter.next();
		double b[] = iter.next();
		double c[] = null;
		while (iter.hasNext()) {
			c = iter.next();
			if (haveT && calculateErrorFactor(t,a,b,c,scale)<threshold && !isCorner(t,a,b,c,scale,minSquaredRatio,maxSquaredRatio)){
				iter.previous();
				iter.previous();
				iter.remove();
				if (iter.hasNext()){
					a = (double[])iter.next();
				}
				if (iter.hasNext()){
					b = (double[])iter.next();
				}
			}else{
				a = b;
				b = c;
			}
			if (c == linkedList.getLast()){
				break;
			}
		}
		if (lg.isInfoEnabled()) {
			lg.info("TOLERANCE="+TOLERANCE+", threshold="+threshold+", size="+linkedList.size());
		}
		threshold += TOLERANCE/10;
		if(threshold >= TOLERANCE){
			TOLERANCE*= 10;
			threshold = TOLERANCE/10;
		}
	}
	System.out.println("final tolerance="+TOLERANCE+" final threshold="+threshold+", "+linkedList.size()+" remaining (keepAtMost="+maxRowCount+")");
	if (linkedList.size()>maxRowCount){
		throw new RuntimeException("sample tolerance "+TOLERANCE+" exceeded while removing time points, "+linkedList.size()+" remaining (keepAtMost="+maxRowCount+")");
	}
	Vector<double[]> values = new Vector<double[]>(linkedList);
	fieldValues = values;
}


private VariableSymbolTable getResultSetSymbolTableWithFunction() {
	if (resultSetSymbolTableWithFunction == null) {
		resultSetSymbolTableWithFunction = createResultSetSymbolTable(true);
	}
	return resultSetSymbolTableWithFunction;
}


private VariableSymbolTable getResultSetSymbolTableWithoutFunction() {
	if (resultSetSymbolTableWithoutFunction == null) {
		resultSetSymbolTableWithoutFunction = createResultSetSymbolTable(false);
	}
	return resultSetSymbolTableWithoutFunction;
}
}
