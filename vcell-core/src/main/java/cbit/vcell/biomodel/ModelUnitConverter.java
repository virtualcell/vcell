package cbit.vcell.biomodel;

import cbit.vcell.mapping.*;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.math.MathDescription;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.model.*;
import cbit.vcell.parser.*;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModelUnitConverter {

	private final static Logger logger = LogManager.getLogger(ModelUnitConverter.class);

	public static ModelUnitSystem createSbmlModelUnitSystem() {
		final String substanceUnit = "uM.um3";
		String volumeSubstanceSymbol = substanceUnit;
		String membraneSubstanceSymbol = substanceUnit;
		String lumpedReactionSubstanceSymbol = substanceUnit;
		String volumeSymbol = "um3";
		String areaSymbol = "um2";
		String lengthSymbol = "um";
		String timeSymbol = "s";
		ModelUnitSystem mus = ModelUnitSystem.createVCModelUnitSystem(volumeSubstanceSymbol, membraneSubstanceSymbol, lumpedReactionSubstanceSymbol, volumeSymbol, areaSymbol, lengthSymbol, timeSymbol);
		return mus;
	}
	public static BioModel createBioModelWithSBMLUnitSystem(BioModel oldBioModel) throws ExpressionException, XmlParseException {
		ModelUnitSystem mus = createSbmlModelUnitSystem();
		BioModel newBioModel = createBioModelWithNewUnitSystem(oldBioModel, mus);
		return newBioModel;
	}
	
	
	public static BioModel createBioModelWithNewUnitSystem(BioModel oldBioModel, ModelUnitSystem newUnitSystem)
			throws ExpressionException, XmlParseException {

		oldBioModel.refreshDependencies();
		Map<String, MathDescription> previousMathDescriptionMap = new LinkedHashMap<>();
		for (SimulationContext simContext : oldBioModel.getSimulationContexts()){
			//
			// force new math generation
			//
			MathMapping mathMapping = simContext.createNewMathMapping();
			try {
				MathDescription mathDesc = mathMapping.getMathDescription();
				previousMathDescriptionMap.put(simContext.getName(), mathDesc);
			}catch (Exception e){
				throw new RuntimeException("failed to generated math for application "+simContext.getName()+": "+e.getMessage(), e);
			}
		}

		// new BioModel has new unit system applied to all built-in units ... but expressions still need to be corrected (see below).
		String biomodelXMLString = XmlHelper.bioModelToXML(oldBioModel);
		XMLSource newXMLSource = new XMLSource(biomodelXMLString);
		BioModel newBioModel = XmlHelper.XMLToBioModel(newXMLSource, true, newUnitSystem);
		Model newModel = newBioModel.getModel();
		Model oldModel = oldBioModel.getModel();

		VCUnitDefinition dimensionless = newModel.getUnitSystem().getInstance_DIMENSIONLESS();
		Model.ReservedSymbol KMOLE = newModel.getReservedSymbolByRole(Model.ReservedSymbolRole.KMOLE);

		for (Parameter p : newBioModel.getModel().getModelParameters()){
			convertVarsWithUnitFactors(oldBioModel.getModel(), newBioModel.getModel(), p, dimensionless, KMOLE);
		}
		
		for (ReactionStep reactionStep : newBioModel.getModel().getReactionSteps()) {
			SymbolTable oldSymbolTable = oldBioModel.getModel().getReactionStep(reactionStep.getName());
			SymbolTable newSymbolTable = reactionStep;
			for (Parameter p : reactionStep.getKinetics().getUnresolvedParameters()){
				convertVarsWithUnitFactors(oldSymbolTable, newSymbolTable, p, dimensionless, KMOLE);
			}
			for (Parameter p : reactionStep.getKinetics().getKineticsParameters()){
				convertVarsWithUnitFactors(oldSymbolTable, newSymbolTable, p, dimensionless, KMOLE);
			}
			
//			We no longer have to deal with conversion factor expressions and try to be smart and simplify/flatten and rebind
//			This was actually imperfect and introducing unexpected buggy behavior
			
//			Kinetics kinetics = reactionStep.getKinetics();
//			KineticsParameter kineticsParameter = null;
//			if(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate) != null) {
//				kineticsParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate);
//			} else if(kinetics.getKineticsParameterFromRole(Kinetics.ROLE_LumpedReactionRate) != null) {
//				kineticsParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_LumpedReactionRate);
//			} else {
//				throw new RuntimeException("Role 'reaction rate' or role 'lumped reaction rate' expected");
//			}
//			
//			Expression rateExpression = kineticsParameter.getExpression();
//			jscl.math.Expression jsclExpression = null;
//			String jsclExpressionString = rateExpression.infix_JSCL();
//			try {
//				jsclExpression = jscl.math.Expression.valueOf(jsclExpressionString);
//			}catch (jscl.text.ParseException e){
//				e.printStackTrace(System.out);
//				System.out.println("JSCL couldn't parse \""+jsclExpressionString+"\"");
//				return null;
//			}
//			jscl.math.Generic g1=jsclExpression.expand().simplify();
//			Expression newRate=new Expression(SymbolUtils.getRestoredStringJSCL(g1.toString()));
//			newRate.bindExpression(reactionStep);
//		//	reactionStep.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).setExpression(newRate.flatten());
//			if (reactionStep.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate) != null) {
//				reactionStep.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).setExpression(newRate.flatten());
//			}
		}
		for (ReactionRule reactionRule : newBioModel.getModel().getRbmModelContainer().getReactionRuleList()) {
			SymbolTable oldSymbolTable = oldBioModel.getModel().getRbmModelContainer().getReactionRule(reactionRule.getName()).getKineticLaw().getScopedSymbolTable();
			SymbolTable newSymbolTable = reactionRule.getKineticLaw().getScopedSymbolTable();
			for (Parameter p : reactionRule.getKineticLaw().getUnresolvedParameters()){
				convertVarsWithUnitFactors(oldSymbolTable, newSymbolTable, p, dimensionless, KMOLE);
			}
			for (Parameter p : reactionRule.getKineticLaw().getLocalParameters()){
				convertVarsWithUnitFactors(oldSymbolTable, newSymbolTable, p, dimensionless, KMOLE);
			}
		}
		for (SimulationContext simContext : newBioModel.getSimulationContexts()) {
			SimulationContext oldSimContext = oldBioModel.getSimulationContext(simContext.getName());
			// ArrayList<Parameter> parameterList = new ArrayList<Parameter>();	
			for (StructureMapping mapping : simContext.getGeometryContext().getStructureMappings()) {
				Structure oldStructure = oldModel.getStructure(mapping.getStructure().getName());
				StructureMapping oldMapping = oldSimContext.getGeometryContext().getStructureMapping(oldStructure);
				for (Parameter p : mapping.computeApplicableParameterList()){
					convertVarsWithUnitFactors(oldMapping, mapping, p, dimensionless, KMOLE);
				}
			}
			for (SpeciesContextSpec spec : simContext.getReactionContext().getSpeciesContextSpecs()) {
				SpeciesContext oldSpeciesContext = oldModel.getSpeciesContext(spec.getSpeciesContext().getName());
				SpeciesContextSpec oldSpec = oldSimContext.getReactionContext().getSpeciesContextSpec(oldSpeciesContext);
				for (Parameter p : spec.computeApplicableParameterList()){
					convertVarsWithUnitFactors(oldSpec, spec, p, dimensionless, KMOLE);
				}
			}
			for (int i=0; i<simContext.getElectricalStimuli().length; i++){
				ElectricalStimulus newElectricalStimulus = simContext.getElectricalStimuli()[i];
				ElectricalStimulus oldElectricalStimulus = oldSimContext.getElectricalStimuli()[i];
				for (Parameter p : newElectricalStimulus.getParameters()){
					convertVarsWithUnitFactors(oldElectricalStimulus.getNameScope().getScopedSymbolTable(), newElectricalStimulus.getNameScope().getScopedSymbolTable(), p, dimensionless, KMOLE);
				}
			}
			// convert events : trigger and delay parameters and event assignments
			for (int i=0; simContext.getBioEvents()!=null && oldSimContext.getBioEvents()!=null && i<simContext.getBioEvents().length; i++){
				BioEvent newBioEvent = simContext.getBioEvents()[i];
				BioEvent oldBioEvent = oldSimContext.getBioEvent(newBioEvent.getName());
				for (Parameter p : newBioEvent.getEventParameters()){
					convertVarsWithUnitFactors(oldBioEvent.getNameScope().getScopedSymbolTable(), newBioEvent.getNameScope().getScopedSymbolTable(), p, dimensionless, KMOLE);
				}
				// for each event assignment expression
				for (int e=0;e<newBioEvent.getEventAssignments().size();e++){
					ScopedSymbolTable newSymbolTable = newBioEvent.getNameScope().getScopedSymbolTable();
					ScopedSymbolTable oldSymbolTable = oldBioEvent.getNameScope().getScopedSymbolTable();
					EventAssignment newEventAssignment = newBioEvent.getEventAssignments().get(e);
					EventAssignment oldEventAssignment = oldBioEvent.getEventAssignments().get(e);
					VCUnitDefinition oldTargetUnit = oldEventAssignment.getTarget().getUnitDefinition();
					VCUnitDefinition newTargetUnit = newEventAssignment.getTarget().getUnitDefinition();
					Expression eventAssgnExpr = newEventAssignment.getAssignmentExpression();
					convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, oldTargetUnit, newTargetUnit, eventAssgnExpr, dimensionless, KMOLE);
				}
			}
			
			/**
			 * @TODO: If rate rule variable unit is TBD, we still need to handle the rate expression unit.
			 */
			// convert rate rules
			RateRule[] rateRules = simContext.getRateRules();
			if (rateRules != null && rateRules.length > 0) { 
				for (RateRule rateRule : rateRules) {
					RateRule oldRateRule = oldSimContext.getRateRule(rateRule.getName());
					ScopedSymbolTable oldSymbolTable = oldRateRule.getSimulationContext();
					ScopedSymbolTable newSymbolTable = rateRule.getSimulationContext();
	
					VCUnitDefinition oldTargetUnit = oldRateRule.getRateRuleVar().getUnitDefinition();
					VCUnitDefinition newTargetUnit = rateRule.getRateRuleVar().getUnitDefinition();
					Expression rateRuleExpr = rateRule.getRateRuleExpression();
					convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, oldTargetUnit, newTargetUnit, rateRuleExpr, dimensionless, KMOLE);
				}
			}
			AssignmentRule[] assignmentRules = simContext.getAssignmentRules();
			if (assignmentRules != null && assignmentRules.length > 0) { 
				for (AssignmentRule assignmentRule : assignmentRules) {
					AssignmentRule oldAssignRule = oldSimContext.getAssignmentRule(assignmentRule.getName());
					ScopedSymbolTable oldSymbolTable = oldAssignRule.getSimulationContext();
					ScopedSymbolTable newSymbolTable = assignmentRule.getSimulationContext();
	
					VCUnitDefinition oldTargetUnit = oldAssignRule.getAssignmentRuleVar().getUnitDefinition();
					VCUnitDefinition newTargetUnit = assignmentRule.getAssignmentRuleVar().getUnitDefinition();
					Expression assignmentRuleExpr = assignmentRule.getAssignmentRuleExpression();
					convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, oldTargetUnit, newTargetUnit, assignmentRuleExpr, dimensionless, KMOLE);
				}
			}
		}	// end  for - simulationContext
		newBioModel.refreshDependencies();
		for (SimulationContext simContext : newBioModel.getSimulationContexts()){
			//
			// force new math generation
			//
			MathMapping mathMapping = simContext.createNewMathMapping();
			try {
				MathDescription mathDesc = mathMapping.getMathDescription();
				simContext.setMathDescriptionAndPrevious(mathDesc, previousMathDescriptionMap.get(simContext.getName()));
			}catch (Exception e){
				throw new RuntimeException("failed to generated math for application "+simContext.getName()+": "+e.getMessage(), e);
			}
		}
		return newBioModel;
	}

	private static void convertVarsWithUnitFactors(SymbolTable oldSymbolTable, SymbolTable newSymbolTable, Parameter parameter,
												   VCUnitDefinition dimensionless, Model.ReservedSymbol KMOLE) throws ExpressionException {
		// get old unit
		VCUnitDefinition oldExprUnit = null;
		Parameter oldParameter = (Parameter)oldSymbolTable.getEntry(parameter.getName());
		if (oldParameter == null){
			System.err.println("parameter "+parameter.getName() + " was not found in the old symbol table");
		}else if (oldParameter.getUnitDefinition() == null){
			System.err.println("parameter "+parameter.getName() + " has a null unit in old model, can't convert");
		}else{
			oldExprUnit = oldParameter.getUnitDefinition();
		}
		
		// get new unit
		VCUnitDefinition newExprUnit = null;
		if (parameter.getUnitDefinition() == null){
			System.err.println("parameter "+parameter.getName() + " has a null unit in new model, can't convert");
		}else{
			newExprUnit = parameter.getUnitDefinition();
		}
		
		convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, oldExprUnit, newExprUnit, parameter.getExpression(), dimensionless, KMOLE);
	}

	/**
	 * getDimensionlessScaleFactor() returns scale factor expression which may include powers of KMOLE
	 * KMOLE is already bound in the expression.
	 */
	public static Expression getDimensionlessScaleFactor(VCUnitDefinition practicallyDimensionlessUnit, VCUnitDefinition dimensionless, Model.ReservedSymbol KMOLE)  {
		if (practicallyDimensionlessUnit.isEquivalent(dimensionless)){
			return new Expression(1.0);
		}
		if (practicallyDimensionlessUnit.isCompatible(dimensionless)){
			double conversionScale = dimensionless.convertTo(1.0, practicallyDimensionlessUnit);
			RationalNumber rationalConversionScale =  RationalNumber.getApproximateFraction(conversionScale);
			return new Expression(rationalConversionScale);
		}
		//
		// not equivalent with dimensionless - try to introduce powers of KMOLE to get equivalence (introduces moles to molecules equivalence)
		//
		final VCUnitDefinition KMOLE_Unit = KMOLE.getUnitDefinition();
		VCUnitDefinition power_of_KMOLE = dimensionless;
		for (int power=1; power<5; power++) {
			power_of_KMOLE = power_of_KMOLE.multiplyBy(KMOLE_Unit);
			if (practicallyDimensionlessUnit.multiplyBy(power_of_KMOLE).isCompatible(dimensionless)) {
				double conversionScale = dimensionless.convertTo(1.0, practicallyDimensionlessUnit.multiplyBy(power_of_KMOLE));
				try {
					return Expression.mult(new Expression(conversionScale), Expression.power(new Expression(KMOLE, KMOLE.getNameScope()), power));
				} catch (ExpressionException e) {
					throw new RuntimeException(e);
				}
			}
			if (practicallyDimensionlessUnit.divideBy(power_of_KMOLE).isCompatible(dimensionless)) {
				double conversionScale = dimensionless.convertTo(1.0, practicallyDimensionlessUnit.divideBy(power_of_KMOLE));
				try {
					return Expression.mult(new Expression(conversionScale), Expression.power(new Expression(KMOLE, KMOLE.getNameScope()), -power));
				} catch (ExpressionException e) {
					throw new RuntimeException(e);
				}
			}
		}
		throw new RuntimeException("unit " + practicallyDimensionlessUnit.getSymbol() + " is not practically dimensionless, even by using KMOLE");
	}

	/**
	 * getDimensionlessScaleFactor() returns scale factor expression which may include powers of KMOLE
	 * KMOLE is already bound in the expression.
	 */
	public static RationalExp getDimensionlessScaleFactorAsRationalExp(VCUnitDefinition practicallyDimensionlessUnit, VCUnitDefinition dimensionless, Model.ReservedSymbol KMOLE)  {
		if (practicallyDimensionlessUnit.isEquivalent(dimensionless)){
			return RationalExp.ONE;
		}
		if (practicallyDimensionlessUnit.isCompatible(dimensionless)){
			double conversionScale = dimensionless.convertTo(1.0, practicallyDimensionlessUnit);
			RationalNumber rationalConversionScale =  RationalNumber.getApproximateFraction(conversionScale);
			return new RationalExp(rationalConversionScale);
		}
		//
		// not equivalent with dimensionless - try to introduce powers of KMOLE to get equivalence (introduces moles to molecules equivalence)
		//
		final VCUnitDefinition KMOLE_Unit = KMOLE.getUnitDefinition();
		VCUnitDefinition power_of_KMOLE_unit = dimensionless;
		RationalExp power_of_KMOLE_exp = RationalExp.ONE;
		for (int power=1; power<5; power++) {
			power_of_KMOLE_unit = power_of_KMOLE_unit.multiplyBy(KMOLE_Unit);
			power_of_KMOLE_exp = power_of_KMOLE_exp.mult(new RationalExp(KMOLE.getName()));
			if (practicallyDimensionlessUnit.multiplyBy(power_of_KMOLE_unit).isCompatible(dimensionless)) {
				double conversionScale = dimensionless.convertTo(1.0, practicallyDimensionlessUnit.multiplyBy(power_of_KMOLE_unit));
				RationalExp rexp = new RationalExp(RationalNumber.getApproximateFraction(conversionScale));
				return rexp.mult(power_of_KMOLE_exp);
			}
			if (practicallyDimensionlessUnit.divideBy(power_of_KMOLE_unit).isCompatible(dimensionless)) {
				double conversionScale = dimensionless.convertTo(1.0, practicallyDimensionlessUnit.divideBy(power_of_KMOLE_unit));
				RationalExp rexp = new RationalExp(RationalNumber.getApproximateFraction(conversionScale));
				return rexp.div(power_of_KMOLE_exp);
			}
		}
		throw new RuntimeException("unit " + practicallyDimensionlessUnit.getSymbol() + " is not practically dimensionless, even by using KMOLE");
	}

	private static void convertExprWithUnitFactors(SymbolTable oldSymbolTable, SymbolTable newSymbolTable,
												   VCUnitDefinition oldExprUnit, VCUnitDefinition newExprUnit,
												   Expression expr, VCUnitDefinition dimensionless, Model.ReservedSymbol KMOLE) throws ExpressionException {

		if (expr == null) {
			return;
		}
		String[] symbols = expr.getSymbols();
		if (symbols != null) {
			for (String s : symbols) {
				SymbolTableEntry newSTE = newSymbolTable.getEntry(s);
				SymbolTableEntry oldSTE = oldSymbolTable.getEntry(s);
				
				if (oldSTE == null){
					logger.error("symbol '"+s+"' in expression "+expr.infix() + " was not found in the new symbol table");
					continue;
				}
	
				if (newSTE == null){
					logger.error("symbol '"+s+"' in expression "+expr.infix() + " was not bound to the old symbol table");
					continue;
				}
	
				if (oldSTE.getUnitDefinition() == null){
					logger.error("symbol '"+s+"' in expression "+expr.infix() + " is has a null unit in old model, can't convert");
					continue;
				}
				
				if (newSTE.getUnitDefinition() == null){
					logger.error("symbol '"+s+"' in expression "+expr.infix() + " is has a null unit in new model, can't convert");
					continue;
				}
	
				if (oldSTE.getUnitDefinition().isTBD()){
					continue;
				}
				if (newSTE.getUnitDefinition().isTBD()){
					continue;
				}
				VCUnitDefinition oldToNewConversionUnit = oldSTE.getUnitDefinition().divideBy(newSTE.getUnitDefinition());
				Expression conversionFactor = getDimensionlessScaleFactor(oldToNewConversionUnit, dimensionless, KMOLE);
				if (!conversionFactor.isOne()){
					Expression spcSymbol = new Expression(newSTE, newSTE.getNameScope());
					expr.substituteInPlace(spcSymbol, Expression.mult(new Expression(conversionFactor), spcSymbol));
					expr.substituteInPlace(expr, expr.flattenFactors(KMOLE.getName()));
				}
			}
		}
		if (oldExprUnit == null || oldExprUnit.isTBD() || newExprUnit == null || newExprUnit.isTBD()){
			Expression flattened = expr.flatten();
			Expression origExp = new Expression(expr);
			expr.substituteInPlace(origExp,flattened);
			return;
		}
		
		VCUnitDefinition oldToNewConversionUnit = newExprUnit.divideBy(oldExprUnit);
		Expression conversionFactor = getDimensionlessScaleFactor(oldToNewConversionUnit, dimensionless, KMOLE);
		if (!conversionFactor.isOne()){
			Expression oldExp = new Expression(expr);
			expr.substituteInPlace(oldExp, Expression.mult(new Expression(conversionFactor), oldExp));
			expr.substituteInPlace(expr, expr.flattenFactors(KMOLE.getName()));
		}
		Expression flattened = expr.flattenFactors(KMOLE.getName());
		Expression origExp = new Expression(expr);
		expr.substituteInPlace(origExp,flattened);
	}
	
	public static void main(String[] args) {

		double a = 1.0;
		double b = 1E-1;
		System.out.println(a/b+"");
		b = 1E-2;
		System.out.println(a/b+"");
		b = 1E-3;
		System.out.println(a/b+"");
		b = 1E-4;
		System.out.println(a/b+"");
		b = 1E-5;
		System.out.println(a/b+"");
		b = 1E-6;
		System.out.println(a/b+"");
		b = 1E-7;
		System.out.println(a/b+"");
		b = 1E-8;
		System.out.println(a/b+"");
		b = 1E-9;
		System.out.println(a/b+"");
		b = 1E-10;
		System.out.println(a/b+"");
		b = 1E-11;
		System.out.println(a/b+"");
		b = 1E-12;
		System.out.println(a/b+"");
		b = 1E-13;
		System.out.println(a/b+"");
		b = 1E-14;
		System.out.println(a/b+"");
		b = 1E-15;
		System.out.println(a/b+"");
		b = 1E-16;
		System.out.println(a/b+"");
		b = 1E-17;
		System.out.println(a/b+"");
		b = 1E-18;
		System.out.println(a/b+"");
		b = 1E-19;
		System.out.println(a/b+"");
		b = 1E-20;
		System.out.println(a/b+"");
		b = 1E-21;
		System.out.println(a/b+"");
		b = 1E-22;
		System.out.println(a/b+"");
		b = 1E-23;
		System.out.println(a/b+"");
		b = 1E-24;
		System.out.println(a/b+"");
		b = 1E-25;
		System.out.println(a/b+"");
		b = 1E-26;
		System.out.println(a/b+"");
		
		
		
		
		
	}
}
