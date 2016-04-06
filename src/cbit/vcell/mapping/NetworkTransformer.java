package cbit.vcell.mapping;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vcell.model.bngl.ParseException;
import org.vcell.model.rbm.FakeReactionRuleRateParameter;
import org.vcell.model.rbm.FakeSeedSpeciesInitialConditionsParameter;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.BeanUtils;
import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;

import cbit.vcell.bionetgen.BNGOutputFileParser;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.bionetgen.BNGParameter;
import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.bionetgen.ObservableGroup;
import cbit.vcell.client.desktop.biomodel.SimulationConsolePanel;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.server.bionetgen.BNGException;
import cbit.vcell.server.bionetgen.BNGExecutorService;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.units.VCUnitDefinition;

/*
 * Flattening a Rule-based Model
 */
public class NetworkTransformer implements SimContextTransformer {

	private Map<FakeSeedSpeciesInitialConditionsParameter, Pair<SpeciesContext, Expression>> speciesEquivalenceMap = new LinkedHashMap<FakeSeedSpeciesInitialConditionsParameter, Pair<SpeciesContext, Expression>>();
	private Map<FakeReactionRuleRateParameter, LocalParameter> kineticsParameterMap = new LinkedHashMap<FakeReactionRuleRateParameter, LocalParameter>();
		
	@Override
	final public SimContextTransformation transform(SimulationContext originalSimContext, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements) {
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
		transform(originalSimContext,transformedSimContext,entityMappings,mathMappingCallback,networkGenerationRequirements);
		
		ModelEntityMapping[] modelEntityMappings = entityMappings.toArray(new ModelEntityMapping[0]);
		return new SimContextTransformation(originalSimContext, transformedSimContext, modelEntityMappings);
	}
	
	public static class GeneratedSpeciesSymbolTableEntry implements SymbolTableEntry {
		private SpeciesContext generatedSpeciesContext = null;
		
		private GeneratedSpeciesSymbolTableEntry(SpeciesContext generatedSpeciesContext){
			this.generatedSpeciesContext = generatedSpeciesContext;
		}
		public boolean isConstant(){
			return false;
		}
		public String getName(){
			return generatedSpeciesContext.getName();
		}
		public NameScope getNameScope(){
			return null; // unmappedSymbol.getNameScope();
		}
		public VCUnitDefinition getUnitDefinition() {
			return generatedSpeciesContext.getUnitDefinition();
		}
		public Expression getExpression(){
			return null;
		}
		public double getConstantValue() throws ExpressionException {
			throw new ExpressionException("can't evaluate to constant");
		}
		public SpeciesContext getGeneratedSpeciesContext() {
			return generatedSpeciesContext;
		}
		
		@Override
		public int getIndex() {
			return 0;
		}
	};
	
