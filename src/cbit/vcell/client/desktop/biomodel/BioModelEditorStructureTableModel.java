package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorStructureTableModel extends BioModelEditorRightSideTableModel<Structure> {

	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_TYPE = 1;
	public final static int COLUMN_INSIDE_COMPARTMENT = 2;
	public final static int COLUMN_OUTSIDE_COMPARTMENT = 3;	
	private static String[] columnNames = new String[] {"Name", "Type", "Inside", "Outside Parent"};

	public BioModelEditorStructureTableModel(JTable table) {
		super(table);
		columns = columnNames;
		ownerTable = table;
		addPropertyChangeListener(this);
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

	protected void refreshData() {
		rows.clear();
		if (model == null){
			return;
		}
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
			if (row >= 0 && row < rows.size()) {
				Structure structure = getValueAt(row);
				switch (column) {
					case COLUMN_NAME: {
						return structure.getName();
					} 
					case COLUMN_TYPE: {
						return structure.getTypeName();
					} 
					case COLUMN_INSIDE_COMPARTMENT: {
						Feature insideFeature = structure instanceof Membrane ? ((Membrane)structure).getInsideFeature() : null;
						return insideFeature != null ? insideFeature.getName() : "n/a";
					} 
					case COLUMN_OUTSIDE_COMPARTMENT: {
						Structure parentStructure = structure.getParentStructure();
						return parentStructure != null ? parentStructure.getName() : "";
					} 
				}
			} else {
				if (column == COLUMN_NAME) {
					return EditorScrollTable.ADD_NEW_HERE_TEXT;
				} 
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == COLUMN_TYPE) {
			return false;
		}
		if (column == COLUMN_INSIDE_COMPARTMENT) {	
			if (row >= 0 && row < rows.size()) {
				Structure s = getValueAt(row);
				return s instanceof Membrane;
			}
		}
			
		return true;
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == model && evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
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

	public void setValueAt(Object value, int row, int column) {
		if (model == null || value == null || value.toString().length() == 0) {
			return;
		}
		try{
			String newName = (String)value;
			if (row >= 0 && row < rows.size()) {
				Structure s = getValueAt(row);
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
					Structure outsideFeature = null;
					if (newName != null && newName.length() > 0) {
						outsideFeature = model.getStructure(newName);
					}
					s.setParentStructure(outsideFeature);
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
					if (newName.equals(EditorScrollTable.ADD_NEW_HERE_TEXT)) {
						newName = model.getFreeFeatureName();
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

	@Override
	public boolean isSortable(int col) {
		return false;
	}
	
	@Override
	public void sortColumn(int col, boolean ascending) {
		// TODO Auto-generated method stub		
	}

	public String checkInputValue(String inputValue, int row, int column) {
		Structure structure = null;
		if (row >= 0 && row < rows.size()) {
			structure = getValueAt(row);
		}
		switch (column) {
		case COLUMN_NAME:
			if (structure == null || !structure.getName().equals(inputValue)) {
				if (model.getStructure(inputValue) != null) {
					return "Structure '" + inputValue + "' already exist!";
				}
			}
			break;
		case COLUMN_INSIDE_COMPARTMENT: {
			Structure s = model.getStructure(inputValue);
			if (s == null) {
				return "Compartment '" + inputValue + "' does not exist!";
			} 
			if (s instanceof Membrane) {
				return "Structure '" + inputValue + "' is not a compartment!";
			}
			break;
		}
		case COLUMN_OUTSIDE_COMPARTMENT: {
			Structure s = model.getStructure(inputValue);
			if (s == null) {
				return "Structure '" + inputValue + "' does not exist!";
			} 
			if (structure != null){
				return structure.checkNewParent(s);
			}
			break;
		}
		}
		return null;
	}
	
	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final int row, final int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		if (column == COLUMN_INSIDE_COMPARTMENT || column == COLUMN_OUTSIDE_COMPARTMENT) {
			Set<String> words = new HashSet<String>();
			for (Structure s : model.getStructures()) {
				if (column == COLUMN_INSIDE_COMPARTMENT && s instanceof Feature) {					
					words.add(s.getName());
				} else if (column == COLUMN_OUTSIDE_COMPARTMENT) {
					Structure structure = getValueAt(row);
					if (structure instanceof Feature && s instanceof Membrane
						|| structure instanceof Membrane && s instanceof Feature) {
						words.add(s.getName());
					}
				}
			}
			return words;
		}
		return null;
	}
}
