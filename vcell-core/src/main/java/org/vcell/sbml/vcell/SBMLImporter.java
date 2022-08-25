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

import cbit.image.VCImage;
import cbit.image.VCImageCompressed;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.*;
import cbit.vcell.geometry.CSGPrimitive.PrimitiveType;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.CSGSetOperator.OperatorType;
import cbit.vcell.geometry.RegionImage.RegionInfo;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.*;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.BioEvent.TriggerType;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.model.*;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.parser.*;
import cbit.vcell.render.Vect3d;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;
import cbit.vcell.xml.XMLTags;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.RateRule;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.*;
import org.sbml.jsbml.ext.spatial.CSGHomogeneousTransformation;
import org.sbml.jsbml.ext.spatial.CSGPseudoPrimitive;
import org.sbml.jsbml.ext.spatial.CSGRotation;
import org.sbml.jsbml.ext.spatial.CSGScale;
import org.sbml.jsbml.ext.spatial.CSGSetOperator;
import org.sbml.jsbml.ext.spatial.CSGTransformation;
import org.sbml.jsbml.ext.spatial.*;
import org.sbml.jsbml.util.filters.IdFilter;
import org.sbml.jsbml.xml.XMLNode;
import org.vcell.sbml.SBMLHelper;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.vcell.SBMLImportException.Category;
import org.vcell.util.*;
import org.vcell.util.BeanUtils.CastInfo;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.document.BioModelChildSummary;

import javax.swing.tree.TreeNode;
import javax.xml.stream.XMLStreamException;
import java.beans.PropertyVetoException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class SBMLImporter {

	private final SBMLSymbolMapping sbmlSymbolMapping = new SBMLSymbolMapping();

	private final static Logger logger = LogManager.getLogger(SBMLImporter.class);

	public static class SBMLIssueSource implements Issue.IssueSource {
		public final SBase issueSource;

		public SBMLIssueSource(SBase issueSource) {
			this.issueSource = issueSource;
		}
	}

	/**
	 * keywords for VCLogger error message; used to detect specific error
	 */
	public static final String RESERVED_SPATIAL = "reserved spatial";

	/**
	 * detect unsupported "delay" element
	 */
	private static final String DELAY_URL = "www.sbml.org/sbml/symbols/delay";

	private static final String TIME_SYMBOL_OVERRIDE = "___TIME_SYMBOL___";

	private final InputStream sbmlInputStream;
	private final String sbmlFileName;
	private org.sbml.jsbml.Model sbmlModel = null;
	private final VCLogger vcLogger;
	private final boolean bValidateSBML;
	private boolean isFromVCell;

	// issue list for medium-level warnings while importing
	private final Vector<Issue> localIssueList = new Vector<>();
	private final IssueContext issueContext = new IssueContext();

	// for VCell specific annotation
	private final static String RATE_NAME = XMLTags.ReactionRateTag;
	private final static String SPECIES_NAME = XMLTags.SpeciesTag;
	private final static String REACTION = XMLTags.ReactionTag;
	private final static String OUTSIDE_COMP_NAME = XMLTags.OutsideCompartmentTag;

	public SBMLImporter(org.sbml.jsbml.Model sbmlModel, VCLogger argVCLogger, boolean bValidateSBML) {
		this((String) null, argVCLogger, bValidateSBML);
		if (sbmlModel == null) throw new NullPointerException("Model must not be null");
		this.sbmlModel = sbmlModel;
	}
	public SBMLImporter(String argSbmlFileName, VCLogger argVCLogger, boolean bValidateSBML) {
		super();
		this.sbmlInputStream = null;
		this.sbmlFileName = argSbmlFileName;
		this.vcLogger = argVCLogger;
		this.bValidateSBML = bValidateSBML;
	}

	public SBMLImporter(InputStream sbmlInputStream, VCLogger argVCLogger, boolean bValidateSBML) {
		super();
		this.sbmlInputStream = sbmlInputStream;
		this.sbmlFileName = null;
		this.vcLogger = argVCLogger;
		this.bValidateSBML = bValidateSBML;
	}

	public SBMLSymbolMapping getSymbolMapping() {
		return this.sbmlSymbolMapping;
	}

	private static Expression flattenUnbound(Expression originalExpression, SimulationContext simContext, Map<String, SymbolTableEntry> entryMap, int depth) throws ExpressionException {
		if(depth > 20) {
			throw new ExpressionException("Too many iterations.");
		}
		Model model = simContext.getModel();
		GeometryContext gc = simContext.getGeometryContext();
		
		String[] originalSymbols = originalExpression.getSymbols();
		Expression newExpression = new Expression(originalExpression);
		if (originalSymbols != null) {
			for (String symbol : originalSymbols) {
				SymbolTableEntry ste = entryMap.get(symbol);
				Expression steExp = ste.getExpression();
				if (ste instanceof Structure.StructureSize) {
					Structure symbolStructure = model.getStructure(symbol);
					StructureMapping.StructureMappingParameter mapping = gc.getStructureMapping(symbolStructure).getSizeParameter();
					steExp = mapping.getExpression();
				}
				if (steExp == null) {
					throw new ExpressionException("Symbol expression is null.");
				}
				newExpression.substituteInPlace(new Expression(symbol), new Expression(steExp));
			}
		}
		String[] newSymbols = newExpression.getSymbols();
		if(newSymbols == null || newSymbols.length == 0) {
			double constant = newExpression.evaluateConstant();
			return new Expression(constant);
		} else if(newSymbols.length != originalSymbols.length) {
			depth++;
			return flattenUnbound(newExpression, simContext, entryMap, depth);
		} else {
			Arrays.sort(originalSymbols);
			Arrays.sort(newSymbols);
			for(int i=0; i<newSymbols.length; i++) {
				if(!originalSymbols[i].contentEquals(newSymbols[i])) {
					depth++;
					return flattenUnbound(newExpression, simContext, entryMap, depth);
				}
			}
			throw new ExpressionException("Expressions identical.");	// some symbols can't be replaced with numbers, it means they're variables
		}
	}

	private static void finalizeCompartments(org.sbml.jsbml.Model sbmlModel, BioModel vcBioModel, SBMLSymbolMapping sbmlSymbolMapping, Vector<Issue> localIssueList, IssueContext issueContext) {
		SimulationContext simContext = vcBioModel.getSimulationContext(0);
		GeometryContext gc = simContext.getGeometryContext();
		Map<String, SymbolTableEntry> entryMap = new HashMap<>();
		simContext.getEntries(entryMap);

		for (Compartment compartment : sbmlModel.getListOfCompartments()){
			Structure struct = sbmlSymbolMapping.getStructure(compartment);
			StructureMapping.StructureMappingParameter mappingParam = gc.getStructureMapping(struct).getSizeParameter();
			Expression origSizeExpr = mappingParam.getExpression();
			
			// the only chance to evaluate the structure size expression (set by an initial assignment) as a constant
			// if we fail here, it may be a species variable involved in the expression, or who knows what else, which we don't have yet
			try {
				Expression sizeExpr = flattenUnbound(origSizeExpr, simContext, entryMap, 0);	// if we were succesful, it should be a number
				double constant = sizeExpr.evaluateConstant();
				mappingParam.setExpression(new Expression(constant));
				String msg = "Initial assignment for Structure '" + struct.getName() + "' from expression '" + origSizeExpr.infix() + "' was succesfully converted to a number.";
				localIssueList.add(new Issue(vcBioModel, issueContext, IssueCategory.SBMLImport_RestrictedFeature, msg, Issue.Severity.WARNING));
			} catch (ExpressionException e) {
				String msg = "Failed to set the initial assignment for symbol '" + struct.getName() + "' from expression '" + origSizeExpr.infix() + "'. Ignored.";
				logger.error(msg, e);
				localIssueList.add(new Issue(vcBioModel, issueContext, IssueCategory.SBMLImport_UnsupportedAttributeOrElement, msg, Issue.Severity.WARNING));
			}
		}
	}
	
	private static void addCompartments(org.sbml.jsbml.Model sbmlModel, int geometryDimension, BioModel vcBioModel,
										  SBMLSymbolMapping sbmlSymbolMapping,
										  SBMLAnnotationUtil sbmlAnnotationUtil, VCLogger vcLogger) {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf<Compartment> listofCompartments = sbmlModel.getListOfCompartments();
		if (listofCompartments == null) {
			throw new SBMLImportException("Cannot have 0 compartments in model");
		}
		// Using a vector here - since there can be SBML models with only
		// features and no membranes.
		// Hence keeping the datastructure flexible.
		List<Structure> structList = new ArrayList<>();
		java.util.HashMap<String, Structure> structureNameMap = new java.util.HashMap<>();

		try {
			int structIndx = 0;
			// First pass - create the structures
			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				org.sbml.jsbml.Compartment compartment = listofCompartments.get(i);
				String compartmentSid = compartment.getId();
				if (!compartment.isSetSpatialDimensions() || compartment.getSpatialDimensions() == 3) {
					Feature feature = new Feature(compartmentSid);
					sbmlSymbolMapping.putStructure(compartment, feature);
					sbmlSymbolMapping.putRuntime(compartment, feature.getStructureSize());
					structList.add(structIndx, feature);
					structureNameMap.put(compartmentSid, feature);
				} else if (compartment.getSpatialDimensions() == 2) { // spatial dimensions is set (see clause above)
					Membrane membrane = new Membrane(compartmentSid);
					sbmlSymbolMapping.putStructure(compartment, membrane);
					sbmlSymbolMapping.putRuntime(compartment, membrane.getStructureSize());
					structList.add(structIndx, membrane);
					structureNameMap.put(compartmentSid, membrane);
				} else {
					String msg = "compartment '"+compartment.getId()+"' has spatial dimension " + compartment.getSpatialDimensions() + ", not currently supported.";
					vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.CompartmentError, msg);
					// adding a feature anyway so that it fails gracefully
					Feature feature = new Feature(compartmentSid);
					sbmlSymbolMapping.putStructure(compartment, feature);
					sbmlSymbolMapping.putRuntime(compartment, feature.getStructureSize());
					structList.add(structIndx, feature);
					structureNameMap.put(compartmentSid, feature);
				}
				if (compartment.isSetName()) {
					structList.get(structIndx).setSbmlName(compartment.getName());					
				}
		
				structIndx++;
				sbmlAnnotationUtil.readAnnotation(structList.get(i), compartment);
				sbmlAnnotationUtil.readNotes(structList.get(i), compartment);
			}

			// Second pass - connect the structures
			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				org.sbml.jsbml.Compartment sbmlCompartment = listofCompartments.get(i);
				String outsideCompartmentId = null;
				if (sbmlCompartment.getOutside() != null && sbmlCompartment.getOutside().length() > 0) {
					// compartment.getOutside returns the Sid of the 'outside'
					// compartment, so get the compartment from model.
					outsideCompartmentId = sbmlCompartment.getOutside();
				} else {
					Element sbmlImportRelatedElement = sbmlAnnotationUtil.readVCellSpecificAnnotation(sbmlCompartment);
					if (sbmlImportRelatedElement != null) {
						Element embeddedVCellElement = sbmlImportRelatedElement.getChild(OUTSIDE_COMP_NAME, Namespace.getNamespace(SBMLUtils.SBML_VCELL_NS));
						if (embeddedVCellElement != null) {
							outsideCompartmentId = embeddedVCellElement.getAttributeValue(XMLTags.NameTag);
						}
					}
				}
				if (outsideCompartmentId != null) {
					Compartment outsideCompartment = sbmlModel.getCompartment(outsideCompartmentId);
					Structure outsideStructure = structureNameMap.get(outsideCompartment.getId());
					Structure struct = structureNameMap.get(sbmlCompartment.getId());
					struct.setSbmlParentStructure(outsideStructure);
				}
			}

			// set the structures in vc vcBioModel.getSimulationContext(0)
			Structure[] structures = structList.toArray(new Structure[0]);
			Model model = vcBioModel.getSimulationContext(0).getModel();
			model.setStructures(structures);

			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				org.sbml.jsbml.Compartment sbmlCompartment = listofCompartments.get(i);
				//
				// set the inside and outside features in the structure topology if present in the vcell-specific annotation.
				//
				XMLNode compartmentNonRdfAnnotation = sbmlCompartment.getAnnotation().getNonRDFannotation();
				if (compartmentNonRdfAnnotation != null) {
					XMLNode compartmentTopologyElement = compartmentNonRdfAnnotation.getChildElement(XMLTags.SBML_VCELL_CompartmentTopologyTag, "*");
					if (compartmentTopologyElement != null) {
						Structure struct = structureNameMap.get(sbmlCompartment.getId());
						if (struct instanceof Membrane) {
							Membrane membrane = (Membrane) struct;
							int insideAttrIndex = compartmentTopologyElement.getAttrIndex(XMLTags.SBML_VCELL_CompartmentTopologyTag_insideCompartmentAttr, SBMLUtils.SBML_VCELL_NS);
							if (insideAttrIndex >= 0) {
								String insideCompartmentSid = compartmentTopologyElement.getAttrValue(insideAttrIndex);
								Compartment insideCompartment = sbmlModel.getCompartment(insideCompartmentSid);
								Structure insideStructure = structureNameMap.get(insideCompartment.getId());
								model.getStructureTopology().setInsideFeature(membrane, (Feature) insideStructure);
							}
							int outsideAttrIndex = compartmentTopologyElement.getAttrIndex(XMLTags.SBML_VCELL_CompartmentTopologyTag_outsideCompartmentAttr, SBMLUtils.SBML_VCELL_NS);
							if (outsideAttrIndex >= 0) {
								String outsideCompartmentSid = compartmentTopologyElement.getAttrValue(outsideAttrIndex);
								Compartment outsideCompartment = sbmlModel.getCompartment(outsideCompartmentSid);
								Structure outsideStructure = structureNameMap.get(outsideCompartment.getId());
								model.getStructureTopology().setOutsideFeature(membrane, (Feature) outsideStructure);
							}
						}
					}
				}
			}

			// Third pass thro' the list of compartments : set the sizes on the
			// structureMappings - can be done only after setting
			// the structures on vcBioModel.getSimulationContext(0).
			boolean allSizesSet = true;
			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				org.sbml.jsbml.Compartment compartment = listofCompartments.get(i);
				SymbolContext symbolContext = SymbolContext.INITIAL;
				Expression sbmlSizeExpr = sbmlSymbolMapping.getRuleSBMLExpression(compartment, symbolContext);
				if (sbmlSizeExpr != null){
					if (!sbmlSizeExpr.isNumeric()) {
						// We are NOT handling non-numeric compartment sizes at this time ...
						String errMsg = "Compartment '" + compartment.getId() + "' size has an" +
								" assignment or initial assignment rule which is not a numeric value," +
								" cannot handle it at this time. (we should look supporting non-numeric constant expressions)";
						vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.CompartmentError, errMsg);
						logger.error(errMsg);
						sbmlSizeExpr = null;
					}
				}

				// no init assignment or assignment rule; create expression from 'size' attribute,
				if (sbmlSizeExpr == null && compartment.isSetSize()) {
					sbmlSizeExpr = new Expression(compartment.getSize());
				}

				if (sbmlSizeExpr == null) {
					String errMsg = "compartment " + compartment.getId() + " size is not set in SBML document.";
					vcLogger.sendMessage(VCLogger.Priority.MediumPriority, VCLogger.ErrorType.CompartmentError, errMsg);
					logger.warn(errMsg);
					allSizesSet = false;
				} else {
					// Now set the size of the compartment.
					Structure struct = model.getStructure(compartment.getId());
					GeometryContext gc = vcBioModel.getSimulationContext(0).getGeometryContext();
					StructureMapping.StructureMappingParameter mappingParam = gc.getStructureMapping(struct).getSizeParameter();
					sbmlSymbolMapping.putInitial(compartment, mappingParam);
					Expression vcellExpr = adjustExpression(sbmlModel, sbmlSizeExpr, mappingParam.getNameScope(), sbmlSymbolMapping, symbolContext);
					mappingParam.setExpression(vcellExpr);
				}
			}

			// Handle the absolute size to surface_vol/volFraction conversion if all sizes are set
			if (allSizesSet && geometryDimension == 0) {
				StructureSizeSolver.updateRelativeStructureSizes(vcBioModel.getSimulationContext(0));
			}
		} catch (Exception e) {
			throw new SBMLImportException("Error adding Feature to vcModel " + e.getMessage(), e);
		}
	}

	private static void addEvents(org.sbml.jsbml.Model sbmlModel, boolean bSpatial, BioModel vcBioModel,
									LambdaFunction[] lambdaFunctions, SBMLSymbolMapping sbmlSymbolMapping,
									VCLogger vcLogger) {

		if (sbmlModel.getNumEvents() > 0) {
			// VCell does not support events in spatial model
			if (vcBioModel.getSimulationContext(0).getGeometry().getDimension() > 0) {
				throw new SBMLImportException("Events are not supported in a spatial VCell model.");
			}

			ListOf<Event> listofEvents = sbmlModel.getListOfEvents();

			for (int i = 0; i < sbmlModel.getNumEvents(); i++) {
				try {
					Event event = listofEvents.get(i);

					// create bioevent
					String eventName = event.getId();
					if (eventName == null || eventName.length() == 0) {
						eventName = TokenMangler.mangleToSName(event.getName());
						// if event name is still null, get free event name from
						// vcBioModel.getSimulationContext(0).
						if (eventName.length() == 0) {
							eventName = vcBioModel.getSimulationContext(0).getFreeEventName(null);
						}
					}
					
					// delay
					BioEvent vcEvent = new BioEvent(eventName, TriggerType.GeneralTrigger, true, vcBioModel.getSimulationContext(0));

					sbmlSymbolMapping.putBioEvent(event, vcEvent);

					if (event.isSetDelay()) {
						ParameterContext.LocalParameter triggerDelayParameter = vcEvent.getParameter(BioEventParameterType.TriggerDelay);
						sbmlSymbolMapping.putRuntime(event.getDelay(), triggerDelayParameter);

						Expression sbmlDurationExpr = getExpressionFromFormula(event.getDelay().getMath(), lambdaFunctions, vcLogger);
						Expression vcellDurationExpr = adjustExpression(sbmlModel, sbmlDurationExpr, triggerDelayParameter.getNameScope(), sbmlSymbolMapping, SymbolContext.RUNTIME);
						triggerDelayParameter.setExpression(vcellDurationExpr);

						boolean bUseValsFromTriggerTime = true;
						if (event.isSetUseValuesFromTriggerTime()) {
							bUseValsFromTriggerTime = event.isSetUseValuesFromTriggerTime();
						}
						if (!vcellDurationExpr.isZero()) {
							bUseValsFromTriggerTime = false;
						}
						vcEvent.setUseValuesFromTriggerTime(bUseValsFromTriggerTime);
					}
					
					cbit.vcell.mapping.ParameterContext.LocalParameter vcTriggerParameter = vcEvent.getParameter(BioEventParameterType.GeneralTriggerFunction);
					sbmlSymbolMapping.putRuntime(event.getTrigger(), vcTriggerParameter);


					Expression sbmlTriggerExpr = null;
					if (event.isSetTrigger()) {
						sbmlTriggerExpr = getExpressionFromFormula(event.getTrigger().getMath(), lambdaFunctions, vcLogger);
					}
					if (sbmlTriggerExpr != null){
						Expression vcellTriggerExpr = adjustExpression(sbmlModel, sbmlTriggerExpr, vcTriggerParameter.getNameScope(), sbmlSymbolMapping, SymbolContext.RUNTIME);
						vcTriggerParameter.setExpression(vcellTriggerExpr);
					}else{
						vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.SchemaValidation, "trigger expression not set for sbml Event "+event.getId());
					}

					// event assignments
					ArrayList<EventAssignment> vcEvntAssgnList = new ArrayList<>();
					for (int j = 0; j < event.getNumEventAssignments(); j++) {
						org.sbml.jsbml.EventAssignment sbmlEvntAssgn = event.getEventAssignment(j);
						SBase eventAssignTargetSBase = findSBase(sbmlModel, sbmlEvntAssgn.getVariable());
						EditableSymbolTableEntry vcellEventAssignTarget = sbmlSymbolMapping.getRuntimeSte(eventAssignTargetSBase);
						if (vcellEventAssignTarget != null) {
							Expression sbmlEventAssignmentExpr = getExpressionFromFormula(sbmlEvntAssgn.getMath(), lambdaFunctions, vcLogger);
							Expression vcellEventAssignmentExpr = adjustExpression(sbmlModel, sbmlEventAssignmentExpr, vcEvent.getNameScope(), sbmlSymbolMapping, SymbolContext.RUNTIME);
							EventAssignment vcEventAssignment = vcEvent.new EventAssignment(vcellEventAssignTarget, vcellEventAssignmentExpr);
							sbmlSymbolMapping.putRuntime(sbmlEvntAssgn, vcellEventAssignTarget);
							vcEvntAssgnList.add(vcEventAssignment);
						} else {
							String msg = "No symbolTableEntry for '" + eventAssignTargetSBase + "'; Cannot add event assignment.";
							vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.UnsupportedConstruct, msg);
							logger.error(msg);
						}
					}

					vcEvent.setEventAssignmentsList(vcEvntAssgnList);
					vcEvent.bind();
					vcBioModel.getSimulationContext(0).addBioEvent(vcEvent);
				} catch (Exception e) {
					throw new SBMLImportException(e.getMessage(), e);
				} // end - try/catch
			} // end - for(sbmlEvents)
		} // end - if numEvents > 0)
	}

	private static void addCompartmentTypes(org.sbml.jsbml.Model sbmlModel, Vector<Issue> localIssueList, IssueContext issueContext, BioModel vcBioModel) {
		if (sbmlModel.getNumCompartmentTypes() > 0) {
//			throw new SBMLImportException("VCell doesn't support CompartmentTypes at this time");
			localIssueList.add(new Issue(vcBioModel, issueContext, IssueCategory.SBMLImport_RestrictedFeature,
					"VCell doesn't support CompartmentTypes at this time, ignoring. Please check the generated math for consistency.", Issue.Severity.WARNING));
		}
	}

	private static void addSpeciesTypes(org.sbml.jsbml.Model sbmlModel, Vector<Issue> localIssueList, IssueContext issueContext, BioModel vcBioModel) {
		if (sbmlModel.getNumSpeciesTypes() > 0) {
//			throw new SBMLImportException("VCell doesn't support SpeciesTypes at this time");
			localIssueList.add(new Issue(vcBioModel, issueContext, IssueCategory.SBMLImport_RestrictedFeature,
					"VCell doesn't support SpeciesTypes at this time, ignoring. Please check the generated math for consistency.", Issue.Severity.WARNING));
		}
	}

	private static void addConstraints(org.sbml.jsbml.Model sbmlModel, VCLogger vcLogger) {
		if (sbmlModel.getNumConstraints() > 0) {
			throw new SBMLImportException("VCell doesn't support Constraints at this time");
		}
	}


	// Hard to detect bugs may occur when replacing the lambda function formal arguments with the actual arguments
	// when they overlap - for example formal arguments xyz and actual arguments azc will result in a substitution error
	// because of z being both a formal and an actual argument
	// in the above example, an expression like x+y+z won't result in a+z+c as expected, but a+c+c
	// the code involved is in the substitute method of the LambdaFunction class
	// to prevent this from happening, we create unique formal arguments based on the ones provided in the
	// lambda function definition and the lambda function id
	final static String FormalArgumentSuffix = "LambdaFunctionFormalArgument";
	private static LambdaFunction[] addFunctionDefinitions(org.sbml.jsbml.Model sbmlModel, VCLogger vcLogger) {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf<FunctionDefinition> listofFunctionDefinitions = sbmlModel.getListOfFunctionDefinitions();
		if (listofFunctionDefinitions == null) {
			logger.debug("No Function Definitions");
			return new LambdaFunction[0];
		}
		// The function definitions contain lambda function definition.
		// Each lambda function has a name, (list of) argument(s), function body
		// which is represented as a math element.
		LambdaFunction[] lambdaFunctions = new LambdaFunction[(int) sbmlModel.getNumFunctionDefinitions()];
		try {
			for (int i = 0; i < sbmlModel.getNumFunctionDefinitions(); i++) {
				FunctionDefinition fnDefn = listofFunctionDefinitions.get(i);
				String functionName = fnDefn.getId();

				if (fnDefn.isSetMath()) {
					ASTNode math = fnDefn.getMath();
					// Function body.
					if (math.getNumChildren() == 0) {
						logger.debug("(no function body defined)");
						continue;
					}
					// Add function arguments into vector, print args
					// Note that lambda function always should have at least 2 children
					Vector<String> argsVector = new Vector<>();
					Vector<String> secureArgsVector = new Vector<>();
					for (int j = 0; j < math.getNumChildren() - 1; ++j) {
						String baseName = math.getChild(j).getName();
						argsVector.addElement(baseName);
						secureArgsVector.addElement(baseName + "_" + functionName + FormalArgumentSuffix);
					}
					String[] functionArgs = secureArgsVector.toArray(new String[0]);
					math = math.getChild(math.getNumChildren() - 1);
					// formula = libsbml.formulaToString(math);
					Expression fnExpr = getExpressionFromFormula(math, lambdaFunctions, vcLogger);
					for(int j=0; j<argsVector.size(); j++) {
						fnExpr.substituteInPlace(new Expression(argsVector.get(j)), new Expression(secureArgsVector.get(j)));
					}
					lambdaFunctions[i] = new LambdaFunction(functionName, fnExpr, functionArgs);
				}
			}
			return lambdaFunctions;
		} catch (Exception e) {
			throw new SBMLImportException("Error adding Lambda function" + e.getMessage(), e);
		}
	}
	
	/**
	 * addParameters : Adds global parameters from SBML model to VCell model. If
	 * expression for global parameter contains species, creates a conc_factor
	 * parameter (conversion from SBML - VCell conc units) and adds this factor
	 * to VC global params list, and replaces occurances of 'sp' with
	 * 'sp*concFactor' in original param expression.
	 */

	private static void addParameters(org.sbml.jsbml.Model sbmlModel, org.sbml.jsbml.ext.spatial.Geometry sbmlGeometry,
										BioModel vcBioModel, boolean bSpatial, Map<String, VCUnitDefinition> sbmlUnitIdentifierMap,
										SBMLSymbolMapping sbmlSymbolMapping) throws Exception {
		ListOf<Parameter> listofGlobalParams = sbmlModel.getListOfParameters();
		if (listofGlobalParams == null) {
			logger.debug("No Global Parameters");
			return;
		}
		Model vcModel = vcBioModel.getModel();
		ArrayList<ModelParameter> vcModelParamsList = new ArrayList<>();

		// create a hash of reserved symbols so that if there is any reserved
		// symbol occurring as a global parameter in the SBML model,
		// the hash can be used to check for reserved symbols, so that it will
		// not be added as a global parameter in VCell,
		// since reserved symbols cannot be used as other variables (species,
		// structureSize, parameters, reactions, etc.).
		HashSet<String> reservedSymbolHash = new HashSet<>();
		for (ReservedSymbol rs : vcModel.getReservedSymbols()) {
			reservedSymbolHash.add(rs.getName());
		}

		ModelUnitSystem modelUnitSystem = vcModel.getUnitSystem();
		for (int i = 0; i < sbmlModel.getNumParameters(); i++) {
			Parameter sbmlGlobalParam = listofGlobalParams.get(i);
			String sbmlParamId = sbmlGlobalParam.getId();
			String sbmlParamName = sbmlGlobalParam.getName();
			SpatialParameterPlugin spplugin = null;
			if (bSpatial) {
				// check if parameter id is x/y/z : if so, check if its
				// 'spatialSymbolRef' child's spatial id and type are non-empty.
				// If so, the parameter represents a spatial element.
				// If not, throw an exception, since a parameter that does not
				// represent a spatial element cannot have an id of x/y/z

				spplugin = (SpatialParameterPlugin) sbmlGlobalParam.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
				if (sbmlParamId.equals("x") || sbmlParamId.equals("y") || sbmlParamId.equals("z")) {
					boolean bSpatialParam = (spplugin!=null && spplugin.getParamType() instanceof SpatialSymbolReference);

					// if (a) and (b) are not true, for param with id x/y/z,
					// throw exception - not allowed.
					// if (a) and (b) are true, continue with the next parameter
					if (!bSpatialParam) {
						throw new RuntimeException("Parameter '" + sbmlParamId + "' is not a spatial parameter : Cannot have a variable in VCell named '"
							+ sbmlParamId + "' unless it is a spatial variable.");
					} else {
						// go to the next parameter - do not add the spatial
						// parameter to the list of vcell parameters.
						continue;
					}
				}
			}

			// if SBML model is spatial, check if param represents species
			// diffusion/advection/boundary condition parameters for 'spatial' extension
			if (bSpatial) {
				VCAssert.assertTrue(spplugin != null, "invalid initialization logic");
				ParameterType sbmlParamType = spplugin.getParamType();

				final SpeciesContext paramSpContext;
				final SpeciesContextSpec vcSpContextsSpec;
				// Check for diffusion coefficient(s)
				if (sbmlParamType instanceof DiffusionCoefficient){
					DiffusionCoefficient diffCoeff = (DiffusionCoefficient)sbmlParamType;
					if (diffCoeff.isSetVariable()) {
						// find corresponding vcell diffusion parameter and map to this sbml global parameter
						String variableSid = diffCoeff.getVariable();
						SBase sBase = findSBase(sbmlModel, variableSid);
						SymbolTableEntry diffTargetSte = sbmlSymbolMapping.getRuntimeSte(sBase);
						if (diffTargetSte instanceof SpeciesContext){
							paramSpContext = (SpeciesContext) diffTargetSte;
							vcSpContextsSpec = vcBioModel.getSimulationContext(0).getReactionContext().getSpeciesContextSpec(paramSpContext);
							SpeciesContextSpecParameter diffusionParameter = vcSpContextsSpec.getDiffusionParameter();
							sbmlSymbolMapping.putRuntime(sbmlGlobalParam, diffusionParameter);
							if (sbmlGlobalParam.isSetValue()) {
								double sbmlValue = sbmlGlobalParam.getValue();
								sbmlSymbolMapping.putSbmlValue(sbmlGlobalParam, sbmlValue);
							}
						}else{
							logger.error("failed to resolve diffusion coefficient variable (speciesContext) sid="+variableSid+" during SBML import");
							logger.error("add VCLogger event");
						}
						continue; // do not add this sbml parameter to vcell, it already maps to an existing parameter.
					}else{
						logger.error("missing diffusion coefficient variable for sid="+diffCoeff.getId()+" during SBML import");
						logger.error("add VCLogger event");
					}
				}

				// Check for advection coefficient(s)
				if (sbmlParamType instanceof AdvectionCoefficient){
					AdvectionCoefficient advCoeff = (AdvectionCoefficient)sbmlParamType; 
					if (advCoeff.isSetVariable()) {
						String variableSid = advCoeff.getVariable();
						SBase advTargetSBase = findSBase(sbmlModel, variableSid);
						SymbolTableEntry advTargetSte = sbmlSymbolMapping.getRuntimeSte(advTargetSBase);
						if (advTargetSte instanceof SpeciesContext) {
							paramSpContext = (SpeciesContext) advTargetSte;
							vcSpContextsSpec = vcBioModel.getSimulationContext(0).getReactionContext().getSpeciesContextSpec(paramSpContext);
							CoordinateKind coordKind = advCoeff.getCoordinate();
							final SpeciesContextSpecParameter scsAdvParam;
							switch (coordKind){
							case cartesianX:{
								scsAdvParam = vcSpContextsSpec.getParameterFromRole(SpeciesContextSpec.ROLE_VelocityX);
								break;
							}
							case cartesianY:{
								scsAdvParam = vcSpContextsSpec.getParameterFromRole(SpeciesContextSpec.ROLE_VelocityY);
								break;
							}
							case cartesianZ:{
								scsAdvParam = vcSpContextsSpec.getParameterFromRole(SpeciesContextSpec.ROLE_VelocityZ);
								break;
							}
							default: {
								throw new SBMLImportException("unexpected Coordiante Kind "+coordKind);
							}
							}
							sbmlSymbolMapping.putRuntime(sbmlGlobalParam, scsAdvParam);
							if (sbmlGlobalParam.isSetValue()) {
								double sbmlValue = sbmlGlobalParam.getValue();
								sbmlSymbolMapping.putSbmlValue(sbmlGlobalParam, sbmlValue);
							}
						}else{
							logger.error("failed to resolve advection coefficient variable (speciesContext) for sid="+variableSid+" during SBML import");
							logger.error("add VCLogger event");
						}
						continue; // do not add this sbml parameter to vcell, it already maps to an existing parameter.
					}else{
						logger.error("missing advection coefficient variable (speciesContext) for sid="+advCoeff.getId()+" during SBML import");
						logger.error("add VCLogger event");
					}
				}
				
				// Check for Boundary condition(s)
				if (sbmlParamType instanceof BoundaryCondition){
					BoundaryCondition bCondn = (BoundaryCondition)sbmlParamType;
					if (bCondn.isSetVariable()) {
						// get the var of boundaryCondn; find appropriate spContext in vcell;
						// set the BC param of its speciesContextSpec to param value.
						String variableSid = bCondn.getVariable();
						SBase sBase = findSBase(sbmlModel, variableSid);
						SymbolTableEntry bcondTargetSte = sbmlSymbolMapping.getRuntimeSte(sBase);
						if (bcondTargetSte instanceof SpeciesContext) {
							paramSpContext = (SpeciesContext) bcondTargetSte;
							vcSpContextsSpec = vcBioModel.getSimulationContext(0).getReactionContext().getSpeciesContextSpec(paramSpContext);
							SpeciesContextSpecParameter scsParam = null;
							for (CoordinateComponent coordComp : sbmlGeometry.getListOfCoordinateComponents()) {
								String bcMinSpatialId = coordComp.getBoundaryMinimum().getSpatialId();
								String bcMaxSpatialId = coordComp.getBoundaryMaximum().getSpatialId();
								String coordinateBoundary = bCondn.getCoordinateBoundary();
								if (coordinateBoundary.equals(bcMinSpatialId)) {
									switch (coordComp.getType()){
									case cartesianX:{
										scsParam = vcSpContextsSpec.getBoundaryXmParameter();
										break;
									}
									case cartesianY:{
										scsParam = vcSpContextsSpec.getBoundaryYmParameter();
										break;
									}
									case cartesianZ:{
										scsParam = vcSpContextsSpec.getBoundaryZmParameter();
										break;
									}
									default:{
										throw new SBMLImportException("unexpected coordinate component type "+coordComp.getType());
									}
									}
								} else if (coordinateBoundary.equals(bcMaxSpatialId)){
									switch (coordComp.getType()){
									case cartesianX:{
										scsParam = vcSpContextsSpec.getBoundaryXpParameter();
										break;
									}
									case cartesianY:{
										scsParam = vcSpContextsSpec.getBoundaryYpParameter();
										break;
									}
									case cartesianZ:{
										scsParam = vcSpContextsSpec.getBoundaryZpParameter();
										break;
									}
									default:{
										throw new SBMLImportException("unexpected coordinate component type "+coordComp.getType());
									}
									}
								}
							}
							if (scsParam != null) {
								sbmlSymbolMapping.putRuntime(sbmlGlobalParam, scsParam);
								if (sbmlGlobalParam.isSetValue()) {
									double sbmlValue = sbmlGlobalParam.getValue();
									sbmlSymbolMapping.putSbmlValue(sbmlGlobalParam, sbmlValue);
								}
							} else {
								throw new SBMLImportException("could not find corresponding vcell boundary condition for sbml parameter "+sbmlParamId);
							}
						}else{
							logger.error("failed to resolve boundary condition variable (speciesContext) for sid="+variableSid+" during SBML import");
							logger.error("add VCLogger event");
						}
						continue; // do not add this sbml parameter to vcell, it already maps to an existing parameter.
					}else{
						logger.error("missing boundary condition variable for sid="+bCondn.getId()+" during SBML import");
						logger.error("add VCLogger event");
					}
				}
				
				// Check for Boundary condition(s)
				if (sbmlParamType instanceof SpatialSymbolReference){
					SpatialSymbolReference spatialSymbolRef = (SpatialSymbolReference)sbmlParamType;
					throw new RuntimeException("generic Spatial Symbol References not yet supported, unresolved spatial reference '"+spatialSymbolRef.getSpatialRef()+"'");	
				}
			}
					
			// Finally, create and add model parameter to VC model (as it didn't map to a predefined parameter in spatial .. from above)
			ModelParameter vcellModelParameter = vcModel.getModelParameter(sbmlParamId);
			if (vcellModelParameter == null) {
				VCUnitDefinition glParamUnitDefn = sbmlUnitIdentifierMap.get(sbmlGlobalParam.getUnits());
				// if units for param were not defined, don't let it be null;
				// set it to TBD or check if it was dimensionless.
				if (glParamUnitDefn == null) {
					glParamUnitDefn = modelUnitSystem.getInstance_TBD();
				}

				String vcParamName = sbmlParamId;
				// special treatment for x,y,z
				if(isRestrictedXYZT(sbmlParamId, vcBioModel, bSpatial) || reservedSymbolHash.contains(sbmlParamId)) {
					vcParamName = "param_" + sbmlParamId;
				}
				//
				// treat KMOLE special
				//
				ReservedSymbol KMOLE = vcModel.getKMOLE();
				double KMOLE_value;
				try {
					KMOLE_value = KMOLE.getExpression().evaluateConstant();
				} catch (Exception e){ throw new RuntimeException("unexpected exception: "+e.getMessage(),e); }
				if (sbmlGlobalParam.getId().equals(KMOLE.getName()) && sbmlGlobalParam.isSetValue() && sbmlGlobalParam.getValue()==KMOLE_value) {
					sbmlSymbolMapping.putRuntime(sbmlGlobalParam, KMOLE);
					continue;
				}

				//
				// not KMOLE, map to a new VCell parameter
				//
				ModelParameter vcGlobalParam = vcModel.new ModelParameter(vcParamName, new Expression(0.0), Model.ROLE_UserDefined, glParamUnitDefn);
				sbmlSymbolMapping.putRuntime(sbmlGlobalParam, vcGlobalParam);
				if (vcParamName.length() > 64) {
					vcGlobalParam.setDescription("Parameter Name : " + vcParamName);
				}
				if (sbmlParamName != null && !sbmlParamName.isEmpty()) {
					vcGlobalParam.setSbmlName(sbmlParamName);
				}
				if (sbmlGlobalParam.isSetValue()) {
					double sbmlValue = sbmlGlobalParam.getValue();
					sbmlSymbolMapping.putSbmlValue(sbmlGlobalParam, sbmlValue);
					sbmlSymbolMapping.putInitial(sbmlGlobalParam, vcGlobalParam);
				}
				vcModelParamsList.add(vcGlobalParam);
			}else{
				if (sbmlGlobalParam.isSetValue()) {
					double sbmlValue = sbmlGlobalParam.getValue();
					sbmlSymbolMapping.putSbmlValue(sbmlGlobalParam, sbmlValue);
					sbmlSymbolMapping.putInitial(sbmlGlobalParam, vcellModelParameter);
				}else{
					sbmlSymbolMapping.putRuntime(sbmlGlobalParam, vcellModelParameter);
				}
			}
		} // end for - sbmlModel.parameters
		vcModel.setModelParameters(vcModelParamsList.toArray(new ModelParameter[0]));
	}

	/**
	 *
	 * Translate Expressions from SBML symbols to VCell Symbols in the correct namespace and context
	 *
	 */
	private static Expression adjustExpression(org.sbml.jsbml.Model sbmlModel, Expression sbmlExpr, NameScope namescope,
											   SBMLSymbolMapping sbmlSymbolMapping, SymbolContext symbolContext) throws ExpressionException {
		String[] symbols = sbmlExpr.getSymbols();
		if (symbols == null || symbols.length == 0){
			return new Expression(sbmlExpr);
		}
		Expression adjustedExpr = new Expression(sbmlExpr);
		for(String sbmlSymbol : symbols) {
			if (sbmlSymbol.equals(TIME_SYMBOL_OVERRIDE)){
				logger.info("adjustExpression(): replacing '"+TIME_SYMBOL_OVERRIDE+"' with 't' in "+adjustedExpr.infix());
				adjustedExpr.substituteInPlace(new Expression(TIME_SYMBOL_OVERRIDE), new Expression("t"));
			}else {
				SBase sbase = findSBase(sbmlModel, sbmlSymbol);
				final SymbolTableEntry vcellSymbolTableEntry = sbmlSymbolMapping.getSte(sbase, symbolContext);
				if (vcellSymbolTableEntry != null) {
					adjustedExpr.substituteInPlace(new Expression(sbmlSymbol), new Expression(vcellSymbolTableEntry, namescope));
				}
			}
		}
		// ************* TIME CONV_FACTOR if 'time' is present in global
		// parameter expression
		// If time 't' is present in the global expression, it is in VC units
		// (secs), convert it back to SBML units
		// hence, we take the inverse of the time factor
		// (getSBMLTimeUnitsFactor() converts from SBML to VC units)

		// ---- FOR NOW, IGNORE TIME UNIT CONVERSION ----
		// adjustedExpr = adjustTimeConvFactor(model, adjustedExpr);

		return adjustedExpr;
	}

	private static SBase findSBase(org.sbml.jsbml.Model sbmlModel, String sbmlSid) {
		if (sbmlSid == null){
			throw new RuntimeException("sbmlSid cannot be null");
		}
		SBase sbase_from_model = sbmlModel.getSBaseById(sbmlSid);
		Optional<? extends TreeNode> sbase_treeNode = sbmlModel.filter(new IdFilter(sbmlSid)).stream().findFirst(); // getSBaseById(sbmlSymbol);
		SBase sbase_from_tree = null;
		if (sbase_treeNode.isPresent() && (sbase_treeNode.get() instanceof SBase)){
			sbase_from_tree = (SBase)sbase_treeNode.get();
		}
		if (sbase_from_model != null && sbase_from_tree == null){
			throw new RuntimeException("unexpected, found sid in model, but not in tree");
		}
		return sbase_from_tree;
	}

