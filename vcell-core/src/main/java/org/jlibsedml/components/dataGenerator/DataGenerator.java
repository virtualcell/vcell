package org.jlibsedml.components.dataGenerator;

import java.util.List;

import org.jlibsedml.*;
import org.jlibsedml.components.*;
import org.jlibsedml.components.listOfConstructs.ListOfParameters;
import org.jlibsedml.components.listOfConstructs.ListOfVariables;
import org.jmathml.ASTNode;
import org.jmathml.FormulaFormatter;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Encapsulates a DataGenerator element in SED-ML.
 * 
 * @author anu/radams
 * 
 */
public final class DataGenerator extends SedBase implements Calculation {
    private final static FormulaFormatter formulaFormatter = new FormulaFormatter();

	private ASTNode math = null;
	private final ListOfVariables listOfVariables = new ListOfVariables();
	private final ListOfParameters listOfParameters = new ListOfParameters();

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
	public DataGenerator(SId id, String name, ASTNode math) {
		super(id, name);
		if (SedMLElementFactory.getInstance().isStrictCreation()) {
            SedGeneralClass.checkNoNullArgs(math);
		}
		this.math = math;
	}

	/*
	 * Package scoped constructor for reading from XML
	 */
    public DataGenerator(SId id, String name) {
		super(id, name);
	}

    public ListOfVariables getListOfVariables() {
        return this.listOfVariables;
    }

	/**
	 * Returns a read-only list of this element's <code>List</code> of
	 * variables.
	 * 
	 * @return A possibly empty but non-null List</code> of {@link Variable}
	 *         objects.
	 */
	public List<Variable> getVariables() {
		return this.listOfVariables.getContents();
	}

    public boolean containsVariable(Variable variable) {
        return this.listOfVariables.containsContent(variable.getId());
    }

	/**
	 * Adds a {@link Variable} to this object's list of Variables, if not
	 * already present.
	 * 
	 * @param variable
	 *            A non-null {@link Variable} element
	 */
	public void addVariable(Variable variable) {
		this.listOfVariables.addContent(variable);
	}

	/**
	 * Removes a {@link Variable} from this object's list of Variables.
	 * 
	 * @param variable
	 *            A non-null {@link Variable} element
	 */
	public void removeVariable(Variable variable) {
        this.listOfVariables.removeContent(variable);
	}

    public ListOfParameters getListOfParameters() {
        return this.listOfParameters;
    }

	/**
	 * Getter for a read-only list of parameters.
	 * 
	 * @return A possibly empty but non-null List</code> of {@link Parameter}
	 *         objects.
	 */
	public List<Parameter> getParameters() {
		return this.listOfParameters.getContents();
	}


    public boolean containsParameter(Variable variable) {
        return this.listOfParameters.containsContent(variable.getId());
    }

	/**
	 * Adds a {@link Parameter} to this object's list of Parameters, if not
	 * already present.
	 * 
	 * @param parameter
	 *            A non-null {@link Parameter} element
	 */
	public void addParameter(Parameter parameter) {
		this.listOfParameters.addContent(parameter);
	}

	/**
	 * Removes a {@link Parameter} from this object's list of Parameters.
	 * 
	 * @param parameter
	 *            A non-null {@link Parameter} element.
	 */
	public void removeParameter(Parameter parameter) {
		this.listOfParameters.removeContent(parameter);

	}

	/**
	 * Gets the {@link ASTNode} maths for this object
	 * @return an {@link ASTNode}.
	 */
	public ASTNode getMath() {
		return this.math;
    }

    @Override
    public void setMath(ASTNode math) {
        this.math = math;
    }

    /**
	 * Convenience function to return the maths expression as a C-style string.
	 * @return A <code>String</code> representation of the maths of this DataGenerator.
	 */
	public String getMathAsString(){
	    return DataGenerator.formulaFormatter.formulaToString(this.math);
	}

	@Override
	public String getElementName() {
		return SedMLTags.DATA_GENERATOR_TAG;
	}

    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @OverridingMethodsMustInvokeSuper
    public String parametersToString(){
        return super.parametersToString() + ", " + String.join(", ", this.getMathParamsAndVarsAsStringParams());
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        for (Variable var : this.getVariables()) {
            elementFound = var.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        for (Parameter p : this.getParameters()) {
            elementFound = p.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        return elementFound;
    }

}
