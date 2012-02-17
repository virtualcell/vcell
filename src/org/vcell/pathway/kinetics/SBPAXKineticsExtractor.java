/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.kinetics;

import java.util.Set;

import org.vcell.pathway.sbo.SBOList;
import org.vcell.pathway.sbpax.SBEntity;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;

public class SBPAXKineticsExtractor {
	
	public static void extractKinetics(ReactionStep reaction, Set<SBEntity> entities) {
		KineticContext context = new KineticContext(reaction, entities);
		KineticLawBuilder bestBuilder = null;
		KineticLawBuilder.Match bestBuilderMatch = KineticLawBuilder.Match.NONE;
		for(KineticLawBuilder builder : KineticLawBuilder.DEFAULT_LIST) {
			KineticLawBuilder.Match builderMatch = builder.getMatch(context);
			if(builderMatch.isBetterThan(bestBuilderMatch)) {
				bestBuilder = builder;
				bestBuilderMatch = builderMatch;
			}
		}
		if(bestBuilderMatch.isBetterThan(KineticLawBuilder.Match.NONE)) {
			bestBuilder.addKinetics(context);
		}
	}
	
}
