package cbit.vcell.vcml;
import cbit.vcell.model.FluxReaction;
import org.jdom.*;

import cbit.vcell.xml.MIRIAMHelper;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Structure;
import cbit.vcell.mapping.SimulationContext;
import org.sbml.libsbml.*;
import java.util.Vector;

import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Kinetics;
import java.util.ArrayList;
import org.sbml.libsbml.UnitDefinition;
import org.sbml.libsbml.Compartment;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Feature;
import cbit.vcell.biomodel.BioModel;
import org.sbml.libsbml.SBMLDocument;
import cbit.vcell.units.SBMLUnitTranslator;
import org.sbml.libsbml.Unit;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XMLTags;
import cbit.util.TokenMangler;
import cbit.util.xml.XmlUtil;

/**
 * Insert the type's description here.
 * Creation date: (3/31/2006 1:02:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SBMLExporter {
	private int sbmlLevel = 2;
	private org.sbml.libsbml.Model sbmlModel = null;
	private cbit.vcell.biomodel.BioModel vcBioModel = null;
	private String vcPreferredSimContextName = null;
	private java.util.Hashtable<String, String> globalParamNamesHash = new java.util.Hashtable<String, String>();
	private cbit.vcell.vcml.SBMLExporter.SBMLExportErrorReport fieldErrorReport = null;

	public class SBMLExportErrorReport {
		StringBuffer reportBuffer = null;

		protected SBMLExportErrorReport(StringBuffer argBuffer) {
			reportBuffer = argBuffer;
		}

		public void printWarningAndErrorReport() {
			if (reportBuffer == null) {
				System.out.println("VCell Model was exported to SBML without any errors! Resulting SBML model is VALID");
			} else {
				System.out.println(reportBuffer.toString());
			}
		}
	}

	static
	{
		System.loadLibrary("sbmlj");
	}

/**
 * VC_to_SB_Translator constructor comment.
 */
public SBMLExporter(BioModel argBioModel, int argSbmlLevel) {
	super();
	vcBioModel = argBioModel;
	sbmlLevel = argSbmlLevel;
// 	this.logger = argVCLogger;
}


/**
 * addCompartments comment.
 */
protected void addCompartments() {
	cbit.vcell.model.Structure[] vcStructures = vcBioModel.getModel().getStructures();
	
	for (int i = 0; i < vcStructures.length; i++){
		org.sbml.libsbml.Compartment sbmlCompartment = new org.sbml.libsbml.Compartment(TokenMangler.mangleToSName(vcStructures[i].getName()));
		sbmlCompartment.setName(vcStructures[i].getName());
		VCUnitDefinition sbmlSizeUnit = null;
		if (vcStructures[i] instanceof Feature) {
			Feature vcFeature = (Feature)vcStructures[i];
			sbmlCompartment.setSpatialDimensions(3);
			sbmlCompartment.setConstant(true);
			String outside = null;
			if (vcFeature.getParentStructure() != null) {
				outside = TokenMangler.mangleToSName(vcFeature.getParentStructure().getName());
			}
			if (outside != null) {
				if (outside.length() > 0) {
					sbmlCompartment.setOutside(outside);
				}
			}
			sbmlSizeUnit = VCUnitDefinition.UNIT_L;
			sbmlCompartment.setUnits(cbit.util.TokenMangler.mangleToSName(sbmlSizeUnit.getSymbol()));
		} else if (vcStructures[i] instanceof Membrane) {
			Membrane vcMembrane = (Membrane)vcStructures[i];
			sbmlCompartment.setSpatialDimensions(2);
			sbmlCompartment.setOutside(TokenMangler.mangleToSName(vcMembrane.getOutsideFeature().getName()));
			sbmlSizeUnit = VCUnitDefinition.UNIT_um2;
			sbmlCompartment.setUnits(cbit.util.TokenMangler.mangleToSName(sbmlSizeUnit.getSymbol()));
		}

		StructureMapping vcStructMapping = getMatchingSimContext().getGeometryContext().getStructureMapping(vcStructures[i]);
		try {
			// The unit for 3D compartment size in VCell is um3, we are going to write it out in litres while exporting to SBML
			// (we have set the units for compartment size as L above). Hence multiplying the size expression with the conversion factor
			// between VC and SBML units for the compartment size. 
			
			Expression sizeExpr = null;
			VCUnitDefinition vcSizeUnit = vcStructMapping.getSizeParameter().getUnitDefinition();
			if (vcStructMapping.getSizeParameter().getExpression() == null) {
				throw new RuntimeException("Compartment size not set for compartment \"" + vcStructures[i].getName() + "\" ; Please set size and try exporting again.");
			}
			double factor = 1.0;
			factor = vcSizeUnit.convertTo(factor, sbmlSizeUnit);
			sizeExpr = Expression.mult(vcStructMapping.getSizeParameter().getExpression(), new Expression(factor));
			sbmlCompartment.setSize(sizeExpr.evaluateConstant());
		} catch (cbit.vcell.parser.ExpressionException e) {
			// If it is in the catch block, it means that the compartment size was probably not a double, but an assignment.
			// Check if the expression for the compartment size is not null and add it as an assignment rule.
			Expression sizeExpr = vcStructMapping.getSizeParameter().getExpression();
			if (sizeExpr != null) {
				ASTNode ruleFormulaNode = getFormulaFromExpression(sizeExpr);
				AssignmentRule assignRule = new AssignmentRule(vcStructures[i].getName(), ruleFormulaNode);
				// If compartmentSize is specified by an assignment rule, the 'constant' field should be set to 'false' (default - true).
				sbmlCompartment.setConstant(false);
				sbmlModel.addRule(assignRule);
			}
		}
		
		Element annotationElement = null;
		String sbmlAnnotationString = sbmlCompartment.getAnnotationString();
		if(sbmlAnnotationString == null || sbmlAnnotationString.length() == 0){
			annotationElement = new Element(XMLTags.SbmlAnnotationTag, "");
		}else{
			annotationElement = XmlUtil.stringToXML(sbmlAnnotationString, null);
		}
		MIRIAMHelper.addToSBML(annotationElement, vcStructures[i].getMIRIAMAnnotation(), false);
		sbmlCompartment.setAnnotation(XmlUtil.xmlToString(annotationElement, true));
		sbmlModel.addCompartment(sbmlCompartment);
	}
}


