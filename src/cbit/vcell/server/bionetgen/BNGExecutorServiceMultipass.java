package cbit.vcell.server.bionetgen;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.vcell.model.bngl.ASTModel;
import org.vcell.model.bngl.ParseException;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.RbmUtils.BnglObjectConstructionVisitor;
import org.vcell.util.Pair;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.bionetgen.BNGComplexSpecies;
import cbit.vcell.bionetgen.BNGOutputFileParser;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.client.ClientRequestManager.BngUnitSystem;
import cbit.vcell.client.ClientRequestManager.BngUnitSystem.BngUnitOrigin;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.model.Model;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.Structure;

public class BNGExecutorServiceMultipass implements BNGExecutorService, BioNetGenUpdaterCallback {

	private class CorrectedSR {	// corrected species and reactions, at the end of each iteration
		
		public CorrectedSR(List<BNGSpecies> speciesList, ArrayList<BNGReaction> reactionsList) {
			this.speciesList = speciesList;
			this.reactionsList = reactionsList;
		}
		List<BNGSpecies> speciesList;
		List<BNGReaction> reactionsList;
	}
	
	private final BNGInput cBngInput;	// compartmental .bng input file
	
	private final Long timeoutDurationMS;
	private transient List<BioNetGenUpdaterCallback> callbacks = null;
	private boolean bStopped = false;
	private BNGExecutorServiceNative onepassBngService;
	private long startTime = -1;
	
	private int previousIterationTotalSpecies = 0;	// TaskCallbackProcessor needs these to compute if the number of iterations is sufficient
	private int currentIterationTotalSpecies = 0;
	private int needAdjustMaxMolecules = 0;			// TaskCallbackProcessor needs this to see if the number of molecules per species is sufficient

	private Model model;
	private SimulationContext simContext;
	

	BNGExecutorServiceMultipass(BNGInput cBngInput, Long timeoutDurationMS) {
		this.cBngInput = cBngInput;
		this.timeoutDurationMS = timeoutDurationMS;
	}
	
	@Override
	public void registerBngUpdaterCallback(BioNetGenUpdaterCallback callbackOwner) {
		getCallbacks().add(callbackOwner);
	}

	@Override
	public List<BioNetGenUpdaterCallback> getCallbacks() {
		if(callbacks == null) {
			callbacks = new ArrayList<BioNetGenUpdaterCallback>();
		}
		return callbacks;
	}

