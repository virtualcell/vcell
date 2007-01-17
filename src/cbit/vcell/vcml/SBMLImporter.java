package cbit.vcell.vcml;
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
import java.util.TreeMap;
import java.util.Vector;

import org.jdom.*;
import org.sbml.libsbml.*;

import cbit.util.BeanUtils;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.model.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.LambdaFunction;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.SBMLUnitTranslator;
import cbit.util.xml.VCLogger;
import cbit.vcell.vcml.TranslationMessage;
import cbit.vcell.xml.XMLTags;

public class SBMLImporter {

	private String sbmlString = null;
	private org.sbml.libsbml.Model sbmlModel = null;
	private cbit.vcell.mapping.SimulationContext simContext = null;
	private LambdaFunction[] lambdaFunctions = null;
	private java.util.HashMap assignmentRulesHash = new java.util.HashMap();
	private TreeMap vcUnitsHash = new TreeMap();
	private java.util.Hashtable SbmlVcSpeciesHash = new java.util.Hashtable();

	private cbit.util.xml.VCLogger logger = null;
	 
	private static String RATE_NAME = XMLTags.RateTag;
	private static String SPECIES_NAME = XMLTags.SpeciesTag;
	private static String REACTION = XMLTags.ReactionTag;
	private static String SIMPLE_RXN = XMLTags.SimpleReactionTag;
	 
	static
	{
		System.loadLibrary("sbmlj");
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
	Vector structVector = new Vector();
	java.util.HashMap structureNameMap = new java.util.HashMap();
	try {
		// First pass - create the structures
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			org.sbml.libsbml.Compartment compartment = (org.sbml.libsbml.Compartment)listofCompartments.get(i);
			// Sometimes, the compartment name can be null; in that case, use compartment id as the name.
			String compartmentName = getActualName(compartment);
			if (compartment.getSpatialDimensions() == 3) {
				structVector.insertElementAt(new Feature(compartmentName), i);
				structureNameMap.put(compartmentName, (Feature)structVector.elementAt(i));
			} else if (compartment.getSpatialDimensions() == 2) {
				structVector.insertElementAt(new Membrane(compartmentName), i);
				structureNameMap.put(compartmentName, (Membrane)structVector.elementAt(i));
			} else {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.COMPARTMENT_ERROR, "Cannot deal with spatial dimension : " + compartment.getSpatialDimensions() + " for compartments at this time.");
				throw new RuntimeException("Cannot deal with spatial dimension : " + compartment.getSpatialDimensions() + " for compartments at this time");
			}
		}

		// Second pass - connect the structures
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			org.sbml.libsbml.Compartment compartment = (org.sbml.libsbml.Compartment)listofCompartments.get(i);
			String compartmentName = getActualName(compartment);
			if (compartment.getOutside() != null && compartment.getOutside().length() > 0) {
				String outsideCompartmentName = compartment.getOutside();
				Structure outsideStructure = (Structure)structureNameMap.get(outsideCompartmentName);
				if (compartment.getSpatialDimensions() == 3) {
					// Check if outsideStructure is a membrane. If not, we have to add a membrane between the compartments, 
					// since VCell requires that 2 features need to be separated by a membrane, and 2 membranes by a feature.
					Feature feature = (Feature)structVector.elementAt(i);
					if (outsideStructure instanceof Membrane) {
						// If feature, set the parent structure (outside structure) only; i.e., the bounding membrane.
						feature.setParentStructure(outsideStructure);
						// Also, set the inside feature of the bounding membrane to this feature.
						((Membrane)outsideStructure).setInsideFeature(feature);
					} else {
						// VCell doesn't permit the parent structure of a feature to be a feature.
						// hence we add a membrane in between.
						Membrane newMembrane = new Membrane(feature.getName() + "_membrane");
						// add this new membrane to structsVector and the structureNamesMap
						structVector.addElement(newMembrane);
						structureNameMap.put(newMembrane.getName(), newMembrane);
						// set this membrane as the parent for given structure, and set it as inside structure for the outer feature
						feature.setParentStructure(newMembrane);
						newMembrane.setInsideFeature(feature);
						newMembrane.setParentStructure(outsideStructure);
					}
				} else if (compartment.getSpatialDimensions() == 2) {
					// If membrane, need to set both inside and outside feature. Inside feature will be set by the
					// compartment for which this membrane is the outside (bounding) structure.
					((Membrane)structVector.elementAt(i)).setParentStructure(outsideStructure);
				}
			}
		}

		//
		// Check if the # of structures in structVector are the same as the # of compartments in the sbmlModel.
		// It is possible that a membrane(s) might have been added if the compartments in sbmlModel are only features 
		// and are not separated by a membrane. This is added only to structsVector, in the previous pass, now add it to
		// the sbmlModel
		//
		if (structVector.size() != (int)sbmlModel.getNumCompartments()) {
			for (int i = 0; i < structVector.size(); i++){
				Structure struct = (Structure)structVector.elementAt(i);
				Compartment compartment = sbmlModel.getCompartment(struct.getName());
				if (compartment == null) {
					// this compartment was added in the second pass above, hence we need to add it to the sbmlModel
					compartment = new Compartment(struct.getName());
					if (struct instanceof Membrane) {
						compartment.setSpatialDimensions(2);
					} else if (struct instanceof Feature) {
						compartment.setSpatialDimensions(3);
					}
					if (struct.getParentStructure() != null) {
						compartment.setOutside(struct.getParentStructure().getName());
					}
			// ****		System.err.println("CompartmentSize set : " + compartment.isSetSize());
					sbmlModel.addCompartment(compartment);
				}
			}
		}

		// set the structures in vc simContext
		Structure[] structures = (Structure[])BeanUtils.getArray(structVector, Structure.class);
		simContext.getModel().setStructures(structures);
		simContext.getModel().getTopFeature();

	} catch (Exception e) {
		System.out.println("Error adding Feature to vcModel " + e.getMessage());
		throw new RuntimeException("Error adding Feature to vcModel " + e.getMessage());
	}
}


/**
 * Warn user that we do not handle events now. Give a choice between bringing in the model without the events or cancelling the operation.
 *
 *
 **/

