package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.util.Comparator;

import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.common.NetworkConstraintsEntity;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;


@SuppressWarnings("serial")
public class ApplicationNetworkConstraintsTableModel extends VCellSortTableModel<NetworkConstraintsEntity> implements java.beans.PropertyChangeListener {

	public static final int colCount = 3;
	public static final int iColEntity = 0;
	public static final int iColType = 1;
	public static final int iColValue = 2;

	

	
	public ApplicationNetworkConstraintsTableModel(EditorScrollTable molecularTypeTable) {
		
		
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
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
