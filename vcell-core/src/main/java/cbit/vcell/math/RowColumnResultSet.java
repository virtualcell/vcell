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

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.math.Variable.Domain;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionDomainException;
import cbit.vcell.parser.VariableSymbolTable;
import cbit.vcell.util.ColumnDescription;
/**
 * This will have a list of Variables (NB: ReservedVariable.TIME is a ReservedVariable,
 * and a ReservedVariable is a Variable...also, StateVariables are NOT Variables, but
 * they are equivalent to them, and indeed are constructed from them.  So, the cols of
 * this class will be represented by a vector of Variables, and the rows will be a
 * vector of double[]...
 * This guy probably has some synchronization problems...
 */

/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:02 PM)
 *
 * @author: John Wagner
 */
@SuppressWarnings("serial")
public class RowColumnResultSet implements java.io.Serializable {
    private static final Logger lg = LogManager.getLogger(RowColumnResultSet.class);

    private Vector<ColumnDescription> dataColumnDescriptions;
    private Vector<FunctionColumnDescription> functionColumnDescriptions;
    private ArrayList<double[]> values;  // vector of rows (each row is a double[])
    private ColumnDescription[] columnDescriptions;

    protected transient java.beans.PropertyChangeSupport propertyChange;
    private transient VariableSymbolTable resultSetSymbolTableWithFunction = null;
    private transient VariableSymbolTable resultSetSymbolTableWithoutFunction = null;


    /**
     * construct empty, add columns via {@link #addDataColumn(ColumnDescription)} et. al. after creation
     */
    public RowColumnResultSet() {
        this(new Vector<>(), new Vector<>(), new ArrayList<>(), null);
    }

    public RowColumnResultSet(RowColumnResultSet copyThisRowColumnResultSet) {
        this(new Vector<>(copyThisRowColumnResultSet.dataColumnDescriptions),
                new Vector<>(copyThisRowColumnResultSet.functionColumnDescriptions),
                new ArrayList<>(copyThisRowColumnResultSet.values),
                null);
    }

    private RowColumnResultSet(Vector<ColumnDescription> dataColumnDescriptions,
                               Vector<FunctionColumnDescription> functionColumnDescriptions,
                               ArrayList<double[]> values,
                               ColumnDescription[] columnDescriptions) {
        this.dataColumnDescriptions = dataColumnDescriptions;
        this.functionColumnDescriptions = functionColumnDescriptions;
        this.values = values;
        this.columnDescriptions = columnDescriptions;
    }

    public enum DuplicateMode {
        CopyValues,
        ZeroInitialize
    }

    public static RowColumnResultSet deepCopy(RowColumnResultSet original, DuplicateMode mode) {
        RowColumnResultSet copy = new RowColumnResultSet();
        copy.dataColumnDescriptions = new Vector<>(original.dataColumnDescriptions);
        copy.functionColumnDescriptions = new Vector<>(original.functionColumnDescriptions);
        copy.values = new ArrayList<>();
        for (double[] originalRow : original.values) {
            double[] copyRow = new double[originalRow.length];
            if (mode == DuplicateMode.CopyValues) {
                System.arraycopy(originalRow, 0, copyRow, 0, originalRow.length);
            }
            copy.values.add(copyRow);
        }


        return copy;
    }

    /**
     * SimpleODEData constructor comment.
     * JMW : THIS NEEDS TO BE FIXED...THIS CONSTRUCTOR SHOULD NOT
     * BE DOING ANY COLUMN CONSTRUCTION AT ALL!
     */
    public RowColumnResultSet(String[] dataColumnNames) {
        this();
        for (String dataColumnName : dataColumnNames) {
            this.addDataColumn(new ODESolverResultSetColumnDescription(dataColumnName));
        }
    }

    /**
     * getVariableNames method comment.
     * THIS WILL LATER BE DELETED PROBABLY...THE COLUMNS
     * WILL BE SPECIFIED AT CONSTRUCTION TIME!
     */
    public final void addDataColumn(ColumnDescription columnDescription) {
        // cbit.util.Assertion.assert(getRowCount() == 0);
        ColumnDescription[] oldValue = this.columnDescriptions;
        this.dataColumnDescriptions.addElement(columnDescription);

        this.columnDescriptions = null;
        this.resultSetSymbolTableWithFunction = null;
        this.resultSetSymbolTableWithoutFunction = null;

        if (this.getPropertyChange().getPropertyChangeListeners().length > 0) {
            this.firePropertyChange("columnDescriptions", oldValue, this.getColumnDescriptions());
        }
    }

