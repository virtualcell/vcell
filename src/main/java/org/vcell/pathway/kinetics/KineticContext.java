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

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sbpax.schemas.util.SBPAX3Util;
import org.vcell.pathway.sbo.SBOSet;
import org.vcell.pathway.sbo.SBOTerm;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBMeasurable;

import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Model.ModelParameter;

public class KineticContext {
	
	protected final ReactionStep reaction;
	protected final Map<SBOTerm, ModelParameter> termToParam = new HashMap<SBOTerm, ModelParameter>();
	protected final Map<SBOSet, ModelParameter> sboSetToParam = new HashMap<SBOSet, ModelParameter>();
	protected Set<Reactant> reactants = new HashSet<Reactant>();
	protected Set<Catalyst> catalysts = new HashSet<Catalyst>();
	protected Set<Product> products = new HashSet<Product>();
	
	public KineticContext(ReactionStep reaction, Set<SBEntity> entities) { 
		this.reaction = reaction; 
		for(ReactionParticipant participant : reaction.getReactionParticipants()) {
			if(participant instanceof Reactant) { reactants.add((Reactant) participant); }
			else if(participant instanceof Catalyst) { catalysts.add((Catalyst) participant); }
			else if(participant instanceof Product) { products.add((Product) participant); }
		}
		Set<SBEntity> allEntities = SBPAX3Util.extractAllEntities(entities);
		for(SBEntity entity : allEntities) {
			if(entity instanceof SBMeasurable) {				
				try {
					SBMeasurable sbMeasurable = (SBMeasurable) entity;
					termToParam.putAll(SBPAXParameterExtractor.extractParameter(reaction, sbMeasurable));
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public ReactionStep getReaction() { return reaction; }
	public Set<Reactant> getReactants() { return reactants; }
	public Set<Catalyst> getCatalysts() { return catalysts; }
	public Set<Product> getProducts() { return products; }
	public Map<SBOTerm, ModelParameter> getSBOToParamMap() { return termToParam; }
	
	public ModelParameter getParameter(SBOSet sboSet) {
		ModelParameter param = sboSetToParam.get(sboSet);
		if(param == null) {
			for(Map.Entry<SBOTerm, ModelParameter> entry : termToParam.entrySet()) {
				SBOTerm sboTerm = entry.getKey();
				if(sboSet.includes(sboTerm)) {
					param = entry.getValue();
					sboSetToParam.put(sboSet, param);
					break;
				}
			}
		}
		return param;
	}
	
	public boolean hasParamForSBO(SBOSet sboSet) {
		return getParameter(sboSet) != null;
	}
}