package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.common.MaxStoichiometryEntity;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.client.desktop.biomodel.BioModelEditorRightSideTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class StoichiometryTableModel extends BioModelEditorRightSideTableModel<MaxStoichiometryEntity> implements java.beans.PropertyChangeListener {
	public static final int colCount = 2;
	public static final int iColName = 0;
	public static final int iColValue = 1;
	private static String[] columnNames = new String[] {"Molecule", "Max. Stoichiometry"};
	
	private SimulationContext simContext = null;
	private boolean isValueEditable = true;
	
	public StoichiometryTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}
	
	public void setSimulationContext(SimulationContext simContext) {
		if(this.simContext == simContext) {
			return;
		}
		this.simContext = simContext;
		List<MaxStoichiometryEntity> newData = computeData();
		setData(newData);
	}
	protected ArrayList<MaxStoichiometryEntity> computeData() {
		ArrayList<MaxStoichiometryEntity> nceList = new ArrayList<MaxStoichiometryEntity>();

		Model model = simContext.getModel();
		RbmModelContainer rbmModelContainer = model.getRbmModelContainer();
		if(rbmModelContainer == null) {
			return nceList;
		}

		NetworkConstraints networkConstraints = simContext.getNetworkConstraints();
		Map<MolecularType, Integer> stoichiometryMap = networkConstraints.getMaxStoichiometry(simContext);
		for(Map.Entry<MolecularType, Integer> entry : stoichiometryMap.entrySet()) {
			MolecularType mt = entry.getKey();
			Integer value = entry.getValue();
			MaxStoichiometryEntity nce = new MaxStoichiometryEntity(mt, value);
			nceList.add(nce);
		}		
		return nceList;
	}
	public void displayTestMaxStoichiometry() {
		ArrayList<MaxStoichiometryEntity> nceList = new ArrayList<MaxStoichiometryEntity>();

		Model model = simContext.getModel();
		RbmModelContainer rbmModelContainer = model.getRbmModelContainer();
		if(rbmModelContainer == null) {
			setData(nceList);
			return;
		}
		NetworkConstraints networkConstraints = simContext.getNetworkConstraints();
		Map<MolecularType, Integer> smTest = networkConstraints.getTestMaxStoichiometry(simContext);
		Map<MolecularType, Integer> smOld = networkConstraints.getMaxStoichiometry(simContext);
		for(Map.Entry<MolecularType, Integer> entry : smTest.entrySet()) {
			MolecularType mt = entry.getKey();
			Integer valueTest = entry.getValue();
			Integer valueOld = smOld.get(mt);
			MaxStoichiometryEntity nce = new MaxStoichiometryEntity(mt, valueTest);
			if(valueTest != valueOld) {
				nce.setChanged(true);	// not in use
			}
			nceList.add(nce);
		}		
		setData(nceList);
	}
	
	public boolean isChanged() {
		NetworkConstraints nc = simContext.getNetworkConstraints();
		for(int row = 0; row < getRowCount(); row++) {
			Integer candidate = (Integer) getValueAt(row, 1);
			MaxStoichiometryEntity nce = getValueAt(row);
			MolecularType mt = nce.getMolecularType();
			Integer oldValue = simContext.getNetworkConstraints().getMaxStoichiometry(simContext).get(mt);
			if(oldValue.intValue() != candidate.intValue()) {
				return true;	// is changed
			}
		}
		return false;
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case iColName:
			return MolecularType.class;
		case iColValue:
			return Integer.class;
		}
		return Object.class;
	}
	
	public void setValueEditable(boolean isValueEditable) {
		this.isValueEditable = isValueEditable;
	}
	public boolean isCellEditable(int row, int column) {
		if(column == iColValue && isValueEditable) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}
	@Override
	public Object getValueAt(int row, int column) {
		MaxStoichiometryEntity nce = getValueAt(row);
		if(nce == null) {
			return null;
		}
		switch(column) {
		case iColName:
			return nce.getMolecularType().getName();
		case iColValue:
			return  nce.getValue();
		}
		return null;
	}
	@Override
	public void setValueAt(Object value, int row, int column) {
		if (simContext == null || value == null) {
			return;
		}
		String text = value+"";
		if (text == null || text.trim().length() == 0) {
			return;
		}
		
		MaxStoichiometryEntity nce = getValueAt(row);
		if(text.equals(nce.getValue().toString())) {
			return;		// unchanged
		}
		nce.setValue(new Integer(text));
		
		fireTableRowsUpdated(row, row);
		return;
	}
	@Override
	public String checkInputValue(String inputValue, int row, int column) {
		String errMsg = null;
		if (simContext == null) {
			errMsg = "Simulation Context Missing.";
			return errMsg;
		}
		switch (column) {
		case iColValue:
			errMsg = "Only positive integers are accepted.";
			try {
				int n = Integer.parseInt(inputValue);
				if(n>0) {
					return null;
				} else {
					return errMsg;
				}
			} catch(NumberFormatException e) {
				return errMsg;
			}
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		Object source = evt.getSource();
		
		if (source instanceof NetworkConstraints) {
			// nothing may change here, it's a modal dialog
		} else if(source instanceof Model) {
		}
	}

	@Override
	protected Model getModel() {
		return simContext == null ? null : simContext.getModel();
	}
	@Override
	protected Comparator<MaxStoichiometryEntity> getComparator(int col, boolean ascending) {
		return null;
	}
	@Override
	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}
	@Override
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
		return null;
	}
	@Override
	public Set<String> getAutoCompletionWords(int row, int column) {
		return null;
	}

}