protected void addEvents() throws Exception {
	if (sbmlModel.getNumEvents() > 0) {
		logger.sendMessage(cbit.vcell.client.TranslationLogger.HIGH_PRIORITY, TranslationMessage.UNSUPPORED_ELEMENTS_OR_ATTS, "VCell doesn't support Events at this time");
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
			String functionName = getActualName(fnDefn);
			ASTNode math = null;
			String formula = null;
			Vector argsVector = new Vector();
			String[] functionArgs = null;
			if (fnDefn.isSetMath()) {
				math = fnDefn.getMath();
				// Add function arguments into vector, print args 
				if (math.getNumChildren() > 1) {
					argsVector.addElement(math.getLeftChild().getName());
					for (int j = 1; j < math.getNumChildren() - 1; ++j) {
						argsVector.addElement(math.getLeftChild().getName());
					}
				}
			}
			functionArgs = (String[])BeanUtils.getArray(argsVector, String.class);
			// Function body. 
			if (math.getNumChildren() == 0) {
				System.out.println("(no function body defined)");
			} else {
				math = math.getChild(math.getNumChildren() - 1);
				formula = libsbml.formulaToString(math);
			}
			Expression fnExpr = getExpressionFromFormula(formula);
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
	}


/**
 *  addReactionParticipant :
 *		Adds reactants and products and modifiers to a reaction.
 *		Input args are the sbml reaction, vc reaction
 *		This method was created mainly to handle reactions where there are reactants and/or products that appear multiple times
 *		in a reaction. Virtual Cell does not allow the import of such reactions.
 *		As a work-around, we will try to find any such duplicate reactants/products and try to place them as reactant or product
 *		and change their stoichiometry.
 *			For example : A + A -> B will be represented as 2*A -> B
 *
 *		A rough outline of the algorithm :
 *		Multiply by -1 for every occurance of a species as reactant, multiply by 1 if it occurs as a product, and 0 if it is a modifier.
 *		The total stoichiometry for the species n = total_reactant + total_pdt + total_modifier.
 *		If n < 0; add species as reactant
 *		   n = 0; add species as modifier
 *		   n > 0; add species as product.
 *		
**/

protected void addReactionParticipants(org.sbml.libsbml.Reaction sbmlRxn, ReactionStep vcRxn) throws Exception {

	if (vcRxn instanceof FluxReaction) {
		System.out.println("Already added flux participants!");
		return;
	}
	
	int reactantNum;	// will be (-1 * stoichiometry_of_species) for every occurance of species as reactant
	int pdtNum;			// will be (+1 * stoichiometry_of_species) for every occurance of species as product.
	int modifierNum;	// 
	boolean bSpeciesPresent;
	SpeciesContext[] vcSpeciesContexts = simContext.getModel().getSpeciesContexts();

	// for each species in the sbml model,
	for (int i = 0; i < (int)sbmlModel.getNumSpecies(); i++){
		org.sbml.libsbml.Species sbmlSpecies = sbmlModel.getSpecies(i);
		bSpeciesPresent = false;
		reactantNum = 0;
		pdtNum = 0;
		modifierNum = 0;
		
		// check if it is present as reactant, if so, how many reactants
		for (int j = 0; j < (int)sbmlRxn.getNumReactants(); j++){
			SpeciesReference spRef = sbmlRxn.getReactant(j);
			// If stoichiometry of speciesRef is not an integer, it is not handled in the VCell at this time; no point going further
			if ( !((int)(spRef.getStoichiometry()) == spRef.getStoichiometry()) ) {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.REACTION_ERROR, "Non-integer stoichiometry not handled in VCell at this time.");
			}
			if (spRef.getSpecies().equals(sbmlSpecies.getId())) {
				reactantNum += (-1 * (int)spRef.getStoichiometry());
				bSpeciesPresent = true;
			}
		}

		// check if it is present as product, if so, how many products
		for (int j = 0; j < (int)sbmlRxn.getNumProducts(); j++){
			SpeciesReference spRef = sbmlRxn.getProduct(j);
			// If stoichiometry of speciesRef is not an integer, it is not handled in the VCell at this time; no point going further
			if ( !((int)(spRef.getStoichiometry()) == spRef.getStoichiometry()) ) {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.REACTION_ERROR, "Non-integer stoichiometry not handled in VCell at this time.");
			}
			if (spRef.getSpecies().equals(sbmlSpecies.getId())) {
				pdtNum  += (1 * (int)spRef.getStoichiometry());
				bSpeciesPresent = true;
			}
		}

		// check if it is present as modifier, if so, how many modifiers
		for (int j = 0; j < (int)sbmlRxn.getNumModifiers(); j++){
			ModifierSpeciesReference spRef = sbmlRxn.getModifier(j);
			if (spRef.getSpecies().equals(sbmlSpecies.getId())) {
				modifierNum++;
				bSpeciesPresent = true;
			}
		}

		// if species is not present, go to next species in model.
		if (!bSpeciesPresent) {
			continue;
		}

		// get the matching speciesContext for the sbmlSpecies
		SpeciesContext speciesContext = getMatchingSpeciesContext(getActualName(sbmlSpecies), vcSpeciesContexts);

		// Using the algorithm given above (in the comments), compute 'n' - even though we don't need modifierNum since it is multiplied by 0, using it for clarity. 
		int n = reactantNum + pdtNum + (0 * modifierNum);
		
		// Depending on n, add species as reactant, product or catalyst to the vcReaction.
		try {
			if (n < 0) {
				((SimpleReaction)vcRxn).addReactant(speciesContext, (-1*n));
			} else if (n > 0) {
				((SimpleReaction)vcRxn).addProduct(speciesContext, n);
			} else if (n == 0) {
				((SimpleReaction)vcRxn).addCatalyst(speciesContext);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Could not add reaction participant : " + speciesContext.getName() + " for reaction : " + vcRxn.getName());
		}
	}
}


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
				Structure reactionStructure = getReactionStructure(sbmlRxn, vcSpeciesContexts); 
				String rxnName = getActualName(sbmlRxn);
				// Check of reaction annotation is present; if so, does it have an embedded element (flux or simpleRxn).
				// Create a fluxReaction or simpleReaction accordingly.
				String rxnAnnotation = sbmlRxn.getAnnotation();
				org.jdom.Element embeddedRxnElement = null;
				if (rxnAnnotation != null && rxnAnnotation.length() > 0) {
					embeddedRxnElement = getEmbeddedElementInAnnotation(rxnAnnotation, REACTION);
					if (embeddedRxnElement != null) {
						if (embeddedRxnElement.getName().equals(XMLTags.FluxStepTag)) {
							// If embedded element is a flux reaction, set flux reaction's strucure, flux carrier, physicsOption from the element attibutes.
							String structName = embeddedRxnElement.getAttributeValue(XMLTags.StructureAttrTag);
							Structure struct = simContext.getModel().getStructure(structName);
							if (!(struct instanceof Membrane)) {
								throw new RuntimeException("Appears that the flux reaction is not occuring on a membrane.");
							}
							String fluxCarrierSpName = embeddedRxnElement.getAttributeValue(XMLTags.FluxCarrierAttrTag);
							cbit.vcell.model.Species fluxCarrierSp = simContext.getModel().getSpecies(fluxCarrierSpName);
							if (fluxCarrierSp == null) {
								fluxCarrierSp = new cbit.vcell.model.Species(fluxCarrierSpName, fluxCarrierSpName);
								simContext.getModel().addSpecies(fluxCarrierSp);
							}
							vcReactions[i] = new FluxReaction((Membrane)struct, fluxCarrierSp, simContext.getModel(), rxnName);
							// Set the fluxOption on the flux reaction based on whether it is molecular, molecular & electrical, electrical.
							String fluxOptionStr = embeddedRxnElement.getAttributeValue(XMLTags.FluxOptionAttrTag);
							if (fluxOptionStr.equals(XMLTags.FluxOptionMolecularOnly)) {
								((FluxReaction)vcReactions[i]).setPhysicsOptions(ReactionStep.PHYSICS_MOLECULAR_ONLY);
							} else if (fluxOptionStr.equals(XMLTags.FluxOptionMolecularAndElectrical)) {
								((FluxReaction)vcReactions[i]).setPhysicsOptions(ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL);
							} else if (fluxOptionStr.equals(XMLTags.FluxOptionElectricalOnly)) {
								((FluxReaction)vcReactions[i]).setPhysicsOptions(ReactionStep.PHYSICS_ELECTRICAL_ONLY);
							}
						} else if (embeddedRxnElement.getName().equals(XMLTags.SimpleReactionTag)) {
							// if embedded element is a simple reaction, set simple reaction's structure from element attributes
							vcReactions[i] = new cbit.vcell.model.SimpleReaction(reactionStructure, rxnName);
						}
					} else {
						vcReactions[i] = new cbit.vcell.model.SimpleReaction(reactionStructure, rxnName);
					}
				} else {
					vcReactions[i] = new cbit.vcell.model.SimpleReaction(reactionStructure, rxnName);
				}
				
				KineticLaw kLaw = sbmlRxn.getKineticLaw();
				Kinetics kinetics = null;
				
				// Translate RATE_PARAM from substance/time to concentration/time or surface density/time (for membranes)
				// Kinetic law substance unit
				String kLawSubstanceUnitStr = null;
				if (kLaw.isSetSubstanceUnits()) {
					kLawSubstanceUnitStr = kLaw.getSubstanceUnits();
				}
				VCUnitDefinition kLawSubstanceUnit = getSBMLUnit(kLawSubstanceUnitStr, SBMLUnitTranslator.SUBSTANCE);

				// kinetic law time unit
				String kLawTimeUnitStr = null;
				if (kLaw.isSetTimeUnits()) {
					kLawTimeUnitStr = kLaw.getTimeUnits();
				}
				VCUnitDefinition kLawTimeUnit = getSBMLUnit(kLawTimeUnitStr, SBMLUnitTranslator.TIME);

				// kinetic law rate unit in SBML is in terms of substance/time
				VCUnitDefinition kLawRateUnit = kLawSubstanceUnit.divideBy(kLawTimeUnit);

				// Virtual cell rate unit is in terms of concentration/time. Units depend on whether reaction is in feature or membrane
				VCUnitDefinition VC_RateUnit = null;
				if (reactionStructure instanceof Feature) {
					VC_RateUnit = VCUnitDefinition.UNIT_uM_per_s;
				} else if (reactionStructure instanceof Membrane) {
					if (vcReactions[i] instanceof FluxReaction) {
						VC_RateUnit = VCUnitDefinition.UNIT_uM_um_per_s;
					} else if (vcReactions[i] instanceof SimpleReaction) {
						VC_RateUnit = VCUnitDefinition.UNIT_molecules_per_um2_per_s;
					}
				}

				// Convert the formula from kineticLaw into MathML and then to an expression (infix) to be used in VCell kinetics
				String sbmlRateFormula = kLaw.getFormula();
				Expression kLawRateExpr = getExpressionFromFormula(sbmlRateFormula);
				String kLawRateExprStr = kLawRateExpr.infix(); 
				Expression vcRateExpression = new Expression(kLawRateExprStr);

				// Check the kinetic rate equation for occurances of any species in the model that is not a reaction participant.
				// If there exists any such species, it should be added as a modifier (catalyst) to the reaction.
				String[] symbols = vcRateExpression.getSymbols();
				if (symbols != null) {
					for (int j = 0; j < symbols.length; j++){
						for (int k = 0; k < vcSpeciesContexts.length; k++){
							if (vcSpeciesContexts[k].getName().equals(symbols[j])) {
								if ((sbmlRxn.getReactant(vcSpeciesContexts[k].getName()) == null) && (sbmlRxn.getProduct(vcSpeciesContexts[k].getName()) == null) && (sbmlRxn.getModifier(vcSpeciesContexts[k].getName()) == null)) {
									// This means that the speciesContext is not a reactant, product or modifier : it has to be added as a catalyst
									vcReactions[i].addCatalyst(vcSpeciesContexts[k]);
								}
							}
						}
					}
				}

				// Now add the reactants, products, modifiers as specified by the sbmlRxn
				addReactionParticipants(sbmlRxn, vcReactions[i]);

				// Check if any of the reaction participants have 'hasOnlySubstanceUnits' set. If so, we cannot apply GeneralKinetics
				// We have to fall back on GeneralTotalKinetics.
				boolean bSpeciesHasOnlySubstanceUnits = checkSpeciesHasSubstanceOnly(sbmlRxn);

				// Retrive the compartment in which the reaction takes place and get its size units
				// Use this to convert the SBML kinetic rate units from substance/time to substance/size/time = concentration/time
				Compartment compartment = (Compartment)sbmlModel.getCompartment(reactionStructure.getName());
				if (compartment.isSetSize() && !bSpeciesHasOnlySubstanceUnits) {
					double compartmentSize = 0.0;
					adjustCompartmentSizes();
					System.err.println("Compartment " + getActualName(compartment) + " Size set : " + compartment.isSetSize());
					if (compartment != null) {
						compartmentSize = compartment.getSize();
					}
					String spatialDimensionBuiltinName = getSpatialDimentionBuiltInName((int)compartment.getSpatialDimensions());
					VCUnitDefinition compartmentSizeUnit = getSBMLUnit(compartment.getUnits(), spatialDimensionBuiltinName);
					VCUnitDefinition SBML_RateUnit = kLawRateUnit.divideBy(compartmentSizeUnit);

					// Check for compatibility of the SBML and VC rate units. If they are compatible, get the conversion factor.
					if (compartmentSize <= 0.0) {
						logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to determine compartment size (used to scale reaction rate)");
					}

					kinetics = new cbit.vcell.model.GeneralKinetics(vcReactions[i]);
					String SBMLFACTOR_PARAMETER = "sbmlRateFactor";
					String SBMLCOMPARTMENTSIZE_PARAMETER = getActualName(compartment);
					
					ListOf listofLocalParams = kLaw.getListOfParameters();
					for (int j = 0; j < kLaw.getNumParameters(); j++) {
						org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofLocalParams.get(j);
						String paramName = getActualName(param);
						if (paramName.equals(SBMLCOMPARTMENTSIZE_PARAMETER)) {
							SBMLCOMPARTMENTSIZE_PARAMETER = SBMLCOMPARTMENTSIZE_PARAMETER + "_size";
							break;
						}
					}

					// If the name of the rate parameter has been changed by user, it is stored in rxnAnnotation. Retrieve this to re-set rate param name.
					if (rxnAnnotation != null && rxnAnnotation.length() > 0) {
						embeddedRxnElement = getEmbeddedElementInAnnotation(rxnAnnotation, RATE_NAME);
						String vcRateParamName = null;
						if (embeddedRxnElement != null) {
							if (embeddedRxnElement.getName().equals(XMLTags.RateTag)) {
								vcRateParamName = embeddedRxnElement.getAttributeValue(XMLTags.NameAttrTag);
								kinetics.getRateParameter().setName(vcRateParamName);
							}
						} 
					}
					
					// To remove the compartment scale factor from vcRateExpression
					if (vcRateExpression.hasSymbol(SBMLCOMPARTMENTSIZE_PARAMETER)) {
						Expression checkedExpr = checkCompartmentScaleFactorInRxnRateExpr(vcRateExpression, SBMLCOMPARTMENTSIZE_PARAMETER, vcReactions[i].getName());
						vcRateExpression = checkedExpr;
						kinetics.setParameterValue(kinetics.getRateParameter(),vcRateExpression);
					} else {
						// If the compartment scale factor is not present, need to divide rate by compartment size for unit consistency between VCell and SBML
						vcRateExpression = Expression.mult(vcRateExpression, Expression.invert(new Expression(SBMLCOMPARTMENTSIZE_PARAMETER)));
						kinetics.setParameterValue(kinetics.getRateParameter(),vcRateExpression);
						kinetics.setParameterValue(kinetics.getKineticsParameter(SBMLCOMPARTMENTSIZE_PARAMETER),new Expression(compartmentSize));
						kinetics.getKineticsParameter(SBMLCOMPARTMENTSIZE_PARAMETER).setUnitDefinition(compartmentSizeUnit);
					}
					
					//
					// introduce "dimensionless" scale factor for the reaction rate (after adjusting sbml rate for sbml compartment size)
					// note that although physically dimensionless, the VCUnitDefinition will likely have a non-unity scale conversion (e.g. 1e-3)
					//
					double rateScalefactor = 1.0;
					if (VC_RateUnit.isCompatible(SBML_RateUnit)) { 
						rateScalefactor = SBML_RateUnit.convertTo(rateScalefactor, VC_RateUnit);
						VCUnitDefinition rateFactorUnit = VC_RateUnit.divideBy(SBML_RateUnit);
						if (rateScalefactor == 1.0 && rateFactorUnit.getSymbol().equals("1")) {
							// Ignore the factor since rateFactor and its units are 1
						} else {
							Expression currentRateExpr = kinetics.getRateParameter().getExpression();
							currentRateExpr = Expression.mult(vcRateExpression, new Expression(SBMLFACTOR_PARAMETER));
							kinetics.setParameterValue(kinetics.getRateParameter(),currentRateExpr);
							kinetics.setParameterValue(kinetics.getKineticsParameter(SBMLFACTOR_PARAMETER), new Expression(rateScalefactor));
							kinetics.getKineticsParameter(SBMLFACTOR_PARAMETER).setUnitDefinition(rateFactorUnit);
						}
					} else {
						logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to scale the unit for kinetic rate: " + VC_RateUnit.getSymbol() + " " + SBML_RateUnit.getSymbol());
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
									if (species.getHasOnlySubstanceUnits() || sbmlModel.getCompartment(species.getCompartment()).getSpatialDimensions() == 0) {
										logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Species with substance units only or compartments with spatial dimension of 0 is not handled at this time.");
									} else {
										// Check if species name is used as a local parameter in the kinetic law.
										// If so, the parameter in the local namespace takes precedence. 
										// So ignore unit conversion for the species with the same name.
										boolean bSpeciesNameFoundInLocalParamList = false;
										for (int ll = 0; ll < kLaw.getNumParameters(); ll++) {
											org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofLocalParams.get(ll);
											String paramName = getActualName(param);
											if (paramName.equals(getActualName(species))) {
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
										String spatialDimBuiltinName = getSpatialDimentionBuiltInName(dimension);
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

										// Get the scale factor for the SBML -> VC Unit conversion
										double concScaleFactor = 1.0;
										if (VC_conc_unit.isCompatible(SBML_conc_unit)) {
											concScaleFactor = VC_conc_unit.convertTo(concScaleFactor, SBML_conc_unit);
											VCUnitDefinition concScaleFactorUnit = SBML_conc_unit.divideBy(VC_conc_unit);
											if (concScaleFactor == 1.0 && concScaleFactorUnit.getSymbol().equals("1")) {
												// If the factor is 1 and unit conversion evaluates to 1 ( => No conversion is required), we don't need to include that factor
											} else {
												// Substitute any occurance of speciesName in rate expression for kinetics with 'speciesName*concScaleFactor'
												// * Get current rate expression from kinetics, substitute corresponding values, re-set kinetics expression *
												String CONCFACTOR_PARAMETER = getActualName(species) + "_ConcFactor";
												Expression currentRateExpr = kinetics.getRateParameter().getExpression();
												currentRateExpr.substituteInPlace(new Expression(getActualName(species)), new Expression(getActualName(species)+"*"+CONCFACTOR_PARAMETER));
												kinetics.setParameterValue(kinetics.getRateParameter(),currentRateExpr);
												// Add the concentration factor as a parameter
												kinetics.setParameterValue(kinetics.getKineticsParameter(CONCFACTOR_PARAMETER), new Expression(concScaleFactor));
												kinetics.getKineticsParameter(CONCFACTOR_PARAMETER).setUnitDefinition(concScaleFactorUnit);
											}
										} else {
											logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to scale the unit for species concentration: " + VC_conc_unit.getSymbol() + " " + SBML_conc_unit.getSymbol());
										}
									}
								}
							}		// end for - k (vcSpeciesContext)
						}		// end for - j (symbols)
					} 	// end - if symbols != null
				} else {
					// compartment size is not set
					kinetics = new GeneralTotalKinetics(vcReactions[i]);
					if (reactionStructure instanceof Feature) {
						// 'vcRateExpression' in this case is the total rate (in substance/time). In order to be consistent with VC units for
						// rate, we need a multiplicative factor, which we can get by using the ReservedSymbol KMOLE. The units of KMOLE are
						// already incorporated when the GeneralTotalKinetics is created. This is needed only for reactions in features,
						// since membrane reactions are already in terms of molecules.
						String SBMLUNIT_PARAMETER = ReservedSymbol.KMOLE.getName();
						vcRateExpression = Expression.mult(vcRateExpression, Expression.invert(new Expression(SBMLUNIT_PARAMETER)));
						kinetics.setParameterValue(((GeneralTotalKinetics)kinetics).getTotalRateParamter(), vcRateExpression);
						// kinetics.setParameterValue(kinetics.getKineticsParameter(SBMLUNIT_PARAMETER), ReservedSymbol.KMOLE.getExpression());
						// kinetics.getKineticsParameter(SBMLUNIT_PARAMETER).setUnitDefinition(ReservedSymbol.KMOLE.getUnitDefinition());
					} else if (reactionStructure instanceof Membrane) {
						kinetics.setParameterValue(((GeneralTotalKinetics)kinetics).getTotalRateParamter(), vcRateExpression);
					}
					// sometimes, the reaction rate can contain a compartment name, not necessarily the compartment the reaction takes place.
					for (int kk = 0; kk < (int)sbmlModel.getNumCompartments(); kk++){
						Compartment comp1 = sbmlModel.getCompartment(kk);
						String comp1Name = getActualName(comp1);
						if (!comp1Name.equals(getActualName(compartment))) {
							if (vcRateExpression.hasSymbol(comp1Name)) {
								Structure struct1 = simContext.getModel().getStructure(comp1Name);
								if (comp1.isSetSize()) {
									kinetics.setParameterValue(kinetics.getKineticsParameter(comp1Name), new Expression(comp1.getSize()));
								} else {
									kinetics.setParameterValue(kinetics.getKineticsParameter(comp1Name), new Expression(1.0));
								}
								if (struct1 instanceof Feature) {
									kinetics.getKineticsParameter(comp1Name).setUnitDefinition(VCUnitDefinition.UNIT_um3);
								} else {
									kinetics.getKineticsParameter(comp1Name).setUnitDefinition(VCUnitDefinition.UNIT_um2);
								}
							}
						}
					}	// end for - compartments in model loop
				}	// end if - else reaction compartment size set/not set.

				// Check for unresolved parameters in the vcKinetics and add them from the global parameters list 
				ListOf listofGlobalParams = sbmlModel.getListOfParameters();
				for (int j = 0; j < sbmlModel.getNumParameters(); j++) {
					org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofGlobalParams.get(j);
					String paramName = getActualName(param);
					// If the global parameter is a kinetic parameter, then get its value and set the kinetic parameter for the vcKinetics
					// Else continue with the next global parameter.
					if (kinetics.getKineticsParameter(paramName) != null) {
						double value = param.getValue();
						// Check if param is defined by a rule. If so, that value overrides the value existing in the param element.
						Expression valueExpr = getValueFromRule(paramName);
						if (valueExpr == null) {
							valueExpr = new Expression(value);
						}
						kinetics.setParameterValue(paramName, valueExpr.infix());
						VCUnitDefinition paramUnit = getSBMLUnit(param.getUnits(),null);
						kinetics.getKineticsParameter(paramName).setUnitDefinition(paramUnit);
					} 
				}
				
				// Introduce all remaining local parameters from the SBML model - local params cannot be defined by rules.
				ListOf listofLocalParams = kLaw.getListOfParameters();
				for (int j = 0; j < kLaw.getNumParameters(); j++) {
					org.sbml.libsbml.Parameter param = (org.sbml.libsbml.Parameter)listofLocalParams.get(j);
					String paramName = getActualName(param);
					kinetics.setParameterValue(paramName, Double.toString(param.getValue()));
					VCUnitDefinition paramUnit = getSBMLUnit(param.getUnits(),null);
					kinetics.getKineticsParameter(paramName).setUnitDefinition(paramUnit);
				}

				// set the reaction kinetics, anmd add reaction to the vcell model.					
				vcReactions[i].setKinetics(kinetics);
				simContext.getModel().addReactionStep(vcReactions[i]);
				if (sbmlRxn.isSetFast() && sbmlRxn.getFast()) {
					simContext.getReactionContext().getReactionSpec(vcReactions[i]).setReactionMapping(cbit.vcell.mapping.ReactionSpec.FAST);
				}
			}	// end - for vcReactions
		} catch (Exception e1) {
			// TODO Auto-generated catch block
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

protected void addRules() throws Exception {
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
			logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNSUPPORED_ELEMENTS_OR_ATTS, "Algebraic or Rate rules are not handled in the Virtual Cell at this time");
		} else {
			// Get the assignment rule and store it in the hashMap.
			AssignmentRule assignmentRule = (AssignmentRule)rule;
			Expression assignmentRuleMathExpr = getExpressionFromFormula(assignmentRule.getFormula());
			assignmentRulesHash.put(assignmentRule.getVariable(), assignmentRuleMathExpr);
		}
	}
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
			for (int i = 0; i < sbmlModel.getNumSpecies(); i++) {
				org.sbml.libsbml.Species sbmlSpecies = (org.sbml.libsbml.Species)listOfSpecies.get(i);
				// Sometimes, the species name can be null or a blank string; in that case, use species id as the name.
				String speciesName = getActualName(sbmlSpecies);
				cbit.vcell.model.Species vcSpecies = null;
				String speciesAnnotation = sbmlSpecies.getAnnotation();
				if (speciesAnnotation != null && speciesAnnotation.length() > 0) {
					Element embeddedElement = getEmbeddedElementInAnnotation(speciesAnnotation, SPECIES_NAME);
					if (embeddedElement != null) {
						// Get the species name from annotation and create the species.
						if (embeddedElement.getName().equals(cbit.vcell.xml.XMLTags.SpeciesTag)) {
							String vcSpeciesName = embeddedElement.getAttributeValue(cbit.vcell.xml.XMLTags.NameAttrTag);
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
				} else {
					simContext.getModel().addSpecies(new cbit.vcell.model.Species(speciesName, speciesName));
					vcSpecies = simContext.getModel().getSpecies(speciesName);
				}
				
				// Get matching compartment name (of sbmlSpecies[i]) from feature list
				String compartmentName = sbmlSpecies.getCompartment();
				Structure structure = simContext.getModel().getStructure(compartmentName);
				simContext.getModel().addSpeciesContext(vcSpecies, structure);
				vcSpeciesContexts[i] = simContext.getModel().getSpeciesContext(vcSpecies, structure);
				vcSpeciesContexts[i].setHasOverride(true);
				vcSpeciesContexts[i].setName(speciesName);
				
				// Adjust units of species, convert to VC units.
				SpeciesContextSpec speciesContextSpec = simContext.getReactionContext().getSpeciesContextSpec(vcSpeciesContexts[i]);

				// Units in SBML, compute this using some of the attributes of sbmlSpecies
				int dimension = (int)sbmlModel.getCompartment(sbmlSpecies.getCompartment()).getSpatialDimensions();
				String spatialDimBuiltinName = getSpatialDimentionBuiltInName(dimension);

				String spatialSizeUnitStr = sbmlSpecies.getSpatialSizeUnits();
				String substanceUnitStr = sbmlSpecies.getSubstanceUnits();

				// VCell units - the default units of concentration of species in a compartment or a membrane.
				VCUnitDefinition VCConcUnit = null;
				if (structure instanceof Feature) {
					VCConcUnit = VCUnitDefinition.UNIT_uM;
				} else if (structure instanceof Membrane) {
					VCConcUnit = VCUnitDefinition.UNIT_molecules_per_um2;
				}

				// We need to scale and convert the SBUnit to VCUnit
				VCUnitDefinition substanceUnit = getSBMLUnit(substanceUnitStr, SBMLUnitTranslator.SUBSTANCE);

				Expression initExpr = null;
				if (sbmlSpecies.isSetInitialConcentration()) { 		// If initial Concentration is set
					double initConcentration = sbmlSpecies.getInitialConcentration();
					VCUnitDefinition spatialSizeUnit = getSBMLUnit(spatialSizeUnitStr, spatialDimBuiltinName);
					VCUnitDefinition SBConcUnit = substanceUnit.divideBy(spatialSizeUnit);
					double factor = 1.0;
					if (VCConcUnit.isCompatible(SBConcUnit)) {
						factor = SBConcUnit.convertTo(1.0, VCConcUnit);
						if (factor != 1) {
							initConcentration = initConcentration * factor;
						}
					} else {
						logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to scale the unit for the initial condition: " + VCConcUnit.getSymbol() + " " + SBConcUnit.getSymbol());
					}
					initExpr = getValueFromRule(speciesName);
					if (initExpr == null) {
						initExpr = new Expression(initConcentration);
					} else {
						initExpr = Expression.mult(initExpr, new Expression(factor));
					}
				} else if (sbmlSpecies.isSetInitialAmount()) {		// If initial amount is set
					double initAmount = sbmlSpecies.getInitialAmount();
					Compartment compartment = (Compartment)sbmlModel.getCompartment(compartmentName);
					VCUnitDefinition compartmentSizeUnit = getSBMLUnit(compartment.getUnits(), spatialDimBuiltinName); 
					VCUnitDefinition SBConcUnit = substanceUnit.divideBy(compartmentSizeUnit);

					double compartmentSize = compartment.getSize();
					double factor = 1.0;
					if (dimension==0 || dimension==1){
						logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, dimension+" dimensional compartment "+compartmentName+" not supported");
					}
					//if (sbmlSpecies.getHasOnlySubstanceUnits()){
						//logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.UNIT_ERROR, "species "+speciesName+" 'hasOnlySubstanceUnit' is not supported");
					//}
					if (compartmentSize != 0.0) {
						initAmount = initAmount / compartmentSize;
						if (VCConcUnit.isCompatible(SBConcUnit)) {
							factor = SBConcUnit.convertTo(1.0, VCConcUnit);
							if (factor != 1.0) {
								initAmount = initAmount * factor;
							} 
						} else {
							logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to scale the unit for the initial condition: " + VCConcUnit.getSymbol() + " " + SBConcUnit.getSymbol());
						}
					} else {
						logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.UNIT_ERROR, "compartment "+compartmentName+" has zero size, unable to determine initial concentration for species "+speciesName);
					}
					initExpr = getValueFromRule(speciesName);
					if (initExpr == null) {
						initExpr = new Expression(initAmount);
					} else {
						initExpr = Expression.mult(initExpr, new Expression(factor));
					}
				} else {
					initExpr = getValueFromRule(speciesName);
					if (initExpr == null) {
						logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.UNIT_ERROR, "no initial condition for species "+speciesName+", assuming 0.0");
						initExpr = new Expression(0.0);
					}

					// Units for initial conc or amt if it is specified by an assignment rule
					double factor = 1.0;
					if (dimension != 0 && !sbmlSpecies.getHasOnlySubstanceUnits()) {
						// Init conc : 'hasOnlySubstanceUnits' should be false and spatial dimension of compartment should be non-zero.
						VCUnitDefinition spatialSizeUnit = getSBMLUnit(spatialSizeUnitStr, spatialDimBuiltinName);
						VCUnitDefinition SBConcUnit = substanceUnit.divideBy(spatialSizeUnit);
						if (VCConcUnit.isCompatible(SBConcUnit)) {
							factor = SBConcUnit.convertTo(1.0, VCConcUnit);
						} else {
							logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to scale the unit for the initial condition: " + VCConcUnit.getSymbol() + " " + SBConcUnit.getSymbol());
						}
					} else if (dimension == 0 || sbmlSpecies.getHasOnlySubstanceUnits()) {
						// Init Amount : 'hasOnlySubstanceUnits' should be true or spatial dimension of compartment should zero.
						Compartment compartment = (Compartment)sbmlModel.getCompartment(compartmentName);
						VCUnitDefinition compartmentSizeUnit = getSBMLUnit(compartment.getUnits(), spatialDimBuiltinName); 
						VCUnitDefinition SBConcUnit = substanceUnit.divideBy(compartmentSizeUnit);

						double compartmentSize = compartment.getSize();
						if (dimension==0 || dimension==1){
							logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, dimension+" dimensional compartment "+compartmentName+" not supported");
						}
						if (sbmlSpecies.getHasOnlySubstanceUnits()){
							logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.UNIT_ERROR, "species "+speciesName+" 'hasOnlySubstanceUnit' is not supported");
						}
						if (compartmentSize != 0.0) {
							// initAmount = initAmount / compartmentSize;
							if (VCConcUnit.isCompatible(SBConcUnit)) {
								factor = SBConcUnit.convertTo(1.0, VCConcUnit);
							} else {
								logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to scale the unit for the initial condition: " + VCConcUnit.getSymbol() + " " + SBConcUnit.getSymbol());
							}
						} else {
							logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.UNIT_ERROR, "compartment "+compartmentName+" has zero size, unable to determine initial concentration for species "+speciesName);
						}
					}
					initExpr = Expression.mult(initExpr, new Expression(factor));
				}

				String[] symbols = initExpr.getSymbols();
				if (symbols != null) {
					for (int j = 0; j < symbols.length; j++){
						 Expression temp = getValueFromRule(symbols[j]);
						 if (temp != null) {
							 initExpr.substituteInPlace(new Expression(symbols[j]), temp);
						 }
					}
					initExpr = initExpr.flatten();
				}

				speciesContextSpec.getInitialConditionParameter().setExpression(initExpr);
				speciesContextSpec.setConstant(sbmlSpecies.getBoundaryCondition() || sbmlSpecies.getConstant());

				// Add the speciesContext names and sbmlSpecies names into SbmlVcSpeciesHash, so that it can be used while determining the structure
				// in which each reaction takes place.
				if (SbmlVcSpeciesHash.get(vcSpeciesContexts[i].getName()) == null) {
					SbmlVcSpeciesHash.put(vcSpeciesContexts[i].getName(), speciesName);
				}
			}
		}catch (Exception e) {
			e.printStackTrace(System.out);
//			System.out.println("Error adding species context ;\t"+ e.getMessage());
			throw new RuntimeException("Error adding species context ;\t"+ e.getMessage());

		}
	}


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
		String unitName = getActualName(ud);
		VCUnitDefinition vcUnitDef = cbit.vcell.units.SBMLUnitTranslator.getVCUnitDefinition(ud);
		vcUnitsHash.put(unitName, vcUnitDef);
	}
}


