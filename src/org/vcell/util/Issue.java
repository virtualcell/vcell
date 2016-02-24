/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.util.List;
import java.util.Objects;

import org.sbml.libsbml.SBase;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.GeometryContext.UnmappedGeometryClass;
import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.ReactionSpec.ReactionCombo;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.math.Equation;
import cbit.vcell.math.Event;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.opt.ObjectiveFunction;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.OutputFunctionContext.OutputFunctionIssueSource;
import cbit.vcell.solver.Simulation;



/**
 * Insert the type's description here.
 * Creation date: (4/1/2004 10:12:47 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class Issue implements java.io.Serializable, Matchable {

	private java.util.Date date = new java.util.Date();
	private String message = null;
	private String tooltip = null;
	private IssueCategory category = null;
	private Object source = null;
	private IssueContext issueContext = null;
	private int severity = -1;
	private String hyperlink;

	public static final int SEVERITY_INFO = 0;
	public static final int SEVERITY_TIP = 1;
	public static final int SEVERITY_BUILTIN_CONSTRAINT = 2;
	public static final int SEVERITY_WARNING = 3;
	public static final int SEVERITY_ERROR = 4;
	private static final int MAX_SEVERITY = 4;

	private final static String severityName[] = { "info", "tip", "constraint", "warning", "error" };

	public enum IssueCategory {
		Units,
		Identifiers,
		CopyPaste,
		UnresolvedParameter,
		KineticsUnreferencedParameter,
		KineticsApplicability,
		CyclicDependency,
		KineticsExpressionError,
		KineticsExpressionMissing,
		KineticsExpressionNonParticipantSymbol,
		KineticsExpressionUndefinedSymbol,
		StructureMappingSizeParameterNotPositive,
		StructureMappingSizeParameterNotConstant,
		StructureMappingSizeParameterNotSet,
		StructureNotMapped,
		GeometryClassNotMapped,
		SubVolumeVerificationError,

		ModelParameterExpressionError,

		InternalError,
		ParameterEstimationGeneralWarning,
		ParameterBoundsDefinition,
		ParameterEstimationBoundsError,
		ParameterEstimationBoundsViolation,
		ParameterEstimationNoParameterSelected,
		ParameterEstimationRefereceDataNoTime,
		ParameterEstimationRefereceDataNotMapped,
		ParameterEstimationRefereceDataMappedImproperly,

		MathDescription_NoGeometry,
		MathDescription_Constant_NotANumber,
		MathDescription_ExpressionBindingException,
		MathDescription_ExpressionException,
		MathDescription_MathException,
		MathDescription_CompartmentalModel,
		MathDescription_SpatialModel,
		MathDescription_SpatialModel_Subdomain,
		MathDescription_SpatialModel_Geometry,
		MathDescription_SpatialModel_Equation,
		MathDescription_SpatialModel_Variable,
		MathDescription_SpatialModel_Event,
		MathDescription_SpatialModel_PostProcessingBlock,
		MathDescription_StochasticModel,

		Simulation_Override_NotFound,
		Simulation_Override_NotSupported,
		Simulation_SensAnal_And_FastSystem,

		OUTPUTFUNCTIONCONTEXT_FUNCTION_EXPBINDING,
		Smoldyn_Geometry_3DWarning,

		Microscope_Measurement_ProjectionZKernel_Geometry_3DWarning,

		MembraneElectricalPolarityError,

		SBMLImport_UnsupportedAttributeOrElement,
		SBMLImport_Reaction,
		SBMLImport_MissingSpeciesInitCondition,
		SMOLYDN_DIFFUSION
	}


	//
	// Physiology - to be handled by BioModels
	//
	public Issue(Model argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(SpeciesContext argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(KineticsParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(ModelParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(UnresolvedParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(ReactionStep argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this((Object)argSource, issueContext, argCategory, argMessage, null, argSeverity);

	}
	public Issue(ReactionStep argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String tooltip,int argSeverity) {
		this((Object)argSource, issueContext, argCategory, argMessage, tooltip, argSeverity);
	}
	public Issue(Structure argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}



	//
	// Applications - to be handled by BioModels
	//
	public Issue(GeometryContext argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(UnmappedGeometryClass argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(SpeciesContextSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(SpeciesContextSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
		this((Object)argSource, issueContext, argCategory, argMessage, argTooltip, argSeverity);
	}
	public Issue(SpeciesContextSpecParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this((Object)argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(StructureMapping argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(StructureMappingParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(MicroscopeMeasurement argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(ReactionCombo argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
		this((Object)argSource, issueContext, argCategory, argMessage, argTooltip, argSeverity);
	}


	//
	// ParameterEstimation / Optimization - to be handled by BioModels (and Maybe VirtualFRAP)
	//
	public Issue(ModelOptimizationSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(ObjectiveFunction argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(OptimizationSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(ReferenceDataMappingSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(cbit.vcell.opt.Parameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}


	//
	// Geometry - to be handled by BioModels and MathModels
	//
	public Issue(Geometry argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}


	//
	// MathDescription - to be handled by BioModels and MathModels
	//
	public Issue(MathDescription argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(Event argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(SubDomain argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(Variable argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(Equation argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}


	//
	// Simulation - to be handled by BioModels and MathModels
	//
	public Issue(Simulation argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}
	public Issue(OutputFunctionIssueSource argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}


	//
	// too low level ... handle this soon
	//
	public Issue(Expression argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}


	//
	// SBML Importing
	//
	public Issue(SBase argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
		this((Object)argSource, issueContext, argCategory, argMessage, argTooltip, argSeverity);
	}
	public Issue(SBase argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
		this((Object)argSource, issueContext, argCategory, argMessage, null, argSeverity);
	}

/**
 * Private constructor, not type safe
 */
