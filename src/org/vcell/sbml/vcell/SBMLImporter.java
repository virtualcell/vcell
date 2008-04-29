package org.vcell.sbml.vcell;
/*
 * Created on Feb 10, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author anu
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.beans.PropertyVetoException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import org.sbml.libsbml.*;
import org.sbml.libsbml.Parameter;
import org.sbml.libsbml.Species;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SBMLUtils.SBMLUnitParameter;

import cbit.util.BeanUtils;
import cbit.util.TokenMangler;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.LambdaFunction;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.SBMLUnitTranslator;
import cbit.util.xml.VCLogger;
import cbit.vcell.xml.MIRIAMHelper;
import cbit.vcell.xml.XMLTags;

public class SBMLImporter {

	private static int level = 2;
	private static int version = 3;
	
	private String sbmlString = null;
	private org.sbml.libsbml.Model sbmlModel = null;
	private cbit.vcell.mapping.SimulationContext simContext = null;
	private LambdaFunction[] lambdaFunctions = null;
	private java.util.HashMap<String, Expression> assignmentRulesHash = new java.util.HashMap<String, Expression>();
	private java.util.HashMap<String, Expression> globalParamsHash = new java.util.HashMap<String, Expression>();
	private TreeMap<String, VCUnitDefinition> vcUnitsHash = new TreeMap<String, VCUnitDefinition>();
	private java.util.Hashtable<String, String> SbmlVcSpeciesHash = new java.util.Hashtable<String, String>();
	private java.util.Hashtable<String, SBVCConcentrationUnits> speciesUnitsHash = new java.util.Hashtable<String, SBVCConcentrationUnits>();

	private cbit.util.xml.VCLogger logger = null;
	 
	private static String RATE_NAME = XMLTags.RateTag;
	private static String SPECIES_NAME = XMLTags.SpeciesTag;
	private static String REACTION = XMLTags.ReactionTag;
//	private static String SIMPLE_RXN = XMLTags.SimpleReactionTag;

	/* A lightweight inner class to contain the SBML and VC concentration units for species. Needed when running the Semantics test suite.
	 * When SBML model is imported into VCell and a simulation is run, the units of the generated results are different from
	 * units of results in SBML (usually a factor of 1e-6 for species concentration). In order to compare the two results, 
	 * we need to know the units in SBML (this is after importing to VCEll and running simulations, at which point we only know the 
	 * VCell units). Hence using this lookup that is stored in a hashTable, which is retrieved later to make the appropriate unit
	 * conversions before comparing the 2 results.  
	*/
	public static class SBVCConcentrationUnits {
		private VCUnitDefinition SBunits = null;
		private VCUnitDefinition VCunits = null;
		
		public SBVCConcentrationUnits(VCUnitDefinition argSBunits, VCUnitDefinition argVCunits) {
			this.SBunits = argSBunits;
			this.VCunits = argVCunits;
		}
		public VCUnitDefinition getSBConcentrationUnits() {
			return SBunits;			
		}
		public VCUnitDefinition getVCConcentrationUnits() {
			return VCunits;		
		}
	}
	static
	{
		try {
			System.loadLibrary("expat");
			System.loadLibrary("sbml");
			System.loadLibrary("sbmlj");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public SBMLImporter(String argSbmlString, cbit.util.xml.VCLogger argVCLogger) {
		super();
		sbmlString = argSbmlString;
		this.logger = argVCLogger;
	}


protected void addCompartments() {
	if (sbmlModel == null) {
		throw new RuntimeException("SBML model is NULL");
	}
	ListOf listofCompartments = sbmlModel.getListOfCompartments();
	if (listofCompartments == null) {
		throw new RuntimeException("Cannot have 0 compartments in model"); 
	}
	// Using a vector here - since there can be sbml models with only features and no membranes. In that case, we will need to add a membrane in between.
	// Hence keepign the datastructure flexible.
	Vector<Structure> structVector = new Vector<Structure>();
	java.util.HashMap<String, Structure> structureNameMap = new java.util.HashMap<String, Structure>();

	try {
		// First pass - create the structures
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			org.sbml.libsbml.Compartment compartment = (org.sbml.libsbml.Compartment)listofCompartments.get(i);
			// Sometimes, the compartment name can be null; in that case, use compartment id as the name.
			String compartmentName = compartment.getId();
			if (compartment.getSpatialDimensions() == 3) {
				structVector.insertElementAt(new Feature(compartmentName), i);
				structureNameMap.put(compartmentName, (Feature)structVector.elementAt(i));
			} else if (compartment.getSpatialDimensions() == 2) {
				structVector.insertElementAt(new Membrane(compartmentName), i);
				structureNameMap.put(compartmentName, (Membrane)structVector.elementAt(i));
			} else {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.COMPARTMENT_ERROR, "Cannot deal with spatial dimension : " + compartment.getSpatialDimensions() + " for compartments at this time.");
				throw new RuntimeException("Cannot deal with spatial dimension : " + compartment.getSpatialDimensions() + " for compartments at this time");
			}
			MIRIAMHelper.setFromSBMLAnnotation(structVector.get(i), XMLNode.convertXMLNodeToString(compartment.getAnnotation()));
		}

		// Second pass - connect the structures - add membranes if needed.
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			org.sbml.libsbml.Compartment compartment = (org.sbml.libsbml.Compartment)listofCompartments.get(i);
			if (compartment.getOutside() != null && compartment.getOutside().length() > 0) {
				//compartment.getOutside returns the Sid of the 'outside' compartment, so get its name from model.
				String outsideCompartmentId = compartment.getOutside();
				Compartment outsideCompartment = sbmlModel.getCompartment(outsideCompartmentId);
				Structure outsideStructure = (Structure)structureNameMap.get(outsideCompartment.getId());
				if (compartment.getSpatialDimensions() == 3) {
					// Check if outsideStructure is a membrane. If not, we have to add a membrane between the compartments, 
					// since VCell requires that 2 features need to be separated by a membrane, and 2 membranes by a feature.
					Feature feature = (Feature)structVector.elementAt(i);
					if (outsideStructure instanceof Membrane) {
						// If feature, set the parent structure (outside structure) only; i.e., the bounding membrane.
						feature.setParentStructure(outsideStructure);
						// Also, set the inside feature of the bounding membrane to this feature.
						((Membrane)outsideStructure).setInsideFeature(feature);
					} else if (outsideStructure instanceof Feature) {
						// VCell doesn't permit the parent structure of a feature to be a feature.
						// hence we add a membrane in between.
						Membrane newMembrane = new Membrane(feature.getName() + "_membrane");
						// add this new membrane to structsVector and the structureNamesMap
						structVector.addElement(newMembrane);
						structureNameMap.put(newMembrane.getName(), newMembrane);
						// set this membrane as the parent for given structure, and set it as inside structure for the outer feature
						feature.setParentStructure(newMembrane);
						newMembrane.setInsideFeature(feature);
						newMembrane.setOutsideFeature((Feature)outsideStructure);
						// compute the size of membrane
						if (compartment.isSetSize() && compartment.getSize() > 0.0) {
							double size = compartment.getSize();
							String spatialDimBuiltInName = getSpatialDimensionBuiltInName((int)compartment.getSpatialDimensions());
							VCUnitDefinition sbmlSizeUnit = getSBMLUnit(compartment.getUnits(), spatialDimBuiltInName);	
							size = sbmlSizeUnit.convertTo(size, VCUnitDefinition.UNIT_um3);
							// Calculating the smallest surface area enclosing the volume of the compartment.
							// Vol. of inner compartment: size = 4/3*PI*R^3; solving for R, substitute into surface of membrane : 4*PI*R^2
							double membSize = 4 * Math.PI * Math.pow((size * 3/(4*Math.PI)), 2.0/3.0);
							// add the newly added membrane as a compartment to the SBML model (set size, units, etc)
							Compartment newCompartment = new Compartment(newMembrane.getName());
							newCompartment.setSpatialDimensions(2);
							// deal with unit conversion, since default unit for membrane (area) in SBML is m2 and in VCell is always um2.
							newCompartment.setSize(membSize);
							// Define um2 - AREA; add it to model
							UnitDefinition unitDefn = new UnitDefinition(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_um2.getSymbol()));
							org.sbml.libsbml.Unit um2_unit = new Unit("metre", 2, -6);
							unitDefn.addUnit(um2_unit);
							// Also add it to vcUnitsHash, to be able to retreive it later
							String unitName = unitDefn.getId();
							if (vcUnitsHash.get(unitName) == null) {
								sbmlModel.addUnitDefinition(unitDefn);
								VCUnitDefinition vcUnitDef = cbit.vcell.units.SBMLUnitTranslator.getVCUnitDefinition(unitDefn);
								vcUnitsHash.put(unitName, vcUnitDef);
							}
							newCompartment.setUnits(unitName);
							newCompartment.setOutside(newMembrane.getOutsideFeature().getName());
							compartment.setOutside(newCompartment.getId());
							sbmlModel.addCompartment(newCompartment);
						} else {
							logger.sendMessage(VCLogger.MEDIUM_PRIORITY, VCLogger.COMPARTMENT_ERROR, "compartment "+compartment.getId()+" size is not set.");
						}
						newMembrane.setParentStructure(outsideStructure);
					}
				} else if (compartment.getSpatialDimensions() == 2) {
					// If membrane, need to set both inside and outside feature. Inside feature will be set by the
					// compartment for which this membrane is the outside (bounding) structure.
					((Membrane)structVector.elementAt(i)).setParentStructure(outsideStructure);
				}
			}
		}

		// set the structures in vc simContext
		Structure[] structures = (Structure[])BeanUtils.getArray(structVector, Structure.class);
		simContext.getModel().setStructures(structures);
		
		// Third pass thro' the list of compartments : set the sizes on the structureMappings - can be done only after setting 
		// the structures on simContext.
		boolean allSizesSet = true;
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			org.sbml.libsbml.Compartment compartment = (org.sbml.libsbml.Compartment)listofCompartments.get(i);
			String compartmentName = compartment.getId();

			if (!compartment.isSetSize()) {
				// logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.COMPARTMENT_ERROR, "compartment "+compartmentName+" size is not set in SBML document.");
				allSizesSet = false;
			} else {
				double size = compartment.getSize();
				// Check if size is specified by a rule
				Expression sizeExpr = getValueFromRule(compartmentName);
				if (sizeExpr != null) {
					// WE ARE NOT HANDLING COMPARTMENT SIZES WITH ASSIGNMENT RULES AT THIS TIME  ...
					logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.COMPARTMENT_ERROR, "compartment "+compartmentName+" size has an assignment rule, cannot handle it at this time.");
				}
				
				// Convert size units from SBML -> VC compatible units.
				// If compartment (size) unit is not set, it is in the default SBML volume unit for 3d compartment and 
				// area unit for 2d compartment. Check to see if the default units are re-defined. If not, they are "litre"
				// for vol and "sq.m" for area. Convert it to VC units (um3 for 3d and um2 for 2d compartments) -
				// multiply the size value by the conversion factor.
				Expression adjustedSizeExpr = new Expression(size);
				Structure struct = simContext.getModel().getStructure(compartmentName);
				cbit.vcell.mapping.StructureMapping.StructureMappingParameter mappingParam = simContext.getGeometryContext().getStructureMapping(struct).getSizeParameter();
				VCUnitDefinition vcSizeUnit = mappingParam.getUnitDefinition();
				int spatialDim = (int)compartment.getSpatialDimensions();
				String spatialDimBuiltInName = getSpatialDimensionBuiltInName(spatialDim);
				VCUnitDefinition sbmlSizeUnit = getSBMLUnit(compartment.getUnits(), spatialDimBuiltInName);
				// Need to convert the size unit (vol or area) into VC compatible units (um3, um2) if it is not already in VC compatible units
				double factor = 1.0;
				factor  = sbmlSizeUnit.convertTo(factor, vcSizeUnit);
				if (factor != 1.0) {
					adjustedSizeExpr = Expression.mult(adjustedSizeExpr, new Expression(factor));
				}
					
				// Now set the size  & units of the compartment.
				mappingParam.setExpression(new Expression(adjustedSizeExpr));
			}
		}

		// Handle the absolute size to surface_vol/volFraction conversion if size is set
		if (allSizesSet) {
			StructureSizeSolver structSizeSolver = new StructureSizeSolver();
			structSizeSolver.updateRelativeStructureSizes(simContext);
		}
		simContext.getModel().getTopFeature();
	} catch (Exception e) {
		throw new RuntimeException("Error adding Feature to vcModel " + e.getMessage());
	}
}


