package org.vcell.pathway.sbpax;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BioPaxObjectImpl;
import org.vcell.pathway.UtilityClass;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class UnitOfMeasurement extends BioPaxObjectImpl implements UtilityClass {

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
}
