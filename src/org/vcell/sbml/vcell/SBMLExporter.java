/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml.vcell;
import static org.vcell.sbml.SpatialAdapter.SPATIAL_X;
import static org.vcell.sbml.SpatialAdapter.SPATIAL_Y;
import static org.vcell.sbml.SpatialAdapter.SPATIAL_Z;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbml.libsbml.ASTNode;
import org.sbml.libsbml.AdjacentDomains;
import org.sbml.libsbml.AnalyticGeometry;
import org.sbml.libsbml.AnalyticVolume;
import org.sbml.libsbml.AssignmentRule;
import org.sbml.libsbml.Boundary;
import org.sbml.libsbml.CSGeometry;
import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.CompartmentMapping;
import org.sbml.libsbml.CoordinateComponent;
import org.sbml.libsbml.Delay;
import org.sbml.libsbml.Domain;
import org.sbml.libsbml.DomainType;
import org.sbml.libsbml.Event;
import org.sbml.libsbml.InitialAssignment;
import org.sbml.libsbml.InteriorPoint;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLNamespaces;
//import org.sbml.libsbml.SBMLValidator;
import org.sbml.libsbml.SBMLWriter;
import org.sbml.libsbml.SBase;
import org.sbml.libsbml.SampledField;
import org.sbml.libsbml.SampledFieldGeometry;
import org.sbml.libsbml.SampledVolume;
import org.sbml.libsbml.SimpleSpeciesReference;
import org.sbml.libsbml.SpatialCompartmentPlugin;
import org.sbml.libsbml.SpatialModelPlugin;
import org.sbml.libsbml.SpatialParameterPlugin;
import org.sbml.libsbml.SpatialReactionPlugin;
import org.sbml.libsbml.SpatialSymbolReference;
import org.sbml.libsbml.SpeciesReference;
import org.sbml.libsbml.Trigger;
import org.sbml.libsbml.UnitDefinition;
import org.sbml.libsbml.libsbml;
import org.sbml.libsbml.libsbmlConstants;
import org.vcell.sbml.KineticsAdapter;
import org.vcell.sbml.LibSBMLConstantsAdapter;
import org.vcell.sbml.SBMLHelper;
import org.vcell.sbml.SBMLHelper.CompressionKind;
import org.vcell.sbml.SBMLHelper.DataKind;
import org.vcell.sbml.SBMLHelper.InterpolationKind;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SpatialAdapter;
import org.vcell.sbml.vcell.SpeciesContextSpecSBMLBridge.Coordinates;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.Pair;
import org.vcell.util.ProgrammingException;
import org.vcell.util.TokenMangler;
import org.vcell.util.VCAssert;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.RayCaster;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLPrinter;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.resource.NativeLib;
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
public class SBMLExporter extends LibSBMLClient {
	private int sbmlLevel = 2;
	private int sbmlVersion = 3;
	private org.sbml.libsbml.Model sbmlModel = null;
	private cbit.vcell.biomodel.BioModel vcBioModel = null;
	private boolean bSpatial = false;

	private SimulationContext vcSelectedSimContext = null;
	private SimulationJob vcSelectedSimJob = null;
	
	Map<Pair <String, String>, String> l2gMap = new HashMap<>();	// local to global translation map, used for reaction parameters
	
	// used for exporting vcell-related annotations.
	Namespace sbml_vcml_ns = Namespace.getNamespace(XMLTags.VCELL_NS_PREFIX, SBMLUtils.SBML_VCELL_NS);

	// SBMLAnnotationUtil to get the SBML-related annotations, notes, free-text annotations from a Biomodel VCMetaData
	private SBMLAnnotationUtil sbmlAnnotationUtil = null;

	private java.util.Hashtable<String, String> globalParamNamesHash = new java.util.Hashtable<String, String>();
	
	private SBMLExportSpec sbmlExportSpec = null;
	
	private static Logger lg = Logger.getLogger(SBMLExporter.class);
	
	public class SBMLExportSpec {
		private VCUnitDefinition substanceUnits = null;
		private VCUnitDefinition volUnits = null;
		private VCUnitDefinition areaUnits = null;
		private VCUnitDefinition lengthUnits = null;
		private VCUnitDefinition timeUnits = null;
		private Collection<VCUnitDefinition> availableUnits = new ArrayList<VCUnitDefinition>();
		
		public SBMLExportSpec(VCUnitDefinition argSunits, VCUnitDefinition argVUnits, VCUnitDefinition argAUnits, VCUnitDefinition argLUnits, VCUnitDefinition argTUnits) {
			ModelUnitSystem vcModelUnitSystem = vcBioModel.getModel().getUnitSystem();
			if (!argSunits.isCompatible(vcModelUnitSystem.getMembraneSubstanceUnit()) && !argSunits.isCompatible(vcModelUnitSystem.getVolumeSubstanceUnit())) {
				throw new RuntimeException("Substance units not compatible with molecules or moles");
			}
			if (!argVUnits.isCompatible(vcModelUnitSystem.getVolumeUnit())) {
				throw new RuntimeException("Volume units not compatible with m3 (eg., um3 or litre)");
			}
			if (!argAUnits.isCompatible(vcModelUnitSystem.getAreaUnit())) {
				throw new RuntimeException("Area units not compatible with m2");
			}
			if (!argLUnits.isCompatible(vcModelUnitSystem.getLengthUnit())) {
				throw new RuntimeException("Length units not compatible with m");
			}
			if (!argTUnits.isCompatible(vcModelUnitSystem.getTimeUnit())) {
				throw new RuntimeException("Time units not compatible with sec");
			}
			this.substanceUnits = argSunits;
			this.volUnits = argVUnits;
			this.areaUnits = argAUnits;
			this.lengthUnits = argLUnits;
			this.timeUnits = argTUnits;
			
			//add these in order most likely to succeed
			availableUnits.add(volUnits);
			availableUnits.add(areaUnits);
			availableUnits.add(lengthUnits);
			availableUnits.add(substanceUnits);
			availableUnits.add(timeUnits);
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
				throw new RuntimeException("Unsupported dimension " + dimension + " of compartment; unable to compute concentration units");
			}
		}
		/**
		 * find unit that has same dimensionality as existing unit
		 * @param unitToMatch 
		 * @return matching unit
		 * @throws ProgrammingException if not match
		 */
		public VCUnitDefinition getMatchingUnit(VCUnitDefinition unitToMatch) {
			for (VCUnitDefinition candidate : availableUnits) {
				if (unitToMatch.isCompatible(candidate)) {
					return candidate;
				}
			}
			throw new ProgrammingException("no unit match for " + unitToMatch.getSymbol());
		}
		
	}
	
	/**
	 * set log4j level (for unit tests)
	 * @param lvl
	 */
	public void setLogLevel(Level lvl) {
		lg.setLevel(lvl);
	}
	
	public static class VCellSBMLDoc implements AutoCloseable {
		public final SBMLDocument document;
		public final org.sbml.libsbml.Model model; 
		public final String xmlString;

		public VCellSBMLDoc(SBMLDocument document, org.sbml.libsbml.Model model, String xmlString) {
			super();
			this.document = document;
			this.model = model;
			this.xmlString = xmlString;
		}


		/**
		 * cleanup C++ implementation
		 */
		@Override
		public void close() throws Exception {
			//document.delete();
		}
	}

	static
	{
		NativeLib.SBML.load( );
	}

	/**
	 * SBMLExporter constructor comment.
	 */
	public SBMLExporter(BioModel argBioModel, int argSbmlLevel, int argSbmlVersion, boolean isSpatial) {
		super();
		this.vcBioModel = argBioModel;
		sbmlLevel = argSbmlLevel;
		sbmlVersion = argSbmlVersion;
		bSpatial = isSpatial;
		if (vcBioModel != null) {
			sbmlAnnotationUtil = new SBMLAnnotationUtil(vcBioModel.getVCMetaData(), vcBioModel, SBMLHelper.getNamespaceFromLevelAndVersion(sbmlLevel, sbmlVersion));
		}
		ModelUnitSystem vcModelUnitSystem = vcBioModel.getModel().getUnitSystem();
		this.sbmlExportSpec = new SBMLExportSpec(vcModelUnitSystem.getLumpedReactionSubstanceUnit(), vcModelUnitSystem.getVolumeUnit(), vcModelUnitSystem.getAreaUnit(), vcModelUnitSystem.getLengthUnit(), vcModelUnitSystem.getTimeUnit());
	}
	
	public SBMLExporter(SimulationContext ctx, int argSbmlLevel, int argSbmlVersion, boolean isSpatial) {
		if (isSpatial && !validSpatial(ctx)) {
			throw new IllegalArgumentException(ctx.getName() + " not valid for spatialSBML export");
		}
		this.vcBioModel = ctx.getBioModel(); 
		vcSelectedSimContext = ctx;
		sbmlLevel = argSbmlLevel;
		sbmlVersion = argSbmlVersion;
		bSpatial = isSpatial;
		sbmlAnnotationUtil = new SBMLAnnotationUtil(vcBioModel.getVCMetaData(), vcBioModel, SBMLHelper.getNamespaceFromLevelAndVersion(sbmlLevel, sbmlVersion));
		ModelUnitSystem vcModelUnitSystem = vcBioModel.getModel().getUnitSystem();
		this.sbmlExportSpec = new SBMLExportSpec(vcModelUnitSystem.getLumpedReactionSubstanceUnit(), vcModelUnitSystem.getVolumeUnit(), vcModelUnitSystem.getAreaUnit(), vcModelUnitSystem.getLengthUnit(), vcModelUnitSystem.getTimeUnit());
	}
	
	/**
	 * @param ctx
	 * @return true if ctx spatial and not stochastic
	 */
	public static boolean validSpatial(SimulationContext ctx) {
		boolean isSpatial = ctx.getGeometry().getDimension() > 0;
		return isSpatial && !ctx.isStoch( );
	}
	