	@Override
	public BNGOutput executeBNG() throws BNGException, ParseException, PropertyVetoException {
		this.startTime = System.currentTimeMillis();
		
		String cBngInputString = cBngInput.getInputString();
		
		// the "trick" - the modified molecules, species, etc - everything has an extra Site 
		// with the compartments as possible States
		String sBngInputString = preprocessInput(cBngInputString);
		BNGInput sBngInput = new BNGInput(sBngInputString);
		BNGOutput sBngOutput = null;		// output after each iteration
		String sBngOutputString = null;
		String oldSeedSpeciesString;				// the seedSpecies which were given as input for the current iteration
		String correctedReactionsString = null;		// the BNGReactions as they are at the end of the current iteration, after corrections of the species
		
		oldSeedSpeciesString = extractOriginalSeedSpecies(sBngInputString);		// before the first iteration we show the original seed species
		consoleNotification("======= Original Seed Species ===========================\n" + oldSeedSpeciesString);

		NetworkConstraints nc = simContext.getNetworkConstraints();
		int i;		// iterations counter
		for (i = 0; i<nc.getMaxIteration(); i++) {
		
			correctedReactionsString = null;
			this.onepassBngService = new BNGExecutorServiceNative(sBngInput, timeoutDurationMS);
			this.onepassBngService.registerBngUpdaterCallback(this);	// we are the only callback for the native service, acting as middleman
			
			sBngOutput = this.onepassBngService.executeBNG();
			this.onepassBngService = null;
		
			sBngOutputString = sBngOutput.getNetFileContent();		// .net file
			String delta = "";
			String rawSeedSpeciesString = "";

			// this bloc is dealing with the strings, used for console display only
			rawSeedSpeciesString = extractSeedSpecies(sBngOutputString);
			dump("dumpIteration" + (i+1) + ".txt", rawSeedSpeciesString);
			delta = rawSeedSpeciesString.substring(oldSeedSpeciesString.length());
			String rs = extractReactions(sBngOutputString);
			String s = "  --- Iteration " + (i+1) + " ---------------------------";
//			s += "\n" + delta;
//			s += "---------------------------------------\n" + rs;
			consoleNotification(s);

			CorrectedSR correctedSR = doWork(oldSeedSpeciesString, sBngOutputString);
			String correctedSeedSpeciesString = extractCorrectedSeedSpeciesAsString(correctedSR);
			correctedReactionsString = extractCorrectedReactionsAsString(correctedSR);
			oldSeedSpeciesString += correctedSeedSpeciesString;

			if(correctedSR.speciesList.isEmpty()) {
				// if the current iteration didn't provide any VALID NEW species (after correction) then we are done
				break;
			}
			
			sBngInputString = prepareNewBnglString(sBngInputString, oldSeedSpeciesString);
			sBngInput = new BNGInput(sBngInputString);		// the new input for next iteration
		}
		// run one more iteration with all the seed species calculated above and with one single fake 
		// rule (so that no new seed species will be created), to properly compute the observables
	
		String obsInputString = prepareObservableRun(sBngInputString);	// the ObservableGroups
		BNGInput obsBngInput = new BNGInput(obsInputString);
		this.onepassBngService = new BNGExecutorServiceNative(obsBngInput, timeoutDurationMS);
		this.onepassBngService.registerBngUpdaterCallback(this);
		BNGOutput obsBngOutput = this.onepassBngService.executeBNG();
		String correctedObservablesString = extractCorrectedObservablesAsString(obsBngOutput);
		
		// oldSeedSpeciesString contains the final list of seed species
		sBngOutput.insertEntitiesInNetFile(oldSeedSpeciesString, "species");
		sBngOutput.insertEntitiesInNetFile(correctedReactionsString, "reactions");
		sBngOutput.insertEntitiesInNetFile(correctedObservablesString, "groups");
		// analyze the sBnglOutput, strip the fake "compartment" site and produce the proper cBnglOutput
		sBngOutput.extractCompartmentsFromNetFile();	// converts the net file inside sBngOutput
		BNGOutput cBngOutput = sBngOutput;
//		String cBngOutputString = cBngOutput.getNetFileContent();
//		System.out.println(cBngOutputString);
		return cBngOutput;		// we basically return the "corrected" bng output from the last iteration run
	}
	// -------------------------------------------------------------------------------------------------------------------------------------------------

