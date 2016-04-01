/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;

import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.RbmUtils;

import cbit.vcell.client.desktop.biomodel.BioModelEditorRightSideTableModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelProcess;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.server.bionetgen.BNGExecutorService;

import com.ibm.icu.util.StringTokenizer;

/**
 * Insert the type's description here.
 * Creation date: (9/2/2003 3:26:19 PM)
 * @author: Jim Schaff
 */
public class ModelProcessEquation {
	public static final String REACTION_GOESTO = "->";
	
	private ModelProcess modelProcess = null;
	private Model model = null;
	private String equationString = null;
	private String equationleftHand = null;
	private String equationRightHand = null;
	private ExpressionBindingException expressionBindingException = null;
	
	public ModelProcessEquation(ModelProcess modelProcess, Model model) {
		super();
		this.modelProcess = modelProcess;
		this.model = model;
	}

/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 3:28:22 PM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return model.getNameScope();
}

/**
 * Insert the method's description here.
 * Creation date: (9/2/2003 5:39:29 PM)
 * @return java.lang.String
 */
public String toString() {
	if (modelProcess == null) {
		return BioModelEditorRightSideTableModel.ADD_NEW_HERE_REACTION_TEXT;
	} else {
		if (equationString == null) {
			computeEquationString();
		}
		return equationString;
	}
}

private void computeEquationString() {	
	if (modelProcess instanceof ReactionStep){
		ReactionStep reactionStep = (ReactionStep)modelProcess;
		ReactionParticipant[] reactantParticipants = reactionStep.getReactionParticipants();
		ArrayList<ReactionParticipant> reactantList = new ArrayList<ReactionParticipant>();
		ArrayList<ReactionParticipant> productList = new ArrayList<ReactionParticipant>();
	//	if (reactionStep instanceof SimpleReaction) {
			for (ReactionParticipant rp : reactantParticipants) {
				if (rp instanceof Reactant) {
					reactantList.add(rp);
				} else if (rp instanceof Product) {
					productList.add(rp);
				}
			}
	//	} else {
	//		Membrane membrane = (Membrane) ((FluxReaction)reactionStep).getStructure();
	//		StructureTopology structTopology = reactionStep.getModel().getStructureTopology();
	//		for (ReactionParticipant rp : reactantParticipants) {
	//			if (rp instanceof Flux) {
	//				Flux flux = (Flux)rp;
	//				Feature scf = (Feature) flux.getSpeciesContext().getStructure();
	//				if (structTopology.getInsideFeature(membrane) == scf) {
	//					productList.add(rp);
	//				} else {
	//					reactantList.add(rp);
	//				}
	//			}
	//		}
	//	}
		StringBuffer sb = new StringBuffer();
		for (ReactionParticipant r : reactantList) {
			if (sb.length() > 0) {
				sb.append(" + ");
			}
			int stoichiometry = r.getStoichiometry();
			sb.append((stoichiometry>1?stoichiometry:"") + r.getName());
		}
		equationleftHand = sb.toString();
		sb = new StringBuffer();
		for (ReactionParticipant p : productList) {
			if (sb.length() > 0) {
				sb.append(" + ");
			}
			int stoichiometry = p.getStoichiometry();
			sb.append((stoichiometry>1?stoichiometry:"") + p.getName());
		}
		equationRightHand = sb.toString();
		equationString = equationleftHand + " " + REACTION_GOESTO + " " + equationRightHand;
	}else if (modelProcess instanceof ReactionRule){
		ReactionRule reactionRuleEmbedded = (ReactionRule)modelProcess;
		equationString = RbmUtils.toBnglStringShort(reactionRuleEmbedded, CompartmentMode.hide);
	}
}