    /**
     * getVariableNames method comment.
     * THIS WILL LATER BE DELETED PROBABLY...THE COLUMNS
     * WILL BE SPECIFIED AT CONSTRUCTION TIME!
     */
    public final void addFunctionColumn(FunctionColumnDescription functionColumnDescription) throws ExpressionException {
        ColumnDescription[] oldValue = this.columnDescriptions;
        this.addFunctionColumnInternal(functionColumnDescription);
        if (this.getPropertyChange().getPropertyChangeListeners().length > 0) {
            this.firePropertyChange("columnDescriptions", oldValue, this.getColumnDescriptions());
        }
    }

    /**
     * getVariableNames method comment.
     * THIS WILL LATER BE DELETED PROBABLY...THE COLUMNS
     * WILL BE SPECIFIED AT CONSTRUCTION TIME!
     */
    private void addFunctionColumnInternal(FunctionColumnDescription functionColumnDescription) throws ExpressionException {
        //
        // bind and substitute functions (resulting in expressions of only data columns).
        //
        functionColumnDescription.getExpression().bindExpression(this.getResultSetSymbolTableWithFunction());
        Expression exp1 = MathUtilities.substituteFunctions(functionColumnDescription.getExpression(), this.getResultSetSymbolTableWithFunction());
        functionColumnDescription.setExpression(exp1.flatten());

        //
        // didn't throw a binding exception, add to result set.
        //
        this.functionColumnDescriptions.addElement(functionColumnDescription);

        Domain domain = null; //TODO domain
        Function func = new Function(functionColumnDescription.getName(), new Expression(functionColumnDescription.getExpression()), domain);
        this.getResultSetSymbolTableWithFunction().addVar(func);
        func.setIndex(this.getColumnDescriptionsCount() - 1);

        this.columnDescriptions = null;
    }

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        this.getPropertyChange().addPropertyChangeListener(listener);
    }

    /**
     * getVariableNames method comment.
     */
    public synchronized void addRow(double[] values) {
        // cbit.util.Assertion.assert(values.length == getDataColumnCount());
        int dataColumnCount = this.getDataColumnCount();
        double[] v = new double[dataColumnCount];
        if (values.length != dataColumnCount) {
            throw new RuntimeException("number of values in row is not equal to number of columns");
        }
        System.arraycopy(values, 0, v, 0, dataColumnCount);
        this.values.add(v);
    }


    /**
     * getVariableNames method comment.
     */
    private double calculateErrorFactor(int t, double[] a, double[] b, double[] c, double[] scale) {
        double errorFactor = 0.0;
        // we need to average coming into the point otherwise funny things happen in systems close to equilibrium situations
        double c_minus_a_t = (c[t] - a[t]) / scale[t];
        double b_minus_a_t = (b[t] - a[t]) / scale[t];
        double c_minus_a_t_2 = c_minus_a_t * c_minus_a_t;
        if (c[t] == b[t]) return 1.0;

        // dataColumnDescriptions
        for (int i = 0; i < this.getDataColumnCount(); i++) {
            if (i == t) continue;
            double c_minus_a_y = (c[i] - a[i]) / scale[i];
            double F = Math.abs((b_minus_a_t) * (c_minus_a_y) - ((b[i] - a[i]) / scale[i]) * (c_minus_a_t)) /
                    (c_minus_a_t_2 + c_minus_a_y * c_minus_a_y);
            errorFactor = Math.max(errorFactor, F);
        }
        return errorFactor;
    }


    /**
     * checkFunctionValidity method
     * This method is used to check if a user defined function expression is valid.
     * Takes a FunctionColumnDescription as argument. It substitutes the functions and binds the expression of the new function
     * to check the functions validity. If it is not valid, it throws an ExpressionException, which is caught and handled by
     * by the addFunction method in ODESolverPlotSpecificationPanel.
     */
    public void checkFunctionValidity(FunctionColumnDescription fcd) throws ExpressionException {

        if (this.getRowCount() <= 0) return;
        Expression exp = fcd.getExpression();
        //
        // must rebind expression due to transient nature of expression binding (see ASTIdNode.symbolTableEntry)
        //
        exp.bindExpression(this.getResultSetSymbolTableWithFunction());
        Expression exp1 = MathUtilities.substituteFunctions(exp, this.getResultSetSymbolTableWithFunction());
        for (double[] row : this.getRows()) {
            exp1.evaluateVector(row);
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (2/19/2003 4:32:00 PM)
     *
     * @return cbit.vcell.parser.SymbolTable
     */
    private VariableSymbolTable createResultSetSymbolTable(boolean bIncludeFunctions) {
        //
        // create symbol table for binding expression against data columns and functions (of data columns)
        //
        VariableSymbolTable resultSetSymbolTable = new VariableSymbolTable();
        for (int i = 0; i < this.getColumnDescriptionsCount(); i++) {
            ColumnDescription colDesc = this.getColumnDescriptions(i);
            boolean isValidColumnDesc = colDesc instanceof ODESolverResultSetColumnDescription
                    || (colDesc instanceof FunctionColumnDescription && bIncludeFunctions);
            if (!isValidColumnDesc) continue;
            Domain domain = null;
            Variable var = colDesc instanceof FunctionColumnDescription funcColDesc ?
                    new Function(funcColDesc.getName(), new Expression(funcColDesc.getExpression()), domain):
                    new VolVariable(colDesc.getName(), domain);
            var.setIndex(i);
            resultSetSymbolTable.addVar(var);
        }
        return resultSetSymbolTable;
    }


    /**
     * getVariableNames method comment.
     * If column is empty, return null or an empty array?
     * For now, null...
     */
    public synchronized double[] extractColumn(int c) throws ExpressionException {
        if (this.getRowCount() <= 0) return null;
        double[] values = new double[this.getRowCount()];
        Expression exp;
        ColumnDescription colDesc = this.getColumnDescriptions(c);
        if (colDesc instanceof FunctionColumnDescription funcColDesc){
            exp = funcColDesc.getExpression();
            exp.bindExpression(this.getResultSetSymbolTableWithoutFunction()); // must rebind expression due to transient nature of expression binding (see ASTIdNode.symbolTableEntry)
        } else exp = null;
        for (int r = 0; r < this.getRowCount(); r++) {
            try {
                values[r] = !(colDesc instanceof FunctionColumnDescription) ? this.getRow(r)[c] : exp.evaluateVector(this.getRow(r));
            } catch (ExpressionException e) {
                String errMsg = String.format("%s encountered; setting value to NaN: exp = `%s`", e.getClass().getSimpleName(), exp.infix());
                lg.warn(errMsg, e);
                values[r] = Double.NaN;
            }
        }
        return values;
    }


    /**
     * Attempts to find the index of the column with the provided name
     *
     * @param columnName the name to look for
     * @return the index of the desired column, or -1 if it's not found
     */
    public int findColumn(String columnName) {
        for (int i = 0; i < this.getColumnDescriptionsCount(); i++) {
            if (columnName.equals(this.getColumnDescriptions(i).getName())) return (i);
        }
        lg.debug("Could not find `{}` in results set.", columnName);

        return -1;
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
        this.getPropertyChange().firePropertyChange(evt);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
        this.getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
        this.getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
        this.getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }


    public final ColumnDescription[] getColumnDescriptions() {
        if (this.columnDescriptions != null) return this.columnDescriptions;
        this.columnDescriptions = new ColumnDescription[this.getDataColumnCount() + this.getFunctionColumnCount()];
        int index = 0;
        for (int i = 0; i < this.dataColumnDescriptions.size(); i++) {
            this.columnDescriptions[index++] = this.dataColumnDescriptions.elementAt(i);
        }
        for (int i = 0; i < this.functionColumnDescriptions.size(); i++) {
            this.columnDescriptions[index++] = this.functionColumnDescriptions.elementAt(i);
        }
        return this.columnDescriptions;
    }


    public ColumnDescription getColumnDescriptions(int index) {
        if (index < this.dataColumnDescriptions.size()) {
            return this.dataColumnDescriptions.get(index);
        } else if (index < this.getColumnDescriptionsCount()) {
            return this.functionColumnDescriptions.get(index - this.dataColumnDescriptions.size());
        } else {
            throw new ArrayIndexOutOfBoundsException("RowColumnResultSet:getColumnDescriptions(int index), index=" + index + " total count=" + this.getColumnDescriptionsCount());
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/16/2003 11:48:56 AM)
     *
     * @return int
     */
    public int getColumnDescriptionsCount() {
        return this.dataColumnDescriptions.size() + this.functionColumnDescriptions.size();
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/9/2003 2:06:44 PM)
     *
     * @return int
     */
    public int getDataColumnCount() {
        return this.dataColumnDescriptions.size();
    }


    /**
     * getVariableNames method comment.
     */
    public ColumnDescription[] getDataColumnDescriptions() {
        return this.dataColumnDescriptions.toArray(ColumnDescription[]::new);
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/9/2003 2:06:44 PM)
     *
     * @return int
     */
    public int getFunctionColumnCount() {
        return this.functionColumnDescriptions.size();
    }


    /**
     * getVariableNames method comment.
     */
    public FunctionColumnDescription[] getFunctionColumnDescriptions() {
        return this.functionColumnDescriptions.toArray(FunctionColumnDescription[]::new);
    }


    /**
     * Accessor for the propertyChange field.
     */
    protected java.beans.PropertyChangeSupport getPropertyChange() {
        if (this.propertyChange == null) this.propertyChange = new java.beans.PropertyChangeSupport(this);
        return this.propertyChange;
    }


    /**
     * Insert the method's description here.
     * Creation date: (1/9/2003 3:18:26 PM)
     *
     * @param row int
     * @return double[]
     */
    public double[] getRow(int row) {
        return this.values.get(row);
    }

    public List<double[]> getRows() {
        return Collections.unmodifiableList(this.values);
    }


    /**
     * getVariableNames method comment.
     */
    public int getRowCount() {
        return (this.values.size());
    }


    /**
     * The hasListeners method was generated to support the propertyChange field.
     */
    public synchronized boolean hasListeners(java.lang.String propertyName) {
        return this.getPropertyChange().hasListeners(propertyName);
    }


    /**
     * getVariableNames method comment.
     */
    private boolean isCorner(int t, double[] a, double[] b, double[] c, double[] scale, double minSquaredRatio, double maxSquaredRatio) {
        double c_minus_b_t = (c[t] - b[t]) / scale[t];
        double b_minus_a_t = (b[t] - a[t]) / scale[t];
        double c_minus_b_t_2 = c_minus_b_t * c_minus_b_t;
        double b_minus_a_t_2 = b_minus_a_t * b_minus_a_t;
        if (c[t] == b[t]) return false;

        // find corner
        for (int i = 0; i < this.getDataColumnCount(); i++) {
            if (i == t) continue;
            double c_minus_b_y = (c[i] - b[i]) / scale[i];
            double b_minus_a_y = (b[i] - a[i]) / scale[i];
            double ratio = (b_minus_a_y * b_minus_a_y + b_minus_a_t_2) /
                    (c_minus_b_y * c_minus_b_y + c_minus_b_t_2);
            String ratioString = "with ratio = " + ratio + " at b[t]=" + b[t] + ", a[" + i + "]=" + a[i] + ", b[" + i + "]=" + b[i] + ", c[" + i + "]=" + c[i];
            boolean isCornered = ratio < minSquaredRatio || ratio > maxSquaredRatio;
            lg.debug("{} {}", isCornered ? "corner" : "NOT A CORNER", ratioString);
            if (!isCornered) continue;
            return true;
        }
        return false;
    }

    /**
     * getVariableNames method comment.
     */
    public void removeAllRows() {
        this.values.clear();
    }


    /**
     * getVariableNames method comment.
     * THIS WILL LATER BE DELETED PROBABLY...THE COLUMNS
     * WILL BE SPECIFIED AT CONSTRUCTION TIME!
     */
    public final void removeFunctionColumn(FunctionColumnDescription functionColumnDescription) throws ExpressionException {

        //
        // Remove the corresponding FunctionColumnDescription from the fieldFunctionColumnDescriptions vector
        // and refresh column descriptions, which fires a property change event while changing the
        // column descriptions array.
        //

        ColumnDescription[] oldValue = this.columnDescriptions;

        this.functionColumnDescriptions.removeElement(functionColumnDescription);

        this.columnDescriptions = null;
        this.resultSetSymbolTableWithFunction = null;

        if (this.getPropertyChange().getPropertyChangeListeners().length > 0) {
            this.firePropertyChange("columnDescriptions", oldValue, this.getColumnDescriptions());
        }
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        this.getPropertyChange().removePropertyChangeListener(listener);
    }


    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
        this.getPropertyChange().removePropertyChangeListener(propertyName, listener);
    }

    /**
     * getData method comment.
     */
    public void setValue(int r, int c, double value) {
        double[] values = this.values.get(r);
        values[c] = value;
    }


    /**
     * getVariableNames method comment.
     */
    public synchronized void trimRows(int maxRowCount) {
        if (maxRowCount > this.getRowCount() - 1) return; //nothing to do

        if (lg.isDebugEnabled()) lg.info("rowCount={}", this.getRowCount());
        if (maxRowCount <= 0) throw new IllegalArgumentException("must keep at least one row");

        //
        // identify appropriate scaling for time and each variable
        //
        double[] min = new double[this.getDataColumnCount()];
        double[] max = new double[this.getDataColumnCount()];
        double[] scale = new double[this.getDataColumnCount()];
        for (int i = 0; i < this.getDataColumnCount(); i++) {
            min[i] = Double.MAX_VALUE;
            max[i] = -Double.MAX_VALUE;
        }
        for (int i = 0; i < this.getRowCount() - 2; i++) {
            double[] values = this.values.get(i);
            for (int j = 0; j < this.getDataColumnCount(); j++) {
                min[j] = Math.min(min[j], values[j]);
                max[j] = Math.max(max[j], values[j]);
            }
        }
        for (int i = 0; i < this.getDataColumnCount(); i++) {
            scale[i] = max[i] - min[i];
            if (scale[i] == 0) {
                scale[i] = 1;
            }
            if (lg.isDebugEnabled()) {
                lg.info("scale[{}] = {}", i, scale[i]);
            }
        }
        double threshold = 0.01;
        double minSquaredRatio = 0.1 * 0.1;
        double maxSquaredRatio = 10 * 10;
        int t = this.findColumn("t");
        final boolean haveT = t >= 0;
        double TOLERANCE = 0.1;
        LinkedList<double[]> linkedList = new LinkedList<>(this.values);
        while (maxRowCount < linkedList.size() && threshold < TOLERANCE) {
            ListIterator<double[]> iter = linkedList.listIterator(0);
            double[] a = iter.next();
            double[] b = iter.next();
            double[] c;
            while (iter.hasNext()) {
                c = iter.next();
                if (haveT && this.calculateErrorFactor(t, a, b, c, scale) < threshold && !this.isCorner(t, a, b, c, scale, minSquaredRatio, maxSquaredRatio)) {
                    iter.previous();
                    iter.previous();
                    iter.remove();
                    if (iter.hasNext()) {
                        a = iter.next();
                    }
                    if (iter.hasNext()) {
                        b = iter.next();
                    }
                } else {
                    a = b;
                    b = c;
                }
                if (c == linkedList.getLast()) {
                    break;
                }
            }
            if (lg.isDebugEnabled()) {
                lg.info("TOLERANCE={}, threshold={}, size={}", TOLERANCE, threshold, linkedList.size());
            }
            threshold += TOLERANCE / 10;
            if (threshold >= TOLERANCE) {
                TOLERANCE *= 10;
                threshold = TOLERANCE / 10;
            }
        }
        lg.trace("final tolerance={} final threshold={}, {} remaining (keepAtMost={})", TOLERANCE, threshold, linkedList.size(), maxRowCount);
        ArrayList<double[]> values = new ArrayList<>();
        if (linkedList.size() > maxRowCount) {//just sample list evenly in this case
            values.add(this.values.get(0));//Add first value
            if (maxRowCount > 2) {//Add values between first and last
                for (int i = 1; i < (maxRowCount - 1); i++) {
                    values.add(this.values.get(i * this.values.size() / (maxRowCount - 1)));
                }
            }
            if (maxRowCount > 1) {//Add last value
                values.add(this.values.get(this.values.size() - 1));
            }
//		throw new RuntimeException("sample tolerance "+TOLERANCE+" exceeded while removing time points, "+linkedList.size()+" remaining (keepAtMost="+maxRowCount+")");
        } else {
            values.addAll(linkedList);
        }
        this.values = values;
    }


    private VariableSymbolTable getResultSetSymbolTableWithFunction() {
        if (this.resultSetSymbolTableWithFunction == null) {
            this.resultSetSymbolTableWithFunction = this.createResultSetSymbolTable(true);
        }
        return this.resultSetSymbolTableWithFunction;
    }


    private VariableSymbolTable getResultSetSymbolTableWithoutFunction() {
        if (this.resultSetSymbolTableWithoutFunction == null) {
            this.resultSetSymbolTableWithoutFunction = this.createResultSetSymbolTable(false);
        }
        return this.resultSetSymbolTableWithoutFunction;
    }
}
