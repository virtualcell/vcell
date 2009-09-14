package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedExpression;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 4:07:40 PM)
 * @author: Ion Moraru
 */
public class OutputFunctionsListTableModel extends ManageTableModel implements PropertyChangeListener {
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

	public final static int COLUMN_OUTPUTFN_NAME = 0;
	public final static int COLUMN_OUTPUTFN_EXPRESSION = 1;
	
	private final static int NUM_COLUMNS = 2;
	private OutputFunctionContext outputFunctionContext = null;
	private String[] columnNames = new String[] {"Name", "Expression"};
	private JTable ownerTable = null;

/**
 * SimulationListTableModel constructor comment.
 */
public OutputFunctionsListTableModel(JTable table) {
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
	if (outputFunctionContext != null) {
		return outputFunctionContext.getOutputFunctionsList().size();
	} else {
		return 0;
	}
}

public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_OUTPUTFN_NAME:{
			return String.class;
		}
		case COLUMN_OUTPUTFN_EXPRESSION:{
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
		AnnotatedFunction obsFunction = (AnnotatedFunction)getData().get(row);
		if (row >= 0 && row < getRowCount()) {
			switch (column) {
				case COLUMN_OUTPUTFN_NAME: {
					return obsFunction.getName();
				} 
				case COLUMN_OUTPUTFN_EXPRESSION: {
					if (obsFunction.getExpression() == null) {
						return null; 
					} else {
						return new ScopedExpression(obsFunction.getExpression(),outputFunctionContext.getNameScope(), true);
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

public boolean isSortable(int col) {
	if (col == COLUMN_OUTPUTFN_NAME){
		return true;
	}else {
		return false;
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
	if (columnIndex == COLUMN_OUTPUTFN_NAME){
		return false;
	}else {
		return true;
	}
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getOutputFunctionContext() && evt.getPropertyName().equals("outputFunctions")) {
		setData(outputFunctionContext.getOutputFunctionsList());
		fireTableRowsUpdated(0, getRowCount());
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
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ObservablesListTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ObservablesListTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	AnnotatedFunction outputFunction = (AnnotatedFunction)getData().get(rowIndex);
	switch (columnIndex){
		case COLUMN_OUTPUTFN_EXPRESSION:{
			try {
				if (aValue instanceof ScopedExpression){
					Expression exp = ((ScopedExpression)aValue).getExpression();
					// bind expression to outputFunctionContext
					exp.bindExpression(outputFunctionContext);
					outputFunction.setExpression(exp);
				}else if (aValue instanceof String) {
					Expression exp = new Expression((String)aValue);
					exp.bindExpression(outputFunctionContext);
					outputFunction.setExpression(exp);
				}
				// both the 'fire's are being used so that the scopedExpressionRenderer renders the exprs properly, esp with num/dem exprs.
				fireTableDataChanged();
				fireTableRowsUpdated(rowIndex,rowIndex);
			} catch (ExpressionException e){
				e.printStackTrace(System.out);
				cbit.vcell.client.PopupGenerator.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
			}
			break;
		}

	}
}

public OutputFunctionContext getOutputFunctionContext() {
	return outputFunctionContext;
}

public void setOutputFunctionContext(OutputFunctionContext argOutputFnContext) {
	if (argOutputFnContext!=null){
		argOutputFnContext.removePropertyChangeListener(this);
	}
	this.outputFunctionContext = argOutputFnContext;
	if (this.outputFunctionContext!=null){
		this.outputFunctionContext.addPropertyChangeListener(this);
	}
	if (argOutputFnContext != null) {
		setData(argOutputFnContext.getOutputFunctionsList());
	}
	// fireTableDataChanged();
}

public void sortColumn(int col, boolean ascending)
{
  Collections.sort(rows, new FunctionColumnComparator(col, ascending));
  fireTableDataChanged();
}

}