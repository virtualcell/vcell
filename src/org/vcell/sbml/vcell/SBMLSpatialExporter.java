package org.vcell.sbml.vcell;
import java.util.ArrayList;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sbml.libsbml.ASTNode;
import org.sbml.libsbml.AdjacentDomains;
import org.sbml.libsbml.AdvectionCoefficient;
import org.sbml.libsbml.AnalyticGeometry;
import org.sbml.libsbml.AnalyticVolume;
import org.sbml.libsbml.AssignmentRule;
import org.sbml.libsbml.BoundaryCondition;
import org.sbml.libsbml.BoundaryMax;
import org.sbml.libsbml.BoundaryMin;
import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.CompartmentMapping;
import org.sbml.libsbml.CoordinateComponent;
import org.sbml.libsbml.Delay;
import org.sbml.libsbml.DiffusionCoefficient;
import org.sbml.libsbml.Domain;
import org.sbml.libsbml.DomainType;
import org.sbml.libsbml.Event;
import org.sbml.libsbml.GeometryDefinition;
import org.sbml.libsbml.ImageData;
import org.sbml.libsbml.InitialAssignment;
import org.sbml.libsbml.InteriorPoint;
import org.sbml.libsbml.ModifierSpeciesReference;
import org.sbml.libsbml.OStringStream;
import org.sbml.libsbml.RequiredElementsPkgNamespaces;
import org.sbml.libsbml.RequiredElementsSBasePlugin;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLDocumentPlugin;
import org.sbml.libsbml.SBMLNamespaces;
import org.sbml.libsbml.SBMLWriter;
import org.sbml.libsbml.SBase;
import org.sbml.libsbml.SBasePlugin;
import org.sbml.libsbml.SampledField;
import org.sbml.libsbml.SampledFieldGeometry;
import org.sbml.libsbml.SampledVolume;
import org.sbml.libsbml.SpatialCompartmentPlugin;
import org.sbml.libsbml.SpatialModelPlugin;
import org.sbml.libsbml.SpatialParameterPlugin;
import org.sbml.libsbml.SpatialPoint;
import org.sbml.libsbml.SpatialSpeciesRxnPlugin;
import org.sbml.libsbml.SpatialSymbolReference;
import org.sbml.libsbml.Species;
import org.sbml.libsbml.SpeciesReference;
import org.sbml.libsbml.Trigger;
import org.sbml.libsbml.UnitDefinition;
import org.sbml.libsbml.XMLNamespaces;
import org.sbml.libsbml.libsbml;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SBMLUtils.SBMLUnitParameter;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.VCDocument.GeomFromFieldDataCreationInfo;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLPrinter;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlParseException;

/**
 * Insert the type's description here.
 * Creation date: (3/31/2006 1:02:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SBMLSpatialExporter {
	private int sbmlLevel = 2;
	private int sbmlVersion = 3;
	private org.sbml.libsbml.Model sbmlModel = null;
	private cbit.vcell.biomodel.BioModel vcBioModel = null;

	private SimulationContext vcSelectedSimContext = null;
	private SimulationJob vcSelectedSimJob = null;
	
	// used for exporting vcell-related annotations.
	Namespace sbml_vcml_ns = Namespace.getNamespace(XMLTags.VCELL_NS_PREFIX, SBMLUtils.SBML_VCELL_NS);

	// SBMLAnnotationUtil to get the SBML-related annotations, notes, free-text annotations from a Biomodel VCMetaData
	private SBMLAnnotationUtil sbmlAnnotationUtil = null;

	private java.util.Hashtable<String, String> globalParamNamesHash = new java.util.Hashtable<String, String>();
	private SBMLExportSpec sbmlExportSpec = new SBMLExportSpec(VCUnitDefinition.UNIT_molecules, VCUnitDefinition.UNIT_um3, VCUnitDefinition.UNIT_um2, VCUnitDefinition.UNIT_um, VCUnitDefinition.UNIT_s);
	
	public static class SBMLExportSpec {
		private VCUnitDefinition substanceUnits = null;
		private VCUnitDefinition volUnits = null;
		private VCUnitDefinition areaUnits = null;
		private VCUnitDefinition lengthUnits = null;
		private VCUnitDefinition timeUnits = null;
		
		public SBMLExportSpec(VCUnitDefinition argSunits, VCUnitDefinition argVUnits, VCUnitDefinition argAUnits, VCUnitDefinition argLUnits, VCUnitDefinition argTUnits) {
			if (!argSunits.isCompatible(VCUnitDefinition.UNIT_molecules) && !argSunits.isCompatible(VCUnitDefinition.UNIT_mol)) {
				throw new RuntimeException("Substance units not compatible with molecules or moles");
			}
			if (!argVUnits.isCompatible(VCUnitDefinition.UNIT_um3)) {
				throw new RuntimeException("Volume units not compatible with m3 (eg., um3 or litre)");
			}
			if (!argAUnits.isCompatible(VCUnitDefinition.UNIT_um2)) {
				throw new RuntimeException("Area units not compatible with m2");
			}
			if (!argLUnits.isCompatible(VCUnitDefinition.UNIT_um)) {
				throw new RuntimeException("Length units not compatible with m");
			}
			if (!argTUnits.isCompatible(VCUnitDefinition.UNIT_s)) {
				throw new RuntimeException("Time units not compatible with sec");
			}
			this.substanceUnits = argSunits;
			this.volUnits = argVUnits;
			this.areaUnits = argAUnits;
			this.lengthUnits = argLUnits;
			this.timeUnits = argTUnits;
		}
		public VCUnitDefinition getSubstanceUnits() {
			return substanceUnits;			
		}
		public VCUnitDefinition getVolumeUnits() {
			return volUnits;		
		}
		public VCUnitDefinition getAreaUnits() {
			return areaUnits;			
		}
		public VCUnitDefinition getLengthUnits() {
			return lengthUnits;		
		}
		public VCUnitDefinition getTimeUnits() {
			return timeUnits;			
		}
		public VCUnitDefinition getConcentrationUnit(int dimension) {
			switch (dimension) {
			case 1 : 
				return getSubstanceUnits().divideBy(getLengthUnits());
			case 2 :
				return getSubstanceUnits().divideBy(getAreaUnits());
			case 3 : 
				return getSubstanceUnits().divideBy(getVolumeUnits());
			default :
				throw new RuntimeException("Unsupported dimension of compartment; unable to compute concentration units");
			}
		}
	}

	static
	{
		ResourceUtil.loadlibSbmlLibray();
	}

	/**
	 * SBMLExporter constructor comment.
	 */
	public SBMLSpatialExporter(BioModel argBioModel, int argSbmlLevel, int argSbmlVersion) {
		super();
		vcBioModel = argBioModel;
		sbmlLevel = argSbmlLevel;
		sbmlVersion = argSbmlVersion;
		if (vcBioModel != null) {
			sbmlAnnotationUtil = new SBMLAnnotationUtil(vcBioModel.getVCMetaData(), vcBioModel, SBMLUtils.getNamespaceFromLevelAndVersion(sbmlLevel, sbmlVersion));
		}
	}
	
	/**
	 * SBMLExporter constructor comment.
	 */
	public SBMLSpatialExporter(BioModel argBioModel) {
		super();
		vcBioModel = argBioModel;
		if (vcBioModel != null) {
			sbmlAnnotationUtil = new SBMLAnnotationUtil(vcBioModel.getVCMetaData(), vcBioModel, SBMLUtils.SBML_NS_2);
		}
	}

/**
 * addCompartments comment.
 */
protected void addCompartments() {
	cbit.vcell.model.Structure[] vcStructures = vcBioModel.getModel().getStructures();
	
	for (int i = 0; i < vcStructures.length; i++){
		org.sbml.libsbml.Compartment sbmlCompartment = sbmlModel.createCompartment();
		sbmlCompartment.setId(TokenMangler.mangleToSName(vcStructures[i].getName()));
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
			sbmlSizeUnit = sbmlExportSpec.getVolumeUnits();
			sbmlCompartment.setUnits(org.vcell.util.TokenMangler.mangleToSName(sbmlSizeUnit.getSymbol()));
		} else if (vcStructures[i] instanceof Membrane) {
			Membrane vcMembrane = (Membrane)vcStructures[i];
			sbmlCompartment.setSpatialDimensions(2);
			sbmlCompartment.setOutside(TokenMangler.mangleToSName(vcMembrane.getOutsideFeature().getName()));
			sbmlSizeUnit = sbmlExportSpec.getAreaUnits();
			sbmlCompartment.setUnits(org.vcell.util.TokenMangler.mangleToSName(sbmlSizeUnit.getSymbol()));
		}

		StructureMapping vcStructMapping = getSelectedSimContext().getGeometryContext().getStructureMapping(vcStructures[i]);
		try {
			// The unit for 3D compartment size in VCell is um3, we are going to write it out in um3 in the SBML document.
			// Hence multiplying the size expression with the conversion factor between VC and SBML units for the compartment size. 
			
			Expression sizeExpr = null;
			VCUnitDefinition vcSizeUnit = vcStructMapping.getSizeParameter().getUnitDefinition();
			if (vcStructMapping.getSizeParameter().getExpression() != null) {
				double factor = 1.0;
				factor = vcSizeUnit.convertTo(factor, sbmlSizeUnit);
				sizeExpr = Expression.mult(vcStructMapping.getSizeParameter().getExpression(), new Expression(factor));
				sbmlCompartment.setSize(sizeExpr.evaluateConstant());
			} else {
				// really no need to set sizes of compartments in spatial ..... ????
				//	throw new RuntimeException("Compartment size not set for compartment \"" + vcStructures[i].getName() + "\" ; Please set size and try exporting again.");
			}
		} catch (cbit.vcell.parser.ExpressionException e) {
			// If it is in the catch block, it means that the compartment size was probably not a double, but an assignment.
			// Check if the expression for the compartment size is not null and add it as an assignment rule.
			Expression sizeExpr = vcStructMapping.getSizeParameter().getExpression();
			if (sizeExpr != null) {
				ASTNode ruleFormulaNode = getFormulaFromExpression(sizeExpr);
				AssignmentRule assignRule = sbmlModel.createAssignmentRule();
				assignRule.setVariable(vcStructures[i].getName());
				assignRule.setMath(ruleFormulaNode);
				// If compartmentSize is specified by an assignment rule, the 'constant' field should be set to 'false' (default - true).
				sbmlCompartment.setConstant(false);
				sbmlModel.addRule(assignRule);
			}
		}
		
		// Add the outside compartment of given compartment as annotation to the compartment.
		// This is required later while trying to read in compartments ...
		Element sbmlImportRelatedElement = null;
		if (vcStructures[i].getParentStructure() != null) {
			sbmlImportRelatedElement = new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
			Element compartmentElement = new Element(XMLTags.OutsideCompartmentTag, sbml_vcml_ns);
			compartmentElement.setAttribute(XMLTags.NameAttrTag, TokenMangler.mangleToSName(vcStructures[i].getParentStructure().getName()));
			sbmlImportRelatedElement.addContent(compartmentElement);
		}

		// Get annotation (RDF and non-RDF) for reactionStep from SBMLAnnotationUtils
		sbmlAnnotationUtil.writeAnnotation(vcStructures[i], sbmlCompartment, sbmlImportRelatedElement);
		
		// Now set notes,
		sbmlAnnotationUtil.writeNotes(vcStructures[i], sbmlCompartment);
	}
}


