package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

public class OutputFunctionContext implements ScopedSymbolTable, Matchable, Serializable, VetoableChangeListener {
	
	public class OutputFunctionNameScope extends AbstractNameScope  {
		public OutputFunctionNameScope(){
			super();
		}

		@Override
		public NameScope[] getChildren() {
			return null;
		}

		@Override
		public String getName() {
			// return OutputFunctionContext.this.getName();
			return "OutputFunctionsContext";
		}

		@Override
		public NameScope getParent() {
			return null;
		}

		@Override
		public ScopedSymbolTable getScopedSymbolTable() {
			return OutputFunctionContext.this;
		}
	}

	private ArrayList<AnnotatedFunction> outputFunctionsList = new ArrayList<AnnotatedFunction>();
	private transient MathDescription mathDescription = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	private OutputFunctionNameScope nameScope = new OutputFunctionNameScope();
	
	public OutputFunctionContext() {
		super();
		addVetoableChangeListener(this);
	}

	public ArrayList<AnnotatedFunction> getOutputFunctionsList() {
		return outputFunctionsList;
	}

	public boolean compareEqual(Matchable obj){
		if (obj instanceof OutputFunctionContext) {
			if (!Compare.isEqualOrNull(outputFunctionsList, ((OutputFunctionContext)obj).outputFunctionsList)){
				return false;
			}
			return true;
		}
		return false;
	}

