package org.vcell.model.rbm.gui;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.RbmUtils;

import cbit.vcell.bionetgen.ObservableGroup;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.RbmObservable;

public class ObservablesGroupTableRow {
	private String index;
	private ObservableGroup  observableGroupObject;
	private NetworkConstraintsPanel owner;
		
	public ObservablesGroupTableRow(ObservableGroup observableGroupObject, String index, NetworkConstraintsPanel owner) {
		this.setObservableGroupObject(observableGroupObject);
		this.setIndex(index);
		this.setOwner(owner);
	}

	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}

	public ObservableGroup getObservableGroupObject() {
		return observableGroupObject;
	}
	public void setObservableGroupObject(ObservableGroup observableGroupObject) {
		this.observableGroupObject = observableGroupObject;
	}

	public NetworkConstraintsPanel getOwner() {
		return owner;
	}
	public void setOwner(NetworkConstraintsPanel owner) {
		this.owner = owner;
	}
	
	public RbmObservable getObservable(String name) {
		if(owner != null && owner.getSimulationContext() != null) {
			RbmModelContainer rbmmc = owner.getSimulationContext().getModel().getRbmModelContainer();
			if(rbmmc != null) {
				return rbmmc.getObservable(name);
			}
		}
		return null;
	}
	
	public static String toBnglString(RbmObservable observable) {
		String s = "";
		for(SpeciesPattern sp : observable.getSpeciesPatternList()) {
			s += "@" + observable.getStructure().getName() + ":";
			s += RbmUtils.toBnglString(sp, null, CompartmentMode.hide, 0) + " ";
		}
		return s;
	}
	public static String toBnglStringEx(RbmObservable observable) {
		String s = toBnglString(observable);
		switch(observable.getSequence()) {
		case Multimolecular:
			return s;
		case PolymerLengthEqual:
			return s + "=" + observable.getSequenceLength();
		case PolymerLengthGreater:
			return s + ">" + observable.getSequenceLength();
		default:
			return s;
		}
	}
	
}
