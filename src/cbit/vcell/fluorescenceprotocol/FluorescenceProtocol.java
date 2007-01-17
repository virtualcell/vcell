package cbit.vcell.fluorescenceprotocol;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import cbit.vcell.server.DataAccessException;
import java.io.PrintWriter;
import java.util.Random;
import java.io.File;
import cbit.vcell.model.Model;
import cbit.vcell.parser.Expression;
import cbit.vcell.model.ReactionParticipant;
import java.util.Vector;
import cbit.vcell.model.Structure;
import java.util.Hashtable;
import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.bionetgen.BNGMultiStateSpecies;
import cbit.vcell.bionetgen.BNGComplexSpecies;
import cbit.vcell.bionetgen.BNGSingleStateSpecies;
import cbit.vcell.bionetgen.BNGParameter;
import cbit.vcell.bionetgen.BNGReactionRule;
import cbit.vcell.bionetgen.ObservableRule;
import cbit.vcell.bionetgen.BNGInputFileGenerator;
import cbit.vcell.bionetgen.BNGOutputFileParser;
import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.bionetgen.BNGMolecule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.bionetgen.BNGInputSpec;
import cbit.vcell.model.Species;
import cbit.vcell.model.ReactionStep;
import cbit.util.BeanUtils;
import java.io.FileOutputStream;
import cbit.vcell.server.PropertyLoader;
import java.io.FileInputStream;

/**
 * Insert the type's description here.
 * Creation date: (3/15/2006 3:29:56 PM)
 * @author: Anuradha Lakshminarayana
 */
