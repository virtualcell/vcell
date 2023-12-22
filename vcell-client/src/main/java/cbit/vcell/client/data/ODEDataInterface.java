package cbit.vcell.client.data;

import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;

import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.simulation.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.solver.simulation.SimulationModelInfo.ModelCategoryType;
import cbit.vcell.util.ColumnDescription;

public interface ODEDataInterface {
	
	public enum PlotType {
		Min,
		Max,
		Mean,
		Std
	}


	void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

	void addPropertyChangeListener(PropertyChangeListener ivjEventHandler);

	int getColumnDescriptionsCount();

	ColumnDescription getColumnDescription(String columnName) throws ObjectNotFoundException;

	ColumnDescription[] getAllColumnDescriptions();
	
	ColumnDescription[] getFilteredColumnDescriptions();
	
	FunctionColumnDescription[] getFunctionColumnDescriptions();

	LinkedHashMap<String, Integer> parseHDF5File()  throws ExpressionException,ObjectNotFoundException;
	double[] extractColumn(String columnName) throws ExpressionException,ObjectNotFoundException;
	double[] extractColumn(String columnName, PlotType plotType) throws ExpressionException,ObjectNotFoundException;
//	double[] extractColumnMin(String columnName) throws ExpressionException,ObjectNotFoundException;
//	double[] extractColumnStd(String columnName) throws ExpressionException,ObjectNotFoundException;

	boolean isMultiTrialData();

	int getRowCount();
	
	ModelCategoryType[] getSupportedFilterCategories();

	void selectCategory(ModelCategoryType[] filterCategories);
	
	DataSymbolMetadataResolver getDataSymbolMetadataResolver();
}