/**
 * 	adjustCompartmentSizes :
 * 		Once we set the compartments in the vcell biomodel, we need to set the sizes of all compartments (get from sbmlmodel);
 *		and set the volume fractions and surface to volume ratios for all the compartments. This is done by using the
 *		StructureSizeSolver which is a constraint based solver which solves for the required unknowns (s_to_v ratios, volFracts) for
 *		the remaining compartments given the values for some and sizes of all compartments. These values (sizes, s-to_v ratios and
 *		volfracts are set in the structureMapping of the simContext.
 **/
private void adjustCompartmentSizes() {
	ListOf listofCompartments = sbmlModel.getListOfCompartments();
	Structure[] structures = simContext.getModel().getStructures();
	boolean isSizeSet = true;
	try {
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			org.sbml.libsbml.Compartment compartment = (org.sbml.libsbml.Compartment)listofCompartments.get(i);
			String compartmentName = getActualName(compartment);

			if (!compartment.isSetSize()) {
				// logger.sendMessage(VCLogger.MEDIUM_PRIORITY, TranslationMessage.COMPARTMENT_ERROR, "compartment "+compartmentName+" size is not set in SBML document.");
				isSizeSet = false;
			} else {
				double size = compartment.getSize();
				// Check if size is specified by a rule
				Expression sizeExpr = getValueFromRule(compartmentName);
				if (sizeExpr != null) {
					// WE ARE NOT HANDLING COMPARTMENT SIZES WITH ASSIGNMENT RULES AT THIS TIME  ...
					logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.COMPARTMENT_ERROR, "compartment "+compartmentName+" size has an assignment rule, cannot handle it at this time.");
				}
				
				// Convert size units from SBML -> VC compatible units.
				// If compartment (size) unit is not set, it is in the default SBML volume unit for 3d compartment and area unit for 2d compartment. 
				// Check to see if the default units are re-defined. If not, they are "litre" for vol and "sq.m" for area.
				// Convert it to VC units (um3 for 3d and um2 for 2d compartments) - multiply the size value by the conversion factor.

				Expression adjustedSizeExpr = new Expression(size);
				cbit.vcell.mapping.StructureMapping.StructureMappingParameter mappingParam = simContext.getGeometryContext().getStructureMapping(structures[i]).getSizeParameter();
				VCUnitDefinition vcSizeUnit = mappingParam.getUnitDefinition();
				int spatialDim = (int)compartment.getSpatialDimensions();
				String spatialDimBuiltInName = getSpatialDimentionBuiltInName(spatialDim);
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
		if (isSizeSet) {
			StructureSizeSolver structSizeSolver = new StructureSizeSolver();
			structSizeSolver.updateRelativeStructureSizes(simContext);
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error setting sizes; " + e.getMessage());
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

private Expression checkCompartmentScaleFactorInRxnRateExpr(Expression rateExpr, String compartmentSizeParamName, String rxnName) throws Exception {
	Expression diffExpr = rateExpr.differentiate(compartmentSizeParamName).flatten();
	if (diffExpr.hasSymbol(compartmentSizeParamName)) {
		logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
	}

	Expression expr1 = rateExpr.getSubstitutedExpression(new Expression(compartmentSizeParamName), new Expression(1.0)).flatten();
	if (!expr1.compareEqual(diffExpr) && !(cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(expr1, diffExpr))) {
		logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
	}

	Expression expr0 = rateExpr.getSubstitutedExpression(new Expression(compartmentSizeParamName), new Expression(0.0)).flatten();
	if (!expr0.isZero()) {
		logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.UNIT_ERROR, "Unable to interpret Kinetic rate for reaction : " + rxnName + " Cannot interpret non-linear function of compartment size");
	}

	return diffExpr;
}


/**
 *  checkSpeciesHasSubstanceOnly :
 *		Check if reactant or product species has the 'hasOnlySubstanceUnits' flag set. Get reactants, products in the sbml reaction,
 *		check for the corresponding species in sbmlModel, and check if that flag is set.
 **/

private boolean checkSpeciesHasSubstanceOnly(Reaction sbmlRxn) throws ExpressionException {
	// Check if any reactant species has 'hasOnlySubstanceUnits' set
	for (int i = 0; i < (int)sbmlRxn.getNumReactants(); i++){
		SpeciesReference reactRef = sbmlRxn.getReactant(i);
		org.sbml.libsbml.Species sp = sbmlModel.getSpecies(reactRef.getSpecies());
		if (sp.getHasOnlySubstanceUnits()) {
			return true;
		}
	}
	// Check if any product species has 'hasOnlySubstanceUnits' set
	for (int i = 0; i < (int)sbmlRxn.getNumProducts(); i++){
		SpeciesReference pdtRef = sbmlRxn.getProduct(i);
		org.sbml.libsbml.Species sp = sbmlModel.getSpecies(pdtRef.getSpecies());
		if (sp.getHasOnlySubstanceUnits()) {
			return true;
		}
	}
	// Check if reaction rate has any species that has 'hasOnlySubstanceUnits' set
	Expression rateExpression = getExpressionFromFormula(sbmlRxn.getKineticLaw().getFormula());
	SpeciesContext[] vcSpContexts = simContext.getModel().getSpeciesContexts();
	for (int i = 0; i < vcSpContexts.length; i++){
		if (rateExpression.hasSymbol(vcSpContexts[i].getName())) {
			org.sbml.libsbml.Species sp = sbmlModel.getSpecies(vcSpContexts[i].getName());
			if (sp.getHasOnlySubstanceUnits()) {
				return true;
			}
		}
	}
	return false;
}


	/* 
	 * getActualName :
	 * Most SBML Level 2 elements have and 'id' field and a 'name' field. It is possible that the 'name' field is not
	 * always specified. Either the name or id or both are specified (both NOT being specified doesn't happen).
	 * When the name field is not given, the id can be taken as the name. While creating the corresponding 
	 * vcell model objects, the name is required, hence there is a need to check if name is null or is a 
	 * blank string, in which case, the id field is taken as the name of the element. 
	 */
	protected String getActualName(SBase sbase) {
		String name = null;
		if (sbase instanceof org.sbml.libsbml.Model) {
			org.sbml.libsbml.Model	m = (org.sbml.libsbml.Model)sbase;
		   	if (m.getId() != null && !m.getId().equals("")) {
				name = m.getId();
		   	} else {
				name = m.getName();
		   	}
		} else if (sbase instanceof org.sbml.libsbml.FunctionDefinition) {
			FunctionDefinition	f = (FunctionDefinition)sbase;
			if (f.getId() != null && !f.getId().equals("")) {
				name = f.getId();
			} else {
				name = f.getName();
			}
		} else if (sbase instanceof org.sbml.libsbml.UnitDefinition) {
			UnitDefinition	u = (UnitDefinition)sbase;
			if (u.getId() != null && !u.getId().equals("")) {
				name = u.getId();
			} else {
				name = u.getName();
			}
		} else if (sbase instanceof org.sbml.libsbml.Compartment) {
			Compartment	c = (Compartment)sbase;
			if (c.getId() != null && !c.getId().equals("")) {
				name = c.getId();
			} else {
				name = c.getName();
			}
		} else if (sbase instanceof org.sbml.libsbml.Species) {
			org.sbml.libsbml.Species s = (org.sbml.libsbml.Species)sbase;
			if (s.getId() != null && !s.getId().equals("")) {
				name = s.getId();
			} else {
				name = s.getName();
			}
		} else if (sbase instanceof org.sbml.libsbml.Reaction) {
			// For reactions, try getting name from the 'name' field. If it is obtained from VCML, this field contains the
			// actual name of the reaction (with spaces, etc.). If the model is being imported from sbml from another tool
			// like Copasi, etc. the reaction name uniqueness has to be checked. If a reaction name already exists in the
			// list of reactions in the SBML model, ignore the name field from SBML and use the id field instead.
			org.sbml.libsbml.Reaction r = (org.sbml.libsbml.Reaction)sbase;
			if (r.getName() != null && !r.getName().equals("")) {
				// check for uniqueness in reaction name:
				name = r.getName();
				if (simContext != null) {
					boolean bNotUnique = false;
					for (int i = 0; i < simContext.getReactionContext().getReactionSpecs().length; i++){
						if (name.equals(simContext.getReactionContext().getReactionSpecs(i).getReactionStep().getName())) {
							bNotUnique = true;
						}
					}
					if (bNotUnique) {
						// If reaction name is not unique, use the name from reaction id
						if (r.getId() != null && !r.getId().equals("")) {
							name = r.getId();
						}
					}
				}
			} else {
				// reaction 'name' was null, hence get it from 'id'
				name = r.getId();
			}
		} else if (sbase instanceof org.sbml.libsbml.Parameter) {
			org.sbml.libsbml.Parameter p = (org.sbml.libsbml.Parameter)sbase;
			if (p.getId() != null && !p.getId().equals("")) {
				name = p.getId();
			} else {
				name = p.getName();
			}
		} else {
			throw new RuntimeException("Cannot get actual name for the class" + sbase.getClass());
		}
		return name;
	}


	public BioModel getBioModel() {
		// Read SBML model into libSBML SBMLDocument and create an SBML model
		SBMLReader reader = new SBMLReader();
		SBMLDocument document = reader.readSBMLFromString(sbmlString);

		// For an SBML level 1 model, convert it to level 2 (using document.setLevel(int)) and read it in
		if (document.getLevel() == 1) {
			document.setLevel(2);
		}
		sbmlModel = document.getModel();
		
		// Convert SBML Model to VCell model
		// An SBML model will correspond to a simcontext - which needs a Model and a Geometry
		// SBML handles only nonspatial geometries at this time, hence creating a non-spatial default geometry
		String modelName = getActualName(sbmlModel);
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

		bioModel.refreshDependencies();
		return bioModel;
	}


/*
 *  getEmbeddedElementInRxnAnnotation :
 *  Takes the reaction annotation as an argument and returns the embedded element  (fluxstep or simple reaction), if present.
 */
private org.jdom.Element getEmbeddedElementInAnnotation(String annotationStr, String tag) {
	// Get the XML element corresponding to the annotation xmlString.
	Element annotationElement = cbit.util.xml.XmlUtil.stringToXML(annotationStr, null);
	Namespace ns = Namespace.getNamespace(Translator.SBML_VCML_NS);

	Element vcInfoElement = annotationElement.getChild(XMLTags.VCellInfoTag, ns);
	Element embeddedElement = null;
	
	// If there is an annotation element for the reaction or species, retrieve and return.
	if (annotationElement != null && vcInfoElement != null) {
		if (tag.equals(RATE_NAME)) {
			embeddedElement = vcInfoElement.getChild(XMLTags.RateTag, ns);
		} else if (tag.equals(SPECIES_NAME)) {
			embeddedElement = vcInfoElement.getChild(XMLTags.SpeciesTag, ns);
		} else if (tag.equals(REACTION)) {
			embeddedElement = vcInfoElement.getChild(XMLTags.FluxStepTag, ns);
			if (embeddedElement == null) {
				embeddedElement = vcInfoElement.getChild(XMLTags.SimpleReactionTag, ns);
			}
		}
	}
	
	return embeddedElement;
}


/*
 *  getExpressionFromFormula : 
 *	Convert the math formula string in a kineticLaw, rule or lambda function definition into MathML
 *	and use ExpressionMathMLParser to convert the MathML into an expression to be brought into the VCell.
 *	NOTE : ExpressionMathMLParser will handle only the <apply> elements of the MathML string,
 *	hence the ExpressionMathMLParser is given a substring of the MathML containing the <apply> elements. 
 */
private Expression getExpressionFromFormula(String formulaStr) throws ExpressionException {
	MathMLDocument mDoc = new MathMLDocument();
	mDoc.setMath(libsbml.parseFormula(formulaStr));
	String mathMLStr = libsbml.writeMathMLToString(mDoc);
	cbit.vcell.parser.ExpressionMathMLParser exprMathMLParser = new cbit.vcell.parser.ExpressionMathMLParser(lambdaFunctions);
	Expression expr =  exprMathMLParser.fromMathML(mathMLStr);
	return expr;
}


/*
 *  getMatchingSpeciesContext : 
 *  To retrieve the speciesContext corresponding to a reactant or product. The libsbml listofReactants or Pdts
 *  returns a SpeciesRef object which only contains the name of the species. In order to create a SimpleReaction
 * (ReactionStep), we need the associated speciesContext.
 */
private SpeciesContext getMatchingSpeciesContext(String speciesName, SpeciesContext[] speciesContexts) {
	// Loop thro' the speciesContext list to find a match in the species name retrieved from the listofReactants or Pts. 
	for (int i = 0; i < speciesContexts.length; i++) {
		if (speciesContexts[i].getName().equals(speciesName)) {
			return speciesContexts[i];
		}
	}
	return null;
}


/* 
 * getReactionStructure :
 * In order to create a ReactionStep for a VCell model, the structure in which the reaction takes place is needed.
 * This method retrieves the structure for a reaction. Takes an libsbml Reaction object and the list of speciesContexts
 * (computed earlier) as arguments. The method assumes that a reaction takes place within one compartment; it gets the 
 * structure(s) to which the reactant(s) belong (SRs) and the structure(s) to which the product(s) belong (SPs). If the SRs 
 * are different within themselves or if the SPs are different within themselves or if the SR and SP are different, an
 * exception is thrown - at present we do not deal with the case where reactant(s) and product(s) are in different 
 * compartments. Returns the structure/compartment to which the reactants and products belong. 
 */
private Structure getReactionStructure(org.sbml.libsbml.Reaction sbmlRxn, SpeciesContext[] speciesContexts) throws Exception {
    Structure struct = null;
    ListOf listofReactants = sbmlRxn.getListOfReactants();
    ListOf listofProducts = sbmlRxn.getListOfProducts();
    ListOf listofModifiers = sbmlRxn.getListOfModifiers();

    //
    // Check annotation for reaction - if we are importing an exported VCell model, it will contain annotation for reaction.
    // If annotation has structure name, return the corresponding structure.
    //
    String rxnAnnotation = sbmlRxn.getAnnotation();
    String structName = null;
    if (rxnAnnotation != null && rxnAnnotation.length() > 0) {
        // Get the embedded element in the annotation str (fluxStep or simpleReaction), and the structure attribute from the element.
        org.jdom.Element embeddedElement = getEmbeddedElementInAnnotation(rxnAnnotation, REACTION);
        if (embeddedElement != null) {
            structName = embeddedElement.getAttributeValue(cbit.vcell.xml.XMLTags.StructureAttrTag);
	        // Using the structName, get the structure from the structures (compartments) list.
	        struct = simContext.getModel().getStructure(structName);
	        return struct;
        }
    }

    // 
    // If reaction doesn't have annotation or if annotation doesn't contain structure information, 
    // go thro' reactants and products and decide the structure of the reaction.
    //
    boolean bDifferentStruct = false;
    Structure reactantStructure = null;
    Structure productStructure = null;
    // Get the structure(s) to which the reactant(s) belong. 
    for (int i = 0; i < sbmlRxn.getNumReactants(); i++) {
        SpeciesReference sr = (SpeciesReference) listofReactants.get(i);
        for (int j = 0; j < speciesContexts.length; j++) {
	        String speciesName = (String)SbmlVcSpeciesHash.get(speciesContexts[j].getName());
            if (speciesName != null && speciesName.equals(sr.getSpecies())) {
                reactantStructure = (Structure) speciesContexts[j].getStructure();
                if (struct == null) {
                    struct = reactantStructure;
                } else {
                    // If all the reactants do not belong to one compartment, set bDifferentStruc to 'true' 
                    if (!reactantStructure.getName().equals(struct.getName())) {
                        bDifferentStruct = true;
                    }
                }
                break;
            }
        }

    }
    // Get the structure(s) to which the product(s) belong.
    for (int i = 0; i < sbmlRxn.getNumProducts(); i++) {
        SpeciesReference sr = (SpeciesReference) listofProducts.get(i);
        for (int j = 0; j < speciesContexts.length; j++) {
	        String speciesName = (String)SbmlVcSpeciesHash.get(speciesContexts[j].getName());
            if (speciesName != null && speciesName.equals(sr.getSpecies())) {
                productStructure = (Structure) speciesContexts[j].getStructure();
                if (struct == null) {
                    struct = productStructure;
                } else {
                    // If all the products & reactants do not belong to one compartment, set bDifferentStruc to 'true'
                    if (!productStructure.getName().equals(struct.getName())) {
                        bDifferentStruct = true;
                    }
                }
                break;
            }
        }
    }
    // Get the structure(s) to which the modifier(s) belong. 
    for (int i = 0; i < sbmlRxn.getNumModifiers(); i++) {
        ModifierSpeciesReference msr = (ModifierSpeciesReference) listofModifiers.get(i);
        for (int j = 0; j < speciesContexts.length; j++) {
	        String speciesName = (String)SbmlVcSpeciesHash.get(speciesContexts[j].getName());
            if (speciesName != null && speciesName.equals(msr.getSpecies())) {
                Structure reactionStructure = (Structure) speciesContexts[j].getStructure();
                if (struct == null) {
                    struct = reactionStructure;
                } else {
                    // If all the modifiers & reactants & products do not belong to one compartment, set bDifferentStruc to 'true'
                    if (!reactionStructure.getName().equals(struct.getName())) {
                        bDifferentStruct = true;
                    }
                }
                break;
            }
        }
    }

    //
    // If the reactant(s) and product(s) are in different compartments, check if they are in adjacent compartments.
    // If not, such a reaction is not allowed. Also, if the reactant(s) and product(s) are in different structures, the reaction *HAS* 
    // to take place in the membrane (ie., one of the structures should be a membrane); if not, such a reaction is not valid
    // (in that case, we will need to add a membrane for the reaction to occur.
    //
    String rxnName = getActualName(sbmlRxn);
    if (bDifferentStruct) {
	    if ((reactantStructure.getParentStructure() != null && productStructure.getParentStructure() != null)) {
	        if (reactantStructure.getParentStructure().compareEqual(productStructure)
	            || productStructure.getParentStructure().compareEqual(reactantStructure)) {
	            if (productStructure instanceof Membrane) {
	                struct = productStructure;
	            } else
	                if (reactantStructure instanceof Membrane) {
	                    struct = reactantStructure;
	                } else {
	                    logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.COMPARTMENT_ERROR, "Reactant(s) and Product(s) of reaction: " + rxnName + " are in adjacent compartments, but one of the compartments is not a membrane - this is not allowed in the VCell!");
	                }
	        } else {
		        // if the reactant and product compartments are not adjacent, there should be a membrane separating them where the reaction takes place.
		        // if that is not the case, such a reaction is not permitted in the VCell.

		        // Check if there is an interconnecting membrane between the reactant and product compartments; if not. throw an exception
				Structure membrane = productStructure.getParentStructure();
				if (membrane.getParentStructure() != null && membrane.getParentStructure().compareEqual(reactantStructure)) {
					struct = membrane;    
				} else {
					membrane = reactantStructure.getParentStructure();
					if (membrane.getParentStructure() != null && membrane.getParentStructure().compareEqual(productStructure)) {
						struct = membrane;
					} else {
						logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.COMPARTMENT_ERROR, "Reactant(s) and Product(s) of reaction " + rxnName + " are not in compartments connected by a membrane; this is not handled at this time!");
					}
				} 
	        }
	    } else if (reactantStructure.getParentStructure() == null) {
		    // reactant structure is the outermost compartment
		    Structure membrane = productStructure.getParentStructure();
		    if (membrane.getParentStructure() != null && membrane.getParentStructure().compareEqual(reactantStructure)) {
				struct = membrane;    
		    } else {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.COMPARTMENT_ERROR, "Reactant(s) and Product(s) of reaction " + rxnName + " are not in compartments connected by a membrane; this is not handled at this time!");
		    }
	    } else if (productStructure.getParentStructure() == null) {
		    // product structure is the outermost compartment
		    Structure membrane = reactantStructure.getParentStructure();
		    if (membrane.getParentStructure() != null && membrane.getParentStructure().compareEqual(productStructure)) {
				struct = membrane;    
		    } else {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, TranslationMessage.COMPARTMENT_ERROR, "Reactant(s) and Product(s) of reaction " + rxnName + " are not in compartments connected by a membrane; this is not handled at this time!");
		    }
	    }
    }

    return struct;
}


