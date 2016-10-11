package org.vcell.util;

import java.io.Serializable;

/**
 * A hierarchical view of path to issue source object
 * 1) enables issue instantiation to be unaware of complete path to object within data model.
 * 2) enables issue consumer required context to react to issue (e.g. hyperlinks, display object name & path)
 */
public class IssueContext implements Serializable {
	public enum ContextType {
		MathModel,
		BioModel,
		Model,
		SimContext,
		Kinetics,
		MathDescription,
		MathMapping,
		Geometry,
		Simulation,
		SpeciesContextSpec,
		ParameterEstimationTask,
		MicroscopyMeasurement,
		ReactionStep,

	}

	private final IssueContext.ContextType contextType;
	private final Object contextObject;
	private final IssueContext parentContext;

	public IssueContext(){
		this(null,null,null);
	}

	public IssueContext(IssueContext.ContextType contextType, Object contextObject, IssueContext parentContext){
		this.contextType = contextType;
		this.contextObject = contextObject;
		this.parentContext = parentContext;
	}

	public IssueContext newChildContext(IssueContext.ContextType contextType, Object contextObject){
		if (hasContextType(contextType)){
			return this;
		}else{
			return new IssueContext(contextType, contextObject, this);
		}
	}

	public IssueContext.ContextType getContextType() {
		return contextType;
	}

	public Object getContextObject() {
		return contextObject;
	}

	public IssueContext getParentContext() {
		return parentContext;
	}

	//
	// convenience method to search through context hierarchy using contextType
	//
	public Object getContextObject(IssueContext.ContextType contextType){
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