/**
 * addKineticParameterUnits:
 */
private void addKineticAndGlobalParameterUnits(ArrayList<String> unitsList) {

	//
	// Get all kinetic parameters from simple reactions and flux reactions from the Biomodel
	// And all Model (global) parameters from Model.
	// For each parameter,
	//		get its unit (VCunitDefinition)
	//		check if it is part of unitsList - if so, continue
	//		check if it is a base unit - if so, continue
	//		else, get the converted unit (VC -> SBML)
	//		add unit to sbmlModel unit definition
	//

	Vector<Parameter> paramsVector = new Vector<Parameter>();
	// Add globals
	ModelParameter[] globalParams = vcBioModel.getModel().getModelParameters();
	for (int i = 0; i < globalParams.length; i++) {
		paramsVector.addElement(globalParams[i]);
	}
	// Add reaction kinetic parameters
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
		String unitSymbol = org.vcell.util.TokenMangler.mangleToSName(paramUnitDefn.getSymbol());
		// If this unit is present in the unitsList (already in the list of unitDefinitions for SBML model), continue.
		if (unitSymbol == null || unitsList.contains(unitSymbol)) {
			continue;
		}
		// If this unit is a base unit, continue.
		if (SBMLUnitTranslator.isSbmlBaseUnit(unitSymbol)) {
			continue;
		}
		if (!paramUnitDefn.isTBD()) {
			UnitDefinition newUnitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(paramUnitDefn, sbmlLevel, sbmlVersion);
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
protected void addParameters() throws ExpressionException {
	// first add an SBML parameter for VCell reserved symbol 'KMOLE'.
	org.sbml.libsbml.Parameter sbmlParam = sbmlModel.createParameter();
	String paramUnits = TokenMangler.mangleToSName(VCUnitDefinition.UNIT_uM_um3_per_molecules.getSymbol());
	sbmlParam.setId("KMOLE");
	sbmlParam.setValue(1.0/602.0);
	sbmlParam.setUnits(paramUnits);
	sbmlParam.setConstant(true);
	// Now add VCell global parameters to the SBML listofParameters
	Model vcModel = getSelectedSimContext().getModel();
	ModelParameter[] vcGlobalParams = vcModel.getModelParameters();  
	for (int i = 0; vcGlobalParams != null && i < vcGlobalParams.length; i++) {
		sbmlParam = sbmlModel.createParameter();
		sbmlParam.setId(vcGlobalParams[i].getName());
		Expression paramExpr = new Expression(vcGlobalParams[i].getExpression());
		if (paramExpr.isNumeric()) {
			// For a VCell global param, if it is numeric, it has a constant value and is not defined by a rule, hence set Constant = true.
			sbmlParam.setValue(paramExpr.evaluateConstant());
			sbmlParam.setConstant(true);
		} else {
			// non-numeric VCell global parameter will be defined by a (assignment) rule, hence mark Constant = false.
			sbmlParam.setConstant(false);

			// add assignment rule for param
			// First check if 'paramExpr' has any references to speciesContexts. If so, the conc units should be adjusted.
			// Get the VC (from SpeciesContextSpec) and SBML concentration units (from sbmlExportSpec) and get the conversion factor ('factor').
			// Replace the occurrence of species in the 'paramExpr' with the new expr : species*factor.
			String[] symbols = paramExpr.getSymbols();
			for (int j = 0; symbols != null && j < symbols.length; j++) {
				SpeciesContext vcSpeciesContext = vcModel.getSpeciesContext(symbols[j]); 
				if (vcSpeciesContext != null) {
					Species species = sbmlModel.getSpecies(vcSpeciesContext.getName());
					cbit.vcell.mapping.SpeciesContextSpec vcSpeciesContextsSpec = getSelectedSimContext().getReactionContext().getSpeciesContextSpec(vcSpeciesContext);
					VCUnitDefinition vcConcUnit = vcSpeciesContextsSpec.getInitialConditionParameter().getUnitDefinition();
					VCUnitDefinition sbmlConcUnits = sbmlExportSpec.getConcentrationUnit(vcSpeciesContext.getStructure().getDimension());
					SBMLUnitParameter sbmlUnitParam = SBMLUtils.getConcUnitFactor("spConcUnit", sbmlConcUnits, vcConcUnit);
					Expression modifiedSpExpr = Expression.mult(new Expression(species.getId()), sbmlUnitParam.getExpression()).flatten();
					paramExpr.substituteInPlace(new Expression(species.getId()), modifiedSpExpr);
				}
			}

			ASTNode paramFormulaNode = getFormulaFromExpression(paramExpr);
			AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
			sbmlParamAssignmentRule.setVariable(vcGlobalParams[i].getName());
			sbmlParamAssignmentRule.setMath(paramFormulaNode);
		}
		VCUnitDefinition vcParamUnit = vcGlobalParams[i].getUnitDefinition();
		if (!vcParamUnit.compareEqual(VCUnitDefinition.UNIT_TBD)) {
			sbmlParam.setUnits(TokenMangler.mangleToSName(vcParamUnit.getSymbol()));
		}
	}
}


/**
 * addReactions comment.
 */
protected void addReactions() {

	// Check if any reaction has electrical mapping
	boolean bCalculatePotential = false;
	StructureMapping structureMappings[] = getSelectedSimContext().getGeometryContext().getStructureMappings();
	for (int i = 0; i < structureMappings.length; i++){
		if (structureMappings[i] instanceof MembraneMapping){
			if (((MembraneMapping)structureMappings[i]).getCalculateVoltage()){
				bCalculatePotential = true;
			}
		}
		
	}
	// If it does, VCell doesn't export it to SBML (no representation).
	if (bCalculatePotential) {
		throw new RuntimeException("This VCell model has Electrical mapping; cannot be exported to SBML at this time");
	}

	cbit.vcell.mapping.ReactionSpec[] vcReactionSpecs = getSelectedSimContext().getReactionContext().getReactionSpecs();
	for (int i = 0; i < vcReactionSpecs.length; i++){
		if (vcReactionSpecs[i].isExcluded()) {
			continue;
		}
		ReactionStep vcReactionStep = vcReactionSpecs[i].getReactionStep();
		//Create sbml reaction
		String rxnName = vcReactionStep.getName();
		org.sbml.libsbml.Reaction sbmlReaction = sbmlModel.createReaction();
		sbmlReaction.setId(org.vcell.util.TokenMangler.mangleToSName(rxnName));
		sbmlReaction.setName(rxnName);
			
		// If the reactionStep is a flux reaction, add the details to the annotation (structure, carrier valence, flux carrier, fluxOption, etc.)
		// If reactionStep is a simple reaction, add annotation to indicate the structure of reaction.
		// Useful when roundtripping ...
		Element sbmlImportRelatedElement = null;
		try {
			sbmlImportRelatedElement = getAnnotationElement(vcReactionStep);
		} catch (XmlParseException e1) {
			e1.printStackTrace(System.out);
//			throw new RuntimeException("Error ");
		}
		
		// Get annotation (RDF and non-RDF) for reactionStep from SBMLAnnotationUtils
		sbmlAnnotationUtil.writeAnnotation(vcReactionStep, sbmlReaction, sbmlImportRelatedElement);
		
		// Now set notes, 
		sbmlAnnotationUtil.writeNotes(vcReactionStep, sbmlReaction);
		
		// Get reaction kineticLaw
		Kinetics vcRxnKinetics = vcReactionStep.getKinetics();
		org.sbml.libsbml.KineticLaw sbmlKLaw = sbmlReaction.createKineticLaw();

		try {
			// Convert expression from kinetics rate parameter into MathML and use libSBMl utilities to convert it to formula
			// (instead of directly using rate parameter's expression infix) to maintain integrity of formula :
			// for example logical and inequalities are not handled gracefully by libSBMl if expression.infix is used.
			Expression correctedRateExpr = null; 
			if (vcRxnKinetics instanceof DistributedKinetics) {
				// If DistributedKinetics, multiply by compartmentSize & divide by KMOLE to convert to molecules/sec for feature reaction and membrane flux
				// for membrane reaction, we just need to multiply by compartmentSize to convert units to molecules/sec. 
				Compartment sbmlCompartment = sbmlModel.getCompartment(org.vcell.util.TokenMangler.mangleToSName(vcReactionStep.getStructure().getName()));
				correctedRateExpr = Expression.mult(((DistributedKinetics)vcRxnKinetics).getReactionRateParameter().getExpression(), new Expression(sbmlCompartment.getId()));
				if ( (vcReactionStep.getStructure() instanceof Feature) || ((vcReactionStep.getStructure() instanceof Membrane) && (vcReactionStep instanceof FluxReaction)) ) {
					correctedRateExpr = Expression.mult(correctedRateExpr, Expression.invert(new Expression("KMOLE")));
				}
			} else if (vcRxnKinetics instanceof LumpedKinetics) {
				// LumpedKinetics is already in molecules/sec, no need to do anything?
				correctedRateExpr = ((LumpedKinetics)vcRxnKinetics).getLumpedReactionRateParameter().getExpression();
			} else {
				throw new RuntimeException("Unknown kinetics type for reaction : " + vcReactionStep.getName());
			}
			
			// Adjust for species concentration - from SBML molecules/um3 -> VC uM
			correctedRateExpr = adjustSpeciesConcUnitsInRateExpr(correctedRateExpr, vcRxnKinetics);

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
										org.sbml.libsbml.Parameter sbmlKinParam = sbmlModel.createParameter();
										sbmlKinParam.setId(newParamName);
										sbmlKinParam.setValue(vcKineticsParams[j].getConstantValue());
										// Set SBML units for sbmlParam using VC units from vcParam  
										if (!vcKineticsParams[j].getUnitDefinition().isTBD()) {
											sbmlKinParam.setUnits(TokenMangler.mangleToSName(vcKineticsParams[j].getUnitDefinition().getSymbol()));
										}
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
							org.sbml.libsbml.Parameter sbmlKinParam = sbmlKLaw.createParameter();
							sbmlKinParam.setId(origParamName);
							sbmlKinParam.setValue(vcKineticsParams[j].getConstantValue());
							// Set SBML units for sbmlParam using VC units from vcParam  
							if (!vcKineticsParams[j].getUnitDefinition().isTBD()) {
								sbmlKinParam.setUnits(TokenMangler.mangleToSName(vcKineticsParams[j].getUnitDefinition().getSymbol()));
							}
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
			
			// In the fourth pass thro' the kinetic params, the non-numeric params are checked to see if they have any species in their
			// expressions - if so, they have to be adjusted for concentration units : from SBML molecules/um3 -> VC uM
			for (int j = 0; j < kinParamExprs.length; j++) {
				if (kinParamExprs[j] != null) {
					kinParamExprs[j] = adjustSpeciesConcUnitsInRateExpr(kinParamExprs[j], vcRxnKinetics);
				}
			}	// end for (j)  - fourth pass

					
			// In the fifth pass thro' the kinetic params, the non-numeric params are added to the global params of the model
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
					AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
					sbmlParamAssignmentRule.setVariable(paramName);
					sbmlParamAssignmentRule.setMath(paramFormulaNode);
					org.sbml.libsbml.Parameter sbmlKinParam = sbmlModel.createParameter();
					sbmlKinParam.setId(paramName);
					if (!vcKineticsParams[j].getUnitDefinition().isTBD()) {
						sbmlKinParam.setUnits(TokenMangler.mangleToSName(vcKineticsParams[j].getUnitDefinition().getSymbol()));
					}
					// Since the parameter is being specified by a Rule, its 'constant' field shoud be set to 'false' (default - true).
					sbmlKinParam.setConstant(false);
				}
			} // end for (j) - fifth pass

			// After making all necessary adjustments to the rate expression, now set the sbmlKLaw.
			ASTNode exprFormulaNode = getFormulaFromExpression(correctedRateExpr);
			sbmlKLaw.setMath(exprFormulaNode);
		} catch (cbit.vcell.parser.ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error getting value of parameter : "+e.getMessage());
		}
		
		// Add kineticLaw to sbmlReaction - not needed now, since we use sbmlRxn.createKLaw() ??
		sbmlReaction.setKineticLaw(sbmlKLaw);
		
		// Add reactants, products, modifiers
		// Simple reactions have catalysts, fluxes have 'flux' 
		cbit.vcell.model.ReactionParticipant[] rxnParticipants = vcReactionStep.getReactionParticipants();
		for (int j = 0; j < rxnParticipants.length; j++){
			if (rxnParticipants[j] instanceof cbit.vcell.model.Reactant) {
				SpeciesReference reactantSpRef = sbmlReaction.createReactant();
				reactantSpRef.setSpecies(rxnParticipants[j].getSpeciesContext().getName());
				reactantSpRef.setStoichiometry(Double.parseDouble(Integer.toString(rxnParticipants[j].getStoichiometry())));
			} else if (rxnParticipants[j] instanceof cbit.vcell.model.Product) {
				SpeciesReference pdtSpRef = sbmlReaction.createProduct();
				pdtSpRef.setSpecies((rxnParticipants[j].getSpeciesContext().getName()));
				pdtSpRef.setStoichiometry(Double.parseDouble(Integer.toString(rxnParticipants[j].getStoichiometry())));
			} else if (rxnParticipants[j] instanceof cbit.vcell.model.Catalyst) {
				ModifierSpeciesReference modifierSpRef = sbmlReaction.createModifier();
				modifierSpRef.setSpecies(rxnParticipants[j].getSpeciesContext().getName());
			} else if (rxnParticipants[j] instanceof cbit.vcell.model.Flux) {
				// For a flux reaction, the reaction participants are 'fluxes'. The 'outside' participant is considered the reactant while
				// the 'inside' participant is considered the product. Catalysts are modifiers.
				cbit.vcell.model.Flux flux = (cbit.vcell.model.Flux)rxnParticipants[j];
				if (vcReactionStep.getStructure() instanceof Membrane) {
					Membrane fluxMembrane = (Membrane)((cbit.vcell.model.FluxReaction)vcReactionStep).getStructure();
					if (fluxMembrane.getOutsideFeature().compareEqual(flux.getStructure())) {
						SpeciesReference reactantSpRef = sbmlReaction.createReactant();
						reactantSpRef.setSpecies(flux.getSpeciesContext().getName());
						reactantSpRef.setStoichiometry(Double.parseDouble(Integer.toString(flux.getStoichiometry())));
					} else if (fluxMembrane.getInsideFeature().compareEqual(flux.getStructure())) {
						SpeciesReference pdtSpRef = sbmlReaction.createProduct();
						pdtSpRef.setSpecies((rxnParticipants[j].getSpeciesContext().getName()));
						pdtSpRef.setStoichiometry(Double.parseDouble(Integer.toString(rxnParticipants[j].getStoichiometry())));
					}
				}
			}
		}

		if (vcReactionSpecs[i].isFast()) {
			sbmlReaction.setFast(true);
		}
		
		// set requiredElements attributes
		RequiredElementsSBasePlugin reqplugin = (RequiredElementsSBasePlugin)sbmlReaction.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
		reqplugin.setMathOverridden(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		reqplugin.setCoreHasAlternateMath(true);

		// set the "isLocal" attribute = true (in 'spatial' namespace) for each species
		SpatialSpeciesRxnPlugin srplugin = (SpatialSpeciesRxnPlugin)sbmlReaction.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		srplugin.setIsLocal(true);

		// delete used objects
		sbmlKLaw.delete();
		sbmlReaction.delete();
	}
}

/**
 * adjustSpeciesConcUnitsInRateExpr
 * @param origRateExpr
 * @return
 */
private Expression adjustSpeciesConcUnitsInRateExpr(Expression origRateExpr, Kinetics rxnKinetics) throws ExpressionException {
	// The expr for speciesConcentration from VCell is in terms of VCell units (uM); we have converted species units to SBML conc units
	// to export to SBML. We need to translate them back to VCell units within the expr; then we translate the VCell rate units into SBML units.

	Model vcModel = vcBioModel.getModel();
	String[] symbols = origRateExpr.getSymbols();
	if (symbols != null) {
	for (int i = 0; i < symbols.length; i++) {
		SpeciesContext vcSpeciesContext = vcModel.getSpeciesContext(symbols[i]);
		if (vcSpeciesContext != null) {
			// this change is applicable only to species in volumes, not on membranes, since the 
			// concentration units for species on membranes is already in molecules (/um2).
			org.sbml.libsbml.Species species = sbmlModel.getSpecies(vcSpeciesContext.getName());
			/* Check if species name is used as a kinetic parameter. If so, the parameter in the local namespace 
			   takes precedence. So ignore unit conversion for the species with the same name. */
			boolean bSpeciesNameFoundInLocalParamList = false;
			Kinetics.KineticsParameter[] kinParams = rxnKinetics.getKineticsParameters();
			for (int k = 0; k < kinParams.length; k++) {
				if ( (kinParams[k].getRole() != Kinetics.ROLE_ReactionRate) && (kinParams[k].getRole() != Kinetics.ROLE_LumpedReactionRate) ) {
					if (kinParams[k].getName().equals(species.getId())) {
						bSpeciesNameFoundInLocalParamList = true;
						break; 		// break out of kinParams loop
					}
				}
			}
			if (bSpeciesNameFoundInLocalParamList) {
				break;			// break out of speciesContexts loop
			}
			// Get the VC and SBML concentration units (from sbmlExportSpec) and get the conversion factor ('factor').
			// Replace the occurrence of species in the rate expression with the new expr : species*factor.
			cbit.vcell.mapping.SpeciesContextSpec vcSpeciesContextsSpec = getSelectedSimContext().getReactionContext().getSpeciesContextSpec(vcSpeciesContext);
			VCUnitDefinition vcConcUnit = vcSpeciesContextsSpec.getInitialConditionParameter().getUnitDefinition();
			VCUnitDefinition sbmlConcUnits = sbmlExportSpec.getConcentrationUnit(vcSpeciesContext.getStructure().getDimension());
			SBMLUnitParameter sbmlUnitParam = SBMLUtils.getConcUnitFactor("spConcUnit", sbmlConcUnits, vcConcUnit);
			Expression modifiedSpExpr = Expression.mult(new Expression(species.getId()), sbmlUnitParam.getExpression()).flatten();
			origRateExpr.substituteInPlace(new Expression(species.getId()), modifiedSpExpr);
		}
	} 	// end for (i) - symbols
	}
	return origRateExpr;
}

/**
 * addSpecies comment.
 */
protected void addSpecies() {
	SpeciesContext[] vcSpeciesContexts = vcBioModel.getModel().getSpeciesContexts();

	for (int i = 0; i < vcSpeciesContexts.length; i++){
		org.sbml.libsbml.Species sbmlSpecies = sbmlModel.createSpecies();
		sbmlSpecies.setId(vcSpeciesContexts[i].getName());
		// Assuming that at this point, the compartment(s) for the model are already filled in.
		Compartment compartment = sbmlModel.getCompartment(TokenMangler.mangleToSName(vcSpeciesContexts[i].getStructure().getName()));
		if (compartment != null) {
			sbmlSpecies.setCompartment(compartment.getId());
		}

		// 'hasSubstanceOnly' field will be 'false', since VC deals only with initial concentrations and not initial amounts.
		sbmlSpecies.setHasOnlySubstanceUnits(false);

		// Get (and set) the initial concentration value

		if (getSelectedSimContext() == null) {
			throw new RuntimeException("No simcontext (application) specified; Cannot proceed.");
		}

		// Get the speciesContextSpec in the simContext corresponding to the 'speciesContext'; and extract its initial concentration value.
		SpeciesContextSpec vcSpeciesContextsSpec = getSelectedSimContext().getReactionContext().getSpeciesContextSpec(vcSpeciesContexts[i]);
		// since we are setting the substance units for species to 'molecule' or 'item', a unit that is originally in uM (or molecules/um2),
		// we need to convert concentration from uM -> molecules/um3; this can be achieved by dividing by KMOLE.
		VCUnitDefinition vcConcUnit = vcSpeciesContextsSpec.getInitialConditionParameter().getUnitDefinition();
		VCUnitDefinition sbmlConcUnits = sbmlExportSpec.getConcentrationUnit(vcSpeciesContexts[i].getStructure().getDimension());
		SBMLUnitParameter sbmlUnitParam = null;
		try {
			sbmlUnitParam = SBMLUtils.getConcUnitFactor("spConcUnit", vcConcUnit, sbmlConcUnits);
			Expression initConcExpr = Expression.mult(vcSpeciesContextsSpec.getInitialConditionParameter().getExpression(), sbmlUnitParam.getExpression()).flatten();
			sbmlSpecies.setInitialConcentration(initConcExpr.evaluateConstant());
		} catch (cbit.vcell.parser.ExpressionException e) {
			// If it is in the catch block, it means that the initial concentration of the species was not a double, but an expression, probably.
			// Check if the expression for the species is not null. If exporting to L2V1, and species is 'fixed', and if expr is not in terms of
			// x, y, z, or other species then create an assignment rule for species concentration; else throw exception.
			// If exporting to L2V3, if species concentration is not an expr with x, y, z or other species, add as InitialAssignment, else complain.
			if (vcSpeciesContextsSpec.getInitialConditionParameter().getExpression() != null) {
				try {
					sbmlUnitParam = SBMLUtils.getConcUnitFactor("spConcUnit", vcConcUnit, sbmlConcUnits);
					Expression initConcExpr = Expression.mult(vcSpeciesContextsSpec.getInitialConditionParameter().getExpression(), sbmlUnitParam.getExpression());
					if (sbmlLevel == 2 && sbmlVersion == 3) {
						// L2V3 - add expression as init assignment
						ASTNode initAssgnMathNode = getFormulaFromExpression(initConcExpr);
						InitialAssignment initAssignment = sbmlModel.createInitialAssignment();
						initAssignment.setSymbol(vcSpeciesContexts[i].getName());
						initAssignment.setMath(initAssgnMathNode);
					} else { 	// L2V1 (or L1V2 also??)
						// L2V1 (and L1V2?) and species is 'fixed' (constant), and not fn of x,y,z, other sp, add expr as assgn rule 
//						if (getBoundaryCondition(vcSpeciesContexts[i])) {
							ASTNode assgnRuleMathNode = getFormulaFromExpression(initConcExpr);
							AssignmentRule assgnRule = sbmlModel.createAssignmentRule();
							assgnRule.setVariable(vcSpeciesContexts[i].getName());
							assgnRule.setMath(assgnRuleMathNode);
//						} else {
//							throw new RuntimeException("Failed to export : Unable to add species " + vcSpeciesContexts[i].getName() + " to SBML model since its initial expression \'" + initConcExpr.infix() + "\'  is a function of x, y, z or contains another species in its expression.");
//						}
					}
				} catch (ExpressionException e1) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Could not add concentration as Initial assignment for species : " + vcSpeciesContexts[i].getName());
				}
			}
		}

		// Get (and set) the boundary condition value
		boolean bBoundaryCondition = getBoundaryCondition(vcSpeciesContexts[i]);
		sbmlSpecies.setBoundaryCondition(bBoundaryCondition); 

		// set species substance units as 'molecules' - same as defined in the model; irrespective of it is in surface or volume.
		sbmlSpecies.setSubstanceUnits(sbmlExportSpec.getSubstanceUnits().getSymbol());
		
		// set requiredElements attributes
		RequiredElementsSBasePlugin reqplugin = (RequiredElementsSBasePlugin)sbmlSpecies.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
		reqplugin.setMathOverridden(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		reqplugin.setCoreHasAlternateMath(true);

		// set the "isSpatial" attribute = true (in 'spatial' namespace) for each species
		SpatialSpeciesRxnPlugin srplugin = (SpatialSpeciesRxnPlugin)sbmlSpecies.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		srplugin.setIsSpatial(true);
		
		// Required for setting BoundaryConditions : structureMapping for vcSpeciesContext[i] & sbmlGeometry.coordinateComponents
		StructureMapping sm = getSelectedSimContext().getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure());
		SpatialModelPlugin mplugin = (SpatialModelPlugin)sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		org.sbml.libsbml.Geometry sbmlGeometry = mplugin.getGeometry();
		CoordinateComponent ccX = sbmlGeometry.getCoordinateComponent(ReservedSymbol.X.getName());
		CoordinateComponent ccY = sbmlGeometry.getCoordinateComponent(ReservedSymbol.Y.getName());
		CoordinateComponent ccZ = sbmlGeometry.getCoordinateComponent(ReservedSymbol.Z.getName());
		// add diffusion, advection, boundary condition parameters for species, if they exist
		Parameter[] scsParams = vcSpeciesContextsSpec.getParameters();
		if (scsParams != null) {
			for (int j = 0; j < scsParams.length; j++) {
				if (scsParams[j] != null) {
					SpeciesContextSpecParameter scsParam = (SpeciesContextSpecParameter)scsParams[j];
					// no need to add parameters in SBML for init conc or init count
					int role = scsParam.getRole();
					if (role == SpeciesContextSpec.ROLE_InitialConcentration || role == SpeciesContextSpec.ROLE_InitialCount) {
						continue;
					}
					// if diffusion is 0 && vel terms are not specified, boundary condition not present
					boolean bBCParam = ((role == SpeciesContextSpec.ROLE_BoundaryValueXm) || (role == SpeciesContextSpec.ROLE_BoundaryValueXp) ||
							 			(role == SpeciesContextSpec.ROLE_BoundaryValueYm) || (role == SpeciesContextSpec.ROLE_BoundaryValueYp) ||
							 			(role == SpeciesContextSpec.ROLE_BoundaryValueZm) || (role == SpeciesContextSpec.ROLE_BoundaryValueZp));
					if (bBCParam) {
						Expression diffExpr = vcSpeciesContextsSpec.getDiffusionParameter().getExpression();	
						boolean bDiffExprNull = (diffExpr == null);
						boolean bDiffExprIsZero = false;
						if (!bDiffExprNull && diffExpr.isNumeric()) {
							try {
								bDiffExprIsZero =  (diffExpr.evaluateConstant() == 0.0);
							} catch (Exception e) {
								e.printStackTrace(System.out);
								throw new RuntimeException("Unable to evalute numeric value of diffusion parameter for speciesContext '" + vcSpeciesContexts[i] + "'.");
							} 
						}
						boolean bDiffusionZero = (bDiffExprNull || bDiffExprIsZero);
						Expression velX_Expr = vcSpeciesContextsSpec.getVelocityXParameter().getExpression();
						boolean bVelX_ExprIsNull = (velX_Expr == null);
						Expression velY_Expr = vcSpeciesContextsSpec.getVelocityYParameter().getExpression();
						boolean bVelY_ExprIsNull = (velY_Expr == null);
						Expression velZ_Expr = vcSpeciesContextsSpec.getVelocityZParameter().getExpression();
						boolean bVelZ_ExprIsNull = (velZ_Expr == null);
						boolean bAdvectionNull = (bVelX_ExprIsNull && bVelY_ExprIsNull && bVelZ_ExprIsNull);
						if (bDiffusionZero && bAdvectionNull) {
							continue;
						}
					}
					
					// if scsParam is a boundary condition and the corresponding coordniateComponent (from SBML) is null, do not create SBML parameter
					// for example, if scsParam is BC_Zm and if coordinateComponent 'ccZ' is null, no SBML parameter should be created for BC_Zm
					if ( (((role == SpeciesContextSpec.ROLE_BoundaryValueXm) || (role == SpeciesContextSpec.ROLE_BoundaryValueXp)) && (ccX == null)) || 
  						 (((role == SpeciesContextSpec.ROLE_BoundaryValueYm) || (role == SpeciesContextSpec.ROLE_BoundaryValueYp)) && (ccY == null)) ||
						 (((role == SpeciesContextSpec.ROLE_BoundaryValueZm) || (role == SpeciesContextSpec.ROLE_BoundaryValueZp)) && (ccZ == null)) )
					{
						continue;
					}
					org.sbml.libsbml.Parameter sbmlParam = createSBMLParamFromSpeciesParam(vcSpeciesContexts[i], (SpeciesContextSpecParameter)scsParams[j]);
					if (sbmlParam != null) {
						SpatialParameterPlugin spplugin = (SpatialParameterPlugin)sbmlParam.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
						if (role == SpeciesContextSpec.ROLE_DiffusionRate) {
							// set diffusionCoefficient element in SpatialParameterPlugin for param
							DiffusionCoefficient sbmlDiffCoeff = spplugin.getDiffusionCoefficient();
							sbmlDiffCoeff.setVariable(vcSpeciesContexts[i].getName());
							sbmlDiffCoeff.setCoordinateIndex(0);
						} 
						if ((role == SpeciesContextSpec.ROLE_BoundaryValueXm) && (ccX != null)) {
							// set BoundaryCondn Xm element in SpatialParameterPlugin for param
							BoundaryCondition sbmlBCXm = spplugin.getBoundaryCondition();
							sbmlBCXm.setVariable(vcSpeciesContexts[i].getName());
							sbmlBCXm.setType(sm.getBoundaryConditionTypeXm().toString());
							sbmlBCXm.setCoordinateBoundary(ccX.getBoundaryMin().getSpatialId());
						} 
						if ((role == SpeciesContextSpec.ROLE_BoundaryValueXp) && (ccX != null)) {
							// set BoundaryCondn Xp element in SpatialParameterPlugin for param
							BoundaryCondition sbmlBCXp = spplugin.getBoundaryCondition();
							sbmlBCXp.setVariable(vcSpeciesContexts[i].getName());
							sbmlBCXp.setType(sm.getBoundaryConditionTypeXp().toString());
							sbmlBCXp.setCoordinateBoundary(ccX.getBoundaryMax().getSpatialId());
						} 
						if ((role == SpeciesContextSpec.ROLE_BoundaryValueYm)  && (ccY != null)) {
							// set BoundaryCondn Ym element in SpatialParameterPlugin for param
							BoundaryCondition sbmlBCYm = spplugin.getBoundaryCondition();
							sbmlBCYm.setVariable(vcSpeciesContexts[i].getName());
							sbmlBCYm.setType(sm.getBoundaryConditionTypeYm().toString());
							sbmlBCYm.setCoordinateBoundary(ccY.getBoundaryMin().getSpatialId());
						} 
						if ((role == SpeciesContextSpec.ROLE_BoundaryValueYp) && (ccY != null)){
							// set BoundaryCondn Yp element in SpatialParameterPlugin for param
							BoundaryCondition sbmlBCYp = spplugin.getBoundaryCondition();
							sbmlBCYp.setVariable(vcSpeciesContexts[i].getName());
							sbmlBCYp.setType(sm.getBoundaryConditionTypeYp().toString());
							sbmlBCYp.setCoordinateBoundary(ccY.getBoundaryMax().getSpatialId());
						} 
						if ((role == SpeciesContextSpec.ROLE_BoundaryValueZm)  && (ccZ != null)) {
							// set BoundaryCondn Zm element in SpatialParameterPlugin for param
							BoundaryCondition sbmlBCZm = spplugin.getBoundaryCondition();
							sbmlBCZm.setVariable(vcSpeciesContexts[i].getName());
							sbmlBCZm.setType(sm.getBoundaryConditionTypeZm().toString());
							sbmlBCZm.setCoordinateBoundary(ccZ.getBoundaryMin().getSpatialId());
						} 
						if ((role == SpeciesContextSpec.ROLE_BoundaryValueZp)  && (ccZ != null)) {
							// set BoundaryCondn Zp element in SpatialParameterPlugin for param
							BoundaryCondition sbmlBCZp = spplugin.getBoundaryCondition();
							sbmlBCZp.setVariable(vcSpeciesContexts[i].getName());
							sbmlBCZp.setType(sm.getBoundaryConditionTypeZp().toString());
							sbmlBCZp.setCoordinateBoundary(ccZ.getBoundaryMax().getSpatialId());
						} 
						if (role == SpeciesContextSpec.ROLE_VelocityX) {
							// set advectionCoeff X element in SpatialParameterPlugin for param
							AdvectionCoefficient sbmlAdvCoeffX = spplugin.getAdvectionCoefficient();
							sbmlAdvCoeffX.setVariable(vcSpeciesContexts[i].getName());
							sbmlAdvCoeffX.setCoordinateIndex(0);
						} 
						if (role == SpeciesContextSpec.ROLE_VelocityY) {
							// set advectionCoeff Y element in SpatialParameterPlugin for param
							AdvectionCoefficient sbmlAdvCoeffY = spplugin.getAdvectionCoefficient();
							sbmlAdvCoeffY.setVariable(vcSpeciesContexts[i].getName());
							sbmlAdvCoeffY.setCoordinateIndex(1);
						} 
						if (role == SpeciesContextSpec.ROLE_VelocityZ) {
							// set advectionCoeff Z element in SpatialParameterPlugin for param
							AdvectionCoefficient sbmlAdvCoeffZ = spplugin.getAdvectionCoefficient();
							sbmlAdvCoeffZ.setVariable(vcSpeciesContexts[i].getName());
							sbmlAdvCoeffZ.setCoordinateIndex(2);
						}
					} 	// if sbmlParam != null
				}	// if scsParams[j] != null
			}	// end for scsParams
		}	// end scsParams != null

		// Add the common name of species to annotation, and add an annotation element to the species.
		// This is required later while trying to read in fluxes ...
		Element sbmlImportRelatedElement = new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
		Element speciesElement = new Element(XMLTags.SpeciesTag, sbml_vcml_ns);
		speciesElement.setAttribute(XMLTags.NameAttrTag, TokenMangler.mangleToSName(vcSpeciesContexts[i].getSpecies().getCommonName()));
		sbmlImportRelatedElement.addContent(speciesElement);
		
		// Get RDF annotation for species from SBMLAnnotationUtils
		sbmlAnnotationUtil.writeAnnotation(vcSpeciesContexts[i].getSpecies(), sbmlSpecies, sbmlImportRelatedElement);

		// Now set notes,
		sbmlAnnotationUtil.writeNotes(vcSpeciesContexts[i].getSpecies(), sbmlSpecies);
	}
}

