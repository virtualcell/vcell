package cbit.vcell.solver.ode.gui;

import java.beans.PropertyChangeListener;

import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.client.data.SimulationWorkspaceModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo.FilterCategoryType;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.util.ColumnDescription;

public interface MyDataInterface {

	void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

	void addPropertyChangeListener(PropertyChangeListener ivjEventHandler);

	int getColumnDescriptionsCount();

	ColumnDescription getColumnDescription(String columnName) throws ObjectNotFoundException;

	ColumnDescription[] getAllColumnDescriptions();
	
	ColumnDescription[] getFilteredColumnDescriptions();
	
	FunctionColumnDescription[] getFunctionColumnDescriptions();

	double[] extractColumn(String columnName) throws ExpressionException,ObjectNotFoundException;

	boolean isMultiTrialData();

	int getRowCount();
	
	FilterCategoryType[] getSupportedFilterCategories();

	void selectCategory(FilterCategoryType[] filterCategories);
	
	DataSymbolMetadataResolver getDataSymbolMetadataResolver();
}
