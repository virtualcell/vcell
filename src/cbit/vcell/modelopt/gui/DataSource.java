package cbit.vcell.modelopt.gui;

import cbit.plot.Plot2D;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;

/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:23:20 PM)
 * @author: Jim Schaff
 */
public abstract class DataSource {
	private String name = null; 

	public static class DataSourceReferenceData extends DataSource {
		private ReferenceData referenceData = null;
		private int timeIndex = 0;
		
		public DataSourceReferenceData(String argName, int arg_timeIndex, ReferenceData arg_referenceData) {
			super(argName);
			timeIndex = arg_timeIndex;
			referenceData = arg_referenceData;
		}
		
		@Override
		public int getRenderHints() {
			return (Plot2D.RENDERHINT_DRAWLINE);
		}

		@Override
		public int findColumn(String colName) {
			return referenceData.findColumn(colName);
		}

		@Override
		public double[] getColumnData(int columnIndex) {
			return referenceData.getColumnData(columnIndex);
		}

		@Override
		public String[] getColumnNames() {
			return referenceData.getColumnNames();
		}

		@Override
		public int getTimeColumnIndex() {			
			return timeIndex;	
		}

//		@Override
//		public double[] getColumnWeights() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public int getDataSize() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public int getNumColumns() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public int getNumRows() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public double[] getRowData(int rowIndex) {
//			// TODO Auto-generated method stub
//			return null;
//		}
		
	}
	
	public static class DataSourceOdeSolverResultSet extends DataSource {
		private ODESolverResultSet odeSolverResultSet = null;
		
		public DataSourceOdeSolverResultSet(String argName, ODESolverResultSet arg_odeOdeSolverResultSet) {
			super(argName);
			odeSolverResultSet = arg_odeOdeSolverResultSet;
		}
		
		@Override
		public int getRenderHints() {
			return (Plot2D.RENDERHINT_DRAWLINE);
		}

		@Override
		public int findColumn(String colName) {
			return odeSolverResultSet.findColumn(colName);
		}

		@Override
		public double[] getColumnData(int columnIndex) {
			try {
				return odeSolverResultSet.extractColumn(columnIndex);
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}

		@Override
		public String[] getColumnNames() {
			ColumnDescription[] columnDescriptions = odeSolverResultSet.getColumnDescriptions();
			String[] names = new String[columnDescriptions.length];
			for (int i = 0; i < names.length; i++) {
				names[i] = columnDescriptions[i].getName();
			}
			return names;
		}

		@Override
		public int getTimeColumnIndex() {			
			return findColumn("t");
		}

//		@Override
//		public double[] getColumnWeights() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public int getDataSize() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public int getNumColumns() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public int getNumRows() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public double[] getRowData(int rowIndex) {
//			// TODO Auto-generated method stub
//			return null;
//		}
		
	}
/**
 * DataSource constructor comment.
 */
public DataSource(String argName) {
	super();
	this.name = argName;
}

/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:25:28 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:54:22 PM)
 * @return int
 * @param columnName java.lang.String
 */
public int getRenderHints() {
	return (Plot2D.RENDERHINT_DRAWLINE | Plot2D.RENDERHINT_DRAWPOINT);	
}

public abstract int findColumn(String colName);
public abstract double[] getColumnData(int columnIndex);
public abstract String[] getColumnNames();
public abstract int getTimeColumnIndex();
//public abstract double[] getColumnWeights();
//public abstract int getNumColumns();
//public abstract int getNumRows();
//public abstract int getDataSize();
//public abstract double[] getRowData(int rowIndex);
}