/**
 * addKineticParameterUnits:
 */
private void addKineticParameterUnits(ArrayList<String> unitsList) {

	//
	// Get all kinetic parameters from simple reactions and flux reactions from the Biomodel
	// For each parameter,
	//		get its unit (VCunitDefinition)
	//		check if it is part of unitsList - if so, continue
	//		check if it is a base unit - if so, continue
	//		else, get the converted unit (VC -> SBML)
	//		add unit to sbmlModel unit definition
	//

	Vector<Parameter> paramsVector = new Vector<Parameter>();
	ReactionStep[] vcReactions = vcBioModel.getModel().getReactionSteps();
	for (int i = 0; i < vcReactions.length; i++) {
		Kinetics rxnKinetics = vcReactions[i].getKinetics();
		Parameter[] kineticParams = rxnKinetics.getKineticsParameters();
		for (int j = 0; j < kineticParams.length; j++) {
			paramsVector.addElement(kineticParams[j]);
		}
	}

	for (int i = 0; i < paramsVector.size(); i++){
		Parameter param = (Parameter)paramsVector.elementAt(i);
		VCUnitDefinition paramUnitDefn = param.getUnitDefinition();
		if (paramUnitDefn == null || paramUnitDefn.isTBD()) {
			continue;
		}
		String unitSymbol = cbit.util.TokenMangler.mangleToSName(paramUnitDefn.getSymbol());
		// If this unit is present in the unitsList (already in the list of unitDefinitions for SBML model), continue.
		if (unitSymbol == null || unitsList.contains(unitSymbol)) {
			continue;
		}
		// If this unit is a base unit, continue.
		if (SBMLUnitTranslator.isSbmlBaseUnit(unitSymbol)) {
			continue;
		}
		if (!paramUnitDefn.isTBD()) {
			UnitDefinition newUnitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(paramUnitDefn);
			if (newUnitDefn != null) {
				unitsList.add(unitSymbol);
				sbmlModel.addUnitDefinition(newUnitDefn);
			}
		}
	}
}


/**
 * At present, the Virtual cell doesn't support global parameters
 */
protected void addParameters() {
}


/**
 * addReactions comment.
 */
