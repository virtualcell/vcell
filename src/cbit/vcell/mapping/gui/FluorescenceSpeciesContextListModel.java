package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.vcell.util.gui.DefaultListModelCivilized;

import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.model.SpeciesContext;

public class FluorescenceSpeciesContextListModel extends DefaultListModelCivilized implements PropertyChangeListener {
	
	private MicroscopeMeasurement microscopeMeasurement = null;
	

	public void setMicroscopeMeasurement(MicroscopeMeasurement argMicroscopeMeasurement) {
		if (this.microscopeMeasurement!=null){
			this.microscopeMeasurement.removePropertyChangeListener(this);
		}
		this.microscopeMeasurement = argMicroscopeMeasurement;
		if (this.microscopeMeasurement!=null){
			this.microscopeMeasurement.addPropertyChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}
	
	private void refresh(){
		removeAllElements();
		if (microscopeMeasurement!=null){
			for (SpeciesContext sc : microscopeMeasurement.getFluorescentSpecies()){
				addElement(sc);		// add all elements
			}
			this.microscopeMeasurement.addPropertyChangeListener(this);
		}
	}

}
