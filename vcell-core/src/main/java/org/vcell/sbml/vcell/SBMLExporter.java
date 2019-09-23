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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Delay;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.SimpleSpeciesReference;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.Trigger;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.ext.spatial.AdjacentDomains;
import org.sbml.jsbml.ext.spatial.AdvectionCoefficient;
import org.sbml.jsbml.ext.spatial.AnalyticGeometry;
import org.sbml.jsbml.ext.spatial.AnalyticVolume;
import org.sbml.jsbml.ext.spatial.Boundary;
import org.sbml.jsbml.ext.spatial.BoundaryCondition;
import org.sbml.jsbml.ext.spatial.BoundaryConditionKind;
import org.sbml.jsbml.ext.spatial.CSGeometry;
import org.sbml.jsbml.ext.spatial.CompartmentMapping;
import org.sbml.jsbml.ext.spatial.CompressionKind;
import org.sbml.jsbml.ext.spatial.CoordinateComponent;
import org.sbml.jsbml.ext.spatial.CoordinateKind;
import org.sbml.jsbml.ext.spatial.DataKind;
import org.sbml.jsbml.ext.spatial.DiffusionCoefficient;
import org.sbml.jsbml.ext.spatial.DiffusionKind;
import org.sbml.jsbml.ext.spatial.Domain;
import org.sbml.jsbml.ext.spatial.DomainType;
import org.sbml.jsbml.ext.spatial.FunctionKind;
import org.sbml.jsbml.ext.spatial.GeometryKind;
import org.sbml.jsbml.ext.spatial.InteriorPoint;
import org.sbml.jsbml.ext.spatial.InterpolationKind;
import org.sbml.jsbml.ext.spatial.SampledField;
import org.sbml.jsbml.ext.spatial.SampledFieldGeometry;
import org.sbml.jsbml.ext.spatial.SampledVolume;
import org.sbml.jsbml.ext.spatial.SetOperation;
import org.sbml.jsbml.ext.spatial.SpatialCompartmentPlugin;
import org.sbml.jsbml.ext.spatial.SpatialModelPlugin;
import org.sbml.jsbml.ext.spatial.SpatialParameterPlugin;
import org.sbml.jsbml.ext.spatial.SpatialReactionPlugin;
import org.sbml.jsbml.ext.spatial.SpatialSymbolReference;
import org.vcell.sbml.SBMLHelper;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;

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
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.Constant;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.Model.ReservedSymbolRole;
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
import cbit.vcell.parser.ExpressionMathMLPrinter.MathType;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLTags;

/**
 * Insert the type's description here.
 * Creation date: (3/31/2006 1:02:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SBMLExporter {
	public static final String DOMAIN_TYPE_PREFIX = "domainType_";
	private int sbmlLevel = 3;
	private int sbmlVersion = 1;
	private org.sbml.jsbml.Model sbmlModel = null;
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
	
	private static Logger lg = LogManager.getLogger(SBMLExporter.class);
	
	public class SBMLExportSpec {
		private VCUnitDefinition substanceUnits = null;
		private VCUnitDefinition volUnits = null;
		private VCUnitDefinition areaUnits = null;
		private VCUnitDefinition lengthUnits = null;
		private VCUnitDefinition timeUnits = null;
		
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
	}
		
	public static class VCellSBMLDoc implements AutoCloseable {
		public final SBMLDocument document;
		public final org.sbml.jsbml.Model model; 
		public final String xmlString;

		public VCellSBMLDoc(SBMLDocument document, org.sbml.jsbml.Model model, String xmlString) {
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
		return isSpatial && ctx.getApplicationType()==Application.NETWORK_DETERMINISTIC;
	}
	

/**
 * addCompartments comment.
 * @throws XMLStreamException 
 * @throws SbmlException 
 */
