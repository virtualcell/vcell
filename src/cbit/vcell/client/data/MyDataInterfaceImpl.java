package cbit.vcell.client.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.client.data.SimulationWorkspaceModelInfo.FilterCategoryType;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.gui.MyDataInterface;
import cbit.vcell.util.ColumnDescription;

public class MyDataInterfaceImpl implements MyDataInterface {
	
	private transient java.beans.PropertyChangeSupport propertyChange = new PropertyChangeSupport(this);
	private FilterCategoryType[] selectedFilters = null;
	private HashMap<ColumnDescription, FilterCategoryType> filterCategoryMap;
	private ODESolverResultSet odeSolverResultSet;
	
	public MyDataInterfaceImpl(ODESolverResultSet odeSolverResultSet,HashMap<ColumnDescription, FilterCategoryType> filterCategoryMap){
		this.odeSolverResultSet = new ODESolverResultSet(odeSolverResultSet);
		this.filterCategoryMap = filterCategoryMap;
	}
	
	private HashMap<ColumnDescription, FilterCategoryType> getFilterCategoryMap() {
		return filterCategoryMap;
	}

	private FilterCategoryType getFilterCategoryType(ColumnDescription columnDescription) {
		HashMap<ColumnDescription, FilterCategoryType> filterCategoryMap = getFilterCategoryMap();
		if(filterCategoryMap != null){
			return filterCategoryMap.get(columnDescription);
		}
		return null;
	}

	@Override
	public FilterCategoryType[] getSupportedFilterCategories() {
		HashMap<ColumnDescription, FilterCategoryType> filterCategoryMap = getFilterCategoryMap();
		if(filterCategoryMap != null){
			TreeSet<FilterCategoryType> uniqCategoryTypes = new TreeSet<SimulationWorkspaceModelInfo.FilterCategoryType>();
			uniqCategoryTypes.addAll(filterCategoryMap.values());
			return uniqCategoryTypes.toArray(new FilterCategoryType[0]);
		}
		return new FilterCategoryType[0];
	}

	@Override
	public void selectCategory(FilterCategoryType[] filterCategories) {
		this.selectedFilters = filterCategories;
		propertyChange.firePropertyChange("columnDescriptions", null, getFilteredColumnDescriptions());
	}

	@Override
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(listener);
	}

	@Override
	public int getColumnDescriptionsCount() {
		return getFilteredColumnDescriptions().length;
	}

	@Override
	public ColumnDescription getColumnDescription(String columnName) throws ObjectNotFoundException{
		ColumnDescription[] columnDescriptions = getOdeSolverResultSet().getColumnDescriptions();
		for (int j = 0; j < columnDescriptions.length; j++) {
			if(columnDescriptions[j].getName().equals(columnName)){
				return columnDescriptions[j];
			}
		}
		throw new ObjectNotFoundException("Couldn't find DolumnDescription="+columnName);
	}

	@Override
	public FunctionColumnDescription[] getFunctionColumnDescriptions() {
		ArrayList<FunctionColumnDescription> functionColumnDescriptions = new ArrayList<FunctionColumnDescription>();
		ColumnDescription[] filteredColumnDescriptions = getFilteredColumnDescriptions();
		for (int i = 0; i < filteredColumnDescriptions.length; i++) {
			if(filteredColumnDescriptions[i] instanceof FunctionColumnDescription){
				functionColumnDescriptions.add((FunctionColumnDescription)filteredColumnDescriptions[i]);
			}
		}
		return functionColumnDescriptions.toArray(new FunctionColumnDescription[0]);
	}

	@Override
	public double[] extractColumn(String columnName) throws ExpressionException,ObjectNotFoundException{
		for (int i = 0; i < getOdeSolverResultSet().getColumnDescriptionsCount(); i++) {
			if(getOdeSolverResultSet().getColumnDescriptions(i).getName().equals(columnName)){
				return getOdeSolverResultSet().extractColumn(i);
			}
		}
		throw new ObjectNotFoundException("Couldn't find column "+columnName);
	}

	@Override
	public boolean isMultiTrialData() {
		return getOdeSolverResultSet().isMultiTrialData();
	}

	@Override
	public int getRowCount() {
		return getOdeSolverResultSet().getRowCount();
	}

	@Override
	public ColumnDescription[] getAllColumnDescriptions() {
		return getOdeSolverResultSet().getColumnDescriptions();
	}
	
	@Override
	public ColumnDescription[] getFilteredColumnDescriptions() {
		if (selectedFilters == null){
			return getOdeSolverResultSet().getColumnDescriptions();
		}else{
			ArrayList<ColumnDescription> selectedColumnDescriptions = new ArrayList<ColumnDescription>();
			for (int i = 0; i < getOdeSolverResultSet().getColumnDescriptions().length; i++) {
				FilterCategoryType selectedFilterCategory = getFilterCategoryType(getOdeSolverResultSet().getColumnDescriptions()[i]);
				for (int j = 0; j < selectedFilters.length; j++) {
					if(selectedFilters[j].equals(selectedFilterCategory)){
						selectedColumnDescriptions.add(getOdeSolverResultSet().getColumnDescriptions()[i]);
						break;
					}
				}
			}
			return selectedColumnDescriptions.toArray(new ColumnDescription[0]);
		}
	}

	private ODESolverResultSet getOdeSolverResultSet() {
		return odeSolverResultSet;
	}

}
