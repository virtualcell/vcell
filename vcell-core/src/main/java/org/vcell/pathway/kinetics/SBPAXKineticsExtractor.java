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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.vcell.pathway.BioPAXUtil.Process;
import org.vcell.pathway.Control;
import org.vcell.pathway.sbo.SBOListEx;
import org.vcell.pathway.sbo.SBOParam;
import org.vcell.pathway.sbo.SBOTerm;
import org.vcell.pathway.sbo.SBOUtil;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBVocabulary;

import cbit.vcell.model.Catalyst;
import cbit.vcell.model.HMM_IRRKinetics;
import cbit.vcell.model.HMM_REVKinetics;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;

public class SBPAXKineticsExtractor {
	
public enum MappedKinetics {
	SBO_0000012,		// mass action rate law
	SBO_0000069,		// mass action rate law for zeroth order reversible reactions
	SBO_0000078,		// mass action rate law for first order reversible reactions
	SBO_0000043,		// mass action rate law for zeroth order irreversible reactions
	SBO_0000044,		// mass action rate law for first order irreversible reactions
	SBO_0000028,		// enzymatic rate law for irreversible non-modulated non-interacting unireactant enzymes
	SBO_0000432,		// irreversible Michaelis Menten rate law for two substrates
	SBO_0000029,		// Henri-Michaelis-Menten rate law (this should be treated as irreversible)
	SBO_0000438,		// Reversible Michaelis Menten
	}
	
