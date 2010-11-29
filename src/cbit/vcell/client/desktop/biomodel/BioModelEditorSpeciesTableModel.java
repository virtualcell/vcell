package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorSpeciesTableModel extends BioModelEditorRightSideTableModel<SpeciesContext> {
	
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_STRUCTURE = 1;	
	private static String[] columnNames = new String[] {"Name", "Structure"};

	public BioModelEditorSpeciesTableModel(JTable table) {
		super(table);
		setColumns(columnNames);
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
						return speciesContext.getStructure().getName();
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

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null) {
			return;
		}
		try{
			String newValue = (String)value;
			if (row >= 0 && row < getDataSize()) {
				SpeciesContext speciesContext = getValueAt(row);
				switch (column) {
				case COLUMN_NAME: {
					speciesContext.getSpecies().setCommonName(newValue);
					speciesContext.setHasOverride(true);
					speciesContext.setName(newValue);
					break;
				} 
				case COLUMN_STRUCTURE: {
					Structure structure = getModel().getStructure(newValue);
					// TODO
//					speciesContext;
					break;
				} 
				}
			} else {
				SpeciesContext freeSpeciesContext = new SpeciesContext(new Species(getModel().getFreeSpeciesName(), null), getModel().getStructures()[0]);
				switch (column) {
				case COLUMN_NAME: {
					if (!value.equals(ADD_NEW_HERE_TEXT)) {
						freeSpeciesContext.getSpecies().setCommonName(newValue);
					}
					break;
				} 
				case COLUMN_STRUCTURE: {
					Structure s = getModel().getStructure(newValue);
					freeSpeciesContext = new SpeciesContext(new Species(getModel().getFreeSpeciesName(), null), s);
					break;
				} 
				}
				freeSpeciesContext.setHasOverride(true);
				freeSpeciesContext.setName(freeSpeciesContext.getSpecies().getCommonName());
				getModel().addSpecies(freeSpeciesContext.getSpecies());
				getModel().addSpeciesContext(freeSpeciesContext);
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
}