/**
 * Warn user that we do not handle events now. Give a choice between bringing in the model without the events or cancelling the operation.
 *
 *
 **/

protected void addEvents() {
	if (sbmlModel.getNumEvents() > 0) {
		throw new RuntimeException("VCell doesn't support Events at this time");
	}
}

protected void addCompartmentTypes() {
	if (sbmlModel.getNumCompartmentTypes() > 0) {
		throw new RuntimeException("VCell doesn't support CompartmentTypes at this time");
	}
}

protected void addSpeciesTypes() {
	if (sbmlModel.getNumSpeciesTypes() > 0) {
		throw new RuntimeException("VCell doesn't support SpeciesTypes at this time");
	}
}

protected void addConstraints() {
	if (sbmlModel.getNumConstraints() > 0) {
		throw new RuntimeException("VCell doesn't support Constraints at this time");
	}
}

protected void addInitialAssignments() {
	if (sbmlModel.getNumInitialAssignments() > 0) {
		throw new RuntimeException("VCell doesn't support InitialAssignments at this time");
	}
}

protected void addFunctionDefinitions() {
	if (sbmlModel == null) {
		throw new RuntimeException("SBML model is NULL");
	}
	ListOf listofFunctionDefinitions = sbmlModel.getListOfFunctionDefinitions();
	if (listofFunctionDefinitions == null) {
		System.out.println("No Function Definitions");
		return;
	}
	// The function definitions contain lambda function definition.
	// Each lambda function has a name, (list of) argument(s), function body which is represented as a math element.
	lambdaFunctions = new LambdaFunction[(int)sbmlModel.getNumFunctionDefinitions()];
	try {
		for (int i = 0; i < sbmlModel.getNumFunctionDefinitions(); i++) {
			FunctionDefinition fnDefn = (FunctionDefinition)listofFunctionDefinitions.get(i);
			String functionName = fnDefn.getId();
			ASTNode math = null;
			Vector<String> argsVector = new Vector<String>();
			String[] functionArgs = null;
			if (fnDefn.isSetMath()) {
				math = fnDefn.getMath();
				// Add function arguments into vector, print args 
				// Note that lambda function always shoud have at least 2 children
				for (int j = 0; j < math.getNumChildren() - 1; ++j) {
					argsVector.addElement(math.getChild((long)j).getName());
				}
			}
			functionArgs = (String[])BeanUtils.getArray(argsVector, String.class);
			// Function body. 
			if (math.getNumChildren() == 0) {
				System.out.println("(no function body defined)");
			} else {
				math = math.getChild(math.getNumChildren() - 1);
				// formula = libsbml.formulaToString(math);
			}
			Expression fnExpr = getExpressionFromFormula(math);
			lambdaFunctions[i] = new LambdaFunction(functionName, fnExpr, functionArgs);
		}
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error adding Lambda function" + e.getMessage());
	}
}


protected void addParameters() {
	ListOf listofGlobalParams = sbmlModel.getListOfParameters();
	if (listofGlobalParams == null) {
		System.out.println("No Global Parameters");
		return;
	}
	// Put the global parameters into the parametersHash field, it will be useful while adding reactions (for global params that occur in reactions) 
	for (int i = 0; i < sbmlModel.getNumParameters(); i++){
		Parameter globalParam = (Parameter)listofGlobalParams.get(i);
		String paramName = globalParam.getId();
		// Check if param is defined by a rule. If so, that value overrides the value existing in the param element.
		Expression valueExpr = getValueFromRule(paramName);
		if (valueExpr == null) {
			if (globalParam.isSetValue()) {
				double value = globalParam.getValue();
				valueExpr = new Expression(value);
			}
		}
		// If globalParam is not already in the globalParamsHash, add it. Ok if valueExpr is null
		if (globalParamsHash.get(paramName) == null) {
			globalParamsHash.put(paramName, valueExpr);
		}
	}
}


/**
 *  addReactionParticipant :
 *		Adds reactants and products and modifiers to a reaction.
 *		Input args are the sbml reaction, vc reaction
 *		This method was created mainly to handle reactions where there are reactants and/or products that appear multiple times
 *		in a reaction. Virtual Cell now allows the import of such reactions.
 *		
**/
protected void addReactionParticipants(org.sbml.libsbml.Reaction sbmlRxn, ReactionStep vcRxn) throws Exception {
	SpeciesContext[] vcSpeciesContexts = simContext.getModel().getSpeciesContexts();

	// for each species in the sbml model,
	for (int i = 0; i < (int)sbmlModel.getNumSpecies(); i++){
		org.sbml.libsbml.Species sbmlSpecies = sbmlModel.getSpecies(i);
		boolean bSpeciesPresent = false;
		int reactantNum = 0;	// will be (stoichiometry_of_species) for every occurance of species as reactant
		int pdtNum = 0;			// will be (stoichiometry_of_species) for every occurance of species as product.
		int modifierNum = 0;
		boolean bAddedAsReactant = false;
		boolean bAddedAsProduct = false;
		
		// get the matching speciesContext for the sbmlSpecies - loop thro' the speciesContext list to find a match 
		// in the species name retrieved from the listofReactants or Pts. 
		SpeciesContext speciesContext = null;
		for (int j = 0; j < vcSpeciesContexts.length; j++) {
			if (vcSpeciesContexts[j].getName().equals(sbmlSpecies.getId())) {
				speciesContext =  vcSpeciesContexts[j];
			}
		}
		
		if (!(vcRxn instanceof FluxReaction)) {
			// check if it is present as reactant, if so, how many reactants
			for (int j = 0; j < (int)sbmlRxn.getNumReactants(); j++){
				SpeciesReference spRef = sbmlRxn.getReactant(j);
				// If stoichiometry of speciesRef is not an integer, it is not handled in the VCell at this time; no point going further
				if ( ((int)(spRef.getStoichiometry()) != spRef.getStoichiometry()) || spRef.isSetStoichiometryMath()) {
					logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.REACTION_ERROR, "Non-integer stoichiometry or stoichiometryMath not handled in VCell at this time.");
				}
				if (spRef.getSpecies().equals(sbmlSpecies.getId())) {
					reactantNum += (int)spRef.getStoichiometry();
					bSpeciesPresent = true;
				}
			}
	
			// If species is present, add it as a reactant with its cumulative stoichiometry
			if (bSpeciesPresent) {
				((SimpleReaction)vcRxn).addReactant(speciesContext, reactantNum);
				bAddedAsReactant = true;
				bSpeciesPresent = false;
			}
	
			// check if it is present as product, if so, how many products
			for (int j = 0; j < (int)sbmlRxn.getNumProducts(); j++){
				SpeciesReference spRef = sbmlRxn.getProduct(j);
				// If stoichiometry of speciesRef is not an integer, it is not handled in the VCell at this time; no point going further
				if ( ((int)(spRef.getStoichiometry()) != spRef.getStoichiometry()) || spRef.isSetStoichiometryMath()) {
					logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.REACTION_ERROR, "Non-integer stoichiometry or stoichiometryMath not handled in VCell at this time.");
				}
				if (spRef.getSpecies().equals(sbmlSpecies.getId())) {
					pdtNum  += (int)spRef.getStoichiometry();
					bSpeciesPresent = true;
				}
			}
	
			// If species is present, add it as a product with its cumulative stoichiometry
			if (bSpeciesPresent) {
				((SimpleReaction)vcRxn).addProduct(speciesContext, pdtNum);
				bAddedAsProduct = true;
				bSpeciesPresent = false;
			}
		}

		// check if it is present as modifier, if so, how many modifiers
		for (int j = 0; j < (int)sbmlRxn.getNumModifiers(); j++){
			ModifierSpeciesReference spRef = sbmlRxn.getModifier(j);
			if (spRef.getSpecies().equals(sbmlSpecies.getId())) {
				modifierNum++;
			}
		}

		// If species is present and modifierNum > 0, species was already added as reactant and/or pdt, so cannot be added as catalyst; throw exception.
		// If species is not present, and modifierNum > 0, it was not previously added as a reactant and/or pdt, hence can add it as a catalyst.
		if (modifierNum > 0) {
			if (bAddedAsReactant || bAddedAsProduct) {
				logger.sendMessage(VCLogger.LOW_PRIORITY, VCLogger.REACTION_ERROR, "Species " + speciesContext.getName() + " was already added as a reactant and/or product to " + vcRxn.getName() + "; Cannot add it as a catalyst also.");
			} else {
				vcRxn.addCatalyst(speciesContext);
			}
		}
	}
}
/**
 * addReactions:
 *
 */
