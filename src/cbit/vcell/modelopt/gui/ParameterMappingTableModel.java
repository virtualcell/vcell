package cbit.vcell.modelopt.gui;
import java.util.*;

import org.vcell.util.BeanUtils;

import cbit.vcell.model.Model;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
import cbit.vcell.geometry.*;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.DivideByZeroException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ParameterMappingTableModel extends org.vcell.util.gui.sorttable.ManageTableModel implements java.beans.PropertyChangeListener {

	private class ParameterColumnComparator implements Comparator {
		protected int index;
		protected boolean ascending;

		public ParameterColumnComparator(int index, boolean ascending){
			this.index = index;
			this.ascending = ascending;
		}
		
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(Object o1, Object o2){
			
			cbit.vcell.modelopt.ParameterMappingSpec pms1 = (cbit.vcell.modelopt.ParameterMappingSpec)o1;
			cbit.vcell.modelopt.ParameterMappingSpec pms2 = (cbit.vcell.modelopt.ParameterMappingSpec)o2;
			cbit.vcell.model.Parameter parm1 = pms1.getModelParameter();
			cbit.vcell.model.Parameter parm2 = pms2.getModelParameter();
			
			switch (index){
				case COLUMN_NAME:{
					if (ascending){
						return parm1.getName().compareToIgnoreCase(parm2.getName());
					}else{
						return parm2.getName().compareToIgnoreCase(parm1.getName());
					}
					//break;
				}
				case COLUMN_MODELVALUE:{
					try {
						Double parm1_Value = new Double(parm1.getExpression().evaluateConstant());
						Double parm2_Value = new Double(parm2.getExpression().evaluateConstant());
						if (ascending){
							return parm1_Value.compareTo(parm2_Value);
						}else{
							return parm2_Value.compareTo(parm1_Value);
						}						
					} catch (ExpressionException e1) {
						e1.printStackTrace(System.out);
						if (ascending){
							return parm1.getExpression().infix().compareToIgnoreCase(parm2.getExpression().infix());
						}else{
							return parm2.getExpression().infix().compareToIgnoreCase(parm1.getExpression().infix());
						}
					} 

					//break;
				}
				case COLUMN_SCOPE:{
					if (ascending){
						return parm1.getNameScope().getName().compareToIgnoreCase(parm2.getNameScope().getName());
					}else{
						return parm2.getNameScope().getName().compareToIgnoreCase(parm1.getNameScope().getName());
					}
					//break;
				}
				case COLUMN_SELECTED:{
					String sel1 = (new Boolean(pms1.isSelected())).toString();
					String sel2 = (new Boolean(pms2.isSelected())).toString();
					if (ascending){
						return sel1.compareToIgnoreCase(sel2);
					}else{
						return sel2.compareToIgnoreCase(sel1);
					}
					//break;
				}
				case COLUMN_CURRENTVALUE:{
					if (ascending){
						return (new Double(pms1.getCurrent())).compareTo(new Double(pms2.getCurrent()));
					}else{
						return (new Double(pms2.getCurrent())).compareTo(new Double(pms1.getCurrent()));
					}
					//break;
				}
				case COLUMN_LOWVALUE:{
					if (ascending){
						return (new Double(pms1.getLow())).compareTo(new Double(pms2.getLow()));
					}else{
						return (new Double(pms2.getLow())).compareTo(new Double(pms1.getLow()));
					}
					//break;
				}
				case COLUMN_HIGHVALUE:{
					if (ascending){
						return (new Double(pms1.getHigh())).compareTo(new Double(pms2.getHigh()));
					}else{
						return (new Double(pms2.getHigh())).compareTo(new Double(pms1.getHigh()));
					}
					//break;
				}
			}
			return 1;
		}
	}
	private final static int NUM_COLUMNS = 8;
	private final static int COLUMN_NAME = 0;
	private final static int COLUMN_SCOPE = 1;
	private final static int COLUMN_MODELVALUE = 2;
	private final static int COLUMN_SELECTED = 3;
	public final static int COLUMN_CURRENTVALUE = 4;
	private final static int COLUMN_LOWVALUE = 5;
	private final static int COLUMN_HIGHVALUE = 6;
	public final static int COLUMN_SOLUTION = 7;
	private final static String LABELS[] = { "parameter", "context",  "model value", "optimize", "initial guess", "lower", "upper", "solution"  };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private final int indexes[] = new int[0];
	private cbit.vcell.modelopt.ParameterEstimationTask fieldParameterEstimationTask = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public ParameterMappingTableModel() {
	super();
	addPropertyChangeListener(this);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	switch (column){
		case COLUMN_NAME:{
			return String.class;
		}
		case COLUMN_SCOPE:{
			return String.class;
		}
		case COLUMN_CURRENTVALUE:{
			return Double.class;
		}
		case COLUMN_MODELVALUE:{
			return Double.class;
		}
		case COLUMN_HIGHVALUE:{
			return Double.class;
		}
		case COLUMN_LOWVALUE:{
			return Double.class;
		}
		//case COLUMN_SCALE:{
			//return Double.class;
		//}
		case COLUMN_SELECTED:{
			return Boolean.class;
		}
		case COLUMN_SOLUTION:{
			return Double.class;
		}
		//case COLUMN_MATHNAME:{
			//return String.class;
		//}
		default:{
			return Object.class;
		}
	}
}


/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return NUM_COLUMNS;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}