	private CorrectedSR doWork(String oldSpeciesString, String newNetFile) throws ParseException {

		// TODO: make a map to preserve the ancestry of each generated species (rule and iteration that generated it)
		// each species may be generated multiple times, by different rules, at different iterations
		// the key should be the normalized (sorted lexicographically) expression of each species
		
		// parse the .net file with BNGOutputFileParser
		List<BNGSpecies> oldSpeciesList = BNGOutputFileParser.createBngSpeciesOutputSpec(oldSpeciesString);	// seed species at the beginning of the current iteration
		BNGOutputSpec workSpec = BNGOutputFileParser.createBngOutputSpec(newNetFile);		// .net file content generated during current iteration
		List<BNGSpecies> newSpeciesList = new ArrayList<>();		// we build here the list of valid (perhaps even corrected) NEW species
		
		// we build here the list of flattened reactions (corrected at need, if the species were corrected)
		ArrayList<BNGReaction> newReactionsList = new ArrayList<BNGReaction>(Arrays.asList(workSpec.getBNGReactions()));
//		ArrayList<ObservableGroup> newObservablesList = new ArrayList<ObservableGroup>();		// here we put all the observables after correction
//		ArrayList<ObservableGroup> newObservablesList = new ArrayList<ObservableGroup>(Arrays.asList(workSpec.getObservableGroups()));
				
		// identify new products, loop thorough each of them
		Pair<List<BNGSpecies>, List<BNGSpecies>> p = BNGSpecies.diff(oldSpeciesList, workSpec.getBNGSpecies());
		List<BNGSpecies> removed = p.one;
		List<BNGSpecies> added = p.two;		// after possible needed corrections we may end up with more or less new species for this iteration
		if(!removed.isEmpty()) {
			// this may actually be fired if the BNGSpecies.equals() fails for some reason
			throw new RuntimeException("No existing BNGSpecies may be removed: \n" + removed.get(0));
		}
		if(added.isEmpty()) {
			System.out.println("No new species added this iteration. Finished!");
		}
		
		// in this map we store which of the indexes of species generated during the current iteration 
		// were converted into what after correction
		// an index may exist already in which case we won't save a duplicate or the same new species (that needs repair)
		// may result in more new species after being fixed
		// param1: original index of the newly created species before repairing it
		// param2: list of the indexes of the species resulting from the original after repairing
		Map<String, List<String>> indexesMap = new LinkedHashMap<>();

		// as we validate and we add new species, we use this index to set their network index
		// we can't get the indexes given to the newly generated species for granted, during correction we may 
		// lose or gain species
		int firstAvailableIndex = BNGOutputSpec.getFirstAvailableSpeciesIndex(oldSpeciesList);
		
		int summaryCreated = added.size();
		int summaryRepaired = 0;
		int summaryExisted = 0;
		int summarySpecies = 0;			// species we're adding at the end of this iteration, after repairing and checking for existing
		for(BNGSpecies s : added) {
		
		// find the flattened reaction and from the reaction find the rule it's coming from
			for(BNGReaction r : newReactionsList) {
				
				String message = "";						// console only message
				int position = r.findProductPosition(s);
				if(position == -1) {
					continue;	// this species is not a product of this reaction, nothing to do
				}
				
				String ruleName = r.getRuleName();
				boolean isReversed = r.isRuleReversed();	// if the rule is reversed we need to search this species in the reactants!!
//				System.out.println("Species " + s + " found in rule " + r.getRuleName() + ", at index " + position);
			
				// check the product against the rule to see if it's valid
				// sanity check: only "transport" rules can give incorrect products, any rule with all participants in the same
				//   compartment should only give valid products (is that so?)
				ReactionRule rr = model.getRbmModelContainer().getReactionRule(ruleName);
				String structureNameFromRule;
				if(isReversed) {
					final ReactantPattern reactantPattern = rr.getReactantPattern(position);
					structureNameFromRule = reactantPattern.getStructure().getName();
				} else {
					final ProductPattern productPattern = rr.getProductPattern(position);
					structureNameFromRule = productPattern.getStructure().getName();
				}
				
				Pair<List<String>, String> pair = RbmUtils.extractCompartment(s.getName());
				boolean needsRepairing = false;
				if(pair.one.size() > 1) {
//					System.out.println(s.getName() + " multiple compartments, needs repairing.");
					message += s.getName() + " needs repairing... ";
					needsRepairing = true;
				} else {
					String structure = pair.one.get(0);
					if(!structure.equals(structureNameFromRule)) {
//						System.out.println(s.getName() + " single compartment, needs repairing.");
						message += s.getName() + " needs repairing... ";
						needsRepairing = true;
					}
				}
				
				BNGSpecies candidate;	// new generated species that we may add the list of species for the next iteration
				// we use firstAvailableIndex because the index in the species s may be already out of order because of species
				// deleted or added previously during earlier iterations through newly created species
				if(needsRepairing) {
				// if not valid, correct the fake "compartment" site to be conform to the compartment of 
				// the product pattern
					String speciesRepairedName = RbmUtils.repairCompartment(s.getName(), structureNameFromRule);
					candidate = new BNGComplexSpecies(speciesRepairedName, s.getConcentration(), firstAvailableIndex);
//					System.out.println(candidate.getName() + " repaired!");
					message += "repaired from rule " + rr.getDisplayName() + "... ";
					summaryRepaired++;
					System.out.println("");
				} else {
					candidate = new BNGComplexSpecies(s.getName(), s.getConcentration(), firstAvailableIndex);
				}
				//
				// At this point we have a valid candidate - but we may not need it if it already exist in the list of old seed species or in the list of
				// new seed species we're building now (it may exist in either if correction took place)
				//
				// We correct the reaction to match the networkFileIndex of this species (useless activity except for the last iteration 
				//    when we want the valid list of flattened reactions 
				//
				BNGSpecies existingMatchInNew = BNGOutputSpec.findMatch(candidate, newSpeciesList);
				BNGSpecies existingMatchInOld = BNGOutputSpec.findMatch(candidate, oldSpeciesList);
				if(existingMatchInNew != null && existingMatchInOld != null) {
					throw new RuntimeException("The new 'candidate' species cannot exist in both the list of existing seed species and in the new one we're building");
				}
				// if it doesn't exist we add it to the list of seed species we are preparing for the next iteration
				if(existingMatchInNew == null && existingMatchInOld == null) {
					newSpeciesList.add(candidate);
					message += "Candidate " + candidate.getName() + " added to the seed species list.";
					summarySpecies++;
					firstAvailableIndex++;
					r.getProducts()[position] = candidate;		// correct the reaction
					
					manageIndexesMap(indexesMap, s, candidate);
					
				} else {
					message += "Candidate " + candidate.getName() + " already exists, not added.";
					BNGSpecies existingMatch;		// at this point we know for sure that there's one and only one match
					if(existingMatchInNew != null) {
						existingMatch = existingMatchInNew;
					} else {
						existingMatch = existingMatchInOld;
					}
					summaryExisted++;
					r.getProducts()[position] = existingMatch;	// correct the reaction
					
					manageIndexesMap(indexesMap, s, existingMatch);
				}
//				if(!message.isEmpty()) {
//					consoleNotification(message);
//				}
			}		// end checking reactions for this species
		}			// end all new species
		
//		System.out.println("------------- Finished checking newly generated species for this iteration. Summary:");
		System.out.println("   Added " + newSpeciesList.size() + " new species");
		System.out.println(" ");
		
		String summary = "   " + summaryCreated + " species were created.\n";
		summary += "   " + summaryRepaired + " species needed repairing.\n";
		summary += "   " + summaryExisted + " species already present, not added to the seed species list.\n";
		summary += "   " + summarySpecies + " new species added.\n";
		consoleNotification(summary);
		
		currentIterationTotalSpecies = previousIterationTotalSpecies + summarySpecies;	// total number of species at this moment
		String message = previousIterationTotalSpecies + "," + currentIterationTotalSpecies + "," + needAdjustMaxMolecules;
		TaskCallbackMessage newCallbackMessage = new TaskCallbackMessage(TaskCallbackStatus.AdjustAllFlags, message);
		broadcastCallbackMessage(newCallbackMessage);
		
		previousIterationTotalSpecies = currentIterationTotalSpecies;
		CorrectedSR sr = new CorrectedSR(newSpeciesList, newReactionsList);
		return sr;
	}
	
