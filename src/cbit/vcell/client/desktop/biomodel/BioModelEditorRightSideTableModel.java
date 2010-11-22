package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.JTable;

import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.model.Model;
import cbit.vcell.parser.SymbolTable;

/**
 * BioModelEditorRightSideTableModel extends DefaultSortTableModel and always has an extra row for adding new row.
 * 
 * 
 * It has the following abstract methods 
 * protected abstract String checkInputValue(String inputValue, int row, int column);
 * after a value is typed, check to see if the new value is valid. If not, 
 * editing is not stopped and tooltip is set to the error message.
 *  
 * protected abstract SymbolTable getSymbolTable(int row, int column);
 * protected abstract AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column);
 * these 2 methods provide the auto completion if there is a symbol table.  
 *  
 * protected abstract Set<String> getAutoCompletionWords(int row, int column);
 * this method provodes auto completion if there is no symbol table.
 *  
 * @author fgao
 *
 */

@SuppressWarnings("serial")
public abstract class BioModelEditorRightSideTableModel<T> extends DefaultSortTableModel<T> implements PropertyChangeListener, AutoCompleteTableModel {
	protected static final String PROPERTY_NAME_MODEL = "model";
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected static final String PROPERTY_NAME_ERROR_NODE = "errorNode";
	
	protected Model model = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected JTable ownerTable = null;
	protected String searchText = null;
	
	public BioModelEditorRightSideTableModel(JTable table) {
		super(null);
		ownerTable = table;
		addPropertyChangeListener(this);
	}
	
	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * getRowCount method comment.
	 */
	@Override
	public int getRowCount() {
		return super.getRowCount() + (searchText == null || searchText.length() == 0 ? 1 : 0);
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
	protected abstract void refreshData();

	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() == this) {
			if (evt.getPropertyName().equals(PROPERTY_NAME_MODEL)) {
				refreshData();
				Model oldValue = (Model)evt.getOldValue();
				if (oldValue != null) {
					oldValue.removePropertyChangeListener(this);
				}
				Model newValue = (Model)evt.getNewValue();
				if (newValue != null) {
					newValue.addPropertyChangeListener(this);
				}
			} else if (evt.getPropertyName().equals(PROPERTY_NAME_SEARCH_TEXT)) {
				refreshData();
			}
		}
		if (evt.getSource() == model) {
			refreshData();
		}
	}
	
	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public void setModel(Model newValue) {
		Model oldValue = model;
		model = newValue;
		firePropertyChange(PROPERTY_NAME_MODEL, oldValue, newValue);
	}

	public void setSearchText(String newValue) {
		String oldValue = searchText;
		searchText = newValue;
		firePropertyChange(PROPERTY_NAME_SEARCH_TEXT, oldValue, newValue);		
	}
}