	public String convertToBngl(SimulationContext simulationContext, boolean ignoreFunctions, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements) {
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);
		if(simulationContext.getModel().getNumStructures() > 1) {
			RbmNetworkGenerator.writeBngl_internal(simulationContext, pw, kineticsParameterMap, speciesEquivalenceMap, networkGenerationRequirements, CompartmentMode.show);
		} else {
			// if we have just one structure use the "old" way of doing things with no structure prefix
			RbmNetworkGenerator.writeBngl_internal(simulationContext, pw, kineticsParameterMap, speciesEquivalenceMap, networkGenerationRequirements, CompartmentMode.hide);

		}
		String bngl = bnglStringWriter.toString();
		pw.close();
//		System.out.println(bngl);
//		for (Map.Entry<String, Pair<SpeciesContext, Expression>> entry : speciesEquivalenceMap.entrySet()) {
//	    String key = entry.getKey();
//	    Pair<SpeciesContext, Expression> value = entry.getValue();
//	    SpeciesContext sc = value.one;
//	    Expression initial = value.two;
//		System.out.println("key: " + key + ",   species: " + sc.getName() + ", initial: " + initial.infix());
//	}
		return bngl;
	}
	
	private boolean isBngHashValid(String input, String hash, SimulationContext simContext) {
		if(input == null || input.length() == 0) {
			return false;
		}
		if(simContext == null || simContext.getMd5hash() == null || simContext.getMostRecentlyCreatedOutputSpec() == null) {
			return false;
		}
		if(hash.equals(simContext.getMd5hash())) {
			return true;
		} else {
			return false;
		}
	}

	private BNGOutputSpec generateNetwork(SimulationContext simContext, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements) 
									throws ClassNotFoundException, IOException {
		TaskCallbackMessage tcm;
		BNGOutputSpec outputSpec;
		speciesEquivalenceMap.clear();
		kineticsParameterMap.clear(); 
		String input = convertToBngl(simContext, true, mathMappingCallback, networkGenerationRequirements);
		for (Map.Entry<FakeSeedSpeciesInitialConditionsParameter, Pair<SpeciesContext, Expression>> entry : speciesEquivalenceMap.entrySet()) {
		    FakeSeedSpeciesInitialConditionsParameter key = entry.getKey();
		    Pair<SpeciesContext, Expression> value = entry.getValue();
		    SpeciesContext sc = value.one;
		    Expression initial = value.two;
			System.out.println("key: " + key.fakeParameterName + ",   species: " + sc.getName() + ", initial: " + initial.infix());
		}

		String md5hash = MD5.md5(input);
		if(isBngHashValid(input, md5hash, simContext)) {
			String s = "Previously saved outputSpec is up-to-date, no need to generate network.";
			System.out.println(s);
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, s);	// not an error, we just want to show it in red
			simContext.appendToConsole(tcm);
			if(simContext.isInsufficientIterations()) {
				s = SimulationConsolePanel.getInsufficientIterationsMessage();
				System.out.println(s);
				tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, s);
				simContext.appendToConsole(tcm);
			}
			if(simContext.isInsufficientMaxMolecules()) {
				s = SimulationConsolePanel.getInsufficientMaxMoleculesMessage();
				System.out.println(s);
				tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, s);
				simContext.appendToConsole(tcm);
			}
			outputSpec = simContext.getMostRecentlyCreatedOutputSpec();
			return (BNGOutputSpec)BeanUtils.cloneSerializable(outputSpec);
		}
		
		BNGInput bngInput = new BNGInput(input);
		BNGOutput bngOutput = null;
		try {
			final BNGExecutorService bngService = BNGExecutorService.getInstance(bngInput,networkGenerationRequirements.timeoutDurationMS);
			bngService.registerBngUpdaterCallback(simContext);
			bngOutput = bngService.executeBNG();
		} catch (BNGException ex) {
			ex.printStackTrace(System.out);
			System.out.println("bionetgen exception");
			if(ex.getMessage().contains("was asked to write the network, but no reactions were found")) {
				RuntimeException rex = new RuntimeException("Specified species and reaction rules are not sufficient to define reaction network.");
				throw rex;
			} else {
				throw ex; //rethrow without losing context
			}
		} catch (RuntimeException ex) {
			ex.printStackTrace(System.out);
			System.out.println("runtime exception");
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			System.out.println("other exception");
			throw new RuntimeException(ex.getMessage());
		}
		
		simContext.setInsufficientIterations(false);
		simContext.setInsufficientMaxMolecules(false);
		String bngConsoleString = bngOutput.getConsoleOutput();
		
		// TODO: this message we check if insufficient iterations / max molecules
		// DO IT OUTSIDE (in the bng service), we now can
		tcm = new TaskCallbackMessage(TaskCallbackStatus.DetailBatch, bngConsoleString);
		simContext.appendToConsole(tcm);

		String bngNetString = bngOutput.getNetFileContent();
		outputSpec = BNGOutputFileParser.createBngOutputSpec(bngNetString);
		BNGOutputFileParser.printBNGNetOutput(outputSpec);			// prints all output to console

		if (mathMappingCallback.isInterrupted()){
			String msg = "Canceled by user.";
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, msg);
			simContext.appendToConsole(tcm);
			simContext.setMd5hash(null);					// clean the cache if the user interrupts
			throw new UserCancelException(msg);
		}
		if(outputSpec.getBNGSpecies().length > SimulationConsolePanel.speciesLimit) {
			String message = SimulationConsolePanel.getSpeciesLimitExceededMessage(outputSpec);
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, message);
			simContext.appendToConsole(tcm);
			simContext.setMd5hash(null);
			message = "Unable to generate Math for Application " + simContext.getName() + ".\n" + message;
			throw new RuntimeException(message);
		}
		if(outputSpec.getBNGReactions().length > SimulationConsolePanel.reactionsLimit) {
			String message = SimulationConsolePanel.getReactionsLimitExceededMessage(outputSpec);
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, message);
			simContext.appendToConsole(tcm);
			simContext.setMd5hash(null);
			message = "Unable to generate Math for Application " + simContext.getName() + ".\n" + message;
			throw new RuntimeException(message);
		}
		
