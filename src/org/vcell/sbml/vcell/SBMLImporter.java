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

import org.jdom.Element;
import org.sbml.libsbml.ASTNode;
import org.sbml.libsbml.AlgebraicRule;
import org.sbml.libsbml.AssignmentRule;
import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.FunctionDefinition;
import org.sbml.libsbml.InitialAssignment;
import org.sbml.libsbml.KineticLaw;
import org.sbml.libsbml.ListOf;
import org.sbml.libsbml.ModifierSpeciesReference;
import org.sbml.libsbml.Parameter;
import org.sbml.libsbml.RateRule;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.Rule;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.Species;
import org.sbml.libsbml.SpeciesReference;
import org.sbml.libsbml.Unit;
import org.sbml.libsbml.UnitDefinition;
import org.sbml.libsbml.XMLAttributes;
import org.sbml.libsbml.XMLNode;
import org.sbml.libsbml.libsbml;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SBMLUtils.SBMLUnitParameter;
import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;

import org.vcell.util.document.BioModelChildSummary;

import sun.rmi.runtime.GetThreadPoolAction;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.GeneralLumpedKinetics;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCMODL;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.LambdaFunction;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLTags;

public class SBMLImporter {

	private static int level = 2;
	private static int version = 3;
	
	private String sbmlFileName = null;
	private org.sbml.libsbml.Model sbmlModel = null;
	private cbit.vcell.mapping.SimulationContext simContext = null;
	private LambdaFunction[] lambdaFunctions = null;
	private java.util.HashMap<String, Expression> assignmentRulesHash = new java.util.HashMap<String, Expression>();
	private TreeMap<String, VCUnitDefinition> vcUnitsHash = new TreeMap<String, VCUnitDefinition>();
	private java.util.Hashtable<String, SBVCConcentrationUnits> speciesUnitsHash = new java.util.Hashtable<String, SBVCConcentrationUnits>();

	private cbit.util.xml.VCLogger logger = null;
	 
	private static String RATE_NAME = XMLTags.ReactionRateTag;
	private static String SPECIES_NAME = XMLTags.SpeciesTag;
	private static String REACTION = XMLTags.ReactionTag;
	
	// SBMLAnnotationUtil to get the SBML-related annotations, notes, free-text annotations from a Biomodel VCMetaData
	private SBMLAnnotationUtil sbmlAnnotationUtil = null;


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
		ResourceUtil.loadlibSbmlLibray();
	}

	public SBMLImporter(String argSbmlFileName, cbit.util.xml.VCLogger argVCLogger) {
		super();
		sbmlFileName = argSbmlFileName;
		this.logger = argVCLogger;
	}


