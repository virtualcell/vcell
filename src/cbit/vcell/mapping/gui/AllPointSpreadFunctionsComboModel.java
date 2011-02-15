package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultComboBoxModel;

import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class AllPointSpreadFunctionsComboModel extends DefaultComboBoxModel implements PropertyChangeListener {

	private SimulationContext simulationContext = null;

	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	public void setSimulationContext(SimulationContext argSimulationContext) {
		if (simulationContext == argSimulationContext) {
			return;
		}
		if (simulationContext != null) {
			simulationContext.removePropertyChangeListener(this);
			if (simulationContext.getDataContext() != null) {
				simulationContext.getDataContext().removePropertyChangeListener(this);
			}
			if (simulationContext.getMicroscopeMeasurement() != null) {		
				simulationContext.getMicroscopeMeasurement().removePropertyChangeListener(this);
			}
		}
		this.simulationContext = argSimulationContext;
		if (simulationContext != null) {
			simulationContext.addPropertyChangeListener(this);
			if (simulationContext.getDataContext() != null) {
				simulationContext.getDataContext().addPropertyChangeListener(this);
			}
			if (simulationContext.getMicroscopeMeasurement() != null) {		
				simulationContext.getMicroscopeMeasurement().addPropertyChangeListener(this);
			}
		}		
//		System.out.println("setSimulationContext");
		refresh();
	}

	public int getSize() {
		if(simulationContext == null) {
			return 0;
		}
		int size = 0;
		for (DataSymbol dataSymbol : simulationContext.getDataContext().getDataSymbols()){
			if (dataSymbol.getDataSymbolType().equals(DataSymbolType.POINT_SPREAD_FUNCTION)){
				size++;
			}
		}
		return size;
	}
	
	public void refresh() {
		removeAllElements();
		if(simulationContext == null) {
			return;
		}
		if (simulationContext.getMicroscopeMeasurement() != null){
			for (DataSymbol dataSymbol : simulationContext.getDataContext().getDataSymbols()){
				if (dataSymbol.getDataSymbolType().equals(DataSymbolType.POINT_SPREAD_FUNCTION)){
					addElement(dataSymbol.getName());
				}
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
//		System.out.println(evt.getPropertyName());
		refresh();
	}
}
