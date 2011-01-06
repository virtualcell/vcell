package org.vcell.pathway;

public class Score implements UtilityClass {
	private Provenance scoreSource;
	private String value;
	public Provenance getScoreSource() {
		return scoreSource;
	}
	public String getValue() {
		return value;
	}
	public void setScoreSource(Provenance scoreSource) {
		this.scoreSource = scoreSource;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