protected void addReactions() {
	if (sbmlModel == null) {
		throw new RuntimeException("SBML model is NULL");
	}
	ListOf listofReactions = sbmlModel.getListOfReactions();
	if (listofReactions == null) {
		System.out.println("No Reactions");
		return;
	}
	ReactionStep[] vcReactions = new ReactionStep[(int)sbmlModel.getNumReactions()];
	SpeciesContext[] vcSpeciesContexts = simContext.getModel().getSpeciesContexts();
	try {
		for (int i = 0; i < sbmlModel.getNumReactions(); i++) {
			org.sbml.libsbml.Reaction sbmlRxn = (org.sbml.libsbml.Reaction)listofReactions.get(i);
			String rxnName = sbmlRxn.getId();
			Structure reactionStructure = getReactionStructure(sbmlRxn, vcSpeciesContexts); 
			// Check of reaction annotation is present; if so, does it have an embedded element (flux or simpleRxn).
			// Create a fluxReaction or simpleReaction accordingly.
			XMLNode rxnAnnotation = sbmlRxn.getAnnotation();
			XMLNode embeddedRxnElement = null;
			if (rxnAnnotation != null) {
				embeddedRxnElement = getEmbeddedElementInAnnotation(rxnAnnotation, REACTION);
				if (embeddedRxnElement != null) {
					if (embeddedRxnElement.getName().equals(XMLTags.FluxStepTag)) {
						// If embedded element is a flux reaction, set flux reaction's strucure, flux carrier, physicsOption from the element attibutes.
						XMLAttributes attributes = embeddedRxnElement.getAttributes();
						String structName = attributes.getValue(XMLTags.StructureAttrTag);
						Structure struct = simContext.getModel().getStructure(structName);
						if (!(struct instanceof Membrane)) {
							throw new RuntimeException("Appears that the flux reaction is not occuring on a membrane.");
						}
						String fluxCarrierSpName = attributes.getValue(XMLTags.FluxCarrierAttrTag);
						cbit.vcell.model.Species fluxCarrierSp = simContext.getModel().getSpecies(fluxCarrierSpName);
						if (fluxCarrierSp == null) {
							logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.REACTION_ERROR, "Unknown FluxCarrier : " + fluxCarrierSpName + " for SBML reaction : " + rxnName);
						}
						vcReactions[i] = new FluxReaction((Membrane)struct, fluxCarrierSp, simContext.getModel(), rxnName);
						// Set the fluxOption on the flux reaction based on whether it is molecular, molecular & electrical, electrical.
						String fluxOptionStr = attributes.getValue(XMLTags.FluxOptionAttrTag);
						if (fluxOptionStr.equals(XMLTags.FluxOptionMolecularOnly)) {
							((FluxReaction)vcReactions[i]).setPhysicsOptions(ReactionStep.PHYSICS_MOLECULAR_ONLY);
						} else if (fluxOptionStr.equals(XMLTags.FluxOptionMolecularAndElectrical)) {
							((FluxReaction)vcReactions[i]).setPhysicsOptions(ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL);
						} else if (fluxOptionStr.equals(XMLTags.FluxOptionElectricalOnly)) {
							((FluxReaction)vcReactions[i]).setPhysicsOptions(ReactionStep.PHYSICS_ELECTRICAL_ONLY);
						} else {
							logger.sendMessage(VCLogger.MEDIUM_PRIORITY, VCLogger.REACTION_ERROR, "Unknown FluxOption : " + fluxOptionStr + " for SBML reaction : " + rxnName);
						}
					} else if (embeddedRxnElement.getName().equals(XMLTags.SimpleReactionTag)) {
						// if embedded element is a simple reaction, set simple reaction's structure from element attributes
						vcReactions[i] = new cbit.vcell.model.SimpleReaction(reactionStructure, rxnName);
					}
				} else {
					vcReactions[i] = new cbit.vcell.model.SimpleReaction(reactionStructure, rxnName);
				}
				MIRIAMHelper.setFromSBMLAnnotation(vcReactions[i], XMLNode.convertXMLNodeToString(rxnAnnotation));
			} else {
				vcReactions[i] = new cbit.vcell.model.SimpleReaction(reactionStructure, rxnName);
			}
			
			// Now add the reactants, products, modifiers as specified by the sbmlRxn
			addReactionParticipants(sbmlRxn, vcReactions[i]);
			
			KineticLaw kLaw = sbmlRxn.getKineticLaw();
			Kinetics kinetics = null;
			if (kLaw != null) {
			// Convert the formula from kineticLaw into MathML and then to an expression (infix) to be used in VCell kinetics
			ASTNode sbmlRateMath = kLaw.getMath();
			Expression kLawRateExpr = getExpressionFromFormula(sbmlRateMath);
			Expression vcRateExpression = new Expression(kLawRateExpr);

			// Check the kinetic rate equation for occurances of any species in the model that is not a reaction participant.
			// If there exists any such species, it should be added as a modifier (catalyst) to the reaction.
			String[] symbols = vcRateExpression.getSymbols();
			if (symbols != null) {
				for (int j = 0; j < symbols.length; j++){
					for (int k = 0; k < vcSpeciesContexts.length; k++){
						if (vcSpeciesContexts[k].getName().equals(symbols[j])) {
							if ((sbmlRxn.getReactant(vcSpeciesContexts[k].getName()) == null) && (sbmlRxn.getProduct(vcSpeciesContexts[k].getName()) == null) && (sbmlRxn.getModifier(vcSpeciesContexts[k].getName()) == null)) {
								// This means that the speciesContext is not a reactant, product or modifier : it has to be added to the VC Rxn as a catalyst
								vcReactions[i].addCatalyst(vcSpeciesContexts[k]);
							}
						}
					}
				}
			}


			// Retrieve the compartment in which the reaction takes place
			Compartment compartment = sbmlModel.getCompartment(reactionStructure.getName());
			if (compartment == null) {
				throw new RuntimeException("The compartment corresponding to " + reactionStructure.getName() + " was not found");
			}
			
			// Check if the kLaw rate equation has compartment_id (corresponding to reactionStructure); and if so, 
			// check if the id is a local parameter, in that case, the local parameter takes precendence.
			boolean bLocalParamMatchesCompId = false;
			ListOf listofLocalParams = kLaw.getListOfParameters();
			String COMPARTMENTSIZE_SYMBOL = compartment.getId();
			for (int j = 0; j < kLaw.getNumParameters(); j++) {
				org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofLocalParams.get(j);
				String paramName = param.getId();
				// Check if reaction rate param clashes with an existing (pre-defined) kinetic parameter - eg., reaction rate param 'J'
				// If so, change the name of the kinetic param (say, by adding reaction name to it).
				if (paramName.equals(COMPARTMENTSIZE_SYMBOL)) {
					bLocalParamMatchesCompId = true;
				}
			}
			// if local kLaw param matches compartment_id, local param takes precedence,
			// if bLocalParamMatchesCompId is <T> and if kLawRateExpr     HAS 		compartment_id : LumpedKinetics  
			// if bLocalParamMatchesCompId is <F> and if kLawRateExpr DOESN'T HAVE 	compartment_id : LumpedKinetics  
			// if bLocalParamMatchesCompId is <F> and if kLawRateExpr 	  HAS 		compartment_id : GeneralKinetics  
			// if bLocalParamMatchesCompId is <T> and if kLawRateExpr DOESN'T HAVE  compartment_id : SHOULDN'T HAPPEN (it means there is a kLaw local param with compartment_id, but kLaw expression doesn't contain copartment_id, which is not possible.  
			if ((bLocalParamMatchesCompId && kLawRateExpr.hasSymbol(COMPARTMENTSIZE_SYMBOL)) || (!bLocalParamMatchesCompId && !kLawRateExpr.hasSymbol(COMPARTMENTSIZE_SYMBOL))) {
				kinetics = new GeneralLumpedKinetics(vcReactions[i]);
			} else if (kLawRateExpr.hasSymbol(COMPARTMENTSIZE_SYMBOL) && !bLocalParamMatchesCompId) {
				kinetics = new GeneralKinetics(vcReactions[i]);
			} else {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.REACTION_ERROR, "Cannot have a local parameter which does not occur in kinetic law expression for SBML reaction : " + rxnName);
			}

			// If the name of the rate parameter has been changed by user, or matches with global/local param, 
			// it has to be changed.
			resolveRxnParameterNameConflicts(sbmlRxn, kinetics);
			
			// Deal with units : kinetic rate units is in substance/time; obtain substance units, time units from
			// kLaw (L2V1) and obtain kLawRateUnit.
			// kinetic law substance unit :
			String kLawSubstanceUnitStr = null;
			if (kLaw.isSetSubstanceUnits()) {
				kLawSubstanceUnitStr = kLaw.getSubstanceUnits();
			}
			VCUnitDefinition kLawSubstanceUnit = getSBMLUnit(kLawSubstanceUnitStr, SBMLUnitTranslator.SUBSTANCE);

			// kinetic law time unit :
			String kLawTimeUnitStr = null;
			if (kLaw.isSetTimeUnits()) {
				kLawTimeUnitStr = kLaw.getTimeUnits();
			}
			VCUnitDefinition kLawTimeUnit = getSBMLUnit(kLawTimeUnitStr, SBMLUnitTranslator.TIME);

			// kinetic law rate unit in SBML is in terms of substance/time
			VCUnitDefinition kLawRateUnit = kLawSubstanceUnit.divideBy(kLawTimeUnit);
			VCUnitDefinition VC_RateUnit = null;
			VCUnitDefinition SBML_RateUnit = kLawRateUnit;
			VCUnitDefinition KmoleUnits = ReservedSymbol.KMOLE.getUnitDefinition();

			/**
			 * Now, based on the kinetic law expression, see if the rate is expressed in concentration/time or substance/time :
			 * If the compartment_id of the compartment corresponding to the structure in which the reaction takes place 
			 * occurs in the rate law expression, it is in concentration/time; divide it by the compartment size and bring in 
			 * the rate law as 'Distributed' kinetics. If not, the rate law is in substance/time; bring it in (as is) as 
			 * 'Lumped' kinetics. 
			 */ 
			
			if (kinetics instanceof GeneralKinetics) {
				// rate law is in terms of concentration/time; use GeneralKinetics ('Distributed' kinetics) 

				// convert kLawRateUnit (above) from substance/time to concentration/time by dividing by size (of compartment) units
				// deal with compartment size
				VCUnitDefinition compartmentSizeUnit = getSBMLUnit(compartment.getUnits(), getSpatialDimensionBuiltInName((int)compartment.getSpatialDimensions()));
				SBML_RateUnit = SBML_RateUnit.divideBy(compartmentSizeUnit);

				// Virtual cell rate unit in terms of concentration/time. Units depend on whether reaction is in feature or membrane
				if (reactionStructure instanceof Feature) {
					VC_RateUnit = VCUnitDefinition.UNIT_uM_per_s;
				} else if (reactionStructure instanceof Membrane) {
					if (vcReactions[i] instanceof FluxReaction) {
						VC_RateUnit = VCUnitDefinition.UNIT_uM_um_per_s;
					} else if (vcReactions[i] instanceof SimpleReaction) {
						VC_RateUnit = VCUnitDefinition.UNIT_molecules_per_um2_per_s;
					}
				}
				/* Depending on SBML substance units (moles or molecules) and if the reaction is on a membrane or feature, 
				   an intermediate unit conversion is required between SBML and VC units before evaluating 
				   the 'dimensionless' scale factor (see next step below) */
				if (kLawSubstanceUnit.isCompatible(VCUnitDefinition.UNIT_mol)) {
					if (reactionStructure instanceof Membrane) {
						SBML_RateUnit = SBML_RateUnit.divideBy(KmoleUnits);
						vcRateExpression = Expression.mult(vcRateExpression, Expression.invert(new Expression(ReservedSymbol.KMOLE.getName())));
					} 
				} else if (kLawSubstanceUnit.isCompatible(VCUnitDefinition.UNIT_molecules)) {
					if ( (reactionStructure instanceof Feature) || (reactionStructure instanceof Membrane && vcReactions[i] instanceof FluxReaction) ) {
						SBML_RateUnit = SBML_RateUnit.multiplyBy(KmoleUnits);
						vcRateExpression = Expression.mult(vcRateExpression, new Expression(ReservedSymbol.KMOLE.getName()));
					} 
				}

				/* Converting rate expression into density/time (for VCell). We need to divide by compartmentSize to remove the size from
				   the existing equation, but since just dividing by size will accumulate the variables (VCell expression handling), we 
				   differentiate the rate expression to remove the compartmentSize var from the original expression.
				   (No need to check if rate expression has COMP_SYMBOL, since we wouldn't be in this loop otherwise, but checking anyway). */
				 
				if (vcRateExpression.hasSymbol(COMPARTMENTSIZE_SYMBOL)) {
					vcRateExpression = removeCompartmentScaleFactorInRxnRateExpr(vcRateExpression, COMPARTMENTSIZE_SYMBOL, rxnName);
					kinetics.setParameterValue(kinetics.getAuthoritativeParameter(),vcRateExpression);
				} else {
					logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.REACTION_ERROR, "Reaction " + rxnName + " cannot have GeneralKinetics since kinetic law expression is not in terms of concentration/time");
				}
			} else if (kinetics instanceof LumpedKinetics){
				// rate law is in substance/time; use 'Lumped' Kinetics.
				// SBML_RateUnit = kLawRateUnit; - in terms of substance/time - leave it as is.
				// Virtual cell rate unit in terms of substance/time. Units depend on whether reaction is in feature or membrane
				VC_RateUnit = VCUnitDefinition.UNIT_molecules_per_s;

				/* Depending on SBML substance units (moles or molecules) and if the reaction is on a membrane or feature, 
				   an intermediate unit conversion is required between SBML and VC units before evaluating 
				   the 'dimensionless' scale factor (see next step below) */
				if (kLawSubstanceUnit.isCompatible(VCUnitDefinition.UNIT_mol)) {
					SBML_RateUnit = SBML_RateUnit.divideBy(KmoleUnits);
					vcRateExpression = Expression.mult(vcRateExpression, Expression.invert(new Expression(ReservedSymbol.KMOLE.getName())));
				}
				
				// set the kinetics rate parameter.
				kinetics.setParameterValue(kinetics.getAuthoritativeParameter(), vcRateExpression);

				// sometimes, the reaction rate can contain a compartment name, not necessarily the compartment the reaction takes place.
				for (int kk = 0; kk < (int)sbmlModel.getNumCompartments(); kk++){
					Compartment comp1 = sbmlModel.getCompartment(kk);
					boolean bCompFoundInLocalParams = false;
					for (int ll = 0; ll < kLaw.getNumParameters(); ll++) {
						if (comp1.getId().equals(((org.sbml.libsbml.Parameter)listofLocalParams.get((long)ll)).getId())) {
							bCompFoundInLocalParams = true;
						}
					}
					if (vcRateExpression.hasSymbol(comp1.getId()) && !bCompFoundInLocalParams) {
						if (!comp1.getId().equals(compartment.getId())) {
							if (comp1.isSetSize()) {
								kinetics.setParameterValue(kinetics.getKineticsParameter(comp1.getId()), new Expression(comp1.getSize()));
							} else {
								logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.REACTION_ERROR, "Reaction " + rxnName + " has a compartment whose size is not set");
							}
							kinetics.getKineticsParameter(comp1.getId()).setUnitDefinition(getSBMLUnit(comp1.getUnits(), getSpatialDimensionBuiltInName((int)comp1.getSpatialDimensions())));
						} else {
							logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.REACTION_ERROR, "Reaction " + rxnName + " rate expression contains the compartment in which the reaction takes place; the kinetics should not be Lumped kinetics");
						}
					}
				}	// end for - compartments in model loop
			}
			
			// Handle the unit conversion factor - common for both type of kinetics, though the SBML_RateUnit and VC_RateUnit are different.
			String SBMLFACTOR_PARAMETER = "sbmlRateFactor";
			// Check if kLaw rate expression param has same name other SBase elements in the namespace,
			// we don't want to override them with a local param in kLaw.
			while ( (kLawRateExpr.hasSymbol(SBMLFACTOR_PARAMETER)) || 
					(sbmlModel.getParameter(SBMLFACTOR_PARAMETER) != null) || 
					(sbmlModel.getCompartment(SBMLFACTOR_PARAMETER) != null) || 
					(sbmlModel.getSpecies(SBMLFACTOR_PARAMETER) != null)) {
				SBMLFACTOR_PARAMETER = TokenMangler.getNextEnumeratedToken(SBMLFACTOR_PARAMETER);
			}
			// introduce "dimensionless" scale factor for the reaction rate (after adjusting sbml rate for sbml compartment size)
			// note that although physically dimensionless, the VCUnitDefinition will likely have a non-unity scale conversion (e.g. 1e-3)
			double rateScalefactor = 1.0;
			if (VC_RateUnit.isCompatible(SBML_RateUnit)) { 
				rateScalefactor = SBML_RateUnit.convertTo(rateScalefactor, VC_RateUnit);
				VCUnitDefinition rateFactorUnit = VC_RateUnit.divideBy(SBML_RateUnit);
				if (rateScalefactor == 1.0 && rateFactorUnit.equals(VCUnitDefinition.UNIT_DIMENSIONLESS)) {
					// Ignore the factor since rateFactor and its units are 1
				} else {
					Expression newRateExpr = Expression.mult(kinetics.getAuthoritativeParameter().getExpression(), new Expression(SBMLFACTOR_PARAMETER));
					kinetics.setParameterValue(kinetics.getAuthoritativeParameter(), newRateExpr);
					kinetics.setParameterValue(kinetics.getKineticsParameter(SBMLFACTOR_PARAMETER), new Expression(rateScalefactor));
					kinetics.getKineticsParameter(SBMLFACTOR_PARAMETER).setUnitDefinition(rateFactorUnit);
				}
			} else {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "Unable to scale the unit for kinetic rate: " + VC_RateUnit.getSymbol() + " -> " + SBML_RateUnit.getSymbol());
			}
			
			//  ************ <<<< Scale units of SPECIES in all expressions to VC concentration units ************
			//
			// If the rate expression has any species, the units of the species are in concentration units.
			// We need to convert them from SBML unit to VCell unit. If the 'hasOnlySubstanceUnit' field is true
			// for any of the species, or if the spatial dimension of the compartment is 0, we do not handle it
			// at this time, throw an exception.
			//
			symbols = vcRateExpression.getSymbols();
			if (symbols != null) {
				for (int j = 0; j < symbols.length; j++){
					for (int k = 0; k < vcSpeciesContexts.length; k++){
						if (vcSpeciesContexts[k].getName().equals(symbols[j])) {
							org.sbml.libsbml.Species species = sbmlModel.getSpecies(vcSpeciesContexts[k].getName());
					//		if (species.getHasOnlySubstanceUnits() || sbmlModel.getCompartment(species.getCompartment()).getSpatialDimensions() == 0) {
					//			logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "Species with substance units only or compartments with spatial dimension of 0 is not handled at this time.");
					//		} else {
								/* Check if species name is used as a local parameter in the klaw. If so, the parameter in the local namespace 
								   takes precedence. So ignore unit conversion for the species with the same name. */
								boolean bSpeciesNameFoundInLocalParamList = false;
								for (int ll = 0; ll < kLaw.getNumParameters(); ll++) {
									org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofLocalParams.get(ll);
									String paramName = param.getId();
									if (paramName.equals(species.getId())) {
										bSpeciesNameFoundInLocalParamList = true;
										break; 		// break out of klaw local params loop
									}
								}
								if (bSpeciesNameFoundInLocalParamList) {
									break;			// break out of speciesContexts loop
								}
								
								// Get the SBML unit for the species
								Compartment spCompartment= sbmlModel.getCompartment(species.getCompartment());
								int dimension = (int)spCompartment.getSpatialDimensions();
								String spatialDimBuiltinName = getSpatialDimensionBuiltInName(dimension);
								String spatialSizeUnitStr = species.getSpatialSizeUnits();
								String substanceUnitStr = species.getSubstanceUnits();
								VCUnitDefinition substanceUnit = getSBMLUnit(substanceUnitStr, SBMLUnitTranslator.SUBSTANCE);
								VCUnitDefinition spatialSizeUnit = getSBMLUnit(spatialSizeUnitStr, spatialDimBuiltinName);
								VCUnitDefinition SBML_conc_unit = substanceUnit.divideBy(spatialSizeUnit);

								// Get the VC unit for the species (depending on the structure it is in)
								VCUnitDefinition VC_conc_unit = null;
								Structure speciesStructure = vcSpeciesContexts[k].getStructure();
								if (speciesStructure instanceof Feature) {
									VC_conc_unit = VCUnitDefinition.UNIT_uM;
								} else if (speciesStructure instanceof Membrane) {
									VC_conc_unit = VCUnitDefinition.UNIT_molecules_per_um2;
								}

								/* the expr from SBML is in terms of SBML units; VC interprets concs in uM, but we have to translate them back to SBML units 
								   within the expr; then we translate the SBML rate units into VCell units.
								   we convert concs into SBML (using 'sp_conc_factor'); so that the SBML expression is consistent; then we translate the SBML expression 
								   into VCell units (using 'sbmlRateFactor') */
								SBMLUnitParameter concScaleFactor = SBMLUtils.getConcUnitFactor("spConcUnit", VC_conc_unit, SBML_conc_unit);
								if ((concScaleFactor.getExpression().evaluateConstant() == 1.0 && concScaleFactor.getUnitDefinition().compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS)) ) {
									// if VC unit IS compatible with SBML unit and factor is 1 and unit conversion is 1
									// No conversion is required, and we don't need to include a concentration scale factor for the species.
								} else {
									// Substitute any occurance of speciesName in rate expression for kinetics with 'speciesName*concScaleFactor'
									// * Get current rate expression from kinetics, substitute corresponding values, re-set kinetics expression *
									String CONCFACTOR_PARAMETER = species.getId() + "_ConcFactor";
									// Check if this parameter is in the local param list of kLaw
									Parameter localParam = kLaw.getParameter(CONCFACTOR_PARAMETER);
									if (localParam != null) {
										// the concentration factor for this species already exists; multiply species with the 
										// new concentration factor value and substitute it in rate expression. For eg., if the concFactor 
										// for species 's1' has a value 'V1' and si_ConcFactor exists in local params, substitute
										// 's1*V1' in place of 's1' in rate expression, and leave s1_ConcFactor as is.
										Expression newRateExpr = kinetics.getAuthoritativeParameter().getExpression();
										Expression modifiedSpeciesExpression = Expression.mult(new Expression(species.getId()), concScaleFactor.getExpression());
										newRateExpr.substituteInPlace(new Expression(species.getId()), modifiedSpeciesExpression);
										kinetics.setParameterValue(kinetics.getAuthoritativeParameter(), newRateExpr);
									} else {
										Expression newRateExpr = kinetics.getAuthoritativeParameter().getExpression();
										newRateExpr.substituteInPlace(new Expression(species.getId()), new Expression(species.getId()+"*"+CONCFACTOR_PARAMETER));
										kinetics.setParameterValue(kinetics.getAuthoritativeParameter(), newRateExpr);
										// Add the concentration factor as a parameter
										kinetics.setParameterValue(kinetics.getKineticsParameter(CONCFACTOR_PARAMETER), concScaleFactor.getExpression());
										kinetics.getKineticsParameter(CONCFACTOR_PARAMETER).setUnitDefinition(concScaleFactor.getUnitDefinition());
									}
								}	// end - if concScaleFactor
					//		}	// end - sp hasOnlySubsUnits
						}
					}		// end for - k (vcSpeciesContext)
				}		// end for - j (symbols)
			} 	// end - if symbols != null

			// Check for unresolved parameters in the vcKinetics and add them from the global parameters list 
			ListOf listofGlobalParams = sbmlModel.getListOfParameters();
			for (int j = 0; j < sbmlModel.getNumParameters(); j++) {
				org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofGlobalParams.get(j);
				String paramName = param.getId();
				// If the global parameter is a kinetic parameter, then get its value and set the kinetic parameter for the vcKinetics
				// Else continue with the next global parameter.
				if (kinetics.getKineticsParameter(paramName) != null) {
					double value = param.getValue();
					
					// Check if param is defined by a rule. If so, that expression overrides the value existing in the param element.
					Expression valueExpr = getValueFromRule(paramName);
					if (valueExpr == null) {
						valueExpr = new Expression(value);
					}

					// Check if expression for parameter (if from rule) has species in its symbols. If so, we need to
					// consider the unit conversion factor for those species.
				    HashSet<String> refSpeciesNameHash = new HashSet<String>(); 
				    getReferencedSpeciesInExpr(valueExpr, refSpeciesNameHash);
				    java.util.Iterator<String> refSpIterator = refSpeciesNameHash.iterator();
					int spCount = 0;
					while (refSpIterator.hasNext()) {
				    	String spName = refSpIterator.next();
				    	Species sp = sbmlModel.getSpecies(spName);
						SBVCConcentrationUnits sbvcSubstUnits = speciesUnitsHash.get(sp.getId());
						VCUnitDefinition vcUnit = sbvcSubstUnits.getVCConcentrationUnits();
						VCUnitDefinition sbUnit = sbvcSubstUnits.getSBConcentrationUnits();
						
						/* the expr from SBML is in terms of SBML units; VC interprets concs in uM, but we have to translate them back to SBML units 
						   within the expr; then we translate the SBML rate units into VCell units.
						   we convert concs into SBML (using 'sp_conc_factor'); so that the SBML expression is consistent; then we translate the SBML expression 
						   into VCell units (using 'sbmlRateFactor') */
						SBMLUnitParameter concScaleFactor_1 = SBMLUtils.getConcUnitFactor("spConcFactor", vcUnit, sbUnit);
						if ((concScaleFactor_1.getExpression().evaluateConstant() == 1.0 && concScaleFactor_1.getUnitDefinition().compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS)) ) {
							// if VC unit IS compatible with SBML unit and factor is 1 and unit conversion is 1
							// No conversion is required, and we don't need to include a concentration scale factor for the species.
						} else {
							// Substitute any occurance of speciesName in rate expression for kinetics with 'speciesName*concScaleFactor'
							// * Get current rate expression from kinetics, substitute corresponding values, re-set kinetics expression *
							String CONCFACTOR_PARAMETER = sp.getId() + "_ConcFactor";
							// Check if this parameter is in the local param list of kLaw
							Parameter localParam = kLaw.getParameter(CONCFACTOR_PARAMETER);
							Parameter globalParam = sbmlModel.getParameter(CONCFACTOR_PARAMETER + "_" + rxnName);
							if (localParam != null || globalParam != null) {
								// the concentration factor for this species already exists; multiply species with the 
								// new concentration factor value and substitute it in param expression. For eg., if the concFactor 
								// for species 's1' has a value 'V1' and si_ConcFactor exists , substitute
								// 's1*V1' in place of 's1' in param expression; leaving s1_ConcFactor as is.
								Expression modifiedSpeciesExpression = Expression.mult(new Expression(sp.getId()), concScaleFactor_1.getExpression());
								valueExpr.substituteInPlace(new Expression(sp.getId()), modifiedSpeciesExpression);
								kinetics.setParameterValue(paramName,valueExpr.infix());
								spCount++;
							} else {
								// Check if valueExpr has the species, if so, add the species conc factor, else, do nothing.
								if (valueExpr.hasSymbol(spName)) {
									valueExpr.substituteInPlace(new Expression(sp.getId()), new Expression(sp.getId()+"*"+CONCFACTOR_PARAMETER));
									kinetics.setParameterValue(paramName,valueExpr.infix());
									// Add the concentration factor as a parameter
									kinetics.setParameterValue(kinetics.getKineticsParameter(CONCFACTOR_PARAMETER), concScaleFactor_1.getExpression());
									kinetics.getKineticsParameter(CONCFACTOR_PARAMETER).setUnitDefinition(concScaleFactor_1.getUnitDefinition());
									spCount++;
								}
							}
						}

						// If this species is not already a reactant or product or modifier for the reaction, but 
						// occurs in an assignment rule of a parameter, add it as a catalyst.
						if (vcReactions[i].getReactionParticipantFromSymbol(sp.getId()) == null) {
							// This means that the speciesContext is not a reactant, product or modifier : it has to be added as a catalyst
							vcReactions[i].addCatalyst(simContext.getModel().getSpeciesContext(sp.getId()));
						}
					}	// end - while
				
					// if there is no species in the expression for paramater, set the expression in the kinetics
					// to what it is. If there were species, the expression for the parameter would have been 
					// already written above.
					if (spCount == 0) {
						kinetics.setParameterValue(paramName, valueExpr.infix());
					}
					// Check if the expression for the global parameter contains other global parameters that 
					// have already been passed in the 'for' loop. If so, add them from the globalParamsHash as
					// params to the kinetics
					substituteOtherGlobalParams(kinetics, valueExpr);
//						String[] exprSymbols = valueExpr.getSymbols();
//						for (int kk = 0; exprSymbols != null && kk < exprSymbols.length; kk++) {
//							Expression expr = globalParamsHash.get(exprSymbols[kk]);
//							if (expr != null) {
//								Expression newExpr = new Expression(expr);
//								substituteGlobalParamRulesInPlace(newExpr, false);
//								// param has constant value, add it as a kinetic parameter if it is not already in the kinetics
//								kinetics.setParameterValue(exprSymbols[kk], newExpr.infix());
//								kinetics.getKineticsParameter(exprSymbols[kk]).setUnitDefinition(getSBMLUnit(sbmlModel.getParameter(exprSymbols[kk]).getUnits(), null));
//							}
//						}
					
					// finally, set the units for the parameter.
					VCUnitDefinition paramUnit = getSBMLUnit(param.getUnits(),null);
					kinetics.getKineticsParameter(paramName).setUnitDefinition(paramUnit);
				} 
			}
			
			// Introduce all remaining local parameters from the SBML model - local params cannot be defined by rules.
			for (int j = 0; j < kLaw.getNumParameters(); j++) {
				org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofLocalParams.get(j);
				String paramName = param.getId();
				// check if sbml local param is in kinetic params list; if so, add its value. 
				if (kinetics.getKineticsParameter(paramName) != null) {
					kinetics.setParameterValue(paramName, Double.toString(param.getValue()));
					VCUnitDefinition paramUnit = getSBMLUnit(param.getUnits(),null);
					kinetics.getKineticsParameter(paramName).setUnitDefinition(paramUnit);
				}
			}
			} else {
				// sbmlKLaw was null, so creating a GeneralKinetics with 0.0 as rate.
				kinetics = new GeneralKinetics(vcReactions[i]); 
			} // end - if-else  KLaw != null

			// set the reaction kinetics, and add reaction to the vcell model.
			kinetics.resolveUndefinedUnits();
			vcReactions[i].setKinetics(kinetics);
			simContext.getModel().addReactionStep(vcReactions[i]);
			// System.out.println("ADDED SBML REACTION : \"" + rxnName + "\" to VCModel");
			if (sbmlRxn.isSetFast() && sbmlRxn.getFast()) {
				simContext.getReactionContext().getReactionSpec(vcReactions[i]).setReactionMapping(cbit.vcell.mapping.ReactionSpec.FAST);
			}
		}	// end - for vcReactions
	} catch (Exception e1) {
		e1.printStackTrace(System.out);
		throw new RuntimeException(e1.getMessage());
	}
}