	public void refreshDependencies() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
	}
	
	public void addOutputFunction(AnnotatedFunction obsFunction) throws PropertyVetoException {
		if (obsFunction == null){
			return;
		}	
		ArrayList<AnnotatedFunction> newFunctionsList = new ArrayList<AnnotatedFunction>(outputFunctionsList);
		newFunctionsList.add(obsFunction);
		setOutputFunctionsList(newFunctionsList);
	}   

	public void removeOutputFunction(AnnotatedFunction obsFunction) throws PropertyVetoException {
		if (obsFunction == null){
			return;
		}	
		if (outputFunctionsList.contains(obsFunction)){
			ArrayList<AnnotatedFunction> newFunctionsList = new ArrayList<AnnotatedFunction>(outputFunctionsList);
			newFunctionsList.remove(obsFunction);
			setOutputFunctionsList(newFunctionsList);
		}
	}         

	public AnnotatedFunction getOutputFunction(String obsFunctionName){
		for (int i=0;i<outputFunctionsList.size();i++){
			AnnotatedFunction function = outputFunctionsList.get(i); 
			if (function.getName().equals(obsFunctionName)){
				return function;
			}
		}
		return null;
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent event) {
	}
	
	public void setOutputFunctionsList(ArrayList<AnnotatedFunction> outputFunctions) throws java.beans.PropertyVetoException {
		ArrayList<AnnotatedFunction> oldValue = outputFunctionsList;
		fireVetoableChange("outputFunctions", oldValue, outputFunctions);
		outputFunctionsList = outputFunctions;
		firePropertyChange("outputFunctions", oldValue, outputFunctions);
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
		getVetoPropertyChange().addVetoableChangeListener(listener);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
	 */
	public void fireVetoableChange(String propertyName, Object oldValue, Object newValue)
			throws PropertyVetoException {
				getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
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
	 * Accessor for the vetoPropertyChange field.
	 */
	protected VetoableChangeSupport getVetoPropertyChange() {
		if (vetoPropertyChange == null) {
			vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoPropertyChange;
	}

	/**
	 * The hasListeners method was generated to support the vetoPropertyChange field.
	 */
	public synchronized boolean hasListeners(String propertyName) {
		return getVetoPropertyChange().hasListeners(propertyName);
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	/**
	 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {
		getVetoPropertyChange().removeVetoableChangeListener(listener);
	}

	public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
		if (evt.getSource() == this && evt.getPropertyName().equals("outputFunctions")){
			ArrayList<AnnotatedFunction> newOutputFnsList = (ArrayList<AnnotatedFunction>)evt.getNewValue();
			if(newOutputFnsList == null){
				return;
			}
			//
			// while adding a function: check that names are not duplicated and that no common names are mathSymbols
			//
			HashSet<String> namesSet = new HashSet<String>();
			for (int i=0;i<newOutputFnsList.size();i++){
				String fnName = newOutputFnsList.get(i).getName();
				if (namesSet.contains(fnName)){
					throw new PropertyVetoException(" Cannot define multiple output functions with same name '"+fnName+"'.",evt);
				} 
				namesSet.add(fnName);
				// now see if the symbol is a math symbol - cannot use that name for new output function.
				try {
					SymbolTableEntry ste  = getEntry(fnName);
					if (ste != null) {
						if (!(ste instanceof AnnotatedFunction)) {
							throw new PropertyVetoException(" '"+fnName+"' conflicts with existing symbol. Cannot define function with name '" + fnName +".",evt);
						}
					}
				} catch (ExpressionBindingException e) {
					e.printStackTrace(System.out);
				}
			}

			// while deleting an output function: check if it present in expression of other output functions.
			ArrayList<AnnotatedFunction> oldOutputFnsList = (ArrayList<AnnotatedFunction>)evt.getOldValue();
			if (oldOutputFnsList.size() > newOutputFnsList.size()) {
				// if 'newOutputFnList' is smaller than 'oldOutputFnList', one of the functions has been removed, find the missing one
				AnnotatedFunction missingFn = null;
				for (int i = 0; i < oldOutputFnsList.size(); i++) {
					if (!newOutputFnsList.contains(oldOutputFnsList.get(i))) {
						missingFn = oldOutputFnsList.get(i);
					}
				}
				// use this missing output fn (to be deleted) to determine if it is used in any other output fn expressions. 
				if (missingFn != null) {
					// find out if the missing fn is used in other functions in outputFnsList
					Vector<String> referencingOutputFnsVector = new Vector<String>();
					for (int i = 0; i < newOutputFnsList.size(); i++) {
						AnnotatedFunction function = newOutputFnsList.get(i);
						if (function.getExpression().hasSymbol(missingFn.getName())) {
							referencingOutputFnsVector.add(function.getName());
						}
					}
					// if there are any output fns referencing the given 'missingFn', list them all in error msg.
					if (referencingOutputFnsVector.size() > 0) {
						String msg = "Output Function '" + missingFn.getName() + "' is used in expression of other output function(s): ";
						for (int i = 0; i < referencingOutputFnsVector.size(); i++) {
							msg = msg + "'" + referencingOutputFnsVector.elementAt(i) + "'";
							if (i < referencingOutputFnsVector.size()-1) {
								msg = msg + ", "; 
							} else {
								msg = msg + ". ";
							}
						}
						msg = msg + "\n\nCannot delete '" + missingFn.getName() + "'.";
						throw new PropertyVetoException(msg,evt);
					}
				}
			}
		}
	}
	
//	public abstract void validateNamingConflicts(String symbolDescription, Class<?> newSymbolClass, String newSymbolName, PropertyChangeEvent e)  throws PropertyVetoException ;

	public void getEntries(Map<String, SymbolTableEntry> entryMap) {	
		// add all valid entries (variables) from mathdescription
		if (mathDescription != null) {
			Enumeration<Variable> varEnum = mathDescription.getVariables();
			while(varEnum.hasMoreElements()) {
				Variable var = varEnum.nextElement();
				if (!(var instanceof PseudoConstant)) {
					entryMap.put(var.getName(), var);
				} 
			}
		}
		// then add list of output functions.
		for (SymbolTableEntry ste : outputFunctionsList) {
			entryMap.put(ste.getName(), ste);
		}
	}


	public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
		getEntries(entryMap);		
	}


	public SymbolTableEntry getLocalEntry(String identifier) throws ExpressionBindingException {
		return getEntry(identifier);
	}


	public NameScope getNameScope() {
		return nameScope;
	}

	public SymbolTableEntry getEntry(java.lang.String identifierString) throws ExpressionBindingException {
		//
		// use MathDescription as the primary SymbolTable, just replace the Constants with the overrides.
		//
		SymbolTableEntry ste = null;
		if (mathDescription != null) {
			ste = mathDescription.getEntry(identifierString);
			if (ste != null) {
				return ste;
			}
		}
		// see if it is an output function.
		ste = getOutputFunction(identifierString);
		return ste;
	}

	/** @deprecated
	 * 
	 * @return
	 */
	public MathDescription getMathDescription() {
		return mathDescription;
	}

	public void setMathDescription(MathDescription newValue) {
		mathDescription =  newValue;
	}
}
