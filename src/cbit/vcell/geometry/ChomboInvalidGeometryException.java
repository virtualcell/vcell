/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

import org.vcell.chombo.ChomboMeshValidator.ChomboMeshRecommendation;

/**
 * {@link ChomboInvalidGeometryException} represents a problem when EBChombo solver can't be used with the geometry.
 * 
 * @author developer
 *
 */
public class ChomboInvalidGeometryException extends Exception {

	private ChomboMeshRecommendation recommendation;

	public ChomboInvalidGeometryException(ChomboMeshRecommendation recommendation) {
		super(recommendation.getErrorMessage());
		this.recommendation = recommendation;
	}

	public ChomboMeshRecommendation getRecommendation() {
		return recommendation;
	}
}
