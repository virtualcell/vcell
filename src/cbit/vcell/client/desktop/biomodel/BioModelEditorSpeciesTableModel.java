package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Model;
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
		if (getModel() != null) {
			if (searchText == null || searchText.length() == 0) {
				speciesContextList.addAll(Arrays.asList(getModel().getSpeciesContexts()));
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();
					for (SpeciesContext s : getModel().getSpeciesContexts()){
					if (s.getName().toLowerCase().contains(lowerCaseSearchText)		
						|| s.getStructure().getName().toLowerCase().contains(lowerCaseSearchText)) {
						speciesContextList.add(s);
					}
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
			SpeciesContext speciesContext = getValueAt(row);
			if (speciesContext != null) {
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
		return column == COLUMN_NAME;
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == bioModel.getModel()) {
			if (evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
				//updateStructureComboBox();
			} else if (evt.getPropertyName().equals(Model.PROPERTY_NAME_SPECIES_CONTEXTS)) {
				SpeciesContext[] oldValue = (SpeciesContext[]) evt.getOldValue();
				if (oldValue != null) {
					for (SpeciesContext sc : oldValue) {
						sc.removePropertyChangeListener(this);
					}
				}
				SpeciesContext[] newValue = (SpeciesContext[]) evt.getNewValue();
				if (newValue != null) {
					for (SpeciesContext sc : newValue) {
						sc.addPropertyChangeListener(this);
					}
				}
				refreshData();
			}
		} else if (evt.getSource() instanceof SpeciesContext) {
			SpeciesContext speciesContext = (SpeciesContext) evt.getSource();
			int changeRow = getRowIndex(speciesContext);
			if (changeRow >= 0) {
				fireTableRowsUpdated(changeRow, changeRow);
			}
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null || value == null) {
			return;
		}
		try{
			SpeciesContext speciesContext = getValueAt(row);
			if (speciesContext != null) {
				switch (column) {
				case COLUMN_NAME: {
					String inputValue = ((String)value);
					inputValue = inputValue.trim();
					if (inputValue.length() == 0) {
						return;
					}
					speciesContext.setName(inputValue);
					break;
				} 
				case COLUMN_STRUCTURE: {
					// value might be null because no popup in JCombo editor ui.
					// the first time.
					Structure structure = (Structure)value;
					speciesContext.setStructure(structure);
					break;
				} 
				}
			} else {
				switch (column) {
				case COLUMN_NAME: {
					String inputValue = ((String)value);
					if (inputValue.length() == 0 || inputValue.equals(ADD_NEW_HERE_TEXT)) {
						return;
					}
					inputValue = inputValue.trim();
					SpeciesContext freeSpeciesContext = getModel().createSpeciesContext(getModel().getStructures()[0]);
					freeSpeciesContext.setName(inputValue);
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
		return true;
	}
	
	@Override
	public Comparator<SpeciesContext> getComparator(final int col, final boolean ascending) {
		return new Comparator<SpeciesContext>() {
            public int compare(SpeciesContext o1, SpeciesContext o2) {
            	int scale = ascending ? 1 : -1;
                if (col==COLUMN_NAME){
					return scale * o1.getName().compareTo(o2.getName());
				} else if (col == COLUMN_STRUCTURE) {
					return scale * o1.getStructure().getName().compareTo(o2.getStructure().getName());
				}
				return 0;
            }
		};
	}

	public String checkInputValue(String inputValue, int row, int column) {
		SpeciesContext speciesContext = getValueAt(row);
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
//		ownerTable.getColumnModel().getColumn(COLUMN_STRUCTURE).setCellEditor(getStructureComboBoxEditor());
//		updateStructureComboBox();
		
		BioModel oldValue = (BioModel)evt.getOldValue();
		if (oldValue != null) {
			for (SpeciesContext sc : oldValue.getModel().getSpeciesContexts()) {
				sc.removePropertyChangeListener(this);
			}
		}
		BioModel newValue = (BioModel)evt.getNewValue();
		if (newValue != null) {
			for (SpeciesContext sc : newValue.getModel().getSpeciesContexts()) {
				sc.addPropertyChangeListener(this);
			}
		}
	}
	
	@Override
	public int getRowCount() {
		if (bioModel == null || bioModel.getModel().getNumStructures() == 1) {
			return getRowCountWithAddNew();
		}
		return super.getRowCount();
	}
}
