package org.jlibsedml.components.model;

import org.jlibsedml.*;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates an AddXML element in SED-ML. This requires the value of the
 * 'target' attribute to refer to  a parent or container element into which the XML will be added.
 * * @author anu/radams
 *
 */
public final class AddXML extends Change {
    private NewXML newXML;


    /**
     *
     * @param target A non-null <code>XPathTarget</code> of the XPath target
     * @param newXML A non-null <code>NewXML</code> of new XML
     * @throws IllegalArgumentException if either argument is <code>null</code>.
     */
    public AddXML(XPathTarget target, NewXML newXML) {
        this(null, null, target, newXML);
    }

    /**
     *
     * @param target A non-null <code>XPathTarget</code> of the XPath target
     * @param newXML A non-null <code>NewXML</code> of new XML
     * @throws IllegalArgumentException if either argument is <code>null</code>.
     */
    public AddXML(SId id, String name, XPathTarget target, NewXML newXML) {
        super(id, name, target);
        if (SedMLElementFactory.getInstance().isStrictCreation()) SedGeneralClass.checkNoNullArgs(newXML);
        this.newXML = newXML;
    }

    public AddXML clone() throws CloneNotSupportedException {
        AddXML copy = (AddXML) super.clone();
        copy.newXML = this.newXML;
        return copy;
    }

    /**
     * Getter for the new XML to be added to the target.
     *
     * @return the {@link NewXML} to be added.
     */
    public NewXML getNewXML() {
        return this.newXML;
    }


    /**
     * Sets the NewXML for this element.
     *
     * @param newXML A non-null {@link NewXML} object.
     * @since 1.2.0
     */
    public void setNewXML(NewXML newXML) {
        this.newXML = newXML;
    }

    /**
     * Getter for the change kind.
     *
     * @return SEDMLTags.ADD_XML_KIND
     */
    @Override
    public String getChangeKind() {
        return SedMLTags.ADD_XML_KIND;
    }

    @Override
    public String getElementName() {
        return SedMLTags.ADD_XML;
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
        if (this.newXML == null) return super.parametersToString();
        else return super.parametersToString() + ", AddXML=[" + this.newXML.toString() + ']';
    }
}