protected void addReactions() {
	cbit.vcell.mapping.ReactionSpec[] vcReactionSpecs = getMatchingSimContext().getReactionContext().getReactionSpecs();

	for (int i = 0; i < vcReactionSpecs.length; i++){
		if (vcReactionSpecs[i].isExcluded()) {
			continue;
		}
		ReactionStep vcReactionStep = vcReactionSpecs[i].getReactionStep();
		//Create sbml reaction
		String rxnName = vcReactionStep.getName();
		org.sbml.libsbml.Reaction sbmlReaction = new org.sbml.libsbml.Reaction(cbit.util.TokenMangler.mangleToSName(vcReactionStep.getName()));
		sbmlReaction.setName(rxnName);
			

		// If the reactionStep is a flux reaction, add the details to the annotation (structure, carrier valence, flux carrier, fluxOption, etc.)
		// If reactionStep is a simple reaction, add annotation to indicate the structure of reaction.
		// Useful when roundtripping ...
		Element annotationElement = null;
		try {
			annotationElement = getAnnotationElement(vcReactionStep);
		} catch (cbit.vcell.xml.XmlParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Could not get JDOM element for annotation : " + e.getMessage());
		}
		MIRIAMHelper.addToSBML(annotationElement, vcReactionSpecs[i].getReactionStep().getMIRIAMAnnotation(),false);
		sbmlReaction.setAnnotation(cbit.util.xml.XmlUtil.xmlToString(annotationElement));
		
		// Get reaction kineticLaw
		Kinetics vcRxnKinetics = vcReactionStep.getKinetics();
		org.sbml.libsbml.KineticLaw sbmlKLaw = new org.sbml.libsbml.KineticLaw();

		try {
			// Convert expression from kinetics rate parameter into MathML and use libSBMl utilities to convert it to formula
			// (instead of directly using rate parameter's expression infix) to maintain integrity of formula :
			// for example logical and inequalities are not handled gracefully by libSBMl if expression.infix is used.
			Expression correctedRateExpr = null; 
			if (vcRxnKinetics instanceof DistributedKinetics) {
				Compartment sbmlCompartment = sbmlModel.getCompartment(cbit.util.TokenMangler.mangleToSName(vcReactionStep.getStructure().getName()));
				correctedRateExpr = Expression.mult(((DistributedKinetics)vcRxnKinetics).getReactionRateParameter().getExpression(), new Expression(sbmlCompartment.getId()));
	
				// If the reaction is not in a feature, but in a membrane, use/set subtanceUnits for the kinetic law.
				if (!isInFeature(vcReactionStep.getStructure().getName())) {
					if (vcReactionStep instanceof cbit.vcell.model.SimpleReaction) {
						sbmlKLaw.setSubstanceUnits(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_molecules.getSymbol()));
					} else if (vcReactionStep instanceof cbit.vcell.model.FluxReaction) {
						sbmlKLaw.setSubstanceUnits(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_umol_L_per_um3.getSymbol()));
						// correctedRateExpr = Expression.mult(correctedRateExpr, new Expression(1e-15));
					} else {
						throw new RuntimeException("Unknown reaction type - cannot handle at this time");
					}
				}
			} else if (vcRxnKinetics instanceof LumpedKinetics) {
				correctedRateExpr = ((LumpedKinetics)vcRxnKinetics).getLumpedReactionRateParameter().getExpression();
				sbmlKLaw.setSubstanceUnits(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_molecules.getSymbol()));
			} else {
				throw new RuntimeException("Unknown kinetics type for reaction : " + vcReactionStep.getName());
			}


			// Add parameters, if any, to the kineticLaw
			Kinetics.KineticsParameter[] vcKineticsParams = vcRxnKinetics.getKineticsParameters();

			// In the first pass thro' the kinetic params, store the non-numeric param names and expressions in arrays
			String[] kinParamNames = new String[vcKineticsParams.length];
			Expression[] kinParamExprs = new Expression[vcKineticsParams.length];
			for (int j = 0; j < vcKineticsParams.length; j++){
				if ( (vcKineticsParams[j].getRole() != Kinetics.ROLE_ReactionRate) && (vcKineticsParams[j].getRole() != Kinetics.ROLE_LumpedReactionRate) ) {
					// if expression of kinetic param does not evaluate to a double, the param value is defined by a rule. 
					// Since local reaction parameters cannot be defined by a rule, such parameters (with rules) are exported as global parameters.
					if ( (vcKineticsParams[j].getRole() == Kinetics.ROLE_CurrentDensity && (!vcKineticsParams[j].getExpression().isZero())) || 
							(vcKineticsParams[j].getRole() == Kinetics.ROLE_LumpedCurrent && (!vcKineticsParams[j].getExpression().isZero())) ) {
						throw new RuntimeException("Electric current not handled by SBML export; failed to export reaction \"" + vcReactionStep.getName() + "\" at this time");
					}
					if (!vcKineticsParams[j].getExpression().isNumeric()) {		// NON_NUMERIC KINETIC PARAM
						// Create new name for kinetic parameter and store it in kinParamNames, store corresponding exprs in kinParamExprs
						// Will be used later to add this param as global.
						String newParamName = TokenMangler.mangleToSName(vcKineticsParams[j].getName() + "_" + vcReactionStep.getName());
						kinParamNames[j] = newParamName;
						kinParamExprs[j] = new Expression(vcKineticsParams[j].getExpression());
					} 
				}
			} // end for (j) - first pass

			// Second pass - Check if any of the numeric parameters is present in any of the non-numeric param expressions
			// If so, these need to be added as global param (else the SBML doc will not be valid)
			for (int j = 0; j < vcKineticsParams.length; j++){
				if ( (vcKineticsParams[j].getRole() != Kinetics.ROLE_ReactionRate) && (vcKineticsParams[j].getRole() != Kinetics.ROLE_LumpedReactionRate) ) {
					// if expression of kinetic param evaluates to a double, the parameter value is set
					if ( (vcKineticsParams[j].getRole() == Kinetics.ROLE_CurrentDensity && (!vcKineticsParams[j].getExpression().isZero())) || 
							(vcKineticsParams[j].getRole() == Kinetics.ROLE_LumpedCurrent && (!vcKineticsParams[j].getExpression().isZero())) ) {
						throw new RuntimeException("Electric current not handled by SBML export; failed to export reaction \"" + vcReactionStep.getName() + "\" at this time");
					}
					if (vcKineticsParams[j].getExpression().isNumeric()) {		// NUMERIC KINETIC PARAM
						// check if it is used in other parameters that have expressions,
						boolean bAddedParam = false;
						String origParamName = vcKineticsParams[j].getName();
						String newParamName = TokenMangler.mangleToSName(origParamName + "_" + vcReactionStep.getName());
						for (int k = 0; k < vcKineticsParams.length; k++){
							if (kinParamExprs[k] != null) {
								// The param could be in the expression for any other param
								if (kinParamExprs[k].hasSymbol(origParamName)) {
									// if param is present in non-numeric param expression, this param has to be global
									// mangle its name to avoid conflict with other globals
									if (globalParamNamesHash.get(newParamName) == null) {
										globalParamNamesHash.put(newParamName, newParamName);
										org.sbml.libsbml.Parameter sbmlKinParam = new org.sbml.libsbml.Parameter(newParamName);
										sbmlKinParam.setValue(vcKineticsParams[j].getConstantValue());
										// Set SBML units for sbmlParam using VC units from vcParam  
										if (!vcKineticsParams[j].getUnitDefinition().isTBD()) {
											sbmlKinParam.setUnits(cbit.util.TokenMangler.mangleToSName(vcKineticsParams[j].getUnitDefinition().getSymbol()));
										}
										sbmlModel.addParameter(sbmlKinParam);
										bAddedParam = true;
									} else {
										// need to get another name for param and need to change all its refereces in the other kinParam euqations.
									}
									// update the expression to contain new name, since the globalparam has new name
									kinParamExprs[k].substituteInPlace(new Expression(origParamName), new Expression(newParamName));
								}
							} 
						}	// end for - k
						// If the param hasn't been added yet, it is definitely a local param. add it to kineticLaw now.
						if (!bAddedParam) {
							org.sbml.libsbml.Parameter sbmlKinParam = new org.sbml.libsbml.Parameter(origParamName);
							sbmlKinParam.setValue(vcKineticsParams[j].getConstantValue());
							// Set SBML units for sbmlParam using VC units from vcParam  
							if (!vcKineticsParams[j].getUnitDefinition().isTBD()) {
								sbmlKinParam.setUnits(cbit.util.TokenMangler.mangleToSName(vcKineticsParams[j].getUnitDefinition().getSymbol()));
							}
							sbmlKLaw.addParameter(sbmlKinParam);
						} else {
							// if parameter has been added to global param list, its name has been mangled, 
							// hence change its occurance in rate expression if it contains that param name
							if (correctedRateExpr.hasSymbol(origParamName)) {
								correctedRateExpr.substituteInPlace(new Expression(origParamName), new Expression(newParamName));
							}
						}
					}
				}
			} // end for (j) - second pass

			// In the third pass thro' the kinetic params, the expressions are changed to reflect the new names set above
			// (using the kinParamNames and kinParamExprs above) to ensure uniqueness in the global parameter names.
			for (int j = 0; j < vcKineticsParams.length; j++) {
				if ( ((vcKineticsParams[j].getRole() != Kinetics.ROLE_ReactionRate) && (vcKineticsParams[j].getRole() != Kinetics.ROLE_LumpedReactionRate)) && !(vcKineticsParams[j].getExpression().isNumeric())) {
					String oldName = vcKineticsParams[j].getName();
					String newName = kinParamNames[j];
					// change the name of this parameter in the rate expression 
					correctedRateExpr.substituteInPlace(new Expression(oldName), new Expression(newName));
					// Change the occurence of this param in other param expressions
					for (int k = 0; k < vcKineticsParams.length; k++){
						if ( ((vcKineticsParams[k].getRole() != Kinetics.ROLE_ReactionRate) && (vcKineticsParams[j].getRole() != Kinetics.ROLE_LumpedReactionRate)) && !(vcKineticsParams[k].getExpression().isNumeric())) {
							if (k != j && vcKineticsParams[k].getExpression().hasSymbol(oldName)) {
								// for all params except the current param represented by index j (whose name was changed)
								kinParamExprs[k].substituteInPlace(new Expression(oldName), new Expression(newName));
							}
							if (k == j && vcKineticsParams[k].getExpression().hasSymbol(oldName)) {
								throw new RuntimeException("A parameter cannot refer to itself in its expression");
							}
						}
					}	// end for - k
				} 
			}	// end for (j)  - third pass
					
			// In the fourth pass thro' the kinetic params, the non-numeric params are added to the global params of the model
			for (int j = 0; j < vcKineticsParams.length; j++){
				if (((vcKineticsParams[j].getRole() != Kinetics.ROLE_ReactionRate) && (vcKineticsParams[j].getRole() != Kinetics.ROLE_LumpedReactionRate)) && !(vcKineticsParams[j].getExpression().isNumeric())) {
					// Now, add this param to the globalParamNamesHash and add a global parameter to the sbmlModel
					String paramName = kinParamNames[j];
					if (globalParamNamesHash.get(paramName) == null) {
						globalParamNamesHash.put(paramName, paramName);
					} else {
						// need to get another name for param and need to change all its refereces in the other kinParam euqations.
					}
					ASTNode paramFormulaNode = getFormulaFromExpression(kinParamExprs[j]);
					AssignmentRule sbmlParamAssignmentRule = new AssignmentRule(paramName, paramFormulaNode);
					sbmlModel.addRule(sbmlParamAssignmentRule);
					org.sbml.libsbml.Parameter sbmlKinParam = new org.sbml.libsbml.Parameter(paramName);
					if (!vcKineticsParams[j].getUnitDefinition().isTBD()) {
						sbmlKinParam.setUnits(cbit.util.TokenMangler.mangleToSName(vcKineticsParams[j].getUnitDefinition().getSymbol()));
					}
					// Since the parameter is being specified by a Rule, its 'constant' field shoud be set to 'false' (default - true).
					sbmlKinParam.setConstant(false);
					sbmlModel.addParameter(sbmlKinParam);
				}
			} // end for (j) - fourth pass

			// After making all necessary adjustments to the rate expression, now set the sbmlKLaw.
			ASTNode exprFormulaNode = getFormulaFromExpression(correctedRateExpr);
			sbmlKLaw.setMath(exprFormulaNode);
		} catch (cbit.vcell.parser.ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error getting value of parameter : "+e.getMessage());
		}
		
		// Add kineticLaw to sbmlReaction
		sbmlReaction.setKineticLaw(sbmlKLaw);
		
		// Add reactants, products, modifiers
		// Simple reactions have catalysts, fluxes have 'flux' 
		cbit.vcell.model.ReactionParticipant[] rxnParticipants = vcReactionStep.getReactionParticipants();
		for (int j = 0; j < rxnParticipants.length; j++){
			if (rxnParticipants[j] instanceof cbit.vcell.model.Reactant) {
				SpeciesReference reactantSpRef = new SpeciesReference(rxnParticipants[j].getSpeciesContext().getName(), Double.parseDouble(Integer.toString(rxnParticipants[j].getStoichiometry())));
				sbmlReaction.addReactant(reactantSpRef);
			} else if (rxnParticipants[j] instanceof cbit.vcell.model.Product) {
				SpeciesReference pdtSpRef = new SpeciesReference(rxnParticipants[j].getSpeciesContext().getName(), Double.parseDouble(Integer.toString(rxnParticipants[j].getStoichiometry())));
				sbmlReaction.addProduct(pdtSpRef);				
			} else if (rxnParticipants[j] instanceof cbit.vcell.model.Catalyst) {
				ModifierSpeciesReference modifierSpRef = new ModifierSpeciesReference(rxnParticipants[j].getSpeciesContext().getName());
				sbmlReaction.addModifier(modifierSpRef);
			} else if (rxnParticipants[j] instanceof cbit.vcell.model.Flux) {
				// For a flux reaction, the reaction participants are 'fluxes'. The 'outside' participant is considered the reactant while
				// the 'inside' particpant is considered the product. Catalysts are modifiers.
				cbit.vcell.model.Flux flux = (cbit.vcell.model.Flux)rxnParticipants[j];
				if (vcReactionStep.getStructure() instanceof Membrane) {
					Membrane fluxMembrane = (Membrane)((cbit.vcell.model.FluxReaction)vcReactionStep).getStructure();
					if (fluxMembrane.getOutsideFeature().compareEqual(flux.getStructure())) {
						SpeciesReference reactantSpRef = new SpeciesReference(flux.getSpeciesContext().getName(), Double.parseDouble(Integer.toString(flux.getStoichiometry())));
						sbmlReaction.addReactant(reactantSpRef);
					} else if (fluxMembrane.getInsideFeature().compareEqual(flux.getStructure())) {
						SpeciesReference pdtSpRef = new SpeciesReference(rxnParticipants[j].getSpeciesContext().getName(), Double.parseDouble(Integer.toString(rxnParticipants[j].getStoichiometry())));
						sbmlReaction.addProduct(pdtSpRef);				
					}
				}
			}
		}

		if (vcReactionSpecs[i].isFast()) {
			sbmlReaction.setFast(true);
		}
		// Add the reaction to the sbmlModel
		sbmlModel.addReaction(sbmlReaction);
		sbmlKLaw.delete();
		sbmlReaction.delete();
	}
}


