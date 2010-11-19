package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeListener;

import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.gui.ReactionEquation;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class BioModelEditorReactionTableModel extends ManageTableModel<ReactionStep> implements PropertyChangeListener{

	public static final String REACTION_CHOOSE_STRUCTURE_TEXT = "(choose structure)";
	
	private static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	private static final String PROPERTY_NAME_MODEL = "model";
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_EQUATION = 1;
	public final static int COLUMN_KINETICS = 2;
	public final static int COLUMN_PARAMETERS = 3;
	public final static int COLUMN_STRUCTURE = 4;
	
	private Model model = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private String[] columnNames = new String[] {"Name", "Equation", "Parameters", "Kinetics", "Structure"};
	private JTable ownerTable = null;
	private String searchText = null;

	public BioModelEditorReactionTableModel(JTable table) {
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
			case COLUMN_EQUATION:{
				return ReactionEquation.class;
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
			if (row >= 0 && row < super.getRowCount()) {
				ReactionStep rs = (ReactionStep)getData().get(row);
				switch (column) {
					case COLUMN_NAME: {
						return rs.getName();
					} 
					case COLUMN_EQUATION: {
						return new ReactionEquation(rs);						
					} 
					case COLUMN_PARAMETERS: {
						return "";
					} 
					case COLUMN_KINETICS: {
						return rs.getKinetics().getName();
					} 
					case COLUMN_STRUCTURE: {
						return rs.getStructure().getName();
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
			String inputValue = (String)value;
			if (row >= 0 && row < super.getRowCount()) {
				ReactionStep rs = getData().get(row);
				switch (column) {
				case COLUMN_NAME: {
					rs.setName(inputValue);
					break;
				} 
				case COLUMN_EQUATION: {
					if (rs instanceof SimpleReaction) {
						ReactionEquation.parseReaction((SimpleReaction) rs, inputValue);
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
					if (value.equals(REACTION_CHOOSE_STRUCTURE_TEXT)) {
						break;
					}
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
