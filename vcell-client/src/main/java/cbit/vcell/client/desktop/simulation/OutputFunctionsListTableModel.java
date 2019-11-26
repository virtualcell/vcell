/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableColumn;

import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.InconsistentDomainException;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.math.VariableType;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.SimulationOwner;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 4:07:40 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class OutputFunctionsListTableModel extends VCellSortTableModel<AnnotatedFunction> implements PropertyChangeListener {
	private class FunctionColumnComparator implements Comparator<AnnotatedFunction> {
		protected int index;
		protected boolean ascending;

		public FunctionColumnComparator(int index, boolean ascending){
			this.index = index;
			this.ascending = ascending;
		}
		
		public int compare(AnnotatedFunction parm1, AnnotatedFunction parm2){
			
			switch (index){
				case COLUMN_OUTPUTFN_NAME:{
					int bCompare = parm1.getName().compareToIgnoreCase(parm2.getName());
					return ascending ? bCompare : -bCompare;
				}
				case COLUMN_OUTPUTFN_VARIABLETYPE : {
					int bCompare = parm1.getFunctionType().getVariableDomain().getName().compareToIgnoreCase(parm2.getFunctionType().getVariableDomain().getName());
					return ascending ? bCompare : - bCompare;					
				}
			}
			return 1;
		}
	}

	public final static int COLUMN_OUTPUTFN_NAME = 0;
	public final static int COLUMN_OUTPUTFN_EXPRESSION = 1;
	public final static int COLUMN_OUTPUTFN_VARIABLETYPE = 2;
	
	private OutputFunctionContext outputFunctionContext = null;
	private static String[] columnNames = new String[] {"Name", "Expression", "Defined In"};

/**
 * SimulationListTableModel constructor comment.
 */
public OutputFunctionsListTableModel(ScrollTable table) {
	super(table, columnNames);
}

/**
 * getColumnCount method comment.
 */
@Override
public int getColumnCount() {
	int nc = super.getColumnCount();
	if (getOutputFunctionContext() == null 
			||  getOutputFunctionContext().getSimulationOwner().getGeometry().getDimension() == 0) {
		nc = nc - 1; // no domain
	}
	return nc;
}

public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_OUTPUTFN_NAME:{
			return String.class;
		}
		case COLUMN_OUTPUTFN_EXPRESSION:{
			return ScopedExpression.class;
		}
		case COLUMN_OUTPUTFN_VARIABLETYPE: {
			return String.class;
		}
		default:{
			return Object.class;
		}
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int column) {
	try{
		if (row >= 0 && row < getRowCount()) {
			AnnotatedFunction obsFunction = getValueAt(row);
			switch (column) {
				case COLUMN_OUTPUTFN_NAME: {
					return obsFunction.getName();
				} 
				case COLUMN_OUTPUTFN_EXPRESSION: {
					if (obsFunction.getExpression() == null) {
						return null; 
					} else {
						AutoCompleteSymbolFilter autoCompleteSymbolFilter = outputFunctionContext.getAutoCompleteSymbolFilter(obsFunction.getDomain());
						return new ScopedExpression(obsFunction.getExpression(),outputFunctionContext.getNameScope(), true, true, autoCompleteSymbolFilter);
					}
				}
				case COLUMN_OUTPUTFN_VARIABLETYPE: {
					if (obsFunction.getDomain() == null) {
						return obsFunction.getFunctionType().getVariableDomain().getName();
					}
					return obsFunction.getDomain().getName();
				}
				default: {
					return null;
				}
			}
		} else {
			return null;
		}
	}catch(Exception e){
		e.printStackTrace(System.out);
		return null;
	}
}