/**
 * addCompartments comment.
 */
protected void addCompartments() {
	Model vcModel = vcBioModel.getModel();
	cbit.vcell.model.Structure[] vcStructures = vcModel.getStructures();
	for (int i = 0; i < vcStructures.length; i++){
		org.sbml.libsbml.Compartment sbmlCompartment = sbmlModel.createCompartment();
		sbmlCompartment.setId(TokenMangler.mangleToSName(vcStructures[i].getName()));
		sbmlCompartment.setName(vcStructures[i].getName());
		VCUnitDefinition sbmlSizeUnit = null;
		StructureTopology structTopology = getSelectedSimContext().getModel().getStructureTopology();
		Structure parentStructure = structTopology.getParentStructure(vcStructures[i]);
		if (vcStructures[i] instanceof Feature) {
			sbmlCompartment.setSpatialDimensions(3);
			String outside = null;
			if (parentStructure!= null) {
				outside = TokenMangler.mangleToSName(parentStructure.getName());
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
			Feature outsideFeature = structTopology.getOutsideFeature(vcMembrane);
			if (outsideFeature != null) {
				sbmlCompartment.setOutside(TokenMangler.mangleToSName(outsideFeature.getName()));
				sbmlSizeUnit = sbmlExportSpec.getAreaUnits();
				sbmlCompartment.setUnits(org.vcell.util.TokenMangler.mangleToSName(sbmlSizeUnit.getSymbol()));
			} else if (lg.isEnabledFor(Level.WARN)) {
				lg.warn(this.sbmlModel.getName() + " membrame "  + vcMembrane.getName()  + " has not outside feature");
				
			}
		}
		sbmlCompartment.setConstant(true);

		StructureMapping vcStructMapping = getSelectedSimContext().getGeometryContext().getStructureMapping(vcStructures[i]);
		try {
			// The unit for 3D compartment size in VCell is um3, we are going to write it out in um3 in the SBML document.
			// Hence multiplying the size expression with the conversion factor between VC and SBML units for the compartment size. 
			
			Expression sizeExpr = null;
			VCUnitDefinition vcSizeUnit = vcStructMapping.getSizeParameter().getUnitDefinition();
			if (vcStructMapping.getSizeParameter().getExpression() != null) {
				double factor = 1.0;
				//factor = vcSizeUnit.convertTo(factor, sbmlExportSpec.getVolumeUnits());
				factor = vcSizeUnit.convertTo(factor, sbmlExportSpec.getMatchingUnit(vcSizeUnit));
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
		if (parentStructure != null) {
			sbmlImportRelatedElement = new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
			Element compartmentElement = new Element(XMLTags.OutsideCompartmentTag, sbml_vcml_ns);
			compartmentElement.setAttribute(XMLTags.NameAttrTag, TokenMangler.mangleToSName(parentStructure.getName()));
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
 * @throws SbmlException 
 */
private void addKineticAndGlobalParameterUnits(ArrayList<String> unitsList) throws SbmlException {

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
	Model vcModel = vcBioModel.getModel();
	ModelParameter[] globalParams = vcModel.getModelParameters();
	for (int i = 0; i < globalParams.length; i++) {
		paramsVector.addElement(globalParams[i]);
	}
	// Add reaction kinetic parameters
	ReactionStep[] vcReactions = vcModel.getReactionSteps();
	for (int i = 0; i < vcReactions.length; i++) {
		Kinetics rxnKinetics = vcReactions[i].getKinetics();
		Parameter[] kineticParams = rxnKinetics.getKineticsParameters();
		for (int j = 0; j < kineticParams.length; j++) {
			paramsVector.addElement(kineticParams[j]);
		}
	}

	ModelUnitSystem vcModelUnitSystem = vcModel.getUnitSystem();
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
			UnitDefinition newUnitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(paramUnitDefn, sbmlLevel, sbmlVersion, vcModelUnitSystem);
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
	Model vcModel = getSelectedSimContext().getModel();
	// add VCell global parameters to the SBML listofParameters
	ModelParameter[] vcGlobalParams = vcModel.getModelParameters();  
	if (vcGlobalParams != null) {
	for (ModelParameter vcParam : vcGlobalParams) {
		org.sbml.libsbml.Parameter sbmlParam = sbmlModel.createParameter();
		sbmlParam.setId(vcParam.getName());
		sbmlParam.setConstant(vcParam.isConstant());
		
		Expression paramExpr = new Expression(vcParam.getExpression());
		boolean bParamIsNumeric = true;
		if (paramExpr.isNumeric()) {
			// For a VCell global param, if it is numeric, it has a constant value and is not defined by a rule, hence set Constant = true.
			sbmlParam.setValue(paramExpr.evaluateConstant());
			// the expression for modelParam might be numeric, but modelParam could have a rate rule, if so, set constant attribute to 'false'
			if (getSelectedSimContext().getRateRule(vcParam) != null) {
				bParamIsNumeric = false;
			}
		} else {
			// non-numeric VCell global parameter will be defined by a (assignment) rule, hence mark Constant = false.
			bParamIsNumeric = false;
			// add assignment rule for param
			ASTNode paramFormulaNode = getFormulaFromExpression(paramExpr);
			AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
			sbmlParamAssignmentRule.setVariable(vcParam.getName());
			sbmlParamAssignmentRule.setMath(paramFormulaNode);
		}
		sbmlParam.setConstant(bParamIsNumeric);
		VCUnitDefinition vcParamUnit = vcParam.getUnitDefinition();
		if (!vcParamUnit.isTBD()) {
			sbmlParam.setUnits(TokenMangler.mangleToSName(vcParamUnit.getSymbol()));
		}
	}
	}
}


/**
 * addReactions comment.
 * @throws SbmlException 
 */
protected void addReactions() throws SbmlException {

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

	l2gMap.clear();
	ReactionSpec[] vcReactionSpecs = getSelectedSimContext().getReactionContext().getReactionSpecs();
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
		KineticsAdapter kineticsAdapter = KineticsAdapter.create(vcRxnKinetics);

		try {
			// Convert expression from kinetics rate parameter into MathML and use libSBMl utilities to convert it to formula
			// (instead of directly using rate parameter's expression infix) to maintain integrity of formula :
			// for example logical and inequalities are not handled gracefully by libSBMl if expression.infix is used.
			Expression correctedRateExpr = kineticsAdapter.getExpression(); 
			
			// Add parameters, if any, to the kineticLaw
			Kinetics.KineticsParameter[] vcKineticsParams = vcRxnKinetics.getKineticsParameters();

			// In the first pass thro' the kinetic params, store the non-numeric param names and expressions in arrays
			String[] kinParamNames = new String[vcKineticsParams.length];
			Expression[] kinParamExprs = new Expression[vcKineticsParams.length];
			for (int j = 0; j < vcKineticsParams.length; j++){
				if ( true) {
				//if ( (vcKineticsParams[j].getRole() != Kinetics.ROLE_ReactionRate) && (vcKineticsParams[j].getRole() != Kinetics.ROLE_LumpedReactionRate) ) {
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
				final KineticsParameter vcKParam = vcKineticsParams[j];
				//if ( (vcKParam.getRole() != Kinetics.ROLE_ReactionRate) && (vcKParam.getRole() != Kinetics.ROLE_LumpedReactionRate) ) {
				if ( true ) { 
					// if expression of kinetic param evaluates to a double, the parameter value is set
					if ( (vcKParam.getRole() == Kinetics.ROLE_CurrentDensity && (!vcKParam.getExpression().isZero())) || 
							(vcKParam.getRole() == Kinetics.ROLE_LumpedCurrent && (!vcKParam.getExpression().isZero())) ) {
						throw new RuntimeException("Electric current not handled by SBML export; failed to export reaction \"" + vcReactionStep.getName() + "\" at this time");
					}
					if (vcKParam.getExpression().isNumeric()) {		// NUMERIC KINETIC PARAM
						// check if it is used in other parameters that have expressions,
						boolean bAddedParam = false;
						String origParamName = vcKParam.getName();
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
										validate( sbmlKinParam.setId(newParamName) );
										validate( sbmlKinParam.setValue(vcKParam.getConstantValue()) );
										final boolean constValue = vcKParam.isConstant();
										validate( sbmlKinParam.setConstant(true) );
										// Set SBML units for sbmlParam using VC units from vcParam  
										if (!vcKParam.getUnitDefinition().isTBD()) {
											sbmlKinParam.setUnits(TokenMangler.mangleToSName(vcKParam.getUnitDefinition().getSymbol()));
										}
										Pair<String, String> origParam = new Pair<String, String> (rxnName, origParamName);
										l2gMap.put(origParam, newParamName);
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
							validate( sbmlKinParam.setId(origParamName) );
							validate( sbmlKinParam.setValue(vcKParam.getConstantValue()) );
							System.out.println ("tis constant " + sbmlKinParam.getConstant());
							//validate( sbmlKinParam.setConstant(true) ) ;
							// Set SBML units for sbmlParam using VC units from vcParam  
							if (!vcKParam.getUnitDefinition().isTBD()) {
								validate( sbmlKinParam.setUnits(TokenMangler.mangleToSName(vcKParam.getUnitDefinition().getSymbol())) );
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
					Pair<String, String> origParam = new Pair<String, String> (rxnName, paramName);
					l2gMap.put(origParam, paramName);	// keeps its name but becomes a global (?)
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
		//sbmlReaction.setKineticLaw(sbmlKLaw);
		
		// Add reactants, products, modifiers
		// Simple reactions have catalysts, fluxes have 'flux' 
		cbit.vcell.model.ReactionParticipant[] rxnParticipants = vcReactionStep.getReactionParticipants();
		for (ReactionParticipant rxnParticpant:rxnParticipants) { 
			SimpleSpeciesReference ssr = null;
			SpeciesReference sr = null;
			if (rxnParticpant instanceof cbit.vcell.model.Reactant) {
				ssr = sr = sbmlReaction.createReactant();
			} else if (rxnParticpant instanceof cbit.vcell.model.Product) {
				ssr = sr  = sbmlReaction.createProduct();
			} 
			if (rxnParticpant instanceof cbit.vcell.model.Catalyst) {
				ssr = sbmlReaction.createModifier();
			}
			if (ssr != null) {
				ssr.setSpecies(rxnParticpant.getSpeciesContext().getName());
			}
			if (sr != null) {
				sr.setStoichiometry(Double.parseDouble(Integer.toString(rxnParticpant.getStoichiometry())));
				String modelUniqueName = vcReactionStep.getName() + '-'  + rxnParticpant.getName();
				sr.setId(modelUniqueName);
				sr.setConstant(true); //SBML-REVIEW
				//int rcode = sr.appendNotes("<
				try {
					SBMLHelper.addNote(sr, "VCELL guess: how do we know if reaction is constant?");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		sbmlReaction.setFast(vcReactionSpecs[i].isFast());
		
		// this attribute is mandatory for L3, optional for L2. So explicitly setting value.
		sbmlReaction.setReversible(true);
		
		if (bSpatial) {
			// set compartment for reaction if spatial
			sbmlReaction.setCompartment(vcReactionStep.getStructure().getName());
			//CORE  HAS ALT MATH true
	
			// set the "isLocal" attribute = true (in 'spatial' namespace) for each species
			SpatialReactionPlugin srplugin = SBMLHelper.checkedPlugin(SpatialReactionPlugin.class,sbmlReaction,SBMLUtils.SBML_SPATIAL_NS_PREFIX);
			srplugin.setIsLocal(kineticsAdapter.isLocal());
		}

		// delete used objects
		sbmlKLaw.delete();
		sbmlKLaw.delete();
		sbmlReaction.delete();
	}
}


/**
 * addSpecies comment.
 */
protected void addSpecies() {
	Model vcModel = vcBioModel.getModel();
	SpeciesContext[] vcSpeciesContexts = vcModel.getSpeciesContexts();
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
		try {
			sbmlSpecies.setInitialConcentration(vcSpeciesContextsSpec.getInitialConditionParameter().getExpression().evaluateConstant());
		} catch (cbit.vcell.parser.ExpressionException e) {
			// If it is in the catch block, it means that the initial concentration of the species was not a double, but an expression, probably.
			// Check if the expression for the species is not null. If exporting to L2V1, and species is 'fixed', and if expr is not in terms of
			// x, y, z, or other species then create an assignment rule for species concentration; else throw exception.
			// If exporting to L2V3, if species concentration is not an expr with x, y, z or other species, add as InitialAssignment, else complain.
			if (vcSpeciesContextsSpec.getInitialConditionParameter().getExpression() != null) {
					Expression initConcExpr = vcSpeciesContextsSpec.getInitialConditionParameter().getExpression();
					if ((sbmlLevel == 2 && sbmlVersion >= 3) || (sbmlLevel > 2)) {
						// L2V3 and above - add expression as init assignment
						ASTNode initAssgnMathNode = getFormulaFromExpression(initConcExpr);
						InitialAssignment initAssignment = sbmlModel.createInitialAssignment();
						initAssignment.setSymbol(vcSpeciesContexts[i].getName());
						initAssignment.setMath(initAssgnMathNode);
					} else { 	// L2V1 (or L1V2 also??)
						// L2V1 (and L1V2?) and species is 'fixed' (constant), and not fn of x,y,z, other sp, add expr as assgn rule 
						ASTNode assgnRuleMathNode = getFormulaFromExpression(initConcExpr);
						AssignmentRule assgnRule = sbmlModel.createAssignmentRule();
						assgnRule.setVariable(vcSpeciesContexts[i].getName());
						assgnRule.setMath(assgnRuleMathNode);
					}
			}
		}

		// Get (and set) the boundary condition value
		boolean bBoundaryCondition = getBoundaryCondition(vcSpeciesContexts[i]);
		sbmlSpecies.setBoundaryCondition(bBoundaryCondition); 
		
		// mandatory for L3, optional for L2
		sbmlSpecies.setConstant(false);

		// set species substance units as 'molecules' - same as defined in the model; irrespective of it is in surface or volume.
		sbmlSpecies.setSubstanceUnits(sbmlExportSpec.getSubstanceUnits().getSymbol());

		// need to do the following if exporting to SBML spatial
		if (bSpatial) {
			
			// Required for setting BoundaryConditions : structureMapping for vcSpeciesContext[i] & sbmlGeometry.coordinateComponents
			StructureMapping sm = getSelectedSimContext().getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure());
			SpatialModelPlugin mplugin = SBMLHelper.checkedPlugin(SpatialModelPlugin.class,sbmlModel,SBMLUtils.SBML_SPATIAL_NS_PREFIX);
			org.sbml.libsbml.Geometry sbmlGeometry = mplugin.getGeometry();
			CoordinateComponent ccX = sbmlGeometry.getCoordinateComponent(vcModel.getX().getName());
			CoordinateComponent ccY = sbmlGeometry.getCoordinateComponent(vcModel.getY().getName());
			CoordinateComponent ccZ = sbmlGeometry.getCoordinateComponent(vcModel.getZ().getName());
			SpeciesContextSpecSBMLBridge.Coordinates coords = new Coordinates(sbmlGeometry, vcModel);
			
			// add diffusion, advection, boundary condition parameters for species, if they exist
			Parameter[] scsParams = vcSpeciesContextsSpec.getParameters();
			if (scsParams != null) {
				for (int j = 0; j < scsParams.length; j++) {
					if (scsParams[j] != null) {
						SpeciesContextSpecParameter scsParam = (SpeciesContextSpecParameter)scsParams[j];
						// no need to add parameters in SBML for init conc or init count
						int role = scsParam.getRole();
						SpeciesContextSpecSBMLBridge bridge = SpeciesContextSpecSBMLBridge.forRole(role);
						VCAssert.assertTrue(bridge.roleCode == role, "logic error");
						if (role == SpeciesContextSpec.ROLE_InitialConcentration || role == SpeciesContextSpec.ROLE_InitialCount) {
							continue;
						}
						// if diffusion is 0 && vel terms are not specified, boundary condition not present
						if (bridge.isDiffusionOrAdvection()) {
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
						bridge.addSBMLChildIf(this, vcSpeciesContexts[i],scsParam,sm,coords);
						
						// if scsParam is a boundary condition and the corresponding coordniateComponent (from SBML) is null, do not create SBML parameter
						// for example, if scsParam is BC_Zm and if coordinateComponent 'ccZ' is null, no SBML parameter should be created for BC_Zm
						/*
						if ( (((role == SpeciesContextSpec.ROLE_BoundaryValueXm) || (role == SpeciesContextSpec.ROLE_BoundaryValueXp)) && (ccX == null)) || 
	  						 (((role == SpeciesContextSpec.ROLE_BoundaryValueYm) || (role == SpeciesContextSpec.ROLE_BoundaryValueYp)) && (ccY == null)) ||
							 (((role == SpeciesContextSpec.ROLE_BoundaryValueZm) || (role == SpeciesContextSpec.ROLE_BoundaryValueZp)) && (ccZ == null)) )
						{
							continue;
						}
						org.sbml.libsbml.Parameter sbmlParam = createSBMLParamFromSpeciesParam(vcSpeciesContexts[i], (SpeciesContextSpecParameter)scsParams[j]);
						if (sbmlParam != null) {
							SpatialParameterPlugin spplugin = SBMLHelper.checkedPlugin(SpatialParameterPlugin.class,sbmlParam,SBMLUtils.SBML_SPATIAL_NS_PREFIX);
							if (role == SpeciesContextSpec.ROLE_DiffusionRate) {
								// set diffusionCoefficient element in SpatialParameterPlugin for param
								DiffusionCoefficient sbmlDiffCoeff = spplugin.createDiffusionCoefficient();
								sbmlDiffCoeff.setVariable(vcSpeciesContexts[i].getName());
								sbmlDiffCoeff.setType(libsbmlConstants.SPATIAL_DIFFUSIONKIND_ISOTROPIC);
								//SBMLUPGRADE sbmlDiffCoeff.setCoordinateIndex(0);
								//SBMLUtils.setCoordinateReference(sbmlDiffCoeff, 0);
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueXm) && (ccX != null)) {
								// set BoundaryCondn Xm element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCXm = spplugin.createBoundaryCondition();
								sbmlBCXm.setVariable(vcSpeciesContexts[i].getName());
								int sType = BoundaryTypeAdapter.fromBoundaryConditionType(sm.getBoundaryConditionTypeXm());
								sbmlBCXm.setType(sType);
								sbmlBCXm.setCoordinateBoundary(ccX.getBoundaryMin().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueXp) && (ccX != null)) {
								// set BoundaryCondn Xp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCXp = spplugin.createBoundaryCondition();
								sbmlBCXp.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCXp.setType(sm.getBoundaryConditionTypeXp().boundaryTypeStringValue());
								sbmlBCXp.setCoordinateBoundary(ccX.getBoundaryMax().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueYm)  && (ccY != null)) {
								// set BoundaryCondn Ym element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCYm = spplugin.createBoundaryCondition();
								sbmlBCYm.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCYm.setType(sm.getBoundaryConditionTypeYm().boundaryTypeStringValue());
								sbmlBCYm.setCoordinateBoundary(ccY.getBoundaryMin().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueYp) && (ccY != null)){
								// set BoundaryCondn Yp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCYp = spplugin.createBoundaryCondition();
								sbmlBCYp.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCYp.setType(sm.getBoundaryConditionTypeYp().boundaryTypeStringValue());
								sbmlBCYp.setCoordinateBoundary(ccY.getBoundaryMax().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueZm)  && (ccZ != null)) {
								// set BoundaryCondn Zm element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCZm = spplugin.createBoundaryCondition();
								sbmlBCZm.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCZm.setType(sm.getBoundaryConditionTypeZm().boundaryTypeStringValue());
								sbmlBCZm.setCoordinateBoundary(ccZ.getBoundaryMin().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueZp)  && (ccZ != null)) {
								// set BoundaryCondn Zp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCZp = spplugin.createBoundaryCondition();
								sbmlBCZp.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCZp.setType(sm.getBoundaryConditionTypeZp().boundaryTypeStringValue());
								sbmlBCZp.setCoordinateBoundary(ccZ.getBoundaryMax().getId());
							} 
							if (role == SpeciesContextSpec.ROLE_VelocityX) {
								// set advectionCoeff X element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffX = spplugin.createAdvectionCoefficient();
								sbmlAdvCoeffX.setVariable(vcSpeciesContexts[i].getName());
								sbmlAdvCoeffX.setCoordinate(SPATIAL_X.sbmlType());
							} 
							if (role == SpeciesContextSpec.ROLE_VelocityY) {
								// set advectionCoeff Y element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffY = spplugin.createAdvectionCoefficient();
								sbmlAdvCoeffY.setVariable(vcSpeciesContexts[i].getName());
								sbmlAdvCoeffY.setCoordinate(SPATIAL_Y.sbmlType());
							} 
							if (role == SpeciesContextSpec.ROLE_VelocityZ) {
								// set advectionCoeff Z element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffZ = spplugin.createAdvectionCoefficient();
								sbmlAdvCoeffZ.setVariable(vcSpeciesContexts[i].getName());
								sbmlAdvCoeffZ.setCoordinate(SPATIAL_Z.sbmlType());
							}
						} 	// if sbmlParam != null
							*/
					}	// if scsParams[j] != null
				}	// end for scsParams
			}	// end scsParams != null
		} // end if (bSpatial)

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
org.sbml.libsbml.Parameter createSBMLParamFromSpeciesParam(SpeciesContext spContext, SpeciesContextSpecParameter scsParam) {
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
		param.setConstant(scsParam.isConstant());
		
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
 * @throws SbmlException 
 */
protected void addUnitDefinitions() throws SbmlException {

	Model vcModel = vcBioModel.getModel();
	ModelUnitSystem vcUnitSystem = vcModel.getUnitSystem();

	// Define molecule - SUBSTANCE
	UnitDefinition unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(sbmlExportSpec.getSubstanceUnits(), sbmlLevel, sbmlVersion, vcUnitSystem);
	unitDefn.setId(SBMLUnitTranslator.SUBSTANCE);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um3 - VOLUME
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(sbmlExportSpec.getVolumeUnits(), sbmlLevel, sbmlVersion, vcUnitSystem);
	unitDefn.setId(SBMLUnitTranslator.VOLUME);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um2 - AREA
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(sbmlExportSpec.getAreaUnits(), sbmlLevel, sbmlVersion, vcUnitSystem);
	unitDefn.setId(SBMLUnitTranslator.AREA);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define um - LENGTH
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(sbmlExportSpec.getLengthUnits(), sbmlLevel, sbmlVersion, vcUnitSystem);
	unitDefn.setId(SBMLUnitTranslator.LENGTH);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define s - TIME
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(sbmlExportSpec.getTimeUnits(), sbmlLevel, sbmlVersion, vcUnitSystem);
	unitDefn.setId(SBMLUnitTranslator.TIME);
	sbmlModel.addUnitDefinition(unitDefn);


	// Redefine molecules as 'item' 
	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(vcUnitSystem.getLumpedReactionSubstanceUnit(), sbmlLevel, sbmlVersion, vcUnitSystem);
	sbmlModel.addUnitDefinition(unitDefn);

	// Define actual units of substance in vol.,(eg. uM.um3 in default VCell units).
//	unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(vcUnitSystem.getVolumeSubstanceUnit(), sbmlLevel, sbmlVersion, vcUnitSystem);
//	sbmlModel.addUnitDefinition(unitDefn);

	// Add units from parameter list in kinetics
	ArrayList<String> unitList = new ArrayList<String>();
	unitList.add(org.vcell.util.TokenMangler.mangleToSName(vcUnitSystem.getMembraneSubstanceUnit().getSymbol()));
	unitList.add(org.vcell.util.TokenMangler.mangleToSName(vcUnitSystem.getVolumeSubstanceUnit().getSymbol()));
	unitList.add(org.vcell.util.TokenMangler.mangleToSName(sbmlExportSpec.getAreaUnits().getSymbol()));
	addKineticAndGlobalParameterUnits(unitList);
}

/** Export events */
protected void addEvents() {
	BioEvent[] vcBioevents = getSelectedSimContext().getBioEvents();
	
	if (vcBioevents != null) {
		for (BioEvent vcEvent : vcBioevents) {
			Event sbmlEvent = sbmlModel.createEvent();
			sbmlEvent.setId(vcEvent.getName());
			// create trigger
			Trigger trigger = sbmlEvent.createTrigger();
			try {
				Expression triggerExpr = vcEvent.generateTriggerExpression();
				ASTNode math = getFormulaFromExpression(triggerExpr);
				trigger.setMath(math);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("failed to generate trigger expression for event "+vcEvent.getName()+": "+e.getMessage());
			}
			
			// create delay
			LocalParameter delayParam = vcEvent.getParameter(BioEventParameterType.TriggerDelay);
			if (delayParam != null && delayParam.getExpression() != null && !delayParam.getExpression().isZero()) {
				Delay delay = sbmlEvent.createDelay();
				Expression delayExpr = delayParam.getExpression();
				ASTNode math = getFormulaFromExpression(delayExpr);
				delay.setMath(math);
				sbmlEvent.setUseValuesFromTriggerTime(vcEvent.getUseValuesFromTriggerTime());
			}
			
			// create eventAssignments
			ArrayList<EventAssignment> vcEventAssgns = vcEvent.getEventAssignments();
			for (int j = 0; j < vcEventAssgns.size(); j++) {
				org.sbml.libsbml.EventAssignment sbmlEA = sbmlEvent.createEventAssignment();
				SymbolTableEntry target = vcEventAssgns.get(j).getTarget();
				sbmlEA.setVariable(target.getName());
				Expression eventAssgnExpr = new Expression(vcEventAssgns.get(j).getAssignmentExpression());
				
				ASTNode eaMath = getFormulaFromExpression(eventAssgnExpr);
				sbmlEA.setMath(eaMath);
			}
		}
	}
}

/** Export rate rules  */
protected void addRateRules()  {
	RateRule[] vcRateRules = getSelectedSimContext().getRateRules();
	
	if (vcRateRules != null) {
		for (RateRule vcRateRule : vcRateRules) {
			// set name
			org.sbml.libsbml.RateRule sbmlRateRule = sbmlModel.createRateRule();
			sbmlRateRule.setId(vcRateRule.getName());
			
			// set rate rule variable
			sbmlRateRule.setVariable(vcRateRule.getRateRuleVar().getName());
			
			// set rate rule math/expression
			Expression rateRuleExpr = vcRateRule.getRateRuleExpression();
			ASTNode math = getFormulaFromExpression(rateRuleExpr);
			sbmlRateRule.setMath(math);
			
			// set unit?? Same as rate rule var (symbolTableEntry) unit? 
		}
	}
}

/**
 * 	getAnnotationElement : 
 *	For a flux reaction, we need to add an annotation specifying the structure, flux carrier, carrier valence and fluxOption. 
 *  For a simple reaction, we need to add a annotation specifying the structure (useful for import)
 *  Using XML JDOM elements, so that it is convenient for libSBML setAnnotation (requires the annotation to be provided as an xml string).
 *
 **/
private Element getAnnotationElement(ReactionStep reactionStep) throws cbit.vcell.xml.XmlParseException {

	Element sbmlImportRelatedElement = new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
	Element rxnElement = null;
	
	if (reactionStep instanceof FluxReaction) {
		FluxReaction fluxRxn = (FluxReaction)reactionStep;
		// Element for flux reaction. Write out the structure and flux carrier name.
		rxnElement = new Element(XMLTags.FluxStepTag, sbml_vcml_ns);
		rxnElement.setAttribute(XMLTags.StructureAttrTag, fluxRxn.getStructure().getName());

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
	return (ASTNode) mathNode.deepCopy();
}

public SimulationContext getSelectedSimContext() {
	return vcSelectedSimContext;
}

private Simulation getSelectedSimulation() {
	if (vcSelectedSimJob == null) {
		return null;
	}
	return vcSelectedSimJob.getSimulation();
}

public String getSBMLFile() throws SbmlException {
	String rval = null;
	VCellSBMLDoc vdoc = convertToSBML();
	rval = vdoc.xmlString;
	return rval;
}

public VCellSBMLDoc convertToSBML() throws SbmlException {

	SBMLDocument sbmlDocument = null;
	if (bSpatial) {
		// SBMLNamespaces of SBML Level 3 Version 1 with Spatial Version 1
		SBMLNamespaces sbmlns = new SBMLNamespaces(3,1,SBMLUtils.SBML_SPATIAL_NS_PREFIX,1);
		//sbmlns.addPkgNamespace(SBMLUtils.SBML_SPATIAL_NS_PREFIX,1);
		
		// create the L3V1 document with spatial package
		sbmlDocument = new SBMLDocument(sbmlns);
		
	    // set 'required' attribute on document for 'spatial' and 'req' packages to 'T'??
		//SBMLDocumentPlugin dplugin = (SBMLDocumentPlugin)sbmlDocument.getPlugin(SBMLUtils.SBML_REQUIREDELEMENTS_NS_PREFIX);
		//dplugin.setRequired(true);
		//dplugin = (SBMLDocumentPlugin)sbmlDocument.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		//dplugin.setRequired(true);
	} else {
		// Create an SBMLDocument and create the sbmlModel from the document, so that other details can be added to it in translateBioModel()
		sbmlDocument = new SBMLDocument(sbmlLevel,sbmlVersion);
	}


		
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
	
	int rcode = sbmlDocument.setPackageRequired(SBMLUtils.SBML_SPATIAL_NS_PREFIX,true);
	LibSBMLConstantsAdapter.checkReturnCode(lg, rcode);
	String sbmlStr = sbmlWriter.writeToString(sbmlDocument);
	/*
	long numErrors = sbmlDocument.validateSBML() ;

	// Error check - use libSBML's document.printError to print to outputstream
	System.out.println("\n\nSBML Export Error Report, " + numErrors + " errors ");
	OStringStream oStrStream = new OStringStream();
	sbmlDocument.printErrors(oStrStream);
	System.out.println(oStrStream.str());

//	// validate generated sbml document
//	SBMLValidator sbmlValidator = new SBMLValidator();
//	long validationFailureCount = sbmlValidator.validate(sbmlDocument);
//	System.out.println("\nSBML Validator : # of validation failures : " + validationFailureCount);
	
	// cleanup
	sbmlWriter.delete();	
	*/

	return new VCellSBMLDoc(sbmlDocument, sbmlModel, sbmlStr);
}

private void addGeometry() throws SbmlException {

    SpatialModelPlugin mplugin = SBMLHelper.checkedPlugin(SpatialModelPlugin.class,
    		sbmlModel,SBMLUtils.SBML_SPATIAL_NS_PREFIX);

    // Creates a geometry object via SpatialModelPlugin object.
	org.sbml.libsbml.Geometry sbmlGeometry = mplugin.createGeometry();
	sbmlGeometry.setCoordinateSystem(libsbmlConstants.SPATIAL_GEOMETRYKIND_CARTESIAN);
	sbmlGeometry.setId("vcell");

	Geometry vcGeometry = getSelectedSimContext().getGeometry();
	Model vcModel = getSelectedSimContext().getModel();
	//
	// list of CoordinateComponents : 1 if geometry is 1-d, 2 if geometry is 2-d, 3 if geometry is 3-d
	//
	int dimension = vcGeometry.getDimension();
	Extent vcExtent = vcGeometry.getExtent();
	Origin vcOrigin = vcGeometry.getOrigin();
	// add x coordinate component
	CoordinateComponent xComp = sbmlGeometry.createCoordinateComponent();
	xComp.setId(vcModel.getX().getName());
	xComp.setType(SPATIAL_X.sbmlType());
	xComp.setUnit(SBMLUnitTranslator.LENGTH);
	Boundary minX = xComp.createBoundaryMin();
	minX.setId("Xmin");
	minX.setValue(vcOrigin.getX());
	Boundary maxX = xComp.createBoundaryMax();
	maxX.setId("Xmax");
	maxX.setValue(vcOrigin.getX() + (vcExtent.getX()));
	createParamForSpatialElement(xComp, xComp.getId());
	// add y coordinate component
	if (dimension == 2 || dimension == 3) {
		CoordinateComponent yComp = sbmlGeometry.createCoordinateComponent();
		yComp.setId(vcModel.getY().getName());
		yComp.setType(SPATIAL_Y.sbmlType());
		yComp.setUnit(SBMLUnitTranslator.LENGTH);
		Boundary minY = yComp.createBoundaryMin();
		minY.setId("Ymin");
		minY.setValue(vcOrigin.getY());
		Boundary maxY = yComp.createBoundaryMax();
		maxY.setId("Ymax");
		maxY.setValue(vcOrigin.getY() + (vcExtent.getY()));
		createParamForSpatialElement(yComp, yComp.getId());
	}
	// add z coordinate component
	if (dimension == 3) {
		CoordinateComponent zComp = sbmlGeometry.createCoordinateComponent();
		zComp.setId(vcModel.getZ().getName());
		zComp.setType(SPATIAL_Z.sbmlType());
		zComp.setUnit(SBMLUnitTranslator.LENGTH);
		Boundary minZ = zComp.createBoundaryMin();
		minZ.setId("Zmin");
		minZ.setValue(vcOrigin.getZ());
		Boundary maxZ = zComp.createBoundaryMax();
		maxZ.setId("Zmax");
		maxZ.setValue(vcOrigin.getZ() + (vcExtent.getZ()));
		createParamForSpatialElement(zComp, zComp.getId());
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
		SpatialCompartmentPlugin cplugin = 
			SBMLHelper.checkedPlugin(SpatialCompartmentPlugin.class,comp,SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		GeometryClass gc = vcStructMapping.getGeometryClass();
		if (!goodPointer(gc,GeometryClass.class,structName))  {
			continue;
		}
		CompartmentMapping compMapping = cplugin.createCompartmentMapping();
		String geomClassName = gc.getName();
		String id = TokenMangler.mangleToSName(geomClassName + structName);
		compMapping.setId(id);
		compMapping.setDomainType(TokenMangler.mangleToSName(geomClassName));
		try {
			StructureMappingParameter usp = vcStructMapping.getUnitSizeParameter();
			Expression e = usp.getExpression();
			if (goodPointer(e,Expression.class, id)) {
				compMapping.setUnitSize(e.evaluateConstant());
			}
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Unable to create compartment mapping for structureMapping '" + compMapping.getId() +"' : " + e.getMessage());
		}
	}
	
	//
	// list of domain types : subvolumes and surface classes from VC
	//
	boolean bAnyAnalyticSubvolumes = false;
	boolean bAnyImageSubvolumes = false;
	boolean bAnyCSGSubvolumes = false;
	GeometryClass[] vcGeomClasses = vcGeometry.getGeometryClasses();
	int numSubVols = 0;
	for (int i = 0; i < vcGeomClasses.length; i++) {
	    DomainType domainType = sbmlGeometry.createDomainType();
	    domainType.setId(vcGeomClasses[i].getName());
	    if (vcGeomClasses[i] instanceof SubVolume) {
	    	if (((SubVolume)vcGeomClasses[i]) instanceof AnalyticSubVolume) {
	    		bAnyAnalyticSubvolumes = true;
	    	} else if (((SubVolume)vcGeomClasses[i]) instanceof ImageSubVolume) {
	    		bAnyImageSubvolumes = true;
	    	} else if (((SubVolume)vcGeomClasses[i]) instanceof CSGObject) {
	    		bAnyCSGSubvolumes = true;
	    	}
	    	domainType.setSpatialDimensions(3);
	    	numSubVols++;
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
		domain.setId(vcGeometricRegions[i].getName());
		if (vcGeometricRegions[i] instanceof VolumeGeometricRegion) {
			domain.setDomainType(((VolumeGeometricRegion)vcGeometricRegions[i]).getSubVolume().getName());
			
			//
			// get a list of interior points ... should probably use the distance map to find a point 
			// furthest inside (or several points associated with the morphological skeleton).
			//
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

			// adjacent domains : 2 adjacent domain objects for each surfaceClass in VC.
			// adjacent domain 1
			GeometricRegion adjGeomRegion0 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[0];
			GeometricRegion adjGeomRegion1 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[1];
			AdjacentDomains adjDomain = sbmlGeometry.createAdjacentDomains();
			adjDomain.setId(TokenMangler.mangleToSName(vcSurfaceGeomReg.getName()+"_"+adjGeomRegion0.getName()));
			adjDomain.setDomain1(vcSurfaceGeomReg.getName());
			adjDomain.setDomain2(adjGeomRegion0.getName());
			// adj domain 2
			adjDomain = sbmlGeometry.createAdjacentDomains();
			adjDomain.setId(TokenMangler.mangleToSName(vcSurfaceGeomReg.getName()+"_"+adjGeomRegion1.getName()));
			adjDomain.setDomain1(vcSurfaceGeomReg.getName());
			adjDomain.setDomain2(adjGeomRegion1.getName());
		}
	}
	
	//
	// add AnalyticGeometry
	//
	if (bAnyAnalyticSubvolumes && !bAnyImageSubvolumes && !bAnyCSGSubvolumes){
		AnalyticGeometry sbmlAnalyticGeomDefinition = sbmlGeometry.createAnalyticGeometry();
		sbmlAnalyticGeomDefinition.setId(TokenMangler.mangleToSName("Analytic_"+vcGeometry.getName()));	
		sbmlAnalyticGeomDefinition.setIsActive(true);
		for (int i = 0; i < vcGeomClasses.length; i++) {
			if (vcGeomClasses[i] instanceof AnalyticSubVolume) {
				AnalyticVolume analyticVol = sbmlAnalyticGeomDefinition.createAnalyticVolume();
				analyticVol.setId(vcGeomClasses[i].getName());
				analyticVol.setDomainType(vcGeomClasses[i].getName());
				analyticVol.setFunctionType("layered");
				analyticVol.setOrdinal(numSubVols - (i+1));
				Expression expr = ((AnalyticSubVolume)vcGeomClasses[i]).getExpression();
				try {
					String mathMLStr = ExpressionMathMLPrinter.getMathML(expr, true);
					ASTNode mathMLNode = libsbml.readMathMLFromString(mathMLStr);
					analyticVol.setMath(mathMLNode);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Error converting VC subvolume expression to mathML" + e.getMessage());
				}
			}
		}
	}
	//
	// add CSGeometry
	//
	if (!bAnyAnalyticSubvolumes && !bAnyImageSubvolumes && bAnyCSGSubvolumes){
		CSGeometry sbmlCSGeomDefinition = sbmlGeometry.createCsGeometry();
		sbmlCSGeomDefinition.setId(TokenMangler.mangleToSName("CSG_"+vcGeometry.getName()));
		for (int i = 0; i < vcGeomClasses.length; i++) {
			if (vcGeomClasses[i] instanceof CSGObject) {
				CSGObject vcellCSGObject = (CSGObject)vcGeomClasses[i];
				org.sbml.libsbml.CSGObject sbmlCSGObject = sbmlCSGeomDefinition.createCsgObject();
				sbmlCSGObject.setId(vcellCSGObject.getName());
				sbmlCSGObject.setDomainType(vcellCSGObject.getName());
				sbmlCSGObject.setOrdinal(numSubVols - (i+1));	// the ordinal should the the least for the default/background subVolume
				org.sbml.libsbml.CSGNode sbmlcsgNode = getSBMLCSGNode(vcellCSGObject.getRoot());
				sbmlCSGObject.setCsgNode(sbmlcsgNode);
			}
		}
	}
	//
	// add "Segmented" and "DistanceMap" SampledField Geometries
	//
		final boolean bVCGeometryIsImage = bAnyImageSubvolumes && !bAnyAnalyticSubvolumes && !bAnyCSGSubvolumes;
	//55if (bAnyAnalyticSubvolumes || bAnyImageSubvolumes || bAnyCSGSubvolumes){
	if (bVCGeometryIsImage) {
		//
		// add "Segmented" SampledFieldGeometry
		//
		SampledFieldGeometry segmentedImageSampledFieldGeometry = sbmlGeometry.createSampledFieldGeometry();
		segmentedImageSampledFieldGeometry.setId(TokenMangler.mangleToSName("SegmentedImage_"+vcGeometry.getName()));
		segmentedImageSampledFieldGeometry.setIsActive(true);
		//55boolean bVCGeometryIsImage = bAnyImageSubvolumes && !bAnyAnalyticSubvolumes && !bAnyCSGSubvolumes;
		Geometry vcImageGeometry = null;
		{
			if (bVCGeometryIsImage){
				// use existing image
				// make a resampled image;
				if (dimension == 3) {
					try {
						ISize imageSize = vcGeometry.getGeometrySpec().getDefaultSampledImageSize();
						vcGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
						vcImageGeometry = RayCaster.resampleGeometry(new GeometryThumbnailImageFactoryAWT(), vcGeometry, imageSize);
					} catch (Throwable e) {
						e.printStackTrace(System.out);
						throw new RuntimeException("Unable to convert the original analytic or constructed solid geometry to image-based geometry : " + e.getMessage());
					}
				} else {
					try {
						vcGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(),true,false);
						GeometrySpec origGeometrySpec = vcGeometry.getGeometrySpec();
						VCImage newVCImage = origGeometrySpec.getSampledImage().getCurrentValue();
						//
						// construct the new geometry with the sampled VCImage.
						//
						vcImageGeometry = new Geometry(vcGeometry.getName()+"_asImage", newVCImage);
						vcImageGeometry.getGeometrySpec().setExtent(vcGeometry.getExtent());
						vcImageGeometry.getGeometrySpec().setOrigin(vcGeometry.getOrigin());
						vcImageGeometry.setDescription(vcGeometry.getDescription());
						vcImageGeometry.getGeometrySurfaceDescription().setFilterCutoffFrequency(vcGeometry.getGeometrySurfaceDescription().getFilterCutoffFrequency());
						vcImageGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(), true,true);
					} catch (Exception e) {
						e.printStackTrace(System.out);
						throw new RuntimeException("Unable to convert the original analytic or constructed solid geometry to image-based geometry : " + e.getMessage());
					}
				}
				GeometryClass[] vcImageGeomClasses = vcImageGeometry.getGeometryClasses();
				for (int j = 0; j < vcImageGeomClasses.length; j++) {
					if (vcImageGeomClasses[j] instanceof ImageSubVolume) {
						SampledVolume sampledVol = segmentedImageSampledFieldGeometry.createSampledVolume();
						sampledVol.setId(vcGeomClasses[j].getName());
						sampledVol.setDomainType(vcGeomClasses[j].getName());
						sampledVol.setSampledValue(((ImageSubVolume) vcImageGeomClasses[j]).getPixelValue());
					}
				}
				// add sampledField to sampledFieldGeometry
				SampledField segmentedImageSampledField = sbmlGeometry.createSampledField(); 
				VCImage vcImage = vcImageGeometry.getGeometrySpec().getImage();
				validate(segmentedImageSampledField.setId("SegmentedImageSampledField") );
				validate(segmentedImageSampledField.setNumSamples1(vcImage.getNumX()) );
				validate(segmentedImageSampledField.setNumSamples2(vcImage.getNumY() ));
				validate(segmentedImageSampledField.setNumSamples3(vcImage.getNumZ()) );
				validate(segmentedImageSampledField.setInterpolationType(InterpolationKind.NEAREST_NEIGHBOR.ordinal()) );
				validate(segmentedImageSampledField.setCompression(CompressionKind.UNCOMPRESSED.ordinal()) );
				validate(segmentedImageSampledField.setDataType(DataKind.INT.ordinal( )) );
				segmentedImageSampledFieldGeometry.setSampledField(segmentedImageSampledField.getId());
				/*
		if (segmentedImageSampledFieldGeometry.isSampledFieldGeometry()) {
			System.out.println("Sample field is " + segmentedImageSampledFieldGeometry.getSampledField());
		}
				 */

				try {
					byte[] vcImagePixelsBytes = vcImage.getPixels();
					//			imageData.setCompression("");
					int[] segmentedImagePixelsInt = new int[vcImagePixelsBytes.length];
					for (int i = 0; i < vcImagePixelsBytes.length; i++) {
						segmentedImagePixelsInt[i] = 0x000000ff & ((int)vcImagePixelsBytes[i]);
					}
					segmentedImageSampledField.setSamples(segmentedImagePixelsInt, segmentedImagePixelsInt.length);
				} catch (ImageException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Unable to export image from VCell to SBML : " + e.getMessage());
				}
			}
		}

/*		
		//
		// add "DistanceMap" SampledFieldGeometry if there are exactly two subvolumes (else need more fields) and geometry is 3d.
		//
		if (numSubVols==2 && dimension == 3){
			SignedDistanceMap[] distanceMaps = null;
			try {
				distanceMaps = DistanceMapGenerator.computeDistanceMaps(vcImageGeometry, vcImageGeometry.getGeometrySpec().getImage(), false, false);
			} catch (ImageException e) {
				e.printStackTrace(System.out);
				System.err.println("Unable to export distance map sampled field from VCell to SBML : " + e.getMessage());
				// throw new RuntimeException("Unable to export distance map sampled field from VCell to SBML : " + e.getMessage());
				
				// don't want to throw an exception and stop export because distance map geometry couldn't be exported. 
				// just 'return' from method (since this is the last thing that is being done in this method).
				return;
			}
			//
			// the two distanceMaps should be redundant (one is negation of the other) ... so choose first one for field.
			//
			double[] signedDistances = distanceMaps[0].getSignedDistances();
			SampledFieldGeometry distanceMapSampledFieldGeometry = sbmlGeometry.createSampledFieldGeometry();
			distanceMapSampledFieldGeometry.setId(TokenMangler.mangleToSName("DistanceMap_"+vcGeometry.getName()));
			SampledField distanceMapSampledField = distanceMapSampledFieldGeometry.createSampledField();
			distanceMapSampledField.setId("DistanceMapSampledField");
			distanceMapSampledField.setNumSamples1(distanceMaps[0].getSamplesX().length);
			distanceMapSampledField.setNumSamples2(distanceMaps[0].getSamplesY().length);
			distanceMapSampledField.setNumSamples3(distanceMaps[0].getSamplesZ().length);
			distanceMapSampledField.setDataType("real");
System.err.println("do we need distanceMapSampleField.setDataType()?");
			distanceMapSampledField.setInterpolationType("linear");
			ImageData distanceMapImageData = distanceMapSampledField.createImageData();
			distanceMapImageData.setDataType("int16");
System.err.println("should be:\n  distanceMapImageData.setDataType(\"float32\")");
//					distanceMapImageData.setCompression("");

			double maxAbsValue = 0;
			for (int i = 0; i < signedDistances.length; i++) {
				maxAbsValue = Math.max(maxAbsValue,Math.abs(signedDistances[i]));
			}
			if (maxAbsValue==0.0){
				throw new RuntimeException("computed distance map all zeros");
			}
			double scale = (Short.MAX_VALUE-1)/maxAbsValue;
			int[] scaledIntegerDistanceMap = new int[signedDistances.length];
			for (int i = 0; i < signedDistances.length; i++) {
				scaledIntegerDistanceMap[i] = (int)(scale * signedDistances[i]);
			}
			distanceMapImageData.setSamples(scaledIntegerDistanceMap, signedDistances.length);
System.err.println("should be:\n  distanceMapImageData.setSamples((float[])signedDistances,signedDistances.length)");
			SampledVolume sampledVol = distanceMapSampledFieldGeometry.createSampledVolume();
			sampledVol.setId(distanceMaps[0].getInsideSubvolumeName());
			sampledVol.setDomainType(distanceMaps[0].getInsideSubvolumeName());
			sampledVol.setSampledValue(255);
			sampledVol = distanceMapSampledFieldGeometry.createSampledVolume();
			sampledVol.setId(distanceMaps[1].getInsideSubvolumeName());
			sampledVol.setDomainType(distanceMaps[1].getInsideSubvolumeName());
			sampledVol.setSampledValue(1);
		}
*/
	}
	//
	// add "SurfaceMesh" ParametricGeometry
	//
//	if (bAnyAnalyticSubvolumes || bAnyImageSubvolumes || bAnyCSGSubvolumes){
//		ParametricGeometry sbmlParametricGeomDefinition = sbmlGeometry.createParametricGeometry();
//		sbmlParametricGeomDefinition.setId(TokenMangler.mangleToSName("SurfaceMesh_"+vcGeometry.getName()));
//		xxxx
//	}
}

private boolean goodPointer(Object obj, Class<?> clzz, String sourceName) {
	if (lg.isEnabledFor(Level.WARN)) {
		lg.warn(sourceName + "has no " + clzz.getSimpleName());
	}
	return obj != null;
}

public static org.sbml.libsbml.CSGNode getSBMLCSGNode(cbit.vcell.geometry.CSGNode vcCSGNode) {
	String csgNodeName = vcCSGNode.getName();
	if (vcCSGNode instanceof cbit.vcell.geometry.CSGPrimitive){
		cbit.vcell.geometry.CSGPrimitive vcCSGprimitive = (cbit.vcell.geometry.CSGPrimitive)vcCSGNode; 
		org.sbml.libsbml.CSGPrimitive sbmlPrimitive = new org.sbml.libsbml.CSGPrimitive();
		sbmlPrimitive.setId(csgNodeName);
		switch (vcCSGprimitive.getType()){
		case SPHERE: {
			sbmlPrimitive.setPrimitiveType(SBMLSpatialConstants.SOLID_SPHERE);
			break;
		}
		case CONE: {
			sbmlPrimitive.setPrimitiveType(SBMLSpatialConstants.SOLID_CONE);
			break;
		}
		case CUBE: {
			sbmlPrimitive.setPrimitiveType(SBMLSpatialConstants.SOLID_CUBE);
			break;
		}
		case CYLINDER: {
			sbmlPrimitive.setPrimitiveType(SBMLSpatialConstants.SOLID_CYLINDER);
			break;
		}
		default: {
			throw new RuntimeException("unsupported primitive type "+vcCSGprimitive.getType());
		}
		}
		return sbmlPrimitive;
	}else if (vcCSGNode instanceof cbit.vcell.geometry.CSGPseudoPrimitive){
		// org.sbml.libsbml.CSGPseudoPrimitive sbmlPseudoPrimitive = new org.sbml.libsbml.CSGPseudoPrimitive();
		// sbmlPseudoPrimitive.setId(vcCSGNode.getName());
		throw new RuntimeException("pseudoPrimitive not yet supported in sbml export");
	}else if (vcCSGNode instanceof cbit.vcell.geometry.CSGSetOperator){
		cbit.vcell.geometry.CSGSetOperator vcCSGSetOperator = (cbit.vcell.geometry.CSGSetOperator)vcCSGNode; 
		org.sbml.libsbml.CSGSetOperator sbmlSetOperator = new org.sbml.libsbml.CSGSetOperator();
		sbmlSetOperator.setId(csgNodeName);
		switch (vcCSGSetOperator.getOpType()){
		case UNION: {
			sbmlSetOperator.setOperationType(SBMLSpatialConstants.UNION);
			break;
		}
		case DIFFERENCE: {
			sbmlSetOperator.setOperationType(SBMLSpatialConstants.SOLID_CONE);
			break;
		}
		case INTERSECTION: {
			sbmlSetOperator.setOperationType(SBMLSpatialConstants.INTERSECTION);
			break;
		}
		default: {
			throw new RuntimeException("unsupported set operation "+vcCSGSetOperator.getOpType());
		}
		}
		for (cbit.vcell.geometry.CSGNode vcChild : vcCSGSetOperator.getChildren()){
			sbmlSetOperator.addCsgNode(getSBMLCSGNode(vcChild));
		}

		return sbmlSetOperator;
	}else if (vcCSGNode instanceof cbit.vcell.geometry.CSGTransformation){
		cbit.vcell.geometry.CSGTransformation vcTransformation = (cbit.vcell.geometry.CSGTransformation)vcCSGNode;
		if (vcTransformation instanceof cbit.vcell.geometry.CSGTranslation){
			cbit.vcell.geometry.CSGTranslation vcTranslation = (cbit.vcell.geometry.CSGTranslation)vcTransformation;
			org.sbml.libsbml.CSGTranslation sbmlTranslation = new org.sbml.libsbml.CSGTranslation();
			sbmlTranslation.setId(csgNodeName);
			sbmlTranslation.setTranslateX(vcTranslation.getTranslation().getX());
			sbmlTranslation.setTranslateY(vcTranslation.getTranslation().getY());
			sbmlTranslation.setTranslateZ(vcTranslation.getTranslation().getZ());
{
double translateX = sbmlTranslation.getTranslateX();
double translateY = sbmlTranslation.getTranslateY();
double translateZ = sbmlTranslation.getTranslateZ();
if (translateX != vcTranslation.getTranslation().getX()){
	System.err.println("sbml x translation "+translateX+" doesn't match vcell "+vcTranslation.getTranslation().getX());
}
if (translateY != vcTranslation.getTranslation().getY()){
	System.err.println("sbml y translation "+translateY+" doesn't match vcell "+vcTranslation.getTranslation().getY());
}
if (translateZ != vcTranslation.getTranslation().getZ()){
	System.err.println("sbml z translation "+translateZ+" doesn't match vcell "+vcTranslation.getTranslation().getZ());
}
}
			sbmlTranslation.setCsgNode(getSBMLCSGNode(vcTranslation.getChild()));
			return sbmlTranslation;
		}else if (vcTransformation instanceof cbit.vcell.geometry.CSGRotation){
			cbit.vcell.geometry.CSGRotation vcRotation = (cbit.vcell.geometry.CSGRotation)vcTransformation;
			org.sbml.libsbml.CSGRotation sbmlRotation = new org.sbml.libsbml.CSGRotation();
			sbmlRotation.setId(csgNodeName);
			sbmlRotation.setRotateAngleInRadians(vcRotation.getRotationRadians());
			sbmlRotation.setRotateX(vcRotation.getAxis().getX());
			sbmlRotation.setRotateY(vcRotation.getAxis().getY());
			sbmlRotation.setRotateZ(vcRotation.getAxis().getZ());
			sbmlRotation.setCsgNode( getSBMLCSGNode(vcRotation.getChild()));
			return sbmlRotation;
		}else if (vcTransformation instanceof cbit.vcell.geometry.CSGScale){
			cbit.vcell.geometry.CSGScale vcScale = (cbit.vcell.geometry.CSGScale)vcTransformation;
			org.sbml.libsbml.CSGScale sbmlScale = new org.sbml.libsbml.CSGScale();
			sbmlScale.setId(csgNodeName);
			sbmlScale.setScaleX(vcScale.getScale().getX());
			sbmlScale.setScaleY(vcScale.getScale().getY());
			sbmlScale.setScaleZ(vcScale.getScale().getZ());
			sbmlScale.setCsgNode(getSBMLCSGNode(vcScale.getChild()));
			return sbmlScale;
		}else{
			throw new RuntimeException("unsupported transformation "+vcTransformation.getClass().getName());
		}
	}else{
		throw new RuntimeException("unsupported CSG Node "+vcCSGNode.getClass().getName()+" for SBMLSpatial export");
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
		SpatialAdapter sa = SpatialAdapter.fromSBMLType(cc.getType());
		// coordComponent with index = 1 represents X-axis, hence set param id as 'x'
		if (sa != null) {
			p.setId(sa.getName( ));
		}
	} else {
		p.setId(spatialId);
	}
	p.setValue(0.0);
	p.setConstant(true);
	// now need to create a SpatialSymbolReference for parameter
	SpatialParameterPlugin spPlugin = SBMLHelper.checkedPlugin(SpatialParameterPlugin.class,p,SBMLUtils.SBML_SPATIAL_NS_PREFIX);
	SpatialSymbolReference spSymRef = spPlugin.createSpatialSymbolReference();
	spSymRef.setId(spatialId);
	spSymRef.setSpatialRef(spatialId);
	
}

public void setSelectedSimContext(SimulationContext newVcSelectedSimContext) {
	vcSelectedSimContext = newVcSelectedSimContext;
}

public void setSelectedSimulationJob(SimulationJob newVcSelectedSimJob) {
	vcSelectedSimJob = newVcSelectedSimJob;
}

public Map<Pair <String, String>, String> getLocalToGlobalTranslationMap() {
	return l2gMap;
}

/**
 * VC_to_SB_Translator constructor comment.
 * @throws SbmlException 
 */
public void translateBioModel() throws SbmlException {
	// 'Parse' the Virtual cell model into an SBML model
	addUnitDefinitions();
	// Add features/compartments
	addCompartments();
	// add geometry, if present
	if (bSpatial) {
		addGeometry();
	}
	// Add species/speciesContexts
	addSpecies(); 
	// Add Parameters
	try {
		addParameters();
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	// Add rate rules
	addRateRules();
	// Add Reactions
	addReactions();
	// Add Events
	addEvents();
}

}
