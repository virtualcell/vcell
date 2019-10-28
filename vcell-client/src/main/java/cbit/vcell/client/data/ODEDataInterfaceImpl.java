package cbit.vcell.client.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DataSymbolMetadata;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.solver.SimulationModelInfo.ModelCategoryType;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;

class ODEDataInterfaceImpl implements ODEDataInterface {
	
	private transient java.beans.PropertyChangeSupport propertyChange = new PropertyChangeSupport(this);
	private ModelCategoryType[] selectedFilters = null;
	private final SimulationModelInfo simulationModelInfo;
	private ODESolverResultSet odeSolverResultSet;
	
	ODEDataInterfaceImpl(ODESolverResultSet odeSolverResultSet, SimulationModelInfo simulationModelInfo){
		this.odeSolverResultSet = new ODESolverResultSet(odeSolverResultSet);
		this.simulationModelInfo = simulationModelInfo;
	}
	
	@Override
	public ModelCategoryType[] getSupportedFilterCategories() {
		DataSymbolMetadataResolver resolver = (simulationModelInfo!=null)?simulationModelInfo.getDataSymbolMetadataResolver():null;
		if(resolver != null){
			return resolver.getUniqueFilterCategories();
		}
		return new ModelCategoryType[0];
	}

	@Override
	public void selectCategory(ModelCategoryType[] filterCategories) {
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
		if (selectedFilters == null || simulationModelInfo == null){
			return getOdeSolverResultSet().getColumnDescriptions();
		}else{
			ArrayList<ColumnDescription> selectedColumnDescriptions = new ArrayList<ColumnDescription>();
			for (int i = 0; i < getOdeSolverResultSet().getColumnDescriptions().length; i++) {
				String name = getOdeSolverResultSet().getColumnDescriptions()[i].getName();
				DataSymbolMetadataResolver dataSymbolMetadataResolver = simulationModelInfo.getDataSymbolMetadataResolver();
				DataSymbolMetadata dataSymbolMetadata = dataSymbolMetadataResolver.getDataSymbolMetadata(name);
				ModelCategoryType selectedFilterCategory = null;
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
		return (simulationModelInfo!=null?simulationModelInfo.getDataSymbolMetadataResolver():null);
	}

}
