package org.vcell.util;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext.SimulationContextNameScope;
import cbit.vcell.mapping.StructureMapping.StructureMappingNameScope;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.NameScope;


/**
 * Insert the type's description here.
 * Creation date: (4/1/2004 10:12:47 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class Issue implements java.io.Serializable, Matchable {
	private java.util.Date date = new java.util.Date();
	private String message = null;
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
		
		InternalError,
		ParameterBoundsDefinition,
		ParameterEstimationBoundsError,
		ParameterEstimationBoundsViolation,
		ParameterEstimationNoParameterSelected,
		ParameterEstimationRefereceDataNoTime,
		ParameterEstimationRefereceDataNotMapped,
		ParameterEstimationRefereceDataMappedImproperly,
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
	super();
	if (argSeverity<0 || argSeverity>MAX_SEVERITY){
		throw new IllegalArgumentException("unexpected severity="+argSeverity);
	}
	this.source = argSource;
	this.message = argMessage;
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

public String getSourceDescription() {
	String description = null;
	if (source instanceof ModelParameter) {
		description = ((ModelParameter)source).getName();
	} else if (source instanceof KineticsParameter) {
		description =  ((KineticsParameter)source).getName();
	} else if (source instanceof SimpleReaction) {
		description = ((SimpleReaction)source).getName();
	} else if (source instanceof FluxReaction) {
		description = ((FluxReaction)source).getName();
	} else if (source instanceof SpeciesContext) {
		description = ((SpeciesContext)source).getName();
	} else if (source instanceof Feature) {
		description = ((Feature)source).getName();
	} else if (source instanceof Membrane) {
		description = ((Membrane)source).getName();
	} else if (source instanceof StructureMappingParameter) {
		description = ((StructureMappingParameter) source).getDescription() + " '" + ((StructureMappingParameter) source).getName() + "'";
	}
	return description;
}

public String getSourceContextDescription() {
	String description = null;
	if (source instanceof ModelParameter) {
		description = "Global Parameter";
	} else if (source instanceof KineticsParameter) {
		description = "Reaction " + ((KineticsParameter)source).getKinetics().getReactionStep().getName();
	} else if (source instanceof SimpleReaction) {
		description = "Reaction";
	} else if (source instanceof FluxReaction) {
		description = "Flux";
	} else if (source instanceof SpeciesContext) {
		description = "Species";
	} else if (source instanceof Feature) {
		description = Structure.TYPE_NAME_FEATURE;
	} else if (source instanceof Membrane) {
		description = Structure.TYPE_NAME_MEMBRANE;
	} else if (source instanceof StructureMappingParameter) {
		StructureMappingNameScope nameScope = (StructureMappingNameScope)(((StructureMappingParameter)source).getNameScope());
		SimulationContextNameScope parentNameScope = (SimulationContextNameScope)nameScope.getParent();
		description = BioModel.SIMULATION_CONTEXT_DISPLAY_NAME + " '" + parentNameScope.getSimulationContext().getName() 
			+ "': Structure Mapping: '" + nameScope.getStructureMapping().getStructure().getName() + "'";
	}
	return description;
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
}