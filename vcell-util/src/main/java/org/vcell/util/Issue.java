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

import org.apache.commons.lang3.StringUtils;



/**
 * Insert the type's description here.
 * Creation date: (4/1/2004 10:12:47 AM)
 * @author: Jim Schaff
 */

//
// TODO: HOW TO USE
// implement gatherIssues() for the object that has issues (ex: BioEvent)
// populate the 'Source' and 'Defined In' columns in the IssueTableModel
// implement the followHyperlink in the IssuePanel to allow navigation to where the problem can be fixed
// modify the renderer to show the error / warning icon
// show the error / warning icon next to the wrong object (ex event table in Protocols / Events panel)
//    should invoke issueRenderer() in DefaultScrollTableCellRenderer - debug here as follows:
//    check if it's being called, if not check class hierarchy
//    if getIssues() not working check if issueManager is being transmitted all the way down to the 
//       model (ex EventsSummaryTableModel), with setIssueManager()
//

@SuppressWarnings("serial")
public class Issue implements java.io.Serializable, Matchable {

	private java.util.Date date = new java.util.Date();
	private String message = null;
	private String tooltip = null;
	private IssueCategory category = null;
	private IssueSource source = null;
	private IssueSource detailedSource = null;
	private IssueContext issueContext = null;
	private Severity severity = Severity.INFO;
	private String hyperlink;
	public enum Severity {
		INFO("info"),
		TIP("tip"),
		BUILTIN_CONSTRAINT("constraint"),
		WARNING("warning"),
		ERROR("error");


		/**
		 * display name / toString return
		 */
		final String name;

		private Severity(String name) {
			this.name = name;
		}

		/**
		 * @return {@link #name()}
		 */
		public String toString( ) {
			return name;
		}
	}



	/**
	 * use {@link Severity#INFO}
	 */
	@Deprecated
	public static final Severity SEVERITY_INFO = Severity.INFO;
	/**
	 * use {@link Severity#TIP}
	 */
	@Deprecated
	public static final Severity   SEVERITY_TIP = Severity.TIP;
	/**
	 * use {@link Severity#BUILTIN_CONSTRAINT}
	 */
	@Deprecated
	public static final Severity  SEVERITY_BUILTIN_CONSTRAINT = Severity.BUILTIN_CONSTRAINT;
	/**
	 * use {@link Severity#WARNING}
	 */
	@Deprecated
	public static final Severity  SEVERITY_WARNING = Severity.WARNING;
	/**
	 * @deprecated
	 * use {@link Severity#ERROR}
	 */
	@Deprecated
	public static final  Severity SEVERITY_ERROR = Severity.ERROR;


	public interface IssueSource {

	}

	public interface IssueOrigin extends IssueSource {
		/**
		 * end user description of source
		 * @return non-null String
		 */
		public String getDescription( );
	}

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

		Workflow_missingInput,

		RbmMolecularTypesTableBad,
		RbmReactionRulesTableBad,
		RbmObservablesTableBad,
		RbmNetworkConstraintsBad,
		SMOLYDN_DIFFUSION,
	}

	/**
	 * @param argTooltip may be null; HTML formatted
	 */
	public Issue(IssueSource argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, Severity argSeverity) {
		this(argSource, null, issueContext, argCategory, argMessage, null, argSeverity);
	}
	/**
	 * @param argTooltip may be null; HTML formatted
	 */
	public Issue(IssueSource argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, Severity argSeverity) {
		this(argSource, null, issueContext, argCategory, argMessage, argTooltip, argSeverity);
	}

	/**
	 * @param argTooltip may be null; HTML formatted
	 */
	public Issue(IssueSource argSource, IssueSource argDetailedSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, Severity argSeverity) {
		super();
		this.source = argSource;
		this.detailedSource = argDetailedSource;
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
		if (other.detailedSource!=this.detailedSource){
			return false;
		}
		if (!other.category.equals(category)){
			return false;
		}
		if (!Compare.isEqual(other.message,message)){
			return false;
		}
		if (!Compare.isEqualOrNull(other.tooltip,tooltip)){
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
		if (!detailedSource.equals(otherIssue.detailedSource)){
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
 *
 * @return {@link #getTooltip()} wrapped in html tags if not blank; otherwise return {@link #getTooltip()}
 */
public String getHtmlTooltip() {
	if (!StringUtils.isBlank(tooltip)) {
		return "<html>" + tooltip + "</html>";
	}
	return tooltip;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return int
 */
public Severity getSeverity() {
	return severity;
}


/**
 * use {@link Severity#toString()}
 * @return {@link #severity#} {@link #toString()}
 */
@Deprecated
public String getSeverityName() {
	return severity.toString();
}

/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return cbit.util.IssueSource
 */
public IssueSource getSource() {
	return source;
}
public IssueSource getDetailedSource() {
	return detailedSource;
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
	return source.hashCode()^category.hashCode()^message.hashCode()^severity.hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2004 8:33:57 AM)
 * @return java.lang.String
 */
public String toString() {
	//return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ": "+getSeverityName()+", "+getCategory()+", '"+getMessage()+"', source="+getSource();
	return severity + ": '"+getMessage()+"'";
}


public static String getHtmlIssueMessage(List<Issue> issueList) {
	final int LIMIT = 600;
	int size = 0;
	String s = null;
	String slist = null;

	if (issueList == null || issueList.size() == 0) {
		return null;
	}
	slist = "<html>";
	for (Issue issue : issueList) {
		if(issue.getTooltip() == null || issue.getTooltip().isEmpty()) {
			s = issue.getMessage();
		} else {
			s = issue.getTooltip();
		}

		if(s.length() > LIMIT-9) {		// each list entry must be shorter than the limit
			s = s.substring(0, LIMIT-13);
			s += " ...";
		}
		s = "<li>" + s + "</li>";
		size += s.length();				// the list itself must be shorter than the limit
		if(size > LIMIT) {
			s = "<li>text</li>";
			slist += s;
			break;
		} else {
			slist += s;
		}
	}
	slist += "</html>";
	return slist;
}
/**
 * @param string URL user can use to get more information (not null)
 */
public void setHyperlink(String url) {
	Objects.requireNonNull(url);
	hyperlink = url;
}
}