/**
 * addSpecies comment.
 */
protected void addSpecies() {
	SpeciesContext[] vcSpeciesContexts = vcBioModel.getModel().getSpeciesContexts();

	for (int i = 0; i < vcSpeciesContexts.length; i++){
		org.sbml.libsbml.Species sbmlSpecies = new org.sbml.libsbml.Species(vcSpeciesContexts[i].getName());
		// Assuming that at this point, the compartment(s) for the model are already filled in.
		Compartment compartment = sbmlModel.getCompartment(TokenMangler.mangleToSName(vcSpeciesContexts[i].getStructure().getName()));
		if (compartment != null) {
			sbmlSpecies.setCompartment(compartment.getId());
		}

		// 'hasSubstanceOnly' field will be 'false', since VC deals only with initial concentrations and not initial amounts.
		sbmlSpecies.setHasOnlySubstanceUnits(false);

		// Get (and set) the initial concentration value
		// Get the simulationContext (application) that matches the 'vcPreferredSimContextName' field.
		cbit.vcell.mapping.SimulationContext simContext = getMatchingSimContext();
		if (simContext == null) {
			throw new RuntimeException("No simcontext (application) specified; Cannot proceed.");
		}
		
		// Get the speciesContextSpec in the simContext corresponding to the 'speciesContext'; and extract its initial concentration value.
		cbit.vcell.mapping.SpeciesContextSpec vcSpeciesContextsSpec = simContext.getReactionContext().getSpeciesContextSpec(vcSpeciesContexts[i]);
		try {
			sbmlSpecies.setInitialConcentration(vcSpeciesContextsSpec.getInitialConditionParameter().getConstantValue());
		} catch (cbit.vcell.parser.ExpressionException e) {
			// If it is in the catch block, it means that the initial concentration of the species was not a double, but an assignment, probably.
			// Check if the expression for the species is not null and add it as an assignment rule.
			if (vcSpeciesContextsSpec.getInitialConditionParameter().getExpression() != null) {
				ASTNode ruleFormulaNode = getFormulaFromExpression(vcSpeciesContextsSpec.getInitialConditionParameter().getExpression());
				AssignmentRule assignRule = new AssignmentRule(vcSpeciesContextsSpec.getSpeciesContext().getName(), ruleFormulaNode);
				sbmlModel.addRule(assignRule);
			}
		}

		// Get (and set) the boundary condition value
		boolean bBoundaryCondition = getBoundaryCondition(vcSpeciesContexts[i]);
		sbmlSpecies.setBoundaryCondition(bBoundaryCondition); 

		// If species is in membrane, the units should be in molecules/um2; hence set the spatialSize and substance units
		if (!isInFeature(vcSpeciesContexts[i].getStructure().getName())) {
			sbmlSpecies.setSpatialSizeUnits(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_um2.getSymbol()));
			sbmlSpecies.setSubstanceUnits(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_molecules.getSymbol()));
		}

		// Convert from VC -> SBML units, i.e., from uM -> ??		
		//sbmlSpecies.setUnits();

		// Add the common name of species to annotation, and add an annotation element to the species.
		// This is required later while trying to read in fluxes ...
		Namespace ns = Namespace.getNamespace(Translator.SBML_VCML_NS);
		Element annotationElement = new Element(XMLTags.SbmlAnnotationTag, "");
		Element vcellInfoTag = new Element(XMLTags.VCellInfoTag, ns);
		Element speciesElement = new Element(XMLTags.SpeciesTag, ns);
		speciesElement.setAttribute(XMLTags.NameAttrTag, cbit.util.TokenMangler.mangleToSName(vcSpeciesContexts[i].getSpecies().getCommonName()));
		vcellInfoTag.addContent(speciesElement);
		annotationElement.addContent(vcellInfoTag);
		MIRIAMHelper.addToSBML(annotationElement, vcSpeciesContexts[i].getSpecies().getMIRIAMAnnotation(),false);
		sbmlSpecies.setAnnotation(cbit.util.xml.XmlUtil.xmlToString(annotationElement,true));

		// Add the species to the sbmlModel
		sbmlModel.addSpecies(sbmlSpecies);
	}
}


