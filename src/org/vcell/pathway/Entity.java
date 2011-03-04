package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public interface Entity extends BioPaxObject {
	public ArrayList<String> getName();
	public void setName(ArrayList<String> name);

	public ArrayList<String> getAvailability();
	public void setAvailability(ArrayList<String> availability);

	public ArrayList<Xref> getxRef();
	public void setxRef(ArrayList<Xref> xRef);

	public ArrayList<Provenance> getDataSource();
	public void setDataSource(ArrayList<Provenance> dataSource);

	public ArrayList<Evidence> getEvidence();
	public void setEvidence(ArrayList<Evidence> evidence);
}
