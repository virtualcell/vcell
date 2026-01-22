package org.jlibsedml.components.model;

import org.jlibsedml.*;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Class for manipulating the value of an attribute via XPath.
 *
 * @author anu/radams
 *
 */
public final class ChangeAttribute extends Change {
    private String newValue;

    /**
     * @param target   An {@link XPathTarget} to an attribute whose value is to be changed.
     * @param newValue The new value of  <code>target</code> attribute.
     * @throws IllegalArgumentException if either argument is null or empty.
     */
    public ChangeAttribute(XPathTarget target, String newValue) {
        this(null, null, target, newValue);
    }

    /**
     *
     * @param target   An {@link XPathTarget} to an attribute whose value is to be changed.
     * @param newValue The new value of  <code>target</code> attribute.
     * @throws IllegalArgumentException if either argument is null or empty.
     */
    public ChangeAttribute(SId id, String name, XPathTarget target, String newValue) {
        super(id, name, target);
        if (SedMLElementFactory.getInstance().isStrictCreation()) {
            SedGeneralClass.checkNoNullArgs(newValue);
            SedGeneralClass.stringsNotEmpty(newValue);
        }
        this.newValue = newValue;
    }

    /**
     * Getter for the change kind.
     *
     * @return SEDMLTags.CHANGE_ATTRIBUTE_KIND;
     */
    @Override
    public String getChangeKind() {
        return SedMLTags.CHANGE_ATTRIBUTE_KIND;
    }

    /**
     * Getter for the new attribute value to apply to  the target expression.
     *
     * @return A non-null, non-empty <code>String</code>.
     */
    public String getNewValue() {
        return this.newValue;
    }



    /**
     * Setter for the new value of this object.
     *
     * @param newValue A non-null, non-empty <code>String</code>.
     * @throws IllegalArgumentException if <code>newValue </code>is <code>null</code> or empty.
     * @since 1.2.0
     */
    public void setNewValue(String newValue) {
        if (SedMLElementFactory.getInstance().isStrictCreation()) {
            SedGeneralClass.checkNoNullArgs(newValue);
            SedGeneralClass.stringsNotEmpty(newValue);
        }
        this.newValue = newValue;
    }

    @Override
    public String getElementName() {
        // TODO Auto-generated method stub
        return SedMLTags.CHANGE_ATTRIBUTE;
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
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
        if (this.newValue == null) return super.parametersToString();
        else return super.parametersToString() + ", newValue=[" + this.newValue + ']';
    }
}