//	private static void processParameters(BioModel vcBioModel) throws ExpressionException, PropertyVetoException {
//		SimulationContext simContext = vcBioModel.getSimulationContext(0);
//		Model vcModel = simContext.getModel();
//		ModelParameter[] mps = vcModel.getModelParameters();
//		if (mps == null || mps.length == 0) {
//			logger.debug("SBML Import: no model parameters.");
//			return;
//		}
//		for (ModelParameter mp : mps) {
//			Expression expression = mp.getExpression();
//			if(expression.isNumeric()) {
//				continue;
//			}
//			String[] symbols = expression.getSymbols();	// check if the expression has unresolved symbols
//			if(symbols != null) {
//				for(String symbol : symbols) {
//					SymbolTableEntry ste = simContext.getEntry(symbol);
//					if(ste == null) {
//						ReactionStep candidate = vcBioModel.getModel().getReactionStep(symbol);
//						if(candidate != null) {
//							convertLocalParameters(candidate);
//							// we replace the reaction name with the expression of the reaction rate, if possible
//							Kinetics kinetics = candidate.getKinetics();
//							KineticsParameter reactionRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_LumpedReactionRate);
//							if(reactionRate != null && reactionRate.getExpression() != null) {
//								Expression rrExpression = reactionRate.getExpression();
//								expression.substituteInPlace(new Expression(symbol), new Expression(rrExpression.flatten()));
//							} else {
//								throw new SBMLImportException("Unsupported use of a Reaction name '" + symbol + "' in the expression of Global Parameter '" + mp.getDisplayName() + "'.");
//							}
//						} else {
//							logger.warn("Symbol '" + symbol + "' still unresolved.");
//						}
//					}
//				}
//			}
//		}
//	}

	/**
	 * addReactionParticipant : Adds reactants and products and modifiers to a
	 * reaction. Input args are the sbml reaction, vc reaction This method was
	 * created mainly to handle reactions where there are reactants and/or
	 * products that appear multiple times in a reaction. Virtual Cell now
	 * allows the import of such reactions.
	 *
	 **/
	private static void addReactionParticipants(org.sbml.jsbml.Model sbmlModel, org.sbml.jsbml.Reaction sbmlRxn, long level,
												ReactionStep vcRxn, SBMLSymbolMapping sbmlSymbolMapping, VCLogger vcLogger,
												Vector<Issue> localIssueList, IssueContext issueContext) throws Exception {
		//
		// add reactants (compress stoichiometry as needed)
		//
		HashMap<SpeciesReference, Integer> sbmlReactantsHash = new HashMap<>();
		for (int j = 0; j < sbmlRxn.getNumReactants(); j++) {
			SpeciesReference reactantSpeciesReference = sbmlRxn.getReactant(j);
			String sbmlReactantSpId = reactantSpeciesReference.getSpecies();
			org.sbml.jsbml.Species sbmlReactant = sbmlModel.getSpecies(sbmlReactantSpId);
			if (sbmlReactant == null) {
				throw new SBMLImportException("Reactant '" + sbmlReactantSpId + "' in reaction" +
						" '" + sbmlRxn.getId() + "' not found as species in SBML model.");
			}
			final int stoichiometry;
			if (reactantSpeciesReference.isSetStoichiometry()) {
				double sbmlStoichAttribute = reactantSpeciesReference.getStoichiometry();
				if (((int) sbmlStoichAttribute != sbmlStoichAttribute) || reactantSpeciesReference.isSetStoichiometryMath()) {
					vcLogger.sendMessage(VCLogger.Priority.HighPriority,
							VCLogger.ErrorType.UnsupportedConstruct,
							"Non-integer stoichiometry ('" + sbmlStoichAttribute + "' for reactant" +
									" '" + sbmlReactantSpId + "' in reaction '" + sbmlRxn.getId() + "') or stoichiometryMath" +
									" not handled in VCell at this time.");
				}
				stoichiometry = (int) sbmlStoichAttribute;
			}else if (level < 3) {
				stoichiometry = 1;
			}else{
				String msg = "This is a SBML level 3 model, stoichiometry is not set for the reactant" +
						" '" + sbmlReactantSpId + "' and no default value should be assumed, but assuming 1.";
				logger.warn(msg);
				vcLogger.sendMessage(VCLogger.Priority.MediumPriority, VCLogger.ErrorType.UnsupportedConstruct, msg);
				stoichiometry = 1;
			}
			//
			// save Reactant in a Map to support reactions such as A + A ->
			//
			if (sbmlReactantsHash.get(reactantSpeciesReference) == null) {
				sbmlReactantsHash.put(reactantSpeciesReference, stoichiometry);
			} else {
				int prevStoich = sbmlReactantsHash.get(reactantSpeciesReference);
				sbmlReactantsHash.put(reactantSpeciesReference,prevStoich + stoichiometry);
			}
		}
		//
		// add reactants with net stoichiometry (e.g. A + A -> ) goes to 2A ->
		//
		for (Entry<SpeciesReference, Integer> es : sbmlReactantsHash.entrySet()) {
			SpeciesReference reactantSpeciesReference = es.getKey();
			int stoich = es.getValue();

			EditableSymbolTableEntry vcellSte = sbmlSymbolMapping.getSte(reactantSpeciesReference.getSpeciesInstance(), SymbolContext.RUNTIME);
			if (!(vcellSte instanceof SpeciesContext)) {
				throw new SBMLImportException("could not find VCell SpeciesContext for SBML Species "+reactantSpeciesReference.getSpeciesInstance());
			}
			SpeciesContext speciesContext = (SpeciesContext) vcellSte;
			vcRxn.addReactant(speciesContext, stoich);
			Reactant reactant = vcRxn.getReactant(speciesContext.getName());
			sbmlSymbolMapping.putRuntime(reactantSpeciesReference, new StoichiometrySymbolTableEntry(reactant));
		}

		//
		// add products (compress stoichiometry as needed)
		//
		HashMap<SpeciesReference, Integer> sbmlProductsHash = new HashMap<>();
		for (int j = 0; j < sbmlRxn.getNumProducts(); j++) {
			SpeciesReference productSpeciesReference = sbmlRxn.getProduct(j);
			String sbmlProductSpId = productSpeciesReference.getSpecies();
			org.sbml.jsbml.Species sbmlProduct = sbmlModel.getSpecies(sbmlProductSpId);
			if (sbmlProduct == null) {
				throw new SBMLImportException("Product '" + sbmlProductSpId + "' in reaction" +
						" '" + sbmlRxn.getId() + "' not found as species in SBML model.");
			}
			final int stoichiometry;
			if (productSpeciesReference.isSetStoichiometry()) {
				double sbmlStoichAttribute = productSpeciesReference.getStoichiometry();
				if (((int) sbmlStoichAttribute != sbmlStoichAttribute) || productSpeciesReference.isSetStoichiometryMath()) {
					String msg = "Non-integer stoichiometry ('" + sbmlStoichAttribute + "' for product" +
							" '" + sbmlProductSpId + "' in reaction '" + sbmlRxn.getId() + "') or stoichiometryMath" +
							" not handled in VCell at this time.";
					vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.UnsupportedConstruct, msg);
					logger.error(msg);
				}
				stoichiometry = (int) sbmlStoichAttribute;
			}else if (level < 3) {
				stoichiometry = 1;
			}else{
				String msg = "This is a SBML level 3 model, stoichiometry is not set for the product" +
						" '" + sbmlProductSpId + "' and no default value should be assumed, but assuming 1.";
				logger.warn(msg);
				vcLogger.sendMessage(VCLogger.Priority.MediumPriority, VCLogger.ErrorType.UnsupportedConstruct, msg);
				stoichiometry = 1;
			}
			//
			// save Product in a Map to support reactions such as ( -> A + A)
			//
			if (sbmlProductsHash.get(productSpeciesReference) == null) {
				sbmlProductsHash.put(productSpeciesReference, stoichiometry);
			} else {
				int prevStoich = sbmlProductsHash.get(productSpeciesReference);
				sbmlProductsHash.put(productSpeciesReference,prevStoich + stoichiometry);
			}
		}
		//
		// add products with net stoichiometry (e.g.  -> A + A ) goes to ( -> 2A)
		//
		for (Entry<SpeciesReference, Integer> es : sbmlProductsHash.entrySet()) {
			SpeciesReference productSpeciesReference = es.getKey();
			int stoich = es.getValue();

			EditableSymbolTableEntry vcellSte = sbmlSymbolMapping.getSte(productSpeciesReference.getSpeciesInstance(), SymbolContext.RUNTIME);
			if (!(vcellSte instanceof SpeciesContext)) {
				throw new SBMLImportException("could not find VCell SpeciesContext for SBML Species "+productSpeciesReference.getSpeciesInstance());
			}
			SpeciesContext speciesContext = (SpeciesContext) vcellSte;
			vcRxn.addProduct(speciesContext, stoich);
			Product product = vcRxn.getProduct(speciesContext.getName());
			sbmlSymbolMapping.putRuntime(productSpeciesReference, new StoichiometrySymbolTableEntry(product));
		}


		//
		// add catalysts/modifiers
		//
		for (int j = 0; j < sbmlRxn.getNumModifiers(); j++) {
			ModifierSpeciesReference spRef = sbmlRxn.getModifier(j);
			String sbmlSpId = spRef.getSpecies();
			Species sbmlModifier = sbmlModel.getSpecies(sbmlSpId);

			EditableSymbolTableEntry vcellSte = sbmlSymbolMapping.getSte(sbmlModifier, SymbolContext.RUNTIME);
			if (!(vcellSte instanceof SpeciesContext)) {
				throw new SBMLImportException("could not find VCell SpeciesContext for SBML Species " + sbmlModifier);
			}
			SpeciesContext speciesContext = (SpeciesContext) vcellSte;
			boolean bFoundSpeciesContext = Arrays.stream(vcRxn.getReactionParticipants())
					.anyMatch(rp -> rp.getSpeciesContext() == speciesContext);
			if (!bFoundSpeciesContext) {
				vcRxn.addCatalyst(speciesContext);
			} else {
				boolean bFoundAsReactantOrProduct = Arrays.stream(vcRxn.getReactionParticipants())
						.anyMatch(rp -> (
								rp.getSpeciesContext() == speciesContext)
								&& (rp instanceof Reactant || rp instanceof Product));
				if (bFoundAsReactantOrProduct) {
					localIssueList.add(new Issue(speciesContext, issueContext, IssueCategory.SBMLImport_Reaction,
							"Species " + speciesContext.getName() + " was already added as a reactant and/or product to" +
									" " + vcRxn.getName() + "; Cannot add it as a catalyst also.", Issue.Severity.INFO));
				}
			}
		}
	}


	private static void parseAssignmentAndInitialAssignmentExpressions(org.sbml.jsbml.Model sbmlModel, LambdaFunction[] lambdaFunctions,
																	   SBMLSymbolMapping sbmlSymbolMapping, VCLogger vcLogger) throws Exception {
		ListOf<Rule> listofRules = sbmlModel.getListOfRules();
		if (listofRules == null) {
			logger.debug("No Rules specified");
			return;
		}
		for (int i = 0; i < sbmlModel.getNumRules(); i++) {
			Rule rule = listofRules.get(i);
			if (rule instanceof AssignmentRule) {
				AssignmentRule assignmentRule = (AssignmentRule) rule;
				Expression sbmlAssignRuleExpr = getExpressionFromFormula(assignmentRule.getMath(), lambdaFunctions, vcLogger);
				SBase targetSBase = findSBase(sbmlModel, assignmentRule.getVariable());
				if (targetSBase == null) {
					throw new RuntimeException("failed to resolve target of assignment rule with id " + assignmentRule.getId());
				}
				sbmlSymbolMapping.putAssignmentRuleSbmlExpression(targetSBase, sbmlAssignRuleExpr);
			}
		}
		for (int i=0; i < sbmlModel.getNumInitialAssignments(); i++) {
			InitialAssignment initialAssignment = sbmlModel.getInitialAssignment(i);
			Expression sbmlInitialAssignExpr = getExpressionFromFormula(initialAssignment.getMath(), lambdaFunctions, vcLogger);
			SBase targetSBase = findSBase(sbmlModel, initialAssignment.getVariable());
			if (targetSBase == null){
				throw new RuntimeException("failed to resolve target of assignment rule with id "+initialAssignment.getId());
			}
			sbmlSymbolMapping.putInitialAssignmentSbmlExpression(targetSBase, sbmlInitialAssignExpr);
		}
	}
	
	// returns true if reserved x,y,z symbols are used inappropriately - like in a non-spatial model
	private static boolean isRestrictedXYZT(String name, BioModel vcBioModel, boolean bSpatial) {
		if(bSpatial) {
			return false;
		}
		ReservedSymbol rs = vcBioModel.getModel().getReservedSymbolByName(name);
		if(rs == null) {
			return false;
		}
		return rs.isX() || rs.isY() || rs.isZ() || rs.isTime();
	}

	// called after the reactions are parsed, we check if the rule expression references any reaction by name
	// if yes, we replace the reaction name with the expression of the reaction rate

	private static void createAssignmentRules(org.sbml.jsbml.Model sbmlModel, BioModel vcBioModel, SBMLSymbolMapping sbmlSymbolMapping,
											  Vector<Issue> localIssueList, IssueContext issueContext, VCLogger vcLogger) throws Exception {
		SimulationContext simContext = vcBioModel.getSimulationContext(0);
		Model vcModel = simContext.getModel();
		GeometryContext gc = simContext.getGeometryContext();

		boolean foundConstStructureSize = false;
		for (Rule rule : sbmlModel.getListOfRules()){
			if (!(rule instanceof AssignmentRule)) {
				continue;
			}
			AssignmentRule assignmentRule = (AssignmentRule) rule;
			SBase targetSbase = findSBase(sbmlModel, assignmentRule.getVariable());
			if (targetSbase == null){
				String msg = "could not find SBase for assignment rule variable sid '"+assignmentRule.getVariable()+"'";
				vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.OverallWarning, msg);
				logger.error(msg);
				continue;
			}
			EditableSymbolTableEntry assignmentTargetSte = sbmlSymbolMapping.getRuntimeSte(targetSbase);
			try {
				// only add those assignment rules for runtime targets that don't have editable expressions - not sure what to do about these if they don't map to anything.
				if (!assignmentTargetSte.isExpressionEditable()
					&& !(assignmentTargetSte instanceof Structure.StructureSize)
					&& !(assignmentTargetSte instanceof SpeciesContext)) {

					logger.error("unexpected assignment rule target '"+assignmentTargetSte+"', adding as assignment rule - minimal support provided");
					logger.error("need to create a VCLogger event");

					Expression sbmlExpression = sbmlSymbolMapping.getRuleSBMLExpression(targetSbase, SymbolContext.RUNTIME);
					Expression vcellExpr = adjustExpression(sbmlModel, sbmlExpression, assignmentTargetSte.getNameScope(), sbmlSymbolMapping, SymbolContext.RUNTIME);
					String vcRuleName = simContext.getFreeAssignmentRuleName();
					cbit.vcell.mapping.AssignmentRule vcRule = new cbit.vcell.mapping.AssignmentRule(vcRuleName, assignmentTargetSte, vcellExpr, simContext);
					vcRule.bind();
					simContext.addAssignmentRule(vcRule);
				}
			} catch (PropertyVetoException | ExpressionException e) {
				String msg = "Unable to create and add assignment rule to VC model : " + e.getMessage();
				vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.OverallWarning, msg);
				logger.error(msg, e);
				continue;
			}
		}
		if (foundConstStructureSize) {
			try {
				StructureSizeSolver.updateRelativeStructureSizes(simContext);
				String msg = "One or more AssignmentRule variables of StructureSize type evaluated to constant and were used as initial assignment for the Feature (Structure)";
				logger.warn(msg);
				localIssueList.add(new Issue(vcModel, issueContext, IssueCategory.SBMLImport_RestrictedFeature, msg, Issue.Severity.WARNING));
			} catch (Exception e) {
				String msg = "Error initializing a Feature from an assignment rule StructureSize variable. ";
				throw new SBMLImportException(msg + ": " + e.getMessage(), e);
			}
		}
	}

	//------------------------------------------------------------------------------------------------------------------------------------------
	private static void readRateRules(org.sbml.jsbml.Model sbmlModel, LambdaFunction[] lambdaFunctions, SBMLSymbolMapping sbmlSymbolMapping,
									  VCLogger vcLogger) throws Exception {

		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf<Rule> listofRules = sbmlModel.getListOfRules();
		if (listofRules == null) {
			logger.debug("No Rules specified");
			return;
		}
		for (int i = 0; i < sbmlModel.getNumRules(); i++) {
			Rule rule = listofRules.get(i);
			if (rule instanceof RateRule) {
				// Get the rate rule and store it in the hashMap, and create VCell rateRule.
				RateRule sbmlRateRule = (RateRule) rule;

				String sbmlTargetSid = sbmlRateRule.getVariable();		// --------------------- rate rule variable
				SBase sbmlTargetSbase = findSBase(sbmlModel, sbmlTargetSid);
				Expression sbmlRateRuleExpr = getExpressionFromFormula(sbmlRateRule.getMath(), lambdaFunctions, vcLogger);
				sbmlSymbolMapping.putRateRuleSbmlExpression(sbmlTargetSbase, sbmlRateRuleExpr);
			}
		}
	}
	
	// convert user defined local parameters to global in this reaction
	private static void convertLocalParameters(ReactionStep rs) throws ExpressionException, PropertyVetoException {
		KineticsParameter[] kps = rs.getKinetics().getKineticsParameters();
		for(KineticsParameter kp : kps) {
			if(kp.getRole() == Kinetics.ROLE_UserDefined) {
				String newName = rs.getDisplayName() + "_" + kp.getName();
				rs.getKinetics().renameParameter(kp.getName(), newName);
			}
		}
		kps = rs.getKinetics().getKineticsParameters();
		for(KineticsParameter kp : kps) {
			if(kp.getRole() == Kinetics.ROLE_UserDefined) {
				ModelParameter mp = rs.getKinetics().getReactionStep().getModel().getModelParameter(kp.getName());
				if(mp != null) {
					throw new SBMLException("Unable to convert user defined parameter '" + kp.getName() + "' to global in reaction '" + rs.getDisplayName() + "': it already exists.");
				}
				
				if (!kp.getExpression().isNumeric()) {	// if parameter is not numeric and its expression contains other local parameters, throw exception
					String[] symbols = kp.getExpression().getSymbols();
					for (String symbol : symbols) {
						if (rs.getKinetics().getKineticsParameter(symbol) != null) {
							throw new SBMLException("Parameter '" + kp.getName() + "' contains other local kinetic parameters and cannot be converted to global.");
						}
					}
				}
				rs.getKinetics().convertParameterType(kp, true);
			}
		}
	}

	// at the very end, create the RateRules and add them to the simulation context
	private static void createRateRules(org.sbml.jsbml.Model sbmlModel, BioModel vcBioModel, SBMLSymbolMapping sbmlSymbolMapping) {
		SimulationContext simContext = vcBioModel.getSimulationContext(0);

		for (Rule rule : sbmlModel.getListOfRules()){
			if (!(rule instanceof RateRule)){
				continue;
			}
			RateRule rateRule = (RateRule) rule;
			SBase sbmlTarget = findSBase(sbmlModel, rateRule.getVariable());
			Expression sbmlExpression = sbmlSymbolMapping.getRateRuleSBMLExpression(sbmlTarget);
			EditableSymbolTableEntry vcellTargetSte = sbmlSymbolMapping.getRuntimeSte(sbmlTarget);
			try {
				String vcRuleName = simContext.getFreeRateRuleName();
				Expression vcExpression = adjustExpression(sbmlModel, sbmlExpression, vcellTargetSte.getNameScope(), sbmlSymbolMapping, SymbolContext.RUNTIME);
				cbit.vcell.mapping.RateRule vcRule = new cbit.vcell.mapping.RateRule(vcRuleName, vcellTargetSte, vcExpression, simContext);
				vcRule.bind();
				simContext.addRateRule(vcRule);
			} catch (PropertyVetoException | ExpressionException e) {
				throw new SBMLImportException("Unable to create and add rate rule to VC model : " + e.getMessage(), e);
			}
		}
	}
	
	private static void addSpecies(org.sbml.jsbml.Model sbmlModel, BioModel vcBioModel, boolean bSpatial,
									 SBMLSymbolMapping sbmlSymbolMapping, SBMLAnnotationUtil sbmlAnnotationUtil, VCLogger vcLogger) {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf<Species> listOfSpecies = sbmlModel.getListOfSpecies();
		if (listOfSpecies == null) {
			logger.debug("No Spcecies");
			return;
		}
		HashMap<cbit.vcell.model.Species, org.sbml.jsbml.Species> vc_sbmlSpeciesMap = new HashMap<>();
		SpeciesContext[] vcSpeciesContexts = new SpeciesContext[(int) sbmlModel.getNumSpecies()];
		// Get species from SBMLmodel; Add/get speciesContext
		try {
			// First pass - add the speciesContexts
			for (int i = 0; i < sbmlModel.getNumSpecies(); i++) {
				org.sbml.jsbml.Species sbmlSpecies = listOfSpecies.get(i);
				// Sometimes, the species name can be null or a blank string; in
				// that case, use species id as the name.
				String sbmlSpeciesId = sbmlSpecies.getId();
				String vcSpeciesId = sbmlSpeciesId;			// we'll try to use the sbmlSpeciesId as name for the vCell species
				cbit.vcell.model.Species vcSpecies = null;
				// create a species with speciesName as commonName. If it is
				// different in the annotation, can change it later
				Element sbmlImportRelatedElement = sbmlAnnotationUtil.readVCellSpecificAnnotation(sbmlSpecies);
				if (sbmlImportRelatedElement != null) {
					Element embeddedElement = getEmbeddedElementInAnnotation(sbmlImportRelatedElement, SPECIES_NAME);
					if (embeddedElement != null) {
						// Get the species name from annotation and create the
						// species.
						if (embeddedElement.getName().equals(XMLTags.SpeciesTag)) {
							String vcSpeciesName = embeddedElement.getAttributeValue(XMLTags.NameAttrTag);
							vcSpecies = sbmlSymbolMapping.getVCellSpecies(sbmlSpecies);
							if (vcSpecies == null) {
								if(isRestrictedXYZT(vcSpeciesName, vcBioModel, bSpatial)) {
									logger.error("embedded VCell annotation for species name "+vcSpeciesName+" is restricted, using anyway");
								}
								vcSpecies = new cbit.vcell.model.Species(vcSpeciesName, vcSpeciesName);
								sbmlSymbolMapping.putVCellSpecies(sbmlSpecies, vcSpecies);
							}
						}
						// if embedded element is not speciesTag, do I have to
						// do something?
					} else {
						// Annotation element is present, but doesn't contain
						// the species element.
						if(isRestrictedXYZT(sbmlSpeciesId, vcBioModel, bSpatial)) {
							vcSpeciesId = "s_" + sbmlSpeciesId;
						}
						vcSpecies = new cbit.vcell.model.Species(vcSpeciesId, vcSpeciesId);
						sbmlSymbolMapping.putVCellSpecies(sbmlSpecies, vcSpecies);
					}
				} else {
					if(isRestrictedXYZT(sbmlSpeciesId, vcBioModel, bSpatial)) {		// try to rename the vCell species if its name is a vCell reserved symbol
						vcSpeciesId = "s_" + sbmlSpeciesId;
					}
					vcSpecies = new cbit.vcell.model.Species(vcSpeciesId, vcSpeciesId);
					sbmlSymbolMapping.putVCellSpecies(sbmlSpecies, vcSpecies);
				}

				// store vc & sbml species in hash to read in annotation later
				vc_sbmlSpeciesMap.put(vcSpecies, sbmlSpecies);

				// Get matching compartment name (of sbmlSpecies[i]) from
				// feature list
				String compartmentId = sbmlSpecies.getCompartment();
				Structure spStructure = vcBioModel.getSimulationContext(0).getModel().getStructure(compartmentId);
				vcSpeciesContexts[i] = new SpeciesContext(vcSpecies, spStructure);
				vcSpeciesContexts[i].setName(vcSpeciesId);
				sbmlSymbolMapping.putRuntime(sbmlSpecies, vcSpeciesContexts[i]);
				if (sbmlSpecies.isSetInitialAmount()){
					double initAmount = sbmlSpecies.getInitialAmount();
					// initConcentration := initAmount / compartmentSize.
					// If compartmentSize is set and non-zero, compute initConcentration. Else, throw exception.
					Compartment compartment = sbmlModel.getCompartment(sbmlSpecies.getCompartment());
					if (compartment.isSetSize()) {
						double compartmentSize = compartment.getSize();
						double initConcentration = 0.0;
						if (compartmentSize != 0.0) {
							initConcentration = initAmount / compartmentSize;
						} else {
							vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.UnitError,
									"compartment '"	+ compartment.getId() + "' has zero size, unable to determine initial concentration for sbml species " + sbmlSpecies.getId());
						}
						sbmlSymbolMapping.putSbmlValue(sbmlSpecies,initConcentration);
					} else {
						vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.SpeciesError,
								" Compartment '" + compartment.getId() + "' size not set or is defined by a rule; cannot calculate initConc.");
					}
				}
				if (sbmlSpecies.isSetInitialConcentration()){
					sbmlSymbolMapping.putSbmlValue(sbmlSpecies,sbmlSpecies.getInitialConcentration());
				}

				if(sbmlSpecies.isSetName()) {
					String sbmlSpeciesName = sbmlSpecies.getName();
					vcSpeciesContexts[i].setSbmlName(sbmlSpeciesName);
				}

				// Adjust units of species, convert to VC units.
				// Units in SBML, compute this using some of the attributes of
				// sbmlSpecies
				Compartment sbmlCompartment = sbmlModel.getCompartment(sbmlSpecies.getCompartment());
				int dimension = 3;
				if (sbmlCompartment.isSetSpatialDimensions()){
					dimension = (int) sbmlCompartment.getSpatialDimensions();
				}
				if (dimension == 0 || dimension == 1) {
					vcLogger.sendMessage(VCLogger.Priority.HighPriority,
							VCLogger.ErrorType.UnitError, dimension
									+ " dimensional compartment "
									+ compartmentId + " not supported");
				}
			} // end - for sbmlSpecies

			// set the species & speciesContexts on model
			Model vcModel = vcBioModel.getSimulationContext(0).getModel();
			vcModel.setSpecies(sbmlSymbolMapping.getVCellSpeciesArray());
			vcModel.setSpeciesContexts(vcSpeciesContexts);

			// Set annotations and notes from SBML to VCMetadata
			cbit.vcell.model.Species[] vcSpeciesArray = vc_sbmlSpeciesMap.keySet().toArray(new cbit.vcell.model.Species[0]);
			for (cbit.vcell.model.Species vcSpecies : vcSpeciesArray) {
				org.sbml.jsbml.Species sbmlSpecies = vc_sbmlSpeciesMap.get(vcSpecies);
				sbmlAnnotationUtil.readAnnotation(vcSpecies, sbmlSpecies);
				sbmlAnnotationUtil.readNotes(vcSpecies, sbmlSpecies);
			}
		} catch (Exception e) {
			throw new SBMLImportException("Error adding species context; "
					+ e.getMessage(), e);
		}
	}

	/**
	 * for all SpeciesContextSpecs:
	 * 1) map the SpeciesContextSpec initialConditions parameter to the SBML Species (in SBMLSymbolMapping)
	 * 2) set the 'constant' attribute of the SpeciesContextSpec
	 */
	private static void configureSpeciesContextSpecs(org.sbml.jsbml.Model sbmlModel, BioModel vcBioModel,
													 SBMLSymbolMapping sbmlSymbolMapping, VCLogger vcLogger,
													 Vector<Issue> localIssueList, IssueContext issueContext) {
		try {
			// fill in SpeciesContextSpec for each speciesContext
			Model vcModel = vcBioModel.getSimulationContext(0).getModel();
			SpeciesContext[] vcSpeciesContexts = vcModel.getSpeciesContexts();
			for (SpeciesContext vcSpeciesContext : vcSpeciesContexts) {
				SBase sbase = sbmlSymbolMapping.getSBase(vcSpeciesContext, SymbolContext.RUNTIME);
				if (!(sbase instanceof org.sbml.jsbml.Species)) {
					throw new RuntimeException("could not find sbml Species corresponding to SpeciesContext " + vcSpeciesContext.getName());
				}
				org.sbml.jsbml.Species sbmlSpecies = (org.sbml.jsbml.Species) sbase;
				ReactionContext rc = vcBioModel.getSimulationContext(0).getReactionContext();
				SpeciesContextSpec speciesContextSpec = rc.getSpeciesContextSpec(vcSpeciesContext);

				sbmlSymbolMapping.putInitial(sbmlSpecies, speciesContextSpec.getInitialConcentrationParameter());
				speciesContextSpec.setConstant(sbmlSpecies.getBoundaryCondition() || sbmlSpecies.getConstant());

				// set wellmixed attribute (if present from vcell-specific annotation)
				XMLNode speciesNonRdfAnnotation = sbmlSpecies.getAnnotation().getNonRDFannotation();
				if (speciesNonRdfAnnotation != null) {
					XMLNode speciesContextSpecSettingsElement = speciesNonRdfAnnotation.getChildElement(XMLTags.SBML_VCELL_SpeciesContextSpecSettingsTag, "*");
					if (speciesContextSpecSettingsElement != null) {
						int attrWellMixed = speciesContextSpecSettingsElement.getAttrIndex(XMLTags.SBML_VCELL_SpeciesContextSpecSettingsTag_wellmixedAttr, SBMLUtils.SBML_VCELL_NS);
						if (attrWellMixed >= 0) {
							String wellMixedValue = speciesContextSpecSettingsElement.getAttrValue(attrWellMixed);
							if (wellMixedValue.equals("true")) {
								speciesContextSpec.setWellMixed(true);
							} else if (wellMixedValue.equals("false")) {
								speciesContextSpec.setWellMixed(false);
							} else {
								throw new RuntimeException("unexpected attribute value '" + attrWellMixed + "' for attribute " +
										XMLTags.SBML_VCELL_SpeciesContextSpecSettingsTag + ":" + XMLTags.SBML_VCELL_SpeciesContextSpecSettingsTag_wellmixedAttr);
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			throw new SBMLImportException("Error setting initial condition for species context; " + e.getMessage(), e);
		}
	}

	/**
	 * checkCompartmentScaleFactorInRxnRateExpr : Used to check if reaction rate
	 * expression has a compartment scale factor. Need to remove this factor
	 * from the rate expression. Differentiate the rate expression wrt the
	 * compartmentSizeParamName. If the differentiated expression contains the
	 * compartmentSizeParamName, VCell doesn't support non-linear functions of
	 * compartmentSizeParam. Substitute 1.0 for the compartmentSizeParam in the
	 * rateExpr and check its equivalency with the differentiated expr above. If
	 * they are not equal, the rate expression is a non-linear function of
	 * compartmentSizeParam - not acceptable. Substitute 0.0 for
	 * compartmentSizeParam in rateExpr. If the value doesn't evaluate to 0.0,
	 * it is not valid for the same reason above.
	 **/

	/*
	 * pending delete? gcw 4/2014 private Expression
	 * removeCompartmentScaleFactorInRxnRateExpr(Expression rateExpr, String
	 * compartmentSizeParamName, String rxnName) throws Exception { Expression
	 * diffExpr = rateExpr.differentiate(compartmentSizeParamName).flatten(); if
	 * (diffExpr.hasSymbol(compartmentSizeParamName)) {
	 * logger.sendMessage(VCLogger.Priority.HighPriority,
	 * VCLogger.ErrorType.UnitError,
	 * "Unable to interpret Kinetic rate for reaction : " + rxnName +
	 * " Cannot interpret non-linear function of compartment size"); }
	 * 
	 * Expression expr1 = rateExpr.getSubstitutedExpression(new
	 * Expression(compartmentSizeParamName), new Expression(1.0)).flatten(); if
	 * (!expr1.compareEqual(diffExpr) &&
	 * !(ExpressionUtils.functionallyEquivalent(expr1, diffExpr))) {
	 * logger.sendMessage(VCLogger.Priority.HighPriority,
	 * VCLogger.ErrorType.UnitError,
	 * "Unable to interpret Kinetic rate for reaction : " + rxnName +
	 * " Cannot interpret non-linear function of compartment size"); }
	 * 
	 * Expression expr0 = rateExpr.getSubstitutedExpression(new
	 * Expression(compartmentSizeParamName), new Expression(0.0)).flatten(); if
	 * (!expr0.isZero()) { logger.sendMessage(VCLogger.Priority.HighPriority,
	 * VCLogger.ErrorType.UnitError,
	 * "Unable to interpret Kinetic rate for reaction : " + rxnName +
	 * " Cannot interpret non-linear function of compartment size"); }
	 * 
	 * return expr1; }
	 */

	/**
	 * resolveRxnParameterNameConflicts : Check if the reaction rate name
	 * matches with any global or local parameter, in which case, we have to
	 * change the rate name (to oldName_rxnName); since the global or local
	 * parameter value will override the rate equation/value. Also, when we
	 * import a VCell model that has been exported to SBML, if the user has
	 * changed the rate name in a reaction, it is stored in the reaction
	 * annotation. This has to be retrieved and set as reaction rate name.
	 */
	private static void resolveRxnParameterNameConflicts(org.sbml.jsbml.Model sbmlModel, Reaction sbmlRxn,
														 Kinetics vcKinetics)
			throws PropertyVetoException {
		/*
		 * Get the rate name from the kinetics : if it is from GeneralKinetics,
		 * it is the reactionRateParamter name; if it is from LumpedKinetics, it
		 * is the LumpedReactionRateParameter name.
		 */
		String origRateParamName = vcKinetics.getAuthoritativeParameter().getName();

		/*
		 * Check if any parameters (global/local) have the same name as kinetics
		 * rate param name; This will replace any rate expression with the
		 * global/local param value; which is unacceptable. If there is a match,
		 * replace it with a new name for rate param - say,
		 * origName_reactionName.
		 */
		ListOf<Parameter> listofGlobalParams = sbmlModel.getListOfParameters();
		for (int j = 0; j < sbmlModel.getNumParameters(); j++) {
			org.sbml.jsbml.Parameter param = listofGlobalParams.get(j);
			String paramName = param.getId();
			// Check if reaction rate param clashes with an existing
			// (pre-defined) kinetic parameter - eg., reaction rate param 'J'
			// If so, change the name of the kinetic param (say, by adding
			// reaction name to it).
			if (paramName.equals(origRateParamName)) {
				String newName = origRateParamName + "_" + TokenMangler.mangleToSName(sbmlRxn.getId());
				vcKinetics.getAuthoritativeParameter().setName(newName);
			}
		}

		KineticLaw kLaw = sbmlRxn.getKineticLaw();
		if (kLaw != null) {
			for (LocalParameter param : kLaw.getListOfLocalParameters()) {
				String paramName = param.getId();
				// Check if reaction rate param clashes with an existing
				// (pre-defined) kinetic parameter - eg., reaction rate param 'J'
				// If so, change the name of the kinetic param (say, by adding
				// reaction name to it).
				if (paramName.equals(origRateParamName)) {
					String newName = origRateParamName + "_" + TokenMangler.mangleToSName(sbmlRxn.getId());
					vcKinetics.getAuthoritativeParameter().setName(newName);
				}
			}
		}
	}

	/**
	 * getReferencedSpecies(Reaction , HashSet<String> ) : Get the species
	 * referenced in sbmlRxn (reactants and products); store their names in
	 * hashSet (refereceNamesHash) Also, get the species referenced in the
	 * reaction kineticLaw expression from getReferencedSpeciesInExpr.
	 */
	private static void getReferencedSpecies(org.sbml.jsbml.Model sbmlModel, Reaction sbmlRxn, HashSet<String> refSpeciesNameHash,
											 LambdaFunction[] lambdaFunctions, SBMLSymbolMapping sbmlSymbolMapping, VCLogger vcLogger)
			throws Exception {
		// get all species referenced in listOfReactants
		for (int i = 0; i < sbmlRxn.getNumReactants(); i++) {
			SpeciesReference reactRef = sbmlRxn.getReactant(i);
			refSpeciesNameHash.add(reactRef.getSpecies());
		}
		// get all species referenced in listOfProducts
		for (int i = 0; i < sbmlRxn.getNumProducts(); i++) {
			SpeciesReference pdtRef = sbmlRxn.getProduct(i);
			refSpeciesNameHash.add(pdtRef.getSpecies());
		}
		// get all species referenced in reaction rate law
		if (sbmlRxn.getKineticLaw() != null) {
			Expression sbmlRateExpression = getExpressionFromFormula(sbmlRxn.getKineticLaw().getMath(), lambdaFunctions, vcLogger);
			getReferencedSpeciesInExpr(sbmlModel, sbmlRateExpression, refSpeciesNameHash, sbmlSymbolMapping);
		}
	}

	/**
	 * getReferencedSpeciesInExpr(Expression , HashSet<String> ) : Recursive
	 * method to get species referenced in expression 'sbmlExpr' - takes care of
	 * cases where expressions have symbols that are themselves expression and
	 * might contain other species.
	 */
	private static void getReferencedSpeciesInExpr(org.sbml.jsbml.Model sbmlModel, Expression sbmlExpr,
												   HashSet<String> refSpNamesHash, SBMLSymbolMapping sbmlSymbolMappings)  {
		String[] symbols = sbmlExpr.getSymbols();
		for (int i = 0; symbols != null && i < symbols.length; i++) {
			Parameter sbmlParam = sbmlModel.getParameter(symbols[i]);
			if (sbmlParam != null) {
				Expression sbmlParamExpression = sbmlSymbolMappings.getRuleSBMLExpression(sbmlParam, SymbolContext.RUNTIME);
				if (sbmlParamExpression != null) {
					getReferencedSpeciesInExpr(sbmlModel, sbmlParamExpression, refSpNamesHash, sbmlSymbolMappings);
				}
			} else {
				org.sbml.jsbml.Species sbmlSpecies = sbmlModel.getSpecies(symbols[i]);
				if (sbmlSpecies != null) {
					refSpNamesHash.add(sbmlSpecies.getId());
				}
			}
		}
	}

	/**
	 * parse SBML file into biomodel logs errors to log4j if present in source document
	 */
	public BioModel getBioModel() throws IllegalStateException, SBMLImportException, VCLoggerException {
		
		if (sbmlFileName == null && sbmlModel == null && sbmlInputStream == null) {
			throw new IllegalStateException("Expected non-null SBML model");
		}
		
		final SBMLDocument document;
		if (sbmlFileName != null) {
			document = readSbmlDocument(new File(sbmlFileName));
			sbmlModel = document.getModel();
		} else if (sbmlInputStream != null) {
			document = readSbmlDocument(this.sbmlInputStream);
			sbmlModel = document.getModel();
		} else { // sbmlModel != null
			document = sbmlModel.getSBMLDocument();
		}
		// get namespace and SBML model level to use in SBMLAnnotationUtil
		String ns = document.getNamespace();

		//
		// validate SBML model before import
		//
		if (this.bValidateSBML){
			validateSBMLDocument(document, vcLogger);
		}
		validateSBMLPackages(document, localIssueList, issueContext);


		//
		// create VCell unit system from the SBML model and create the bioModel.
		//
		final BioModel vcBioModel;
		final HashMap<String, VCUnitDefinition> sbmlUnitIdentifierHash = new HashMap<>();
		ModelUnitSystem modelUnitSystem;
		try {
			modelUnitSystem = createSBMLUnitSystemForVCModel(sbmlModel, sbmlUnitIdentifierHash, localIssueList, issueContext);

			vcBioModel = new BioModel(null,modelUnitSystem);

			String biomodelName = sbmlModel.getName();
			if ((biomodelName == null) || biomodelName.trim().equals("")) {
				biomodelName = sbmlModel.getId();
			}
			if ((biomodelName == null) || biomodelName.trim().equals("")) {
				biomodelName = "newBioModel";
			}
			vcBioModel.setName(biomodelName);
		} catch (Exception e) {
			String msg = "Inconsistent unit system in SBML model, cannot import into VCell.";
			vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.UnitError, msg);
			logger.error(msg, e);
			throw new SBMLImportException("Inconsistent unit system. Cannot import SBML model into VCell.", Category.INCONSISTENT_UNIT, e);
		}

		//
		// set up default 0-Dimensional Geometry and nonspatial Application
		//
		try {
			Geometry geometry = new Geometry(BioModelChildSummary.COMPARTMENTAL_GEO_STR, 0);
			SimulationContext simulationContext = new SimulationContext(vcBioModel.getModel(), geometry, null, null, Application.NETWORK_DETERMINISTIC);
			vcBioModel.addSimulationContext(simulationContext);
			simulationContext.setName(vcBioModel.getSimulationContext(0).getModel().getName());
		} catch (Exception e) {		// PropertyVetoException
			throw new SBMLImportException("Could not create default Application: "+e.getMessage(), e);
		}

		//
		// read SBML Annotation
		//
		final SBMLAnnotationUtil sbmlAnnotationUtil;
		try {
			sbmlAnnotationUtil = new SBMLAnnotationUtil(vcBioModel.getVCMetaData(), vcBioModel, ns);
			sbmlAnnotationUtil.readAnnotation(vcBioModel, sbmlModel);
			sbmlAnnotationUtil.readNotes(vcBioModel, sbmlModel);
//			vcBioModel.getVCMetaData().printRdfPretty();
//			vcBioModel.getVCMetaData().printRdfStatements();
		} catch(Exception e) {
			throw new SBMLImportException("failed to process SBML annotations: "+e.getMessage(), e);
		}

		//
		// translate read SBML model and import contents into VCell BioModel
		//
		try {
			translateSBMLModel(sbmlModel, vcBioModel, sbmlAnnotationUtil, sbmlUnitIdentifierHash, sbmlSymbolMapping, vcLogger);
			vcBioModel.refreshDependencies();
		} catch(Exception e) {
			throw new SBMLImportException("Failed to translate SBML model into BioModel: "+e.getMessage(), e);
		}

		//
		// for imported lumped reactions kinetics, attempt to switch to the corresponding distributed kinetics
		//
		try {
			for (ReactionStep reactionStep : vcBioModel.getModel().getReactionSteps()) {
				if (reactionStep.getKinetics().getKineticsDescription().isLumped()) {
					DistributedKinetics.toDistributedKinetics((LumpedKinetics) reactionStep.getKinetics());
				}
			}
		} catch(Exception e) {
			throw new SBMLImportException("failed to convert lumped reaction kinetics to distributed: "+e.getMessage(), e);
		}

		//
		// report issues VCLogger and log4j2 logger
		//
		Issue[] warnings = localIssueList.toArray(new Issue[0]);
		if (warnings.length > 0) {
			StringBuilder messageBuffer = new StringBuilder("Issues encountered during SBML Import:\n");
			int issueCount = 0;
			for (Issue warning : warnings) {
				if (warning.getSeverity() == Issue.Severity.ERROR || warning.getSeverity() == Issue.Severity.WARNING || warning.getSeverity() == Issue.Severity.INFO) {
					org.apache.logging.log4j.Level logLevel = org.apache.logging.log4j.Level.getLevel(warning.getSeverity().name());
					logger.log(logLevel, warning.getMessage() + " (" + warning.getCategory() + ")\n");
					messageBuffer.append("- ").append(warning.getMessage()).append("\n");
					issueCount++;
				}
			}
			if (issueCount > 0) {
				try {
					vcLogger.sendMessage(VCLogger.Priority.MediumPriority, VCLogger.ErrorType.OverallWarning, messageBuffer.toString());
				} catch (Exception e) {
					logger.error("failed to report issues to VCLogger", e);
				}
			}
		}

		//
		// finally convert back to VCell unit system if it originated from VCell
		// TODO: need to expand options here:
		//   do not convert if called from CLI or other automated conversion routines
		//   if called from GUI application convert if VCell origin, otherwise ask user
		//
		if (isFromVCell) {
			logger.error("SKIPPING translation back to vcell unit system ... testing only");
//			ModelUnitSystem vcUnitSystem = ModelUnitSystem.createDefaultVCModelUnitSystem();
//			try {
//				return ModelUnitConverter.createBioModelWithNewUnitSystem(vcBioModel, vcUnitSystem);
//			} catch (Exception e) {
//				logger.error("failed to convert imported SBML model into default VCell Unit System - continuing anyway!!",e);
//				// TODO: maybe alert user? for now fail silently...
//			}
		}
		return vcBioModel;
	}

	private SBMLDocument readSbmlDocument(File sbmlFile) {
		SBMLDocument document;
		final String defaultErrorPrefix = "Unable to read SBML file";
		try {
			// Read SBML model into libSBML SBMLDocument and create an SBML model
			List<String> readLines = FileUtils.readLines(sbmlFile,Charset.defaultCharset());
			StringBuilder sb = new StringBuilder();
			//Temporary fix for org.sbml.jsbml.xml.parsers.RenderParser.processEndDocument(SBMLDocument sbmlDocument)
			//throws NPE when "<sbml ... xmlns:render... " is defined in input document
			for (String line:readLines) {
				String str = "xmlns:render=\"http://www.sbml.org/sbml/level3/version1/render/version1\"";
				int indexOf = line.indexOf(str);
				if(indexOf != -1) {
					line = line.substring(0,indexOf-1)+line.substring(indexOf+str.length());
				}
				str = "render:required=\"false\"";
				indexOf = line.indexOf(str);
				if(indexOf != -1) {
					line = line.substring(0,indexOf-1)+line.substring(indexOf+str.length());
				}
				sb.append(line).append("\n");
			}
			SBMLReader reader = new SBMLReader();
			String sbmlString = sb.toString();
			document = reader.readSBMLFromString(sbmlString);
			// check for VCell origin
			String topNotes = document.getNotesString();
			if (topNotes != null && topNotes.contains("VCell")) {
				isFromVCell  = true;
			}

		} catch(Exception e) {
			throw new SBMLImportException(defaultErrorPrefix + ": \n" + sbmlFile, e);
		}
		return document;
	}

	private SBMLDocument readSbmlDocument(InputStream sbmlInputStream) {
		final String defaultErrorPrefix = "Unable to read SBML stream";
		try {
			SBMLReader reader = new SBMLReader();
			SBMLDocument document = reader.readSBMLFromStream(new BufferedInputStream(sbmlInputStream));
			// check for VCell origin
			String topNotes = document.getNotesString();
			if (topNotes != null && topNotes.contains("VCell")) {
				isFromVCell  = true;
			}
			return document;

		} catch(XMLStreamException e) {
			throw new SBMLImportException("Unable to read SBML stream", e);
		} finally {
			try {
				sbmlInputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void validateSBMLDocument(SBMLDocument document, VCLogger vcLogger) throws VCLoggerException {
		//document.checkConsistencyOffline();
		document.checkConsistencyOnline();

		long numProblems = document.getNumErrors();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		if (document.getErrorCount()>0) {
			document.printErrors(ps);
		}
		String output = os.toString();
		if (numProblems > 0) {
			logger.error("Num problems in original SBML document : " + numProblems);
			logger.error(output);
			if (output.contains("[warning]") && !output.contains("[error]")) {
				vcLogger.sendMessage(VCLogger.Priority.MediumPriority, VCLogger.ErrorType.OverallWarning, "SBML document had warnings: " + output);
			}else{
				vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.OverallWarning, "SBML document had errors: " + output);
			}
		}
	}

	private static void validateSBMLPackages(SBMLDocument document, Vector<Issue> localIssueList, IssueContext issueContext) {
		int numPackages = 0;
		String msgPackages = "";
		try {
			if(document.isPackageEnabled("comp")) {
				numPackages++;
				msgPackages += "'comp', ";
			}
			if(document.isPackageEnabled("fbc")) {
				numPackages++;
				msgPackages += "'fbc', ";
			}
			if(document.isPackageEnabled("multi")) {
				numPackages++;
				msgPackages += "'multi', ";
			}
			if(document.isPackageEnabled("qual")) {
				numPackages++;
				msgPackages += "'qual', ";
			}
		} catch(Exception e) {
			throw new SBMLImportException("Unable to check the SBML file package requirements.", e);
		}
		String ext = "extension";
		String is = "is";
		String has = "has";
		if(numPackages > 0) {
			if(numPackages > 1) {
				ext += "s";
				is = "are";
			}
			msgPackages = msgPackages.substring(0, msgPackages.length()-1);
			msgPackages = "The model includes elements of SBML " + ext + " " + msgPackages + " which " + is + " required for simulating the model but " + is + " not supported.";
			throw new SBMLImportException("Unable to import the SBML file.\n" + msgPackages);
		}
		try {
			if(document.isPackageEnabled("groups")) {
				numPackages++;
				msgPackages += "'groups', ";
			}
			if(document.isPackageEnabled("layout")) {
				numPackages++;
				msgPackages += "'layout', ";
			}
			if(document.isPackageEnabled("render")) {
				numPackages++;
				msgPackages += "'render', ";
			}
		} catch(Exception e) {
			logger.error("we are going to ignore groups/layout/render packages anyway", e);
		}
		if(numPackages > 0) {
			if(numPackages > 1) {
				ext += "s";
				is = "are";
				has = "have";
			}
			msgPackages = "The model includes elements of SBML " + ext + " " + msgPackages + " which " + is + " not required for simulating the model and " + has + " been ignored.";
			localIssueList.add(new Issue(new SBMLIssueSource(document), issueContext, IssueCategory.SBMLImport_UnsupportedFeature, msgPackages, Issue.Severity.WARNING));
		}
	}

	private static ModelUnitSystem createSBMLUnitSystemForVCModel(
			org.sbml.jsbml.Model sbmlModel, HashMap<String, VCUnitDefinition> sbmlUnitIdentifierHash,
			Vector<Issue> localIssueList, IssueContext issueContext)  {

		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf<UnitDefinition> listofUnitDefns = sbmlModel.getListOfUnitDefinitions();
		if (listofUnitDefns == null) {
			logger.warn("No Unit Definitions in SBML model, using default VCell Unit System");
			// if < level 3, use SBML default units to create unit system; else,
			// return a default VC modelUnitSystem.
			// @TODO: deal with SBML level < 3.
			return ModelUnitSystem.createDefaultVCModelUnitSystem();
		}

		@SuppressWarnings("serial")
		VCUnitSystem tempVCUnitSystem = ModelUnitSystem.createDefaultVCModelUnitSystem();
		// add base SI unit identifiers (as defined in SBML spec) to hash
		sbmlUnitIdentifierHash.put("ampere", tempVCUnitSystem.getInstance("A"));
		sbmlUnitIdentifierHash.put("avogadro",
				tempVCUnitSystem.getInstance("6.02214179e23"));
		// sbmlUnitIdentifierHash.put("becquerel",
		// tempVCUnitSystem.getInstance("becquerel"));
		// sbmlUnitIdentifierHash.put("candela",
		// tempVCUnitSystem.getInstance("candela"));
		sbmlUnitIdentifierHash
				.put("coulomb", tempVCUnitSystem.getInstance("C"));
		sbmlUnitIdentifierHash.put("dimensionless",
				tempVCUnitSystem.getInstance("1"));
		sbmlUnitIdentifierHash.put("farad", tempVCUnitSystem.getInstance("F"));
		sbmlUnitIdentifierHash.put("gram", tempVCUnitSystem.getInstance("g"));
		// sbmlUnitIdentifierHash.put("gray",
		// tempVCUnitSystem.getInstance("gray"));
		sbmlUnitIdentifierHash.put("henry", tempVCUnitSystem.getInstance("H"));
		sbmlUnitIdentifierHash.put("hertz", tempVCUnitSystem.getInstance("Hz"));
		sbmlUnitIdentifierHash.put("item",
				tempVCUnitSystem.getInstance("molecules"));
		sbmlUnitIdentifierHash.put("joule", tempVCUnitSystem.getInstance("J"));
		// sbmlUnitIdentifierHash.put("katal",
		// tempVCUnitSystem.getInstance("katal"));
		sbmlUnitIdentifierHash.put("kelvin", tempVCUnitSystem.getInstance("K"));
		sbmlUnitIdentifierHash.put("kilogram",
				tempVCUnitSystem.getInstance("kg"));
		sbmlUnitIdentifierHash.put("litre",
				tempVCUnitSystem.getInstance("litre"));
		// sbmlUnitIdentifierHash.put("lumen",
		// tempVCUnitSystem.getInstance("lumen"));
		// sbmlUnitIdentifierHash.put("lux",
		// tempVCUnitSystem.getInstance("lux"));
		sbmlUnitIdentifierHash.put("metre", tempVCUnitSystem.getInstance("m"));
		sbmlUnitIdentifierHash.put("mole", tempVCUnitSystem.getInstance("mol"));
		sbmlUnitIdentifierHash.put("newton", tempVCUnitSystem.getInstance("N"));
		// sbmlUnitIdentifierHash.put("ohm",
		// tempVCUnitSystem.getInstance("ohm"));
		// sbmlUnitIdentifierHash.put("pascal",
		// tempVCUnitSystem.getInstance("pascal"));
		// sbmlUnitIdentifierHash.put("radian",
		// tempVCUnitSystem.getInstance("radian"));
		sbmlUnitIdentifierHash.put("second", tempVCUnitSystem.getInstance("s"));
		sbmlUnitIdentifierHash
				.put("siemens", tempVCUnitSystem.getInstance("S"));
		// sbmlUnitIdentifierHash.put("sievert",
		// tempVCUnitSystem.getInstance("sievert"));
		// sbmlUnitIdentifierHash.put("steradian",
		// tempVCUnitSystem.getInstance("steradian"));
		// sbmlUnitIdentifierHash.put("tesla",
		// tempVCUnitSystem.getInstance("tesla"));
		sbmlUnitIdentifierHash.put("volt", tempVCUnitSystem.getInstance("V"));
		sbmlUnitIdentifierHash.put("watt", tempVCUnitSystem.getInstance("W"));
		sbmlUnitIdentifierHash.put("weber", tempVCUnitSystem.getInstance("Wb"));

		long sbmlLevel = sbmlModel.getLevel();
		if (sbmlLevel < 3) {
			// SBML predefined unit identifiers
			sbmlUnitIdentifierHash.put(UnitDefinition.SUBSTANCE, tempVCUnitSystem.getInstance("mole"));
			sbmlUnitIdentifierHash.put(UnitDefinition.VOLUME, tempVCUnitSystem.getInstance("litre"));
			sbmlUnitIdentifierHash.put(UnitDefinition.AREA, tempVCUnitSystem.getInstance("m2"));
			sbmlUnitIdentifierHash.put(UnitDefinition.LENGTH, tempVCUnitSystem.getInstance("m"));
			sbmlUnitIdentifierHash.put(UnitDefinition.TIME, tempVCUnitSystem.getInstance("s"));
		} else {
			sbmlUnitIdentifierHash.put(UnitDefinition.TIME, tempVCUnitSystem.getInstance("s"));
		}

		if (sbmlModel.isSetSubstanceUnits()){
			UnitDefinition ud = sbmlModel.getSubstanceUnitsInstance();
			VCUnitDefinition vcUnitDef = SBMLUnitTranslator.getVCUnitDefinition(ud, tempVCUnitSystem);
			sbmlUnitIdentifierHash.put(UnitDefinition.SUBSTANCE, vcUnitDef);
		}
		if (sbmlModel.isSetVolumeUnits()){
			UnitDefinition ud = sbmlModel.getVolumeUnitsInstance();
			VCUnitDefinition vcUnitDef = SBMLUnitTranslator.getVCUnitDefinition(ud, tempVCUnitSystem);
			sbmlUnitIdentifierHash.put(UnitDefinition.VOLUME, vcUnitDef);
		}
		if (sbmlModel.isSetAreaUnits()){
			UnitDefinition ud = sbmlModel.getAreaUnitsInstance();
			VCUnitDefinition vcUnitDef = SBMLUnitTranslator.getVCUnitDefinition(ud, tempVCUnitSystem);
			sbmlUnitIdentifierHash.put(UnitDefinition.AREA, vcUnitDef);
		}
		if (sbmlModel.isSetLengthUnits()){
			UnitDefinition ud = sbmlModel.getLengthUnitsInstance();
			VCUnitDefinition vcUnitDef = SBMLUnitTranslator.getVCUnitDefinition(ud, tempVCUnitSystem);
			sbmlUnitIdentifierHash.put(UnitDefinition.LENGTH, vcUnitDef);
		}
		if (sbmlModel.isSetTimeUnits()){
			UnitDefinition ud = sbmlModel.getTimeUnitsInstance();
			VCUnitDefinition vcUnitDef = SBMLUnitTranslator.getVCUnitDefinition(ud, tempVCUnitSystem);
			sbmlUnitIdentifierHash.put(UnitDefinition.TIME, vcUnitDef);
		}
		// read unit definition (identifiers) declared in SBML model
		for (int i = 0; i < sbmlModel.getNumUnitDefinitions(); i++) {
			UnitDefinition ud = listofUnitDefns.get(i);
			String unitName = ud.getId();
//			logger.trace("sbml id: " + unitName);
			VCUnitDefinition vcUnitDef = SBMLUnitTranslator.getVCUnitDefinition(ud, tempVCUnitSystem);
			sbmlUnitIdentifierHash.put(unitName, vcUnitDef);
		}

		// For SBML level 2
		// default units
		VCUnitDefinition defaultSubstanceUnit = sbmlUnitIdentifierHash.get(UnitDefinition.SUBSTANCE);
		VCUnitDefinition defaultVolumeUnit = sbmlUnitIdentifierHash.get(UnitDefinition.VOLUME);
		VCUnitDefinition defaultAreaUnit = sbmlUnitIdentifierHash.get(UnitDefinition.AREA);
		VCUnitDefinition defaultLengthUnit = sbmlUnitIdentifierHash.get(UnitDefinition.LENGTH);
		VCUnitDefinition defaultTimeUnit = sbmlUnitIdentifierHash.get(UnitDefinition.TIME);

		VCUnitDefinition modelSubstanceUnit = null;
		VCUnitDefinition modelVolumeUnit = null;
		VCUnitDefinition modelAreaUnit = null;
		VCUnitDefinition modelLengthUnit = null;
		VCUnitDefinition modelTimeUnit = null;

		// units in SBML model

		// compartments
		ListOf<Compartment> listOfCompartments = sbmlModel.getListOfCompartments();
		for (Compartment sbmlComp : listOfCompartments) {
			double dim = 3;
			if (sbmlComp.isSetSpatialDimensions()){
				dim = sbmlComp.getSpatialDimensions();
			}
			String unitStr = sbmlComp.getUnits();
			VCUnitDefinition sbmlUnitDefinition = null;
			if (unitStr != null && unitStr.length() > 0) {
				sbmlUnitDefinition = sbmlUnitIdentifierHash.get(unitStr);
			} else {
				// applying default unit if not defined for this compartment
				if (dim == 3) {
					sbmlUnitDefinition = defaultVolumeUnit;
				} else if (dim == 2) {
					sbmlUnitDefinition = defaultAreaUnit;
				} else if (dim == 1) {
					sbmlUnitDefinition = defaultLengthUnit;
				}
			}
			if (dim == 3) {
				if (sbmlUnitDefinition == null) {
					sbmlUnitDefinition = defaultVolumeUnit;
				}
				if (modelVolumeUnit == null) {
					modelVolumeUnit = sbmlUnitDefinition;
				} else if (!sbmlUnitDefinition.isEquivalent(modelVolumeUnit)) {
					localIssueList
							.add(new Issue(
									new SBMLIssueSource(sbmlComp),
									issueContext,
									IssueCategory.Units,
									"unit for compartment '"
											+ sbmlComp.getId()
											+ "' ("
											+ unitStr
											+ ") : ("
											+ sbmlUnitDefinition.getSymbol()
											+ ") not compatible with current vol unit ("
											+ modelVolumeUnit.getSymbol() + ")",
									Issue.Severity.WARNING));
					// logger.sendMessage(VCLogger.Priority.MediumPriority,
					// VCLogger.ErrorType.UnitError, "unit for compartment '" +
					// sbmlComp.getId() + "' (" + unitStr + ") : (" +
					// sbmlUnitDefinition.getSymbol() +
					// ") not compatible with current vol unit (" +
					// modelVolumeUnit.getSymbol() + ")");
				}
			} else if (dim == 2) {
				if (modelAreaUnit == null) {
					modelAreaUnit = sbmlUnitDefinition;
				} else if (!sbmlUnitDefinition.isEquivalent(modelAreaUnit)) {
					localIssueList
							.add(new Issue(
									new SBMLIssueSource(sbmlComp),
									issueContext,
									IssueCategory.Units,
									"unit for compartment '"
											+ sbmlComp.getId()
											+ "' ("
											+ unitStr
											+ ") : ("
											+ sbmlUnitDefinition.getSymbol()
											+ ") not compatible with current area unit ("
											+ modelAreaUnit.getSymbol() + ")",
									Issue.Severity.WARNING));
					// logger.sendMessage(VCLogger.Priority.MediumPriority,
					// VCLogger.ErrorType.UnitError, "unit for compartment '" +
					// sbmlComp.getId() + "' (" + unitStr + ") : (" +
					// sbmlUnitDefinition.getSymbol() +
					// ") not compatible with current area unit (" +
					// modelAreaUnit.getSymbol() + ")");
				}
			}
		}

		// species
		ListOf<org.sbml.jsbml.Species> listOfSpecies = sbmlModel.getListOfSpecies();
		for (Species sbmlSpecies : listOfSpecies) {
			String unitStr = sbmlSpecies.getSubstanceUnits();
			final VCUnitDefinition sbmlUnitDefinition;
			if (unitStr != null && unitStr.length() > 0) {
				sbmlUnitDefinition = sbmlUnitIdentifierHash.get(unitStr);
			} else {
				// apply default substance unit
				sbmlUnitDefinition = defaultSubstanceUnit;
			}
			if (modelSubstanceUnit == null) {
				modelSubstanceUnit = sbmlUnitDefinition;
			} else if (sbmlUnitDefinition != null && !sbmlUnitDefinition.isEquivalent(modelSubstanceUnit)) {
				localIssueList.add(new Issue(
								new SBMLIssueSource(sbmlSpecies),
								issueContext,
								IssueCategory.Units,
								"unit for species '"	+ sbmlSpecies.getId() + "'" +
										" (" + unitStr + ") :" + " (" + sbmlUnitDefinition.getSymbol() + ")" +
										" not compatible with current substance unit (" + modelSubstanceUnit.getSymbol() + ")",
								Issue.Severity.WARNING));
			}
		}

		// reactions for SBML level 2 version < 3
		long sbmlVersion = sbmlModel.getVersion();
		if (sbmlVersion < 3) {
			ListOf<Reaction> listOfReactions = sbmlModel.getListOfReactions();
			for (Reaction sbmlReaction : listOfReactions) {
				KineticLaw kineticLaw = sbmlReaction.getKineticLaw();
				if (kineticLaw != null) {
					// first check substance unit
					String unitStr = kineticLaw.getSubstanceUnits();
					VCUnitDefinition sbmlUnitDefinition;
					if (unitStr != null && unitStr.length() > 0) {
						sbmlUnitDefinition = sbmlUnitIdentifierHash.get(unitStr);
					} else {
						// apply default substance unit
						sbmlUnitDefinition = defaultSubstanceUnit;
					}
					if (modelSubstanceUnit == null) {
						modelSubstanceUnit = sbmlUnitDefinition;
					} else if (sbmlUnitDefinition != null && !sbmlUnitDefinition.isEquivalent(modelSubstanceUnit)) {
						localIssueList.add(new Issue(
							new SBMLIssueSource(sbmlReaction),
							issueContext,
							IssueCategory.Units,
							"substance unit for reaction '" + sbmlReaction.getId() + "'" +
										" (" + unitStr + ") :" + " (" + sbmlUnitDefinition.getSymbol() + ")" +
										" not compatible with current substance unit (" + modelSubstanceUnit.getSymbol() + ")",
							Issue.Severity.WARNING));
					}
					// check time unit
					unitStr = kineticLaw.getTimeUnits();
					if (unitStr != null && unitStr.length() > 0) {
						sbmlUnitDefinition = sbmlUnitIdentifierHash.get(unitStr);
					} else {
						// apply default time unit
						sbmlUnitDefinition = defaultTimeUnit;
					}
					if (modelTimeUnit == null) {
						modelTimeUnit = sbmlUnitDefinition;
					} else if (!sbmlUnitDefinition.isEquivalent(modelTimeUnit)) {
						localIssueList.add(new Issue(
							new SBMLIssueSource(sbmlReaction),
							issueContext,
							IssueCategory.Units,
							"time unit for reaction '" + sbmlReaction.getId() + "'" +
										" (" + unitStr + ") : (" + sbmlUnitDefinition.getSymbol() + ")" +
										" not compatible with current time unit (" + modelTimeUnit.getSymbol() + ")",
							Issue.Severity.WARNING)
						);
					}
				}
			}
		}

		ModelUnitSystem mus = ModelUnitSystem.createDefaultVCModelUnitSystem();
		if(modelVolumeUnit != null && modelVolumeUnit.isEquivalent(mus.getInstance_DIMENSIONLESS())) {
			if(modelAreaUnit == null) {
				modelLengthUnit = mus.getInstance_DIMENSIONLESS();
				modelAreaUnit = mus.getInstance_DIMENSIONLESS();
			}
		}
		if (modelSubstanceUnit == null) {
			modelSubstanceUnit = defaultSubstanceUnit;
		}
		if (modelVolumeUnit == null) {
			modelVolumeUnit = defaultVolumeUnit;
		}
		if (modelAreaUnit == null) {
			modelAreaUnit = defaultAreaUnit;
		}
		if (modelLengthUnit == null) {
			modelLengthUnit = defaultLengthUnit;
		}
		if (modelTimeUnit == null) {
			modelTimeUnit = defaultTimeUnit;
		}

		if (modelSubstanceUnit == null && modelVolumeUnit == null
				&& modelAreaUnit == null && modelLengthUnit == null
				&& modelTimeUnit == null) {
			// no default units specified in SBML Level 3, so just return a
			// default (VC)modelUnitSystem
			return ModelUnitSystem.createDefaultVCModelUnitSystem();
		} else {
			ModelUnitSystem modelUnitSystem = ModelUnitSystem.createDefaultVCModelUnitSystem();
			VCUnitDefinition mole = modelUnitSystem.getInstance("mole");
			VCUnitDefinition molecules = modelUnitSystem.getInstance("molecules");
			VCUnitDefinition dimensionless = modelUnitSystem.getInstance_DIMENSIONLESS();
			if (modelSubstanceUnit == null) {
				modelSubstanceUnit = dimensionless;
			}
			if (modelVolumeUnit == null) {
				modelVolumeUnit = dimensionless;
			}
			if (modelAreaUnit == null) {
				modelAreaUnit = dimensionless;
			}
			if (modelLengthUnit == null) {
				modelLengthUnit = dimensionless;
			}
			if (!modelSubstanceUnit.isCompatible(mole) && !modelSubstanceUnit.isCompatible(molecules) && !modelSubstanceUnit.isCompatible(dimensionless)) {
				localIssueList.add(new Issue(new SBMLIssueSource(sbmlModel), issueContext, IssueCategory.SBMLImport_RestrictedFeature,
						"Model substance unit ["+modelSubstanceUnit+"] is not compatible with mole or molecules", Issue.Severity.WARNING));
			}
			return ModelUnitSystem.createSBMLUnitSystem(modelSubstanceUnit,
					modelVolumeUnit, modelAreaUnit, modelLengthUnit, modelTimeUnit);
		}
	}

	/**
	 * getEmbeddedElementInRxnAnnotation : Takes the reaction annotation as an
	 * argument and returns the embedded element (fluxstep or simple reaction),
	 * if present.
	 */
	private static Element getEmbeddedElementInAnnotation(Element sbmlImportRelatedElement, String tag) {
		// Get the XML element corresponding to the annotation xmlString.
		String elementName = null;
		switch (tag) {
			case RATE_NAME: {
				elementName = XMLTags.ReactionRateTag;
				break;
			}
			case SPECIES_NAME: {
				elementName = XMLTags.SpeciesTag;
				break;
			}
			case REACTION: {
				if (sbmlImportRelatedElement.getChild(XMLTags.FluxStepTag, sbmlImportRelatedElement.getNamespace()) != null) {
					elementName = XMLTags.FluxStepTag;
				} else if (sbmlImportRelatedElement.getChild(XMLTags.SimpleReactionTag, sbmlImportRelatedElement.getNamespace()) != null) {
					elementName = XMLTags.SimpleReactionTag;
				}
				break;
			}
			case OUTSIDE_COMP_NAME: {
				elementName = XMLTags.OutsideCompartmentTag;
				break;
			}
		}
		// If there is an annotation element for the reaction or species,
		// retrieve and return.
		if (sbmlImportRelatedElement != null) {
			for (int j = 0; j < sbmlImportRelatedElement.getChildren().size(); j++) {
				Element infoChild = sbmlImportRelatedElement.getChild(
						elementName, sbmlImportRelatedElement.getNamespace());
				if (infoChild != null) {
					return infoChild;
				}
			}
		}
		return null;
	}

	/**
	 * getExpressionFromFormula : Convert the math formula string in a
	 * kineticLaw, rule or lambda function definition into MathML and use
	 * ExpressionMathMLParser to convert the MathML into an expression to be
	 * brought into the VCell. NOTE : ExpressionMathMLParser will handle only
	 * the <apply> elements of the MathML string, hence the
	 * ExpressionMathMLParser is given a substring of the MathML containing the
	 * <apply> elements.
	 */
	private static Expression getExpressionFromFormula(ASTNode math, LambdaFunction[] lambdaFunctions, VCLogger vcLogger) throws Exception {
		if (math == null){
			throw new RuntimeException("ASTNode is null");
		}
		String mathMLStr = JSBML.writeMathMLToString(math);
		if (mathMLStr.contains(DELAY_URL)) {
			String msg = "unsupported SBML element 'delay' in expression '"+mathMLStr+"'";
			logger.error(msg);
			vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.UnsupportedConstruct, msg);
			return new Expression(0.0);
		}
		ExpressionMathMLParser exprMathMLParser = new ExpressionMathMLParser(lambdaFunctions);
		try {
			return exprMathMLParser.fromMathML(mathMLStr, TIME_SYMBOL_OVERRIDE);
		}catch (ExpressionException e){
			String msg = "error parsing expression '"+mathMLStr+"': "+e.getMessage();
			logger.error(msg, e);
			vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.UnsupportedConstruct, msg);
			return new Expression(0.0);
		}
	}

	/**
	 *
	 */
	private static Structure getReactionStructure(org.sbml.jsbml.Model sbmlModel, org.sbml.jsbml.Reaction sbmlRxn, BioModel vcBioModel,
												  LambdaFunction[] lambdaFunctions, SBMLSymbolMapping sbmlSymbolMapping,
												  VCLogger vcLogger) throws Exception {
		Structure struct;
		String structName = null;
		Model vcModel = vcBioModel.getSimulationContext(0).getModel();

		// if sbml model is level 3, see if reaction has compartment attribute,
		// return structure from vcmodel, if present.
		structName = sbmlRxn.getCompartment();
		if (structName != null && structName.length() > 0) {
			struct = vcModel.getStructure(structName);
			if (struct != null) {
				return struct;
			}
		}

		if (sbmlRxn.isSetKineticLaw()) {
			// String rxnName = sbmlRxn.getId();
			KineticLaw kLaw = sbmlRxn.getKineticLaw();
			Expression kRateExp = getExpressionFromFormula(kLaw.getMath(), lambdaFunctions, vcLogger);
			String[] symbols = kRateExp.getSymbols();
			if (symbols != null) {
				for (String symbol : symbols) {
					Compartment sbmlCompartment = sbmlModel
							.getCompartment(symbol);
					if (sbmlCompartment != null) {
						return vcBioModel.getSimulationContext(0).getModel().getStructure(
								sbmlCompartment.getId());
					}
				}
			}
		}

		HashSet<String> refSpeciesNameHash = new HashSet<>();
		getReferencedSpecies(sbmlModel, sbmlRxn, refSpeciesNameHash, lambdaFunctions, sbmlSymbolMapping, vcLogger);

		java.util.Iterator<String> refSpIterator = refSpeciesNameHash.iterator();
		HashSet<String> compartmentNamesHash = new HashSet<>();
		while (refSpIterator.hasNext()) {
			String spName = refSpIterator.next();
			String rxnCompartmentName = sbmlModel.getSpecies(spName)
					.getCompartment();
			compartmentNamesHash.add(rxnCompartmentName);
		}

		if (compartmentNamesHash.size() == 1) {
			struct = vcModel.getStructure(compartmentNamesHash.iterator().next());
			return struct;
		} else if (compartmentNamesHash.size() == 0) {
			struct = vcModel.getStructures()[0];
			return struct;
		} else {
			// more than one structure in reaction participants, try to figure
			// out which one to choose
			HashMap<String, Integer> structureFrequencyHash = new HashMap<>();
			for (String structureName : compartmentNamesHash) {
				if (structureFrequencyHash.containsKey(structureName)) {
					structureFrequencyHash.put(structureName,
							structureFrequencyHash.get(structName) + 1);
				} else {
					structureFrequencyHash.put(structureName, 1);
				}
			}
			Iterator<Entry<String, Integer>> iterator = structureFrequencyHash
					.entrySet().iterator();
			Entry<String, Integer> mostUsedStructureEntry = iterator.next();
			while (iterator.hasNext()) {
				Entry<String, Integer> currentStructureEntry = iterator.next();
				if (currentStructureEntry.getValue() > mostUsedStructureEntry
						.getValue()) {
					mostUsedStructureEntry = currentStructureEntry;
				}
			}
			String mostUsedStructureName = mostUsedStructureEntry.getKey();
			struct = vcModel.getStructure(mostUsedStructureName);
			return struct;
		}
	}

	//
	// getSpatialDimentionBuiltInName :
	//
	/*
	 * pending delete? gcw 4/2014 private String
	 * getSpatialDimensionBuiltInName(int dimension) { String name = null;
	 * switch (dimension) { case 0 : { name = SBMLUnitTranslator.DIMENSIONLESS;
	 * break; } case 1 : { name = SBMLUnitTranslator.LENGTH; break; } case 2 : {
	 * name = SBMLUnitTranslator.AREA; break; } case 3 : { name =
	 * SBMLUnitTranslator.VOLUME; break; } } return name; }
	 */

	//
	// replaced with SBMLSymbolMapping.getAssignmentRuleSBMLExpression(SBase sbase)
	//
//	/**
//	 * getValueFromRuleOrFunctionDefinition : If the value of a kinetic law
//	 * parameter or species initial concentration/amount (or compartment volume)
//	 * is 0.0, check if it is given by a rule or functionDefinition, and return
//	 * the string (of the rule or functionDefinition expression).
//	 */
//	@Deprecated
//	private static Expression getValueFromAssignmentRule(String paramName, SBMLSymbolMapping sbmlSymbolMapping) {
//		Expression valueExpr = null;
//		// Check if param name has an assignment rule associated with it
//		int numAssgnRules = assignmentRulesMap.size();
//		for (int i = 0; i < numAssgnRules; i++) {
//			valueExpr = (Expression) assignmentRulesMap.get(paramName);
//			if (valueExpr != null) {
//				return new Expression(valueExpr);
//			}
//		}
//		return null;
//	}

	/*
	 * pending delete? gcw 4/2014 private boolean varHasRateRule(String
	 * paramName) { // Check if param name has an assignment rule associated
	 * with it int numRateRules = rateRulesHash.size(); for (int i = 0; i <
	 * numRateRules; i++) { Expression valueExpr =
	 * (Expression)rateRulesHash.get(paramName); if (valueExpr != null) { return
	 * true; } } return false; }
	 */

	//
	// checkForUnsupportedVCellFeatures:
	//
	// Check if SBML model has algebraic, rate rules, events, other
	// functionality that VCell does not support, such as:
	// 'hasOnlySubstanceUnits'; compartments with dimension 0; species that have
	// assignment rules that contain other species, etc. If so, stop the import
	// process, since there is no point proceeding with the import any further.
	//
	//
	private static void checkForUnsupportedVCellFeaturesAndApplyDefaults(
			org.sbml.jsbml.Model sbmlModel, VCLogger vcLogger, long level, boolean bSpatial) throws Exception {

		// Check if rules, if present, are algrbraic rules
		if (sbmlModel.getNumRules() > 0) {
			for (int i = 0; i < sbmlModel.getNumRules(); i++) {
				Rule rule = sbmlModel.getRule(i);
				if (rule instanceof AlgebraicRule) {
					vcLogger.sendMessage(VCLogger.Priority.HighPriority,
							VCLogger.ErrorType.UnsupportedConstruct,
							"Algebraic rules are not handled in the Virtual Cell at this time");
				}
			}
		}

		// Check if any of the compartments have spatial dimension 0
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			Compartment comp = sbmlModel.getCompartment(i);

			if (!comp.isSetSpatialDimensions()) {
				comp.setSpatialDimensions(3); // set default value to 3D
				
				if (level > 2) {
					// level 3+ does not have default value for spatialDimension. So cannot assume a value.
					vcLogger.sendMessage(
							VCLogger.Priority.MediumPriority,
							VCLogger.ErrorType.CompartmentError,
							"Compartment '" + comp.getId() + "' spatial dimension is not set; assuming 3.");
				}
			}
			if (!comp.isSetSize() && comp.isSetSpatialDimensions() && comp.getSpatialDimensions()>0) {
				comp.setSize(1.0); // set default size to 1.0
				
				if (level > 2) {
					// level 3+ does not have default value for size. So cannot assume a value.
					vcLogger.sendMessage(
							VCLogger.Priority.MediumPriority,
							VCLogger.ErrorType.CompartmentError,
							"Compartment '"	+ comp.getId() + "' size is not set; assuming 1.");
				}
			}

			// already adds this error during processing
//			if (comp.getSpatialDimensions() == 0 || comp.getSpatialDimensions() == 1) {
//				vcLogger.sendMessage(
//						VCLogger.Priority.HighPriority,
//						VCLogger.ErrorType.CompartmentError,
//						"Compartment '" + comp.getId() + "' has spatial dimension 0; this is not supported in VCell");
//			}
		}

		// if SBML model is spatial and has events, it cannot be imported, since
		// events are not supported in a spatial VCell model.
		if (bSpatial) {
			if (sbmlModel.getNumEvents() > 0) {
				vcLogger.sendMessage(
						VCLogger.Priority.HighPriority,
						VCLogger.ErrorType.UnsupportedConstruct,
						"Events are not supported in a spatial Virtual Cell model at this time, they are only supported in a non-spatial model.");
			}
		}

	}

	/**
	 * translateSBMLModel:
	 *
	 * 	1) parse SBML model for Compartments/Geometry/Species/Parameters/Reactions
	 * 	2) store SBase/STE and SBase/SbmlExpression mappings
	 * 	3) parse AssignmentRules, InitialAssignments, RateRules
	 * 	4) apply rules, translating sbml expressions to vcell expressions and set expressions on VCell objects.
	 *
	 */
	private static void translateSBMLModel(org.sbml.jsbml.Model sbmlModel, BioModel vcBioModel,
										   SBMLAnnotationUtil sbmlAnnotationUtil,
										   Map<String, VCUnitDefinition> sbmlUnitIdentifierHash,
										   SBMLSymbolMapping sbmlSymbolMapping, VCLogger vcLogger) throws Exception {

		final Vector<Issue> localIssueList = new Vector<>();
		final IssueContext issueContext = new IssueContext();

		boolean bSpatial = false;
		SpatialModelPlugin mplugin = (SpatialModelPlugin) sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		if (mplugin != null && mplugin.isSetGeometry()){
			bSpatial = true;
		}

		// Add Function Definitions (Lambda functions).
		LambdaFunction[] lambdaFunctions = addFunctionDefinitions(sbmlModel, vcLogger);
		long level = sbmlModel.getLevel();
		// Check for SBML features not supported in VCell; stop import process if present.
		try {
			checkForUnsupportedVCellFeaturesAndApplyDefaults(sbmlModel, vcLogger, level, bSpatial);
		} catch (Exception e) {
			throw new SBMLImportException(e.getMessage(), e);
		}

		// Create Virtual Cell Model with species, compartment, etc. and read in the 'values' from the SBML model

		// Add compartmentTypes (not handled in VCell)
		addCompartmentTypes(sbmlModel, localIssueList, issueContext, vcBioModel);

		// Add spciesTypes (not handled in VCell)
		addSpeciesTypes(sbmlModel, localIssueList, issueContext, vcBioModel);

		// determine geometric dimension
		int geometryDimension = 0;
		final org.sbml.jsbml.ext.spatial.Geometry sbmlGeometry;
		try {
			sbmlGeometry = getSbmlGeometry(sbmlModel, localIssueList, issueContext);
			if (sbmlGeometry != null) {
				geometryDimension = sbmlGeometry.getListOfCoordinateComponents().size();
			}
		} catch (Exception e) {
			throw new SBMLImportException(e.getMessage(), e);
		}

		// Add features/compartments
		VCMetaData vcMetaData = vcBioModel.getVCMetaData();
		addCompartments(sbmlModel, geometryDimension, vcBioModel, sbmlSymbolMapping, sbmlAnnotationUtil, vcLogger);
		// Add species/speciesContexts
		addSpecies(sbmlModel, vcBioModel, bSpatial, sbmlSymbolMapping, sbmlAnnotationUtil, vcLogger);

		// Add geometry, if sbml model is spatial (will set up speciesContextSpec parameters as needed)
		if (bSpatial) {
			addGeometry(sbmlModel, vcBioModel, lambdaFunctions, sbmlSymbolMapping, localIssueList, issueContext, vcLogger);

			//
			// reset all diffusion coefficients to 0.0
			// VCell defaults diffusion coefficients to non-zero values - but this would introduce SBML default values.
			//
			List<SpeciesContextSpecParameter> allDiffusionParameters = Arrays.stream(vcBioModel.getSimulationContext(0).getReactionContext().getSpeciesContextSpecs())
					.map(scs -> scs.getDiffusionParameter()).collect(Collectors.toList());
			for (SpeciesContextSpecParameter diffParam : allDiffusionParameters){
				try {
					diffParam.setExpression(new Expression(0.0));
				} catch (ExpressionException e){}
			}
		}

		// Add Parameters
		try {
			addParameters(sbmlModel, sbmlGeometry, vcBioModel, bSpatial, sbmlUnitIdentifierHash, sbmlSymbolMapping);
		} catch (Exception e) {
			throw new SBMLImportException(e.getMessage(), e);
		}

		// parse AssignmentRule and InitialAssignment and get (SBML Sbase / SBML Expression) pairs to be used later)
		try {
			parseAssignmentAndInitialAssignmentExpressions(sbmlModel, lambdaFunctions, sbmlSymbolMapping, vcLogger);
		} catch (SBMLImportException sie) {
			throw sie;
		} catch (Exception ee) {
			throw new SBMLImportException(ee.getMessage(), ee);
		}

		/**
		 * commented out old facility for renaming x,y,z ...
		 * just mangle the vcell name for the sbml x,y,z symbol and the adjustExpression will take care of the rest
		 */
//		// Create the vCell Assignment Rules, now that the species, parameters and structures are defined
//		// If species variables were renamed from x,y,z, apply corrections to the hash
//		try {
//			readAssignmentRules();
//		} catch (ExpressionException | PropertyVetoException e) {
//			throw new SBMLImportException(e.getMessage(), e);
//		}

		// Set initial conditions on species
		// assignment rules must be present already because initConc set by an assignment rule
		// takes precedence over initConc value set on species
		configureSpeciesContextSpecs(sbmlModel, vcBioModel, sbmlSymbolMapping, vcLogger, localIssueList, issueContext);

		// Add InitialAssignments
//		addInitialAssignments(sbmlModel, lambdaFunctions, sbmlSymbolMapping);

		// Add constraints (not handled in VCell)
		addConstraints(sbmlModel, vcLogger);
		
		// Add Reactions
		addReactions(sbmlModel, vcBioModel, bSpatial, lambdaFunctions, level, sbmlUnitIdentifierHash,
				sbmlSymbolMapping, sbmlAnnotationUtil, vcMetaData, vcLogger, localIssueList, issueContext);

		// Add Rules Rules : adding these later (after assignment rules, since
		// compartment/species/parameter need to be defined before rate rules
		// for those vars can be read in).
		try {
			readRateRules(sbmlModel, lambdaFunctions, sbmlSymbolMapping, vcLogger);
			applySavedExpressions(sbmlModel, sbmlSymbolMapping, vcLogger);
		} catch (ExpressionException | SBMLException | XMLStreamException e) {
			throw new SBMLImportException(e.getMessage(), e);
		}
		
		// now that we have the parameters loaded, we can bind
		// TODO: use BMDB model Whitcomb to test comp size initialized with expression
		finalizeCompartments(sbmlModel, vcBioModel, sbmlSymbolMapping, localIssueList, issueContext);

		try {
			if (!bSpatial) {
				StructureSizeSolver.updateRelativeStructureSizes(vcBioModel.getSimulationContext(0));
			}
		} catch (Exception e) {
			throw new SBMLImportException("Error adding Feature to vcModel " + e.getMessage(), e);
		}


		// Sort VCell-model Structures in structure array according to reaction
		// adjacency and parentCompartment.
		Structure[] sortedStructures = StructureSorter.sortStructures(vcBioModel.getSimulationContext(0).getModel());
		try {
			vcBioModel.getSimulationContext(0).getModel().setStructures(sortedStructures);
		} catch (PropertyVetoException e1) {
			throw new SBMLImportException("Error while sorting compartments: " + e1.getMessage(), e1);
		}

		// Add Events
		addEvents(sbmlModel, bSpatial, vcBioModel, lambdaFunctions, sbmlSymbolMapping, vcLogger);
		// Check if names of species, structures, reactions, parameters are long
		// (say, > 64), if so give warning.
		try {
			checkIdentifiersNameLength(sbmlModel, localIssueList, issueContext);
		} catch (Exception e) {
			throw new SBMLImportException(e.getMessage(), e);
		}

		// post processing
		createAssignmentRules(sbmlModel, vcBioModel, sbmlSymbolMapping, localIssueList, issueContext, vcLogger);
		createRateRules(sbmlModel, vcBioModel, sbmlSymbolMapping);
		postProcessing(vcBioModel);
	}

	private static void applySavedExpressions(org.sbml.jsbml.Model sbmlModel, SBMLSymbolMapping sbmlSymbolMapping, VCLogger vcLogger) throws Exception {
		for (SBase sbmlValueTargetSbase : sbmlSymbolMapping.getSbmlValueTargets()){
			Double sbmlValue = sbmlSymbolMapping.getSbmlValue(sbmlValueTargetSbase);
			if (sbmlValue != null && !sbmlValue.isInfinite() && !sbmlValue.isNaN()) {
				EditableSymbolTableEntry targetSte = sbmlSymbolMapping.getInitialSte(sbmlValueTargetSbase);
				try {
					if (targetSte != null) {
						if (targetSte.isExpressionEditable()) {
							targetSte.setExpression(new Expression(sbmlValue));
						}
					} else {
						targetSte = sbmlSymbolMapping.getRuntimeSte(sbmlValueTargetSbase);
						if (targetSte != null) {
							if (targetSte.isExpressionEditable()) {
								targetSte.setExpression(new Expression(sbmlValue));
							}
						} else {
							logger.error("couldn't find vcell object mapped to sbml object: " + sbmlValueTargetSbase);
						}
					}
				} catch (ExpressionException e1) {
					String msg = "failed to set expression for SBML object " + sbmlValueTargetSbase + " on vcell object " + targetSte + ": " + e1.getMessage();
					logger.error(msg, e1);
					vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.OverallWarning, msg);
				}
			}else{
				String msg = "missing or unexpected value attribute '"+sbmlValue+"' for SBML object id "+sbmlValueTargetSbase.getId();
				logger.error(msg);
				vcLogger.sendMessage(VCLogger.Priority.HighPriority, VCLogger.ErrorType.OverallWarning, msg);
			}
		}
		for (SBase initialAssignmentTargetSbase : sbmlSymbolMapping.getInitialAssignmentTargets()){
			Expression sbmlExpr = sbmlSymbolMapping.getRuleSBMLExpression(initialAssignmentTargetSbase, SymbolContext.INITIAL);
			EditableSymbolTableEntry initialAssignmentTargetSte = sbmlSymbolMapping.getSte(initialAssignmentTargetSbase, SymbolContext.INITIAL);
			try {
				if (initialAssignmentTargetSte.isExpressionEditable()) {
					Expression vcellExpr = adjustExpression(sbmlModel, sbmlExpr, initialAssignmentTargetSte.getNameScope(), sbmlSymbolMapping, SymbolContext.INITIAL);
					initialAssignmentTargetSte.setExpression(vcellExpr);
				}
			}catch (Exception e){
				logger.error("failed to set expression for SBML object "+initialAssignmentTargetSbase+" on vcell object "+initialAssignmentTargetSte);
			}
		}
		for (SBase assignmentRuleTargetSbase : sbmlSymbolMapping.getAssignmentRuleTargets()){
			Expression sbmlExpr = sbmlSymbolMapping.getRuleSBMLExpression(assignmentRuleTargetSbase, SymbolContext.RUNTIME);
			EditableSymbolTableEntry assignmentRuleTargetSte = sbmlSymbolMapping.getSte(assignmentRuleTargetSbase, SymbolContext.RUNTIME);
			try {
				if (assignmentRuleTargetSte.isExpressionEditable()) {
					Expression vcellExpr = adjustExpression(sbmlModel, sbmlExpr, assignmentRuleTargetSte.getNameScope(), sbmlSymbolMapping, SymbolContext.RUNTIME);
					assignmentRuleTargetSte.setExpression(vcellExpr);
				}
			}catch (Exception e){
				logger.error("failed to set expression for SBML object "+assignmentRuleTargetSbase+" on vcell object "+assignmentRuleTargetSte);
			}
		}
	}

	private static void postProcessing(BioModel vcBioModel) {
		// clamp all RateRule and AssignmentRule species
		SimulationContext simContext = vcBioModel.getSimulationContext(0);
		SpeciesContextSpec[] scss = simContext.getReactionContext().getSpeciesContextSpecs();
		for(SpeciesContextSpec scs : scss) {
			SpeciesContext sc = scs.getSpeciesContext();
			if(simContext.getAssignmentRule(sc) != null || simContext.getRateRule(sc) != null) {
				scs.setConstant(true); // TODO JCS: this doesn't look right - what about reactions.
			}
		}
	}

	private static void checkIdentifiersNameLength(org.sbml.jsbml.Model sbmlModel, Vector<Issue> localIssueList, IssueContext issueContext) {
		// Check compartment name lengths
		ListOf<Compartment> listOfCompartments = sbmlModel.getListOfCompartments();
		boolean bLongCompartmentName = false;
		SBase issueSource = null;
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			Compartment compartment = listOfCompartments.get(i);
			String compartmentName = compartment.getId();
			if (compartmentName.length() > 64) {
				bLongCompartmentName = true;
				issueSource = compartment;
			}
		}
		// Check species name lengths
		ListOf<Species> listofSpecies = sbmlModel.getListOfSpecies();
		boolean bLongSpeciesName = false;
		for (int i = 0; i < sbmlModel.getNumSpecies(); i++) {
			org.sbml.jsbml.Species species = listofSpecies.get(i);
			String speciesName = species.getId();
			if (speciesName.length() > 64) {
				bLongSpeciesName = true;
				issueSource = species;
			}
		}
		// Check parameter name lengths
		ListOf<Parameter> listOfParameters = sbmlModel.getListOfParameters();
		boolean bLongParameterName = false;
		for (int i = 0; i < sbmlModel.getNumParameters(); i++) {
			Parameter param = listOfParameters.get(i);
			String paramName = param.getId();
			if (paramName.length() > 64) {
				bLongParameterName = true;
				issueSource = param;
			}
		}
		// Check reaction name lengths
		ListOf<Reaction> listOfReactions = sbmlModel.getListOfReactions();
		boolean bLongReactionName = false;
		for (int i = 0; i < sbmlModel.getNumReactions(); i++) {
			Reaction rxn = listOfReactions.get(i);
			String rxnName = rxn.getId();
			if (rxnName.length() > 64) {
				bLongReactionName = true;
				issueSource = rxn;
			}
		}

		if (bLongCompartmentName || bLongSpeciesName || bLongParameterName
				|| bLongReactionName) {
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
			warningMsg = warningMsg
					+ "that have ids/names that are longer than 64 characters. \n\nUser is STRONGLY recommeded to shorten "
					+ "the names to avoid problems with the length of expressions these names might be used in.";

			localIssueList.add(new Issue(new SBMLIssueSource(issueSource),
					issueContext,
					IssueCategory.SBMLImport_UnsupportedAttributeOrElement,
					warningMsg, Issue.Severity.WARNING));
			// logger.sendMessage(VCLogger.Priority.MediumPriority,
			// VCLogger.ErrorType.UnsupportedConstruct, warningMsg);
		}
	}

	private static void addReactions(org.sbml.jsbml.Model sbmlModel, BioModel vcBioModel, boolean bSpatial,
									   LambdaFunction[] lambdaFunctions, long level,
									   Map<String, VCUnitDefinition> sbmlUnitIdentifierHash,
									   SBMLSymbolMapping sbmlSymbolMapping,
									   SBMLAnnotationUtil sbmlAnnotationUtil, VCMetaData metaData,
									   VCLogger vcLogger, Vector<Issue> localIssueList, IssueContext issueContext) {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf<Reaction> reactions = sbmlModel.getListOfReactions();
		final int numReactions = reactions.size(); 
		if (numReactions == 0) {
			logger.info("No Reactions");
			return;
		}
		ArrayList<ReactionStep> vcReactionList = new ArrayList<>(); //all reactions
		ArrayList<ReactionStep> fastReactionList = new ArrayList<>(); //just the fast ones
		Model vcModel = vcBioModel.getSimulationContext(0).getModel();
		ModelUnitSystem vcModelUnitSystem = vcModel.getUnitSystem();
		SpeciesContext[] vcSpeciesContexts = vcModel.getSpeciesContexts();
		try {
			for (Reaction sbmlReaction: reactions) {
				ReactionStep vcReaction = null;
				String rxnSbmlName = sbmlReaction.getName();

				boolean bReversible = true;
				if (sbmlReaction.isSetReversible()){
					bReversible = sbmlReaction.getReversible();
				}
				boolean bIsFluxReaction = false;
				boolean bIsFast = false;
				XMLNode reactionNonRdfAnnotation = sbmlReaction.getAnnotation().getNonRDFannotation();
				if (reactionNonRdfAnnotation != null) {
					XMLNode reactionAttributesElement = reactionNonRdfAnnotation.getChildElement(XMLTags.SBML_VCELL_ReactionAttributesTag, "*");
					if (reactionAttributesElement != null) {
						int fastAttrIndex = reactionAttributesElement.getAttrIndex(XMLTags.SBML_VCELL_ReactionAttributesTag_fastAttr, SBMLUtils.SBML_VCELL_NS);
						if (fastAttrIndex >= 0) {
							String fastAttrValue = reactionAttributesElement.getAttrValue(fastAttrIndex);
							if (fastAttrValue.equals("true")) {
								fastReactionList.add(vcReaction);
							} else if (fastAttrValue.equals("false")) {
								logger.info("ignoring fast=false when reading reaction " + vcReaction.getName());
							} else {
								throw new RuntimeException("unexpected value " + fastAttrValue + " for " + XMLTags.SBML_VCELL_ReactionAttributesTag
										+ " attribute " + XMLTags.SBML_VCELL_ReactionAttributesTag_fastAttr);
							}
						}
						int fluxReactionAttrIndex = reactionAttributesElement.getAttrIndex(XMLTags.SBML_VCELL_ReactionAttributesTag_fluxReactionAttr, SBMLUtils.SBML_VCELL_NS);
						if (fluxReactionAttrIndex >= 0) {
							String fluxReactionAttrValue = reactionAttributesElement.getAttrValue(fluxReactionAttrIndex);
							if (fluxReactionAttrValue.equals("true")) {
								bIsFluxReaction = true;
							} else if (fluxReactionAttrValue.equals("false")) {
								logger.info("ignoring fluxReaction=false when reading reaction " + vcReaction.getName());
							} else {
								throw new RuntimeException("unexpected value " + fluxReactionAttrValue + " for " + XMLTags.SBML_VCELL_ReactionAttributesTag
										+ " attribute " + XMLTags.SBML_VCELL_ReactionAttributesTag_fluxReactionAttr);
							}
						}
					}
				}
				Structure reactionStructure = getReactionStructure(sbmlModel, sbmlReaction, vcBioModel, lambdaFunctions, sbmlSymbolMapping, vcLogger);
				if (bIsFluxReaction) {
					if (!(reactionStructure instanceof Membrane)){
						throw new SBMLImportException("Flux reaction on " + reactionStructure.getClass().getSimpleName() + ", not a membrane.");
					}
					vcReaction = new FluxReaction(vcModel, (Membrane) reactionStructure, null, sbmlReaction.getId(), bReversible);
					vcReaction.setModel(vcModel);
				} else {
					vcReaction = new SimpleReaction(vcModel, reactionStructure, sbmlReaction.getId(), bReversible);
				}
				
				if(rxnSbmlName != null && !rxnSbmlName.isEmpty()) {
					vcReaction.setSbmlName(rxnSbmlName);
				}
				// set annotations and notes on vcReactions[i]
				sbmlAnnotationUtil.readAnnotation(vcReaction, sbmlReaction);
				sbmlAnnotationUtil.readNotes(vcReaction, sbmlReaction);
				// record reaction name in annotation if it is greater than 64 characters. Choosing 64, since that is
				// (as of 12/2/08) the limit on the reactionName length.
				if (sbmlReaction.getId().length() > 64) {
					String freeTextAnnotation = metaData.getFreeTextAnnotation(vcReaction);
					if (freeTextAnnotation == null) {
						freeTextAnnotation = "";
					}
					metaData.setFreeTextAnnotation(vcReaction, freeTextAnnotation + "\n\n" + sbmlReaction.getId());
				}

				// Now add the reactants, products, modifiers as specified by
				// the sbmlReaction
				addReactionParticipants(sbmlModel, sbmlReaction, level, vcReaction, sbmlSymbolMapping, vcLogger, localIssueList, issueContext);

				/**
				 * process Kinetic Law
				 */
				KineticLaw kLaw = sbmlReaction.getKineticLaw();
				Kinetics kinetics;
				if (kLaw != null) {
					Expression sbmlKLawRateExpr = getExpressionFromFormula(kLaw.getMath(), lambdaFunctions, vcLogger);
					Expression vcellKLawExpr = adjustExpression(sbmlModel, sbmlKLawRateExpr, vcReaction.getNameScope(), sbmlSymbolMapping, SymbolContext.RUNTIME);
					Expression vcRateExpression = new Expression(vcellKLawExpr);

					// Check the kinetic rate equation for occurances of any species in the model that is not a reaction participant.
					// If there exists any such species, it should be added as a modifier (catalyst) to the reaction.
					for (SpeciesContext sc : vcSpeciesContexts) {
						if (vcRateExpression.hasSymbol(sc.getName())) {
							ReactionParticipant r = vcReaction.getReactant(sc.getName());
							ReactionParticipant p = vcReaction.getProduct(sc.getName());
							ReactionParticipant c = vcReaction.getCatalyst(sc.getName());
							if ((r == null) && (p == null) && (c == null)) {
								// This means that the speciesContext is not a reactant, product or modifier : it has to be
								// added to the VC Rxn as a catalyst
								vcReaction.addCatalyst(sc);
							}
						}
					}

					// set kinetics on VCell reaction - and determine assumed SBML rate units (may need to be converted)
					if (bSpatial) {
						// if spatial SBML ('isSpatial' attribute set), create DistributedKinetics)
						SpatialReactionPlugin ssrplugin = (SpatialReactionPlugin) sbmlReaction.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
						// (a) the requiredElements attributes should be 'spatial'
						if (ssrplugin != null && ssrplugin.isSetIsLocal() && ssrplugin.getIsLocal()) {
							kinetics = new GeneralKinetics(vcReaction);
						} else {
							kinetics = new GeneralLumpedKinetics(vcReaction);
						}
					} else {
						kinetics = new GeneralLumpedKinetics(vcReaction);
					}

					// set kinetics on vcReaction
					vcReaction.setKinetics(kinetics);

					// If the name of the rate parameter has been changed by
					// user, or matches with global/local param, it has to be changed.
					resolveRxnParameterNameConflicts(sbmlModel, sbmlReaction, kinetics);

					/**
					 * Now, based on the kinetic law expression, see if the rate
					 * is expressed in concentration/time or substance/time : If
					 * the compartment_id of the compartment corresponding to
					 * the structure in which the reaction takes place occurs in
					 * the rate law expression, it is in concentration/time;
					 * divide it by the compartment size and bring in the rate
					 * law as 'Distributed' kinetics. If not, the rate law is in
					 * substance/time; bring it in (as is) as 'Lumped' kinetics.
					 */

					ListOf<LocalParameter> localParameters = kLaw.getListOfLocalParameters();
					for (LocalParameter p: localParameters) {
						String paramName = p.getId();
						KineticsParameter kineticsParameter = kinetics.getKineticsParameter(paramName);
						if (kineticsParameter == null) {
							//add unresolved for now to prevent errors in kinetics.setParameterValue(kp,vcRateExpression) below 
							kinetics.addUnresolvedParameter(paramName); 
						}
					}
					
					KineticsParameter kp = kinetics.getAuthoritativeParameter();
					if (logger.isDebugEnabled()) {
						logger.debug("Setting " + kp.getName() + ":  " + vcRateExpression.infix());
					}
					kinetics.setParameterValue(kp, vcRateExpression);

					// If there are any global parameters used in the kinetics, and if they have species,
					// check if the species are already reactionParticipants in the reaction. If not, add them as catalysts.
					for (KineticsProxyParameter kpp : kinetics.getProxyParameters()) {
						if (kpp.getTarget() instanceof ModelParameter) {
							ModelParameter mp = (ModelParameter) kpp.getTarget();
							String[] symbols = mp.getExpression().getSymbols();
							if (symbols != null){
								for (String symbol : symbols) {
									SymbolTableEntry ste = mp.getExpression().getSymbolBinding(symbol);
									if (ste instanceof SpeciesContext){
										SpeciesContext sc = (SpeciesContext) ste;
										if (vcReaction.getReactant(sc.getName()) == null
												&& vcReaction.getProduct(sc.getName()) == null
												&& vcReaction.getCatalyst(sc.getName()) == null){
											vcReaction.addCatalyst(sc);
										}
									}
								}
							}
						}
					}

					// Introduce all remaining local parameters from the SBML model - local params cannot be defined by rules.
					for (LocalParameter param : localParameters ) {
						String paramName = param.getId();
						Expression exp = new Expression(param.getValue());
						String unitString = param.getUnits();
						VCUnitDefinition paramUnit = sbmlUnitIdentifierHash.get(unitString);
						if (paramUnit == null) {
							paramUnit = vcModelUnitSystem.getInstance_TBD();
						}
						// check if sbml local param is in kinetic params list;
						// if so, add its value.
						boolean lpSet = false;
						KineticsParameter kineticsParameter = kinetics.getKineticsParameter(paramName);
						if (kineticsParameter != null) {
							if (logger.isDebugEnabled()) {
								logger.debug("Setting local " + kineticsParameter.getName() + ":  " + exp.infix());
							}
							kineticsParameter.setExpression(exp);
							kineticsParameter.setUnitDefinition(paramUnit);
							lpSet = true;
						}
						else { 
							UnresolvedParameter ur = kinetics.getUnresolvedParameter(paramName);
							if (ur != null) {
								kinetics.addUserDefinedKineticsParameter(paramName,exp,paramUnit);
								lpSet = true;
							}
						}
						if (!lpSet) {
							// check if it is a proxy parameter (specifically,
							// speciesContext or model parameter (structureSize too)).
							KineticsProxyParameter kpp = kinetics.getProxyParameter(paramName);
							// if there is a proxy param with same name as sbml kinetic local param, if proxy param
							// is a model global parameter, change proxy param to local, set its value
							// and units to local param values
							if (kpp != null && kpp.getTarget() instanceof ModelParameter) {
								kinetics.convertParameterType(kpp, false);
								kineticsParameter = kinetics.getKineticsParameter(paramName);
								kinetics.setParameterValue(kineticsParameter,exp);
								kineticsParameter.setUnitDefinition(paramUnit);
							}
						}
					}
				} else {
					// sbmlKLaw was null, so creating a GeneralKinetics with 0.0 as rate.
					kinetics = new GeneralKinetics(vcReaction);
				} // end - if-else KLaw != null

				// set the reaction kinetics, and add reaction to the vcell model.
				kinetics.resolveUndefinedUnits();
				// logger.trace("ADDED SBML REACTION : \"" + rxnName + "\" to VCModel");
				vcReactionList.add(vcReaction);
				if ((sbmlReaction.isSetFast() && sbmlReaction.getFast()) || bIsFast) {
					fastReactionList.add(vcReaction);
				}
			} // end - for vcReactions

			ReactionStep[] array = vcReactionList.toArray(new ReactionStep[0]);
			vcModel.setReactionSteps( array); 
			
			final ReactionContext rc = vcBioModel.getSimulationContext(0).getReactionContext();
			for (ReactionStep frs: fastReactionList) {
					final ReactionSpec rs = rc.getReactionSpec(frs);
					rs.setReactionMapping(ReactionSpec.FAST);
			}

		} catch (ModelPropertyVetoException mpve) {
			throw new SBMLImportException(mpve.getMessage(), mpve);
		} catch (Exception e1) {
			throw new SBMLImportException(e1.getMessage(), e1);
		}
	}

	public static cbit.vcell.geometry.CSGNode getVCellCSGNode(org.sbml.jsbml.ext.spatial.CSGNode sbmlCSGNode) {
		String csgNodeName = sbmlCSGNode.getParent().getId();
		if (sbmlCSGNode instanceof org.sbml.jsbml.ext.spatial.CSGPrimitive){
			PrimitiveKind primitiveKind = ((org.sbml.jsbml.ext.spatial.CSGPrimitive) sbmlCSGNode).getPrimitiveType();
			cbit.vcell.geometry.CSGPrimitive.PrimitiveType vcellCSGPrimitiveType = getVCellPrimitiveType(primitiveKind);
			cbit.vcell.geometry.CSGPrimitive vcellPrimitive = new cbit.vcell.geometry.CSGPrimitive(csgNodeName, vcellCSGPrimitiveType);
			return vcellPrimitive;
		} else if (sbmlCSGNode instanceof CSGPseudoPrimitive){
			throw new RuntimeException("Pseudo primitives not yet supported in CSGeometry.");
		} else if (sbmlCSGNode instanceof CSGSetOperator) {
			org.sbml.jsbml.ext.spatial.CSGSetOperator sbmlSetOperator = (org.sbml.jsbml.ext.spatial.CSGSetOperator) sbmlCSGNode;
			final OperatorType opType;
			switch (sbmlSetOperator.getOperationType()){
			case difference:{
				opType = OperatorType.DIFFERENCE;
				break;
			}
			case intersection:{
				opType = OperatorType.INTERSECTION;
				break;
			}
			case union: {
				opType = OperatorType.UNION;
				break;
			}
			default:{
				throw new RuntimeException("sbml CSG geometry set operator "+sbmlSetOperator.getOperationType().name()+" not supported");
			}
			}
			cbit.vcell.geometry.CSGSetOperator vcellSetOperator = new cbit.vcell.geometry.CSGSetOperator(csgNodeName, opType);
			for (int c = 0; c < sbmlSetOperator.getListOfCSGNodes().size(); c++) {
				vcellSetOperator.addChild(getVCellCSGNode(sbmlSetOperator.getListOfCSGNodes().get(c)));
			}
			return vcellSetOperator;
		} else if (sbmlCSGNode instanceof CSGTransformation) {
			org.sbml.jsbml.ext.spatial.CSGTransformation sbmlTransformation = (org.sbml.jsbml.ext.spatial.CSGTransformation) sbmlCSGNode;
			cbit.vcell.geometry.CSGNode vcellCSGChild = getVCellCSGNode(sbmlTransformation.getCSGNode());
			if (sbmlTransformation instanceof org.sbml.jsbml.ext.spatial.CSGTranslation) {
				org.sbml.jsbml.ext.spatial.CSGTranslation sbmlTranslation = (org.sbml.jsbml.ext.spatial.CSGTranslation) sbmlTransformation;
				Vect3d translation = new Vect3d(
						sbmlTranslation.getTranslateX(),
						sbmlTranslation.getTranslateY(),
						sbmlTranslation.getTranslateZ());
				cbit.vcell.geometry.CSGTranslation vcellTranslation = new cbit.vcell.geometry.CSGTranslation(csgNodeName, translation);
				vcellTranslation.setChild(vcellCSGChild);
				return vcellTranslation;
			} else if (sbmlTransformation instanceof CSGRotation) {
				CSGRotation sbmlRotation = (CSGRotation) sbmlTransformation;
				Vect3d axis = new Vect3d(
						sbmlRotation.getRotateX(),
						sbmlRotation.getRotateY(), 
						sbmlRotation.getRotateZ());
				double rotationAngleRadians = sbmlRotation.getRotateAngleInRadians();
				cbit.vcell.geometry.CSGRotation vcellRotation = new cbit.vcell.geometry.CSGRotation(
						csgNodeName, axis, rotationAngleRadians);
				vcellRotation.setChild(vcellCSGChild);
				return vcellRotation;
			} else if (sbmlTransformation instanceof CSGScale) {
				CSGScale sbmlScale = (CSGScale) sbmlTransformation;
				Vect3d scale = new Vect3d(sbmlScale.getScaleX(),
						sbmlScale.getScaleY(), sbmlScale.getScaleZ());
				cbit.vcell.geometry.CSGScale vcellScale = new cbit.vcell.geometry.CSGScale(
						csgNodeName, scale);
				vcellScale.setChild(vcellCSGChild);
				return vcellScale;
			} else if (sbmlTransformation instanceof CSGHomogeneousTransformation) {
				throw new SBMLImportException("homogeneous transformations not supported yet.");
			} else {
				throw new SBMLImportException("unsupported type of CSGTransformation");
			}
		} else {
			throw new SBMLImportException("unsupported type of CSGNode");
		}
	}

	private static cbit.vcell.geometry.CSGPrimitive.PrimitiveType getVCellPrimitiveType(PrimitiveKind primitiveKind) {
		final cbit.vcell.geometry.CSGPrimitive.PrimitiveType vcellCSGPrimitiveType;
		switch (primitiveKind){
		case cone:{
			vcellCSGPrimitiveType = PrimitiveType.CONE;
			break;
		}
		case cube:{
			vcellCSGPrimitiveType = PrimitiveType.CUBE;
			break;
		}
		case cylinder:{
			vcellCSGPrimitiveType = PrimitiveType.CYLINDER;
			break;
		}
		case sphere:{
			vcellCSGPrimitiveType = PrimitiveType.SPHERE;
			break;
		}
//		case circle:
//		case rightTriangle:
//		case square:
		default:{
			throw new RuntimeException("Constructive Solid Geometry primative type "+primitiveKind.name()+" not supported");
		}
		}
		return vcellCSGPrimitiveType;
	}

	/**
	 * extract SBML geometry issue warning if not defined
	 * 
	 * @return null if not defined
	 * @throws IllegalArgumentException
	 *             if plugin or geometry not found
	 */
	private static org.sbml.jsbml.ext.spatial.Geometry getSbmlGeometry(org.sbml.jsbml.Model sbmlModel, Vector<Issue> localIssueList, IssueContext issueContext) {
		// Get a SpatialModelPlugin object plugged in the model object.
		//
		// The type of the returned value of SBase::getPlugin() function is
		// SBasePlugin*, and thus the value needs to be cast for the
		// corresponding derived class.
		//
		SpatialModelPlugin mplugin = (SpatialModelPlugin) sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		if (mplugin == null) {
			throw new IllegalArgumentException("SpatialModelPlugin not found");
		}

		// get a Geometry object via SpatialModelPlugin object.
		final org.sbml.jsbml.ext.spatial.Geometry geometry;
		try {
			geometry = mplugin.getGeometry();
		} catch (PropertyUndefinedError e){
			logger.info("model '"+sbmlModel.getId()+"' doesn't have a spatial geometry");
			localIssueList.add(new Issue(new SBMLIssueSource(sbmlModel),
					issueContext,
					IssueCategory.SBMLImport_UnsupportedAttributeOrElement,
					"Geometry not defined in spatial model.",
					Issue.Severity.WARNING));
			return null;
		}
		if (geometry == null) {
			throw new IllegalArgumentException("SBML Geometry not found");
		}

		if (geometry.getListOfGeometryDefinitions().size() < 1) {
			// (2/15/2013) For now, allow model to be imported even without
			// geometry defined. Issue a warning.
			logger.warn("model '"+sbmlModel.getId()+"' doesn't have a spatial geometry");
			localIssueList.add(new Issue(new SBMLIssueSource(sbmlModel),
					issueContext,
					IssueCategory.SBMLImport_UnsupportedAttributeOrElement,
					"Geometry not defined in spatial model.",
					Issue.Severity.WARNING));
			return null;
			// throw new
			// RuntimeException("SBML model does not have any geometryDefinition. Cannot proceed with import.");
		}
		return geometry;

	}

	private static void addGeometry(org.sbml.jsbml.Model sbmlModel, BioModel vcBioModel, LambdaFunction[] lambdaFunctions,
									  SBMLSymbolMapping sbmlSymbolMapping, Vector<Issue> localIssueList, IssueContext issueContext, VCLogger vcLogger) {
		// get a Geometry object via SpatialModelPlugin object.
		org.sbml.jsbml.ext.spatial.Geometry sbmlGeometry = getSbmlGeometry(sbmlModel, localIssueList, issueContext);
		if (sbmlGeometry == null) {
			return;
		}

		int dimension = 0;
		final Origin vcOrigin;
		final Extent vcExtent;
		{ // local code block
			// get a CoordComponent object via the Geometry object.
			ListOf<CoordinateComponent> listOfCoordComps = sbmlGeometry.getListOfCoordinateComponents();
			if (listOfCoordComps == null) {
				throw new RuntimeException("Cannot have 0 coordinate compartments in geometry");
			}
			// coord component
			double ox=0.0;
			double oy=0.0;
			double oz=0.0;
			double ex=1.0;
			double ey=1.0;
			double ez=1.0;
			for (CoordinateComponent coordComponent : listOfCoordComps) {
				double minValue = coordComponent.getBoundaryMinimum().getValue();
				double maxValue = coordComponent.getBoundaryMaximum().getValue();

				/**
				 * find sbml parameter associated with this coordComponent
				 * this is needed to map the parameter to the VCell x,y,z reserved symbols
				 */
				String spatialId = coordComponent.getSpatialId();
				Parameter coordComponentParameter = getSpatialParameter(sbmlModel, spatialId);
				final ReservedSymbol vcellCoordinateSymbol;
				switch (coordComponent.getType()){
				case cartesianX:{
					vcellCoordinateSymbol = vcBioModel.getModel().getX();
					ox = minValue;
					ex = maxValue - minValue;
					break;
				}
				case cartesianY:{
					vcellCoordinateSymbol = vcBioModel.getModel().getY();
					oy = minValue;
					ey = maxValue - minValue;
					break;
				}
				case cartesianZ:{
					vcellCoordinateSymbol = vcBioModel.getModel().getZ();
					oz = minValue;
					ez = maxValue - minValue;
					break;
				}
				default:{
					throw new RuntimeException("unsupported coordinateComponent "+coordComponent);
				}
				}
				if (coordComponentParameter != null){
					sbmlSymbolMapping.putRuntime(coordComponentParameter, vcellCoordinateSymbol);
				}
				dimension++;
			}

			vcOrigin = new Origin(ox, oy, oz);
			vcExtent = new Extent(ex, ey, ez);
		}

		// from geometry definition, find out which type of geometry : image or
		// analytic or CSG

		AnalyticGeometry analyticGeometryDefinition = null;
		CSGeometry csGeometry = null;
		SampledFieldGeometry segmentedSampledFieldGeometry = null;
		SampledFieldGeometry distanceMapSampledFieldGeometry = null;
		ParametricGeometry parametricGeometry = null;

		for (int i = 0; i < sbmlGeometry.getListOfGeometryDefinitions().size(); i++) {
			GeometryDefinition gd_temp = sbmlGeometry.getListOfGeometryDefinitions().get(i);
			if (!gd_temp.isSetIsActive()) {
				continue;
			}
			if (gd_temp instanceof AnalyticGeometry) {
				analyticGeometryDefinition = (AnalyticGeometry) gd_temp;
			} else if (gd_temp instanceof SampledFieldGeometry) {
				SampledFieldGeometry sfg = (SampledFieldGeometry) gd_temp;
				ListOf<SampledField> sampledFields = sbmlGeometry.getListOfSampledFields();
				if (sampledFields.size() > 1){
					throw new RuntimeException("only one sampled field supported");
				}
				InterpolationKind ik = sampledFields.get(0).getInterpolationType();
				switch (ik) {
				case linear:
					distanceMapSampledFieldGeometry = sfg;
					break;
				case nearestNeighbor:
					segmentedSampledFieldGeometry = sfg;
					break;
				default:
					logger.warn("Unsupported " + sampledFields.get(0).getName() + " interpolation type " + ik);
				}
			} else if (gd_temp instanceof CSGeometry) {
				csGeometry = (CSGeometry) gd_temp;
			} else if (gd_temp instanceof ParametricGeometry) {
				parametricGeometry = (ParametricGeometry) gd_temp;
			}else{
				throw new RuntimeException("unsupported geometry definition type "+gd_temp.getClass().getSimpleName());
			}
		}

		if (analyticGeometryDefinition == null
				&& segmentedSampledFieldGeometry == null
				&& distanceMapSampledFieldGeometry == null
				&& csGeometry == null) {
			throw new SBMLImportException(
					"VCell supports only Analytic, Image based (segmentd or distance map) or Constructed Solid Geometry at this time.");
		}
		final GeometryDefinition selectedGeometryDefinition;
		if (csGeometry != null) {
			selectedGeometryDefinition = csGeometry;
		} else if (analyticGeometryDefinition != null) {
			selectedGeometryDefinition = analyticGeometryDefinition;
		} else if (segmentedSampledFieldGeometry != null) {
			selectedGeometryDefinition = segmentedSampledFieldGeometry;
		} else if (distanceMapSampledFieldGeometry != null) {
			selectedGeometryDefinition = distanceMapSampledFieldGeometry;
		} else if (parametricGeometry != null) {
			selectedGeometryDefinition = parametricGeometry;
		} else {
			throw new SBMLImportException("no geometry definition found");
		}
		Geometry vcGeometry = null;
		if (selectedGeometryDefinition == analyticGeometryDefinition
				|| selectedGeometryDefinition == csGeometry) {
			vcGeometry = new Geometry("spatialGeom", dimension);
		} else if (selectedGeometryDefinition == distanceMapSampledFieldGeometry
				|| selectedGeometryDefinition == segmentedSampledFieldGeometry) {
			SampledFieldGeometry sfg = (SampledFieldGeometry) selectedGeometryDefinition;
			// get image from sampledFieldGeometry
			// get a sampledVol object via the listOfSampledVol (from
			// SampledGeometry) object.

			// gcw gcw gcw
			String sfn = sfg.getSampledField();
			SampledField sf = null;
			for (SampledField sampledField : sbmlGeometry.getListOfSampledFields()){
				if (sampledField.getSpatialId().equals(sfn)){
					sf = sampledField;
				}
			}
			int numX = sf.getNumSamples1();
			int numY = sf.getNumSamples2();
			int numZ = sf.getNumSamples3();
			int[] samples = new int[sf.getSamplesLength()];
			StringTokenizer tokens = new StringTokenizer(sf.getSamples()," ,;");
			int count = 0;
			while (tokens.hasMoreTokens()){
				int sample = Integer.parseInt(tokens.nextToken());
				samples[count++] = sample;
			}
			byte[] imageInBytes = new byte[samples.length];
			if (selectedGeometryDefinition == distanceMapSampledFieldGeometry) {
				//
				// single distance-map ... negative values are 1, zero and
				// positive are 2 (for now assume that there are only two
				// DomainTypes which are volume)
				// alternatively, one could use the marching cube algorithm to
				// create polygons which can be super sampled.
				//
				// could resample to higher resolution segmented images (via
				// linear interpolation).
				//
				for (int i = 0; i < imageInBytes.length; i++) {
					// if (interpolation(samples[i])<0){
					if (samples[i] < 0) {
						imageInBytes[i] = -1;
					} else {
						imageInBytes[i] = 1;
					}
				}
			} else {
				for (int i = 0; i < imageInBytes.length; i++) {
					imageInBytes[i] = (byte) samples[i];
				}
			}
			try {
				// logger.trace("ident " + sf.getId() + " " + sf.getName());
				VCImage vcImage = null;
				CompressionKind ck = sf.getCompression();
				DataKind dk = sf.getDataType();

				if (ck == CompressionKind.deflated) {
					vcImage = new VCImageCompressed(null, imageInBytes,
							vcExtent, numX, numY, numZ);
				} else {
					switch (dk) {
					case UINT8:
					case UINT16:
					case UINT32:
						vcImage = new VCImageUncompressed(null, imageInBytes,
								vcExtent, numX, numY, numZ);
					default:
					}
				}
				if (vcImage == null) {
					throw new SbmlException("Unsupported type combination "
							+ ck + ", " + dk + " for sampled field "
							+ sf.getName());
				}
				vcImage.setName(sf.getId());
				ListOf<SampledVolume> sampledVolumes = sfg.getListOfSampledVolumes();
				
				final int numSampledVols = sampledVolumes.size();
				if (numSampledVols == 0) {
					throw new RuntimeException("Cannot have 0 sampled volumes in sampledField (image_based) geometry");
				}
				//check to see if values are uniquely integer , add set up scaling if necessary
				double scaleFactor = checkPixelScaling(sampledVolumes, 1);
				if (scaleFactor != 1) {
					double checkScaleFactor = checkPixelScaling(sampledVolumes, scaleFactor);
					VCAssert.assertTrue(checkScaleFactor != scaleFactor, "Scale factor check failed");
				}
				VCPixelClass[] vcpixelClasses = new VCPixelClass[numSampledVols];
				// get pixel classes for geometry
				for (int i = 0; i < numSampledVols; i++) {
					SampledVolume sVol = sampledVolumes.get(i);
					// from subVolume, get pixelClass?
					final int scaled = (int) (scaleFactor * sVol.getSampledValue());
					vcpixelClasses[i] = new VCPixelClass(null, sVol.getDomainType(),scaled);
				}
				vcImage.setPixelClasses(vcpixelClasses);
				// now create image geometry
				vcGeometry = new Geometry("spatialGeom", vcImage);
			} catch (Exception e) {
				throw new RuntimeException(
						"Unable to create image from SampledFieldGeometry : "
								+ e.getMessage(), e);
			}
		}
		GeometrySpec vcGeometrySpec = vcGeometry.getGeometrySpec();
		vcGeometrySpec.setOrigin(vcOrigin);
		try {
			vcGeometrySpec.setExtent(vcExtent);
		} catch (PropertyVetoException e) {
			throw new SBMLImportException(
					"Unable to set extent on VC geometry : " + e.getMessage(),
					e);
		}

		// get listOfDomainTypes via the Geometry object.
		ListOf<DomainType> listOfDomainTypes = sbmlGeometry.getListOfDomainTypes();
		if (listOfDomainTypes == null || listOfDomainTypes.size() < 1) {
			throw new SBMLImportException(
					"Cannot have 0 domainTypes in geometry");
		}
		// get a listOfDomains via the Geometry object.
		ListOf<Domain> listOfDomains = sbmlGeometry.getListOfDomains();
		if (listOfDomains == null || listOfDomains.size() < 1) {
			throw new SBMLImportException("Cannot have 0 domains in geometry");
		}

		// ListOfGeometryDefinitions listOfGeomDefns =
		// sbmlGeometry.getListOfGeometryDefinitions();
		// if ((listOfGeomDefns == null) ||
		// (sbmlGeometry.getNumGeometryDefinitions() > 1)) {
		// throw new
		// RuntimeException("Can have only 1 geometry definition in geometry");
		// }
		// use the boolean bAnalytic to create the right kind of subvolume.
		// First match the somVol=domainTypes for spDim=3. Deal witl spDim=2
		// afterwards.
		GeometrySurfaceDescription vcGsd = vcGeometry.getGeometrySurfaceDescription();
		Vector<DomainType> surfaceClassDomainTypesVector = new Vector<>();
		try {
			for (DomainType dt : listOfDomainTypes) {
				if (dt.getSpatialDimensions() == vcGeometry.getDimension()) {
					// subvolume
					if (selectedGeometryDefinition == analyticGeometryDefinition) {
						// will set expression later - when reading in Analytic
						// Volumes in GeometryDefinition
						vcGeometrySpec.addSubVolume(new AnalyticSubVolume(dt.getId(), new Expression(1.0)));
					}
//					else {
//						// add SubVolumes later for CSG and Image-based
//					}
				} else if (dt.getSpatialDimensions() == vcGeometry.getDimension()-1) {
					surfaceClassDomainTypesVector.add(dt);
				}
			}

			// get an AnalyticGeometry object via the Geometry object. The
			// analytic vol is needed to get the expression for subVols
			if (selectedGeometryDefinition == analyticGeometryDefinition) {
				// get an analyticVol object via the listOfAnalyticVol (from
				// AnalyticGeometry) object.
				ListOf<AnalyticVolume> aVolumes = analyticGeometryDefinition.getListOfAnalyticVolumes();
				if (aVolumes.size() < 1) {
					throw new SBMLImportException("Cannot have 0 Analytic volumes in analytic geometry");
				}
				for (AnalyticVolume analyticVol : aVolumes) { 
					// get subVol from VC geometry using analyticVol spatialId;
					// set its expr using analyticVol's math.
					SubVolume vcSubvolume = vcGeometrySpec .getSubVolume(analyticVol.getDomainType());
					CastInfo<AnalyticSubVolume> ci = BeanUtils.attemptCast(AnalyticSubVolume.class, vcSubvolume);
					if (!ci.isGood()) {
						throw new RuntimeException("analytic volume '"
								+ analyticVol.getId()
								+ "' does not map to any VC subvolume.");
					}
					AnalyticSubVolume asv = ci.get();
					try {
						
						Expression subVolExpr = getExpressionFromFormula(analyticVol.getMath(), lambdaFunctions, vcLogger);
						asv.setExpression(subVolExpr);
					} catch (ExpressionException e) {
						throw new SBMLImportException(
								"Unable to set expression on subVolume '"
										+ asv.getName() + "'. "
										+ e.getMessage(), e);
					}
				}
				// specify default volume sampling (can be overridden by vcell-specific annotation)
				vcGsd.setVolumeSampleSize(vcGeometry.getGeometrySpec().getDefaultSampledImageSize());
				// read vcell-specific annotation for volume sampling
				readGeometrySamplingAnnotation(analyticGeometryDefinition, vcGsd);
			}

			SampledFieldGeometry sfg = BeanUtils.downcast(SampledFieldGeometry.class, selectedGeometryDefinition);
			if (sfg != null) {
				ListOf<SampledVolume> sampledVolumes = sfg.getListOfSampledVolumes();
				
				int numSampledVols = sampledVolumes.size(); 
				if (numSampledVols == 0) {
					throw new SBMLImportException("Cannot have 0 sampled volumes in sampledField (image_based) geometry");
				}
				VCPixelClass[] vcpixelClasses = new VCPixelClass[numSampledVols];
				ImageSubVolume[] vcImageSubVols = new ImageSubVolume[numSampledVols];
				// get pixel classes for geometry
				int idx = 0;
				for (SampledVolume sVol: sampledVolumes) {
					// from subVolume, get pixelClass?
					final String name =  sVol.getDomainType();
					final int pixelValue = SBMLUtils.ignoreZeroFraction( sVol.getSampledValue() );
					VCPixelClass pc = new VCPixelClass(null, name,  pixelValue); 
					vcpixelClasses[idx] = pc; 
					// Create the new Image SubVolume - use index of this for
					// loop as 'handle' for ImageSubVol?
					ImageSubVolume isv = new ImageSubVolume(null, pc, idx);
					isv.setName(name);
					vcImageSubVols[idx++] = isv;
				}
				vcGeometry.getGeometrySpec().setSubVolumes(vcImageSubVols);
			}
			if (selectedGeometryDefinition == csGeometry) {
				ListOf<org.sbml.jsbml.ext.spatial.CSGObject> listOfcsgObjs = csGeometry.getListOfCSGObjects();

				ArrayList<org.sbml.jsbml.ext.spatial.CSGObject> sbmlCSGs = new ArrayList<>(listOfcsgObjs);
				// we want the CSGObj with highest ordinal to be the first
				// element in the CSG subvols array.
				sbmlCSGs.sort((lhs, rhs) -> {
					// minus one to reverse sort
					return -1 * Integer.compare(lhs.getOrdinal(), rhs.getOrdinal());
				});

				int n = sbmlCSGs.size();
//				CSGObject[] vcCSGSubVolumes = new CSGObject[n];
				for (int i = 0; i < n; i++) {
					org.sbml.jsbml.ext.spatial.CSGObject sbmlCSGObject = sbmlCSGs.get(i);
					CSGObject vcellCSGObject = new CSGObject(null, sbmlCSGObject.getSpatialId(), i);
					vcellCSGObject.setRoot(getVCellCSGNode(sbmlCSGObject.getCSGNode()));
					boolean bFront = true;
					vcGeometry.getGeometrySpec().addSubVolume(vcellCSGObject, bFront);
				}

				// specify default volume sampling (can be overridden by vcell-specific annotation)
				vcGsd.setVolumeSampleSize(vcGeometry.getGeometrySpec().getDefaultSampledImageSize());
				// read vcell-specific annotation for volume sampling
				readGeometrySamplingAnnotation(csGeometry, vcGsd);
//				vcGeometry.getGeometrySpec().setSubVolumes(vcCSGSubVolumes);
			}

			// Call geom.geomSurfDesc.updateAll() to automatically generate
			// surface classes.
			// vcGsd.updateAll();
			vcGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(),true, true);
		} catch (Exception e) {
			throw new SBMLImportException(
					"Unable to create VC subVolumes from SBML domainTypes : "
							+ e.getMessage(), e);
		}

		// should now map each SBML domain to right VC geometric region.
		GeometricRegion[] vcGeomRegions = vcGsd.getGeometricRegions();
		ISize sampleSize = vcGsd.getVolumeSampleSize();
		RegionInfo[] regionInfos = vcGsd.getRegionImage().getRegionInfos();
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		double ox = vcOrigin.getX();
		double oy = vcOrigin.getY();
		double oz = vcOrigin.getZ();

		for (Domain domain : listOfDomains){
			String domainType = domain.getDomainType();
			InteriorPoint interiorPt = domain.getListOfInteriorPoints().get(0);
			if (interiorPt == null){
				DomainType currDomainType = null;
				for (DomainType dt : sbmlGeometry.getListOfDomainTypes()){
					if (dt.getSpatialId().equals(domainType)){
						currDomainType = dt;
					}
				}
				if (currDomainType.getSpatialDimensions() == vcGeometry.getDimension() - 1){
					logger.warn("ignoring missing interior point for domain "+domain.getId()+" with domainType "+currDomainType.getSpatialId());
					continue;
				}
			}
			Coordinate sbmlInteriorPtCoord = new Coordinate(
					interiorPt.getCoord1(),
					(vcGeometry.getDimension()>1)?interiorPt.getCoord2():0.0,
					(vcGeometry.getDimension()>2)?interiorPt.getCoord3():0.0);
			GeometricRegion vcGeometricRegionNearest = null;
			for (GeometricRegion vcGeomRegion : vcGeomRegions) {
				if (vcGeomRegion instanceof VolumeGeometricRegion) {
					int regionID = ((VolumeGeometricRegion) vcGeomRegion).getRegionID();
					for (RegionImage.RegionInfo regionInfo : regionInfos) {
						// get the regionInfo corresponding to the vcGeomRegion
						// (using gemoRegion regionID).
						if (regionInfo.getRegionIndex() == regionID) {
							int volIndx = 0;
							Coordinate nearestPtCoord = null;
							double minDistance = Double.MAX_VALUE;
							// for each point in the region, find it if is close
							// to 'sbmlInteriorPt'. If it is, this is the region
							// represented by SBML 'domain[i]'.
							for (int z = 0; z < numZ; z++) {
								for (int y = 0; y < numY; y++) {
									for (int x = 0; x < numX; x++) {
										if (regionInfo.isIndexInRegion(volIndx)) {
											double unit_z = (numZ > 1) ? ((double) z) / (numZ - 1) : 0.5;
											double coordZ = oz + vcExtent.getZ() * unit_z;
											double unit_y = (numY > 1) ? ((double) y) / (numY - 1) : 0.5;
											double coordY = oy + vcExtent.getY() * unit_y;
											double unit_x = (numX > 1) ? ((double) x) / (numX - 1) : 0.5;
											double coordX = ox + vcExtent.getX() * unit_x;
											// for now, find the shortest dist
											// coord. Can refine algo later.
											Coordinate vcCoord = new Coordinate(coordX, coordY, coordZ);
											double distance = sbmlInteriorPtCoord.distanceTo(vcCoord);
											if (distance < minDistance) {
												minDistance = distance;
												nearestPtCoord = vcCoord;
												vcGeometricRegionNearest = vcGeomRegion;
											}
										}
										volIndx++;
									} // end - for x
								} // end - for y
							} // end - for z
							// verify that domainType of domain and geomClass of geomRegion are the same; if so, name
							// vcGeomReg[j] with domain name
						} // end if (regInfoIndx = regId)
					} // end - for regInfo
				}
			} // end for - vcGeomRegions
			if (vcGeometricRegionNearest != null) {
				GeometryClass geomClassSBML = vcGeometry.getGeometryClass(domainType);
				// we know vcGeometryReg[j] is a VolGeomRegion
				GeometryClass geomClassVC = ((VolumeGeometricRegion) vcGeometricRegionNearest).getSubVolume();
				if (geomClassSBML.compareEqual(geomClassVC)) {
					vcGeometricRegionNearest.setName(domain.getId()); // TODO ... gets set every iteration ... pull outside of loop??
				}
			}

		} // end - for domains

		// now that we have the subVolumes:spDim3-domainTypes mapped, we need to
		// deal with surfaceClass:spDim2-domainTypes
		for (int i = 0; i < surfaceClassDomainTypesVector.size(); i++) {
			DomainType surfaceClassDomainType = surfaceClassDomainTypesVector
					.elementAt(i);
			// get the domain that has the same 'domainType' field as
			// 'surfaceClassDomainType'
			for (Domain d : listOfDomains){
				if (d.getDomainType().equals(surfaceClassDomainType.getId())) {
					// get the adjacent domains of this 'surface' domain
					// (surface domain + its 2 adj vol domains)
					Set<Domain> adjacentDomainsSet = getAssociatedAdjacentDomains(sbmlGeometry, d);
					// get the domain types of the adjacent domains in SBML and
					// store the corresponding subVol counterparts from VC for
					// adj vol domains
					Vector<SubVolume> adjacentSubVolumesVector = new Vector<>();
					Vector<VolumeGeometricRegion> adjVolGeomRegionsVector = new Vector<>();
					for (Domain dom : adjacentDomainsSet) {
						DomainType dt = getBySpatialID(sbmlGeometry.getListOfDomainTypes(),dom.getDomainType());
						if (dt.getSpatialDimensions() == vcGeometry.getDimension()) {
							// for domain type with sp. dim = geometry dimension, get
							// correspoinding subVol from VC geometry.
							GeometryClass gc = vcGeometry.getGeometryClass(dt.getId());
							adjacentSubVolumesVector.add((SubVolume) gc);
							// store volGeomRegions corresponding to this (vol)
							// geomClass in adjVolGeomRegionsVector : this
							// should return ONLY 1 region for subVol.
							GeometricRegion[] geomRegion = vcGsd.getGeometricRegions(gc);
							adjVolGeomRegionsVector.add((VolumeGeometricRegion) geomRegion[0]);
						}
					}
					// there should be only 2 subVols in this vector
					if (adjacentSubVolumesVector.size() != 2) {
						throw new RuntimeException(
								"Cannot have more or less than 2 subvolumes that are adjacent to surface (membrane) '"
										+ d.getId() + "'");
					}
					// get the surface class with these 2 adj subVols. Set its
					// name to that of 'surfaceClassDomainType'
					SurfaceClass surfacClass = vcGsd.getSurfaceClass(
							adjacentSubVolumesVector.get(0),
							adjacentSubVolumesVector.get(1));
					surfacClass.setName(surfaceClassDomainType.getSpatialId());
					// get surfaceGeometricRegion that has adjVolGeomRegions as
					// its adjacent vol geom regions and set its name from
					// domain 'd'
					SurfaceGeometricRegion surfaceGeomRegion = getAssociatedSurfaceGeometricRegion(vcGsd, adjVolGeomRegionsVector);
					if (surfaceGeomRegion != null) {
						surfaceGeomRegion.setName(d.getId());
					}
				} // end if - domain.domainType == surfaceClassDomainType
			} // end for - numDomains
		} // end surfaceClassDomainTypesVector

		// structureMappings in VC from compartmentMappings in SBML
		try {
			// set geometry first and then set structureMappings?
			vcBioModel.getSimulationContext(0).setGeometry(vcGeometry);
			// update simContextName ...
			vcBioModel.getSimulationContext(0).setName(vcBioModel.getSimulationContext(0).getName() + "_"
					+ vcGeometry.getName());
			Model vcModel = vcBioModel.getSimulationContext(0).getModel();
			ModelUnitSystem vcModelUnitSystem = vcModel.getUnitSystem();

			Vector<StructureMapping> structMappingsVector = new Vector<>();
			SpatialCompartmentPlugin cplugin;
			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				Compartment c = sbmlModel.getCompartment(i);
				String cname = c.getName();
				cplugin = (SpatialCompartmentPlugin) c.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
				CompartmentMapping compMapping = (cplugin.isSetCompartmentMapping()) ? cplugin.getCompartmentMapping() : null;
				if (compMapping != null) {
					CastInfo<Structure> ci = SBMLHelper.getTypedStructure(Structure.class, vcModel, cname);
					if (ci.isGood()) {
						Structure struct = ci.get(); 
						String domainType = compMapping.getDomainType();
						GeometryClass geometryClass = vcGeometry.getGeometryClass(domainType);
						double unitSize = compMapping.getUnitSize();
						Feature feat = BeanUtils.downcast(Feature.class,struct);
						if (feat != null) {
							FeatureMapping featureMapping = new FeatureMapping(feat, vcBioModel.getSimulationContext(0), vcModelUnitSystem);
							featureMapping.setGeometryClass(geometryClass);
							if (geometryClass instanceof SubVolume) {
								featureMapping.getVolumePerUnitVolumeParameter().setExpression(new Expression(unitSize));
								sbmlSymbolMapping.putInitial(compMapping, featureMapping.getVolumePerUnitVolumeParameter());
							} else if (geometryClass instanceof SurfaceClass) {
								featureMapping.getVolumePerUnitAreaParameter().setExpression(new Expression(unitSize));
								sbmlSymbolMapping.putInitial(compMapping, featureMapping.getVolumePerUnitAreaParameter());
							}
							structMappingsVector.add(featureMapping);
						} else if (struct instanceof Membrane) {
							MembraneMapping membraneMapping = new MembraneMapping( (Membrane) struct, vcBioModel.getSimulationContext(0), vcModelUnitSystem); membraneMapping.setGeometryClass(geometryClass);
							if (geometryClass instanceof SubVolume) {
								membraneMapping.getAreaPerUnitVolumeParameter().setExpression(new Expression(unitSize));
								sbmlSymbolMapping.putInitial(compMapping, membraneMapping.getAreaPerUnitVolumeParameter());
							} else if (geometryClass instanceof SurfaceClass) {
								membraneMapping.getAreaPerUnitAreaParameter().setExpression(new Expression(unitSize));
								sbmlSymbolMapping.putInitial(compMapping, membraneMapping.getAreaPerUnitAreaParameter());
							}
							structMappingsVector.add(membraneMapping);
						}
					}
				}
			}
			StructureMapping[] structMappings = structMappingsVector
					.toArray(new StructureMapping[0]);
			vcBioModel.getSimulationContext(0).getGeometryContext()
					.setStructureMappings(structMappings);

			// if type from SBML parameter Boundary Condn is not the same as the boundary type of the
			// structureMapping of structure of paramSpContext, set the boundary condn type of the structureMapping
			// to the value of 'type' from SBML parameter Boundary Condn.

			for (Parameter sbmlGlobalParam : sbmlModel.getListOfParameters()){
				SpatialParameterPlugin spplugin = (SpatialParameterPlugin) sbmlGlobalParam.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
				ParameterType paramType = spplugin.getParamType();
				if (!(paramType instanceof BoundaryCondition)){
					continue;
				}
				BoundaryCondition bCondn = (BoundaryCondition)paramType;

				if (bCondn.isSetVariable()) {
					// get the var of boundaryCondn; find appropriate spContext in vcell;
					SBase boundaryConditionTargetSbase = findSBase(sbmlModel, bCondn.getVariable());
					EditableSymbolTableEntry boundaryConditionTargetSTE = sbmlSymbolMapping.getSte(boundaryConditionTargetSbase, SymbolContext.RUNTIME);
					if (!(boundaryConditionTargetSTE instanceof SpeciesContext)){
						throw new SBMLImportException("expecting boundary condition '"+bCondn+"' target variable to map to a VCell SpeciesContext");
					}
					SpeciesContext paramSpContext = (SpeciesContext) boundaryConditionTargetSTE;
					Structure s = paramSpContext.getStructure();
					StructureMapping sm = vcBioModel.getSimulationContext(0).getGeometryContext().getStructureMapping(s);
					if (sm != null) {
						BoundaryConditionType bct;
						switch (bCondn.getType()){
						case Dirichlet:{
							bct = BoundaryConditionType.DIRICHLET;
							break;
						}
						case Neumann:{
							bct = BoundaryConditionType.NEUMANN;
							break;
						}
						case Robin_inwardNormalGradientCoefficient:
						case Robin_sum:
						case Robin_valueCoefficient:
						default:
							throw new RuntimeException("boundary condition type "+bCondn.getType().name()+" not supported");
						}
						ReactionContext reactionContext = vcBioModel.getSimulationContext(0).getReactionContext();
						for (CoordinateComponent coordComp : sbmlGeometry.getListOfCoordinateComponents()){
							SpeciesContextSpec.SpeciesContextSpecParameter vcellBCondParam = null;
							if (bCondn.getCoordinateBoundary().equals(coordComp.getBoundaryMinimum().getSpatialId())){
								switch (coordComp.getType()){
								case cartesianX:{
									sm.setBoundaryConditionTypeXm(bct);
									vcellBCondParam = reactionContext.getSpeciesContextSpec(paramSpContext).getBoundaryXmParameter();
									break;
								}
								case cartesianY:{
									sm.setBoundaryConditionTypeYm(bct);
									vcellBCondParam = reactionContext.getSpeciesContextSpec(paramSpContext).getBoundaryYmParameter();
									break;
								}
								case cartesianZ:{
									sm.setBoundaryConditionTypeZm(bct);
									vcellBCondParam = reactionContext.getSpeciesContextSpec(paramSpContext).getBoundaryZmParameter();
									break;
								}
								}
							}
							if (bCondn.getCoordinateBoundary().equals(coordComp.getBoundaryMaximum().getSpatialId())){
								switch (coordComp.getType()){
								case cartesianX:{
									sm.setBoundaryConditionTypeXp(bct);
									vcellBCondParam = reactionContext.getSpeciesContextSpec(paramSpContext).getBoundaryXpParameter();
									break;
								}
								case cartesianY:{
									sm.setBoundaryConditionTypeYp(bct);
									vcellBCondParam = reactionContext.getSpeciesContextSpec(paramSpContext).getBoundaryYpParameter();
									break;
								}
								case cartesianZ:{
									sm.setBoundaryConditionTypeZp(bct);
									vcellBCondParam = reactionContext.getSpeciesContextSpec(paramSpContext).getBoundaryZpParameter();
									break;
								}
								}
							}
							if (vcellBCondParam != null){
								if (bCondn.getParent() instanceof Parameter) {
									Parameter sbmlBCondParam = (Parameter) bCondn.getParent();
									sbmlSymbolMapping.putRuntime(sbmlBCondParam, vcellBCondParam);
									if (sbmlBCondParam.isSetValue() && sbmlBCondParam.getValue() != 0.0) {
										vcellBCondParam.setExpression(new Expression(sbmlBCondParam.getValue()));
									}
								} else {
									logger.error("unexpected boundary condition parent type "+bCondn.getParent());
								}
							}
						}
					} // sm != null
					else {
						vcLogger.sendMessage(
								VCLogger.Priority.MediumPriority,
								VCLogger.ErrorType.OverallWarning,
								"No structure " + s.getName()
										+ " requested by species context "
										+ paramSpContext.getName());
					}
				} // end if (bCondn.isSetVar())
			} // end for (sbmlModel.numParams)

			vcBioModel.getSimulationContext(0).getGeometryContext().refreshStructureMappings();
			vcBioModel.getSimulationContext(0).refreshSpatialObjects();
		} catch (Exception e) {
			throw new SBMLImportException(
					"Unable to create VC structureMappings from SBML compartment mappings : "
							+ e.getMessage(), e);
		}
	}

	private static void readGeometrySamplingAnnotation(GeometryDefinition sbmlGeometryDefinition, GeometrySurfaceDescription vcGsd) throws PropertyVetoException {
		Annotation geometryDefinitionAnnotation = sbmlGeometryDefinition.getAnnotation();
		if (geometryDefinitionAnnotation != null && geometryDefinitionAnnotation.getNonRDFannotation() != null) {
			XMLNode geometrySamplingElement = geometryDefinitionAnnotation.getNonRDFannotation().getChildElement(XMLTags.SBML_VCELL_GeometrySamplingTag, "*");
			if (geometrySamplingElement != null) {
				int numX = 1;
				int numY = 1;
				int numZ = 1;
				double cutoffFrequency = 0.3;
				int attrIndexX = geometrySamplingElement.getAttrIndex(XMLTags.SBML_VCELL_GeometrySamplingTag_numXAttr, SBMLUtils.SBML_VCELL_NS);
				if (attrIndexX >= 0) {
					numX = Integer.parseInt(geometrySamplingElement.getAttrValue(attrIndexX));
				}
				int attrIndexY = geometrySamplingElement.getAttrIndex(XMLTags.SBML_VCELL_GeometrySamplingTag_numYAttr, SBMLUtils.SBML_VCELL_NS);
				if (attrIndexY >= 0) {
					numY = Integer.parseInt(geometrySamplingElement.getAttrValue(attrIndexY));
				}
				int attrIndexZ = geometrySamplingElement.getAttrIndex(XMLTags.SBML_VCELL_GeometrySamplingTag_numZAttr, SBMLUtils.SBML_VCELL_NS);
				if (attrIndexZ >= 0) {
					numZ = Integer.parseInt(geometrySamplingElement.getAttrValue(attrIndexZ));
				}
				int attrIndexC = geometrySamplingElement.getAttrIndex(XMLTags.SBML_VCELL_GeometrySamplingTag_cutoffFrequencyAttr, SBMLUtils.SBML_VCELL_NS);
				if (attrIndexC >= 0) {
					cutoffFrequency = Double.parseDouble(geometrySamplingElement.getAttrValue(attrIndexC));
				}
				vcGsd.setVolumeSampleSize(new ISize(numX,numY,numZ));
				vcGsd.setFilterCutoffFrequency(cutoffFrequency);
			}
		}
	}

	private static Parameter getSpatialParameter(org.sbml.jsbml.Model sbmlModel, String spatialId) {
		Parameter spatialParameter = null;
		for (Parameter p : sbmlModel.getListOfParameters()){
			SpatialParameterPlugin mplugin = (SpatialParameterPlugin) p.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
			if (mplugin != null) {
				String spIdRef = mplugin.getParamType().getSpatialRef();
				if (spatialId.equals(spIdRef)){
					spatialParameter = p;
					break;
				}
			}
		}
		return spatialParameter;
	}

	/**
	 * check / find scaling factor for pixel values  in sampledVolumes (double) to map onto integers without duplicates
	 * @param sampledVolumes not null
	 * @param scaleFactor not null
	 * @return scaleFactor if it works, different scale factor if possible
	 * @throws RuntimeException if no easily calculatable scale factor present
	 */
	private static double checkPixelScaling(Collection<SampledVolume >sampledVolumes, double scaleFactor) {
		//check to see if values are uniquely integer , add set up scaling if necessary
		double maxPix = Double.MIN_VALUE;
		double minPix = Double.MAX_VALUE;
		boolean unique = true;
		Set<Integer> uniqSet = new HashSet<>();
		for (SampledVolume sv : sampledVolumes) {
			final double px  = sv.getSampledValue() * scaleFactor; 
			maxPix = Math.max(maxPix, px);
			minPix = Math.min(minPix, px);
			int ipx  = (int) px;
			unique &= uniqSet.add(ipx);
		}	
		if (unique) {
			return scaleFactor;
		}
		final double upperScale = maxPix > 0 ? Integer.MAX_VALUE / maxPix :  Double.MAX_VALUE;
		final double lowerScale = minPix < 0 ? Integer.MIN_VALUE / maxPix : Double.MAX_VALUE; 
		final double rScale = Math.min(upperScale, lowerScale);
		if (rScale == scaleFactor) {
			throw new RuntimeException("unable to set up double to integer scale for pixel values ranging from "
				+ minPix + " to " + maxPix);
		}
		return rScale;
	}

	private static SurfaceGeometricRegion getAssociatedSurfaceGeometricRegion(
			GeometrySurfaceDescription vcGsd,
			Vector<VolumeGeometricRegion> volGeomRegionsVector) {
		GeometricRegion[] geomeRegions = vcGsd.getGeometricRegions();
		// adjVolGeomRegionsVector should have only 2 elements - the 2 adj
		// volGeomRegions for any surfaceRegion.
		VolumeGeometricRegion[] volGeomRegionsArray = volGeomRegionsVector.toArray(new VolumeGeometricRegion[0]);
		for (GeometricRegion vcGeometricRegion : geomeRegions) {
			if (vcGeometricRegion instanceof SurfaceGeometricRegion) {
				SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion) vcGeometricRegion;
				GeometricRegion[] adjVolGeomRegs = surfaceRegion
						.getAdjacentGeometricRegions();
				// adjVolGeomRegs array should also have 2 elements : the 2 adj
				// volGeomRegions for surfaceRegion.
				// if the 2 arrays do not have 2 elements each, throw exception
				if (volGeomRegionsArray.length != 2
						&& adjVolGeomRegs.length != 2) {
					throw new SBMLImportException(
							"There should be 2 adjacent geometric regions for surfaceRegion '"
									+ surfaceRegion.getName() + "'");
				}
				// if the vol geomtric regions in both arrays match, we have a
				// winner! - return surfaceRegion
				if ((adjVolGeomRegs[0].compareEqual(volGeomRegionsArray[0]) && adjVolGeomRegs[1]
						.compareEqual(volGeomRegionsArray[1]))
						|| (adjVolGeomRegs[0]
								.compareEqual(volGeomRegionsArray[1]) && adjVolGeomRegs[1]
								.compareEqual(volGeomRegionsArray[0]))) {
					return surfaceRegion;
				}
			}
		}
		return null;
	}

	// collect the domains that are adjacent to each other. Typically, 'd' is a
	// 'surface' domain. We scan through adjacent domains to get the
	// the surface domain and its adjacent volume domains. The set returned
	// should have 3 domains.
	private static Set<Domain> getAssociatedAdjacentDomains(
			org.sbml.jsbml.ext.spatial.Geometry sbmlGeom, Domain d) {
		Set<Domain> adjacentDomainsSet = new HashSet<>();
		for (AdjacentDomains adjDomains : sbmlGeom.getListOfAdjacentDomains()){
			Domain domain1 = getBySpatialID(sbmlGeom.getListOfDomains(),adjDomains.getDomain1());
			Domain domain2 = getBySpatialID(sbmlGeom.getListOfDomains(),adjDomains.getDomain2());
			if (adjDomains.getDomain1().equals(d.getId())){
				adjacentDomainsSet.add(domain2);
			}
			if (adjDomains.getDomain2().equals(d.getId())) {
				if (domain1 != null) {
					adjacentDomainsSet.add(domain1);
				}
			}
		}
		return adjacentDomainsSet;
	}
	
	private static <T extends SpatialNamedSBase> T getBySpatialID(ListOf<T> list, String spatialId) {
		for (T t : list){
			if (t.getSpatialId().equals(spatialId)){
				return t;
			}
		}
		return null;
	}

}
