package org.vcell.pathway;

import java.util.ArrayList;

public class Complex extends PhysicalEntity {
	private ArrayList<PhysicalEntity> component;
	private ArrayList<Stoichiometry> componentStoichiometry;
	
	
	public Complex(String name) {
		super(name);
	}


	public ArrayList<PhysicalEntity> getComponent() {
		return component;
	}


	public ArrayList<Stoichiometry> getComponentStoichiometry() {
		return componentStoichiometry;
	}


	public void setComponent(ArrayList<PhysicalEntity> component) {
		this.component = component;
	}


	public void setComponentStoichiometry(
			ArrayList<Stoichiometry> componentStoichiometry) {
		this.componentStoichiometry = componentStoichiometry;
	}
	

}