/**
 *  addRules :
 *		Adds Rules from the SBML document
 *		Assignment rules are allowed (initial concentration of species; parameter definitions, etc.
 *		Rate rules and Algebraic rules are not allowed (used) in the Virtual Cell.
 *		
**/
protected void addRules() throws ExpressionException {
	if (sbmlModel == null) {
		throw new RuntimeException("SBML model is NULL");
	}
	ListOf listofRules = sbmlModel.getListOfRules();
	if (listofRules == null) {
		System.out.println("No Rules specified");
		return;
	}
	for (int i = 0; i < sbmlModel.getNumRules(); i++){
		Rule rule = (org.sbml.libsbml.Rule)listofRules.get(i);
		if (!(rule instanceof AssignmentRule)) {
			throw new RuntimeException("Algebraic or Rate rules are not handled in the Virtual Cell at this time");
		} else {
			// Get the assignment rule and store it in the hashMap.
			AssignmentRule assignmentRule = (AssignmentRule)rule;
			Expression assignmentRuleMathExpr = getExpressionFromFormula(assignmentRule.getMath());
			assignmentRulesHash.put(assignmentRule.getVariable(), assignmentRuleMathExpr);
		}
	}
}

/**
 * 	getSpeciesConcUnitFactor : 
 * 		Calculates species concentration unit conversion from 'fromUnit' to 'toUnit'. 
 * 		If they are directly compatible, it computes the non-dimensional conversion factor/
 * 		If the 'fromUnit' is in item and 'toUnit' is in moles, it checks compatibility of fromUnit/KMOLE with toUnit.
 * 		Note : KMOLE is the Virtual VCell-defined reserved work (constant) = 1/602.
 * 		If the 'toUnit' is in item and 'fromUnit' is in moles, it checks compatibility of fromUnit*KMOLE with toUnit.
 * 		 
 * @param fromUnit
 * @param toUnit
 * @return	non-dimensional (numerical) conversion factor
 * @throws ExpressionException
 */
