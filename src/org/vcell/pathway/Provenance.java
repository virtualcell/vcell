package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Provenance extends BioPaxObjectImpl implements UtilityClass {
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<Xref> xRef = new ArrayList<Xref>();

	public ArrayList<Xref> getxRef() {
		return xRef;
	}

	public void setxRef(ArrayList<Xref> xRef) {
		this.xRef = xRef;
	}

	public ArrayList<String> getName() {
		return name;
	}

	public void setName(ArrayList<String> name) {
		this.name = name;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing == objectProxy) {
				xRef.set(i, (Xref)concreteObject);
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printStrings(sb, "name",name,level);
		printObjects(sb, "xRef",xRef,level);
	}

}
