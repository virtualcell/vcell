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

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.*;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.*;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;
import cbit.vcell.math.Variable;
import cbit.vcell.math.*;
import cbit.vcell.model.*;
import cbit.vcell.model.Model;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.Model.ReservedSymbolRole;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLPrinter;
import cbit.vcell.parser.ExpressionMathMLPrinter.MathType;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.*;
import org.sbml.jsbml.ext.spatial.*;
import org.vcell.sbml.SBMLHelper;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.UnsupportedSbmlExportException;
import org.vcell.sbml.vcell.SBMLSymbolMapping.SBaseWrapper;
import org.vcell.util.*;
import org.vcell.util.document.VCellSoftwareVersion;

import javax.xml.stream.XMLStreamException;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Insert the type's description here.
 * Creation date: (3/31/2006 1:02:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SBMLExporter {

	public static boolean bWriteDebugFiles = false;

	private final static Logger logger = LogManager.getLogger(SBMLExporter.class);
	//public static final String DOMAIN_TYPE_PREFIX = "domainType_";
	public static final String DOMAIN_TYPE_PREFIX = "";
	public static final String SAMPLED_VOLUME_PREFIX = "SampledVolume_";
	private int sbmlLevel = 3;
	private int sbmlVersion = 2;
	private org.sbml.jsbml.Model sbmlModel = null;
	private cbit.vcell.biomodel.BioModel vcBioModel = null;
	private boolean bSpatial = false;

	private final boolean bRoundTripValidation;

	private SimulationContext vcSelectedSimContext = null;

	private final Map<String, UnitDefinition> vcUnitSymbolToSBMLUnit = new LinkedHashMap<>(); // to avoid repeated creation of

	private final Map<Pair <String, String>, String> l2gMap = new HashMap<>();    // local to global translation map, used for reaction parameters

	private final SBMLExportSymbolMapping sbmlExportSymbolMapping = new SBMLExportSymbolMapping();

	// used for exporting vcell-related annotations.
	public static final Namespace sbml_vcml_ns = Namespace.getNamespace(XMLTags.VCELL_NS_PREFIX, SBMLUtils.SBML_VCELL_NS);

	// SBMLAnnotationUtil to get the SBML-related annotations, notes, free-text annotations from a Biomodel VCMetaData
	private SBMLAnnotationUtil sbmlAnnotationUtil = null;

	private java.util.Hashtable<String, String> globalParamNamesHash = new java.util.Hashtable<String, String>();

	private SBMLExportSpec sbmlExportSpec = null;

	public static class MemoryVCLogger extends VCLogger {
		public final List<String> highPriority = new ArrayList<>();
		public final List<String> medPriority = new ArrayList<>();
		public final List<String> lowPriority = new ArrayList<>();

		@Override
		public boolean hasMessages() {
			return false;
		}

		@Override
		public void sendAllMessages() {
		}

		@Override
		public void sendMessage(Priority p, ErrorType et, String message)
				throws VCLoggerException {
			String msg = p + " " + et + ": " + message;
			if (p == Priority.HighPriority) {
				highPriority.add(msg);
			} else if (p == Priority.MediumPriority) {
				medPriority.add(msg);
			} else if (p == Priority.LowPriority) {
				lowPriority.add(msg);
			}
			System.err.println(p + " " + et + ": " + message);
		}
	}

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

	public SBMLExporter(SimulationContext ctx, int argSbmlLevel, int argSbmlVersion, boolean bRoundTripValidation) {
		this.vcBioModel = ctx.getBioModel();
		vcSelectedSimContext = ctx;
		sbmlLevel = argSbmlLevel;
		sbmlVersion = argSbmlVersion;
		this.bRoundTripValidation = bRoundTripValidation;
		bSpatial = validSpatial(ctx);
		sbmlAnnotationUtil = new SBMLAnnotationUtil(vcBioModel.getVCMetaData(), vcBioModel, SBMLHelper.getNamespaceFromLevelAndVersion(sbmlLevel, sbmlVersion));
		ModelUnitSystem vcModelUnitSystem = vcBioModel.getModel().getUnitSystem();
		this.sbmlExportSpec = new SBMLExportSpec(vcModelUnitSystem.getLumpedReactionSubstanceUnit(), vcModelUnitSystem.getVolumeUnit(), vcModelUnitSystem.getAreaUnit(), vcModelUnitSystem.getLengthUnit(), vcModelUnitSystem.getTimeUnit());
	}

	/**
	 * @param ctx
	 * @return true if ctx spatial and not stochastic
	 */
	private static boolean validSpatial(SimulationContext ctx) {
		boolean isSpatial = ctx.getGeometry().getDimension() > 0;
		return isSpatial && ctx.getApplicationType()==Application.NETWORK_DETERMINISTIC;
	}


	/**
 * addCompartments comment.
 * @throws XMLStreamException 
 * @throws SbmlException 
 */
private void addCompartments() throws XMLStreamException, SbmlException {
	Model vcModel = vcBioModel.getModel();
	try {
		// old vcell nonspatial application, with only relative compartment sizes (SBML wants absolute sizes for nonspatial ... easier to understand anyway)
		if (getSelectedSimContext().getGeometry().getDimension() == 0 && !getSelectedSimContext().getGeometryContext().isAllSizeSpecifiedPositive()) {
			Structure structure = getSelectedSimContext().getModel().getStructure(0);
			double structureSize = 1.0;
			StructureMapping structMapping = getSelectedSimContext().getGeometryContext().getStructureMapping(structure);
			StructureSizeSolver.updateAbsoluteStructureSizes(getSelectedSimContext(), structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());
		}
	}catch (Exception e) {
		throw new RuntimeException("Failed to solve for absolute compartment sizes for nonspatial application: "+e.getMessage(), e);
	}
	try {
		// old vcell spatial application, with only relative compartment sizes (SBML wants absolute sizes for nonspatial ... easier to understand anyway)
		if (getSelectedSimContext().getGeometry().getDimension() > 0 && !getSelectedSimContext().getGeometryContext().isAllUnitSizeParameterSetForSpatial()) {
			for (GeometryClass geometryClass : getSelectedSimContext().getGeometry().getGeometryClasses()) {
				StructureSizeSolver.updateUnitStructureSizes(getSelectedSimContext(), geometryClass);
			}
		}
	}catch (Exception e) {
		throw new RuntimeException("Failed to solve for unit sizes for spatial application: "+e.getMessage(), e);
	}
	cbit.vcell.model.Structure[] vcStructures = vcModel.getStructures();
	for (int i = 0; i < vcStructures.length; i++) {        // first we populate compartment vcell name to sbml id map
		Compartment sbmlCompartment = sbmlModel.createCompartment();
		String cName = vcStructures[i].getName();
		String sid = TokenMangler.mangleToSName(cName);
		sbmlCompartment.setId(sid);
		sbmlCompartment.setName(cName);
		sbmlExportSymbolMapping.structureToSidMap.put(vcStructures[i], sid);
		sbmlExportSymbolMapping.putSteToSidMapping(vcStructures[i].getStructureSize(), sid);
	}

	for (int i = 0; i < vcStructures.length; i++) {
		String sid = sbmlExportSymbolMapping.structureToSidMap.get(vcStructures[i]);
		Compartment sbmlCompartment = sbmlModel.getCompartment(sid);
		VCUnitDefinition sbmlSizeUnit = null;
		StructureTopology structTopology = getSelectedSimContext().getModel().getStructureTopology();
		Structure parentStructure = structTopology.getParentStructure(vcStructures[i]);
		if (vcStructures[i] instanceof Feature) {
			sbmlCompartment.setSpatialDimensions(3);
			String outside = null;
			if (parentStructure!= null) {
				outside = sbmlExportSymbolMapping.structureToSidMap.get(parentStructure);
			}
			if (outside != null) {
				if (outside.length() > 0) {
					// TODO: outside is deprecated since Level 3 Version 1
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
			Feature insideFeature = structTopology.getInsideFeature(vcMembrane);
			if (outsideFeature != null && insideFeature != null) {
				// add custom vcell annotation for the SBML compartment element
				Element compartmentTopologyElement = new Element(XMLTags.SBML_VCELL_CompartmentTopologyTag, sbml_vcml_ns);
				compartmentTopologyElement.setAttribute(XMLTags.SBML_VCELL_CompartmentTopologyTag_insideCompartmentAttr, sbmlExportSymbolMapping.structureToSidMap.get(insideFeature), sbml_vcml_ns);
				compartmentTopologyElement.setAttribute(XMLTags.SBML_VCELL_CompartmentTopologyTag_outsideCompartmentAttr, sbmlExportSymbolMapping.structureToSidMap.get(outsideFeature), sbml_vcml_ns);
				sbmlCompartment.getAnnotation().appendNonRDFAnnotation(XmlUtil.xmlToString(compartmentTopologyElement));

				sbmlCompartment.setOutside(sbmlExportSymbolMapping.structureToSidMap.get(outsideFeature)); // leave this in for level 2 support?
				sbmlSizeUnit = sbmlExportSpec.getAreaUnits();
				UnitDefinition unitDefn = getOrCreateSBMLUnit(sbmlSizeUnit);
				sbmlCompartment.setUnits(unitDefn);
			} else if (logger.isWarnEnabled()) {
				logger.warn(this.sbmlModel.getName() + " membrame "  + vcMembrane.getName()  + " has not outside feature");
			}
		}
		sbmlCompartment.setConstant(true);

		StructureMapping vcStructMapping = getSelectedSimContext().getGeometryContext().getStructureMapping(vcStructures[i]);
		try {
			if (vcStructMapping.getSizeParameter().getExpression() != null) {
				if(vcSelectedSimContext.getGeometry().getDimension() == 0) {
					sbmlCompartment.setSize(vcStructMapping.getSizeParameter().getExpression().evaluateConstant());
				} else {
					GeometryClass srcGeometryClass = vcStructMapping.getGeometryClass();
					if (srcGeometryClass!=null) {
						Expression sizeRatio = vcStructMapping.getUnitSizeParameter().getExpression();
						GeometricRegion[] srcGeometricRegions = vcSelectedSimContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(srcGeometryClass);
						if (srcGeometricRegions != null) {
							double size = 0;
							for (GeometricRegion srcGeometricRegion : srcGeometricRegions) {
								size += srcGeometricRegion.getSize();
							}
							sbmlCompartment.setSize(Expression.mult(new Expression(sizeRatio), new Expression(size)).evaluateConstant());
						}
					}else{
						sbmlCompartment.setSize(vcStructMapping.getSizeParameter().getExpression().evaluateConstant());
					}
				}
			}
		} catch (cbit.vcell.parser.ExpressionException e) {
			// If it is in the catch block, it means that the compartment size was probably not a double, but an assignment.
			// Check if the expression for the compartment size is not null and add it as an initialAssignment.
			Expression sizeExpr = vcStructMapping.getSizeParameter().getExpression();
			if (sizeExpr != null) {
				ASTNode ruleFormulaNode = getFormulaFromExpression(sizeExpr);
				InitialAssignment initialAssignment = sbmlModel.createInitialAssignment();
				initialAssignment.setVariable(vcStructures[i].getName());
				initialAssignment.setMath(ruleFormulaNode);
			}
		}

		// Add the outside compartment of given compartment as annotation to the compartment.
		// This is required later while trying to read in compartments ...

		Element sbmlImportRelatedElement = null;

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

private void setSbmlParameterValueAndUnit(Parameter vcParam, org.sbml.jsbml.Parameter sbmlParam) throws SbmlException, ExpressionException {
	// check if any event action modifies any parameter
	boolean bParamIsEventTarget = false;
	{
		BioEvent[] vcBioevents = getSelectedSimContext().getBioEvents();
		if (vcBioevents != null) {
			for (BioEvent vcEvent : vcBioevents) {
				for (EventAssignment ea : vcEvent.getEventAssignments()) {
					if (ea.getTarget() == vcParam) {
						bParamIsEventTarget = true;
					}
				}
			}
		}
	}

	cbit.vcell.mapping.AssignmentRule vcAssignmentRuleForParam = getSelectedSimContext().getAssignmentRule(vcParam);

	VCUnitDefinition vcParamUnit = vcParam.getUnitDefinition();
	if (!vcParamUnit.isTBD()) {
		sbmlParam.setUnits(getOrCreateSBMLUnit(vcParamUnit));
	}

	Expression paramExpr = new Expression(vcParam.getExpression());

	if (getSelectedSimContext().getRateRule(vcParam) != null || bParamIsEventTarget) {
		// parameter value is modified externally - need an initial condition
		if (vcAssignmentRuleForParam != null) {
			throw new SbmlException("parameter " + vcParam.getName() + " is specified by an assignment rule but is modified by an event or rate rule");
		}
		sbmlParam.setConstant(false);
		if (paramExpr.isNumeric()) {
			sbmlParam.setValue(paramExpr.evaluateConstant());
		} else {
			InitialAssignment initAssignment = sbmlModel.createInitialAssignment();
			initAssignment.setVariable(sbmlParam.getId());
			sbmlExportSymbolMapping.initialAssignmentToVcmlExpressionMap.put(initAssignment, paramExpr);
		}
	} else if (vcAssignmentRuleForParam != null) {
		sbmlParam.setConstant(false);
		Expression vcAssignmentRuleExpr = vcAssignmentRuleForParam.getAssignmentRuleExpression();
		AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
		sbmlParamAssignmentRule.setVariable(sbmlParam.getId());    // freshly created above, guaranteed to be valid
		sbmlExportSymbolMapping.assignmentRuleToVcmlExpressionMap.put(sbmlParamAssignmentRule, vcAssignmentRuleExpr);    // expression will be post-processed
	} else {
		// typical case, parameter
		if (paramExpr.isNumeric()) {
			sbmlParam.setConstant(true);
			sbmlParam.setValue(paramExpr.evaluateConstant());
		} else {
			ASTNode paramFormulaNode = getFormulaFromExpression(paramExpr);
			if (isMappedToMathDescriptionConstantOrConstantFunction(vcParam) && (vcParam instanceof SpeciesContextSpecParameter)) {
				sbmlParam.setConstant(true);
				InitialAssignment sbmlParamInitialAssignment = sbmlModel.createInitialAssignment();
				sbmlParamInitialAssignment.setVariable(sbmlParam.getId());    // freshly created above, guaranteed to be valid
				sbmlParamInitialAssignment.setMath(paramFormulaNode);
				sbmlExportSymbolMapping.initialAssignmentToVcmlExpressionMap.put(sbmlParamInitialAssignment, paramExpr);    // expression will be post-processed
			}else{
				sbmlParam.setConstant(false);
				AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
				sbmlParamAssignmentRule.setVariable(sbmlParam.getId());    // freshly created above, guaranteed to be valid
				sbmlParamAssignmentRule.setMath(paramFormulaNode);
				sbmlExportSymbolMapping.assignmentRuleToVcmlExpressionMap.put(sbmlParamAssignmentRule, paramExpr);    // expression will be post-processed
			}
		}
	}
	if (!sbmlParam.isSetConstant()){
		sbmlParam.setConstant(isMappedToMathDescriptionConstantOrConstantFunction(vcParam));
	}
}

private void addParameters() throws ExpressionException, SbmlException, XMLStreamException {

	// add VCell global parameters to the SBML listofParameters
	Model vcModel = getSelectedSimContext().getModel();
	ModelParameter[] vcGlobalParams = vcModel.getModelParameters();
	if (vcGlobalParams != null) {
		int idSuffixCounter = 0;
		for (ModelParameter vcParam : vcGlobalParams) {
			org.sbml.jsbml.Parameter sbmlParam = sbmlModel.createParameter();
			String sbmlParameterId;
			String sbmlIdBase = TokenMangler.mangleToSName(vcParam.getName());
			SBase used = sbmlModel.getSBaseById(sbmlIdBase);
			if(used == null) {
				sbmlParameterId = sbmlIdBase;
			} else {            // the mangled vcell name may be already used as id by some other sbml entity
				while(true) {    // make sure it's unique, otherwise setId will fail
					sbmlParameterId = sbmlIdBase + idSuffixCounter;
					if(sbmlModel.getSBaseById(sbmlParameterId) == null) {
						break;
					}
					idSuffixCounter++;
				}
			}
			sbmlParam.setId(sbmlParameterId);
			sbmlExportSymbolMapping.putSteToSidMapping(vcParam, sbmlParameterId);
			String sbmlName = vcParam.getSbmlName();
			if(sbmlName != null && !sbmlName.isEmpty()) {
				sbmlParam.setName(sbmlName);
			} else {    // we give it vcParam name if sbml name is missing
				sbmlParam.setName(vcParam.getName());
			}

			setSbmlParameterValueAndUnit(vcParam, sbmlParam);
		}
	}

	// add output functions, if any

	List<AnnotatedFunction> outputFunctions = vcSelectedSimContext.getOutputFunctionContext().getOutputFunctionsList();
	int idSuffixCounter = 0;
	for (AnnotatedFunction of : outputFunctions) {
		org.sbml.jsbml.Parameter sbmlParam = sbmlModel.createParameter();
		String sbmlParameterId;
		String sbmlIdBase = TokenMangler.mangleToSName(of.getName());
		SBase used = sbmlModel.getSBaseById(sbmlIdBase);
		if(used == null) {
			sbmlParameterId = sbmlIdBase;
		} else {
			while(true) {
				sbmlParameterId = sbmlIdBase + idSuffixCounter;
				if(sbmlModel.getSBaseById(sbmlParameterId) == null) {
					break;
				}
				idSuffixCounter++;
			}
		}
		sbmlParam.setId(sbmlParameterId);
		sbmlParam.setName(of.getName());
		sbmlParam.setConstant(false);
		Expression paramExpr = new Expression(of.getExpression());
		String[] symbols = paramExpr.getSymbols();
		if (symbols != null) {
			for (String symbol : symbols) {
				Variable var = vcSelectedSimContext.getMathDescription().getSourceSymbolMapping()
						.findVariableByName(symbol);
				if (var != null) { // output functions can also reference other output functions, thus variable could be null
					SymbolTableEntry ste = vcSelectedSimContext.getMathDescription().getSourceSymbolMapping()
							.getBiologicalSymbol(var)[0];
					if (ste instanceof KineticsParameter) {
						Kinetics kinetics = ((KineticsParameter) ste).getKinetics();
						if (ste == kinetics.getAuthoritativeParameter()) {
							// need to use the reaction sbmlId when refereing to reaction rate
							paramExpr.substituteInPlace(new Expression(symbol),
									new Expression(TokenMangler.mangleToSName(kinetics.getReactionStep().getName())));
						}
					}
				}
			}
		}
		ASTNode paramFormulaNode = getFormulaFromExpression(paramExpr);
		AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
		sbmlParamAssignmentRule.setVariable(sbmlParam.getId());
		sbmlParamAssignmentRule.setMath(paramFormulaNode);
		Element outputFunctionElement = new Element(XMLTags.SBML_VCELL_OutputFunctionTag, sbml_vcml_ns);
		outputFunctionElement.setAttribute(XMLTags.SBML_VCELL_OutputFunctionTag_varTypeAttr, of.getFunctionType().getTypeName(), sbml_vcml_ns);
		if (of.getDomain()!=null) {
			outputFunctionElement.setAttribute(XMLTags.SBML_VCELL_OutputFunctionTag_domainAttr, of.getDomain().getName(), sbml_vcml_ns);
		}
		sbmlParam.getAnnotation().appendNonRDFAnnotation(XmlUtil.xmlToString(outputFunctionElement));
	}

	// add membrane voltages if defined and constants
	// these may be used in expressions in various places
	// (if calculate V is set SBML export is not supported and appropriate error thrown elsewhere)

	StructureMapping structureMappings[] = vcSelectedSimContext.getGeometryContext().getStructureMappings();
	for (int i = 0; i < structureMappings.length; i++){
		if (structureMappings[i] instanceof MembraneMapping){
			StructureMappingParameter voltage = ((MembraneMapping)structureMappings[i]).getInitialVoltageParameter();
			if (voltage.getExpression().isNumeric()) {
				org.sbml.jsbml.Parameter sbmlParam = sbmlModel.createParameter();
				// we take no extra precaution for membrane voltage parameter
				sbmlParam.setId(TokenMangler.mangleToSName(((Membrane)voltage.getStructure()).getMembraneVoltage().getName()));
				sbmlParam.setConstant(true);
				sbmlParam.setValue(voltage.getConstantValue());
			}
		}
	}

	ReservedSymbol[] vcReservedSymbols = vcModel.getReservedSymbols();
	for (ReservedSymbol vcParam : vcReservedSymbols) {
		// x,y,z were exported in the addGeometry()
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
		// no extra precautions needed for reserved parameters, not even mangling is needed
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
			boolean bConstant = true;
			if (paramExpr.isNumeric()) {
				sbmlParam.setValue(paramExpr.evaluateConstant());
				if (getSelectedSimContext().getRateRule(vcParam) != null) {
					bConstant = false;
				}
			} else {
				bConstant = isMappedToMathDescriptionConstantOrConstantFunction(vcParam);
				ASTNode paramFormulaNode = getFormulaFromExpression(paramExpr);
				InitialAssignment sbmlParamInitialAssignment = sbmlModel.createInitialAssignment();
				sbmlParamInitialAssignment.setVariable(vcParam.getName());
				sbmlParamInitialAssignment.setMath(paramFormulaNode);
			}
			sbmlParam.setConstant(bConstant);
		}
		VCUnitDefinition vcParamUnit = vcParam.getUnitDefinition();
		if (!vcParamUnit.isTBD()) {
			sbmlParam.setUnits(getOrCreateSBMLUnit(vcParamUnit));
		}
	}
}


/**
 * addReactions comment.
 * @throws SbmlException 
 * @throws XMLStreamException 
 */
private void addReactions() throws SbmlException, XMLStreamException {

	// l2gMap.clear();
	if (!l2gMap.isEmpty()){
		throw new RuntimeException("expecting l2gMap to be initially empty");
	}
	int idSuffixCounter = 0;
	ReactionSpec[] vcReactionSpecs = getSelectedSimContext().getReactionContext().getReactionSpecs();
	for (int i = 0; i < vcReactionSpecs.length; i++){
		if (vcReactionSpecs[i].isExcluded()) {
			continue;
		}
		ReactionStep vcReactionStep = vcReactionSpecs[i].getReactionStep();
		//Create sbml reaction
		String rxnName = vcReactionStep.getName();
		org.sbml.jsbml.Reaction sbmlReaction = sbmlModel.createReaction();
		String sbmlReactionId;
		String sbmlIdBase = org.vcell.util.TokenMangler.mangleToSName(rxnName);
		SBase used = sbmlModel.getSBaseById(sbmlIdBase);
		if(used == null) {
			sbmlReactionId = sbmlIdBase;
		} else {            // the mangled vcell name may be already used as id by some other sbml entity
			while(true) {    // make sure it's unique, otherwise setId will fail
				sbmlReactionId = sbmlIdBase + idSuffixCounter;
				if(sbmlModel.getSBaseById(sbmlReactionId) == null) {
					break;
				}
				idSuffixCounter++;
			}
		}
		sbmlReaction.setId(sbmlReactionId);
		sbmlReaction.setName(rxnName);
		String rxnSbmlName = vcReactionStep.getSbmlName();
		if(rxnSbmlName != null && !rxnSbmlName.isEmpty()) {
			sbmlReaction.setName(rxnSbmlName);
		}

		// Get annotation (RDF and non-RDF) for reactionStep from SBMLAnnotationUtils
		sbmlAnnotationUtil.writeAnnotation(vcReactionStep, sbmlReaction, null);

		// Now set notes,
		sbmlAnnotationUtil.writeNotes(vcReactionStep, sbmlReaction);

		// Get reaction kineticLaw
		Kinetics vcRxnKinetics = vcReactionStep.getKinetics();
		org.sbml.jsbml.KineticLaw sbmlKLaw = sbmlReaction.createKineticLaw();

		try {
			Expression localRateExpr;
			Expression lumpedRateExpr;
			if (vcRxnKinetics instanceof DistributedKinetics){
				Expression temp_localRateExpr = ((DistributedKinetics) vcRxnKinetics).getReactionRateParameter().getExpression();
				if (temp_localRateExpr != null){
					localRateExpr = new Expression(temp_localRateExpr);
				}else{
					localRateExpr = null;
				}
				lumpedRateExpr = null;
			}else if (vcRxnKinetics instanceof LumpedKinetics){
				localRateExpr = null;
				Expression temp_lumpedRateExpr = ((LumpedKinetics) vcRxnKinetics).getLumpedReactionRateParameter().getExpression();
				if (temp_lumpedRateExpr != null){
					lumpedRateExpr = new Expression(temp_lumpedRateExpr);
				}else{
					lumpedRateExpr = null;
				}
			}else{
				throw new RuntimeException("unexpected Rate Law '"+vcRxnKinetics.getClass().getSimpleName()+"', not distributed or lumped type");
			}

			// Add parameters, if any, to the kineticLaw
			Kinetics.KineticsParameter[] vcKineticsParams = vcRxnKinetics.getKineticsParameters();

			// In the first pass thro' the kinetic params, store the non-numeric param names and expressions in arrays
			String[] kinParamNames = new String[vcKineticsParams.length];
			Expression[] kinParamExprs = new Expression[vcKineticsParams.length];
			for (int j = 0; j < vcKineticsParams.length; j++){
				final KineticsParameter vcKParam = vcKineticsParams[j];
				if ( true) {
					// if expression of kinetic param does not evaluate to a double, the param value is defined by a rule.
					// Since local reaction parameters cannot be defined by a rule, such parameters (with rules) are exported as global parameters.
					if ( (vcKParam.getRole() == Kinetics.ROLE_CurrentDensity && (!vcKParam.getExpression().isZero())) ||
							(vcKParam.getRole() == Kinetics.ROLE_LumpedCurrent && (!vcKParam.getExpression().isZero())) ) {
						throw new RuntimeException("Electric current not handled by SBML export; failed to export reaction \"" + vcReactionStep.getName() + "\" at this time");
					}
					if (!vcKParam.getExpression().isNumeric()) {        // NON_NUMERIC KINETIC PARAM
						// Create new name for kinetic parameter and store it in kinParamNames, store corresponding exprs in kinParamExprs
						// Will be used later to add this param as global.
						// since we already made sure that sbmlReactionId doesn't conflict with any other sid, we assume for now that
						// the combination with origParamName is also unique and don't check again
						String origParamName = vcKParam.getName();
						String newParamName = TokenMangler.mangleToSName(origParamName + "_" + sbmlReactionId);
						kinParamNames[j] = newParamName;
						kinParamExprs[j] = new Expression(vcKineticsParams[j].getExpression());
					}
				}
			} // end for (j) - first pass

			// Second pass - Check if any of the numeric parameters is present in any of the non-numeric param expressions
			// If so, these need to be added as global param (else the SBML doc will not be valid)
			int idSuffixCounterP = 0;
			for (int j = 0; j < vcKineticsParams.length; j++){
				final KineticsParameter vcKParam = vcKineticsParams[j];
				if ( (vcKParam.getRole() != Kinetics.ROLE_ReactionRate) && (vcKParam.getRole() != Kinetics.ROLE_LumpedReactionRate) ) {
					// if expression of kinetic param evaluates to a double, the parameter value is set
					if ( (vcKParam.getRole() == Kinetics.ROLE_CurrentDensity && (!vcKParam.getExpression().isZero())) ||
							(vcKParam.getRole() == Kinetics.ROLE_LumpedCurrent && (!vcKParam.getExpression().isZero())) ) {
						throw new RuntimeException("Electric current not handled by SBML export; failed to export reaction \"" + vcReactionStep.getName() + "\" at this time");
					}
					if (vcKParam.getExpression().isNumeric()) {        // NUMERIC KINETIC PARAM
						// check if it is used in other parameters that have expressions,
						boolean bAddedParam = false;
						String origParamName = vcKParam.getName();
						String newParamName = TokenMangler.mangleToSName(origParamName + "_" + sbmlReactionId);        // same as in the for loop above
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
										l2gMap.put(origParam, newParamName);    // key = vcell param name, value = sbml unique param id
										bAddedParam = true;
									} else {
										// need to get another name for param and need to change all its refereces in the other kinParam euqations.
									}
									// update the expression to contain new name, since the globalparam has new name
									kinParamExprs[k].substituteInPlace(new Expression(origParamName), new Expression(newParamName));
								}
							}
						}    // end for - k
						// If the param hasn't been added yet, it is definitely a local param. add it to kineticLaw now.
						if (!bAddedParam) {
							org.sbml.jsbml.LocalParameter sbmlKinParam = sbmlKLaw.createLocalParameter();
							String sbmlParameterId;
							String sbmlIdBaseP = TokenMangler.mangleToSName(origParamName);
							if(sbmlModel.getSBaseById(sbmlIdBaseP) == null) {
								sbmlParameterId = sbmlIdBaseP;
							} else {
								while(true) {
									sbmlParameterId = sbmlIdBaseP + idSuffixCounterP;
									if(sbmlModel.getSBaseById(sbmlParameterId) == null) {
										break;
									}
									idSuffixCounterP++;
								}
							}
							sbmlKinParam.setId(sbmlParameterId);
							sbmlKinParam.setValue(vcKParam.getConstantValue());
							logger.trace("tis constant " + sbmlKinParam.isExplicitlySetConstant());
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
					}    // end for - k
				}
			}    // end for (j)  - third pass

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
					Pair<String, String> origParam = new Pair<String, String> (rxnName, vcKineticsParams[j].getName());
					l2gMap.put(origParam, paramName);    // keeps its name but becomes a global (?)
					ASTNode paramFormulaNode = getFormulaFromExpression(kinParamExprs[j]);
					AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
					sbmlParamAssignmentRule.setVariable(paramName);
					sbmlParamAssignmentRule.setMath(paramFormulaNode);
					org.sbml.jsbml.Parameter sbmlKinParam = sbmlModel.createParameter();
					sbmlKinParam.setId(paramName);
					sbmlKinParam.setConstant(false); // because target of an assignment rule
					if (!vcKineticsParams[j].getUnitDefinition().isTBD()) {
						sbmlKinParam.setUnits(getOrCreateSBMLUnit(vcKineticsParams[j].getUnitDefinition()));
					}
//					sbmlKinParam.setConstant(isMappedToMathDescriptionConstantOrConstantFunction(vcKineticsParams[j]));
				}
			} // end for (j) - fifth pass

			// After making all necessary adjustments to the rate expression, now set the sbmlKLaw.
			// if the rate expression is constant, add it as a SBML model parameter anyway to support overrides.
			Expression rateExpr = lumpedRateExpr != null ? lumpedRateExpr : localRateExpr;
			if (lumpedRateExpr == null && !bSpatial) {
				rateExpr = Expression.mult(rateExpr, new Expression(vcReactionStep.getStructure().getStructureSize(), vcReactionStep.getNameScope()));
			}
			KineticsParameter rateParam = vcRxnKinetics.getAuthoritativeParameter();
			MathSymbolMapping msm = (MathSymbolMapping)vcSelectedSimContext.getMathDescription().getSourceSymbolMapping();
			Variable var = msm.getVariable(rateParam);
			if (var != null && var.isConstant()) {
				// make a global parameter with expression of rate parameter (set via an assignment rule)
				String newParamName = TokenMangler.mangleToSName(rateParam.getName() + "_" + vcReactionStep.getName());
				Pair<String, String> origParam = new Pair<>(rxnName, rateParam.getName());
				l2gMap.put(origParam, newParamName);
				org.sbml.jsbml.Parameter sbmlKinParam = sbmlModel.createParameter();
				sbmlKinParam.setId(newParamName);
				sbmlKinParam.setName(rateParam.getName());
				sbmlKinParam.setUnits(getOrCreateSBMLUnit(rateParam.getUnitDefinition()));
				sbmlKinParam.setConstant(false);

				// mark the global parameter as a proxy for the VCell reaction rate parameter (e.g. J or LumpedJ)
				Element rateParamElement = new Element(XMLTags.SBML_VCELL_RateParamTag, sbml_vcml_ns);
				rateParamElement.setAttribute(XMLTags.SBML_VCELL_RateParamTag_parRoleAttr, Integer.toString(rateParam.getRole()), sbml_vcml_ns);
				rateParamElement.setAttribute(XMLTags.SBML_VCELL_RateParamTag_rxIDAttr, sbmlReactionId, sbml_vcml_ns);
				sbmlKinParam.getAnnotation().appendNonRDFAnnotation(XmlUtil.xmlToString(rateParamElement));

				// create the assignment rule to set the value of the new model parameter
				ASTNode paramFormulaNode = getFormulaFromExpression(rateExpr);
				AssignmentRule sbmlParamAssignmentRule = sbmlModel.createAssignmentRule();
				sbmlParamAssignmentRule.setVariable(newParamName);
				sbmlParamAssignmentRule.setMath(paramFormulaNode);

				rateExpr = new Expression(newParamName);
			}
			ASTNode exprFormulaNode = getFormulaFromExpression(rateExpr);
			sbmlKLaw.setMath(exprFormulaNode);
		} catch (cbit.vcell.parser.ExpressionException e) {
			throw new RuntimeException("Error getting value of parameter : "+e.getMessage(), e);
		}

		// Add kineticLaw to sbmlReaction - not needed now, since we use sbmlRxn.createKLaw() ??
		//sbmlReaction.setKineticLaw(sbmlKLaw);

		// Add reactants, products, modifiers
		// Simple reactions have catalysts, fluxes have 'flux'
		cbit.vcell.model.ReactionParticipant[] rxnParticipants = vcReactionStep.getReactionParticipants();

		//
		// account for repeated reactants or products (e.g. a + a --> b) ... treat like (2a --> b)
		// reactantParticipants = [reactant("a",1), reactant("a",1), product("b",1) ]
		// stoichiometries      = [        2      ,         0      ,         1      ]
		// ... second reactant is omitted, but stoichiometry is preserved.
		//
		Integer[] stoichiometries = new Integer[rxnParticipants.length];
		for (int currIndex=0; currIndex < rxnParticipants.length ; currIndex++){
			// if already reactionParticipant of the same type, add stochiometry and mark stochiometry as null
			stoichiometries[currIndex] = rxnParticipants[currIndex].getStoichiometry();
			for (int prevIndex=0; prevIndex < currIndex; prevIndex++){
				if (rxnParticipants[prevIndex].getClass().equals(rxnParticipants[currIndex].getClass()) &&
						rxnParticipants[prevIndex].getSpeciesContext() == rxnParticipants[currIndex].getSpeciesContext()){
					stoichiometries[prevIndex] += rxnParticipants[currIndex].getStoichiometry();
					stoichiometries[currIndex] = null;
					break;
				}
			}
		}

		for (int rpIndex=0; rpIndex < rxnParticipants.length; rpIndex++){
			if (stoichiometries[rpIndex] == null){
				// skip repeated reactant or product
				continue;
			}
			ReactionParticipant rxnParticpant = rxnParticipants[rpIndex];
			SimpleSpeciesReference ssr = null;
			SpeciesReference sr = null;
			String rolePostfix = "";    // to get unique ID when the same species is both a reactant and a product
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
				sr.setStoichiometry(stoichiometries[rpIndex]); // use stoichiometry computed above
				String modelUniqueName = sbmlReactionId + '_'  + rxnParticpant.getName() + rolePostfix;
				String mangledUniqueName = TokenMangler.mangleToSName(modelUniqueName);        // probably unique because of sbmlReactionId
				sr.setId(mangledUniqueName);
				sr.setConstant(true); //SBML-REVIEW
				//int rcode = sr.appendNotes("<
				// we know that in VCell we can't override stoichiometry anywhere, below is no longer questionable
//				try {
//					SBMLHelper.addNote(sr, "VCELL guess: how do we know if reaction is constant?");
//				} catch (Exception e) {
//					lg.error(e);
//				}
			}
		}

//      Fast attribute was eliminated in L3V2		
//		sbmlReaction.setFast(vcReactionSpecs[i].isFast());
		if (vcReactionSpecs[i].isFast() || vcReactionStep instanceof FluxReaction) {
			logger.warn("WARNING: Reaction "+vcReactionSpecs[i].getDisplayName()+" is set in VCell as FAST but this attribute is no longer supported by SBML, non-VCell solvers will not simulate it in pseudo-equilibrium");
			Element compartmentTopologyElement = new Element(XMLTags.SBML_VCELL_ReactionAttributesTag, sbml_vcml_ns);
			if (vcReactionSpecs[i].isFast()) {
				compartmentTopologyElement.setAttribute(XMLTags.SBML_VCELL_ReactionAttributesTag_fastAttr, Boolean.toString(vcReactionSpecs[i].isFast()), sbml_vcml_ns);
			}
			if (vcReactionStep instanceof FluxReaction){
				compartmentTopologyElement.setAttribute(XMLTags.SBML_VCELL_ReactionAttributesTag_fluxReactionAttr, Boolean.toString(true), sbml_vcml_ns);
			}
			sbmlReaction.getAnnotation().appendNonRDFAnnotation(XmlUtil.xmlToString(compartmentTopologyElement));
		}

		// this attribute is mandatory for L3, optional for L2. So explicitly setting value.
		sbmlReaction.setReversible(true);
		Compartment reactionCompartment = sbmlModel.getCompartment(TokenMangler.mangleToSName(vcReactionStep.getStructure().getName()));
		sbmlReaction.setCompartment(reactionCompartment);

		if (bSpatial) {
			// set compartment for reaction if spatial
			String structureSid = sbmlExportSymbolMapping.structureToSidMap.get(vcReactionStep.getStructure());
			sbmlReaction.setCompartment(structureSid);
			//CORE  HAS ALT MATH true

			// set the "isLocal" attribute = true (in 'spatial' namespace) for each species
			SpatialReactionPlugin srplugin = (SpatialReactionPlugin) sbmlReaction.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
			srplugin.setIsLocal(vcRxnKinetics instanceof DistributedKinetics);
		}
	}
}