protected void addCompartments() throws XMLStreamException, SbmlException {
	Model vcModel = vcBioModel.getModel();
	cbit.vcell.model.Structure[] vcStructures = vcModel.getStructures();
	for (int i = 0; i < vcStructures.length; i++){
		Compartment sbmlCompartment = sbmlModel.createCompartment();
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
			UnitDefinition unitDefn = getOrCreateSBMLUnit(sbmlSizeUnit);
			sbmlCompartment.setUnits(unitDefn);
		} else if (vcStructures[i] instanceof Membrane) {
			Membrane vcMembrane = (Membrane)vcStructures[i];
			sbmlCompartment.setSpatialDimensions(2);
			Feature outsideFeature = structTopology.getOutsideFeature(vcMembrane);
			if (outsideFeature != null) {
				sbmlCompartment.setOutside(TokenMangler.mangleToSName(outsideFeature.getName()));
				sbmlSizeUnit = sbmlExportSpec.getAreaUnits();
				UnitDefinition unitDefn = getOrCreateSBMLUnit(sbmlSizeUnit);
				sbmlCompartment.setUnits(unitDefn);
			} else if (lg.isWarnEnabled()) {
				lg.warn(this.sbmlModel.getName() + " membrame "  + vcMembrane.getName()  + " has not outside feature");
				
			}
		}
		sbmlCompartment.setConstant(true);

		StructureMapping vcStructMapping = getSelectedSimContext().getGeometryContext().getStructureMapping(vcStructures[i]);
		try {
			if (vcStructMapping.getSizeParameter().getExpression() != null) {
				sbmlCompartment.setSize(vcStructMapping.getSizeParameter().getExpression().evaluateConstant());
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
//		if (parentStructure != null) {
//			sbmlImportRelatedElement = new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
//			Element compartmentElement = new Element(XMLTags.OutsideCompartmentTag, sbml_vcml_ns);
//			compartmentElement.setAttribute(XMLTags.NameAttrTag, TokenMangler.mangleToSName(parentStructure.getName()));
//			sbmlImportRelatedElement.addContent(compartmentElement);
//		}

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
//private void addKineticAndGlobalParameterUnits() throws SbmlException {
//
//	//
//	// Get all kinetic parameters from simple reactions and flux reactions from the Biomodel
//	// And all Model (global) parameters from Model.
//	// For each parameter,
//	//		get its unit (VCunitDefinition)
//	//		check if it is part of unitsList - if so, continue
//	//		check if it is a base unit - if so, continue
//	//		else, get the converted unit (VC -> SBML)
//	//		add unit to sbmlModel unit definition
//	//
//
//	Vector<Parameter> paramsVector = new Vector<Parameter>();
//	// Add globals
//	Model vcModel = vcBioModel.getModel();
//	ModelParameter[] globalParams = vcModel.getModelParameters();
//	for (int i = 0; i < globalParams.length; i++) {
//		paramsVector.addElement(globalParams[i]);
//	}
//	// Add reaction kinetic parameters
//	ReactionStep[] vcReactions = vcModel.getReactionSteps();
//	for (int i = 0; i < vcReactions.length; i++) {
//		Kinetics rxnKinetics = vcReactions[i].getKinetics();
//		Parameter[] kineticParams = rxnKinetics.getKineticsParameters();
//		for (int j = 0; j < kineticParams.length; j++) {
//			paramsVector.addElement(kineticParams[j]);
//		}
//	}
//
//	for (int i = 0; i < paramsVector.size(); i++){
//		Parameter param = (Parameter)paramsVector.elementAt(i);
//		VCUnitDefinition paramUnitDefn = param.getUnitDefinition();
//		if (paramUnitDefn == null || paramUnitDefn.isTBD()) {
//			continue;
//		}
//		String unitSymbol = org.vcell.util.TokenMangler.mangleToSName(paramUnitDefn.getSymbol());
//		if (unitSymbol == null) {
//			continue;
//		}
//		getOrCreateSBMLUnit(paramUnitDefn);
//	}
//}


/**
 * At present, the Virtual cell doesn't support global parameters
 * @throws SbmlException 
 */
protected void addParameters() throws ExpressionException, SbmlException {
	Model vcModel = getSelectedSimContext().getModel();
	// add VCell global parameters to the SBML listofParameters
	ModelParameter[] vcGlobalParams = vcModel.getModelParameters();  
	if (vcGlobalParams != null) {
	for (ModelParameter vcParam : vcGlobalParams) {
		org.sbml.jsbml.Parameter sbmlParam = sbmlModel.createParameter();
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
			sbmlParam.setUnits(getOrCreateSBMLUnit(vcParamUnit));
		}
	}
	}
	
	ReservedSymbol[] vcReservedSymbols = vcModel.getReservedSymbols();  
	if (vcReservedSymbols != null) {
	for (ReservedSymbol vcParam : vcReservedSymbols) {
		if(vcParam.isTime() || vcParam.isX() || vcParam.isY() || vcParam.isZ()) {
			continue;
		}
		if(vcParam.getRole().equals(ReservedSymbolRole.KMILLIVOLTS)) {
//			System.out.println("KMILLIVOLTS");
//			continue;
		}
		if(vcParam.getRole().equals(ReservedSymbolRole.K_GHK)) {
//			System.out.println("K_GHK");
		}
		
		org.sbml.jsbml.Parameter sbmlParam = sbmlModel.createParameter();
		sbmlParam.setId(vcParam.getName());
		sbmlParam.setConstant(vcParam.isConstant());
		
		Expression reservedSymbolExpression = vcParam.getExpression();
		if(reservedSymbolExpression == null) {
			if(vcParam.isTemperature()) {
				SimulationContext sc = getSelectedSimContext();
				double T = sc.getTemperatureKelvin();
				sbmlParam.setValue(T);
				sbmlParam.setConstant(true);
			}
		} else {
			Expression paramExpr = new Expression(reservedSymbolExpression);
			boolean bParamIsNumeric = true;
			if (paramExpr.isNumeric()) {
				sbmlParam.setValue(paramExpr.evaluateConstant());
				if (getSelectedSimContext().getRateRule(vcParam) != null) {
					bParamIsNumeric = false;
				}
			} else {
				bParamIsNumeric = false;
				ASTNode paramFormulaNode = getFormulaFromExpression(paramExpr);
				AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
				sbmlParamAssignmentRule.setVariable(vcParam.getName());
				sbmlParamAssignmentRule.setMath(paramFormulaNode);
			}
			sbmlParam.setConstant(bParamIsNumeric);
		}
		VCUnitDefinition vcParamUnit = vcParam.getUnitDefinition();
		if (!vcParamUnit.isTBD()) {
			sbmlParam.setUnits(getOrCreateSBMLUnit(vcParamUnit));
		}
	}
	}
}


/**
 * addReactions comment.
 * @throws SbmlException 
 * @throws XMLStreamException 
 */
protected void addReactions() throws SbmlException, XMLStreamException {

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
		org.sbml.jsbml.Reaction sbmlReaction = sbmlModel.createReaction();
		sbmlReaction.setId(org.vcell.util.TokenMangler.mangleToSName(rxnName));
		sbmlReaction.setName(rxnName);
			
		// If the reactionStep is a flux reaction, add the details to the annotation (structure, carrier valence, flux carrier, fluxOption, etc.)
		// If reactionStep is a simple reaction, add annotation to indicate the structure of reaction.
		// Useful when roundtripping ...
		Element sbmlImportRelatedElement = null;
//		try {
//			sbmlImportRelatedElement = getAnnotationElement(vcReactionStep);
//		} catch (XmlParseException e1) {
//			e1.printStackTrace(System.out);
////			throw new RuntimeException("Error ");
//		}
		
		// Get annotation (RDF and non-RDF) for reactionStep from SBMLAnnotationUtils
		sbmlAnnotationUtil.writeAnnotation(vcReactionStep, sbmlReaction, sbmlImportRelatedElement);
		
		// Now set notes, 
		sbmlAnnotationUtil.writeNotes(vcReactionStep, sbmlReaction);
		
		// Get reaction kineticLaw
		Kinetics vcRxnKinetics = vcReactionStep.getKinetics();
		org.sbml.jsbml.KineticLaw sbmlKLaw = sbmlReaction.createKineticLaw();

		try {
			// Convert expression from kinetics rate parameter into MathML and use libSBMl utilities to convert it to formula
			// (instead of directly using rate parameter's expression infix) to maintain integrity of formula :
			// for example logical and inequalities are not handled gracefully by libSBMl if expression.infix is used.
			final Expression localRateExpr;
			final Expression lumpedRateExpr;
			if (vcRxnKinetics instanceof DistributedKinetics){
				localRateExpr = ((DistributedKinetics) vcRxnKinetics).getReactionRateParameter().getExpression();
				lumpedRateExpr = null;
			}else if (vcRxnKinetics instanceof LumpedKinetics){
				localRateExpr = null;
				lumpedRateExpr = ((LumpedKinetics) vcRxnKinetics).getLumpedReactionRateParameter().getExpression();
			}else{
				throw new RuntimeException("unexpected Rate Law '"+vcRxnKinetics.getClass().getSimpleName()+"', not distributed or lumped type");
			}
			//			if (vcRxnKinetics instanceof DistributedKinetics)
//			Expression correctedRateExpr = kineticsAdapter.getExpression(); 
			
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
				if ( (vcKParam.getRole() != Kinetics.ROLE_ReactionRate) && (vcKParam.getRole() != Kinetics.ROLE_LumpedReactionRate) ) {
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
						VCUnitDefinition vcUnit = vcKParam.getUnitDefinition();
						for (int k = 0; k < vcKineticsParams.length; k++){
							if (kinParamExprs[k] != null) {
								// The param could be in the expression for any other param
								if (kinParamExprs[k].hasSymbol(origParamName)) {
									// if param is present in non-numeric param expression, this param has to be global
									// mangle its name to avoid conflict with other globals
									if (globalParamNamesHash.get(newParamName) == null) {
										globalParamNamesHash.put(newParamName, newParamName);
										org.sbml.jsbml.Parameter sbmlKinParam = sbmlModel.createParameter();
										sbmlKinParam.setId(newParamName);
										sbmlKinParam.setValue(vcKParam.getConstantValue());
										final boolean constValue = vcKParam.isConstant();
										sbmlKinParam.setConstant(true);
										// Set SBML units for sbmlParam using VC units from vcParam  
										if (!vcUnit.isTBD()) {
											UnitDefinition unitDefn = getOrCreateSBMLUnit(vcUnit);
											sbmlKinParam.setUnits(unitDefn);
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
							org.sbml.jsbml.LocalParameter sbmlKinParam = sbmlKLaw.createLocalParameter();
							sbmlKinParam.setId(origParamName);
							sbmlKinParam.setValue(vcKParam.getConstantValue());
							System.out.println ("tis constant " + sbmlKinParam.isExplicitlySetConstant());
							//sbmlKinParam.setConstant(true) ) ;
							// Set SBML units for sbmlParam using VC units from vcParam  
							if (!vcUnit.isTBD()) {
								UnitDefinition unitDefn = getOrCreateSBMLUnit(vcUnit);
								sbmlKinParam.setUnits(unitDefn);
							}
						} else {
							// if parameter has been added to global param list, its name has been mangled, 
							// hence change its occurance in rate expression if it contains that param name
							if (localRateExpr!=null && localRateExpr.hasSymbol(origParamName)) {
								localRateExpr.substituteInPlace(new Expression(origParamName), new Expression(newParamName));
							}
							if (lumpedRateExpr!=null && lumpedRateExpr.hasSymbol(origParamName)) {
								lumpedRateExpr.substituteInPlace(new Expression(origParamName), new Expression(newParamName));
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
					if (localRateExpr!=null && localRateExpr.hasSymbol(oldName)) {
						localRateExpr.substituteInPlace(new Expression(oldName), new Expression(newName));
					}
					if (lumpedRateExpr!=null && lumpedRateExpr.hasSymbol(oldName)) {
						lumpedRateExpr.substituteInPlace(new Expression(oldName), new Expression(newName));
					}
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
					org.sbml.jsbml.Parameter sbmlKinParam = sbmlModel.createParameter();
					sbmlKinParam.setId(paramName);
					if (!vcKineticsParams[j].getUnitDefinition().isTBD()) {
						sbmlKinParam.setUnits(getOrCreateSBMLUnit(vcKineticsParams[j].getUnitDefinition()));
					}
					// Since the parameter is being specified by a Rule, its 'constant' field shoud be set to 'false' (default - true).
					sbmlKinParam.setConstant(false);
				}
			} // end for (j) - fifth pass

			// After making all necessary adjustments to the rate expression, now set the sbmlKLaw.
			final ASTNode exprFormulaNode;
			if (lumpedRateExpr!=null){
				exprFormulaNode = getFormulaFromExpression(lumpedRateExpr);
			}else{
				if (bSpatial){
					exprFormulaNode = getFormulaFromExpression(localRateExpr);
				}else{
					exprFormulaNode = getFormulaFromExpression(Expression.mult(localRateExpr, new Expression(vcReactionStep.getStructure().getName())));
				}
			}
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
			String rolePostfix = "";	// to get unique ID when the same species is both a reactant and a product
			if (rxnParticpant instanceof cbit.vcell.model.Reactant) {
				rolePostfix = "r";
				ssr = sr = sbmlReaction.createReactant();
			} else if (rxnParticpant instanceof cbit.vcell.model.Product) {
				rolePostfix = "p";
				ssr = sr  = sbmlReaction.createProduct();
			} 
			if (rxnParticpant instanceof cbit.vcell.model.Catalyst) {
				rolePostfix = "c";
				ssr = sbmlReaction.createModifier();
			}
			if (ssr != null) {
				ssr.setSpecies(rxnParticpant.getSpeciesContext().getName());
			}
			if (sr != null) {
				sr.setStoichiometry(Double.parseDouble(Integer.toString(rxnParticpant.getStoichiometry())));
				String modelUniqueName = vcReactionStep.getName() + '_'  + rxnParticpant.getName() + rolePostfix;
				sr.setId(TokenMangler.mangleToSName(modelUniqueName));
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
			SpatialReactionPlugin srplugin = (SpatialReactionPlugin) sbmlReaction.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
			srplugin.setIsLocal(vcRxnKinetics instanceof DistributedKinetics);
		}
	}
}

private UnitDefinition getOrCreateSBMLUnit(VCUnitDefinition vcUnit) throws SbmlException {
	String mangledSymbol = TokenMangler.mangleToSName(vcUnit.getSymbol());
	UnitDefinition unitDefn = sbmlModel.getUnitDefinition(mangledSymbol);
	if (unitDefn == null){
		unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(vcUnit, sbmlLevel, sbmlVersion, vcBioModel.getModel().getUnitSystem());
		unitDefn.setId(mangledSymbol);
		sbmlModel.addUnitDefinition(unitDefn);
	}
	return unitDefn;
}

private BoundaryConditionKind getBoundaryConditionKind(BoundaryConditionType vcellBoundaryConditionType){
	if (vcellBoundaryConditionType == BoundaryConditionType.DIRICHLET){
		return BoundaryConditionKind.Dirichlet;
	}else if (vcellBoundaryConditionType == BoundaryConditionType.NEUMANN){
		return BoundaryConditionKind.Neumann;
	}else if (vcellBoundaryConditionType == BoundaryConditionType.PERIODIC){
		throw new RuntimeException("periodic boundary conditions not yet supported for SBML Spatial export");
	}else{
		throw new RuntimeException("unexpected boundary conditions type "+vcellBoundaryConditionType.boundaryTypeStringValue());
	}
}


/**
 * addSpecies comment.
 * @throws XMLStreamException 
 * @throws SbmlException 
 */
protected void addSpecies() throws XMLStreamException, SbmlException {
	Model vcModel = vcBioModel.getModel();
	SpeciesContext[] vcSpeciesContexts = vcModel.getSpeciesContexts();
	for (int i = 0; i < vcSpeciesContexts.length; i++){
		org.sbml.jsbml.Species sbmlSpecies = sbmlModel.createSpecies();
		sbmlSpecies.setId(vcSpeciesContexts[i].getName());
		if(vcSpeciesContexts[i].getSbmlName() != null) {
			sbmlSpecies.setName(vcSpeciesContexts[i].getSbmlName());
		}
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
		UnitDefinition unitDefn = getOrCreateSBMLUnit(sbmlExportSpec.getSubstanceUnits());
		sbmlSpecies.setSubstanceUnits(unitDefn);

		// need to do the following if exporting to SBML spatial
		if (bSpatial) {
			
			// Required for setting BoundaryConditions : structureMapping for vcSpeciesContext[i] & sbmlGeometry.coordinateComponents
			StructureMapping sm = getSelectedSimContext().getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure());
			SpatialModelPlugin mplugin = (SpatialModelPlugin)sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
			org.sbml.jsbml.ext.spatial.Geometry sbmlGeometry = mplugin.getGeometry();
			CoordinateComponent ccX = sbmlGeometry.getListOfCoordinateComponents().get(vcModel.getX().getName());
			CoordinateComponent ccY = sbmlGeometry.getListOfCoordinateComponents().get(vcModel.getY().getName());
			CoordinateComponent ccZ = sbmlGeometry.getListOfCoordinateComponents().get(vcModel.getZ().getName());
			
			// add diffusion, advection, boundary condition parameters for species, if they exist
			Parameter[] scsParams = vcSpeciesContextsSpec.getParameters();
			if (scsParams != null) {
				for (int j = 0; j < scsParams.length; j++) {
					if (scsParams[j] != null) {
						SpeciesContextSpecParameter scsParam = (SpeciesContextSpecParameter)scsParams[j];
						// no need to add parameters in SBML for init conc or init count
						int role = scsParam.getRole();
						switch (role){
						case SpeciesContextSpec.ROLE_BoundaryValueXm:{
							break;
						}
						case SpeciesContextSpec.ROLE_BoundaryValueXp:{
							break;
						}
						case SpeciesContextSpec.ROLE_BoundaryValueYm:{
							break;
						}
						case SpeciesContextSpec.ROLE_BoundaryValueYp:{
							break;
						}
						case SpeciesContextSpec.ROLE_BoundaryValueZm:{
							break;
						}
						case SpeciesContextSpec.ROLE_BoundaryValueZp:{
							break;
						}
						case SpeciesContextSpec.ROLE_DiffusionRate:{
							break;
						}
						case SpeciesContextSpec.ROLE_InitialConcentration:{
							continue; // done elsewhere??
							//break;
						}
						case SpeciesContextSpec.ROLE_InitialCount:{
							continue; // done elsewhere??
							//break;
						}
						case SpeciesContextSpec.ROLE_VelocityX:{
							break;
						}
						case SpeciesContextSpec.ROLE_VelocityY:{
							break;
						}
						case SpeciesContextSpec.ROLE_VelocityZ:{
							break;
						}
						default:{
							throw new RuntimeException("SpeciesContext Specification parameter with role "+SpeciesContextSpec.RoleNames[role]+" not yet supported for SBML export");
						}
						}
						// if diffusion is 0 && vel terms are not specified, boundary condition not present
						if (vcSpeciesContextsSpec.isAdvecting() || vcSpeciesContextsSpec.isDiffusing()) {
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
							SpatialQuantity[] velX_Quantities = vcSpeciesContextsSpec.getVelocityQuantities(QuantityComponent.X);
							boolean bVelX_ExprIsNull = (velX_Expr == null && velX_Quantities.length == 0);
							Expression velY_Expr = vcSpeciesContextsSpec.getVelocityYParameter().getExpression();
							SpatialQuantity[] velY_Quantities = vcSpeciesContextsSpec.getVelocityQuantities(QuantityComponent.Y);
							boolean bVelY_ExprIsNull = (velY_Expr == null && velY_Quantities.length == 0);
							Expression velZ_Expr = vcSpeciesContextsSpec.getVelocityZParameter().getExpression();
							SpatialQuantity[] velZ_Quantities = vcSpeciesContextsSpec.getVelocityQuantities(QuantityComponent.Z);
							boolean bVelZ_ExprIsNull = (velZ_Expr == null && velZ_Quantities.length == 0);
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
						org.sbml.jsbml.Parameter sbmlParam = createSBMLParamFromSpeciesParam(vcSpeciesContexts[i], (SpeciesContextSpecParameter)scsParams[j]);
						if (sbmlParam != null) {
							BoundaryConditionType vcBCType_Xm = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure()).getBoundaryConditionTypeXm();
							BoundaryConditionType vcBCType_Xp = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure()).getBoundaryConditionTypeXp();
							BoundaryConditionType vcBCType_Ym = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure()).getBoundaryConditionTypeYm();
							BoundaryConditionType vcBCType_Yp = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure()).getBoundaryConditionTypeYp();
							BoundaryConditionType vcBCType_Zm = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure()).getBoundaryConditionTypeZm();
							BoundaryConditionType vcBCType_Zp = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContexts[i].getStructure()).getBoundaryConditionTypeZp();
							SpatialParameterPlugin spplugin = (SpatialParameterPlugin)sbmlParam.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
							if (role == SpeciesContextSpec.ROLE_DiffusionRate) {
								// set diffusionCoefficient element in SpatialParameterPlugin for param
								DiffusionCoefficient sbmlDiffCoeff = new DiffusionCoefficient();
								sbmlDiffCoeff.setVariable(vcSpeciesContexts[i].getName());
								sbmlDiffCoeff.setDiffusionKind(DiffusionKind.isotropic);
								sbmlDiffCoeff.setSpeciesRef(vcSpeciesContexts[i].getName());
								spplugin.setParamType(sbmlDiffCoeff);
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueXm) && (ccX != null)) {
								// set BoundaryCondn Xm element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCXm = new BoundaryCondition();
								spplugin.setParamType(sbmlBCXm);
								sbmlBCXm.setType(getBoundaryConditionKind(vcBCType_Xm));
								sbmlBCXm.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCXm.setCoordinateBoundary(ccX.getBoundaryMinimum().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueXp) && (ccX != null)) {
								// set BoundaryCondn Xp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCXp = new BoundaryCondition();
								spplugin.setParamType(sbmlBCXp);
								sbmlBCXp.setType(getBoundaryConditionKind(vcBCType_Xp));
								sbmlBCXp.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCXp.setType(sm.getBoundaryConditionTypeXp().boundaryTypeStringValue());
								sbmlBCXp.setCoordinateBoundary(ccX.getBoundaryMaximum().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueYm)  && (ccY != null)) {
								// set BoundaryCondn Ym element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCYm = new BoundaryCondition();
								spplugin.setParamType(sbmlBCYm);
								sbmlBCYm.setType(getBoundaryConditionKind(vcBCType_Yp));
								sbmlBCYm.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCYm.setType(sm.getBoundaryConditionTypeYm().boundaryTypeStringValue());
								sbmlBCYm.setCoordinateBoundary(ccY.getBoundaryMinimum().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueYp) && (ccY != null)){
								// set BoundaryCondn Yp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCYp = new BoundaryCondition();
								spplugin.setParamType(sbmlBCYp);
								sbmlBCYp.setType(getBoundaryConditionKind(vcBCType_Yp));
								sbmlBCYp.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCYp.setType(sm.getBoundaryConditionTypeYp().boundaryTypeStringValue());
								sbmlBCYp.setCoordinateBoundary(ccY.getBoundaryMaximum().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueZm)  && (ccZ != null)) {
								// set BoundaryCondn Zm element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCZm = new BoundaryCondition();
								spplugin.setParamType(sbmlBCZm);
								sbmlBCZm.setType(getBoundaryConditionKind(vcBCType_Zm));
								sbmlBCZm.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCZm.setType(sm.getBoundaryConditionTypeZm().boundaryTypeStringValue());
								sbmlBCZm.setCoordinateBoundary(ccZ.getBoundaryMinimum().getId());
							} 
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueZp)  && (ccZ != null)) {
								// set BoundaryCondn Zp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCZp = new BoundaryCondition();
								spplugin.setParamType(sbmlBCZp);
								sbmlBCZp.setType(getBoundaryConditionKind(vcBCType_Zp));
								sbmlBCZp.setVariable(vcSpeciesContexts[i].getName());
								sbmlBCZp.setType(sm.getBoundaryConditionTypeZp().boundaryTypeStringValue());
								sbmlBCZp.setCoordinateBoundary(ccZ.getBoundaryMaximum().getId());
							} 
							if (role == SpeciesContextSpec.ROLE_VelocityX) {
								// set advectionCoeff X element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffX = new AdvectionCoefficient();
								spplugin.setParamType(sbmlAdvCoeffX);
								sbmlAdvCoeffX.setVariable(vcSpeciesContexts[i].getName());
								sbmlAdvCoeffX.setCoordinate(CoordinateKind.cartesianX);
							} 
							if (role == SpeciesContextSpec.ROLE_VelocityY) {
								// set advectionCoeff Y element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffY = new AdvectionCoefficient();
								spplugin.setParamType(sbmlAdvCoeffY);
								sbmlAdvCoeffY.setVariable(vcSpeciesContexts[i].getName());
								sbmlAdvCoeffY.setCoordinate(CoordinateKind.cartesianY);
							} 
							if (role == SpeciesContextSpec.ROLE_VelocityZ) {
								// set advectionCoeff Z element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffZ = new AdvectionCoefficient();
								spplugin.setParamType(sbmlAdvCoeffZ);
								sbmlAdvCoeffZ.setVariable(vcSpeciesContexts[i].getName());
								sbmlAdvCoeffZ.setCoordinate(CoordinateKind.cartesianZ);
							}
						} 	// if sbmlParam != null
					}	// if scsParams[j] != null
				}	// end for scsParams
			}	// end scsParams != null
		} // end if (bSpatial)

		// Add the common name of species to annotation, and add an annotation element to the species.
		// This is required later while trying to read in fluxes ...
		Element sbmlImportRelatedElement = null;   //new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