	private String prepareObservableRun(String inputString) {
		String outputString = "";
		
		// we add a fake molecular type and a fake reaction rule
		String pattern = "end molecule types";
		String prologue = inputString.substring(0, inputString.indexOf(pattern));
		String epilogue = inputString.substring(inputString.indexOf(pattern));
		outputString = prologue + "AAAAAA(AAA~x~y)\n" + epilogue;
		
		pattern = "end seed species";
		prologue = outputString.substring(0, outputString.indexOf(pattern));
		epilogue = outputString.substring(outputString.indexOf(pattern));
		outputString = prologue + "AAAAAA(AAA~x) 0.0\n" + epilogue;
		
		pattern = "begin reaction rules";
		prologue = outputString.substring(0, outputString.indexOf(pattern)+pattern.length());
		pattern = "end reaction rules";
		epilogue = outputString.substring(outputString.indexOf(pattern));
		outputString = prologue + "\nr0: AAAAAA(AAA~x) <-> AAAAAA(AAA~y) 1,1\n" + epilogue;
		return outputString;
	}
	private String extractCorrectedObservablesAsString(BNGOutput sBngOutput) {
		String input = sBngOutput.getNetFileContent();
		
		String pattern = "begin groups";
		String observablesString = input.substring(input.indexOf(pattern)+pattern.length());
		pattern = "end groups";
		observablesString = observablesString.substring(0, observablesString.indexOf(pattern));
		return observablesString;
	}
	