private UnitDefinition getOrCreateSBMLUnit(VCUnitDefinition vcUnit) throws SbmlException {
	if (vcUnitSymbolToSBMLUnit.containsKey(vcUnit.getSymbol())){
		return vcUnitSymbolToSBMLUnit.get(vcUnit.getSymbol());
	}
	UnitDefinition unitDefn = SBMLUnitTranslator.getSBMLUnitDefinition(vcUnit, sbmlLevel, sbmlVersion, vcBioModel.getModel().getUnitSystem());
	if (sbmlModel.getUnitDefinition(unitDefn.getId()) == null) {
		sbmlModel.addUnitDefinition(unitDefn);
		vcUnitSymbolToSBMLUnit.put(vcUnit.getSymbol(), unitDefn);
	}
	return unitDefn;
}

private BoundaryConditionKind getBoundaryConditionKind(BoundaryConditionType vcellBoundaryConditionType){
	if (vcellBoundaryConditionType.equals(BoundaryConditionType.DIRICHLET)){    // argument is vcell.math BoundaryConditionType
		return BoundaryConditionKind.Dirichlet;                                    // ret value is sbml.jsbml BoundaryConditionKind
	}else if (vcellBoundaryConditionType.equals(BoundaryConditionType.NEUMANN)){
		return BoundaryConditionKind.Neumann;
	}else if (vcellBoundaryConditionType.equals(BoundaryConditionType.PERIODIC)){
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
private void addSpecies() throws XMLStreamException, SbmlException {
	Model vcModel = vcBioModel.getModel();
	SpeciesContext[] vcSpeciesContexts = vcModel.getSpeciesContexts();
	int idSuffixCounter = 0;
	for (SpeciesContext vcSpeciesContext : vcSpeciesContexts){

		org.sbml.jsbml.Species sbmlSpecies = sbmlModel.createSpecies();
		String sbmlSpeciesId;
		String sbmlIdBase = org.vcell.util.TokenMangler.mangleToSName(vcSpeciesContext.getName());
		if(sbmlModel.getSBaseById(sbmlIdBase) == null) {
			sbmlSpeciesId = sbmlIdBase;
		} else {            // the mangled vcell name may be already used as id by some other sbml entity
			while(true) {    // make sure it's unique, otherwise setId will fail
				sbmlSpeciesId = sbmlIdBase + idSuffixCounter;
				if(sbmlModel.getSBaseById(sbmlSpeciesId) == null) {
					break;
				}
				idSuffixCounter++;
			}
		}
		sbmlSpecies.setId(sbmlSpeciesId);

		boolean bSbmlConstantAttribute = isMappedToMathDescriptionConstantOrConstantFunction(vcSpeciesContext);
		sbmlSpecies.setConstant(bSbmlConstantAttribute);

		sbmlExportSymbolMapping.putSteToSidMapping(vcSpeciesContext, sbmlSpeciesId);
		if(vcSpeciesContext.getSbmlName() != null) {
			sbmlSpecies.setName(vcSpeciesContext.getSbmlName());
		} else {
			sbmlSpecies.setName(vcSpeciesContext.getName());
		}
		// Assuming that at this point, the compartment(s) for the model are already filled in.
		String compartmentSid = sbmlExportSymbolMapping.structureToSidMap.get(vcSpeciesContext.getStructure());
		Compartment compartment = sbmlModel.getCompartment(compartmentSid);
		if (compartment != null) {
			sbmlSpecies.setCompartment(compartment.getId());
		}

		// 'hasSubstanceOnly' field will be 'false', since we use concentrations in rate law expressions.
		sbmlSpecies.setHasOnlySubstanceUnits(false);

		// Get (and set) the initial concentration value
		if (getSelectedSimContext() == null) {
			throw new RuntimeException("No simcontext (application) specified; Cannot proceed.");
		}

		// Get the speciesContextSpec in the simContext corresponding to the 'speciesContext'; and extract its initial concentration value.
		SpeciesContextSpec vcSpeciesContextsSpec = getSelectedSimContext().getReactionContext().getSpeciesContextSpec(vcSpeciesContext);
		if (bSpatial && vcSpeciesContextsSpec.isWellMixed()) {
			Element speciesContextSpecSettingsElement = new Element(XMLTags.SBML_VCELL_SpeciesContextSpecSettingsTag, sbml_vcml_ns);
			speciesContextSpecSettingsElement.setAttribute(XMLTags.SBML_VCELL_SpeciesContextSpecSettingsTag_wellmixedAttr, "true", sbml_vcml_ns);
			sbmlSpecies.getAnnotation().appendNonRDFAnnotation(XmlUtil.xmlToString(speciesContextSpecSettingsElement));
		}
		// since we are setting the substance units for species to 'molecule' or 'item', a unit that is originally in uM (or molecules/um2),
		// we need to convert concentration from uM -> molecules/um3; this can be achieved by dividing by KMOLE.
		logger.trace("in SBMLExporter");
		// for now we don't do this here and defer to the mechanisms built into the SimContext to convert and set amount instead of concentration
		// TO-DO: change to export either concentrations or amounts depending on the type of SimContext and setting
		SpeciesContextSpecParameter initialConcentrationParameter = vcSpeciesContextsSpec.getInitialConcentrationParameter();
		if (initialConcentrationParameter.getExpression() == null) {
			try {
				getSelectedSimContext().convertSpeciesIniCondition(true);
			} catch (MappingException | PropertyVetoException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		Expression initConcExp = initialConcentrationParameter.getExpression();

		Double initConcConstantValue = null;
		try {
			initConcConstantValue = initConcExp.evaluateConstantSafe();
		} catch (ExpressionException e) {
		}

		cbit.vcell.mapping.AssignmentRule vcAssignmentRule = getSelectedSimContext().getAssignmentRule(vcSpeciesContext);
		cbit.vcell.mapping.RateRule vcRateRule = getSelectedSimContext().getRateRule(vcSpeciesContext);

		if (!vcSpeciesContextsSpec.isClamped() || vcRateRule != null) {
			// species is not clamped nor assigned, we need an initial condition
			if (initConcConstantValue != null) {
//				sbmlSpecies.setConstant(true);
				sbmlSpecies.setInitialConcentration(initConcConstantValue);
			}else {
				//sbmlSpecies.setConstant(false);
				InitialAssignment initAssignment = sbmlModel.createInitialAssignment();
				initAssignment.setVariable(sbmlSpecies.getId());
				sbmlExportSymbolMapping.initialAssignmentToVcmlExpressionMap.put(initAssignment, initConcExp);
			}
		} else if (vcAssignmentRule == null){  // vcAssignmentRule's are handled elsewhere.
			if (isMappedToMathDescriptionConstantOrConstantFunction(initialConcentrationParameter)) {
				Expression vcSpeciesExpr = initConcExp;
				InitialAssignment sbmlInitialAssignment = sbmlModel.createInitialAssignment();
				sbmlInitialAssignment.setVariable(sbmlSpeciesId);
				sbmlExportSymbolMapping.initialAssignmentToVcmlExpressionMap.put(sbmlInitialAssignment, vcSpeciesExpr);    // expression will be post-processed
			} else {
				Expression vcSpeciesExpr = initConcExp;
				AssignmentRule sbmlAssignmentRule = sbmlModel.createAssignmentRule();
				sbmlAssignmentRule.setVariable(sbmlSpeciesId);
				sbmlExportSymbolMapping.assignmentRuleToVcmlExpressionMap.put(sbmlAssignmentRule, vcSpeciesExpr);    // expression will be post-processed
				sbmlSpecies.setConstant(false);
			}
		}

		// Get (and set) the boundary condition value
		boolean bBoundaryCondition = vcSpeciesContextsSpec.isClamped();
		sbmlSpecies.setBoundaryCondition(bBoundaryCondition);

		// set species substance units as 'molecules' - same as defined in the model; irrespective of it is in surface or volume.
		UnitDefinition unitDefn = getOrCreateSBMLUnit(sbmlExportSpec.getSubstanceUnits());
		sbmlSpecies.setSubstanceUnits(unitDefn);

		// need to do the following if exporting to SBML spatial
		if (bSpatial && !bBoundaryCondition) {

			// Required for setting BoundaryConditions : structureMapping for vcSpeciesContext[i] & sbmlGeometry.coordinateComponents
			StructureMapping sm = getSelectedSimContext().getGeometryContext().getStructureMapping(vcSpeciesContext.getStructure());
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
							case SpeciesContextSpec.ROLE_BoundaryValueXm:
							case SpeciesContextSpec.ROLE_BoundaryValueXp:
							case SpeciesContextSpec.ROLE_BoundaryValueYm:
							case SpeciesContextSpec.ROLE_BoundaryValueYp:
							case SpeciesContextSpec.ROLE_BoundaryValueZm:
							case SpeciesContextSpec.ROLE_BoundaryValueZp:
							case SpeciesContextSpec.ROLE_DiffusionRate:{
								break;
							}
							case SpeciesContextSpec.ROLE_InitialConcentration:
							case SpeciesContextSpec.ROLE_InitialCount: {
								continue; // done elsewhere??
								//break;
							}

							case SpeciesContextSpec.ROLE_VelocityX:
							case SpeciesContextSpec.ROLE_VelocityY:
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
									throw new RuntimeException("Unable to evalute numeric value of diffusion parameter for speciesContext '" + vcSpeciesContext + "'.", e);
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
								(((role == SpeciesContextSpec.ROLE_BoundaryValueZm) || (role == SpeciesContextSpec.ROLE_BoundaryValueZp)) && (ccZ == null)) ) {
							continue;
						}
						org.sbml.jsbml.Parameter sbmlParam = createSBMLParamFromSpeciesParam(vcSpeciesContext, (SpeciesContextSpecParameter)scsParams[j]);
						if (sbmlParam != null) {
							BoundaryConditionType vcBCType_Xm = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContext.getStructure()).getBoundaryConditionTypeXm();
							BoundaryConditionType vcBCType_Xp = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContext.getStructure()).getBoundaryConditionTypeXp();
							BoundaryConditionType vcBCType_Ym = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContext.getStructure()).getBoundaryConditionTypeYm();
							BoundaryConditionType vcBCType_Yp = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContext.getStructure()).getBoundaryConditionTypeYp();
							BoundaryConditionType vcBCType_Zm = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContext.getStructure()).getBoundaryConditionTypeZm();
							BoundaryConditionType vcBCType_Zp = vcSelectedSimContext.getGeometryContext().getStructureMapping(vcSpeciesContext.getStructure()).getBoundaryConditionTypeZp();
							SpatialParameterPlugin spplugin = (SpatialParameterPlugin)sbmlParam.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
							if (role == SpeciesContextSpec.ROLE_DiffusionRate) {
								// set diffusionCoefficient element in SpatialParameterPlugin for param
								DiffusionCoefficient sbmlDiffCoeff = new DiffusionCoefficient();
								sbmlDiffCoeff.setVariable(vcSpeciesContext.getName());
								sbmlDiffCoeff.setType(DiffusionKind.isotropic);
								sbmlDiffCoeff.setSpeciesRef(vcSpeciesContext.getName());
								spplugin.setParamType(sbmlDiffCoeff);
							}
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueXm) && (ccX != null)) {
								// set BoundaryCondn Xm element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCXm = new BoundaryCondition();
								sbmlBCXm.setType(getBoundaryConditionKind(vcBCType_Xm));
								sbmlBCXm.setVariable(vcSpeciesContext.getName());
								sbmlBCXm.setCoordinateBoundary(ccX.getBoundaryMinimum().getId());
								spplugin.setParamType(sbmlBCXm);
							}
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueXp) && (ccX != null)) {
								// set BoundaryCondn Xp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCXp = new BoundaryCondition();
								sbmlBCXp.setType(getBoundaryConditionKind(vcBCType_Xp));
								sbmlBCXp.setVariable(vcSpeciesContext.getName());
								sbmlBCXp.setCoordinateBoundary(ccX.getBoundaryMaximum().getId());
								spplugin.setParamType(sbmlBCXp);
							}
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueYm)  && (ccY != null)) {
								// set BoundaryCondn Ym element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCYm = new BoundaryCondition();
								sbmlBCYm.setType(getBoundaryConditionKind(vcBCType_Ym));
								sbmlBCYm.setVariable(vcSpeciesContext.getName());
								sbmlBCYm.setCoordinateBoundary(ccY.getBoundaryMinimum().getId());
								spplugin.setParamType(sbmlBCYm);
							}
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueYp) && (ccY != null)){
								// set BoundaryCondn Yp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCYp = new BoundaryCondition();
								sbmlBCYp.setType(getBoundaryConditionKind(vcBCType_Yp));
								sbmlBCYp.setVariable(vcSpeciesContext.getName());
								sbmlBCYp.setCoordinateBoundary(ccY.getBoundaryMaximum().getId());
								spplugin.setParamType(sbmlBCYp);
							}
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueZm)  && (ccZ != null)) {
								// set BoundaryCondn Zm element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCZm = new BoundaryCondition();
								sbmlBCZm.setType(getBoundaryConditionKind(vcBCType_Zm));
								sbmlBCZm.setVariable(vcSpeciesContext.getName());
								sbmlBCZm.setCoordinateBoundary(ccZ.getBoundaryMinimum().getId());
								spplugin.setParamType(sbmlBCZm);
							}
							if ((role == SpeciesContextSpec.ROLE_BoundaryValueZp)  && (ccZ != null)) {
								// set BoundaryCondn Zp element in SpatialParameterPlugin for param
								BoundaryCondition sbmlBCZp = new BoundaryCondition();
								sbmlBCZp.setType(getBoundaryConditionKind(vcBCType_Zp));
								sbmlBCZp.setVariable(vcSpeciesContext.getName());
								sbmlBCZp.setCoordinateBoundary(ccZ.getBoundaryMaximum().getId());
								spplugin.setParamType(sbmlBCZp);
							}
							if ((role == SpeciesContextSpec.ROLE_VelocityX) && (ccX != null)) {
								// set advectionCoeff X element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffX = new AdvectionCoefficient();
								sbmlAdvCoeffX.setVariable(vcSpeciesContext.getName());
								sbmlAdvCoeffX.setCoordinate(CoordinateKind.cartesianX);
								spplugin.setParamType(sbmlAdvCoeffX);
							}
							if ((role == SpeciesContextSpec.ROLE_VelocityY) && (ccY != null)){
								// set advectionCoeff Y element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffY = new AdvectionCoefficient();
								sbmlAdvCoeffY.setVariable(vcSpeciesContext.getName());
								sbmlAdvCoeffY.setCoordinate(CoordinateKind.cartesianY);
								spplugin.setParamType(sbmlAdvCoeffY);
							}
							if ((role == SpeciesContextSpec.ROLE_VelocityZ) && (ccZ != null)) {
								// set advectionCoeff Z element in SpatialParameterPlugin for param
								AdvectionCoefficient sbmlAdvCoeffZ = new AdvectionCoefficient();
								sbmlAdvCoeffZ.setVariable(vcSpeciesContext.getName());
								sbmlAdvCoeffZ.setCoordinate(CoordinateKind.cartesianZ);
								spplugin.setParamType(sbmlAdvCoeffZ);
							}
						}     // if sbmlParam != null
					}    // if scsParams[j] != null
				}    // end for scsParams
			}    // end scsParams != null
		} // end if (bSpatial)

		// Add the common name of species to annotation, and add an annotation element to the species.
		// This is required later while trying to read in fluxes ...
		Element sbmlImportRelatedElement = null;   //new Element(XMLTags.VCellRelatedInfoTag, sbml_vcml_ns);