/**
 * createSBMLParamFromSpeciesParam : creates an SBML parameter for each speciesContextSpecParameter (diffusion coefficient, 
 * advection coeffs, boundary conditions (X,Y,Z).
 *  
 * @param spContext
 * @param scsParam
 * @return
 */
private org.sbml.libsbml.Parameter createSBMLParamFromSpeciesParam(SpeciesContext spContext, SpeciesContextSpecParameter scsParam) {
	try {
		Expression paramExpr = scsParam.getExpression();
		// if scsParam is diff, Vel X, Y, Z parameter and if its expression is null or 0.0, don't create parameter.
		int role = scsParam.getRole();
		if ( ((role == SpeciesContextSpec.ROLE_DiffusionRate) || (role == SpeciesContextSpec.ROLE_VelocityX) || 
			  (role == SpeciesContextSpec.ROLE_VelocityY) || (role == SpeciesContextSpec.ROLE_VelocityZ)) && 
			  ((paramExpr == null) || (paramExpr.isNumeric() && (scsParam.getConstantValue() == 0.0))) ) {
			return null;
		}
		// if scsParam is a BoundaryCondition, and paramExpr is null, values are set based on boundary condition type. 
		if ( ((role == SpeciesContextSpec.ROLE_BoundaryValueXm) || (role == SpeciesContextSpec.ROLE_BoundaryValueXp) ||
			  (role == SpeciesContextSpec.ROLE_BoundaryValueYm) || (role == SpeciesContextSpec.ROLE_BoundaryValueYp) ||
			  (role == SpeciesContextSpec.ROLE_BoundaryValueZm) || (role == SpeciesContextSpec.ROLE_BoundaryValueZp)) &&
			  (paramExpr == null) ) {
			StructureMapping sm = getSelectedSimContext().getGeometryContext().getStructureMapping(spContext.getStructure());
			Expression initCondnExpr = getSelectedSimContext().getReactionContext().getSpeciesContextSpec(spContext).getInitialConditionParameter().getExpression();
			// if BC type is Dirichlet (val), its value is same as init condn of speciesContext
			// if BC type is Neumann (flux), its value is 0.0
			if ((role == SpeciesContextSpec.ROLE_BoundaryValueXm)) {
				if (sm.getBoundaryConditionTypeXm().isDIRICHLET()) {
					paramExpr = new Expression(initCondnExpr);
				} else if (sm.getBoundaryConditionTypeXm().isNEUMANN()) {
					paramExpr = new Expression(0.0);
				}
			}
			if ((role == SpeciesContextSpec.ROLE_BoundaryValueXp)) {
				if (sm.getBoundaryConditionTypeXp().isDIRICHLET()) {
					paramExpr = new Expression(initCondnExpr);
				} else if (sm.getBoundaryConditionTypeXp().isNEUMANN()) {
					paramExpr = new Expression(0.0);
				}
			}
			if ((role == SpeciesContextSpec.ROLE_BoundaryValueYm)) {
				if (sm.getBoundaryConditionTypeYm().isDIRICHLET()) {
					paramExpr = new Expression(initCondnExpr);
				} else if (sm.getBoundaryConditionTypeYm().isNEUMANN()) {
					paramExpr = new Expression(0.0);
				}
			}
			if ((role == SpeciesContextSpec.ROLE_BoundaryValueYp)) {
				if (sm.getBoundaryConditionTypeYp().isDIRICHLET()) {
					paramExpr = new Expression(initCondnExpr);
				} else if (sm.getBoundaryConditionTypeYp().isNEUMANN()) {
					paramExpr = new Expression(0.0);
				}
			}
			if ((role == SpeciesContextSpec.ROLE_BoundaryValueZm)) {
				if (sm.getBoundaryConditionTypeZm().isDIRICHLET()) {
					paramExpr = new Expression(initCondnExpr);
				} else if (sm.getBoundaryConditionTypeZm().isNEUMANN()) {
					paramExpr = new Expression(0.0);
				}
			}
			if ((role == SpeciesContextSpec.ROLE_BoundaryValueZp)) {
				if (sm.getBoundaryConditionTypeZp().isDIRICHLET()) {
					paramExpr = new Expression(initCondnExpr);
				} else if (sm.getBoundaryConditionTypeZp().isNEUMANN()) {
					paramExpr = new Expression(0.0);
				}
			}
		}
		
		// create SBML parameter
		org.sbml.libsbml.Parameter param = sbmlModel.createParameter();
		param.setId(TokenMangler.mangleToSName(spContext.getName() + "_" + scsParam.getName()));
		param.setUnits(scsParam.getUnitDefinition().getSymbol());
		// set requiredElements attributes
		RequiredElementsSBasePlugin reqplugin = (RequiredElementsSBasePlugin)param.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
		reqplugin.setMathOverridden(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		reqplugin.setCoreHasAlternateMath(true);
		
		if (paramExpr.isNumeric()) {
			param.setValue(paramExpr.evaluateConstant());
			param.setConstant(true);
		} else {
			// we need to create a parameter and a rule for the non-numeric expr of diffParam
			param.setValue(0.0);
			param.setConstant(false);
			// now add assignment rule in SBML for the diff param
			ASTNode assgnRuleMathNode = getFormulaFromExpression(paramExpr);
			AssignmentRule assgnRule = sbmlModel.createAssignmentRule();
			assgnRule.setVariable(param.getId());
			assgnRule.setMath(assgnRuleMathNode);
		}
		return param;
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Unable to interpret parameter '" + scsParam.getName() + "' of species : " + spContext.getName());
	}
}

/**
 * Add unit definitions to the model.
 */
protected void addUnitDefinitions() {
	// Define molecule - SUBSTANCE
	UnitDefinition unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(sbmlExportSpec.getSubstanceUnits(), sbmlLevel, sbmlVersion);
	unitDefn.setId(SBMLUnitTranslator.SUBSTANCE);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um3 - VOLUME
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(sbmlExportSpec.getVolumeUnits(), sbmlLevel, sbmlVersion);
	unitDefn.setId(SBMLUnitTranslator.VOLUME);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um2 - AREA
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(sbmlExportSpec.getAreaUnits(), sbmlLevel, sbmlVersion);
	unitDefn.setId(SBMLUnitTranslator.AREA);
	sbmlModel.addUnitDefinition(unitDefn);

	// Redefine molecules as 'item' 
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(VCUnitDefinition.UNIT_molecules, sbmlLevel, sbmlVersion);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define umol.um3.L-1 - VCell (actual units of concentration, but with a multiplication factor.  Value = 1e-15 umol).
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(VCUnitDefinition.UNIT_umol_um3_per_L, sbmlLevel, sbmlVersion);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um3 - VCell
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(VCUnitDefinition.UNIT_um3, sbmlLevel, sbmlVersion);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um2 - VCell
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(VCUnitDefinition.UNIT_um2, sbmlLevel, sbmlVersion);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define KMOLE units : uM.um3/molecules - VCell (required in exported SBML for other tools).
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(VCUnitDefinition.UNIT_uM_um3_per_molecules, sbmlLevel, sbmlVersion);
	sbmlModel.addUnitDefinition(unitDefn);

	// Add units from paramater list in kinetics
	ArrayList<String> unitList = new ArrayList<String>();
	unitList.add(org.vcell.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_molecules.getSymbol()));
	unitList.add(org.vcell.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_umol_um3_per_L.getSymbol()));
	unitList.add(org.vcell.util.TokenMangler.mangleToSName(VCUnitDefinition.UNIT_um2.getSymbol()));
	addKineticAndGlobalParameterUnits(unitList);
}