public static double getSpeciesConcUnitFactor(VCUnitDefinition fromUnit, VCUnitDefinition toUnit) throws ExpressionException {
		double factor = 1.0;
		double KMoleVal = ReservedSymbol.KMOLE.getExpression().evaluateConstant();
		
		if (fromUnit.isCompatible(toUnit)) {
			factor = fromUnit.convertTo(1.0, toUnit);
		} else if (fromUnit.divideBy(ReservedSymbol.KMOLE.getUnitDefinition()).isCompatible(toUnit)) {
			// if SBML substance unit is 'item'; VC substance unit is 'moles'
			fromUnit = fromUnit.divideBy(ReservedSymbol.KMOLE.getUnitDefinition());
			factor = factor/KMoleVal;
			factor = fromUnit.convertTo(factor, toUnit);
		} else if (fromUnit.multiplyBy(ReservedSymbol.KMOLE.getUnitDefinition()).isCompatible(toUnit)) {
			// if VC substance unit is 'item'; SBML substance unit is 'moles' 
			fromUnit = fromUnit.multiplyBy(ReservedSymbol.KMOLE.getUnitDefinition());
			factor = factor*KMoleVal;
			factor = fromUnit.convertTo(factor, toUnit);
		}  else {
			throw new RuntimeException("Unable to scale the species unit from: " + fromUnit + " -> " + toUnit.getSymbol());
		}
	    return factor;
}

	protected void addSpecies() {
		if (sbmlModel == null) {
			throw new RuntimeException("SBML model is NULL");
		}
		ListOf listOfSpecies = sbmlModel.getListOfSpecies();
		if (listOfSpecies == null) {
			System.out.println("No Spcecies");
			return;
		}
		SpeciesContext[] vcSpeciesContexts = new cbit.vcell.model.SpeciesContext[(int)sbmlModel.getNumSpecies()];	
		// Get species from SBMLmodel;  Add/get speciesContext
		try {
			// First pass - add the speciesContexts
			for (int i = 0; i < sbmlModel.getNumSpecies(); i++) {
				org.sbml.libsbml.Species sbmlSpecies = (org.sbml.libsbml.Species)listOfSpecies.get(i);
				// Sometimes, the species name can be null or a blank string; in that case, use species id as the name.
				String speciesName = sbmlSpecies.getId();
				cbit.vcell.model.Species vcSpecies = null;
				XMLNode speciesAnnotation = sbmlSpecies.getAnnotation();
				if (speciesAnnotation != null) {
					XMLNode embeddedElement = getEmbeddedElementInAnnotation(speciesAnnotation, SPECIES_NAME);
					if (embeddedElement != null) {
						// Get the species name from annotation and create the species.
						if (embeddedElement.getName().equals(cbit.vcell.xml.XMLTags.SpeciesTag)) {
							String vcSpeciesName = embeddedElement.getAttributes().getValue(cbit.vcell.xml.XMLTags.NameAttrTag);
							vcSpecies = simContext.getModel().getSpecies(vcSpeciesName);
							if (vcSpecies == null) {
								simContext.getModel().addSpecies(new cbit.vcell.model.Species(vcSpeciesName, vcSpeciesName));
								vcSpecies = simContext.getModel().getSpecies(vcSpeciesName);
							}
						}
						// if embedded element is not speciesTag, do I have to do something?
					} else {
						// Annotation element is present, but doesn't contain the species element.
						simContext.getModel().addSpecies(new cbit.vcell.model.Species(speciesName, speciesName));
						vcSpecies = simContext.getModel().getSpecies(speciesName);
					}
					MIRIAMHelper.setFromSBMLAnnotation(vcSpecies,XMLNode.convertXMLNodeToString(speciesAnnotation));
				} else {
					simContext.getModel().addSpecies(new cbit.vcell.model.Species(speciesName, speciesName));
					vcSpecies = simContext.getModel().getSpecies(speciesName);
				}
				
				// Get matching compartment name (of sbmlSpecies[i]) from feature list
				String compartmentId = sbmlSpecies.getCompartment();
				Structure structure = simContext.getModel().getStructure(compartmentId);
				simContext.getModel().addSpeciesContext(vcSpecies, structure);
				vcSpeciesContexts[i] = simContext.getModel().getSpeciesContext(vcSpecies, structure);
				vcSpeciesContexts[i].setHasOverride(true);
				vcSpeciesContexts[i].setName(speciesName);
				// System.out.println("ADDED SBML SPECIES : \"" + speciesName + "\" to VCModel");
			}

			// Second pass - fill in SpeciesContextSpec for each speciesContext
			for (int i = 0; i < sbmlModel.getNumSpecies(); i++) {
				org.sbml.libsbml.Species sbmlSpecies = (org.sbml.libsbml.Species)listOfSpecies.get(i);
				// Sometimes, the species name can be null or a blank string; in that case, use species id as the name.
				String speciesName = sbmlSpecies.getId();
				Compartment compartment = (Compartment)sbmlModel.getCompartment(sbmlSpecies.getCompartment());

				// Adjust units of species, convert to VC units.
				SpeciesContextSpec speciesContextSpec = simContext.getReactionContext().getSpeciesContextSpec(vcSpeciesContexts[i]);
				Structure spStructure = vcSpeciesContexts[i].getStructure();

				// Units in SBML, compute this using some of the attributes of sbmlSpecies
				int dimension = (int)sbmlModel.getCompartment(sbmlSpecies.getCompartment()).getSpatialDimensions();
				if (dimension == 0 || dimension == 1){
					logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, dimension+" dimensional compartment "+compartment.getId()+" not supported");
				}
				String spatialDimBuiltinName = getSpatialDimensionBuiltInName(dimension);

				String spatialSizeUnitStr = sbmlSpecies.getSpatialSizeUnits();
				if (spatialSizeUnitStr == null) {
					spatialSizeUnitStr = compartment.getUnits();
				}
				VCUnitDefinition spatialSizeUnit = getSBMLUnit(spatialSizeUnitStr, spatialDimBuiltinName); 
				String substanceUnitStr = sbmlSpecies.getSubstanceUnits();
				VCUnitDefinition substanceUnit = getSBMLUnit(substanceUnitStr, SBMLUnitTranslator.SUBSTANCE);
				VCUnitDefinition SBConcUnit = substanceUnit.divideBy(spatialSizeUnit);

				// To be used later in SBVCConcentrationUnit along with SBConcUnit.
				VCUnitDefinition vcUnit = null;
				if (spStructure instanceof Feature) {
					vcUnit = VCUnitDefinition.UNIT_uM;
				} else if (spStructure instanceof Membrane) {
					vcUnit = VCUnitDefinition.UNIT_molecules_per_um2;
				}
				// add the <sbmlSpName, sbvcSubstanceUnit> pair to speciesUnitsHash, to be used later (for validation testing)
				if (speciesUnitsHash.get(speciesName) == null) {
					SBVCConcentrationUnits sbvcSubstUnits = new SBVCConcentrationUnits(SBConcUnit, vcUnit);
					speciesUnitsHash.put(speciesName, sbvcSubstUnits);
				}
				
				Expression initExpr = null;
				if (sbmlSpecies.isSetInitialConcentration()) { 		// If initial Concentration is set
					Expression initConcentration = new Expression(sbmlSpecies.getInitialConcentration());
					SBMLUnitParameter unitFactor = SBMLUtils.getConcUnitFactor("spConcUnitFactor", SBConcUnit, vcUnit);
					initConcentration = Expression.mult(initConcentration, unitFactor.getExpression());
					initExpr = getValueFromRule(speciesName);
					if (initExpr == null) {
						initExpr = new Expression(initConcentration);
					} else {
						initExpr = Expression.mult(initExpr, unitFactor.getExpression());
					}
				} else if (sbmlSpecies.isSetInitialAmount()) {		// If initial amount is set
					double initAmount = sbmlSpecies.getInitialAmount();

					if (compartment.isSetSize()) {
						double compartmentSize = compartment.getSize();
						Expression initConcentration = new Expression(0.0);
						SBMLUnitParameter factor = null;
						if (compartmentSize != 0.0) {
							initConcentration = new Expression(initAmount / compartmentSize);
							factor = SBMLUtils.getConcUnitFactor("spConcUnitParam", SBConcUnit, vcUnit);
							initConcentration = Expression.mult(initConcentration, factor.getExpression());
						} else {
							logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "compartment "+compartment.getId()+" has zero size, unable to determine initial concentration for species "+speciesName);
						}
						initExpr = getValueFromRule(speciesName);
						if (initExpr == null) {
							initExpr = new Expression(initConcentration);
						} else {
							initExpr = Expression.mult(initExpr, factor.getExpression());
						}
					} else {
						logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.SPECIES_ERROR, " Compartment " + compartment.getId() + " size not set or is defined by a rule; cannot calculate initConc.");
					}
				} else {
					initExpr = getValueFromRule(speciesName);
					if (initExpr == null) {
						// System.out.println("no initial condition for species "+speciesName+", assuming 0.0");
						logger.sendMessage(VCLogger.MEDIUM_PRIORITY, VCLogger.UNIT_ERROR, "no initial condition for species "+speciesName+", assuming 0.0");
						initExpr = new Expression(0.0);
					}

					// Units for initial conc or amt if it is specified by an assignment rule
					SBMLUnitParameter factor = null;
					Expression adjustedFactorExpr = null;
					if (dimension != 0 && !sbmlSpecies.getHasOnlySubstanceUnits()) {
						// Init conc : 'hasOnlySubstanceUnits' should be false and spatial dimension of compartment should be non-zero.
						factor = SBMLUtils.getConcUnitFactor("spConcFactor", SBConcUnit, vcUnit);
						adjustedFactorExpr = factor.getExpression();
					} else if (dimension == 0 || sbmlSpecies.getHasOnlySubstanceUnits()) {
						// Init Amount : 'hasOnlySubstanceUnits' should be true or spatial dimension of compartment should zero.
						if (compartment.isSetSize()) {
							double compartmentSize = compartment.getSize();
							if (compartmentSize != 0.0) {
								// initConcentration := initAmount / compartmentSize
								factor = SBMLUtils.getConcUnitFactor("spConcFactor", SBConcUnit, vcUnit);
								adjustedFactorExpr = Expression.mult(factor.getExpression(), Expression.invert(new Expression(compartmentSize)));
							} else {
								logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "compartment "+compartment.getId()+" has zero size, unable to determine initial concentration for species "+speciesName);
							}
						} else {
							logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.SPECIES_ERROR, " Compartment " + compartment.getId() + " size not set or is defined by a rule; cannot calculate initConc.");
						}
					} // else, there is nothing else to happen.
					initExpr = Expression.mult(initExpr, adjustedFactorExpr);
				}

				// If any of the symbols in the expression for speciesConc is a rule, expand it.
				substituteGlobalParamRulesInPlace(initExpr, true);

				// PROBABLY DON't NEED THIS PART - WILL DELETE AFTER TESTING FURTHER.
