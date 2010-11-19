package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeListener;

import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;

@SuppressWarnings("serial")
public class BioModelEditorSpeciesTableModel extends ManageTableModel<SpeciesContext> implements PropertyChangeListener{

	private static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	private static final String PROPERTY_NAME_MODEL = "model";
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_STRUCTURE = 1;
	
	private Model model = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private String[] columnNames = new String[] {"Name", "Structure"};
	private JTable ownerTable = null;
	private String searchText = null;

	public BioModelEditorSpeciesTableModel(JTable table) {
		super();
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

	public Class<?> getColumnClass(int column) {
		switch (column){
		
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_STRUCTURE:{
				return String.class;
			}
		}
		return Object.class;
	}

	private void refreshData() {

		if (model == null){
			return;
		}
		rows.clear();
		SpeciesContext[] speciesContextList = model.getSpeciesContexts();
		if (speciesContextList != null) {
			for (SpeciesContext s : speciesContextList){
				if (searchText == null || searchText.length() == 0 || s.getName().startsWith(searchText)) {
					rows.add(s);
				}
			}
		}
		fireTableDataChanged();
	}

	public Object getValueAt(int row, int column) {
		if (model == null) {
			return null;
		}
		try{
			if (row >= 0 && row < super.getRowCount()) {
				SpeciesContext speciesContext = (SpeciesContext)getData().get(row);
				switch (column) {
					case COLUMN_NAME: {
						return speciesContext.getName();
					} 
					case COLUMN_STRUCTURE: {
						return speciesContext.getStructure().getName();
					} 
				}
			} else {
				if (column == COLUMN_NAME) {
					return BioModelEditor.ADD_NEW_HERE_TEXT;
				} 
			}
			return null;
		} catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	/**
	 * The hasListeners method was generated to support the propertyChange field.
	 */
	public synchronized boolean hasListeners(java.lang.String propertyName) {
		return getPropertyChange().hasListeners(propertyName);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

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

	public void setValueAt(Object value, int row, int column) {
		if (model == null) {
			return;
		}
		try{
			if (row >= 0 && row < super.getRowCount()) {
				SpeciesContext speciesContext = getData().get(row);
				switch (column) {
				case COLUMN_NAME: {
					speciesContext.setHasOverride(true);
					speciesContext.setName((String)value);
					break;
				} 
				case COLUMN_STRUCTURE: {
					speciesContext.getStructure().setName((String)value);
					break;
				} 
				}
			} else {
				SpeciesContext freeSpeciesContext = new SpeciesContext(new Species(model.getFreeSpeciesName(), null), model.getStructures()[0]);
				switch (column) {
				case COLUMN_NAME: {
					if (!value.equals(BioModelEditor.ADD_NEW_HERE_TEXT)) {
						freeSpeciesContext.getSpecies().setCommonName((String)value);
						freeSpeciesContext.setHasOverride(true);
						freeSpeciesContext.setName((String)value);
					}
					break;
				} 
				case COLUMN_STRUCTURE: {
					freeSpeciesContext.getStructure().setName((String)value);
					break;
				} 
				}
				model.addSpecies(freeSpeciesContext.getSpecies());
				model.addSpeciesContext(freeSpeciesContext);
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage(), e);
		}
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