/**
 * Add unit definitions to the model.
 */
protected void addUnitDefinitions() {
	// Define mole - SUBSTANCE
	UnitDefinition unitDefn = new UnitDefinition(SBMLUnitTranslator.SUBSTANCE);
	Unit moleUnit = new Unit("mole", 1, -6);
	unitDefn.addUnit(moleUnit);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define litre - VOLUME
	unitDefn = new UnitDefinition(SBMLUnitTranslator.VOLUME);
	Unit litreUnit = new Unit("litre", 1, 0);
	unitDefn.addUnit(litreUnit);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um2 - AREA
	unitDefn = new UnitDefinition(SBMLUnitTranslator.AREA);
	Unit um2Unit = new Unit("metre", 2, -6);
	unitDefn.addUnit(um2Unit);
	sbmlModel.addUnitDefinition(unitDefn);

	// Redefine 'item' as molecules
	unitDefn = new UnitDefinition(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_molecules.getSymbol()));
	Unit itemUnit = new Unit("item");
	unitDefn.addUnit(itemUnit);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define umol.L.um-3 - VCell (actual units of concentration, but with a multiplication factor.  Value = 1e-15 umol).
	unitDefn = new UnitDefinition(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_umol_L_per_um3.getSymbol()));
	org.sbml.libsbml.Unit umol_unit = new Unit("mole", 1, -21);
	unitDefn.addUnit(umol_unit);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um2 - VCell
	unitDefn = new UnitDefinition(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_um2.getSymbol()));
	org.sbml.libsbml.Unit um2_unit = new Unit("metre", 2, -6);
	unitDefn.addUnit(um2_unit);
	sbmlModel.addUnitDefinition(unitDefn);

	// Add units from paramater list in kinetics
	ArrayList<String> unitList = new ArrayList<String>();
	unitList.add(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_molecules.getSymbol()));
	unitList.add(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_umol_L_per_um3.getSymbol()));
	unitList.add(cbit.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_um2.getSymbol()));
	addKineticParameterUnits(unitList);
}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2006 5:09:09 PM)
 * @return java.lang.String
 */