protected void addCompartments(VCMetaData metaData) {
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
			
			sbmlAnnotationUtil.readAnnotation(structVector.get(i), compartment);
			sbmlAnnotationUtil.readNotes(structVector.get(i), compartment);
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
							Compartment newCompartment = sbmlModel.createCompartment();
							newCompartment.setId(newMembrane.getName());
							newCompartment.setSpatialDimensions(2);
							// deal with unit conversion, since default unit for membrane (area) in SBML is m2 and in VCell is always um2.
							newCompartment.setSize(membSize);
							// Define um2 - AREA; add it to model
							UnitDefinition unitDefn = new UnitDefinition(sbmlModel.getLevel(), sbmlModel.getVersion());
							unitDefn.setId(TokenMangler.mangleToSName(VCUnitDefinition.UNIT_um2.getSymbol()));
							Unit um2_unit = new Unit(sbmlModel.getLevel(), sbmlModel.getVersion());
							int kind = libsbml.UnitKind_forName("metre");
							um2_unit.setKind(kind);
							um2_unit.setExponent(2);
							um2_unit.setScale(-6);
							unitDefn.addUnit(um2_unit);
							// Also add it to vcUnitsHash, to be able to retreive it later
							String unitName = unitDefn.getId();
							if (vcUnitsHash.get(unitName) == null) {
								sbmlModel.addUnitDefinition(unitDefn);
								VCUnitDefinition vcUnitDef = org.vcell.sbml.vcell.SBMLUnitTranslator.getVCUnitDefinition(unitDefn);
								vcUnitsHash.put(unitName, vcUnitDef);
							}
							newCompartment.setUnits(unitName);
							newCompartment.setOutside(newMembrane.getOutsideFeature().getName());
							compartment.setOutside(newCompartment.getId());
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
	if (sbmlModel == null) {
		throw new RuntimeException("SBML model is NULL");
	}
	ListOf listofInitialAssgns = sbmlModel.getListOfInitialAssignments();
	if (listofInitialAssgns == null) {
		System.out.println("No Initial Assignments specified");
		return;
	}
	for (int i = 0; i < sbmlModel.getNumInitialAssignments(); i++){
		try {
			InitialAssignment initAssgn = (InitialAssignment)listofInitialAssgns.get(i);
			String initAssgnSymbol = initAssgn.getSymbol();
			// if initial assignment is for a compartment, VCell doesn't support compartmentSize expressions, warn and bail out.
			if (sbmlModel.getCompartment(initAssgnSymbol) != null) {
				logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.COMPARTMENT_ERROR, "compartment '"+initAssgnSymbol+"' size has an initial assignment, cannot handle it at this time.");
			}
			Expression initAssignMathExpr = getExpressionFromFormula(initAssgn.getMath());
			// Check if init assgn expr for a species is in terms of x,y,z or other species. Not allowed for species.
			if (sbmlModel.getSpecies(initAssgnSymbol) != null) {
				if (initAssignMathExpr.hasSymbol(ReservedSymbol.X.getName()) || 
					initAssignMathExpr.hasSymbol(ReservedSymbol.Y.getName()) ||
					initAssignMathExpr.hasSymbol(ReservedSymbol.Z.getName()) ) {
					logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.SPECIES_ERROR, "species '"+initAssgnSymbol+"' initial assignment expression cannot contain 'x', 'y', 'z'.");
				}
			}
			// set the init assgn expr on VCell species init condn or global parameter expression
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(simContext.getModel().getSpeciesContext(initAssgnSymbol));
			ModelParameter mp = simContext.getModel().getModelParameter(initAssgnSymbol);
			if (scs != null) {
				scs.getInitialConditionParameter().setExpression(initAssignMathExpr);
			} else if (mp != null) {
				mp.setExpression(initAssignMathExpr);
			} else {
				logger.sendMessage(VCLogger.MEDIUM_PRIORITY, VCLogger.UNSUPPORED_ELEMENTS_OR_ATTS, "Symbol '"+initAssgnSymbol+"' not a species or global parameter in VCell; initial assignment ignored..");
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error reading InitialAssignment : " + e.getMessage()); 
		}
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

/**
 * addParameters : 
 * 		Adds global parameters from SBML model to VCell model. 
 * 		If expression for global parameter contains species, creates a conc_factor parameter (conversion from SBML - VCell conc units)
 * 		and adds this factor to VC global params list, and replaces occurances of 'sp' with 'sp*concFactor' in original param expression.
 * 
 * @throws PropertyVetoException
 */

protected void addParameters() throws PropertyVetoException {
	ListOf listofGlobalParams = sbmlModel.getListOfParameters();
	if (listofGlobalParams == null) {
		System.out.println("No Global Parameters");
		return;
	}
	Model vcModel = simContext.getModel();
	SpeciesContext[] vcSpContexts = vcModel.getSpeciesContexts();
	
	// create a hash of reserved symbols so that if there is any reserved symbol occurring as a global parameter in the SBML model,
	// the hash can be used to check for reserved symbols, so that it will not be added as a global parameter in VCell, 
	// since reserved symbols cannot be used as other variables (species, structureSize, parameters, reactions, etc.).
	HashSet<String> reservedSymbolHash = new HashSet<String>();
	reservedSymbolHash.add(ReservedSymbol.FARADAY_CONSTANT.getName());
	reservedSymbolHash.add(ReservedSymbol.FARADAY_CONSTANT_NMOLE.getName());
	reservedSymbolHash.add(ReservedSymbol.GAS_CONSTANT.getName());
	reservedSymbolHash.add(ReservedSymbol.KMILLIVOLTS.getName());
	reservedSymbolHash.add(ReservedSymbol.KMOLE.getName());
	reservedSymbolHash.add(ReservedSymbol.N_PMOLE.getName());
	reservedSymbolHash.add(ReservedSymbol.TEMPERATURE.getName());
	reservedSymbolHash.add(ReservedSymbol.K_GHK.getName());
	reservedSymbolHash.add(ReservedSymbol.TIME.getName());

	Vector<String> spConcFactorInModelParamsList = new Vector<String>();
	for (int i = 0; i < sbmlModel.getNumParameters(); i++){
		Parameter sbmlGlobalParam = (Parameter)listofGlobalParams.get(i);
		String paramName = sbmlGlobalParam.getId();
		// Check if param is defined by a rule. If so, that value overrides the value existing in the param element.
		Expression valueExpr = getValueFromRule(paramName);
		if (valueExpr == null) {
			if (sbmlGlobalParam.isSetValue()) {
				double value = sbmlGlobalParam.getValue();
				valueExpr = new Expression(value);
			}
		}
		
		// ************* TIME CONV_FACTOR if 'time' is present in global parameter expression
		// If time 't' is present in the global expression, it is in VC units (secs), convert it back to SBML units
		// hence, we take the inverse of the time factor (getSBMLTimeUnitsFactor() converts from SBML to VC units)
		String t = ReservedSymbol.TIME.getName();
		double timeFactorVal = 1.0/getSBMLTimeUnitsFactor(); 
		if ((timeFactorVal != 1) && (valueExpr.hasSymbol(t))) {
			String TIME_CONVFACTOR = t + "_ConvFactor";
			if (!valueExpr.hasSymbol(TIME_CONVFACTOR)) {
				// If no matching param for time conversion factor was found in sbml model, 
				// add TIME_CONVFACTOR as a global param in VCell before setting this global.
				ModelParameter timeConvParam = vcModel.getModelParameter(TIME_CONVFACTOR);
				if (timeConvParam == null) {
					timeConvParam = vcModel.new ModelParameter(TIME_CONVFACTOR, new Expression(timeFactorVal), Model.ROLE_UserDefined, VCUnitDefinition.UNIT_DIMENSIONLESS);
					String annotation = "Conversion from SBML time units to VC time units";
					timeConvParam.setModelParameterAnnotation(annotation);
					vcModel.addModelParameter(timeConvParam);	
				}
				// now replace 't' with 't*TIME_CONVFACTOR' in the parameter expression.
				try {
					valueExpr.substituteInPlace(new Expression(t), new Expression(t +"*" + TIME_CONVFACTOR));
				} catch (ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException(e.getMessage());
				}
			}
		}
		
		// ************** SPECIES CONC_FACTOR if species are present in global parameter expression ******************
		// if global parameter is an expression with model species, we need a conversion factor for the species units (SBML - VC units),
		// similar to the conversion that is done in reactions.
		if (valueExpr.getSymbols() != null) {
			for (int j = 0; j < vcSpContexts.length; j++) {
				// Check if valueExpr has the species, if so, add the species conc factor, else, do nothing.
				if (valueExpr.hasSymbol(vcSpContexts[j].getName())) {
			    	Species sp = sbmlModel.getSpecies(vcSpContexts[j].getName());
					String CONCFACTOR_PARAMETER = sp.getId() + "_ConcFactor";
					if (!spConcFactorInModelParamsList.contains(vcSpContexts[j].getName())) {
						SBVCConcentrationUnits sbvcSubstUnits = speciesUnitsHash.get(sp.getId());
						VCUnitDefinition vcUnit = sbvcSubstUnits.getVCConcentrationUnits();
						VCUnitDefinition sbUnit = sbvcSubstUnits.getSBConcentrationUnits();
										
						// the expr from SBML is in terms of SBML units; VC interprets concs in uM, but we have to translate them back to SBML units 
						//   within the expr; we convert concs into SBML (using 'sp_conc_factor'), so that the SBML expression is consistent;  
						try {
							SBMLUnitParameter concScaleFactor = SBMLUtils.getConcUnitFactor("spConcFactor", vcUnit, sbUnit);
							if ((concScaleFactor.getExpression().evaluateConstant() == 1.0 && concScaleFactor.getUnitDefinition().compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS)) ) {
								// if VC unit IS compatible with SBML unit and factor is 1 and unit conversion is 1
								// No conversion is required, and we don't need to include a concentration scale factor for the species.
							} else {
								// Substitute any occurrence of speciesName in global param expression with 'speciesName*concScaleFactor'
								// check if CONC_FACTOR is already defined in VCell model.
								ModelParameter concParam = vcModel.getModelParameter(CONCFACTOR_PARAMETER);
								if (concParam != null) {
									// if it already exists in list of global params, see if its expression/val matches the current param.
									Expression concParamValueExpr = concParam.getExpression();
									// If CONCFACTOR_PARAM exists in VC globals, check its expression with concScaleFactor. 
									// If they are not equal, multiply CONCFACTOR_PARAM expression with concScaleFactor expr,
									// since the new concFactor is in addition to existing CONCFACTOR value; no need to add another CONCFACTOR param.
									if (concParamValueExpr != null && concScaleFactor.getExpression() != null) {
										concParamValueExpr = Expression.mult(concParamValueExpr, concScaleFactor.getExpression());
										concParam.setExpression(concParamValueExpr);
										// if the expressions are being multiplied, the units should be too, as long as both are not TBD.
										if ((concParam.getUnitDefinition() != VCUnitDefinition.UNIT_TBD) && (concScaleFactor.getUnitDefinition() != VCUnitDefinition.UNIT_TBD)) {
											concParam.setUnitDefinition(concParam.getUnitDefinition().multiplyBy(concScaleFactor.getUnitDefinition()));
										}
									} 
									// ?? no need to do anything if the 2 expressions are the same ??
								} else {
									// If no matching param for concentration factor was found in sbml model, 
									// add CONCFACTOR_PARAM as a global param in VCell before setting this global.
									ModelParameter concScaleParam = vcModel.new ModelParameter(CONCFACTOR_PARAMETER, concScaleFactor.getExpression().flatten(), Model.ROLE_UserDefined, concScaleFactor.getUnitDefinition());
									String annotation = "Conversion from VC concentration units to SBML concentration units";
									concScaleParam.setModelParameterAnnotation(annotation);
									vcModel.addModelParameter(concScaleParam);	
								}
							}
						} catch (ExpressionException e) {
							e.printStackTrace(System.out);
							throw new RuntimeException(e.getMessage());
						}	// end try - catch
						// add this speciesContext name to vector 'spConcFactorInModelParamsList' so that its val isn't modified if used in another param expr.
						spConcFactorInModelParamsList.add(vcSpContexts[j].getName());
					} // end 'spConcFactorInModelParamsList' !contain spContext
					// any occurance of "sp" in param valeExpr should be replaced by "sp*CONCFACTOR_PARAM", if not already present.
					if ((vcModel.getModelParameter(CONCFACTOR_PARAMETER) != null) && !valueExpr.hasSymbol(CONCFACTOR_PARAMETER)) {
						try {
							valueExpr.substituteInPlace(new Expression(sp.getId()), new Expression(sp.getId()+"*"+CONCFACTOR_PARAMETER));
						} catch (ExpressionException e) {
							e.printStackTrace(System.out);
							throw new RuntimeException(e.getMessage());
						}
					}
				}	// end if (valExp contains spContext)
			}	// end for j - spContexts
		}	// end valExpr.Symbols != null
		
		// Finally, create and add model parameter to VC model if it already doesn't exist.
		if (vcModel.getModelParameter(paramName) == null) {
			VCUnitDefinition glParamUnitDefn = vcUnitsHash.get(sbmlGlobalParam.getUnits());
			// if units for param were not defined, don't let it be null; set it to TBD or check if it was dimensionless.
			if (glParamUnitDefn == null) {
				glParamUnitDefn = VCUnitDefinition.UNIT_TBD;
			}
			if (sbmlGlobalParam.getUnits().equals("dimensionless")) {
				glParamUnitDefn = VCUnitDefinition.UNIT_DIMENSIONLESS;
			}
			// Also check if the SBML global param is a reserved symbol in VCell : cannot add reserved symbol to model params.
			if (!reservedSymbolHash.contains(paramName)) {
				ModelParameter vcGlobalParam = vcModel.new ModelParameter(paramName, valueExpr, Model.ROLE_UserDefined, glParamUnitDefn);
				if (paramName.length() > 64) {
					// record global parameter name in annotation if it is longer than 64 characeters
					vcGlobalParam.setDescription("Parameter Name : " + paramName);
				}
				vcModel.addModelParameter(vcGlobalParam);
			}
		}
	}	// end for - sbmlModel.parameters
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
protected void addReactions(VCMetaData metaData) {
	if (sbmlModel == null) {
		throw new RuntimeException("SBML model is NULL");
	}
	ListOf listofReactions = sbmlModel.getListOfReactions();
	if (listofReactions == null) {
		System.out.println("No Reactions");
		return;
	}
	ReactionStep[] vcReactions = new ReactionStep[(int)sbmlModel.getNumReactions()];
	Model model = simContext.getModel();
	SpeciesContext[] vcSpeciesContexts = model.getSpeciesContexts();
	try {
		for (int i = 0; i < sbmlModel.getNumReactions(); i++) {
			org.sbml.libsbml.Reaction sbmlRxn = (org.sbml.libsbml.Reaction)listofReactions.get(i);
			String rxnName = sbmlRxn.getId();
			// Check of reaction annotation is present; if so, does it have an embedded element (flux or simpleRxn).
			// Create a fluxReaction or simpleReaction accordingly.
			Element sbmlImportRelatedElement = sbmlAnnotationUtil.readVCellSpecificAnnotation(sbmlRxn);
			Structure reactionStructure = getReactionStructure(sbmlRxn, vcSpeciesContexts, sbmlImportRelatedElement); 
			// XMLNode embeddedRxnElement = null;
			if (sbmlImportRelatedElement != null) {
				Element embeddedRxnElement = sbmlImportRelatedElement.getChild(REACTION);
				if (embeddedRxnElement != null) {
					if (embeddedRxnElement.getName().equals(XMLTags.FluxStepTag)) {
						// If embedded element is a flux reaction, set flux reaction's strucure, flux carrier, physicsOption from the element attibutes.
						String structName = embeddedRxnElement.getAttributeValue(XMLTags.StructureAttrTag);
						Structure struct = model.getStructure(structName);
						if (!(struct instanceof Membrane)) {
							throw new RuntimeException("Appears that the flux reaction is not occuring on a membrane.");
						}
						String fluxCarrierSpName = embeddedRxnElement.getAttributeValue(XMLTags.FluxCarrierAttrTag);
						cbit.vcell.model.Species fluxCarrierSp = model.getSpecies(fluxCarrierSpName);
						if (fluxCarrierSp == null) {
							logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.REACTION_ERROR, "Unknown FluxCarrier : " + fluxCarrierSpName + " for SBML reaction : " + rxnName);
						}
						vcReactions[i] = new FluxReaction((Membrane)struct, fluxCarrierSp, model, rxnName);
						// Set the fluxOption on the flux reaction based on whether it is molecular, molecular & electrical, electrical.
						String fluxOptionStr = embeddedRxnElement.getAttributeValue(XMLTags.FluxOptionAttrTag);
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
			} else {
				vcReactions[i] = new cbit.vcell.model.SimpleReaction(reactionStructure, rxnName);
			}
			
			// set annotations and notes on vcReactions[i]
			sbmlAnnotationUtil.readAnnotation(vcReactions[i], sbmlRxn);
			sbmlAnnotationUtil.readNotes(vcReactions[i], sbmlRxn);
			// record reaction name in annotation if it is greater than 64 characters. Choosing 64, since that is (as of 12/2/08) 
			// the limit on the reactionName length.
			if (rxnName.length() > 64) {
				StringBuffer oldRxnAnnotation = new StringBuffer(metaData.getFreeTextAnnotation(vcReactions[i]));
				oldRxnAnnotation.append("\n\n" + rxnName);
				metaData.setFreeTextAnnotation(vcReactions[i], oldRxnAnnotation.toString());
			}
			model.addReactionStep(vcReactions[i]);

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
				for (int k = 0; k < vcSpeciesContexts.length; k++){
					if (vcRateExpression.hasSymbol(vcSpeciesContexts[k].getName())) {
						if ((sbmlRxn.getReactant(vcSpeciesContexts[k].getName()) == null) && 
							(sbmlRxn.getProduct(vcSpeciesContexts[k].getName()) == null) && 
							(sbmlRxn.getModifier(vcSpeciesContexts[k].getName()) == null)) {
							// This means that the speciesContext is not a reactant, product or modifier : it has to be added to the VC Rxn as a catalyst
							vcReactions[i].addCatalyst(vcSpeciesContexts[k]);
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
	
				// set kinetics on vcReaction
				vcReactions[i].setKinetics(kinetics);
	
				// If the name of the rate parameter has been changed by user, or matches with global/local param, 
				// it has to be changed.
				resolveRxnParameterNameConflicts(sbmlRxn, kinetics, sbmlImportRelatedElement);
				
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
						if (reactionStructure instanceof Membrane && vcReactions[i] instanceof SimpleReaction) {
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
							if (comp1.getId().equals(((Parameter)listofLocalParams.get((long)ll)).getId())) {
								bCompFoundInLocalParams = true;
							}
						}
						if (vcRateExpression.hasSymbol(comp1.getId()) && !bCompFoundInLocalParams) {
							/* this compartmentSize is being used in the rate expr, but since compSizes are now proxy parameters, their actual sizes 
							   need not be added as a kinetic parameter. Compare the units of the compSize in SBML (usually L) with VCell (um3)
							   The expr from SBML is in terms of SBML units, and it is now in VC units, but we have to translate them back to SBML units 
							   within the expr; then we translate the SBML rate units into VCell units.
							   we convert sizes into SBML (using 'comp_sizefactor'); so that the SBML expression is consistent; 
							   then we translate the SBML expression into VCell units (using 'sbmlRateFactor') - happens later
							*/
							// If there is a conversion factor, we need to use it ('comp*comp_Sizefactor' instead of 'comp').This conversion factor
							// needs to be added; but add it as a global parameter if it doesn't already exist.
							if (!comp1.getId().equals(compartment.getId())) {
								int spatialDim = (int)comp1.getSpatialDimensions();
								String spatialDimBuiltInName = getSpatialDimensionBuiltInName(spatialDim);
								VCUnitDefinition sbmlSizeUnit = getSBMLUnit(comp1.getUnits(), spatialDimBuiltInName);
								// Need to convert the size unit (vol or area) into VC compatible units (um3, um2) if it is not already in VC compatible units
								double factor = 1.0;
								VCUnitDefinition vcSizeUnit = null;
								if (spatialDim == 3) {
									vcSizeUnit = VCUnitDefinition.UNIT_um3;
								} else if (spatialDim == 2) {
									vcSizeUnit = VCUnitDefinition.UNIT_um2;
								}
								factor  = vcSizeUnit.convertTo(factor, sbmlSizeUnit);
								if (factor != 1.0) {
									String COMPSIZE_PARAMETER = comp1.getId() + "_SizeUnitFactor";
									Expression adjSizeExpr = new Expression(factor);
									VCUnitDefinition adjSizeUnit = sbmlSizeUnit.divideBy(vcSizeUnit);
									// if a global with the name doesn't exists, add it as a global to VCell.
									ModelParameter comp1Param = model.getModelParameter(COMPSIZE_PARAMETER); 
									if (comp1Param == null) {
										 comp1Param = model.new ModelParameter(COMPSIZE_PARAMETER, adjSizeExpr, Model.ROLE_UserDefined, adjSizeUnit);
										 String annotation = "Conversion from VC size units to SBML size units";
										 comp1Param.setModelParameterAnnotation(annotation);
										 model.addModelParameter(comp1Param);
									}
									// adjust reaction rate expr with comp*comp_SizeUnitParam
									Expression newRateExpr = kinetics.getAuthoritativeParameter().getExpression();
									newRateExpr.substituteInPlace(new Expression(comp1.getId()), new Expression(comp1.getId()+"*"+COMPSIZE_PARAMETER));
									kinetics.setParameterValue(kinetics.getAuthoritativeParameter(), newRateExpr.flatten());
								}
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
				// We need to convert them from SBML unit to VCell unit. If the spatial dimension of the compartment is 0, we do not handle it
				// at this time, throw an exception.
				//
				vcRateExpression = kinetics.getAuthoritativeParameter().getExpression();
				for (int k = 0; k < vcSpeciesContexts.length; k++){
					if (vcRateExpression.hasSymbol(vcSpeciesContexts[k].getName())) {
						org.sbml.libsbml.Species species = sbmlModel.getSpecies(vcSpeciesContexts[k].getName());
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
						
						// Get the SBML and VC units for the species
						SBVCConcentrationUnits sbvcSubstUnits = speciesUnitsHash.get(species.getId());
						VCUnitDefinition VC_conc_unit = sbvcSubstUnits.getVCConcentrationUnits();
						VCUnitDefinition SBML_conc_unit = sbvcSubstUnits.getSBConcentrationUnits();

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
								// the concentration factor for this species already exists; multiply species_ConcFactor with the 
								// new concentration factor value. For eg., if the concFactor 
								// for species 's1' has a value 'V1' and si_ConcFactor exists in local params, multiply
								// 's1_ConcFactor*V1'
								Expression newRateExpr = kinetics.getAuthoritativeParameter().getExpression();
								Expression modifiedSpeciesExpression = Expression.mult(new Expression(species.getId()), concScaleFactor.getExpression());
								newRateExpr.substituteInPlace(new Expression(species.getId()), modifiedSpeciesExpression);
								kinetics.setParameterValue(kinetics.getAuthoritativeParameter(), newRateExpr.flatten());
							} else {
								// If CONCFACTOR_PARAM is not already in the kinetic expression, include it.
								Expression newRateExpr = kinetics.getAuthoritativeParameter().getExpression();
								if (!newRateExpr.hasSymbol(CONCFACTOR_PARAMETER)) {
									newRateExpr.substituteInPlace(new Expression(species.getId()), new Expression(species.getId()+"*"+CONCFACTOR_PARAMETER));
									kinetics.setParameterValue(kinetics.getAuthoritativeParameter(), newRateExpr.flatten());
								}
								ModelParameter mp = model.getModelParameter(CONCFACTOR_PARAMETER);
								if (mp == null) {
									// no global CONCFACTOR found (and there was no local), add it is as local.
									kinetics.setParameterValue(kinetics.getKineticsParameter(CONCFACTOR_PARAMETER), concScaleFactor.getExpression().flatten());
									kinetics.getKineticsParameter(CONCFACTOR_PARAMETER).setUnitDefinition(concScaleFactor.getUnitDefinition());
								} else if (mp != null) {
									// Check if this parameter is in the global param list of sbmlModel. If there is a global parameter,
									// compute glParam*concScaleFactor value. Check its value with model parameter of same name in VC model, if it exists.
									// If they are not equal, there is a problem. If they are equal, don't need to add CONCFACTOR as local param (the vc global
									// value will be used).
									Expression tempConcFactorExpr = concScaleFactor.getExpression();
									Parameter glParam = sbmlModel.getParameter(CONCFACTOR_PARAMETER);
									if (glParam != null) {
										Expression paramValExpr = getValueFromRule(glParam.getId());
										if (paramValExpr == null) {
											if (glParam.isSetValue()) {
												double value = glParam.getValue();
												paramValExpr = new Expression(value);
											}
										}
										tempConcFactorExpr = Expression.mult(tempConcFactorExpr, paramValExpr);
									} 
									if (!mp.getExpression().compareEqual(tempConcFactorExpr)) {
										// if there is a CONCFACTOR global with different value from the present local CONCFACTOR,
										// throw exception, since the concentration factors for the species should be the same.
										logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "Species '" + species.getId() + "' concentration factor values in global scope and local scope cannot be different.");
									}
								}	// if - else (mp == null)
							}	// if - else (localParam != null)
						}	// end - if concScaleFactor
					}	// end - vcSpecContext found in vcRateExpression
				}	// end for - k (vcSpeciesContext)
				
				
				//  ************ <<<< Scale units of TIME if present in kinetic expressions (to VC time units : secs) ************
				
				// If kinetic rate expression has time 't' in it, and if SBML time unit is not in seconds, we need to multiply
				// the 't' in the kinetic rate expression with the conversion factor (t_ConvFactor)
				// 't' is in VC units (secs), convert it back to SBML units; hence, we take the inverse of 
				// the time factor (getSBMLTimeUnitsFactor() converts from SBML to VC units)

				double timeFactor = 1.0/getSBMLTimeUnitsFactor();
				vcRateExpression = kinetics.getAuthoritativeParameter().getExpression();
				String t = ReservedSymbol.TIME.getName();
				if ((timeFactor != 1.0) && (vcRateExpression.hasSymbol(t))) {
					String TIME_CONVFACTOR = t + "_ConvFactor";
					// If TIME_CONVFACTOR is not already in the kinetic expression, include it.
					if (!vcRateExpression.hasSymbol(TIME_CONVFACTOR)) {
						vcRateExpression.substituteInPlace(new Expression(t), new Expression(t+"*"+TIME_CONVFACTOR));
						kinetics.setParameterValue(kinetics.getAuthoritativeParameter(), vcRateExpression);
					}
					// Check if TIME_CONVFACTOR is a global parameter, if not, add it as a local parameter. 
					ModelParameter mp = model.getModelParameter(TIME_CONVFACTOR);
					if (mp == null) {
						// no global TIME_CONVFACTOR found (and there was no local), add it is as local.
						kinetics.setParameterValue(kinetics.getKineticsParameter(TIME_CONVFACTOR), new Expression(timeFactor));
						kinetics.getKineticsParameter(TIME_CONVFACTOR).setUnitDefinition(VCUnitDefinition.UNIT_DIMENSIONLESS);
					} 
				}	// if - (timeFactor != 1)
	

				
				// If there are any global parameters used in the kinetics, and if they have species,
				// check if the species are already reactionParticipants in the reaction. If not, add them as catalysts.
				KineticsProxyParameter[] kpps = kinetics.getProxyParameters();
				for (int j = 0; j < kpps.length; j++) {
					if (kpps[j].getTarget() instanceof ModelParameter) {
						ModelParameter mp = (ModelParameter)kpps[j].getTarget();
					    HashSet<String> refSpeciesNameHash = new HashSet<String>(); 
					    getReferencedSpeciesInExpr(mp.getExpression(), refSpeciesNameHash);
					    java.util.Iterator<String> refSpIterator = refSpeciesNameHash.iterator();
						while (refSpIterator.hasNext()) {
					    	String spName = refSpIterator.next();
					    	Species sp = sbmlModel.getSpecies(spName);
							if (vcReactions[i].getReactionParticipantFromSymbol(sp.getId()) == null) {
								// This means that the speciesContext is not a reactant, product or modifier : it has to be added as a catalyst
								vcReactions[i].addCatalyst(model.getSpeciesContext(sp.getId()));
							}
						}
	
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

protected void addSpecies(VCMetaData metaData) {
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

			// create a species with speciesName as commonName. If it is different in the annotation, can change it later
			cbit.vcell.model.Species vcSpecies = null;

			Element sbmlImportRelatedElement = sbmlAnnotationUtil.readVCellSpecificAnnotation(sbmlSpecies);
			if (sbmlImportRelatedElement != null) {
				Element embeddedElement = getEmbeddedElementInAnnotation(sbmlImportRelatedElement, SPECIES_NAME);
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

			// Set annotations and notes from SBML to VCMetadata
			sbmlAnnotationUtil.readAnnotation(vcSpecies, sbmlSpecies);
			sbmlAnnotationUtil.readNotes(vcSpecies, sbmlSpecies);
			
			// Get matching compartment name (of sbmlSpecies[i]) from feature list
			String compartmentId = sbmlSpecies.getCompartment();
			Structure spStructure = simContext.getModel().getStructure(compartmentId);
			simContext.getModel().addSpeciesContext(vcSpecies, spStructure);
			vcSpeciesContexts[i] = simContext.getModel().getSpeciesContext(vcSpecies, spStructure);
			vcSpeciesContexts[i].setHasOverride(true);
			vcSpeciesContexts[i].setName(speciesName);

			// Adjust units of species, convert to VC units.
			// Units in SBML, compute this using some of the attributes of sbmlSpecies
			int dimension = (int)sbmlModel.getCompartment(sbmlSpecies.getCompartment()).getSpatialDimensions();
			if (dimension == 0 || dimension == 1){
				logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, dimension+" dimensional compartment "+compartmentId+" not supported");
			}
			String spatialDimBuiltinName = getSpatialDimensionBuiltInName(dimension);

			String spatialSizeUnitStr = sbmlSpecies.getSpatialSizeUnits();
			if (spatialSizeUnitStr == null) {
				spatialSizeUnitStr = sbmlModel.getCompartment(compartmentId).getUnits();
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
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error adding species context; "+ e.getMessage());
	}
}

/**
 * setSpeciesInitialConditions : called after speciesContexts and global parameters have been set.  Checks for init conditions set on species
 * in the Sbml model, and if it is set using an assignment rule, obtain the corresponding expression. Obtain the sbml -> vc unit conversion 
 * factor for species concentrations to adjust the species initial condition units/factor.
 *  
 */
private void setSpeciesInitialConditions() {
	try {
		// fill in SpeciesContextSpec for each speciesContext
		Model vcModel = simContext.getModel();
		SpeciesContext[] vcSpeciesContexts = vcModel.getSpeciesContexts();
		for (int i = 0; i < vcSpeciesContexts.length; i++) {
			org.sbml.libsbml.Species sbmlSpecies = (org.sbml.libsbml.Species)sbmlModel.getSpecies(vcSpeciesContexts[i].getName());
			// Sometimes, the species name can be null or a blank string; in that case, use species id as the name.
			String speciesName = sbmlSpecies.getId();
			Compartment compartment = (Compartment)sbmlModel.getCompartment(sbmlSpecies.getCompartment());
	
			// get the conversion units from speciesUnitsHash
			SBVCConcentrationUnits sbvcSubstUnits = speciesUnitsHash.get(speciesName);
			VCUnitDefinition vcUnit = sbvcSubstUnits.getVCConcentrationUnits();
			VCUnitDefinition sbUnit = sbvcSubstUnits.getSBConcentrationUnits();
	
			Expression initExpr = null;
			if (sbmlSpecies.isSetInitialConcentration()) { 		// If initial Concentration is set
				Expression initConcentration = new Expression(sbmlSpecies.getInitialConcentration());
				SBMLUnitParameter unitFactor = SBMLUtils.getConcUnitFactor("spConcUnitFactor", sbUnit, vcUnit);
				initConcentration = Expression.mult(initConcentration, unitFactor.getExpression());
				// check if initConc is set by a (assignment) rule. That takes precedence over initConc value set on species.
				initExpr = getValueFromRule(speciesName);
				if (initExpr == null) {
					initExpr = new Expression(initConcentration);
				} else {
					initExpr = Expression.mult(initExpr, unitFactor.getExpression());
				}
			} else if (sbmlSpecies.isSetInitialAmount()) {		// If initial amount is set
				double initAmount = sbmlSpecies.getInitialAmount();
				// initConcentration := initAmount / compartmentSize.
				// If compartmentSize is set and non-zero, compute initConcentration. Else, throw exception.
				if (compartment.isSetSize()) {
					double compartmentSize = compartment.getSize();
					Expression initConcentration = new Expression(0.0);
					SBMLUnitParameter factor = null;
					if (compartmentSize != 0.0) {
						initConcentration = new Expression(initAmount / compartmentSize);
						factor = SBMLUtils.getConcUnitFactor("spConcUnitParam", sbUnit, vcUnit);
						initConcentration = Expression.mult(initConcentration, factor.getExpression());
					} else {
						logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.UNIT_ERROR, "compartment "+compartment.getId()+" has zero size, unable to determine initial concentration for species "+speciesName);
					}
					// check if initConc is set by a (assignment) rule. That takes precedence over initConc/initAmt value set on species.
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
				// initConc/initAmt not set; check if species has a (assignment) rule.
				initExpr = getValueFromRule(speciesName);
				if (initExpr == null) {
					// no assignment rule (and there was no initConc or initAmt); if it doesn't have initialAssignment, throw warning and set it to 0.0
					if (sbmlModel.getInitialAssignment(speciesName) == null) {
						logger.sendMessage(VCLogger.MEDIUM_PRIORITY, VCLogger.UNIT_ERROR, "no initial condition for species "+speciesName+", assuming 0.0");
					}
					initExpr = new Expression(0.0);
				}
	
				// Units for initial conc or amt if it is specified by an assignment rule
				SBMLUnitParameter factor = null;
				Expression adjustedFactorExpr = null;
				int dimension = (int)sbmlModel.getCompartment(sbmlSpecies.getCompartment()).getSpatialDimensions();
				if (dimension != 0 && !sbmlSpecies.getHasOnlySubstanceUnits()) {
					// Init conc : 'hasOnlySubstanceUnits' should be false and spatial dimension of compartment should be non-zero.
					factor = SBMLUtils.getConcUnitFactor("spConcFactor", sbUnit, vcUnit);
					adjustedFactorExpr = factor.getExpression();
				} else if (dimension == 0 || sbmlSpecies.getHasOnlySubstanceUnits()) {
					// Init Amount : 'hasOnlySubstanceUnits' should be true or spatial dimension of compartment should zero.
					if (compartment.isSetSize()) {
						double compartmentSize = compartment.getSize();
						if (compartmentSize != 0.0) {
							// initConcentration := initAmount / compartmentSize
							factor = SBMLUtils.getConcUnitFactor("spConcFactor", sbUnit, vcUnit);
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

			// if initExpr is an expression with model species, we need a conversion factor for the species units (SBML - VC units),
			// similar to the conversion that is done in reactions.
			String[] symbols = initExpr.getSymbols();
			for (int j = 0; symbols!= null && j < symbols.length; j++) {
				// check if symbol[i] is a vcSpeciesContext. If so, add a spConcFactor if not already there, else do nothing.
				SpeciesContext sc = vcModel.getSpeciesContext(symbols[j]);
				if (sc != null && !sc.compareEqual(vcSpeciesContexts[i])) {
					// At this point, 'sc' is not null, so check if a conc-factor for 'sc' exists in modelParameter list. If not, add it. 
			    	Species sp = sbmlModel.getSpecies(sc.getName());
					SBVCConcentrationUnits sc_sbvcSubstUnits = speciesUnitsHash.get(sp.getId());
					VCUnitDefinition sc_VCUnit = sc_sbvcSubstUnits.getVCConcentrationUnits();
					VCUnitDefinition sc_SBUnit = sc_sbvcSubstUnits.getSBConcentrationUnits();
									
					// the expr from SBML is in terms of SBML units; VC interprets concs in uM, but we have to translate them back to SBML units 
					// within the expr; we convert concs into SBML (using 'sp_conc_factor'), so that the SBML expression is consistent;  
					try {
						SBMLUnitParameter concScaleFactor = SBMLUtils.getConcUnitFactor("spConcFactor", sc_VCUnit, sc_SBUnit);
						if ((concScaleFactor.getExpression().evaluateConstant() == 1.0 && concScaleFactor.getUnitDefinition().compareEqual(VCUnitDefinition.UNIT_DIMENSIONLESS)) ) {
							// if VC unit IS compatible with SBML unit and factor is 1 and unit conversion is 1
							// No conversion is required, and we don't need to include a concentration scale factor for the species.
							System.err.println("\nNo need for concentration factor!\n");
						} else {
							// Substitute any occurrence of speciesName in global param expression with 'speciesName*concScaleFactor'
							String CONCFACTOR_PARAMETER = sp.getId() + "_ConcFactor";
							// check if CONC_FACTOR is defined as a modelParameter in VCell.
							ModelParameter sc_concParam = vcModel.getModelParameter(CONCFACTOR_PARAMETER);
							if (sc_concParam == null) {
								// If CONC_FACTOR is not defined in VCell model as a modelParameter, add it as a global Parameter.
								ModelParameter concScaleParam = vcModel.new ModelParameter(CONCFACTOR_PARAMETER, concScaleFactor.getExpression().flatten(), Model.ROLE_UserDefined, concScaleFactor.getUnitDefinition());
								vcModel.addModelParameter(concScaleParam);	
							} else {
								// if CONC_FACTOR is already defined, compare its value with concScaleFactor expr
								Expression tempExpr = concScaleFactor.getExpression();
								Parameter glParam = sbmlModel.getParameter(CONCFACTOR_PARAMETER);
								if (glParam != null) {
									Expression paramValExpr = getValueFromRule(glParam.getId());
									if (paramValExpr == null) {
										if (glParam.isSetValue()) {
											double value = glParam.getValue();
											paramValExpr = new Expression(value);
										}
									}
									tempExpr = Expression.mult(tempExpr, paramValExpr);
								} 
								Expression sc_concParamExpr = sc_concParam.getExpression();
								if (sc_concParamExpr != null && tempExpr!= null && !(sc_concParamExpr.compareEqual(tempExpr))) {
									sc_concParam.setExpression(tempExpr);
								}
							}
							// any occurrence of "sp" in param valeExpr should be replaced by "sp*CONCFACTOR_PARAM", if not already present.
							if (!initExpr.hasSymbol(CONCFACTOR_PARAMETER)) {
								initExpr.substituteInPlace(new Expression(sp.getId()), new Expression(sp.getId()+"*"+CONCFACTOR_PARAMETER));
							}
						}
					} catch (ExpressionException e) {
						e.printStackTrace(System.out);
						throw new RuntimeException(e.getMessage());
					}	// end try - catch
				}	// end if (sc != null ...)
			}	// end for j - symbols

			// If any of the symbols in the expression for speciesConc is a rule, expand it.
			substituteGlobalParamRulesInPlace(initExpr, false);
	
			SpeciesContextSpec speciesContextSpec = simContext.getReactionContext().getSpeciesContextSpec(vcSpeciesContexts[i]);
			speciesContextSpec.getInitialConditionParameter().setExpression(initExpr);
			speciesContextSpec.setConstant(sbmlSpecies.getBoundaryCondition() || sbmlSpecies.getConstant());
		}
	} catch (Throwable e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error setting initial condition for species context; "+ e.getMessage()); 
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
		VCUnitDefinition vcUnitDef = org.vcell.sbml.vcell.SBMLUnitTranslator.getVCUnitDefinition(ud);
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
 * 		(to oldName_rxnName); since the global or local parameter value will override the rate equation/value. Also, when we import 
 * 		a VCell model that has been exported to SBML, if the user has changed the rate name in a reaction, it is stored in the 
 * 		reaction annotation. This has to be retrieved and set as reaction rate name.
 * 		
 * @param sbmlRxn
 * @param newKinetics
 * @throws ExpressionException
 */
private void resolveRxnParameterNameConflicts(Reaction sbmlRxn, Kinetics vcKinetics, Element sbmlImportElement) throws PropertyVetoException {
	// If the name of the rate parameter has been changed by user, it is stored in rxnAnnotation. 
	// Retrieve this to re-set rate param name.
	if (sbmlImportElement != null) {
		Element embeddedRxnElement = getEmbeddedElementInAnnotation(sbmlImportElement, RATE_NAME);
		String vcRateParamName = null;
		if (embeddedRxnElement != null) {
			if (embeddedRxnElement.getName().equals(XMLTags.RateTag)) {
				vcRateParamName = embeddedRxnElement.getAttributeValue(XMLTags.NameAttrTag);
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
			vcKinetics.getAuthoritativeParameter().setName(origRateParamName+"_"+org.vcell.util.TokenMangler.mangleToSName(sbmlRxn.getId()));
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
				vcKinetics.getAuthoritativeParameter().setName(origRateParamName+"_"+org.vcell.util.TokenMangler.mangleToSName(sbmlRxn.getId()));
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
	Model vcModel = simContext.getModel();
	for (int kk = 0; kk < exprSymbols.length; kk++) {
		ModelParameter mp = vcModel.getModelParameter(exprSymbols[kk]);
		if (mp != null) {
			Expression expr = mp.getExpression();
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
}

public BioModel getBioModel() {
	// Read SBML model into libSBML SBMLDocument and create an SBML model
	SBMLReader reader = new SBMLReader();
	SBMLDocument document = reader.readSBML(sbmlFileName);
	
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
	// if sbml 'model' didn't have either id or name set, use a default name, say 'newModel'
	if (modelName == null || modelName.trim().equals("")) {
		modelName = "newModel";
	} 
	cbit.vcell.biomodel.BioModel bioModel = new BioModel(null);
	if (bioModel != null) {
		sbmlAnnotationUtil = new SBMLAnnotationUtil(bioModel.getVCMetaData(), bioModel, SBMLUtils.SBML_NS_2);
	}
	cbit.vcell.model.Model vcModel = new cbit.vcell.model.Model(modelName);

	Geometry geometry = new Geometry(BioModelChildSummary.COMPARTMENTAL_GEO_STR, 0);
	try {
		simContext = new SimulationContext(vcModel, geometry);
		simContext.setName(simContext.getModel().getName()+"_"+simContext.getGeometry().getName());
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not create simulation context corresponding to the input SBML model");
	}
	translateSBMLModel(bioModel.getVCMetaData());

	try {
		// **** TEMPORARY BLOCK - to name the biomodel with proper name, rather than model id
		String biomodelName = sbmlModel.getName();
		// if name is not set, use id
		if ((biomodelName == null) || biomodelName.trim().equals("")) {
			biomodelName = sbmlModel.getId();
		}
		// if id is not set, use a default, say, 'newModel'
		if ((biomodelName == null) || biomodelName.trim().equals("")) {
			biomodelName = "newBioModel";
		}
		bioModel.setName(biomodelName);
		// **** end - TEMPORARY BLOCK
		
		// bioModel.setName(modelName);
		bioModel.setModel(simContext.getModel());
		bioModel.setSimulationContexts(new SimulationContext[] {simContext});			
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not create Biomodel");
	}
	
	sbmlAnnotationUtil.readAnnotation(bioModel, sbmlModel);
	sbmlAnnotationUtil.readNotes(bioModel, sbmlModel);

	bioModel.refreshDependencies();
	return bioModel;
}

/**
 *  getEmbeddedElementInRxnAnnotation :
 *  Takes the reaction annotation as an argument and returns the embedded element  (fluxstep or simple reaction), if present.
 */
private Element getEmbeddedElementInAnnotation(Element sbmlImportRelatedElement, String tag) {
	// Get the XML element corresponding to the annotation xmlString.
	String elementName = null;
	if (tag.equals(RATE_NAME)) {
		elementName = XMLTags.ReactionRateTag;
	} else if (tag.equals(SPECIES_NAME)) {
		elementName = XMLTags.SpeciesTag;
	} else if (tag.equals(REACTION)) {
		elementName = XMLTags.FluxStepTag;
		if (elementName == null) {
			elementName = XMLTags.SimpleReactionTag;
		}
	}
	// If there is an annotation element for the reaction or species, retrieve and return.
	if (sbmlImportRelatedElement != null) {
		for (int j = 0; j < sbmlImportRelatedElement.getChildren().size(); j++) {
			Element infoChild = sbmlImportRelatedElement.getChild(elementName);
			if (infoChild != null){
				return infoChild;
			}
		}
	}	
	return null;
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

/**
 * If SBML time units are not defined in seconds (default), we need to convert it to secs and use a conversion factor when using
 * time 't' in rate and parameter expressions. This method converts from SBML time units to VC time units (seconds)
 * @return
 */
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
private Structure getReactionStructure(org.sbml.libsbml.Reaction sbmlRxn, SpeciesContext[] speciesContexts, Element sbmlImportElement) throws Exception {
    Structure struct = null;

    // Check annotation for reaction - if we are importing an exported VCell model, it will contain annotation for reaction.
    // If annotation has structure name, return the corresponding structure.
    String structName = null;
    if (sbmlImportElement != null) {
        // Get the embedded element in the annotation str (fluxStep or simpleReaction), and the structure attribute from the element.
        Element embeddedElement = getEmbeddedElementInAnnotation(sbmlImportElement, REACTION);
        if (embeddedElement != null) {
            structName = embeddedElement.getAttributeValue(cbit.vcell.xml.XMLTags.StructureAttrTag);
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
				SbmlUnit = org.vcell.sbml.vcell.SBMLUnitTranslator.getDefaultSBMLUnit(builtInName);
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
					SbmlUnit = org.vcell.sbml.vcell.SBMLUnitTranslator.getDefaultSBMLUnit(builtInName);
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
			return new Expression(valueExpr);
		}
	}
	return null;
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
						if (assignRuleMathExpr.hasSymbol(ReservedSymbol.X.getName()) || 
							assignRuleMathExpr.hasSymbol(ReservedSymbol.Y.getName()) || 
							assignRuleMathExpr.hasSymbol(ReservedSymbol.Z.getName())) {
							logger.sendMessage(VCLogger.HIGH_PRIORITY, VCLogger.SPECIES_ERROR, "An assignment rule for species " + ruleSpecies.getId() + " contains reserved spatial variable(s) (x,y,z), this is not allowed for a non-spatial model in VCell");
						}
					}
				}
			} 
		}
	}
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
public void translateSBMLModel(VCMetaData metaData) {
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
	addCompartments(metaData);
	// Add species/speciesContexts
	addSpecies(metaData); 
	// Add Parameters
	try {
		addParameters();
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	// Set initial conditions on species
	setSpeciesInitialConditions();
	// Add InitialAssignments 
	addInitialAssignments();
	// Add constraints (not handled in VCell)
	addConstraints();
	// Add Reactions
	addReactions(metaData);
	// Add Events
	addEvents();
	// Check if names of species, structures, reactions, parameters are long (say, > 64), if so give warning.
	try {
		checkIdentifiersNameLength();
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}

private void checkIdentifiersNameLength() throws Exception {
	// Check compartment name lengths
	ListOf listofIds = sbmlModel.getListOfCompartments();
	boolean bLongCompartmentName = false;
	for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
		Compartment compartment = (Compartment)listofIds.get(i);
		String compartmentName = compartment.getId();
		if (compartmentName.length() > 64) {
			bLongCompartmentName = true;
		}
	}
	// Check species name lengths
	listofIds = sbmlModel.getListOfSpecies();
	boolean bLongSpeciesName = false;
	for (int i = 0; i < sbmlModel.getNumSpecies(); i++) {
		Species species = (Species)listofIds.get(i);
		String speciesName = species.getId();
		if (speciesName.length() > 64) {
			bLongSpeciesName = true;
		}
	}
	// Check parameter name lengths
	listofIds = sbmlModel.getListOfParameters();
	boolean bLongParameterName = false;
	for (int i = 0; i < sbmlModel.getNumParameters(); i++) {
		Parameter param = (Parameter)listofIds.get(i);
		String paramName = param.getId();
		if (paramName.length() > 64) {
			bLongParameterName = true;
		}
	}
	// Check reaction name lengths
	listofIds = sbmlModel.getListOfReactions();
	boolean bLongReactionName = false;
	for (int i = 0; i < sbmlModel.getNumReactions(); i++) {
		Reaction rxn = (Reaction)listofIds.get(i);
		String rxnName = rxn.getId();
		if (rxnName.length() > 64) {
			bLongReactionName = true;
		}
	}

	if (bLongCompartmentName || bLongSpeciesName || bLongParameterName || bLongReactionName) {
		String warningMsg = "WARNING: The imported model has one or more ";
		if (bLongCompartmentName) {
			warningMsg = warningMsg + "compartments, ";
		} 
		if (bLongSpeciesName) {
			warningMsg = warningMsg + "species, ";
		}
		if (bLongParameterName) {
			warningMsg = warningMsg + "global parameters, ";
		}
		if (bLongReactionName) {
			warningMsg = warningMsg + "reactions ";
		}
		warningMsg = warningMsg + "that have ids/names that are longer than 64 characters. \n\nUser is STRONGLY recommeded to shorten " +
						"the names to avoid problems with the length of expressions these names might be used in.";
		logger.sendMessage(VCLogger.MEDIUM_PRIORITY, VCLogger.UNSUPPORED_ELEMENTS_OR_ATTS, warningMsg);
	}
}
}