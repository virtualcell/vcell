package org.vcell.pathway;

import java.util.ArrayList;

public class Catalysis extends Control {
	private String catalysisDirection;
	private ArrayList<PhysicalEntity> cofactors;

	public Catalysis(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

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
	

}