	// the newly created species 'from' may exist already or it may expand after correction in multiple species
	// we manage a correspondence map to know which observable indexes need to be corrected
	private void manageIndexesMap(Map<String, List<String>> indexesMap, BNGSpecies from, BNGSpecies to) {
		String key = from.getNetworkFileIndex()+"";
		if(indexesMap.containsKey(key)) {
			List<String> list = indexesMap.get(key);
			list.add(to.getNetworkFileIndex() + "");
			indexesMap.put(key, list);
		} else {
			List<String> list = new ArrayList<>();
			list.add(to.getNetworkFileIndex()+"");
			indexesMap.put(key, list);
		}
	}
	
	private static String extractCorrectedSeedSpeciesAsString(CorrectedSR corrected) {
		String correctedSeedSpeciesString = "";
		for(BNGSpecies s : corrected.speciesList) {
			correctedSeedSpeciesString += s.toBnglString() + "\n";
		}
		return correctedSeedSpeciesString;
	}
	private static String extractCorrectedReactionsAsString(CorrectedSR corrected) {
		String correctedReactionsString = "";
		for(int i=0; i<corrected.reactionsList.size(); i++) {
			BNGReaction r = corrected.reactionsList.get(i);
			correctedReactionsString += (i+1) + " " + r.toBnglString() + "\n";
		}
		return correctedReactionsString;
	}

	@Override
	public void stopBNG() throws Exception {
		if (onepassBngService!=null){
			onepassBngService.stopBNG();
		}
		this.bStopped = true;
	}
	@Override
	public boolean isStopped() {
		return bStopped;
	}
	@Override
	public long getStartTime() {
		return startTime;
	}
	
