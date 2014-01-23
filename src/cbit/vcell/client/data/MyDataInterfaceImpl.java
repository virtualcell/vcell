package cbit.vcell.client.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeSet;

import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.client.data.SimulationWorkspaceModelInfo.FilterCategoryType;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.gui.MyDataInterface;
import cbit.vcell.util.ColumnDescription;

public class MyDataInterfaceImpl implements MyDataInterface {
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private FilterCategoryType[] selectedFilters = null;
	private ODEDataViewer odeDataViewer;
	
	private PropertyChangeListener myPropertyChangeListener =
			new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
//					System.out.println("-----"+MyDataInterfaceImpl.class.getSimpleName()+": "+evt.getPropertyName()+" "+evt.getSource().getClass().getSimpleName());
					if( (evt.getSource() == getOdeSolverResultSet() && evt.getPropertyName().equals("columnDescriptions")) ||
						(evt.getSource() == odeDataViewer && evt.getPropertyName().equals(DataViewer.PROP_SIM_MODEL_INFO))){
						resetFilterCategories();
					}
				}
			};
	public MyDataInterfaceImpl(){
		propertyChange = new PropertyChangeSupport(this);
	}

	public void setODEDataViewer(ODEDataViewer newODEDataViewer){
		if(this.odeDataViewer != null){
			this.odeDataViewer.removePropertyChangeListener(myPropertyChangeListener);
		}
		this.odeDataViewer = newODEDataViewer;
		if(this.odeDataViewer != null){
			this.odeDataViewer.addPropertyChangeListener(myPropertyChangeListener);
		}
		resetFilterCategories();
	}
	private void resetFilterCategories(){
		if(this.odeDataViewer == null ||
			this.odeDataViewer.getSimulationModelInfo() == null ||
			!(this.odeDataViewer.getSimulationModelInfo() instanceof SimulationWorkspaceModelInfo)){
			return;
		}
		AsynchClientTask filterCategoriesTask = new AsynchClientTask("Calculating Filter...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				filterCategoryMap =
					((SimulationWorkspaceModelInfo)odeDataViewer.getSimulationModelInfo()).getFilterCategories(
						getOdeSolverResultSet().getColumnDescriptions());
			}
		};
		AsynchClientTask firePropertyChangeTask = new AsynchClientTask("Fire Property Change...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				propertyChange.firePropertyChange("columnDescriptions", null, getFilteredColumnDescriptions());
			}
		};
		ClientTaskDispatcher.dispatch(odeDataViewer, new Hashtable<String, Object>(),
				new AsynchClientTask[] {filterCategoriesTask,firePropertyChangeTask},
				false, false, false, null, true);
	}
	
	private HashMap<ColumnDescription, FilterCategoryType> filterCategoryMap;
	private HashMap<ColumnDescription, FilterCategoryType> getFilterCategoryMap() {
		return filterCategoryMap;
	}

	private FilterCategoryType getFilterCategory(ColumnDescription columnDescription) {
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
	public ColumnDescription[] getXColumnDescriptions() {
		return getOdeSolverResultSet().getColumnDescriptions();
	}
	
	@Override
	public ColumnDescription[] getFilteredColumnDescriptions() {
		if (selectedFilters == null){
			return getOdeSolverResultSet().getColumnDescriptions();
		}else{
			ArrayList<ColumnDescription> selectedColumnDescriptions = new ArrayList<ColumnDescription>();
			for (int i = 0; i < getOdeSolverResultSet().getColumnDescriptions().length; i++) {
				FilterCategoryType selectedFilterCategory = getFilterCategory(getOdeSolverResultSet().getColumnDescriptions()[i]);
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
		if(this.odeDataViewer == null){
			return null;
		}
		return odeDataViewer.getOdeSolverResultSet();
	}

}
