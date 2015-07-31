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
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.Range;

import cbit.vcell.client.data.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (2/8/2001 12:43:18 PM)
 * @author: Ion Moraru
 */
public class Plot2D {
	private SymbolTableEntry[] symbolTableEntries;
	private DataSymbolMetadataResolver dataSymbolMetadataResolver;
	private String[] plotNames = new String[0];
	private PlotData[] plotDatas = new PlotData[0];
	private Range xDataRange = new Range(0,0);
	private Range yDataRange = new Range(0,0);
	private String[] labels = new String[3];
	private int[] fieldRenderHints = new int[0];
	private int numPlots = 0;
	private boolean[] fieldVisiblePlots = new boolean[0];
	protected transient java.util.Vector<ChangeListener> aChangeListener = null;
	private boolean isHistogram = false;
	//
	public static final int LABEL_TITLE = 0;
	public static final int LABEL_X = 1;
	public static final int LABEL_Y = 2;

	public static final int RENDERHINT_DRAWLINE = 0x0001;
	public static final int RENDERHINT_DRAWPOINT = 0x0002;

/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:47:50 PM)
 * @param names java.lang.String[]
 * @param dataValues double[][]
 * @param labels java.lang.String[]
 * @param visiblePlots boolean[]
 */
protected Plot2D(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String[] names, double[][] dataValues, String[] labels, boolean[] visiblePlots) {
	/*
	 for a Plot2D with a "single X" dataset (all PlotDatas share the same independent values)
	
	 dataValues is treated as "column x rows" i.e. it is an array holding the independent
	 values ("X" data) as a double[] in the first element and each set of dependent values
	 ("Y" datasets) as a double[] in the following elements
	
	 dataValues[7][3] is the fourth dependent value in the seventh dependent values dataset
	 dataValues[0][5] is the sixth independent value in the single independent values dataset
	*/
	PlotData[] plotDatas = null;
	if (dataValues != null && dataValues.length > 1) {
		plotDatas = new PlotData[dataValues.length - 1];
		for (int i = 0; i < plotDatas.length; i++) {
			plotDatas[i] = new PlotData(dataValues[0], dataValues[i+1]);
		}
	}
	initialize(argSymbolTableEntries,metadataResolver, names, plotDatas, labels, visiblePlots, null);
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:17:41 PM)
 * @param names java.lang.String[]
 * @param plotDatas cbit.plot.PlotData[]
 */
public Plot2D(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String[] names, PlotData[] plotDatas) {
	this(argSymbolTableEntries,metadataResolver,names, plotDatas, null, null);
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:47:50 PM)
 * @param names java.lang.String[]
 * @param plotDatas cbit.plot.PlotData[]
 * @param labels java.lang.String[]
 */
public Plot2D(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String[] names, PlotData[] plotDatas, String[] labels) {
	this(argSymbolTableEntries,metadataResolver,names, plotDatas, labels, null);
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:47:50 PM)
 * @param names java.lang.String[]
 * @param plotDatas cbit.plot.PlotData[]
 * @param labels java.lang.String[]
 * @param visiblePlots boolean[]
 */
public Plot2D(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String[] names, PlotData[] plotDatas, String[] labels, boolean[] visiblePlots) {
	this(argSymbolTableEntries,metadataResolver,names, plotDatas, labels, visiblePlots, null);
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:47:50 PM)
 * @param names java.lang.String[]
 * @param plotDatas cbit.plot.PlotData[]
 * @param labels java.lang.String[]
 * @param visiblePlots boolean[]
 */
public Plot2D(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String[] names, PlotData[] plotDatas, String[] labels, boolean[] visiblePlots, int[] argRenderHints) {
	initialize(argSymbolTableEntries,metadataResolver,names, plotDatas, labels, visiblePlots, argRenderHints);
}

/**
 * @return metadataResolver, if (may be null)
 */
public DataSymbolMetadataResolver getDataSymbolMetadataResolver(){
	return this.dataSymbolMetadataResolver;
}


/**
 * Add a javax.swing.event.ChangeListener.
 */
public void addChangeListener(javax.swing.event.ChangeListener newListener) {
	if (aChangeListener == null) {
		aChangeListener = new java.util.Vector<ChangeListener>();
	};
	if (! aChangeListener.contains(newListener)) {
		aChangeListener.addElement(newListener);
	}
}


/**
 * Method to support listener events.
 */
