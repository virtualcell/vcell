package org.jlibsedml;

import java.util.ArrayList;
import java.util.List;

import org.jmathml.ASTNode;
import org.jmathml.ASTRootNode;

/**
 * Encapsulates the ComputeChange element of SEDML.
 * 
 * * @author anu/radams
 * 
 */
public class ComputeChange extends Change implements IMathContainer {
    
	private ASTNode math = null;
	private List<Variable> listOfVariables;
	private List<Parameter> listOfParameters;

	/**
	 * 
	 * @param target
	 *            A non-null XPathTarget to which the change should be
	 *            applied.
	 * @param math
	 *            An {@link ASTRootNode} used  to compute the new value of the target element.
	 */
    public ComputeChange(XPathTarget target, ASTNode math) {
        super(target);
        this.setMath(math);
        listOfVariables = new ArrayList<Variable>();
        listOfParameters = new ArrayList<Parameter>();
    }
    public ComputeChange(XPathTarget target) {
        this(target, null);
    }

	 public void setMath(ASTNode math) {
        this.math = math;
    }
	  public ASTNode getMath() {
	        return math;
	    }

    /**
     * Getter for the change kind.
     * @return SEDMLTags.COMPUTE_CHANGE_KIND;
     */
	@Override
	public String getChangeKind() {
		return SEDMLTags.COMPUTE_CHANGE_KIND;
	}

	 public boolean accept(SEDMLVisitor visitor) {
	        
	     if(visitor.visit(this)){
	            for (Variable var :getListOfVariables()){
	               if( !var.accept(visitor)) {
	                   return false;
	               }
	            
	            }
	            for (Parameter p :getListOfParameters()){
	                   if(! p.accept(visitor)){
	                       return false;
	                   }
	             }
	            return true;
	        }else {
	            return false;
	        } 
   }
	
	/**
	 * Returns a possible empty but non-null list of {@link Variable} objects
	 * @return a list of {@link Variable}
	 */
	public List<Variable> getListOfVariables() {
		return listOfVariables;
	}

	@Override
	public String toString() {
		return "ComputeChange [math=" + getMath() + ", getTarget()=" + getTargetXPath()
				+ "]";
	}

	void setListOfVariables(List<Variable> listOfVariables) {
		this.listOfVariables = listOfVariables;
	}

	void setListOfParameters(List<Parameter> listOfParameters) {
		this.listOfParameters = listOfParameters;
	}

	/**
	 * Returns a possible empty but non-null list of {@link Parameter} objects
	 * @return a list of {@link Parameter}
	 */
	public List<Parameter> getListOfParameters() {
		return listOfParameters;
	}
	
	/**
	 * Adds a parameter to this element
	 * @param param
	 * @return <code>true</code> if param was successfully added.
	 */
	public boolean addParameter(Parameter param) {
		return listOfParameters.add(param);
	}
	
	/**
	 * Adds a variable to this element
	 * @param var a {@link Variable}
	 * @return <code>true</code> if var was successfully added.
	 */
	public boolean addVariable(Variable	 var) {
		return listOfVariables.add(var);
	}

	public  String getId() {
		return SEDMLTags.COMPUTE_CHANGE_KIND;
	}

	@Override
	public String getElementName() {
		return SEDMLTags.COMPUTE_CHANGE;
	}
}