	public static boolean extractKineticsExactMatch(ReactionStep reaction, Process process) 
		throws ExpressionException, PropertyVetoException {
//		Set<SBEntity> entities = Collections.<SBEntity>unmodifiableSet(process.getInteractions());
//		KineticContext context = new KineticContext(reaction, entities);
		
		// we try a perfect match first based on the existence of a SBOTerm in the kinetic law
		if(process.getControl() == null) {
			return true;		// no control means no kinetic law - nothing more to do
		}
		Control control = process.getControl();
		ArrayList<SBEntity> sbEntities = control.getSBSubEntity();
		for(SBEntity sbE : sbEntities) {
			// the following if clause may not be needed, we KNOW that 
			// the only SBSubEntities allowed in a control are kinetic laws
			if(sbE.getID().contains("kineticLaw")) {
				// build list of the parameters of this kinetic law in sboParams
				ArrayList<SBOParam> sboParams = new ArrayList<SBOParam>();	// params of this kinetic law
				ArrayList<SBEntity> subEntities = sbE.getSBSubEntity();
				for(SBEntity subEntity : subEntities) {
					if(subEntity instanceof SBMeasurable) {
						SBMeasurable m  = (SBMeasurable)subEntity;
						if(!m.hasTerm()) {
							break;	// we don't know what to do with a measurable with no SBTerm
						}
						String termName = m.extractSBOTermAsString();
						SBOTerm sboT = SBOListEx.sboMap.get(termName);
						System.out.println("    " + sboT.getIndex() + "  " + sboT.getName());
						SBOParam sboParam = matchSBOParam(sboT);
						
						if(m.hasNumber()) {
							double number = m.getNumber().get(0);
							sboParam.setNumber(number);
						}
						if(m.hasUnit()) {
							String unit = m.extractSBOUnitAsString();
							sboParam.setUnit(unit);
						}
						sboParams.add(sboParam);
					}
				}
				// find if a kinetic law type exists and if not guesstimate one based on parameters
				// simple rule: if we have a Km param it's MM, otherwise it's mass action
				ArrayList<SBVocabulary> sbTerms = sbE.getSBTerm();
				if(sbTerms.isEmpty()) {
//					return false;
					SBVocabulary sbTerm = new SBVocabulary();
					ArrayList<String> termNames = new ArrayList<String>();
					String id;
					SBOParam kMichaelis = extractMichaelisForwardParam(sboParams);
					if(kMichaelis == null) {
						id = new String("SBO:0000012");	// mass action rate law
					} else {
						id = new String("SBO:0000029");	// irreversible Henri-Michaelis-Menten rate law
					}
//					termNames.add(id);
//					sbTerm.setTerm(termNames);
					sbTerm.setID(id);
					sbTerms.add(sbTerm);
				}
				System.out.println(" kinetic law ID: " + sbTerms.get(0).getID());

				// identify the kinetic law type (mass action, michaelis menten, etc) and bring it in vCell
				for(SBVocabulary sbv : sbTerms) {		// use for loop even though we only expect 1 SBTerm
					String vocabularyID = sbv.getID();	// SBVocabulary id, used to retrieve the kinetic law type
					SBOTerm sboT = SBOUtil.getSBOTermFromVocabularyId(vocabularyID);
					System.out.println(vocabularyID + "   " + sboT.getName());
					SBOParam kForward;
					SBOParam kCat;
					SBOParam vM;
					SBOParam kReverse;
					SBOParam kMichaelis;
					Kinetics kinetics;
					MappedKinetics current = matchSBOKineticLaw(sboT);
					switch (current) {
					case SBO_0000069:
					case SBO_0000432:
						// some kinetic laws unknown to vCell will fall through to this category
						// honestly i don't know what to do with them
						System.out.println("GeneralKinetics");
						// TODO: what to do here?
						return true; 
					case SBO_0000012:
					case SBO_0000078:
						System.out.println("MassActionKinetics - reversible"); 
						kForward = extractKForwardParam(sboParams);
						kReverse = extractKReverseParam(sboParams);
						kinetics = new MassActionKinetics(reaction);
						reaction.setKinetics(kinetics);
						setKForwardParam(reaction, kForward, kinetics);
						setKReverseParam(reaction, kReverse, kinetics);
						return true; 
					case SBO_0000043:
						System.out.println("MassActionKinetics - zeroth order irreversible, Kr <- 0  ");
						kForward = extractKForwardParam(sboParams);
						kinetics = new MassActionKinetics(reaction);
						// TODO: what to do here?
						return true; 
					case SBO_0000044: 
						System.out.println("MassActionKinetics - first order irreversible, Kr <- 0  ");
						kForward = extractKForwardParam(sboParams);
						kinetics = new MassActionKinetics(reaction);
						reaction.setKinetics(kinetics);
						setKForwardParam(reaction, kForward, kinetics);
						return true; 
					case SBO_0000028:
					case SBO_0000029:
						System.out.println("HMM_IRRKinetics");
						// TODO: make kCat global variable, set its number and unit in annotation
						// TODO: make vM global variable, set its number and unit in annotation
						kMichaelis = extractMichaelisForwardParam(sboParams);	// get the numbers, if present (may be null)
						vM = extractVMForwardParam(sboParams, process);
						kCat = extractKCatForwardParam(sboParams);
						kinetics = new HMM_IRRKinetics((SimpleReaction)reaction);
						try {
							// TODO: create expression only if kCat != null, otherwise use vM directly (if != null) otherwise ???
							kinetics.reading(true);
							setVMForwardParamAsExpression(reaction, kCat, kinetics);
						} finally {
							kinetics.reading(false);
						}
						reaction.setKinetics(kinetics);
						setMichaelisForwardParam(reaction, kMichaelis, kinetics);
						return true; 
					case SBO_0000438:
						System.out.println("HMMREVKinetics"); 
						kinetics = new HMM_REVKinetics((SimpleReaction)reaction);
						
						return true; 
					default:
						// TODO: guessing happens above - if we have nothing by now we need to raise runtime exception
						// change the code below !!!
						System.out.println("Unable to match the SBOTerm with any compatible kinetic law."); 
						return false; 	// found unmapped kinetic law, we'll try to guess a match
					}
				}
			}
		}
		return false;	// no SBTerm found so we cannot know for sure the kinetic law, we'll have to guess it
	}

