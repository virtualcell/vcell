package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorSpeciesTableModel extends BioModelEditorRightSideTableModel<SpeciesContext> {
	
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_STRUCTURE = 1;	
	private static String[] columnNames = new String[] {"Name", "Structure"};

	public BioModelEditorSpeciesTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
		
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_STRUCTURE:{
				return Structure.class;
			}
		}
		return Object.class;
	}

	protected ArrayList<SpeciesContext> computeData() {
		ArrayList<SpeciesContext> speciesContextList = new ArrayList<SpeciesContext>();
		if (getModel() != null){
			for (SpeciesContext s : getModel().getSpeciesContexts()){
				if (searchText == null || searchText.length() == 0 || s.getName().startsWith(searchText)) {
					speciesContextList.add(s);
				}
			}
		}
		return speciesContextList;
	}

	public Object getValueAt(int row, int column) {
		if (getModel() == null) {
			return null;
		}
		try{
			if (row >= 0 && row < getDataSize()) {
				SpeciesContext speciesContext = getValueAt(row);
				switch (column) {
					case COLUMN_NAME: {
						return speciesContext.getName();
					} 
					case COLUMN_STRUCTURE: {
						return speciesContext.getStructure();
					} 
				}
			} else {
				if (column == COLUMN_NAME) {
					return ADD_NEW_HERE_TEXT;
				} 
			}
			return null;
		} catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	public boolean isCellEditable(int row, int column) {
		if (row < getDataSize()) {
			//return column != COLUMN_STRUCTURE;
			return true;
		}
		return column == COLUMN_NAME;
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == bioModel.getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
			updateStructureComboBox();
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null) {
			return;
		}
		try{
			if (row >= 0 && row < getDataSize()) {
				SpeciesContext speciesContext = getValueAt(row);
				switch (column) {
				case COLUMN_NAME: {
					String newValue = (String)value;
					speciesContext.setName(newValue);
					break;
				} 
				case COLUMN_STRUCTURE: {
					// value might be null because no popup in JCombo editor ui.
					// the first time.
					if (value == null) {
						return;
					}
					Structure structure = (Structure)value;
					speciesContext.setStructure(structure);
					break;
				} 
				}
			} else {
				switch (column) {
				case COLUMN_NAME: {
					if (value.equals(ADD_NEW_HERE_TEXT)) {
						return;
					}
					String newValue = (String)value;
					SpeciesContext freeSpeciesContext = new SpeciesContext(new Species(newValue, null), getModel().getStructures()[0]);
					freeSpeciesContext.setName(newValue);
					getModel().addSpecies(freeSpeciesContext.getSpecies());
					getModel().addSpeciesContext(freeSpeciesContext);
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
	public Comparator<SpeciesContext> getComparator(int col, boolean ascending) {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkInputValue(String inputValue, int row, int column) {
		SpeciesContext speciesContext = null;
		if (row >= 0 && row < getDataSize()) {
			speciesContext = getValueAt(row);
		}
		String errMsg = null;
		switch (column) {
		case COLUMN_NAME:
			if (speciesContext == null || !speciesContext.getName().equals(inputValue)) {
				if (getModel().getSpeciesContext(inputValue) != null) {
					errMsg = "Species '" + inputValue + "' already exists!";
				}
			}
			break;
		case COLUMN_STRUCTURE:
			if (getModel().getStructure(inputValue) == null) {
				errMsg = "Structure '" + inputValue + "' does not exist!";
			}
			break;
		}
		return errMsg;
	}
	
	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final int row, final int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		if (column == COLUMN_STRUCTURE) {
			Set<String> words = new HashSet<String>();
			for (Structure s : getModel().getStructures()) {
				words.add(s.getName());
			}
			return words;
		}
		return null;
	}
	
	@Override
	protected void bioModelChange(PropertyChangeEvent evt) {		
		super.bioModelChange(evt);
		ownerTable.getColumnModel().getColumn(COLUMN_STRUCTURE).setCellEditor(getStructureComboBoxEditor());
		updateStructureComboBox();
	}
}