protected void addEvents() throws ExpressionException {
	BioEvent[] vcBioevents = getSelectedSimContext().getBioEvents();
	
	if (vcBioevents != null) {
		Model vcModel = vcBioModel.getModel();
		for (int i = 0; i < vcBioevents.length; i++) {
			Event sbmlEvent = sbmlModel.createEvent();
			sbmlEvent.setId(vcBioevents[i].getName());
			// create trigger
			Trigger trigger = sbmlEvent.createTrigger();
			// if trigger expression has speciesContext in its list of symbols, need to multiply the unit conversion factor to SBML
			Expression triggerExpr = adjustSpeciesConcFactor(vcBioevents[i].getTriggerExpression());
			ASTNode math = getFormulaFromExpression(triggerExpr);
			trigger.setMath(math);
			
			// create delay
			if (vcBioevents[i].getDelay() != null) {
				Delay delay = sbmlEvent.createDelay();
				// if delay expression has speciesContext in its list of symbols, need to multiply the unit conversion factor to SBML
				Expression delayExpr = adjustSpeciesConcFactor(vcBioevents[i].getDelay().getDurationExpression());
				math = getFormulaFromExpression(delayExpr);
				delay.setMath(math);
			}
			
			// create eventAssignments
			ArrayList<EventAssignment> vcEventAssgns = vcBioevents[i].getEventAssignments();
			for (int j = 0; j < vcEventAssgns.size(); j++) {
				org.sbml.libsbml.EventAssignment sbmlEA = sbmlEvent.createEventAssignment();
				SymbolTableEntry target = vcEventAssgns.get(j).getTarget();
				sbmlEA.setVariable(target.getName());
				Expression eventAssgnExpr = new Expression(vcEventAssgns.get(j).getAssignmentExpression());
				
				// if event assignment expression has speciesContext in its list of symbols, need to multiply the unit conversion factor to SBML
				eventAssgnExpr = adjustSpeciesConcFactor(eventAssgnExpr);

				// if the event assignment variable is a speciesContext, need to convert the entire expression from VC units to SBML units
				if (target instanceof SpeciesContext) {
					SpeciesContext spContext = (SpeciesContext)target;
					SpeciesContextSpec vcSpeciesContextsSpec = getSelectedSimContext().getReactionContext().getSpeciesContextSpec(spContext);
					VCUnitDefinition vcConcUnit = vcSpeciesContextsSpec.getInitialConditionParameter().getUnitDefinition();
					VCUnitDefinition sbmlConcUnits = sbmlExportSpec.getConcentrationUnit(spContext.getStructure().getDimension());
					SBMLUnitParameter sbmlUnitParam = SBMLUtils.getConcUnitFactor("spConcUnit", vcConcUnit, sbmlConcUnits);
					eventAssgnExpr = Expression.mult(eventAssgnExpr, sbmlUnitParam.getExpression()).flatten();
				}
				math = getFormulaFromExpression(eventAssgnExpr);
				sbmlEA.setMath(math);
			}
		}
	}
}

