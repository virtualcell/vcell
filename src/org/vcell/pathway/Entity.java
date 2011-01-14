package org.vcell.pathway;

import java.util.ArrayList;

public interface Entity extends BioPaxObject {
	public ArrayList<String> getName();
	public void setName(ArrayList<String> name);

	public ArrayList<Xref> getxRef();
	public void setxRef(ArrayList<Xref> xRef);

	public ArrayList<Provenance> getDataSource();
	public void setDataSource(ArrayList<Provenance> dataSource);

	public ArrayList<Evidence> getEvidence();
	public void setEvidence(ArrayList<Evidence> evidence);
}
