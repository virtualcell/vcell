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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.AlgebraicRule;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.RateRule;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.ext.spatial.AdjacentDomains;
import org.sbml.jsbml.ext.spatial.AdvectionCoefficient;
import org.sbml.jsbml.ext.spatial.AnalyticGeometry;
import org.sbml.jsbml.ext.spatial.AnalyticVolume;
import org.sbml.jsbml.ext.spatial.BoundaryCondition;
import org.sbml.jsbml.ext.spatial.CSGHomogeneousTransformation;
import org.sbml.jsbml.ext.spatial.CSGPseudoPrimitive;
import org.sbml.jsbml.ext.spatial.CSGRotation;
import org.sbml.jsbml.ext.spatial.CSGScale;
import org.sbml.jsbml.ext.spatial.CSGSetOperator;
import org.sbml.jsbml.ext.spatial.CSGTransformation;
import org.sbml.jsbml.ext.spatial.CSGeometry;
import org.sbml.jsbml.ext.spatial.CompartmentMapping;
import org.sbml.jsbml.ext.spatial.CompressionKind;
import org.sbml.jsbml.ext.spatial.CoordinateComponent;
import org.sbml.jsbml.ext.spatial.CoordinateKind;
import org.sbml.jsbml.ext.spatial.DataKind;
import org.sbml.jsbml.ext.spatial.DiffusionCoefficient;
import org.sbml.jsbml.ext.spatial.Domain;
import org.sbml.jsbml.ext.spatial.DomainType;
import org.sbml.jsbml.ext.spatial.GeometryDefinition;
import org.sbml.jsbml.ext.spatial.InteriorPoint;
import org.sbml.jsbml.ext.spatial.InterpolationKind;
import org.sbml.jsbml.ext.spatial.ParameterType;
import org.sbml.jsbml.ext.spatial.ParametricGeometry;
import org.sbml.jsbml.ext.spatial.PrimitiveKind;
import org.sbml.jsbml.ext.spatial.SampledField;
import org.sbml.jsbml.ext.spatial.SampledFieldGeometry;
import org.sbml.jsbml.ext.spatial.SampledVolume;
import org.sbml.jsbml.ext.spatial.SpatialCompartmentPlugin;
import org.sbml.jsbml.ext.spatial.SpatialModelPlugin;
import org.sbml.jsbml.ext.spatial.SpatialNamedSBase;
import org.sbml.jsbml.ext.spatial.SpatialParameterPlugin;
import org.sbml.jsbml.ext.spatial.SpatialReactionPlugin;
import org.sbml.jsbml.ext.spatial.SpatialSymbolReference;
import org.vcell.sbml.SBMLHelper;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.vcell.SBMLImportException.Category;
import org.vcell.util.BeanUtils;
import org.vcell.util.BeanUtils.CastInfo;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;
import org.vcell.util.Origin;
import org.vcell.util.TokenMangler;
import org.vcell.util.VCAssert;
import org.vcell.util.document.BioModelChildSummary;

import cbit.image.VCImage;
import cbit.image.VCImageCompressed;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGPrimitive.PrimitiveType;
import cbit.vcell.geometry.CSGSetOperator.OperatorType;
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
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
//import cbit.vcell.mapping.BioEvent.Delay;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.BioEvent.TriggerType;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.GeneralLumpedKinetics;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.ModelPropertyVetoException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.StructureSorter;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.parser.LambdaFunction;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.render.Vect3d;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;
import cbit.vcell.xml.XMLTags;

public class SBMLImporter {
	public static class SBMLIssueSource implements Issue.IssueSource {
		private final SBase issueSource;

		public SBMLIssueSource(SBase issueSource) {
			this.issueSource = issueSource;
		}

		public SBase getSBase() {
			return issueSource;
		}
	}

	/**
	 * keywords for VCLogger error message; used to detect specific error
	 */
	public static final String RESERVED_SPATIAL = "reserved spatial";

	private long level = 2;
	// private long version = 3;

	/**
	 * detect unsupported "delay" element
	 */
	private static final String DELAY_URL = "www.sbml.org/sbml/symbols/delay";

	private String sbmlFileName = null;
	private org.sbml.jsbml.Model sbmlModel = null;
//	private SimulationContext vcBioModel.getSimulationContext(0) = null;
	private LambdaFunction[] lambdaFunctions = null;
	private BioModel vcBioModel = null;
	private HashMap<String, Expression> assignmentRulesHash = new HashMap<String, Expression>();
	private HashMap<String, Expression> rateRulesHash = new HashMap<String, Expression>();
	private HashMap<String, VCUnitDefinition> sbmlUnitIdentifierHash = new HashMap<String, VCUnitDefinition>();
	// private Hashtable<String, SBVCConcentrationUnits> speciesUnitsHash = new
	// Hashtable<String, SBVCConcentrationUnits>();

	// issue list for medium-level warnings while importing
	private Vector<Issue> localIssueList = new Vector<Issue>();
	private final IssueContext issueContext = new IssueContext();
	// is model spatial?
	private boolean bSpatial = false;

	private VCLogger logger = null;
	// for VCell specific annotation
	private static String RATE_NAME = XMLTags.ReactionRateTag;
	private static String SPECIES_NAME = XMLTags.SpeciesTag;
	private static String REACTION = XMLTags.ReactionTag;
	private static String OUTSIDE_COMP_NAME = XMLTags.OutsideCompartmentTag;

	// For SBML geometry (definition) type
	public static int GEOM_OTHER = 0;
	public static int GEOM_ANALYTIC = 1;
	public static int GEOM_IMAGEBASED = 2;
	public static int GEOM_CSG = 3;

	// SBMLAnnotationUtil to get the SBML-related annotations, notes, free-text
	// annotations from a Biomodel VCMetaData
	private SBMLAnnotationUtil sbmlAnnotationUtil = null;

	/*
	 * A lightweight nested class to contain the SBML and VC concentration units
	 * for species. Needed when running the Semantics test suite. When SBML
	 * model is imported into VCell and a simulation is run, the units of the
	 * generated results are different from units of results in SBML (usually a
	 * factor of 1e-6 for species concentration). In order to compare the two
	 * results, we need to know the units in SBML (this is after importing to
	 * VCEll and running simulations, at which point we only know the VCell
	 * units). Hence using this lookup that is stored in a hashTable, which is
	 * retrieved later to make the appropriate unit conversions before comparing
	 * the 2 results.
	 */
	public static class SBVCConcentrationUnits {
		private VCUnitDefinition SBunits = null;
		private VCUnitDefinition VCunits = null;

		public SBVCConcentrationUnits(VCUnitDefinition argSBunits,
				VCUnitDefinition argVCunits) {
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

	private static Logger lg = LogManager.getLogger(SBMLImporter.class);

	public SBMLImporter(org.sbml.jsbml.Model sbmlModel, VCLogger argVCLogger, boolean isSpatial) {
		this((String) null, argVCLogger, isSpatial);
		if (sbmlModel == null) throw new NullPointerException("Model must not be null");
		this.sbmlModel = sbmlModel;
	}
	public SBMLImporter(String argSbmlFileName, VCLogger argVCLogger,
			boolean isSpatial) {
		super();
		this.sbmlFileName = argSbmlFileName;
		this.logger = argVCLogger;
		this.bSpatial = isSpatial;
	}
	
	protected void addCompartments(VCMetaData metaData) {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf listofCompartments = sbmlModel.getListOfCompartments();
		if (listofCompartments == null) {
			throw new SBMLImportException("Cannot have 0 compartments in model");
		}
		// Using a vector here - since there can be SBML models with only
		// features and no membranes.
		// Hence keeping the datastructure flexible.
		List<Structure> structList = new ArrayList<Structure>();
		java.util.HashMap<String, Structure> structureNameMap = new java.util.HashMap<String, Structure>();

		try {
			int structIndx = 0;
			// First pass - create the structures
			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				org.sbml.jsbml.Compartment compartment = (org.sbml.jsbml.Compartment) listofCompartments
						.get(i);
				String compartmentName = compartment.getId();
				if (!compartment.isSetSpatialDimensions() || compartment.getSpatialDimensions() == 3) {
					Feature feature = new Feature(compartmentName);
					structList.add(structIndx, feature);
					structureNameMap.put(compartmentName, feature);
				} else if (compartment.getSpatialDimensions() == 2) { // spatial dimensions is set (see clause above)
					Membrane membrane = new Membrane(compartmentName);
					structList.add(structIndx, membrane);
					structureNameMap.put(compartmentName, membrane);
				} else {
					logger.sendMessage(VCLogger.Priority.HighPriority,
							VCLogger.ErrorType.CompartmentError,
							"Cannot deal with spatial dimension : "
									+ compartment.getSpatialDimensions()
									+ " for compartments at this time.");
					throw new SBMLImportException(
							"Cannot deal with spatial dimension : "
									+ compartment.getSpatialDimensions()
									+ " for compartments at this time");
				}
				structIndx++;
				sbmlAnnotationUtil.readAnnotation(structList.get(i), compartment);
				sbmlAnnotationUtil.readNotes(structList.get(i), compartment);
			}

			// Second pass - connect the structures
			Model model = vcBioModel.getSimulationContext(0).getModel();
			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				org.sbml.jsbml.Compartment sbmlCompartment = (org.sbml.jsbml.Compartment) listofCompartments.get(i);
				String outsideCompartmentId = null;
				if (sbmlCompartment.getOutside() != null && sbmlCompartment.getOutside().length() > 0) {
					// compartment.getOutside returns the Sid of the 'outside'
					// compartment, so get the compartment from model.
					outsideCompartmentId = sbmlCompartment.getOutside();
				} else {
					Element sbmlImportRelatedElement = sbmlAnnotationUtil.readVCellSpecificAnnotation(sbmlCompartment);
					if (sbmlImportRelatedElement != null) {
						Element embeddedVCellElement = sbmlImportRelatedElement
								.getChild(OUTSIDE_COMP_NAME, Namespace.getNamespace(SBMLUtils.SBML_VCELL_NS));
						if (embeddedVCellElement != null) {
							outsideCompartmentId = embeddedVCellElement.getAttributeValue(XMLTags.NameTag);
						}
					}
				}
				if (outsideCompartmentId != null) {
					Compartment outsideCompartment = sbmlModel.getCompartment(outsideCompartmentId);
					Structure outsideStructure = (Structure) structureNameMap.get(outsideCompartment.getId());
					Structure struct = (Structure) structureNameMap.get(sbmlCompartment.getId());
					struct.setSbmlParentStructure(outsideStructure);
				}
			}

			// set the structures in vc vcBioModel.getSimulationContext(0)
			Structure[] structures = structList.toArray(new Structure[structList.size()]);
			model.setStructures(structures);

			// Third pass thro' the list of compartments : set the sizes on the
			// structureMappings - can be done only after setting
			// the structures on vcBioModel.getSimulationContext(0).
			boolean allSizesSet = true;
			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				org.sbml.jsbml.Compartment compartment = (org.sbml.jsbml.Compartment) listofCompartments.get(i);
				String compartmentName = compartment.getId();

				if (!compartment.isSetSize()) {
					// logger.sendMessage(VCLogger.Priority.MediumPriority,
					// TranslationMessage.COMPARTMENT_ERROR,
					// "compartment "+compartmentName+" size is not set in SBML document.");
					allSizesSet = false;
				} else {
					double size = compartment.getSize();
					// Check if size is specified by a rule
					Expression sizeExpr = getValueFromAssignmentRule(compartmentName);
					if (sizeExpr != null && !sizeExpr.isNumeric()) {
						// We are NOT handling compartment sizes with assignment
						// rules/initial Assignments that are NON-numeric at
						// this time ...
						logger.sendMessage(
								VCLogger.Priority.HighPriority,
								VCLogger.ErrorType.CompartmentError,
								"compartment "
										+ compartmentName
										+ " size has an assignment rule which is not a numeric value, cannot handle it at this time.");
					}
					// check if sizeExpr is null - no assignment rule for size -
					// check if it is specified by initial assignment
					if (sizeExpr == null) {
						InitialAssignment compInitAssgnment = sbmlModel
								.getInitialAssignment(compartmentName);
						if (compInitAssgnment != null) {
							sizeExpr = getExpressionFromFormula(compInitAssgnment.getMath());
						}
					}
					if (sizeExpr != null && !sizeExpr.isNumeric()) {
						// We are NOT handling compartment sizes with assignment
						// rules/initial Assignments that are NON-numeric at
						// this time ...
						logger.sendMessage(
								VCLogger.Priority.HighPriority,
								VCLogger.ErrorType.CompartmentError,
								"compartment "
										+ compartmentName
										+ " size has an initial assignment which is not a numeric value, cannot handle it at this time.");
					}

					// no init assignment or assignment rule; create expression
					// from 'size' attribute,
					if (sizeExpr == null) {
						sizeExpr = new Expression(size);
					}

					// Now set the size of the compartment.
					Structure struct = model.getStructure(compartmentName);
					StructureMapping.StructureMappingParameter mappingParam = vcBioModel.getSimulationContext(0)
							.getGeometryContext().getStructureMapping(struct).getSizeParameter();
					mappingParam.setExpression(sizeExpr);
				}
			}

			// Handle the absolute size to surface_vol/volFraction conversion if
			// size is set
			if (allSizesSet) {
				StructureSizeSolver.updateRelativeStructureSizes(vcBioModel.getSimulationContext(0));
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new SBMLImportException("Error adding Feature to vcModel "
					+ e.getMessage(), e);
		}
	}

	protected void addEvents() {
		if (sbmlModel.getNumEvents() > 0) {
			// VCell does not support events in spatial model
			if (bSpatial) {
				throw new SBMLImportException(
						"Events are not supported in a spatial VCell model.");
			}

			ListOf<Event> listofEvents = sbmlModel.getListOfEvents();

			Model vcModel = vcBioModel.getSimulationContext(0).getModel();
			for (int i = 0; i < sbmlModel.getNumEvents(); i++) {
				try {
					Event event = listofEvents.get(i);

					// trigger - adjust for species context and time conversion
					// factors if necessary
					Expression triggerExpr = null;
					if (event.isSetTrigger()) {
						triggerExpr = getExpressionFromFormula(event
								.getTrigger().getMath());
						triggerExpr = adjustExpression(triggerExpr, vcModel);
					}

					// create bioevent
					String eventName = event.getId();
					if (eventName == null || eventName.length() == 0) {
						eventName = TokenMangler.mangleToSName(event.getName());
						// if event name is still null, get free event name from
						// vcBioModel.getSimulationContext(0).
						if (eventName == null || eventName.length() == 0) {
							eventName = vcBioModel.getSimulationContext(0).getFreeEventName(null);
						}
					}

					// delay
					BioEvent vcEvent = new BioEvent(eventName,
							TriggerType.GeneralTrigger, true, vcBioModel.getSimulationContext(0));
					if (event.isSetDelay()) {
						Expression durationExpr = null;
						durationExpr = getExpressionFromFormula(event
								.getDelay().getMath());
						durationExpr = adjustExpression(durationExpr, vcModel);
						boolean bUseValsFromTriggerTime = true;
						if (event.isSetUseValuesFromTriggerTime()) {
							bUseValsFromTriggerTime = event
									.isSetUseValuesFromTriggerTime();
						} else {
							if (durationExpr != null && !durationExpr.isZero()) {
								bUseValsFromTriggerTime = false;
							}
						}

						if (durationExpr != null && !durationExpr.isZero()) {
							bUseValsFromTriggerTime = false;
						}
						vcEvent.setUseValuesFromTriggerTime(bUseValsFromTriggerTime);
						vcEvent.getParameter(BioEventParameterType.TriggerDelay)
								.setExpression(durationExpr);
					}

					// event assignments
					ArrayList<EventAssignment> vcEvntAssgnList = new ArrayList<EventAssignment>();
					for (int j = 0; j < event.getNumEventAssignments(); j++) {
						org.sbml.jsbml.EventAssignment sbmlEvntAssgn = event
								.getEventAssignment(j);
						String varName = sbmlEvntAssgn.getVariable();
						SymbolTableEntry varSTE = vcBioModel.getSimulationContext(0).getEntry(varName);
						if (varSTE != null) {
							Expression evntAssgnExpr = getExpressionFromFormula(sbmlEvntAssgn
									.getMath());
							evntAssgnExpr = adjustExpression(evntAssgnExpr,
									vcModel);
							EventAssignment vcEvntAssgn = vcEvent.new EventAssignment(
									varSTE, evntAssgnExpr);
							vcEvntAssgnList.add(vcEvntAssgn);
						} else {
							logger.sendMessage(VCLogger.Priority.HighPriority,
									VCLogger.ErrorType.UnsupportedConstruct,
									"No symbolTableEntry for '" + varName
											+ "'; Cannot add event assignment.");
						}
					}

					vcEvent.setEventAssignmentsList(vcEvntAssgnList);
					vcEvent.bind();
					vcBioModel.getSimulationContext(0).addBioEvent(vcEvent);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					throw new SBMLImportException(e.getMessage(), e);
				} // end - try/catch
			} // end - for(sbmlEvents)
		} // end - if numEvents > 0)
	}

