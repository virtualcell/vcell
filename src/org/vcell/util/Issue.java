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
	private int severity = -1;

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
		Simulation_SensAnal_And_FastSystem,
		
		OUTPUTFUNCTIONCONTEXT_FUNCTION_EXPBINDING,
		Smoldyn_Geometry_3DWarning,
		
		Microscope_Measurement_ProjectionZKernel_Geometry_3DWarning,
		
		MembraneElectricalPolarityError,
		
		SBMLImport_UnsupportedAttributeOrElement, 
		SBMLImport_Reaction, 
		SBMLImport_MissingSpeciesInitCondition
	}

	//
	// categories
	//
	//public static final int CATEGORY_KineticsApplicability		= 0;
	//public static final int CATEGORY_ParameterLoop				= 1;
	//public static final int CATEGORY_InconsistentUnits			= 2;

/**
 * AbstractIssue constructor comment.
 */
public Issue(Object argSource, IssueCategory argCategory, String argMessage, int argSeverity) {
	this(argSource, argCategory, argMessage, null, argSeverity);
}
public Issue(Object argSource, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
	super();
	if (argSeverity<0 || argSeverity>MAX_SEVERITY){
		throw new IllegalArgumentException("unexpected severity="+argSeverity);
	}
	this.source = argSource;
	this.message = argMessage;
	this.tooltip = argTooltip;
	this.category = argCategory;
	this.severity = argSeverity;
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
}