private Expression adjustSpeciesConcFactor(Expression origExpr) throws ExpressionException {
	Expression expr = new Expression(origExpr);
	String[] symbols = expr.getSymbols();
	for (int k = 0; symbols != null && k < symbols.length; k++) {
		SpeciesContext vcSpeciesContext = vcBioModel.getModel().getSpeciesContext(symbols[k]); 
		if (vcSpeciesContext != null) {
			Species species = sbmlModel.getSpecies(vcSpeciesContext.getName());
			SpeciesContextSpec vcSpeciesContextsSpec = getSelectedSimContext().getReactionContext().getSpeciesContextSpec(vcSpeciesContext);
			VCUnitDefinition vcConcUnit = vcSpeciesContextsSpec.getInitialConditionParameter().getUnitDefinition();
			VCUnitDefinition sbmlConcUnits = sbmlExportSpec.getConcentrationUnit(vcSpeciesContext.getStructure().getDimension());
			SBMLUnitParameter sbmlUnitParam = SBMLUtils.getConcUnitFactor("spConcUnit", sbmlConcUnits, vcConcUnit);
			Expression modifiedSpExpr = Expression.mult(new Expression(species.getId()), sbmlUnitParam.getExpression()).flatten();
			expr.substituteInPlace(new Expression(species.getId()), modifiedSpExpr);
		}
	}
	return expr;
}
/**
 * 	getAnnotationElement : 
 *	For a flux reaction, we need to add an annotation specifying the structure, flux carrier, carrier valence and fluxOption. 
 *  For a simple reaction, we need to add a annotation specifying the structure (useful for import)
 *  Using XML JDOM elements, so that it is convenient for libSBML setAnnotation (requires the annotation to be provided as an xml string).
 *
 **/
