package org.jlibsedml.components;

import org.jlibsedml.SedMLElementFactory;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;

/**
 * Represents the SED-ML Parameter element which  defines a constant value in
 *  either ComputeChange or Variable elements.
 *
 */
public final class Parameter extends SedBase {


    @Override
    public String parametersToString() {
        return super.parametersToString() + ", value=" + value;
    }
	
	public  boolean accept(SEDMLVisitor visitor){
        return visitor.visit(this);
    }

	
	private double value;

	/**
	 * Copy constructor
	 * @param parameter A non-null parameter.
	 */
	public Parameter(Parameter parameter) {
		this(parameter.getId(), parameter.isNameSet() ? parameter.getName()	: null, parameter.getValue());
	}

	

	/**
	 * 
	 * @param id The id of this element.
	 * @param name optional, can be <code>null</code>
	 * @param value the Parameter value.
	 * @throws IllegalArgumentException if id or value is null or the empty string.
	 */
	public Parameter(SId id, String name, double value) {
		super(id,name);
		if(SedMLElementFactory.getInstance().isStrictCreation()){
            SedGeneralClass.checkNoNullArgs(value);
		}		
		this.value = value;
	}

	/**
	 * Boolean test for whether this parameter is set or not.
	 * @return <code>true</code> if set, <code>false</code> otherwise
	 */
	public boolean isNameSet() {
		return getName() != null;
	}

	/**
	 * Getter for the parameter's value.
	 * @return the value
	 */
	public final double getValue() {
		return value;
	}


	/**
	 * Setter for this parameter's value.
	 * @param value <code>double</code>
	 * @since 1.2.0
	 */
	public final void setValue(double value) {	
		this.value = value;
	}

	@Override
	public String getElementName() {
		return SedMLTags.DATAGEN_ATTR_PARAMETER;
	}
}