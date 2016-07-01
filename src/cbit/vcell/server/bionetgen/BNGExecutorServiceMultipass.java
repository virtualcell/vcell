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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

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
import cbit.vcell.bionetgen.BNGMultiStateSpecies;
import cbit.vcell.bionetgen.BNGOutputFileParser;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.bionetgen.BNGSpeciesComponent;
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
import cbit.vcell.parser.ExpressionBindingException;

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

	private Model model;							// model identical with the original, created from the compartmental bngl file
	private SimulationContext simContext;
	
	private Map <String, Set<String>> anchorsMap;
	

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
	public BNGOutput executeBNG() throws BNGException, ParseException, PropertyVetoException, ExpressionBindingException {
		this.startTime = System.currentTimeMillis();
		long elt = 0;	// elapsed time in doWork
		
		String cBngInputString = cBngInput.getInputString();
		
		String anchorsString = extractAnchors(cBngInputString);
		anchorsMap = parseAnchors(anchorsString);
		
		// the "trick" - the modified molecules, species, etc
		// everything has an extra Site with the compartments as possible States
		// and yet another site that we use to certify the compartment validity 
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
			long st = System.currentTimeMillis();
			CorrectedSR correctedSR = doWork(oldSeedSpeciesString, sBngOutputString);
			long et = System.currentTimeMillis();
			elt += et - st;
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
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println("Done " + i + " Iterations in " + (int)(elapsedTime/1000.0) + " s.");
		System.out.println("out of which " + (int)(elt/1000.0) + " s was spent in 'doWork'.");
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
				List<Integer> reactionProductPositions = r.findProductPositions(s);	// array list with position of this species in the product (may be more than 1)
				if(reactionProductPositions.isEmpty()) {
					continue;			// this species is not a product of this reaction, nothing to do
				}
				
				String ruleName = r.getRuleName();
				if(r.isRuleReversed()) {
					throw new RuntimeException("Reaction " + ruleName + " cannot be 'reverse'.");
				}
//				System.out.println("Species " + s + " found in rule " + r.getRuleName() + ", at index " + position);
			
				// check the product against the rule to see if it's valid
				// sanity check: only "transport" rules can give incorrect products, any rule with all participants in the same
				//   compartment should only give valid products (is that so?)
				String structureName = findRuleProductCompartment(s);		// this is the structure where the product should be, according to the rule
				ReactionRule rr = model.getRbmModelContainer().getReactionRule(ruleName);
				
				// TODO: the code below may be greatly simplified using the more advanced BNGSpecies classes instead of using the strings
				// 'one' is the list of all the compartments mentioned in this product
				// 'two' is the BNGSpecies string with the compartment info extracted (that is, the AAA sites extracted)
				Pair<List<String>, String> pair = RbmUtils.extractCompartment(s.getName());
				Map<String, Set<String>> speciesAnchorMap = extractSpeciesAnchorMap(s);			// ex of one entry: key T,  value  mem, cyt
				boolean needsRepairing = false;
				if(pair.one.size() > 1) {
//					System.out.println(s.getName() + " multiple compartments, needs repairing.");
					message += s.getName() + " needs repairing... ";
					needsRepairing = true;
					
				/*	At this point we have the 'probable' structure of this product, extracted from the rule (structureName)
					However, we need to verify against the lists of anchors to make sure is not excluded from the possible list of anchors
					for any of the molecules and choose a compatible structure name, as follows:

					compartments x y z
					molecules A B C		A comes as transport
					rule says that this product belongs in x
					cases:
					1. A~y	 xB.xC.yA ->  yB.yC.yA	- can't use x as the rule says because A must be anchored to y and that takes precedence
													- if B or C can't be in y throw exception, our anchors are too restrictive

					2. A~x~y xB.xC.yA ->  xB.xC.xA	- correct to what the rule wants since x is acceptable as anchor for A

					3. A~z	 xB.xC.yA ->  !!!		- impossible, there is no reactant such that A can get in y (throw exception)
					4. A~x~z xB.xC.yA ->  !!!		- same as above

					
					step 1. look for exceptions: make sets with the compartments for each molecule in the species and see if 
							they all are allowed by the anchor rules (if not, we are in cases 3 or 4 - throw exception
					step 2. see if structureName is acceptable for all anchored molecules - if yes, use it
					step 3. intersect all sets from step 1
						 3.1 - if there's 1 compartment that is acceptable for all, use it
						 3.2 - if there's no compartment then we can't put the species anywhere - throw exception
						 3.3 - if there's more than 1 compartment, it's ambiguous - throw exception
				*/
//					anchorsMap
					
					// sanity check, step 1
					for (Map.Entry<String, Set<String>> entry : speciesAnchorMap.entrySet()) {
					    String molecule = entry.getKey();				// molecule in the newly generated species (product)
				    	if(!anchorsMap.containsKey(molecule)) {
				    		continue;	// this molecule can be anywhere, it's not anchored - nothing to verify
				    	}
					    Set<String> structures = entry.getValue();		// 1 or more location where we try to put it (before correction may be more than 1)
						// these structures MUST all be allowed by the anchor rule, otherwise it would mean they are coming from a reactant that cannot exist (case 3 or 4 above)
					    for(String wantedStructure : structures) {
					    	Set<String> allowedStructures = anchorsMap.get(molecule);
					    	if(!allowedStructures.contains(wantedStructure)) {
					    		// should never happen: no reactant and no rule should have placed this molecule in this structure
					    		throw new RuntimeException("Error in " + s.getName() + "\nMolecule " + molecule + " cannot possibly be in Structure " + wantedStructure);
					    	}
					    }
					}
					// step 2
					boolean isCandidateStructureAcceptable = true;
					for (Map.Entry<String, Set<String>> entry : speciesAnchorMap.entrySet()) {
					    String molecule = entry.getKey();				// molecule in the newly generated species (product)
				    	if(!anchorsMap.containsKey(molecule)) {
				    		continue;	// this molecule can be anywhere, it's not anchored - nothing to verify
				    	}
					    Set<String> allowedStructures = anchorsMap.get(molecule);
				    	if(!allowedStructures.contains(structureName)) {
				    		// the structure 'wanted' by the rule for this product is not acceptable because of some anchor rule
				    		isCandidateStructureAcceptable = false;
				    		break;
				    	}
					}
					// step 3
					if(isCandidateStructureAcceptable == false) {	// we try to find one and only one structure that would satisfy all anchored molecules of this species
						TreeSet<String> intersection = new TreeSet<>();
						for(Structure str : model.getStructures()) {
							intersection.add(str.getName());		// we start by adding all structures in the model
						}
						for (Map.Entry<String, Set<String>> entry : speciesAnchorMap.entrySet()) {
						    String molecule = entry.getKey();			// molecule in the newly generated species (product)
					    	if(!anchorsMap.containsKey(molecule)) {
					    		continue;	// this molecule can be anywhere, it's not anchored - nothing to verify
					    	}
						    Set<String> structures = entry.getValue();	// we already know from step 1 that all structures here are acceptable by the anchor
						    intersection.retainAll(structures);			// intersection retains only the elements in 'structures'
						}
						if(intersection.size() == 0) {			// no structure satisfies the anchor rules for the molecules in this species
							throw new RuntimeException("Error in " + s.getName() + "\nThe Structures allowed for anchored molecules are mutually exclusive.");
						} else if(intersection.size() > 1) {	// ambiguous, don't know which compartment to pick
							throw new RuntimeException("Error in " + s.getName() + "\nMultiple Structures allowed by the anchor rules, don't know which to choose.");
						} else {								// found one single structure everybody is happy about
							structureName = intersection.first();
						}
					}
				} else {		// no transport caused by a 'wild card', just direct rule application (no compartment ambiguity, nothing to correct)
					String structure = pair.one.get(0);
					if(!structure.equals(structureName)) {
						// This should never happen, if just one structure is present it must come directly from 
						// the rule (no transport caused by '?' is possible)
						throw new RuntimeException("Error in " + s.getName() + "\nIf one single structure is present in the species it must match the structure of the rule product");
					}
					// product structure (from rule) should not conflict with the anchor - we should have error issue in the rule itself reporting the conflict
					// TODO: check here too anyway, just in case?
				}
				
				BNGSpecies candidate;	// new generated species that we may add the list of species for the next iteration
				// we use firstAvailableIndex because the index in the species s may be already out of order because of species
				// deleted or added previously during earlier iterations through newly created species
				if(needsRepairing) {
				// if not valid, correct the fake "compartment" site to be conform to the compartment of 
				// the product pattern
					String speciesRepairedName = RbmUtils.repairCompartment(s.getName(), structureName);
					speciesRepairedName = RbmUtils.resetProductIndex(speciesRepairedName);
					candidate = new BNGComplexSpecies(speciesRepairedName, s.getConcentration(), firstAvailableIndex);
//					System.out.println(candidate.getName() + " repaired!");
					message += "repaired from rule " + rr.getDisplayName() + "... ";
					summaryRepaired++;
					System.out.println("");
				} else {
					String speciesName = s.getName();
					speciesName = RbmUtils.resetProductIndex(speciesName);
					candidate = new BNGComplexSpecies(speciesName, s.getConcentration(), firstAvailableIndex);
				}
				
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
					for(int reactionProductPosition : reactionProductPositions) {
						r.getProducts()[reactionProductPosition] = candidate;		// correct the reaction
					}
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
					for(int reactionProductPosition : reactionProductPositions) {
						r.getProducts()[reactionProductPosition] = existingMatch;		// correct the reaction
					}
					
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
	private static String findRuleProductCompartment(BNGSpecies s) {

		boolean needsCorrection = false;
		Set<String> structureNameSet = new TreeSet<>();		// the structures we encounter in this species (use tree set to avoid duplicates)
		List<BNGSpecies> simpleSpeciesList = new ArrayList<>();	// if it's a complex species we break it down in simple ones and populate this list 
		if(s instanceof BNGComplexSpecies) {
			simpleSpeciesList.addAll(Arrays.asList(s.parseBNGSpeciesName()));
		} else {
			simpleSpeciesList.add(s);		// if it's a simple species to begin with we'll only have one element in this list
		}
		for(BNGSpecies simpleSpecies : simpleSpeciesList) {
			// must be multi state (actually multi-site!), we have at least 2 components (sites): RbmUtils.SiteStruct and RbmUtils.SiteProduct
			if(!(simpleSpecies instanceof BNGMultiStateSpecies)) {
				throw new RuntimeException("Species " + s.getName() + " must be instance of BNGMultiStateSpecies");
			}
			BNGMultiStateSpecies mss = (BNGMultiStateSpecies)simpleSpecies;
//			System.out.println("  " + mss.toString());
			List<BNGSpeciesComponent> componentsList = new ArrayList<>(Arrays.asList(mss.getComponents()));
			String structName = "";
			String prodPosition = "";
			for(BNGSpeciesComponent sc : componentsList) {
//				System.out.println("     " + sc.getComponentName() + "~" + sc.getCurrentState());
				if(sc.getComponentName().equals(RbmUtils.SiteStruct)) {
					structName = sc.getCurrentState();
				} else if(sc.getComponentName().equals(RbmUtils.SiteProduct)) {
					prodPosition = sc.getCurrentState();
				}
			}
			if(structName == null || structName.isEmpty() || structName.equals("null")) {
				throw new RuntimeException("Structure name site missing from BNGSpecies " + mss);
			}
			if(prodPosition == null || prodPosition.isEmpty() || prodPosition.equals("null")) {
				throw new RuntimeException("Index of original rule product site missing from BNGSpecies " + mss);
			}
			switch(prodPosition) {
			case "0":				// comes from matching a wildcard with a seed species, its compartment may be wrong
				break;
			case "1":				// comes by directly applying a rule, this is the correct compartment inherited from the rule product
				structureNameSet.add(structName);
				break;
			default:
				throw new RuntimeException("Product position must be 0 or 1 for " + mss.getName());
			}
		}
		if(structureNameSet.size() != 1) {
			throw new RuntimeException("The number of structures for " + s.getName() + " must be exactly 1.");
		}

		String structName = structureNameSet.iterator().next();
		return structName;
	}
	
	private String prepareObservableRun(String inputString) {
		String outputString = "";
		
		// we add a fake molecular type and a fake reaction rule
		String pattern = "end molecule types";
		String prologue = inputString.substring(0, inputString.indexOf(pattern));
		String epilogue = inputString.substring(inputString.indexOf(pattern));
		outputString = prologue + "AAAAAA(" + RbmUtils.SiteStruct + "~x~y," +  RbmUtils.SiteProduct + "~0~1)\n" + epilogue;
		
		pattern = "end seed species";
		prologue = outputString.substring(0, outputString.indexOf(pattern));
		epilogue = outputString.substring(outputString.indexOf(pattern));
		outputString = prologue + "AAAAAA(" + RbmUtils.SiteStruct + "~x," + RbmUtils.SiteProduct + "~0) 0.0\n" + epilogue;
		
		pattern = "begin reaction rules";
		prologue = outputString.substring(0, outputString.indexOf(pattern)+pattern.length());
		pattern = "end reaction rules";
		epilogue = outputString.substring(outputString.indexOf(pattern));
		outputString = prologue + "\nr0: AAAAAA(" + RbmUtils.SiteStruct + "~x," + RbmUtils.SiteProduct + "~0) <-> AAAAAA(" + RbmUtils.SiteStruct + "~y," + RbmUtils.SiteProduct + "~1) 1,1\n" + epilogue;
		return outputString;
	}
	private String extractCorrectedObservablesAsString(BNGOutput sBngOutput) {
		String input = sBngOutput.getNetFileContent();
		String pattern = "begin groups";
		final int indexOfBeginGroups = input.indexOf(pattern);
		if(indexOfBeginGroups == -1) {		// no observable group
			return null;
		}
		String observablesString = input.substring(indexOfBeginGroups+pattern.length());
		pattern = "end groups";
		final int indexOfEndGroups = observablesString.indexOf(pattern);
		observablesString = observablesString.substring(0, indexOfEndGroups);
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
	private String preprocessInput(String cBngInputString) throws ParseException, PropertyVetoException, ExpressionBindingException {
		
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
		
		// replace all reversible rules with 2 direct rules
		List<ReactionRule> newRRList = new ArrayList<>();
		for(ReactionRule rr : model.getRbmModelContainer().getReactionRuleList()) {
			if(rr.isReversible()) {
				ReactionRule rr1 = ReactionRule.deriveDirectRule(rr);
				newRRList.add(rr1);
				ReactionRule rr2 = ReactionRule.deriveInverseRule(rr);
				newRRList.add(rr2);
			} else {
				newRRList.add(rr);
			}
			model.getRbmModelContainer().removeReactionRule(rr);
		}
//		model.getRbmModelContainer().getReactionRuleList().clear();
		model.getRbmModelContainer().setReactionRules(newRRList);
				
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
	
	private String extractAnchors(String s) {
		// extract the species from the output
		boolean inAnchorsBlock = false;
		int index = 1;
		boolean foundBlock = false;
		StringBuilder rawAnchors = new StringBuilder();
		StringTokenizer st = new StringTokenizer(s, "\n");
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();

			if(nextToken.equals("begin anchors")) {
				inAnchorsBlock = true;
				foundBlock = true;
				continue;
			} else if(nextToken.equals("end anchors")) {
				inAnchorsBlock = false;
				break;		// nothing more to do, we got all our species
			}
			if(inAnchorsBlock == false) {
				continue;
			}
			// inside anchors block;
			rawAnchors.append(nextToken);
			rawAnchors.append("\n");
			index++;
		}
		String rawAnchorsString = rawAnchors.toString();
		if(foundBlock) {
			return rawAnchorsString;
		} else {
			return null;
		}
	}
	public Map <String, Set<String>> parseAnchors(String inputString) {
		Map <String, Set<String>> anchorsMap = new HashMap<>();
		if(inputString == null || inputString.isEmpty()) {
			return anchorsMap;
		}
		StringTokenizer st = new StringTokenizer(inputString, "\n");
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();
			StringTokenizer lineTokenizer = new StringTokenizer(nextToken, "(,)");
			String molecule = lineTokenizer.nextToken();
			Set<String> structures = new HashSet<>();
			while (lineTokenizer.hasMoreTokens()) {
				String structure = lineTokenizer.nextToken();
				structures.add(structure);
			}
			anchorsMap.put(molecule, structures);
		}
		return anchorsMap;
	}
	public Map <String, Set<String>> extractSpeciesAnchorMap(BNGSpecies species) {
		Map <String, Set<String>> anchorsMap = new HashMap<>();
		StringTokenizer st = new StringTokenizer(species.getName(), ".");			// T(AAA~mem,AAB~1,c!1).T(AAA~cyt,AAB~1,c!1)
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();
			StringTokenizer lineTokenizer = new StringTokenizer(nextToken, "(,)");
			String molecule = lineTokenizer.nextToken();
			String structureSite = lineTokenizer.nextToken();						// AAA~cyt
			String structure = structureSite.substring(structureSite.indexOf("~")+1);
			
			if(anchorsMap.containsKey(molecule)) {
				Set<String> structures = anchorsMap.get(molecule);
				structures.add(structure);
				anchorsMap.put(molecule, structures);
			} else {
				Set<String> structures = new HashSet<>();
				structures.add(structure);
				anchorsMap.put(molecule, structures);			// molecule: T   structures:  mem, cyt
			}
		}
		return anchorsMap;
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

