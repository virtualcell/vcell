package cbit.vcell.client.data;

import java.beans.PropertyChangeListener;

import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.solver.SimulationModelInfo.ModelCategoryType;
import cbit.vcell.util.ColumnDescription;

public interface ODEDataInterface {

	void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

	void addPropertyChangeListener(PropertyChangeListener ivjEventHandler);

	int getColumnDescriptionsCount();

	ColumnDescription getColumnDescription(String columnName) throws ObjectNotFoundException;

	ColumnDescription[] getAllColumnDescriptions();
	
	ColumnDescription[] getFilteredColumnDescriptions();
	
	FunctionColumnDescription[] getFunctionColumnDescriptions();

	double[] extractColumn(String columnName) throws ExpressionException,ObjectNotFoundException;
	double[] extractColumnMax(String columnName) throws ExpressionException,ObjectNotFoundException;
	double[] extractColumnMin(String columnName) throws ExpressionException,ObjectNotFoundException;
	double[] extractColumnStd(String columnName) throws ExpressionException,ObjectNotFoundException;

	boolean isMultiTrialData();

	int getRowCount();
	
	ModelCategoryType[] getSupportedFilterCategories();

	void selectCategory(ModelCategoryType[] filterCategories);
	
	DataSymbolMetadataResolver getDataSymbolMetadataResolver();
}
