package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ReactionEquation;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorReactionTableModel extends BioModelEditorRightSideTableModel<ReactionStep> {	
	public final static int COLUMN_EQUATION = 0;
	public final static int COLUMN_NAME = 1;
	public final static int COLUMN_STRUCTURE = 2;
	public final static int COLUMN_KINETICS = 3;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private static String[] columnNames = new String[] {"Equation", "Name", "Structure", "Kinetics"};

	public BioModelEditorReactionTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}

	public Class<?> getColumnClass(int column) {
		switch (column){		
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_EQUATION:{
				return ReactionEquation.class;
			}
			case COLUMN_STRUCTURE:{
				return Structure.class;
			}
			case COLUMN_KINETICS:
				return Kinetics.class;
		}
		return Object.class;
	}

	protected ArrayList<ReactionStep> computeData() {
		ArrayList<ReactionStep> reactionStepList = new ArrayList<ReactionStep>();
		if (getModel() != null){
			for (ReactionStep rs : getModel().getReactionSteps()){
				if (searchText == null || searchText.length() == 0) {
					reactionStepList.add(rs);
				} else {
					String lowerCaseSearchText = searchText.toLowerCase();	
					if (rs.getName().toLowerCase().contains(lowerCaseSearchText)
						|| new ReactionEquation(rs, bioModel.getModel()).toString().toLowerCase().contains(lowerCaseSearchText)
						|| rs.getStructure().getName().toLowerCase().contains(lowerCaseSearchText)
						|| rs.getKinetics().getKineticsDescription().getDescription().toLowerCase().contains(lowerCaseSearchText)) {
						reactionStepList.add(rs);
					}
				}
			}
		}
		return reactionStepList;
	}

	public Object getValueAt(int row, int column) {
		if (getModel() == null) {
			return null;
		}
		try{
			if (row >= 0 && row < getDataSize()) {
				ReactionStep reactionStep = getValueAt(row);
				switch (column) {
					case COLUMN_NAME: {
						return reactionStep.getName();
					} 
					case COLUMN_EQUATION: {
						return new ReactionEquation(reactionStep, bioModel.getModel());
					} 
					case COLUMN_STRUCTURE: {
						return reactionStep.getStructure();
					} 
					case COLUMN_KINETICS:
						return reactionStep.getKinetics();
				}
			} else {
				if (column == COLUMN_EQUATION) {
					return new ReactionEquation(null, bioModel.getModel());
				} 
			}
			return null;
		} catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	public boolean isCellEditable(int row, int column) {
		return (column != COLUMN_KINETICS) && (row < getDataSize() || column == COLUMN_EQUATION);
	}
	
	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == bioModel.getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
			updateStructureComboBox();
		} else if (evt.getSource() instanceof ReactionStep) {
			int[] selectedRows = ownerTable.getSelectedRows();
			ReactionStep reactionStep = (ReactionStep) evt.getSource();
			int changeRow = getRowIndex(reactionStep);
			if (changeRow >= 0) {
				fireTableRowsUpdated(changeRow, changeRow);
				GuiUtils.tableSetSelectedRows(ownerTable, selectedRows);
			}
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null || value == null) {
			return;
		}
		try{
			if (row < getDataSize()) {
				ReactionStep reactionStep = getValueAt(row);
				switch (column) {
				case COLUMN_NAME: {
					String inputValue = ((String)value);
					inputValue = inputValue.trim();
					reactionStep.setName(inputValue);
					break;
				} 
				case COLUMN_EQUATION: {
					String inputValue = (String)value;
					ReactionParticipant[] rpArray = ReactionEquation.parseReaction(reactionStep, getModel(), inputValue);
					for (ReactionParticipant rp : rpArray) {
						SpeciesContext speciesContext = rp.getSpeciesContext();
						if (bioModel.getModel().getSpeciesContext(speciesContext.getName()) == null) {
							bioModel.getModel().addSpecies(speciesContext.getSpecies());
							bioModel.getModel().addSpeciesContext(speciesContext);
						}
					}
					reactionStep.setReactionParticipants(rpArray);
					break;
				}
				case COLUMN_STRUCTURE: {
					Structure s = (Structure)value;
					reactionStep.setStructure(s);
					break;
				} 
				}
			} else {
				switch (column) {
				case COLUMN_EQUATION: {
					String inputValue = ((String)value);
					inputValue = inputValue.trim();
					if (BioModelEditorRightSideTableModel.ADD_NEW_HERE_REACTION_TEXT.equals(inputValue)) {
						return;
					}
					ReactionStep reactionStep = getModel().createSimpleReaction(getModel().getStructure(0));
					ReactionParticipant[] rpArray = ReactionEquation.parseReaction(reactionStep, getModel(), inputValue);
					for (ReactionParticipant rp : rpArray) {
						SpeciesContext speciesContext = rp.getSpeciesContext();
						if (bioModel.getModel().getSpeciesContext(speciesContext.getName()) == null) {
							bioModel.getModel().addSpecies(speciesContext.getSpecies());
							bioModel.getModel().addSpeciesContext(speciesContext);
						}
					}
					reactionStep.setReactionParticipants(rpArray);
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
	public Comparator<ReactionStep> getComparator(final int col, final boolean ascending) {
		return new Comparator<ReactionStep>() {
            public int compare(ReactionStep o1, ReactionStep o2) {
            	int scale = ascending ? 1 : -1;
                if (col==COLUMN_NAME){
					return scale * o1.getName().compareTo(o2.getName());
				} else if (col == COLUMN_EQUATION) {
					ReactionEquation re1 = new ReactionEquation(o1, bioModel.getModel());
					ReactionEquation re2 = new ReactionEquation(o2, bioModel.getModel());
					return scale * re1.toString().compareTo(re2.toString());
				} else if (col == COLUMN_STRUCTURE) {
					return scale * o1.getStructure().getName().compareTo(o2.getStructure().getName());
				}
				return 0;
            }
      };
	}
	
	public String checkInputValue(String inputValue, int row, int column) {
		ReactionStep reactionStep = null;
		if (row >= 0 && row < getDataSize()) {
			reactionStep = getValueAt(row);
		}
		switch (column) {
		case COLUMN_NAME:
			if (reactionStep == null || !reactionStep.getName().equals(inputValue)) {
				if (getModel().getReactionStep(inputValue) != null) {
					return "Reaction '" + inputValue + "' already exist!";
				}
			}
			break;
		case COLUMN_EQUATION:
			try {
				ReactionEquation.parseReaction(reactionStep, getModel(), inputValue);
			} catch (Exception ex) {
				return ex.getMessage();
			}
			break;
		case COLUMN_STRUCTURE:
			if (getModel().getStructure(inputValue) == null) {
				return "Structure '" + inputValue + "' does not exist!";
			}
			break;
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
		
		BioModel oldValue = (BioModel)evt.getOldValue();
		if (oldValue != null) {
			for (ReactionStep rs : oldValue.getModel().getReactionSteps()) {
				rs.removePropertyChangeListener(this);
			}
		}
		BioModel newValue = (BioModel)evt.getNewValue();
		if (newValue != null) {
			for (ReactionStep rs : newValue.getModel().getReactionSteps()) {
				rs.addPropertyChangeListener(this);
			}
		}
	}
}