//				cbit.vcell.parser.SymbolTable reservedSymbolTable= new ReservedSymbolTable(true);
//				try {
//					initExpr.bindExpression(reservedSymbolTable);
//					initExpr = initExpr.flatten();
//				} catch (ExpressionBindingException e) {
//					// logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.SPECIES_ERROR, " Species " + speciesName + " could not be added; the initial condition expression is a function of variables other than 'time'; this is not allowed for a non-spatial model.");
//					throw new RuntimeException("Species " + speciesName + " could not be added : it could have an assignment rule which is a function of other species or reserved spatial symbols (x, y z); this is not allowed for a non-spatial model in VCell.");
//				}

				speciesContextSpec.getInitialConditionParameter().setExpression(initExpr);
				speciesContextSpec.setConstant(sbmlSpecies.getBoundaryCondition() || sbmlSpecies.getConstant());
				// System.out.println("Completed SpeciesContextSpec for Species : \"" + speciesName + "\"");

				// Add the speciesContext names and sbmlSpecies names into SbmlVcSpeciesHash, so that it can be used while determining the structure
				// in which each reaction takes place.
				if (SbmlVcSpeciesHash.get(vcSpeciesContexts[i].getName()) == null) {
					SbmlVcSpeciesHash.put(vcSpeciesContexts[i].getName(), speciesName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error adding species context; "+ e.getMessage());
		}
	}

/**
 * addUnitDefinitions:
 *
 */
protected void addUnitDefinitions() {
	if (sbmlModel == null) {
		throw new RuntimeException("SBML model is NULL");
	}
	ListOf listofUnitDefns = sbmlModel.getListOfUnitDefinitions();
	if (listofUnitDefns == null) {
		System.out.println("No Unit Definitions");
		return;
	}
	for (int i = 0; i < sbmlModel.getNumUnitDefinitions(); i++) {
		UnitDefinition ud = (org.sbml.libsbml.UnitDefinition)listofUnitDefns.get(i);
		String unitName = ud.getId();
		VCUnitDefinition vcUnitDef = cbit.vcell.units.SBMLUnitTranslator.getVCUnitDefinition(ud);
		vcUnitsHash.put(unitName, vcUnitDef);
	}
}
/**
 *  checkCompartmentScaleFactorInRxnRateExpr :
 *		Used to check if reaction rate expression has a compartment scale factor. Need to remove this factor from the rate expression.
 *		Differentiate the rate expression wrt the compartmentSizeParamName. If the differentiated expression contains the compartmentSizeParamName,
 *		VCell doesn't support non-linear functions of compartmentSizeParam.
 *		Substitute 1.0 for the compartmentSizeParam in the rateExpr and check its equivalency with the differentiated expr above.
 *		If they are not equal, the rate expression is a non-linear function of compartmentSizeParam - not acceptable.
 *		Substitute 0.0 for compartmentSizeParam in rateExpr. If the value doesn't evaluate to 0.0, it is not valid for the same reason above.
 **/

private Expression removeCompartmentScaleFactorInRxnRateExpr(Expression rateExpr, String compartmentSizeParamName, String rxnName) throws Exception {
	Expression diffExpr = rateExpr.differentiate(compartmentSizeParamName).flatten();
	if (diffExpr.hasSymbol(compartmentSizeParamName)) {
		logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
	}

	Expression expr1 = rateExpr.getSubstitutedExpression(new Expression(compartmentSizeParamName), new Expression(1.0)).flatten();
	if (!expr1.compareEqual(diffExpr) && !(cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(expr1, diffExpr))) {
		logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
	}

	Expression expr0 = rateExpr.getSubstitutedExpression(new Expression(compartmentSizeParamName), new Expression(0.0)).flatten();
	if (!expr0.isZero()) {
		logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
	}

	return expr1;
}


/**
 * resolveRxnParameterNameConflicts :
 * 		Check if the reaction rate name matches with any global or local parameter, in which case, we have to change the rate name 
 * 		(to oldName_rxnName); since the global or	local parameter value will override the rate equation/value. Also, when we import 
 * 		a VCell model that has been exported to SBML, if the user has changed the rate name in a reaction, it is stored in the 
 * 		reaction annotation. This has to be retrieved and set as reaction rate name.
 * 		
 * @param sbmlRxn
 * @param newKinetics
 * @throws ExpressionException
 */
private void resolveRxnParameterNameConflicts(Reaction sbmlRxn, Kinetics vcKinetics) throws PropertyVetoException {
	XMLNode rxnAnnotation = sbmlRxn.getAnnotation();
	// If the name of the rate parameter has been changed by user, it is stored in rxnAnnotation. 
	// Retrieve this to re-set rate param name.
	if (rxnAnnotation != null) {
		XMLNode embeddedRxnElement = getEmbeddedElementInAnnotation(rxnAnnotation, RATE_NAME);
		String vcRateParamName = null;
		if (embeddedRxnElement != null) {
			if (embeddedRxnElement.getName().equals(XMLTags.RateTag)) {
				XMLAttributes attributes = embeddedRxnElement.getAttributes();
				vcRateParamName = attributes.getValue(XMLTags.NameAttrTag);
				vcKinetics.getAuthoritativeParameter().setName(vcRateParamName);
			}
		} 
	}

	/* Get the rate name from the kinetics : if it is from GeneralKinetics, it is the reactionRateParamter name;
	 * if it is from LumpedKinetics, it is the LumpedReactionRateParameter name.
	 */
	String origRateParamName = vcKinetics.getAuthoritativeParameter().getName();
	
	/* Check if any parameters (global/local) have the same name as kinetics rate param name;
	 * This will replace any rate expression with the global/local param value; which is unacceptable.
	 * If there is a match, replace it with a new name for rate param - say, origName_reactionName.
	 */
	ListOf listofGlobalParams = sbmlModel.getListOfParameters();
	for (int j = 0; j < sbmlModel.getNumParameters(); j++) {
		org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofGlobalParams.get(j);
		String paramName = param.getId();
		// Check if reaction rate param clashes with an existing (pre-defined) kinetic parameter - eg., reaction rate param 'J'
		// If so, change the name of the kinetic param (say, by adding reaction name to it).
		if (paramName.equals(origRateParamName)) {
			vcKinetics.getAuthoritativeParameter().setName(origRateParamName+"_"+cbit.util.TokenMangler.mangleToSName(sbmlRxn.getId()));
		}
	}

	KineticLaw kLaw = sbmlRxn.getKineticLaw();
	if (kLaw != null) {
		ListOf listofLocalParams = kLaw.getListOfParameters();
		for (int j = 0; j < kLaw.getNumParameters(); j++) {
			org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofLocalParams.get(j);
			String paramName = param.getId();
			// Check if reaction rate param clashes with an existing (pre-defined) kinetic parameter - eg., reaction rate param 'J'
			// If so, change the name of the kinetic param (say, by adding reaction name to it).
			if (paramName.equals(origRateParamName)) {
				vcKinetics.getAuthoritativeParameter().setName(origRateParamName+"_"+cbit.util.TokenMangler.mangleToSName(sbmlRxn.getId()));
			}
		}
	} 
}

