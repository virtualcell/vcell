package cbit.vcell.client.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.client.data.SimulationWorkspaceModelInfo.DataSymbolMetadata;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo.FilterCategoryType;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.gui.MyDataInterface;
import cbit.vcell.util.ColumnDescription;

public class MyDataInterfaceImpl implements MyDataInterface {
	
	private transient java.beans.PropertyChangeSupport propertyChange = new PropertyChangeSupport(this);
	private FilterCategoryType[] selectedFilters = null;
	private final SimulationWorkspaceModelInfo simulationWorkspaceModelInfo;
	private ODESolverResultSet odeSolverResultSet;
	
	public MyDataInterfaceImpl(ODESolverResultSet odeSolverResultSet, SimulationWorkspaceModelInfo simulationWorkspaceModelInfo){
		this.odeSolverResultSet = new ODESolverResultSet(odeSolverResultSet);
		this.simulationWorkspaceModelInfo = simulationWorkspaceModelInfo;
	}
	
	@Override
	public FilterCategoryType[] getSupportedFilterCategories() {
		DataSymbolMetadataResolver resolver = (simulationWorkspaceModelInfo!=null)?simulationWorkspaceModelInfo.getDataSymbolMetadataResolver():null;
		if(resolver != null){
			return resolver.getUniqueFilterCategories();
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
		if (selectedFilters == null || simulationWorkspaceModelInfo == null){
			return getOdeSolverResultSet().getColumnDescriptions();
		}else{
			ArrayList<ColumnDescription> selectedColumnDescriptions = new ArrayList<ColumnDescription>();
			for (int i = 0; i < getOdeSolverResultSet().getColumnDescriptions().length; i++) {
				DataSymbolMetadata dataSymbolMetadata = simulationWorkspaceModelInfo.getDataSymbolMetadataResolver().getDataSymbolMetadata(getOdeSolverResultSet().getColumnDescriptions()[i].getName());
				FilterCategoryType selectedFilterCategory = null;
				if (dataSymbolMetadata!=null){
					selectedFilterCategory = dataSymbolMetadata.filterCategory;
				}
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

	@Override
	public DataSymbolMetadataResolver getDataSymbolMetadataResolver() {
		return (simulationWorkspaceModelInfo!=null?simulationWorkspaceModelInfo.getDataSymbolMetadataResolver():null);
	}

}
