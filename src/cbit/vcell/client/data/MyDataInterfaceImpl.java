package cbit.vcell.client.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.vcell.util.BeanUtils;

import cbit.vcell.math.MathDescription;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.ode.gui.MyDataInterface;
import cbit.vcell.solver.stoch.StochSolverResultSetColumnDescription;
import cbit.vcell.util.ColumnDescription;

public class MyDataInterfaceImpl implements MyDataInterface {
	
	private ODESolverResultSet odeSolverResultSet = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private FilterCategory[] selectedFilters = null;
	
	public class FilterCategoryImpl implements FilterCategory {
		private final String categoryName;
		public FilterCategoryImpl(String categoryName){
			this.categoryName = categoryName;
		}
		public String getName(){
			return this.categoryName;
		}
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof FilterCategory)){
				return false;
			}
			return getName().equals(((FilterCategory)obj).getName());
		}
		@Override
		public int hashCode() {
			return categoryName.hashCode();
		}
		
	}
	
//	private final FilterCategory[] filterCategories = new FilterCategory[]{
//		new FilterCategoryImpl("volume"),
//		new FilterCategoryImpl("user function"),
//		new FilterCategoryImpl("model function"),
//		new FilterCategoryImpl("reserved variable")
//	};
	
	public MyDataInterfaceImpl(ODESolverResultSet odeSolverResultSet){
		this.odeSolverResultSet = odeSolverResultSet;
		propertyChange = new PropertyChangeSupport(this);
	}

	private MathDescription mathDescription;
	public void setMathDescription(MathDescription mathDescription){
		this.mathDescription = mathDescription;
	}
	private FilterCategory getFilterCategory(ColumnDescription columnDescription){
		String displayName = null;
		if(columnDescription instanceof ODESolverResultSetColumnDescription){
			ODESolverResultSetColumnDescription odeSolverResultSetColumnDescription = (ODESolverResultSetColumnDescription)columnDescription;
			if(mathDescription.getVariable(columnDescription.getName()) != null){
				Variable var = mathDescription.getVariable(columnDescription.getName());
				displayName = var.getClass().getSimpleName();
			}else{
				if(ReservedVariable.TIME.getName().equals(columnDescription.getName()) ||
					ReservedVariable.X.getName().equals(columnDescription.getName()) ||
					ReservedVariable.Y.getName().equals(columnDescription.getName()) ||
					ReservedVariable.Z.getName().equals(columnDescription.getName())){
					displayName = FilterCategory.RESERVE_VAR_FILTER_CATEGORY;
				}else if(columnDescription.getName().equals(SimDataConstants.HISTOGRAM_INDEX_NAME)){
					displayName = SimDataConstants.HISTOGRAM_INDEX_NAME;
				}
//				else if(getPlot2D() != null){
//					for (int i = 0; i < getPlot2D().getSymbolTableEntries().length; i++) {
//						if(getPlot2D().getSymbolTableEntries()[i].getName().equals(columnDescription.getName())){
//							displayName = getPlot2D().getSymbolTableEntries()[i].getClass().getSimpleName();
//							break;
//						}
//					}
//				}
				if(displayName == null){
					displayName = "ODE Unknown";
				}
			}
		}else if(columnDescription instanceof FunctionColumnDescription){
			boolean bUser = ((FunctionColumnDescription)columnDescription).getIsUserDefined();
			displayName = "Function"+(bUser?" (user)":" (model)");
		}else if(columnDescription instanceof StochSolverResultSetColumnDescription){
			StochSolverResultSetColumnDescription stochSolverResultSetColumnDescription = (StochSolverResultSetColumnDescription)columnDescription;
			if(mathDescription.getVariable(columnDescription.getName()) != null){
				Variable var = mathDescription.getVariable(columnDescription.getName());
				displayName = "Stoch"+var.getClass().getSimpleName();
			}else{
				displayName = "Stoch Unknown";
			}
		}
		return new FilterCategoryImpl(displayName);
	}
	@Override
	public FilterCategory[] getSupportedFilterCategories() {
		ArrayList<FilterCategory> filterCategoryArrList = new ArrayList<MyDataInterface.FilterCategory>();
		for (int i = 0; i < odeSolverResultSet.getColumnDescriptionsCount(); i++) {
			ColumnDescription columnDescription = odeSolverResultSet.getColumnDescriptions(i);
			FilterCategory filterCategory = getFilterCategory(columnDescription);
			if(filterCategory.getName().equals(SimDataConstants.HISTOGRAM_INDEX_NAME)){
				//multitrial data have 'TrialNo' placeholder that is not displayed as plottable variable
				//so skip this.
				continue;
			}
			if(filterCategory.getName().equals(FilterCategory.RESERVE_VAR_FILTER_CATEGORY)){
				//ODESolverPlotSpecificationPanel non-multitrial data must have TIME reserved variable
				//so skip (don't give user chance to filter it off the display list)
				continue;
			}
			filterCategoryArrList.add(filterCategory);
		}
		return filterCategoryArrList.toArray(new FilterCategory[0]);
	}

	@Override
	public void selectCategory(FilterCategory[] filterCategories) {
		this.selectedFilters = filterCategories;
		propertyChange.firePropertyChange("columnDescriptions", null, getColumnDescriptions());
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
		return getColumnDescriptions().length;
//		if (selectedFilters == null || selectedFilters.length == 0){
//			return odeSolverResultSet.getColumnDescriptionsCount();
//		}else{
//			throw new RuntimeException("not yet implemented");
//		}
	}

	@Override
	public ColumnDescription getColumnDescriptions(int i) {
		return getColumnDescriptions()[i];
//		if (selectedFilters == null || selectedFilters.length == 0){
//			return odeSolverResultSet.getColumnDescriptions(i);
//		}else{
//			throw new RuntimeException("not yet implemented");
//		}
	}

	@Override
	public FunctionColumnDescription[] getFunctionColumnDescriptions() {
		ArrayList<FunctionColumnDescription> functionColumnDescriptions = new ArrayList<FunctionColumnDescription>();
		ColumnDescription[] filteredColumnDescriptions = getColumnDescriptions();
		for (int i = 0; i < filteredColumnDescriptions.length; i++) {
			if(filteredColumnDescriptions[i] instanceof FunctionColumnDescription){
				functionColumnDescriptions.add((FunctionColumnDescription)filteredColumnDescriptions[i]);
			}
		}
		return functionColumnDescriptions.toArray(new FunctionColumnDescription[0]);
		
//		if (selectedFilters == null || selectedFilters.length == 0){
//			return odeSolverResultSet.getFunctionColumnDescriptions();
//		}else{
//			throw new RuntimeException("not yet implemented");
//		}
	}

	@Override
	public int findColumn(String columnName) {
		for (int i = 0; i < getColumnDescriptionsCount(); i++) {
			if (columnName.equals(getColumnDescriptions(i).getName())) return (i);
		}
		return (-1);

//		if (selectedFilters == null || selectedFilters.length == 0){
//			return odeSolverResultSet.findColumn(sensParamName);
//		}else{
//			throw new RuntimeException("not yet implemented");
//		}
	}

	@Override
	public double[] extractColumn(int findColumn) throws ExpressionException {
		ColumnDescription filterColumnDescription = getColumnDescriptions(findColumn);
		for (int i = 0; i < odeSolverResultSet.getColumnDescriptionsCount(); i++) {
			if(filterColumnDescription.getName().equals(odeSolverResultSet.getColumnDescriptions(i).getName())){
				return odeSolverResultSet.extractColumn(i);
			}
		}
		throw new RuntimeException("Couldn't find column "+findColumn);
//		if (selectedFilters == null || selectedFilters.length == 0){
//			return odeSolverResultSet.extractColumn(findColumn);
//		}else{
//			throw new RuntimeException("not yet implemented");
//		}
	}

	@Override
	public boolean isMultiTrialData() {
		return odeSolverResultSet.isMultiTrialData();
//		if (selectedFilters == null || selectedFilters.length == 0){
//			return odeSolverResultSet.isMultiTrialData();
//		}else{
//			throw new RuntimeException("not yet implemented");
//		}
	}

	@Override
	public int getRowCount() {
		return odeSolverResultSet.getRowCount();
//		if (selectedFilters == null || selectedFilters.length == 0){
//			return odeSolverResultSet.getRowCount();
//		}else{
//			throw new RuntimeException("not yet implemented");
//		}
	}

	@Override
	public ColumnDescription[] getColumnDescriptions() {
		if (selectedFilters == null){
			return odeSolverResultSet.getColumnDescriptions();
		}else{
			ArrayList<ColumnDescription> selectedColumnDescriptions = new ArrayList<ColumnDescription>();
			for (int i = 0; i < odeSolverResultSet.getColumnDescriptions().length; i++) {
				if(getFilterCategory(odeSolverResultSet.getColumnDescriptions()[i]).getName().equals(FilterCategory.RESERVE_VAR_FILTER_CATEGORY)){
					selectedColumnDescriptions.add(odeSolverResultSet.getColumnDescriptions()[i]);
					continue;
				}
				FilterCategory selectedFilterCategory = getFilterCategory(odeSolverResultSet.getColumnDescriptions()[i]);
				for (int j = 0; j < selectedFilters.length; j++) {
					if(selectedFilters[j].equals(selectedFilterCategory)){
						selectedColumnDescriptions.add(odeSolverResultSet.getColumnDescriptions()[i]);
						break;
					}
				}
			}
			return selectedColumnDescriptions.toArray(new ColumnDescription[0]);
		}
	}

}
