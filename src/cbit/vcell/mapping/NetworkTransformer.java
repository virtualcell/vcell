package cbit.vcell.mapping;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.pathway.Stoichiometry;
import org.vcell.util.BeanUtils;
import org.vcell.util.Matchable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.bionetgen.BNGOutputFileParser;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.bionetgen.BNGParameter;
import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.server.bionetgen.BNGUtils;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Kinetics.KineticsParameter;


public class NetworkTransformer implements SimContextTransformer {

	@Override
	final public SimContextTransformation transform(SimulationContext originalSimContext) {
		SimulationContext transformedSimContext;
		try {
			transformedSimContext = (SimulationContext)BeanUtils.cloneSerializable(originalSimContext);
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
		
		String input = RbmUtils.convertToBngl(simContext);
		BNGInput bngInput = new BNGInput(input);
		BNGOutput bngOutput = null;
		try {
			bngOutput = BNGUtils.executeBNG(bngInput);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}
		String bngNetString = bngOutput.getNetFileContent();
		
		BNGOutputSpec outputSpec = null;
		outputSpec = BNGOutputFileParser.createBngOutputSpec(bngNetString);
		BNGOutputFileParser.printBNGNetOutput(outputSpec);
		
		Model model = transformedSimulationContext.getModel();
		ReactionContext reactionContext = transformedSimulationContext.getReactionContext();
		try {
		System.out.println("Parameters : \n");
		for (int i = 0; i < outputSpec.getBNGParams().length; i++){
			BNGParameter p = outputSpec.getBNGParams()[i];
			System.out.println(i+1 + ":\t\t"+ p.toString());
			if(model.getRbmModelContainer().getParameter(p.getName()) != null) {
				System.out.println("   ...already exists.");
				continue;		// if it's already there we don't try to add it again; this should be true for all of them!
			}
			Expression exp = new Expression(p.getValue());
			exp.bindExpression(model.getRbmModelContainer().getSymbolTable());
			model.getRbmModelContainer().addParameter(p.getName(), exp);
		}
		
		HashMap<Integer, String>  speciesMap = new HashMap(); // the reactions will need this map to recover the names of species knowing only the networkFileIndex
		System.out.println("\n\nSpecies : \n");
		for (int i = 0; i < outputSpec.getBNGSpecies().length; i++){
			BNGSpecies s = outputSpec.getBNGSpecies()[i];
			System.out.println(i+1 + ":\t\t"+ s.toString());
			SpeciesContext species = model.getSpeciesContextByPattern(s.getName());
			if(species != null) {
				System.out.println("   ...already exists.");
				speciesMap.put(s.getNetworkFileIndex(), species.getName());		// existing name
				continue;
			}
			int count = 0;		// generate unique name for the species
			String speciesName = null;
			while (true) {
				speciesName = "s" + count;	
				if (model.getSpecies(speciesName) == null && model.getSpeciesContext(speciesName) == null) {
					break;
				}	
				count++;
			}
			speciesMap.put(s.getNetworkFileIndex(), speciesName);				// newly created name
			SpeciesContext speciesContext = new SpeciesContext(new Species(speciesName, s.getName()), model.getStructure(0), null);
			speciesContext.setName(speciesName);
			model.addSpecies(speciesContext.getSpecies());
			model.addSpeciesContext(speciesContext);
			SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(speciesContext);
			Parameter param = scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration);
			param.setExpression(s.getConcentration());
		}
		
		System.out.println("\n\nReactions : \n");
		for (int i = 0; i < outputSpec.getBNGReactions().length; i++){
			BNGReaction r = outputSpec.getBNGReactions()[i];
//			System.out.println(i+1 + ":\t\t"+ r.writeReaction());
			int count=0;
			String reactionName = null;
			while (true) {
				reactionName = "r" + count;	
				if (model.getReactionStep(reactionName) == null && model.getRbmModelContainer().getReactionRule(reactionName) == null) {
					break;
				}	
				count++;
			}
			SimpleReaction sr = new SimpleReaction(model, model.getStructure(0), reactionName);
			for (int j = 0; j < r.getReactants().length; j++){
				BNGSpecies s = r.getReactants()[j];
				String scName = speciesMap.get(s.getNetworkFileIndex());
				SpeciesContext sc = model.getSpeciesContext(scName);
				Reactant reactant = sr.getReactant(scName);
				if(reactant == null) { 
					int stoichiometry = 1;
					sr.addReactant(sc, stoichiometry);
				} else {
					int stoichiometry = reactant.getStoichiometry();
					stoichiometry += 1;
					reactant.setStoichiometry(stoichiometry);
				}
			}
			for (int j = 0; j < r.getProducts().length; j++){
				BNGSpecies s = r.getProducts()[j];
				String scName = speciesMap.get(s.getNetworkFileIndex());
				SpeciesContext sc = model.getSpeciesContext(scName);
				Product product = sr.getProduct(scName);
				if(product == null) { 
					int stoichiometry = 1;
					sr.addProduct(sc, stoichiometry);
				} else {
					int stoichiometry = product.getStoichiometry();
					stoichiometry += 1;
					product.setStoichiometry(stoichiometry);
				}
			}
			MassActionKinetics k = new MassActionKinetics(sr);
			sr.setKinetics(k);
			KineticsParameter kforward = k.getForwardRateParameter();
			sr.getKinetics().setParameterValue(kforward, r.getParamExpression());
			model.addReactionStep(sr);
		}
		} catch (PropertyVetoException ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		} catch (ExpressionBindingException ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		} catch (ModelException ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		} catch (ExpressionException ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}
		System.out.println("Done transforming");
	}

}
