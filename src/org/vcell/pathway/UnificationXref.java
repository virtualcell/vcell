package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class UnificationXref extends Xref {
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}

}
