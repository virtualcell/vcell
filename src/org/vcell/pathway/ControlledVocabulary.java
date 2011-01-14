package org.vcell.pathway;

import java.util.ArrayList;

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
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printStrings(sb,"term",term,level);
		printObjects(sb,"xRef",xRef,level);
	}

}
