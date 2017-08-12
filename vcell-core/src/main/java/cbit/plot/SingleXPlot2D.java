/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.plot;

import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.SimulationModelInfo.DataSymbolMetadataResolver;

/**
 * Insert the type's description here.
 * Creation date: (4/19/2001 1:29:15 PM)
 * @author: Ion Moraru
 */
public class SingleXPlot2D extends Plot2D {
	private double[][] dataValues = null;
	private String xName = "";
/**
 * SingleXPlot2D constructor comment.
 * @param names String[]
 * @param dataValues double[][]
 */
public SingleXPlot2D(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String xName, String[] names, double[][] dataValues) {
	this(argSymbolTableEntries,metadataResolver,xName, names, dataValues, null, null);
}
/**
 * SingleXPlot2D constructor comment.
 * @param names String[]
 * @param dataValues double[][]
 * @param labels String[]
 */
public SingleXPlot2D(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String xName, String[] names, double[][] dataValues, String[] labels) {
	this(argSymbolTableEntries,metadataResolver,xName, names, dataValues, labels, null);
}
/**
 * SingleXPlot2D constructor comment.
 * @param names String[]
 * @param dataValues double[][]
 * @param labels String[]
 * @param visiblePlots boolean[]
 */
public SingleXPlot2D(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String xName, String[] names, double[][] dataValues, String[] labels, boolean[] visiblePlots) {
	super(argSymbolTableEntries,metadataResolver,names, dataValues, labels, visiblePlots);
	this.dataValues = dataValues;
	if (xName != null) {
		this.xName = xName;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 12:07:44 PM)
 * @return String[]
 */
public String[] getColumnTitles() {
	String [] columnTitles = new String[getNumberOfPlots() + 1];
	columnTitles[0] = xName;
	for (int i = 0; i < getNumberOfPlots(); i++){
		columnTitles[i + 1] = getPlotNames()[i];
	}
	return columnTitles;
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 2:51:48 PM)
 * @return double[][]
 */
public double[][] getDataValues() {
	return dataValues;
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 2:51:48 PM)
 * @return Double[][]
 */
public Double[][] getDataValuesByRow() {
	if (dataValues == null) {
		return null;
	} else {
		Double[][] valuesByRow = new Double[dataValues[0].length][dataValues.length];
		for (int i = 0; i < dataValues.length; i++){
			for (int j = 0; j < dataValues[0].length; j++){
				valuesByRow[j][i] = new Double(dataValues[i][j]);
			}
		}
		return valuesByRow;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 12:07:44 PM)
 * @return String[]
 */
public String[] getVisiblePlotColumnTitles() {
	String [] visiblePlotColumnTitles = new String[getNumberOfVisiblePlots() + 1];
	visiblePlotColumnTitles[0] = getLabels()[LABEL_X];
	for (int i = 0; i < getNumberOfVisiblePlots(); i++){
		visiblePlotColumnTitles[i + 1] = getPlotNames()[getVisiblePlotIndices()[i]];
		//visiblePlotColumnTitles[i + 1] = getLabels()[LABEL_Y];
	}
	return visiblePlotColumnTitles;
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 2:51:48 PM)
 * @return Double[][]
 */
public Double[][] getVisiblePlotDataValuesByRow() {
	if (dataValues == null) {
		return null;
	} else {
		Double[][] visiblePlotValuesByRow = new Double[dataValues[0].length][getNumberOfVisiblePlots() + 1];
		for (int j = 0; j < dataValues[0].length; j++){
			visiblePlotValuesByRow[j][0] = new Double(dataValues[0][j]);
		}
		for (int i = 0; i < getNumberOfVisiblePlots(); i++){
			for (int j = 0; j < dataValues[0].length; j++){
				visiblePlotValuesByRow[j][i + 1] = new Double(dataValues[getVisiblePlotIndices()[i] + 1][j]);
			}
		}
		return visiblePlotValuesByRow;
	}
}

@Override
public SymbolTableEntry getPlotDataSymbolTableEntry(int tableColumn) {
	int index = getVisiblePlotIndices()[tableColumn - 1];
	return getSymbolTableEntries()[index];
}
}
