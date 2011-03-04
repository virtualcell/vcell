package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

import cbit.vcell.biomodel.meta.Identifiable;

public interface BioPaxObject extends Identifiable {

	String getID();
	void setID(String value);
	boolean hasID();
	
	String resourceFromID();

	public ArrayList<String> getComments();
	public void setComments(ArrayList<String> comment);
	
	void show(StringBuffer stringBuffer);
	void showChildren(StringBuffer stringBuffer, int level);
	void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject);
	
	String getTypeLabel();

}