/**
 *  getReferencedSpecies(Reaction , HashSet<String> ) :
 *  	Get the species referenced in sbmlRxn (reactants and products); store their names in hashSet (refereceNamesHash)
 *		Also, get the species referenced in the reaction kineticLaw expression from getReferencedSpeciesInExpr.
 * @param sbmlRxn
 * @param refSpeciesNameHash
 * @throws ExpressionException
 */
private void getReferencedSpecies(Reaction sbmlRxn, HashSet<String> refSpeciesNameHash) throws ExpressionException {
	// get all species referenced in listOfReactants
	for (int i = 0; i < (int)sbmlRxn.getNumReactants(); i++){
		SpeciesReference reactRef = sbmlRxn.getReactant(i);
		refSpeciesNameHash.add(reactRef.getSpecies());
	}
	// get all species referenced in listOfProducts
	for (int i = 0; i < (int)sbmlRxn.getNumProducts(); i++){
		SpeciesReference pdtRef = sbmlRxn.getProduct(i);
		refSpeciesNameHash.add(pdtRef.getSpecies());
	}
	// get all species referenced in reaction rate law
	if (sbmlRxn.getKineticLaw() != null) {
		Expression rateExpression = getExpressionFromFormula(sbmlRxn.getKineticLaw().getMath());
		getReferencedSpeciesInExpr(rateExpression, refSpeciesNameHash);
	} 
}

/**
 * getReferencedSpeciesInExpr(Expression , HashSet<String> ) : 
 * 		Recursive method to get species referenced in expression 'sbmlExpr' - takes care of cases where expressions
 * 		have symbols that are themselves expression and might contain other species.
 * @param sbmlExpr
 * @param refSpNamesHash
 * @throws ExpressionException
 */
private void getReferencedSpeciesInExpr(Expression sbmlExpr, HashSet<String> refSpNamesHash) throws ExpressionException {
	String[] symbols = sbmlExpr.getSymbols();
	for (int i = 0; symbols != null && i < symbols.length; i++) {
		Parameter sbmlParam = sbmlModel.getParameter(symbols[i]);
		if (sbmlParam != null){
			Expression paramExpression = getValueFromRule(sbmlParam.getId());
			if (paramExpression != null) {
				getReferencedSpeciesInExpr(paramExpression, refSpNamesHash);
			}
		}else{
			Species sbmlSpecies = sbmlModel.getSpecies(symbols[i]);
			if (sbmlSpecies!=null){
				refSpNamesHash.add(sbmlSpecies.getId());
			}
		}
	}
}

/**
 * substituteGlobalParamRulesInPlace:
 * @param sbmlExpr
 * @param expandedExpr
 * @throws ExpressionException
 */
private void substituteGlobalParamRulesInPlace(Expression sbmlExpr, boolean bReplaceValues) throws ExpressionException {
	boolean bParamChanged = true;
	while (bParamChanged) {
		bParamChanged = false;
		String[] symbols = sbmlExpr.getSymbols();
		for (int i = 0; symbols != null && i < symbols.length; i++) {
			Parameter sbmlParam = sbmlModel.getParameter(symbols[i]);
			if (sbmlParam != null){
				Expression paramExpression = getValueFromRule(sbmlParam.getId());
				if (paramExpression != null) {
					sbmlExpr.substituteInPlace(new Expression(sbmlParam.getId()), paramExpression);
					bParamChanged = true;
				} else if (bReplaceValues) {
					sbmlExpr.substituteInPlace(new Expression(sbmlParam.getId()), new Expression(sbmlParam.getValue()));
				}
			}
		}
	}
}

/**
 * 	@ TODO: This method doesn't take care of adjusting species in nested parameter rules with the species_concetration_factor.
 * @param kinetics
 * @param paramExpr
 * @throws ExpressionException
 */
private void substituteOtherGlobalParams(Kinetics kinetics, Expression paramExpr) throws ExpressionException, PropertyVetoException {
	String[] exprSymbols = paramExpr.getSymbols();
	if (exprSymbols == null || exprSymbols.length == 0) {
		return;
	}
	for (int kk = 0; kk < exprSymbols.length; kk++) {
		Expression expr = globalParamsHash.get(exprSymbols[kk]);
		if (expr != null) {
			Expression newExpr = new Expression(expr);
			substituteGlobalParamRulesInPlace(newExpr, false);
			// param has constant value, add it as a kinetic parameter if it is not already in the kinetics
			kinetics.setParameterValue(exprSymbols[kk], newExpr.infix());
			kinetics.getKineticsParameter(exprSymbols[kk]).setUnitDefinition(getSBMLUnit(sbmlModel.getParameter(exprSymbols[kk]).getUnits(), null));
			if (newExpr.getSymbols() != null) {
				substituteOtherGlobalParams(kinetics, newExpr);
			}
		}
	}
}

public BioModel getBioModel() {
	// Read SBML model into libSBML SBMLDocument and create an SBML model
	SBMLReader reader = new SBMLReader();
	SBMLDocument document = reader.readSBMLFromString(sbmlString);
	
	long numProblems = document.getNumErrors();
	System.out.println("\n Num problems in original SBML document : " + numProblems + "\n");

	sbmlModel = document.getModel();
	
	// Convert SBML Model to VCell model
	// An SBML model will correspond to a simcontext - which needs a Model and a Geometry
	// SBML handles only nonspatial geometries at this time, hence creating a non-spatial default geometry
	String modelName = sbmlModel.getId();
	if (modelName == null || modelName.trim().equals("")) {
		modelName = sbmlModel.getName();
	} 
	cbit.vcell.model.Model vcModel = null;
	if (modelName != null) {
		vcModel = new cbit.vcell.model.Model(modelName);
	} else {
		vcModel = new cbit.vcell.model.Model("newModel");
	}
	Geometry geometry = new Geometry("Compartmental", 0);
	try {
		simContext = new SimulationContext(vcModel, geometry);
		simContext.setName(simContext.getModel().getName()+"_"+simContext.getGeometry().getName());
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not create simulation context corresponding to the input SBML model");
	}
	translateSBMLModel();

	// Create the Biomodel with the simContext and return
	cbit.vcell.biomodel.BioModel bioModel = null;
	try {
		bioModel = new BioModel(null);
		bioModel.setModel(simContext.getModel());
		bioModel.setSimulationContexts(new SimulationContext[] {simContext});			
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not create Biomodel");
	}
	
	MIRIAMHelper.setFromSBMLAnnotation(bioModel,sbmlModel.getAnnotationString());
	bioModel.refreshDependencies();
	return bioModel;
}

/**
 *  getEmbeddedElementInRxnAnnotation :
 *  Takes the reaction annotation as an argument and returns the embedded element  (fluxstep or simple reaction), if present.
 */
private XMLNode getEmbeddedElementInAnnotation(XMLNode annotationNode, String tag) {
	// Get the XML element corresponding to the annotation xmlString.
	XMLNode embeddedElement = null;
	XMLNode vcInfoElement = null;
	for (int i=0;i<annotationNode.getNumChildren();i++){
		XMLNode annotationChildNode = annotationNode.getChild(i);
		if (annotationChildNode.getName().equals(XMLTags.VCellInfoTag)){
			vcInfoElement = annotationChildNode;
		}
	}
	String elementName = null;
	if (tag.equals(RATE_NAME)) {
		elementName = XMLTags.RateTag;
	} else if (tag.equals(SPECIES_NAME)) {
		elementName = XMLTags.SpeciesTag;
	} else if (tag.equals(REACTION)) {
		elementName = XMLTags.FluxStepTag;
		if (elementName == null) {
			elementName = XMLTags.SimpleReactionTag;
		}
	}
	// If there is an annotation element for the reaction or species, retrieve and return.
	if (vcInfoElement != null) {
		for (int j = 0; j < vcInfoElement.getNumChildren(); j++) {
			XMLNode infoChild = vcInfoElement.getChild(j);
			if (infoChild.getName().equals(elementName)){
				return infoChild;
			}
		}
	}	
	return embeddedElement;
}


/**
 *  getExpressionFromFormula : 
 *	Convert the math formula string in a kineticLaw, rule or lambda function definition into MathML
 *	and use ExpressionMathMLParser to convert the MathML into an expression to be brought into the VCell.
 *	NOTE : ExpressionMathMLParser will handle only the <apply> elements of the MathML string,
 *	hence the ExpressionMathMLParser is given a substring of the MathML containing the <apply> elements. 
 */
private Expression getExpressionFromFormula(ASTNode math) throws ExpressionException {
//	MathMLDocument mDoc = new MathMLDocument();
//	mDoc.setMath(math.deepCopy());
	String mathMLStr = libsbml.writeMathMLToString(math);
	cbit.vcell.parser.ExpressionMathMLParser exprMathMLParser = new cbit.vcell.parser.ExpressionMathMLParser(lambdaFunctions);
	Expression expr =  exprMathMLParser.fromMathML(mathMLStr);
	return expr;
}

public Hashtable<String, SBVCConcentrationUnits> getSpeciesUnitsHash()  {
	return speciesUnitsHash;
}

public double getSBMLTimeUnitsFactor() {
	double timeFactor = 1.0;
	VCUnitDefinition timeUnits = getSBMLUnit("", SBMLUnitTranslator.TIME);
	if (timeUnits.isCompatible(VCUnitDefinition.UNIT_s)) {
		timeFactor = timeUnits.convertTo(timeFactor, VCUnitDefinition.UNIT_s);
	} else {
		
	}
	return timeFactor;
}
/**
 * getReactionStructure :
 */