//		Element speciesElement = new Element(XMLTags.SpeciesTag, sbml_vcml_ns);
//		speciesElement.setAttribute(XMLTags.NameAttrTag, TokenMangler.mangleToSName(vcSpeciesContexts[i].getSpecies().getCommonName()));
//		sbmlImportRelatedElement.addContent(speciesElement);

		// Get RDF annotation for species from SBMLAnnotationUtils
		sbmlAnnotationUtil.writeAnnotation(vcSpeciesContext.getSpecies(), sbmlSpecies, sbmlImportRelatedElement);

		// Now set notes,
		sbmlAnnotationUtil.writeNotes(vcSpeciesContext.getSpecies(), sbmlSpecies);
	}
}

	private boolean isMappedToMathDescriptionConstantOrConstantFunction(SymbolTableEntry ste) {
		MathDescription mathDesc = vcSelectedSimContext.getMathDescription();
		if (mathDesc !=null && mathDesc.getSourceSymbolMapping()!=null){
			Variable var = mathDesc.getSourceSymbolMapping().getVariable(ste);
			if (var instanceof Constant){
				return true;
			}
			if (var instanceof Function){
				try {
					Expression flattenedExp = MathUtilities.substituteFunctions(var.getExpression(), mathDesc, true).flatten();
					return flattenedExp.isNumeric();
				}catch (ExpressionException e){
					throw new RuntimeException("isMappedToMathDescriptionConstantOrConstantFunction(): flattening failed: "+e.getMessage());
				}
			}
			return false;
		} else {
			throw new RuntimeException("need math description to export SBML correctly");
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
private org.sbml.jsbml.Parameter createSBMLParamFromSpeciesParam(SpeciesContext spContext, SpeciesContextSpecParameter scsParam) throws SbmlException {
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

		// may want to add scsParam earlier
		sbmlExportSymbolMapping.putSteToSidMapping(scsParam,
				TokenMangler.mangleToSName(scsParam.getSpeciesContext().getName()+"_"+scsParam.getName()));

		param.setId(sbmlExportSymbolMapping.getSid(scsParam));
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
		throw new RuntimeException("Unable to interpret parameter '" + scsParam.getName() + "' of species : " + spContext.getName(), e);
	}
}

/**
 * Add unit definitions to the model.
 * @throws SbmlException 
 */
private void addUnitDefinitions() throws SbmlException {

//	Model vcModel = vcBioModel.getModel();
//	ModelUnitSystem vcUnitSystem = vcModel.getUnitSystem();

	sbmlModel.setSubstanceUnits(getOrCreateSBMLUnit(sbmlExportSpec.getSubstanceUnits()));

	cbit.vcell.units.VCUnitDefinition vcud = sbmlExportSpec.getVolumeUnits();
	org.sbml.jsbml.UnitDefinition ud = getOrCreateSBMLUnit(vcud);
	sbmlModel.setVolumeUnits(ud);

	sbmlModel.setAreaUnits(getOrCreateSBMLUnit(sbmlExportSpec.getAreaUnits()));
	sbmlModel.setLengthUnits(getOrCreateSBMLUnit(sbmlExportSpec.getLengthUnits()));
	sbmlModel.setTimeUnits(getOrCreateSBMLUnit(sbmlExportSpec.getTimeUnits()));
	sbmlModel.setExtentUnits(getOrCreateSBMLUnit(sbmlExportSpec.getSubstanceUnits()));

	//addKineticAndGlobalParameterUnits();
}

/** Export events */
private void addEvents() {
	BioEvent[] vcBioevents = getSelectedSimContext().getBioEvents();

	if (vcBioevents != null) {
		int idSuffixCounter = 0;
		for (BioEvent vcEvent : vcBioevents) {
			Event sbmlEvent = sbmlModel.createEvent();
			String sbmlEventId;
			String sbmlIdBase = TokenMangler.mangleToSName(vcEvent.getName());
			if(sbmlModel.getSBaseById(sbmlIdBase) == null) {
				sbmlEventId = sbmlIdBase;
			} else {            // the mangled vcell name may be already used as id by some other sbml entity
				while(true) {    // make sure it's unique, otherwise setId will fail
					sbmlEventId = sbmlIdBase + idSuffixCounter;
					if(sbmlModel.getSBaseById(sbmlEventId) == null) {
						break;
					}
					idSuffixCounter++;
				}
			}
			sbmlEvent.setId(sbmlEventId);
			sbmlEvent.setUseValuesFromTriggerTime(vcEvent.getUseValuesFromTriggerTime());

			// create trigger
			Trigger trigger = sbmlEvent.createTrigger();
			trigger.setPersistent(true);
			// NOTE: VCell solver behavior is to fire if trigger is true at timepoint 0
			// solver does work correctly if there is a non-zero delay
			// BUT - solver does not correctly simulate when the delay is 0 and when it also fires at 0
			// ----> will need a fix in solver
			// HOWEVER - the correct SBML translation requires to set the initial value to false
			trigger.setInitialValue(false);
			// get the math for the trigger in terms of variables and globals only
			try {
				Expression triggerExpr = vcEvent.generateTriggerExpression();
				Expression flattenedTrigger = MathUtilities.substituteModelParameters(triggerExpr, vcSelectedSimContext);
				// TODO - we will need to add event parameter info into VCell notes so we can recover the semantic of trigger type on roundtrip and transform back a flattened expression
				ASTNode math = getFormulaFromExpression(flattenedTrigger,MathType.BOOLEAN);
				trigger.setMath(math);
			}catch (ExpressionException e){
				throw new RuntimeException("failed to generate trigger expression for event "+vcEvent.getName()+": "+e.getMessage(), e);
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
				String sid = sbmlExportSymbolMapping.getSid(target);
				sbmlEA.setVariable(sid);
				Expression eventAssgnExpr = new Expression(vcEventAssgns.get(j).getAssignmentExpression());

				// creates a SBaseWrapper around the sbml EventAssignment to make it work as a map key
				// the sbml EventAssignment.equals() is poorly implemented, we need to compare instances
				putEventAssignment(sbmlEA, eventAssgnExpr);
//				ASTNode eaMath = getFormulaFromExpression(eventAssgnExpr);
//				sbmlEA.setMath(eaMath);
				System.out.println("EA done");
			}
			System.out.println("Event done");
		}
	}
}

/** Export rate rules  */
private void addRateRules()  {
	RateRule[] vcRateRules = getSelectedSimContext().getRateRules();

	if (vcRateRules != null) {
		for (RateRule vcRule : vcRateRules) {
			// set name
			org.sbml.jsbml.RateRule sbmlRule = sbmlModel.createRateRule();
			sbmlRule.setId(TokenMangler.mangleToSName(vcRule.getName()));

			// set rate rule variable
			String sid = sbmlExportSymbolMapping.getSid(vcRule.getRateRuleVar());
			if(sbmlModel.getSBaseById(sid) == null) {
				throw new RuntimeException("Missing rate rule variable");
			}
			sbmlRule.setVariable(sid);

			// set rate rule math/expression
			Expression vcRuleExpression = vcRule.getRateRuleExpression();
			sbmlExportSymbolMapping.rateRuleToVcmlExpressionMap.put(sbmlRule, vcRuleExpression);    // expression will be post-processed
//			ASTNode math = getFormulaFromExpression(vcRuleExpression);
//			sbmlRule.setMath(math);
		}
	}
}
private void addAssignmentRules()  {
	cbit.vcell.mapping.AssignmentRule[] vcAssignmentRules = getSelectedSimContext().getAssignmentRules();
	if (vcAssignmentRules != null) {
		for(cbit.vcell.mapping.AssignmentRule vcRule : vcAssignmentRules) {
			Expression vcRuleExpression = vcRule.getAssignmentRuleExpression();
			org.sbml.jsbml.AssignmentRule sbmlRule = sbmlModel.createAssignmentRule();
			String sid = sbmlExportSymbolMapping.getSid(vcRule.getAssignmentRuleVar());
			sbmlRule.setVariable(sid);
			sbmlRule.getVariableInstance().setConstant(false);
			sbmlExportSymbolMapping.assignmentRuleToVcmlExpressionMap.put(sbmlRule, vcRuleExpression);    // expression will be post-processed
//			ASTNode math = getFormulaFromExpression(vcRuleExpression);
//			sbmlRule.setMath(math);
		}
	}
}

private static ASTNode getFormulaFromExpression(Expression expression) {
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
private static ASTNode getFormulaFromExpression(Expression expression, MathType desiredType) {

	// first replace VCell reserved symbol t with SBML reserved symbol time, so that it gets correct translation to MathML
//	try {
//		expression.substituteInPlace(new Expression("t"), new Expression("time"));
//	} catch (ExpressionException e2) {
//		// TODO Auto-generated catch block
//		lg.error(e);
//		throw new RuntimeException(e2.toString());
//	}
//
//	// switch to libSBML for non-boolean
//	if (!desiredType.equals(MathType.BOOLEAN)) {
//		try {
//			String expr = expression.infix();
//			ASTNode math = ASTNode.parseFormula(expr);
//			return math;
//		} catch (ParseException e) {
//			// (konm * (h ^  - 1.0) / koffm)
//			lg.error(e);
//			throw new RuntimeException(e.toString());
//		}
//	}

	// Convert expression into MathML string
	String expMathMLStr = null;

	try {
		expMathMLStr = cbit.vcell.parser.ExpressionMathMLPrinter.getMathML(expression, false, desiredType, ExpressionMathMLPrinter.Dialect.SBML_SUBSET);
	} catch (IOException | ExpressionException e) {
		throw new RuntimeException("Error converting expression to MathML string :" + e.getMessage(), e);
	}

	// Use libSBMl routines to convert MathML string to MathML document and a libSBML-readable formula string
	ASTNode mathNode = ASTNode.readMathMLFromString(expMathMLStr);
	return mathNode;
}

public String getSBMLString() throws SbmlException, SBMLException, XMLStreamException, SBMLValidationException {
	String rval = null;
	VCellSBMLDoc vdoc = convertToSBML();
	rval = vdoc.xmlString;
	if (bRoundTripValidation){
		roundTripValidation();
	}
	return rval;
}

public static class SBMLValidationException extends RuntimeException {
	public SBMLValidationException(String msg, Exception e){
		super(msg, e);
	}

	public SBMLValidationException(String msg){
		super(msg);
	}
}

private void roundTripValidation() throws SBMLValidationException {

	BioModel bioModel = null;
	BioModel reread_BioModel_sbml_units = null;
	BioModel reread_BioModel_vcell_units = null;
	try {
		//
		// make a copy of the original BioModel with only the selected SimulationContext in it.
		//
		bioModel = XmlHelper.cloneBioModel(this.vcSelectedSimContext.getBioModel());
		SimulationContext[] simulationContexts = bioModel.getSimulationContexts();
		for (SimulationContext simContext : simulationContexts){
			if (!simContext.getName().equals(this.vcSelectedSimContext.getName())){
				for (Simulation sim : simContext.getSimulations()){
					simContext.removeSimulation(sim);
				}
				bioModel.removeSimulationContext(simContext);
			}
		}
		bioModel.refreshDependencies();
		// for cloned biomodel/simcontext, force no mass conservation and regenerate math for later comparison.
		bioModel.getSimulationContext(0).setUsingMassConservationModelReduction(false);
		bioModel.getSimulationContext(0).updateAll(false);

		//
		// reimport the recently exported SBML model as a BioModel (still with SBML units)
		// report a validation problem upon an exception or a High Priority VCLogger event.
		//
		MemoryVCLogger memoryVCLogger = new MemoryVCLogger();
		SBMLImporter sbmlImporter = new SBMLImporter(sbmlModel, memoryVCLogger, true);
		reread_BioModel_sbml_units = sbmlImporter.getBioModel();
		if (memoryVCLogger.highPriority.size() > 0) {
			throw new SBMLValidationException(memoryVCLogger.highPriority.toString());
		}
		BioModel reread_BioModel_sbml_units_cloned = XmlHelper.cloneBioModel(reread_BioModel_sbml_units);
		reread_BioModel_sbml_units_cloned.refreshDependencies();

		//
		// transform cloned BioModel from SBML untis back to original vcell unit system
		//
		reread_BioModel_vcell_units = ModelUnitConverter.createBioModelWithNewUnitSystem(
				reread_BioModel_sbml_units_cloned, bioModel.getModel().getUnitSystem());
		if (reread_BioModel_vcell_units == null) {
			throw new SBMLValidationException("Unable to clone BioModel: " + reread_BioModel_sbml_units_cloned.getName());
		}

		//
		// before testing for mathematical equivalence, we have to make sure the imported SimulationContext has the same mathematical framework
		//
		Application applicationType_original = bioModel.getSimulationContext(0).getApplicationType();
		Application applicationType_reread = reread_BioModel_vcell_units.getSimulationContext(0).getApplicationType();
		if (!applicationType_original.equals(applicationType_reread)) {
			//
			// replace re-read SimualtionContext with transformed SimulationContext with the same mathenmatical framework
			// (e.g. nonspatial determinstic, nonspatial stochastic, spatial deterministic)
			//
			boolean bSpatial_original = bioModel.getSimulationContext(0).getGeometry().getDimension() > 0;
			boolean bSpatial_reread = reread_BioModel_vcell_units.getSimulationContext(0).getGeometry().getDimension() > 0;
			if (bSpatial_original != bSpatial_reread){
				String failureMessage = "original application and reread application differ regarding spatial/nonspatial";
				throw new SBMLValidationException(failureMessage);
			}

			SimulationContext simulationContextToReplace = reread_BioModel_vcell_units.getSimulationContext(0);
			String simContextName = simulationContextToReplace.getName();
			SimulationContext newSimulationContext = SimulationContext.copySimulationContext(simulationContextToReplace, simContextName, bSpatial_original, applicationType_original);
			newSimulationContext.getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
			reread_BioModel_vcell_units.setSimulationContexts(new SimulationContext[] { newSimulationContext });
			reread_BioModel_vcell_units.updateAll(false);
		}

		MathDescription origMathDescription = bioModel.getSimulationContext(0).getMathDescription();
		MathDescription rereadMathDescription = reread_BioModel_vcell_units.getSimulationContext(0).getMathDescription();
		MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), origMathDescription, rereadMathDescription);
		if (!mathCompareResults.isEquivalent()) {
			String failureMessage = "MathDescriptions not equivalent after VCML->SBML->VCML: " + mathCompareResults.toDatabaseStatus();
			throw new SBMLValidationException(failureMessage);
		} else {
			logger.info("Round trip math validation passed: "+mathCompareResults.toDatabaseStatus());
		}

		if (bWriteDebugFiles) {
			try {
				String dirName = "vcell_files_"+ Integer.toString(this.vcSelectedSimContext.getBioModel().getName().hashCode());
				File outputDir = Files.createTempDirectory(dirName).toFile();
				writeIntermediateFiles(bioModel, reread_BioModel_sbml_units, reread_BioModel_vcell_units, outputDir);
				System.err.println("wrote temp files ./sbml.xml, ./orig_vcml.xml, ./reread_vcml_sbml_units.xml and ./reread_vcml.xml to "+outputDir.getAbsolutePath());
			} catch (Exception ex) {
				logger.info("error printing debug files: " + ex.getMessage());
			}
		}

	}catch (Exception e){
		try {
			String dirName = "vcell_files_"+ Integer.toString(this.vcSelectedSimContext.getBioModel().getName().hashCode());
			File outputDir = Files.createTempDirectory(dirName).toFile();
			writeIntermediateFiles(bioModel, reread_BioModel_sbml_units, reread_BioModel_vcell_units, outputDir);
		} catch (Exception ex) {
			logger.error("error printing debug files: "+ex.getMessage(), e);
		}
		logger.error("error validating SBML: "+e.getMessage(), e);
		throw new SBMLValidationException(e.getMessage(), e);
	}
}

	private void writeIntermediateFiles(BioModel bioModel, BioModel reread_BioModel_sbml_units, BioModel reread_BioModel_vcell_units, File outputDir) throws IOException, XMLStreamException, XmlParseException, MappingException {
		logger.error("writing temp files ./sbml.xml, ./orig_vcml.xml, ./reread_vcml_sbml_units.xml and ./reread_vcml.xml to "+outputDir);
		if (sbmlModel != null) {
			SBMLWriter sbmlWriter = new SBMLWriter();
			sbmlWriter.writeSBML(sbmlModel.getSBMLDocument(), Paths.get(outputDir.getAbsolutePath(), "sbml.xml").toFile());
		}
		if (bioModel != null) {
			Files.write(Paths.get(outputDir.getAbsolutePath(), "orig_vcml.xml"), XmlHelper.bioModelToXML(bioModel, false).getBytes(StandardCharsets.UTF_8));
		}
		if (reread_BioModel_sbml_units != null) {
			reread_BioModel_sbml_units.updateAll(false);
			Files.write(Paths.get(outputDir.getAbsolutePath(), "reread_vcml_sbml_units.xml"), XmlHelper.bioModelToXML(reread_BioModel_sbml_units, false).getBytes(StandardCharsets.UTF_8));
		}
		if (reread_BioModel_vcell_units != null) {
			Files.write(Paths.get(outputDir.getAbsolutePath(), "reread_vcml.xml"), XmlHelper.bioModelToXML(reread_BioModel_vcell_units, false).getBytes(StandardCharsets.UTF_8));
		}
	}

	private VCellSBMLDoc convertToSBML() throws SbmlException, SBMLException, XMLStreamException {

		SBMLDocument sbmlDocument = new SBMLDocument(sbmlLevel,sbmlVersion);
		// mark it as originating from VCell
		// TO DO expand to formally label version and build
		VCellSoftwareVersion swVersion = VCellSoftwareVersion.fromSystemProperty();
		sbmlDocument.setNotes("Exported by VCell "+swVersion.getDescription(true));

		String modelName = vcBioModel.getName() + "_" + getSelectedSimContext().getName();
		sbmlModel = sbmlDocument.createModel(TokenMangler.mangleToSName(modelName));    // it's enough to mangle, there can be no conflict at this point
		sbmlModel.setName(modelName);

		// needed?
		sbmlLevel = (int)sbmlModel.getLevel();
		sbmlVersion = (int)sbmlModel.getVersion();

		// method below was forcibly changing to item based units
		// exporter has no business changing units but export whatever was given to it
		// TODO: create method that does real checks and generates warnings as needed
//	checkUnistSystem();

		translateBioModel(sbmlDocument);

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

//	sbmlDocument.setConsistencyChecks(SBMLValidator.CHECK_CATEGORY.UNITS_CONSISTENCY, false);
//	int errors = sbmlDocument.checkConsistency();
//	SBMLErrorLog listOfErrors = sbmlDocument.getListOfErrors();


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

@Deprecated
private void checkUnistSystem() {

	// this method is forcibly changing to item based units
	// exporter has no business changing units but export whatever was given to it
	// TODO: create method that does real checks and generates warnings as needed

	// check if model to be exported to SBML has units compatible with SBML default units (default units in SBML can be assumed only until SBML Level2)
	ModelUnitSystem forcedModelUnitSystem = getSelectedSimContext().getModel().getUnitSystem();
	if (sbmlLevel < 3 && !ModelUnitSystem.isCompatibleWithDefaultSBMLLevel2Units(forcedModelUnitSystem)) {
		forcedModelUnitSystem = ModelUnitSystem.createDefaultSBMLLevel2Units();
	} else if (forcedModelUnitSystem.getVolumeSubstanceUnit().getSymbol() != "molecules"){
		// need to replace volumeSubstanceUnit; molecules is the only one that allows the exporter to create valid unit conversions of parameters
		String volumeSubstanceSymbol = "molecules";
		String membraneSubstanceSymbol = forcedModelUnitSystem.getMembraneSubstanceUnit().getSymbol();
		String lumpedReactionSubstanceSymbol = forcedModelUnitSystem.getLumpedReactionSubstanceUnit().getSymbol();
		String lengthSymbol = forcedModelUnitSystem.getLengthUnit().getSymbol();
		String areaSymbol = forcedModelUnitSystem.getAreaUnit().getSymbol();
		String volumeSymbol = forcedModelUnitSystem.getVolumeUnit().getSymbol();
		String timeSymbol = forcedModelUnitSystem.getTimeUnit().getSymbol();
		forcedModelUnitSystem = ModelUnitSystem.createVCModelUnitSystem(volumeSubstanceSymbol, membraneSubstanceSymbol, lumpedReactionSubstanceSymbol, volumeSymbol, areaSymbol, lengthSymbol, timeSymbol);
	}
	// create new Biomodel with new (SBML compatible)  unit system
	BioModel modifiedBiomodel = null;
	try {
		modifiedBiomodel = ModelUnitConverter.createBioModelWithNewUnitSystem(getSelectedSimContext().getBioModel(), forcedModelUnitSystem);
	} catch (ExpressionException | XmlParseException e) {
		throw new RuntimeException("could not convert units to SBML compatible", e);
	}
	vcBioModel = modifiedBiomodel;
	// extract the simContext from new Biomodel
	SimulationContext simContextFromModifiedBioModel = modifiedBiomodel.getSimulationContext(getSelectedSimContext().getName());
	setSelectedSimContext(simContextFromModifiedBioModel);
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
		Compartment comp = sbmlModel.getCompartment(sbmlExportSymbolMapping.structureToSidMap.get(vcStructMapping.getStructure()));
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
			throw new RuntimeException("Unable to create compartment mapping for structureMapping '" + compMapping.getId() +"' : " + e.getMessage(), e);
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
			SubVolume vcSubVolume = (SubVolume) vcGeomClasses[i];
			if (vcSubVolume instanceof AnalyticSubVolume) {
				bAnyAnalyticSubvolumes = true;
			} else if (vcSubVolume instanceof ImageSubVolume) {
				bAnyImageSubvolumes = true;
			} else if (vcSubVolume instanceof CSGObject) {
				bAnyCSGSubvolumes = true;
			}
			domainType.setSpatialDimensions(dimension);

			Element analyticSubVolumeElement = new Element(XMLTags.SBML_VCELL_SubVolumeAttributesTag, sbml_vcml_ns);
			analyticSubVolumeElement.setAttribute(XMLTags.SBML_VCELL_SubVolumeAttributesTag_handleAttr, Integer.toString(vcSubVolume.getHandle()), sbml_vcml_ns);
			StructureMapping[] structureMappings = vcGeoContext.getStructureMappings(vcSubVolume);
			for (StructureMapping structureMapping : structureMappings){
				analyticSubVolumeElement.setAttribute(XMLTags.SBML_VCELL_SubVolumeAttributesTag_defaultBCtypeXminAttr,
						structureMapping.getBoundaryConditionTypeXm().boundaryTypeStringValue(), sbml_vcml_ns);
				analyticSubVolumeElement.setAttribute(XMLTags.SBML_VCELL_SubVolumeAttributesTag_defaultBCtypeXmaxAttr,
						structureMapping.getBoundaryConditionTypeXp().boundaryTypeStringValue(), sbml_vcml_ns);
				if (vcGeometry.getDimension()>1){
					analyticSubVolumeElement.setAttribute(XMLTags.SBML_VCELL_SubVolumeAttributesTag_defaultBCtypeYminAttr,
							structureMapping.getBoundaryConditionTypeYm().boundaryTypeStringValue(), sbml_vcml_ns);
					analyticSubVolumeElement.setAttribute(XMLTags.SBML_VCELL_SubVolumeAttributesTag_defaultBCtypeYmaxAttr,
							structureMapping.getBoundaryConditionTypeYp().boundaryTypeStringValue(), sbml_vcml_ns);
				}
				if (vcGeometry.getDimension()>2){
					analyticSubVolumeElement.setAttribute(XMLTags.SBML_VCELL_SubVolumeAttributesTag_defaultBCtypeZminAttr,
							structureMapping.getBoundaryConditionTypeZm().boundaryTypeStringValue(), sbml_vcml_ns);
					analyticSubVolumeElement.setAttribute(XMLTags.SBML_VCELL_SubVolumeAttributesTag_defaultBCtypeZmaxAttr,
							structureMapping.getBoundaryConditionTypeZp().boundaryTypeStringValue(), sbml_vcml_ns);
				}
			}
			Annotation annotation = domainType.getAnnotation();
			if (annotation!=null) {
				try {
					annotation.appendNonRDFAnnotation(XmlUtil.xmlToString(analyticSubVolumeElement));
				} catch (XMLStreamException e) {
					throw new RuntimeException(e);
				}
			}

			numSubVols++;
		} else if (vcGeomClasses[i] instanceof SurfaceClass) {
			domainType.setSpatialDimensions(dimension - 1);
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
			throw new RuntimeException("Unable to generate region images for geometry", e);
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
									if (dimension > 1) {
										interiorPt.setCoord2(coordY);
										if (dimension > 2) {
											interiorPt.setCoord3(coordZ);
										}
									}
								}
								volIndx++;
							}    // end - for x
						}    // end - for y
					}    // end - for z
				}    // end if
			}    // end for regionInfos
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
				AnalyticSubVolume vcAnalyticSubVolume = (AnalyticSubVolume) vcGeomClasses[i];
				// need to avoid id name clash with structures (compartments)
				AnalyticVolume analyticVol = sbmlAnalyticGeomDefinition.createAnalyticVolume(TokenMangler.mangleToSName("AnalyticVol_"+vcAnalyticSubVolume.getName()));
