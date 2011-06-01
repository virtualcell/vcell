/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.sbbox.factories;

/*   USTAssumptionFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for unmodifiable substance class assumptions
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.USTAssumptionImp;
import org.vcell.sybil.rdf.reason.SYBREAMO;
import com.hp.hpl.jena.rdf.model.Resource;

public class USTAssumptionFactory 
extends ThingFactory<SBBox.MutableUSTAssumption> {

	public USTAssumptionFactory(SBBox box) { super(box, SYBREAMO.UnmodifiableSubstancesClass); }
	public SBBox.MutableUSTAssumption newThing(Resource node) { 
		return new USTAssumptionImp(box, node); 
	}
	public String baseLabel() { return "UnmodSubstanceTypeAssumption"; }

}