//		Element speciesElement = new Element(XMLTags.SpeciesTag, sbml_vcml_ns);
//		speciesElement.setAttribute(XMLTags.NameAttrTag, TokenMangler.mangleToSName(vcSpeciesContexts[i].getSpecies().getCommonName()));
//		sbmlImportRelatedElement.addContent(speciesElement);
		
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
 * @throws SbmlException 
 */
org.sbml.jsbml.Parameter createSBMLParamFromSpeciesParam(SpeciesContext spContext, SpeciesContextSpecParameter scsParam) throws SbmlException {
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
		org.sbml.jsbml.Parameter param = sbmlModel.createParameter();
		param.setId(TokenMangler.mangleToSName(spContext.getName() + "_" + scsParam.getName()));
		UnitDefinition unitDefn = getOrCreateSBMLUnit(scsParam.getUnitDefinition());
		param.setUnits(unitDefn);
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

//	Model vcModel = vcBioModel.getModel();
//	ModelUnitSystem vcUnitSystem = vcModel.getUnitSystem();

	sbmlModel.setSubstanceUnits(getOrCreateSBMLUnit(sbmlExportSpec.getSubstanceUnits()));
	sbmlModel.setVolumeUnits(getOrCreateSBMLUnit(sbmlExportSpec.getVolumeUnits()));
	sbmlModel.setAreaUnits(getOrCreateSBMLUnit(sbmlExportSpec.getAreaUnits()));
	sbmlModel.setLengthUnits(getOrCreateSBMLUnit(sbmlExportSpec.getLengthUnits()));
	sbmlModel.setTimeUnits(getOrCreateSBMLUnit(sbmlExportSpec.getTimeUnits()));
	//sbmlModel.setExtentUnits(getOrCreateSBMLUnit(???);

	//addKineticAndGlobalParameterUnits();
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
				ASTNode math = getFormulaFromExpression(triggerExpr,MathType.BOOLEAN);
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
				org.sbml.jsbml.EventAssignment sbmlEA = sbmlEvent.createEventAssignment();
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
			org.sbml.jsbml.RateRule sbmlRateRule = sbmlModel.createRateRule();
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
protected void addAssignmentRules()  {
	cbit.vcell.mapping.AssignmentRule[] vcAssignmentRules = getSelectedSimContext().getAssignmentRules();
	if (vcAssignmentRules != null) {
		for(cbit.vcell.mapping.AssignmentRule vcRule : vcAssignmentRules) {
			org.sbml.jsbml.AssignmentRule sbmlRule = sbmlModel.createAssignmentRule();
			sbmlRule.setId(vcRule.getName());
			sbmlRule.setName(vcRule.getName());
			sbmlRule.setVariable(vcRule.getAssignmentRuleVar().getName());
			Expression vcRuleExpression = vcRule.getAssignmentRuleExpression();
			ASTNode math = getFormulaFromExpression(vcRuleExpression);
			sbmlRule.setMath(math);
		}
	}
}
protected void addInitialAssignments() throws ExpressionException  {	// used for parameter scan exports
	
//	private SimulationContext vcSelectedSimContext = null;
//	private SimulationJob vcSelectedSimJob = null;

	if(vcSelectedSimJob == null) {
		return;
	}
	int index = vcSelectedSimJob.getJobIndex();
	System.out.println("Simulation Job index = " + index);
	
	MathOverrides mo = vcSelectedSimJob.getSimulation().getMathOverrides();
	if(mo == null || !mo.hasOverrides()) {
		return;
	}
	
	String[] ocns = mo.getOverridenConstantNames();
//	String[] scns = mo.getScannedConstantNames();
//	
//	
	for(String ocn : ocns) {
		Expression exp = mo.getActualExpression(ocn, index);
//		Constant co = mo.getConstant(ocn);
//		SymbolTableEntry ste = vcSelectedSimContext.getEntry(ocn);
//		SpeciesContext sc = vcSelectedSimContext.getModel().getSpeciesContext(ocn);
//		ReactionContext rc = vcSelectedSimContext.getReactionContext();
//		SpeciesContextSpec[] scs = rc.getSpeciesContextSpecs();
//		ModelParameter mp = vcSelectedSimContext.getModel().getModelParameter(ocn);
		System.out.println("   overriden constant: " + ocn + ", " + exp.infix());
	}
	
	
//	for(String scn : scns) {
//		System.out.println("  scanned constant: " + scn);
//	}

//	String initAssgnSymbol = "s0";
//	Expression initAssignMathExpr = new Expression("0.0");
//	SpeciesContext sc = vcBioModel.getSimulationContext(0).getModel().getSpeciesContext(initAssgnSymbol);
//	SpeciesContextSpec scs = vcBioModel.getSimulationContext(0).getReactionContext().getSpeciesContextSpec(sc);
//	ModelParameter mp = vcBioModel.getSimulationContext(0).getModel().getModelParameter(initAssgnSymbol);
//	if (scs != null) {
//		Expression exp = scs.getInitialConditionParameter().getExpression();
////		scs.getInitialConditionParameter().setExpression(initAssignMathExpr);
//	} else if (mp != null) {
//		Expression exp = mp.getExpression();
////		mp.setExpression(initAssignMathExpr);
//	}
	// TODO: here
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


public static ASTNode getFormulaFromExpression(Expression expression) { 
	return getFormulaFromExpression(expression, MathType.REAL);
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
public static ASTNode getFormulaFromExpression(Expression expression, MathType desiredType) { 
	// Convert expression into MathML string
	String expMathMLStr = null;

	try {
		expMathMLStr = cbit.vcell.parser.ExpressionMathMLPrinter.getMathML(expression, false, desiredType);
	} catch (java.io.IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error converting expression to MathML string :" + e.getMessage());
	} catch (cbit.vcell.parser.ExpressionException e1) {
		e1.printStackTrace(System.out);
		throw new RuntimeException("Error converting expression to MathML string :" + e1.getMessage());
	}

	// Use libSBMl routines to convert MathML string to MathML document and a libSBML-readable formula string
	ASTNode mathNode = ASTNode.readMathMLFromString(expMathMLStr);
	return mathNode;
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

public String getSBMLFile() throws SbmlException, SBMLException, XMLStreamException {
	String rval = null;
	VCellSBMLDoc vdoc = convertToSBML();
	rval = vdoc.xmlString;
	return rval;
}

public VCellSBMLDoc convertToSBML() throws SbmlException, SBMLException, XMLStreamException {

	SBMLDocument sbmlDocument = new SBMLDocument(sbmlLevel,sbmlVersion);
		
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
	Element sbmlImportRelatedElement = null; // new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
//	Element biomodelElement = new Element(XMLTags.BioModelTag, sbml_vcml_ns);
//	biomodelElement.setAttribute(XMLTags.NameAttrTag, org.vcell.util.TokenMangler.mangleToSName(vcBioModel.getName())); 
//	if (vcBioModel.getVersion() != null) {
//		biomodelElement.setAttribute(XMLTags.KeyValueAttrTag, vcBioModel.getVersion().getVersionKey().toString());
//	}
//	sbmlImportRelatedElement.addContent(biomodelElement);
//	Element simSpecElement = new Element(XMLTags.SimulationSpecTag, sbml_vcml_ns);
//	simSpecElement.setAttribute(XMLTags.NameAttrTag, org.vcell.util.TokenMangler.mangleToSName(getSelectedSimContext().getName()));
//	if (getSelectedSimContext().getVersion() != null) {
//		simSpecElement.setAttribute(XMLTags.KeyValueAttrTag, getSelectedSimContext().getVersion().getVersionKey().toString());
//	}
//	sbmlImportRelatedElement.addContent(simSpecElement);
//	if (getSelectedSimulation() != null) {
//		Element simElement = new Element(XMLTags.SimulationTag, sbml_vcml_ns);
//		simElement.setAttribute(XMLTags.NameAttrTag, org.vcell.util.TokenMangler.mangleToSName(getSelectedSimulation().getName()));
//		if (getSelectedSimulation().getVersion() != null) {
//			simElement.setAttribute(XMLTags.KeyValueAttrTag, getSelectedSimulation().getVersion().getVersionKey().toString());
//		}
//		sbmlImportRelatedElement.addContent(simElement);
//	}

	// Get RDF annotation for species from SBMLAnnotationUtils
	sbmlAnnotationUtil.writeAnnotation(vcBioModel, sbmlModel, sbmlImportRelatedElement);
	
	// Now set notes, 
	sbmlAnnotationUtil.writeNotes(vcBioModel, sbmlModel);

	// write sbml document into sbml writer, so that the sbml str can be retrieved
	SBMLWriter sbmlWriter = new SBMLWriter();
	
	String sbmlStr = sbmlWriter.writeSBMLToString(sbmlDocument);
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

    SpatialModelPlugin mplugin = (SpatialModelPlugin) sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);

    // Creates a geometry object via SpatialModelPlugin object.
	org.sbml.jsbml.ext.spatial.Geometry sbmlGeometry = mplugin.createGeometry();
	sbmlGeometry.setCoordinateSystem(GeometryKind.cartesian);
	sbmlGeometry.setSpatialId("vcell");

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
	xComp.setSpatialId(vcModel.getX().getName());
	xComp.setType(CoordinateKind.cartesianX);
	final UnitDefinition sbmlUnitDef_length = getOrCreateSBMLUnit(vcModel.getUnitSystem().getLengthUnit());
	xComp.setUnits(sbmlUnitDef_length);
	Boundary minX = new Boundary();
	xComp.setBoundaryMinimum(minX);
	minX.setSpatialId("Xmin");
	minX.setValue(vcOrigin.getX());
	Boundary maxX = new Boundary();
	xComp.setBoundaryMaximum(maxX);
	maxX.setSpatialId("Xmax");
	maxX.setValue(vcOrigin.getX() + (vcExtent.getX()));
	
	org.sbml.jsbml.Parameter pX = sbmlModel.createParameter();
	pX.setId(vcModel.getX().getName());
	pX.setValue(0.0);
	pX.setConstant(false);
	pX.setUnits(sbmlUnitDef_length);
	SpatialParameterPlugin spPluginPx = (SpatialParameterPlugin) pX.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
	SpatialSymbolReference spSymRefPx = new SpatialSymbolReference();
	spPluginPx.setParamType(spSymRefPx);
	spSymRefPx.setSpatialRef(xComp.getSpatialId());
	
	// add y coordinate component
	if (dimension == 2 || dimension == 3) {
		CoordinateComponent yComp = sbmlGeometry.createCoordinateComponent();
		yComp.setSpatialId(vcModel.getY().getName());
		yComp.setType(CoordinateKind.cartesianY);
		yComp.setUnits(sbmlUnitDef_length);
		Boundary minY = new Boundary();
		yComp.setBoundaryMinimum(minY);
		minY.setSpatialId("Ymin");
		minY.setValue(vcOrigin.getY());
		Boundary maxY = new Boundary();
		yComp.setBoundaryMaximum(maxY);
		maxY.setSpatialId("Ymax");
		maxY.setValue(vcOrigin.getY() + (vcExtent.getY()));
		
		org.sbml.jsbml.Parameter pY = sbmlModel.createParameter();
		pY.setId(vcModel.getY().getName());
		pY.setValue(0.0);
		pY.setConstant(false);
		pY.setUnits(sbmlUnitDef_length);
		SpatialParameterPlugin spPluginPy = (SpatialParameterPlugin) pY.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		SpatialSymbolReference spSymRefPy = new SpatialSymbolReference();
		spPluginPy.setParamType(spSymRefPy);
		spSymRefPy.setSpatialRef(yComp.getSpatialId());
	}
	// add z coordinate component
	if (dimension == 3) {
		CoordinateComponent zComp = sbmlGeometry.createCoordinateComponent();
		zComp.setSpatialId(vcModel.getZ().getName());
		zComp.setType(CoordinateKind.cartesianZ);
		zComp.setUnits(sbmlUnitDef_length);
		Boundary minZ = new Boundary();
		zComp.setBoundaryMinimum(minZ);
		minZ.setSpatialId("Zmin");
		minZ.setValue(vcOrigin.getZ());
		Boundary maxZ = new Boundary();
		zComp.setBoundaryMaximum(maxZ);
		maxZ.setSpatialId("Zmax");
		maxZ.setValue(vcOrigin.getZ() + (vcExtent.getZ()));

		org.sbml.jsbml.Parameter pZ = sbmlModel.createParameter();
		pZ.setId(vcModel.getZ().getName());
		pZ.setValue(0.0);
		pZ.setConstant(false);
		pZ.setUnits(sbmlUnitDef_length);
		SpatialParameterPlugin spPluginPz = (SpatialParameterPlugin) pZ.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		SpatialSymbolReference spSymRefPz = new SpatialSymbolReference();
		spPluginPz.setParamType(spSymRefPz);
		spSymRefPz.setSpatialRef(zComp.getSpatialId());
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
		SpatialCompartmentPlugin cplugin = (SpatialCompartmentPlugin) comp.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		GeometryClass gc = vcStructMapping.getGeometryClass();
		if (!goodPointer(gc,GeometryClass.class,structName))  {
			continue;
		}
		CompartmentMapping compMapping = new CompartmentMapping();
		cplugin.setCompartmentMapping(compMapping);
		String geomClassName = gc.getName();
		String id = TokenMangler.mangleToSName(geomClassName + structName);
		compMapping.setSpatialId(id);
		compMapping.setDomainType(TokenMangler.mangleToSName(DOMAIN_TYPE_PREFIX+geomClassName));
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
	    domainType.setSpatialId(DOMAIN_TYPE_PREFIX+vcGeomClasses[i].getName());
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
		domain.setSpatialId(vcGeometricRegions[i].getName());
		if (vcGeometricRegions[i] instanceof VolumeGeometricRegion) {
			domain.setDomainType(DOMAIN_TYPE_PREFIX+((VolumeGeometricRegion)vcGeometricRegions[i]).getSubVolume().getName());
			
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
			domain.setDomainType(DOMAIN_TYPE_PREFIX+surfaceClass.getName());

			// adjacent domains : 2 adjacent domain objects for each surfaceClass in VC.
			// adjacent domain 1
			GeometricRegion adjGeomRegion0 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[0];
			GeometricRegion adjGeomRegion1 = vcSurfaceGeomReg.getAdjacentGeometricRegions()[1];
			AdjacentDomains adjDomain = new AdjacentDomains();
			adjDomain.setSpatialId(TokenMangler.mangleToSName(vcSurfaceGeomReg.getName()+"_"+adjGeomRegion0.getName()));
			adjDomain.setDomain1(vcSurfaceGeomReg.getName());
			adjDomain.setDomain2(adjGeomRegion0.getName());
			sbmlGeometry.addAdjacentDomain(adjDomain);
			// adj domain 2
			adjDomain = new AdjacentDomains();
			adjDomain.setSpatialId(TokenMangler.mangleToSName(vcSurfaceGeomReg.getName()+"_"+adjGeomRegion1.getName()));
			adjDomain.setDomain1(vcSurfaceGeomReg.getName());
			adjDomain.setDomain2(adjGeomRegion1.getName());
			sbmlGeometry.addAdjacentDomain(adjDomain);
		}
	}
	
	//
	// add AnalyticGeometry
	//
	if (bAnyAnalyticSubvolumes && !bAnyImageSubvolumes && !bAnyCSGSubvolumes){
		AnalyticGeometry sbmlAnalyticGeomDefinition = sbmlGeometry.createAnalyticGeometry();
		sbmlAnalyticGeomDefinition.setSpatialId(TokenMangler.mangleToSName("Analytic_"+vcGeometry.getName()));	
		sbmlAnalyticGeomDefinition.setIsActive(true);
		for (int i = 0; i < vcGeomClasses.length; i++) {
			if (vcGeomClasses[i] instanceof AnalyticSubVolume) {
				AnalyticVolume analyticVol = sbmlAnalyticGeomDefinition.createAnalyticVolume();
				analyticVol.setSpatialId(vcGeomClasses[i].getName());
				analyticVol.setDomainType(DOMAIN_TYPE_PREFIX+vcGeomClasses[i].getName());
				analyticVol.setFunctionType(FunctionKind.layered);
				analyticVol.setOrdinal(numSubVols - (i+1));
				Expression expr = ((AnalyticSubVolume)vcGeomClasses[i]).getExpression();
				try {
					String mathMLStr = ExpressionMathMLPrinter.getMathML(expr, true, MathType.BOOLEAN);
					ASTNode mathMLNode = ASTNode.readMathMLFromString(mathMLStr);
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
		CSGeometry sbmlCSGeomDefinition = new CSGeometry();
		sbmlGeometry.addGeometryDefinition(sbmlCSGeomDefinition);
		sbmlCSGeomDefinition.setSpatialId(TokenMangler.mangleToSName("CSG_"+vcGeometry.getName()));
		for (int i = 0; i < vcGeomClasses.length; i++) {
			if (vcGeomClasses[i] instanceof CSGObject) {
				CSGObject vcellCSGObject = (CSGObject)vcGeomClasses[i];
				org.sbml.jsbml.ext.spatial.CSGObject sbmlCSGObject = new org.sbml.jsbml.ext.spatial.CSGObject();
				sbmlCSGeomDefinition.addCSGObject(sbmlCSGObject);
				sbmlCSGObject.setSpatialId(vcellCSGObject.getName());
				sbmlCSGObject.setDomainType(DOMAIN_TYPE_PREFIX+vcellCSGObject.getName());
				sbmlCSGObject.setOrdinal(numSubVols - (i+1));	// the ordinal should the the least for the default/background subVolume
				org.sbml.jsbml.ext.spatial.CSGNode sbmlcsgNode = getSBMLCSGNode(vcellCSGObject.getRoot());
				sbmlCSGObject.setCSGNode(sbmlcsgNode);
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
		segmentedImageSampledFieldGeometry.setSpatialId(TokenMangler.mangleToSName("SegmentedImage_"+vcGeometry.getName()));
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
						sampledVol.setSpatialId(vcGeomClasses[j].getName());
						sampledVol.setDomainType(DOMAIN_TYPE_PREFIX+vcGeomClasses[j].getName());
						sampledVol.setSampledValue(((ImageSubVolume) vcImageGeomClasses[j]).getPixelValue());
					}
				}
				// add sampledField to sampledFieldGeometry
				SampledField segmentedImageSampledField = sbmlGeometry.createSampledField(); 
				VCImage vcImage = vcImageGeometry.getGeometrySpec().getImage();
				segmentedImageSampledField.setSpatialId("SegmentedImageSampledField");
				segmentedImageSampledField.setNumSamples1(vcImage.getNumX());
				segmentedImageSampledField.setNumSamples2(vcImage.getNumY());
				segmentedImageSampledField.setNumSamples3(vcImage.getNumZ());
				segmentedImageSampledField.setInterpolationType(InterpolationKind.nearestneighbor);
				segmentedImageSampledField.setCompression(CompressionKind.uncompressed);
				segmentedImageSampledField.setDataType(DataKind.UINT8);
				segmentedImageSampledFieldGeometry.setSampledField(segmentedImageSampledField.getId());
				/*
		if (segmentedImageSampledFieldGeometry.isSampledFieldGeometry()) {
			System.out.println("Sample field is " + segmentedImageSampledFieldGeometry.getSampledField());
		}
				 */

				try {
					byte[] vcImagePixelsBytes = vcImage.getPixels();
					//			imageData.setCompression("");
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < vcImagePixelsBytes.length; i++) {
						int uint8_sample = ((int)vcImagePixelsBytes[i]) & 0xff;
						sb.append(uint8_sample+" ");
					}
					segmentedImageSampledField.setSamplesLength(vcImage.getNumXYZ());
					segmentedImageSampledField.setSamples(sb.toString().trim());
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
			distanceMapSampledFieldGeometry.setSpatialId(TokenMangler.mangleToSName("DistanceMap_"+vcGeometry.getName()));
			SampledField distanceMapSampledField = distanceMapSampledFieldGeometry.createSampledField();
			distanceMapSampledField.setSpatialId("DistanceMapSampledField");
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
			sampledVol.setSpatialId(distanceMaps[0].getInsideSubvolumeName());
			sampledVol.setDomainType(DOMAIN_TYPE_PREFIX+distanceMaps[0].getInsideSubvolumeName());
			sampledVol.setSampledValue(255);
			sampledVol = distanceMapSampledFieldGeometry.createSampledVolume();
			sampledVol.setSpatialId(distanceMaps[1].getInsideSubvolumeName());
			sampledVol.setDomainType(DOMAIN_TYPE_PREFIX+distanceMaps[1].getInsideSubvolumeName());
			sampledVol.setSampledValue(1);
		}
*/
	}
	//
	// add "SurfaceMesh" ParametricGeometry
	//
//	if (bAnyAnalyticSubvolumes || bAnyImageSubvolumes || bAnyCSGSubvolumes){
//		ParametricGeometry sbmlParametricGeomDefinition = sbmlGeometry.createParametricGeometry();
//		sbmlParametricGeomDefinition.setSpatialId(TokenMangler.mangleToSName("SurfaceMesh_"+vcGeometry.getName()));
//		xxxx
//	}
}

private boolean goodPointer(Object obj, Class<?> clzz, String sourceName) {
	if (lg.isWarnEnabled()) {
		lg.warn(sourceName + "has no " + clzz.getSimpleName());
	}
	return obj != null;
}

public static org.sbml.jsbml.ext.spatial.CSGNode getSBMLCSGNode(cbit.vcell.geometry.CSGNode vcCSGNode) {
	String csgNodeName = vcCSGNode.getName();
	if (vcCSGNode instanceof cbit.vcell.geometry.CSGPrimitive){
		cbit.vcell.geometry.CSGPrimitive vcCSGprimitive = (cbit.vcell.geometry.CSGPrimitive)vcCSGNode; 
		org.sbml.jsbml.ext.spatial.CSGPrimitive sbmlPrimitive = new org.sbml.jsbml.ext.spatial.CSGPrimitive();
		sbmlPrimitive.setSpatialId(csgNodeName);
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
		// org.sbml.jsbml.ext.spatial.CSGPseudoPrimitive sbmlPseudoPrimitive = new org.sbml.jsbml.ext.spatial.CSGPseudoPrimitive();
		// sbmlPseudoPrimitive.setSpatialId(vcCSGNode.getName());
		throw new RuntimeException("pseudoPrimitive not yet supported in sbml export");
	}else if (vcCSGNode instanceof cbit.vcell.geometry.CSGSetOperator){
		cbit.vcell.geometry.CSGSetOperator vcCSGSetOperator = (cbit.vcell.geometry.CSGSetOperator)vcCSGNode; 
		org.sbml.jsbml.ext.spatial.CSGSetOperator sbmlSetOperator = new org.sbml.jsbml.ext.spatial.CSGSetOperator();
		sbmlSetOperator.setSpatialId(csgNodeName);
		switch (vcCSGSetOperator.getOpType()){
		case UNION: {
			sbmlSetOperator.setOperationType(SetOperation.union);
			break;
		}
		case DIFFERENCE: {
			sbmlSetOperator.setOperationType(SetOperation.difference);
			break;
		}
		case INTERSECTION: {
			sbmlSetOperator.setOperationType(SetOperation.intersection);
			break;
		}
		default: {
			throw new RuntimeException("unsupported set operation "+vcCSGSetOperator.getOpType());
		}
		}
		for (cbit.vcell.geometry.CSGNode vcChild : vcCSGSetOperator.getChildren()){
			sbmlSetOperator.addCSGNode(getSBMLCSGNode(vcChild));
		}

		return sbmlSetOperator;
	}else if (vcCSGNode instanceof cbit.vcell.geometry.CSGTransformation){
		cbit.vcell.geometry.CSGTransformation vcTransformation = (cbit.vcell.geometry.CSGTransformation)vcCSGNode;
		if (vcTransformation instanceof cbit.vcell.geometry.CSGTranslation){
			cbit.vcell.geometry.CSGTranslation vcTranslation = (cbit.vcell.geometry.CSGTranslation)vcTransformation;
			org.sbml.jsbml.ext.spatial.CSGTranslation sbmlTranslation = new org.sbml.jsbml.ext.spatial.CSGTranslation();
			sbmlTranslation.setSpatialId(csgNodeName);
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
			sbmlTranslation.setCSGNode(getSBMLCSGNode(vcTranslation.getChild()));
			return sbmlTranslation;
		}else if (vcTransformation instanceof cbit.vcell.geometry.CSGRotation){
			cbit.vcell.geometry.CSGRotation vcRotation = (cbit.vcell.geometry.CSGRotation)vcTransformation;
			org.sbml.jsbml.ext.spatial.CSGRotation sbmlRotation = new org.sbml.jsbml.ext.spatial.CSGRotation();
			sbmlRotation.setSpatialId(csgNodeName);
			sbmlRotation.setRotateAngleInRadians(vcRotation.getRotationRadians());
			sbmlRotation.setRotateAxisX(vcRotation.getAxis().getX());
			sbmlRotation.setRotateAxisY(vcRotation.getAxis().getY());
			sbmlRotation.setRotateAxisZ(vcRotation.getAxis().getZ());
			sbmlRotation.setCSGNode( getSBMLCSGNode(vcRotation.getChild()));
			return sbmlRotation;
		}else if (vcTransformation instanceof cbit.vcell.geometry.CSGScale){
			cbit.vcell.geometry.CSGScale vcScale = (cbit.vcell.geometry.CSGScale)vcTransformation;
			org.sbml.jsbml.ext.spatial.CSGScale sbmlScale = new org.sbml.jsbml.ext.spatial.CSGScale();
			sbmlScale.setSpatialId(csgNodeName);
			sbmlScale.setScaleX(vcScale.getScale().getX());
			sbmlScale.setScaleY(vcScale.getScale().getY());
			sbmlScale.setScaleZ(vcScale.getScale().getZ());
			sbmlScale.setCSGNode(getSBMLCSGNode(vcScale.getChild()));
			return sbmlScale;
		}else{
			throw new RuntimeException("unsupported transformation "+vcTransformation.getClass().getName());
		}
	}else{
		throw new RuntimeException("unsupported CSG Node "+vcCSGNode.getClass().getName()+" for SBMLSpatial export");
	}
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
 * @throws XMLStreamException 
 */
public void translateBioModel() throws SbmlException, XMLStreamException {
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

	try {
		addInitialAssignments();
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	addRateRules();
	addAssignmentRules();
	
	// Add Reactions
	addReactions();
	// Add Events
	addEvents();
}

}