private Element getAnnotationElement(ReactionStep reactionStep) throws cbit.vcell.xml.XmlParseException {
	  		//"http://www.vcell.org/vcell";
//	Element annotationElement = new Element(XMLTags.SbmlAnnotationTag, "");

	Element sbmlImportRelatedElement = new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
	Element rxnElement = null;
	
	if (reactionStep instanceof FluxReaction) {
		FluxReaction fluxRxn = (FluxReaction)reactionStep;
		// Element for flux reaction. Write out the structure and flux carrier name.
		rxnElement = new Element(XMLTags.FluxStepTag, sbml_vcml_ns);
		rxnElement.setAttribute(XMLTags.StructureAttrTag, fluxRxn.getStructure().getName());
		if (fluxRxn.getFluxCarrier() != null) {
			rxnElement.setAttribute(XMLTags.FluxCarrierAttrTag, TokenMangler.mangleToSName(fluxRxn.getFluxCarrier().getCommonName()));
		}

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
		rxnElement = new org.jdom.Element(cbit.vcell.xml.XMLTags.SimpleReactionTag, sbml_vcml_ns);
		rxnElement.setAttribute(cbit.vcell.xml.XMLTags.StructureAttrTag, simpleRxn.getStructure().getName());
	}

	// Add rate name as an element of annotation - this is especially useful when roundtripping VCell models, when the reaction
	// rate parameters have been renamed by user.
	Element rateElement = new Element(XMLTags.ReactionRateTag, sbml_vcml_ns);
	if (reactionStep.getKinetics() instanceof DistributedKinetics){
		rateElement.setAttribute(XMLTags.NameAttrTag, ((DistributedKinetics)reactionStep.getKinetics()).getReactionRateParameter().getName());
	}else if (reactionStep.getKinetics() instanceof LumpedKinetics){
		rateElement.setAttribute(XMLTags.NameAttrTag, ((LumpedKinetics)reactionStep.getKinetics()).getLumpedReactionRateParameter().getName());
	}else{
		throw new RuntimeException("unexpected kinetic type "+reactionStep.getKinetics().getClass().getName());
	}

	sbmlImportRelatedElement.addContent(rxnElement);
	sbmlImportRelatedElement.addContent(rateElement);
//	annotationElement.addContent(vcellInfoElement);
	
	return sbmlImportRelatedElement;
}


/**
 * 	getInitialConc : 
 */
