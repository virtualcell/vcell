package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.util.Displayable;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.EditorScrollTable.DefaultScrollTableComboBoxEditor;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.RbmObservable.ObservableType;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

public class ObservableTableModel  extends BioModelEditorRightSideTableModel<RbmObservable> {

	private DefaultScrollTableComboBoxEditor defaultScrollTableComboBoxEditor = null;

	public enum Column {
		name("Name"),
		structure("Structure"),
		depiction("Depiction"),
		species_pattern("BioNetGen Definition"),
		type("Count");
		
		String columeName;
		Column(String col) {
			columeName = col;
		}
	}
	
	private final static String[] columnNames = new String[Column.values().length];
	static
	{
		int i = -1;
		for (Column col : Column.values()) {
			columnNames[++ i] = col.columeName;
		}
	}
	ObservableTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}

	public String checkInputValue(String inputValue, int row, int column) {
		String errMsg = null;
		final Column col = Column.values()[column];
		RbmObservable selectedObservable = getValueAt(row);
		switch (col) {
		case name:
			inputValue = inputValue.trim();
			if (inputValue.length() > 0) {
				String mangled = TokenMangler.fixTokenStrict(inputValue);
				if(!mangled.equals(inputValue)) {
					errMsg = RbmObservable.typeName + " '" + inputValue + "' not legal identifier, try '" + mangled + "'";
					errMsg += VCellErrorMessages.PressEscToUndo;
					errMsg = "<html>" + errMsg + "</html>";
					return errMsg;
				}
				RbmObservable o = getModel().getRbmModelContainer().getObservable(inputValue);
				if (o != null && o != selectedObservable) {
					errMsg = "Observable '" + inputValue + "' already exists!";
					errMsg += VCellErrorMessages.PressEscToUndo;
					errMsg = "<html>" + errMsg + "</html>";
					return errMsg;
				}
			}
			break;
		case species_pattern:
			try {
				inputValue = inputValue.trim();
				if (inputValue.length() > 0) {
					StringTokenizer tokens = new StringTokenizer(inputValue);
					while(tokens.hasMoreTokens()) {
						String token = tokens.nextToken();
						// parsing will throw appropriate exception if molecular type or component don't exist
						SpeciesPattern speciesPattern = RbmUtils.parseSpeciesPattern(token, bioModel.getModel());
					}
				}
			} catch (Exception ex) {
				errMsg = ex.getMessage();
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			}
			break;
		case type:
			try {
				inputValue = inputValue.trim();
				RbmObservable.ObservableType ot = RbmObservable.ObservableType.valueOf((String) inputValue);
			} catch(IllegalArgumentException e) {
				e.printStackTrace(System.out);
				errMsg = "";
				for (int i=0; i<RbmObservable.ObservableType.values().length; i++) {
					errMsg += RbmObservable.ObservableType.values()[i].toString();
					if(i<RbmObservable.ObservableType.values().length-1) {
						errMsg += " or ";						}
			    }
				errMsg = "Type may only be " + errMsg;
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			} catch (Exception ex) {
				errMsg = ex.getMessage();
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;

			}
			break;
		}
		return null;
	}

	public SymbolTable getSymbolTable(int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}

	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row,
			int column) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		RbmObservable	observable = getValueAt(rowIndex);
		Column col = Column.values()[columnIndex];
		if (observable == null) {
			if (col == Column.name) {
				return ADD_NEW_HERE_TEXT;
			} 
		} else {
			switch(col) {
			case name:
				return observable.getName();
			case species_pattern:
				if(!observable.getSpeciesPatternList().isEmpty()) {
					String speciesPatterns = "";
					for(SpeciesPattern sp : observable.getSpeciesPatternList()) {
						speciesPatterns += sp.toString() + " ";
					}
					return speciesPatterns;
				} else {
					return("");
				}
			case type:
				return observable.getType();
			case structure:
				return observable.getStructure();
			}
		}
		return null;
	}
	
	public boolean isCellEditable(int row, int columnIndex) {
		Column col = Column.values()[columnIndex];
		if (col == Column.name) {
			return true;
		}
		RbmObservable o = getValueAt(row);
		if (o == null) {
			return false;
		}
		if (col == Column.structure){
			return false;
		}
		if (col == Column.type) {
			return true;
		}
		if (col == Column.depiction) {
			return false;
		}
		final List<SpeciesPattern> spList = o.getSpeciesPatternList();
		for(SpeciesPattern sp : spList) {
			final List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
			for(MolecularTypePattern mtp : mtpList) {
				MolecularType mt = mtp.getMolecularType();
				if(mt.getComponentList().size() != 0) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected List<RbmObservable> computeData() {
		if (getModel() == null) {
			return new ArrayList<RbmObservable>();
		}
		List<RbmObservable> oList;
		if (searchText == null || searchText.length() == 0) {
			oList = new ArrayList<RbmObservable>(getModel().getRbmModelContainer().getObservableList());
		} else {
			oList = new ArrayList<RbmObservable>();
			String lowerCaseSearchText = searchText.toLowerCase();
			for (RbmObservable o : getModel().getRbmModelContainer().getObservableList()){
				String name = o.getName();
				String struct = o.getStructure().getName();
				String type = o.getType().name();
				if(name != null && name.toLowerCase().contains(lowerCaseSearchText)) {
					oList.add(o);
				} else if(struct != null && struct.toLowerCase().contains(lowerCaseSearchText)) {
					oList.add(o);
				} else if(type != null && type.toLowerCase().contains(lowerCaseSearchText)) {
					oList.add(o);
				} else {
					String expression = "";
					for(SpeciesPattern sp : o.getSpeciesPatternList()) {
						expression += sp.toString() + " ";
					}
					if(expression.toLowerCase().contains(lowerCaseSearchText)) {
						oList.add(o);
					}
				}
			}
		}
		return oList;
	}

	@Override
	protected Comparator<RbmObservable> getComparator(final int columnIndex, final boolean ascending) {
		
		return new Comparator<RbmObservable>() {
			int scale = ascending ? 1 : -1;
			Column col = Column.values()[columnIndex];
			public int compare(RbmObservable o1, RbmObservable o2) {
				switch (col) {
				case name:
					return scale * o1.getName().compareToIgnoreCase(o2.getName());
				case structure:
					return scale * o1.getStructure().getName().compareToIgnoreCase(o2.getStructure().getName());
				case species_pattern:
					return scale * RbmUtils.toBnglString(o1.getSpeciesPattern(0), null, CompartmentMode.hide, 0).compareToIgnoreCase(RbmUtils.toBnglString(o2.getSpeciesPattern(0), null, CompartmentMode.hide, 0));
				case type:					
					return scale * o1.getType().name().compareToIgnoreCase(o2.getType().name());
				}
				return 0;
			}
		};
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Column col = Column.values()[columnIndex];
		switch (col) {
		case name:
			return String.class;
		case structure:
			return Structure.class;
		case species_pattern:
			return String.class;
		case type:
			return RbmObservable.ObservableType.class;
		case depiction:
			return Object.class;
		}
		return String.class;
	}
	
	protected void updateObservableTypeComboBox() {
		@SuppressWarnings("unchecked")
		JComboBox<RbmObservable.ObservableType> typeComboBoxCellEditor = (JComboBox<RbmObservable.ObservableType>) getObservableTypeComboBoxEditor().getComponent();
		if (typeComboBoxCellEditor == null) {
			typeComboBoxCellEditor = new JComboBox<RbmObservable.ObservableType>();
		}
		DefaultComboBoxModel<RbmObservable.ObservableType> aModel = new DefaultComboBoxModel<RbmObservable.ObservableType>();
		for(RbmObservable.ObservableType ot : RbmObservable.ObservableType.values()) {
			aModel.addElement(ot);
		}
		DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setHorizontalTextPosition(SwingConstants.LEFT);
				if (value instanceof RbmObservable.ObservableType) {
					setText(((RbmObservable.ObservableType) value).name());
				}
				return this;
			}
		};
		typeComboBoxCellEditor.setRenderer(defaultListCellRenderer);
		typeComboBoxCellEditor.setModel(aModel);
		typeComboBoxCellEditor.setSelectedIndex(0);
	}
	protected DefaultScrollTableComboBoxEditor getObservableTypeComboBoxEditor() {
		if (defaultScrollTableComboBoxEditor == null) {
			defaultScrollTableComboBoxEditor = ((EditorScrollTable)ownerTable).new DefaultScrollTableComboBoxEditor(new JComboBox<RbmObservable.ObservableType>());
		}
		return defaultScrollTableComboBoxEditor;
	}
	
	@Override
	protected void bioModelChange(PropertyChangeEvent evt) {
		super.bioModelChange(evt);
		BioModel oldValue = (BioModel)evt.getOldValue();
		if (oldValue != null) {
			RbmModelContainer rbmModelContainer = (RbmModelContainer)(oldValue.getModel().getRbmModelContainer());
			// TODO: listen to something ???  	rbmModelContainer.removePropertyChangeListener(this);
			for (RbmObservable observable : rbmModelContainer.getObservableList()) {
				observable.removePropertyChangeListener(this);
				for(SpeciesPattern speciesPattern : observable.getSpeciesPatternList()) {
					RbmUtils.removePropertyChangeListener(speciesPattern, this);
				}
			}			
		}
		BioModel newValue = (BioModel)evt.getNewValue();
		if (newValue != null) {
			RbmModelContainer rbmModelContainer = (RbmModelContainer)(newValue.getModel().getRbmModelContainer());
// TODO:			rbmModelContainer.addPropertyChangeListener(this);
			for (RbmObservable observable : rbmModelContainer.getObservableList()) {
				observable.addPropertyChangeListener(this);
				for(SpeciesPattern speciesPattern : observable.getSpeciesPatternList()) {
					RbmUtils.addPropertyChangeListener(speciesPattern, this);
				}
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
		Object source = evt.getSource();
//		if (source == getModel().getRbmModelContainer()) {
		if (source == getModel()) {
			if (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_OBSERVABLE_LIST)) {
				refreshData();
				
				List<RbmObservable> oldValue = (List<RbmObservable>) evt.getOldValue();
				if (oldValue != null) {
					for (RbmObservable observable : oldValue) {
						observable.removePropertyChangeListener(this);
						SpeciesPattern speciesPattern = observable.getSpeciesPattern(0);
						RbmUtils.removePropertyChangeListener(speciesPattern, this);
					}
				}
				List<RbmObservable> newValue = (List<RbmObservable>) evt.getNewValue();
				if (newValue != null) {
					for (RbmObservable observable : newValue) {
						observable.addPropertyChangeListener(this);
						SpeciesPattern speciesPattern = observable.getSpeciesPattern(0);
						RbmUtils.addPropertyChangeListener(speciesPattern, this);							
					}
				}
			} else if(evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				refreshData();		// we need this?
			}
		} else if (source instanceof RbmObservable) {
			RbmObservable mt = (RbmObservable) source;
			int changeRow = getRowIndex(mt);
			if (changeRow >= 0) {
				fireTableRowsUpdated(changeRow, changeRow);
			}
			
//			if (evt.getPropertyName().equals(RbmObservable.PROPERTY_NAME_SPECIES_PATTERN_LIST)) {
//				SpeciesPattern oldValue = (SpeciesPattern) evt.getOldValue();
//				if (oldValue != null) {
//					RbmUtils.removePropertyChangeListener(oldValue, this);
//				}
//				SpeciesPattern newValue = (SpeciesPattern) evt.getNewValue();
//				if (newValue != null) {
//					RbmUtils.addPropertyChangeListener(newValue, this);
//				}
//			}
		} else if (source instanceof SpeciesPattern) {
			fireTableRowsUpdated(0, getRowCount() - 1);
			if (evt.getPropertyName().equals(SpeciesPattern.PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS)) {
				List<MolecularTypePattern> oldValue = (List<MolecularTypePattern>) evt.getOldValue();
				if (oldValue != null) {
					for (MolecularTypePattern mtp : oldValue) {
						RbmUtils.removePropertyChangeListener(mtp, this);						
					}
				}
				List<MolecularTypePattern> newValue = (List<MolecularTypePattern>) evt.getNewValue();
				if (newValue != null) {
					for (MolecularTypePattern mtp : newValue) {
						RbmUtils.addPropertyChangeListener(mtp, this);
					}
				}
			}
		} else if (source instanceof MolecularTypePattern) {
			fireTableRowsUpdated(0, getRowCount() - 1);
			if (evt.getPropertyName().equals(MolecularTypePattern.PROPERTY_NAME_COMPONENT_PATTERN_LIST)) {
				List<MolecularComponentPattern> oldValue = (List<MolecularComponentPattern>) evt.getOldValue();
				if (oldValue != null) {
					for (MolecularComponentPattern mcp : oldValue) {
						RbmUtils.removePropertyChangeListener(mcp, this);
					}
				}
				List<MolecularComponentPattern> newValue = (List<MolecularComponentPattern>) evt.getNewValue();
				if (newValue != null) {
					for (MolecularComponentPattern mcp : newValue) {
						RbmUtils.addPropertyChangeListener(mcp, this);
					}
				}
			}
		} else if (source instanceof MolecularComponentPattern) {
			fireTableRowsUpdated(0, getRowCount() - 1);
			
			if (source.equals(MolecularComponentPattern.PROPERTY_NAME_COMPONENT_STATE)) {
				ComponentStateDefinition oldValue = (ComponentStateDefinition) evt.getOldValue();
				if (oldValue != null) {
					oldValue.removePropertyChangeListener(this);
				}
				ComponentStateDefinition newValue = (ComponentStateDefinition) evt.getNewValue();
				if (newValue != null) {
					newValue.addPropertyChangeListener(this);
				}
			}
		} else if (evt.getSource() instanceof MolecularComponent) {
			fireTableRowsUpdated(0, getRowCount() - 1);
		} else if (evt.getSource() instanceof ComponentStateDefinition) {
			fireTableRowsUpdated(0, getRowCount() - 1);
		}
//		updateStructureComboBox();
	}

	public void setValueAt(Object value, int row, int columnIndex) {
		if (getModel() == null || value == null) {
			return;
		}
		Column col = Column.values()[columnIndex];
		try{
			RbmObservable observable = getValueAt(row);
			switch (col) {
			case name: {
				String inputValue = ((String)value);
				inputValue = inputValue.trim();
				if (inputValue.length() == 0) {
					return;
				}
				if (inputValue.equals(ADD_NEW_HERE_TEXT)) {
					return;
				}
				if (observable == null) {	// new observable in empty row of the table
					if (getModel().getStructures().length == 0){
						throw new RuntimeException("cannot add observable without a structure");
					}
					Structure structure = getModel().getStructure(0);
					RbmObservable o = new RbmObservable(getModel(),inputValue,structure,RbmObservable.ObservableType.Molecules);
					getModel().getRbmModelContainer().addObservable(o);
					SpeciesPattern sp = new SpeciesPattern();	// we add an empty species pattern
					o.addSpeciesPattern(sp);

				} else {					// renaming existing observable
					observable.setName(inputValue);
				}
				fireTableRowsUpdated(row, row);
				break;
			}
			case species_pattern: {
				String inputValue = ((String)value);
				inputValue = inputValue.trim();
				List<SpeciesPattern> speciesPatternList = new ArrayList<SpeciesPattern>();
				StringTokenizer tokens = new StringTokenizer(inputValue);
				while(tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					SpeciesPattern speciesPattern = RbmUtils.parseSpeciesPattern(token, bioModel.getModel());
					speciesPatternList.add(speciesPattern);
				}
				if(!speciesPatternList.isEmpty()) {
					observable.setSpeciesPatternList(speciesPatternList);
				}
				fireTableRowsUpdated(row, row);
				break;
			}
			case type: {
				RbmObservable.ObservableType ot = (ObservableType) value;
				observable.setType(ot);
				fireTableRowsUpdated(row, row);
				break;
			}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage(), e);
		}
	}

	@Override
	public int getRowCount() {
//		return getRowCountWithAddNew();
		return super.getRowCount();
	}
	
	
//	ownerTable.getColumnModel().getColumn(Column.type.ordinal()).setCellEditor(getStructureComboBoxEditor());
//	updateStructureComboBox();

	
}
