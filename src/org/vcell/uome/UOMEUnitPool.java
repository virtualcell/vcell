/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome;

import java.util.Collections;
import java.util.Set;

import org.openrdf.model.Resource;
import org.sbpax.schemas.UOMECore;
import org.vcell.sybil.rdf.pool.RDFObjectSimplePool;
import org.vcell.sybil.rdf.pool.UnsupportedRDFTypeException;
import org.vcell.uome.core.UOMEUnit;

public class UOMEUnitPool extends RDFObjectSimplePool<UOMEUnit> {

	@Override
	protected UOMEUnit createObjectOnly(Resource resource, Resource type) 
	throws UnsupportedRDFTypeException {
		if(!UOMECore.UnitOfMeasurement.equals(type)) {
			throw new UnsupportedRDFTypeException("To create UOME unit, need resource of " +
					"type uome:UnitOfMeasurement.");
		}
		return new UOMEUnit();
	}

	@Override
	protected UOMEUnit createObjectOnly(Resource resource, Set<Resource> types) 
	throws UnsupportedRDFTypeException {
		if(!types.contains(UOMECore.UnitOfMeasurement)) {
			throw new UnsupportedRDFTypeException("To create UOME unit, need resource of " +
					"type uome:UnitOfMeasurement.");
		}
		return new UOMEUnit();
	}

	@Override
	public Set<Resource> getSupportedTypes() {
		return Collections.<Resource>singleton(UOMECore.UnitOfMeasurement);
	}

	@Override
	public Class<UOMEUnit> getCreatedClass() { return UOMEUnit.class; }

	@Override	
	public boolean canSuggestType(UOMEUnit unit) { return true; }
	
	@Override
	public Resource getSuggestedType(UOMEUnit object) { return UOMECore.UnitOfMeasurement; }

}
