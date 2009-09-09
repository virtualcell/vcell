package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.math.Function;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedExpression;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 4:07:40 PM)
 * @author: Ion Moraru
 */
//public class ObservablesListTableModel extends AbstractTableModel implements PropertyChangeListener {
public class ObservablesListTableModel extends ManageTableModel implements PropertyChangeListener {

	private class FunctionColumnComparator implements Comparator<Function> {
		protected int index;
		protected boolean ascending;

		public FunctionColumnComparator(int index, boolean ascending){
			this.index = index;
			this.ascending = ascending;
		}
		
		public int compare(Function parm1, Function parm2){
			
			switch (index){
				case COLUMN_OBS_NAME:{
					if (ascending){
						return parm1.getName().compareToIgnoreCase(parm2.getName());
					}else{
						return parm2.getName().compareToIgnoreCase(parm1.getName());
					}
				}
			}
			return 1;
		}
	}
	public boolean isSortable(int col) {
		return (col != COLUMN_OBS_EXPRESSION);
	}
	private final static int COLUMN_OBS_NAME = 0;
	private final static int COLUMN_OBS_EXPRESSION = 1;
	
	private final static int NUM_COLUMNS = 2;
//	private Function[] observableFunctionsList = null;
	private SimulationOwner simulationOwner = null;
	private String[] columnNames = new String[] {"Name", "Expression"};
	private JTable ownerTable = null;

/**
 * SimulationListTableModel constructor comment.
 */
public ObservablesListTableModel(JTable table) {
	super();
	ownerTable = table;
}

/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return columnNames.length;
}

/**
 * getColumnCount method comment.
 */
public String getColumnName(int column) {
	return columnNames[column];
}


/**
 * getRowCount method comment.
 */
public int getRowCount() {
	if (simulationOwner != null) {
		return simulationOwner.getObservableFunctionsList().size();
	} else {
		return 0;
	}
}

public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_OBS_NAME:{
			return String.class;
		}
		case COLUMN_OBS_EXPRESSION:{
			return cbit.vcell.parser.ScopedExpression.class;
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
		Function obsFunction = (Function)getData().get(row);
		if (row >= 0 && row < getRowCount()) {
			switch (column) {
				case COLUMN_OBS_NAME: {
					return obsFunction.getName();
				} 
				case COLUMN_OBS_EXPRESSION: {
					if (obsFunction.getExpression() == null) {
						return null; 
					} else {
						return new ScopedExpression(obsFunction.getExpression(),simulationOwner.getNameScope(), true);
					}
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


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 1:56:12 PM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	return true;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getSimulationOwner() && evt.getPropertyName().equals("observableFunctions")) {
		setData(simulationOwner.getObservableFunctionsList());
		fireTableDataChanged();
	}
//	if (evt.getSource() == getSimulationWorkspace() && evt.getPropertyName().equals("status")) {
//		fireTableRowsUpdated(((Integer)evt.getNewValue()).intValue(), ((Integer)evt.getNewValue()).intValue());
//	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 2:01:23 PM)
 * @param aValue java.lang.Object
 * @param rowIndex int
 * @param columnIndex int
 */
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ObservablesListTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ObservablesListTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	Function obsFunction = (Function)getData().get(rowIndex);
	switch (columnIndex){
		case COLUMN_OBS_NAME:{
			 try {
				if (aValue instanceof String){
					String newName = (String)aValue;
					if (!obsFunction.getName().equals(newName)){
						// cannot set name on fn, so create new fn with new name, old expr; replace old fn with new fn in simOwner
						Function newObsFunction = new Function(newName, obsFunction.getExpression());
						getSimulationOwner().replaceObservableFunction(obsFunction, newObsFunction);
						// fireTableRowsUpdated(rowIndex,rowIndex);
					}
				}
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, e.getMessage());
			}
			break;
		}
		case COLUMN_OBS_EXPRESSION:{
			try {
				if (aValue instanceof ScopedExpression){
					Expression exp = ((ScopedExpression)aValue).getExpression();
					obsFunction.setExpression(exp);
				}else if (aValue instanceof String) {
					String newExpressionString = (String)aValue;
					obsFunction.setExpression(new Expression(newExpressionString));
				}
				// fireTableRowsUpdated(rowIndex,rowIndex);
			} catch (ExpressionException e){
				e.printStackTrace(System.out);
				cbit.vcell.client.PopupGenerator.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
			}
			break;
		}

	}
}

public void sortColumn(int col, boolean ascending)
{
  Collections.sort(rows, new FunctionColumnComparator(col, ascending));
  fireTableRowsUpdated(0,rows.size()-1);//Added to make sure formatted table cells display with enough space
}

public SimulationOwner getSimulationOwner() {
	return simulationOwner;
}

public void setSimulationOwner(SimulationOwner simulationOwner) {
	if (simulationOwner!=null){
		simulationOwner.removePropertyChangeListener(this);
	}
	this.simulationOwner = simulationOwner;
	if (this.simulationOwner!=null){
		this.simulationOwner.addPropertyChangeListener(this);
	}
}
}