	private static void setVMForwardParamAsExpression(ReactionStep reaction, SBOParam kCat, Kinetics kinetics) 
	throws ExpressionException, PropertyVetoException {
		if(kCat != null) {
			KineticsParameter Vmax = ((HMM_IRRKinetics) kinetics).getVmaxParameter();
			String unitF = SBOParam.formatUnit(kCat.getUnit());
			VCUnitDefinition kCatUnit = reaction.getModel().getUnitSystem().getInstance(unitF);
			Double numberKCat = 0.0;
			if(kCat.hasNumber()) {
				numberKCat = kCat.getNumber();
			}
			Expression KCatExpression = new Expression(numberKCat);
			KineticsParameter Kcat = kinetics.addUserDefinedKineticsParameter("Kcat", KCatExpression, kCatUnit);
			
			String enzymeName = "";
			ReactionParticipant[] participants = reaction.getReactionParticipants();
			for(int i = 0;i< participants.length; i++){
				ReactionParticipant rp = participants[i];
				if(rp instanceof Catalyst) {
					enzymeName = rp.getName();
				}
			}
			SpeciesContext enzyme = reaction.getModel().getSpeciesContext(enzymeName);
			Vmax.setExpression(Expression.mult(new Expression(Kcat,reaction.getNameScope()),new Expression(enzyme,reaction.getNameScope())));
		}
	}

	private static void setVMForwardParam(ReactionStep reaction, SBOParam kVMForward, Kinetics kinetics) 
	throws ExpressionException, PropertyVetoException {
		if(kVMForward != null) {
			KineticsParameter fwdParam = ((HMM_IRRKinetics) kinetics).getVmaxParameter();
			kinetics.setParameterValue(fwdParam, new Expression(kVMForward.getNumber()));
			String unitF = SBOParam.formatUnit(kVMForward.getUnit());
			VCUnitDefinition fwdUnit = reaction.getModel().getUnitSystem().getInstance(unitF); 
			fwdParam.setUnitDefinition(fwdUnit);
		}
	}

	private static void setKReverseParam(ReactionStep reaction, SBOParam kReverse, Kinetics kinetics) 
				throws ExpressionException, PropertyVetoException {
		if(kReverse != null) {
			KineticsParameter revParam = ((MassActionKinetics) kinetics).getReverseRateParameter();
			kinetics.setParameterValue(revParam, new Expression(kReverse.getNumber()));
			String unitR = SBOParam.formatUnit(kReverse.getUnit());
			VCUnitDefinition revUnit = reaction.getModel().getUnitSystem().getInstance(unitR); 
			revParam.setUnitDefinition(revUnit);
		}
	}

	private static void setKForwardParam(ReactionStep reaction, SBOParam kForward, Kinetics kinetics) 
				throws ExpressionException, PropertyVetoException {
		if(kForward != null) {
			KineticsParameter fwdParam = ((MassActionKinetics) kinetics).getForwardRateParameter();
			kinetics.setParameterValue(fwdParam, new Expression(kForward.getNumber()));
			String unitF = SBOParam.formatUnit(kForward.getUnit());
			VCUnitDefinition fwdUnit = reaction.getModel().getUnitSystem().getInstance(unitF); 
			fwdParam.setUnitDefinition(fwdUnit);
		}
	}
	private static void setMichaelisForwardParam(ReactionStep reaction, SBOParam kMichaelis, Kinetics kinetics) 
	throws ExpressionException, PropertyVetoException {
		if(kMichaelis != null) {
			KineticsParameter fwdParam = ((HMM_IRRKinetics) kinetics).getKmParameter();
			kinetics.setParameterValue(fwdParam, new Expression(kMichaelis.getNumber()));
			String unitF = SBOParam.formatUnit(kMichaelis.getUnit());
			VCUnitDefinition fwdUnit = reaction.getModel().getUnitSystem().getInstance(unitF); 
			fwdParam.setUnitDefinition(fwdUnit);
		}
	}
	
	private static SBOParam extractKForwardParam(ArrayList<SBOParam> sboParams) {
		for(SBOParam p : sboParams) {
			if(p.getRole() == Kinetics.ROLE_KForward) {
				return p;
			}
		}
		// if we can't find a KForward we try to get a "degraded" role which still makes sense
		for(SBOParam p : sboParams) {
			if(p.getRole() == Kinetics.ROLE_ReactionRate) {
				return p;
			}
		}
		return null;
	}
	private static SBOParam extractKReverseParam(ArrayList<SBOParam> sboParams) {
		for(SBOParam p : sboParams) {
			if(p.getRole() == Kinetics.ROLE_KReverse) {
				return p;
			}
		}
		return null;
	}
	public static SBOParam extractMichaelisForwardParam(ArrayList<SBOParam> sboParams) {
		for(SBOParam p : sboParams) {
			if(p.getRole() == Kinetics.ROLE_KmFwd) {
				return p;
			}
		}
		// if we can't find a ROLE_KmFwd we try to get a "degraded" role ROLE_Km which still makes sense
		for(SBOParam p : sboParams) {
			if(p.getRole() == Kinetics.ROLE_Km) {
				return p;
			}
		}
		return null;
	}
	
