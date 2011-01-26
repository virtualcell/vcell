package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class Complex extends PhysicalEntity {
	private ArrayList<PhysicalEntity> component = new ArrayList<PhysicalEntity>();
	private ArrayList<Stoichiometry> componentStoichiometry = new ArrayList<Stoichiometry>();
	
	
	public ArrayList<PhysicalEntity> getComponents() {
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
	
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		for (int i=0;i<component.size();i++){
			PhysicalEntity participant = component.get(i);
			if (participant == objectProxy){
				component.set(i, (PhysicalEntity)concreteObject);
			}
		}
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObjects(sb,"component",component,level);
		printObjects(sb,"componentStoichiometry",componentStoichiometry,level);
	}

}
