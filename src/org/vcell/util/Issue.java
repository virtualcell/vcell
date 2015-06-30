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

import org.apache.commons.lang3.StringUtils;



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
	private Object source2 = null;
	private IssueContext issueContext = null;
	private Severity severity = Severity.INFO; 
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
	 * use {@link Severity#ERROR}
	 */
	@Deprecated
	public static final  Severity SEVERITY_ERROR = Severity.ERROR;  
	

	public interface IssueSource {
		
	}
	
	public interface IssueOrigin extends IssueSource {
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
	}


	//
	// Physiology - to be handled by BioModels
	//
//	public Issue(Model argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(SpeciesContext argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(KineticsParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(ModelParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(UnresolvedParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(ReactionStep argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(Structure argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(ComponentStateDefinition argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(RbmObservable argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(RbmModelContainer argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(ReactionRule argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(ProductPattern argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(RbmKineticLaw argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(MolecularComponent argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(MolecularComponentPattern argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(MolecularType argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(MolecularTypePattern argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(SpeciesPattern argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(SeedSpecies argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
	
	
	//
	// Applications - to be handled by BioModels
	//
//	public Issue(GeometryContext argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(UnmappedGeometryClass argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(SpeciesContextSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(SpeciesContextSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
//		this((Object)argSource, issueContext, argCategory, argMessage, argTooltip, argSeverity);
//	}
//	public Issue(SpeciesContextSpecParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this((Object)argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(StructureMapping argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(StructureMappingParameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(MicroscopeMeasurement argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(ReactionCombo argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
//		this((Object)argSource, issueContext, argCategory, argMessage, argTooltip, argSeverity);
//	}
	

	//
	// ParameterEstimation / Optimization - to be handled by BioModels (and Maybe VirtualFRAP)
	//
//	public Issue(ModelOptimizationSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(ObjectiveFunction argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(OptimizationSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(ReferenceDataMappingSpec argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(cbit.vcell.opt.Parameter argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}

	
	//
	// Geometry - to be handled by BioModels and MathModels
	//
//	public Issue(Geometry argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
	
	
	//
	// MathDescription - to be handled by BioModels and MathModels
	//
//	public Issue(MathDescription argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(Event argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(SubDomain argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(Variable argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(Equation argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
	
	
	//
	// Simulation - to be handled by BioModels and MathModels
	//
//	public Issue(Simulation argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
//	public Issue(OutputFunctionIssueSource argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}
	
	
	//
	// too low level ... handle this soon
	//
//	public Issue(Expression argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this(argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}


	//
	// SBML Importing
	//
//	public Issue(SBase argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
//		this((Object)argSource, issueContext, argCategory, argMessage, argTooltip, argSeverity);
//	}
//	public Issue(SBase argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, int argSeverity) {
//		this((Object)argSource, issueContext, argCategory, argMessage, null, argSeverity);
//	}

///**
// * Private constructor, not type safe
// */
//	private Issue(Object argSource, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, int argSeverity) {
//		super();
//		if (argSeverity<0 || argSeverity>MAX_SEVERITY){
//			throw new IllegalArgumentException("unexpected severity="+argSeverity);
//		}
//		this.source = argSource;
//		this.issueContext = issueContext;
//		this.message = argMessage;
//		this.tooltip = argTooltip;
//		this.category = argCategory;
//		this.severity = argSeverity;
//	}
	
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
	public Issue(IssueSource argSource, IssueSource argSource2, IssueContext issueContext, IssueCategory argCategory, String argMessage, String argTooltip, Severity argSeverity) {
		super();
		this.source = argSource;
		this.source2 = argSource2;
		this.issueContext = issueContext;
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
		if (other.source2!=this.source2){
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
		if (!source2.equals(otherIssue.source2)){
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
public Object getSource() {
	return source;
}
public Object getSource2() {
	return source2;
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
	return getSeverityName()+": '"+getMessage()+"'";
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
}