public class FluorescenceProtocol implements cbit.vcell.server.bionetgen.BNGService {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private FluorescenceProtocolSpec fieldFluorescentProtocolSpec = null;
/**
 * VCellBNGProtocol constructor comment.
 */
public FluorescenceProtocol() {
	super();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/** 
	Checks if a reaction rule that has a reactant which is a multistate species has a corresponding multistate species product.
	If not, return false, which will be thrown as an exception in the calling method.
**/
private boolean checkReactionValidity(ReactionStep reactionStep) {
	return true;
}
/** 
	The species not in the list of initial species need to have a corresponding speciesContext in order to 
	construct a VCell output model and reaction network. This method creates a speciesContext for such bngSpecies. 
	Pass as arguments the BNGSpecies that requires a new speciesContext and the orginal speciesContext Hashtable.
**/
private SpeciesContext createSpeciesContextForNewBngSpecies(BNGSpecies bngSpecies, Hashtable spHash, boolean bIndicator) {
	SpeciesContext newSpeciesContext = null;
	Structure speciesStructure = getFluorescentProtocolSpec().getInputModel().getStructures(0);
	if (bngSpecies instanceof BNGMultiStateSpecies) {
		if (bIndicator) {
			// Fluorescent indicator protocol chosen
			BNGSpecies[] msSpecies = bngSpecies.parseBNGSpeciesName();
			newSpeciesContext = new SpeciesContext(new Species(msSpecies[0].getName(), msSpecies[0].getName()), speciesStructure);
		} else {
			// Fluorescent label protocol chosen
			java.util.Iterator speciesIterator = spHash.keySet().iterator();
			while (speciesIterator.hasNext()) {
				SpeciesContext spHashKey = (SpeciesContext)speciesIterator.next();
				BNGSpecies spHashValue = (BNGSpecies)spHash.get(spHashKey);
				if (spHashValue instanceof BNGMultiStateSpecies && (spHashValue.getName().indexOf("*") > -1)) {
					int starIndx = spHashValue.getName().indexOf("*");
					String hashStr = spHashValue.getName().substring(0, starIndx);
					String bngSpeciesStr = bngSpecies.getName().substring(0, starIndx);
					if (bngSpeciesStr.equals(hashStr)) {
						String suffixStr = "";
						if (bngSpecies.getName().substring(starIndx, starIndx+1).equals("0")) {
							suffixStr = "_Fl";
						} else if (bngSpecies.getName().substring(starIndx, starIndx+1).equals("1")) {
							suffixStr = "_Bl";
						}
						String newSpeciesName = spHashKey.getSpecies().getCommonName()+suffixStr;
						Species newSpecies = new Species(newSpeciesName, newSpeciesName);
						newSpeciesContext = new SpeciesContext(newSpecies, speciesStructure);
					}
				} 
			}
			if (newSpeciesContext == null) {
				BNGSpecies[] msSpecies = bngSpecies.parseBNGSpeciesName();
				newSpeciesContext = new SpeciesContext(new Species(msSpecies[0].getName(), msSpecies[0].getName()), speciesStructure);
			}
		}
	} else if (bngSpecies instanceof BNGComplexSpecies) {
		//// If bngSpecies is a complexSpecies, it will have a '.' separator.
		//// Need to verify if any one or all parts of the complex species exists in the original list of species.
		//// Use the convenience method in BNGSpecies to parse the bngSpecies name into its 'components'.
		//// Check if the individual parts of the complex species are present in the original list.
		//// If so, use the name of the speciesContext from that list, else use the name of the bngSpecies.
		//// Combine all the names of the complexSpecies components using the '_' separator.
		//// Create a new speciesContext using the combined name.
		//String complexSpeciesName = "";
		//BNGSpecies[] complexSpeciesComponents = bngSpecies.parseBNGSpeciesName();
		//for (int i = 0; i < complexSpeciesComponents.length; i++){
			//BNGSpecies speciesComponent = (BNGSpecies)complexSpeciesComponents[i];
			//newSpeciesContext = getSpeciesContextFromBNGSpecies(speciesComponent, spHash);
			//if (newSpeciesContext != null) {
				//if (i == 0) {
					//// First name doesn't have separator!
					//complexSpeciesName = newSpeciesContext.getName();
				//} else {
					//complexSpeciesName = complexSpeciesName + "__" + newSpeciesContext.getSpecies().getCommonName();
				//}
			//} else {
				//if (i == 0) {
					//// First name doesn't have separator!
					//complexSpeciesName = speciesComponent.getName();
				//} else {
					//complexSpeciesName = complexSpeciesName + "__" + speciesComponent.getName();
				//}
			//}
		//}
		//complexSpeciesName = cbit.util.TokenMangler.fixToken(complexSpeciesName);
		//// Form a speciesContext for the bngComplexSpecies
		//newSpeciesContext = new SpeciesContext(new Species(complexSpeciesName, complexSpeciesName), speciesStructure);
	}
	
	return newSpeciesContext;
}
/**
	Execute BNG
**/
public cbit.vcell.server.bionetgen.BNGOutput executeBNG(BNGInput bngInput) {
	//
	// create an input file for the BNG in the BNG directory
	// create the perl command to run BioNetGenerator on the input file to generate the net file and any other options (say SBML file).
	//

	String prefix = "vcell_bng_";
	java.io.File workingDir = new java.io.File("C:\\");
	File tempDir = null; 	//TempDirectory(prefix, workingDir);

	int  counter = new Random().nextInt() & 0xffff;
	tempDir = new File(workingDir, prefix + Integer.toString(counter));
	if (!tempDir.exists()) {
		tempDir.mkdir();
	}
	
	File bngInputFile = null;
	FileOutputStream fos = null;
	String suffix = ".bngl";
	try {
		bngInputFile = File.createTempFile(prefix, suffix, tempDir);
		fos = new java.io.FileOutputStream(bngInputFile);
	}catch (java.io.IOException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("error opening input file '"+bngInputFile.getName()+": "+e.getMessage());
	}	
		
	PrintWriter inputFile = new PrintWriter(fos);
	inputFile.print(bngInput.getInputString());
	inputFile.close();

	try {
		PropertyLoader.loadProperties();
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not load properties from Property Loader : " + e.getMessage());
	}
 	String perlCommand = "\"" + PropertyLoader.getRequiredProperty(PropertyLoader.vcellBNGPerl) + "\" \"" 
 		+ PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.vcellBNGScript) + "\" \"" + bngInputFile.getAbsolutePath() + "\"";

 	cbit.vcell.server.bionetgen.BNGOutput bngOutput = null;
 	cbit.util.Executable executable = null;
	try {
		System.out.println("-------------Starting BNG ...-------------------------------");
		executable = new cbit.util.Executable(perlCommand);
		executable.start();

		File[] files = tempDir.listFiles();
		String[] filenames = new String[files.length];
		String[] filecontents = new String[files.length];

		for (int i = 0; i < files.length; i ++) {
			filenames[i] = files[i].getName();
			filecontents[i] = getFileContentFromFileName(files[i]);
			files[i].delete();
		}
		tempDir.delete();
		
		bngOutput = new cbit.vcell.server.bionetgen.BNGOutput(executable.getStdoutString(), filenames, filecontents);
	
		System.out.println("--------------Finished BNG----------------------------");
	} catch (cbit.util.ExecutableException e) {
		e.printStackTrace(System.out);		
		throw new RuntimeException(executable.getStderrString());
	} catch (Exception e) {
		e.printStackTrace(System.out);		
		throw new RuntimeException("Failed running BioNetGen: " + e.getMessage());
	} 

	return bngOutput;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Comment
 */
public void fluorescentIndicatorProtocol() throws cbit.vcell.parser.ExpressionException {
	try {
		if (!(getFluorescentProtocolSpec() instanceof FluorescenceIndicatorProtocolSpec)) {
			throw new RuntimeException("Wrong protocol selected : please select labeling protocol.");
		}
		FluorescenceIndicatorProtocolSpec flIndicatorProtocol = (FluorescenceIndicatorProtocolSpec)getFluorescentProtocolSpec();
		//
		// get convenient lists from model.
		//
		// **** GET THE PROPER STRUCTURE - SHOULDN'T BE FIRST STRUCTURE IN MODEL ****
		Model flModel = flIndicatorProtocol.getInputModel();
		Structure inputStructure = flModel.getStructures(0);
		ReactionStep[] reactionSteps = flModel.getReactionSteps();

		// get all the speciesContexts and selectedSpeciesContexts and indicator from ProtocolSpec - to get the bng Initial set of species
		SpeciesContext[] speciesContexts = flModel.getSpeciesContexts();
		SpeciesContext[] selectedSCs = flIndicatorProtocol.getSelectedSpeciesContexts();
		SpeciesContext indicatorSC = flIndicatorProtocol.getIndicatorSpeciesContex();
		//
		// name all species, 
		//     a) those that were "selected" (in selectedSpeciesHashset) are MultiState and are enumerated as COMPLEX(i)
		//     b) others are SingleState with their original name (speciesContext.getname())
		//
		// reactions will then be:
		//
		//     COMPLEX(i) + species_j ==> COMPLEX(k)
		//
		//     so the first "binding site" dictates who is bound to it.
		//

		Vector bngParamsList = new Vector();
		Vector initialSpeciesList = new Vector();
		Vector bngMoleculeTypesList = new Vector();
		Vector selectedSpeciesContextsVector = new Vector();
		for (int i = 0; i < selectedSCs.length; i++){
			selectedSpeciesContextsVector.addElement(selectedSCs[i]);
		}
		java.util.Hashtable speciesHash = new java.util.Hashtable();
		
		// Create BNGSpecies for the selected and unselected species in inputModel. The indicator will appear as a regular species.
		// create BNG molecules	(allowed components and states of the selected and unselected speciesContexts for the bleaching reaction
		for (int i = 0; i < speciesContexts.length; i++){
			String speciesName = speciesContexts[i].getName();
			if (!selectedSpeciesContextsVector.contains(speciesContexts[i])){
				// If speciesContext is not selected, treat it as a singlestate species; add it to the initial species seed set and speciesHash
				BNGSpecies ssSpecies = new BNGSingleStateSpecies(speciesName,new Expression(speciesName+"_tot"),-1);
				// Use the 'AugmentedSpeciesContext' as key to the speciesHash
				AugmentedSpeciesContext augSpContext = new AugmentedSpeciesContext(speciesContexts[i], null, false, false);
				speciesHash.put(augSpContext,ssSpecies);
				// add it to initialSpeciesList
				initialSpeciesList.add(ssSpecies);
				// Add init condition (conc) as param
				bngParamsList.add(new BNGParameter(speciesName+"_tot", 50));
				// add allowable molecule - in this case, since species is not selected, just the species name will do.
				bngMoleculeTypesList.add(new BNGMolecule(speciesName));
			} else {
				// If it is selected in the reaction network, the speciesContexts with the fluorescent label component is added to the speciesHash
				// Here, we are designating only one component called 'label' to the species - this represents the fluorescent label;
				// The 2 states of this component are 'none' for no fluorescence and 'F' for fluorescence.

				// For the initial set of species, the fluorescent labelled and "bleached" species are required
				// Add the fluorescent labelled species
				String flSpeciesName = speciesName + "(label~F)";
				BNGMultiStateSpecies FlSpecies = new BNGMultiStateSpecies(flSpeciesName, new Expression(speciesName+"_flu"), -1);
				// Use the 'AugmentedSpeciesContext' as key to the speciesHash
				AugmentedSpeciesContext augSpContext = new AugmentedSpeciesContext(speciesContexts[i], indicatorSC, true, true);
				speciesHash.put(augSpContext, FlSpecies);
				// add it to initialSpeciesList
				initialSpeciesList.add(FlSpecies);
				// Add init condition (conc) as param
				bngParamsList.add(new BNGParameter(speciesName+"_flu", 50));

				// Add the "bleached" species to initial list.
				String nonFlSpeciesName = speciesName + "(label~none)";
				BNGMultiStateSpecies nonFlSpecies = new BNGMultiStateSpecies(nonFlSpeciesName, new Expression(speciesName+"_fre"), -1);
				// Use the 'AugmentedSpeciesContext' as key to the speciesHash
				augSpContext = new AugmentedSpeciesContext(speciesContexts[i], null, false, true);
				speciesHash.put(augSpContext,nonFlSpecies);
				// add it to initialSpeciesList
				initialSpeciesList.add(nonFlSpecies);
				// Add init condition (conc) as param
				bngParamsList.add(new BNGParameter(speciesName+"_fre", 50));

				// For selected species, add allowable molecule.
				// In the fluorescent label case, the species has one component, which can be labelled (F) or bleached (none) - 2 states.
				bngMoleculeTypesList.add(new BNGMolecule(speciesName + "(label~none~F)"));
			}
		}

		// Create the BNGSpecies (initial seed set of species)
		BNGSpecies[] bngInitialSpecies = (BNGSpecies[])BeanUtils.getArray(initialSpeciesList,BNGSpecies.class);

		// Create the BNGMolecules (allowable molecule patterns for the generates species as the network is generated).
		BNGMolecule[] bngMoleculeTypes = (BNGMolecule[])BeanUtils.getArray(bngMoleculeTypesList, BNGMolecule.class);

		// collect reaction rules and parameters from the reactions in the VCell input
		Vector bngReactionRuleList = new Vector();
		for (int i = 0; i < reactionSteps.length; i++){
			cbit.vcell.model.Kinetics kinetics = reactionSteps[i].getKinetics();
			BNGParameter[] params = null;
			if (kinetics instanceof cbit.vcell.model.MassActionKinetics){
				cbit.vcell.model.MassActionKinetics maKinetics = (cbit.vcell.model.MassActionKinetics)kinetics;
				BNGParameter kf = new BNGParameter(maKinetics.getForwardRateParameter().getName(),maKinetics.getForwardRateParameter().getExpression().evaluateConstant());
				BNGParameter kr = new BNGParameter(maKinetics.getReverseRateParameter().getName(),maKinetics.getReverseRateParameter().getExpression().evaluateConstant());
				bngParamsList.add(kf);
				bngParamsList.add(kr);
				params = new BNGParameter[] { kf, kr };
			}else{
				System.out.println("kinetic type '"+kinetics.getKineticsDescription().getName()+"' not supported");
				BNGParameter kf = new BNGParameter(reactionSteps[i].getName()+"_kf",1.0);
				BNGParameter kr = new BNGParameter(reactionSteps[i].getName()+"_kr",1.0);
				bngParamsList.add(kf);
				bngParamsList.add(kr);
				params = new BNGParameter[] { kf, kr };
			}
			Vector reactantList = new Vector();
			Vector productList = new Vector();
			boolean bReactantsSelected = false;
			boolean bProductsSelected = false;
			cbit.vcell.model.ReactionParticipant[] reactionParticipants = reactionSteps[i].getReactionParticipants();
			for (int j = 0; j < reactionParticipants.length; j++){
				if (reactionParticipants[j] instanceof cbit.vcell.model.Reactant){
					BNGSpecies reactantBngSp = getReactionParticipantBNGSpeciesFromSpeciesHash(reactionParticipants[j].getSpeciesContext(), speciesHash);
					reactantList.add(reactantBngSp);
					if (selectedSpeciesContextsVector.contains(reactionParticipants[j].getSpeciesContext())) {
						bReactantsSelected = true;
					}
				}else if (reactionParticipants[j] instanceof cbit.vcell.model.Product){
					BNGSpecies productBngSp = getReactionParticipantBNGSpeciesFromSpeciesHash(reactionParticipants[j].getSpeciesContext(), speciesHash);
					productList.add(productBngSp);
					if (selectedSpeciesContextsVector.contains(reactionParticipants[j].getSpeciesContext())) {
						bProductsSelected = true;
					}					
				}else if (reactionParticipants[j] instanceof cbit.vcell.model.Catalyst){
					throw new RuntimeException("can't handle modifiers for now ... probably not too bad in the future");
				}
			}

			// If a reaction has a reactant labeled, it should have a product also labeled; or vice versa. If not, throw an exception.
			if (bReactantsSelected && !bProductsSelected) {
				throw new RuntimeException("Reaction \""+reactionSteps[i].getName()+"\" has a reactant labeled, but no product labeled - NOT allowed");
			}
			if (!bReactantsSelected && bProductsSelected) {
				throw new RuntimeException("Reaction \""+reactionSteps[i].getName()+"\" has NO reactant labeled, but a product labeled - NOT allowed");
			}
			
			BNGSpecies[] reactants = (BNGSpecies[])BeanUtils.getArray(reactantList,BNGSpecies.class);
			BNGSpecies[] products = (BNGSpecies[])BeanUtils.getArray(productList,BNGSpecies.class);
			boolean bReversible = true;
			BNGReactionRule bngReactionRule = new BNGReactionRule(params,reactants,products,bReversible);
			bngReactionRuleList.add(bngReactionRule);
		}

		//
		// Add the flourescence and bleaching reaction rule (considered reversible) and parameter for the reaction. 
		//		sp(label~none) + I <-> sp(label~F)
		//
		// Find the BNGSpecies corresponding to indicator in the initialSpeciesList
		BNGSingleStateSpecies indicatorBngSp = null;
		for (int j = 0; j < initialSpeciesList.size(); j++) {
			BNGSpecies bngSp = (BNGSpecies)initialSpeciesList.elementAt(j);
			// IndicatorSpeciesContext will be a BNGSingleStateSpecies
			if (bngSp instanceof BNGSingleStateSpecies && bngSp.getName().equals(indicatorSC.getName())) {
				indicatorBngSp = (BNGSingleStateSpecies)bngSp;
				break;
			}
		}
		
		for (int i = 0; i < selectedSCs.length; i++){
			String flSpeciesName = selectedSCs[i].getName() + "(label~F)";
			String blSpeciesName = selectedSCs[i].getName() + "(label~none)";
			BNGMultiStateSpecies flmsSpecies = null;
			BNGMultiStateSpecies blmsSpecies = null;
			// Check the initialSpeciesList to get the BNGMultistateSpecies associated with each selected speciesContext
			// (both the fluorescent and bleached versions are stored in the initSpeciesList)
			for (int j = 0; j < initialSpeciesList.size(); j++) {
				BNGSpecies bngSp = (BNGSpecies)initialSpeciesList.elementAt(j);
				if (bngSp instanceof BNGMultiStateSpecies) {
					if (bngSp.getName().equals(flSpeciesName)) {
						flmsSpecies = (BNGMultiStateSpecies)bngSp;
					} else if (bngSp.getName().equals(blSpeciesName)) {
						blmsSpecies = (BNGMultiStateSpecies)bngSp;
					}
				}
			}
			BNGSpecies[] bl_reactantSpecies = {blmsSpecies, indicatorBngSp};
			BNGSpecies[] fl_pdtSpecies = {flmsSpecies};
			BNGParameter[] fl_params = {new BNGParameter("fl_param_"+i, 1.0), new BNGParameter("bl_param_"+i, 1.0)};
			BNGReactionRule FlReactionRule = new BNGReactionRule(fl_params, bl_reactantSpecies, fl_pdtSpecies, true);
			bngReactionRuleList.add(FlReactionRule);
			for (int j = 0; j < fl_params.length; j++){
				bngParamsList.add(fl_params[j]);
			}
		}
		
		BNGParameter[] bngParams = (BNGParameter[])BeanUtils.getArray(bngParamsList,BNGParameter.class);
		BNGReactionRule[] bngReactionRules = (BNGReactionRule[])BeanUtils.getArray(bngReactionRuleList,BNGReactionRule.class);

		// define observables
		ObservableRule[] bngObservableRules = new ObservableRule[0];

		System.out.println("\n Species Hash : " + speciesHash.toString());
 
		// create spec and generate rules
		BNGInputSpec bngInputSpec = new BNGInputSpec(bngParams,bngMoleculeTypes,bngInitialSpecies,bngReactionRules,bngObservableRules);

		// run bioNetGen
		BNGOutputSpec bngOutputSpec = runBioNetGen(bngInputSpec);
		
		// Translate the BNGOutputSpec into a VCell model.
		Model outputModel = translateBNGToVCellModel(bngOutputSpec, speciesHash, inputStructure);
		outputModel.setName(flModel.getName() + "_bng");
		System.out.println("\n\n\t\t ALL DONE!\n");
	}catch (Exception e){
		e.printStackTrace(System.out);
		cbit.gui.DialogUtils.showErrorDialog(e.getMessage());
	}	
	return;
}
/**
 * Comment
 */
public void fluorescentLabelProtocol() throws cbit.vcell.parser.ExpressionException {
	try {
		if (!(getFluorescentProtocolSpec() instanceof FluorescenceLabelProtocolSpec)) {
			throw new RuntimeException("Wrong protocol selected : please select labeling protocol.");
		}
		//
		// get convenient lists from model.
		//
		// **** GET THE PROPER STRUCTURE - SHOULDN't BE FIRST STRUCTURE IN MODEL ****
		Model flModel = getFluorescentProtocolSpec().getInputModel();
		Structure inputStructure = flModel.getStructures(0);
		ReactionStep[] reactionSteps = flModel.getReactionSteps();

		// get all the speciesContexts and selectedSpeciesContexts from ProtocolSpec - to get the bng Initial set of species
		SpeciesContext[] speciesContexts = flModel.getSpeciesContexts();
		SpeciesContext[] selectedSCs = getFluorescentProtocolSpec().getSelectedSpeciesContexts();		

		//
		// name all species, 
		//     a) those that were "selected" (in selectedSpeciesHashset) are MultiState and are enumerated as COMPLEX(i,*)
		//     b) others are SingleState with their original name (speciesContext.getname())
		//
		// reactions will then be:
		//
		//     COMPLEX(i,*) + species_j ==> COMPLEX(k,*)
		//
		//     so the first "binding site" dictates who is bound to it.
		//

		Vector bngParamsList = new Vector();
		Vector initialSpeciesList = new Vector();
		Vector bngMoleculeTypesList = new Vector();
		Vector selectedSpeciesContextsVector = new Vector();
		for (int i = 0; i < selectedSCs.length; i++){
			selectedSpeciesContextsVector.addElement(selectedSCs[i]);
		}
		java.util.Hashtable speciesHash = new java.util.Hashtable();

		// Create BNGSpecies for the selected and unselected species in inputModel
		// create BNG molecules	(allowed components and states of the selected and unselected speciesContexts for the bleaching reaction
		for (int i = 0; i < speciesContexts.length; i++){
			String speciesName = speciesContexts[i].getName();
			if (!selectedSpeciesContextsVector.contains(speciesContexts[i])){
				// If speciesContext is not selected, treat it as a singlestate species; add it to the initial species seed set and speciesHash
				BNGSpecies ssSpecies = new BNGSingleStateSpecies(speciesName,new Expression(speciesName+"_tot"),-1);
				// Use the 'AugmentedSpeciesContext' as key to the speciesHash
				AugmentedSpeciesContext augSpContext = new AugmentedSpeciesContext(speciesContexts[i], null, false, false);
				speciesHash.put(augSpContext,ssSpecies);
				// add it to initialSpeciesList
				initialSpeciesList.add(ssSpecies);
				// Add init condition (conc) as param
				bngParamsList.add(new BNGParameter(speciesName+"_tot", 50));
				// add allowable molecule - in this case, since species is not selected, just the species name will do.
				bngMoleculeTypesList.add(new BNGMolecule(speciesName));
			} else {
				// If it is selected in the reaction network, the speciesContexts with the fluorescent label component is added to the speciesHash
				// Here, we are designating only one component called 'label' to the species - this represents the fluorescent label;
				// The 2 states of this component are 'none' for no fluorescence and 'F' for fluorescence.

				// For the initial set of species, the fluorescent labelled and "bleached" species are required
				// Add the fluorescent labelled species
				String flSpeciesName = speciesName + "(label~F)";
				BNGMultiStateSpecies FlSpecies = new BNGMultiStateSpecies(flSpeciesName, new Expression(speciesName+"_flu"), -1);
				// Use the 'AugmentedSpeciesContext' as key to the speciesHash
				AugmentedSpeciesContext augSpContext = new AugmentedSpeciesContext(speciesContexts[i], null, true, true);
				speciesHash.put(augSpContext, FlSpecies);
				// add it to initialSpeciesList
				initialSpeciesList.add(FlSpecies);
				// Add init condition (conc) as param
				bngParamsList.add(new BNGParameter(speciesName+"_flu", 50));

				// Add the "bleached" species to initial list.
				String nonFlSpeciesName = speciesName + "(label~none)";
				BNGMultiStateSpecies nonFlSpecies = new BNGMultiStateSpecies(nonFlSpeciesName, new Expression(speciesName+"_fre"), -1);
				// Use the 'AugmentedSpeciesContext' as key to the speciesHash
				augSpContext = new AugmentedSpeciesContext(speciesContexts[i], null, false, true);
				speciesHash.put(augSpContext,nonFlSpecies);
				// add it to initialSpeciesList
				initialSpeciesList.add(nonFlSpecies);
				// Add init condition (conc) as param
				bngParamsList.add(new BNGParameter(speciesName+"_fre", 50));

				// For selected species, add allowable molecule.
				// In the fluorescent label case, the species has one component, which can be labelled (F) or bleached (none) - 2 states.
				bngMoleculeTypesList.add(new BNGMolecule(speciesName + "(label~none~F)"));
			}
		}

		// Create the BNGSpecies (initial seed set of species)
		BNGSpecies[] bngInitialSpecies = (BNGSpecies[])BeanUtils.getArray(initialSpeciesList,BNGSpecies.class);

		// Create the BNGMolecules (allowable molecule patterns for the generates species as the network is generated).
		BNGMolecule[] bngMoleculeTypes = (BNGMolecule[])BeanUtils.getArray(bngMoleculeTypesList, BNGMolecule.class);

		// collect reaction rules and parameters from the reactions in the VCell input
		Vector bngReactionRuleList = new Vector();
		for (int i = 0; i < reactionSteps.length; i++){
			cbit.vcell.model.Kinetics kinetics = reactionSteps[i].getKinetics();
			BNGParameter[] params = null;
			if (kinetics instanceof cbit.vcell.model.MassActionKinetics){
				cbit.vcell.model.MassActionKinetics maKinetics = (cbit.vcell.model.MassActionKinetics)kinetics;
				BNGParameter kf = new BNGParameter(maKinetics.getForwardRateParameter().getName(),maKinetics.getForwardRateParameter().getExpression().evaluateConstant());
				BNGParameter kr = new BNGParameter(maKinetics.getReverseRateParameter().getName(),maKinetics.getReverseRateParameter().getExpression().evaluateConstant());
				bngParamsList.add(kf);
				bngParamsList.add(kr);
				params = new BNGParameter[] { kf, kr };
			}else{
				System.out.println("kinetic type '"+kinetics.getKineticsDescription().getName()+"' not supported");
				BNGParameter kf = new BNGParameter(reactionSteps[i].getName()+"_kf",1.0);
				BNGParameter kr = new BNGParameter(reactionSteps[i].getName()+"_kr",0.5);
				bngParamsList.add(kf);
				bngParamsList.add(kr);
				params = new BNGParameter[] { kf, kr };
			}
			Vector reactantList = new Vector();
			Vector productList = new Vector();
			boolean bReactantsSelected = false;
			boolean bProductsSelected = false;
			cbit.vcell.model.ReactionParticipant[] reactionParticipants = reactionSteps[i].getReactionParticipants();
			for (int j = 0; j < reactionParticipants.length; j++){
				if (reactionParticipants[j] instanceof cbit.vcell.model.Reactant){
					BNGSpecies reactantBngSp = getReactionParticipantBNGSpeciesFromSpeciesHash(reactionParticipants[j].getSpeciesContext(), speciesHash);
					reactantList.add(reactantBngSp);
					if (selectedSpeciesContextsVector.contains(reactionParticipants[j].getSpeciesContext())) {
						bReactantsSelected = true;
					}
				}else if (reactionParticipants[j] instanceof cbit.vcell.model.Product){
					BNGSpecies productBngSp = getReactionParticipantBNGSpeciesFromSpeciesHash(reactionParticipants[j].getSpeciesContext(), speciesHash);
					productList.add(productBngSp);
					if (selectedSpeciesContextsVector.contains(reactionParticipants[j].getSpeciesContext())) {
						bProductsSelected = true;
					}					
				}else if (reactionParticipants[j] instanceof cbit.vcell.model.Catalyst){
					throw new RuntimeException("can't handle modifiers for now ... probably not too bad in the future");
				}
			}

			// If a reaction has a reactant labeled, it should have a product also labeled; or vice versa. If not, throw an exception.
			if (bReactantsSelected && !bProductsSelected) {
				throw new RuntimeException("Reaction \""+reactionSteps[i].getName()+"\" has a reactant labeled, but no product labeled - NOT allowed");
			}
			if (!bReactantsSelected && bProductsSelected) {
				throw new RuntimeException("Reaction \""+reactionSteps[i].getName()+"\" has NO reactant labeled, but a product labeled - NOT allowed");
			}
			
			BNGSpecies[] reactants = (BNGSpecies[])BeanUtils.getArray(reactantList,BNGSpecies.class);
			BNGSpecies[] products = (BNGSpecies[])BeanUtils.getArray(productList,BNGSpecies.class);
			boolean bReversible = true;
			BNGReactionRule bngReactionRule = new BNGReactionRule(params,reactants,products,bReversible);
			bngReactionRuleList.add(bngReactionRule);
		}

		//
		// Add the bleaching reaction rule and parameter for the reaction
		//		sp(label~F) -> sp(label~none)
		//
		for (int i = 0; i < selectedSCs.length; i++){
			String flSpeciesName = selectedSCs[i].getName() + "(label~F)";
			String blSpeciesName = selectedSCs[i].getName() + "(label~none)";
			BNGMultiStateSpecies flmsSpecies = null;
			BNGMultiStateSpecies blmsSpecies = null;
			// Check the initialSpeciesList to get the BNGMultistateSpecies associated with each selected speciesContext
			// (both the fluorescent and bleached versions are stored in the initSpeciesList)
			for (int j = 0; j < initialSpeciesList.size(); j++) {
				BNGSpecies bngSp = (BNGSpecies)initialSpeciesList.elementAt(j);
				if (bngSp instanceof BNGMultiStateSpecies) {
					if (bngSp.getName().equals(flSpeciesName)) {
						flmsSpecies = (BNGMultiStateSpecies)bngSp;
					} else if (bngSp.getName().equals(blSpeciesName)) {
						blmsSpecies = (BNGMultiStateSpecies)bngSp;
					}
				}
			}
			BNGSpecies[] bl_reactantSpecies = {flmsSpecies};
			BNGSpecies[] bl_pdtSpecies = {blmsSpecies};
			BNGParameter[] bl_params = {new BNGParameter("bl_param_"+i, 1.0)};
			BNGReactionRule BlReactionRule = new BNGReactionRule(bl_params, bl_reactantSpecies, bl_pdtSpecies, false);
			bngReactionRuleList.add(BlReactionRule);
			for (int j = 0; j < bl_params.length; j++){
				bngParamsList.add(bl_params[j]);
			}
			
		}
		
		BNGParameter[] bngParams = (BNGParameter[])BeanUtils.getArray(bngParamsList,BNGParameter.class);
		BNGReactionRule[] bngReactionRules = (BNGReactionRule[])BeanUtils.getArray(bngReactionRuleList,BNGReactionRule.class);

		// define observables
		ObservableRule[] bngObservableRules = new ObservableRule[0];
		 
		// create spec and generate rules
		System.out.println("\n Species Hash : " + speciesHash.toString());
		BNGInputSpec bngInputSpec = new BNGInputSpec(bngParams,bngMoleculeTypes,bngInitialSpecies,bngReactionRules,bngObservableRules);

		// run bioNetGen
		BNGOutputSpec bngOutputSpec = runBioNetGen(bngInputSpec);

		// Translate the BNGOutputSpec into a VCell model.
		Model outputModel = translateBNGToVCellModel(bngOutputSpec, speciesHash, inputStructure);
		outputModel.setName(flModel.getName() + "_bng");

		System.out.println("\n\n\t\t ALL DONE!\n");
	}catch (Exception e){
		e.printStackTrace(System.out);
		cbit.gui.DialogUtils.showErrorDialog(e.getMessage());
	}	
}

private cbit.vcell.model.ReactionStep[] getCollapsedReactionSteps(cbit.vcell.model.ReactionStep[] reactionSteps) { 
	Vector collapsedRxnStepsVector = new Vector();

	Vector rxnStepsVector = new Vector();
	for (int i = 0; i < reactionSteps.length; i++){
		rxnStepsVector.addElement(reactionSteps[i]);
	}
	
	for (int i = 0; i < rxnStepsVector.size(); i++){
		cbit.vcell.model.ReactionStep fwdRStep = (cbit.vcell.model.ReactionStep)rxnStepsVector.elementAt(i);
		// Get the reactionParticipants and the corresponding reactants and products in an array
		ReactionParticipant[] rps = fwdRStep.getReactionParticipants();
		Vector fwdReactantsVector = new Vector();
		Vector fwdProductsVector = new Vector();
		for (int j = 0; j < rps.length; j++){
			if (rps[j] instanceof cbit.vcell.model.Reactant) {
				fwdReactantsVector.addElement(rps[j].getSpeciesContext());
			} else if (rps[j] instanceof cbit.vcell.model.Product) {
				fwdProductsVector.addElement(rps[j].getSpeciesContext());
			}
		}
		SpeciesContext[] fwdReactants = (SpeciesContext[])cbit.util.BeanUtils.getArray(fwdReactantsVector, SpeciesContext.class);
		SpeciesContext[] fwdProducts = (SpeciesContext[])cbit.util.BeanUtils.getArray(fwdProductsVector, SpeciesContext.class);

		boolean bReverseReactionFound = false;

		// Loop through all the reactions to find the corresponding reverse reaction
		for (int ii = 0; ii < reactionSteps.length; ii++){
			cbit.vcell.model.ReactionStep revRStep = reactionSteps[ii];
			// Get the reactionParticipants and the corresponding reactants and products in an array
			ReactionParticipant[] revRps = revRStep.getReactionParticipants();
			Vector revReactantsVector = new Vector();
			Vector revProductsVector = new Vector();
			for (int j = 0; j < revRps.length; j++){
				if (revRps[j] instanceof cbit.vcell.model.Reactant) {
					revReactantsVector.addElement(revRps[j].getSpeciesContext());
				} else if (revRps[j] instanceof cbit.vcell.model.Product) {
					revProductsVector.addElement(revRps[j].getSpeciesContext());
				}
			}
			SpeciesContext[] revReactants = (SpeciesContext[])cbit.util.BeanUtils.getArray(revReactantsVector, SpeciesContext.class);
			SpeciesContext[] revProducts = (SpeciesContext[])cbit.util.BeanUtils.getArray(revProductsVector, SpeciesContext.class);

			// Check if reactants of reaction in outer 'for' loop match products in inner 'for' loop and vice versa.
			if (cbit.util.BeanUtils.arrayEquals(fwdReactants, revProducts) && cbit.util.BeanUtils.arrayEquals(fwdProducts, revReactants)) {
				// Set the reverse kinetic rate expression for the reaction in outer loop with the forward rate from reactionStep in inner loop
				cbit.vcell.model.MassActionKinetics revMAKinetics = (cbit.vcell.model.MassActionKinetics)revRStep.getKinetics(); // inner 'for' loop
				cbit.vcell.model.MassActionKinetics fwdMAKinetics = (cbit.vcell.model.MassActionKinetics)fwdRStep.getKinetics();  // outer 'for' loop
				try {
					fwdMAKinetics.setParameterValue(fwdMAKinetics.getReverseRateParameter().getName(), revMAKinetics.getForwardRateParameter().getExpression().infix());
					cbit.vcell.model.Parameter param = revMAKinetics.getParameter(revMAKinetics.getForwardRateParameter().getExpression().infix());
					fwdMAKinetics.setParameterValue(param.getName(), param.getExpression().infix());
				} catch (Exception e) {
					e.printStackTrace(System.out);
					throw new RuntimeException(e.getMessage());
				}

				// Add this to the collapsedRxnStepsVector
				collapsedRxnStepsVector.addElement(fwdRStep);
				rxnStepsVector.removeElement(revRStep);
				bReverseReactionFound = true;	
				break;	
			}
		}

		// If 'bReverseReactionFound' is false after checking all reactions for the reverse, the reaction is probably an irreversible reaction
		// Add it as is to the 'collapsedRxnStepsVector'
		if (!bReverseReactionFound) {
			collapsedRxnStepsVector.addElement(fwdRStep);
		}
	}

	// Convert the vector into an array of reactionSteps and return
	cbit.vcell.model.ReactionStep[] collapsedRxnSteps = (cbit.vcell.model.ReactionStep[])cbit.util.BeanUtils.getArray(collapsedRxnStepsVector, cbit.vcell.model.ReactionStep.class);
	return collapsedRxnSteps;
}
/**
 * Insert the method's description here.
 * Creation date: (6/30/2005 5:22:16 PM)
 * @return cbit.util.BigString
 */
private String getFileContentFromFileName(File file) {
	if (!file.exists()) {
		System.out.println(file + " doesn't exists!");
		return "";
	}
	
	// Read characters from input file into character array and transfer into string buffer.
	StringBuffer stringBuffer = new StringBuffer();
	FileInputStream fis = null;
	try {
		fis = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(reader);
		char charArray[] = new char[10000];
		while (true) {
			int numRead = br.read(charArray, 0, charArray.length);
			if (numRead > 0) {
				stringBuffer.append(charArray,0,numRead);
			} else if (numRead == -1) {
				break;
			}
		}
		fis.close();
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Errors reading input file .... ");
	}
	if (stringBuffer == null || stringBuffer.length() == 0){
		System.out.println("<<<SYSOUT ALERT>>> null input file");
	}

	return stringBuffer.toString();
}
/**
 * Gets the fluorescentProtocolSpec property (bngclientserverapi.FluorescenceProtocolSpec) value.
 * @return The fluorescentProtocolSpec property value.
 * @see #setFluorescentProtocolSpec
 */
public FluorescenceProtocolSpec getFluorescentProtocolSpec() {
	return fieldFluorescentProtocolSpec;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
	getReactionParticipantBNGSpeciesFromSpeciesHash :
	Check if the reactionParticipant speciesContext is contained in the speciesHash. But speciesHash contains the augmentedSpeciesContext as
	the key. So there could be multiple augmentedSpeciesContexts that match the current reactionParticipant. When the reactionParticipant speciesContext
	matches the augmentedSpeciesXontext, check if the augmentedSpeciesContext is a selected species (bSelected flag). If it is not, return 
	the BNGSpecies associated with the augmentedSpeciesContext. If it is selected, we need to create a BNGSpecies with 'wildcard' and return.
**/
private BNGSpecies getReactionParticipantBNGSpeciesFromSpeciesHash(SpeciesContext rpSpeciesContext, Hashtable allSpeciesHash) {
	java.util.Iterator speciesIterator = allSpeciesHash.keySet().iterator();
	while (speciesIterator.hasNext()) {
		AugmentedSpeciesContext spHashKey = (AugmentedSpeciesContext)speciesIterator.next();
		if (spHashKey.getSpeciesContext().compareEqual(rpSpeciesContext)) {
			if  (!spHashKey.isSelected()) {
				// reactionParticipant is not a selected species => doesn't fluoresce.
				BNGSpecies spHashValue = (BNGSpecies)allSpeciesHash.get(spHashKey);
				return spHashValue;				
			} else {
				// reactionparticipant fluoresces, but the BNGspecies in the hashTable is specific, so create a general one and return.
				BNGSpecies spHashValue = (BNGSpecies)allSpeciesHash.get(spHashKey);
				String newSpeciesName = null;
				if (spHashValue instanceof BNGMultiStateSpecies) {
					String componentName = ((BNGMultiStateSpecies)spHashValue).getComponents()[0].getComponentName();
					int componentIndx = spHashValue.getName().indexOf(componentName);
					newSpeciesName = spHashValue.getName().substring(0, componentIndx+componentName.length()) + "%1)";
				}
				System.out.println(" RP BNGSp : " + newSpeciesName);
				BNGSpecies newBngSpecies = new BNGMultiStateSpecies(newSpeciesName, spHashValue.getConcentration(), spHashValue.getNetworkFileIndex());
				return newBngSpecies;
			}
		}
	}
	return null;
}

private AugmentedSpeciesContext getSpeciesContextFromBNGSpecies(BNGSpecies bngSpecies, java.util.Hashtable spHash) {
	java.util.Iterator speciesIterator = spHash.keySet().iterator();
	while (speciesIterator.hasNext()) {
		AugmentedSpeciesContext spHashKey = (AugmentedSpeciesContext)speciesIterator.next();
		BNGSpecies spHashValue = (BNGSpecies)spHash.get(spHashKey);
		if (bngSpecies.compareEqual(spHashValue) || (bngSpecies.getName().indexOf(spHashValue.getName()) > -1)) {
			return spHashKey;
		} 
	}
	return null;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
	Execute BNG
**/
public BNGOutputSpec runBioNetGen(BNGInputSpec argBngInputSpec) throws Exception {
	BNGInput bngInput = new BNGInput(BNGInputFileGenerator.createBNGInput(argBngInputSpec,100,100));
	System.out.println("\n\n BNGL file :\n\n" + bngInput.getInputString());
	
	// Run the BioNetGen script and get the output. (this will be displayed on the other tabs)
	if (bngInput == null) {
		throw new RuntimeException("No input; Cannot run BioNetGen");
	}

	// run bionetgen
	cbit.vcell.server.bionetgen.BNGOutput bngOutput = executeBNG(bngInput);
	
	// Now, convert BNG output to VCell output (use the speciesHash, etc. to match the names of species between BNG and VCell
	// Get reactions and species(Contexts) for the generated network from bngOutputSpec and translate it to VCell entities

	File bngOutputFile = null;
	FileOutputStream fos = null;
	File tempDir = new File("C:\\VCellBNG");
	if (!tempDir.exists()) {
		tempDir.mkdir();
	}
	try {
		bngOutputFile = File.createTempFile("vcell_bng_", ".net", tempDir);
		fos = new java.io.FileOutputStream(bngOutputFile);
	}catch (java.io.IOException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("error opening input file '"+bngOutputFile.getName()+": "+e.getMessage());
	}	
		
	PrintWriter outputFile = new PrintWriter(fos);
	outputFile.print(bngOutput.getBNGFileContent(1));
	outputFile.close();
	
	BNGOutputSpec bngOutputSpec = BNGOutputFileParser.createBngOutputSpec(bngOutputFile);
	return bngOutputSpec;
}
/**
 * Sets the fluorescentProtocolSpec property (bngclientserverapi.FluorescenceProtocolSpec) value.
 * @param fluorescentProtocolSpec The new value for the property.
 * @see #getFluorescentProtocolSpec
 */
public void setFluorescentProtocolSpec(FluorescenceProtocolSpec fluorescentProtocolSpec) {
	FluorescenceProtocolSpec oldValue = fieldFluorescentProtocolSpec;
	fieldFluorescentProtocolSpec = fluorescentProtocolSpec;
	firePropertyChange("fluorescentProtocolSpec", oldValue, fluorescentProtocolSpec);
}
/**
	Execute BNG
**/
public Model translateBNGToVCellModel(BNGOutputSpec argBngOutputSpec, Hashtable argSpeciesHash, Structure argStructure) throws Exception {
	// Lookup the output species from bngOutput in the speciesHash, if there is a match, get the corresponding speciesContext
	// for the VCell output. The remainder of the species will be represented as is (converted to a speciesContext).
	// Create an outputSpeciesHash using outputBngSpecies and outputSpeciesContexts (they should be of same length).
	// This is used later to get the reactants and products for reactions

	BNGSpecies[] outputBngSpecies = argBngOutputSpec.getBNGSpecies();
	SpeciesContext[] outputSpeciesContexts = new SpeciesContext[outputBngSpecies.length];
	java.util.Hashtable outputSpeciesHash = new Hashtable();
	for (int i = 0; i < outputBngSpecies.length; i++){
		AugmentedSpeciesContext asp = getSpeciesContextFromBNGSpecies(outputBngSpecies[i], argSpeciesHash);
		if (asp != null) {
			// find out from 'sp' if the speciesContext is selected, and get the appropriate speciesContext (non-selected, fluoresced, bleached).
			if (!asp.isSelected()) {
				outputSpeciesContexts[i] = asp.getSpeciesContext();
				outputSpeciesHash.put(outputBngSpecies[i], outputSpeciesContexts[i]);
			} else {
				outputSpeciesContexts[i] = asp.getAugmentedSpeciesContext();
				outputSpeciesHash.put(outputBngSpecies[i], outputSpeciesContexts[i]);
			}
		}
	}

	// Create the output model and add the structure(s), since we need to add the species to the model before adding species contexts.
	Model outputModel = new cbit.vcell.model.Model("_bng");
	outputModel.setStructures(new Structure[] {argStructure});

	// Adding species and speciesContexts to the output model.
	for (int i = 0; i < outputSpeciesContexts.length; i++){
		Species species = outputSpeciesContexts[i].getSpecies();
		outputModel.addSpecies(species);
		outputModel.addSpeciesContext(outputSpeciesContexts[i]);
	}

	// Now deal with the reactions
	BNGParameter[] outputBngParameters = argBngOutputSpec.getBNGParams();
	BNGReaction[] outputBngReactions = argBngOutputSpec.getBNGReactions();

	cbit.vcell.model.ReactionStep[] outputReactionSteps = new cbit.vcell.model.ReactionStep[outputBngReactions.length];
	for (int i = 0; i < outputBngReactions.length; i++){
		cbit.vcell.model.SimpleReaction simpleRxn = new cbit.vcell.model.SimpleReaction(argStructure, "reaction_"+i);
		// Create separate reactants from BNGReaction and add these as reactionParticipants to each reactionStep
		Vector reactionParticipantsVector = new Vector();
		BNGSpecies[] bngReactants = outputBngReactions[i].getReactants();
		for (int j = 0; j < bngReactants.length; j++){
			SpeciesContext reactantSC = (SpeciesContext)outputSpeciesHash.get(bngReactants[j]);
			if (reactantSC != null) {
				cbit.vcell.model.Reactant reactant = new cbit.vcell.model.Reactant(null, simpleRxn, reactantSC, 1);
				reactionParticipantsVector.add(reactant);
			} else {
				throw new RuntimeException("Could not find reactant in list of species!");
			}
		}
		// Create separate products from BNGReaction and add these as reactionParticipants to each reactionStep
		BNGSpecies[] bngProducts = outputBngReactions[i].getProducts();
		for (int j = 0; j < bngProducts.length; j++){
			SpeciesContext productSC = (SpeciesContext)outputSpeciesHash.get(bngProducts[j]);
			if (productSC != null) {
				cbit.vcell.model.Product product = new cbit.vcell.model.Product(null, simpleRxn, productSC, 1);
				reactionParticipantsVector.add(product);
			} else {
				throw new RuntimeException("Could not find reactant in list of species!");
			}
		}
		ReactionParticipant[] reactionParticipants = (ReactionParticipant[])BeanUtils.getArray(reactionParticipantsVector, ReactionParticipant.class);
		simpleRxn.setReactionParticipants(reactionParticipants);

		// Set the kinetics
		Expression bngRxnParamExpression = outputBngReactions[i].getParamExpression();
		cbit.vcell.model.MassActionKinetics kinetics = new cbit.vcell.model.MassActionKinetics(simpleRxn);
		kinetics.setParameterValue(kinetics.getForwardRateParameter(), bngRxnParamExpression);
		String bngParamExprStr = bngRxnParamExpression.infix();
		BNGParameter bngRxnParam = null;
		for (int j = 0; j < outputBngParameters.length; j++){
			if (bngParamExprStr.indexOf(outputBngParameters[j].getName()) > -1) {
				bngRxnParam = outputBngParameters[j];
				break;
			}
		}
		kinetics.setParameterValue(kinetics.getKineticsParameter(bngRxnParam.getName()), new Expression(bngRxnParam.getValue()));

		// Add kinetics to the reaction
		simpleRxn.setKinetics(kinetics);
		outputReactionSteps[i] = simpleRxn;
	}

	// Collapse matching irreversible reactions (actually a reversible reaction, but listed as irreversible) into one reversible reaction.
	// Leave the truly irreversible reactions as is.
	cbit.vcell.model.ReactionStep[] newOutputReactionSteps = getCollapsedReactionSteps(outputReactionSteps);

	
	// Set reactions in output model, and output model in outputReactionCartoonEditorPanel
	outputModel.setReactionSteps(newOutputReactionSteps);
	
	return outputModel;
}
}
