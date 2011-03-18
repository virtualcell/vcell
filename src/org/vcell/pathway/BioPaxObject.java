package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.util.Matchable;

import cbit.vcell.biomodel.meta.Identifiable;

public interface BioPaxObject extends Identifiable, Matchable {

	String getID();
	void setID(String value);
	boolean hasID();
	
	String resourceFromID();

	public ArrayList<String> getComments();
	public void setComments(ArrayList<String> comment);
	
	void show(StringBuffer stringBuffer);
	boolean fullCompare(HashSet<BioPaxObject> theirBiopaxObjects);
	boolean compareEqual(Matchable obj);
	void showChildren(StringBuffer stringBuffer, int level);
	void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject);
	
	String getTypeLabel();

}
