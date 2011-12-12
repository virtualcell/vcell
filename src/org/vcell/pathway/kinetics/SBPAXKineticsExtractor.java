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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sbpax.util.StringUtil;
import org.sbpax.util.sets.SetUtil;
import org.vcell.pathway.Xref;
import org.vcell.pathway.sbo.SBOList;
import org.vcell.pathway.sbo.SBOTerm;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBVocabulary;
import org.vcell.pathway.sbpax.UnitOfMeasurement;

import cbit.vcell.model.HMM_IRRKinetics;
import cbit.vcell.model.HMM_REVKinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;

public class SBPAXKineticsExtractor {
	
	public static void printSBEntity(SBEntity entity) {
		printSBEntity(entity, "");
	}
		
	public static void printSBEntity(SBEntity entity, String indent) {
		System.out.println(indent + entity.getClass().getSimpleName());
		for(SBVocabulary sbTerm : entity.getSBTerm()) {
			for(String term : sbTerm.getTerm()) {
				System.out.println(indent + "  " + term);
			}
			for(Xref xref : sbTerm.getxRef()) {
				System.out.println(indent + "  " + xref.getDb() + " " + xref.getId());
			}
		}	
		if(entity instanceof SBMeasurable) {
			SBMeasurable measurable = (SBMeasurable) entity;
			for(Double number : measurable.getNumber()) {
				System.out.println(indent + "  " + number);
			}
			for(UnitOfMeasurement unit : measurable.getUnit()) {
				System.out.println(indent + "  " + unit.getID());				
			}
		}
		for(SBEntity subEntity : entity.getSBSubEntity()) {
			printSBEntity(subEntity, indent + "    ");
		}
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

	public static int nParameter = 0;
	
	public static Map<SBOTerm, ModelParameter> extractParameter(ReactionStep reaction, SBMeasurable measurable) 
	throws PropertyVetoException {
		Map<SBOTerm, ModelParameter> sboToParameters = new HashMap<SBOTerm, ModelParameter>();
		Set<SBOTerm> sboTerms = SBPAXSBOExtractor.extractSBOTerms(measurable);
		String symbol = null;
		VCUnitDefinition targetUnit = null;
		Set<SBOTerm> termsWithUnituM = SetUtil.newSet(SBOList.sbo0000027, SBOList.sbo0000322);
		for(SBOTerm sboTerm : sboTerms) {
			if(termsWithUnituM.contains(sboTerm)) {
				targetUnit = VCUnitDefinition.UNIT_uM;
			}
			if(StringUtil.notEmpty(sboTerm.getSymbol())) {
				symbol = sboTerm.getSymbol();
			}
			for(int i = 0; i < symbol.length(); ++i) {
				char charAt = symbol.charAt(i);
				if(!Character.isJavaIdentifierPart(charAt)) {
					symbol = symbol.replace(charAt, '_');
				}
			}
		}
		VCUnitDefinition unit = UOMEUnitExtractor.extractVCUnitDefinition(measurable);
		double conversionFactor = 1.0;
		if(targetUnit != null && unit != null && unit != VCUnitDefinition.UNIT_TBD && !targetUnit.equals(unit)) {
			if(unit.equals(VCUnitDefinition.UNIT_M) && targetUnit.equals(VCUnitDefinition.UNIT_uM)) {
				conversionFactor = 1e6;
				unit = VCUnitDefinition.UNIT_uM;
			}
		}
		Model model = reaction.getModel();
		ArrayList<Double> numbers = measurable.getNumber();
		if(StringUtil.isEmpty(symbol)) {
			symbol = "p" + (++nParameter);
		}
		for(Double number : numbers) {
			String parameterName = symbol + "_" + reaction.getName();
			if(model.getModelParameter(parameterName) != null) {
				int count = 0;
				while(model.getModelParameter(parameterName + "_"+ count) != null) {
					++count;
				}
				parameterName = parameterName + "_" + count;
			}
			ModelParameter parameter = 
				model.new ModelParameter(parameterName, new Expression(conversionFactor*number.doubleValue()), 
						Model.ROLE_UserDefined, unit);
			model.addModelParameter(parameter);
			for(SBOTerm sboTerm : sboTerms) {
				sboToParameters.put(sboTerm, parameter);
			}
		}			
		return sboToParameters;
	}
	
	public static ModelParameter getParameter(Map<SBOTerm, ModelParameter> sboToParameter, SBOTerm ... sboTerms) {
		for(SBOTerm sboTerm : sboTerms) {
			ModelParameter parameter = sboToParameter.get(sboTerm);
			if(parameter != null) {
				return parameter;
			}
		}
		return null;
	}
	
	public static void extractKinetics(ReactionStep reaction, Set<SBEntity> entities) {
		for(SBEntity entity : entities) {
			printSBEntity(entity);
		}
		Set<SBEntity> allEntities = extractAllEntities(entities);
		Map<SBOTerm, ModelParameter> sboToModelParameter = new HashMap<SBOTerm, ModelParameter>();
		for(SBEntity entity : allEntities) {
			if(entity instanceof SBMeasurable) {				
				try {
					SBMeasurable sbMeasurable = (SBMeasurable) entity;
					sboToModelParameter.putAll(extractParameter(reaction, sbMeasurable));
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		ModelParameter kMichaelis = getParameter(sboToModelParameter, SBOList.sbo0000027);
		ModelParameter kMichaelisFwd = getParameter(sboToModelParameter, SBOList.sbo0000322);
		ModelParameter vMax = getParameter(sboToModelParameter, SBOList.sbo0000186);
		ModelParameter vMaxf = getParameter(sboToModelParameter, SBOList.sbo0000324);
		if(kMichaelisFwd != null || vMaxf != null) {
			try {
				HMM_REVKinetics kinetics = new HMM_REVKinetics(reaction);
				if(kMichaelisFwd != null) {
					try { 
						KineticsParameter kmParameter = kinetics.getKmFwdParameter();
						kmParameter.setExpression(new Expression(kMichaelisFwd.getName())); 
						kmParameter.setUnitDefinition(kMichaelisFwd.getUnitDefinition());
					} 
					catch (PropertyVetoException e) { e.printStackTrace(); }					
				}
				if(vMaxf != null) {
					try { 
						KineticsParameter vmaxParameter = kinetics.getVmaxFwdParameter();
						vmaxParameter.setExpression(new Expression(vMaxf.getName())); 
						vmaxParameter.setUnitDefinition(vMaxf.getUnitDefinition());
					} 
					catch (PropertyVetoException e) { e.printStackTrace(); }					
				}
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
		} else if(kMichaelis != null || vMax != null) {
			try {
				HMM_IRRKinetics kinetics = new HMM_IRRKinetics(reaction);
				if(kMichaelis != null) {
					try { 
						KineticsParameter kmParameter = kinetics.getKmParameter();
						kmParameter.setExpression(new Expression(kMichaelis.getName())); 
						kmParameter.setUnitDefinition(kMichaelis.getUnitDefinition());
					} 
					catch (PropertyVetoException e) { e.printStackTrace(); }					
				}
				if(vMax != null) {
					try { 
						KineticsParameter vmaxParameter = kinetics.getVmaxParameter();
						vmaxParameter.setExpression(new Expression(vMax.getName())); 
						vmaxParameter.setUnitDefinition(vMax.getUnitDefinition());
					} 
					catch (PropertyVetoException e) { e.printStackTrace(); }					
				}
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
		}
	}
	
}
