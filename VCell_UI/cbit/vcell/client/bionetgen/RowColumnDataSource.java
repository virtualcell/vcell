package cbit.vcell.client.bionetgen;

import org.vcell.expression.ExpressionException;

import cbit.plot.DataSource;
import cbit.plot.Plot2D;
import cbit.vcell.simdata.ColumnDescription;
import cbit.vcell.simdata.RowColumnResultSet;

public class RowColumnDataSource implements DataSource {
	
	private RowColumnResultSet rowColumnResultSet = null;
	private String name = null;
	
	public RowColumnDataSource(RowColumnResultSet argRowColumnResultSet, String argName){
		this.rowColumnResultSet = argRowColumnResultSet;
		this.name = argName;
	}

	public int findColumn(String columnName) {
		return rowColumnResultSet.findColumn(columnName);
	}

	public double[] getColumnData(int index) {
		try {
			return rowColumnResultSet.extractColumn(index);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public String[] getColumnNames() {
		ColumnDescription[] colDescriptions = rowColumnResultSet.getColumnDescriptions();
		String[] colNames = new String[colDescriptions.length];
		for (int i = 0; i < colDescriptions.length; i++) {
			colNames[i] = colDescriptions[i].getName();
		}
		return colNames;
	}

	public String getName() {
		return name;
	}

	public int getRenderHints(int index) {
		return Plot2D.RENDERHINT_DRAWLINE;
	}

	public int getColumnCount() {
		return rowColumnResultSet.getColumnDescriptionsCount();
	}

}
