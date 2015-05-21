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
import cbit.vcell.model.RbmKineticLaw.ParameterType;
import cbit.vcell.model.RbmKineticLaw.RateLawType;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("unexpected exception: "+e.getMessage());
		}
		transformedSimContext.getModel().refreshDependencies();
		transformedSimContext.refreshDependencies1(false);

		ArrayList<ModelEntityMapping> entityMappings = new ArrayList<ModelEntityMapping>();
		
		transform(originalSimContext,transformedSimContext,entityMappings);
		
		ModelEntityMapping[] modelEntityMappings = entityMappings.toArray(new ModelEntityMapping[0]);
		
		return new SimContextTransformation(originalSimContext, transformedSimContext, modelEntityMappings);
	}
		
	public void transform(SimulationContext simContext, SimulationContext transformedSimulationContext, ArrayList<ModelEntityMapping> entityMappings){
		Model newModel = transformedSimulationContext.getModel();
		Model originalModel = simContext.getModel();
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
				newModel.getRbmModelContainer().addMolecularType(newmt);
				MolecularTypePattern newmtp = new MolecularTypePattern(newmt);
				SpeciesPattern newsp = new SpeciesPattern();
				newsp.addMolecularTypePattern(newmtp);
				sc.setSpeciesPattern(newsp);
				
				RbmObservable newo = new RbmObservable(sc.getName());
				newo.setType(RbmObservable.ObservableType.Molecules);
				newo.setModel(newModel);
				newo.addSpeciesPattern(newsp);
				newo.setStructure(sc.getStructure());
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
		for(ReactionStep rs : newModel.getReactionSteps()) {
			
			String name = rs.getName();
			Kinetics k = rs.getKinetics();
			if(!(k instanceof MassActionKinetics)) {
				throw new RuntimeException("Only Mass Action Kinetics supported at this time");
			}
			
			boolean bReversible = true;
			ReactionRule rr = new ReactionRule(newModel, name, bReversible);
			rr.setStructure(rs.getStructure());
		
			Expression forwardRateExp = ((MassActionKinetics)k).getForwardRateParameter().getExpression();
			Expression reverseRateExp = ((MassActionKinetics)k).getReverseRateParameter().getExpression();
			RateLawType rateLawType = RateLawType.MassAction;
			RbmKineticLaw kineticLaw = new RbmKineticLaw(rr, rateLawType);
			try {
				LocalParameter fR = kineticLaw.getParameter(ParameterType.MassActionForwardRate);
				fR.setExpression(forwardRateExp);
				LocalParameter rR = kineticLaw.getParameter(ParameterType.MassActionReverseRate);
				rR.setExpression(reverseRateExp);
			} catch (ExpressionBindingException e) {
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
					SpeciesPattern speciesPattern = p.getSpeciesContext().getSpeciesPattern();
					ReactantPattern reactantPattern = new ReactantPattern(speciesPattern);
					rr.addReactant(reactantPattern);
					
				} else if(p instanceof Product) {
					SpeciesPattern speciesPattern = p.getSpeciesContext().getSpeciesPattern();
					ProductPattern productPattern = new ProductPattern(speciesPattern);
					rr.addProduct(productPattern);
				}
			}
			newModel.getRbmModelContainer().addReactionRule(rr);
		}
		// TODO; for debug only, can be commented out once this code gets stable enough
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);
		RbmNetworkGenerator.writeBngl(transformedSimulationContext, pw, false);
		String resultString = bnglStringWriter.toString();
		System.out.println(resultString);
		pw.close();
	}
}