public boolean isSortable(int col) {
	return col != COLUMN_OUTPUTFN_EXPRESSION;
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 1:56:12 PM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	return columnIndex == COLUMN_OUTPUTFN_EXPRESSION || columnIndex == COLUMN_OUTPUTFN_VARIABLETYPE;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	OutputFunctionContext fc = getOutputFunctionContext();
	SimulationOwner so = null;
	if(fc != null) {
		so = fc.getSimulationOwner();
	}

	if (evt.getSource() == fc && evt.getPropertyName().equals(OutputFunctionContext.PROPERTY_OUTPUT_FUNCTIONS)) {
		setData(outputFunctionContext.getOutputFunctionsList());
	}
	if(evt.getSource() instanceof SimulationContext && evt.getSource() == so && evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_ENTITY_NAME)) {
		SimulationContext simulationContext = (SimulationContext)so;
		
		if(fc.getOutputFunctionsList() == null || fc.getOutputFunctionsList().isEmpty()) {
			return;
		}
		
		Hashtable<String,Object> hashTable = new Hashtable<String, Object>();
		//
		// WARNING: this should NOT be used under any circumstance for batch renaming
		// MathDescription, MathMapping, expressions are NOT thread safe
		//
		AsynchClientTask task0 = new AsynchClientTask("Renaming Functions", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING, false, false) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				MathMappingCallback callback = new MathMappingCallback() {
					@Override
					public void setProgressFraction(float fractionDone) { }
					@Override
					public void setMessage(String message) { }
					@Override
					public boolean isInterrupted() { return false; }
				};
				MathMapping mathMapping = simulationContext.createNewMathMapping(callback, NetworkGenerationRequirements.ComputeFullNoTimeout);
				MathDescription mathDesc = null;
				try {
					mathDesc = mathMapping.getMathDescription(callback);
				} catch (MappingException | MathException | MatrixException | ExpressionException | ModelException e1) {
					e1.printStackTrace();
				}
				String oldName = (String)evt.getOldValue();
				String newName = (String)evt.getNewValue();
				ArrayList<AnnotatedFunction> afList = fc.getOutputFunctionsList();
				List<Expression> changedExpressions = new ArrayList<> ();
				for(AnnotatedFunction af : afList) {
					if(af == null) {
						continue;
					}
					Expression exp = af.getExpression();
					if(exp == null || exp.getSymbols() == null || exp.getSymbols().length == 0) {
						continue;
					}
					String errMsg = "Failed to rename symbol '" + oldName + "' with '" + newName + "' in the Expression of Function '" + af.getName() + "'.";
					for(String symbol : exp.getSymbols()) {
						if(symbol.contentEquals(oldName)) {
							try {
								exp.substituteInPlace(new Expression(oldName), new Expression(newName));
								changedExpressions.add(exp);
							} catch (ExpressionException e) {
								e.printStackTrace();
								throw new RuntimeException(errMsg);
							}
						}
					}
				}
				if(changedExpressions.size() > 0) {
					try {
						simulationContext.setMathDescription(mathDesc);
						for(Expression exp : changedExpressions) {
							exp.bindExpression(outputFunctionContext);
						}
					} catch (ExpressionException | PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			}
		};
		ClientTaskDispatcher.dispatch(ownerTable, hashTable, new AsynchClientTask[]{task0}, false);
	}
	if (evt.getPropertyName().equals(GeometryOwner.PROPERTY_NAME_GEOMETRY)) {
		Geometry oldGeometry = (Geometry)evt.getOldValue();
		Geometry newGeometry = (Geometry)evt.getNewValue();
		// changing from ode to pde
		if (oldGeometry.getDimension() == 0 && newGeometry.getDimension() > 0) {
			fireTableStructureChanged();
			setData(getOutputFunctionContext().getOutputFunctionsList());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 2:01:23 PM)
 * @param aValue java.lang.Object
 * @param rowIndex int
 * @param columnIndex int
 */
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	AnnotatedFunction outputFunction = getValueAt(rowIndex);
	switch (columnIndex){
		case COLUMN_OUTPUTFN_EXPRESSION:{
			try {
				Expression exp = new Expression((String)aValue);
				if (exp.compareEqual(outputFunction.getExpression())) {
					return;
				}
				exp.bindExpression(outputFunctionContext);
				VariableType vt = outputFunctionContext.computeFunctionTypeWRTExpression(outputFunction, exp);
				if (!vt.compareEqual(outputFunction.getFunctionType())) {
				}
				outputFunction.setExpression(exp);
				
				fireTableRowsUpdated(rowIndex,rowIndex);
				outputFunctionContext.firePropertyChange("outputFunctions", null, outputFunctionContext.getOutputFunctionsList());
			} catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
			} catch (InconsistentDomainException e) {
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
			}
			break;
		}
		case COLUMN_OUTPUTFN_VARIABLETYPE:{
			for (int i = 0; i < availableGeometryClasses.length; i++) {
				if(availableGeometryClasses[i].getName().equals(aValue)) {
					Domain newDomain = new Domain(availableGeometryClasses[i]);
					outputFunction.setDomain(newDomain);
					fireTableRowsUpdated(rowIndex,rowIndex);
					outputFunctionContext.firePropertyChange("outputFunctions", null, outputFunctionContext.getOutputFunctionsList());
					break;
				}
			}
			break;
		}
	}
}

private OutputFunctionContext getOutputFunctionContext() {
	return outputFunctionContext;
}

private GeometryClass[] availableGeometryClasses = null;
public void setAvailableDomains(GeometryClass[] availableGeometryClasses) {
	this.availableGeometryClasses = availableGeometryClasses;
	if(ownerTable.getColumnModel().getColumnCount() > COLUMN_OUTPUTFN_VARIABLETYPE) {
		TableColumn varTypeCol = ownerTable.getColumnModel().getColumn(COLUMN_OUTPUTFN_VARIABLETYPE);
		JComboBox<String> varTypesCB = new JComboBox<>();
		for (int i = 0; availableGeometryClasses != null && i < availableGeometryClasses.length; i++) {
			varTypesCB.addItem(availableGeometryClasses[i].getName());
		}
		varTypeCol.setCellEditor(new DefaultCellEditor(varTypesCB));
	}

}
public void setOutputFunctionContext(OutputFunctionContext newValue) {
	OutputFunctionContext oldValue = this.outputFunctionContext;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
		oldValue.getSimulationOwner().removePropertyChangeListener(this);
	}
	this.outputFunctionContext = newValue;
	if (this.outputFunctionContext!=null){
		this.outputFunctionContext.addPropertyChangeListener(this);
		newValue.getSimulationOwner().addPropertyChangeListener(this);
	}
	if (newValue == null) {
		setData(null);
	} else {
		fireTableStructureChanged();
		setData(newValue.getOutputFunctionsList());
	}
}

public Comparator<AnnotatedFunction> getComparator(int col, boolean ascending)
{
	return new FunctionColumnComparator(col, ascending);
}

}
