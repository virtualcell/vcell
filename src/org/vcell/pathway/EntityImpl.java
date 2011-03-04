package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

import cbit.vcell.biomodel.meta.Identifiable;

public class EntityImpl extends BioPaxObjectImpl implements Entity, Identifiable {
	
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<String> availability = new ArrayList<String>();
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

	public ArrayList<String> getAvailability() {
		return this.availability;
	}

	public void setAvailability(ArrayList<String> availability) {
		this.availability = availability;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing == objectProxy) {
				xRef.set(i, (Xref)concreteObject);
			}
		}
		for (int i=0; i<dataSource.size(); i++) {
			Provenance thing = dataSource.get(i);
			if(thing == objectProxy) {
				dataSource.set(i, (Provenance)concreteObject);
			}
		}
		for (int i=0; i<evidence.size(); i++) {
			Evidence thing = evidence.get(i);
			if(thing == objectProxy) {
				evidence.set(i, (Evidence)concreteObject);
			}
		}
	}
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printStrings(sb, "name",name,level);
		printStrings(sb, "availability",availability,level);
		printObjects(sb, "xRef",xRef,level);
		printObjects(sb, "dataSource",dataSource,level);
		printObjects(sb, "evidence",evidence,level);
	}
}
