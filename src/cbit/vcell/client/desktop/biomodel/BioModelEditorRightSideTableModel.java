package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.EditorScrollTable.DefaultScrollTableComboBoxEditor;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;

import cbit.gui.ReactionEquation;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

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
	protected static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	
	protected BioModel bioModel = null;
	protected EditorScrollTable ownerTable = null;
	protected String searchText = null;
	public static final String ADD_NEW_HERE_TEXT = "(add new here)";
	public static final String ADD_NEW_HERE_HTML = "<html><i>(add new here)</i></html>";
	public static final String ADD_NEW_HERE_REACTION_TEXT = ReactionEquation.REACTION_GOESTO;
	public static final String ADD_NEW_HERE_REACTION_HTML = "<html><i>(add new here, e.g. <font color=blue><b>a+b" + ReactionEquation.REACTION_GOESTO + "c</b></font>)</i></html>";
	
	public BioModelEditorRightSideTableModel(EditorScrollTable table) {
		super(null);
		ownerTable = table;
		addPropertyChangeListener(this);
	}
	
	/**
	 * getRowCount method comment.
	 */
	@Override
	public int getRowCount() {
		return super.getRowCount() + (searchText == null || searchText.length() == 0 ? 1 : 0);
	}
	
	protected abstract List<T> computeData();
	protected boolean containedByModel() {
		return true;
	}
	
	protected void refreshData() {
		List<T> newData = computeData();
		setData(newData);
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() == this) {
			if (evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				bioModelChange(evt);
			} else if (evt.getPropertyName().equals(PROPERTY_NAME_SEARCH_TEXT)) {
				refreshData();
			}
		} else if (containedByModel() && evt.getSource() == bioModel.getModel() || evt.getSource() == bioModel) {
			refreshData();
		}
	}

	protected void bioModelChange(java.beans.PropertyChangeEvent evt) {
		refreshData();
		BioModel oldValue = (BioModel)evt.getOldValue();
		if (oldValue != null) {
			if (containedByModel()) {
				oldValue.getModel().removePropertyChangeListener(this);
			} else {
				oldValue.removePropertyChangeListener(this);
			}
		}
		BioModel newValue = (BioModel)evt.getNewValue();
		if (newValue != null) {
			if (containedByModel()) {
				newValue.getModel().addPropertyChangeListener(this);
			} else {
				newValue.addPropertyChangeListener(this);
			}
		}
	}
	
	public void setBioModel(BioModel newValue) {
		BioModel oldValue = bioModel;
		bioModel = newValue;
		firePropertyChange(PROPERTY_NAME_BIO_MODEL, oldValue, newValue);
	}

	public void setSearchText(String newValue) {
		if (newValue == searchText) { // takes care of both null
			return;
		}
		if (newValue == null && searchText.length() == 0
				|| searchText == null && newValue.length() == 0) {
			return;
		}
		String oldValue = searchText;
		searchText = newValue;
		firePropertyChange(PROPERTY_NAME_SEARCH_TEXT, oldValue, newValue);		
	}
	
	protected Model getModel() {
		return bioModel == null ? null : bioModel.getModel();
	}
	
	private DefaultScrollTableComboBoxEditor defaultScrollTableComboBoxEditor = null;
	protected void updateStructureComboBox() {
		JComboBox structureComboBoxCellEditor = (JComboBox) getStructureComboBoxEditor().getComponent();
		if (structureComboBoxCellEditor == null) {
			structureComboBoxCellEditor = new JComboBox();
		}
		Structure[] structures = bioModel.getModel().getStructures();
		DefaultComboBoxModel aModel = new DefaultComboBoxModel();
		for (Structure s : structures) {
			aModel.addElement(s);
		}
		structureComboBoxCellEditor.setRenderer(new DefaultListCellRenderer() {
			
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setHorizontalTextPosition(SwingConstants.LEFT);
				if (value instanceof Structure) {
					setText(((Structure)value).getName());
				}
				return this;
			}
		});
		structureComboBoxCellEditor.setModel(aModel);
		structureComboBoxCellEditor.setSelectedIndex(0);
	}
	
	protected DefaultScrollTableComboBoxEditor getStructureComboBoxEditor() {
		if (defaultScrollTableComboBoxEditor == null) {
			defaultScrollTableComboBoxEditor = ownerTable.new DefaultScrollTableComboBoxEditor(new JComboBox());
		}
		return defaultScrollTableComboBoxEditor;
	}
}
