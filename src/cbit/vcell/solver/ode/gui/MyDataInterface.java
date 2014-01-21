package cbit.vcell.solver.ode.gui;

import java.beans.PropertyChangeListener;

import cbit.vcell.math.MathDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.util.ColumnDescription;

public interface MyDataInterface {

	public interface FilterCategory {
		public static final String RESERVE_VAR_FILTER_CATEGORY = "Reserved XYZT";
		public String getName();
	}

	void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

	void addPropertyChangeListener(PropertyChangeListener ivjEventHandler);

	int getColumnDescriptionsCount();

	ColumnDescription getColumnDescriptions(int i);

	ColumnDescription[] getColumnDescriptions();
	
	FunctionColumnDescription[] getFunctionColumnDescriptions();

	int findColumn(String sensParamName);

	double[] extractColumn(int findColumn) throws ExpressionException;

	boolean isMultiTrialData();

	int getRowCount();
	
	FilterCategory[] getSupportedFilterCategories();

	void selectCategory(FilterCategory[] filterCategories);
	
	void setMathDescription(MathDescription mathDescription);
}