public final AutoCompleteSymbolFilter getAutoCompleteSymbolFilter() {
	if(modelProcess instanceof ReactionStep) {
		ReactionStep reactionStep = (ReactionStep)modelProcess;
		return reactionStep == null ? null : reactionStep.getAutoCompleteSymbolFilter();
	} else {
		return null;
	}
	
}
public final ExpressionBindingException getExpressionBindingException() {
	return expressionBindingException;
}

public static ReactionParticipant[] parseReaction(ReactionStep reactionStep, Model model, String equationString) throws ExpressionException, PropertyVetoException {
	int gotoIndex = equationString.indexOf(REACTION_GOESTO);
	if (gotoIndex < 1 && equationString.length() == 0) {
		throw new ExpressionException("Syntax error! " + REACTION_GOESTO + " not found. (e.g. a+b->c)");
	}
	if (reactionStep == null) {
		return null;
	}
	String leftHand = equationString.substring(0, gotoIndex);
	String rightHand = equationString.substring(gotoIndex + REACTION_GOESTO.length());
	StringTokenizer st = new StringTokenizer(leftHand, "+");
	ArrayList<ReactionParticipant> rplist = new ArrayList<ReactionParticipant>();
	HashMap<String, SpeciesContext> speciesContextMap = new HashMap<String, SpeciesContext>();
	Structure rxnStructure = reactionStep.getStructure();
	while (st.hasMoreElements()) {
		String nextToken = st.nextToken().trim();
		if (nextToken.length() == 0) {
			continue;
		}
		int stoichiIndex = 0;
		while (true) {
			if (Character.isDigit(nextToken.charAt(stoichiIndex))) {
				stoichiIndex ++;
			} else {
				break;
			}
		}
		int stoichi = 1;
		String tmp = nextToken.substring(0, stoichiIndex);
		if (tmp.length() > 0) {
			stoichi = Integer.parseInt(tmp);
		}
		String var = nextToken.substring(stoichiIndex).trim();
		SpeciesContext sc = model.getSpeciesContext(var);
		if (sc == null) {
			sc = speciesContextMap.get(var);
			if (sc == null) {
				Species species = model.getSpecies(var);
				if (species == null) {
					species = new Species(var, null);
				}
				sc = new SpeciesContext(species, rxnStructure);
				sc.setName(var);
				speciesContextMap.put(var, sc);
			}
		}
//		if (reactionStep instanceof SimpleReaction) {
			rplist.add(new Reactant(null,(SimpleReaction) reactionStep, sc, stoichi));
//		} else if (reactionStep instanceof FluxReaction) {
//			rplist.add(new Flux(null, (FluxReaction) reactionStep, sc));
//		}
	}
	st = new StringTokenizer(rightHand, "+");
	while (st.hasMoreElements()) {
		String nextToken = st.nextToken().trim();
		if (nextToken.length() == 0) {
			continue;
		}
		int stoichiIndex = 0;
		while (true) {
			if (Character.isDigit(nextToken.charAt(stoichiIndex))) {
				stoichiIndex ++;
			} else {
				break;
			}
		}
		int stoichi = 1;
		String tmp = nextToken.substring(0, stoichiIndex);
		if (tmp.length() > 0) {
			stoichi = Integer.parseInt(tmp);
		}
		String var = nextToken.substring(stoichiIndex);
		SpeciesContext sc = model.getSpeciesContext(var);
		if (sc == null) {
			sc = speciesContextMap.get(var);
			if (sc == null) {
				Species species = model.getSpecies(var);
				if (species == null) {
					species = new Species(var, null);
				}
				sc = new SpeciesContext(species, rxnStructure);
				sc.setName(var);
				speciesContextMap.put(var, sc);
			}
		}
//		if (reactionStep instanceof SimpleReaction) {
			rplist.add(new Product(null,(SimpleReaction) reactionStep, sc, stoichi));
//		} else if (reactionStep instanceof FluxReaction) {
//			rplist.add(new Flux(null, (FluxReaction) reactionStep, sc));
//		}
	}
	return rplist.toArray(new ReactionParticipant[0]);
}
}
