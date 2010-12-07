package cbit.vcell.mapping.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.vcell.util.gui.DefaultListModelCivilized;

import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.model.SpeciesContext;

@SuppressWarnings("serial")
public class FluorescenceSpeciesContextListModel extends DefaultListModelCivilized implements PropertyChangeListener {
	
	private MicroscopeMeasurement microscopeMeasurement = null;
	

	public void setMicroscopeMeasurement(MicroscopeMeasurement argMicroscopeMeasurement) {
		if (this.microscopeMeasurement == argMicroscopeMeasurement) {
			return;
		}
		if (this.microscopeMeasurement!=null){
			this.microscopeMeasurement.removePropertyChangeListener(this);
		}
		this.microscopeMeasurement = argMicroscopeMeasurement;
		if (this.microscopeMeasurement!=null){
			this.microscopeMeasurement.addPropertyChangeListener(this);
		}
		refresh();
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
		}
	}
}
