package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class ControlledVocabulary extends BioPaxObjectImpl implements UtilityClass {
	private ArrayList<String> term = new ArrayList<String>();
	private ArrayList<Xref> xRef = new ArrayList<Xref>();
	
	public ArrayList<Xref> getxRef() {
		return xRef;
	}
	public void setxRef(ArrayList<Xref> xRef) {
		this.xRef = xRef;
	}

	public ArrayList<String> getTerm() {
		return term;
	}

	public void setTerm(ArrayList<String> term) {
		this.term = term;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){

		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing == objectProxy) {
				xRef.set(i, (Xref)concreteObject);
			}
		}
}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printStrings(sb,"term",term,level);
		printObjects(sb,"xRef",xRef,level);
	}

}