private Structure getReactionStructure(org.sbml.libsbml.Reaction sbmlRxn, SpeciesContext[] speciesContexts) throws Exception {
    Structure struct = null;

    // Check annotation for reaction - if we are importing an exported VCell model, it will contain annotation for reaction.
    // If annotation has structure name, return the corresponding structure.
    XMLNode rxnAnnotation = sbmlRxn.getAnnotation();
    String structName = null;
    if (rxnAnnotation != null) {
        // Get the embedded element in the annotation str (fluxStep or simpleReaction), and the structure attribute from the element.
        XMLNode embeddedElement = getEmbeddedElementInAnnotation(rxnAnnotation, REACTION);
        if (embeddedElement != null) {
        	XMLAttributes attributes = embeddedElement.getAttributes();
            structName = attributes.getValue(cbit.vcell.xml.XMLTags.StructureAttrTag);
	        // Using the structName, get the structure from the structures (compartments) list.
	        struct = simContext.getModel().getStructure(structName);
	        return struct;
        }
    }

    HashSet<String> refSpeciesNameHash = new HashSet<String>(); 
    getReferencedSpecies(sbmlRxn, refSpeciesNameHash);
    
    java.util.Iterator<String> refSpIterator = refSpeciesNameHash.iterator();
    HashSet<String> compartmentNamesHash = new HashSet<String>();
    while (refSpIterator.hasNext()) {
    	String spName = refSpIterator.next();
    	String rxnCompartmentName = sbmlModel.getSpecies(spName).getCompartment();
    	compartmentNamesHash.add(rxnCompartmentName);
    }
    
    if (compartmentNamesHash.size() == 1) {
    	struct = simContext.getModel().getStructure(compartmentNamesHash.iterator().next());
    	return struct;
    } else {
    	// Check adjacency of compartments of reactants/products/modifiers
    	if (compartmentNamesHash.size() > 3) {
    		logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.COMPARTMENT_ERROR, "Cannot resolve location of reaction : " + sbmlRxn.getId());
    	}
    	String[] compNames = compartmentNamesHash.toArray(new String[compartmentNamesHash.size()]);
    	if (compNames.length == 2) {
			Compartment compartment1 = sbmlModel.getCompartment(compNames[0]);
			Compartment compartment2 = sbmlModel.getCompartment(compNames[1]);
			boolean bAdjacent = compartment1.getOutside().equals(compartment2.getId()) || compartment2.getOutside().equals(compartment1.getId());
    		if ((compartment1.getSpatialDimensions() == 2  && compartment2.getSpatialDimensions() == 3) && bAdjacent) {
    			struct = simContext.getModel().getStructure(compartment1.getId());
    		} else if ((compartment2.getSpatialDimensions() == 2  && compartment1.getSpatialDimensions() == 3) && bAdjacent) {
    			struct = simContext.getModel().getStructure(compartment2.getId());
    		} else if (compartment1.getSpatialDimensions() == 3  && compartment2.getSpatialDimensions() == 3) {
    			Compartment outside1 = null;
    			Compartment outside2 = null;
    			if (compartment1.isSetOutside()) {
    				outside1 = sbmlModel.getCompartment(compartment1.getOutside());
    			} 
    			if (compartment2.isSetOutside()) {
    				outside2 = sbmlModel.getCompartment(compartment2.getOutside());
    			}
				if ( (outside1 != null) && ((outside1.getSpatialDimensions() == 2) && (compartment2.getId().equals(outside1.getOutside()))) ) {
					struct = simContext.getModel().getStructure(outside1.getId());
				} else if ( (outside2 != null) && ((outside2.getSpatialDimensions() == 2) && (compartment1.getId().equals(outside2.getOutside()))) ) {
					struct = simContext.getModel().getStructure(outside2.getId());				
				}
    		}
    	} else if (compNames.length == 3) {
    		int dim2 = 0;
    		int dim3 = 0;
    		int membraneIndx = -1;
    		for (int i = 0; i < compNames.length; i++) {
    			Compartment comp = sbmlModel.getCompartment(compNames[i]);
    			if (comp.getSpatialDimensions() == 2) {
    				dim2++;
    				membraneIndx = i;
    			} else if (comp.getSpatialDimensions() == 3) {
    				dim3++;
    			}
			}
    		if (dim2 != 1 || dim3 != 2) {
    	   		logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.COMPARTMENT_ERROR, "Cannot resolve location of reaction : " + sbmlRxn.getId());
    		}
    		Compartment membraneComp = sbmlModel.getCompartment(compNames[membraneIndx]);
    		Compartment volComp1 = sbmlModel.getCompartment(compNames[(membraneIndx+1)%3]);
    		Compartment volComp2 = sbmlModel.getCompartment(compNames[(membraneIndx+2)%3]);
    		if ( (volComp1.getId().equals(membraneComp.getOutside()) && membraneComp.getId().equals(volComp2.getOutside())) ||  
    				(volComp2.getId().equals(membraneComp.getOutside()) && membraneComp.getId().equals(volComp1.getOutside())) ) {
    					struct = simContext.getModel().getStructure(membraneComp.getId());
    		} 
    	} 
    	if (struct == null) {
	   		logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.COMPARTMENT_ERROR, "Cannot resolve location of reaction : " + sbmlRxn.getId());
    	} 
    	return struct;
    }
}


/**
 *  getSBMLUnits : 
 */
private VCUnitDefinition getSBMLUnit(String unitSymbol, String builtInName) {
	//
	// Check to see if 'unitSymbol' is a base unit (one of a list of unitKinds) or 
	// built-in unit (substance, volume, area, length, time) or
	// is defined in the list of unit definitions
	//

	VCUnitDefinition SbmlUnit = null;

	if (unitSymbol == null || unitSymbol.equals("")) {
		if (builtInName != null) {
			SbmlUnit = (VCUnitDefinition)vcUnitsHash.get(builtInName);
			if (SbmlUnit == null) {
				SbmlUnit = cbit.vcell.units.SBMLUnitTranslator.getDefaultSBMLUnit(builtInName);
			}
		} else if (builtInName == null) {
			SbmlUnit = VCUnitDefinition.UNIT_TBD;
		}
	} else {
		if (org.sbml.libsbml.Unit.isUnitKind(unitSymbol,level,version)) {
			SbmlUnit = VCUnitDefinition.getInstance(unitSymbol);
		} else if (org.sbml.libsbml.Unit.isBuiltIn(unitSymbol,level)) {
			//check if its a built-in unit that was explicitly specified
			if (builtInName != null) {
				SbmlUnit = (VCUnitDefinition)vcUnitsHash.get(builtInName);
				if (SbmlUnit == null) { 
					SbmlUnit = cbit.vcell.units.SBMLUnitTranslator.getDefaultSBMLUnit(builtInName);
				}
			} else {
				SbmlUnit = (VCUnitDefinition)vcUnitsHash.get(unitSymbol);
			}
		} else {
			SbmlUnit = (VCUnitDefinition)vcUnitsHash.get(unitSymbol);
		}
	}
	if (SbmlUnit == null) {
		System.err.println("SBML unit not found or not supported: " + unitSymbol);    //allow nulls for params.
		SbmlUnit = VCUnitDefinition.UNIT_TBD;
	}

	return SbmlUnit;
}


/**
 *  getSpatialDimentionBuiltInName : 
 */
private String getSpatialDimensionBuiltInName(int dimension) {
	String name = null;
	switch (dimension) {
		case 0 : {
			name = SBMLUnitTranslator.DIMENSIONLESS;
			break;
		}
		case 1 : {
			name = SBMLUnitTranslator.LENGTH;
			break;
		}
		case 2 : {
			name = SBMLUnitTranslator.AREA;
			break;
		}
		case 3 : {
			name = SBMLUnitTranslator.VOLUME;
			break;
		}						
	}
	return name;
}


/**
 *  getValueFromRuleOrFunctionDefinition : 
 *	If the value of a kinetic law parameter or species initial concentration/amount (or compartment volume)
 *	is 0.0, check if it is given by a rule or functionDefinition, and return the string (of the rule or
 *	functionDefinition expression).
 */
private Expression getValueFromRule(String paramName)  {
	Expression valueExpr = null;
	// Check if param name has an assignment rule associated with it
	for (int i = 0; i < assignmentRulesHash.size(); i++) {
		valueExpr = (Expression)assignmentRulesHash.get(paramName);
		if (valueExpr != null) {
			return valueExpr;
		}
	}
	return valueExpr;
}

/**
 * checkForUnsupportedVCellFeatures:
 * 
 * Check if SBML model has algebraic, rate rules, events, other functionality that VCell does not support, 
 * such as: 'hasOnlySubstanceUnits'; compartments with dimension 0; species that have assignment rules that contain other species, etc.
 * If so, stop the import process, since there is no point proceeding with the import any further.
 * 
 */
private void checkForUnsupportedVCellFeatures() throws Exception {
	
	// Check if rules, if present, are algrbraic or rate rules
	if (sbmlModel.getNumRules() > 0) {
		for (int i = 0; i < sbmlModel.getNumRules(); i++){
			Rule rule = (org.sbml.libsbml.Rule)sbmlModel.getRule((long)i);
			if (rule instanceof AlgebraicRule) {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNSUPPORED_ELEMENTS_OR_ATTS, "Algebraic rules are not handled in the Virtual Cell at this time");
			}  else if (rule instanceof RateRule) {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNSUPPORED_ELEMENTS_OR_ATTS, "Rate rules are not handled in the Virtual Cell at this time");
			}
		}
	}
	// Check if events are present.
	if (sbmlModel.getNumEvents() > 0) {
		logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNSUPPORED_ELEMENTS_OR_ATTS, "VCell doesn't support Events at this time");
	}
	// Check if species are specified by assignemnt rules; and if they refer to other species ...
	if (sbmlModel.getNumRules() > 0) {
		for (int i = 0; i < sbmlModel.getNumRules(); i++){
			Rule rule = (org.sbml.libsbml.Rule)sbmlModel.getRule((long)i);
			if (rule instanceof AssignmentRule) {
				// Check if assignment rule variable is a species. 
				AssignmentRule assignRule = (AssignmentRule)rule;
				Species ruleSpecies = sbmlModel.getSpecies(assignRule.getVariable());
				if (ruleSpecies != null) {
					Expression assignRuleMathExpr = getExpressionFromFormula(assignRule.getMath());
					// if the rule variable is a species, check if rule math refers to other species; if so, throw exception - can't handle it in VCell.
					if (assignRuleMathExpr != null) {
						for (int j = 0; j < sbmlModel.getNumSpecies(); j++) {
							Species sp = (Species)sbmlModel.getSpecies((long)j);
							if (sp.getId().equals(ruleSpecies.getId())) {
								continue;
							} else {
								if (assignRuleMathExpr.hasSymbol(sp.getId())) {
									logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.SPECIES_ERROR, "An assignment rule for species " + ruleSpecies.getId() + " contains another species in the model, this is not allowed for a non-spatial model in VCell");
								} else if (assignRuleMathExpr.hasSymbol(ReservedSymbol.X.getName()) || 
										   assignRuleMathExpr.hasSymbol(ReservedSymbol.Y.getName()) || 
										   assignRuleMathExpr.hasSymbol(ReservedSymbol.Z.getName())) {
									logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.SPECIES_ERROR, "An assignment rule for species " + ruleSpecies.getId() + " contains reserved spatial variable(s) (x,y,z), this is not allowed for a non-spatial model in VCell");
								}
							}
						}
					}
				}
			} 
		}
	}
	// Check if any of the species have 'hasOnlySubstanceUnits set:
//	for (int j = 0; j < (int)sbmlModel.getNumSpecies(); j++) {
//		Species sp = (Species)sbmlModel.getSpecies(j);
//		if (sp.getHasOnlySubstanceUnits()) {
//			logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.SPECIES_ERROR, "Species " + sp.getId() + " has 'hasOnlySubstanceUnits' set to true, this is not supported in VCell");
//		}
//	}
	// Check if any of the compartments have spatial dimension 0
	for (int i = 0; i < (int)sbmlModel.getNumCompartments(); i++) {
		Compartment comp = (Compartment)sbmlModel.getCompartment(i);
		if (comp.getSpatialDimensions() == 0) {
			logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.COMPARTMENT_ERROR, "Compartment " + comp.getId() + " has spatial dimension 0; this is not supported in VCell");
		}
	}
}

/**
 * translateSBMLModel:
 *
 */
public void translateSBMLModel() {
	// Check for SBML features not supported in VCell; stop import process if present.
	try {
		checkForUnsupportedVCellFeatures();
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	
	// Create Virtual Cell Model with species, compartment, etc. and read in the 'values' from the SBML model
	// Add Function Definitions (Lambda functions).
	addFunctionDefinitions();
	// Add Unit definitions
	addUnitDefinitions();
	// Add compartmentTypes (not handled in VCell)
	addCompartmentTypes();
	// Add spciesTypes (not handled in VCell)
	addSpeciesTypes();
	// Add Rules
	try {
		addRules();
	} catch (ExpressionException ee) {
		ee.printStackTrace(System.out);
		throw new RuntimeException(ee.getMessage());
	}
	// Add features/compartments
	addCompartments();
	// Add species/speciesContexts
	addSpecies(); 
	// Add Parameters
	addParameters();
	// Add InitialAssignments 
	addInitialAssignments();
	// Add constraints (not handled in VCell)
	addConstraints();
	// Add Reactions
	addReactions();
	// Add Events
	addEvents();
}
}