	protected void addCompartmentTypes() {
		if (sbmlModel.getNumCompartmentTypes() > 0) {
			throw new SBMLImportException("VCell doesn't support CompartmentTypes at this time");
		}
	}

	protected void addSpeciesTypes() {
		if (sbmlModel.getNumSpeciesTypes() > 0) {
			throw new SBMLImportException("VCell doesn't support SpeciesTypes at this time");
		}
	}

	protected void addConstraints() {
		if (sbmlModel.getNumConstraints() > 0) {
			throw new SBMLImportException("VCell doesn't support Constraints at this time");
		}
	}

	protected void addInitialAssignments() {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf listofInitialAssgns = sbmlModel.getListOfInitialAssignments();
		if (listofInitialAssgns == null) {
			System.out.println("No Initial Assignments specified");
			return;
		}
		Model vcModel = vcBioModel.getSimulationContext(0).getModel();

		for (int i = 0; i < sbmlModel.getNumInitialAssignments(); i++) {
			try {
				InitialAssignment initAssgn = (InitialAssignment) listofInitialAssgns
						.get(i);
				String initAssgnSymbol = initAssgn.getSymbol();
				Expression initAssignMathExpr = getExpressionFromFormula(initAssgn
						.getMath());
				// if initial assignment is for a compartment, VCell doesn't
				// support compartmentSize expressions, warn and bail out.
				if (sbmlModel.getCompartment(initAssgnSymbol) != null) {
					if (!initAssignMathExpr.isNumeric()) {
						logger.sendMessage(
								VCLogger.Priority.HighPriority,
								VCLogger.ErrorType.CompartmentError,
								"compartment '"
										+ initAssgnSymbol
										+ "' size has an initial assignment, cannot handle it at this time.");
					}
					// if init assgn for compartment is numeric, the numeric
					// value for size is set in addCompartments().
				}
				// Check if init assgn expr for a species is in terms of x,y,z
				// or other species. Not allowed for species.
				if (!bSpatial && sbmlModel.getSpecies(initAssgnSymbol) != null) {
					if (initAssignMathExpr.hasSymbol(vcModel.getX().getName())
							|| initAssignMathExpr.hasSymbol(vcModel.getY()
									.getName())
							|| initAssignMathExpr.hasSymbol(vcModel.getZ()
									.getName())) {
						logger.sendMessage(
								VCLogger.Priority.HighPriority,
								VCLogger.ErrorType.SpeciesError,
								"species '"
										+ initAssgnSymbol
										+ "' initial assignment expression cannot contain 'x', 'y', 'z'.");
					}
				}

				initAssignMathExpr = adjustExpression(initAssignMathExpr,
						vcModel);
				// set the init assgn expr on VCell species init condn or global
				// parameter expression
				SpeciesContextSpec scs = vcBioModel.getSimulationContext(0).getReactionContext()
						.getSpeciesContextSpec(
								vcBioModel.getSimulationContext(0).getModel().getSpeciesContext(
										initAssgnSymbol));
				ModelParameter mp = vcBioModel.getSimulationContext(0).getModel().getModelParameter(
						initAssgnSymbol);
				if (scs != null) {
					scs.getInitialConditionParameter().setExpression(
							initAssignMathExpr);
				} else if (mp != null) {
					mp.setExpression(initAssignMathExpr);
				} else {
					localIssueList
							.add(new Issue(
									new SBMLIssueSource(initAssgn),
									issueContext,
									IssueCategory.SBMLImport_UnsupportedAttributeOrElement,
									"Symbol '"
											+ initAssgnSymbol
											+ "' not a species or global parameter in VCell; initial assignment ignored.",
									Issue.SEVERITY_WARNING));
					// logger.sendMessage(VCLogger.Priority.MediumPriority,
					// VCLogger.ErrorType.UnsupportedConstruct,
					// "Symbol '"+initAssgnSymbol+"' not a species or global parameter in VCell; initial assignment ignored..");
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Error reading InitialAssignment : "
						+ e.getMessage());
			}
		}
	}

