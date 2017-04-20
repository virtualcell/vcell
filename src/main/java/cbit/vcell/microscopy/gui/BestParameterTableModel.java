/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.opt.Parameter;

@SuppressWarnings("serial")
public class BestParameterTableModel extends AbstractTableModel implements PropertyChangeListener
{
	public final static int NUM_COLUMNS = 3;
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_VALUE = 1;
	public final static int COLUMN_UNITS = 2;
	public final static String COL_LABELS[] = {"Parameter Name",/* "Description",*/ "Expression", "Unit"};
	
	private FRAPSingleWorkspace frapWorkspace = null;
	Integer bestModelIndex = null;
	Parameter[] parameters = null;
	
	public BestParameterTableModel() {
    	super();
    }

    public int getColumnCount() {
        return NUM_COLUMNS;
    }

    public int getRowCount() 
    {
    	if(getBestModelParameters() != null)
    	{
    		return getBestModelParameters().length;
    	}
    	else
    	{
    		return 0;
    	}
    }

    public Object getValueAt(int row, int col) 
    {
    	if (col<0 || col>=NUM_COLUMNS){
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
    	if (row<0 || row>=getRowCount()){
    		throw new RuntimeException("AnalysisTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
    	}
    	if(bestModelIndex == null)
    	{
    		return null;
    	}
    	Parameter param = getParameter(row);
        
    	if (param == null)
    	{
    		return null;
    	}
    	if(col == COLUMN_NAME)
    	{
    		return param.getName();
    	}
//    	else if(col == COLUMN_DESCRIPTION)
//    	{
//    		return FRAPModel.MODEL_PARAMETER_NAMES[row];
//    	}
    	else if(col == COLUMN_VALUE)
    	{
    		return param.getInitialGuess();
    	}
    	else if(col == COLUMN_UNITS)
    	{
    		return FRAPModel.MODEL_PARAMETER_UNITS[row].getSymbol();
    	}
    	
    	return null;
    }

    public Class<?> getColumnClass(int column) {
    	switch (column){
    		case COLUMN_NAME:{
    			return String.class;
    		}
//    		case COLUMN_DESCRIPTION:{
//    			return String.class;
//    		}
    		case COLUMN_VALUE: {
    			return Double.class;
    		}
    		case COLUMN_UNITS: {
    			return String.class;
    		}
    		default:{
    			return Object.class;
    		}
    	}
    }

    public String getColumnName(int column) {
    	if (column<0 || column>=NUM_COLUMNS){
    		throw new RuntimeException("AnalysisTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
    	}
        return COL_LABELS[column];
    }

    public boolean isCellEditable(int rowIndex,int columnIndex) 
    {
        return false;
    }

    public void setValueAt(Object aValue,int rowIndex, int columnIndex) 
    {
    }
    
    public Integer getBestModelIndex() {
		return bestModelIndex;
	}

	public void setBestModelIndex(Integer bestModelIndex) {
		this.bestModelIndex = bestModelIndex;
		fireTableDataChanged();
	}

    
    public Parameter[] getBestModelParameters()
    {
    	if( bestModelIndex != null && -1 < bestModelIndex.intValue() && bestModelIndex.intValue() < FRAPModel.NUM_MODEL_TYPES)
    	{
	    	FRAPModel bestModel = getFrapWorkspace().getWorkingFrapStudy().getModels()[bestModelIndex.intValue()];
	    	if(bestModel != null)
	    	{
		    	if(bestModelIndex != FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
		    	{
		    		parameters = bestModel.getModelParameters();
		    	}
		    	else
		    	{
		    		parameters = new Parameter[2];
		    		parameters[0] = bestModel.getModelParameters()[FRAPModel.INDEX_BLEACH_MONITOR_RATE];
		    		parameters[1] = bestModel.getModelParameters()[FRAPModel.INDEX_OFF_RATE];
		    	}
		    	return parameters;
	    	}
    	}
   		return null;
   	}
    
    public Parameter getParameter(int paramIndex)
    {
    	if(parameters != null && paramIndex < parameters.length)
    	{
    		return parameters[paramIndex];
    	}
    	else
    	{
    		return null;
    	}
    }
    
    public FRAPSingleWorkspace getFrapWorkspace()
    {
        return frapWorkspace;
    }
   
    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
    {
    	this.frapWorkspace = frapWorkspace;
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_BEST_MODEL))
		{
			if(evt.getNewValue() instanceof Integer)
			{
				setBestModelIndex(((Integer)evt.getNewValue()));
			}
		}
		
	}
}
