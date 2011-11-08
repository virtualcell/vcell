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

import java.util.Set;

import org.openrdf.model.Resource;
import org.sbpax.schemas.UOMECore;
import org.sbpax.util.sets.SetUtil;
import org.vcell.sybil.rdf.pool.RDFObjectSimplePool;
import org.vcell.sybil.rdf.pool.UnsupportedRDFTypeException;
import org.vcell.uome.core.UOMEEquivalenzExpression;
import org.vcell.uome.core.UOMEExponentialExpression;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMEOffsetExpression;
import org.vcell.uome.core.UOMEProductExpression;
import org.vcell.uome.core.UOMEQuotientExpression;
import org.vcell.uome.core.UOMEScalingExpression;

public class UOMEExpressionPool extends RDFObjectSimplePool<UOMEExpression> {

	public static final Set<Resource> SUPPORTED_TYPES = 
		SetUtil.<Resource>newSet(UOMECore.EquivalenzExpression, UOMECore.ScalingExpression,
				UOMECore.OffsetExpression, UOMECore.ExponentialExpression, 
				UOMECore.ProductExpression, UOMECore.QuotientExpression);

	@Override
	protected UOMEExpression createObjectOnly(Resource resource,
			Resource type) throws UnsupportedRDFTypeException {
		if(UOMECore.EquivalenzExpression.equals(type)) {
			return new UOMEEquivalenzExpression();
		} else if(UOMECore.ScalingExpression.equals(type)) {
			return new UOMEScalingExpression();
		} else if(UOMECore.OffsetExpression.equals(type)) {
			return new UOMEOffsetExpression();
		} else if(UOMECore.ExponentialExpression.equals(type)) {
			return new UOMEExponentialExpression();
		} else if(UOMECore.ProductExpression.equals(type)) {
			return new UOMEProductExpression();
		} else if(UOMECore.QuotientExpression.equals(type)) {
			return new UOMEQuotientExpression();
		} else {
			throw new UnsupportedRDFTypeException("Can not create UOMEExpression if resource " +
					"is not of expression type.");
		}
	}

	@Override
	protected UOMEExpression createObjectOnly(Resource resource,
			Set<Resource> types) throws UnsupportedRDFTypeException {
		if(types.contains(UOMECore.EquivalenzExpression)) {
			return new UOMEEquivalenzExpression();
		} else if(types.contains(UOMECore.ScalingExpression)) {
			return new UOMEScalingExpression();
		} else if(types.contains(UOMECore.OffsetExpression)) {
			return new UOMEOffsetExpression();
		} else if(types.contains(UOMECore.ExponentialExpression)) {
			return new UOMEExponentialExpression();
		} else if(types.contains(UOMECore.ProductExpression)) {
			return new UOMEProductExpression();
		} else if(types.contains(UOMECore.QuotientExpression)) {
			return new UOMEQuotientExpression();
		} else {
			throw new UnsupportedRDFTypeException("Can not create UOMEExpression if resource " +
					"is not of expression type.");
		}
	}

	@Override
	public Set<Resource> getSupportedTypes() { return SUPPORTED_TYPES; }

	@Override
	public Class<UOMEExpression> getCreatedClass() { return UOMEExpression.class; }

	@Override
	public boolean canSuggestType(UOMEExpression expression) {
		return expression instanceof UOMEEquivalenzExpression 
		|| expression instanceof UOMEScalingExpression || expression instanceof UOMEOffsetExpression 
		|| expression instanceof UOMEExponentialExpression 
		|| expression instanceof UOMEProductExpression 
		|| expression instanceof UOMEQuotientExpression;
	}

	@Override
	public Resource getSuggestedType(UOMEExpression expression) throws UnsupportedRDFTypeException {
		if(expression instanceof UOMEEquivalenzExpression) {
			return UOMECore.EquivalenzExpression;
		} else if(expression instanceof UOMEScalingExpression) {
			return UOMECore.ScalingExpression;
		} else if(expression instanceof UOMEOffsetExpression) {
			return UOMECore.OffsetExpression;
		} else if(expression instanceof UOMEExponentialExpression) {
			return UOMECore.ExponentialExpression;
		} else if(expression instanceof UOMEProductExpression) {
			return UOMECore.ProductExpression;
		} else if(expression instanceof UOMEQuotientExpression) {
			return UOMECore.QuotientExpression;
		} else {
			throw new UnsupportedRDFTypeException("Can not suggest a type for this kind of object.");			
		}
	}

}