private void generateErrorReport(SBMLDocument sbmlDoc) {
	long numFailedChecks = sbmlDoc.checkConsistency();
	StringBuffer errorReportBuffer = new StringBuffer();
	errorReportBuffer.append("\n\nBIOMODEL : " + vcBioModel.getName() + " : Num Of Failed Checks = " + numFailedChecks + "\n");
	long numSBMLErrors = sbmlDoc.getNumErrors();
	errorReportBuffer.append("\nWARNINGS : " + sbmlDoc.getErrorLog().getNumFailsWithSeverity(XMLError.Warning));
	for (long i = 0; i < numSBMLErrors; i++){
		SBMLError sbmlError = sbmlDoc.getError(i);
		if (sbmlError.getSeverity()==XMLError.Warning){
			errorReportBuffer.append("\n\tlibSBML issue: severity=" + sbmlError.getSeverity()+ ", message=" + sbmlError.getMessage());
		}
	}
	errorReportBuffer.append("\n------- END REPORT-------");

	// set the report inner class :
	setErrorReport(new SBMLExportErrorReport(errorReportBuffer));
	
}


/**
 * 	getAnnotationElement : 
 *	For a flux reaction, we need to add an annotation specifying the structure, flux carrier, carrier valence and fluxOption. 
 *  For a simple reaction, we need to add a annotation specifying the structure (useful for import)
 *  Using XML JDOM elements, so that it is convenient for libSBML setAnnotation (requires the annotation to be provided as an xml string).
 *
 **/
