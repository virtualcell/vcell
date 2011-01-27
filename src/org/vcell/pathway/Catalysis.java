package org.vcell.pathway;

import java.util.ArrayList;

public class Catalysis extends Control {
	private String catalysisDirection;
	private ArrayList<PhysicalEntity> cofactors  = new ArrayList<PhysicalEntity>();

	public String getCatalysisDirection() {
		return catalysisDirection;
	}
	public ArrayList<PhysicalEntity> getCofactors() {
		return cofactors;
	}

	public void setCatalysisDirection(String catalysisDirection) {
		this.catalysisDirection = catalysisDirection;
	}
	public void setCofactors(ArrayList<PhysicalEntity> cofactors) {
		this.cofactors = cofactors;
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"catalysisDirection",catalysisDirection,level);
		printObjects(sb,"physicalControllers",cofactors,level);
	}

}
