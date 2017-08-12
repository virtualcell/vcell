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

import java.util.List;
import java.util.Set;

import org.sbpax.util.StringUtil;
import org.vcell.pathway.kinetics.SBPAXSBOExtractor;
import org.vcell.pathway.sbo.SBOTerm;

public class SBPAXLabelUtil {

	public static String makeLabel(SBEntity entity) {
		String label = "";
		if(entity instanceof SBMeasurable) {
			SBMeasurable measurable = (SBMeasurable) entity;
			List<Double> numbers = measurable.getNumber();
			if(numbers != null && !numbers.isEmpty()) {
				label = label + numbers.get(0) + " ";
			} else {
				label = "? ";
			}
			List<UnitOfMeasurement> units = measurable.getUnit();
			if(units != null && !units.isEmpty()) {
				label = label + makeLabel(units.get(0)) + " ";
			} else {
				label = label + "? ";
			}
		}
		List<SBVocabulary> vocabularies = entity.getSBTerm();
		if(vocabularies != null && !vocabularies.isEmpty()) {
			label = label + "(" + makeLabel(vocabularies.get(0)) + ")";
		} else {
			label = label + "(???)";
		}
		return label;
	}
	
	public static String makeLabel(UnitOfMeasurement unit) {
		if(!unit.getSymbols().isEmpty()) {
			return unit.getSymbols().iterator().next();
		}
		return "[?]";
	}
	
	public static String makeLabel(SBVocabulary vocabulary) {
		Set<SBOTerm> sboTerms = SBPAXSBOExtractor.extractSBOTerms(vocabulary);
		for(SBOTerm sboTerm : sboTerms) {
			String symbol = sboTerm.getSymbol();
			if(StringUtil.notEmpty(symbol)) {
				return symbol;
			} else {
				return sboTerm.getId();
			}
		}
		return "???";
	}
	
}
