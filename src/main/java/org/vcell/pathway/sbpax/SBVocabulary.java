/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