protected void fireStateChanged() {
	if (aChangeListener != null) {
		ChangeEvent e = new ChangeEvent(this);
		Enumeration<ChangeListener> en = aChangeListener.elements();
		while (en.hasMoreElements()) {
			ChangeListener listener = (ChangeListener)en.nextElement();
			listener.stateChanged(e);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 2:51:48 PM)
 * @return Double[][]
 */
public Double[][] getDataValuesByRow() {
	Double[][] valuesByRow = new Double[getMaxPlotDataSize()][2 * getNumberOfPlots()];
	for (int j = 0; j < getNumberOfPlots(); j++){
		for (int i = 0; i < plotDatas[j].getSize(); i++){
			valuesByRow[i][2 * j] = new Double(plotDatas[j].getIndependent()[i]);
			valuesByRow[i][2 * j + 1] = new Double(plotDatas[j].getDependent()[i]);
		}
	}
	return valuesByRow;
}


/**
 * Insert the method's description here.
 * Creation date: (10/7/2004 5:31:19 PM)
 * @return java.lang.String[]
 */
public String[] getLabels() {
	return labels;
}


/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 11:49:34 AM)
 * @return int
 */
public int getMaxPlotDataSize() {
	int size = 0;
	for (int i = 0; i < getNumberOfPlots(); i++){
		size = Math.max(size, plotDatas[i].getSize());
	}
	return size;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2001 12:50:53 AM)
 * @return int
 */
public int getNumberOfPlots() {
	return plotDatas.length;
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2001 3:43:10 PM)
 * @return int
 */
public int getNumberOfVisiblePlots() {
	int numberOfVisiblePlots = 0;
	for (int i = 0; i < fieldVisiblePlots.length; i++) {
		if (fieldVisiblePlots[i]) {
			numberOfVisiblePlots++;
		}
	}
	return numberOfVisiblePlots;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:38:55 PM)
 * @return cbit.plot.PlotData[]
 */
public PlotData[] getPlotDatas() {
	return plotDatas;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:38:19 PM)
 * @return java.lang.String[]
 */
public String[] getPlotNames() {
	return plotNames;
}


/**
 * Insert the method's description here.
 * Creation date: (8/26/2005 10:42:16 AM)
 * @return int[]
 */
public int[] getRenderHints() {
	return fieldRenderHints;
}


/**
 * Insert the method's description here.
 * Creation date: (7/26/2006 4:29:13 PM)
 * @return cbit.vcell.parser.SymbolTableEntry[]
 */
public SymbolTableEntry[] getSymbolTableEntries() {
	return symbolTableEntries;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:51:05 PM)
 * @return java.lang.String
 */
public String getTitle() {
	return labels[LABEL_TITLE];
}


/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 12:07:44 PM)
 * @return java.lang.String[]
 */
public String[] getVisiblePlotColumnTitles() {
	String [] vsisblePlotColumnTitles = new String[2 * getNumberOfVisiblePlots()];
	String xTitle = labels[LABEL_X];
	for (int i = 0; i < getNumberOfVisiblePlots(); i++){
		String yTitle = getPlotNames()[getVisiblePlotIndices()[i]];
		if(isHistogram)
		{
			vsisblePlotColumnTitles[2 * i] = "No.of molecules of " + yTitle;
			vsisblePlotColumnTitles[2 * i + 1] = "Prob. distribution of " + yTitle;
		}
		else
		{
			vsisblePlotColumnTitles[2 * i] = xTitle;
			vsisblePlotColumnTitles[2 * i + 1] = yTitle;
		}
	}
	return vsisblePlotColumnTitles;
}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 2:51:48 PM)
 * @return Double[][]
 */
public Double[][] getVisiblePlotDataValuesByRow() {
	Double[][] visiblePlotValuesByRow = new Double[getMaxPlotDataSize()][2 * getNumberOfVisiblePlots()];
	for (int j = 0; j < getVisiblePlotIndices().length; j++){
		for (int i = 0; i < plotDatas[getVisiblePlotIndices()[j]].getSize(); i++){
			visiblePlotValuesByRow[i][2 * j] = new Double(plotDatas[getVisiblePlotIndices()[j]].getIndependent()[i]);
			visiblePlotValuesByRow[i][2 * j + 1] = new Double(plotDatas[getVisiblePlotIndices()[j]].getDependent()[i]);
		}
	}
	return visiblePlotValuesByRow;
}


/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 1:16:56 PM)
 * @return int[]
 */
public int[] getVisiblePlotIndices() {
	int[] indices = new int[getNumberOfVisiblePlots()];
	int idx = 0;
	for (int i = 0; i < getNumberOfPlots(); i++){
		if (isVisiblePlot(i)) {
			indices[idx] = i;
			idx++;
		}
	}
	return indices;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:32:46 PM)
 * @return cbit.image.Range
 */