	private static SBOParam extractVMForwardParam(ArrayList<SBOParam> sboParams, Process process) {
		SBOParam kcat = null;
		// 1st try: find ROLE_VmaxFwd
		for(SBOParam p : sboParams) {
			if(p.getRole() == Kinetics.ROLE_VmaxFwd) {
				return p;
			}
		}
		// 2nd try: find a degraded param that would still make sense ROLE_Vmax
		for(SBOParam p : sboParams) {
			if(p.getRole() == Kinetics.ROLE_Vmax) {
				return p;
			}
		}
//		String controller = process.getControl().getPhysicalControllers().get(0).getID();
//		for(SBOParam p : sboParams) {	// we need to find the concentration of the catalyst (enzyme)
//			if(p.getRole() == Kinetics.ROLE_Concentration_Participant) {	// ROLE_Concentration_Catalyst
//				String candidate = p.getIndex();		// this participant may be the catalyst
//				if(controller.equals(candidate)) {
//					enzyme = p;
//					break;
//				}
//			}
//		}
//		if(kcat == null || enzyme == null) {
//			return null;	// all 3 attempts failed, really nothing else to try
//		}
		return null;		
	}
	private static SBOParam extractKCatForwardParam(ArrayList<SBOParam> sboParams) {
		for(SBOParam p : sboParams) {
			if(p.getRole() == Kinetics.ROLE_Kcat) {
				return p;
			}
		}
		return null;
	}


