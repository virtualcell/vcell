package org.vcell.util;

import java.io.Serializable;

import org.vcell.util.Issue.IssueSource;

/**
 * A hierarchical view of path to issue source object
 * 1) enables issue instantiation to be unaware of complete path to object within data model.
 * 2) enables issue consumer required context to react to issue (e.g. hyperlinks, display object name & path)
 */
public class IssueContext implements Serializable {
	public enum ContextType {
		Default,
		MathModel,
		BioModel,
		Model,
		SimContext,
		ModelProcessDynamics,
		MathDescription,
		MathMapping,
		Geometry,
		Simulation,
		SpeciesContextSpec,
		ParameterEstimationTask,
		MicroscopyMeasurement,
		ReactionStep, 
		SeedSpecies,    	// context for species pattern
		ReactionRule, 
		RbmObservable,
		SpeciesContext,		// context for species pattern
		MolecularType,
	}

	private final IssueContext.ContextType contextType;
	private final IssueSource contextObject;
	private final IssueContext parentContext;
	
	public IssueContext(){
		this(ContextType.Default, null, null);
	}
	
	public IssueContext(IssueContext.ContextType contextType, IssueSource contextObject, IssueContext parentContext){
		this.contextType = contextType;
		this.contextObject = contextObject;
		this.parentContext = parentContext;
	}
	
	public IssueContext newChildContext(IssueContext.ContextType contextType, IssueSource contextObject){
		if (hasContextType(contextType)){
			return this;
		}else{
			return new IssueContext(contextType, contextObject, this);
		}
	}

	public IssueContext.ContextType getContextType() {
		return contextType;
	}

	public IssueSource getContextObject() {
		return contextObject;
	}

	public IssueContext getParentContext() {
		return parentContext;
	}

	//
	// convenience method to search through context hierarchy using contextType
	//
	public IssueSource getContextObject(IssueContext.ContextType contextType){
		if (contextType==null){
			throw new IllegalArgumentException("contextType is null");
		}
		if (contextType == this.contextType){
			return this.contextObject;
		}else if (this.parentContext!=null){
			return this.parentContext.getContextObject(contextType);
		}else{
			return null;
		}
	}

	//
	// convenience method to search through context hierarchy using contextType
	//
	public boolean hasContextType(IssueContext.ContextType contextType){
		if (contextType==null){
			throw new IllegalArgumentException("contextType is null");
		}
		if (contextType == this.contextType){
			return true;
		}else if (this.parentContext!=null){
			return this.parentContext.hasContextType(contextType);
		}else{
			return false;
		}
	}

	/**
	 * model component needs more time before reporting
	 * forward to parent, if any
	 */
	public void needMoreTime() {
		if (parentContext != null) {
			parentContext.needMoreTime();
		}

	}

}