private Issue(Object argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
	super();
	if (argSeverity<0 || argSeverity>MAX_SEVERITY){
		throw new IllegalArgumentException("unexpected severity="+argSeverity);
	}
	this.source = argSource;
	this.issueContext = issueContext;
	this.message = argMessage;
	this.tooltip = argTooltip;
	this.category = argCategory;
	this.severity = argSeverity;
	this.hyperlink = null;
}

/**
 * @return hyperlink for more information may be null
 */
public String getHyperlink() {
	return hyperlink;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof Issue){
		Issue other = (Issue)obj;
		if (other.source!=this.source){
			return false;
		}
		if (!other.category.equals(category)){
			return false;
		}
		if (!other.message.equals(message)){
			return false;
		}
		if (!other.tooltip.equals(tooltip)){
			return false;
		}
		if (other.severity != severity){
			return false;
		}
		return true;
	}else{
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 11:11:46 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equal(Object object) {
	if (this == object){
		return true;
	}
	if (object instanceof Issue){
		Issue otherIssue = (Issue)object;
		if (!source.equals(otherIssue.source)){
			return false;
		}
		if (!category.equals(otherIssue.category)){
			return false;
		}
		if (!message.equals(otherIssue.message)){
			return false;
		}
		if (!tooltip.equals(otherIssue.tooltip)){
			return false;
		}
		if (severity != otherIssue.severity){
			return false;
		}
		//if (!date.equals(otherIssue.date)){
			//return false;
		//}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 11:11:07 AM)
 * @return java.lang.String
 */
public IssueCategory getCategory() {
	return category;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return java.util.Date
 */
public java.util.Date getDate() {
	return date;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return java.lang.String
 */
public java.lang.String getMessage() {
	return message;
}
public java.lang.String getTooltip() {
	return tooltip;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return int
 */
public int getSeverity() {
	return severity;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2004 8:37:29 AM)
 * @return java.lang.String
 */
public String getSeverityName() {
	return severityName[severity];
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return cbit.util.IssueSource
 */
public Object getSource() {
	return source;
}

public IssueContext getIssueContext() {
	return issueContext;
}

/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 11:17:07 AM)
 * @return int
 */
public int hashCode() {
	return source.hashCode()+category.hashCode()+message.hashCode()+severity;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2004 8:33:57 AM)
 * @return java.lang.String
 */
public String toString() {
	//return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ": "+getSeverityName()+", "+getCategory()+", '"+getMessage()+"', source="+getSource();
	return getSeverityName()+": '"+getMessage()+"'";
}


public static String getHtmlIssueMessage(List<Issue> issueList) {
	if (issueList == null || issueList.size() == 0) {
		return null;
	}
	StringBuilder sb = new StringBuilder();
	sb.append("<html>");
	for (Issue issue : issueList) {
		if(issue.getTooltip() == null || issue.getTooltip().isEmpty()) {
			sb.append("<li>" + issue.getMessage() + "</li>");
		} else {
			sb.append("<li>" + issue.getTooltip() + "</li>");
		}
	}
	sb.append("</html>");
	return sb.toString();
}
/**
 * @param string URL user can use to get more information (not null)
 */
public void setHyperlink(String url) {
	Objects.requireNonNull(url);
	hyperlink = url;
}
}