/**
 * Gets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
 * @return The parameterEstimationTask property value.
 * @see #setParameterEstimationTask
 */
public cbit.vcell.modelopt.ParameterEstimationTask getParameterEstimationTask() {
	return fieldParameterEstimationTask;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private cbit.vcell.modelopt.ParameterMappingSpec getParameterMappingSpec(int row) {

	if (getParameterEstimationTask()==null){
		return null;
	}
	if (row<0){
		throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range");
	}
	int count = getParameterEstimationTask().getModelOptimizationSpec().getParameterMappingSpecs().length;
	int index = row;
	if (index<count){
		return getParameterEstimationTask().getModelOptimizationSpec().getParameterMappingSpecs()[index];
	}
	throw new RuntimeException("rowIndex = "+row+" not found");
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return rows.size();
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private List getUnsortedParameters() {

	if (getParameterEstimationTask()==null){
		return null;
	}
	
	int count = getParameterEstimationTask().getModelOptimizationSpec().getParameterMappingSpecs().length;

	java.util.ArrayList list = new java.util.ArrayList();
	for (int i = 0; i < count; i++){
		list.add(getParameterMappingSpec(i));
	}
	return list;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	cbit.vcell.modelopt.ParameterMappingSpec parameterMappingSpec = (cbit.vcell.modelopt.ParameterMappingSpec)getData().get(row);
	switch (col){
		case COLUMN_NAME:{
			//return getBioModel().getModel().getNameScope().getSymbolName(parameter);
			return parameterMappingSpec.getModelParameter().getName();
		}
		case COLUMN_SCOPE:{
			if (parameterMappingSpec.getModelParameter() instanceof cbit.vcell.model.Kinetics.UnresolvedParameter){
				return "unresolved";
			}else if (parameterMappingSpec.getModelParameter().getNameScope()==null){
				return "null";
			} if (parameterMappingSpec.getModelParameter() instanceof ModelParameter) {
				return "Model";
			} else{
				return parameterMappingSpec.getModelParameter().getNameScope().getName();
			}
		}
		case COLUMN_MODELVALUE:{
			try {
				return new Double(parameterMappingSpec.getModelParameter().getExpression().evaluateConstant());
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				return null;
			}
		}
		case COLUMN_CURRENTVALUE:{
			return new Double(parameterMappingSpec.getCurrent());
		}
		case COLUMN_LOWVALUE:{
			return new Double(parameterMappingSpec.getLow());
		}
		case COLUMN_HIGHVALUE:{
			return new Double(parameterMappingSpec.getHigh());
		}
		//case COLUMN_SCALE:{
			//return new Double(parameterMappingSpec.getScale());
		//}
		case COLUMN_SELECTED:{
			return new Boolean(parameterMappingSpec.isSelected());
		}
		case COLUMN_SOLUTION:{
			if (getParameterEstimationTask()!=null && getParameterEstimationTask().getOptimizationResultSet()!=null){
				//
				// find mapping from this parameterMappingSpec to the optimization parameter.  Then lookup the opt parameter in the result set.
				//
				for (int i = 0; i < getParameterEstimationTask().getModelOptimizationMapping().getParameterMappings().length; i++){
					if (getParameterEstimationTask().getModelOptimizationMapping().getParameterMappings()[i].getModelParameter() == parameterMappingSpec.getModelParameter()){
						cbit.vcell.modelopt.ParameterMapping parameterMapping = getParameterEstimationTask().getModelOptimizationMapping().getParameterMappings()[i];
						for (int j = 0; getParameterEstimationTask().getOptimizationResultSet().getParameterNames()!=null && j < getParameterEstimationTask().getOptimizationResultSet().getParameterNames().length; j++){
							if (getParameterEstimationTask().getOptimizationResultSet().getParameterNames()[j].equals(parameterMapping.getOptParameter().getName())){
								return new Double(getParameterEstimationTask().getOptimizationResultSet().getParameterValues()[j]);
							}
						}
					}
				}
			}
			return null;
		}
		//case COLUMN_MATHNAME:{
			//if (getOptimizationResultSet()!=null && getModelOptimizationMapping()!=null){
				////
				//// find mapping from this parameterMappingSpec to the optimization parameter.  Then lookup the opt parameter in the result set.
				////
				//for (int i = 0; i < getModelOptimizationMapping().getParameterMappings().length; i++){
					//if (getModelOptimizationMapping().getParameterMappings()[i].getModelParameter() == parameterMappingSpec.getModelParameter()){
						//cbit.vcell.modelopt.ParameterMapping parameterMapping = getModelOptimizationMapping().getParameterMappings()[i];
						//return parameterMapping.getOptParameter().getName();
					//}
				//}
			//}
			//return null;
		//}
		default:{
			return null;
		}
	}
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:27:46 AM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	cbit.vcell.modelopt.ParameterMappingSpec parameterMapping = (cbit.vcell.modelopt.ParameterMappingSpec)getData().get(rowIndex);
	if (columnIndex == COLUMN_NAME){
		return parameterMapping.getModelParameter().isNameEditable();
	}else if (columnIndex == COLUMN_SCOPE){
		return false;
	}else if (columnIndex == COLUMN_SELECTED){
		return true;
	}else if (columnIndex == COLUMN_CURRENTVALUE){
		return true;
	}else if (columnIndex == COLUMN_LOWVALUE){
		return true;
	}else if (columnIndex == COLUMN_HIGHVALUE){
		return true;
	//}else if (columnIndex == COLUMN_SCALE){
		//return true;
	}else if (columnIndex == COLUMN_MODELVALUE){
		return false;
		// return parameterMapping.getModelParameter().isExpressionEditable();
	}else if (columnIndex == COLUMN_SOLUTION){
		return false;
	//}else if (columnIndex == COLUMN_MATHNAME){
		//return false;
	}else{
		return false;
	}
}


/**
 * isSortable method comment.
 */
public boolean isSortable(int col) {
	switch (col){
		case COLUMN_NAME:{
			return true;
		}
		case COLUMN_SCOPE:{
			return true;
		}
		case COLUMN_CURRENTVALUE:{
			return true;
		}
		case COLUMN_MODELVALUE:{
			return true;
		}
		case COLUMN_HIGHVALUE:{
			return true;
		}
		case COLUMN_LOWVALUE:{
			return true;
		}
		//case COLUMN_SCALE:{
			//return true;
		//}
		case COLUMN_SELECTED:{
			return true;
		}
		case COLUMN_SOLUTION:{
			return false;
		}
		//case COLUMN_MATHNAME:{
			//return false;
		//}
		default:{
			return false;
		}
	}
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("parameterEstimationTask")) {
		cbit.vcell.modelopt.ParameterEstimationTask oldValue = (cbit.vcell.modelopt.ParameterEstimationTask)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
			cbit.vcell.modelopt.ParameterMappingSpec[] oldPMS = oldValue.getModelOptimizationSpec().getParameterMappingSpecs();
			for (int i = 0; oldPMS!=null && i < oldPMS.length; i++){
				oldPMS[i].removePropertyChangeListener(this);
			}
		}
		cbit.vcell.modelopt.ParameterEstimationTask newValue = (cbit.vcell.modelopt.ParameterEstimationTask)evt.getNewValue();
		if (newValue!=null){
			newValue.addPropertyChangeListener(this);
			cbit.vcell.modelopt.ParameterMappingSpec[] newPMS = newValue.getModelOptimizationSpec().getParameterMappingSpecs();
			for (int i = 0; newPMS!=null && i < newPMS.length; i++){
				newPMS[i].addPropertyChangeListener(this);
			}
		}
		setData(getUnsortedParameters());
		fireTableDataChanged();
	}
	if (evt.getSource() == getParameterEstimationTask() && evt.getPropertyName().equals("optimizationResultSet")) {
		setData(getUnsortedParameters());
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.modelopt.ModelOptimizationSpec && evt.getPropertyName().equals("parameterMappingSpecs")) {
		setData(getUnsortedParameters());
		fireTableDataChanged();
	}
	
	if (evt.getSource() instanceof cbit.vcell.modelopt.ParameterMappingSpec) {
		fireTableDataChanged();
	}
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Sets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
 * @param parameterEstimationTask The new value for the property.
 * @see #getParameterEstimationTask
 */
public void setParameterEstimationTask(cbit.vcell.modelopt.ParameterEstimationTask parameterEstimationTask) {
	cbit.vcell.modelopt.ParameterEstimationTask oldValue = fieldParameterEstimationTask;
	fieldParameterEstimationTask = parameterEstimationTask;
	firePropertyChange("parameterEstimationTask", oldValue, parameterEstimationTask);
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.modelopt.ParameterMappingSpec parameterMappingSpec = (cbit.vcell.modelopt.ParameterMappingSpec)getData().get(rowIndex);
	//try {
		switch (columnIndex){
			case COLUMN_SELECTED:{
				//try {
					if (aValue instanceof Boolean){
						Boolean bSelected = (Boolean)aValue;
						if (parameterMappingSpec.isSelected() != bSelected.booleanValue()){
							parameterMappingSpec.setSelected(bSelected.booleanValue());
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				//}
				break;
			}
			case COLUMN_LOWVALUE:{
				if (aValue instanceof Double){
					double value = ((Double)aValue).doubleValue();
					parameterMappingSpec.setLow(value);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}else if (aValue instanceof String) {
					String newDoubleString = (String)aValue;
					try {
						parameterMappingSpec.setLow(Double.parseDouble(newDoubleString));
						fireTableRowsUpdated(rowIndex,rowIndex);
					}catch (NumberFormatException e){
						if (newDoubleString.equals("") || newDoubleString.equalsIgnoreCase("-Infinity") || newDoubleString.equalsIgnoreCase("-Inf")){
							parameterMappingSpec.setLow(Double.NEGATIVE_INFINITY);
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}
				break;
			}
			case COLUMN_HIGHVALUE:{
				if (aValue instanceof Double){
					double value = ((Double)aValue).doubleValue();
					parameterMappingSpec.setHigh(value);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}else if (aValue instanceof String) {
					String newDoubleString = (String)aValue;
					try {
						parameterMappingSpec.setHigh(Double.parseDouble(newDoubleString));
						fireTableRowsUpdated(rowIndex,rowIndex);
					}catch (NumberFormatException e){
						if (newDoubleString.equals("") || newDoubleString.equalsIgnoreCase("Infinity") || newDoubleString.equalsIgnoreCase("Inf")){
							parameterMappingSpec.setHigh(Double.POSITIVE_INFINITY);
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}
				break;
			}
			case COLUMN_CURRENTVALUE:{
				if (aValue instanceof Double){
					double value = ((Double)aValue).doubleValue();
					parameterMappingSpec.setCurrent(value);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}else if (aValue instanceof String) {
					String newDoubleString = (String)aValue;
					parameterMappingSpec.setCurrent(Double.parseDouble((String)aValue));
					fireTableRowsUpdated(rowIndex,rowIndex);
				}
				break;
			}
			//case COLUMN_SCALE:{
				//if (aValue instanceof Double){
					//double value = ((Double)aValue).doubleValue();
					//parameterMappingSpec.setScale(value);
					//fireTableRowsUpdated(rowIndex,rowIndex);
				//}else if (aValue instanceof String) {
					//String newDoubleString = (String)aValue;
					//parameterMappingSpec.setScale(Double.parseDouble((String)aValue));
					//fireTableRowsUpdated(rowIndex,rowIndex);
				//}
				//break;
			//}
		}
	//}catch (java.beans.PropertyVetoException e){
		//e.printStackTrace(System.out);
	//}
}


  public void sortColumn(int col, boolean ascending)
  {
    Collections.sort(rows,
      new ParameterColumnComparator(col, ascending));
  }
}