package cbit.vcell.biomodel;

import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.Delay;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

public class ModelUnitConverter {

	public static BioModel createBioModelWithNewUnitSystem(BioModel oldBioModel, ModelUnitSystem newUnitSystem) throws ExpressionException, XmlParseException {
		// new BioModel has new unit system applied to all built-in units ... but expressions still need to be corrected (see below).
		BioModel newBioModel = XmlHelper.cloneBioModelWithNewUnitSystem(oldBioModel, newUnitSystem);
		Model newModel = newBioModel.getModel();
		Model oldModel = oldBioModel.getModel();

		for (Parameter p : newBioModel.getModel().getModelParameters()){
			convertVarsWithUnitFactors(oldBioModel.getModel(), newBioModel.getModel(), p);
		}
		
		for (ReactionStep reactionStep : newBioModel.getModel().getReactionSteps()) {
			for (Parameter p : reactionStep.getKinetics().getUnresolvedParameters()){
				convertVarsWithUnitFactors(oldBioModel.getModel().getReactionStep(reactionStep.getName()), reactionStep, p);
			}
			for (Parameter p : reactionStep.getKinetics().getKineticsParameters()){
				convertVarsWithUnitFactors(oldBioModel.getModel().getReactionStep(reactionStep.getName()), reactionStep, p);
			}
		}
		for (SimulationContext simContext : newBioModel.getSimulationContexts()) {
			SimulationContext oldSimContext = oldBioModel.getSimulationContext(simContext.getName());
			// ArrayList<Parameter> parameterList = new ArrayList<Parameter>();	
			for (StructureMapping mapping : simContext.getGeometryContext().getStructureMappings()) {
				Structure oldStructure = oldModel.getStructure(mapping.getStructure().getName());
				StructureMapping oldMapping = oldSimContext.getGeometryContext().getStructureMapping(oldStructure);
				for (Parameter p : mapping.computeApplicableParameterList()){
					convertVarsWithUnitFactors(oldMapping, mapping, p);
				}
			}
			for (SpeciesContextSpec spec : simContext.getReactionContext().getSpeciesContextSpecs()) {
				SpeciesContext oldSpeciesContext = oldModel.getSpeciesContext(spec.getSpeciesContext().getName());
				SpeciesContextSpec oldSpec = oldSimContext.getReactionContext().getSpeciesContextSpec(oldSpeciesContext);
				for (Parameter p : spec.computeApplicableParameterList()){
					convertVarsWithUnitFactors(oldSpec, spec, p);
				}
			}
			for (int i=0; i<simContext.getElectricalStimuli().length; i++){
				ElectricalStimulus newElectricalStimulus = simContext.getElectricalStimuli()[i];
				ElectricalStimulus oldElectricalStimulus = oldSimContext.getElectricalStimuli()[i];
				for (Parameter p : newElectricalStimulus.getParameters()){
					convertVarsWithUnitFactors(oldElectricalStimulus.getNameScope().getScopedSymbolTable(), newElectricalStimulus.getNameScope().getScopedSymbolTable(), p);
				}
			}
			// convert events : trigger, delay, event assignments
			VCUnitDefinition oldTimeUnit = oldModel.getUnitSystem().getTimeUnit();
			VCUnitDefinition newTimeUnit = newModel.getUnitSystem().getTimeUnit();
			BioEvent[] events = simContext.getBioEvents();
			if (events != null) {
				for (BioEvent event : events) {
					BioEvent oldEvent = oldSimContext.getBioEvent(event.getName());
					ScopedSymbolTable oldSymbolTable = oldEvent.getNameScope().getScopedSymbolTable();
					ScopedSymbolTable newSymbolTable = event.getNameScope().getScopedSymbolTable();
	
					// trigger expression
					Expression triggerExpr = event.getTrigger().getGeneratedExpression();
					convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, null, null, triggerExpr);
					
					// delay expression
					Delay eventDelay = event.getDelay();
					if (eventDelay != null) {
						Expression delayExpr = event.getDelay().getDurationExpression();
						convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, oldTimeUnit, newTimeUnit, delayExpr);
					}
					// for each event assignment expression
					for (int i=0;i<event.getEventAssignments().size();i++){
						EventAssignment newEventAssignment = event.getEventAssignments().get(i);
						EventAssignment oldEventAssignment = oldEvent.getEventAssignments().get(i);
						VCUnitDefinition oldTargetUnit = oldEventAssignment.getTarget().getUnitDefinition();
						VCUnitDefinition newTargetUnit = newEventAssignment.getTarget().getUnitDefinition();
						Expression eventAssgnExpr = newEventAssignment.getAssignmentExpression();
						convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, oldTargetUnit, newTargetUnit, eventAssgnExpr);
					}
				}	// end for - events
			}	// end if (events != null)
			
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
					convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, oldTargetUnit, newTargetUnit, rateRuleExpr);
				}
			}

		}	// end  for - simulationContext
		return newBioModel;
	}

	private static void convertVarsWithUnitFactors(SymbolTable oldSymbolTable, SymbolTable newSymbolTable, Parameter parameter) throws ExpressionException {
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
		
		convertExprWithUnitFactors(oldSymbolTable, newSymbolTable, oldExprUnit, newExprUnit, parameter.getExpression());
	}
	
	private static void convertExprWithUnitFactors(SymbolTable oldSymbolTable, SymbolTable newSymbolTable, VCUnitDefinition oldExprUnit, VCUnitDefinition newExprUnit, Expression expr) throws ExpressionException {
		if (expr == null) {
			return;
		}
		String[] symbols = expr.getSymbols();
		if (symbols != null) {
			for (String s : symbols) {
				SymbolTableEntry boundSTE = expr.getSymbolBinding(s);
				
				SymbolTableEntry newSTE = newSymbolTable.getEntry(s);
				SymbolTableEntry oldSTE = oldSymbolTable.getEntry(s);
				
				if (boundSTE == null){
					System.err.println("symbol '"+s+"' in expression "+expr.infix() + " was not bound to the model");
					continue;
				}
	
				if (oldSTE == null){
					System.err.println("symbol '"+s+"' in expression "+expr.infix() + " was not found in the new symbol table");
					continue;
				}
	
				if (newSTE == null){
					System.err.println("symbol '"+s+"' in expression "+expr.infix() + " was not bound to the old symbol table");
					continue;
				}
	
				if (newSTE != boundSTE){
					System.err.println("symbol '"+s+"' in expression "+expr.infix() + " is not bound properly (binding doesn't match new symbol table)");
					continue;
				}
				
				if (oldSTE.getUnitDefinition() == null){
					System.err.println("symbol '"+s+"' in expression "+expr.infix() + " is has a null unit in old model, can't convert");
					continue;
				}
				
				if (newSTE.getUnitDefinition() == null){
					System.err.println("symbol '"+s+"' in expression "+expr.infix() + " is has a null unit in new model, can't convert");
					continue;
				}
	
				if (oldSTE.getUnitDefinition().isTBD()){
					continue;
				}
				if (newSTE.getUnitDefinition().isTBD()){
					continue;
				}
				VCUnitDefinition oldToNewConversionUnit = oldSTE.getUnitDefinition().divideBy(newSTE.getUnitDefinition());
				RationalNumber conversionFactor = oldToNewConversionUnit.getDimensionlessScale();
				if (conversionFactor.doubleValue() != 1.0){
					Expression spcSymbol = new Expression(newSTE, newSTE.getNameScope());
					expr.substituteInPlace(spcSymbol, Expression.mult(spcSymbol,new Expression(conversionFactor)));
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
		RationalNumber conversionFactor = oldToNewConversionUnit.getDimensionlessScale();
		if (conversionFactor.doubleValue() != 1.0){
			Expression oldExp = new Expression(expr);
			expr.substituteInPlace(oldExp, Expression.mult(oldExp,new Expression(conversionFactor)));
		}
		Expression flattened = expr.flatten();
		Expression origExp = new Expression(expr);
		expr.substituteInPlace(origExp,flattened);
	}
}
