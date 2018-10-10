package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.common.NetworkConstraintsEntity;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.client.desktop.biomodel.BioModelEditorRightSideTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class NetworkConstraintsTableModel extends BioModelEditorRightSideTableModel<NetworkConstraintsEntity> implements java.beans.PropertyChangeListener {
	public static final String sMaxIterationName = "Max Iterations";
	public static final String sMaxMoleculesName = "Max Molecules / Species";
	public static final String sSpeciesLimitName = "Species Limit";
	public static final String sReactionsLimitName = "Reactions Limit";
	
	public static final int colCount = 2;
	public static final int iColName = 0;
	public static final int iColValue = 1;
	private static String[] columnNames = new String[] {"Name", "Value"};
	
	private SimulationContext simContext = null;
	
	public NetworkConstraintsTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}
	
	public void setSimulationContext(SimulationContext simContext) {
		if(this.simContext == simContext) {
			return;
		}
		if(this.simContext != null && this.simContext.getNetworkConstraints() != null) {
			simContext.getNetworkConstraints().removePropertyChangeListener(this); 
		}
		this.simContext = simContext;
		simContext.getNetworkConstraints().addPropertyChangeListener(this);
		
		List<NetworkConstraintsEntity> newData = computeData();
		setData(newData);
	}
	protected ArrayList<NetworkConstraintsEntity> computeData() {
		ArrayList<NetworkConstraintsEntity> nceList = new ArrayList<NetworkConstraintsEntity>();

		Model model = simContext.getModel();
		RbmModelContainer rbmModelContainer = model.getRbmModelContainer();
		if(rbmModelContainer == null) {
			return nceList;
		}
		String s1, s2, s3, s4;
		NetworkConstraintsEntity nce;
		NetworkConstraints networkConstraints = simContext.getNetworkConstraints();
		if (networkConstraints != null) {
			s1 = networkConstraints.getMaxIteration() + "";
			s2 = networkConstraints.getMaxMoleculesPerSpecies() + "";
			s3 = networkConstraints.getSpeciesLimit() + "";
			s4 = networkConstraints.getReactionsLimit() + "";
		} else {
			s1 = "?";
			s2 = "?";
			s3 = "?";
			s4 = "?";
		}
		nce = new NetworkConstraintsEntity(sMaxIterationName, s1);
		nceList.add(nce);
		nce = new NetworkConstraintsEntity(sMaxMoleculesName, s2);
		nceList.add(nce);
		nce = new NetworkConstraintsEntity(sSpeciesLimitName, s3);
		nceList.add(nce);
		nce = new NetworkConstraintsEntity(sReactionsLimitName, s4);
		nceList.add(nce);
		// we read them here, all from rbmModelContainer.getMolecularTypeList()
		// TODO: if the molecule is in the NetworkConstraints.maxStoichiometryMap set value from there
		// otherwise put "default"
//		for(MolecularType mt : rbmModelContainer.getMolecularTypeList()) {
//			nce = new NetworkConstraintsEntity(mt.getDisplayName(), "max stoichiometry", "default");
//			nceList.add(nce);
//		}		
		return nceList;
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
		
			case iColName: {
				return String.class;
			}
			case iColValue: {
				return String.class;
			}
		}
		return Object.class;
	}
	public boolean isCellEditable(int row, int column) {
//		if(column == iColValue && row < 2 ) {
//			return true;
//		}
		// the table isn't editable anymore, all is done in the Edit / Test constraints dialogs
		return false;
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}
	@Override
	public Object getValueAt(int row, int column) {
		if(simContext == null) {
			return null;
		}
		NetworkConstraintsEntity nce = getValueAt(row);
		String colName = nce.getName();
		String colValue = nce.getValue();
		NetworkConstraints networkConstraints = simContext.getNetworkConstraints();
		if(row == 0) {
			colValue = networkConstraints.getMaxIteration() + "";
		} else if(row == 1) {
			colValue = networkConstraints.getMaxMoleculesPerSpecies() + "";
		} else if(row == 2) {
			colValue = networkConstraints.getSpeciesLimit() + "";
		} else if(row == 3) {
			colValue = networkConstraints.getReactionsLimit() + "";
		}
		if(nce != null) {
			switch(column) {
			case iColName:
				return colName;
			case iColValue:
				return colValue;
			}
		}
		return null;
	}
	@Override
	public void setValueAt(Object value, int row, int column) {
		if (simContext == null || value == null) {
			return;
		}
		String text = (String)value;
		if (text == null || text.trim().length() == 0) {
			return;
		}
		NetworkConstraints networkConstraints = simContext.getNetworkConstraints();
		if(row == 0) {
			networkConstraints.setMaxIteration(Integer.valueOf(text));
		} else if(row == 1) {
			networkConstraints.setMaxMoleculesPerSpecies(Integer.valueOf(text));
		} else if(row == 2) {
			networkConstraints.setSpeciesLimit(Integer.valueOf(text));
		} else if(row == 3) {
			networkConstraints.setReactionsLimit(Integer.valueOf(text));
		}
		// TODO: add molecular type and max stoichiometry 
		// to NetworkConstraints.maxStoichiometryMap (if stoichiometry is not trivial)
		// remove the combo from map if stoichiometry for that molecular type goes back to trivial
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
//		super.propertyChange(evt);
		Object source = evt.getSource();
		
		if (source instanceof NetworkConstraints) {
			if(evt.getPropertyName().equals(NetworkConstraints.PROPERTY_NAME_MAX_ITERATION)) {
				List<NetworkConstraintsEntity> newData = computeData();
				setData(newData);
			} else if(evt.getPropertyName().equals(NetworkConstraints.PROPERTY_NAME_MOLECULES_PER_SPECIES)) {
				List<NetworkConstraintsEntity> newData = computeData();
				setData(newData);
			} else if(evt.getPropertyName().equals(NetworkConstraints.PROPERTY_NAME_SPECIES_LIMIT)) {
				List<NetworkConstraintsEntity> newData = computeData();
				setData(newData);
			} else if(evt.getPropertyName().equals(NetworkConstraints.PROPERTY_NAME_REACTIONS_LIMIT)) {
				List<NetworkConstraintsEntity> newData = computeData();
				setData(newData);
			} else {
				System.out.println("Property " + evt.getPropertyName() + " not yet implemented.");
			}
		}
	}

	@Override
	protected Model getModel() {
		return simContext == null ? null : simContext.getModel();
	}
	@Override
	protected Comparator<NetworkConstraintsEntity> getComparator(int col, boolean ascending) {
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
