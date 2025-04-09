package cbit.vcell.mapping;

import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyVetoException;

/*
 * Flattening a Rule-based Model
 */
public class RateRuleTransformer implements SimContextTransformer {
	private final static Logger lg = LogManager.getLogger(RateRuleTransformer.class);

	@Override
	final public SimContextTransformation transform(SimulationContext originalSimContext, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements) {
		SimulationContext transformedSimContext;
		try {
			mathMappingCallback.setMessage("transforming the Rate Rules...");
			transformedSimContext = XmlHelper.cloneSimulationContext(originalSimContext);
		} catch (XmlParseException e) {
			throw new RuntimeException("unexpected exception: "+e.getMessage(), e);
		}
		transformedSimContext.getModel().refreshDependencies();
		transformedSimContext.refreshDependencies1(false);

// TODO: we don't perform the transformation anymore because we deal with the rate rules in the DiffEquMathMapping.java
//		transform(originalSimContext, transformedSimContext, mathMappingCallback, networkGenerationRequirements);
		
		return new SimContextTransformation(originalSimContext, originalSimContext, null);
	}

	private void transform(SimulationContext simContext, SimulationContext transformedSimulationContext, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements){

		if(transformedSimulationContext.getRateRules() == null) {
			return;
		}
		
		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		String msg = "Start Rate Rule conversion.";
		System.out.println(msg);
		
		Model model = transformedSimulationContext.getModel();
		ReactionContext reactionContext = transformedSimulationContext.getReactionContext();
		SpeciesContextSpec[] scss = reactionContext.getSpeciesContextSpecs();

		for(RateRule rr : transformedSimulationContext.getRateRules()) {
			String rrName = rr.getName();
			SymbolTableEntry ste = rr.getRateRuleVar();
			SpeciesContext sc;
			if(ste instanceof Model.ModelParameter) {
				
				Model.ModelParameter mp = (Model.ModelParameter)ste;
				String speciesName = mp.getName();
				Expression exp = mp.getExpression();
				Structure struct = model.getStructure(0);
				
				try {
					model.removeModelParameter(mp);
				} catch (PropertyVetoException e) {
					lg.warn(e);
				}

				
				Species sp = new Species(speciesName, speciesName);
				sc = new SpeciesContext(sp, struct, null);
				try {
					model.addSpecies(sp);
					model.addSpeciesContext(sc);
				} catch (PropertyVetoException exc) {
					throw new RuntimeException("RateRule '" + rr.getName() + "' conversion failed.\n" + exc.getMessage(), exc);
				}
				rr.setRateRuleVar(sc);
				SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(sc);
				try {
					scs.getInitialConcentrationParameter().setExpression(exp);
				} catch (ExpressionBindingException e) {
					lg.error(e);
				}
			}
			
		}
		
		
//		for(RateRule rr : transformedSimulationContext.getRateRules()) {
//			
//			String rrName = rr.getName();
//			SymbolTableEntry ste = rr.getRateRuleVar();
//			SpeciesContext sc;
//			if(ste instanceof Model.ModelParameter) {
//				if(model.getStructures().length > 1) {
//					throw new RuntimeException("The variable for RateRule '" + rrName + "' cannot be a Global Parameter in a multi-compartmental BioModel.");
//				}
//				String speciesName = "s_" + rr.getRateRuleVar().getName();
//				Structure struct = model.getStructure(0);
//				Species sp = new Species(speciesName, speciesName);
//				sc = new SpeciesContext(sp, struct, null);
//				try {
//					model.addSpecies(sp);
//					model.addSpeciesContext(sc);
//				} catch (PropertyVetoException exc) {
//					lg.error(e);
//					throw new RuntimeException("RateRule '" + rr.getName() + "' conversion failed.\n" + exc.getMessage());
//				}
//			} else if(ste instanceof SpeciesContext) {
//				sc = (SpeciesContext)ste;
//			} else {
//				throw new RuntimeException("The variable for RateRule '" + rrName + "' is missing or not a Species / Global Parameter.");
//			}
//			
//			SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(sc);
//			if(scs.isConstant()) {
//				scs.setConstant(false);		// rate rules species must not be constant (even if they are for plain reactions)  TODO: conflict here!!!
//			}
//			
//			// make a reaction with name derived from the ReactionRule name
//			String reactionName = rr.getName();
//			reactionName = TokenMangler.getNextEnumeratedToken(reactionName);
//			Structure structure = sc.getStructure();	// the rate rule happens in the compartment where its species variable is defined
//			SimpleReaction sr = null;
//			try {
//				sr = new SimpleReaction(model, structure, reactionName, false);
//				
//				int stoichiometry = 1;		// add the species variable as the unique product of this reaction
//				sr.addProduct(sc, stoichiometry);
//
//				
//				KineticsDescription newKineticChoice = KineticsDescription.GeneralLumped;
//				sr.setKinetics(newKineticChoice.createKinetics(sr));		// kinetics as GeneralLumped
//				
//				// initialize the reaction rate with the expression of the rate rule
//				Kinetics.KineticsParameter kp = sr.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_LumpedReactionRate);
//				Expression rrExpression = rr.getRateRuleExpression();
//				sr.getKinetics().setParameterValue(kp, new Expression(rrExpression));
//				sr.getKinetics().resolveUndefinedUnits();
//				
//				model.addReactionStep(sr);
//				
//				transformedSimulationContext.removeRateRule(rr);
//
//			} catch (Exception exc) {
//				throw new RuntimeException("RateRule '" + rr.getName() + "' conversion failed.\n" + exc.getMessage());
//			}
//		}
		System.out.println("Done transforming");		
		msg = "Generating math...";
		mathMappingCallback.setMessage(msg);
		System.out.println(msg);
		
	}

	
}
