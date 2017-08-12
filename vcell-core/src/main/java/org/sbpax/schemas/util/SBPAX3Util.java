/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas.util;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.sbpax.schemas.BioPAX3;
import org.sbpax.schemas.SBPAX3;
import org.sbpax.schemas.UOMEList;
import org.vcell.pathway.sbpax.SBEntity;


public class SBPAX3Util {
	
	public static URI addXRef(Graph model, String uri, String term, String db, String id) {
		URI xRef = model.getValueFactory().createURI(uri);
		model.add(xRef, RDF.TYPE, BioPAX3.UnificationXref);
		model.add(xRef, BioPAX3.db, model.getValueFactory().createLiteral(db));
		model.add(xRef, BioPAX3.id, model.getValueFactory().createLiteral(id));
		model.add(xRef, BioPAX3.comment, model.getValueFactory().createLiteral(term));
		return xRef;
	}
	
	public static URI addSBTerm(Graph model, String baseURI, String term, String ... refs) {
		URI sbTerm = model.getValueFactory().createURI(baseURI + "Term");
		model.add(sbTerm, RDF.TYPE, SBPAX3.SBVocabulary);
		int i = 0;
		if(refs.length % 2 ==1) {
			URI xRef = SBPAX3Util.addXRef(model, baseURI + "SBO" + "Ref", term, "SBO", refs[0]); 
			model.add(sbTerm, BioPAX3.xref, xRef);
			++i;
		}
		while(i < refs.length - 1) {
			URI xRef = 
				SBPAX3Util.addXRef(model, baseURI + refs[i] + "Ref", term, refs[i], refs[i+1]); 
			model.add(sbTerm, BioPAX3.xref, xRef);
			i += 2;
		}
		model.add(sbTerm, BioPAX3.term, model.getValueFactory().createLiteral(term));	
		return sbTerm;
	}

	public static URI addSubEntity(Graph model, String uri, Resource parent, Resource sbTerm) {
		URI subEntity = model.getValueFactory().createURI(uri);
		model.add(subEntity, RDF.TYPE, SBPAX3.SBEntity);
		model.add(subEntity, SBPAX3.sbTerm, sbTerm);
		model.add(parent, SBPAX3.sbSubEntity, subEntity);		
		return subEntity;
	}
	
	public static URI addSBState(Graph model, String uri, Resource parent, Resource sbTerm) {
		URI sbState = model.getValueFactory().createURI(uri);
		model.add(sbState, RDF.TYPE, SBPAX3.SBState);
		model.add(sbState, SBPAX3.sbTerm, sbTerm);
		model.add(parent, SBPAX3.sbSubEntity, sbState);		
		return sbState;
	}
	
	public static URI addMeasurableSubEntity(Graph model, String uri, Resource parent, 
			Resource sbTerm, double value, Resource unit) {
		URI measurable = model.getValueFactory().createURI(uri);
		model.add(measurable, RDF.TYPE, SBPAX3.SBMeasurable);
		model.add(measurable, SBPAX3.sbTerm, sbTerm);
		model.add(measurable, SBPAX3.hasNumber, model.getValueFactory().createLiteral(value));
		model.add(measurable, SBPAX3.hasUnit, unit);
		model.add(parent, SBPAX3.sbSubEntity, measurable);
		return measurable;
	}
	
	public static void addMichaelisMentenKineticsKmKcat(Graph model, String NS, Resource catalysis, 
			String index, double kM, double kCat) {
		URI kinetics = model.getValueFactory().createURI(NS + "kinetics" + index);
		model.add(kinetics, RDF.TYPE, SBPAX3.SBState);
		Resource michaelisMentenKinetics = 
			SBPAX3Util.addSBTerm(model, NS + "michaelisMentenKinetics", "Henri-Michaelis-Menten rate law", 
					"0000029");
		model.add(kinetics, SBPAX3.sbTerm, michaelisMentenKinetics);
		model.add(catalysis, SBPAX3.sbSubEntity, kinetics);
		Resource michaelisSubstrateConcentration = 
			SBPAX3Util.addSBTerm(model, NS + "michaelisSubstrateConcentration", 
					"Michaelis constant for substrate", "0000322", "MetaCyc", "Km");
		SBPAX3Util.addMeasurableSubEntity(model, NS + "kM" + index, kinetics, michaelisSubstrateConcentration, 
				kM, UOMEList.MolePerMilligram);
		Resource catalyticRateConstant = 
			SBPAX3Util.addSBTerm(model, NS + "catalyticRateConstant", "catalytic rate constant", "0000025",
					"MetaCyc", "kcat");
		SBPAX3Util.addMeasurableSubEntity(model, NS + "kCat" + index, kinetics, catalyticRateConstant, kCat, 
				UOMEList.PerSecond);
	}
	
	public static Set<SBEntity> extractAllEntities(SBEntity entity) {
		Set<SBEntity> entities = new HashSet<SBEntity>();
		entities.add(entity);
		for(SBEntity subEntity : entity.getSBSubEntity()) {
			entities.addAll(extractAllEntities(subEntity));
		}
		return entities;
	}

	public static Set<SBEntity> extractAllEntities(Set<SBEntity> entities) {
		Set<SBEntity> entities2 = new HashSet<SBEntity>();
		for(SBEntity entity : entities) {
			entities2.addAll(extractAllEntities(entity));
		}
		return entities2;
	}
		
}
