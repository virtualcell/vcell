package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.vcell.util.gui.DefaultListModelCivilized;

import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.SpeciesContext;

@SuppressWarnings("serial")
public class FluorescenceSpeciesContextListModel extends DefaultListModelCivilized implements PropertyChangeListener {
	
	
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
			if (simulationContext.getMicroscopeMeasurement() != null) {		
				simulationContext.getMicroscopeMeasurement().removePropertyChangeListener(this);
			}
		}
		this.simulationContext = argSimulationContext;
		if (simulationContext != null) {
			simulationContext.addPropertyChangeListener(this);
			if (simulationContext.getMicroscopeMeasurement() != null) {		
				simulationContext.getMicroscopeMeasurement().addPropertyChangeListener(this);
			}
		}
		refresh();
	}

	public void refresh() {
		removeAllElements();
		if (simulationContext!=null && simulationContext.getMicroscopeMeasurement()!=null){
			for (SpeciesContext sc : simulationContext.getMicroscopeMeasurement().getFluorescentSpecies()){
				addElement(sc);
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}

}