public Range getXDataRange() {
	double xmin = 0;
	double xmax = 0;
	for (int i=0;i<plotDatas.length;i++) {
		if (isVisiblePlot(i)) {
			xmin = Math.min(xmin, plotDatas[i].getIndependentMin());
			xmax = Math.max(xmax, plotDatas[i].getIndependentMax());
		}
	}
	xDataRange = new Range(xmin, xmax);
	return xDataRange;
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:32:46 PM)
 * @return cbit.image.Range
 */
public boolean visiblePlotsInvalid() {
	boolean bVisblePlotsInvalid = false;
	for (int i=0;i<plotDatas.length;i++) {
		if (isVisiblePlot(i) && plotDatas[i].hasInvalidNumericValues()){
			bVisblePlotsInvalid = true;
		}
	}
	return bVisblePlotsInvalid;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:51:05 PM)
 * @return java.lang.String
 */
public String getXLabel() {
	return labels[LABEL_X];
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:33:21 PM)
 * @return cbit.image.Range
 */
public Range getYDataRange() {
	double ymin = 0;
	double ymax = 0;
	for (int i=0;i<plotDatas.length;i++) {
		if (isVisiblePlot(i)) {
			ymin = Math.min(ymin, plotDatas[i].getDependentMin());
			ymax = Math.max(ymax, plotDatas[i].getDependentMax());
		}
	}
	yDataRange = new Range(ymin, ymax);
	return yDataRange;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:51:05 PM)
 * @return java.lang.String
 */
public String getYLabel() {
	return labels[LABEL_Y];
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 1:47:50 PM)
 * @param names java.lang.String[]
 * @param plotDatas cbit.plot.PlotData[]
 * @param labels java.lang.String[]
 * @param visiblePlots boolean[]
 */
private void initialize(SymbolTableEntry[] argSymbolTableEntries,DataSymbolMetadataResolver metadataResolver, String[] names, PlotData[] plotDatas, String[] labels, boolean[] visiblePlots, int[] renderHints) {

	if(argSymbolTableEntries != null && argSymbolTableEntries.length != plotDatas.length){
		throw new IllegalArgumentException("Number of SymbolTableEntries does not equal number of Data Columns");
	}
	symbolTableEntries = argSymbolTableEntries;
	this.dataSymbolMetadataResolver = metadataResolver;
	
	if (names != null && plotDatas != null) {
		if (names.length != plotDatas.length) {
			throw new IllegalArgumentException("arrays must have same length");
		} else {
			this.plotNames = names;
			this.plotDatas = plotDatas;
			numPlots = plotDatas.length;
			if (fieldVisiblePlots.length == 0) {
				fieldVisiblePlots = new boolean[plotDatas.length];
				Arrays.fill(fieldVisiblePlots,true);
			}
			if (fieldRenderHints.length == 0) {
				fieldRenderHints = new int[plotDatas.length];
				Arrays.fill(fieldRenderHints,RENDERHINT_DRAWLINE|RENDERHINT_DRAWPOINT);
			}
		}
	}
	if (labels != null) {
		for (int i=0;i<labels.length && i<this.labels.length;i++) {
			this.labels[i] = labels[i];
		}
	}
	if (visiblePlots != null && visiblePlots.length == fieldVisiblePlots.length) {
		fieldVisiblePlots = visiblePlots;
	}
	if (renderHints != null && renderHints.length == fieldRenderHints.length) {
		fieldRenderHints = renderHints;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2001 4:11:57 PM)
 * @return boolean
 * @param index int
 */
public boolean isVisiblePlot(int index) {
	try {
		return fieldVisiblePlots[index];
	} catch (IndexOutOfBoundsException exc) {
		return false;
	}
}


/**
 * Remove a javax.swing.event.ChangeListener.
 */
public void removeChangeListener(javax.swing.event.ChangeListener newListener) {
	if (aChangeListener != null) {
		aChangeListener.removeElement(newListener);
	};
}


/**
 * Sets the visiblePlots property (boolean[]) value.
 * @param visiblePlots The new value for the property.
 * @see #getVisiblePlots
 */
public void setVisiblePlots(boolean[] visiblePlots, boolean arg_isHistogram) {
	if (visiblePlots.length != plotDatas.length) {
		throw new IllegalArgumentException("visiblePlots array must have same length as plotData array");
	} else {
		fieldVisiblePlots = visiblePlots;
		isHistogram = arg_isHistogram;
		fireStateChanged();
	}
}

public SymbolTableEntry getPlotDataSymbolTableEntry(int tableColumn) {
	int index = tableColumn % 2;
	if (index == 0) {
		return ReservedVariable.TIME;
	}
	
	return getSymbolTableEntries()[index];
}
}
