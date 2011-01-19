package org.vcell.pathway;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

import cbit.vcell.biomodel.meta.Identifiable;

public interface BioPaxObject extends Identifiable {

	String getID();
	void setID(String value);
	
	String getResource();
	void setResource(String value);

	String getComment();
	void setComment(String value);
	
	void show(StringBuffer stringBuffer);
	
	void showChildren(StringBuffer stringBuffer, int level);
	
	void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject);
	
	String getTypeLabel();

}
