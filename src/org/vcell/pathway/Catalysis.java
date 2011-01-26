package org.vcell.pathway;

public class Catalysis extends Control {
	private String catalysisDirection;

	public String getCatalysisDirection() {
		return catalysisDirection;
	}

	public void setCatalysisDirection(String catalysisDirection) {
		this.catalysisDirection = catalysisDirection;
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"catalysisDirection",catalysisDirection,level);
	}

}