//		System.out.println("new hash: " + md5hash);
//		System.out.println("old hash: " + simContext.getMd5hash());
		if(md5hash != null && md5hash.length() != 0 && outputSpec != null) {
			System.out.println("saving hash and output spec");
			synchronized (this) {
				simContext.setMd5hash(md5hash);
				simContext.setMostRecentlyCreatedOutputSpec(outputSpec);
			}
		} else {
			System.out.println("something is wrong with the hash and/or output spec");
		}
		return (BNGOutputSpec)BeanUtils.cloneSerializable(outputSpec);
	}

	static final float progressFractionQuota = 2.0f/5.0f;
	static final float progressFractionQuotaSpecies = progressFractionQuota / 2.0f / 10.0f;
	
	private void transform(SimulationContext simContext, SimulationContext transformedSimulationContext, ArrayList<ModelEntityMapping> entityMappings, MathMappingCallback mathMappingCallback, NetworkGenerationRequirements networkGenerationRequirements){

		String msg = "Generating network: flattening...";
		mathMappingCallback.setMessage(msg);
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.Clean, "");
		simContext.appendToConsole(tcm);
		tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskStart, msg);
		simContext.appendToConsole(tcm);
		long startTime = System.currentTimeMillis();
		System.out.println("Convert to bngl, execute BNG, retrieve the results.");
		try {
		BNGOutputSpec outputSpec = generateNetwork(simContext, mathMappingCallback, networkGenerationRequirements);
		if (mathMappingCallback.isInterrupted()){
			msg = "Canceled by user.";
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, msg);
			simContext.appendToConsole(tcm);
			throw new UserCancelException(msg);
		}
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println("     " + elapsedTime + " milliseconds");
		
		Model model = transformedSimulationContext.getModel();
		ReactionContext reactionContext = transformedSimulationContext.getReactionContext();
			// ---- Parameters -----------------------------------------------------------------------------------------------
		startTime = System.currentTimeMillis();
		for (int i = 0; i < outputSpec.getBNGParams().length; i++){
			BNGParameter p = outputSpec.getBNGParams()[i];
//			System.out.println(i+1 + ":\t\t"+ p.toString());
			if(model.getRbmModelContainer().getParameter(p.getName()) != null) {
//				System.out.println("   ...already exists.");
				continue;		// if it's already there we don't try to add it again; this should be true for all of them!
			}
			String s = p.getName();
			FakeSeedSpeciesInitialConditionsParameter fakeICParam = FakeSeedSpeciesInitialConditionsParameter.fromString(s);
			if(speciesEquivalenceMap.containsKey(fakeICParam)) {
				continue;	// we get rid of the fake parameters we use as keys
			}
			FakeReactionRuleRateParameter fakeKineticParam = FakeReactionRuleRateParameter.fromString(s);
			if(fakeKineticParam != null) {
				System.out.println("found fakeKineticParam "+fakeKineticParam.fakeParameterName);
				continue;	// we get rid of the fake parameters we use as keys
			}
			throw new RuntimeException("unexpected parameter "+p.getName()+" in internal BNG processing");
//			Expression exp = new Expression(p.getValue());
//			exp.bindExpression(model.getRbmModelContainer().getSymbolTable());
//			model.getRbmModelContainer().addParameter(p.getName(), exp, model.getUnitSystem().getInstance_TBD());
		}
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		msg = "Adding " + outputSpec.getBNGParams().length + " parameters to model, " + elapsedTime + " ms";
		System.out.println(msg);
		
		// ---- Species ------------------------------------------------------------------------------------------------------------
		mathMappingCallback.setMessage("generating network: adding species...");
		mathMappingCallback.setProgressFraction(progressFractionQuota/4.0f);
		startTime = System.currentTimeMillis();
		System.out.println("\nSpecies :");
		HashMap<Integer, String>  speciesMap = new HashMap<Integer, String>(); // the reactions will need this map to recover the names of species knowing only the networkFileIndex
		LinkedHashMap<String, Species> sMap = new LinkedHashMap<String, Species>();
		LinkedHashMap<String, SpeciesContext> scMap = new LinkedHashMap<String, SpeciesContext>();
		LinkedHashMap<String, BNGSpecies> crossMap = new LinkedHashMap<String, BNGSpecies>();
		List<SpeciesContext> noMapForThese = new ArrayList<SpeciesContext>();
		
