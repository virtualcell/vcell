package cbit.vcell.mapping;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmKineticLaw.RateLawType;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class RulebasedTransformer implements SimContextTransformer {

	@Override
	final public SimContextTransformation transform(SimulationContext originalSimContext, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements netGenReq_NOT_USED) {
		SimulationContext transformedSimContext;
		try {
			transformedSimContext = (SimulationContext)BeanUtils.cloneSerializable(originalSimContext);
			transformedSimContext.getModel().refreshDependencies();
			transformedSimContext.refreshDependencies();
			transformedSimContext.compareEqual(originalSimContext);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected transform exception: "+e.getMessage());
		}
		transformedSimContext.getModel().refreshDependencies();
		transformedSimContext.refreshDependencies1(false);

		ArrayList<ModelEntityMapping> entityMappings = new ArrayList<ModelEntityMapping>();
		
		try {
			transform(originalSimContext,transformedSimContext,entityMappings);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected transform exception: "+e.getMessage());
		}
		
		ModelEntityMapping[] modelEntityMappings = entityMappings.toArray(new ModelEntityMapping[0]);
		
		return new SimContextTransformation(originalSimContext, transformedSimContext, modelEntityMappings);
	}
		
	public void transform(SimulationContext originalSimContext, SimulationContext transformedSimulationContext, ArrayList<ModelEntityMapping> entityMappings) throws PropertyVetoException{
		Model newModel = transformedSimulationContext.getModel();
		Model originalModel = originalSimContext.getModel();
		ModelEntityMapping em = null;
		
//		for(ReactionRule newrr : newModel.getRbmModelContainer().getReactionRuleList()) {			// map new and old kinetic parameters of reaction rules
//			ReactionRule oldrr = originalModel.getRbmModelContainer().getReactionRule(newrr.getName());
//			for(Parameter newkp : newrr.getKineticLaw().getKineticsParameters()) {
//				Parameter oldkp = oldrr.getKineticLaw().getKineticsParameter(newkp.getName());
//				em = new ModelEntityMapping(oldkp, newkp);
//				entityMappings.add(em);
//			}
//		}
		
		for(SpeciesContext sc : newModel.getSpeciesContexts()) {
			em = new ModelEntityMapping(originalModel.getSpeciesContext(sc.getName()), sc);		// map new and old species contexts
			entityMappings.add(em);
			if(sc.hasSpeciesPattern()) {
				continue;	// it's perfect already and can't be improved
			}
			try {
				MolecularType newmt = newModel.getRbmModelContainer().createMolecularType();
				newModel.getRbmModelContainer().addMolecularType(newmt, false);
				MolecularTypePattern newmtp = new MolecularTypePattern(newmt);
				SpeciesPattern newsp = new SpeciesPattern();
				newsp.addMolecularTypePattern(newmtp);
				sc.setSpeciesPattern(newsp);
				
				RbmObservable newo = new RbmObservable(newModel,sc.getName()+"_SeedSpeciesObservable",sc.getStructure(),RbmObservable.ObservableType.Molecules);
				newo.addSpeciesPattern(newsp);
				newModel.getRbmModelContainer().addObservable(newo);

				em = new ModelEntityMapping(originalModel.getSpeciesContext(sc.getName()), newo);	// map new observable to old species context
				entityMappings.add(em);
			} catch (ModelException e) {
				e.printStackTrace();
				throw new RuntimeException("unable to transform species context: "+e.getMessage());
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ReactionSpec[] reactionSpecs = transformedSimulationContext.getReactionContext().getReactionSpecs();
		for(ReactionSpec reactionSpec : reactionSpecs) {
			
			if (reactionSpec.isExcluded()){
				continue;	// we create rules only from those reactions which are not excluded
			}
			ReactionStep rs = reactionSpec.getReactionStep();
			String name = rs.getName();
			String mangled = TokenMangler.fixTokenStrict(name);
			mangled = newModel.getReactionName(mangled);
			Kinetics k = rs.getKinetics();
			if(!(k instanceof MassActionKinetics)) {
				throw new RuntimeException("Only Mass Action Kinetics supported at this time, reaction \""+rs.getName()+"\" uses kinetic law type \""+rs.getKinetics().getName()+"\"");
			}
			
			boolean bReversible = true;
			ReactionRule rr = new ReactionRule(newModel, mangled, rs.getStructure(), bReversible);
			rr.setStructure(rs.getStructure());
		
			MassActionKinetics massActionKinetics = (MassActionKinetics)k;
			Expression forwardRateExp = massActionKinetics.getForwardRateParameter().getExpression();
			String forwardRateName = massActionKinetics.getForwardRateParameter().getName();
			Expression reverseRateExp = massActionKinetics.getReverseRateParameter().getExpression();
			String reverseRateName = massActionKinetics.getReverseRateParameter().getName();
			RateLawType rateLawType = RateLawType.MassAction;
			RbmKineticLaw kineticLaw = new RbmKineticLaw(rr, rateLawType);
			try {
				LocalParameter fR = kineticLaw.getLocalParameter(RbmKineticLawParameterType.MassActionForwardRate);
				fR.setName(forwardRateName);
				LocalParameter rR = kineticLaw.getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
				rR.setName(reverseRateName);
				kineticLaw.setParameterValue(fR, forwardRateExp, true);
				kineticLaw.setParameterValue(rR, reverseRateExp, true);
				
				//
				// set values of local user-defined parameters also (user defined).
				//
				for (KineticsParameter reaction_p : massActionKinetics.getKineticsParameters()){
					if (reaction_p.getRole() == Kinetics.ROLE_UserDefined){
						LocalParameter rule_p = kineticLaw.getLocalParameter(reaction_p.getName());
						if (rule_p.getRole() != RbmKineticLawParameterType.UserDefined){
							throw new RuntimeException("user defined parameter "+reaction_p.getName()+" didn't map to a user defined parameter in generated rule");
						}
						kineticLaw.setParameterValue(rule_p, reaction_p.getExpression(), true);
						rule_p.setUnitDefinition(reaction_p.getUnitDefinition());
					}
				}
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException("Problem attempting to set RbmKineticLaw expression: "+ e.getMessage());
			}
			rr.setKineticLaw(kineticLaw);
			
			KineticsParameter[] kpList = k.getKineticsParameters();
			ModelParameter[] mpList = rs.getModel().getModelParameters();
			ModelParameter mp = rs.getModel().getModelParameter(kpList[0].getName());
			
			ReactionParticipant[] pList = rs.getReactionParticipants();
			for(ReactionParticipant p : pList) {
				if(p instanceof Reactant) {
					int stoichiometry = p.getStoichiometry();
					// TODO: must not reuse the SP of the species, must make new deep constructor
					// because the molecular type patterns are going to be different (each its own match for example)
					for(int i=0; i<stoichiometry; i++) {
						SpeciesPattern speciesPattern = new SpeciesPattern(p.getSpeciesContext().getSpeciesPattern());
						ReactantPattern reactantPattern = new ReactantPattern(speciesPattern, rr.getStructure());
						rr.addReactant(reactantPattern);
					}
					
				} else if(p instanceof Product) {
					int stoichiometry = p.getStoichiometry();
					for(int i=0; i<stoichiometry; i++) {
						SpeciesPattern speciesPattern = new SpeciesPattern(p.getSpeciesContext().getSpeciesPattern());
						ProductPattern productPattern = new ProductPattern(speciesPattern, rr.getStructure());
						rr.addProduct(productPattern);
					}
				}
			}
			// NFSim doesn't support more then 2 reactants / products
			if(rr.getReactantPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 reactants within a reaction: " + name;
				throw new RuntimeException(message);
			}
			if(rr.getProductPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 products within a reaction: " + name;
				throw new RuntimeException(message);
			}
			newModel.removeReactionStep(rs);
			newModel.getRbmModelContainer().addReactionRule(rr);
		}
		
		for(ReactionRuleSpec rrs : transformedSimulationContext.getReactionContext().getReactionRuleSpecs()) {
			if(rrs == null) {
				continue;
			}
			ReactionRule rr = rrs.getReactionRule();
			if(rrs.isExcluded()) {		// delete those rules which are disabled (excluded) in the Specifications / Reaction table
				newModel.getRbmModelContainer().removeReactionRule(rr);
				continue;
			}
			// NFSim doesn't support more then 2 reactants / products
			if(rr.getReactantPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 reactants within a reaction rule: " + rr.getDisplayName();
				throw new RuntimeException(message);
			}
			if(rr.getProductPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 products within a reaction rule: " + rr.getDisplayName();
				throw new RuntimeException(message);
			}
		}
		
		// now that we generated the rules we can delete the reaction steps they're coming from
		for(ReactionStep rs : newModel.getReactionSteps()) {
			newModel.removeReactionStep(rs);
		}
		
		// TODO; for debug only, can be commented out once this code gets stable enough
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);
		RbmNetworkGenerator.writeBngl(transformedSimulationContext, pw, false, false);
		String resultString = bnglStringWriter.toString();
		System.out.println(resultString);
		pw.close();
	}
}
