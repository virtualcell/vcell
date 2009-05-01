package cbit.vcell.constraints;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
import java.util.Enumeration;
/**
 * Insert the type's description here.
 * Creation date: (6/25/01 4:40:26 PM)
 * @author: Jim Schaff
 */
import net.sourceforge.interval.ia_math.RealInterval;

public class ConstraintSolver implements cbit.vcell.parser.SymbolTable, java.beans.PropertyChangeListener {
	private java.util.Vector expressionList = new java.util.Vector();
	private java.util.Vector symbolList = new java.util.Vector();
	private ConstraintContainerImpl constraintContainerImpl = null;
	

	class Symbol implements cbit.vcell.parser.ConstraintSymbolTableEntry {
		int index = -1;
		String name = null;

		Symbol(String argName) {
			this.name = argName;
		}
		
		public boolean isConstant() { 
			return false; 
		}
		public cbit.vcell.parser.NameScope getNameScope() {
			return null;
		}
		public cbit.vcell.parser.Expression getExpression() { 
			throw new RuntimeException("not supported"); 
		}
		public String getName() { 
			return name; 
		}
		public boolean dontNarrow() {
			if (name.equals("x") || name.equals("y") || name.equals("z") || name.equals("t")){
				return true;
			}else{
				return false;
			}
		}
		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() { 
			return null; 
		}
		public int getIndex() { 
			return index; 
		}
		public double getConstantValue() throws cbit.vcell.parser.ExpressionException { 
			throw new cbit.vcell.parser.ExpressionException("not supported"); 
		}
		public String toString() {
			return "ConstraintSolver.Symbol(\""+name+"\", index="+index+")";
		}
	};
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private java.lang.String[] fieldSymbols = null;
	private net.sourceforge.interval.ia_math.RealInterval[] fieldIntervals = null;

/**
 * ConstraintContainer constructor comment.
 */
public ConstraintSolver(ConstraintContainerImpl argConstraintContainerImpl) throws cbit.vcell.parser.ExpressionBindingException {
	this.constraintContainerImpl = argConstraintContainerImpl;

	argConstraintContainerImpl.removePropertyChangeListener(this);
	argConstraintContainerImpl.addPropertyChangeListener(this);
	
	GeneralConstraint generalConstraints[] = argConstraintContainerImpl.getGeneralConstraints();
	for (int i = 0; i < generalConstraints.length; i++){
		generalConstraints[i].addPropertyChangeListener(this);
	}
	
	SimpleBounds simpleBounds[] = argConstraintContainerImpl.getSimpleBounds();
	for (int i = 0; i < simpleBounds.length; i++){
		simpleBounds[i].addPropertyChangeListener(this);
	}
	
	updateExpressions();
	resetIntervals();
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
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
 * Insert the method's description here.
 * Creation date: (5/15/2003 10:30:33 AM)
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
public ConstraintContainerImpl getConstraintContainerImpl() {
	return constraintContainerImpl;
}


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	
	cbit.vcell.parser.SymbolTableEntry ste = null;
	
	for (int i = 0; i < symbolList.size(); i++){
		ste = (cbit.vcell.parser.SymbolTableEntry)symbolList.elementAt(i);
		if (ste.getName().equals(identifierString)){
			return ste;
		}
	}

	ste = new ConstraintSolver.Symbol(identifierString);
	symbolList.add(ste);
	//
	// after adding new entry, sort the list in alphabetical order, and reassign indices
	//
	java.util.Collections.sort(symbolList,new java.util.Comparator() {
		public int compare(Object obj1, Object obj2){
			if (obj1 instanceof ConstraintSolver.Symbol && obj2 instanceof ConstraintSolver.Symbol){
				ConstraintSolver.Symbol symbol1 = (ConstraintSolver.Symbol)obj1;
				ConstraintSolver.Symbol symbol2 = (ConstraintSolver.Symbol)obj2;
				return symbol1.getName().compareTo(symbol2.getName());
			}else{
				throw new RuntimeException("cannot compare");
			}
		}
		public boolean equals(Object obj){
			if (obj == this){
				return true;
			}else{
				return false;
			}
		}
	});
	for (int i = 0; i < symbolList.size(); i++){
		((ConstraintSolver.Symbol)symbolList.elementAt(i)).index = i;
	}
	return ste;
}


/**
 * Gets the intervals property (net.sourceforge.interval.ia_math.RealInterval[]) value.
 * @return The intervals property value.
 * @see #setIntervals
 */
public net.sourceforge.interval.ia_math.RealInterval[] getIntervals() {
	return fieldIntervals;
}


/**
 * Gets the intervals index property (net.sourceforge.interval.ia_math.RealInterval) value.
 * @return The intervals property value.
 * @param index The index value into the property array.
 * @see #setIntervals
 */
public net.sourceforge.interval.ia_math.RealInterval getIntervals(int index) {
	return getIntervals()[index];
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
 * Gets the symbols property (java.lang.String[]) value.
 * @return The symbols property value.
 * @see #setSymbols
 */
public java.lang.String[] getSymbols() {
	return fieldSymbols;
}


/**
 * Gets the symbols index property (java.lang.String) value.
 * @return The symbols property value.
 * @param index The index value into the property array.
 * @see #setSymbols
 */
public java.lang.String getSymbols(int index) {
	return getSymbols()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:57:35 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 */
public cbit.vcell.parser.SymbolTableEntry[] getSymbolTableEntries() {
	return (ConstraintSolver.Symbol[])org.vcell.util.BeanUtils.getArray(symbolList,ConstraintSolver.Symbol.class);
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:45:31 PM)
 */
public boolean narrow() throws cbit.vcell.parser.ExpressionException {

	RealInterval prevValues[] = new RealInterval[fieldIntervals.length];
	for (int i = 0; i < prevValues.length; i++){
		prevValues[i] = (RealInterval)fieldIntervals[i].clone();
	}
	RealInterval values[] = new RealInterval[fieldIntervals.length];
	for (int i = 0; i < values.length; i++){
		values[i] = (RealInterval)fieldIntervals[i].clone();
	}
//System.out.println(constraintContainerImpl.toECLiPSe());
	//
	// clear inconsistent flag for all constraints
	//
	GeneralConstraint generalConstraints[] = constraintContainerImpl.getGeneralConstraints();
	for (int i = 0; i < generalConstraints.length; i++){
		constraintContainerImpl.setConsistent(generalConstraints[i],true);
	}
	int count=0;
	boolean bValuesChanged = true;
	boolean bValuesEverChanged = false;
	while (bValuesChanged){
		//System.out.println("narrowing ("+(count++)+")");
		
		for (int i=0;i<expressionList.size();i++){
			Expression exp = (Expression)expressionList.elementAt(i);
			if (!exp.narrow(values)){
				//
				// set failed constraint to inconsistent
				//
				int genConstraintIndex = 0;
				for (int j = 0; j < generalConstraints.length; j++){
					if (constraintContainerImpl.getActive(generalConstraints[j])){
						if (genConstraintIndex == i){
							constraintContainerImpl.setConsistent(generalConstraints[genConstraintIndex],false);
						}
					}
					genConstraintIndex++;
				}
				
				setIntervals(prevValues); // should we have this??
				return false;
			}
		}
		
		bValuesChanged = false;
		for (int i = 0; i < values.length; i++){
			if (!prevValues[i].equals(values[i])){
				bValuesChanged = true;
				bValuesEverChanged = true;
				prevValues[i] = (RealInterval)values[i].clone();
			}
		}

	}
	if (bValuesEverChanged){
		setIntervals(values);
	}		
	return true;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() instanceof GeneralConstraint && evt.getPropertyName().equals("active")){
		try {
			updateExpressions();
			resetIntervals();
		}catch (cbit.vcell.parser.ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
	}
	if (evt.getSource() instanceof SimpleBounds && evt.getPropertyName().equals("active")){
		try {
			updateExpressions();
			resetIntervals();
		}catch (cbit.vcell.parser.ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
	}
	if (evt.getSource() == constraintContainerImpl && evt.getPropertyName().equals("generalConstraints")){
		try {
			GeneralConstraint oldValues[] = (GeneralConstraint[])evt.getOldValue();
			for (int i = 0; oldValues != null && i < oldValues.length; i++){
				oldValues[i].removePropertyChangeListener(this);
			}
			GeneralConstraint newValues[] = (GeneralConstraint[])evt.getNewValue();
			for (int i = 0; newValues != null && i < newValues.length; i++){
				newValues[i].addPropertyChangeListener(this);
			}
			updateExpressions();
			resetIntervals();
		}catch (cbit.vcell.parser.ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
	}
	if (evt.getSource() == constraintContainerImpl && evt.getPropertyName().equals("simpleBounds")){
		try {
			SimpleBounds oldValues[] = (SimpleBounds[])evt.getOldValue();
			for (int i = 0; oldValues != null && i < oldValues.length; i++){
				oldValues[i].removePropertyChangeListener(this);
			}
			SimpleBounds newValues[] = (SimpleBounds[])evt.getNewValue();
			for (int i = 0; newValues != null && i < newValues.length; i++){
				newValues[i].addPropertyChangeListener(this);
			}
			updateExpressions();
			resetIntervals();
		}catch (cbit.vcell.parser.ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
	}
	if (evt.getSource() instanceof GeneralConstraint && evt.getPropertyName().equals("expression")){
		try {
			updateExpressions();
			resetIntervals();
		}catch (cbit.vcell.parser.ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
	}
	if (evt.getSource() instanceof SimpleBounds && evt.getPropertyName().equals("bounds")){
		try {
			updateExpressions();
			resetIntervals();
		}catch (cbit.vcell.parser.ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
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
 * Insert the method's description here.
 * Creation date: (6/25/01 4:50:43 PM)
 */
public void resetIntervals() throws cbit.vcell.parser.ExpressionBindingException {
	//
	// allocate RealInterval array (one for each bound symbol)
	//
//	if (fieldIntervals==null || fieldIntervals.length!=symbolList.size()){
		RealInterval[] intervals = new RealInterval[symbolList.size()];
//	}

	//
	// initialize all intervals to (-Infinity,Infinity)
	//
	for (int i = 0; i < intervals.length; i++){
		intervals[i] = RealInterval.fullInterval();
	}

	//
	// pre-load with simple bounds on interval symbols.
	//
	int symbolIndex = 0;
	java.util.Iterator iter = symbolList.iterator();
	while (iter.hasNext()){
		ConstraintSolver.Symbol symbol = (ConstraintSolver.Symbol)iter.next();
		symbol.index = symbolIndex++;
		RealInterval bounds = constraintContainerImpl.getBounds(symbol.getName());
		intervals[symbol.index] = (RealInterval)bounds.clone();
	}

	//
	// resets expression intervals (used in narrowing)
	//
	for (int i = 0; i < expressionList.size(); i++){
		((Expression)expressionList.elementAt(i)).bindExpression(null);
		((Expression)expressionList.elementAt(i)).bindExpression(this);
	}

	//
	// reset "Consistency" flag in general constraints
	//
	GeneralConstraint generalConstraints[] = constraintContainerImpl.getGeneralConstraints();
	for (int i = 0; i < generalConstraints.length; i++){
		constraintContainerImpl.setConsistent(generalConstraints[i],true);
	}
	
	setIntervals(intervals);
}


/**
 * Sets the intervals property (net.sourceforge.interval.ia_math.RealInterval[]) value.
 * @param intervals The new value for the property.
 * @see #getIntervals
 */
private void setIntervals(net.sourceforge.interval.ia_math.RealInterval[] intervals) {
	net.sourceforge.interval.ia_math.RealInterval[] oldValue = fieldIntervals;
	fieldIntervals = intervals;
	firePropertyChange("intervals", oldValue, intervals);
}


/**
 * Sets the symbols property (java.lang.String[]) value.
 * @param symbols The new value for the property.
 * @see #getSymbols
 */
private void setSymbols(java.lang.String[] symbols) {
	java.lang.String[] oldValue = fieldSymbols;
	fieldSymbols = symbols;
	firePropertyChange("symbols", oldValue, symbols);
}


/**
 * Insert the method's description here.
 * Creation date: (5/15/2003 6:17:15 AM)
 */
private void updateExpressions() throws cbit.vcell.parser.ExpressionBindingException {
	
	expressionList.clear();
	symbolList.clear();
	setSymbols(new String[0]);
	
	GeneralConstraint generalConstraints[] = constraintContainerImpl.getGeneralConstraints();
	for (int i = 0; i < generalConstraints.length; i++){
		if (constraintContainerImpl.getActive(generalConstraints[i])){
			Expression exp = new Expression(generalConstraints[i].getExpression().getBinaryExpression());
			exp.bindExpression(this);
			expressionList.add(exp);
		}
	}
	
	SimpleBounds simpleBounds[] = constraintContainerImpl.getSimpleBounds();
	for (int i = 0; i < simpleBounds.length; i++){
		//
		// preload symbolTable with simple bounds also (not necessary, but nice for display).
		//
		if (constraintContainerImpl.getActive(simpleBounds[i])){
			getEntry(simpleBounds[i].getIdentifier());
		}
	}

	String symbolNames[] = new String[symbolList.size()];
	for (int i = 0; i < symbolNames.length; i++){
		symbolNames[i] = ((cbit.vcell.parser.SymbolTableEntry)symbolList.elementAt(i)).getName();
	}
	setSymbols(symbolNames);
}
}