//		int countGenerated = 0;
//		final int decimalTickCount = Math.max(outputSpec.getBNGSpecies().length/10, 1);
		for (int i = 0; i < outputSpec.getBNGSpecies().length; i++){
			BNGSpecies s = outputSpec.getBNGSpecies()[i];
//			System.out.println(i+1 + ":\t\t"+ s.toString());
			
			String key = s.getConcentration().infix();
			FakeSeedSpeciesInitialConditionsParameter fakeParam = FakeSeedSpeciesInitialConditionsParameter.fromString(key);
			if (fakeParam != null) {
			    Pair<SpeciesContext, Expression> value = speciesEquivalenceMap.get(fakeParam);
			    SpeciesContext originalsc = value.one;		// the species context of the original model
			    Expression initial = value.two;
				s.setConcentration(initial);		// replace the fake initial condition with the real one
				
				// we'll have to find the species context from the cloned model which correspond to the original species
				SpeciesContext sc = model.getSpeciesContext(originalsc.getName());
				
//				System.out.println(sc.getName() + ", " + sc.getSpecies().getCommonName() + "   ...is one of the original seed species.");
				speciesMap.put(s.getNetworkFileIndex(), sc.getName());		// existing name
				sMap.put(sc.getName(), sc.getSpecies());
				scMap.put(sc.getName(), sc);
				crossMap.put(sc.getName(), s);
				noMapForThese.add(sc);
				continue;
			}
			
			// all these species are new!
			int count = 0;				// generate unique name for the species
			String speciesName = null;
			String nameRoot = "s";
			String speciesPatternNameString = s.extractName();
			while (true) {
				speciesName = nameRoot + count;	
				if (Model.isNameUnused(speciesName, model) && !sMap.containsKey(speciesName) && !scMap.containsKey(speciesName)) {
					break;
				}	
				count++;
			}
			speciesMap.put(s.getNetworkFileIndex(), speciesName);				// newly created name
			SpeciesContext speciesContext;
			
			if(s.hasCompartment()) {
				String speciesPatternCompartmentString = s.extractCompartment();
				speciesContext = new SpeciesContext(new Species(speciesName, s.getName()), model.getStructure(speciesPatternCompartmentString), null);
			} else {
				speciesContext = new SpeciesContext(new Species(speciesName, s.getName()), model.getStructure(0), null);
			}
			speciesContext.setName(speciesName);
			try {
				if(speciesPatternNameString != null) {
					SpeciesPattern sp = RbmUtils.parseSpeciesPattern(speciesPatternNameString, model);
					speciesContext.setSpeciesPattern(sp);
				}
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException("Bad format for species pattern string: " + e.getMessage());
			}
//			speciesContext.setSpeciesPatternString(speciesPatternString);
			
//			model.addSpecies(speciesContext.getSpecies());
//			model.addSpeciesContext(speciesContext);
			sMap.put(speciesName, speciesContext.getSpecies());
			scMap.put(speciesName, speciesContext);
			crossMap.put(speciesName, s);
//			SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(speciesContext);
//			Parameter param = scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration);
//			param.setExpression(s.getConcentration());
//			SpeciesContext origSpeciesContext = simContext.getModel().getSpeciesContext(s.getName());
//			
//			if (origSpeciesContext!=null){
//				// execution never goes through here because we do a "continue" early in the for look
//				// when we find one of the original seed species
//				ModelEntityMapping em = new ModelEntityMapping(origSpeciesContext,speciesContext);
//				entityMappings.add(em);
//			}else{
//				ModelEntityMapping em = new ModelEntityMapping(new GeneratedSpeciesSymbolTableEntry(speciesContext),speciesContext);
//				entityMappings.add(em);
//				countGenerated++;
//			}
			if (mathMappingCallback.isInterrupted()){
				msg = "Canceled by user.";
				tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, msg);
				simContext.appendToConsole(tcm);
				throw new UserCancelException(msg);
			}
