package cbit.vcell.modelopt.gui;

import cbit.plot.DataSource;
import cbit.plot.Plot2D;
import cbit.vcell.opt.ReferenceData;

public class ReferenceDataSource implements DataSource {
	private ReferenceData refData = null;
	private String name = null;
	
	public ReferenceDataSource(ReferenceData argReferenceData, String argName){
		this.refData = argReferenceData;
		this.name = argName;
	}

	public int findColumn(String columnName) {
		// TODO Auto-generated method stub
		return refData.findColumn(columnName);
	}

	public double[] getColumnData(int index) {
		// TODO Auto-generated method stub
		return refData.getColumnData(index);
	}

	public String[] getColumnNames() {
		// TODO Auto-generated method stub
		return refData.getColumnNames();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public int getRenderHints(int index) {
		// TODO Auto-generated method stub
		return Plot2D.RENDERHINT_DRAWPOINT;
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return refData.getNumColumns();
	}

}