private Element getAnnotationElement(ReactionStep reactionStep) throws cbit.vcell.xml.XmlParseException {
	Namespace vcNamespace = Namespace.getNamespace(Translator.SBML_VCML_NS);  		//"http://www.vcell.org/vcell";
	Element annotationElement = new Element(XMLTags.SbmlAnnotationTag, "");

	Element vcellInfoElement = new Element(XMLTags.VCellInfoTag, vcNamespace);
	Element rxnElement = null;
	
	if (reactionStep instanceof FluxReaction) {
		FluxReaction fluxRxn = (FluxReaction)reactionStep;
		// Element for flux reaction. Write out the structure and flux carrier name.
		rxnElement = new Element(XMLTags.FluxStepTag, vcNamespace);
		rxnElement.setAttribute(XMLTags.StructureAttrTag, fluxRxn.getStructure().getName());
		rxnElement.setAttribute(XMLTags.FluxCarrierAttrTag, TokenMangler.mangleToSName(fluxRxn.getFluxCarrier().getCommonName()));

		// Get the charge carrier valence (if its expression has constant value).
		Expression tempExp = null;
		int valence;
		try {
			tempExp = fluxRxn.getChargeCarrierValence().getExpression();
			double d = (int)tempExp.evaluateConstant();
			if ((int)d != d) {
				throw new XmlParseException("Invalid value for charge valence: " + d + " for reaction: " + fluxRxn.getName());
			}
			valence = (int)d;
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("Invalid value for the charge valence: " + 
										(tempExp == null ? "null": tempExp.infix()) + " for reaction: " + fluxRxn.getName()+" : "+e.getMessage());
		}
		rxnElement.setAttribute(XMLTags.FluxCarrierValenceAttrTag, String.valueOf(valence));

		// Get the physics option value.
		if (fluxRxn.getPhysicsOptions() == ReactionStep.PHYSICS_ELECTRICAL_ONLY){
			rxnElement.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionElectricalOnly);
		}else if (fluxRxn.getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
			rxnElement.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionMolecularAndElectrical);
		}else if (fluxRxn.getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_ONLY){
			rxnElement.setAttribute(XMLTags.FluxOptionAttrTag, XMLTags.FluxOptionMolecularOnly);
		}

	} else if (reactionStep instanceof cbit.vcell.model.SimpleReaction) {
		// Element for a simple reaction - just store structure name - will be useful while importing.
		cbit.vcell.model.SimpleReaction simpleRxn = (cbit.vcell.model.SimpleReaction)reactionStep;
		rxnElement = new org.jdom.Element(cbit.vcell.xml.XMLTags.SimpleReactionTag, vcNamespace);
		rxnElement.setAttribute(cbit.vcell.xml.XMLTags.StructureAttrTag, simpleRxn.getStructure().getName());
	}

	// Add rate name as an element of annotation - this is especially useful when roundtripping VCell models, when the reaction
	// rate parameters have been renamed by user.
	Element rateElement = new Element(XMLTags.ReactionRateTag, vcNamespace);
	if (reactionStep.getKinetics() instanceof DistributedKinetics){
		rateElement.setAttribute(XMLTags.NameAttrTag, ((DistributedKinetics)reactionStep.getKinetics()).getReactionRateParameter().getName());
	}else if (reactionStep.getKinetics() instanceof LumpedKinetics){
		rateElement.setAttribute(XMLTags.NameAttrTag, ((LumpedKinetics)reactionStep.getKinetics()).getLumpedReactionRateParameter().getName());
	}else{
		throw new RuntimeException("unexpected kinetic type "+reactionStep.getKinetics().getClass().getName());
	}

	vcellInfoElement.addContent(rxnElement);
	vcellInfoElement.addContent(rateElement);
	annotationElement.addContent(vcellInfoElement);
	
	return annotationElement;
}


/**
 * 	getInitialConc : 
 */
private boolean getBoundaryCondition(SpeciesContext speciesContext) {

	// Get the simulationContext (application) that matches the 'vcPreferredSimContextName' field.
	cbit.vcell.mapping.SimulationContext simContext = getMatchingSimContext();
	if (simContext == null) {
		return false;
	}
	
	// Get the speciesContextSpec in the simContext corresponding to the 'speciesContext'; and extract its boundary condition value.
	
	cbit.vcell.mapping.SpeciesContextSpec[] vcSpeciesContextsSpecs = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < vcSpeciesContextsSpecs.length; i++){
		if (speciesContext.compareEqual(vcSpeciesContextsSpecs[i].getSpeciesContext())) {
			boolean bBoundaryCondition = vcSpeciesContextsSpecs[i].isConstant();
			return bBoundaryCondition;
		}
	}

	return false;
}


/**
 * 	getFormulaFromExpression : 
 *  Expression infix strings are not handled gracefully by libSBML, esp when ligical or inequality operators are used.
 *  This method 
 *		converts the expression into MathML using ExpressionMathMLPrinter;
 *		converts that into libSBMl-readable formula using libSBML utilties.
 *		returns the new formula string.
 *  
 */
private ASTNode getFormulaFromExpression(Expression expression) { 
	// Convert expression into MathML string
	String expMathMLStr = null;

	try {
		expMathMLStr = cbit.vcell.parser.ExpressionMathMLPrinter.getMathML(expression);
	} catch (java.io.IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error converting expression to MathML string :" + e.getMessage());
	} catch (cbit.vcell.parser.ExpressionException e1) {
		e1.printStackTrace(System.out);
		throw new RuntimeException("Error converting expression to MathML string :" + e1.getMessage());
	}
	
	// Use libSBMl routines to convert MathML string to MathML document and a libSBML-readable formula string

	ASTNode mathNode = libsbml.readMathMLFromString(expMathMLStr);
	return mathNode.deepCopy();
}


