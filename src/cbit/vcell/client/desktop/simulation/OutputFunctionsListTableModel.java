package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.VariableType;
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
	private String[] columnNames = new String[] {"Name", "Expression", "Subdomain"};
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
	return columnNames.length - ((getOutputFunctionContext() == null 
			|| getOutputFunctionContext().getSimulationOwner().getGeometry().getDimension() > 0) ? 0 : 1);
}

/**
 * getColumnCount method comment.
 */
public String getColumnName(int column) {
	return columnNames[column];
}

public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_OUTPUTFN_NAME:{
			return String.class;
		}
		case COLUMN_OUTPUTFN_EXPRESSION:{
			return cbit.gui.ScopedExpression.class;
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
			AnnotatedFunction obsFunction = (AnnotatedFunction)getData().get(row);
			switch (column) {
				case COLUMN_OUTPUTFN_NAME: {
					return obsFunction.getName();
				} 
				case COLUMN_OUTPUTFN_EXPRESSION: {
					if (obsFunction.getExpression() == null) {
						return null; 
					} else {
						final MathDescription mathDescription = outputFunctionContext.getSimulationOwner().getMathDescription();
						final VariableType varType = obsFunction.getFunctionType();
						AutoCompleteSymbolFilter autoCompleteSymbolFilter = new AutoCompleteSymbolFilter() {
							public boolean accept(SymbolTableEntry ste) {
								if (mathDescription.isSpatial()) {
									if (ste instanceof AnnotatedFunction) {
										return varType.getVariableDomain().equals(((AnnotatedFunction)ste).getFunctionType().getVariableDomain());
									}			
									if (ste instanceof Function) {
										Function f = (Function)ste;
										try {
											outputFunctionContext.validateExpression(f, varType, f.getExpression());
											return true;
										} catch (Exception e) {
											return false;
										}
									}
									
									// must be Variables(Volume, Membrane, VolumeRegion, MembraneRegion)
									VariableType vt = VariableType.getVariableType((Variable)ste);
									if (!varType.getVariableDomain().equals(vt.getVariableDomain())) {
										return false;
									}
								}
						
								return true;
							}
							public boolean acceptFunction(String funcName) {
								return true;
							}
						};
						
						return new ScopedExpression(obsFunction.getExpression(),outputFunctionContext.getNameScope(), true, autoCompleteSymbolFilter);
					}
				}
				case COLUMN_OUTPUTFN_VARIABLETYPE: {
					return obsFunction.getFunctionType().getVariableDomain().getName();
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
	return columnIndex == COLUMN_OUTPUTFN_EXPRESSION;
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
	if (evt.getPropertyName().equals("geometry")) {
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
	AnnotatedFunction outputFunction = (AnnotatedFunction)getData().get(rowIndex);
	switch (columnIndex){
		case COLUMN_OUTPUTFN_EXPRESSION:{
			try {
				Expression exp = new Expression((String)aValue);
				exp.bindExpression(outputFunctionContext);
				getOutputFunctionContext().validateExpression(outputFunction, outputFunction.getFunctionType(), exp);
				outputFunction.setExpression(exp);
				
				// both the 'fire's are being used so that the scopedExpressionRenderer renders the exprs properly, esp with num/dem exprs.
				fireTableDataChanged();
				fireTableRowsUpdated(rowIndex,rowIndex);
				outputFunctionContext.firePropertyChange("outputFunctions", null, outputFunctionContext.getOutputFunctionsList());
			} catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
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
		argOutputFnContext.getSimulationOwner().removeGeometryPropertyChangeListener(this);
	}
	this.outputFunctionContext = argOutputFnContext;
	if (this.outputFunctionContext!=null){
		this.outputFunctionContext.addPropertyChangeListener(this);
		argOutputFnContext.getSimulationOwner().addGeometryPropertyChangeListener(this);
	}
	if (argOutputFnContext != null) {
		fireTableStructureChanged();
		setData(argOutputFnContext.getOutputFunctionsList());
	}
}

public void sortColumn(int col, boolean ascending)
{
  Collections.sort(rows, new FunctionColumnComparator(col, ascending));
  fireTableDataChanged();
}

public AnnotatedFunction getSelectedOutputFunction() {
	int row = ownerTable.getSelectedRow();
	if (row < 0) {
		return null;
	}
	return (AnnotatedFunction)getData().get(row);
}

public void selectOutputFunction(AnnotatedFunction annotatedFunction) {
	for (int i = 0; i < getRowCount(); i ++) {
		if (getValueAt(i, COLUMN_OUTPUTFN_NAME).equals(annotatedFunction.getName())) {
			ownerTable.changeSelection(i, 0, false, false);
			break;
		}
	}
}
}