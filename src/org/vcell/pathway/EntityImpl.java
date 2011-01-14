package org.vcell.pathway;

import java.util.ArrayList;

import cbit.vcell.biomodel.meta.Identifiable;

public class EntityImpl extends BioPaxObjectImpl implements Entity, Identifiable {
	
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<Xref> xRef = new ArrayList<Xref>();
	private ArrayList<Provenance> dataSource = new ArrayList<Provenance>();
	private ArrayList<Evidence> evidence = new ArrayList<Evidence>();
	
	public ArrayList<String> getName() {
		return name;
	}

	public void setName(ArrayList<String> name) {
		this.name = name;
	}

	public ArrayList<Xref> getxRef() {
		return xRef;
	}

	public void setxRef(ArrayList<Xref> xRef) {
		this.xRef = xRef;
	}

	public ArrayList<Provenance> getDataSource() {
		return dataSource;
	}

	public void setDataSource(ArrayList<Provenance> dataSource) {
		this.dataSource = dataSource;
	}

	public ArrayList<Evidence> getEvidence() {
		return evidence;
	}

	public void setEvidence(ArrayList<Evidence> evidence) {
		this.evidence = evidence;
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printStrings(sb, "name",name,level);
		printObjects(sb, "xRef",xRef,level);
		printObjects(sb, "dataSource",dataSource,level);
		printObjects(sb, "evidence",evidence,level);
	}
}
