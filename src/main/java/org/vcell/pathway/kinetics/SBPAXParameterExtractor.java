package org.vcell.pathway.kinetics;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sbpax.util.StringUtil;
import org.sbpax.util.sets.SetUtil;
import org.vcell.pathway.sbo.SBOList;
import org.vcell.pathway.sbo.SBOTerm;
import org.vcell.pathway.sbpax.SBMeasurable;

import cbit.vcell.model.Model;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.units.VCUnitDefinition;

public class SBPAXParameterExtractor {

	public static int nParameter = 0;
	
	public static Map<SBOTerm, ModelParameter> extractParameter(ReactionStep reaction, 
			SBMeasurable measurable) 
	throws PropertyVetoException {
		Map<SBOTerm, ModelParameter> sboToParameters = new HashMap<SBOTerm, ModelParameter>();
		Set<SBOTerm> sboTerms = SBPAXSBOExtractor.extractSBOTerms(measurable);
		String symbol = null;
		VCUnitDefinition targetUnit = null;
		Model model = reaction.getModel();
		ModelUnitSystem modelUnitSystem = model.getUnitSystem();
		Set<SBOTerm> termsWithUnituM = SetUtil.newSet(SBOList.sbo0000027MichaelisConstant, 
				SBOList.sbo0000322MichaelisConstantForSubstrate);
		for(SBOTerm sboTerm : sboTerms) {
			if(termsWithUnituM.contains(sboTerm)) {
				targetUnit = modelUnitSystem.getVolumeConcentrationUnit(); 
				// targetUnit = VCUnitDefinition.UNIT_uM;
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
		VCUnitDefinition unit = UOMEUnitExtractor.extractVCUnitDefinition(measurable, modelUnitSystem);
		double conversionFactor = 1.0;
		if(targetUnit != null && unit != null && unit != modelUnitSystem.getInstance_TBD() && 
				!targetUnit.equals(unit)) {
//			if(unit.equals(VCUnitDefinition.UNIT_M) && targetUnit.equals(VCUnitDefinition.UNIT_uM)) {
			if(unit.isCompatible(targetUnit)) {
				conversionFactor = unit.convertTo(conversionFactor, targetUnit);
				unit = targetUnit;
			}
		}
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
				model.new ModelParameter(parameterName, 
						new Expression(conversionFactor*number.doubleValue()), 
						Model.ROLE_UserDefined, unit);
			model.addModelParameter(parameter);
			for(SBOTerm sboTerm : sboTerms) {
				sboToParameters.put(sboTerm, parameter);
			}
		}			
		return sboToParameters;
	}
	
}