	protected void addFunctionDefinitions() {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf listofFunctionDefinitions = sbmlModel
				.getListOfFunctionDefinitions();
		if (listofFunctionDefinitions == null) {
			System.out.println("No Function Definitions");
			return;
		}
		// The function definitions contain lambda function definition.
		// Each lambda function has a name, (list of) argument(s), function body
		// which is represented as a math element.
		lambdaFunctions = new LambdaFunction[(int) sbmlModel
				.getNumFunctionDefinitions()];
		try {
			for (int i = 0; i < sbmlModel.getNumFunctionDefinitions(); i++) {
				FunctionDefinition fnDefn = (FunctionDefinition) listofFunctionDefinitions
						.get(i);
				String functionName = new String(fnDefn.getId());
				ASTNode math = null;
				Vector<String> argsVector = new Vector<String>();
				String[] functionArgs = null;

				if (fnDefn.isSetMath()) {
					math = fnDefn.getMath();
					// Function body.
					if (math.getNumChildren() == 0) {
						System.out.println("(no function body defined)");
						continue;
					}
					// Add function arguments into vector, print args
					// Note that lambda function always should have at least 2
					// children
					for (int j = 0; j < math.getNumChildren() - 1; ++j) {
						argsVector.addElement(new String(math.getChild(j).getName()));
					}

					functionArgs = argsVector.toArray(new String[0]);

					math = math.getChild(math.getNumChildren() - 1);
					// formula = libsbml.formulaToString(math);

					Expression fnExpr = getExpressionFromFormula(math);
					lambdaFunctions[i] = new LambdaFunction(functionName,
							fnExpr, functionArgs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new SBMLImportException("Error adding Lambda function"
					+ e.getMessage(), e);
		}
	}

	/**
	 * addParameters : Adds global parameters from SBML model to VCell model. If
	 * expression for global parameter contains species, creates a conc_factor
	 * parameter (conversion from SBML - VCell conc units) and adds this factor
	 * to VC global params list, and replaces occurances of 'sp' with
	 * 'sp*concFactor' in original param expression.
	 * 
	 * @throws PropertyVetoException
	 */

	protected void addParameters() throws Exception {
		ListOf listofGlobalParams = sbmlModel.getListOfParameters();
		if (listofGlobalParams == null) {
			System.out.println("No Global Parameters");
			return;
		}
		Model vcModel = vcBioModel.getSimulationContext(0).getModel();
		ArrayList<ModelParameter> vcModelParamsList = new ArrayList<Model.ModelParameter>();

		// create a hash of reserved symbols so that if there is any reserved
		// symbol occurring as a global parameter in the SBML model,
		// the hash can be used to check for reserved symbols, so that it will
		// not be added as a global parameter in VCell,
		// since reserved symbols cannot be used as other variables (species,
		// structureSize, parameters, reactions, etc.).
		HashSet<String> reservedSymbolHash = new HashSet<String>();
		for (ReservedSymbol rs : vcModel.getReservedSymbols()) {
			reservedSymbolHash.add(rs.getName());
		}

		ModelUnitSystem modelUnitSystem = vcModel.getUnitSystem();

		for (int i = 0; i < sbmlModel.getNumParameters(); i++) {
			Parameter sbmlGlobalParam = (Parameter) listofGlobalParams.get(i);
			String paramName = sbmlGlobalParam.getId();

			SpatialParameterPlugin spplugin = null;
			if (bSpatial) {
				// check if parameter id is x/y/z : if so, check if its
				// 'spatialSymbolRef' child's spatial id and type are non-empty.
				// If so, the parameter represents a spatial element.
				// If not, throw an exception, since a parameter that does not
				// represent a spatial element cannot have an id of x/y/z

				spplugin = (SpatialParameterPlugin) sbmlGlobalParam.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
				if (paramName.equals("x") || paramName.equals("y") || paramName.equals("z")) {
					boolean bSpatialParam = (spplugin!=null && spplugin.getParamType() instanceof SpatialSymbolReference);

					// if (a) and (b) are not true, for param with id x/y/z,
					// throw exception - not allowed.
					// if (a) and (b) are true, continue with the next parameter
					if (!bSpatialParam) {
						throw new RuntimeException(
								"Parameter '"
										+ paramName
										+ "' is not a spatial parameter : Cannot have a variable in VCell named '"
										+ paramName
										+ "' unless it is a spatial variable.");
					} else {
						// go to the next parameter - do not add the spatial
						// parameter to the list of vcell parameters.
						continue;
					}
				}
			}

			//
			// Get param value if set or get its expression from rule
			//

			// Check if param is defined by an assignment rule or initial
			// assignment. If so, that value overrides the value existing in the
			// param element.
			// assignment rule, first
			Expression valueExpr = getValueFromAssignmentRule(paramName);
			if (valueExpr == null) {
				if (sbmlGlobalParam.isSetValue()) {
					double value = sbmlGlobalParam.getValue();
					valueExpr = new Expression(value);
				} else {
					// if value for global param is not set and param has a rate
					// rule, need to set an init value for param (else, there
					// will be a problem in reaction which uses this parameter).
					// use a 'default' initial value of '0'
					valueExpr = new Expression(0.0);
					// logger.sendMessage(VCLogger.Priority.MediumPriority,
					// VCLogger.Priority.LowPriority,
					// "Parameter did not have an initial value, but has a rate rule specified. Using a default value of 0.0.");
				}
			}

			if (valueExpr != null) {
				// valueExpr will be changed
				valueExpr = adjustExpression(valueExpr, vcModel);
			}

			// if SBML model is spatial, check if param represents species
			// diffusion/advection/boundary condition parameters for 'spatial'
			// extension
			if (bSpatial) {
				VCAssert.assertTrue(spplugin != null,
						"invalid initialization logic");
				ParameterType sbmlParamType = spplugin.getParamType();

				SpeciesContext paramSpContext = null;
				SpeciesContextSpec vcSpContextsSpec = null;
				// Check for diffusion coefficient(s)
				if (sbmlParamType instanceof DiffusionCoefficient){
					DiffusionCoefficient diffCoeff = (DiffusionCoefficient)sbmlParamType;
					if (diffCoeff != null && diffCoeff.isSetVariable()) {
						// get the var of diffCoeff; find appropriate spContext
						// in vcell; set its diff param to param value.
						paramSpContext = vcModel.getSpeciesContext(diffCoeff.getVariable());
						if (paramSpContext != null) {
							vcSpContextsSpec = vcBioModel.getSimulationContext(0).getReactionContext().getSpeciesContextSpec(paramSpContext);
							vcSpContextsSpec.getDiffusionParameter().setExpression(valueExpr);
						}
						// go to the next parameter - do not add the diffusion
						// coeff parameter to the list of vcell parameters.
						continue;
					}
				}

				// Check for advection coefficient(s)
				if (sbmlParamType instanceof AdvectionCoefficient){
					AdvectionCoefficient advCoeff = (AdvectionCoefficient)sbmlParamType; 
					if (advCoeff != null && advCoeff.isSetVariable()) {
						// get the var of advCoeff; find appropriate spContext
						// in vcell; set its adv param to param value.
						paramSpContext = vcModel.getSpeciesContext(advCoeff.getVariable());
						if (paramSpContext != null) {
							vcSpContextsSpec = vcBioModel.getSimulationContext(0).getReactionContext().getSpeciesContextSpec(paramSpContext);
							CoordinateKind coordKind = advCoeff.getCoordinate();
							SpeciesContextSpecParameter param = null;
							switch (coordKind){
							case cartesianX:{
								param = vcSpContextsSpec.getParameterFromRole(SpeciesContextSpec.ROLE_VelocityX);
								break;
							}
							case cartesianY:{
								param = vcSpContextsSpec.getParameterFromRole(SpeciesContextSpec.ROLE_VelocityY);
								break;
							}
							case cartesianZ:{
								param = vcSpContextsSpec.getParameterFromRole(SpeciesContextSpec.ROLE_VelocityZ);
								break;
							}
							}
							param.setExpression(valueExpr);
						}
						// go to the next parameter - do not add the advection
						// coeff parameter to the list of vcell parameters.
						continue;
					}
				}
				
				// Check for Boundary condition(s)
				if (sbmlParamType instanceof BoundaryCondition){
					BoundaryCondition bCondn = (BoundaryCondition)sbmlParamType;
					if (bCondn != null  && bCondn.isSetVariable()) {
						// get the var of boundaryCondn; find appropriate
						// spContext in vcell;
						// set the BC param of its speciesContextSpec to param
						// value.
						paramSpContext = vcModel.getSpeciesContext(bCondn.getVariable());
						if (paramSpContext == null) {
							throw new RuntimeException("unable to process boundary condition for variable "+bCondn.getVariable());
						}
						StructureMapping sm = vcBioModel.getSimulationContext(0).getGeometryContext().getStructureMapping(paramSpContext.getStructure());
						vcSpContextsSpec = vcBioModel.getSimulationContext(0).getReactionContext().getSpeciesContextSpec(paramSpContext);
						for (CoordinateComponent coordComp : getSbmlGeometry().getListOfCoordinateComponents()){
							if (bCondn.getSpatialRef().equals(coordComp.getBoundaryMinimum().getSpatialId())){
								switch (coordComp.getType()){
								case cartesianX:{
									vcSpContextsSpec.getBoundaryXmParameter().setExpression(valueExpr);
								}
								case cartesianY:{
									vcSpContextsSpec.getBoundaryYmParameter().setExpression(valueExpr);
								}
								case cartesianZ:{
									vcSpContextsSpec.getBoundaryZmParameter().setExpression(valueExpr);
								}
								}
							}
							if (bCondn.getSpatialRef().equals(coordComp.getBoundaryMaximum().getSpatialId())){
								switch (coordComp.getType()){
								case cartesianX:{
									vcSpContextsSpec.getBoundaryXpParameter().setExpression(valueExpr);
								}
								case cartesianY:{
									vcSpContextsSpec.getBoundaryYpParameter().setExpression(valueExpr);
								}
								case cartesianZ:{
									vcSpContextsSpec.getBoundaryZpParameter().setExpression(valueExpr);
								}
								}
							}
						}
						continue;
					}
				}
				
				// Check for Boundary condition(s)
				if (sbmlParamType instanceof SpatialSymbolReference){
					SpatialSymbolReference spatialSymbolRef = (SpatialSymbolReference)sbmlParamType;
					throw new RuntimeException("generic Spatial Symbol References not yet supported, unresolved spatial reference '"+spatialSymbolRef.getSpatialRef()+"'");	
				}
			}
					
			// Finally, create and add model parameter to VC model if it already
			// doesn't exist.
			if (vcModel.getModelParameter(paramName) == null) {
				VCUnitDefinition glParamUnitDefn = sbmlUnitIdentifierHash
						.get(sbmlGlobalParam.getUnits());
				// if units for param were not defined, don't let it be null;
				// set it to TBD or check if it was dimensionless.
				if (glParamUnitDefn == null) {
					glParamUnitDefn = modelUnitSystem.getInstance_TBD();
				}
				// Also check if the SBML global param is a reserved symbol in
				// VCell : cannot add reserved symbol to model params.
				if (!reservedSymbolHash.contains(paramName)) {
					ModelParameter vcGlobalParam = vcModel.new ModelParameter(
							paramName, valueExpr, Model.ROLE_UserDefined,
							glParamUnitDefn);
					if (paramName.length() > 64) {
						// record global parameter name in annotation if it is
						// longer than 64 characeters
						vcGlobalParam.setDescription("Parameter Name : "
								+ paramName);
					}
					vcModelParamsList.add(vcGlobalParam);
				}
			}
		} // end for - sbmlModel.parameters
		vcModel.setModelParameters(vcModelParamsList
				.toArray(new ModelParameter[0]));
	}

	/**
	 * @param spConcFactorInModelParamsList
	 * @param valueExpr
	 * @param vcModel
	 * @param vcSpContexts
	 * @throws PropertyVetoException
	 */
	private Expression adjustExpression(Expression valueExpr, Model model)
			throws PropertyVetoException {
		Expression adjustedExpr = new Expression(valueExpr);
		// ************* TIME CONV_FACTOR if 'time' is present in global
		// parameter expression
		// If time 't' is present in the global expression, it is in VC units
		// (secs), convert it back to SBML units
		// hence, we take the inverse of the time factor
		// (getSBMLTimeUnitsFactor() converts from SBML to VC units)

		/** ---- FOR NOW, IGNORE TIME UNIT CONVERSION ---- */
		// adjustedExpr = adjustTimeConvFactor(model, adjustedExpr);

		return adjustedExpr;
	}

	/**
	 * addReactionParticipant : Adds reactants and products and modifiers to a
	 * reaction. Input args are the sbml reaction, vc reaction This method was
	 * created mainly to handle reactions where there are reactants and/or
	 * products that appear multiple times in a reaction. Virtual Cell now
	 * allows the import of such reactions.
	 *
	 **/
	private void addReactionParticipants(org.sbml.jsbml.Reaction sbmlRxn, ReactionStep vcRxn, Map<String, String> sbmlToVclNameMap) throws Exception {
		Model vcModel = vcBioModel.getSimulationContext(0).getModel();
		//VCReactionProxy proxy = VCReactionProxy.factory(vcRxn);
		//VCAssert.assertTrue(vcModel == proxy.getModel(), "mismatch in Model between ReactionStep and SimulationContext" );

		//if (!(vcRxn instanceof FluxReaction)) {
		if (true) {
			// reactants in sbmlRxn
			HashMap<String, Integer> sbmlReactantsHash = new HashMap<String, Integer>();
			for (int j = 0; j < (int) sbmlRxn.getNumReactants(); j++) {
				SpeciesReference spRef = sbmlRxn.getReactant(j);
				String sbmlReactantSpId = spRef.getSpecies();
				if (sbmlModel.getSpecies(sbmlReactantSpId) != null) { // check
																		// if
																		// spRef
																		// is in
																		// sbml
																		// model
					// If stoichiometry of speciesRef is not an integer, it is
					// not handled in the VCell at this time; no point going
					// further
					double stoichiometry = 0.0;
					if (level < 3) { // for SBML models < L3, default
										// stoichiometry is 1, if field is not
										// set.
						stoichiometry = 1.0; // default value of stoichiometry,
												// if not set.
						if (spRef.isSetStoichiometry()) {
							stoichiometry = spRef.getStoichiometry();
							if (((int) stoichiometry != stoichiometry)
									|| spRef.isSetStoichiometryMath()) {
								throw new SBMLImportException(
										"Non-integer stoichiometry ('"
												+ stoichiometry
												+ "' for reactant '"
												+ sbmlReactantSpId
												+ "' in reaction '"
												+ sbmlRxn.getId()
												+ "') or stoichiometryMath not handled in VCell at this time.",
										Category.NON_INTEGER_STOICH);
								// logger.sendMessage(VCLogger.Priority.HighPriority,
								// VCLogger.ErrorType.ReactionError,
								// "Non-integer stoichiometry or stoichiometryMath not handled in VCell at this time.");
							}
						}
					} else {
						if (spRef.isSetStoichiometry()) {
							stoichiometry = spRef.getStoichiometry();
							if (((int) stoichiometry != stoichiometry)
									|| spRef.isSetStoichiometryMath()) {
								throw new SBMLImportException(
										"Non-integer stoichiometry ('"
												+ stoichiometry
												+ "' for reactant '"
												+ sbmlReactantSpId
												+ "' in reaction '"
												+ sbmlRxn.getId()
												+ "') or stoichiometryMath not handled in VCell at this time.",
										Category.NON_INTEGER_STOICH);
								// logger.sendMessage(VCLogger.Priority.HighPriority,
								// VCLogger.ErrorType.ReactionError,
								// "Non-integer stoichiometry or stoichiometryMath not handled in VCell at this time.");
								// logger.sendMessage(VCLogger.Priority.HighPriority,
								// VCLogger.ErrorType.ReactionError,
								// "Non-integer stoichiometry or stoichiometryMath not handled in VCell at this time.");
							}
						} else {
							throw new SBMLImportException(
									"This is a SBML level 3 model, stoichiometry is not set for the reactant '"
											+ sbmlReactantSpId
											+ "' and no default value can be assumed.");
							// logger.sendMessage(VCLogger.Priority.HighPriority,
							// VCLogger.ErrorType.ReactionError,
							// "This is a SBML level 3 model, stoichiometry is not set for the reactant '"
							// + spRef.getSpecies() +
							// "' and no default value can be assumed.");
						}
					}

					if (sbmlReactantsHash.get(sbmlReactantSpId) == null) {
						// if sbmlReactantSpId is NOT in sbmlReactantsHash, add
						// it with its stoichiometry
						sbmlReactantsHash.put(sbmlReactantSpId,
								Integer.valueOf((int) stoichiometry));
					} else {
						// if sbmlReactantSpId IS in sbmlReactantsHash, update
						// its stoichiometry value to (existing-from-hash +
						// stoichiometry) and put it back in hash
						int intStoich = sbmlReactantsHash.get(sbmlReactantSpId)
								.intValue();
						intStoich += (int) stoichiometry;
						sbmlReactantsHash.put(sbmlReactantSpId,
								Integer.valueOf(intStoich));
					}
				} else {
					// spRef is not in model, throw exception
					throw new SBMLImportException("Reactant '"
							+ sbmlReactantSpId + "' in reaction '"
							+ sbmlRxn.getId()
							+ "' not found as species in SBML model.");
				} // end - if (spRef is species in model)
			} // end - for reactants
			//proxy.addReactants(sbmlReactantsHash);

			// now add the reactants for the sbml reaction from
			// sbmlReactionParticipantsHash as reactants to vcRxn
			for (Entry<String, Integer> es : sbmlReactantsHash.entrySet()) {
				String sbmlReactantSpId = es.getKey();
				String vcSpeciesName = sbmlReactantSpId;
				if(sbmlToVclNameMap.get(sbmlReactantSpId) != null) {
					vcSpeciesName = sbmlToVclNameMap.get(sbmlReactantSpId);
				}
				SpeciesContext speciesContext = vcModel.getSpeciesContext(vcSpeciesName);
				int stoich = es.getValue(); 
				vcRxn.addReactant(speciesContext, stoich);	
			}
			/*
			Iterator<String> sbmlReactantsIter = sbmlReactantsHash.keySet()
					.iterator();
			while (sbmlReactantsIter.hasNext()) {
				String sbmlReactantStr = sbmlReactantsIter.next();
				SpeciesContext speciesContext = vcModel
						.getSpeciesContext(sbmlReactantStr);
				int stoich = sbmlReactantsHash.get(sbmlReactantStr).intValue();
				((SimpleReaction) vcRxn).addReactant(speciesContext, stoich);
			}
			*/

			// products in sbmlRxn
			HashMap<String, Integer> sbmlProductsHash = new HashMap<String, Integer>();
			for (int j = 0; j < (int) sbmlRxn.getNumProducts(); j++) {
				SpeciesReference spRef = sbmlRxn.getProduct(j);
				String sbmlProductSpId = spRef.getSpecies();
				if (sbmlModel.getSpecies(sbmlProductSpId) != null) { 
					/* check if spRef is in sbml model
					If stoichiometry of speciesRef is not an integer, it is
					not handled in the VCell at this time; no point going
					further */
					double stoichiometry = 0.0;
					if (level < 3) { // for sBML models < L3, default
										// stoichiometry is 1, if field is not
										// set.
						stoichiometry = 1.0; // default value of stoichiometry,
												// if not set.
						if (spRef.isSetStoichiometry()) {
							stoichiometry = spRef.getStoichiometry();
							if (((int) stoichiometry != stoichiometry)
									|| spRef.isSetStoichiometryMath()) {
								throw new SBMLImportException(
										"Non-integer stoichiometry ('"
												+ stoichiometry
												+ "' for product '"
												+ sbmlProductSpId
												+ "' in reaction '"
												+ sbmlRxn.getId()
												+ "') or stoichiometryMath not handled in VCell at this time.",
										Category.NON_INTEGER_STOICH);
								// logger.sendMessage(VCLogger.Priority.HighPriority,
								// VCLogger.ErrorType.ReactionError,
								// "Non-integer stoichiometry or stoichiometryMath not handled in VCell at this time.");
							}
						}
					} else {
						if (spRef.isSetStoichiometry()) {
							stoichiometry = spRef.getStoichiometry();
							if (((int) stoichiometry != stoichiometry)
									|| spRef.isSetStoichiometryMath()) {
								throw new SBMLImportException(
										"Non-integer stoichiometry ('"
												+ stoichiometry
												+ "' for product '"
												+ sbmlProductSpId
												+ "' in reaction '"
												+ sbmlRxn.getId()
												+ "') or stoichiometryMath not handled in VCell at this time.",
										Category.NON_INTEGER_STOICH);
								// logger.sendMessage(VCLogger.Priority.HighPriority,
								// VCLogger.ErrorType.ReactionError,
								// "Non-integer stoichiometry or stoichiometryMath not handled in VCell at this time.");
							}
						} else {
							throw new SBMLImportException(
									"This is a SBML level 3 model, stoichiometry is not set for the product '"
											+ sbmlProductSpId
											+ "' and no default value can be assumed.");
							// logger.sendMessage(VCLogger.Priority.HighPriority,
							// VCLogger.ErrorType.ReactionError,
							// "This is a SBML level 3 model, stoichiometry is not set for the product '"
							// + spRef.getSpecies() +
							// "' and no default value can be assumed.");
						}
					}

					if (sbmlProductsHash.get(sbmlProductSpId) == null) {
						// if sbmlProductSpId is NOT in sbmlProductsHash, add it
						// with its stoichiometry
						sbmlProductsHash.put(sbmlProductSpId,
								Integer.valueOf((int) stoichiometry));
					} else {
						// if sbmlProductSpId IS in sbmlProductsHash, update its
						// stoichiometry value to (existing-value-from-hash +
						// stoichiometry) and put it back in hash
						int intStoich = sbmlProductsHash.get(sbmlProductSpId)
								.intValue();
						intStoich += (int) stoichiometry;
						sbmlProductsHash.put(sbmlProductSpId,
								Integer.valueOf(intStoich));
					}
				} else {
					// spRef is not in model, throw exception
					throw new SBMLImportException("Product '" + sbmlProductSpId
							+ "' in reaction '" + sbmlRxn.getId()
							+ "' not found as species in SBML model.");
				} // end - if (spRef is species in model)
			} // end - for products

			// now add the products for the sbml reaction from sbmlProductsHash
			// as products to vcRxn
			for (Entry<String, Integer> es : sbmlProductsHash.entrySet()) {
				String sbmlProductSpId = es.getKey();
				String vcSpeciesName = sbmlProductSpId;
				if(sbmlToVclNameMap.get(sbmlProductSpId) != null) {
					vcSpeciesName = sbmlToVclNameMap.get(sbmlProductSpId);
				}
				SpeciesContext speciesContext = vcModel.getSpeciesContext(vcSpeciesName);
				int stoich = es.getValue(); 
				vcRxn.addProduct(speciesContext, stoich);	
			}
		
			/*
			Iterator<String> sbmlProductsIter = sbmlProductsHash.keySet()
					.iterator();
			while (sbmlProductsIter.hasNext()) {
				String sbmlProductStr = sbmlProductsIter.next();
				SpeciesContext speciesContext = vcModel
						.getSpeciesContext(sbmlProductStr);
				int stoich = sbmlProductsHash.get(sbmlProductStr).intValue();
				((SimpleReaction) vcRxn).addProduct(speciesContext, stoich);
			}
			*/
			//proxy.addProducts(sbmlProductsHash);
		} // end - if (vcRxn NOT FluxRxn)

		// modifiers
		for (int j = 0; j < (int) sbmlRxn.getNumModifiers(); j++) {
			ModifierSpeciesReference spRef = sbmlRxn.getModifier(j);
			String sbmlSpId = spRef.getSpecies();
			String vcSpeciesName = sbmlSpId;
			if(sbmlToVclNameMap.get(sbmlSpId) != null) {
				vcSpeciesName = sbmlToVclNameMap.get(sbmlSpId);
			}
			if (sbmlModel.getSpecies(sbmlSpId) != null) {
				// check if this modifier species is preesent in vcRxn (could
				// have been added as reactamt/product/catalyst).
				// If alreay a catalyst in vcRxn, do nothing
				ArrayList<ReactionParticipant> vcRxnParticipants = getVCReactionParticipantsFromSymbol(
						vcRxn, vcSpeciesName);
				SpeciesContext speciesContext = vcModel.getSpeciesContext(vcSpeciesName);
				if (vcRxnParticipants == null || vcRxnParticipants.size() == 0) {
					// If not in reactionParticipantList of vcRxn, add as
					// catalyst.
					vcRxn.addCatalyst(speciesContext);
				} else {
					for (ReactionParticipant rp : vcRxnParticipants) {
						if (rp instanceof Reactant || rp instanceof Product) {
							// If already a reactant or product in vcRxn, add
							// warning to localIssuesList, don't do anything
							localIssueList
									.add(new Issue(
											speciesContext,
											issueContext,
											IssueCategory.SBMLImport_Reaction,
											"Species "
													+ speciesContext.getName()
													+ " was already added as a reactant and/or product to "
													+ vcRxn.getName()
													+ "; Cannot add it as a catalyst also.",
											Issue.SEVERITY_INFO));
							break;
						}
					}
				}
			} else {
				// spRef is not in model, throw exception
				throw new SBMLImportException("Modifier '" + sbmlSpId
						+ "' in reaction '" + sbmlRxn.getId()
						+ "' not found as species in SBML model.");
			} // end - if (spRef is species in model)
		} // end - for modifiers
	}

	/**
	 * addAssignmentRules : Adds Assignment Rules from the SBML document
	 * Assignment rules are allowed (initial concentration of species; parameter
	 * definitions, etc.
	 *
	 **/
	protected void addAssignmentRules() throws Exception {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf listofRules = sbmlModel.getListOfRules();
		if (listofRules == null) {
			System.out.println("No Rules specified");
			return;
		}
		for (int i = 0; i < sbmlModel.getNumRules(); i++) {
			Rule rule = (org.sbml.jsbml.Rule) listofRules.get(i);
			if (rule instanceof AssignmentRule) {
				// Get the assignment rule and store it in the hashMap.
				AssignmentRule assignmentRule = (AssignmentRule) rule;
				Expression assignmentRuleMathExpr = getExpressionFromFormula(assignmentRule
						.getMath());
				String assgnRuleVar = assignmentRule.getVariable();
				// check if assignment rule is for species. If so, check if
				// expression has x/y/z term. This is not allowed for
				// non-spatial models in vcell.
				org.sbml.jsbml.Species ruleSpecies = sbmlModel
						.getSpecies(assgnRuleVar);
				if (ruleSpecies != null) {
					if (assignmentRuleMathExpr != null) {
						Model vcModel = vcBioModel.getSimulationContext(0).getModel();
						if (!bSpatial) {
							if (assignmentRuleMathExpr.hasSymbol(vcModel.getX()
									.getName())
									|| assignmentRuleMathExpr.hasSymbol(vcModel
											.getY().getName())
									|| assignmentRuleMathExpr.hasSymbol(vcModel
											.getZ().getName())) {
								logger.sendMessage(
										VCLogger.Priority.HighPriority,
										VCLogger.ErrorType.SpeciesError,
										"An assignment rule for species "
												+ ruleSpecies.getId()
												+ " contains "
												+ RESERVED_SPATIAL
												+ " variable(s) (x,y,z), this is not allowed for a non-spatial model in VCell");
							}
						}
					}
				}
				assignmentRulesHash.put(assignmentRule.getVariable(),
						assignmentRuleMathExpr);
			}
		} // end - for i : rules
	}

	/**
	 * addRateRules : Adds Rate Rules from the SBML document Rate rules are
	 * allowed (initial concentration of species; parameter definitions, etc.
	 * @throws XMLStreamException 
	 * @throws SBMLException 
	 *
	 **/
	protected void addRateRules() throws ExpressionException, SBMLException, XMLStreamException {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf listofRules = sbmlModel.getListOfRules();
		if (listofRules == null) {
			System.out.println("No Rules specified");
			return;
		}

		boolean bRateRule = false;
		for (int i = 0; i < sbmlModel.getNumRules(); i++) {
			Rule rule = (org.sbml.jsbml.Rule) listofRules.get(i);
			if (rule instanceof RateRule) {
				bRateRule = true;
				// TODO: re-enable importing of rate rules here
//				break;
				
				// Get the rate rule and store it in the hashMap, and create
				// VCell rateRule.
				RateRule sbmlRateRule = (RateRule) rule;
				// rate rule name
				String rateruleName = sbmlRateRule.getId();
				if (rateruleName == null || rateruleName.length() == 0) {
					rateruleName = TokenMangler.mangleToSName(sbmlRateRule.getName());
					// if rate rule name is still null, get free rate rule name
					// from vcBioModel.getSimulationContext(0).
					if (rateruleName == null || rateruleName.length() == 0) {
						rateruleName = vcBioModel.getSimulationContext(0).getFreeRateRuleName();
					}
				}
				// rate rule variable
				String varName = sbmlRateRule.getVariable();
				SymbolTableEntry rateRuleVar = vcBioModel.getSimulationContext(0).getEntry(varName);
				if (rateRuleVar instanceof Structure || rateRuleVar instanceof Structure.StructureSize) {
					throw new SBMLImportException("Compartment '" + rateRuleVar.getName() + "' has a rate rule: not allowed in VCell at this time.");
				}
				try {
					if (rateRuleVar != null) {
						Expression vcRateRuleExpr = getExpressionFromFormula(sbmlRateRule.getMath());
						cbit.vcell.mapping.RateRule vcRateRule = new cbit.vcell.mapping.RateRule(rateruleName, rateRuleVar, 
								vcRateRuleExpr,	vcBioModel.getSimulationContext(0));
						vcRateRule.bind();
						rateRulesHash.put(rateRuleVar.getName(), vcRateRuleExpr);
						vcBioModel.getSimulationContext(0).addRateRule(vcRateRule);
					}
				} catch (PropertyVetoException e) {
					e.printStackTrace(System.out);
					throw new SBMLImportException("Unable to create and add rate rule to VC model : " + e.getMessage());
				}

			} // end if - RateRule
		} // end - for i : rules
		if(bRateRule) {
			localIssueList.add(new Issue(vcBioModel, issueContext, IssueCategory.SBMLImport_UnsupportedFeature,
				"RateRules are not supported at this time. It may be possible to implement some of them through the Physiology.",
				Issue.Severity.WARNING));
		}
		
//		throw new SBMLImportException("Rate rules are not allowed in VCell at this time.");
	}

	protected void addSpecies(VCMetaData metaData, Map<String, String> vcToSbmlNameMap, Map<String, String> sbmlToVcNameMap) {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf listOfSpecies = sbmlModel.getListOfSpecies();
		if (listOfSpecies == null) {
			System.out.println("No Spcecies");
			return;
		}
		HashMap<String, Species> vcSpeciesHash = new HashMap<String, Species>();
		HashMap<Species, org.sbml.jsbml.Species> vc_sbmlSpeciesHash = new HashMap<Species, org.sbml.jsbml.Species>();
		SpeciesContext[] vcSpeciesContexts = new SpeciesContext[(int) sbmlModel.getNumSpecies()];
		// Get species from SBMLmodel; Add/get speciesContext
		try {
			// First pass - add the speciesContexts
			for (int i = 0; i < sbmlModel.getNumSpecies(); i++) {
				org.sbml.jsbml.Species sbmlSpecies = (org.sbml.jsbml.Species) listOfSpecies.get(i);
				// Sometimes, the species name can be null or a blank string; in
				// that case, use species id as the name.
				String sbmlSpeciesId = sbmlSpecies.getId();
				String vcSpeciesId = sbmlSpeciesId;			// we'll try to use the sbmlSpeciesId as name for the vCell species
				Species vcSpecies = null;
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
							vcSpecies = vcSpeciesHash.get(vcSpeciesName);
							if (vcSpecies == null) {
								ReservedSymbol rs = vcBioModel.getModel().getReservedSymbolByName(vcSpeciesName);
								if(rs != null) {
									System.out.println("Here too?");
								}
								vcSpecies = new Species(vcSpeciesName, vcSpeciesName);
								vcSpeciesHash.put(vcSpeciesName, vcSpecies);
							}
						}
						// if embedded element is not speciesTag, do I have to
						// do something?
					} else {
						// Annotation element is present, but doesn't contain
						// the species element.
						ReservedSymbol rs = vcBioModel.getModel().getReservedSymbolByName(sbmlSpeciesId);
						if(rs != null) {
							vcSpeciesId = "s_" + sbmlSpeciesId;
							vcToSbmlNameMap.put(vcSpeciesId, sbmlSpeciesId);
							sbmlToVcNameMap.put(sbmlSpeciesId, vcSpeciesId);
						}
						vcSpecies = new Species(vcSpeciesId, vcSpeciesId);
						vcSpeciesHash.put(vcSpeciesId, vcSpecies);
					}
				} else {
					ReservedSymbol rs = vcBioModel.getModel().getReservedSymbolByName(sbmlSpeciesId);
					if(rs != null) {		// try to rename the vCell species if its name is a vCell reserved symbol
						vcSpeciesId = "s_" + sbmlSpeciesId;
						vcToSbmlNameMap.put(vcSpeciesId, sbmlSpeciesId);
						sbmlToVcNameMap.put(sbmlSpeciesId, vcSpeciesId);
					}
					vcSpecies = new Species(vcSpeciesId, vcSpeciesId);
					vcSpeciesHash.put(vcSpeciesId, vcSpecies);
				}

				// store vc & sbml species in hash to read in annotation later
				vc_sbmlSpeciesHash.put(vcSpecies, sbmlSpecies);

				// Get matching compartment name (of sbmlSpecies[i]) from
				// feature list
				String compartmentId = sbmlSpecies.getCompartment();
				Structure spStructure = vcBioModel.getSimulationContext(0).getModel().getStructure(compartmentId);
				vcSpeciesContexts[i] = new SpeciesContext(vcSpecies, spStructure);
				vcSpeciesContexts[i].setName(vcSpeciesId);
				
				if(sbmlSpecies.isSetName()) {
					String sbmlSpeciesName = sbmlSpecies.getName();
					vcSpeciesContexts[i].setSbmlName(sbmlSpeciesName);
				}

				// Adjust units of species, convert to VC units.
				// Units in SBML, compute this using some of the attributes of
				// sbmlSpecies
				Compartment sbmlCompartment = sbmlModel.getCompartment(
						sbmlSpecies.getCompartment());
				int dimension = 3;
				if (sbmlCompartment.isSetSpatialDimensions()){
					dimension = (int) sbmlCompartment.getSpatialDimensions();
				}
				if (dimension == 0 || dimension == 1) {
					logger.sendMessage(VCLogger.Priority.HighPriority,
							VCLogger.ErrorType.UnitError, dimension
									+ " dimensional compartment "
									+ compartmentId + " not supported");
				}
			} // end - for sbmlSpecies

			// set the species & speciesContexts on model
			Model vcModel = vcBioModel.getSimulationContext(0).getModel();
			vcModel.setSpecies(vcSpeciesHash.values().toArray(new Species[0]));
			vcModel.setSpeciesContexts(vcSpeciesContexts);

			// Set annotations and notes from SBML to VCMetadata
			Species[] vcSpeciesArray = vc_sbmlSpeciesHash.keySet().toArray(
					new Species[0]);
			for (Species vcSpecies : vcSpeciesArray) {
				org.sbml.jsbml.Species sbmlSpecies = vc_sbmlSpeciesHash
						.get(vcSpecies);
				sbmlAnnotationUtil.readAnnotation(vcSpecies, sbmlSpecies);
				sbmlAnnotationUtil.readNotes(vcSpecies, sbmlSpecies);
			}
		} catch (ModelPropertyVetoException e) {
			throw new SBMLImportException("Error adding species context; "
					+ e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new SBMLImportException("Error adding species context; "
					+ e.getMessage(), e);
		}
	}

	/**
	 * setSpeciesInitialConditions : called after speciesContexts and global
	 * parameters have been set. Checks for init conditions set on species in
	 * the Sbml model, and if it is set using an assignment rule, obtain the
	 * corresponding expression. Obtain the sbml -> vc unit conversion factor
	 * for species concentrations to adjust the species initial condition
	 * units/factor.
	 * 
	 */
	private void setSpeciesInitialConditions(Map<String, String> vcToSbmlNameMap) {
		try {
			// fill in SpeciesContextSpec for each speciesContext
			Model vcModel = vcBioModel.getSimulationContext(0).getModel();
			SpeciesContext[] vcSpeciesContexts = vcModel.getSpeciesContexts();
			for (int i = 0; i < vcSpeciesContexts.length; i++) {
				String vcSpeciesContextName = vcSpeciesContexts[i].getName();
				String sbmlSpeciesName = vcSpeciesContextName;
				if(vcToSbmlNameMap.get(vcSpeciesContextName) != null) {
					sbmlSpeciesName = vcToSbmlNameMap.get(vcSpeciesContextName);
				}
				org.sbml.jsbml.Species sbmlSpecies = (org.sbml.jsbml.Species) sbmlModel.getSpecies(sbmlSpeciesName);
				// Sometimes, the species name can be null or a blank string; in
				// that case, use species id as the name.
				String speciesName = sbmlSpecies.getId();
				Compartment compartment = (Compartment) sbmlModel.getCompartment(sbmlSpecies.getCompartment());

				Expression initExpr = null;
				if (sbmlSpecies.isSetInitialConcentration()) { // If initial
																// Concentration
																// is set
					Expression initConcentration = new Expression(
							sbmlSpecies.getInitialConcentration());
					// check if initConc is set by a (assignment) rule. That
					// takes precedence over initConc value set on species.
					initExpr = getValueFromAssignmentRule(speciesName);
					if (initExpr == null) {
						initExpr = new Expression(initConcentration);
					}
				} else if (sbmlSpecies.isSetInitialAmount()) { // If initial
																// amount is set
					double initAmount = sbmlSpecies.getInitialAmount();
					// initConcentration := initAmount / compartmentSize.
					// If compartmentSize is set and non-zero, compute
					// initConcentration. Else, throw exception.
					if (compartment.isSetSize()) {
						double compartmentSize = compartment.getSize();
						Expression initConcentration = new Expression(0.0);
						if (compartmentSize != 0.0) {
							initConcentration = new Expression(initAmount
									/ compartmentSize);
						} else {
							logger.sendMessage(
									VCLogger.Priority.HighPriority,
									VCLogger.ErrorType.UnitError,
									"compartment '"
											+ compartment.getId()
											+ "' has zero size, unable to determine initial concentration for species "
											+ speciesName);
						}
						// check if initConc is set by a (assignment) rule. That
						// takes precedence over initConc/initAmt value set on
						// species.
						initExpr = getValueFromAssignmentRule(speciesName);
						if (initExpr == null) {
							initExpr = new Expression(initConcentration);
						}
					} else {
						logger.sendMessage(
								VCLogger.Priority.HighPriority,
								VCLogger.ErrorType.SpeciesError,
								" Compartment '"
										+ compartment.getId()
										+ "' size not set or is defined by a rule; cannot calculate initConc.");
					}
				} else {
					// initConc/initAmt not set; check if species has a
					// (assignment) rule.
					initExpr = getValueFromAssignmentRule(speciesName);
					if (initExpr == null) {
						// no assignment rule (and there was no initConc or
						// initAmt); if it doesn't have initialAssignment, throw
						// warning and set it to 0.0
						if (sbmlModel.getInitialAssignment(speciesName) == null) {
							localIssueList
									.add(new Issue(
											new SBMLIssueSource(sbmlModel
													.getSpecies(speciesName)),
											issueContext,
											IssueCategory.SBMLImport_MissingSpeciesInitCondition,
											"no initial condition for species "
													+ speciesName
													+ ", assuming 0.0",
											Issue.SEVERITY_WARNING));
							// logger.sendMessage(VCLogger.Priority.MediumPriority,
							// VCLogger.ErrorType.UnitError,
							// "no initial condition for species "+speciesName+", assuming 0.0");
						}
						initExpr = new Expression(0.0);
					}

				}

				// if initExpr is an expression with model species, we need a
				// conversion factor for the species units (SBML - VC units),
				// similar to the conversion that is done in reactions.
				if (initExpr != null) {
					// initExpr will be changed
					initExpr = adjustExpression(initExpr, vcModel);
				}

				// If any of the symbols in the expression for speciesConc is a
				// rule, expand it.
				substituteGlobalParamRulesInPlace(initExpr, false);

				SpeciesContextSpec speciesContextSpec = vcBioModel.getSimulationContext(0)
						.getReactionContext().getSpeciesContextSpec(
								vcSpeciesContexts[i]);
				speciesContextSpec.getInitialConditionParameter()
						.setExpression(initExpr);
				speciesContextSpec.setConstant(sbmlSpecies
						.getBoundaryCondition() || sbmlSpecies.getConstant());
			}
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			throw new SBMLImportException(
					"Error setting initial condition for species context; "
							+ e.getMessage(), e);
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
	 * 
	 * @param sbmlRxn
	 * @param newKinetics
	 * @throws ExpressionException
	 */
	private void resolveRxnParameterNameConflicts(Reaction sbmlRxn,
			Kinetics vcKinetics, Element sbmlImportElement)
			throws PropertyVetoException {
		// If the name of the rate parameter has been changed by user, it is
		// stored in rxnAnnotation.
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
		ListOf listofGlobalParams = sbmlModel.getListOfParameters();
		for (int j = 0; j < sbmlModel.getNumParameters(); j++) {
			org.sbml.jsbml.Parameter param = (org.sbml.jsbml.Parameter) listofGlobalParams.get(j);
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
			ListOf listofLocalParams = kLaw.getListOfParameters();
			for (int j = 0; j < kLaw.getNumParameters(); j++) {
				org.sbml.jsbml.LocalParameter param = (org.sbml.jsbml.LocalParameter) listofLocalParams.get(j);
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
	 * 
	 * @param sbmlRxn
	 * @param refSpeciesNameHash
	 * @throws ExpressionException
	 * @throws XMLStreamException 
	 * @throws SBMLException 
	 */
	private void getReferencedSpecies(Reaction sbmlRxn,
			HashSet<String> refSpeciesNameHash) throws ExpressionException, SBMLException, XMLStreamException {
		// get all species referenced in listOfReactants
		for (int i = 0; i < (int) sbmlRxn.getNumReactants(); i++) {
			SpeciesReference reactRef = sbmlRxn.getReactant(i);
			refSpeciesNameHash.add(reactRef.getSpecies());
		}
		// get all species referenced in listOfProducts
		for (int i = 0; i < (int) sbmlRxn.getNumProducts(); i++) {
			SpeciesReference pdtRef = sbmlRxn.getProduct(i);
			refSpeciesNameHash.add(pdtRef.getSpecies());
		}
		// get all species referenced in reaction rate law
		if (sbmlRxn.getKineticLaw() != null) {
			Expression rateExpression = getExpressionFromFormula(sbmlRxn
					.getKineticLaw().getMath());
			getReferencedSpeciesInExpr(rateExpression, refSpeciesNameHash);
		}
	}

	/**
	 * getReferencedSpeciesInExpr(Expression , HashSet<String> ) : Recursive
	 * method to get species referenced in expression 'sbmlExpr' - takes care of
	 * cases where expressions have symbols that are themselves expression and
	 * might contain other species.
	 * 
	 * @param sbmlExpr
	 * @param refSpNamesHash
	 * @throws ExpressionException
	 */
	private void getReferencedSpeciesInExpr(Expression sbmlExpr,
			HashSet<String> refSpNamesHash) throws ExpressionException {
		String[] symbols = sbmlExpr.getSymbols();
		for (int i = 0; symbols != null && i < symbols.length; i++) {
			Parameter sbmlParam = sbmlModel.getParameter(symbols[i]);
			if (sbmlParam != null) {
				Expression paramExpression = getValueFromAssignmentRule(sbmlParam
						.getId());
				if (paramExpression != null) {
					getReferencedSpeciesInExpr(paramExpression, refSpNamesHash);
				}
			} else {
				org.sbml.jsbml.Species sbmlSpecies = sbmlModel
						.getSpecies(symbols[i]);
				if (sbmlSpecies != null) {
					refSpNamesHash.add(sbmlSpecies.getId());
				}
			}
		}
	}

	/**
	 * substituteGlobalParamRulesInPlace:
	 * 
	 * @param sbmlExpr
	 * @param expandedExpr
	 * @throws ExpressionException
	 */
	private void substituteGlobalParamRulesInPlace(Expression sbmlExpr,
			boolean bReplaceValues) throws ExpressionException {
		boolean bParamChanged = true;
		while (bParamChanged) {
			bParamChanged = false;
			String[] symbols = sbmlExpr.getSymbols();
			for (int i = 0; symbols != null && i < symbols.length; i++) {
				Parameter sbmlParam = sbmlModel.getParameter(symbols[i]);
				if (sbmlParam != null) {
					Expression paramExpression = getValueFromAssignmentRule(sbmlParam
							.getId());
					if (paramExpression != null) {
						sbmlExpr.substituteInPlace(
								new Expression(sbmlParam.getId()),
								paramExpression);
						bParamChanged = true;
					} else if (bReplaceValues) {
						sbmlExpr.substituteInPlace(
								new Expression(sbmlParam.getId()),
								new Expression(sbmlParam.getValue()));
					}
				}
			}
		}
	}

	// /**
	// * @ TODO: This method doesn't take care of adjusting species in nested
	// parameter rules with the species_concetration_factor.
	// * @param kinetics
	// * @param paramExpr
	// * @throws ExpressionException
	// */
	// private void substituteOtherGlobalParams(Kinetics kinetics, Expression
	// paramExpr) throws ExpressionException, PropertyVetoException {
	// String[] exprSymbols = paramExpr.getSymbols();
	// if (exprSymbols == null || exprSymbols.length == 0) {
	// return;
	// }
	// Model vcModel = vcBioModel.getSimulationContext(0).getModel();
	// for (int kk = 0; kk < exprSymbols.length; kk++) {
	// ModelParameter mp = vcModel.getModelParameter(exprSymbols[kk]);
	// if (mp != null) {
	// Expression expr = mp.getExpression();
	// if (expr != null) {
	// Expression newExpr = new Expression(expr);
	// substituteGlobalParamRulesInPlace(newExpr, false);
	// // param has constant value, add it as a kinetic parameter if it is not
	// already in the kinetics
	// kinetics.setParameterValue(exprSymbols[kk], newExpr.infix());
	// kinetics.getKineticsParameter(exprSymbols[kk]).setUnitDefinition(getSBMLUnit(sbmlModel.getParameter(exprSymbols[kk]).getUnits(),
	// null));
	// if (newExpr.getSymbols() != null) {
	// substituteOtherGlobalParams(kinetics, newExpr);
	// }
	// }
	// }
	// }
	// }

	/**
	 * parse SBML file into biomodel logs errors to log4j if present in source
	 * document
	 * 
	 * @return new Biomodel
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public BioModel getBioModel() throws XMLStreamException, IOException {
		SBMLDocument document;
		String output = "didn't check";
		try {
			if (sbmlFileName != null) {
				// Read SBML model into libSBML SBMLDocument and create an SBML model
				SBMLReader reader = new SBMLReader();
				document = reader.readSBML(sbmlFileName);
	//		document.checkConsistencyOffline();
	//		long numProblems = document.getNumErrors();
	//
	//		System.out.println("\n\nSBML Import Error Report");
	//		ByteArrayOutputStream os = new ByteArrayOutputStream();
	//		PrintStream ps = new PrintStream(os);
	//		document.printErrors(ps);
	//		String output = os.toString();
	//		if (numProblems > 0 && lg.isEnabledFor(Level.WARN)) {
	//			lg.warn("Num problems in original SBML document : " + numProblems);
	//			lg.warn(output);
	//		}

				sbmlModel = document.getModel();
				if (sbmlModel == null) {
					throw new SBMLImportException("Unable to read SBML file : \n" + output);
				}
			}
			else {
				if (sbmlModel == null) {
					throw new IllegalStateException("Expected non-null SBML model");
				}
				document = sbmlModel.getSBMLDocument();
			}

			// Convert SBML Model to VCell model
			// An SBML model will correspond to a simcontext - which needs a
			// Model and a Geometry
			// SBML handles only nonspatial geometries at this time, hence
			// creating a non-spatial default geometry
			String modelName = sbmlModel.getId();
			if (modelName == null || modelName.trim().equals("")) {
				modelName = sbmlModel.getName();
			}
			// if sbml 'model' didn't have either id or name set, use a default
			// name, say 'newModel'
			if (modelName == null || modelName.trim().equals("")) {
				modelName = "newModel";
			}

			// get namespace based on SBML model level and version to use in
			// SBMLAnnotationUtil
			this.level = sbmlModel.getLevel();
			// this.version = sbmlModel.getVersion();
			String ns = document.getNamespace();
			
			try {
				// create SBML unit system for the model and create the bioModel.
				ModelUnitSystem modelUnitSystem;
				try {
					modelUnitSystem = createSBMLUnitSystemForVCModel();
				} catch (Exception e) {
					e.printStackTrace(System.out);
					throw new SBMLImportException(
							"Inconsistent unit system. Cannot import SBML model into VCell",
							Category.INCONSISTENT_UNIT, e);
				}
				Geometry geometry = new Geometry(BioModelChildSummary.COMPARTMENTAL_GEO_STR, 0);
				vcBioModel = new BioModel(null,modelUnitSystem);
				SimulationContext simulationContext = new SimulationContext(vcBioModel.getModel(), geometry, null, null, Application.NETWORK_DETERMINISTIC);
				vcBioModel.addSimulationContext(simulationContext);
				simulationContext.setName(vcBioModel.getSimulationContext(0).getModel().getName());
				// vcBioModel.getSimulationContext(0).setName(vcBioModel.getSimulationContext(0).getModel().getName()+"_"+vcBioModel.getSimulationContext(0).getGeometry().getName());
			} catch (PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new SBMLImportException("Could not create simulation context corresponding to the input SBML model",e);
			}
			
			// SBML annotation
			sbmlAnnotationUtil = new SBMLAnnotationUtil(vcBioModel.getVCMetaData(), vcBioModel, ns);

			translateSBMLModel();

			try {
				// **** TEMPORARY BLOCK - to name the biomodel with proper name,
				// rather than model id
				String biomodelName = sbmlModel.getName();
				// if name is not set, use id
				if ((biomodelName == null) || biomodelName.trim().equals("")) {
					biomodelName = sbmlModel.getId();
				}
				// if id is not set, use a default, say, 'newModel'
				if ((biomodelName == null) || biomodelName.trim().equals("")) {
					biomodelName = "newBioModel";
				}
				vcBioModel.setName(biomodelName);
				// **** end - TEMPORARY BLOCK
			} catch (Exception e) {
				e.printStackTrace(System.out);
				throw new SBMLImportException("Could not create Biomodel", e);
			}

			sbmlAnnotationUtil.readAnnotation(vcBioModel, sbmlModel);
			sbmlAnnotationUtil.readNotes(vcBioModel, sbmlModel);
//			try {
//				vcBioModel.getVCMetaData().printRdfPretty();
//				vcBioModel.getVCMetaData().printRdfStatements();
//			} catch(Exception e) {
//				System.out.println("Error importing RBM MetaData from SBML");
//			}

			vcBioModel.refreshDependencies();

			Issue warningIssues[] = localIssueList
					.toArray(new Issue[localIssueList.size()]);
			if (warningIssues != null && warningIssues.length > 0) {
				StringBuffer messageBuffer = new StringBuffer(
						"Issues encountered during SBML Import:\n");
				int issueCount = 0;
				for (int i = 0; i < warningIssues.length; i++) {
					if (warningIssues[i].getSeverity() == Issue.SEVERITY_WARNING
							|| warningIssues[i].getSeverity() == Issue.SEVERITY_INFO) {
						messageBuffer.append("- " + warningIssues[i].getMessage()
								+ " (" + warningIssues[i].getCategory() + ", " + warningIssues[i].getSeverityName() + ")\n");
						issueCount++;
					}
				}
				if (issueCount > 0) {
					try {
						logger.sendMessage(VCLogger.Priority.MediumPriority,
								VCLogger.ErrorType.OverallWarning,
								messageBuffer.toString());
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
					// PopupGenerator.showWarningDialog(requester,messageBuffer.toString(),new
					// String[] { "OK" }, "OK");
				}
			}
		} catch (Exception e) {
			throw new SBMLImportException("Unable to read SBML file : \n"
					+ output, e);
		}

		return vcBioModel;
	}

	private ModelUnitSystem createSBMLUnitSystemForVCModel() throws Exception {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf listofUnitDefns = sbmlModel.getListOfUnitDefinitions();
		if (listofUnitDefns == null) {
			System.out.println("No Unit Definitions");
			// if < level 3, use SBML default units to create unit system; else,
			// return a default VC modelUnitSystem.
			// @TODO: deal with SBML level < 3.
			return ModelUnitSystem.createDefaultVCModelUnitSystem();
		}

		@SuppressWarnings("serial")
		VCUnitSystem tempVCUnitSystem = ModelUnitSystem.createDefaultVCModelUnitSystem();
		sbmlUnitIdentifierHash = new HashMap<String, VCUnitDefinition>();
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
			UnitDefinition ud = (org.sbml.jsbml.UnitDefinition) listofUnitDefns.get(i);
			String unitName = ud.getId();
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
		for (int i = 0; i < listOfCompartments.size(); i++) {
			Compartment sbmlComp = listOfCompartments.get(i);
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
									Issue.SEVERITY_WARNING));
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
									Issue.SEVERITY_WARNING));
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
		for (int i = 0; i < listOfSpecies.size(); i++) {
			org.sbml.jsbml.Species sbmlSpecies = listOfSpecies.get(i);
			String unitStr = sbmlSpecies.getSubstanceUnits();
			VCUnitDefinition sbmlUnitDefinition = null;
			if (unitStr != null && unitStr.length() > 0) {
				sbmlUnitDefinition = sbmlUnitIdentifierHash.get(unitStr);
			} else {
				// apply default substance unit
				sbmlUnitDefinition = defaultSubstanceUnit;
			}
			if (modelSubstanceUnit == null) {
				modelSubstanceUnit = sbmlUnitDefinition;
			} else if (!sbmlUnitDefinition.isEquivalent(modelSubstanceUnit)) {
				localIssueList
						.add(new Issue(
								new SBMLIssueSource(sbmlSpecies),
								issueContext,
								IssueCategory.Units,
								"unit for species '"
										+ sbmlSpecies.getId()
										+ "' ("
										+ unitStr
										+ ") : ("
										+ sbmlUnitDefinition.getSymbol()
										+ ") not compatible with current substance unit ("
										+ modelSubstanceUnit.getSymbol() + ")",
								Issue.SEVERITY_WARNING));
				// logger.sendMessage(VCLogger.Priority.MediumPriority,
				// VCLogger.ErrorType.UnitError, "unit for species '" +
				// sbmlSpecies.getId() + "' (" + unitStr + ") : (" +
				// sbmlUnitDefinition.getSymbol() +
				// ") not compatible with current substance unit (" +
				// modelSubstanceUnit.getSymbol() + ")");
			}
		}

		// reactions for SBML level 2 version < 3
		long sbmlVersion = sbmlModel.getVersion();
		if (sbmlVersion < 3) {
			ListOf<Reaction> listOfReactions = sbmlModel.getListOfReactions();
			for (int i = 0; i < listOfReactions.size(); i++) {
				Reaction sbmlReaction = listOfReactions.get(i);
				KineticLaw kineticLaw = sbmlReaction.getKineticLaw();
				if (kineticLaw != null) {
					// first check substance unit
					String unitStr = kineticLaw.getSubstanceUnits();
					VCUnitDefinition sbmlUnitDefinition = null;
					if (unitStr != null && unitStr.length() > 0) {
						sbmlUnitDefinition = sbmlUnitIdentifierHash
								.get(unitStr);
					} else {
						// apply default substance unit
						sbmlUnitDefinition = defaultSubstanceUnit;
					}
					if (modelSubstanceUnit == null) {
						modelSubstanceUnit = sbmlUnitDefinition;
					} else if (!sbmlUnitDefinition
							.isEquivalent(modelSubstanceUnit)) {
						localIssueList
								.add(new Issue(
										new SBMLIssueSource(sbmlReaction),
										issueContext,
										IssueCategory.Units,
										"substance unit for reaction '"
												+ sbmlReaction.getId()
												+ "' ("
												+ unitStr
												+ ") : ("
												+ sbmlUnitDefinition
														.getSymbol()
												+ ") not compatible with current substance unit ("
												+ modelSubstanceUnit
														.getSymbol() + ")",
										Issue.SEVERITY_WARNING));
						// logger.sendMessage(VCLogger.Priority.MediumPriority,
						// VCLogger.ErrorType.UnitError,
						// "substance unit for reaction '" +
						// sbmlReaction.getId() + "' (" + unitStr + ") : (" +
						// sbmlUnitDefinition.getSymbol() +
						// ") not compatible with current substance unit (" +
						// modelSubstanceUnit.getSymbol() + ")");
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
						localIssueList
								.add(new Issue(
										new SBMLIssueSource(sbmlReaction),
										issueContext,
										IssueCategory.Units,
										"time unit for reaction '"
												+ sbmlReaction.getId()
												+ "' ("
												+ unitStr
												+ ") : ("
												+ sbmlUnitDefinition
														.getSymbol()
												+ ") not compatible with current time unit ("
												+ modelTimeUnit.getSymbol()
												+ ")", Issue.SEVERITY_WARNING));
						// logger.sendMessage(VCLogger.Priority.MediumPriority,
						// VCLogger.ErrorType.UnitError,
						// "time unit for reaction '" + sbmlReaction.getId() +
						// "' (" + unitStr + ") : (" +
						// sbmlUnitDefinition.getSymbol() +
						// ") not compatible with current time unit (" +
						// modelTimeUnit.getSymbol() + ")");
					}
				}
			}
		}

		ModelUnitSystem mus = ModelUnitSystem.createDefaultVCModelUnitSystem();
		if(modelVolumeUnit.isEquivalent(mus.getInstance_DIMENSIONLESS())) {
			if(modelLengthUnit == null && modelAreaUnit == null) {
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
			return ModelUnitSystem.createSBMLUnitSystem(modelSubstanceUnit,
					modelVolumeUnit, modelAreaUnit, modelLengthUnit,
					modelTimeUnit);
		}

	}

	/**
	 * getEmbeddedElementInRxnAnnotation : Takes the reaction annotation as an
	 * argument and returns the embedded element (fluxstep or simple reaction),
	 * if present.
	 */
	private Element getEmbeddedElementInAnnotation(
			Element sbmlImportRelatedElement, String tag) {
		// Get the XML element corresponding to the annotation xmlString.
		String elementName = null;
		if (tag.equals(RATE_NAME)) {
			elementName = XMLTags.ReactionRateTag;
		} else if (tag.equals(SPECIES_NAME)) {
			elementName = XMLTags.SpeciesTag;
		} else if (tag.equals(REACTION)) {
			if (sbmlImportRelatedElement.getChild(XMLTags.FluxStepTag,
					sbmlImportRelatedElement.getNamespace()) != null) {
				elementName = XMLTags.FluxStepTag;
			} else if (sbmlImportRelatedElement.getChild(
					XMLTags.SimpleReactionTag,
					sbmlImportRelatedElement.getNamespace()) != null) {
				elementName = XMLTags.SimpleReactionTag;
			}
		} else if (tag.equals(OUTSIDE_COMP_NAME)) {
			elementName = XMLTags.OutsideCompartmentTag;
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
	 * @throws XMLStreamException 
	 * @throws SBMLException 
	 */
	private Expression getExpressionFromFormula(ASTNode math)
			throws ExpressionException, SBMLException, XMLStreamException {
		String mathMLStr = JSBML.writeMathMLToString(math);
		if (mathMLStr.contains(DELAY_URL)) {
			throw new SBMLImportException("unsupported SBML element 'delay'",
					SBMLImportException.Category.DELAY);

		}
		ExpressionMathMLParser exprMathMLParser = new ExpressionMathMLParser(
				lambdaFunctions);
		Expression expr = exprMathMLParser.fromMathML(mathMLStr);
		return expr;
	}

	/**
	 * getReactionStructure :
	 */
	private Structure getReactionStructure(org.sbml.jsbml.Reaction sbmlRxn,
			SpeciesContext[] speciesContexts, Element sbmlImportElement)
			throws Exception {
		Structure struct = null;
		String structName = null;
		Model vcModel = vcBioModel.getSimulationContext(0).getModel();

		// if sbml model is spatial, see if reaction has compartment atribute,
		// return structure from vcmodel, if present.
		if (bSpatial) {
			structName = sbmlRxn.getCompartment();
			if (structName != null && structName.length() > 0) {
				struct = vcModel.getStructure(structName);
				if (struct != null) {
					return struct;
				}
			}
		}

		// Check annotation for reaction - if we are importing an exported VCell
		// model, it will contain annotation for reaction.
		// If annotation has structure name, return the corresponding structure.
		if (sbmlImportElement != null) {
			// Get the embedded element in the annotation str (fluxStep or
			// simpleReaction), and the structure attribute from the element.
			Element embeddedElement = getEmbeddedElementInAnnotation(
					sbmlImportElement, REACTION);
			if (embeddedElement != null) {
				structName = embeddedElement
						.getAttributeValue(XMLTags.StructureAttrTag);
				// Using the structName, get the structure from the structures
				// (compartments) list.
				struct = vcModel.getStructure(structName);
				return struct;
			}
		}

		if (sbmlRxn.isSetKineticLaw()) {
			// String rxnName = sbmlRxn.getId();
			KineticLaw kLaw = sbmlRxn.getKineticLaw();
			Expression kRateExp = getExpressionFromFormula(kLaw.getMath());
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

		HashSet<String> refSpeciesNameHash = new HashSet<String>();
		getReferencedSpecies(sbmlRxn, refSpeciesNameHash);

		java.util.Iterator<String> refSpIterator = refSpeciesNameHash
				.iterator();
		HashSet<String> compartmentNamesHash = new HashSet<String>();
		while (refSpIterator.hasNext()) {
			String spName = refSpIterator.next();
			String rxnCompartmentName = sbmlModel.getSpecies(spName)
					.getCompartment();
			compartmentNamesHash.add(rxnCompartmentName);
		}

		if (compartmentNamesHash.size() == 1) {
			struct = vcModel.getStructure(compartmentNamesHash.iterator()
					.next());
			return struct;
		} else if (compartmentNamesHash.size() == 0) {
			struct = vcModel.getStructures()[0];
			return struct;
		} else {
			// more than one structure in reaction participants, try to figure
			// out which one to choose
			HashMap<String, Integer> structureFrequencyHash = new HashMap<String, Integer>();
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

	/**
	 * getSpatialDimentionBuiltInName :
	 */
	/*
	 * pending delete? gcw 4/2014 private String
	 * getSpatialDimensionBuiltInName(int dimension) { String name = null;
	 * switch (dimension) { case 0 : { name = SBMLUnitTranslator.DIMENSIONLESS;
	 * break; } case 1 : { name = SBMLUnitTranslator.LENGTH; break; } case 2 : {
	 * name = SBMLUnitTranslator.AREA; break; } case 3 : { name =
	 * SBMLUnitTranslator.VOLUME; break; } } return name; }
	 */

	/**
	 * getValueFromRuleOrFunctionDefinition : If the value of a kinetic law
	 * parameter or species initial concentration/amount (or compartment volume)
	 * is 0.0, check if it is given by a rule or functionDefinition, and return
	 * the string (of the rule or functionDefinition expression).
	 */
	private Expression getValueFromAssignmentRule(String paramName) {
		Expression valueExpr = null;
		// Check if param name has an assignment rule associated with it
		int numAssgnRules = assignmentRulesHash.size();
		for (int i = 0; i < numAssgnRules; i++) {
			valueExpr = (Expression) assignmentRulesHash.get(paramName);
			if (valueExpr != null) {
				return new Expression(valueExpr);
			}
		}
		return null;
	}

	/*
	 * pending delete? gcw 4/2014 private boolean varHasRateRule(String
	 * paramName) { // Check if param name has an assignment rule associated
	 * with it int numRateRules = rateRulesHash.size(); for (int i = 0; i <
	 * numRateRules; i++) { Expression valueExpr =
	 * (Expression)rateRulesHash.get(paramName); if (valueExpr != null) { return
	 * true; } } return false; }
	 */
	/**
	 * checkForUnsupportedVCellFeatures:
	 * 
	 * Check if SBML model has algebraic, rate rules, events, other
	 * functionality that VCell does not support, such as:
	 * 'hasOnlySubstanceUnits'; compartments with dimension 0; species that have
	 * assignment rules that contain other species, etc. If so, stop the import
	 * process, since there is no point proceeding with the import any further.
	 * 
	 */
	private void checkForUnsupportedVCellFeaturesAndApplyDefaults() throws Exception {

		// Check if rules, if present, are algrbraic rules
		if (sbmlModel.getNumRules() > 0) {
			for (int i = 0; i < sbmlModel.getNumRules(); i++) {
				Rule rule = (org.sbml.jsbml.Rule) sbmlModel.getRule(i);
				if (rule instanceof AlgebraicRule) {
					logger.sendMessage(VCLogger.Priority.HighPriority,
							VCLogger.ErrorType.UnsupportedConstruct,
							"Algebraic rules are not handled in the Virtual Cell at this time");
				}
			}
		}

		// Check if any of the compartments have spatial dimension 0
		for (int i = 0; i < (int) sbmlModel.getNumCompartments(); i++) {
			Compartment comp = (Compartment) sbmlModel.getCompartment(i);

			if (!comp.isSetSpatialDimensions()) {
				comp.setSpatialDimensions(3); // set default value to 3D
				
				if (level > 2) {
					// level 3+ does not have default value for spatialDimension. So cannot assume a value.
					logger.sendMessage(
							VCLogger.Priority.MediumPriority,
							VCLogger.ErrorType.CompartmentError,
							"Compartment '" + comp.getId() + "' spatial dimension is not set; assuming 3.");
				}
			}
			if (!comp.isSetSize()) {
				comp.setSize(1.0); // set default size to 1.0
				
				if (level > 2) {
					// level 3+ does not have default value for size. So cannot assume a value.
					logger.sendMessage(
							VCLogger.Priority.MediumPriority,
							VCLogger.ErrorType.CompartmentError,
							"Compartment '"	+ comp.getId() + "' size is not set; assuming 1.");
				}
			}
			
			if (comp.getSpatialDimensions() == 0 || comp.getSpatialDimensions() == 1) {
				logger.sendMessage(
						VCLogger.Priority.HighPriority,
						VCLogger.ErrorType.CompartmentError,
						"Compartment " + comp.getId() + " has spatial dimension 0; this is not supported in VCell");
			}
		}

		// if SBML model is spatial and has events, it cannot be imported, since
		// events are not supported in a spatial VCell model.
		if (bSpatial) {
			if (sbmlModel.getNumEvents() > 0) {
				logger.sendMessage(
						VCLogger.Priority.HighPriority,
						VCLogger.ErrorType.UnsupportedConstruct,
						"Events are not supported in a spatial Virtual Cell model at this time, they are only supported in a non-spatial model.");
			}
		}

	}

	/**
	 * translateSBMLModel:
	 *
	 */
	public void translateSBMLModel() {
		// Add Function Definitions (Lambda functions).
		addFunctionDefinitions();
		// Check for SBML features not supported in VCell; stop import process
		// if present.
		try {
			checkForUnsupportedVCellFeaturesAndApplyDefaults();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new SBMLImportException(e.getMessage(), e);
		}

		// Create Virtual Cell Model with species, compartment, etc. and read in
		// the 'values' from the SBML model

		// Add compartmentTypes (not handled in VCell)
		addCompartmentTypes();
		// Add spciesTypes (not handled in VCell)
		addSpeciesTypes();
		// Add Assignment Rules : adding these first, since
		// compartment/species/parameter init condns could be defined by
		// assignment rules
		try {
			addAssignmentRules();
		} catch (SBMLImportException sie) {
			throw sie;
		} catch (Exception ee) {
			ee.printStackTrace(System.out);
			throw new SBMLImportException(ee.getMessage(), ee);
		}
		// Add features/compartments
		VCMetaData vcMetaData = vcBioModel.getVCMetaData();
		addCompartments(vcMetaData);
		// Add species/speciesContexts
		Map<String, String> vcToSbmlNameMap = new HashMap<> ();		// when sbml file uses a vCell reserved symbol as species or reaction name
		Map<String, String> sbmlToVcNameMap = new HashMap<> ();		// happens with x, y, z in the BMDB database
		addSpecies(vcMetaData, vcToSbmlNameMap, sbmlToVcNameMap);
		// Add Parameters
		try {
			addParameters();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new SBMLImportException(e.getMessage(), e);
		}
		// Set initial conditions on species
		setSpeciesInitialConditions(vcToSbmlNameMap);
		// Add InitialAssignments
		addInitialAssignments();
		// Add constraints (not handled in VCell)
		addConstraints();
		// Add Reactions
		addReactions(vcMetaData, vcToSbmlNameMap, sbmlToVcNameMap);
		// Check if we found and renamed successfully any reserved symbols used as species or reaction name
		for(Map.Entry<String, String> entry : sbmlToVcNameMap.entrySet()) {
			String sbmlName = entry.getKey();
			String vcName = entry.getValue();
			localIssueList.add(new Issue(vcBioModel, issueContext, IssueCategory.SBMLImport_ReservedSymbolUsed,
					"Reserved vCell symbol '" + sbmlName + "' found and replaced with '" + vcName + 
					"' during import. Please check for correctness.", Issue.Severity.WARNING));
		}
		
		// Add Rules Rules : adding these later (after assignment rules, since
		// compartment/species/parameter need to be defined before rate rules
		// for those vars can be read in).
		try {
			addRateRules();
		} catch (ExpressionException | SBMLException | XMLStreamException ee) {
			ee.printStackTrace(System.out);
			throw new SBMLImportException(ee.getMessage(), ee);
		}
		// Sort VCell-model Structures in structure array according to reaction
		// adjacency and parentCompartment.
		Structure[] sortedStructures = StructureSorter
				.sortStructures(vcBioModel.getSimulationContext(0).getModel());
		try {
			vcBioModel.getSimulationContext(0).getModel().setStructures(sortedStructures);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace(System.out);
			throw new SBMLImportException("Error while sorting compartments: "
					+ e1.getMessage(), e1);
		}

		// Add Events
		addEvents();
		// Check if names of species, structures, reactions, parameters are long
		// (say, > 64), if so give warning.
		try {
			checkIdentifiersNameLength();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new SBMLImportException(e.getMessage(), e);
		}

		// Add geometry, if sbml model is spatial
		if (bSpatial) {
			addGeometry();
		}
	}

	private void checkIdentifiersNameLength() throws Exception {
		// Check compartment name lengths
		ListOf listofIds = sbmlModel.getListOfCompartments();
		boolean bLongCompartmentName = false;
		SBase issueSource = null;
		for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
			Compartment compartment = (Compartment) listofIds.get(i);
			String compartmentName = compartment.getId();
			if (compartmentName.length() > 64) {
				bLongCompartmentName = true;
				issueSource = compartment;
			}
		}
		// Check species name lengths
		listofIds = sbmlModel.getListOfSpecies();
		boolean bLongSpeciesName = false;
		for (int i = 0; i < sbmlModel.getNumSpecies(); i++) {
			org.sbml.jsbml.Species species = (org.sbml.jsbml.Species) listofIds
					.get(i);
			String speciesName = species.getId();
			if (speciesName.length() > 64) {
				bLongSpeciesName = true;
				issueSource = species;
			}
		}
		// Check parameter name lengths
		listofIds = sbmlModel.getListOfParameters();
		boolean bLongParameterName = false;
		for (int i = 0; i < sbmlModel.getNumParameters(); i++) {
			Parameter param = (Parameter) listofIds.get(i);
			String paramName = param.getId();
			if (paramName.length() > 64) {
				bLongParameterName = true;
				issueSource = param;
			}
		}
		// Check reaction name lengths
		listofIds = sbmlModel.getListOfReactions();
		boolean bLongReactionName = false;
		for (int i = 0; i < sbmlModel.getNumReactions(); i++) {
			Reaction rxn = (Reaction) listofIds.get(i);
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
					warningMsg, Issue.SEVERITY_WARNING));
			// logger.sendMessage(VCLogger.Priority.MediumPriority,
			// VCLogger.ErrorType.UnsupportedConstruct, warningMsg);
		}
	}

	private ArrayList<ReactionParticipant> getVCReactionParticipantsFromSymbol(
			ReactionStep reactionStep, String reactParticipantName) {

		ReactionParticipant rp_Array[] = reactionStep.getReactionParticipants();
		ArrayList<ReactionParticipant> matchingRxnParticipants = new ArrayList<ReactionParticipant>();
		for (int i = 0; i < rp_Array.length; i++) {
			if (AbstractNameScope.getStrippedIdentifier(reactParticipantName)
					.equals(rp_Array[i].getSpeciesContext().getName())) {
				matchingRxnParticipants.add(rp_Array[i]);
			}
		}
		return matchingRxnParticipants;
	}

	/**
	 * addReactions:
	 *
	 */
	protected void addReactions(VCMetaData metaData, Map<String, String> vcToSbmlNameMap, Map<String, String> sbmlToVcNameMap) {
		if (sbmlModel == null) {
			throw new SBMLImportException("SBML model is NULL");
		}
		ListOf<Reaction> reactions = sbmlModel.getListOfReactions();
		final int numReactions = reactions.size(); 
		if (numReactions == 0) {
			lg.info("No Reactions");
			return;
		}
		ArrayList<ReactionStep> vcReactionList = new ArrayList<>(); //all reactions
		ArrayList<ReactionStep> fastReactionList = new ArrayList<>(); //just the fast ones
		Model vcModel = vcBioModel.getSimulationContext(0).getModel();
		ModelUnitSystem vcModelUnitSystem = vcModel.getUnitSystem();
		SpeciesContext[] vcSpeciesContexts = vcModel.getSpeciesContexts();
		try {
			for (Reaction sbmlRxn: reactions) {
				ReactionStep vcReaction = null;
				String rxnName = sbmlRxn.getId();
				ReservedSymbol rs = vcBioModel.getModel().getReservedSymbolByName(rxnName);
				if(rs != null) {
					rxnName = "r_" + rxnName;
					vcToSbmlNameMap.put(rxnName, sbmlRxn.getId());
					sbmlToVcNameMap.put(sbmlRxn.getId(), rxnName);
				}
				
				boolean bReversible = true;
				if (sbmlRxn.isSetReversible()){
					bReversible = sbmlRxn.getReversible();
				}
				// Check of reaction annotation is present; if so, does it have
				// an embedded element (flux or simpleRxn).
				// Create a fluxReaction or simpleReaction accordingly.
				Element sbmlImportRelatedElement = sbmlAnnotationUtil.readVCellSpecificAnnotation(sbmlRxn);
				Structure reactionStructure = getReactionStructure(sbmlRxn, vcSpeciesContexts, sbmlImportRelatedElement);
				if (sbmlImportRelatedElement != null) {
					Element embeddedRxnElement = getEmbeddedElementInAnnotation(
							sbmlImportRelatedElement, REACTION);
					if (embeddedRxnElement != null) {
						if (embeddedRxnElement.getName().equals(
								XMLTags.FluxStepTag)) {
							// If embedded element is a flux reaction, set flux
							// reaction's strucure, flux carrier, physicsOption
							// from the element attributes.
							String structName = embeddedRxnElement
									.getAttributeValue(XMLTags.StructureAttrTag);
							CastInfo<Membrane> ci = SBMLHelper
									.getTypedStructure(Membrane.class, vcModel,
											structName);
							if (!ci.isGood()) {
								throw new SBMLImportException(
										"Appears that the flux reaction is occuring on "
												+ ci.actualName()
												+ ", not a membrane.");
							}
							vcReaction = new FluxReaction(vcModel, ci.get(), null, rxnName, bReversible);
							vcReaction.setModel(vcModel);
							// Set the fluxOption on the flux reaction based on
							// whether it is molecular, molecular & electrical,
							// electrical.
							String fluxOptionStr = embeddedRxnElement
									.getAttributeValue(XMLTags.FluxOptionAttrTag);
							if (fluxOptionStr
									.equals(XMLTags.FluxOptionMolecularOnly)) {
								((FluxReaction) vcReaction)
										.setPhysicsOptions(ReactionStep.PHYSICS_MOLECULAR_ONLY);
							} else if (fluxOptionStr
									.equals(XMLTags.FluxOptionMolecularAndElectrical)) {
								((FluxReaction) vcReaction)
										.setPhysicsOptions(ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL);
							} else if (fluxOptionStr
									.equals(XMLTags.FluxOptionElectricalOnly)) {
								((FluxReaction) vcReaction)
										.setPhysicsOptions(ReactionStep.PHYSICS_ELECTRICAL_ONLY);
							} else {
								localIssueList.add(new Issue(vcReaction,
										issueContext,
										IssueCategory.SBMLImport_Reaction,
										"Unknown FluxOption : " + fluxOptionStr
												+ " for SBML reaction : "
												+ rxnName,
										Issue.SEVERITY_WARNING));
								// logger.sendMessage(VCLogger.Priority.MediumPriority,
								// VCLogger.ErrorType.ReactionError,
								// "Unknown FluxOption : " + fluxOptionStr +
								// " for SBML reaction : " + rxnName);
							}
						} else if (embeddedRxnElement.getName().equals(
								XMLTags.SimpleReactionTag)) {
							// if embedded element is a simple reaction, set
							// simple reaction's structure from element
							// attributes
							vcReaction = new SimpleReaction(vcModel, reactionStructure, rxnName, bReversible);
						}
					} else {
						vcReaction = new SimpleReaction(vcModel, reactionStructure, rxnName, bReversible);
					}
				} else {
					vcReaction = new SimpleReaction(vcModel, reactionStructure, rxnName, bReversible);
				}

				// set annotations and notes on vcReactions[i]
				sbmlAnnotationUtil.readAnnotation(vcReaction, sbmlRxn);
				sbmlAnnotationUtil.readNotes(vcReaction, sbmlRxn);
				// record reaction name in annotation if it is greater than 64
				// characters. Choosing 64, since that is (as of 12/2/08)
				// the limit on the reactionName length.
				if (rxnName.length() > 64) {
					String freeTextAnnotation = metaData
							.getFreeTextAnnotation(vcReaction);
					if (freeTextAnnotation == null) {
						freeTextAnnotation = "";
					}
					StringBuffer oldRxnAnnotation = new StringBuffer(
							freeTextAnnotation);
					oldRxnAnnotation.append("\n\n" + rxnName);
					metaData.setFreeTextAnnotation(vcReaction,
							oldRxnAnnotation.toString());
				}

				// Now add the reactants, products, modifiers as specified by
				// the sbmlRxn
				addReactionParticipants(sbmlRxn, vcReaction, sbmlToVcNameMap);

				KineticLaw kLaw = sbmlRxn.getKineticLaw();
				Kinetics kinetics = null;
				if (kLaw != null) {
					// Convert the formula from kineticLaw into MathML and then
					// to an expression (infix) to be used in VCell kinetics
					ASTNode sbmlRateMath = kLaw.getMath();
					Expression kLawRateExpr = getExpressionFromFormula(sbmlRateMath);
					for(Map.Entry<String, String> entry : sbmlToVcNameMap.entrySet()) {
						String sbmlName = entry.getKey();
						String vcName = entry.getValue();
						kLawRateExpr.substituteInPlace(new Expression(sbmlName), new Expression(vcName));
					}
					Expression vcRateExpression = new Expression(kLawRateExpr);

					// Check the kinetic rate equation for occurances of any
					// species in the model that is not a reaction participant.
					// If there exists any such species, it should be added as a
					// modifier (catalyst) to the reaction.
					for (int k = 0; k < vcSpeciesContexts.length; k++) {
						if (vcRateExpression.hasSymbol(vcSpeciesContexts[k].getName())) {
							if ((vcReaction.getReactant(vcSpeciesContexts[k].getName()) == null)
								&& (vcReaction.getProduct(vcSpeciesContexts[k].getName()) == null)
								&& (vcReaction.getCatalyst(vcSpeciesContexts[k].getName()) == null)) {
								// This means that the speciesContext is not a
								// reactant, product or modifier : it has to be
								// added to the VC Rxn as a catalyst
								vcReaction.addCatalyst(vcSpeciesContexts[k]);
							}
						}
					}

					// set kinetics on VCell reaction
					if (bSpatial) {
						// if spatial SBML ('isSpatial' attribute set), create
						// DistributedKinetics)
						SpatialReactionPlugin ssrplugin = (SpatialReactionPlugin) sbmlRxn.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
						// (a) the requiredElements attributes should be
						// 'spatial'
						if (ssrplugin != null && ssrplugin.getIsLocal()) {
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
					// user, or matches with global/local param,
					// it has to be changed.
					resolveRxnParameterNameConflicts(sbmlRxn, kinetics, sbmlImportRelatedElement);

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
					if (lg.isDebugEnabled()) {
						lg.debug("Setting " + kp.getName() + ":  " + vcRateExpression.infix());
						
					}
					kinetics.setParameterValue(kp, vcRateExpression);

					// If there are any global parameters used in the kinetics,
					// and if they have species,
					// check if the species are already reactionParticipants in
					// the reaction. If not, add them as catalysts.
					KineticsProxyParameter[] kpps = kinetics
							.getProxyParameters();
					for (int j = 0; j < kpps.length; j++) {
						if (kpps[j].getTarget() instanceof ModelParameter) {
							ModelParameter mp = (ModelParameter) kpps[j].getTarget();
							HashSet<String> refSpeciesNameHash = new HashSet<String>();
							getReferencedSpeciesInExpr(mp.getExpression(), refSpeciesNameHash);
							java.util.Iterator<String> refSpIterator = refSpeciesNameHash.iterator();
							while (refSpIterator.hasNext()) {
								String spName = refSpIterator.next();
								org.sbml.jsbml.Species sp = sbmlModel.getSpecies(spName);
								ArrayList<ReactionParticipant> rpArray = getVCReactionParticipantsFromSymbol(
										vcReaction, sp.getId());
								if (rpArray == null || rpArray.size() == 0) {
									// This means that the speciesContext is not
									// a reactant, product or modifier : it has
									// to be added as a catalyst
									vcReaction.addCatalyst(vcModel .getSpeciesContext(sp.getId()));
								}
							}
						}
					}

					// Introduce all remaining local parameters from the SBML
					// model - local params cannot be defined by rules.
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
							if (lg.isDebugEnabled()) {
								lg.debug("Setting local " + kineticsParameter.getName() + ":  " + exp.infix());
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
							// speciesContext or model parameter (structureSize
							// too)).
							KineticsProxyParameter kpp = kinetics.getProxyParameter(paramName);
							// if there is a proxy param with same name as sbml
							// kinetic local param, if proxy param
							// is a model global parameter, change proxy param
							// to local, set its value
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
					// sbmlKLaw was null, so creating a GeneralKinetics with 0.0
					// as rate.
					kinetics = new GeneralKinetics(vcReaction);
				} // end - if-else KLaw != null

				// set the reaction kinetics, and add reaction to the vcell
				// model.
				kinetics.resolveUndefinedUnits();
				// System.out.println("ADDED SBML REACTION : \"" + rxnName +
				// "\" to VCModel");
				vcReactionList.add(vcReaction);
				if (sbmlRxn.isSetFast() && sbmlRxn.getFast()) {
					fastReactionList.add(vcReaction);
				}
			} // end - for vcReactions

			ReactionStep[] array = vcReactionList.toArray(new ReactionStep[vcReactionList.size()]);
			vcModel.setReactionSteps( array); 
			
			final ReactionContext rc = vcBioModel.getSimulationContext(0).getReactionContext();
			for (ReactionStep frs: fastReactionList) {
					final ReactionSpec rs = rc.getReactionSpec(frs);
					rs.setReactionMapping(ReactionSpec.FAST);
			}

		} catch (ModelPropertyVetoException mpve) {
			throw new SBMLImportException(mpve.getMessage(), mpve);
		} catch (Exception e1) {
			e1.printStackTrace(System.out);
			throw new SBMLImportException(e1.getMessage(), e1);
		}

	}

	public static cbit.vcell.geometry.CSGNode getVCellCSGNode(org.sbml.jsbml.ext.spatial.CSGNode sbmlCSGNode) {
		String csgNodeName = sbmlCSGNode.getId();
		if (sbmlCSGNode instanceof org.sbml.jsbml.ext.spatial.CSGPrimitive){
			PrimitiveKind primitiveKind = ((org.sbml.jsbml.ext.spatial.CSGPrimitive) sbmlCSGNode).getPrimitiveType();
			cbit.vcell.geometry.CSGPrimitive.PrimitiveType vcellCSGPrimitiveType = getVCellPrimitiveType(primitiveKind);
			cbit.vcell.geometry.CSGPrimitive vcellPrimitive = new cbit.vcell.geometry.CSGPrimitive(csgNodeName, vcellCSGPrimitiveType);
			return vcellPrimitive;
		} else if (sbmlCSGNode instanceof CSGPseudoPrimitive){
			throw new RuntimeException("Pseudo primitives not yet supported in CSGeometry.");
		} else if (sbmlCSGNode instanceof CSGSetOperator) {
			org.sbml.jsbml.ext.spatial.CSGSetOperator sbmlSetOperator = (org.sbml.jsbml.ext.spatial.CSGSetOperator) sbmlCSGNode;
			OperatorType opType = null;
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
						sbmlRotation.getRotateAxisX(),
						sbmlRotation.getRotateAxisY(), 
						sbmlRotation.getRotateAxisZ());
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
		cbit.vcell.geometry.CSGPrimitive.PrimitiveType vcellCSGPrimitiveType = null;
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
	private org.sbml.jsbml.ext.spatial.Geometry getSbmlGeometry() {
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
		org.sbml.jsbml.ext.spatial.Geometry geometry = mplugin.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("SBML Geometry not found");
		}

		if (geometry.getListOfGeometryDefinitions().size() < 1) {
			// (2/15/2013) For now, allow model to be imported even without
			// geometry defined. Issue a warning.
			localIssueList.add(new Issue(new SBMLIssueSource(sbmlModel),
					issueContext,
					IssueCategory.SBMLImport_UnsupportedAttributeOrElement,
					"Geometry not defined in spatial model.",
					Issue.SEVERITY_WARNING));
			return null;
			// throw new
			// RuntimeException("SBML model does not have any geometryDefinition. Cannot proceed with import.");
		}
		return geometry;

	}

	protected void addGeometry() {
		// get a Geometry object via SpatialModelPlugin object.
		org.sbml.jsbml.ext.spatial.Geometry sbmlGeometry = getSbmlGeometry();
		if (sbmlGeometry == null) {
			return;
		}

		int dimension = 0;
		Origin vcOrigin = null;
		Extent vcExtent = null;
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
				switch (coordComponent.getType()){
				case cartesianX:{
					ox = minValue;
					ex = maxValue - minValue;
					break;
				}
				case cartesianY:{
					oy = minValue;
					ey = maxValue - minValue;
					break;
				}
				case cartesianZ:{
					oz = minValue;
					ez = maxValue - minValue;
					break;
				}
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
				String sfn = sfg.getSampledField();
				ListOf<SampledField> sampledFields = sbmlGeometry.getListOfSampledFields();
				if (sampledFields.size() > 1){
					throw new RuntimeException("only one sampled field supported");
				}
				InterpolationKind ik = sampledFields.get(0).getInterpolationType();
				switch (ik) {
				case linear:
					distanceMapSampledFieldGeometry = sfg;
					break;
				case nearestneighbor:
					segmentedSampledFieldGeometry = sfg;
					break;
				default:
					lg.warn("Unsupported " + sampledFields.get(0).getName() + " interpolation type " + ik);
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
		GeometryDefinition selectedGeometryDefinition = null;
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
			StringTokenizer tokens = new StringTokenizer(sf.getSamples()," ");
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
				//System.out.println("ident " + sf.getId() + " " + sf.getName());
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
				e.printStackTrace(System.out);
				throw new RuntimeException(
						"Unable to create image from SampledFieldGeometry : "
								+ e.getMessage());
			}
		}
		GeometrySpec vcGeometrySpec = vcGeometry.getGeometrySpec();
		vcGeometrySpec.setOrigin(vcOrigin);
		try {
			vcGeometrySpec.setExtent(vcExtent);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
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
		Vector<DomainType> surfaceClassDomainTypesVector = new Vector<DomainType>();
		try {
			for (DomainType dt : listOfDomainTypes) {
				if (dt.getSpatialDimensions() == 3) {
					// subvolume
					if (selectedGeometryDefinition == analyticGeometryDefinition) {
						// will set expression later - when reading in Analytic
						// Volumes in GeometryDefinition
						vcGeometrySpec.addSubVolume(new AnalyticSubVolume(dt.getId(), new Expression(1.0)));
					} else {
						// add SubVolumes later for CSG and Image-based
					}
				} else if (dt.getSpatialDimensions() == 2) {
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
						
						Expression subVolExpr = getExpressionFromFormula(analyticVol.getMath());
						asv.setExpression(subVolExpr);
					} catch (ExpressionException e) {
						e.printStackTrace(System.out);
						throw new SBMLImportException(
								"Unable to set expression on subVolume '"
										+ asv.getName() + "'. "
										+ e.getMessage(), e);
					}
				}
			}
			SampledFieldGeometry sfg = BeanUtils.downcast(SampledFieldGeometry.class, selectedGeometryDefinition);
			if (sfg != null) {
				ListOf<SampledVolume> sampledVolumes = sfg.getListOfSampledVolumes();
				
				int numSampledVols = sampledVolumes.size(); 
				if (numSampledVols == 0) {
					throw new SBMLImportException("Cannot have 0 sampled volumes in sampledField (image_based) geometry");
				}
				VCPixelClass[] vcpixelClasses = new VCPixelClass[numSampledVols];
				ImageSubVolume vcImageSubVols[] = new ImageSubVolume[numSampledVols];
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

				ArrayList<org.sbml.jsbml.ext.spatial.CSGObject> sbmlCSGs = new ArrayList<org.sbml.jsbml.ext.spatial.CSGObject>(listOfcsgObjs);
				// we want the CSGObj with highest ordinal to be the first
				// element in the CSG subvols array.
				Collections.sort(sbmlCSGs,
						new Comparator<org.sbml.jsbml.ext.spatial.CSGObject>() {
							@Override
							public int compare(org.sbml.jsbml.ext.spatial.CSGObject lhs,
									org.sbml.jsbml.ext.spatial.CSGObject rhs) {
								// minus one to reverse sort
								return -1
										* Integer.compare(lhs.getOrdinal(),
												rhs.getOrdinal());
							}
						});

				int n = sbmlCSGs.size();
				CSGObject vcCSGSubVolumes[] = new CSGObject[n];
				for (int i = 0; i < n; i++) {
					org.sbml.jsbml.ext.spatial.CSGObject sbmlCSGObject = sbmlCSGs.get(i);
					CSGObject vcellCSGObject = new CSGObject(null,
							sbmlCSGObject.getDomainType(), i);
					vcellCSGObject.setRoot(getVCellCSGNode(sbmlCSGObject.getCSGNode()));
				}

				vcGeometry.getGeometrySpec().setSubVolumes(vcCSGSubVolumes);
			}

			// Call geom.geomSurfDesc.updateAll() to automatically generate
			// surface classes.
			// vcGsd.updateAll();
			vcGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(),
					true, true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
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
				if (currDomainType.getSpatialDimensions() == 2){
					continue;
				}
			}
			Coordinate sbmlInteriorPtCoord = new Coordinate(
					interiorPt.getCoord1(), interiorPt.getCoord2(),
					interiorPt.getCoord3());
			for (int j = 0; j < vcGeomRegions.length; j++) {
				if (vcGeomRegions[j] instanceof VolumeGeometricRegion) {
					int regionID = ((VolumeGeometricRegion) vcGeomRegions[j])
							.getRegionID();
					for (int k = 0; k < regionInfos.length; k++) {
						// get the regionInfo corresponding to the vcGeomRegion
						// (using gemoRegion regionID).
						if (regionInfos[k].getRegionIndex() == regionID) {
							int volIndx = 0;
							Coordinate nearestPtCoord = null;
							double minDistance = Double.MAX_VALUE;
							// for each point in the region, find it if is close
							// to 'sbmlInteriorPt'. If it is, this is the region
							// represented by SBML 'domain[i]'.
							for (int z = 0; z < numZ; z++) {
								for (int y = 0; y < numY; y++) {
									for (int x = 0; x < numX; x++) {
										if (regionInfos[k]
												.isIndexInRegion(volIndx)) {
											double unit_z = (numZ > 1) ? ((double) z)
													/ (numZ - 1)
													: 0.5;
											double coordZ = oz
													+ vcExtent.getZ() * unit_z;
											double unit_y = (numY > 1) ? ((double) y)
													/ (numY - 1)
													: 0.5;
											double coordY = oy
													+ vcExtent.getY() * unit_y;
											double unit_x = (numX > 1) ? ((double) x)
													/ (numX - 1)
													: 0.5;
											double coordX = ox
													+ vcExtent.getX() * unit_x;
											// for now, find the shortest dist
											// coord. Can refine algo later.
											Coordinate vcCoord = new Coordinate(
													coordX, coordY, coordZ);
											double distance = sbmlInteriorPtCoord
													.distanceTo(vcCoord);
											if (distance < minDistance) {
												minDistance = distance;
												nearestPtCoord = vcCoord;
											}
										}
										volIndx++;
									} // end - for x
								} // end - for y
							} // end - for z
								// verify that domainType of domain and
								// geomClass of
								// geomRegion are the same; if so, name
								// vcGeomReg[j]
								// with domain name
							if (nearestPtCoord != null) {
								GeometryClass geomClassSBML = vcGeometry
										.getGeometryClass(domainType);
								// we know vcGeometryReg[j] is a VolGeomRegion
								GeometryClass geomClassVC = ((VolumeGeometricRegion) vcGeomRegions[j])
										.getSubVolume();
								if (geomClassSBML.compareEqual(geomClassVC)) {
									vcGeomRegions[j].setName(domain.getId());
								}
							}
						} // end if (regInfoIndx = regId)
					} // end - for regInfo
				}
			} // end for - vcGeomRegions
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
					Vector<SubVolume> adjacentSubVolumesVector = new Vector<SubVolume>();
					Vector<VolumeGeometricRegion> adjVolGeomRegionsVector = new Vector<VolumeGeometricRegion>();
					Iterator<Domain> iterator = adjacentDomainsSet.iterator();
					while (iterator.hasNext()) {
						Domain dom = iterator.next();
						DomainType dt = getBySpatialID(sbmlGeometry.getListOfDomainTypes(),dom.getDomainType());
						if (dt.getSpatialDimensions() == 3) {
							// for domain type with sp. dim = 3, get
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

			Vector<StructureMapping> structMappingsVector = new Vector<StructureMapping>();
			SpatialCompartmentPlugin cplugin = null;
			for (int i = 0; i < sbmlModel.getNumCompartments(); i++) {
				Compartment c = sbmlModel.getCompartment(i);
				String cname = c.getName();
				cplugin = (SpatialCompartmentPlugin) c.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
				CompartmentMapping compMapping = cplugin.getCompartmentMapping();
				if (compMapping != null) {
					//final String id = compMapping.getId();
					//final String name = compMapping.getName();
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
							} else if (geometryClass instanceof SurfaceClass) {
								featureMapping.getVolumePerUnitAreaParameter().setExpression(new Expression(unitSize));
							}
							structMappingsVector.add(featureMapping);
						} else if (struct instanceof Membrane) {
							MembraneMapping membraneMapping = new MembraneMapping( (Membrane) struct, vcBioModel.getSimulationContext(0), vcModelUnitSystem); membraneMapping.setGeometryClass(geometryClass);
							if (geometryClass instanceof SubVolume) {
								membraneMapping.getAreaPerUnitVolumeParameter().setExpression(new Expression(unitSize));
							} else if (geometryClass instanceof SurfaceClass) {
								membraneMapping.getAreaPerUnitAreaParameter().setExpression(new Expression(unitSize));
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

			// if type from SBML parameter Boundary Condn is not the same as the
			// boundary type of the
			// structureMapping of structure of paramSpContext, set the boundary
			// condn type of the structureMapping
			// to the value of 'type' from SBML parameter Boundary Condn.
			ListOf<Parameter> listOfGlobalParams = sbmlModel.getListOfParameters();

			for (Parameter sbmlGlobalParam : sbmlModel.getListOfParameters()){
				SpatialParameterPlugin spplugin = (SpatialParameterPlugin) sbmlGlobalParam.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
				ParameterType paramType = spplugin.getParamType();
				if (!(paramType instanceof BoundaryCondition)){
					continue;
				}
				BoundaryCondition bCondn = (BoundaryCondition)paramType;

				if (bCondn.isSetVariable()) {
					// get the var of boundaryCondn; find appropriate spContext
					// in vcell;
					SpeciesContext paramSpContext = vcBioModel.getSimulationContext(0).getModel()
							.getSpeciesContext(bCondn.getVariable());
					if (paramSpContext != null) {
						Structure s = paramSpContext.getStructure();
						StructureMapping sm = vcBioModel.getSimulationContext(0).getGeometryContext()
								.getStructureMapping(s);
						if (sm != null) {
							BoundaryConditionType bct = null;
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
							
							for (CoordinateComponent coordComp : getSbmlGeometry().getListOfCoordinateComponents()){
								if (bCondn.getSpatialRef().equals(coordComp.getBoundaryMinimum().getSpatialId())){
									switch (coordComp.getType()){
									case cartesianX:{
										sm.setBoundaryConditionTypeXm(bct);
									}
									case cartesianY:{
										sm.setBoundaryConditionTypeYm(bct);
									}
									case cartesianZ:{
										sm.setBoundaryConditionTypeZm(bct);
									}
									}
								}
								if (bCondn.getSpatialRef().equals(coordComp.getBoundaryMaximum().getSpatialId())){
									switch (coordComp.getType()){
									case cartesianX:{
										sm.setBoundaryConditionTypeXm(bct);
									}
									case cartesianY:{
										sm.setBoundaryConditionTypeYm(bct);
									}
									case cartesianZ:{
										sm.setBoundaryConditionTypeZm(bct);
									}
									}
								}
							}
						} // sm != null
						else {
							logger.sendMessage(
									VCLogger.Priority.MediumPriority,
									VCLogger.ErrorType.OverallWarning,
									"No structure " + s.getName()
											+ " requested by species context "
											+ paramSpContext.getName());
						}
					} // end if (paramSpContext != null)
				} // end if (bCondn.isSetVar())
			} // end for (sbmlModel.numParams)

			vcBioModel.getSimulationContext(0).getGeometryContext().refreshStructureMappings();
			vcBioModel.getSimulationContext(0).refreshSpatialObjects();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new SBMLImportException(
					"Unable to create VC structureMappings from SBML compartment mappings : "
							+ e.getMessage(), e);
		}
	}
	
	/**
	 * check / find scaling factor for pixel values  in sampledVolumes (double) to map onto integers without duplicates
	 * @param sampledVolumes not null
	 * @param scaleFactor not null
	 * @return scaleFactor if it works, different scale factor if possible
	 * @throws RuntimeException if no easily calculatable scale factor present
	 */
	private double checkPixelScaling(Collection<SampledVolume >sampledVolumes, double scaleFactor) {
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

	private SurfaceGeometricRegion getAssociatedSurfaceGeometricRegion(
			GeometrySurfaceDescription vcGsd,
			Vector<VolumeGeometricRegion> volGeomRegionsVector) {
		GeometricRegion[] geomeRegions = vcGsd.getGeometricRegions();
		// adjVolGeomRegionsVector should have only 2 elements - the 2 adj
		// volGeomRegions for any surfaceRegion.
		VolumeGeometricRegion[] volGeomRegionsArray = volGeomRegionsVector
				.toArray(new VolumeGeometricRegion[0]);
		for (int i = 0; i < geomeRegions.length; i++) {
			if (geomeRegions[i] instanceof SurfaceGeometricRegion) {
				SurfaceGeometricRegion surfaceRegion = (SurfaceGeometricRegion) geomeRegions[i];
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
	private Set<Domain> getAssociatedAdjacentDomains(
			org.sbml.jsbml.ext.spatial.Geometry sbmlGeom, Domain d) {
		Set<Domain> adjacentDomainsSet = new HashSet<Domain>();
		for (AdjacentDomains adjDomains : sbmlGeom.getListOfAdjacentDomains()){
			Domain domain1 = getBySpatialID(sbmlGeom.getListOfDomains(),adjDomains.getDomain1());
			Domain domain2 = getBySpatialID(sbmlGeom.getListOfDomains(),adjDomains.getDomain2());
			if (adjDomains.getDomain1().equals(d.getId())){
				if (!adjacentDomainsSet.contains(domain2)) {
					adjacentDomainsSet.add(domain2);
				}
			}
			if (adjDomains.getDomain2().equals(d.getId())) {
				if (domain1 != null && !adjacentDomainsSet.contains(domain1)) {
					adjacentDomainsSet.add(domain1);
				}
			}
		}
		return adjacentDomainsSet;
	}
	
	private <T extends SpatialNamedSBase> T getBySpatialID(ListOf<T> list, String spatialId) {
		for (T t : list){
			if (t.getSpatialId().equals(spatialId)){
				return t;
			}
		}
		return null;
	}

}
