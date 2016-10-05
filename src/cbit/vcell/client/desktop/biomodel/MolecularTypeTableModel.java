package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.ModelPropertyVetoException;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
class MolecularTypeTableModel extends BioModelEditorRightSideTableModel<MolecularType> {

	public enum Column {
		name("Name"),
		depiction("Depiction"),
		bngl_pattern("BioNetGen Definition");
		
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
	MolecularTypeTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Column col = Column.values()[columnIndex];
		MolecularType molecularType = getValueAt(rowIndex);
		if (molecularType == null) {
			if (col == Column.name) {
				return ADD_NEW_HERE_TEXT;
			} 
		} else {
			switch(col) {
			case name:
				return molecularType.getName();
			case bngl_pattern:
				return pattern(molecularType);
			}
		}
		return null;
	}
	
	private String pattern(MolecularType molecularType) {
		String str = molecularType.getName() + "(";
		List<MolecularComponent> mcl = molecularType.getComponentList();
		for(int i=0; i < mcl.size(); i++) {
			MolecularComponent mc = mcl.get(i);
			str += mc.getName();
			List<ComponentStateDefinition> csdl = mc.getComponentStateDefinitions();
			for(int j=0; j < csdl.size(); j++) {
				ComponentStateDefinition cs = csdl.get(j);
				str += "~" + cs.getName();
			}
			if(i<mcl.size()-1) { str += ","; }
		}
		str += ")";
		return str;
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null || value == null) {
			return;
		}
		String stringValue = ((String)value);
		stringValue = stringValue.trim();
		if (stringValue.length() == 0) {
			return;
		}
		Column col = Column.values()[column];
		try{
			MolecularType ourMt = getValueAt(row);
			switch (col) {
			case name: {
				if (stringValue.equals(ADD_NEW_HERE_TEXT)) {
					return;
				}
				if (ourMt == null) {	// new molecular type in empty row
					getModel().getRbmModelContainer().addMolecularType(new MolecularType(stringValue, getModel()), true);
				} else {						// rename it
					ourMt.setName(stringValue);
				}
				fireTableRowsUpdated(row, row);
				break;
			}
			case bngl_pattern: {
				MolecularType tempMolecularType = RbmUtils.parseMolecularType(stringValue);
				if (ourMt == null) {	// new
					getModel().getRbmModelContainer().addMolecularType(tempMolecularType, true);
				} else {						// change it
					ourMt.setName(tempMolecularType.getName());		// if it had been renamed
					// here we add components
					for(MolecularComponent tempMc : tempMolecularType.getComponentList()) {
						if(ourMt.getMolecularComponent(tempMc.getName()) == null) {	// component not found in the existing molecular type, it's a new component
							ourMt.addMolecularComponent(tempMc);	// add the new component (and its states, if any)
							getModel().getRbmModelContainer().adjustSpeciesContextPatterns(ourMt, tempMc);
						} else {		// existing component being modified (by adding or removing states)
							// check for new states added to the existing components
							MolecularComponent ourMc = ourMt.getMolecularComponent(tempMc.getName());
							for(ComponentStateDefinition tempCsd : tempMc.getComponentStateDefinitions()) {
								if(ourMc.getComponentStateDefinition(tempCsd.getName()) == null) {	// state not found in the existing component, it's a new state
									ourMc.addComponentStateDefinition(tempCsd);
								}
							}
							// TODO: check for deleted states from existing components
							
						}
					}
					// TODO: here we delete components
					for(MolecularComponent ourMc : ourMt.getComponentList()) {
						if(tempMolecularType.getMolecularComponent(ourMc.getName()) == null) {
							// component present in the existing (our) molecular type is not present in the freshly edited one (temp) 
							// component has to go from our molecular type and from everywhere else where it's being used
							// ATTENTION! renaming doesn't work here because we can't know the user's mind, we always consider addition + deletion here
							if(getModel().getRbmModelContainer().delete(ourMt, ourMc) == true) {
								ourMt.removeMolecularComponent(ourMc);
							}
						}
					}
				}
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
	public boolean isCellEditable(int row, int column) {
		Column col = Column.values()[column];
		if (col == Column.name) {
			return true;
		} else if(col == Column.bngl_pattern) {
			// we allow editing as long as it's a new molecular type or there are no components yet
			MolecularType molecularType = getValueAt(row);
			return molecularType == null || molecularType.getComponentList().size() == 0;
		} else {
			return false;
		}
	}
	
	@Override
	public int getRowCount() {
//		return getRowCountWithAddNew();
		return super.getRowCount();
	}
	
	@Override
	protected Comparator<MolecularType> getComparator(final int columnIndex, final boolean ascending) {
		final int scale = ascending ? 1 : -1;
		final Column col = Column.values()[columnIndex];
		return new Comparator<MolecularType>() {
			public int compare(MolecularType o1, MolecularType o2) {
				switch (col) {
				case name:
					return scale * o1.getName().compareToIgnoreCase(o2.getName());
				}
				return 0;
			}
		};
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		final Column col = Column.values()[columnIndex];
		switch (col) {
		case name:
			return String.class;
		case bngl_pattern:
			return MolecularType.class;
		}
		return Object.class;
	}
	
	@Override
	public String checkInputValue(String inputValue, int row, int columnIndex) {
		String errMsg = null;
		final Column col = Column.values()[columnIndex];
		MolecularType selectedMolecularType = getValueAt(row);
		switch (col) {
		case name: {
			if (!inputValue.equals(TokenMangler.fixTokenStrict(inputValue))){
				errMsg = "'" + inputValue + "' not legal identifier, try '"+TokenMangler.fixTokenStrict(inputValue)+"'.";
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			}
			inputValue = TokenMangler.fixTokenStrict(inputValue);
			MolecularType mt = getModel().getRbmModelContainer().getMolecularType(inputValue);
			if (mt != null && mt != selectedMolecularType) {
				errMsg = mt.getDisplayType() + " '" + inputValue + "' already exists!";
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			}
			break;
		}
		case bngl_pattern: {
			try {
				inputValue = inputValue.trim();
				if (inputValue.length() > 0) {
					MolecularType mt = RbmUtils.parseMolecularType(inputValue);
					MolecularType mt1 = getModel().getRbmModelContainer().getMolecularType(mt.getName());
					if (mt1 != null && getRowIndex(mt1) != row) {	// molecular type with this name exists already on another row
						errMsg = mt.getDisplayType() + " '" + mt.getDisplayName() + "' already exists!";
						errMsg += VCellErrorMessages.PressEscToUndo;
						errMsg = "<html>" + errMsg + "</html>";
						return errMsg;
					}
//					if(!selectedMolecularType.getName().equals(mt.getName())) {	// attempt to rename, must fail if molecular type is in use
//						if(!getModel().getRbmModelContainer().isDeleteAllowed(selectedMolecularType)) {
//							errMsg = MolecularType.type + " '" + mt + "' cannot be deleted because it's already being used.";
//						}
//					}
					// need to check if any Component we try to delete is not already in use elsewhere
					for(MolecularComponent selectedMolecularComponent : selectedMolecularType.getComponentList()) {
						if(mt.getMolecularComponent(selectedMolecularComponent.getName()) == null) {	// the user tries to delete this mc
							Map<String, Pair<Displayable, SpeciesPattern>> usedHere = new LinkedHashMap<String, Pair<Displayable, SpeciesPattern>>();
							bioModel.getModel().getRbmModelContainer().findComponentUsage(selectedMolecularType, selectedMolecularComponent, usedHere);
							
							if(!usedHere.isEmpty()) {
								errMsg = selectedMolecularComponent.dependenciesToHtml(usedHere);
								errMsg += "<br><br>Deleting and Renaming a Component can be done in the Object Properties tree below.";
								errMsg += VCellErrorMessages.PressEscToUndo;
								errMsg = "<html>" + errMsg + "</html>";
								return errMsg;
							}
						}
					}
					// need to check if any State we try to delete is not already in use elsewhere
					for(MolecularComponent selectedMolecularComponent : selectedMolecularType.getComponentList()) {
						for(ComponentStateDefinition selectedComponentStateDefinition : selectedMolecularComponent.getComponentStateDefinitions()) {
							MolecularComponent mc = mt.getMolecularComponent(selectedMolecularComponent.getName());
							if(mc.getComponentStateDefinition(selectedComponentStateDefinition.getName()) == null) {	// new list is missing a state which was present in the original
								if(!getModel().getRbmModelContainer().isDeleteAllowed(selectedMolecularType, selectedMolecularComponent, selectedComponentStateDefinition)) {
									errMsg = "State '" + selectedComponentStateDefinition + "' cannot be deleted because it's already being used.";
									errMsg += "<br>Deleting and Renaming a State can be done in the Object Properties tree below.";
									errMsg += VCellErrorMessages.PressEscToUndo;
									errMsg = "<html>" + errMsg + "</html>";
									return errMsg;
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				errMsg = ex.getMessage();
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			}
			break;
		}
		}
		return null;
	}
	
	@Override
	public SymbolTable getSymbolTable(int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row,
			int column) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<String> getAutoCompletionWords(int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected List<MolecularType> computeData() {
		if (getModel() == null) {
			return new ArrayList<MolecularType>();
		}
		List<MolecularType> mtList;
		if (searchText == null || searchText.length() == 0) {
			mtList = new ArrayList<MolecularType>(getModel().getRbmModelContainer().getMolecularTypeList());
		} else {
			mtList = new ArrayList<MolecularType>();
			String lowerCaseSearchText = searchText.toLowerCase();
			for (MolecularType mt : getModel().getRbmModelContainer().getMolecularTypeList()){
				String expression = pattern(mt);
				if(expression != null && expression.toLowerCase().contains(lowerCaseSearchText)) {
					mtList.add(mt);
				}
			}
		}
		return mtList;
	}
	@Override
	protected void bioModelChange(PropertyChangeEvent evt) {
		super.bioModelChange(evt);
		BioModel oldValue = (BioModel)evt.getOldValue();
		if (oldValue != null) {
			RbmModelContainer rbmModelContainer = (RbmModelContainer)(oldValue.getModel().getRbmModelContainer());
//			rbmModelContainer.getNetworkConstraints().removePropertyChangeListener(this);
			// TODO: make sure to eventually listen
//			rbmModelContainer.removePropertyChangeListener(this);
			for (MolecularType molecularType : rbmModelContainer.getMolecularTypeList()) {
				RbmUtils.removePropertyChangeListener(molecularType, this);
			}
		}
		BioModel newValue = (BioModel)evt.getNewValue();
		if (newValue != null) {
			RbmModelContainer rbmModelContainer = newValue.getModel().getRbmModelContainer();
//			rbmModelContainer.getNetworkConstraints().addPropertyChangeListener(this);
//			rbmModelContainer.addPropertyChangeListener(this);
			for (MolecularType molecularType : rbmModelContainer.getMolecularTypeList()) {
				RbmUtils.addPropertyChangeListener(molecularType, this);
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
//		if (evt.getSource() == getModel().getRbmModelContainer()) {
		if (evt.getSource() == getModel()) {
			if (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				refreshData();
				
				List<MolecularType> oldValue = (List<MolecularType>) evt.getOldValue();
				for (MolecularType molecularType : oldValue) {
					RbmUtils.removePropertyChangeListener(molecularType, this);
				}
				List<MolecularType> newValue = (List<MolecularType>) evt.getNewValue();
				for (MolecularType molecularType : newValue) {
					RbmUtils.addPropertyChangeListener(molecularType, this);
				}
			}
			refreshData();
//		} else if (evt.getSource() == getModel().getRbmModelContainer().getNetworkConstraints()) {
//			if (evt.getPropertyName().equals(NetworkConstraints.PROPERTY_NAME_MAX_STOICHIOMETRY)) {
//				fireTableRowsUpdated(0, getRowCount() - 1);
//			}
//			refreshData();

		} else if (evt.getSource() instanceof MolecularType) {
			MolecularType mt = (MolecularType) evt.getSource();
			int changeRow = getRowIndex(mt);
			if (changeRow >= 0) {
				fireTableRowsUpdated(changeRow, changeRow);
			}
			if (evt.getPropertyName().equals(MolecularType.PROPERTY_NAME_COMPONENT_LIST)) {
				List<MolecularComponent> oldValue = (List<MolecularComponent>) evt.getOldValue();
				if (oldValue != null) {
					for (MolecularComponent molecularComponent : oldValue) {
						RbmUtils.removePropertyChangeListener(molecularComponent, this);
					}
				}
				List<MolecularComponent> newValue = (List<MolecularComponent>) evt.getNewValue();
				if (newValue != null) {
					for (MolecularComponent molecularComponent : newValue) {
						RbmUtils.addPropertyChangeListener(molecularComponent, this);
					}
				}
			}
			refreshData();
		} else if (evt.getSource() instanceof MolecularComponent) {
			fireTableRowsUpdated(0, getRowCount() - 1);
			
			if (evt.getPropertyName().equals(MolecularComponent.PROPERTY_NAME_COMPONENT_STATE_DEFINITIONS)) {
				List<ComponentStateDefinition> oldValue = (List<ComponentStateDefinition>) evt.getOldValue();
				if (oldValue != null) {
					for (ComponentStateDefinition componentState : oldValue) {
						componentState.removePropertyChangeListener(this);
					}
				}
				List<ComponentStateDefinition> newValue = (List<ComponentStateDefinition>) evt.getNewValue();
				if (newValue != null) {
					for (ComponentStateDefinition componentState : newValue) {
						componentState.addPropertyChangeListener(this);
					}
				}
			}
			refreshData();
		} else if (evt.getSource() instanceof ComponentStateDefinition) {
			fireTableRowsUpdated(0, getRowCount() - 1);
			refreshData();
		}
	}
}