	// parse the compartmental bngl file and produce the "trick"
	// where each molecule has an extra Site with the compartments as possible States
	// a reserved name will be used for this Site
	//
	private String preprocessInput(String cBngInputString) throws ParseException, PropertyVetoException {
		
		// take the cBNGL file (as string), parse it to recover the rules (we'll need them later)
		// and create the bngl string with the extra, fake site for the compartments
		BioModel bioModel = new BioModel(null);
		bioModel.setName("BngBioModel");
		model = new Model("model");
		bioModel.setModel(model);
		model.createFeature();

		simContext = bioModel.addNewSimulationContext("BioNetGen app", SimulationContext.Application.NETWORK_DETERMINISTIC);
		List<SimulationContext> appList = new ArrayList<SimulationContext>();
		appList.add(simContext);
		// set convention for initial conditions in generated application for seed species (concentration or count)
		BngUnitSystem bngUnitSystem = new BngUnitSystem(BngUnitOrigin.DEFAULT);

		InputStream is = new ByteArrayInputStream(cBngInputString.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		ASTModel astModel = RbmUtils.importBnglFile(br);
		if(astModel.hasCompartments()) {
			Structure struct = model.getStructure(0);
			if(struct != null) {
				try {
					model.removeStructure(struct);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			}
		}
		BnglObjectConstructionVisitor constructionVisitor = null;
		constructionVisitor = new BnglObjectConstructionVisitor(model, appList, bngUnitSystem, true);
		astModel.jjtAccept(constructionVisitor, model.getRbmModelContainer());
		
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(bnglStringWriter);
		writer.println(RbmNetworkGenerator.BEGIN_MODEL);
		writer.println();
//		RbmNetworkGenerator.writeCompartments(writer, model, null);
		RbmNetworkGenerator.writeParameters(writer, model.getRbmModelContainer(), false);
		RbmNetworkGenerator.writeMolecularTypes(writer, model, CompartmentMode.asSite);
		RbmNetworkGenerator.writeSpeciesSortedAlphabetically(writer, model, simContext, CompartmentMode.asSite);
		RbmNetworkGenerator.writeObservables(writer, model.getRbmModelContainer(), CompartmentMode.asSite);
//		RbmNetworkGenerator.writeFunctions(writer, rbmModelContainer, ignoreFunctions);
		RbmNetworkGenerator.writeReactions(writer, model.getRbmModelContainer(), null, false, CompartmentMode.asSite);
		
		writer.println(RbmNetworkGenerator.END_MODEL);	
		writer.println();
		
		// we parse the real numbers from the bngl file provided by the caller, the nc in the simContext has the default ones
		NetworkConstraints realNC = extractNetworkConstraints(cBngInputString);
		simContext.getNetworkConstraints().setMaxMoleculesPerSpecies(realNC.getMaxMoleculesPerSpecies());
		simContext.getNetworkConstraints().setMaxIteration(realNC.getMaxIteration());
		RbmNetworkGenerator.generateNetworkEx(1, simContext.getNetworkConstraints().getMaxMoleculesPerSpecies(), writer, model.getRbmModelContainer(), simContext, NetworkGenerationRequirements.AllowTruncatedStandardTimeout);

		String sInputString = bnglStringWriter.toString();
		return sInputString;
	}
	
	private static NetworkConstraints extractNetworkConstraints(String cBngInputString) {
		NetworkConstraints nc = new NetworkConstraints();
		
		String s1 = cBngInputString.substring(cBngInputString.indexOf("max_iter=>") + "max_iter=>".length());
		s1 = s1.substring(0, s1.indexOf(","));
		int maxi = Integer.parseInt(s1);
		nc.setMaxIteration(maxi);
		
		String s2 = cBngInputString.substring(cBngInputString.indexOf("max_agg=>") + "max_agg=>".length());
		s2 = s2.substring(0, s2.indexOf(","));
		int maxa = Integer.parseInt(s2);
		nc.setMaxMoleculesPerSpecies(maxa);

		return nc;
	}

	
	// output - the resulting .net file we received from bngl.exe
	//  we extract the seed species from the .net file,insert them in the .bngl file
	//  in preparation for the next round
	//
	private static String extractSeedSpecies(String output) {
		// extract the species from the output
		boolean inSpeciesBlock = false;
		StringBuilder rawSeedSpecies = new StringBuilder();
		StringTokenizer st = new StringTokenizer(output, "\n");
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();

			if(nextToken.equals("begin species")) {
				inSpeciesBlock = true;
				continue;
			} else if(nextToken.equals("end species")) {
				inSpeciesBlock = false;
				break;		// nothing more to do, we got all our species
			}
			if(inSpeciesBlock == false) {
				continue;
			}
			// inside species block;
			rawSeedSpecies.append(nextToken);
			rawSeedSpecies.append("\n");
		}
		String rawSeedSpeciesString = rawSeedSpecies.toString();
//		System.out.println(rawSeedSpeciesString);
		return rawSeedSpeciesString;
	}
	// used only initially to extract and display the original seed species
	private String extractOriginalSeedSpecies(String s) {
		// extract the species from the output
		boolean inSpeciesBlock = false;
		int index = 1;
		StringBuilder rawSeedSpecies = new StringBuilder();
		StringTokenizer st = new StringTokenizer(s, "\n");
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();

			if(nextToken.equals("begin seed species")) {
				inSpeciesBlock = true;
				continue;
			} else if(nextToken.equals("end seed species")) {
				inSpeciesBlock = false;
				break;		// nothing more to do, we got all our species
			}
			if(inSpeciesBlock == false) {
				continue;
			}
			// inside species block;
//			rawSeedSpecies.append(index + " ");
			rawSeedSpecies.append(nextToken);
			rawSeedSpecies.append("\n");
			
			previousIterationTotalSpecies++;
			currentIterationTotalSpecies++;
			index++;
		}
		String rawSeedSpeciesString = rawSeedSpecies.toString();
		return rawSeedSpeciesString;
	}

