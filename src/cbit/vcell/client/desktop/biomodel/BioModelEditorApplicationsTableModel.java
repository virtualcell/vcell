package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;

import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.gui.DialogUtils;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.desktop.Annotation;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorApplicationsTableModel extends BioModelEditorRightSideTableModel<SimulationContext> {	
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_MATH_TYPE = 1;
	public final static int COLUMN_ANNOTATION = 2;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private static String[] columnNames = new String[] {"Name", "Math Type", "Annotation"};

	public BioModelEditorApplicationsTableModel(JTable table) {
		super(table);
		setColumns(columnNames);
		addPropertyChangeListener(this);
	}

	public Class<?> getColumnClass(int column) {
		switch (column){		
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_MATH_TYPE: {
				return String.class;
			}
			case COLUMN_ANNOTATION: {
				return String.class;
			}
		}
		return Object.class;
	}

	protected ArrayList<SimulationContext> computeData() {
		ArrayList<SimulationContext> simulationContextList = new ArrayList<SimulationContext>();
		if (bioModel != null){
			for (SimulationContext simulationContext : bioModel.getSimulationContexts()){
				if (searchText == null || searchText.length() == 0 || simulationContext.getName().startsWith(searchText)) {
					simulationContextList.add(simulationContext);
				}
			}
		}
		return simulationContextList;
	}

	public Object getValueAt(int row, int column) {
		if (bioModel == null) {
			return null;
		}
		try{
			SimulationContext simulationContext = getValueAt(row);
			switch (column) {
				case COLUMN_NAME: {
					return simulationContext.getName();
				} 
				case COLUMN_MATH_TYPE: {
					return simulationContext.getMathType();
				} 
				case COLUMN_ANNOTATION: {
					return simulationContext.getDescription();
				} 
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex != COLUMN_MATH_TYPE;
	}
	
	protected boolean containedByModel() {
		return false;
	}
	
	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (bioModel == null) {
			return;
		}
		try{
			SimulationContext simulationContext = getValueAt(row);
			String inputValue = (String)value;
			switch (column) {
			case COLUMN_NAME: {
				if (!inputValue.equals(ADD_NEW_HERE_TEXT)) {
					simulationContext.setName(inputValue);
				}
				break;
			} 
			case COLUMN_ANNOTATION: {
				simulationContext.setDescription(inputValue);
				break;
			}
			}
			if (row == getDataSize()) {
				bioModel.addSimulationContext(simulationContext);
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
	public Comparator<SimulationContext> getComparator(int col, boolean ascending) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String checkInputValue(String inputValue, int row, int column) {
		SimulationContext simulationContext = null;
		if (row >= 0 && row < getDataSize()) {
			simulationContext = getValueAt(row);
		}
		switch (column) {
		case COLUMN_NAME:
			if (simulationContext != null && simulationContext.getName().equals(inputValue)) {
				return null; // name did not change
			}
			if (bioModel.getSimulationContext(inputValue) != null) {
				return BioModel.SIMULATION_CONTEXT_DISPLAY_NAME + " '" + inputValue + "' already exist!";
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
		return null;
	}

	@Override
	public int getRowCount() {
		return getDataSize();
	}
}