//			if(i%50 == 0) {
//				System.out.println(i+"");
//			}
//			if(i%decimalTickCount == 0) {
//				int multiplier = i/decimalTickCount;
//				float progress = progressFractionQuota/4.0f + progressFractionQuotaSpecies*multiplier;
//				mathMappingCallback.setProgressFraction(progress);
//			}
		}
		
//		System.out.println("Total generated species: " + countGenerated);
//		System.out.println("------------------------ " + sMap.size() + " species in the map.");
//		System.out.println("------------------------ " + scMap.size() + " species contexts in the map.");
//		System.out.println("------------------------ " + model.getSpecies().length + " species in the Model.");
//		System.out.println("------------------------ " + model.getSpeciesContexts().length + " species contexts in the Model.");

		for(SpeciesContext sc1 : model.getSpeciesContexts()) {
			boolean found = false;
			for (Map.Entry<String, SpeciesContext> entry : scMap.entrySet()) {
				SpeciesContext sc2 = entry.getValue();
				if(sc1.getName().equals(sc2.getName())) {
					found = true;
//					System.out.println("found species context " + sc1.getName() + " of species " + sc1.getSpecies().getCommonName() + " // " + sc2.getSpecies().getCommonName());
					break;
				}
			}
			if(found == false) {	// we add to the map the species context and the species which exist in the model but which are not in the map yet
									// the only ones in this situation should be plain species which were not given to bngl for flattening (they are flat already)
//				System.out.println("species context " + sc1.getName() + " not found in the map. Adding it.");
				scMap.put(sc1.getName(), sc1);
				sMap.put(sc1.getName(), sc1.getSpecies());
				noMapForThese.add(sc1);
			}
		}
		for(Species s1 : model.getSpecies()) {
			boolean found = false;
			for(Map.Entry<String, Species> entry : sMap.entrySet()) {
				Species s2 = entry.getValue();
				if(s1.getCommonName().equals(s2.getCommonName())) {
					found = true;
//					System.out.println("found species " + s1.getCommonName());
					break;
				}
			}
			if(found == false) {
				System.err.println("species " + s1.getCommonName() + " not found in the map!");
			}
		}
		SpeciesContext[] sca = new SpeciesContext[scMap.size()];
		scMap.values().toArray(sca);
		Species[] sa = new Species[sMap.size()];
		sMap.values().toArray(sa); 

		model.setSpecies(sa);
		model.setSpeciesContexts(sca);
		
		for(SpeciesContext sc : sca) {
			if(noMapForThese.contains(sc)) {
				continue;
			}
			SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(sc);
			Parameter param = scs.getParameter(SpeciesContextSpec.ROLE_InitialConcentration);
			BNGSpecies s = crossMap.get(sc.getName());
			param.setExpression(s.getConcentration());
			SpeciesContext origSpeciesContext = simContext.getModel().getSpeciesContext(s.getName());
			if (origSpeciesContext!=null){
				ModelEntityMapping em = new ModelEntityMapping(origSpeciesContext,sc);
				entityMappings.add(em);
			}else{
				ModelEntityMapping em = new ModelEntityMapping(new GeneratedSpeciesSymbolTableEntry(sc),sc);
				entityMappings.add(em);
			}
		}
