package org.vcell.pathway;

import java.util.ArrayList;

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
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printStrings(sb, "name",name,level);
		printObjects(sb, "xRef",xRef,level);
	}

}
