package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.util.ArrayList;

import org.vcell.util.BeanUtils;

import cbit.vcell.document.SimulationOwner;

public abstract class AbstractSimulationOwner implements SimulationOwner {
	
	private ArrayList<Function> observableFunctionsList = new ArrayList<Function>();
	
	public ArrayList<Function> getObservableFunctionsList() {
		return observableFunctionsList;
	}
	

	public void addObservableFunction(Function obsFunction) throws PropertyVetoException {
		if (obsFunction == null){
			return;
		}	
		if (!observableFunctionsList.contains(obsFunction)){
			ArrayList<Function> newFunctionsList = new ArrayList<Function>(observableFunctionsList);
			newFunctionsList.add(obsFunction);
			setObservableFunctionsList(newFunctionsList);
		}	
	}   

	public void removeObservableFunction(Function obsFunction) throws PropertyVetoException {
		if (observableFunctionsList.contains(obsFunction)){
			ArrayList<Function> newObsFunctionsList = new ArrayList<Function>(observableFunctionsList);
			newObsFunctionsList.remove(obsFunction);
			setObservableFunctionsList(newObsFunctionsList);
		}
	}         

	public void replaceObservableFunction(Function oldObsFn, Function newObsFn) throws PropertyVetoException {
		if (!observableFunctionsList.contains(oldObsFn)){
			throw new RuntimeException("Function '" + oldObsFn.getName() + "' is not found in list of observable functions; cannot be replaced.");
		} else {
			ArrayList<Function> newObsFunctionsList = new ArrayList<Function>(observableFunctionsList);
			int oldIndx = newObsFunctionsList.indexOf(oldObsFn);
			if (oldIndx > -1) {
				newObsFunctionsList.remove(oldIndx);
				newObsFunctionsList.add(oldIndx, newObsFn);
				setObservableFunctionsList(newObsFunctionsList);
			}
		}
	}         

	public Function getObservableFunction(String obsFunctionName){
		for (int i=0;i<observableFunctionsList.size();i++){
			Function function = observableFunctionsList.get(i); 
			if (function.getName().equals(obsFunctionName)){
				return function;
			}
		}
		return null;
	}
	
	public abstract void fireVetoableChange(String name, Object oldValue, Object newValue) throws PropertyVetoException;
	public abstract void firePropertyChange(String name, Object oldValue, Object newValue);

	public void setObservableFunctionsList(ArrayList<Function> observableFunctions) throws java.beans.PropertyVetoException {
		if (observableFunctions==null){
			throw new RuntimeException("observable functions cannot be null");
		}
		ArrayList<Function> oldValue = observableFunctionsList;
		fireVetoableChange("observableFunctions", oldValue, observableFunctions);
		observableFunctionsList = observableFunctions;
		firePropertyChange("observableFunctions", oldValue, observableFunctions);

		observableFunctionsList = observableFunctions;
	}
	
}
