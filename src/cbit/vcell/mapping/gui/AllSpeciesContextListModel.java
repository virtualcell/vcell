package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.vcell.util.gui.DefaultListModelCivilized;

import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.SpeciesContext;

public class AllSpeciesContextListModel extends DefaultListModelCivilized implements PropertyChangeListener {
	
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
		refresh();
	}

	public void refresh() {
		removeAllElements();
		if (simulationContext!=null && microscopyMeasurement!=null){
			for (SpeciesContext sc : simulationContext.getModel().getSpeciesContexts()){
				if (!microscopyMeasurement.contains(sc)){
					addElement(sc);
				}
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}

}