	private static String extractReactions(String output) {
		// extract the species from the output
		boolean inReactionsBlock = false;
		StringBuilder rb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(output, "\n");
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();

			if(nextToken.equals("begin reactions")) {
				inReactionsBlock = true;
				continue;
			} else if(nextToken.equals("end reactions")) {
				inReactionsBlock = false;
				break;		// nothing more to do, we got all our species
			}
			if(inReactionsBlock == false) {
				continue;
			}
			// inside species block;
			rb.append(nextToken);
			rb.append("\n");
		}
		String rawReactionsString = rb.toString();
//		System.out.println(rawReactionsString);
		return rawReactionsString;
	}
	
	// sBngInputStringOld - .the bngl file we used for the current iteration (which we just finished)
	// insert the new set of seed species in a new string, otherwise identical
	// inside the species block in preparation for the next iteration
	//
	private static String prepareNewBnglString(String sBngInputStringOld, String seedSpecies) {
		String prologue = sBngInputStringOld.substring(0, sBngInputStringOld.indexOf("begin seed species"));
		String epilogue = sBngInputStringOld.substring(sBngInputStringOld.indexOf("end seed species"));
		
		String sBngInputStringNew = prologue;
		sBngInputStringNew += "begin seed species\n";
		sBngInputStringNew += seedSpecies;
		sBngInputStringNew += epilogue;
		return sBngInputStringNew;
	}
	
	private static void dump(String fileName, String text) {
//		String debugUser = PropertyLoader.getProperty("debug.user", "not_defined");
//		if (debugUser.equals("danv") || debugUser.equals("mblinov"))
//		{
//			String fullPath = "c:\\TEMP\\dump\\"+fileName;
//			System.out.println("Dumping to file: " + fullPath);
//			RulebasedTransformer.saveAsText(fullPath, text);
//		}
	}

	// ------------------------------------------------------------------------------- BioNetGenUpdaterCallback stuff -----
	private void broadcastCallbackMessage(TaskCallbackMessage newCallbackMessage) {
		for (BioNetGenUpdaterCallback callback : getCallbacks()){
			callback.setNewCallbackMessage(newCallbackMessage);
		}
	}
	private void consoleNotification(String message) {
		for (BioNetGenUpdaterCallback callback : getCallbacks()){
			TaskCallbackMessage newCallbackMessage = new TaskCallbackMessage(TaskCallbackStatus.Notification, message);
			callback.setNewCallbackMessage(newCallbackMessage);
		}
	}

	
	@Override
	public void updateBioNetGenOutput(BNGOutputSpec outputSpec) {
		System.out.println(" === updateBioNetGenOutput - called");
	}
	@Override
	public void setNewCallbackMessage(TaskCallbackMessage newCallbackMessage) {
//		System.out.println(" === setNewCallbackMessage - called, command: " + newCallbackMessage.getStatus().name() + ", message: " + newCallbackMessage.getText().substring(0, 25));
		if(newCallbackMessage.getText().contains("WARNING: maximal length of aggregate is reached in reaction")) {

			needAdjustMaxMolecules = 1;		// set the "insufficient molecules per species" flag here
		}
	}
	@Override
	public boolean isInterrupted() {
		System.out.println(" === isInterrupted - called");
		return false;
	}
}

//ASTModel astModel;
//try {
//	astModel = RbmUtils.importBnglFile(new StringReader(cBngInputString));
//} catch (ParseException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//	return "";
//}
//
//System.out.println(astModel.toBNGL());
// -------------------------------------------------------------------------------------------
/*
begin seed species
EGFR(ecd,tmd,y1068~u,y1173~u)		unique_id_used_as_fake_parameter_R
EGF(rb)		unique_id_used_as_fake_parameter_L
Grb2(sh2,sos)		unique_id_used_as_fake_parameter_Grb2
Shc(sh3,Y773~u)		unique_id_used_as_fake_parameter_Shc
Grb2(sh2,sos!1).Sos(site!1)		unique_id_used_as_fake_parameter_Grb2_Sos
end seed species
*/