//		for(SpeciesContext sc : sca) {		// clean all the species patterns from the flattened species, we have no sp now
//			sc.setSpeciesPattern(null);
//		}
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		msg = "Adding " + outputSpec.getBNGSpecies().length + " species to model, " + elapsedTime + " ms";
		System.out.println(msg);
		
		// ---- Reactions -----------------------------------------------------------------------------------------------------
		mathMappingCallback.setMessage("generating network: adding reactions...");
		mathMappingCallback.setProgressFraction(progressFractionQuota/4.0f*3.0f);
		startTime = System.currentTimeMillis();
		System.out.println("\nReactions :");
		
		Map<String, HashSet<String>> ruleKeyMap = new HashMap<String, HashSet<String>>();
		Map<String, BNGReaction> directBNGReactionsMap = new HashMap<String, BNGReaction>();
		Map<String, BNGReaction> reverseBNGReactionsMap = new HashMap<String, BNGReaction>();
		for (int i = 0; i < outputSpec.getBNGReactions().length; i++){
			BNGReaction r = outputSpec.getBNGReactions()[i];
			if(!r.isRuleReversed()) {		// direct
				directBNGReactionsMap.put(r.getKey(), r);
			} else {
				reverseBNGReactionsMap.put(r.getKey(), r);
			}
			//
			// for each rule name, store set of keySets (number of unique keysets are number of generated reactions from this ruleName).
			//
			HashSet<String> keySet = ruleKeyMap.get(r.getRuleName());
			if (keySet == null){
				keySet = new HashSet<String>();
				ruleKeyMap.put(r.getRuleName(),keySet);
			}
			keySet.add(r.getKey());
		}
		
		Map<String, ReactionStep> reactionStepMap = new HashMap<String, ReactionStep>();
		for (int i = 0; i < outputSpec.getBNGReactions().length; i++){
			BNGReaction bngReaction = outputSpec.getBNGReactions()[i];
//			System.out.println(i+1 + ":\t\t"+ r.writeReaction());
			String baseName = bngReaction.getRuleName();
			String reactionName = null;
			HashSet<String> keySetsForThisRule = ruleKeyMap.get(bngReaction.getRuleName());
			if (keySetsForThisRule.size()==1 && model.getReactionStep(bngReaction.getRuleName()) == null && !reactionStepMap.containsKey(bngReaction.getRuleName())) {	// we can reuse the reaction rule labels
				reactionName = bngReaction.getRuleName();
			}else{
				reactionName = bngReaction.getRuleName()+"_0";
				while (true) {
					if (model.getReactionStep(reactionName) == null && !reactionStepMap.containsKey(reactionName)) {	// we can reuse the reaction rule labels
						break;
					}	
					reactionName = TokenMangler.getNextEnumeratedToken(reactionName);
				}
			}
			//
			// if this is a direct reaction (forward through the rule), then create the forward reaction (either reversible or irreversible)
			//
			if (directBNGReactionsMap.containsValue(bngReaction)){
				BNGReaction forwardBNGReaction = bngReaction;
				BNGReaction reverseBNGReaction = reverseBNGReactionsMap.get(bngReaction.getKey());
				ReactionRule rr = model.getRbmModelContainer().getReactionRule(bngReaction.extractRuleName());
				Structure structure = rr.getStructure();
				boolean bReversible = reverseBNGReaction != null;
				SimpleReaction sr = new SimpleReaction(model, structure, reactionName, bReversible);
				for (int j = 0; j < forwardBNGReaction.getReactants().length; j++){
					BNGSpecies s = forwardBNGReaction.getReactants()[j];
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
				for (int j = 0; j < forwardBNGReaction.getProducts().length; j++){
					BNGSpecies s = forwardBNGReaction.getProducts()[j];
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
				MassActionKinetics targetKinetics = new MassActionKinetics(sr);
				sr.setKinetics(targetKinetics);
				KineticsParameter kforward = targetKinetics.getForwardRateParameter();
				applyKineticsExpressions(forwardBNGReaction, kforward, targetKinetics);
				if (reverseBNGReaction!=null){
					KineticsParameter kReverse = targetKinetics.getReverseRateParameter();
					applyKineticsExpressions(reverseBNGReaction, kReverse, targetKinetics);
				}
	//			String fieldParameterName = kforward.getName();
	//			fieldParameterName += "_" + r.getRuleName();
	//			kforward.setName(fieldParameterName);
				reactionStepMap.put(reactionName, sr);
			} else if (reverseBNGReactionsMap.containsValue(bngReaction) && !directBNGReactionsMap.containsKey(bngReaction.getKey())){
				// reverse only (must be irreversible)
				BNGReaction reverseBNGReaction = reverseBNGReactionsMap.get(bngReaction.getKey());
				ReactionRule rr = model.getRbmModelContainer().getReactionRule(reverseBNGReaction.extractRuleName());
				Structure structure = rr.getStructure();		
				boolean bReversible = false;
				SimpleReaction sr = new SimpleReaction(model, structure, reactionName, bReversible);
				for (int j = 0; j < reverseBNGReaction.getReactants().length; j++){
					BNGSpecies s = reverseBNGReaction.getReactants()[j];
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
				for (int j = 0; j < reverseBNGReaction.getProducts().length; j++){
					BNGSpecies s = reverseBNGReaction.getProducts()[j];
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
				applyKineticsExpressions(reverseBNGReaction, kforward, k);
	//			String fieldParameterName = kforward.getName();
	//			fieldParameterName += "_" + r.getRuleName();
	//			kforward.setName(fieldParameterName);
				reactionStepMap.put(reactionName, sr);
			}
		}
		for(ReactionStep rs : model.getReactionSteps()) {
			reactionStepMap.put(rs.getName(), rs);
		}
		ReactionStep[] reactionSteps = new ReactionStep[reactionStepMap.size()];
		reactionStepMap.values().toArray(reactionSteps); 
		model.setReactionSteps(reactionSteps);
		if (mathMappingCallback.isInterrupted()){
			msg = "Canceled by user.";
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, msg);
			simContext.appendToConsole(tcm);
			throw new UserCancelException(msg);
		}
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		msg = "Adding " + outputSpec.getBNGReactions().length + " reactions to model, " + elapsedTime + " ms";
		System.out.println(msg);
		// clean all the reaction rules
		model.getRbmModelContainer().getReactionRuleList().clear();

		// ---- Observables -------------------------------------------------------------------------------------------------
		mathMappingCallback.setMessage("generating network: adding observables...");
		mathMappingCallback.setProgressFraction(progressFractionQuota/8.0f*7.0f);
		startTime = System.currentTimeMillis();
		System.out.println("\nObservables :");
		for (int i = 0; i < outputSpec.getObservableGroups().length; i++){
			ObservableGroup o = outputSpec.getObservableGroups()[i];
//			System.out.println(i+1 + ":\t\t" + o.toString());
			
			if(model.getRbmModelContainer().getParameter(o.getObservableGroupName()) != null) {
				System.out.println("   ...already exists.");
				continue;		// if it's already there we don't try to add it again; this should be true for all of them!
			}
			ArrayList<Expression> terms = new ArrayList<Expression>();
			for (int j=0; j<o.getListofSpecies().length; j++){
				Expression term = Expression.mult(new Expression(o.getSpeciesMultiplicity()[j]),new Expression(speciesMap.get(o.getListofSpecies()[j].getNetworkFileIndex())));
				terms.add(term);
			}
			Expression exp = Expression.add(terms.toArray(new Expression[terms.size()]));
			exp.bindExpression(model.getRbmModelContainer().getSymbolTable());
			RbmObservable originalObservable = model.getRbmModelContainer().getObservable(o.getObservableGroupName());
			VCUnitDefinition observableUnitDefinition = originalObservable.getUnitDefinition();
			model.getRbmModelContainer().removeObservable(originalObservable);
			Parameter newParameter = model.getRbmModelContainer().addParameter(o.getObservableGroupName(), exp, observableUnitDefinition);

			RbmObservable origObservable = simContext.getModel().getRbmModelContainer().getObservable(o.getObservableGroupName());
			ModelEntityMapping em = new ModelEntityMapping(origObservable,newParameter);
			entityMappings.add(em);
		}
		if (mathMappingCallback.isInterrupted()){
			msg = "Canceled by user.";
			tcm = new TaskCallbackMessage(TaskCallbackStatus.Error, msg);
			simContext.appendToConsole(tcm);
			throw new UserCancelException(msg);
		}
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		msg = "Adding " + outputSpec.getObservableGroups().length + " observables to model, " + elapsedTime + " ms";
		System.out.println(msg);

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
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex.getMessage());
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		
		System.out.println("Done transforming");		
		msg = "Generating math...";
		System.out.println(msg);
		mathMappingCallback.setMessage(msg);
		mathMappingCallback.setProgressFraction(progressFractionQuota);
	}

//	private Expression substituteFakeParameters(Expression paramExpression) throws ExpressionException {
//		Expression newExp = new Expression(paramExpression);
//		String[] fakeSymbols = paramExpression.getSymbols();
//		for (String fakeSymbol : fakeSymbols){
//			FakeReactionRuleRateParameter fakeParameter = FakeReactionRuleRateParameter.fromString(fakeSymbol);
//			if (fakeParameter == null){
//				throw new RuntimeException("unexpected identifier "+fakeSymbol+" in kinetic law during network generation");
//			}
//			LocalParameter localParameter = this.kineticsParameterMap.get(fakeParameter);
//			Expression ruleExpression = localParameter.getExpression();
//			newExp.substituteInPlace(new Expression(fakeSymbol), new Expression(ruleExpression));
//		}
//		return newExp;
//	}

	private Expression applyKineticsExpressions(BNGReaction bngReaction, KineticsParameter rateParameter, MassActionKinetics targetKinetics) throws ExpressionException {
		ReactionRule reactionRule = null;
		Expression paramExpression = bngReaction.getParamExpression();
		Expression newExp = new Expression(paramExpression);
		String[] fakeSymbols = paramExpression.getSymbols();
		for (String fakeSymbol : fakeSymbols){
			FakeReactionRuleRateParameter fakeParameter = FakeReactionRuleRateParameter.fromString(fakeSymbol);
			if (fakeParameter == null){
				throw new RuntimeException("unexpected identifier "+fakeSymbol+" in kinetic law during network generation");
			}
			LocalParameter localParameter = this.kineticsParameterMap.get(fakeParameter);
			System.out.println(localParameter.getNameScope());
			if (localParameter.getNameScope() instanceof ReactionRule.ReactionRuleNameScope){
				reactionRule = ((ReactionRule.ReactionRuleNameScope)localParameter.getNameScope()).getReactionRule();
			}
			
			Expression ruleExpression = localParameter.getExpression();
			newExp.substituteInPlace(new Expression(fakeSymbol), new Expression(ruleExpression));
		}
		//
		// set substituted expression into new kinetics (should automatically create zero-valued user-defined parameters as needed)
		//
		try {
			targetKinetics.setParameterValue(rateParameter, newExp);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to set kinetics expression for reaction "+targetKinetics.getReactionStep().getName()+": "+e.getMessage(),e);
		}
		//
		// if there were any user-defined parameters in the ReactionRule, then set their values here on the new Kinetics.
		//
		if (reactionRule!=null){
			// try to set values from user-defined parameters into the target kinetics
			for (LocalParameter localParameter : reactionRule.getKineticLaw().getLocalParameters()){
				if (localParameter.getRole() == RbmKineticLawParameterType.UserDefined){
					KineticsParameter userDefinedParam = targetKinetics.getKineticsParameter(localParameter.getName());
					if (userDefinedParam!=null){
						try {
							targetKinetics.setParameterValue(userDefinedParam, localParameter.getExpression());
						} catch (PropertyVetoException e) {
							e.printStackTrace();
							throw new RuntimeException("failed to set kinetics expression for reaction "+targetKinetics.getReactionStep().getName()+": "+e.getMessage(),e);
						}
					}
				}
			}
		}
		return newExp;
	}

}