/**
 * 	getMatchingSimContext :
 	Policy : If requested application (simContext) does not exist, behave as if none exist.
    If it exists, but it is 1/2/3 dimensional (level 2), let it go through.
    If no application specified, return the first one that is not 1/2/3 dimensional.
    If none exist, return null.

 */
private SimulationContext getMatchingSimContext() {

	// Get the simulationContext (application) that matches the 'vcPreferredSimContextName' field.

	SimulationContext simContext = null;

	SimulationContext[] vcSimContexts = vcBioModel.getSimulationContexts();

	if (vcPreferredSimContextName != null) {
		for (int i = 0; i < vcSimContexts.length; i++){
			if (vcPreferredSimContextName.equals(vcSimContexts[i].getName())) {
				simContext = vcSimContexts[i];
			}
		}
		if (simContext == null) {
			System.err.println("Error : specified simulationContext " + vcPreferredSimContextName + " does not exist");
		}
	} else {
		// Check if it is non-spatial - SBML does not support geometries with dimensions > 0 !!
		for (int i = 0; i < vcSimContexts.length; i++){
			int dimension = vcSimContexts[i].getGeometry().getDimension();
			if (dimension == 1 || dimension == 2 || dimension == 3) {
				continue;
			} else {
				// Return  the first non-spatial simulationContext in 'simContext'.
				simContext = vcSimContexts[i];
				return simContext;
			}
		}
	}
	
	return simContext;
}


/**
 * VC_to_SB_Translator constructor comment.
 */
public String getSBMLFile() {

	// Create the sbmlModel, so that other details can be added to it in translateBioModel()
	sbmlModel = new org.sbml.libsbml.Model(TokenMangler.mangleToSName(vcBioModel.getName()));
	sbmlModel.setName(vcBioModel.getName());
	translateBioModel();

	Element annotationElement = null;
	String sbmlAnnotationString = sbmlModel.getAnnotationString();
	if(sbmlAnnotationString == null || sbmlAnnotationString.length() == 0){
		annotationElement = new Element(XMLTags.SbmlAnnotationTag, "");
	}else{
		annotationElement = XmlUtil.stringToXML(sbmlAnnotationString, null);
	}
	MIRIAMHelper.addToSBML(annotationElement, vcBioModel.getMIRIAMAnnotation(), false);
	sbmlModel.setAnnotation(XmlUtil.xmlToString(annotationElement, true));

	//
	// Set the SBMLDocument with the SBML model. 
	//
	// ***** Can use a different constructor to set level and version *****
	//
	SBMLDocument sbmlDocument = new org.sbml.libsbml.SBMLDocument(sbmlLevel);
	sbmlDocument.setModel(sbmlModel);

	org.sbml.libsbml.SBMLWriter sbmlWriter = new org.sbml.libsbml.SBMLWriter();
	String sbmlStr = sbmlWriter.writeToString(sbmlDocument);

	generateErrorReport(sbmlDocument);

	sbmlModel.delete();
	sbmlDocument.delete();
	sbmlWriter.delete();	

	return sbmlStr;
}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2006 5:09:09 PM)
 * @return java.lang.String
 */
public java.lang.String getVcPreferredSimContextName() {
	return vcPreferredSimContextName;
}


/**
 * 	isInFeature : 
 *	Check if the speciesStructure is a Feature or membrane, 
 *	Return 'true' if the species is in a feature; 'false' if species is in a membrane
 **/
private boolean isInFeature(String speciesStructureName) {
	Structure[] vcStructures = vcBioModel.getModel().getStructures();
	for (int i = 0; i < vcStructures.length; i++){
		if (vcStructures[i] instanceof Feature && speciesStructureName.equals(vcStructures[i].getName())) {
			return true;
		} else if (vcStructures[i] instanceof Membrane && speciesStructureName.equals(vcStructures[i].getName())) {
			return false;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2006 5:09:09 PM)
 * @return java.lang.String
 */
public void printErrorReport() {
	fieldErrorReport.printWarningAndErrorReport();
}


/**
 * Insert the method's description here.
 * Creation date: (10/31/2006 12:17:53 PM)
 * @param newFieldErrorReport cbit.vcell.vcml.SBMLExporter.SBMLExportErrorReport
 */
private void setErrorReport(cbit.vcell.vcml.SBMLExporter.SBMLExportErrorReport newFieldErrorReport) {
	fieldErrorReport = newFieldErrorReport;
}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2006 5:09:09 PM)
 * @param newVcPreferredSimContextName java.lang.String
 */
public void setVcPreferredSimContextName(java.lang.String newVcPreferredSimContextName) {
	vcPreferredSimContextName = newVcPreferredSimContextName;
}


/**
 * VC_to_SB_Translator constructor comment.
 */
public void translateBioModel() {
	// 'Parse' the Virtual cell model into an SBML model
	addUnitDefinitions();
	// Add features/compartments
	addCompartments();
	// Add species/speciesContexts
	addSpecies(); 
	// Add Parameters
	addParameters();
	// Add Reactions
	addReactions();
}
}