//				analyticVol.setSpatialId(vcGeomClasses[i].getName());
				analyticVol.setDomainType(DOMAIN_TYPE_PREFIX+vcAnalyticSubVolume.getName());
				analyticVol.setFunctionType(FunctionKind.layered);
				analyticVol.setOrdinal(numSubVols - (i+1));

				Expression expr = ((AnalyticSubVolume)vcGeomClasses[i]).getExpression();
				try {
					String mathMLStr = ExpressionMathMLPrinter.getMathML(expr, true, MathType.BOOLEAN, ExpressionMathMLPrinter.Dialect.SBML_SUBSET);
					ASTNode mathMLNode = ASTNode.readMathMLFromString(mathMLStr);
					analyticVol.setMath(mathMLNode);
				} catch (Exception e) {
					throw new RuntimeException("Error converting VC subvolume expression to mathML" + e.getMessage(), e);
				}
			}
		}
		addGeometrySamplingAnnotation(dimension, vcGSD, sbmlAnalyticGeomDefinition);
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
				sbmlCSGObject.setOrdinal(numSubVols - (i+1));    // the ordinal should the the least for the default/background subVolume
				org.sbml.jsbml.ext.spatial.CSGNode sbmlcsgNode = getSBMLCSGNode(vcellCSGObject.getRoot());
				sbmlCSGObject.setCSGNode(sbmlcsgNode);
			}
		}
		addGeometrySamplingAnnotation(dimension, vcGSD, sbmlCSGeomDefinition);
	}
	//
	// add "Segmented" and "DistanceMap" SampledField Geometries
	//
	final boolean bVCGeometryIsImage = bAnyImageSubvolumes && !bAnyAnalyticSubvolumes && !bAnyCSGSubvolumes;
	if (bVCGeometryIsImage) {
		//
		// add "Segmented" SampledFieldGeometry
		//
		SampledFieldGeometry segmentedImageSampledFieldGeometry = sbmlGeometry.createSampledFieldGeometry();
		segmentedImageSampledFieldGeometry.setSpatialId(TokenMangler.mangleToSName("SegmentedImage_"+vcGeometry.getName()));
		segmentedImageSampledFieldGeometry.setIsActive(true);
		//55boolean bVCGeometryIsImage = bAnyImageSubvolumes && !bAnyAnalyticSubvolumes && !bAnyCSGSubvolumes;
		Geometry vcImageGeometry = vcGeometry;
		{
			// use existing image
			// make a resampled image;
//			if (dimension == 3) {
//				try {
//					ISize imageSize = vcGeometry.getGeometrySpec().getDefaultSampledImageSize();
//					vcGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
//					vcImageGeometry = RayCaster.resampleGeometry(new GeometryThumbnailImageFactoryAWT(), vcGeometry, imageSize);
//				} catch (Throwable e) {
//					lg.error(e);
//					throw new RuntimeException("Unable to convert the original analytic or constructed solid geometry to image-based geometry : " + e.getMessage());
//				}
//			} else {
//				try {
//					vcGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(),true,false);
//					GeometrySpec origGeometrySpec = vcGeometry.getGeometrySpec();
//					VCImage newVCImage = origGeometrySpec.getSampledImage().getCurrentValue();
//					//
//					// construct the new geometry with the sampled VCImage.
//					//
//					vcImageGeometry = new Geometry(vcGeometry.getName()+"_asImage", newVCImage);
//					vcImageGeometry.getGeometrySpec().setExtent(vcGeometry.getExtent());
//					vcImageGeometry.getGeometrySpec().setOrigin(vcGeometry.getOrigin());
//					vcImageGeometry.setDescription(vcGeometry.getDescription());
//					vcImageGeometry.getGeometrySurfaceDescription().setFilterCutoffFrequency(vcGeometry.getGeometrySurfaceDescription().getFilterCutoffFrequency());
//					vcImageGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(), true,true);
//				} catch (Exception e) {
//					lg.error(e);
//					throw new RuntimeException("Unable to convert the original analytic or constructed solid geometry to image-based geometry : " + e.getMessage());
//				}
//			}
			GeometryClass[] vcImageGeomClasses = vcImageGeometry.getGeometryClasses();
			for (int j = 0; j < vcImageGeomClasses.length; j++) {
				if (vcImageGeomClasses[j] instanceof ImageSubVolume) {
					ImageSubVolume vcImageSubVolume = (ImageSubVolume) vcImageGeomClasses[j];
					SampledVolume sampledVol = segmentedImageSampledFieldGeometry.createSampledVolume();
					sampledVol.setSpatialId(SAMPLED_VOLUME_PREFIX+vcImageSubVolume.getName());
					sampledVol.setDomainType(DOMAIN_TYPE_PREFIX+vcImageSubVolume.getName());
					sampledVol.setSampledValue(vcImageSubVolume.getPixelValue());
				}
			}
			// add sampledField to sampledFieldGeometry
			SampledField segmentedImageSampledField = sbmlGeometry.createSampledField();
			VCImage vcImage = vcImageGeometry.getGeometrySpec().getImage();
			segmentedImageSampledField.setSpatialId("SegmentedImageSampledField");
			segmentedImageSampledField.setNumSamples1(vcImage.getNumX());
			if (vcGeometry.getDimension()>=2) {
				segmentedImageSampledField.setNumSamples2(vcImage.getNumY());
			}
			if (vcGeometry.getDimension()==3) {
				segmentedImageSampledField.setNumSamples3(vcImage.getNumZ());
			}
			segmentedImageSampledField.setInterpolationType(InterpolationKind.nearestNeighbor);
			segmentedImageSampledField.setCompression(CompressionKind.deflated);
			segmentedImageSampledField.setDataType(DataKind.UINT8);
			segmentedImageSampledFieldGeometry.setSampledField(segmentedImageSampledField.getId());

			try {
				byte[] vcImagePixelsBytes = vcImage.getPixels();
				StringBuffer uncompressedBuffer = new StringBuffer();
				for (int i = 0; i < vcImagePixelsBytes.length; i++) {
					int uint8_sample = ((int)vcImagePixelsBytes[i]) & 0xff;
					uncompressedBuffer.append(uint8_sample+" ");
				}
				String uncompressedStringData = uncompressedBuffer.toString().trim();

				byte[] compressedData = VCImage.deflate(uncompressedStringData.getBytes(StandardCharsets.UTF_8));
				StringBuffer compressedBuffer = new StringBuffer();
				for (int i = 0; i < compressedData.length; i++) {
					int uint8_sample = ((int)compressedData[i]) & 0xff;
					compressedBuffer.append(uint8_sample+" ");
				}
				segmentedImageSampledField.setSamplesLength(compressedData.length);
				segmentedImageSampledField.setSamples(compressedBuffer.toString().trim());
			} catch (ImageException | IOException e) {
				throw new RuntimeException("Unable to export image from VCell to SBML : " + e.getMessage(), e);
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
				lg.error(e);
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

	private void addGeometrySamplingAnnotation(int dimension, GeometrySurfaceDescription vcGSD, GeometryDefinition sbmlGeomDefinition) {
		// add custom vcell annotation for the SBML compartment element
		try {
			Element geometrySamplingElement = new Element(XMLTags.SBML_VCELL_GeometrySamplingTag, sbml_vcml_ns);
			geometrySamplingElement.setAttribute(XMLTags.SBML_VCELL_GeometrySamplingTag_numXAttr, Integer.toString(vcGSD.getVolumeSampleSize().getX()), sbml_vcml_ns);
			if (dimension > 1) {
				geometrySamplingElement.setAttribute(XMLTags.SBML_VCELL_GeometrySamplingTag_numYAttr, Integer.toString(vcGSD.getVolumeSampleSize().getY()), sbml_vcml_ns);
			}
			if (dimension > 2) {
				geometrySamplingElement.setAttribute(XMLTags.SBML_VCELL_GeometrySamplingTag_numZAttr, Integer.toString(vcGSD.getVolumeSampleSize().getZ()), sbml_vcml_ns);
			}
			geometrySamplingElement.setAttribute(XMLTags.SBML_VCELL_GeometrySamplingTag_cutoffFrequencyAttr, Double.toString(vcGSD.getFilterCutoffFrequency()), sbml_vcml_ns);
			sbmlGeomDefinition.getAnnotation().appendNonRDFAnnotation(XmlUtil.xmlToString(geometrySamplingElement));
		} catch (XMLStreamException e){
			logger.error("failed to add optional vcell-specific GeometrySampling annotation to SBML", e);
		}
	}

	private boolean goodPointer(Object obj, Class<?> clzz, String sourceName) {
		if (obj == null) {
			if (logger.isWarnEnabled()) {
				logger.warn(sourceName + " has no " + clzz.getSimpleName());
			}
			return false;
		}else{
			return true;
		}
}

private static org.sbml.jsbml.ext.spatial.CSGNode getSBMLCSGNode(cbit.vcell.geometry.CSGNode vcCSGNode) {
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
			sbmlRotation.setRotateX(vcRotation.getAxis().getX());
			sbmlRotation.setRotateY(vcRotation.getAxis().getY());
			sbmlRotation.setRotateZ(vcRotation.getAxis().getZ());
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

public Map<Pair <String, String>, String> getLocalToGlobalTranslationMap() {
	return l2gMap;
}

public static void validateSimulationContextSupport(SimulationContext simulationContext) throws UnsupportedSbmlExportException {
	String applicationTypeErrorMessage = null;
	if (!simulationContext.getModel().getRbmModelContainer().isEmpty()) {
		applicationTypeErrorMessage = "Application '" + simulationContext.getName() + "' has reaction rules, SBML Export is not supported";
	}else {
		switch (simulationContext.getApplicationType()) {
			case NETWORK_DETERMINISTIC: {
				break;
			}
			case NETWORK_STOCHASTIC: {
				if (simulationContext.getGeometry().getDimension() > 0) {
					applicationTypeErrorMessage = "Application '" + simulationContext.getName() + "' is a spatial stochastic application, SBML Export is not supported";
				}
				break;
			}
			case RULE_BASED_STOCHASTIC: {
				applicationTypeErrorMessage = "Application '" + simulationContext.getName() + "' is a spatial stochastic application, SBML Export is not supported";
				break;
			}
			default: {
				applicationTypeErrorMessage = "Application '" + simulationContext.getName() + "' is " + simulationContext.getApplicationType().name() + ", SBML Export is not supported";
				break;
			}
		}
	}
	if (applicationTypeErrorMessage != null) {
		throw new UnsupportedSbmlExportException(applicationTypeErrorMessage);
	}

	// Check if any membrane has 'calculateVoltage' set, not supported for SBML Export.
	StructureMapping structureMappings[] = simulationContext.getGeometryContext().getStructureMappings();
	for (int i = 0; i < structureMappings.length; i++){
		if (structureMappings[i] instanceof MembraneMapping){
			if (((MembraneMapping)structureMappings[i]).getCalculateVoltage()){
				throw new UnsupportedSbmlExportException("Membrane '"+((MembraneMapping) structureMappings[i]).getMembrane().getName()+" has membrane potential enabled, SBML Export is not supported");
			}
		}
	}

	// Check if any reaction has electric current defined, not supported by SBML Export.
	List<KineticsParameter> kineticsParameters = Arrays.stream(simulationContext.getModel().getReactionSteps())
			.map(rs -> rs.getKinetics()).flatMap(kinetics -> Arrays.stream(kinetics.getKineticsParameters())).collect(Collectors.toList());
	for (KineticsParameter kineticsParameter : kineticsParameters) {
		if ((kineticsParameter.getRole() == Kinetics.ROLE_CurrentDensity && (!kineticsParameter.getExpression().isZero()))
				|| (kineticsParameter.getRole() == Kinetics.ROLE_LumpedCurrent && (!kineticsParameter.getExpression().isZero()))) {
			String reactionName = kineticsParameter.getKinetics().getReactionStep().getName();
			throw new UnsupportedSbmlExportException("Reaction '"+reactionName+"' has electric current defined, SBML Export is not supported");
		}
	}

	// Check whether species init uses field data, not supported by SBML export
	List<SpeciesContextSpec> scSpecs = Arrays.stream(simulationContext.getReactionContext().getSpeciesContextSpecs()).collect(Collectors.toList());
	for (SpeciesContextSpec scs : scSpecs) {
		Expression initExp = scs.getInitialConditionParameter().getExpression();
		FieldFunctionArguments[] ffas = FieldUtilities.getFieldFunctionArguments(initExp);
		if (ffas != null && ffas.length > 0) {
			throw new UnsupportedSbmlExportException("Species '"+scs.getDisplayName()+"' has FieldData as initial condition, SBML Export is not supported");
		}
	}

	// Check if there is a microscopy protocol defined (e.g. convolution)
	if (simulationContext.getMicroscopeMeasurement()!=null && simulationContext.getMicroscopeMeasurement().getFluorescentSpecies().size()>0){
		throw new UnsupportedSbmlExportException("MicroscopyMeasurement '"+simulationContext.getMicroscopeMeasurement().getName()+"' defined involving convolution with kernel (point spread function), SBML Export is not supported");
	}

	// Check if this is a spatial deterministic application and the model contains lumped kinetics.
	// VCell math has vcRegionVolume('cyt') functions, SBML round trip will use a constant size instead.
	// The round trip validation will always fail.
	Optional<ReactionStep> lumpedReaction = Arrays.stream(simulationContext.getModel().getReactionSteps()).filter(rs -> rs.getKinetics() instanceof LumpedKinetics).findFirst();
	if (simulationContext.getGeometry().getDimension()>0 && simulationContext.getApplicationType()==Application.NETWORK_DETERMINISTIC && lumpedReaction.isPresent()){
		throw new UnsupportedSbmlExportException("Lumped reaction '"+lumpedReaction.get().getName()+"' in spatial application, SBML Export is not supported");
	}

	// Check if this is a spatial application with kinematics defined (i.e. moving boundary problems)
	// The round trip validation will always fail.
	SpatialProcess[] spatialProcesses = simulationContext.getSpatialProcesses();
	if (simulationContext.getGeometry().getDimension()>0 && spatialProcesses!=null && spatialProcesses.length>0){
		throw new UnsupportedSbmlExportException("Spatial processes '"+spatialProcesses[0].getName()+"' (for cell kinematics) defined in spatial application, SBML Export is not supported");
	}
}

/**
 * VC_to_SB_Translator constructor comment.
 * @throws SbmlException 
 * @throws XMLStreamException 
 */
private void translateBioModel(SBMLDocument sbmlDocument) throws SbmlException, UnsupportedSbmlExportException, XMLStreamException {

	validateSimulationContextSupport(vcSelectedSimContext);

	sbmlExportSymbolMapping.reservedSymbolSet.clear();
	sbmlExportSymbolMapping.clear();
	sbmlExportSymbolMapping.initialAssignmentToVcmlExpressionMap.clear();
	sbmlExportSymbolMapping.assignmentRuleToVcmlExpressionMap.clear();
	sbmlExportSymbolMapping.rateRuleToVcmlExpressionMap.clear();
	sbmlExportSymbolMapping.eventAssignmentToVcmlExpressionMap.clear();
	for (ReservedSymbol rs : vcSelectedSimContext.getModel().getReservedSymbols()) {
		sbmlExportSymbolMapping.reservedSymbolSet.add(rs.getName());
	}

	// 'Parse' the Virtual cell model into an SBML model

	addUnitDefinitions();
	addCompartments();            // Add Features/Compartments
	if (bSpatial) {
		addGeometry();            // add Geometry, if present
	}
	addSpecies();                 // Add Species/SpeciesContexts
	try {
		addParameters();        // Add Parameters
	} catch (ExpressionException e) {
		throw new RuntimeException(e.getMessage(), e);
	}
	addRateRules();                // Add Rules
	addAssignmentRules();
	addReactions();                // Add Reactions
	addEvents();                // Add Events

	postProcessExpressions();
	filterUnusedReservedSymbols(sbmlDocument);
}

private SimulationContext getSelectedSimContext() {
	return vcSelectedSimContext;
}

private void setSelectedSimContext(SimulationContext vcSelectedSimContext) {
	this.vcSelectedSimContext = vcSelectedSimContext;
}

private void filterUnusedReservedSymbols(SBMLDocument sbmlDocument) throws SBMLException, XMLStreamException {
	SBMLWriter sbmlWriter = new SBMLWriter();
	String sbmlStr = sbmlWriter.writeSBMLToString(sbmlDocument);
	Model vcModel = getSelectedSimContext().getModel();
	ReservedSymbol[] vcReservedSymbols = vcModel.getReservedSymbols();
	Set<String> overriddenSymbols = new HashSet<>();
	for (Simulation sim : getSelectedSimContext().getSimulations()){
		overriddenSymbols.addAll(Arrays.asList(sim.getMathOverrides().getOverridenConstantNames()));
	}
	for (ReservedSymbol vcParam : vcReservedSymbols) {
		if(vcParam.isTime() || vcParam.isX() || vcParam.isY() || vcParam.isZ()) {
			continue;
		}
		int count = StringUtils.countMatches(sbmlStr, vcParam.getName());
		if (count == 1 && !overriddenSymbols.contains(vcParam.getName())){
			// remove if only found once (in declaration) and not overridden in any simulation
			sbmlModel.removeParameter(vcParam.getName());    // we know for sure that the sbmlParam id is same as vcmlParam name
		}
	}
}

// now that we have a complete symbolTableEntryNameToSidMap, we can substitute in all expressions the
// vCell symbols with the sbml entities id
private void postProcessExpressions() {
	// InitialAssignment
	for (Map.Entry<org.sbml.jsbml.InitialAssignment, Expression> entry : sbmlExportSymbolMapping.initialAssignmentToVcmlExpressionMap.entrySet()) {
		org.sbml.jsbml.InitialAssignment ia = entry.getKey();
		Expression vcmlExpression = entry.getValue();
		Expression sbmlExpression = substituteSteWithSid(vcmlExpression);
		ASTNode math = getFormulaFromExpression(sbmlExpression);
		ia.setMath(math);
	}
	// AssignmentRule
	for (Map.Entry<org.sbml.jsbml.AssignmentRule, Expression> entry : sbmlExportSymbolMapping.assignmentRuleToVcmlExpressionMap.entrySet()) {
		org.sbml.jsbml.AssignmentRule ar = entry.getKey();
		Expression vcmlExpression = entry.getValue();
		Expression sbmlExpression = substituteSteWithSid(vcmlExpression);
		ASTNode math = getFormulaFromExpression(sbmlExpression);
		ar.setMath(math);
		ar.getVariableInstance().setConstant(false);
	}
	// RateRule
	for (Map.Entry<org.sbml.jsbml.RateRule, Expression> entry : sbmlExportSymbolMapping.rateRuleToVcmlExpressionMap.entrySet()) {
		org.sbml.jsbml.RateRule rr = entry.getKey();
		Expression vcmlExpression = entry.getValue();
		Expression sbmlExpression = substituteSteWithSid(vcmlExpression);
		ASTNode math = getFormulaFromExpression(sbmlExpression);
		rr.setMath(math);
	}
	// Events
	for (Map.Entry<SBaseWrapper<org.sbml.jsbml.EventAssignment>, Expression> entry : sbmlExportSymbolMapping.eventAssignmentToVcmlExpressionMap.entrySet()) {
		SBaseWrapper<org.sbml.jsbml.EventAssignment> eaWrapper = entry.getKey();
		Expression vcmlExpression = entry.getValue();
		Expression sbmlExpression = substituteSteWithSid(vcmlExpression);
		ASTNode eaMath = getFormulaFromExpression(sbmlExpression);
		org.sbml.jsbml.EventAssignment ea = eaWrapper.getSBase();
		ea.setMath(eaMath);
	}
	System.out.println("Finished postProcessExpressions");
}

private Expression substituteSteWithSid(Expression vcExpression) {
	String[] symbols = vcExpression.getSymbols();
	Expression sidExpression = new Expression(vcExpression);
	if(symbols == null || symbols.length == 0) {
		return sidExpression;
	}
	try {
		for(String symbol : symbols) {
			SymbolTableEntry ste = vcExpression.getSymbolBinding(symbol);
			if(SBMLImporter.isRestrictedXYZT(symbol, vcBioModel, bSpatial) || sbmlExportSymbolMapping.reservedSymbolSet.contains(symbol)) {
				continue;
			}
			String sid = sbmlExportSymbolMapping.getSid(ste);
			if(sid == null) {
				String msg = "no sbml Sid found for vcell symbol: " + symbol;
				logger.error(msg);
				throw new RuntimeException(msg);
			}
			sidExpression.substituteInPlace(new Expression(symbol), new Expression(sid));
		}
	} catch(Exception e) {
		String msg = "Substituting Ste with Sid failed, " + e.getMessage();
		logger.error(msg, e);
		throw new RuntimeException(msg, e);
	}
	return sidExpression;
}

void putEventAssignment(org.sbml.jsbml.EventAssignment _ea, Expression vcellExpression) {
    SBaseWrapper<org.sbml.jsbml.EventAssignment> eaWrapper = new SBaseWrapper<org.sbml.jsbml.EventAssignment>(_ea);
    Expression exp = sbmlExportSymbolMapping.eventAssignmentToVcmlExpressionMap.get(eaWrapper);
    if (exp != null && exp != vcellExpression) {
        throw new RuntimeException("sbml event assignment is already bound to an expression " + exp.infix() + ", trying to bind to expression " + vcellExpression.infix());
    } else {
		sbmlExportSymbolMapping.eventAssignmentToVcmlExpressionMap.put(eaWrapper, vcellExpression);
    }
}


}
