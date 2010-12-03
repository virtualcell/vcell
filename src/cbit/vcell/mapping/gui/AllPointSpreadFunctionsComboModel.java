package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultComboBoxModel;

import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.SpeciesContext;

public class AllPointSpreadFunctionsComboModel extends DefaultComboBoxModel implements PropertyChangeListener {

	private SimulationContext simulationContext = null;
	private MicroscopeMeasurement microscopyMeasurement = null;

	public MicroscopeMeasurement getMicroscopyMeasurement() {
		return microscopyMeasurement;
	}

	public void setMicroscopyMeasurement(MicroscopeMeasurement microscopyMeasurement) {
		if (this.microscopyMeasurement!=null){
			this.microscopyMeasurement.removePropertyChangeListener(this);
		}
		this.microscopyMeasurement = microscopyMeasurement;
		if (this.microscopyMeasurement!=null){
			this.microscopyMeasurement.addPropertyChangeListener(this);
		}
		refresh();
	}

	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	public void setSimulationContext(SimulationContext argSimulationContext) {
		this.simulationContext = argSimulationContext;
		System.out.println("setSimulationContext");
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
		if (microscopyMeasurement!=null){
			for (DataSymbol dataSymbol : simulationContext.getDataContext().getDataSymbols()){
				if (dataSymbol.getDataSymbolType().equals(DataSymbolType.POINT_SPREAD_FUNCTION)){
					addElement(dataSymbol.getName());
				}
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println(evt.getPropertyName());
		refresh();
	}
}
