package cbit.vcell.client.desktop.biomodel;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ReactionEquation;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorReactionTableModel extends BioModelEditorRightSideTableModel<ReactionStep> {	
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_EQUATION = 1;
	public final static int COLUMN_STRUCTURE = 2;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private static String[] columnNames = new String[] {"Name", "Equation", "Structure"};

	public BioModelEditorReactionTableModel(JTable table) {
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
			case COLUMN_EQUATION:{
				return ReactionEquation.class;
			}
			case COLUMN_STRUCTURE:{
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
		ReactionStep[] reactionStepList = model.getReactionSteps();
		if (reactionStepList != null) {
			for (ReactionStep rs : reactionStepList){
				if (searchText == null || searchText.length() == 0 || rs.getName().startsWith(searchText)) {
					rows.add(rs);
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
				ReactionStep reactionStep = getValueAt(row);
				switch (column) {
					case COLUMN_NAME: {
						return reactionStep.getName();
					} 
					case COLUMN_EQUATION: {
						return new ReactionEquation(reactionStep);
					} 
					case COLUMN_STRUCTURE: {
						return reactionStep.getStructure().getName();
					} 
				}
			} else {
				if (column == COLUMN_NAME) {
					return EditorScrollTable.ADD_NEW_HERE_TEXT;
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
		if (model == null) {
			return;
		}
		try{
			String inputValue = (String)value;
			if (row >= 0 && row < rows.size()) {
				ReactionStep rs = getValueAt(row);
				switch (column) {
				case COLUMN_NAME: {
					rs.setName(inputValue);
					break;
				} 
				case COLUMN_EQUATION: {
					if (rs instanceof SimpleReaction) {
						rs.setReactionParticipants(ReactionEquation.parseReaction((SimpleReaction) rs, inputValue));
					}
					break;
				} 
				}
			} else {
				SimpleReaction reactionStep = new SimpleReaction(model.getStructures()[0], model.getFreeReactionStepName());
				model.addReactionStep(reactionStep);
				switch (column) {
				case COLUMN_NAME: {
					reactionStep.setName(inputValue);
					break;
				} 
				case COLUMN_EQUATION: {
					ReactionEquation.parseReaction(reactionStep, inputValue);
					break;
				} 
				case COLUMN_STRUCTURE: {
					Structure s = model.getStructure(inputValue);
					reactionStep.setStructure(s);
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
		ReactionStep reactionStep = null;
		if (row >= 0 && row < rows.size()) {
			reactionStep = getValueAt(row);
		}
		switch (column) {
		case COLUMN_NAME:
			if (reactionStep != null && reactionStep.getName().equals(inputValue)) {
				return null; // name did not change
			}
			if (model.getReactionStep(inputValue) != null) {
				return "Reaction '" + inputValue + "' already exist!";
			}
			break;
		case COLUMN_EQUATION:
			try {
				ReactionEquation.parseReaction(reactionStep, inputValue);
			} catch (Exception ex) {
				return ex.getMessage();
			}
			break;
		case COLUMN_STRUCTURE:
			if (model.getStructure(inputValue) == null) {
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
			for (Structure s : model.getStructures()) {
				words.add(s.getName());
			}
			return words;
		}
		return null;
	}
}