/*
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
		if (org.sbml.libsbml.Unit.isUnitKind(unitSymbol)) {
			SbmlUnit = VCUnitDefinition.getInstance(unitSymbol);
		} else if (org.sbml.libsbml.Unit.isBuiltIn(unitSymbol)) {
			//check if its a built-in unit that was explicitly specified
			SbmlUnit = (VCUnitDefinition)vcUnitsHash.get(builtInName);
			if (SbmlUnit == null) { 
				SbmlUnit = cbit.vcell.units.SBMLUnitTranslator.getDefaultSBMLUnit(builtInName);
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


	/*
	 *  getSpatialDimentionBuiltInName : 
	 */
private String getSpatialDimentionBuiltInName(int dimension) {
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


/*
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


	public void translateSBMLModel() {
		// Create Virtual Cell Model with species, compartment, etc. and read in the 'values' from the SBML model

		// Add Function Definitions (Lambda functions).
		addFunctionDefinitions();
		// Add Unit definitions
		addUnitDefinitions();
		// Add Rules
		try {
			addRules();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		// Add features/compartments
		addCompartments();
		// Add species/speciesContexts
		addSpecies(); 
		// Add Parameters
		addParameters();
		// Add Reactions
		addReactions();
		// Add Events
		try {
			addEvents();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
}