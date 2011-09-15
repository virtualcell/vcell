package org.vcell.pathway.sbpax;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.ControlledVocabulary;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class SBVocabulary extends ControlledVocabulary{

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}

}
