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

import java.util.HashSet;
import java.util.Set;

import org.vcell.pathway.Xref;
import org.vcell.pathway.sbo.SBOList;
import org.vcell.pathway.sbo.SBOTerm;
import org.vcell.pathway.sbo.SBOUtil;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBVocabulary;

public class SBPAXSBOExtractor {
	
	public static Set<SBOTerm> extractSBOTerms(SBEntity entity) {
		Set<SBOTerm> sboTerms = new HashSet<SBOTerm>();
		for(SBVocabulary sbTerm : entity.getSBTerm()) {
			sboTerms.addAll(extractSBOTerms(sbTerm));
		}
		return sboTerms;
	}
		
	public static Set<SBOTerm> extractSBOTerms(SBVocabulary sbTerm) {
		Set<SBOTerm> sboTerms = new HashSet<SBOTerm>();
		for(Xref xref : sbTerm.getxRef()) {
			String db = xref.getDb().trim();
			String id = xref.getId().trim();
			if(db != null && id != null) {
				if(db.equalsIgnoreCase("sbo") || db.equalsIgnoreCase("systems biology ontology")) {
					SBOTerm sboTerm = SBOList.getTermFromIndex(SBOUtil.getIndexFromId(id));
					if(sboTerm != null) {
						sboTerms.add(sboTerm);
					}
				}
			}
		}
		return sboTerms;
	}

}
