package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.common.NetworkConstraintsEntity;
import org.vcell.model.rbm.common.RbmStoichiometry;
import org.vcell.pathway.Entity;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.RbmModelContainer;

@SuppressWarnings("serial")
public class NetworkConstraintsTableModel extends VCellSortTableModel<NetworkConstraintsEntity> implements java.beans.PropertyChangeListener {

	public static final int colCount = 3;
	public static final int iColName = 0;
	public static final int iColType = 1;
	public static final int iColValue = 2;
	private static String[] columnNames = new String[] {"Name", "Type", "Value"};

	private SimulationContext simContext = null;
	


	
	public NetworkConstraintsTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}
	
	public void setSimulationContext(SimulationContext simContext) {
		if(this.simContext == simContext) {
			return;
		}
		this.simContext = simContext;
		
		List<NetworkConstraintsEntity> newData = computeData();
		setData(newData);
	}
	protected ArrayList<NetworkConstraintsEntity> computeData() {
		ArrayList<NetworkConstraintsEntity> nceList = new ArrayList<NetworkConstraintsEntity>();

		Model model = simContext.getModel();
		RbmModelContainer mc = model.getRbmModelContainer();
		if(mc == null) {
			return nceList;
		}
		String s1, s2;
		NetworkConstraintsEntity nce;
		NetworkConstraints networkConstraints = mc.getNetworkConstraints();
		if (networkConstraints != null) {
			s1 = networkConstraints.getMaxIteration() + "";
			s2 = networkConstraints.getMaxMoleculesPerSpecies() + "";
		} else {
			s1 = "?";
			s2 = "?";
		}
		nce = new NetworkConstraintsEntity("Max Iterations", "value", s1);
		nceList.add(nce);
		nce = new NetworkConstraintsEntity("Max Molecules / Species", "value", s2);
		nceList.add(nce);
		for(MolecularType mt : mc.getMolecularTypeList()) {
			nce = new NetworkConstraintsEntity(mt.getDisplayName(), "max stoichiometry", "10");
			nceList.add(nce);
		}		
		return nceList;
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
		
			case iColName: {
				return String.class;
			}
			case iColType: {
				return String.class;
			}
			case iColValue: {
				return String.class;
			}
		}
		return Object.class;
	}
	public boolean isCellEditable(int row, int column) {
		if(column == iColValue && row < 2 ) {
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
		if(simContext == null) {
			return null;
		}
		NetworkConstraintsEntity nce = getValueAt(row);
		
		if(nce != null) {
			switch(column) {
			case iColName:
				return nce.getName();
			case iColType:
				return nce.getType();
			case iColValue:
				return nce.getValue();
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
		Model model = simContext.getModel();
		RbmModelContainer mc = model.getRbmModelContainer();
		NetworkConstraints networkConstraints = mc.getNetworkConstraints();
		if(row == 0) {
			networkConstraints.setMaxIteration(Integer.valueOf(text));
		} else if(row == 1) {
			networkConstraints.setMaxMoleculesPerSpecies(Integer.valueOf(text));
		}
		return;
	}
	public String checkInputValue(String inputValue, int row, int column) {
		String errMsg = null;
		if (simContext == null) {
			errMsg = "Simulation Context Missing.";
			return errMsg;
		}
		NetworkConstraintsEntity nce = getValueAt(row);
		
		switch (column) {
		case iColValue:
			return null;
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Comparator<NetworkConstraintsEntity> getComparator(int col, boolean ascending) {
		// TODO Auto-generated method stub
		return null;
	}


}
