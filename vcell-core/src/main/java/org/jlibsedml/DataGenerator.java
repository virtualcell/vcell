package org.jlibsedml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmathml.ASTNode;
import org.jmathml.FormulaFormatter;

/**
 * Encapsulates a DataGenerator element in SED-ML.
 * 
 * @author anu/radams
 * 
 */
public final class DataGenerator extends AbstractIdentifiableElement implements
		IMathContainer {

	private ASTNode math = null;

	private ArrayList<Variable> listOfVariables = new ArrayList<Variable>();
	private ArrayList<Parameter> listOfParameters = new ArrayList<Parameter>();
	private FormulaFormatter formulaFormatter=new FormulaFormatter();

	/**
	 * 
	 * @param id
	 *            A unique identifier for this element.
	 * @param name
	 *            An optional name, can be null.
	 * @param math An ASTNode
	 * @throws IllegalArgumentException
	 *             if id is null or empty string, or <code>math</code> is null.
	 */
	public DataGenerator(String id, String name, ASTNode math) {
		super(id, name);
		if (SEDMLElementFactory.getInstance().isStrictCreation()) {
			Assert.checkNoNullArgs(math);
		}
		this.math = math;
	}

	/*
	 * Package scoped constructor for reading from XML
	 */
	DataGenerator(String id, String name) {
		super(id, name);
	}

	@Override
	public String toString() {
		return "DataGenerator [math=" + math + ", name=" + getName()
				+ ", getId()=" + getId() + "]";
	}

	/**
	 * Returns a read-only list of this element's <code>List</code> of
	 * variables.
	 * 
	 * @return A possibly empty but non-null List</code> of {@link Variable}
	 *         objects.
	 */
	public List<Variable> getListOfVariables() {
		return Collections.unmodifiableList(listOfVariables);
	}

	/**
	 * Adds a {@link Variable} to this object's list of Variables, if not
	 * already present.
	 * 
	 * @param variable
	 *            A non-null {@link Variable} element
	 * @return <code>true</code> if variable added, <code>false </code>
	 *         otherwise.
	 */
	public boolean addVariable(Variable variable) {
		if (!listOfVariables.contains(variable))
			return listOfVariables.add(variable);
		return false;
	}

	/**
	 * Removes a {@link Variable} from this object's list of Variables.
	 * 
	 * @param variable
	 *            A non-null {@link Variable} element
	 * @return <code>true</code> if variable removed, <code>false </code>
	 *         otherwise.
	 */
	public boolean removeVariable(Variable variable) {

		return listOfVariables.remove(variable);

	}

	/**
	 * Getter for a read-only list of parameters.
	 * 
	 * @return A possibly empty but non-null List</code> of {@link Parameter}
	 *         objects.
	 */
	public List<Parameter> getListOfParameters() {
		return Collections.unmodifiableList(listOfParameters);
	}

	/**
	 * Adds a {@link Parameter} to this object's list of Parameters, if not
	 * already present.
	 * 
	 * @param parameter
	 *            A non-null {@link Parameter} element
	 * @return <code>true</code> if parameter added, <code>false </code>
	 *         otherwise.
	 */
	public boolean addParameter(Parameter parameter) {
		if (!listOfParameters.contains(parameter))
			return listOfParameters.add(parameter);
		return false;
	}

	/**
	 * Removes a {@link Parameter} from this object's list of Parameters.
	 * 
	 * @param parameter
	 *            A non-null {@link Parameter} element.
	 * @return <code>true</code> if parameter removed, <code>false </code>
	 *         otherwise.
	 */
	public boolean removeParameter(Parameter parameter) {

		return listOfParameters.remove(parameter);

	}

	void setMathML(ASTNode math) {
		this.math = math;
	}

	/**
	 * Gets the {@link ASTNode} maths for this object
	 * @return an {@link ASTNode}.
	 */
	public ASTNode getMath() {
		return math;
    }
	
	/**
	 * Convenience function to return the maths expression as a C-style string.
	 * @return A <code>String</code> representation of the maths of this DataGenerator.
	 */
	public String getMathAsString(){
	    return formulaFormatter.formulaToString(math);
	}

	@Override
	public String getElementName() {
		return SEDMLTags.DATAGENERATOR_TAG;
	}
	
	public  boolean accept(SEDMLVisitor visitor){
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

}