	public static void extractKineticsInferredMatch(ReactionStep reaction, Set<SBEntity> entities) {
		KineticContext context = new KineticContext(reaction, entities);
		// if the SBOTerm is missing we'll try to guess based on the parameters... 
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
	
	private static SBOParam createEntry(String index, String name, int role)
	{
		SBOParam element = new SBOParam(index, name, role);
		SBPAXKineticsExtractor.sboParamsEquivalenceMap.put(index, element);
		return element;
	}
	
	public static Map<String, SBOParam> sboParamsEquivalenceMap = new HashMap<String, SBOParam>();

	// recursively navigate the hierarchy of SBOTerms until we find a match with a known kinetic law
	// the root element should be SBO:0000006 - kinetic constant
	public static MappedKinetics matchSBOKineticLaw(SBOTerm sboT) {
		MappedKinetics current = MappedKinetics.valueOf(sboT.getIndex().replace(':', '_'));
		switch (current) {
		case SBO_0000069:
		case SBO_0000432:
		case SBO_0000012:
		case SBO_0000078:
		case SBO_0000043: 
		case SBO_0000044: 
		case SBO_0000028:
		case SBO_0000029:
		case SBO_0000438:
			return current;
		default:
			break;
		}
		Set<SBOTerm> subC = SBOUtil.getSuperClasses(sboT);
		if(subC.isEmpty()) {
			return null;
		}
		for(SBOTerm sbot : subC) {		// due to multiple inheritance we may have 2 ancestors
			MappedKinetics ancestor = matchSBOKineticLaw(sbot);
			if(ancestor != null) {
				// for simplicity we return as soon as we find a match, for now we don't care about depth
				return ancestor;
			}
		}
		return null;	// no match found, perhaps this SBOTerm is not a kinetic law
	}

	
	// recursively navigate the hierarchy of SBOTerms until we find a match in the params equivalence map
	// the root element should be SBO:0000006 - kinetic constant
	public static SBOParam matchSBOParam(SBOTerm sboT) {
		if(sboParamsEquivalenceMap.containsKey(sboT.getIndex())) {
			System.out.println("    matched to " + sboT.getIndex() + "  " + sboT.getName());
			return sboParamsEquivalenceMap.get(sboT.getIndex());
		}
		Set<SBOTerm> subC = SBOUtil.getSuperClasses(sboT);
		if(subC.isEmpty()) {
			return null;
		}
		for(SBOTerm sbot : subC) {		// due to multiple inheritance we may have 2 ancestors
			SBOParam sboParam = matchSBOParam(sbot);
			if(sboParam != null) {
				// for simplicity we return as soon as we find a match, for now we don't care about depth
				return sboParam;
			}
		}
		return null;	// no match found, perhaps this SBOTerm is not a parameter
	}

	public static final SBOParam x_0000000 = 
		createEntry("SBO:0000000", "systems biology representation", Kinetics.ROLE_UserDefined);
	public static final SBOParam x_0000002 = 
		createEntry("SBO:0000002", "quantitative systems description parameter", Kinetics.ROLE_NotARole);
	public static final SBOParam x_0000006 = 
		createEntry("SBO:0000006", "obsolete parameter", Kinetics.ROLE_NotARole);
	public static final SBOParam x_0000009 = 
		createEntry("SBO:0000009", "kinetic constant", Kinetics.ROLE_ReactionRate);		// the velocity of a chemical reaction 
	public static final SBOParam x_0000016 = 
		createEntry("SBO:0000016", "unimolecular rate constant", Kinetics.ROLE_KForward);			// velocity of a chemical reaction involving only one reactant 
	public static final SBOParam x_0000017 = 
		createEntry("SBO:0000017", "bimolecular rate constant", Kinetics.ROLE_KForward);			// the velocity of a chemical reaction involving two reactants
	public static final SBOParam x_0000018 = 
		createEntry("SBO:0000018", "trimolecular rate constant", Kinetics.ROLE_KForward);			// the velocity of a chemical reaction involving three reactants 
	public static final SBOParam x_0000022 = 
		createEntry("SBO:0000022", "forward unimolecular rate constant", Kinetics.ROLE_KForward);	// the forward velocity of a chemical reaction involving only one reactant
	public static final SBOParam x_0000023 = 
		createEntry("SBO:0000023", "forward bimolecular rate constant", Kinetics.ROLE_KForward);	// the forward velocity of a chemical reaction involving two reactants
	public static final SBOParam x_0000024 = 
		createEntry("SBO:0000024", "forward trimolecular rate constant", Kinetics.ROLE_KForward);	// the forward velocity of a chemical reaction involving three reactants 
	public static final SBOParam x_0000025 = 
		createEntry("SBO:0000025", "catalytic rate constant", Kinetics.ROLE_Kcat);			// the velocity of an enzymatic reaction
	public static final SBOParam x_0000026 = 
		createEntry("SBO:0000026", "new term name", Kinetics.ROLE_NotARole);
	public static final SBOParam x_0000027 = 
		createEntry("SBO:0000027", "Michaelis constant", Kinetics.ROLE_Km);				// substrate concentration at which the velocity of reaction is half its maximum. Michaelis constant is an experimental parameter
	public static final SBOParam x_0000032 = 
		createEntry("SBO:0000032", "reverse unimolecular rate constant", Kinetics.ROLE_KReverse);	// the reverse velocity of a chemical reaction involving only one product. This parameter encompasses all the contributions to the velocity except the quantity of the product 
	public static final SBOParam x_0000033 = 
		createEntry("SBO:0000033", "reverse bimolecular rate constant", Kinetics.ROLE_KReverse);
	public static final SBOParam x_0000034 = 
		createEntry("SBO:0000034", "reverse trimolecular rate constant", Kinetics.ROLE_KReverse);
	public static final SBOParam x_0000035 = 
		createEntry("SBO:0000035", "forward unimolecular rate constant, continuous case", Kinetics.ROLE_KForward);
	public static final SBOParam x_0000036 = 
		createEntry("SBO:0000036", "forward bimolecular rate constant, continuous case", Kinetics.ROLE_KForward);
	public static final SBOParam x_0000037 = 
		createEntry("SBO:0000037", "forward trimolecular rate constant, continuous case", Kinetics.ROLE_KForward);
	public static final SBOParam x_0000038 = 
		createEntry("SBO:0000038", "reverse unimolecular rate constant, continuous case", Kinetics.ROLE_KReverse);
	public static final SBOParam x_0000039 = 
		createEntry("SBO:0000039", "reverse bimolecular rate constant, continuous case", Kinetics.ROLE_KReverse);
	public static final SBOParam x_0000040 = 
		createEntry("SBO:0000040", "reverse trimolecular rate constant, continuous case", Kinetics.ROLE_KReverse);		// three products
	public static final SBOParam x_0000046 = 
		createEntry("SBO:0000046", "zeroth order rate constant", Kinetics.ROLE_KForward);	// the velocity of a chemical reaction independant of the reactant quantities 
	public static final SBOParam x_0000149 = 
		createEntry("SBO:0000149", "number of substrates", Kinetics.ROLE_NotARole);		// number of molecules which are acted upon by an enzyme 
	public static final SBOParam x_0000153 = 
		createEntry("SBO:0000153", "forward rate constant", Kinetics.ROLE_KForward);	// the forward velocity of a chemical reaction 
	public static final SBOParam x_0000156 = 
		createEntry("SBO:0000156", "reverse rate constant", Kinetics.ROLE_KReverse);	// the reverse velocity of a chemical reaction 
	public static final SBOParam x_0000160 = 
		createEntry("SBO:0000160", "forward non-integral order rate constant", Kinetics.ROLE_KForward);	// the forward velocity of a chemical reaction where reactants have non-integral orders 
	public static final SBOParam x_0000161 = 
		createEntry("SBO:0000161", "reverse non-integral order rate constant", Kinetics.ROLE_KReverse);
	public static final SBOParam x_0000162 = 
		createEntry("SBO:0000162", "forward zeroth order rate constant", Kinetics.ROLE_KForward);	// the forward velocity of a chemical reaction independant of the reactant quantities 
	public static final SBOParam x_0000186 = 
		createEntry("SBO:0000186", "maximal velocity", Kinetics.ROLE_Vmax);	// limiting maximal velocity of an enzymatic reaction, reached when the substrate is in large excess and all the enzyme is complexed 
	public static final SBOParam x_0000190 = 
		createEntry("SBO:0000190", "Hill coefficient", Kinetics.ROLE_UserDefined);	// empirical parameter created by Archibald Hill to describe the cooperative binding of oxygen on hemoglobine 
	public static final SBOParam x_0000191 = 
		createEntry("SBO:0000191", "Hill constant", Kinetics.ROLE_UserDefined);
	public static final SBOParam x_0000193 = 
		createEntry("SBO:0000193", "equilibrium or steady-state constant", Kinetics.ROLE_UserDefined);	// constant with the dimension of a powered concentration. It is determined at half-saturation, half-activity etc 
	public static final SBOParam x_0000196 = 
		createEntry("SBO:0000196", "concentration of an entity pool", Kinetics.ROLE_NotARole);		// kCat???
	public static final SBOParam x_0000226 = 
		createEntry("SBO:0000226", "density of an entity pool", Kinetics.ROLE_NotARole);
	public static final SBOParam x_0000257 = 
		createEntry("SBO:0000257", "conductance", Kinetics.ROLE_Conductivity);		// measure of how easily electricity flows along a certain path through an electrical element. The SI derived unit of conductance is the Siemens
	public static final SBOParam x_0000282 = 
		createEntry("SBO:0000282", "dissociation constant", Kinetics.ROLE_UserDefined);		// equilibrium constant that measures the propensity of a larger object to separate (dissociate) reversibly into smaller components, as when a complex falls apart into its component molecules, or when a salt splits up into its component ions
	public static final SBOParam x_0000308 = 
		createEntry("SBO:0000308", "equilibrium or steady-state characteristic", Kinetics.ROLE_UserDefined);
	public static final SBOParam x_0000322 = 
		createEntry("SBO:0000322", "Michaelis constant for substrate", Kinetics.ROLE_Km);	// substrate concentration at which the velocity of product production by the forward activity of a reversible enzyme is half its maximum 
	public static final SBOParam x_0000323 = 
		createEntry("SBO:0000323", "Michaelis constant for product", Kinetics.ROLE_Km);
	public static final SBOParam x_0000324 = 
		createEntry("SBO:0000324", "forward maximal velocity", Kinetics.ROLE_VmaxFwd);	// limiting maximal velocity of the forward reaction of a reversible enzyme, reached when the substrate is in large excess and all the enzyme is complexed 
	public static final SBOParam x_0000325 = 
		createEntry("SBO:0000325", "reverse maximal velocity", Kinetics.ROLE_VmaxRev);	// limiting maximal velocity of the reverse reaction of a reversible enzyme, reached when the product is in large excess and all the enzyme is complexed 
	public static final SBOParam x_0000331 = 
		createEntry("SBO:0000331", "half-life", Kinetics.ROLE_UserDefined);				// time interval over which a quantified entity is reduced to half its original value 
	public static final SBOParam x_0000337 = 
		createEntry("SBO:0000337", "association constant", Kinetics.ROLE_KForward);		// equilibrium constant that measures the propensity of two objects to assemble (associate) reversibly into a larger component 
	public static final SBOParam x_0000338 = 
		createEntry("SBO:0000338", "dissociation rate constant", Kinetics.ROLE_KReverse);		// rate with which a complex dissociates into its components 
	public static final SBOParam x_0000341 = 
		createEntry("SBO:0000341", "association rate constant", Kinetics.ROLE_KForward);		// rate with which components associate into a complex 
	public static final SBOParam x_0000345 = 
		createEntry("SBO:0000345", "time", Kinetics.ROLE_UserDefined);		// quantity of the measuring system used to sequence events, to compare the durations of events and the intervals between them, and to quantify the motions or the transformation of entities. The SI base unit for time is the SI second 
	public static final SBOParam x_0000350 = 
		createEntry("SBO:0000350", "forward reaction velocity", Kinetics.ROLE_KForward);		// the speed of an enzymatic reaction at a defined concentration of substrate(s) and enzyme 
	public static final SBOParam x_0000352 = 
		createEntry("SBO:0000352", "reverse zeroth order rate constant", Kinetics.ROLE_KReverse);	// the reverse velocity of a chemical reaction independant of the reactant quantities. This parameter encompasses all the contributions to the velocity. It is to be used in a reaction modelled using a continuous framework 
	public static final SBOParam x_0000356 = 
		createEntry("SBO:0000356", "decay constant", Kinetics.ROLE_KForward);					// kinetic constant characterizing a mono-exponential decay. It is the inverse of the mean lifetime of the continuant being decayed. Its unit is per time 
	public static final SBOParam x_0000481 = 
		createEntry("SBO:0000481", "stoichiometric coefficient", Kinetics.ROLE_UserDefined);	// the degree to which a chemical species participates in a reaction. It corresponds to the number of molecules of a reactant that are consumed or produced with each occurrence of a reaction event 
	public static final SBOParam x_0000482 = 
		createEntry("SBO:0000482", "geometric mean rate constant", Kinetics.ROLE_ReactionRate);	// the geometric mean turnover rate of an enzyme in either forward or backward direction for a reaction, measured per second.", 
	public static final SBOParam x_0000483 = 
		createEntry("SBO:0000483", "forward geometric mean rate constant", Kinetics.ROLE_KForward); 
	public static final SBOParam x_0000484 = 
		createEntry("SBO:0000484", "reverse geometric mean rate constant", Kinetics.ROLE_KReverse);
	public static final SBOParam x_0000491 = 
		createEntry("SBO:0000491", "diffusion coefficient", Kinetics.ROLE_Concentration_Reactant1);	// the amount of substance diffusing across a unit area through a unit concentration gradient in unit time. The higher the diffusion coefficient (of one substance with respect to another), the faster they diffuse into each other. This coefficient has an SI unit of m2/s (length2/time) 
	public static final SBOParam x_0000538 = 
		createEntry("SBO:0000538", "ionic permeability", Kinetics.ROLE_Permeability);			// the permeability of an ion channel with respect to a particular ion 

}