private boolean getBoundaryCondition(SpeciesContext speciesContext) {

	// Get the simulationContext (application) that matches the 'vcPreferredSimContextName' field.
	cbit.vcell.mapping.SimulationContext simContext = getSelectedSimContext();
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
public static ASTNode getFormulaFromExpression(Expression expression) { 
	// Convert expression into MathML string
	String expMathMLStr = null;

	try {
		expMathMLStr = cbit.vcell.parser.ExpressionMathMLPrinter.getMathML(expression, false);
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

//private SimulationContext getOverriddenSimContext() {
//	return vcOverridenSimContext;
//}

public SimulationContext getSelectedSimContext() {
	return vcSelectedSimContext;
}

private Simulation getSelectedSimulation() {
	if (vcSelectedSimJob == null) {
		return null;
	}
	return vcSelectedSimJob.getSimulation();
}


public String getSBMLFile() {

	
	// SBMLNamespaces of SBML Level 3 Version 1 with Spatial Version 1
	SBMLNamespaces sbmlns = new SBMLNamespaces(3,1,SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX,1);
	sbmlns.addPkgNamespace(SBMLUtils.SBML_SPATIAL_NS_PREFIX,1);
	// SpatialPkgNamespaces spatialns(3,1,1);
	
	/*
    RequiredElementsPkgNamespaces sbmlns = new RequiredElementsPkgNamespaces(3,1,1);
	sbmlns.addPkgNamespace(SBMLUtils.SBML_SPATIAL_NS_PREFIX,1);
	*/

	// create the L3V1 document with spatial package
	SBMLDocument sbmlDocument = new SBMLDocument(sbmlns);
	
    // set 'required' attribute on document for 'spatial' and 'req' packages to 'T'??
	SBMLDocumentPlugin dplugin = (SBMLDocumentPlugin)sbmlDocument.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
	dplugin.setRequired(true);
	dplugin = (SBMLDocumentPlugin)sbmlDocument.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
	dplugin.setRequired(true);

	// If the chosen simulation is not null, the exported model's name should reflect it
	String modelName = vcBioModel.getName() + "_" + getSelectedSimContext().getName();  
	if (getSelectedSimulation() != null) {
		modelName += "_" + getSelectedSimulation().getName();
	}
	sbmlModel = sbmlDocument.createModel(TokenMangler.mangleToSName(modelName));
	sbmlModel.setName(modelName);
	
	// needed?
	sbmlLevel = (int)sbmlModel.getLevel();
	sbmlVersion = (int)sbmlModel.getVersion();

	translateBioModel();

	// include specific vcellInfo annotations
	Element sbmlImportRelatedElement = new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
	Element biomodelElement = new Element(XMLTags.BioModelTag, sbml_vcml_ns);
	biomodelElement.setAttribute(XMLTags.NameAttrTag, org.vcell.util.TokenMangler.mangleToSName(vcBioModel.getName())); 
	if (vcBioModel.getVersion() != null) {
		biomodelElement.setAttribute(XMLTags.KeyValueAttrTag, vcBioModel.getVersion().getVersionKey().toString());
	}
	sbmlImportRelatedElement.addContent(biomodelElement);
	Element simSpecElement = new Element(XMLTags.SimulationSpecTag, sbml_vcml_ns);
	simSpecElement.setAttribute(XMLTags.NameAttrTag, org.vcell.util.TokenMangler.mangleToSName(getSelectedSimContext().getName()));
	if (getSelectedSimContext().getVersion() != null) {
		simSpecElement.setAttribute(XMLTags.KeyValueAttrTag, getSelectedSimContext().getVersion().getVersionKey().toString());
	}
	sbmlImportRelatedElement.addContent(simSpecElement);
	if (getSelectedSimulation() != null) {
		Element simElement = new Element(XMLTags.SimulationTag, sbml_vcml_ns);
		simElement.setAttribute(XMLTags.NameAttrTag, org.vcell.util.TokenMangler.mangleToSName(getSelectedSimulation().getName()));
		if (getSelectedSimulation().getVersion() != null) {
			simElement.setAttribute(XMLTags.KeyValueAttrTag, getSelectedSimulation().getVersion().getVersionKey().toString());
		}
		sbmlImportRelatedElement.addContent(simElement);
	}

	// Get RDF annotation for species from SBMLAnnotationUtils
	sbmlAnnotationUtil.writeAnnotation(vcBioModel, sbmlModel, sbmlImportRelatedElement);
	
	// Now set notes, 
	sbmlAnnotationUtil.writeNotes(vcBioModel, sbmlModel);

	// write sbml document into sbml writer, so that the sbml str can be retrieved
	SBMLWriter sbmlWriter = new SBMLWriter();
	String sbmlStr = sbmlWriter.writeToString(sbmlDocument);
	
	// Error check - use libSBML's document.printError to print to outputstream
	System.out.println("\n\nSBML Export Error Report");
	OStringStream oStrStream = new OStringStream();
	sbmlDocument.printErrors(oStrStream);
	System.out.println(oStrStream.str());

	// cleanup
	sbmlModel.delete();
	sbmlDocument.delete();
	sbmlWriter.delete();	

	return sbmlStr;
}

private void addGeometry() {

	SBasePlugin plugin = sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
    SpatialModelPlugin mplugin = (SpatialModelPlugin)plugin;

    // Creates a geometry object via SpatialModelPlugin object.
	org.sbml.libsbml.Geometry sbmlGeometry = mplugin.getGeometry();
	sbmlGeometry.setCoordinateSystem("Cartesian");

	Geometry vcGeometry = getSelectedSimContext().getGeometry();
	//
	// list of CoordinateComponents : 1 if geometry is 1-d, 2 if geometry is 2-d, 3 if geometry is 3-d
	//
	int dimension = vcGeometry.getDimension();
	Extent vcExtent = vcGeometry.getExtent();
	Origin vcOrigin = vcGeometry.getOrigin();
	// add x coordinate component
	CoordinateComponent xComp = sbmlGeometry.createCoordinateComponent();
	xComp.setSpatialId(ReservedSymbol.X.getName());
	xComp.setComponentType("cartesianX");
	xComp.setSbmlUnit(ReservedSymbol.X.getUnitDefinition().getSymbol());
	xComp.setIndex(0);
	BoundaryMin minX = xComp.createBoundaryMin();
	minX.setSpatialId("Xmin");
	minX.setValue(vcOrigin.getX());
	BoundaryMax maxX = xComp.createBoundaryMax();
	maxX.setSpatialId("Xmax");
	maxX.setValue(vcOrigin.getX() + (vcExtent.getX()));
	createParamForSpatialElement(xComp, xComp.getSpatialId());
	// add y coordinate component
	if (dimension == 2 || dimension == 3) {
		CoordinateComponent yComp = sbmlGeometry.createCoordinateComponent();
		yComp.setSpatialId(ReservedSymbol.Y.getName());
		yComp.setComponentType("cartesianY");
		yComp.setSbmlUnit(ReservedSymbol.Y.getUnitDefinition().getSymbol());
		yComp.setIndex(1);
		BoundaryMin minY = yComp.createBoundaryMin();
		minY.setSpatialId("Ymin");
		minY.setValue(vcOrigin.getY());
		BoundaryMax maxY = yComp.createBoundaryMax();
		maxY.setSpatialId("Ymax");
		maxY.setValue(vcOrigin.getY() + (vcExtent.getY()));
		createParamForSpatialElement(yComp, yComp.getSpatialId());
	}
	// add z coordinate component
	if (dimension == 3) {
		CoordinateComponent zComp = sbmlGeometry.createCoordinateComponent();
		zComp.setSpatialId(ReservedSymbol.Z.getName());
		zComp.setComponentType("cartesianZ");
		zComp.setSbmlUnit(ReservedSymbol.Z.getUnitDefinition().getSymbol());
		zComp.setIndex(2);
		BoundaryMin minZ = zComp.createBoundaryMin();
		minZ.setSpatialId("Zmin");
		minZ.setValue(vcOrigin.getZ());
		BoundaryMax maxZ = zComp.createBoundaryMax();
		maxZ.setSpatialId("Zmax");
		maxZ.setValue(vcOrigin.getZ() + (vcExtent.getZ()));
		createParamForSpatialElement(zComp, zComp.getSpatialId());
	}

	//
	// list of compartmentMappings : VC structureMappings
	//
	GeometryContext vcGeoContext = getSelectedSimContext().getGeometryContext();
	StructureMapping[] vcStrucMappings = vcGeoContext.getStructureMappings();
	for (int i = 0; i < vcStrucMappings.length; i++) {
		StructureMapping vcStructMapping = vcStrucMappings[i];
		String structName = vcStructMapping.getStructure().getName();
		Compartment comp = sbmlModel.getCompartment(TokenMangler.mangleToSName(structName));
		plugin = comp.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		SpatialCompartmentPlugin cplugin = (SpatialCompartmentPlugin)plugin;
		CompartmentMapping compMapping = cplugin.getCompartmentMapping();
		String geomClassName = vcStructMapping.getGeometryClass().getName();
		compMapping.setSpatialId(TokenMangler.mangleToSName(geomClassName + structName));
		compMapping.setCompartment(TokenMangler.mangleToSName(structName));
		compMapping.setDomainType(TokenMangler.mangleToSName(geomClassName));
		try {
			compMapping.setUnitSize(vcStructMapping.getUnitSizeParameter().getConstantValue());
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Unable to create compartment mapping for structureMapping '" + compMapping.getSpatialId() +"' : " + e.getMessage());
		}
	}
	
	//
	// list of domain types : subvolumes and surface classes from VC
	//
	boolean bAnalyticGeom = false;
	boolean bImageGeom = false;
	GeometryClass[] vcGeomClasses = vcGeometry.getGeometryClasses();
	for (int i = 0; i < vcGeomClasses.length; i++) {
	    DomainType domainType = sbmlGeometry.createDomainType();
	    domainType.setSpatialId(vcGeomClasses[i].getName());
	    if (vcGeomClasses[i] instanceof SubVolume) {
	    	if (((SubVolume)vcGeomClasses[i]) instanceof AnalyticSubVolume) {
	    		bAnalyticGeom = true;
	    	} else if (((SubVolume)vcGeomClasses[i]) instanceof ImageSubVolume) {
	    		bImageGeom = true;
	    	}
	    	domainType.setSpatialDimensions(3);
	    } else if (vcGeomClasses[i] instanceof SurfaceClass) {
	    	domainType.setSpatialDimensions(2);
	    }
	}
	
	
	//
	// list of domains, adjacent domains : from VC geometricRegions
	//
	GeometrySurfaceDescription vcGSD = vcGeometry.getGeometrySurfaceDescription();
	if (vcGSD.getRegionImage() == null) {
		try {
			vcGSD.updateAll();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Unable to generate region images for geometry");
		}
	}
	GeometricRegion[] vcGeometricRegions = vcGSD.getGeometricRegions();
	ISize sampleSize = vcGSD.getVolumeSampleSize();
	int numX = sampleSize.getX();
	int numY = sampleSize.getY();
	int numZ = sampleSize.getZ();
	double ox = vcOrigin.getX();
	double oy = vcOrigin.getY();
	double oz = vcOrigin.getZ();
	RegionInfo[] regionInfos = vcGSD.getRegionImage().getRegionInfos();

	for (int i = 0; i < vcGeometricRegions.length; i++) {
		// domains
		Domain domain = sbmlGeometry.createDomain();
		domain.setSpatialId(vcGeometricRegions[i].getName());
		if (vcGeometricRegions[i] instanceof VolumeGeometricRegion) {
			domain.setDomainType(((VolumeGeometricRegion)vcGeometricRegions[i]).getSubVolume().getName());
			domain.setImplicit(false);
			InteriorPoint interiorPt = domain.createInteriorPoint();
			int regionID = ((VolumeGeometricRegion)vcGeometricRegions[i]).getRegionID();
			boolean bFound = false;
			int regInfoIndx = 0;
			for (int j = 0; j < regionInfos.length; j++) {
				regInfoIndx = j;
				if (regionInfos[j].getRegionIndex() == regionID) {
					int volIndx = 0;
					for (int z = 0; z < numZ && !bFound; z++) {
						for (int y = 0; y < numY && !bFound; y++) {
							for (int x = 0; x < numX && !bFound; x++) {
								if (regionInfos[j].isIndexInRegion(volIndx)) {
									bFound = true;
									double unit_z = (numZ>1)?((double)z)/(numZ-1):0.5;
									double coordZ = oz + vcExtent.getZ() * unit_z;
									double unit_y = (numY>1)?((double)y)/(numY-1):0.5;
									double coordY = oy + vcExtent.getY() * unit_y;
									double unit_x = (numX>1)?((double)x)/(numX-1):0.5;
									double coordX = ox + vcExtent.getX() * unit_x;
									interiorPt.setCoord1(coordX);
									interiorPt.setCoord2(coordY);
									interiorPt.setCoord3(coordZ);
								}
								volIndx++;
							}	// end - for x
						}	// end - for y
					}	// end - for z
				}	// end if 
			}	// end for regionInfos
			if (!bFound) {
				throw new RuntimeException("Unable to find interior point for region '" + regionInfos[regInfoIndx].toString());
			}
		} else if (vcGeometricRegions[i] instanceof SurfaceGeometricRegion) {
			SurfaceGeometricRegion vcSurfaceGeomReg = (SurfaceGeometricRegion)vcGeometricRegions[i];
			GeometricRegion geomRegion0 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[0];
			GeometricRegion geomRegion1 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[1];
			SurfaceClass surfaceClass = vcGSD.getSurfaceClass(((VolumeGeometricRegion)geomRegion0).getSubVolume(), ((VolumeGeometricRegion)geomRegion1).getSubVolume());
			domain.setDomainType(surfaceClass.getName());
			domain.setImplicit(true);

			// adjacent domains : 2 adjacent domain objects for each surfaceClass in VC.
			// adjacent domain 1
			GeometricRegion adjGeomRegion0 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[0];
			GeometricRegion adjGeomRegion1 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[1];
			AdjacentDomains adjDomain = sbmlGeometry.createAdjacentDomains();
			adjDomain.setSpatialId(TokenMangler.mangleToSName(vcSurfaceGeomReg.getName()+"_"+adjGeomRegion0.getName()));
			adjDomain.setDomain1(vcSurfaceGeomReg.getName());
			adjDomain.setDomain2(adjGeomRegion0.getName());
			// adj domain 2
			adjDomain = sbmlGeometry.createAdjacentDomains();
			adjDomain.setSpatialId(TokenMangler.mangleToSName(vcSurfaceGeomReg.getName()+"_"+adjGeomRegion1.getName()));
			adjDomain.setDomain1(vcSurfaceGeomReg.getName());
			adjDomain.setDomain2(adjGeomRegion1.getName());
		}
//		if (geomDefn != null && geomDefn instanceof AnalyticGeometry) 
//			domain.setShapeId(geomDefn.getSpatialId());
	}
	
	AnalyticGeometry sbmlAnalyticGeom = null;
	SampledFieldGeometry sbmlSFGeom = null;
	// If subvolumes in geometry are analytic, should GeometryDefinition be analyticGeometry? What if VC geom has
	// both image and analytic subvolumes?? == not handled in SBML at this time.
	if (bAnalyticGeom && !bImageGeom) {
		sbmlAnalyticGeom = sbmlGeometry.createAnalyticGeometry();
		sbmlAnalyticGeom.setSpatialId(TokenMangler.mangleToSName(vcGeometry.getName()));
	} else if (bImageGeom && !bAnalyticGeom){
		// assuming image based geometry if not analytic geometry
		sbmlSFGeom = sbmlGeometry.createSampledFieldGeometry();
		sbmlSFGeom.setSpatialId(TokenMangler.mangleToSName(vcGeometry.getName()));
	} else if (bAnalyticGeom && bImageGeom) {
		throw new RuntimeException("Export to SBML of a combination of Image-based and Analytic geometries is not supported yet.");
	} else if (!bAnalyticGeom && !bImageGeom) {
		throw new RuntimeException("Unknown geometry type.");
	}
	
	//
	// list of analytic volumes to analyticGeometry (geometricDefinition) : 
	// 
	for (int i = 0; i < vcGeomClasses.length; i++) {
		if (vcGeomClasses[i] instanceof AnalyticSubVolume) {
			// add analytiVols to sbmlAnalyticGeometry
			if (sbmlAnalyticGeom != null) {
				AnalyticVolume analyticVol = sbmlAnalyticGeom.createAnalyticVolume();
				analyticVol.setSpatialId(vcGeomClasses[i].getName());
				analyticVol.setDomainType(vcGeomClasses[i].getName());
				analyticVol.setFunctionType("layered");
				analyticVol.setOrdinal(i);
				Expression expr = ((AnalyticSubVolume)vcGeomClasses[i]).getExpression();
				try {
					String mathMLStr = ExpressionMathMLPrinter.getMathML(expr, true);
					ASTNode mathMLNode = libsbml.readMathMLFromString(mathMLStr);
					analyticVol.setMath(mathMLNode);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Error converting VC subvolume expression to mathML" + e.getMessage());
				}
			} else {
				throw new RuntimeException("SBML AnalyticGeometry is null.");
			}
		} else if (vcGeomClasses[i] instanceof ImageSubVolume) {
			// add sampledVols to sbmlSFGeometry
			if (sbmlSFGeom != null) {
				SampledVolume sampledVol = sbmlSFGeom.createSampledVolume();
				sampledVol.setSpatialId(vcGeomClasses[i].getName());
				sampledVol.setDomainType(vcGeomClasses[i].getName());
				sampledVol.setSampledValue(((ImageSubVolume)vcGeomClasses[i]).getPixelValue());
			} else {
				throw new RuntimeException("SBML SampledFieldGeometry is null.");
			}
		}
	}
	
	if (sbmlSFGeom != null) {
		// add sampledField to sampledFieldGeometry
		SampledField sampledField = sbmlSFGeom.createSampledField();
		VCImage vcImage = vcGeometry.getGeometrySpec().getImage();
		sampledField.setSpatialId(vcImage.getName());
		sampledField.setNumSamples1(vcImage.getNumX());
		sampledField.setNumSamples2(vcImage.getNumY());
		sampledField.setNumSamples3(vcImage.getNumZ());
		sampledField.setDataType("integer");
		sampledField.setInterpolationType("0-order interpolation");
		// add image from vcGeometrySpec to sampledField.
		try {
			ImageData imageData = sampledField.createImageData();
			byte[] imagePixelsBytes = vcImage.getPixelsCompressed();
			int[] imagePixelsInt = new int[imagePixelsBytes.length];
			for (int i = 0; i < imagePixelsBytes.length; i++) {
				imagePixelsInt[i] = (int)imagePixelsBytes[i];
			}
			imageData.setSamples(imagePixelsInt, imagePixelsInt.length);
		} catch (ImageException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Unable to export image from VCell to SBML : " + e.getMessage());
		}
	}
}

/**
 * This method is used to create a parameter for geometry elements that have references in SBML core. For example, 
 * CoordinateComponent: for vcell there will be 3 coordinate components (x,y,z) for 3-d application (2 for 2-d and so forth).
 * Create a parameter for each coordinate component. Whenever x/y/z is used in any expression in the model (init condn, 
 * boundary condition, etc.) this parameter is to be used.  
 * @param sBaseElement
 */
private void createParamForSpatialElement(SBase sBaseElement, String spatialId) {
	org.sbml.libsbml.Parameter p = sbmlModel.createParameter();
	if (sBaseElement instanceof CoordinateComponent) {
		CoordinateComponent cc = (CoordinateComponent)sBaseElement;
		// coordComponent with index = 1 represents X-axis, hence set param id as 'x'
		if (cc.getIndex() == 0) {
			p.setId("x");
		} else if (cc.getIndex() == 1) {
			p.setId("y");
		} else if (cc.getIndex() == 2) {
			p.setId("z");
		}
	} else {
		p.setId(spatialId);
	}
	p.setValue(0.0);
	// since p is a parameter from 'spatial' package, need to set the
	// requiredElements attributes on parameter 
	SBasePlugin plugin = p.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
	RequiredElementsSBasePlugin reqPlugin = (RequiredElementsSBasePlugin)plugin;
	reqPlugin.setMathOverridden(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
	reqPlugin.setCoreHasAlternateMath(false);
	// now need to create a SpatialSymbolReference for parameter
	plugin = p.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
	SpatialParameterPlugin spPlugin = (SpatialParameterPlugin)plugin;
	SpatialSymbolReference spSymRef = spPlugin.getSpatialSymbolReference();
	spSymRef.setSpatialId(spatialId);
	spSymRef.setType(sBaseElement.getElementName());
	
}

public void setSelectedSimContext(SimulationContext newVcSelectedSimContext) {
	vcSelectedSimContext = newVcSelectedSimContext;
}

public void setSelectedSimulationJob(SimulationJob newVcSelectedSimJob) {
	vcSelectedSimJob = newVcSelectedSimJob;
}

/**
 * VC_to_SB_Translator constructor comment.
 */
public void translateBioModel() {
	// 'Parse' the Virtual cell model into an SBML model
	addUnitDefinitions();
	// Add features/compartments
	addCompartments();
	// add geometry, if present
	addGeometry();
	// Add species/speciesContexts
	addSpecies(); 
	// Add Parameters
	try {
		addParameters();
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	// Add Reactions
	addReactions();
	// Add Events
	try {
		addEvents();
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	
}

}