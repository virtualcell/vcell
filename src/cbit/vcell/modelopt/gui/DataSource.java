/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt.gui;

import cbit.plot.Plot2D;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.ExpressionException;
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
		private final static int DEFAULT_TIME_COLUMN_INDEX = 0;
		
		public DataSourceReferenceData(String argName, ReferenceData arg_referenceData) {
			this(argName, DEFAULT_TIME_COLUMN_INDEX, arg_referenceData);
		}
		
		public DataSourceReferenceData(String argName, int arg_timeColumnIndex, ReferenceData arg_referenceData) {
			super(argName);
			timeIndex = arg_timeColumnIndex;
			referenceData = arg_referenceData;
		}
		
		@Override
		public int getRenderHints() {
			return (Plot2D.RENDERHINT_DRAWPOINT);
		}

		@Override
		public int findColumn(String colName) {
			return referenceData.findColumn(colName);
		}

		@Override
		public double[] getColumnData(int columnIndex) {
			return referenceData.getDataByColumn(columnIndex);
		}

		@Override
		public String[] getColumnNames() {
			return referenceData.getColumnNames();
		}

		@Override
		public int getTimeColumnIndex() {			
			return timeIndex;	
		}

		@Override
		public int getNumColumns() {
			return referenceData.getNumDataColumns();
		}

		@Override
		public int getNumRows() {
			return referenceData.getNumDataRows();
		}

		@Override
		public double[] getRowData(int rowIndex) {
			return referenceData.getDataByRow(rowIndex);
		}

		@Override
		public boolean isSourceNull() {
			return referenceData == null;
		}
		
	}
	
	public static class DataSourceRowColumnResultSet extends DataSource {
		private RowColumnResultSet rowColumnResultSet = null;
		private final int renderHints;
		
		public DataSourceRowColumnResultSet(String argName, RowColumnResultSet arg_rowColumnResultSet) {
			this(argName, arg_rowColumnResultSet, false);
		}
		
		public DataSourceRowColumnResultSet(String argName, RowColumnResultSet arg_rowColumnResultSet, boolean bDrawPoints) {
			super(argName);
			rowColumnResultSet = arg_rowColumnResultSet;
			if (bDrawPoints){
				renderHints = Plot2D.RENDERHINT_DRAWPOINT;
			}else{
				renderHints = Plot2D.RENDERHINT_DRAWLINE;
			}
		}
		
		@Override
		public int getRenderHints() {
			return renderHints;
		}

		@Override
		public int findColumn(String colName) {
			return rowColumnResultSet.findColumn(colName);
		}

		@Override
		public double[] getColumnData(int columnIndex) {
			try {
				return rowColumnResultSet.extractColumn(columnIndex);
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}

		@Override
		public String[] getColumnNames() {
			ColumnDescription[] columnDescriptions = rowColumnResultSet.getColumnDescriptions();
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

		@Override
		public int getNumColumns() {
			return rowColumnResultSet.getColumnDescriptionsCount();
		}

		@Override
		public int getNumRows() {
			return rowColumnResultSet.getRowCount();
		}

		@Override
		public double[] getRowData(int rowIndex) {
			return rowColumnResultSet.getRow(rowIndex);
		}

		@Override
		public boolean isSourceNull() {
			return rowColumnResultSet == null;
		}		
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
public abstract int getNumColumns();
public abstract int getNumRows();
public abstract double[] getRowData(int rowIndex);
public abstract boolean isSourceNull();
}
