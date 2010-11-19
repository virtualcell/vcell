package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeListener;

import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class BioModelEditorStructureTableModel extends ManageTableModel<Structure> implements PropertyChangeListener{

	private static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	private static final String PROPERTY_NAME_MODEL = "model";
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_TYPE = 1;
	public final static int COLUMN_INSIDE_COMPARTMENT = 2;
	public final static int COLUMN_OUTSIDE_COMPARTMENT = 3;
	
	private Model model = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private String[] columnNames = new String[] {"Name", "Type", "Inside Compartment", "Outside Compartment"};
	private JTable ownerTable = null;
	private String searchText = null;

	public BioModelEditorStructureTableModel(JTable table) {
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
			case COLUMN_TYPE:{
				return String.class;
			}
			case COLUMN_INSIDE_COMPARTMENT:{
				return String.class;
			}
			case COLUMN_OUTSIDE_COMPARTMENT:{
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
		Structure[] structureList = model.getStructures();
		if (structureList != null) {
			for (Structure s : structureList){
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
				Structure s = (Structure)getData().get(row);
				switch (column) {
					case COLUMN_NAME: {
						return s.getName();
					} 
					case COLUMN_TYPE: {
						return s.getTypeName();
					} 
					case COLUMN_INSIDE_COMPARTMENT: {
						Feature insideFeature = s instanceof Membrane ? ((Membrane)s).getInsideFeature() : null;
						return insideFeature != null ? insideFeature.getName() : "n/a";
					} 
					case COLUMN_OUTSIDE_COMPARTMENT: {
						Feature outsideFeature = s instanceof Membrane ? ((Membrane)s).getOutsideFeature() : null;
						return outsideFeature != null ? outsideFeature.getName() : "n/a";
					} 
				}
			} else {
				if (column == COLUMN_NAME) {
					return BioModelEditor.ADD_NEW_HERE_TEXT;
				} 
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	/**
	 * The hasListeners method was generated to support the propertyChange field.
	 */
	public synchronized boolean hasListeners(java.lang.String propertyName) {
		return getPropertyChange().hasListeners(propertyName);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == COLUMN_TYPE) {
			return false;
		}
		if (column == COLUMN_INSIDE_COMPARTMENT || column == COLUMN_OUTSIDE_COMPARTMENT) {	
			if (row >= 0 && row < super.getRowCount()) {
				Structure s = (Structure)getData().get(row);
				return s instanceof Membrane;
			}
		}
			
		return true;
	}

	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() == this) {
			if (evt.getPropertyName().equals(PROPERTY_NAME_MODEL)) {
				refreshData();
				Model oldValue = (Model)evt.getOldValue();
				if (oldValue != null) {
					oldValue.removePropertyChangeListener(this);
					Structure[] structures = oldValue.getStructures();
					if (structures != null) {
						for (Structure s : structures) {
							s.removePropertyChangeListener(this);
						}
					}
				}
				Model newValue = (Model)evt.getNewValue();
				if (newValue != null) {
					newValue.addPropertyChangeListener(this);
					Structure[] structures = newValue.getStructures();
					if (structures != null) {
						for (Structure s : structures) {
							s.addPropertyChangeListener(this);
						}
					}
				}
			} else if (evt.getPropertyName().equals(PROPERTY_NAME_SEARCH_TEXT)) {
				refreshData();
			}
		} else if (evt.getSource() == model && evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
			Structure[] oldValue = (Structure[]) evt.getOldValue();
			if (oldValue != null) {
				for (Structure s : oldValue) {
					s.removePropertyChangeListener(this);
				}
			}
			Structure[] newValue = (Structure[]) evt.getOldValue();
			if (newValue != null) {
				for (Structure s : newValue) {
					s.addPropertyChangeListener(this);
				}
			}
			refreshData();
		} else if (evt.getSource() instanceof Structure) {
			fireTableDataChanged();
		}
	}
	
	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public void setValueAt(Object value, int row, int column) {
		if (model == null || value == null || value.toString().length() == 0) {
			return;
		}
		try{
			String newName = (String)value;
			if (row >= 0 && row < super.getRowCount()) {
				Structure s = getData().get(row);
				switch (column) {
				case COLUMN_NAME: {
					if (!value.equals(s.getName())) {
						s.setName(newName);
						s.getStructureSize().setName(Structure.getDefaultStructureSizeName(newName));
						if (s instanceof Membrane) {
							((Membrane)s).getMembraneVoltage().setName(Membrane.getDefaultMembraneVoltageName(newName));
						}
					}
					break;
				} 
				case COLUMN_INSIDE_COMPARTMENT: {
					Structure insideFeature = model.getStructure(newName);
					if (insideFeature instanceof Membrane) {
						DialogUtils.showErrorDialog(ownerTable, Structure.TYPE_NAME_FEATURE + " is expected!");
					} else {
						((Membrane)s).setInsideFeature((Feature)insideFeature);
					}
					break;
				} 
				case COLUMN_OUTSIDE_COMPARTMENT: {
					Structure outsideFeature = model.getStructure(newName);
					if (outsideFeature instanceof Membrane) {
						DialogUtils.showErrorDialog(ownerTable, Structure.TYPE_NAME_FEATURE + " is expected!");
					} else {
						((Membrane)s).setOutsideFeature((Feature)outsideFeature);
					}
					break;
				} 
				}
			} else {
				switch (column) {
				case COLUMN_NAME: {
					Feature parentFeature = null;
					for (int i = model.getNumStructures() - 1; i >= 0; i --) {
						if (model.getStructures()[i] instanceof Feature) {
							parentFeature = (Feature) model.getStructures()[i];
							break;
						}
					}
					model.addFeature(newName, parentFeature, model.getFreeMembraneName());
					break;
				} 
				}
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

	@Override
	public boolean isSortable(int col) {
		return false;
	}
}
